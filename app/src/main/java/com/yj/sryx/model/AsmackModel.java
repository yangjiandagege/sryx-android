package com.yj.sryx.model;

import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by eason.yang on 2017/7/17.
 */

public interface AsmackModel {
    void initXMPPConnection(SubscriberOnNextListener<Integer> callback);
    void register(String account, String pwd, String name, SubscriberOnNextListener<Integer> callback);
    void login(String account, String pwd, String name, SubscriberOnNextListener<Integer> callback);
    void registerThenLogin(String account, String pwd, String name, SubscriberOnNextListener<Integer> callback);
}
