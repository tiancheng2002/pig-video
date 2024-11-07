package com.zhu.vo;

import com.zhu.model.entity.Message;
import com.zhu.model.entity.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class MessageVo extends Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;

    private MessageVideoVo messageVideo;

}
