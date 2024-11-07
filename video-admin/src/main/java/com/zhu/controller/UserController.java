package com.zhu.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zhu.exception.ErrorException;
import com.zhu.query.UserQuery;
import com.zhu.model.entity.User;
import com.zhu.model.vo.PasswordVo;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IUserService;
import com.zhu.utils.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2022-08-22
 */
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private IUserService userService;

//    @Autowired
//    private IRoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /*
        查询全部用户
     */
    @PostMapping("/all")
    public List<User> userList(@RequestBody(required = false) UserQuery userQuery){
        List<User> userList = userService.userSearch(userQuery);
        return userList;
    }

    @RequestMapping("/show")
    public User show(){
        User user = new User();
        user.setSex(0);
        user.setUsername("aaa");
        user.setPassword("123");
        return user;
    }


    /*
        用户详情信息
     */
    @RequestMapping("/detail")
    public User userDetail(@RequestParam("uid") Integer uid){
        return userService.getById(uid);
    }

    /*
        添加用户或者是修改用户
     */
    @RequestMapping("/action")
    public String action(@RequestBody User user){
//        System.out.println(user);
        if(user.getUid()==null){
            userService.save(user);
        }else{
            userService.updateById(user);
        }
        return "ok";
    }

    /*
        修改用户的密码
     */
    @RequestMapping("/password")
    public String password(PasswordVo passwordVo){
        User user = userService.getOne(new QueryWrapper<User>().eq("username", passwordVo.getUsername()));
        if(user==null){
            throw new ErrorException(RespBeanEnum.USER_NO);
        }
        if(!passwordEncoder.matches(passwordVo.getOldPassword(),user.getPassword())){
            //直接手动抛出异常
            throw new ErrorException(RespBeanEnum.PASSWORD);
        }
        boolean update = userService.update(new UpdateWrapper<User>().eq("username", passwordVo.getUsername()).setSql("password=" + JSON.toJSONString(passwordEncoder.encode(passwordVo.getNewPassword()))));
        if(!update){
            //$2a$10$R92iIQTjswSN2Yexg81z7emnn7HJS/ofSRJ7BjZL6xO0z94Zi0miC
            throw new ErrorException(RespBeanEnum.ACTION_ERROR);
        }
        return "修改密码成功";
    }

    /*
        删除用户
     */
    @PostMapping("/del")
    public String delUser(@RequestBody List<Integer> selectionList){
        userService.removeBatchByIds(selectionList);
        return "ok";
    }

//    @RequestMapping("/perms")
//    public String perms(){
//        return roleService.getPerms(5L);
//    }

    @RequestMapping("/ip")
    public String getIp(HttpServletRequest request){
        String ipAddr = IpUtil.getIp(request);
        return ipAddr;
    }

}
