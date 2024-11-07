package com.zhu.model.dto.video;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long videoId;

    private String description;

    private String videoUrl;

    private String videoPic;

    private String videoName;

    private Long typeId;

    private Long uid;

}
