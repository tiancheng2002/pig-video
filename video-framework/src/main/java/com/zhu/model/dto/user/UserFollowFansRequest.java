package com.zhu.model.dto.user;

import lombok.Data;

/**
 *  用户关注或者粉丝用户列表请求类
 */
@Data
public class UserFollowFansRequest {

    private String searchText;

    private String type;

    private long current;

    private long pageSize;

}
