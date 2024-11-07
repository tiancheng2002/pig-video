package com.zhu.model.dto.danmu;

import lombok.Data;

import java.io.Serializable;

@Data
public class DanmuAddRequest implements Serializable {

    private Long uid;

    private Long videoId;

    private String content;

    private Float createTime;

}
