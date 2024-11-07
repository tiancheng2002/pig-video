package com.zhu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.mapper.UserFollowMapper;
import com.zhu.model.dto.user.UserFollowFansRequest;
import com.zhu.model.entity.User;
import com.zhu.model.entity.UserFollow;
import com.zhu.model.enums.MessageTypeEnums;
import com.zhu.service.IUserFollowService;
import com.zhu.utils.MessageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-11-02
 */
@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements IUserFollowService {

    @Resource
    private MessageUtils messageUtils;

    @Override
    public int doFollow(Long followId, Long uid) {
        UserFollow userFollow = new UserFollow();
        userFollow.setUserId(uid);
        userFollow.setFollowUserId(followId);
        QueryWrapper<UserFollow> userFollowQueryWrapper = new QueryWrapper<>(userFollow);
        UserFollow userFollowData = this.getOne(userFollowQueryWrapper);
        Boolean result;
        if(userFollowData==null){
            result = this.save(userFollow);
            if(result){
                messageUtils.sendMessageToUser(uid,followId,null, MessageTypeEnums.FOLLOW);
            }
        }else{
            result = this.remove(userFollowQueryWrapper);
        }
        return result?1:0;
    }

    @Override
    public Map<String,List<String>> getUserFollowIds(Long uid) {
        Map<String,List<String>> result = new HashMap<>();
        QueryWrapper<UserFollow> userFollowQueryWrapper = new QueryWrapper<>();
        userFollowQueryWrapper.eq("user_id",uid);
        userFollowQueryWrapper.select("follow_user_id");
        List<Object> listObjs = this.listObjs(userFollowQueryWrapper);
        List<String> followIds = listObjs.stream().map(o -> String.valueOf(o)).collect(Collectors.toList());
        List<String> fansIds = getUserFansIds(uid);
        result.put("followIds",followIds);
        result.put("fansIds",fansIds);
        return result;
    }

    @Override
    public List<String> getUserFansIds(Long uid) {
        QueryWrapper<UserFollow> userFollowQueryWrapper = new QueryWrapper<>();
        userFollowQueryWrapper.eq("follow_user_id",uid);
        userFollowQueryWrapper.select("user_id");
        List<Object> listObjs = this.listObjs(userFollowQueryWrapper);
        List<String> followIds = listObjs.stream().map(o -> String.valueOf(o)).collect(Collectors.toList());
        return followIds;
    }

    @Override
    public Page<User> getUserFollowFans(UserFollowFansRequest userFollowFansRequest, Long uid) {
        String searchText = userFollowFansRequest.getSearchText();
        String type = userFollowFansRequest.getType();
        long current = userFollowFansRequest.getCurrent();
        long pageSize = userFollowFansRequest.getPageSize();
        Page<User> userPageList = this.baseMapper.gerUserFollowFans(new Page<>(current, pageSize), searchText, type, uid);
        return userPageList;
    }

}
