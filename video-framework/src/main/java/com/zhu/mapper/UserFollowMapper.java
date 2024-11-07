package com.zhu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.model.entity.User;
import com.zhu.model.entity.UserFollow;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2023-11-02
 */
public interface UserFollowMapper extends BaseMapper<UserFollow> {

    Page<User> gerUserFollowFans(Page<User> userPage, @Param("searchText") String searchText,@Param("type") String type,@Param("uid") Long uid);

}
