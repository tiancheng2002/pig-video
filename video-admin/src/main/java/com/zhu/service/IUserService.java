package com.zhu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.query.UserQuery;
import com.zhu.model.entity.User;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-08-29
 */
public interface IUserService extends IService<User> {

    List<User> userSearch(UserQuery userQuery);

    User userByName(String username);

}
