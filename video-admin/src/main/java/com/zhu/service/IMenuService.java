package com.zhu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.model.entity.Menu;
import com.zhu.model.vo.MenuVo;
import com.zhu.query.MenuQuery;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-09-02
 */
public interface IMenuService extends IService<Menu> {

    List<MenuVo> getMenu(MenuQuery menuQuery);

    void removeTree(List<Long> idList);
}
