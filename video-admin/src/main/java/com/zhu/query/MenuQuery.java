package com.zhu.query;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MenuQuery {

    private Long menuId;

    private String menuName;

    private Integer status;

    public MenuQuery(){
        this.menuId = 0L;
    }

}
