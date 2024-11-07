package com.zhu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.model.dto.comment.CommentAddRequest;
import com.zhu.model.dto.comment.CommentReplyRequest;
import com.zhu.model.entity.Comment;
import com.zhu.model.entity.CommentFavour;
import com.zhu.vo.CommentVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-27
 */
public interface ICommentService extends IService<Comment> {

    List<CommentVo> getVideoComment(Long videoId);

    Page<CommentVo> getMoreReply(CommentReplyRequest commentReplyRequest);

    int doFavour(CommentFavour commentFavour);

    List<Long> getCommentLikesList(Long videoId, Long uid);

    int doCommentFavourInner(CommentFavour commentFavour,Long authorId);

    Comment addComment(CommentAddRequest commentAddRequest, HttpServletRequest request, Long uid);

    int delComment(Comment comment, Long delCommentId);

}
