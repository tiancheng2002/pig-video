package com.zhu.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2022-09-03
 */
@TableName("sys_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "roleId",type = IdType.AUTO)
    private Long roleId;

    private String name;

    private String authority;

    private Integer status;

}
