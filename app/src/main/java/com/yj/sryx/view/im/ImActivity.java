package com.yj.sryx.view.im;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yj.sryx.R;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.model.beans.Contact;
import com.yj.sryx.utils.LogUtils;
import com.yj.sryx.utils.ToastUtils;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.view.game.MainActivity;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.yj.sryx.SryxApp.sWxUser;

public class ImActivity extends BaseActivity {
    static final String HOST = "39.108.82.35";
    static final int PORT = 5222;
    @Bind(R.id.btn_login)
    Button btnLogin;

    private String mUserTmp;
    private XMPPConnection mXMPPConn;
    private AsmackModel mAsmackModel;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im);
        ButterKnife.bind(this);
//        initXMPPConnection();
        mAsmackModel = new AsmackModelImpl(this);
        mActivity = this;
    }


    @OnClick({R.id.btn_register, R.id.btn_login, R.id.btn_search, R.id.btn_add_friend, R.id.btn_vcard})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                mAsmackModel.register(sWxUser.getOpenid(), sWxUser.getOpenid(), sWxUser.getNickname(), new SubscriberOnNextListener<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        ToastUtils.showLongToast(mActivity, "注册Openfire成功！");
                    }

                    @Override
                    public void onError(String msg) {
                        ToastUtils.showLongToast(mActivity, "注册Openfire失败！");
                    }
                });
//                register();
                break;
            case R.id.btn_login:
                mAsmackModel.login(sWxUser.getOpenid(), sWxUser.getOpenid(), sWxUser.getNickname(), new SubscriberOnNextListener<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        ToastUtils.showLongToast(mActivity, "登录Openfire成功！");
                    }

                    @Override
                    public void onError(String msg) {
                        ToastUtils.showLongToast(mActivity, "登录Openfire失败！");
                    }
                });
//                login();
                break;
            case R.id.btn_search:
                searchAccount();
                break;
            case R.id.btn_add_friend:
                addFriend();
                break;
            case R.id.btn_vcard:
                getVcard();
                break;
        }
    }

    private void getVcard() {
        Observable.create(new Observable.OnSubscribe<Contact>() {
            @Override
            public void call(Subscriber<? super Contact> subscriber) {
                Contact contact = new Contact();
                VCard vCard = new VCard();
                try {
                    vCard.load(mXMPPConn, mUserTmp);
                    contact.account = mUserTmp;
                    contact.name = vCard.getNickName();
//                    contact.avatar = Base64.encodeBytes(vCard.getAvatar());
                    subscriber.onNext(contact);
                } catch (SmackException.NoResponseException
                        | XMPPException.XMPPErrorException
                        | SmackException.NotConnectedException e) {
                    LogUtils.logout("  "+e.getMessage());
                    e.printStackTrace();
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
                .subscribe(new Observer<Contact>() {   //订阅观察者（其实是观察者订阅被观察者）
                    @Override
                    public void onNext(Contact res) {
                        ToastUtils.showLongToast(ImActivity.this, "account：" + res.account+" name: "+res.name+" avatar: "+res.avatar);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showLongToast(ImActivity.this, "获取联系人信息失败"+e);
                    }
                });
    }

    private void addFriend() {

    }

    private void searchAccount() {
        Observable.create(new Observable.OnSubscribe<ReportedData.Row>() {
            @Override
            public void call(Subscriber<? super ReportedData.Row> subscriber) {
                try {
                    // 创建搜索
                    UserSearchManager searchManager = new UserSearchManager(mXMPPConn);
                    LogUtils.logout("search." + mXMPPConn.getServiceName());
                    // 获取搜索表单
                    Form searchForm = searchManager.getSearchForm("search." + mXMPPConn.getServiceName());
                    // 提交表单
                    Form answerForm = searchForm.createAnswerForm();
                    // 某个字段设成true就会在那个字段里搜索关键字，search字段设置要搜索的关键字
                    answerForm.setAnswer("search", "miaomiao");
                    answerForm.setAnswer("Username", true);
                    // 提交搜索表单
                    ReportedData data = searchManager.getSearchResults(answerForm, "search." + mXMPPConn.getServiceName());
                    // 遍历结果列
                    for (ReportedData.Row row : data.getRows()) {
                        subscriber.onNext(row);
                    }
                } catch (SmackException.NoResponseException e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                }
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
                .subscribe(new Observer<ReportedData.Row>() {   //订阅观察者（其实是观察者订阅被观察者）
                    @Override
                    public void onNext(ReportedData.Row res) {
                        LogUtils.logout("搜到：" + res.getValues("jid").get(0));
                        ToastUtils.showLongToast(ImActivity.this, "搜到：" + res.getValues("jid").get(0));
                        mUserTmp = res.getValues("jid").get(0);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showLongToast(ImActivity.this, "搜索失败");
                    }
                });
    }

    private void register() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    AccountManager accountManager = AccountManager.getInstance(mXMPPConn);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name", "yangjiandagege");
                    accountManager.createAccount("yangjiandagege", "yangjiandagege", map);
                } catch (SmackException | XMPPException e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                }
                subscriber.onNext(0);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
                .subscribe(new Observer<Integer>() {   //订阅观察者（其实是观察者订阅被观察者）
                    @Override
                    public void onNext(Integer res) {
                        ToastUtils.showLongToast(ImActivity.this, "注册成功");
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showLongToast(ImActivity.this, "注册失败");
                    }
                });
    }

    private void login() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    mXMPPConn.login("yangjian1", "yangjian1", mXMPPConn.getServiceName());

                    VCard me = new VCard();
                    me.load(mXMPPConn);
                    me.setEmailHome("miaomiao@sina.com");
                    me.setOrganization("售后");
                    me.setNickName("颜昌军t2");
                    me.setField("sex", "男");
                    me.setPhoneWork("PHONE", "3443233");
                    me.setField("DESC", "描述信息。。。");
                    me.save(mXMPPConn);
                } catch (SmackException e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } catch (IOException e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } catch (XMPPException e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                }
                subscriber.onNext(0);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
                .subscribe(new Observer<Integer>() {   //订阅观察者（其实是观察者订阅被观察者）
                    @Override
                    public void onNext(Integer res) {
                        ToastUtils.showLongToast(ImActivity.this, "登录成功");
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showLongToast(ImActivity.this, "登录失败");
                    }
                });
    }

    private void initXMPPConnection() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                ConnectionConfiguration config = new ConnectionConfiguration(HOST, PORT);
                config.setDebuggerEnabled(true);
                // 关闭安全模式
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                mXMPPConn = new XMPPTCPConnection(config);
                try {
                    mXMPPConn.connect();
                    ProviderManager.getInstance().addIQProvider(VCardManager.ELEMENT, VCardManager.NAMESPACE, new VCardProvider());
                } catch (SmackException e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } catch (IOException e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                } catch (XMPPException e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                }
                subscriber.onNext(0);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
                .subscribe(new Observer<Integer>() {   //订阅观察者（其实是观察者订阅被观察者）
                    @Override
                    public void onNext(Integer res) {
                        ToastUtils.showLongToast(ImActivity.this, "连接成功");
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showLongToast(ImActivity.this, "连接失败");
                    }
                });
    }
}
