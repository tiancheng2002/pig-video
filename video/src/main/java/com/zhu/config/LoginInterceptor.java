//package com.zhu.config;
//
//import com.zhu.constant.UserConstant;
//import com.zhu.vo.LoginUserVo;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//@Component
//public class LoginInterceptor implements HandlerInterceptor {
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println(request.getRequestURI());
//        LoginUserVo loginUserVo = (LoginUserVo) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
////        System.out.println(loginUserVo);
//        return true;
//    }
//
//}
