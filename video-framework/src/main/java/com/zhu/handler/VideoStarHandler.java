package com.zhu.handler;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.mapper.VideoStarMapper;
import com.zhu.model.entity.VideoInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class VideoStarHandler implements VideoHandler {

    @Resource
    private VideoStarMapper videoStarMapper;

    @Override
    public Page<VideoInfo> videoHandler(IPage<VideoInfo> videoInfoIPage, Long uid) {
        Page<VideoInfo> videoInfoPage = videoStarMapper.listStarVideoByPage(videoInfoIPage, uid);
//        List<VideoInfo> userStarVideo = videoStarService.getUserStarVideo(uid);
        return videoInfoPage;
    }

}
