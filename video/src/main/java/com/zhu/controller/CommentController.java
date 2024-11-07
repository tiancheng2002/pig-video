package com.zhu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.annotation.AuthCheck;
import com.zhu.common.IdRequest;
import com.zhu.exception.ErrorException;
import com.zhu.model.dto.comment.CommentAddRequest;
import com.zhu.model.dto.comment.CommentLikesRequest;
import com.zhu.model.dto.comment.CommentReplyRequest;
import com.zhu.model.entity.Comment;
import com.zhu.model.entity.CommentFavour;
import com.zhu.result.RespBean;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.ICommentService;
import com.zhu.service.IUserService;
import com.zhu.vo.CommentVo;
import com.zhu.vo.LoginUserVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-27
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private ICommentService commentService;

    @Resource
    private IUserService userService;

    @GetMapping("/list")
    public RespBean getCommentByVideo(@RequestParam(value = "videoId",required = false) Long videoId){
        //todo 可以直接把用户所点赞的评论数据返回
        List<CommentVo> commentVos = commentService.getVideoComment(videoId);
//        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
//        commentQueryWrapper.eq("video_id",videoId);
//        List<Comment> comments = commentService.list(commentQueryWrapper);
        return RespBean.success(commentVos);
    }

    /**
     * 根据parent_id获取更多的回复评论
     * @param commentReplyRequest
     * @return
     */
    @PostMapping("/getReply")
    public RespBean getMoreReply(@RequestBody CommentReplyRequest commentReplyRequest){
        //todo 分页获取某一个评论下面的回复评论
        if(commentReplyRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        Page<CommentVo> reply = commentService.getMoreReply(commentReplyRequest);
//        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
//        commentQueryWrapper.eq("parent_id",parentId);
//        Page<Comment> commentPage = commentService.page(new Page<>(current, pageSize), commentQueryWrapper);
        return RespBean.success(reply);
    }

    @PostMapping("/add")
    @AuthCheck
    public RespBean addVideoComment(@RequestBody CommentAddRequest commentAddRequest,HttpServletRequest request){
        if(commentAddRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
        Comment comment = commentService.addComment(commentAddRequest,request,loginUser.getUid());
        return RespBean.success(comment);
    }

    @PostMapping("/del")
    @AuthCheck
    public RespBean delVideoComment(@RequestBody IdRequest idRequest,HttpServletRequest request){
        Long commentId = idRequest.getId();
        if(idRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        Comment comment = commentService.getById(commentId);
        LoginUserVo loginUser = userService.getLoginUser(request);
        if(!comment.getUid().equals(loginUser.getUid())){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        int delCount = commentService.delComment(comment,commentId);
        return RespBean.success(delCount);
    }

    @PostMapping("/favour")
    @AuthCheck
    public RespBean doCommentFavour(@RequestBody CommentFavour commentFavour, HttpServletRequest request){
        if(commentFavour==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
        System.out.println(loginUser);
        commentFavour.setUid(loginUser.getUid());
        int result = commentService.doFavour(commentFavour);
        return RespBean.success(result);
    }

    /**
     * 获取用户当前视频下的评论点赞列表
     * @param commentLikesRequest
     * @param request
     * @return
     */
    @PostMapping("/get/likes")
    public RespBean getUserCommentLikes(@RequestBody CommentLikesRequest commentLikesRequest,HttpServletRequest request){
        if(commentLikesRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
        //获取用户某个视频的评论点赞列表
        List<Long> likesList = new ArrayList<>();
        if(loginUser!=null){
            Long videoId = commentLikesRequest.getVideoId();
            Long uid = loginUser.getUid();
            likesList = commentService.getCommentLikesList(videoId,uid);
        }
        return RespBean.success(likesList);
    }

}
