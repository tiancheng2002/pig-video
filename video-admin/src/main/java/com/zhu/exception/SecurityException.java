package com.zhu.exception;

import com.zhu.result.RespBeanEnum;
import org.springframework.security.core.AuthenticationException;

public class SecurityException extends AuthenticationException {

    RespBeanEnum respBeanEnum;

    public SecurityException(String msg) {
        super(msg);
    }

    public SecurityException(RespBeanEnum respBeanEnum) {
        super(respBeanEnum.getMsg());
        this.respBeanEnum = respBeanEnum;
    }

    public SecurityException(String msg, RespBeanEnum respBeanEnum) {
        super(msg);
        this.respBeanEnum = respBeanEnum;
    }

    public RespBeanEnum getRespBeanEnum() {
        return respBeanEnum;
    }

}
