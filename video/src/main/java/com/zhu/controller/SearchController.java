package com.zhu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.datasource.DataSource;
import com.zhu.datasource.DataSourceRegister;
import com.zhu.datasource.UserDataSource;
import com.zhu.datasource.VideoDataSource;
import com.zhu.exception.ErrorException;
import com.zhu.model.dto.search.SearchRequest;
import com.zhu.model.enums.SearchTypeEnums;
import com.zhu.result.RespBean;
import com.zhu.result.RespBeanEnum;
import com.zhu.vo.SearchVo;
import com.zhu.vo.UserVo;
import com.zhu.vo.VideoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private DataSourceRegister dataSourceRegister;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private VideoDataSource videoDataSource;

    @PostMapping("/all")
    public RespBean searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request){
//        RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(),true);
        String type = searchRequest.getType();
        SearchTypeEnums enumByValue = SearchTypeEnums.getEnumByValue(type);
        if(enumByValue.equals(SearchTypeEnums.SEARCH_all)){
            //查询全部（用户搜索出两个粉丝量靠前的，视频按照分页加载）
            //并行调用接口，提高接口响应速度
            CompletableFuture<Page<UserVo>> userTask = CompletableFuture.supplyAsync(() -> {
                searchRequest.setPageSize(2);
                Page<UserVo> userVOPage = userDataSource.doSearch(searchRequest,request);
                return userVOPage;
            });

            CompletableFuture<Page<VideoVo>> videoTask = CompletableFuture.supplyAsync(() -> {
                Page<VideoVo> videoVoPage = videoDataSource.doSearch(searchRequest,request);
                return videoVoPage;
            });

            CompletableFuture.allOf(userTask, videoTask).join();
            try {
                Page<UserVo> userVoPage = userTask.get();
                Page<VideoVo> videoVoPage = videoTask.get();
                SearchVo searchVo = new SearchVo();
                searchVo.setUserList(userVoPage.getRecords());
                searchVo.setVideoList(videoVoPage.getRecords());
                return RespBean.success(searchVo);
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new ErrorException(RespBeanEnum.ERROR);
            }
        }else{
            //根据类型进行指定的查询
            SearchVo searchVo = new SearchVo();
            DataSource dataSourceByType = dataSourceRegister.getDataSourceByType(type);
            Page<?> page = dataSourceByType.doSearch(searchRequest,request);
            searchVo.setDataList(page.getRecords());
            return RespBean.success(searchVo);
        }
    }

}
