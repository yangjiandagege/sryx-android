package com.yj.sryx.model;

import android.content.Context;

import com.yj.sryx.manager.XmppConnSingleton;
import com.yj.sryx.manager.httpRequest.subscribers.ProgressSubscriber;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.utils.LogUtils;
import com.yj.sryx.utils.ToastUtils;
import com.yj.sryx.view.im.ImActivity;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPTCPConnection;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by eason.yang on 2017/7/17.
 */

public class AsmackModelImpl implements AsmackModel {
    static final String HOST = "39.108.82.35";
    static final int PORT = 5222;
    private Context mContext;

    public AsmackModelImpl(Context context) {
        mContext = context;
    }

    @Override
    public void initXMPPConnection(final SubscriberOnNextListener<Integer> callback) {
        LogUtils.logout("");
        if(null == XmppConnSingleton.getInstance()) {
            LogUtils.logout("");
            Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    ConnectionConfiguration config = new ConnectionConfiguration(HOST, PORT);
                    config.setDebuggerEnabled(true);
                    // 关闭安全模式
                    config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                    XMPPConnection connection = new XMPPTCPConnection(config);
                    try {
                        connection.connect();
                        ProviderManager.getInstance().addIQProvider(VCardManager.ELEMENT, VCardManager.NAMESPACE, new VCardProvider());
                        XmppConnSingleton.setXMPPConnection(connection);
                        subscriber.onNext(0);
                    } catch (SmackException | XMPPException | IOException e) {
                        subscriber.onError(e);
                        e.printStackTrace();
                    }finally {
                        subscriber.onCompleted();
                    }
                }
            })
            .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
            .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
            .subscribe(new ProgressSubscriber(callback, mContext, false));
        }else {
            callback.onSuccess(1);
        }
    }

    @Override
    public void register(final String account, final String pwd, final String name, final SubscriberOnNextListener<Integer> callback) {
        initXMPPConnection(new SubscriberOnNextListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                LogUtils.logout("");
                final XMPPConnection connection = XmppConnSingleton.getInstance();
                Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        LogUtils.logout("");
                        try {
                            AccountManager accountManager = AccountManager.getInstance(connection);
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("name", name);
                            accountManager.createAccount(account, pwd, map);
                            LogUtils.logout("");
                            subscriber.onNext(0);
                        } catch (SmackException | XMPPException e) {
                            LogUtils.logout("");
                            subscriber.onError(e);
                            e.printStackTrace();
                        } finally {
                            LogUtils.logout("");
                            subscriber.onCompleted();
                        }
                        LogUtils.logout("");
                    }
                })
                .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
                .subscribe(new ProgressSubscriber(callback, mContext));
            }

            @Override
            public void onError(String msg) {
                LogUtils.logout("");
                ToastUtils.showLongToast(mContext, "连接失败"+msg);
            }
        });

    }

    @Override
    public void login(final String account, final String pwd, final String name, final SubscriberOnNextListener<Integer> callback) {
        initXMPPConnection(new SubscriberOnNextListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                final XMPPConnection connection = XmppConnSingleton.getInstance();
                Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        try {
                            connection.login(account, pwd, connection.getServiceName());

                            VCard me = new VCard();
                            me.load(connection);
                            me.setNickName(name);
                            me.save(connection);
                            subscriber.onNext(0);
                        } catch (SmackException | IOException | XMPPException e) {
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
            public void onError(String msg) {

            }
        });
    }

    @Override
    public void registerThenLogin(final String account, final String pwd, final String name, final SubscriberOnNextListener<Integer> callback) {
        register(account, pwd, name, new SubscriberOnNextListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                login(account, pwd, name, callback);
            }

            @Override
            public void onError(String msg) {
                callback.onError(msg);
            }
        });
    }
}
