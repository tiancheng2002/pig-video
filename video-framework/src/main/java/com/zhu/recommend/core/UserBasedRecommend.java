package com.zhu.recommend.core;

import com.zhu.model.entity.UserRating;

import java.util.*;
import java.util.stream.Collectors;

public class UserBasedRecommend {

    private Map<Long,Map<Long,Integer>> userVideoRating;

    private Long uid;

    private double similarityThreshold = 0.7; // 相似度阈值，可以根据实际情况调整

    private int recommendationCount = 10; // 推荐的视频数量，可以根据实际情况调整

    public UserBasedRecommend(List<UserRating> userRatings,Long uid){
        this.uid = uid;
        this.userVideoRating = convertUserRating(userRatings);
    }

    private Map<Long, Map<Long, Integer>> convertUserRating(List<UserRating> userRatings) {
        Map<Long,Map<Long,Integer>> map = new HashMap<>();
        //将list集合按照uid进行分组并转换成map集合
        Map<Long, List<UserRating>> collect = userRatings.stream().collect(Collectors.groupingBy(UserRating::getUid));
        //遍历map集合
        Iterator<Map.Entry<Long, List<UserRating>>> iter = collect.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry<Long, List<UserRating>> iterNext = iter.next();
            if(iterNext.getKey()!=uid){
                //将List集合转换成以videoId为键，rate为值的map集合
                Map<Long, Integer> longIntegerMap = iterNext.getValue()
                        .stream()
                        .collect(Collectors.toMap(UserRating::getVideoId, UserRating::getRate));
                map.put(iterNext.getKey(),longIntegerMap);
            }
        }
        return map;
    }

    public List<Long> recommendForUser() {
        Map<Long, Integer> ratings = userVideoRating.getOrDefault(uid,new HashMap<>());
        List<Long> watchedVideos = ratings.keySet().stream().collect(Collectors.toList());
        List<Long> recommendations = new ArrayList<>();

        // 获取与目标用户最相似的用户集合
        List<Long> mostSimilarUsers = getMostSimilarUsers(uid);

        // 根据最相似用户的评分推荐视频给目标用户，并过滤掉已经看过的视频
        for (Long similarUserId : mostSimilarUsers) {
            Map<Long, Integer> similarUserRatings = userVideoRating.get(similarUserId);
            for (Map.Entry<Long, Integer> ratingEntry : similarUserRatings.entrySet()) {
                Long videoId = ratingEntry.getKey();
                if (!watchedVideos.contains(videoId)) { // 过滤掉已经看过的视频
                    recommendations.add(videoId); // 将视频ID添加到推荐列表中，按照相似度排序，相似度越高越靠前
                    if (recommendations.size() >= recommendationCount) { // 达到推荐数量后停止推荐
                        break;
                    }
                }
            }
            if (recommendations.size() >= recommendationCount) { // 达到推荐数量后停止推荐其他相似用户
                break;
            }
        }
        return recommendations;
    }

    private List<Long> getMostSimilarUsers(Long userId) {
//        List<Map.Entry<Long, Integer>> otherUserRatings = StreamSupport.stream(userVideoRating.get(userId).entrySet().spliterator(), false)
//                .filter(entry -> !entry.getKey().equals(userId)) // 过滤掉目标用户自己的评分数据
//                .collect(Collectors.toList()); // 将其他用户的评分数据收集到一个列表中
        List<Long> mostSimilarUsers = new ArrayList<>(); // 存储最相似用户的列表
        double maxSimilarity = Double.NEGATIVE_INFINITY; // 最大相似度值，初始化为负无穷大
        // 计算目标用户与其他用户之间的相似度，并找到最相似用户集合和最大相似度值
        Iterator<Map.Entry<Long, Map<Long,Integer>>> iter = userVideoRating.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry<Long, Map<Long, Integer>> iterNext = iter.next();
            Long otherUserId = iterNext.getKey();
            double similarity = calculateSimilarity(userId, otherUserId); // 计算两个用户之间的相似度
            if (similarity > maxSimilarity && similarity > similarityThreshold) { // 如果相似度大于阈值，则将该用户添加到最相似用户集合中，并更新最大相似度值
                mostSimilarUsers.add(otherUserId);
                maxSimilarity = similarity;
            }
        }
        return mostSimilarUsers; // 返回最相似用户集合，按照相似度从高到低排序（由于我们使用的是HashMap，所以自然会有一个排序）
    }

    private double calculateSimilarity(Long userId1, Long userId2) {
        Map<Long, Integer> ratings1 = userVideoRating.getOrDefault(userId1,new HashMap<>()); // 获取用户1的评分数据
        Map<Long, Integer> ratings2 = userVideoRating.get(userId2); // 获取用户2的评分数据
        double sumXY = 0; // 存储用户1和用户2共同评分的视频的评分乘积之和
        double sumX2 = 0; // 存储用户1评分的视频的评分平方和（归一化因子）
        double sumY2 = 0; // 存储用户2评分的视频的评分平方和（归一化因子）
        int n = 0; // 记录用户1和用户2共同评分的视频数量（即皮尔逊相关系数的分母）

        // 遍历两个用户的评分数据，计算共同评分的视频数量和评分乘积之和
        for (Map.Entry<Long, Integer> entry : ratings1.entrySet()) {
            Long videoId = entry.getKey();
            if (ratings2.containsKey(videoId)) { // 如果两个用户都对该视频进行了评分
                double rating1 = entry.getValue();
                double rating2 = ratings2.get(videoId);
                sumXY += rating1 * rating2; // 计算评分乘积之和
                sumX2 += Math.pow(rating1, 2); // 计算用户1评分的视频的评分平方和
                sumY2 += Math.pow(rating2, 2); // 计算用户2评分的视频的评分平方和
                n++; // 增加共同评分的视频数量
            }
        }

        // 如果两个用户没有共同评分的视频，则返回0
        if (n == 0) {
            return 0;
        }

        // 计算皮尔逊相关系数（相似度），并返回结果
        double denominator = Math.sqrt(sumX2) * Math.sqrt(sumY2);
        if (denominator == 0) {
            return 0; // 如果分母为0，则说明两个用户的评分数据完全相同，相似度为0
        } else {
            return sumXY / denominator;
        }
    }
}
