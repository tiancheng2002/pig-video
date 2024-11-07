package com.zhu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.model.entity.Dict;
import com.zhu.model.vo.DictTypeVo;
import com.zhu.model.vo.DictVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-08-28
 */
public interface IDictService extends IService<Dict> {

    List<DictVo> getDictDetail();

    List<DictTypeVo> getDictByCode(String dictCode);

    List<Dict> getDict(Dict dict);
}
