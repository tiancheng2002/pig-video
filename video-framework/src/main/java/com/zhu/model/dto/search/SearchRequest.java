package com.zhu.model.dto.search;

import com.zhu.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class SearchRequest extends PageRequest implements Serializable {

    private String searchKey;

    private Long typeId;

    private String type;

    private Integer publishTime;

    private Integer sortType;

}
