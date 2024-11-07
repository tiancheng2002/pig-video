package com.zhu.security;

import com.zhu.exception.SecurityException;
import com.zhu.result.RespBeanEnum;
import com.zhu.utils.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();

        try {
            if(url.equals("/login")&&request.getMethod().equals("POST")){

                ValidatedCaptcha(request);
            }
        }catch (SecurityException e){
            loginFailureHandler.onAuthenticationFailure(request,response,e);
            return;
        }

        filterChain.doFilter(request,response);
    }

    private void ValidatedCaptcha(HttpServletRequest request) {
        String code = request.getParameter("captcha");
        String key = request.getParameter("key");
        String ip = IpUtil.getIpAddr(request);

//        if(StringUtils.isBlank(code)){
//            throw new CaptchaException("验证码错误");
//        }

        //从Redis缓存当中获取对应的验证码
        String captcha = (String) redisTemplate.opsForValue().get("captcha:" + key);
        if(captcha==null){
            throw new SecurityException(RespBeanEnum.Captcha_OVER);
        }

        if(!code.equals(captcha)){
            throw new SecurityException(RespBeanEnum.Captcha_ERROR);
        }

        redisTemplate.delete("captcha:"+ip);

    }

}
