package com.zhu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.annotation.AuthCheck;
import com.zhu.exception.ErrorException;
import com.zhu.model.dto.user.UserFollowFansRequest;
import com.zhu.model.dto.user.UserFollowRequest;
import com.zhu.model.entity.User;
import com.zhu.result.RespBean;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IUserFollowService;
import com.zhu.service.IUserService;
import com.zhu.vo.LoginUserVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2023-11-02
 */
@RestController
@RequestMapping("/userFollow")
public class UserFollowController {

    @Resource
    private IUserFollowService userFollowService;

    @Resource
    private IUserService userService;

    @PostMapping("/")
    @AuthCheck
    public RespBean userFollow(@RequestBody UserFollowRequest userFollowRequest, HttpServletRequest request){
        if(userFollowRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        //首先会去数据库中查询是否关注该用户（如果追求性能的话可以通过缓存）
        //如果关注过该用户的话那么就将数据库中的记录进行删除，否则就添加对应的记录
        LoginUserVo loginUser = userService.getLoginUser(request);
        Long uid = loginUser.getUid();
        Long followId = userFollowRequest.getFollowId();
        int result = userFollowService.doFollow(followId, uid);
        return RespBean.success(result);
    }

    /**
     * 获取用户关注列表的id列表
     * @param request
     * @return
     */
    @PostMapping("/list")
    public RespBean getUserFollowsWithId(HttpServletRequest request){
        if(request==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
        Map<String,List<String>> followIds = new HashMap<>();
        if(loginUser!=null){
            followIds = userFollowService.getUserFollowIds(loginUser.getUid());
        }
        return RespBean.success(followIds);
    }

    /**
     * 根据传递的type获取用户关注或者粉丝列表数据
     * @param userFollowFansRequest
     * @param request
     * @return
     */
    @PostMapping("/user/followsFans")
    public RespBean getUserFollowsWithUser(@RequestBody UserFollowFansRequest userFollowFansRequest,HttpServletRequest request){
        //todo 需要判断该作者是否开启查看关注或粉丝列表数据
        if(userFollowFansRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
        if(loginUser==null){
            throw new ErrorException(RespBeanEnum.USER_UN_LOGIN);
        }
        Page<User> userListPage = userFollowService.getUserFollowFans(userFollowFansRequest,loginUser.getUid());
        return RespBean.success(userListPage);
    }

//    @PostMapping("/user/fans")
//    public RespBean getUserFansWithUser(HttpServletRequest request){
//        if(request==null){
//            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
//        }
//        LoginUserVo loginUser = userService.getLoginUser(request);
//        List<User> fansUsers = new ArrayList<>();
//        if(loginUser!=null){
//            List<String> followIds = userFollowService.getUserFansIds(loginUser.getUid());
//            fansUsers = userService.listByIds(followIds);
//        }
//        return RespBean.success(fansUsers);
//    }

}
