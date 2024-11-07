package com.zhu.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.model.dto.search.SearchRequest;
import com.zhu.model.entity.User;
import com.zhu.service.IUserService;
import com.zhu.vo.UserVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserDataSource implements DataSource<UserVo> {

    @Resource
    private IUserService userService;

    @Override
    public Page<UserVo> doSearch(SearchRequest searchRequest, HttpServletRequest request) {
        //根据用户名或用户简介来查找指定的用户
        String searchKey = searchRequest.getSearchKey();
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();
        Page<UserVo> userVoPage = userService.searchUser(searchKey,current,pageSize);
        return userVoPage;
    }

}
