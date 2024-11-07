package com.zhu.controller;

import com.zhu.annotation.AuthCheck;
import com.zhu.exception.ErrorException;
import com.zhu.model.dto.video.VideoFavourRequest;
import com.zhu.result.RespBean;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IUserService;
import com.zhu.service.IVideoFavourService;
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
@RequestMapping("/videoFavour")
public class VideoFavourController {

    @Resource
    private IVideoFavourService videoFavourService;

    @Resource
    private IUserService userService;

    /**
     * 用户点赞视频操作
     * @param videoFavourRequest
     * @return
     */
    @PostMapping("/")
    @AuthCheck
    public RespBean doVideoFavour(@RequestBody VideoFavourRequest videoFavourRequest, HttpServletRequest request){
        if(videoFavourRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
        Long videoId = videoFavourRequest.getVideoId();
        int result = videoFavourService.doVideoFavour(videoId, loginUser.getUid());
        return RespBean.success(result);
    }


}
