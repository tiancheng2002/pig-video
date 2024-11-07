package com.zhu.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userEmail;

    private String userPassword;

    private String userNickname;

    private String confirmPassword;

    private String keyCode;

}
