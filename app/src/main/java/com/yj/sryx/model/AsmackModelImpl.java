package com.yj.sryx.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.yj.sryx.manager.XmppConnSingleton;
import com.yj.sryx.manager.httpRequest.subscribers.ProgressSubscriber;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.beans.SearchContact;
import com.yj.sryx.utils.FormatTools;
import com.yj.sryx.utils.LogUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by eason.yang on 2017/7/17.
 */

public class AsmackModelImpl implements AsmackModel {
    private Context mContext;

    public AsmackModelImpl(Context context) {
        mContext = context;
    }

    @Override
    public void searchFriends(final String keyword, final SubscriberOnNextListener<List<SearchContact>> callback) {
        final XMPPConnection connection = XmppConnSingleton.getInstance();
        Observable.create(new Observable.OnSubscribe<List<SearchContact>>() {
            @Override
            public void call(Subscriber<? super List<SearchContact>> subscriber) {
                try {
                    List<SearchContact> contactList = new ArrayList<SearchContact>();
                    // 创建搜索
                    UserSearchManager searchManager = new UserSearchManager(connection);
                    // 获取搜索表单
                    Form searchForm = searchManager.getSearchForm("search." + connection.getServiceName());
                    // 提交表单
                    Form answerForm = searchForm.createAnswerForm();
                    // 某个字段设成true就会在那个字段里搜索关键字，search字段设置要搜索的关键字
                    answerForm.setAnswer("search", keyword);
                    answerForm.setAnswer("Name", true);
                    // 提交搜索表单
                    ReportedData data = searchManager.getSearchResults(answerForm, "search." + connection.getServiceName());
                    // 遍历结果列
                    for (ReportedData.Row row : data.getRows()) {
                        String jid = row.getValues("jid").get(0);
                        SearchContact contact = new SearchContact();
                        VCard vCard = new VCard();
                        vCard.load(connection, jid);
                        contact.account = jid;
                        contact.name = vCard.getNickName();
                        LogUtils.logout(contact.getName());
                        contact.sex = vCard.getField("sex");
                        contact.province = vCard.getField("province");
                        contact.city = vCard.getField("city");
                        if(null != vCard.getAvatar()) {
                            contact.avatar = FormatTools.getInstance().InputStream2Drawable(new ByteArrayInputStream(vCard.getAvatar()));
                        }
                        contactList.add(contact);
                    }
                    subscriber.onNext(contactList);
                } catch (SmackException.NoResponseException
                        | XMPPException.XMPPErrorException
                        | SmackException.NotConnectedException e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } finally {
                    subscriber.onCompleted();
                }
            }
        })
        .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
        .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
        .subscribe(new ProgressSubscriber(callback, mContext));
    }

    @Override
    public void sendAddFriendApply(final String account, SubscriberOnNextListener<Integer> callback) {
        final XMPPConnection connection = XmppConnSingleton.getInstance();
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    Presence subscription=new Presence(Presence.Type.subscribe);
                    subscription.setTo(account);
                    connection.sendPacket(subscription);
                    subscriber.onNext(0);
                } catch (Exception e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } finally {
                    subscriber.onCompleted();
                }
            }
        })
        .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
        .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
        .subscribe(new ProgressSubscriber(callback, mContext));
    }



    @Override
    public void getAllRosterEntries(SubscriberOnNextListener<List<RosterEntry>> callback) {
        final XMPPConnection connection = XmppConnSingleton.getInstance();
        Observable.create(new Observable.OnSubscribe<List<RosterEntry>>() {
            @Override
            public void call(Subscriber<? super List<RosterEntry>> subscriber) {
                try {
                    List<RosterEntry> entrylist = new ArrayList<RosterEntry>();
                    Collection<RosterEntry> rosterEntry = connection.getRoster().getEntries();
                    Iterator<RosterEntry> i = rosterEntry.iterator();
                    while (i.hasNext()) {
                        RosterEntry entry = i.next();
                        entrylist.add(entry);
                        LogUtils.logout(entry.getUser()+" "+entry.getName()+" "+entry.getStatus()+" "+entry.getType());
                    }
                    subscriber.onNext(entrylist);
                } catch (Exception e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } finally {
                    subscriber.onCompleted();
                }
            }
        })
        .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
        .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
        .subscribe(new ProgressSubscriber(callback, mContext));
    }

    @Override
    public void addFriend(final String userName, final String name, SubscriberOnNextListener<Integer> callback) {
        final XMPPConnection connection = XmppConnSingleton.getInstance();
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    connection.getRoster().createEntry(userName, name, null);
                    subscriber.onNext(0);
                } catch (Exception e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } finally {
                    subscriber.onCompleted();
                }
            }
        })
        .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
        .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
        .subscribe(new ProgressSubscriber(callback, mContext));
    }

    @Override
    public void getHeaderPic(final String jid, SubscriberOnNextListener<Drawable> callback) {
        final XMPPConnection connection = XmppConnSingleton.getInstance();
        Observable.create(new Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                try {
                    VCard vCard = new VCard();
                    vCard.load(connection, jid);
                    subscriber.onNext(FormatTools.getInstance().InputStream2Drawable(new ByteArrayInputStream(vCard.getAvatar())));
                } catch (Exception e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } finally {
                    subscriber.onCompleted();
                }
            }
        })
        .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
        .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
        .subscribe(new ProgressSubscriber(callback, mContext, false));
    }



    @Override
    public void getVCard(final String jid, SubscriberOnNextListener<VCard> callback) {
        final XMPPConnection connection = XmppConnSingleton.getInstance();
        Observable.create(new Observable.OnSubscribe<VCard>() {
            @Override
            public void call(Subscriber<? super VCard> subscriber) {
                try {
                    VCard vCard = new VCard();
                    vCard.load(connection, jid);
                    subscriber.onNext(vCard);
                } catch (Exception e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } finally {
                    subscriber.onCompleted();
                }
            }
        })
        .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
        .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
        .subscribe(new ProgressSubscriber(callback, mContext, false));
    }

    @Override
    public void sendMessage(final String sessionJID, final String sessionName, final String message, final MessageListener listener, SubscriberOnNextListener<Integer> callback) {
        final XMPPConnection connection = XmppConnSingleton.getInstance();
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    /** 获取当前登陆用户的聊天管理器 */
                    ChatManager chatManager = ChatManager.getInstanceFor(connection);
                    Chat chat = chatManager.createChat(sessionJID, listener);
                    chat.sendMessage(message);
                    subscriber.onNext(0);
                } catch (XMPPException | SmackException.NotConnectedException e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } finally {
                    subscriber.onCompleted();
                }
            }
        })
        .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
//        .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
        .subscribe(new ProgressSubscriber(callback, mContext));
    }

    @Override
    public void getOfflineMessages(SubscriberOnNextListener<List<Message>> callback) {
        final XMPPConnection connection = XmppConnSingleton.getInstance();
        Observable.create(new Observable.OnSubscribe<List<Message>>() {
            @Override
            public void call(Subscriber<? super List<Message>> subscriber) {
                try {
                    OfflineMessageManager offlineManager = new OfflineMessageManager(connection);
                    List<Message> offlineMessageList = offlineManager.getMessages();
                    subscriber.onNext(offlineMessageList);
                } catch (XMPPException | SmackException.NotConnectedException | SmackException.NoResponseException e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } finally {
                    subscriber.onCompleted();
                }
            }
        })
        .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
//        .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
        .subscribe(new ProgressSubscriber(callback, mContext));
    }

    @Override
    public void isMyFriend(final String playerId, SubscriberOnNextListener<Boolean> callback) {
        final XMPPConnection connection = XmppConnSingleton.getInstance();
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    Boolean isMyFriend = false;
                    Collection<RosterEntry> rosterEntry = connection.getRoster().getEntries();
                    Iterator<RosterEntry> i = rosterEntry.iterator();
                    while (i.hasNext()) {
                        RosterEntry entry = i.next();
                        LogUtils.logout(entry.getUser()+" "+entry.getName()+" "+entry.getStatus()+" "+entry.getType());
                        if(entry.getUser().contains(playerId)){
                            isMyFriend = true;
                        }
                    }

                    subscriber.onNext(isMyFriend);
                } catch (Exception e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } finally {
                    subscriber.onCompleted();
                }
            }
        })
        .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
        .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
        .subscribe(new ProgressSubscriber(callback, mContext));
    }


    private static ConnectionListener mConnectionListener = new ConnectionListener() {

        @Override
        public void reconnectionSuccessful() {
            Log.i("connection", "reconnectionSuccessful");
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            Log.i("connection", "reconnectionFailed");
        }

        @Override
        public void reconnectingIn(int arg0) {
            Log.i("connection", "reconnectingIn");
        }

        @Override
        public void connected(XMPPConnection xmppConnection) {
            Log.i("connection", "connected");
        }

        @Override
        public void authenticated(XMPPConnection xmppConnection) {
            Log.i("connection", "authenticated");
        }

        @Override
        public void connectionClosed() {
            Log.i("connection", "connectionClosed");
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            Log.i("connection", "connectionClosedOnError");
        }
    };


    /**
     * Get image from newwork
     * @param path The path of image
     * @return byte[]
     * @throws Exception
     */
    public byte[] getImage(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            return readStream(inStream);
        }
        return null;
    }

    /**
     * Get image from newwork
     * @param path The path of image
     * @return InputStream
     * @throws Exception
     */
    public InputStream getImageStream(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            return conn.getInputStream();
        }
        return null;
    }

    /**
     * Get data from stream
     * @param inStream
     * @return byte[]
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }


}
