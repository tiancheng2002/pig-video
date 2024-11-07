package com.zhu.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.model.dto.search.SearchRequest;

import javax.servlet.http.HttpServletRequest;

public interface DataSource<T> {

    /**
     * 根据关键词查找不同类型数据
     * @param searchRequest
     * @return
     */
    Page<T> doSearch(SearchRequest searchRequest, HttpServletRequest request);

}
