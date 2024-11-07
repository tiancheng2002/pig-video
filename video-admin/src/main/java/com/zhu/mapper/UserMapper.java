package com.zhu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhu.query.UserQuery;
import com.zhu.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2022-08-29
 */
@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {

    List<User> userSearch(UserQuery userQuery);

}
