package com.zhu.model.dto.video;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoFavourRequest implements Serializable {

    private Long videoId;

    private static final long serialVersionUID = 1L;

}
