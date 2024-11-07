package com.zhu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.model.entity.VideoFavour;
import com.zhu.model.entity.VideoInfo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-31
 */
public interface IVideoFavourService extends IService<VideoFavour> {

    int doVideoFavour(Long videoId,Long uid);

    int doVideoFavourInner(Long videoId,Long uid,Long authorId);

    List<VideoInfo> getUserFavourVideo(Long uid);

}
