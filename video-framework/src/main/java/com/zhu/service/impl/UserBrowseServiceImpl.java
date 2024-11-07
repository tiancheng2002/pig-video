package com.zhu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.constant.RedisConstant;
import com.zhu.mapper.UserBrowseMapper;
import com.zhu.model.entity.UserBrowse;
import com.zhu.service.IUserBrowseService;
import com.zhu.utils.RedisUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-12-04
 */
@Service
public class UserBrowseServiceImpl extends ServiceImpl<UserBrowseMapper, UserBrowse> implements IUserBrowseService {

    @Resource
    private RedisUtils redisUtils;

    @Override
    public int actionBrowse(Long videoId, Long uid) {
        QueryWrapper<UserBrowse> userBrowseQueryWrapper = new QueryWrapper<>();
        userBrowseQueryWrapper.eq("video_id",videoId);
        userBrowseQueryWrapper.eq("uid",uid);
        UserBrowse userBrowse = getOne(userBrowseQueryWrapper);
        boolean result;
        if(userBrowse!=null){
            userBrowse.setBrowseTime(new Date());
            result = updateById(userBrowse);
        }else{
            userBrowse = new UserBrowse();
            userBrowse.setVideoId(videoId);
            userBrowse.setUid(uid);
            result = save(userBrowse);
        }
        //将该视频浏览量加一,并且需要更新浏览记录
        redisUtils.hincr(RedisConstant.VideoPlayNumKey,String.valueOf(videoId), RedisConstant.incrNum);
        return result?1:0;
    }

}
