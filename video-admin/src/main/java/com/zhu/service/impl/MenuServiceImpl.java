package com.zhu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.mapper.MenuMapper;
import com.zhu.model.entity.Menu;
import com.zhu.model.vo.MenuVo;
import com.zhu.query.MenuQuery;
import com.zhu.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-09-02
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<MenuVo> getMenu(MenuQuery menuQuery) {
        List<MenuVo> menuVoList = menuMapper.getMenu(menuQuery.getMenuId(),menuQuery.getMenuName(),menuQuery.getStatus());
        List<MenuVo> menuVos = menuVoList;
        for (MenuVo menuVo:menuVoList){
            menuVos = menuVos.stream().filter(v->v.getParentId()!=menuVo.getMenuId()).collect(Collectors.toList());
        }
        return menuVos;
    }

    @Override
    public void removeTree(List<Long> idList) {
        //获取该id下的所有子数据的id
        selectChildren(idList.get(0),idList);
        System.out.println(idList);
        //将所有的子数据以及本身都删除
//        baseMapper.deleteBatchIds(idList);
    }

    public void selectChildren(Long did,List<Long> idList){
        //根据pid获取到下等级的子数据
        List<Menu> deptList = baseMapper.selectList(new QueryWrapper<Menu>().eq("parent_id", did).select("menu_id"));
        //遍历子数据，将id添加到集合当中，并且递归的获取
        deptList.forEach(item->{
            idList.add(item.getMenuId());
            selectChildren(item.getMenuId(),idList);
        });
    }

}
