package com.yj.sryx.model.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by eason.yang on 2017/7/19.
 */
@Entity
public class ChatMessage {
    private String from;
    private String to;
    private String body;
    private long time;
    private boolean isRead;
    private boolean isSendOk;

    @Generated(hash = 898818499)
    public ChatMessage(String from, String to, String body, long time,
            boolean isRead, boolean isSendOk) {
        this.from = from;
        this.to = to;
        this.body = body;
        this.time = time;
        this.isRead = isRead;
        this.isSendOk = isSendOk;
    }

    @Generated(hash = 2271208)
    public ChatMessage() {
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean getIsRead() {
        return this.isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean getIsSendOk() {
        return this.isSendOk;
    }

    public void setIsSendOk(boolean isSendOk) {
        this.isSendOk = isSendOk;
    }
}
