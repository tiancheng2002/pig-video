package com.zhu.config;

import com.zhu.constant.RedisConstant;
import com.zhu.model.entity.VideoInfo;
import com.zhu.service.IVideoInfoService;
import com.zhu.utils.RedisUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Configuration
@EnableScheduling
public class ScheduleHandler {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private IVideoInfoService videoInfoService;

    @Scheduled(cron = "0 0/1  * * * ?")
    private void updatePlayNum(){
        //每一分钟更新一次浏览量
        Map<Object, Object> playNums = redisUtils.hmget(RedisConstant.VideoPlayNumKey);
        Iterator<Map.Entry<Object, Object>> iterator =playNums.entrySet().iterator();
        List<VideoInfo> videoInfos = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<Object, Object> item = iterator.next();
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setVideoId(Long.parseLong((String) item.getKey()));
            videoInfo.setPlayNum(Long.parseLong(String.valueOf(item.getValue())));
            videoInfos.add(videoInfo);
        }
        videoInfoService.updateBatchById(videoInfos);
    }

}
