package com.zhu.security;

import com.alibaba.fastjson.JSON;
import com.zhu.result.RespBean;
import com.zhu.result.RespBeanEnum;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//用来解决匿名用户访问无权限资源时的异常
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ServletOutputStream outputStream = response.getOutputStream();
//        RespBean respBean = new RespBean();
//        respBean.setCode(HttpServletResponse.SC_UNAUTHORIZED);
//        respBean.setMsg("请先登录");

//        outputStream.write(JSON.toJSONString(RespBean.error(RespBeanEnum.ERROR,"请先登录")).getBytes("UTF-8"));
        outputStream.write(JSON.toJSONString(RespBean.error(RespBeanEnum.UNAUTHORIZED,"请先登录")).getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();

    }

}
