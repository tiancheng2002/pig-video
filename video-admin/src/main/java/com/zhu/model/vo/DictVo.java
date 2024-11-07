package com.zhu.model.vo;

import com.zhu.model.entity.DictData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DictVo {

    private String code;

    private List<DictData> dictDataList;

}
