package com.zhu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.model.dto.search.SearchRequest;
import com.zhu.model.dto.video.VideoRecommendRequest;
import com.zhu.model.dto.video.VideoTypeRequest;
import com.zhu.model.entity.VideoInfo;
import com.zhu.vo.VideoVo;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-24
 */
public interface IVideoInfoService extends IService<VideoInfo> {

    Page<VideoVo> getVideoVoListByType(VideoTypeRequest videoTypeRequest, HttpServletRequest request);

    Page<VideoInfo> searchFromES(SearchRequest searchRequest);

    Page<VideoVo> getVideoVoOther(Page<VideoInfo> videoVoPage,boolean isOnlyAuthorInfo, HttpServletRequest request);

    VideoVo getVideoVo(VideoInfo videoInfo, HttpServletRequest request);

    Page<VideoVo> getVideoByRandom(VideoRecommendRequest videoRecommendRequest, HttpServletRequest request);

    Page<VideoVo> getHotVideoWithTopK(int k, HttpServletRequest request);

    Page<VideoVo> getVideoByFollow(Long uid, HttpServletRequest request);

}
