package com.zhu.controller;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.VideoApplication;
import com.zhu.mapper.MessageMapper;
import com.zhu.mapper.UserRatingMapper;
import com.zhu.model.dto.video.VideoEsDTO;
import com.zhu.model.entity.User;
import com.zhu.model.entity.UserRating;
import com.zhu.model.entity.VideoInfo;
import com.zhu.model.enums.MessageTypeEnums;
import com.zhu.recommend.FileDataSource;
import com.zhu.recommend.core.UserBasedRecommend;
import com.zhu.service.*;
import com.zhu.utils.BeanCopeUtils;
import com.zhu.utils.RedisUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest(classes = VideoApplication.class)
public class MainTest {

    @Resource
    private IUserService userService;

    @Resource
    private IVideoInfoService videoInfoService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private IDanmuService danmuService;

    @Resource
    private IUserRatingService userRatingService;

    @Resource
    private UserRatingMapper userRatingMapper;

    @Resource
    private IVideoFavourService videoFavourService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private MessageMapper messageMapper;

    public static void main(String[] args)
    {
        String time = "0:16";
        float timeFloat = Float.parseFloat(time.replace(":","."));
        float resultTo100 = convertTo100(timeFloat);
        System.out.println(resultTo100);
        String resultTo60 = convertTo60(resultTo100);
        System.out.println(resultTo60);
    }

    public static float convertTo100(float input)
    {
        String input_string = Float.toString(input);
        BigDecimal inputBD = new BigDecimal(input_string);
        String hhStr = input_string.split("\\.")[0];
        BigDecimal output = new BigDecimal(Float.toString(Integer.parseInt(hhStr)));
        output = output.add((inputBD.subtract(output).divide(BigDecimal.valueOf(60), 10, BigDecimal.ROUND_HALF_EVEN)).multiply(BigDecimal.valueOf(100)));

        return Float.parseFloat(output.toString());
    }

    public static String convertTo60(float input)
    {
        String input_string = Float.toString(input);
        BigDecimal inputBD = new BigDecimal(input_string);
        String hhStr = input_string.split("\\.")[0];
        BigDecimal output = new BigDecimal(Float.toString(Integer.parseInt(hhStr)));
        output = output.add((inputBD.subtract(output).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_EVEN)).multiply(BigDecimal.valueOf(60)));

        return output.toString().replace(".",":");
    }


    @Test
    public void contextLoads() {
        List<User> users = new ArrayList<>();
        for (int i=0;i<2000;i++){
            User user = new User();
            user.setUserEmail("184953"+(1000+i)+"@qq.com");
            user.setUserPassword("4532a9efe7f7028cca653d9f462ae526");
            user.setUserNickname(RandomUtil.randomString(8));
            user.setUserAvatar("https://p3-pc.douyinpic.com/aweme/100x100/aweme-avatar/tos-cn-avt-0015_0a69114a5280102312f509a571b00783.jpeg?from=2956013662");
            users.add(user);
        }
        userService.saveBatch(users);
    }

    @Test
    public void insertVideoInEs(){
        //全量同步视频数据到ES当中
        List<VideoInfo> videoInfos = videoInfoService.list();
//        VideoEsDTO videoEsDTO = BeanCopeUtils.copyBean(videoInfos.get(0), VideoEsDTO.class);
//        System.out.println(videoEsDTO);
        for (VideoInfo videoInfo:videoInfos){
            VideoEsDTO videoEsDTO = BeanCopeUtils.copyBean(videoInfo, VideoEsDTO.class);
            elasticsearchRestTemplate.save(videoEsDTO);
        }
    }

    @Test
    public void updateVideoPlayNumTest(){
        //测试更新视频播放量
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setVideoId(1L);
        videoInfo.setPlayNum(1L);
        boolean b = videoInfoService.updateById(videoInfo);
        System.out.println(b);
    }

    @Test
    public void mockRatingData(){
        //随机生成两千条用户评分数据
        List<User> userList = userService.list();
        List<Long> userIds = userList.stream().map(User::getUid).collect(Collectors.toList());
        List<UserRating> userRatings = new ArrayList<>();
        for (int i=0;i<20000;i++){
            boolean add = true;
            UserRating userRating = new UserRating();
            //随机获取用户id
            Long uid = userIds.get(RandomUtil.randomInt(0, userIds.size()/2));
            userRating.setUid(uid);
            userRating.setVideoId(RandomUtil.randomLong(1,301));
            userRating.setRate(RandomUtil.randomInt(1,6));
            for (UserRating rating:userRatings){
                if(rating.getUid().equals(userRating.getUid())&&rating.getVideoId().equals(userRating.getVideoId())){
                    add = false;
                    break;
                }
            }
            if(add){
                userRatings.add(userRating);
            }
        }
        userRatingService.saveBatch(userRatings);
    }

    @Test
    public void getUserData(){
        System.out.println(userService.list());
    }

//    @Test
//    public void recommendVideo() throws TasteException {
//        List<UserRating> userRatings = userRatingService.list();
////        List<Long> recommend = RecommendUtils.recommend(1717008431916822529L,userRatings);
//        List<Long> recommend = RecommendUtils.itemRecommend(1717008431916822529L,userRatings);
//        recommend.forEach(System.out::println);
//    }

    @Test
    public void recommendVideoTest(){
        System.out.println("------基于用户协同过滤推荐---------------下列电影");
        List<UserRating> data = userRatingService.list();
//        List<Long> recommendations = UserCF.recommend(1717008431916822529L, data);
//        recommendations.forEach(e-> System.out.println(e));
    }

    @Test
    public void insertVideo(){
        List<User> userList = userService.list();
        List<VideoInfo> videoInfoList = FileDataSource.getData(userList);
        videoInfoService.saveBatch(videoInfoList);
//        List<User> userList = userService.list();
//        QueryWrapper<VideoInfo> videoInfoQueryWrapper = new QueryWrapper<>();
//        videoInfoQueryWrapper.isNull("type_id");
//        List<VideoInfo> list = videoInfoService.list(videoInfoQueryWrapper);
//        for (VideoInfo videoInfo:list){
//            videoInfo.setUid(userList.get(RandomUtil.randomInt(0,userList.size()-1)).getUid());
//            videoInfo.setTypeId(RandomUtil.randomLong(2,9));
//            videoInfo.setStatus(1);
//        }
//        videoInfoService.updateBatchById(list);
    }

    @Test
    public void delVideoInEs(){
        elasticsearchRestTemplate.delete("1",VideoEsDTO.class);
    }

    @Test
    public void selectVideos(){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("status", 1));
        boolQueryBuilder.should(QueryBuilders.matchQuery("description", "挑战大哥"));
        boolQueryBuilder.minimumShouldMatch(1);
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(PageRequest.of(0, 10)).build();
        SearchHits<VideoEsDTO> search = elasticsearchRestTemplate.search(query, VideoEsDTO.class);
        List<SearchHit<VideoEsDTO>> searchHits = search.getSearchHits();
        System.out.println(searchHits);
        System.out.println(search.getTotalHits());
        List<VideoEsDTO> list = new ArrayList<>();
        searchHits.forEach(sh->{
            list.add(sh.getContent());
        });
        System.out.println(list);
    }

    @Test
    public void redisTest(){
        System.out.println(redisUtils.hHasKey("message_unRead", "1718536711274815500"));
//        double hincr = redisUtils.hincr("message_unRoad", "abc", 1);
//        System.out.println(hincr);
    }

    @Test
    public void doVideoFavour(){
        List<User> users = userService.list();
        for (User user:users){
            videoFavourService.doVideoFavour(188L,user.getUid());
        }
    }

    @Test
    public void selectTest(){
        List<Long> ids = new ArrayList<>(Arrays.asList(188L, 2L));
        System.out.println(ids);
        System.out.println(videoInfoService.listByIds(ids));
    }

//    @Test
//    public void updateMessage(){
//        UpdateWrapper<Message> messageUpdateWrapper = new UpdateWrapper<>();
//        Integer[] messageTypes = MessageConstant.messageMap.get("likes");
//        messageUpdateWrapper.in("message_type", messageTypes);
//        messageUpdateWrapper.eq("receiver_id","1718536711274815500");
//        messageUpdateWrapper.eq("is_delivered",0);
//        messageUpdateWrapper.set("is_delivered",1);
//        int update = messageMapper.update(null, messageUpdateWrapper);
//        System.out.println(update);
//    }

    @Test
    public void testMessageEnums(){
        MessageTypeEnums messageTypeEnums = MessageTypeEnums.setCommentText(MessageTypeEnums.COMMENT, "真是不错的曲子");
        System.out.println(messageTypeEnums.getText());
        System.out.println(messageTypeEnums.getValue());
    }

    @Test
    public void getRating(){
        Map<Long, List<UserRating>> collect = userRatingService.list().stream().collect(Collectors.groupingBy(UserRating::getUid));
        List<UserRating> userRatings = collect.get(1718536711337730051L);
        Map<Long, Integer> collect1 = userRatings.stream().collect(Collectors.toMap(UserRating::getVideoId, UserRating::getRate));
        System.out.println(collect1);
//        Map<Long, Map<Long, Object>> userRatings = userRatingMapper.getUserRatings();
//        for (Long id:userRatings.keySet()){
////            System.out.println(id);
//            System.out.println(userRatings.get(id).size());
//        }
    }

    @Test
    public void testRating(){
        UserBasedRecommend userBasedRecommend = new UserBasedRecommend(userRatingService.list(),1717008431916822529L);
        List<Long> videoIds = userBasedRecommend.recommendForUser();
        System.out.println(videoIds);
        QueryWrapper<VideoInfo> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("video_id", videoIds);
        Map<Long, List<VideoInfo>> userIdUserListMap = videoInfoService.list(userQueryWrapper)
                .stream()
                .collect(Collectors.groupingBy(VideoInfo::getVideoId));
        Page<VideoInfo> videoInfoPage = new Page<>(1, videoIds.size());
    }

}
