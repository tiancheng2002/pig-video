package com.zhu.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long uid;

}
