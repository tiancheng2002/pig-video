package com.zhu.model.enums;

import com.zhu.constant.MessageConstant;

public enum MessageTypeEnums {

    FAVOUR("赞了你的视频",MessageConstant.MESSAGE_FAVOUR),
    STAR("收藏了你的视频",MessageConstant.MESSAGE_STAR),
    COMMENT("评论了你的视频",MessageConstant.MESSAGE_COMMENT),
    COMMENT_FAVOUR("点赞了你的评论",MessageConstant.MESSAGE_COMMENT_FAVOUR),
    COMMENT_REPLY("回复了你的评论",MessageConstant.MESSAGE_COMMENT_REPLY),
    FOLLOW("关注你了",MessageConstant.MESSAGE_FOLLOW);

    private String text;

    private Integer value;

    MessageTypeEnums(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static MessageTypeEnums setCommentText(MessageTypeEnums messageTypeEnums, String text) {
        messageTypeEnums.text = text;
        return messageTypeEnums;
    }

}
