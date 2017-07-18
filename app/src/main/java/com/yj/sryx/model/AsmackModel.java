package com.yj.sryx.model;

import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.beans.Contact;

import org.jivesoftware.smack.XMPPConnection;

import java.util.List;

/**
 * Created by eason.yang on 2017/7/17.
 */

public interface AsmackModel {
    void initXMPPConnection(SubscriberOnNextListener<Integer> callback);
    void register(String account, String pwd, String name, SubscriberOnNextListener<Integer> callback);
    void login(String account, String pwd, String name, SubscriberOnNextListener<Integer> callback);
    void registerThenLogin(String account, String pwd, String name, SubscriberOnNextListener<Integer> callback);
    void searchFriends(String keyword, final SubscriberOnNextListener<List<Contact>> callback);
}
