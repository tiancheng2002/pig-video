package com.zhu.security;

import com.alibaba.fastjson.JSON;
import com.zhu.model.entity.User;
import com.zhu.result.RespBean;
import com.zhu.service.IUserService;
import com.zhu.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private IUserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();

        //登录成功之后会将生成的jwt字符串返回出去，并存储到Redis当中
        String jwt = jwtUtils.generateToken(authentication.getName());
        redisTemplate.opsForValue().set("token:" + authentication.getName(), jwt);
        //因为前端获取不到我们自定义的header，所以需要设置前端想要获取的header
        response.setHeader("Access-Control-Expose-Headers", jwtUtils.getHeader());
        response.setHeader(jwtUtils.getHeader(), jwt);
//        System.out.println(jwt);

        //获取到对应用户的信息并返回给前端
        User user = userService.userByName(authentication.getName());

        outputStream.write(JSON.toJSONString(RespBean.success(user)).getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

}
