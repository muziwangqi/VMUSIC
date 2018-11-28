package com.soling.bt.model;

import java.io.Serializable;

public class ChatInfo implements Serializable {
    private int chatId;
    private FriendInfo friendInfo;
    private BaseMessage baseMessage;
    private boolean isSend;
    private String sendTime;
    private String receiveTime;

    public int getChatId() {
        return chatId;
    }

    public ChatInfo setChatId(int chatId) {
        this.chatId = chatId;
        return this;
    }

    public FriendInfo getFriendInfo() {
        return friendInfo;
    }

    public ChatInfo setFriendInfo(FriendInfo friendInfo) {
        this.friendInfo = friendInfo;
        return this;
    }

    public BaseMessage getBaseMessage() {
        return baseMessage;
    }

    public ChatInfo setBaseMessage(BaseMessage baseMessage) {
        this.baseMessage = baseMessage;
        return this;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public ChatInfo setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
        return this;
    }

    public String getSendTime() {
        return sendTime;
    }

    public ChatInfo setSendTime(String sendTime) {
        this.sendTime = sendTime;
        return this;
    }


    @Override
    public String toString() {
        return "ChatInfo{"+
                ",chatId="+chatId+
                ",friendInfo="+friendInfo+
                ",baseMessage="+baseMessage+
                ",isSend="+isSend+
                ",sendTime='"+sendTime+'\''+
                ",receiveTime='"+receiveTime+'\'';
    }
}
