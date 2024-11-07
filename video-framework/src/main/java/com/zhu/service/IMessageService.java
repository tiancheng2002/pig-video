package com.zhu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.model.entity.Message;
import com.zhu.vo.MessageTypeVo;
import com.zhu.vo.MessageVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2023-11-13
 */
public interface IMessageService extends IService<Message> {

    int updateDelivered(Long uid, String type);

    int getUnReadNumber(Long uid);

    Page<MessageVo> getMessageVoList(Page<MessageVo> messagePage,Integer[] messageTypes,Long receiverId);

    MessageTypeVo getUnReadMessageByType(Long uid);

}
