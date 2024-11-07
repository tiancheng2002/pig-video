package com.zhu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.mapper.UserMapper;
import com.zhu.query.UserQuery;
import com.zhu.model.entity.User;
import com.zhu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-08-29
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> userSearch(UserQuery userQuery) {
        return userMapper.userSearch(userQuery);
    }

    @Override
    public User userByName(String username) {
        return getOne(new QueryWrapper<User>().eq("username", username));
    }

}

