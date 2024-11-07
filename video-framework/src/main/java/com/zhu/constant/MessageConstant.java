package com.zhu.constant;

import java.util.HashMap;
import java.util.Map;

public interface MessageConstant {

    String MessageKey = "message_unRead";

    //点赞消息
    Integer MESSAGE_FAVOUR = 1;

    //收藏消息
    Integer MESSAGE_STAR = 2;

    //评论消息
    Integer MESSAGE_COMMENT = 3;

    //评论点赞消息
    Integer MESSAGE_COMMENT_FAVOUR = 4;

    //被回复消息
    Integer MESSAGE_COMMENT_REPLY = 5;

    //关注消息
    Integer MESSAGE_FOLLOW = 6;

    //第一类消息（评论和被回复）
    Integer[] comments = {MESSAGE_COMMENT,MESSAGE_COMMENT_REPLY};

    //第二类消息（点赞和收藏）
    Integer[] likes = {MESSAGE_FAVOUR,MESSAGE_STAR,MESSAGE_COMMENT_FAVOUR};

    //第三类消息（关注）
    Integer[] follows = {MESSAGE_FOLLOW};

    //消息Map集合
    Map<String,Integer[]> messageMap = new HashMap<String,Integer[]>() {{
        put("comments", comments);
        put("likes", likes);
        put("follows", follows);
    }};

}
