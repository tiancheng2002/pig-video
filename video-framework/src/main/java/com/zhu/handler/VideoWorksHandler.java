package com.zhu.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.model.entity.VideoInfo;
import com.zhu.service.IVideoInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class VideoWorksHandler implements VideoHandler {

    @Resource
    private IVideoInfoService videoInfoService;

    @Override
    public Page<VideoInfo> videoHandler(IPage<VideoInfo> videoInfoIPage, Long uid) {
        QueryWrapper<VideoInfo> videoInfoQueryWrapper = new QueryWrapper<>();
        videoInfoQueryWrapper.eq("uid",uid);
        Page<VideoInfo> videoPageList = (Page<VideoInfo>) videoInfoService.page(videoInfoIPage, videoInfoQueryWrapper);
//        List<VideoInfo> videoInfos = videoInfoService.list(videoInfoQueryWrapper);
//        return CollectionUtils.isEmpty(videoInfos)? Collections.EMPTY_LIST:videoInfos;
        return videoPageList;
    }

}
