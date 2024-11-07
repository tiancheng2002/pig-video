package com.zhu.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageTypeVo implements Serializable {

    private boolean hasCommentsMessage;

    private boolean hasLikesMessage;

    private boolean hasFollowsMessage;

}
