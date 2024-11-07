package com.zhu.utils;

import com.zhu.constant.MQConstant;
import com.zhu.model.entity.Message;
import com.zhu.model.entity.VideoInfo;
import com.zhu.model.enums.MessageTypeEnums;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MessageUtils {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessageToUser(Long senderId,Long receiverId,Long videoId,MessageTypeEnums messageTypeEnums){
        //如果发送方和接受方的id一致，那么就不需要发送互动消息提示
        if(!receiverId.equals(senderId)){
            Message message = handlerMessage(senderId, receiverId, videoId, messageTypeEnums);
            rabbitTemplate.convertAndSend(MQConstant.VIDEO_TOPIC,MQConstant.MESSAGE_QUEUE,message);
        }
    }

    public void updateESData(VideoInfo videoInfo){
        rabbitTemplate.convertAndSend(MQConstant.VIDEO_TOPIC,MQConstant.ES_QUEUE,videoInfo);
    }

    /**
     * 处理传递过来的参数，将他转换成一个实体类
     * @param senderId
     * @param receiverId
     * @param messageTypeEnums
     * @return
     */
    public static Message handlerMessage(Long senderId,Long receiverId,Long videoId,MessageTypeEnums messageTypeEnums){
        Message message = new Message();
        message.setMessageType(messageTypeEnums.getValue());
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setVideoId(videoId);
        message.setMessageContent(messageTypeEnums.getText());
        return message;
    }

}
