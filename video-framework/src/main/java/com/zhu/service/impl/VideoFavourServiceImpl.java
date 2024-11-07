package com.zhu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.exception.ErrorException;
import com.zhu.mapper.VideoFavourMapper;
import com.zhu.model.entity.VideoFavour;
import com.zhu.model.entity.VideoInfo;
import com.zhu.model.enums.MessageTypeEnums;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IVideoFavourService;
import com.zhu.service.IVideoInfoService;
import com.zhu.utils.MessageUtils;
import com.zhu.utils.RedisUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-31
 */
@Service
public class VideoFavourServiceImpl extends ServiceImpl<VideoFavourMapper, VideoFavour> implements IVideoFavourService {

    @Resource
    private IVideoInfoService videoInfoService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private MessageUtils messageUtils;

    @Override
    public int doVideoFavour(Long videoId,Long uid) {
        // 判断实体是否存在，根据类别获取实体
        VideoInfo videoInfo = videoInfoService.getById(videoId);
        if (videoInfo == null) {
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        // 每个用户串行点赞
        // 锁必须要包裹住事务方法
//        IVideoStarService videoStarService = (IVideoStarService) AopContext.currentProxy();
        synchronized (String.valueOf(uid).intern()) {
            return doVideoFavourInner(videoId, uid,videoInfo.getUid());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doVideoFavourInner(Long videoId, Long uid,Long authorId) {
        VideoFavour videoFavour = new VideoFavour();
        videoFavour.setVideoId(videoId);
        videoFavour.setUid(uid);
        QueryWrapper<VideoFavour> videoStarQueryWrapper = new QueryWrapper<>(videoFavour);
        VideoFavour oldVideoFavour = this.getOne(videoStarQueryWrapper);
        boolean result;
        // 已点赞
        if (oldVideoFavour != null) {
            result = this.remove(videoStarQueryWrapper);
            if (result) {
                // 点赞数 - 1
                result = videoInfoService.update()
                        .eq("video_id", videoId)
                        .gt("favour_num", 0)
                        .setSql("favour_num = favour_num - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new ErrorException(RespBeanEnum.ERROR);
            }
        } else {
            // 未点赞
            result = this.save(videoFavour);
            if (result) {
                // 点赞数 + 1
                result = videoInfoService.update()
                        .eq("video_id", videoId)
                        .setSql("favour_num = favour_num + 1")
                        .update();
                if(result){
                    messageUtils.sendMessageToUser(uid,authorId,videoId, MessageTypeEnums.FAVOUR);
                }
                return result ? 1 : 0;
            } else {
                throw new ErrorException(RespBeanEnum.ERROR);
            }
        }
    }

    @Override
    public List<VideoInfo> getUserFavourVideo(Long uid) {
        QueryWrapper<VideoFavour> videoFavourQueryWrapper = new QueryWrapper<>();
        videoFavourQueryWrapper.select("video_id").eq("uid",uid);
        List<Long> videoIds = this.baseMapper.selectObjs(videoFavourQueryWrapper)
                .stream()
                .map(o -> (Long) o).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(videoIds)){
            return Collections.EMPTY_LIST;
        }
        List<VideoInfo> videoInfos = videoInfoService.listByIds(videoIds);
        return videoInfos;
    }

//    @Override
//    public int doVideoFavour(Long videoId,Long uid) {
//        VideoInfo videoInfo = videoInfoService.getById(videoId);
//        if(videoInfo==null){
//            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
//        }
//        //判断用户是否登录了可以用拦截器进行统一的判断
//        //判断用户是否对视频进行点赞，如果没有对视频进行点赞，那么就对视频进行点赞，如果点赞了，那么就取消点赞（点赞数也要对应的发生变化）
//        //判断是否点赞可以走缓存
//        boolean isFavour = redisUtils.sHasKey("video:" + videoId, uid);
//        boolean result;
//        if(isFavour){
//            //取消用户点赞，删除缓存中的用户信息
//            //并且删除点赞表中的数据
//            redisUtils.setRemove("video:"+videoId,uid);
//        }else{
//            //用户点赞，添加用户信息到缓存当中
//            redisUtils.sSet("video:" + videoId,uid);
//        }
//        return 1;
//    }

}
