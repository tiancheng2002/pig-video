package com.zhu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhu.model.entity.Menu;
import com.zhu.model.vo.MenuVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2022-09-02
 */
@Mapper
@Repository
public interface MenuMapper extends BaseMapper<Menu> {

    List<MenuVo> getMenu(@Param("menuId") Long menuId, @Param("menuName") String menuName, @Param("myStatus") Integer status);

}
