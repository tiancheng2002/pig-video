package com.zhu.vo;

import com.zhu.model.entity.Comment;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ReplyVo {

    private Long id;

    private Integer total;

    private List<Comment> replies = Collections.EMPTY_LIST;

}
