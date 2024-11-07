package com.zhu.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhu.common.CommonEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-25
 */
@TableName("t_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends CommonEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long uid;

    private String userEmail;

    private String userPassword;

    private String userNickname;

    private String userAvatar;

    private String userProfile;

    private Integer sex;

    private String userRole;

    @TableField(value = "is_hiddenFavour")
    private Boolean isHiddenFavour;

}
