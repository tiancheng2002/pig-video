package com.zhu.model.dto.user;

import lombok.Data;

@Data
public class UserVideoRequest {

    private Long uid;

    private String type;

    private Long current;

    private Long pageSize;

}
