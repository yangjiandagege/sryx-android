package com.yj.sryx.model;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.widget.ImageView;

import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.beans.Contact;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
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
    void searchFriends(String keyword, SubscriberOnNextListener<List<Contact>> callback);
    void sendAddFriendApply(String account, SubscriberOnNextListener<Integer> callback);
    void getAllRosterEntries(SubscriberOnNextListener<List<RosterEntry>> callback);
    void addFriend(String userName, String name, SubscriberOnNextListener<Integer> callback);
    void getHeaderPic(String jid, SubscriberOnNextListener<Drawable> callback);
    void sendMessage(String sessionJID, String sessionName, String message, MessageListener listener, SubscriberOnNextListener<Integer> callback);
}
