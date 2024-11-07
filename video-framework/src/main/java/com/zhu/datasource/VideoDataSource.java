package com.zhu.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.constant.CommonConstant;
import com.zhu.model.dto.search.SearchRequest;
import com.zhu.model.entity.VideoInfo;
import com.zhu.service.IVideoInfoService;
import com.zhu.vo.VideoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class VideoDataSource implements DataSource<VideoVo> {

    @Resource
    private IVideoInfoService videoInfoService;

    @Override
    public Page<VideoVo> doSearch(SearchRequest searchRequest,HttpServletRequest request) {
        Page<VideoInfo> videoInfoPage =  videoInfoService.searchFromES(searchRequest);
//        ServletRequestAttributes servletRequestAttributes =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = servletRequestAttributes.getRequest();
        return videoInfoService.getVideoVoOther(videoInfoPage, CommonConstant.NOT_ONLY_AUTHOR,request);
    }

}
