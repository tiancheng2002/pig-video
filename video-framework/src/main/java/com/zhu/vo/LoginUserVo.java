package com.zhu.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LoginUserVo implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long uid;

    private String userNickname;

    private String userAvatar;

    private Integer sex;

    private String userRole;

    private Date createTime;

    private Date updateTime;

}
