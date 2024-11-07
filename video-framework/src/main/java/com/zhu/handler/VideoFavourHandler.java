package com.zhu.handler;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.mapper.VideoFavourMapper;
import com.zhu.model.entity.VideoInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class VideoFavourHandler implements VideoHandler {

    @Resource
    private VideoFavourMapper videoFavourMapper;

    @Override
    public Page<VideoInfo> videoHandler(IPage<VideoInfo> videoInfoIPage,Long uid) {
        Page<VideoInfo> videoPageList = videoFavourMapper.listFavourVideoByPage(videoInfoIPage, uid);
//        List<VideoInfo> videoInfoList = videoFavourService.getUserFavourVideo(uid);
        return videoPageList;
    }

}
