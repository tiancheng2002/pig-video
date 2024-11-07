package com.zhu.exception;

import com.zhu.result.RespBeanEnum;
import org.springframework.security.core.AuthenticationException;

public class JwtException extends AuthenticationException {

    RespBeanEnum respBeanEnum;

    public JwtException(String msg) {
        super(msg);
    }

    public JwtException(String msg, RespBeanEnum respBeanEnum) {
        super(msg);
        this.respBeanEnum = respBeanEnum;
    }

    public RespBeanEnum getRespBeanEnum() {
        return respBeanEnum;
    }
}
