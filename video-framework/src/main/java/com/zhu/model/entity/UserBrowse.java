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
 * @since 2023-12-04
 */
@TableName("t_user_browse")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBrowse implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long videoId;

    private Long uid;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date browseTime;

}
