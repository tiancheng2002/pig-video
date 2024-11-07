package com.zhu.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long uid;

    private String userEmail;

    private String userNickname;

    private String userAvatar;

    private String userProfile;

    private Integer sex;

    private Long follows;

    private Long fans;

    private Long favourTotal;

    private String userRole;

}
