package com.zhu.customer;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhu.config.WebSocket;
import com.zhu.constant.MQConstant;
import com.zhu.constant.MessageConstant;
import com.zhu.model.entity.Message;
import com.zhu.service.IMessageService;
import com.zhu.service.IUserService;
import com.zhu.utils.BeanCopeUtils;
import com.zhu.utils.RedisUtils;
import com.zhu.vo.MessageVo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MessageCustomer {

    @Resource
    private IMessageService messageService;

    @Resource
    private IUserService userService;

    @Resource
    private WebSocket webSocket;

    @Resource
    private RedisUtils redisUtils;

    @RabbitListener(queues = MQConstant.MESSAGE_QUEUE)
    public void videoMessage(Message message){
        //用户收到消息之后先去数据库中判断是否有对应的消息已经存入
        //如果有的话那么就不在会发送消息和将消息添加到数据库中
        //并且发送消息的时候还会判断用户是否在线，如果离线就先将消息存入到redis当中，当用户上线的时候会去扫描redis中是否有消息未读
        //会获取到发送消息的用户信息，以便在前端能够很好的展示
        Long receiverId = message.getReceiverId();
        Long senderId = message.getSenderId();
        QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
        messageQueryWrapper.eq("message_type",message.getMessageType());
        messageQueryWrapper.eq("sender_id",senderId);
        messageQueryWrapper.eq("receiver_id",receiverId);
        Message messageData = messageService.getOne(messageQueryWrapper);
        if(messageData==null){
            //先假设在线，后面需判断是否在线
            if(!webSocket.isOnline(String.valueOf(receiverId))){
                redisUtils.hincr(MessageConstant.MessageKey,String.valueOf(receiverId),1);
                message.setIsDelivered(false);
            }else{
                //如果用户在线，直接给用户发送对应消息
                message.setIsDelivered(true);
                MessageVo messageVo = BeanCopeUtils.copyBean(message, MessageVo.class);
                messageVo.setUser(userService.getUserBasicInfo(senderId));
                webSocket.sendOneMessage(String.valueOf(receiverId), JSON.toJSONString(messageVo));
            }
            messageService.save(message);
        }
    }

}
