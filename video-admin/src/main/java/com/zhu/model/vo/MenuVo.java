package com.zhu.model.vo;

import com.zhu.model.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MenuVo extends Menu {

    private List<Menu> children;

}
