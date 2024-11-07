package com.zhu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhu.model.entity.Dict;
import com.zhu.model.vo.DictTypeVo;
import com.zhu.model.vo.DictVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2022-08-28
 */
@Mapper
@Repository
public interface DictMapper extends BaseMapper<Dict> {

    List<DictVo> getDictDetail();

    List<DictTypeVo> getDictByCode(@Param("dictCode") String dictCode);

    List<Dict> getDict(Dict dict);

}
