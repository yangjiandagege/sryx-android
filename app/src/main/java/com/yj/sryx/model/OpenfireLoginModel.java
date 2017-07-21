package com.yj.sryx.model;

import android.graphics.drawable.Drawable;

import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.beans.SearchContact;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Message;

import java.util.List;

/**
 * Created by eason.yang on 2017/7/17.
 */

public interface OpenfireLoginModel {
    void connectThenRegisterThenLogin(String openid, String pwd, String nickname, SubscriberOnNextListener<Integer> callback);
    void connectThenLogin(String openid, String pwd, String nickname, SubscriberOnNextListener<Integer> callback);
}
