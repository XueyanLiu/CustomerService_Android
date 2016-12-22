package com.customerservice.chat.model;

import com.customerservice.chat.jsonmodel.JsonParentEntity;

import java.io.Serializable;

/**
 * Created by Bill on 2016/12/22.
 */

public class ChatEntity implements Serializable{

    public static final int CHAT_TYPE_PEOPLE_SEND_TEXT = 0; // 发送普通文本
    public static final int CHAT_TYPE_ROBOT_TEXT = 1; // 接收机器人的text消息
    public static final int CHAT_TYPE_PEOPLE_SEND_IMAGE = 2; // 发送图片
    public static final int CHAT_TYPE_ROBOT_IMAGE = 3; // 接收图片
    public static final int CHAT_TYPE_NOTICE = 4; // 通知

    public int msgType; // 消息类型，决定在adapter中怎样展示
    public long time; // 消息时间
    public boolean isShowTime; // 是否显示时间标签
    public JsonParentEntity jsonParentEntity; // 客服消息
    public FileEntity fileEntity; // 文件消息
    public String nickName; // 昵称
    public String headUrl; // 头像uil
}
