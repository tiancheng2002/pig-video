package com.zhu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.model.dto.user.UserLoginRequest;
import com.zhu.model.dto.user.UserRegisterRequest;
import com.zhu.model.entity.User;
import com.zhu.vo.AuthorInfoVo;
import com.zhu.vo.LoginUserVo;
import com.zhu.vo.UserVo;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-25
 */
public interface IUserService extends IService<User> {

    LoginUserVo login(UserLoginRequest userLoginRequest, HttpServletRequest request);

    LoginUserVo getLoginUser(HttpServletRequest request);

    Long register(UserRegisterRequest userRegisterRequest);

    void sendCaptcha(String toEmail);

    Page<UserVo> searchUser(String searchText, long pageNum, long pageSize);

    AuthorInfoVo getUserInfo(Long uid);

    User getUserBasicInfo(Long uid);

    boolean userLogout(HttpServletRequest request);

}
