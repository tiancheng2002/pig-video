package com.zhu.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhu.annotate.Dict;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaozhu
 * @since 2022-08-22
 */
@TableName("sys_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "uid", type = IdType.AUTO)
    private Integer uid;

    private String username;

    private String password;

    private String head;

    @Dict(code = "user_gender")
    private Integer sex;

    private String phone;

    private String email;

    @Dict(code = "user_status")
    private Integer status;

    @TableField(value = "roleId")
    private Long roleId;

}
