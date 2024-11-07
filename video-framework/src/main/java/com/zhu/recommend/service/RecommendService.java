package com.zhu.recommend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.constant.CommonConstant;
import com.zhu.model.dto.video.VideoRecommendRequest;
import com.zhu.model.entity.UserRating;
import com.zhu.model.entity.VideoInfo;
import com.zhu.recommend.core.UserBasedRecommend;
import com.zhu.recommend.core.UserCF;
import com.zhu.service.IUserRatingService;
import com.zhu.service.IVideoInfoService;
import com.zhu.vo.VideoVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RecommendService {

    @Resource
    private IUserRatingService userRatingService;

    @Resource
    private IVideoInfoService videoInfoService;

    public Page<VideoVo> videoRecommend(Long uid, HttpServletRequest request){
        List<UserRating> userRatings = userRatingService.list();
        //为用户生成个性化推荐视频
        UserBasedRecommend userBasedRecommend = new UserBasedRecommend(userRatings, uid);
        List<Long> videoIds = userBasedRecommend.recommendForUser();
        //如果个性化推荐数量为0的话，第一种情况可能是新用户，第二种情况是没有相似的用户
        //这个时候我们可以随机推荐或者按照分类兴趣推荐
        if(videoIds.size()==0){
            return randomRecommend(new VideoRecommendRequest(),request);
        }
        //如果直接调用listByIds会打乱id列表的顺序，所以我们需要进行查询处理
        QueryWrapper<VideoInfo> videoInfoQueryWrapper = new QueryWrapper<>();
        videoInfoQueryWrapper.in("video_id", videoIds);
        Map<Long, List<VideoInfo>> userIdUserListMap = videoInfoService.list(videoInfoQueryWrapper)
                .stream()
                .collect(Collectors.groupingBy(VideoInfo::getVideoId));
        //把查询出来的视频数据添加到列表当中
        Page<VideoInfo> videoInfoPage = new Page<>(1, videoIds.size());
        List<VideoInfo> videoInfoList = new ArrayList<>();
        for (Long videoId:videoIds){
            videoInfoList.add(userIdUserListMap.get(videoId).get(0));
        }
        videoInfoPage.setRecords(videoInfoList);
        //获取视频的作者、是否点赞、是否收藏等信息
        Page<VideoVo> videoVoPage = videoInfoService.getVideoVoOther(videoInfoPage, CommonConstant.NOT_ONLY_AUTHOR, request);
        return videoVoPage;
    }

    public Page<VideoVo> recommend(VideoRecommendRequest videoRecommendRequest, Long uid, HttpServletRequest request){
        List<UserRating> userRatings = userRatingService.list();
        List<Long> videoIds = UserCF.recommend(uid, userRatings);
        Page<VideoInfo> videoInfoPage = new Page<>(videoRecommendRequest.getCurrent(), videoRecommendRequest.getPageSize());
        List<VideoInfo> videoInfos = videoInfoService.listByIds(videoIds);
        videoInfoPage.setRecords(videoInfos);
        return videoInfoService.getVideoVoOther(videoInfoPage, CommonConstant.NOT_ONLY_AUTHOR, request);
    }

    public Page<VideoVo> randomRecommend(VideoRecommendRequest videoRecommendRequest, HttpServletRequest request){
        // todo 随机推荐视频
        Page<VideoVo> videoVoList = videoInfoService.getVideoByRandom(videoRecommendRequest, request);
        return videoVoList;
    }

}
