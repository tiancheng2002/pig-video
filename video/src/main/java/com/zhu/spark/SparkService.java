package com.zhu.spark;

import com.zhu.constant.CommentConstant;
import com.zhu.model.entity.Comment;
import com.zhu.service.ICommentService;
import com.zhu.utils.WhisperUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SparkService {

    @Resource
    private SparkManager sparkManager;

    @Resource
    private ICommentService commentService;

    /**
     * todo 1、发布成功后对长视频进行拆分，拆分成每个视频最长为30秒的视频
     * 2、用whisper模型对拆分的视频进行并行音频文字识别
     * 3、整理识别结果，用星火大模型将识别内容进行总结
     * 4、获取视频总结，添加到对应视频的评论列表中
     * @return
     */
    public void videoSummaryWithAI(String audioPath, Long videoId){
//        File file = new File(audioPath);
//        if(!file.exists()){
//            System.out.println("文件不存在");
//            throw new ErrorException(RespBeanEnum.FILE_NOT_EXIST);
//        }
//        System.out.println("文件存在");
        String videoText = WhisperUtils.doWhisper(audioPath);
        System.out.println("完成文字识别");
        //todo 如果识别出来的文字过少，那么就不调用星火大模型
        String aiSummary = sparkManager.sendMesToAIUseXingHuo(videoText);
       //将AI出来的总结添加到评论
        //todo 可以定义一个常量接口，里面放入官方的用户信息
        Comment comment = new Comment();
        comment.setContent(aiSummary);
        comment.setUid(CommentConstant.AIUserCommentID);
        comment.setVideoId(videoId);
        commentService.save(comment);
    }

}
