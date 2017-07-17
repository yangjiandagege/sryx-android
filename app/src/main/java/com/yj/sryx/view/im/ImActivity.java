package com.yj.sryx.view.im;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yj.sryx.R;
import com.yj.sryx.utils.ToastUtils;
import com.yj.sryx.view.BaseActivity;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPTCPConnection;

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

public class ImActivity extends BaseActivity {
    static final String HOST = "39.108.82.35";
    static final int PORT = 5222;
    @Bind(R.id.btn_login)
    Button btnLogin;

    private XMPPConnection mXMPPConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im);
        ButterKnife.bind(this);
        initXMPPConnection();
    }


    @OnClick({R.id.btn_register, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                register();
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }

    private void register() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    AccountManager accountManager = AccountManager.getInstance(mXMPPConn);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name","yangjian1");
                    accountManager.createAccount("yangjian1", "yangjian1", map);
                } catch (SmackException e) {
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

    private void login(){
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    mXMPPConn.login("miaomiao", "miaomiao", "eason.yang");
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
