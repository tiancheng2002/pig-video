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
        if(e.getMessage().contains("Duplicate entry")){
            String[] result = e.getMessage().split(" ");
            return RespBean.error(RespBeanEnum.ERROR,result[2]+"已存在");
        }
        return RespBean.error(RespBeanEnum.ERROR);
    }

    @ExceptionHandler({ErrorException.class})
    public RespBean error(ErrorException e){
        return RespBean.error(e.getRespBeanEnum());
    }

}
