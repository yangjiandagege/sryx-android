package com.yj.sryx.model;

import android.content.Context;

import com.yj.sryx.SryxApp;
import com.yj.sryx.manager.XmppConnSingleton;
import com.yj.sryx.manager.httpRequest.subscribers.ProgressSubscriber;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.beans.Contact;
import com.yj.sryx.utils.FormatTools;
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
import org.jivesoftware.smack.util.Base64;
import org.jivesoftware.smack.util.StringUtils;
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
import java.util.HashMap;
import java.util.List;
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

                            VCard meVcard = new VCard();
                            meVcard.load(connection);
                            meVcard.setNickName(name);
                            meVcard.setAvatar(getImage(SryxApp.sWxUser.getHeadimgurl()));
                            meVcard.save(connection);

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

    @Override
    public void searchFriends(final String keyword, final SubscriberOnNextListener<List<Contact>> callback) {
        final XMPPConnection connection = XmppConnSingleton.getInstance();
        Observable.create(new Observable.OnSubscribe<List<Contact>>() {
            @Override
            public void call(Subscriber<? super List<Contact>> subscriber) {
                try {
                    List<Contact> contactList = new ArrayList<Contact>();
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
                        Contact contact = new Contact();
                        VCard vCard = new VCard();
                        vCard.load(connection, jid);
                        contact.account = jid;
                        contact.name = vCard.getNickName();
                        if(null != vCard.getAvatar()) {
                            LogUtils.logout(vCard.getAvatar().toString());
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
