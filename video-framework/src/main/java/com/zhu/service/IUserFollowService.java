package com.zhu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.model.dto.user.UserFollowFansRequest;
import com.zhu.model.entity.User;
import com.zhu.model.entity.UserFollow;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-11-02
 */
public interface IUserFollowService extends IService<UserFollow> {

    int doFollow(Long followId, Long uid);

    Map<String,List<String>> getUserFollowIds(Long uid);

    List<String> getUserFansIds(Long uid);

    Page<User> getUserFollowFans(UserFollowFansRequest userFollowFansRequest, Long uid);

}
