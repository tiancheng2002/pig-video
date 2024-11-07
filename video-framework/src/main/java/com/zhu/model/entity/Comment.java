package com.zhu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-27
 */
@TableName("t_comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String respondentName;

    private String address;

    private String content;

    private Long likes;

    private Long uid;

    private Long videoId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private Integer isDelete;

}
