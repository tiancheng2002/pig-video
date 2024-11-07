package com.zhu.model.dto.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageUpdateRequest implements Serializable {

    private String type;

}
