package com.zhu.model.dto.message;

import com.zhu.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class MessageQueryRequest extends PageRequest implements Serializable {

    private String type;

}
