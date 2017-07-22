package com.yj.sryx.model;

import android.graphics.drawable.Drawable;

import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.beans.SearchContact;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.List;

/**
 * Created by eason.yang on 2017/7/17.
 */

public interface AsmackModel {
    void searchFriends(String keyword, SubscriberOnNextListener<List<SearchContact>> callback);
    void sendAddFriendApply(String account, SubscriberOnNextListener<Integer> callback);
    void getAllRosterEntries(SubscriberOnNextListener<List<RosterEntry>> callback);
    void addFriend(String userName, String name, SubscriberOnNextListener<Integer> callback);
    void getHeaderPic(String jid, SubscriberOnNextListener<Drawable> callback);
    void getVCard(String jid, SubscriberOnNextListener<VCard> callback);
    void sendMessage(String sessionJID, String sessionName, String message, MessageListener listener, SubscriberOnNextListener<Integer> callback);
    void getOfflineMessages(SubscriberOnNextListener<List<Message>> callback);
    void isMyFriend(String playId, SubscriberOnNextListener<Boolean> callback);
    void createRoom(String roomName, String password, SubscriberOnNextListener<Integer> callback);
    void getChatRooms(SubscriberOnNextListener<List<DiscoverItems.Item>> callback);
}
