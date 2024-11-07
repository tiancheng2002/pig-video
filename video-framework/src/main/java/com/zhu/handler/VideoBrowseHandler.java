package com.zhu.handler;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.mapper.UserBrowseMapper;
import com.zhu.model.entity.VideoInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class VideoBrowseHandler implements VideoHandler {

    @Resource
    private UserBrowseMapper userBrowseMapper;

    @Override
    public Page<VideoInfo> videoHandler(IPage<VideoInfo> videoInfoIPage,Long uid) {
        Page<VideoInfo> videoPageList = userBrowseMapper.listBrowseVideoByPage(videoInfoIPage, uid);
        return videoPageList;
    }
}
