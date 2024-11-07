package com.zhu.security;

import com.alibaba.fastjson.JSON;
import com.zhu.result.RespBean;
import com.zhu.result.RespBeanEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//用来解决认证过的用户访问去权限资源的异常
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        RespBean respBean = new RespBean();
//        respBean.setCode(HttpServletResponse.SC_FORBIDDEN);
//        respBean.setMsg("权限不足");
        ServletOutputStream outputStream = response.getOutputStream();

        outputStream.write(JSON.toJSONString(RespBean.error(RespBeanEnum.FORBIDDEN)).getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();

    }

}
