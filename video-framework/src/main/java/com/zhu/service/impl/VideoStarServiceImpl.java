package com.zhu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.exception.ErrorException;
import com.zhu.mapper.VideoStarMapper;
import com.zhu.model.entity.VideoInfo;
import com.zhu.model.entity.VideoStar;
import com.zhu.model.enums.MessageTypeEnums;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IVideoInfoService;
import com.zhu.service.IVideoStarService;
import com.zhu.utils.MessageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
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
public class VideoStarServiceImpl extends ServiceImpl<VideoStarMapper, VideoStar> implements IVideoStarService {

    @Resource
    private IVideoInfoService videoInfoService;

    @Resource
    private MessageUtils messageUtils;

    @Override
    public int doVideoStar(Long videoId, Long uid) {
        // 判断实体是否存在，根据类别获取实体
        VideoInfo videoInfo = videoInfoService.getById(videoId);
        if (videoInfo == null) {
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        // 每个用户串行点赞
        // 锁必须要包裹住事务方法
//        IVideoStarService videoStarService = (IVideoStarService) AopContext.currentProxy();
        synchronized (String.valueOf(uid).intern()) {
            return doVideoStarInner(videoId, uid,videoInfo.getUid());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doVideoStarInner(Long videoId, Long uid, Long authorId) {
        VideoStar videoStar = new VideoStar();
        videoStar.setVideoId(videoId);
        videoStar.setUid(uid);
        QueryWrapper<VideoStar> videoStarQueryWrapper = new QueryWrapper<>(videoStar);
        VideoStar oldVideoStar = this.getOne(videoStarQueryWrapper);
        boolean result;
        // 已点赞
        if (oldVideoStar != null) {
            result = this.remove(videoStarQueryWrapper);
            if (result) {
                // 点赞数 - 1
                result = videoInfoService.update()
                        .eq("video_id", videoId)
                        .gt("star_num", 0)
                        .setSql("star_num = star_num - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new ErrorException(RespBeanEnum.ERROR);
            }
        } else {
            // 未点赞
            result = this.save(videoStar);
            if (result) {
                // 点赞数 + 1
                result = videoInfoService.update()
                        .eq("video_id", videoId)
                        .setSql("star_num = star_num + 1")
                        .update();
                if(result){
                    messageUtils.sendMessageToUser(uid,authorId,videoId, MessageTypeEnums.STAR);
                }
                return result ? 1 : 0;
            } else {
                throw new ErrorException(RespBeanEnum.ERROR);
            }
        }
    }

    @Override
    public List<VideoInfo> getUserStarVideo(Long uid) {
        QueryWrapper<VideoStar> videoStarQueryWrapper = new QueryWrapper<>();
        videoStarQueryWrapper.select("video_id").eq("uid",uid);
        List<Long> videoIds = this.baseMapper.selectObjs(videoStarQueryWrapper)
                .stream()
                .map(o -> (Long) o).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(videoIds)){
            return null;
        }
        List<VideoInfo> videoInfos = videoInfoService.listByIds(videoIds);
        return videoInfos;
    }

}
