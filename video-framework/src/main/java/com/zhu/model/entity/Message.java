package com.zhu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * @since 2023-11-13
 */
@TableName("t_message")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer messageType;

    private Long videoId;

    private String messageContent;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long senderId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long receiverId;

    private Boolean isDelivered;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
