package com.zhu.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthorInfoVo extends UserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    //是否隐藏喜欢列表
    private Boolean isHiddenFavour;

    //发布作品数量
    private Long worksNum;

    //喜欢作品数量
    private Long favoursNum;

}
