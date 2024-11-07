package com.zhu.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentLikesRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long videoId;

}
