package com.zhu.handler;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.model.entity.VideoInfo;

public interface VideoHandler {

    Page<VideoInfo> videoHandler(IPage<VideoInfo> videoInfoIPage, Long uid);

}
