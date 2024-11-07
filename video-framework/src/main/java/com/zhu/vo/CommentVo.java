package com.zhu.vo;

import com.zhu.model.entity.Comment;
import com.zhu.model.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo extends Comment {

    private boolean isFold;

    private User user;

    private List<Long> likeList;

    private ReplyVo replyVo;

}
