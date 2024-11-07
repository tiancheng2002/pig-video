package com.zhu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhu.model.entity.Type;
import com.zhu.result.RespBean;
import com.zhu.service.ITypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-29
 */
@RestController
@RequestMapping("/type")
public class TypeController {

    @Resource
    private ITypeService typeService;

    /**
     * 获取所有分类数据
     * @return
     */
    @GetMapping("/list")
    public RespBean getListType(){
        List<Type> typeList = typeService.list(new QueryWrapper<Type>().eq("status", 0));
        return RespBean.success(typeList);
    }

}
