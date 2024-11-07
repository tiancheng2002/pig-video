package com.zhu.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.constant.UserConstant;
import com.zhu.exception.ErrorException;
import com.zhu.mapper.UserMapper;
import com.zhu.model.dto.user.UserLoginRequest;
import com.zhu.model.dto.user.UserRegisterRequest;
import com.zhu.model.entity.User;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IUserService;
import com.zhu.utils.BeanCopeUtils;
import com.zhu.utils.RedisUtils;
import com.zhu.vo.AuthorInfoVo;
import com.zhu.vo.LoginUserVo;
import com.zhu.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private JavaMailSenderImpl mailSender;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 加密盐
     */
    private static final String SALT = "xiaozhu";

    public static final String Email_key = "emailCode";

    @Override
    public LoginUserVo login(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userEmail = userLoginRequest.getUserEmail();
        String userPassword = userLoginRequest.getUserPassword();
        //邮箱或密码其中一个为空就返回错误（前端已判断，为了防止直接调用接口）
        if(StringUtils.isAnyBlank(userEmail,userPassword)){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        //对用户输入密码进行加密并进行与数据库中的密码进行比对
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_email",userEmail);
        userQueryWrapper.eq("user_password",encryptPassword);
        User user = baseMapper.selectOne(userQueryWrapper);
        if(user==null){
            throw new ErrorException(RespBeanEnum.USERNAME_PASSWORD);
        }
        //如果用户被封号了，也不予登录
        if(user.getUserRole().equals(UserConstant.BAN_ROLE)){
            throw new ErrorException(RespBeanEnum.USER_STATUS_PROHIBIT);
        }
        LoginUserVo loginUserVo = getLoginUserVo(user);
        //将用户信息添加到缓存当中
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,loginUserVo);
        return loginUserVo;
    }

    @Override
    public LoginUserVo getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
//        System.out.println(userObj);
        LoginUserVo currentUser = (LoginUserVo) userObj;
        return currentUser;
    }

    @Override
    public Long register(UserRegisterRequest userRegisterRequest) {
        String userEmail = userRegisterRequest.getUserEmail();
        String userPassword = userRegisterRequest.getUserPassword();
        String confirmPassword = userRegisterRequest.getConfirmPassword();
        String userNickname = userRegisterRequest.getUserNickname();
        String keyCode = userRegisterRequest.getKeyCode();
        if(StringUtils.isAnyBlank(userNickname,userPassword,confirmPassword)){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        if (userPassword.length() < 8 || confirmPassword.length() < 8) {
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        //从缓存当中获取对应的邮箱验证码
        String captcha = (String) redisUtils.hget(Email_key, userEmail);
        //邮箱验证码为空表示可能过期了也可以未发送邮件
        if(StringUtils.isBlank(captcha)){
            throw new ErrorException(RespBeanEnum.Captcha_OVER);
        }
        //邮箱验证码不正确直接返回错误
        if(!captcha.equals(keyCode)){
            throw new ErrorException(RespBeanEnum.Captcha_ERROR);
        }
        if(!userPassword.equals(confirmPassword)){
            throw new ErrorException(RespBeanEnum.PASSWORD_NO_MATCH);
        }
        //如果邮箱已经注册了，直接返回对应提示信息
        Long count = baseMapper.selectCount(new QueryWrapper<User>().eq("user_email", userEmail));
        if(count > 0){
            throw new ErrorException(RespBeanEnum.EMAIL_EXIST);
        }
        User user = new User();
        user.setUserEmail(userEmail);
        //将用户密码进行自定义的加密规则加密，并保存到数据库中
        user.setUserPassword(DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes()));
        user.setUserNickname(userNickname);
        boolean save = this.save(user);
        if(!save){
            throw new ErrorException(RespBeanEnum.ERROR);
        }
        //注册成功直接返回用户id
        return user.getUid();
    }

    @Override
    public void sendCaptcha(String toEmail) {
        String captcha = RandomUtil.randomNumbers(6);
        //一个简单的邮件
        SimpleMailMessage message = new SimpleMailMessage();
        //设置发送的邮箱标题
        message.setSubject("邮箱验证码");
        //发送者
        message.setFrom("1849530179@qq.com");
        //接收者
        message.setTo(toEmail);
        //发送内容
        message.setText("尊敬的用户，欢迎您注册iBO视频网站，验证码："+captcha+"，请在三分钟之内进行验证！");
        redisUtils.hset(Email_key,toEmail,captcha,1800);
        mailSender.send(message);
    }

    @Override
    public Page<UserVo> searchUser(String searchText, long pageNum, long pageSize) {
//        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
//        userQueryWrapper.like("user_nickname",searchText);
//        userQueryWrapper.or().like("user_profile",searchText);
//        Page<User> userPage = this.page(new Page<>(pageNum, pageSize), userQueryWrapper);
//        Page<UserVo> userVOPage = new Page<>(pageNum, pageSize, userPage.getTotal());
//        List<UserVo> userVO = getUserVO(userPage.getRecords());
//        userVOPage.setRecords(userVO);
        Page<UserVo> userVoPage = new Page<>(pageNum, pageSize);
        Page<UserVo> userVOList = this.baseMapper.getUserVOList(userVoPage, searchText);
        return userVOList;
    }

    @Override
    public AuthorInfoVo getUserInfo(Long uid) {
        return this.baseMapper.getUserInfo(uid);
    }

    @Override
    public User getUserBasicInfo(Long uid) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("uid",uid);
        userQueryWrapper.select("uid","user_nickname","user_avatar");
        User user = this.getOne(userQueryWrapper);
        return user;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null) {
            throw new ErrorException(RespBeanEnum.USER_UN_LOGIN);
        }
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    public List<UserVo> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(o->BeanCopeUtils.copyBean(o,UserVo.class)).collect(Collectors.toList());
    }

    public LoginUserVo getLoginUserVo(User user){
        if (user == null) {
            return null;
        }
        LoginUserVo loginUserVo = BeanCopeUtils.copyBean(user, LoginUserVo.class);
        return loginUserVo;
    }

    public static void main(String[] args) {
        String s = RandomUtil.randomString(8);
        System.out.println(s);
//        System.out.println(DigestUtils.md5DigestAsHex((SALT+"12345678").getBytes()));
    }

}
