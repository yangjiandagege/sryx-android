package com.yj.sryx.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.yj.sryx.SryxApp;
import com.yj.sryx.manager.XmppConnSingleton;
import com.yj.sryx.manager.httpRequest.subscribers.ProgressSubscriber;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.beans.SearchContact;
import com.yj.sryx.model.beans.WxGetTokenRes;
import com.yj.sryx.model.beans.WxUser;
import com.yj.sryx.utils.FormatTools;
import com.yj.sryx.utils.LogUtils;
import com.yj.sryx.utils.ToastUtils;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPTCPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.Form;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by eason.yang on 2017/7/17.
 */

public class OpenfireLoginModelImpl implements OpenfireLoginModel {
    static final String HOST = "39.108.82.35";
    static final int PORT = 5222;
    private Context mContext;

    public OpenfireLoginModelImpl(Context context) {
        mContext = context;
    }

    public void connectThenRegisterThenLogin(final String account, final String pwd, final String name, final SubscriberOnNextListener<Integer> callback){
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                //连接
                try {
                    connect();
                    subscriber.onNext(0);
                } catch (IOException | XMPPException | SmackException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }finally {
                    subscriber.onCompleted();
                }
            }
        }).flatMap(new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer result) {
                return Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        try {
                            register(account, pwd, name);
                            subscriber.onNext(0);
                        } catch (SmackException.NotConnectedException | XMPPException.XMPPErrorException | SmackException.NoResponseException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        } finally {
                            subscriber.onCompleted();
                        }
                    }
                });
            }
        }).flatMap(new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer result) {
                return Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        //登录
                        try {
                            login(account, pwd, name);
                            subscriber.onNext(0);
                        } catch (Exception e) {
                            subscriber.onError(e);
                            e.printStackTrace();
                        } finally {
                            subscriber.onCompleted();
                        }
                    }
                });
            }
        })
        .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
        .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                LogUtils.logout("connectThenRegisterThenLogin onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(e.getMessage());
                LogUtils.logout("connectThenRegisterThenLogin onError " + e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                callback.onSuccess(integer);
                LogUtils.logout("connectThenRegisterThenLogin onNext ");
            }
        });
    }

    public void connectThenLogin(final String account, final String pwd, final String name, final SubscriberOnNextListener<Integer> callback){
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                //连接
                try {
                    connect();
                    subscriber.onNext(0);
                } catch (IOException | XMPPException | SmackException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }finally {
                    subscriber.onCompleted();
                }
            }
        }).flatMap(new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer result) {
                return Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        //登录
                        try {
                            login(account, pwd, name);
                            subscriber.onNext(0);
                        } catch (Exception e) {
                            subscriber.onError(e);
                            e.printStackTrace();
                        } finally {
                            subscriber.onCompleted();
                        }
                    }
                });
            }
        })
        .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
        .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                LogUtils.logout("connectThenLogin onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(e.getMessage());
                LogUtils.logout("connectThenLogin onError " + e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                callback.onSuccess(integer);
                LogUtils.logout("connectThenLogin onNext ");
            }
        });
    }


    private void connect() throws IOException, XMPPException, SmackException {
        ConnectionConfiguration config = new ConnectionConfiguration(HOST, PORT);
        config.setReconnectionAllowed(true);
        config.setDebuggerEnabled(true);
        config.setSendPresence(false);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        XMPPConnection connection = new XMPPTCPConnection(config);
        ProviderManager.getInstance().addIQProvider(VCardManager.ELEMENT, VCardManager.NAMESPACE, new VCardProvider());
        connection.connect();
        XmppConnSingleton.setXMPPConnection(connection);
    }

    private void register(String account, String pwd, String name) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        XMPPConnection connection = XmppConnSingleton.getInstance();
        AccountManager accountManager = AccountManager.getInstance(connection);
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", name);
        accountManager.createAccount(account, pwd, map);
    }

    private void login(String account, String pwd, String name) throws Exception {
        XMPPConnection connection = XmppConnSingleton.getInstance();
        connection.login(account, pwd, connection.getServiceName());

        VCard meVcard = new VCard();
        meVcard.load(connection);
        meVcard.setNickName(name);
        meVcard.setAvatar(getImage(SryxApp.sWxUser.getHeadimgurl()));
        if(SryxApp.sWxUser.getSex() == 1){
            meVcard.setField("sex", "男");
        }else if (SryxApp.sWxUser.getSex() == 0){
            meVcard.setField("sex", "女");
        }else {
            meVcard.setField("sex", " ");
        }
        meVcard.save(connection);
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
