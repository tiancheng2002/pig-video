package com.zhu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.annotation.AuthCheck;
import com.zhu.common.IdRequest;
import com.zhu.constant.FileConstant;
import com.zhu.exception.ErrorException;
import com.zhu.handler.VideoHandler;
import com.zhu.handler.VideoHandlerRegister;
import com.zhu.model.dto.user.UserVideoRequest;
import com.zhu.model.dto.video.VideoAddRequest;
import com.zhu.model.dto.video.VideoBrowseRequest;
import com.zhu.model.dto.video.VideoRecommendRequest;
import com.zhu.model.dto.video.VideoTypeRequest;
import com.zhu.model.entity.VideoInfo;
import com.zhu.result.RespBean;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IUserBrowseService;
import com.zhu.service.IUserService;
import com.zhu.service.IVideoInfoService;
import com.zhu.spark.SparkService;
import com.zhu.utils.BeanCopeUtils;
import com.zhu.utils.QiNiuUtils;
import com.zhu.utils.VideoCoverUtils;
import com.zhu.vo.LoginUserVo;
import com.zhu.vo.VideoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2023-10-24
 */
@RestController
@RequestMapping("/video")
@Slf4j
public class VideoInfoController {

    @Resource
    private IVideoInfoService videoInfoService;

    @Resource
    private IUserService userService;

    @Resource
    private IUserBrowseService userBrowseService;

    @Resource
    private VideoHandlerRegister videoHandlerRegister;

    @Resource
    private QiNiuUtils qiNiuUtils;

    @Resource
    private SparkService sparkService;

    @PostMapping("/")
    public RespBean getVideoRandom(@RequestBody VideoRecommendRequest videoRecommendRequest, HttpServletRequest request){
        //首页随机获取一定数量视频
        Page<VideoVo> videoVoList = videoInfoService.getVideoByRandom(videoRecommendRequest,request);
        return RespBean.success(videoVoList);
    }

    @GetMapping("/byFollow")
    public RespBean getVideoByFollowUser(HttpServletRequest request){
        LoginUserVo loginUser = userService.getLoginUser(request);
        if(loginUser==null){
            throw new ErrorException(RespBeanEnum.USER_UN_LOGIN);
        }
        Page<VideoVo> videoVoPage = videoInfoService.getVideoByFollow(loginUser.getUid(),request);
        return RespBean.success(videoVoPage);
    }

    /**
     * 根据分类获取视频数据
     * @param videoTypeRequest
     * @param request
     * @return
     */
    @PostMapping("/type")
    public RespBean getVideoByType(@RequestBody VideoTypeRequest videoTypeRequest, HttpServletRequest request){
        //todo 这个可以当作分类视频的推送，按照一定规则获取到一定数量的视频，然后返回给用户
        Page<VideoVo> videoVoList = videoInfoService.getVideoVoListByType(videoTypeRequest,request);
        return RespBean.success(videoVoList);
    }

    @PostMapping("/hot")
    public RespBean getHotVideo(HttpServletRequest request){
        //获取热门视频，可以根据点赞数、评论数、收藏数等各类数据的权重，计算出最后的分值
        //还可以根据视频播放量点赞量等各类数据增加的速率来计算
        //比如有个视频两分钟内被浏览了1000次，另外一个视频三分钟内被浏览了500次，那么第一个视频的分值肯定比第二个视频高
        //然后按照分值排序，前十个视频就是要推送给用户的热门视频
        Page<VideoVo> videoVoPage = videoInfoService.getHotVideoWithTopK(10,request);
        return RespBean.success(videoVoPage);
    }

    @PostMapping("/user")
//    @AuthCheck
    public RespBean getVideoByUser(@RequestBody UserVideoRequest userVideoRequest){
        //首先获取想要获取视频的类型（我的作品、喜欢、收藏）
        //然后调用对应的方法返回就行
        if(userVideoRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        Long uid = userVideoRequest.getUid();
        String type = userVideoRequest.getType();
        Long current = userVideoRequest.getCurrent();
        Long pageSize = userVideoRequest.getPageSize();
        if(uid==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        Page<VideoInfo> videoInfoPage = new Page<>(current, pageSize);
        VideoHandler videoHandler  = this.videoHandlerRegister.getDataSourceByType(type);
        if(videoHandler==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        Page<VideoInfo> videoInfos = videoHandler.videoHandler(videoInfoPage,uid);
        return RespBean.success(videoInfos);
    }

    @GetMapping("/get/vo")
    public RespBean getVideoVoById(Long videoId,HttpServletRequest request){
        if(videoId<=0){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        VideoInfo videoInfo = videoInfoService.getById(videoId);
        if(videoInfo==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
        if(loginUser!=null){
            userBrowseService.actionBrowse(videoId,loginUser.getUid());
        }
        return RespBean.success(videoInfoService.getVideoVo(videoInfo,request));
    }

    @PostMapping("/browse")
    public RespBean userBrowseVideo(@RequestBody VideoBrowseRequest videoBrowseRequest,HttpServletRequest request){
        Long videoId = videoBrowseRequest.getVideoId();
        LoginUserVo loginUser = userService.getLoginUser(request);
        int result = 0;
        if(loginUser!=null){
            result = userBrowseService.actionBrowse(videoId, loginUser.getUid());
        }
        return RespBean.success(result);
    }

    @PostMapping("/edit")
    public RespBean videoEditData(@RequestBody IdRequest idRequest,HttpServletRequest request){
        if(idRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
        if(loginUser==null){
            throw new ErrorException(RespBeanEnum.USER_UN_LOGIN);
        }
        VideoInfo videoInfo = videoInfoService.getById(idRequest.getId());
        if(videoInfo==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        if(!videoInfo.getUid().equals(loginUser.getUid())){
            throw new ErrorException(RespBeanEnum.FORBIDDEN);
        }
        return RespBean.success(videoInfo);
    }

    /**
     * 上传图片或视频接口
     * @param type
     * @param file
     * @return
     */
    @PostMapping("/{type}/upload")
    @AuthCheck
    public RespBean uploadVideo(@PathVariable("type") String type,@RequestParam("file") MultipartFile file){
        String url = null;
        String fileType;
        if(type.equals("url")){
            fileType = FileConstant.FILE_VIDEO;
        }else{
            fileType = FileConstant.FILE_PICTURE;
        }
        try {
            url = qiNiuUtils.upload(file.getInputStream(),file.getOriginalFilename(), fileType);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return RespBean.success(url);
    }

//    @PostMapping("/uploadTest")
//    public RespBean uploadTest(){
//        Map<String, Object> map = VideoCoverUtils.getVedioImg("https://image.xiaozhu02.top/", "video1.mp4", 20);
//        InputStream inputStream = (InputStream) map.get("inputStream");
//        String fileName = (String) map.get("fileName");
//        String url = qiNiuUtils.uploadImg(inputStream, fileName);
//        return RespBean.success(url);
//    }

    /**
     * 视频发布接口（没有上传封面图将自动用视频第一帧作为封面图）
     * 限制上传视频的时长和大小
     * todo 1、发布成功后对长视频进行拆分，拆分成每个视频最长为30秒的视频
     * 2、用whisper模型对拆分的视频进行并行音频文字识别
     * 3、整理识别结果，用星火大模型将识别内容进行总结
     * 4、获取视频总结，添加到对应视频的评论列表中
     * @param videoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/publish")
    @Transactional(rollbackFor = Exception.class)
    public RespBean publishVideo(@RequestBody VideoAddRequest videoAddRequest,HttpServletRequest request){
        if(videoAddRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
        Long uid = loginUser.getUid();
        VideoInfo videoInfo = BeanCopeUtils.copyBean(videoAddRequest, VideoInfo.class);
        boolean save;
        if(videoAddRequest.getVideoId()!=null){
            if(!uid.equals(videoAddRequest.getUid())){
                throw new ErrorException(RespBeanEnum.FORBIDDEN);
            }
            save = videoInfoService.updateById(videoInfo);
        }else{
            //首先根据用户上传的视频截取到封面图片
            String videoName = videoAddRequest.getVideoName();
            try {
                Map<String, Object> map = VideoCoverUtils.getVedioImg(FileConstant.ImageUrl, videoName);
                InputStream inputStream = (InputStream) map.get("inputStream");
                String fileName = (String) map.get("fileName");
                //todo 上传会报错，所以最好加个try catch保护一下
                String url = qiNiuUtils.upload(inputStream, fileName,FileConstant.FILE_PICTURE);
                videoInfo.setVideoPic(url);
            }catch (Exception e){
                log.error(e.getMessage());
            }
            videoInfo.setUid(uid);
            save = videoInfoService.save(videoInfo);
        }
        return RespBean.success(save?1:0);
    }

    @PostMapping("/TempPublish")
    public RespBean tempPublishVideo(@RequestBody VideoAddRequest videoAddRequest,HttpServletRequest request){
        if(videoAddRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
        Long uid = loginUser.getUid();
        VideoInfo videoInfo = BeanCopeUtils.copyBean(videoAddRequest, VideoInfo.class);
        videoInfo.setUid(uid);
        //将发布的视频添加到数据库中
        boolean save = videoInfoService.save(videoInfo);
        //异步的去whisper模型进行文字识别
        CompletableFuture.runAsync(() -> {
            sparkService.videoSummaryWithAI(videoAddRequest.getVideoUrl(),videoInfo.getVideoId());
        });
        return RespBean.success(save?1:0);
    }

    @GetMapping("/summary")
    public String doVideoSummary(String audioPath){
        CompletableFuture.runAsync(() -> {
            sparkService.videoSummaryWithAI(audioPath,312L);
        });
        return "ok";
    }

    //todo 项目加载前，需要先将视频的播放量存储到Redis缓存当中

}
