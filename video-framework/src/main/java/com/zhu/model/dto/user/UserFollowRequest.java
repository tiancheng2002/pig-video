package com.zhu.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserFollowRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long followId;

}
