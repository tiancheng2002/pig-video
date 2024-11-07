package com.zhu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhu.model.entity.UserRating;
import org.apache.ibatis.annotations.MapKey;

import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2023-12-05
 */
public interface UserRatingMapper extends BaseMapper<UserRating> {

    @MapKey("uid")
    Map<Long, Map<Long,Object>> getUserRatings();

}
