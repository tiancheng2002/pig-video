package com.zhu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.model.entity.Message;
import com.zhu.vo.MessageVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2023-11-13
 */
public interface MessageMapper extends BaseMapper<Message> {

    Page<MessageVo> getMessageVoList(Page<MessageVo> messagePage,@Param("messageTypes") Integer[] messageTypes,@Param("receiverId") Long receiverId);

}
