package com.zhu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.annotation.AuthCheck;
import com.zhu.constant.MessageConstant;
import com.zhu.exception.ErrorException;
import com.zhu.model.dto.message.MessageQueryRequest;
import com.zhu.model.dto.message.MessageUpdateRequest;
import com.zhu.result.RespBean;
import com.zhu.result.RespBeanEnum;
import com.zhu.service.IMessageService;
import com.zhu.service.IUserService;
import com.zhu.vo.LoginUserVo;
import com.zhu.vo.MessageTypeVo;
import com.zhu.vo.MessageVo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private IMessageService messageService;

    @Resource
    private IUserService userService;

    /**
     * 分页获取指定类型的消息
     * @param messageQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/user/type")
    @ApiOperation(value = "获取指定类型消息")
    @AuthCheck
    public RespBean getMessageByType(@RequestBody MessageQueryRequest messageQueryRequest, HttpServletRequest request){
        //获取消息的会带上对应类型是否有消息，如果有消息的话，那么就需要将该类型所有未读消息更改为已读
        String type = messageQueryRequest.getType();
        long current = messageQueryRequest.getCurrent();
        long pageSize = messageQueryRequest.getPageSize();
        if(StringUtils.isBlank(type)){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        Integer[] messageTypes = MessageConstant.messageMap.get(type);
        if(ArrayUtils.isEmpty(messageTypes)){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
//        QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
//        messageQueryWrapper.in("message_type", messageTypes);
//        messageQueryWrapper.eq("receiver_id",loginUser.getUid());
        //todo 消息类需要转成带有用户和视频封面的消息返回类
        Page<MessageVo> messageVoList = messageService.getMessageVoList(new Page<>(current, pageSize), messageTypes, loginUser.getUid());
//        Page<Message> messagePage = messageService.page(new Page<>(current, pageSize), messageQueryWrapper);
        return RespBean.success(messageVoList);
    }

    /**
     * 更新某个类型的未读消息数量
     * @param messageUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update/read")
    @AuthCheck
    public RespBean updateMessageDelivered(@RequestBody MessageUpdateRequest messageUpdateRequest,HttpServletRequest request){
        //根据对应消息类型更新消息未读状态，并且更新Redis缓存当中的未读消息数量
        if(messageUpdateRequest==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
        Long uid = loginUser.getUid();
        String type = messageUpdateRequest.getType();
        int updateTotal = messageService.updateDelivered(uid, type);
        return RespBean.success(updateTotal>0);
    }

    /**
     * 获取用户未读消息数量
     * @param request
     * @return
     */
    @GetMapping("/unRead")
    public RespBean getUnReadMessageNumber(HttpServletRequest request){
        if(request==null){
            throw new ErrorException(RespBeanEnum.PARAM_ERROR);
        }
        LoginUserVo loginUser = userService.getLoginUser(request);
        int total = 0;
        if(loginUser!=null){
            // todo 将数据库中消息的状态更改为已读
            Long uid = loginUser.getUid();
            total = messageService.getUnReadNumber(uid);
        }
        return RespBean.success(total);
    }

    /**
     * 根据消息类型获取用户是否有未读消息
     * @param request
     * @return
     */
    @GetMapping("/unRead/type")
    @AuthCheck
    public RespBean getUnReadMessageByType(HttpServletRequest request){
        //首先去Redis当中判断当前用户是否有未读消息
        LoginUserVo loginUser = userService.getLoginUser(request);
        Long uid = loginUser.getUid();
        MessageTypeVo messageTypeVo = messageService.getUnReadMessageByType(uid);
        return RespBean.success(messageTypeVo);
    }

}
