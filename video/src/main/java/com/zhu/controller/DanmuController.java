package com.zhu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhu.annotation.AuthCheck;
import com.zhu.exception.ErrorException;
import com.zhu.model.dto.danmu.DanmuAddRequest;
import com.zhu.model.entity.Danmu;
import com.zhu.model.entity.VideoInfo;
import com.zhu.result.RespBean;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IDanmuService;
import com.zhu.service.IUserService;
import com.zhu.service.IVideoInfoService;
import com.zhu.utils.BeanCopeUtils;
import com.zhu.vo.LoginUserVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2023-11-14
 */
@RestController
@RequestMapping("/danmu")
public class DanmuController {

    @Resource
    private IDanmuService danmuService;

    @Resource
    private IVideoInfoService videoInfoService;

    @Resource
    private IUserService userService;

    @GetMapping("/list")
    public RespBean getVideoDanmu(Long videoId){
        QueryWrapper<Danmu> danmuQueryWrapper = new QueryWrapper<>();
        danmuQueryWrapper.eq("video_id",videoId);
        List<Danmu> danmus = danmuService.list(danmuQueryWrapper);
        return RespBean.success(danmus);
    }

    @PostMapping("/add")
    @AuthCheck
    public RespBean addDanmu(@RequestBody DanmuAddRequest danmuAddRequest, HttpServletRequest request){
        if(danmuAddRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
        Long videoId = danmuAddRequest.getVideoId();
        VideoInfo video = videoInfoService.getById(videoId);
        if(video==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        Danmu danmu = BeanCopeUtils.copyBean(danmuAddRequest, Danmu.class);
        danmu.setUid(loginUser.getUid());
        danmuService.save(danmu);
        return RespBean.success(danmu);
    }

}
