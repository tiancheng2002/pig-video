package com.zhu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.constant.CommentConstant;
import com.zhu.exception.ErrorException;
import com.zhu.mapper.CommentMapper;
import com.zhu.model.dto.comment.CommentAddRequest;
import com.zhu.model.dto.comment.CommentReplyRequest;
import com.zhu.model.entity.Comment;
import com.zhu.model.entity.CommentFavour;
import com.zhu.model.enums.MessageTypeEnums;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.ICommentFavourService;
import com.zhu.service.ICommentService;
import com.zhu.utils.AddressUtils;
import com.zhu.utils.BeanCopeUtils;
import com.zhu.utils.IpUtil;
import com.zhu.utils.MessageUtils;
import com.zhu.vo.CommentVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-27
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    
    @Resource
    private ICommentFavourService commentFavourService;

    @Resource
    private MessageUtils messageUtils;

    @Override
    public List<CommentVo> getVideoComment(Long videoId) {
        return baseMapper.getVideoComment(videoId);
    }

    @Override
    public Page<CommentVo> getMoreReply(CommentReplyRequest commentReplyRequest) {
        Long current = commentReplyRequest.getCurrent();
        Long pageSize = commentReplyRequest.getPageSize();
        Long parentId = commentReplyRequest.getParentId();
        Page<CommentVo> commentPage = new Page<>(current, pageSize);
        Page<CommentVo> reply = this.baseMapper.getReply(commentPage, parentId);
        return reply;
    }

    @Override
    public int doFavour(CommentFavour commentFavour) {
        Long commentId = commentFavour.getCommentId();
        Long uid = commentFavour.getUid();
        Comment comment = this.getById(commentId);
        if(comment==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        synchronized (String.valueOf(uid).intern()) {
            return doCommentFavourInner(commentFavour,comment.getUid());
        }
    }

    @Override
    public List<Long> getCommentLikesList(Long videoId, Long uid) {
        QueryWrapper<CommentFavour> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("video_id",videoId);
        commentQueryWrapper.eq("uid",uid);
        commentQueryWrapper.select("comment_id");
        List<Object> resultList = commentFavourService.listObjs(commentQueryWrapper);
        List<Long> likesList = resultList.stream().map(o -> (Long) o).collect(Collectors.toList());
        return likesList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doCommentFavourInner(CommentFavour commentFavour,Long authorId){
        QueryWrapper<CommentFavour> commentFavourQueryWrapper = new QueryWrapper<>(commentFavour);
        CommentFavour oldCommentFavour = commentFavourService.getOne(commentFavourQueryWrapper);
        boolean result;
        if (oldCommentFavour != null) {
            result = commentFavourService.remove(commentFavourQueryWrapper);
            if (result) {
                // 点赞数 - 1
                result = this.update()
                        .eq("id", commentFavour.getCommentId())
                        .gt("likes", 0)
                        .setSql("likes = likes - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new ErrorException(RespBeanEnum.ERROR);
            }
        } else {
            // 未点赞
            result = commentFavourService.save(commentFavour);
            if (result) {
                // 点赞数 + 1
                result = this.update()
                        .eq("id", commentFavour.getCommentId())
                        .setSql("likes = likes + 1")
                        .update();
                if(result){
                    messageUtils.sendMessageToUser(commentFavour.getUid(),authorId,commentFavour.getVideoId(), MessageTypeEnums.COMMENT_FAVOUR);
                }
                return result ? 1 : 0;
            } else {
                throw new ErrorException(RespBeanEnum.ERROR);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Comment addComment(CommentAddRequest commentAddRequest, HttpServletRequest request, Long uid) {
        Long receiverId = commentAddRequest.getReceiverId();
        Comment comment = BeanCopeUtils.copyBean(commentAddRequest, Comment.class);
        //获取用户的ip地址，并且根据ip地址获取到用户对应的省份
        Map address = AddressUtils.getAddress(IpUtil.getIp(request));
        String city = address.get("pro").toString();
        city = city.substring(0,city.length()-1);
        comment.setAddress(city);
        comment.setUid(uid);
        comment.setLikes(0L);
        //保存评论数据
        boolean save = this.save(comment);
        if(!save){
            //未评论成功，直接抛出异常
            throw new ErrorException(RespBeanEnum.ACTION_ERROR);
        }
        //根据传递的parentId判断该评论是否是回复
        MessageTypeEnums messageTypeEnums;
        if(commentAddRequest.getParentId().equals(CommentConstant.headParentId)){
            messageTypeEnums = MessageTypeEnums.COMMENT;
        }else{
            messageTypeEnums = MessageTypeEnums.COMMENT_REPLY;
        }
        messageUtils.sendMessageToUser(
                uid,
                receiverId,
                commentAddRequest.getVideoId(),
                MessageTypeEnums.setCommentText(messageTypeEnums,comment.getContent()));
        return comment;
    }

    @Override
    public int delComment(Comment comment, Long delCommentId) {
        QueryWrapper<Comment> commentDelWrapper = new QueryWrapper<>();
        if(comment.getParentId()==0){
            commentDelWrapper.eq("id",delCommentId).or().eq("parent_id",delCommentId);
        }else{
            commentDelWrapper.eq("id",delCommentId);
        }
        int delCount = baseMapper.delete(commentDelWrapper);
        //将redis中当前视频的评论数量进行更改
        return delCount;
    }

}
