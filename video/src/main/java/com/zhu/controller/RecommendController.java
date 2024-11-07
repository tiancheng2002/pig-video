package com.zhu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.exception.ErrorException;
import com.zhu.model.dto.video.VideoRecommendRequest;
import com.zhu.recommend.core.UserBasedRecommend;
import com.zhu.recommend.service.RecommendService;
import com.zhu.result.RespBean;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IUserRatingService;
import com.zhu.service.IUserService;
import com.zhu.vo.LoginUserVo;
import com.zhu.vo.VideoVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Resource
    private RecommendService recommendService;

    @Resource
    private IUserService userService;

    @Resource
    private IUserRatingService userRatingService;

    @GetMapping("/rate")
    public RespBean getRating(){
        UserBasedRecommend userBasedRecommend = new UserBasedRecommend(userRatingService.list(),1717008431916822529L);
        List<Long> longs = userBasedRecommend.recommendForUser();
        return RespBean.success(longs);
    }

    @PostMapping("/")
    @ApiOperation(value = "个性化视频推荐")
    public RespBean recommendVideo(@RequestBody VideoRecommendRequest videoRecommendRequest, HttpServletRequest request) {
        // todo 还可以基于类型进行推荐，比如说看某个类型的视频多一点，就多推荐点该类型的视频
        LoginUserVo loginUser = userService.getLoginUser(request);
        long pageSize = videoRecommendRequest.getPageSize();
        Page<VideoVo> videoVoPage;
        if(loginUser!=null){
            //登录用户按照个性化推荐
            // todo 如果评分表中没有该用户数据的话就会报错，没有评分数据按照随机推荐
            videoVoPage = recommendService.videoRecommend(loginUser.getUid(),request);
//            videoVoPage = recommendService.recommend(videoRecommendRequest,loginUser.getUid(),request);
        }else{
            //没登录用户随机推荐视频
            //todo 并且只给显示20个视频，登录后可查看更多视频
            if(pageSize>20){
                throw new ErrorException(RespBeanEnum.USER_NEED_LOGIN);
            }
            videoVoPage = recommendService.randomRecommend(videoRecommendRequest, request);
        }
        return RespBean.success(videoVoPage);
    }

}
