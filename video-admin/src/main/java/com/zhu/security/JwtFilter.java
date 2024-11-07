package com.zhu.security;

import com.zhu.exception.SecurityException;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zhu.model.entity.User;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IUserService;
import com.zhu.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//Jwt过滤器
public class JwtFilter extends BasicAuthenticationFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private IUserService userService;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    public JwtFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //从请求头部当中获取到对应的jwt信息
        String jwt = request.getHeader(jwtUtils.getHeader());
        //如果jwt为空的话，说明访问的可能是登录页面或者是一些白名单的页面，直接放行即可
        //如果访问的是需要权限的页面的话，在后面的过滤器中都会进行拦截
        if(jwt==null|| StringUtils.isBlank(jwt)){
            chain.doFilter(request,response);
            return;
        }

        Claims claims = jwtUtils.claimsToken(jwt);
        try {
            tokenStatus(claims);
        } catch (SecurityException e) {
            loginFailureHandler.onAuthenticationFailure(request,response,e);
            return;
        }

        //获取到对应的用户信息
        String username = claims.getSubject();

        //根据用户名获取到对应的用户信息
//        User user = userService.userByName(username);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.commaSeparatedStringToAuthorityList(JSONArray.parseArray("[\"sys:manage\",\"sys:user:list\",\"sys:user:add\",\"sys:user:edit\",\"sys:user:del\",\"sys:user:query\",\"sys:role:list\",\"sys:role:add\",\"sys:role:edit\",\"sys:role:del\",\"sys:role:query\",\"sys:post:list\",\"sys:post:add\",\"sys:post:edit\",\"sys:post:del\",\"sys:post:query\",\"sys:dept:list\",\"sys:dept:add\",\"sys:dept:edit\",\"sys:dept:del\",\"sys:dept:query\",\"sys:dict:list\",\"sys:dict:add\",\"sys:dict:edit\",\"sys:dict:del\",\"sys:dict:query\",\"sys:dict:cache\",\"sys:menu:list\",\"sys:menu:add\",\"sys:menu:edit\",\"sys:menu:del\",\"sys:menu:query\",\"sys:monitor\",\"sys:server:list\",\"sys:app:setting\"]", String.class).toString().replace("[", "").replace("]", "")));

        SecurityContextHolder.getContext().setAuthentication(token);

        chain.doFilter(request,response);

    }

    public void tokenStatus(Claims claims) {
        if(claims==null){
            throw new SecurityException("token异常", RespBeanEnum.UNAUTHORIZED);
        }

        //判断用户的token是否存在缓存当中，如果不存在缓存当中就说明缓存已经过期了
        String userToken = (String) redisTemplate.opsForValue().get("token:" + claims.getSubject());

        if(jwtUtils.isTokenExpire(claims)||userToken==null){
            throw new SecurityException("token已过期", RespBeanEnum.UNAUTHORIZED);
        }
    }

}
