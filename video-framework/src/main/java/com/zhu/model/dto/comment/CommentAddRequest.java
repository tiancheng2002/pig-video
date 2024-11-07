package com.zhu.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentAddRequest implements Serializable {

    private Long parentId;

    private String respondentName;

    private String address;

    private String content;

    private Long uid;

    private Long receiverId;

    private Long videoId;

}
