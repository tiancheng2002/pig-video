package com.zhu.recommend.core;

import com.zhu.model.entity.UserRating;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 核心算法
 *
 * @author tarzan
 * @version 1.0
 * @date 2020/7/31$ 15:21$
 * @since JDK1.8
 */
public class UserCF {

    /**
     * 方法描述: 推荐电影id列表
     *
     * @param userId 当前用户
     * @param list 用户电影评分数据
     * @return {@link List < Integer >}
     * @date 2023年02月02日 14:51:42
     */
    public static List<Long> recommend(Long userId, List<UserRating> list) {
        //按用户分组
        Map<Long, List<UserRating>> userMap=list.stream().collect(Collectors.groupingBy(UserRating::getUid));
        //获取其他用户与当前用户的关系值
        Map<Long, Double> userDisMap = CoreMath.computeNeighbor(userId, userMap,0);
        //获取关系最近的用户
        double maxValue= Collections.max(userDisMap.values());
        Set<Long> userIds=userDisMap.entrySet().stream().filter(e->e.getValue()==maxValue).map(Map.Entry::getKey).collect(Collectors.toSet());
        //取关系最近的用户
        Long nearestUserId = userIds.stream().findAny().orElse(null);
        if(nearestUserId==null){
            return Collections.emptyList();
        }
        //最近邻用户看过电影列表
        List<Long> neighborItems = userMap.get(nearestUserId).stream().map(UserRating::getVideoId).collect(Collectors.toList());
        //指定用户看过电影列表
        List<Long> userItems  = userMap.get(userId).stream().map(UserRating::getVideoId).collect(Collectors.toList());
        //找到最近邻看过，但是该用户没看过的电影
        neighborItems.removeAll(userItems);
        return neighborItems;
    }

}
