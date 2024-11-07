package com.zhu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.constant.MessageConstant;
import com.zhu.mapper.MessageMapper;
import com.zhu.model.entity.Message;
import com.zhu.service.IMessageService;
import com.zhu.utils.RedisUtils;
import com.zhu.vo.MessageTypeVo;
import com.zhu.vo.MessageVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-11-13
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    @Resource
    private RedisUtils redisUtils;

    @Override
    public int updateDelivered(Long uid, String type) {
        Integer[] messageTypes = MessageConstant.messageMap.get(type);
        UpdateWrapper<Message> messageUpdateWrapper = new UpdateWrapper<>();
        messageUpdateWrapper.in("message_type", messageTypes);
        messageUpdateWrapper.eq("receiver_id",uid);
        messageUpdateWrapper.eq("is_delivered",0);
        messageUpdateWrapper.set("is_delivered",1);
        int updateTotal = this.baseMapper.update(null, messageUpdateWrapper);
        if(updateTotal>0){
            redisUtils.hdecr(MessageConstant.MessageKey,String.valueOf(uid),updateTotal);
        }
        return updateTotal;
    }

    @Override
    public int getUnReadNumber(Long uid) {
        if(redisUtils.hHasKey(MessageConstant.MessageKey, String.valueOf(uid))){
            return (int) redisUtils.hget(MessageConstant.MessageKey, String.valueOf(uid));
        }
        return 0;
    }

    @Override
    public Page<MessageVo> getMessageVoList(Page<MessageVo> messagePage, Integer[] messageTypes, Long receiverId) {
        return this.baseMapper.getMessageVoList(messagePage,messageTypes, receiverId);
    }

    @Override
    public MessageTypeVo getUnReadMessageByType(Long uid) {
        MessageTypeVo messageTypeVo = new MessageTypeVo();
        if(redisUtils.hHasKey(MessageConstant.MessageKey,String.valueOf(uid))){
            QueryWrapper<Message> messageCommentsQueryWrapper = setQueryWrapper(uid);
            messageCommentsQueryWrapper.in("message_type",MessageConstant.comments);
            messageTypeVo.setHasCommentsMessage(this.count(messageCommentsQueryWrapper)>0);
            QueryWrapper<Message> messageLikesQueryWrapper = setQueryWrapper(uid);
            messageLikesQueryWrapper.in("message_type",MessageConstant.likes);
            messageTypeVo.setHasLikesMessage(this.count(messageLikesQueryWrapper)>0);
            QueryWrapper<Message> messageFollowsQueryWrapper = setQueryWrapper(uid);
            messageFollowsQueryWrapper.in("message_type",MessageConstant.follows);
            messageTypeVo.setHasFollowsMessage(this.count(messageFollowsQueryWrapper)>0);
        }
        return messageTypeVo;
    }

    public QueryWrapper<Message> setQueryWrapper(Long uid){
        QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
        messageQueryWrapper.eq("receiver_id",uid);
        messageQueryWrapper.eq("is_delivered",0);
        return messageQueryWrapper;
    }

}
