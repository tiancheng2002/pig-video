package com.zhu.model.dto.video;

import lombok.Data;

@Data
public class VideoTypeRequest {

    private Long type;

    private long current;

    private long pageSize;

}
