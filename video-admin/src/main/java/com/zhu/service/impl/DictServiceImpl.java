package com.zhu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.mapper.DictMapper;
import com.zhu.model.entity.Dict;
import com.zhu.model.vo.DictTypeVo;
import com.zhu.model.vo.DictVo;
import com.zhu.service.IDictService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-08-28
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {

    @Override
    public List<DictVo> getDictDetail() {
        return baseMapper.getDictDetail();
    }

    @Override
    public List<DictTypeVo> getDictByCode(String dictCode) {
        return baseMapper.getDictByCode(dictCode);
    }

    @Override
    public List<Dict> getDict(Dict dict) {
        return baseMapper.getDict(dict);
    }
}
