package com.yj.sryx.model.beans;

/**
 * Created by eason.yang on 2017/7/19.
 */

public class ChatMessage {
    private String from;
    private String to;
    private String body;
    private String time;
    private boolean isRead;
    private boolean isSendOk;

    public ChatMessage() {
    }

    public ChatMessage(String from, String body) {
        this.from = from;
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isSendOk() {
        return isSendOk;
    }

    public void setSendOk(boolean sendOk) {
        isSendOk = sendOk;
    }
}
