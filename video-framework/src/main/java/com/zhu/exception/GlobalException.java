package com.zhu.exception;

import com.zhu.result.RespBean;
import com.zhu.result.RespBeanEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public RespBean uniqueData(SQLIntegrityConstraintViolationException e){
        return RespBean.error(RespBeanEnum.EMAIL_EXIST);
    }

    @ExceptionHandler({ErrorException.class})
    public RespBean handleError(ErrorException e){
        return RespBean.error(e.getRespBeanEnum());
    }

}
