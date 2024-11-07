package com.zhu.aop;

import com.zhu.annotation.AuthCheck;
import com.zhu.constant.UserConstant;
import com.zhu.exception.ErrorException;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IUserService;
import com.zhu.vo.LoginUserVo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验 AOP
 *
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private IUserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        LoginUserVo loginUser = userService.getLoginUser(request);
        // 必须有该权限才通过
        if(loginUser==null){
            throw new ErrorException(RespBeanEnum.USER_NO_AUTH);
        }
        //如果账号为封禁状态也不允许访问
        if(loginUser.getUserRole()== UserConstant.BAN_ROLE){
            throw new ErrorException(RespBeanEnum.FORBIDDEN);
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

