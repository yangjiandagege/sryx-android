package com.yj.sryx.model.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by eason.yang on 2017/7/20.
 */
@Entity
public class ChatSession {
    @Id
    private String sessionId;
    private String sessionName;
    private long lastTime;
    private String lastBody;
    private int unreadCount;

    @Generated(hash = 1135951920)
    public ChatSession(String sessionId, String sessionName, long lastTime,
            String lastBody, int unreadCount) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.lastTime = lastTime;
        this.lastBody = lastBody;
        this.unreadCount = unreadCount;
    }

    @Generated(hash = 1350292942)
    public ChatSession() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public String getLastBody() {
        return this.lastBody;
    }
    public void setLastBody(String lastBody) {
        this.lastBody = lastBody;
    }
    public int getUnreadCount() {
        return this.unreadCount;
    }
    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

}
