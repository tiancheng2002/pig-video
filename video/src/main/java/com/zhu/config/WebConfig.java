//package com.zhu.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
//import javax.annotation.Resource;
//
//@Configuration
//public class WebConfig extends WebMvcConfigurationSupport {
//
//    @Resource
//    private LoginInterceptor loginInterceptor;
//
//    @Override
//    protected void addInterceptors(InterceptorRegistry registry) {
//        // 添加自定义拦截器，设置路径
//        registry.addInterceptor(loginInterceptor)
//                // 拦截小程序请求
//                .addPathPatterns("/**");
//                // 排除登陆请求
////                .excludePathPatterns("/api/user/weixin/wxLogin/*");
//        super.addInterceptors(registry);
//    }
//}
