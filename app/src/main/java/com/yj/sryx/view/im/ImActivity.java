package com.yj.sryx.view.im;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.yj.sryx.R;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.view.game.GameRecordJudgeFragment;
import com.yj.sryx.view.game.GameRecordPlayerFragment;
import com.yj.sryx.view.game.GameRecordsFragment;
import com.yj.sryx.view.game.QuizActivity;

import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImActivity extends BaseActivity {
    public final static String TAB_SESSION = "消息";
    public final static String TAB_CONTACT = "联系人";
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.vp_content)
    ViewPager vpContent;

    private Activity mActivity;
    private List<String> mTabList;
    private List<Fragment> mFragList;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im);
        ButterKnife.bind(this);
        mActivity = this;
        initFragment();
    }

    private void initFragment() {
        mTabList = new ArrayList<>();
        mTabList.add(TAB_SESSION);
        mTabList.add(TAB_CONTACT);

        mFragList = new ArrayList<>();
        mFragList.add(new SessionsFragment());
        mFragList.add(new ContactsFragment());

        mAdapter = new MyAdapter(getSupportFragmentManager(), mFragList, mTabList);
        vpContent.setAdapter(mAdapter);
        tabs.setupWithViewPager(vpContent);
    }

    private class MyAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;
        private List<String> mTabList;

        MyAdapter(FragmentManager fm, List<Fragment> fragments, List<String> tabList) {
            super(fm);
            this.mFragmentList = fragments;
            this.mTabList = tabList;
        }

        @Override
        public Fragment getItem(int position) {
            return (mFragmentList == null || mFragmentList.size() == 0) ? null : mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList == null ? 0 : mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_im, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_friend:
                startActivity(new Intent(ImActivity.this, SearchFriendActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    //
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btn_register:
//                mAsmackModel.register(sWxUser.getOpenid(), sWxUser.getOpenid(), sWxUser.getNickname(), new SubscriberOnNextListener<Integer>() {
//                    @Override
//                    public void onSuccess(Integer integer) {
//                        ToastUtils.showLongToast(mActivity, "注册Openfire成功！");
//                    }
//
//                    @Override
//                    public void onError(String msg) {
//                        ToastUtils.showLongToast(mActivity, "注册Openfire失败！");
//                    }
//                });
////                register();
//                break;
//            case R.id.btn_login:
//                mAsmackModel.login(sWxUser.getOpenid(), sWxUser.getOpenid(), sWxUser.getNickname(), new SubscriberOnNextListener<Integer>() {
//                    @Override
//                    public void onSuccess(Integer integer) {
//                        ToastUtils.showLongToast(mActivity, "登录Openfire成功！");
//                    }
//
//                    @Override
//                    public void onError(String msg) {
//                        ToastUtils.showLongToast(mActivity, "登录Openfire失败！");
//                    }
//                });
////                login();
//                break;
//            case R.id.btn_search:
//                searchAccount();
//                break;
//            case R.id.btn_add_friend:
//                addFriend();
//                break;
//            case R.id.btn_vcard:
//                getVcard();
//                break;
//        }
//    }
//
//    private void getVcard() {
//        Observable.create(new Observable.OnSubscribe<Contact>() {
//            @Override
//            public void call(Subscriber<? super Contact> subscriber) {
//                Contact contact = new Contact();
//                VCard vCard = new VCard();
//                try {
//                    vCard.load(mXMPPConn, mUserTmp);
//                    contact.account = mUserTmp;
//                    contact.name = vCard.getNickName();
////                    contact.avatar = Base64.encodeBytes(vCard.getAvatar());
//                    subscriber.onNext(contact);
//                } catch (SmackException.NoResponseException
//                        | XMPPException.XMPPErrorException
//                        | SmackException.NotConnectedException e) {
//                    LogUtils.logout("  "+e.getMessage());
//                    e.printStackTrace();
//                    subscriber.onError(e);
//                }
//                subscriber.onCompleted();
//            }
//        })
//                .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
//                .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
//                .subscribe(new Observer<Contact>() {   //订阅观察者（其实是观察者订阅被观察者）
//                    @Override
//                    public void onNext(Contact res) {
//                        ToastUtils.showLongToast(ImActivity.this, "account：" + res.account+" name: "+res.name+" avatar: "+res.avatar);
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        ToastUtils.showLongToast(ImActivity.this, "获取联系人信息失败"+e);
//                    }
//                });
//    }
//
//    private void addFriend() {
//
//    }
//
//    private void searchAccount() {
//        Observable.create(new Observable.OnSubscribe<ReportedData.Row>() {
//            @Override
//            public void call(Subscriber<? super ReportedData.Row> subscriber) {
//                try {
//                    // 创建搜索
//                    UserSearchManager searchManager = new UserSearchManager(mXMPPConn);
//                    LogUtils.logout("search." + mXMPPConn.getServiceName());
//                    // 获取搜索表单
//                    Form searchForm = searchManager.getSearchForm("search." + mXMPPConn.getServiceName());
//                    // 提交表单
//                    Form answerForm = searchForm.createAnswerForm();
//                    // 某个字段设成true就会在那个字段里搜索关键字，search字段设置要搜索的关键字
//                    answerForm.setAnswer("search", "miaomiao");
//                    answerForm.setAnswer("Username", true);
//                    // 提交搜索表单
//                    ReportedData data = searchManager.getSearchResults(answerForm, "search." + mXMPPConn.getServiceName());
//                    // 遍历结果列
//                    for (ReportedData.Row row : data.getRows()) {
//                        subscriber.onNext(row);
//                    }
//                } catch (SmackException.NoResponseException e) {
//                    subscriber.onError(e);
//                    e.printStackTrace();
//                } catch (XMPPException.XMPPErrorException e) {
//                    subscriber.onError(e);
//                    e.printStackTrace();
//                } catch (SmackException.NotConnectedException e) {
//                    subscriber.onError(e);
//                    e.printStackTrace();
//                }
//                subscriber.onCompleted();
//            }
//        })
//                .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
//                .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
//                .subscribe(new Observer<ReportedData.Row>() {   //订阅观察者（其实是观察者订阅被观察者）
//                    @Override
//                    public void onNext(ReportedData.Row res) {
//                        LogUtils.logout("搜到：" + res.getValues("jid").get(0));
//                        ToastUtils.showLongToast(ImActivity.this, "搜到：" + res.getValues("jid").get(0));
//                        mUserTmp = res.getValues("jid").get(0);
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        ToastUtils.showLongToast(ImActivity.this, "搜索失败");
//                    }
//                });
//    }
//
//    private void register() {
//        Observable.create(new Observable.OnSubscribe<Integer>() {
//            @Override
//            public void call(Subscriber<? super Integer> subscriber) {
//                try {
//                    AccountManager accountManager = AccountManager.getInstance(mXMPPConn);
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put("name", "yangjiandagege");
//                    accountManager.createAccount("yangjiandagege", "yangjiandagege", map);
//                } catch (SmackException | XMPPException e) {
//                    subscriber.onError(e);
//                    e.printStackTrace();
//                }
//                subscriber.onNext(0);
//                subscriber.onCompleted();
//            }
//        })
//                .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
//                .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
//                .subscribe(new Observer<Integer>() {   //订阅观察者（其实是观察者订阅被观察者）
//                    @Override
//                    public void onNext(Integer res) {
//                        ToastUtils.showLongToast(ImActivity.this, "注册成功");
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        ToastUtils.showLongToast(ImActivity.this, "注册失败");
//                    }
//                });
//    }
//
//    private void login() {
//        Observable.create(new Observable.OnSubscribe<Integer>() {
//            @Override
//            public void call(Subscriber<? super Integer> subscriber) {
//                try {
//                    mXMPPConn.login("yangjian1", "yangjian1", mXMPPConn.getServiceName());
//
//                    VCard me = new VCard();
//                    me.load(mXMPPConn);
//                    me.setEmailHome("miaomiao@sina.com");
//                    me.setOrganization("售后");
//                    me.setNickName("颜昌军t2");
//                    me.setField("sex", "男");
//                    me.setPhoneWork("PHONE", "3443233");
//                    me.setField("DESC", "描述信息。。。");
//                    me.save(mXMPPConn);
//                } catch (SmackException e) {
//                    subscriber.onError(e);
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    subscriber.onError(e);
//                    e.printStackTrace();
//                } catch (XMPPException e) {
//                    subscriber.onError(e);
//                    e.printStackTrace();
//                }
//                subscriber.onNext(0);
//                subscriber.onCompleted();
//            }
//        })
//                .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
//                .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
//                .subscribe(new Observer<Integer>() {   //订阅观察者（其实是观察者订阅被观察者）
//                    @Override
//                    public void onNext(Integer res) {
//                        ToastUtils.showLongToast(ImActivity.this, "登录成功");
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        ToastUtils.showLongToast(ImActivity.this, "登录失败");
//                    }
//                });
//    }
//
//    private void initXMPPConnection() {
//        Observable.create(new Observable.OnSubscribe<Integer>() {
//            @Override
//            public void call(Subscriber<? super Integer> subscriber) {
//                ConnectionConfiguration config = new ConnectionConfiguration(HOST, PORT);
//                config.setDebuggerEnabled(true);
//                // 关闭安全模式
//                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
//                mXMPPConn = new XMPPTCPConnection(config);
//                try {
//                    mXMPPConn.connect();
//                    ProviderManager.getInstance().addIQProvider(VCardManager.ELEMENT, VCardManager.NAMESPACE, new VCardProvider());
//                } catch (SmackException e) {
//                    subscriber.onError(e);
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    subscriber.onError(e);
//                    e.printStackTrace();
//                } catch (XMPPException e) {
//                    subscriber.onError(e);
//                    e.printStackTrace();
//                }
//                subscriber.onNext(0);
//                subscriber.onCompleted();
//            }
//        })
//                .subscribeOn(Schedulers.io()) // 指定subscribe()发生在IO线程
//                .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在UI线程
//                .subscribe(new Observer<Integer>() {   //订阅观察者（其实是观察者订阅被观察者）
//                    @Override
//                    public void onNext(Integer res) {
//                        ToastUtils.showLongToast(ImActivity.this, "连接成功");
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        ToastUtils.showLongToast(ImActivity.this, "连接失败");
//                    }
//                });
//    }
}
