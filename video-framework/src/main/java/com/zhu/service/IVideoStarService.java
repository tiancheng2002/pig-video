package com.zhu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.model.entity.VideoInfo;
import com.zhu.model.entity.VideoStar;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-27
 */
public interface IVideoStarService extends IService<VideoStar> {

    int doVideoStar(Long videoId, Long uid);

    int doVideoStarInner(Long videoId, Long uid, Long authorId);

    List<VideoInfo> getUserStarVideo(Long uid);

}
