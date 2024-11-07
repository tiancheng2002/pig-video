package com.zhu.security;

import com.alibaba.fastjson.JSONArray;
import com.zhu.model.entity.User;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private IUserService userService;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.userByName(username);
        if(user==null){
            throw new UsernameNotFoundException(RespBeanEnum.USERNAME_PASSWORD.getMsg());
        }
        System.out.println(user);
        return new UserDetail(user.getUid(),user.getUsername(),user.getPassword(),        AuthorityUtils.commaSeparatedStringToAuthorityList(JSONArray.parseArray("[\"sys:manage\",\"sys:user:list\",\"sys:user:add\",\"sys:user:edit\",\"sys:user:del\",\"sys:user:query\",\"sys:role:list\",\"sys:role:add\",\"sys:role:edit\",\"sys:role:del\",\"sys:role:query\",\"sys:post:list\",\"sys:post:add\",\"sys:post:edit\",\"sys:post:del\",\"sys:post:query\",\"sys:dept:list\",\"sys:dept:add\",\"sys:dept:edit\",\"sys:dept:del\",\"sys:dept:query\",\"sys:dict:list\",\"sys:dict:add\",\"sys:dict:edit\",\"sys:dict:del\",\"sys:dict:query\",\"sys:dict:cache\",\"sys:menu:list\",\"sys:menu:add\",\"sys:menu:edit\",\"sys:menu:del\",\"sys:menu:query\",\"sys:monitor\",\"sys:server:list\",\"sys:app:setting\"]", String.class).toString().replace("[", "").replace("]", "")));
    }

}
