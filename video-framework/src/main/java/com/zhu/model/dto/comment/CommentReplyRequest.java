package com.zhu.model.dto.comment;

import lombok.Data;

@Data
public class CommentReplyRequest {

    private Long parentId;

    private Long current;

    private Long pageSize;

}
