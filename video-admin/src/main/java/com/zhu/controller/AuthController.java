package com.zhu.controller;

import com.wf.captcha.ArithmeticCaptcha;
import com.zhu.model.vo.CaptchaVo;
import com.zhu.utils.JwtUtils;
import com.zhu.utils.RedisUtils;
import com.zhu.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class AuthController {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private JwtUtils jwtUtils;

    @RequestMapping("/captcha")
    public CaptchaVo captcha(@RequestParam(value = "key", required = false) String key, HttpServletRequest request, HttpServletResponse response) {
        if (key != null) {
            redisUtils.remove("captcha:" + key);
        }
        //获取验证码
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码，将验证码放入redis当中
        ArithmeticCaptcha captch = new ArithmeticCaptcha(110, 38, 3);
        //随机生成一个UUID，然后将UUID和验证码进行绑定存储到Redis当中，并把UUID返回到前端中
        String uuid = UUIDUtils.getUUID();
//        String ip = IpUtil.getIpAddr(request);
        redisUtils.set("captcha:" + uuid, captch.text(), 300);
        //            captch.out(response.getOutputStream());
        String base64 = captch.toBase64();
        return new CaptchaVo(base64, uuid);
    }

//    @RequestMapping("/login")
//    public String doLogin(HttpServletResponse response){
//        String jwt = jwtUtils.generateToken("abc");
//        response.setHeader("jwt",jwt);
//        return jwt;
//    }

    @PreAuthorize("hasAuthority('sys:user:list')")
    @RequestMapping("/test01")
    public String test01() {
        return "test01";
    }

    @PreAuthorize("hasAuthority('sys:user:app')")
    @RequestMapping("/test02")
    public String test02() {
        return "test02";
    }

    @RequestMapping("/test")
    public void test() {
        throw new InternalAuthenticationServiceException("错误");
    }

}
