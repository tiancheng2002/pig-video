package com.zhu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.model.entity.Comment;
import com.zhu.vo.CommentVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-27
 */
public interface CommentMapper extends BaseMapper<Comment> {

    List<CommentVo> getVideoComment(@Param("videoId") Long videoId);

    Page<CommentVo> getReply(Page<CommentVo> page, @Param("id") Long id);

}
