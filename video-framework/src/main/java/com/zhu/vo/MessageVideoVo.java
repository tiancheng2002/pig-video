package com.zhu.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageVideoVo implements Serializable {

    private Long videoId;

    private String description;

    private String videoPic;

}
