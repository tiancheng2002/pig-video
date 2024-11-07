package com.zhu.controller;

import com.zhu.annotation.AuthCheck;
import com.zhu.constant.FileConstant;
import com.zhu.exception.ErrorException;
import com.zhu.model.dto.user.UserEditRequest;
import com.zhu.model.dto.user.UserInfoRequest;
import com.zhu.model.dto.user.UserLoginRequest;
import com.zhu.model.dto.user.UserRegisterRequest;
import com.zhu.model.entity.User;
import com.zhu.result.RespBean;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IUserService;
import com.zhu.utils.BeanCopeUtils;
import com.zhu.utils.QiNiuUtils;
import com.zhu.vo.AuthorInfoVo;
import com.zhu.vo.LoginUserVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-25
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private QiNiuUtils qiNiuUtils;

    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public RespBean userLogin(@RequestBody UserLoginRequest userLoginRequest,HttpServletRequest request){
        log.info("用户登录请求信息:",userLoginRequest);
        //首选根据邮箱判断用户是否已经注册
        //接着对输入的密码进行加密处理并于数据库对比
        if(userLoginRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo user = userService.login(userLoginRequest, request);
        //登录成功之后将用户的信息进行返回并保存在前端当中
        return RespBean.success(user);
    }

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public RespBean userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        Long uid = userService.register(userRegisterRequest);
        return RespBean.success(uid);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public RespBean userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        boolean result = userService.userLogout(request);
        return RespBean.success(result);
    }

    /**
     * 获取当前登录用户信息
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    @ApiOperation(value = "获取当前登录用户信息")
    public RespBean getLoginUser(HttpServletRequest request){
        LoginUserVo loginUserVo = userService.getLoginUser(request);
        return RespBean.success(loginUserVo);
    }

    /**
     * 编辑用户信息
     * @param userEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    @AuthCheck
    public RespBean editUserInfo(@RequestBody UserEditRequest userEditRequest,HttpServletRequest request) {
        if (userEditRequest == null) {
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        Long uid = userEditRequest.getUid();
        LoginUserVo loginUser = userService.getLoginUser(request);
        if(!uid.equals(loginUser.getUid())){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        User user = BeanCopeUtils.copyBean(userEditRequest, User.class);
        boolean result = userService.updateById(user);
        return RespBean.success(result);
    }

    /**
     * 上传修改头像
     * @param file
     * @return
     */
    @PostMapping("/upload/avatar")
    @AuthCheck
    public RespBean uploadUserAvatar(@RequestParam("file") MultipartFile file){
        String url = null;
        try {
            url = qiNiuUtils.upload(file.getInputStream(),file.getOriginalFilename(), FileConstant.FILE_VIDEO);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return RespBean.success(url);
    }

    /**
     * 发送验证码到指定邮箱
     * @param toEmail
     * @return
     */
    @GetMapping("/send/captcha")
    public RespBean sendCaptchaToEmail(@RequestParam("toEmail") String toEmail){
        userService.sendCaptcha(toEmail);
        return RespBean.success(true);
    }

    /**
     * 获取作者信息（粉丝量、关注量、点赞量等）
     * @param userInfoRequest
     * @return
     */
    @PostMapping("/info")
    public RespBean getUserInfoDetail(@RequestBody UserInfoRequest userInfoRequest){
        if(userInfoRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        Long uid = userInfoRequest.getUid();
        AuthorInfoVo authorInfoVo = userService.getUserInfo(uid);
        return RespBean.success(authorInfoVo);
    }


}
