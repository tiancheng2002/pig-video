//package com.zhu.recommend;
//
//import com.zhu.model.entity.UserRating;
//import org.apache.mahout.cf.taste.common.TasteException;
//import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
//import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
//import org.apache.mahout.cf.taste.impl.model.GenericPreference;
//import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
//import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
//import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
//import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
//import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
//import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
//import org.apache.mahout.cf.taste.model.DataModel;
//import org.apache.mahout.cf.taste.model.PreferenceArray;
//import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
//import org.apache.mahout.cf.taste.recommender.RecommendedItem;
//import org.apache.mahout.cf.taste.recommender.Recommender;
//import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
//import org.apache.mahout.cf.taste.similarity.UserSimilarity;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class RecommendUtils {
//
//    public static List<Long> recommend(Long userId,List<UserRating> userList) throws TasteException {
//        //创建数据模型
//        DataModel dataModel = createDataModel(userList);
//        //获取用户相似程度
//        UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
//        //获取用户邻居
//        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(3, similarity, dataModel);
//        //构建推荐器
//        Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);
//        //推荐2个
//        List<RecommendedItem> recommendedItems = recommender.recommend(userId, 5);
//        List<Long> itemIds = recommendedItems.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
//        System.out.println("推荐个数："+itemIds.size());
//        return itemIds;
//    }
//
//    public static List<Long> itemRecommend(Long itemId,List<UserRating> userList) throws TasteException {
//        List<Long> idList = new ArrayList<>();
//        Map<Long,Float> map = new HashMap<>();
//        DataModel dataModel = createDataModel(userList);
//        ItemSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
//        Recommender recommender = new GenericItemBasedRecommender(dataModel,similarity);
//        List<RecommendedItem> items = recommender.recommend(itemId, 20);
//        for (RecommendedItem recommendedItem:items){
//            idList.add(recommendedItem.getItemID());
//        }
//        System.out.println("推荐个数："+idList.size());
//        return idList;
//    }
//
//    private static DataModel createDataModel(List<UserRating> userArticleOperations) {
//        FastByIDMap<PreferenceArray> fastByIdMap = new FastByIDMap<>();
//        Map<Long, List<UserRating>> map = userArticleOperations.stream().collect(Collectors.groupingBy(UserRating::getUid));
//        Collection<List<UserRating>> list = map.values();
//        for(List<UserRating> userPreferences : list){
//            GenericPreference[] array = new GenericPreference[userPreferences.size()];
//            for(int i = 0; i < userPreferences.size(); i++){
//                UserRating userPreference = userPreferences.get(i);
//                GenericPreference item = new GenericPreference(userPreference.getUid(), userPreference.getVideoId(), userPreference.getRate());
//                array[i] = item;
//            }
//            fastByIdMap.put(array[0].getUserID(), new GenericUserPreferenceArray(Arrays.asList(array)));
//        }
//        return new GenericDataModel(fastByIdMap);
//    }
//
//}
