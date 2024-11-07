package com.zhu.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2022-09-02
 */
@TableName("sys_menu")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Menu extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    private String name;

    private Long parentId;

    private String path;

    private String component;

    private String perms;

    private String icon;

    private Integer type;

    private Boolean isExternal;

    @TableField(value = "orderNum")
    private Integer orderNum;

    private Integer status;

}
