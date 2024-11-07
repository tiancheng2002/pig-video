package com.zhu.security;

import com.zhu.exception.SecurityException;
import com.alibaba.fastjson.JSON;
import com.zhu.result.RespBean;
import com.zhu.result.RespBeanEnum;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();

        RespBean result;
        if (exception instanceof BadCredentialsException || exception instanceof UsernameNotFoundException) {
            result = RespBean.error(RespBeanEnum.USERNAME_PASSWORD, exception.getMessage());
        } else {
            SecurityException securityException = (SecurityException) exception;
            result = RespBean.error(securityException.getRespBeanEnum(), securityException.getMessage());
        }

        outputStream.write(JSON.toJSONString(result).getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

}
