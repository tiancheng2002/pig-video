package com.zhu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.constant.CommonConstant;
import com.zhu.mapper.VideoFavourMapper;
import com.zhu.mapper.VideoInfoMapper;
import com.zhu.mapper.VideoStarMapper;
import com.zhu.model.dto.search.SearchRequest;
import com.zhu.model.dto.video.VideoEsDTO;
import com.zhu.model.dto.video.VideoRecommendRequest;
import com.zhu.model.dto.video.VideoTypeRequest;
import com.zhu.model.entity.User;
import com.zhu.model.entity.VideoFavour;
import com.zhu.model.entity.VideoInfo;
import com.zhu.model.entity.VideoStar;
import com.zhu.model.enums.SortTypeEnums;
import com.zhu.model.enums.TimeScopeEnum;
import com.zhu.service.IUserService;
import com.zhu.service.IVideoInfoService;
import com.zhu.utils.BeanCopeUtils;
import com.zhu.vo.LoginUserVo;
import com.zhu.vo.VideoVo;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-24
 */
@Service
public class VideoInfoServiceImpl extends ServiceImpl<VideoInfoMapper, VideoInfo> implements IVideoInfoService {

    @Resource
    private IUserService userService;

    @Resource
    private VideoStarMapper videoStarMapper;

    @Resource
    private VideoFavourMapper videoFavourMapper;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public Page<VideoVo> getVideoVoListByType(VideoTypeRequest videoTypeRequest, HttpServletRequest request) {
        Long type = videoTypeRequest.getType();
        long current = videoTypeRequest.getCurrent();
        long pageSize = videoTypeRequest.getPageSize();
        QueryWrapper<VideoInfo> videoInfoQueryWrapper = new QueryWrapper<>();
        if(type!=null&&type!=' '&&type>0){
            videoInfoQueryWrapper.eq("type_id",type);
        }
        //todo 不需要判断用户是否点赞或收藏了该视频，只需要获取到视频的作者信息即可
        Page<VideoInfo> videoInfoPage = this.page(new Page<>(current,pageSize), videoInfoQueryWrapper);
        return getVideoVoOther(videoInfoPage, CommonConstant.ONLY_AUTHOR_INFO, request);
    }

    @Override
    public Page<VideoInfo> searchFromES(SearchRequest searchRequest) {
        String searchKey = searchRequest.getSearchKey();
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();
        Integer publishTime = searchRequest.getPublishTime();
        Integer sortType = searchRequest.getSortType();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //过滤
        boolQueryBuilder.must(QueryBuilders.termQuery("status", 1));
        //按关键词进行检索
        if (StringUtils.isNotBlank(searchKey)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("description", searchKey));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        //排序（通过枚举类获取到排序标签以及规则）
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        SortTypeEnums enumByValue = SortTypeEnums.getEnumByValue(sortType);
        if(!enumByValue.equals(SortTypeEnums.SORT_COMPREHENSIVE)){
            sortBuilder = SortBuilders.fieldSort(enumByValue.getText());
            sortBuilder.order(SortOrder.DESC);
        }
        //根据发布时间范围进行查询
        TimeScopeEnum timeScopeEnum = TimeScopeEnum.getEnumByValue(publishTime);
        if(!timeScopeEnum.equals(TimeScopeEnum.TIME_ALL)){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, timeScopeEnum.getTime());
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("createTime").from(calendar.getTime()).to(new Date().getTime()));
        }
        // 分页
        PageRequest pageRequest = PageRequest.of((int) current-1, (int) pageSize);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest).withSorts(sortBuilder).build();
        SearchHits<VideoEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, VideoEsDTO.class);
        Page<VideoInfo> videoInfoPage = new Page<>();
        List<VideoInfo> videoInfoList = new ArrayList<>();
        videoInfoPage.setTotal(searchHits.getTotalHits());
        //查出结果后，从 db 获取最新动态数据（比如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<VideoEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> videoIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getVideoId())
                    .collect(Collectors.toList());
            //根据id去数据库中查询出对应的视频信息以及作者信息
            //接着判断用户是否已经登录，如果登录了判断用户是否点赞或收藏了该视频
//            videoVoList = videoIdList.stream().map(videoId->{
//                VideoVo videoVo = this.baseMapper.getVideoById(videoId);
//                return videoVo;
//            }).collect(Collectors.toList());
            videoInfoList = this.listByIds(videoIdList);
        }
        videoInfoPage.setRecords(videoInfoList);
        return videoInfoPage;
    }

    @Override
    public Page<VideoVo> getVideoVoOther(Page<VideoInfo> videoVoPage, boolean isOnlyAuthorInfo,HttpServletRequest request) {
        //todo 只获取作者信息，不获取点赞、收藏信息
        List<VideoInfo> records = videoVoPage.getRecords();
        Page<VideoVo> videoVoListPage = new Page<>(videoVoPage.getCurrent(), videoVoPage.getSize(), videoVoPage.getTotal());
        if (CollectionUtils.isEmpty(records)) {
            return videoVoListPage;
        }
        //查询关联用户信息
        Set<Long> userIdsSet = records.stream().map(VideoInfo::getUid).collect(Collectors.toSet());
        Map<Long, List<User>> userMap = userService.listByIds(userIdsSet).stream().collect(Collectors.groupingBy(User::getUid));
        //获取登录用户信息
        LoginUserVo loginUser = userService.getLoginUser(request);
        Map<Long, Boolean> videoIdHasFavourMap = new HashMap<>();
        Map<Long, Boolean> videoIdHasStarMap = new HashMap<>();
        if(loginUser!=null&&!isOnlyAuthorInfo){
            //获取用户所有收藏数据
            Set<Long> videoIds = videoVoPage.getRecords().stream().map(VideoInfo::getVideoId).collect(Collectors.toSet());
            QueryWrapper<VideoStar> videoStarQueryWrapper = new QueryWrapper<>();
            videoStarQueryWrapper.in("video_id",videoIds);
            videoStarQueryWrapper.eq("uid",loginUser.getUid());
            videoStarQueryWrapper.select("video_id");
            List<Long> videoStarList = videoStarMapper.selectObjs(videoStarQueryWrapper)
                    .stream().map(o -> (Long) o).collect(Collectors.toList());
            videoStarList.forEach(videoId->videoIdHasStarMap.put(videoId,true));
            //获取用户所有点赞数据
            QueryWrapper<VideoFavour> videoFavourQueryWrapper = new QueryWrapper<>();
            videoFavourQueryWrapper.in("video_id",videoIds);
            videoFavourQueryWrapper.eq("uid",loginUser.getUid());
            videoFavourQueryWrapper.select("video_id");
            List<Long> videoFavourList = videoFavourMapper.selectObjs(videoFavourQueryWrapper)
                    .stream().map(o -> (Long) o).collect(Collectors.toList());
            videoFavourList.forEach(videoId->videoIdHasFavourMap.put(videoId,true));
        }
        List<VideoVo> videoVoList = records.stream().map(videoInfo -> {
            VideoVo videoVo = BeanCopeUtils.copyBean(videoInfo, VideoVo.class);
            Long uid = videoInfo.getUid();
            Long videoId = videoInfo.getVideoId();
            User user = null;
            if (userMap.containsKey(uid)) {
                user = userMap.get(uid).get(0);
            }
            videoVo.setUser(user);
            videoVo.setStar(videoIdHasStarMap.getOrDefault(videoId,false));
            videoVo.setFavour(videoIdHasFavourMap.getOrDefault(videoId,false));
            return videoVo;
        }).collect(Collectors.toList());
        return videoVoListPage.setRecords(videoVoList);
    }

    @Override
    public VideoVo getVideoVo(VideoInfo videoInfo, HttpServletRequest request) {
        VideoVo videoVo = BeanCopeUtils.copyBean(videoInfo, VideoVo.class);
        Long videoId = videoInfo.getVideoId();
        Long uid = videoInfo.getUid();
        User user = null;
        if(uid!= null && uid > 0){
            user = userService.getUserBasicInfo(uid);
        }
        videoVo.setUser(user);
        //判断用户是否已登录，获取用户点赞、收藏状态
        LoginUserVo loginUser = userService.getLoginUser(request);
        if(loginUser!=null){
            QueryWrapper<VideoStar> videoStarQueryWrapper = new QueryWrapper<>();
            videoStarQueryWrapper.in("video_id", videoId);
            videoStarQueryWrapper.eq("uid", loginUser.getUid());
            VideoStar videoStar = videoStarMapper.selectOne(videoStarQueryWrapper);
            videoVo.setStar(videoStar != null);
            // 获取收藏
            QueryWrapper<VideoFavour> videoFavourQueryWrapper = new QueryWrapper<>();
            videoFavourQueryWrapper.in("video_id", videoId);
            videoFavourQueryWrapper.eq("uid", loginUser.getUid());
            VideoFavour videoFavour = videoFavourMapper.selectOne(videoFavourQueryWrapper);
            videoVo.setFavour(videoFavour != null);
        }
        return videoVo;
    }

    @Override
    public Page<VideoVo> getVideoByRandom(VideoRecommendRequest videoRecommendRequest, HttpServletRequest request) {
        long current = videoRecommendRequest.getCurrent();
        long pageSize = videoRecommendRequest.getPageSize();
        Page<VideoInfo> videoInfoPage = new Page<>(current, pageSize);
        Page<VideoInfo> videoPageList = baseMapper.randomVideo(videoInfoPage);
        Page<VideoVo> videoVoOther = getVideoVoOther(videoPageList,CommonConstant.ONLY_AUTHOR_INFO, request);
        return videoVoOther;
    }

    @Override
    public Page<VideoVo> getHotVideoWithTopK(int k,HttpServletRequest request) {
        //todo 实际会根据发布时间查找七天内发布的视频进行计算
        List<VideoInfo> videoInfos = this.list();
        List<VideoInfo> videoIds = new ArrayList<>();
        Map<VideoInfo,Double> weights = new HashMap<>();
        for (VideoInfo videoInfo:videoInfos){
            double likeWeight = videoInfo.getFavourNum() * 0.8;
            double commentWeight = videoInfo.getCommentNum() * 0.5;
            double starWeight = videoInfo.getStarNum() * 0.3;
            double viewWeight = videoInfo.getPlayNum() * 1.5;
            double videoWeight = calculateRateWeight(videoInfo.getCreateTime());
            weights.put(videoInfo,likeWeight+commentWeight+starWeight+viewWeight+videoWeight);
        }
        weights.entrySet().stream()
                .sorted(Map.Entry.<VideoInfo,Double>comparingByValue().reversed())
                .limit(k)
                .forEach(entry->{
                    videoIds.add(entry.getKey());
//                    hostVideos.put(entry.getKey(),entry.getValue());
                });
        Page<VideoInfo> videoInfoPage = new Page<>(1, k);
//        videoInfoPage.setRecords(this.baseMapper.selectList(new QueryWrapper<VideoInfo>().in("video_id",videoIds)));
        videoInfoPage.setRecords(videoIds);
        Page<VideoVo> videoVoPage = this.getVideoVoOther(videoInfoPage, CommonConstant.ONLY_AUTHOR_INFO, request);
        return videoVoPage;
    }

    @Override
    public Page<VideoVo> getVideoByFollow(Long uid, HttpServletRequest request) {
        Page<VideoInfo> videoInfoPage = new Page<>(1, 10);
        Page<VideoInfo> videoByFollow = this.baseMapper.getVideoByFollow(videoInfoPage, uid);
        return getVideoVoOther(videoByFollow,CommonConstant.ONLY_AUTHOR_INFO, request);
    }

    final double a = 0.5;

    public double calculateRateWeight(Date createTime){
        long nowTime = System.currentTimeMillis();
        long publishTime = createTime.getTime();
        return Math.exp(-a * TimeUnit.MILLISECONDS.toDays(nowTime-publishTime));
    }

}
