package com.zhu.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserEditRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long uid;

    private String userAvatar;

    private String userNickname;

    private String userProfile;

}
