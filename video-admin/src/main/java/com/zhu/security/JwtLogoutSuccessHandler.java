package com.zhu.security;

import com.alibaba.fastjson.JSON;
import com.zhu.result.RespBean;
import com.zhu.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if(authentication!=null){
            new SecurityContextLogoutHandler().logout(request,response,authentication);
        }

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();

        //因为前端获取不到我们自定义的header，所以需要设置前端想要获取的header
//        response.setHeader("Access-Control-Expose-Headers",jwtUtils.getHeader());
//        response.setHeader(jwtUtils.getHeader(),"");
//        System.out.println(jwt);

        outputStream.write(JSON.toJSONString(RespBean.success("退出成功")).getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();

    }

}
