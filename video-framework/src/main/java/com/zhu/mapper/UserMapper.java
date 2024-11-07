package com.zhu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.model.entity.User;
import com.zhu.vo.AuthorInfoVo;
import com.zhu.vo.UserVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-25
 */
public interface UserMapper extends BaseMapper<User> {

    Page<UserVo> getUserVOList(Page<UserVo> userVoPage, @Param("searchKey") String searchKey);

    AuthorInfoVo getUserInfo(Long uid);

}
