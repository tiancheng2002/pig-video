package com.zhu.controller;

import com.zhu.annotation.AuthCheck;
import com.zhu.exception.ErrorException;
import com.zhu.model.dto.video.VideoStarRequest;
import com.zhu.result.RespBean;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IUserService;
import com.zhu.service.IVideoStarService;
import com.zhu.vo.LoginUserVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-31
 */
@RestController
@RequestMapping("/videoStar")
public class VideoStarController {

    @Resource
    private IVideoStarService videoStarService;

    @Resource
    private IUserService userService;

    @PostMapping("/")
    @AuthCheck
    public RespBean doVideoStar(@RequestBody VideoStarRequest videoStarRequest, HttpServletRequest request){
        if(videoStarRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
        Long videoId = videoStarRequest.getVideoId();
        int result = videoStarService.doVideoStar(videoId, loginUser.getUid());
        return RespBean.success(result);
    }

}
