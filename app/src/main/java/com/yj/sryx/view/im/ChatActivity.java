package com.yj.sryx.view.im;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.manager.RxBus;
import com.yj.sryx.manager.XmppConnSingleton;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.model.beans.ChatMessage;
import com.yj.sryx.model.beans.ChatMessageDao.Properties;
import com.yj.sryx.utils.ToastUtils;
import com.yj.sryx.widget.AcceBar;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ChatActivity extends AppCompatActivity {
    public static String OTHER_USER_ID;
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_USER = "user";
    @Bind(R.id.toolbar)
    AcceBar toolbar;
    @Bind(R.id.edt_msg_content)
    EditText edtMsgContent;
    @Bind(R.id.btn_send_msg)
    Button btnSendMsg;
    @Bind(R.id.rv_chat)
    RecyclerView rvChat;

    private AsmackModel mAsmackModel;
    private String mOtherUser;
    private String mOtherName;
    private String mMeUser;
    private List<ChatMessage> mMsgList;
    private ChatAdapter mAdapter;
    private Drawable mMeHeaderDrawable;
    private Drawable mOtherHeaderDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            mOtherUser = getIntent().getExtras().getString(EXTRA_USER);
            mOtherName = getIntent().getExtras().getString(EXTRA_NAME);
        }
        mMeUser = SryxApp.sWxUser.getOpenid() + "@" + XmppConnSingleton.getInstance().getServiceName();
        OTHER_USER_ID = mOtherUser;
        mAsmackModel = new AsmackModelImpl(this);

        // 改变输入框
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        initData();
        initRxbus();
        initDrawable();
    }

    private void initData() {
        mMsgList = new ArrayList<>();
        QueryBuilder<ChatMessage> qb = SryxApp.sDaoSession.getChatMessageDao().queryBuilder();
        qb.or(qb.and(Properties.From.eq(mOtherUser), Properties.To.eq(mMeUser)),
                qb.and(Properties.From.eq(mMeUser), Properties.To.eq(mOtherUser)));
        mMsgList.addAll(qb.list());
    }

    private void initDrawable() {
        mAsmackModel.getHeaderPic(mMeUser, new SubscriberOnNextListener<Drawable>() {
            @Override
            public void onSuccess(Drawable drawable) {
                mMeHeaderDrawable = drawable;
                mAsmackModel.getHeaderPic(mOtherUser, new SubscriberOnNextListener<Drawable>() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        mOtherHeaderDrawable = drawable;
                        initLayout();
                    }

                    @Override
                    public void onError(String msg) {
                    }
                });
            }

            @Override
            public void onError(String msg) {
            }
        });

    }

    private void initLayout() {
        toolbar.setTitleText(mOtherName);

        mAdapter = new ChatAdapter(this, mMsgList, mMeHeaderDrawable, mOtherHeaderDrawable);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) rvChat.getItemAnimator()).setSupportsChangeAnimations(false);
        rvChat.setAdapter(mAdapter);

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAsmackModel.sendMessage(mOtherUser, mOtherName, edtMsgContent.getText().toString(), null, new SubscriberOnNextListener<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setFrom(mMeUser);
                        chatMessage.setTo(mOtherUser);
                        chatMessage.setBody(edtMsgContent.getText().toString());
                        chatMessage.setTime(System.currentTimeMillis());
                        chatMessage.setIsSendOk(true);
                        chatMessage.setIsRead(true);
                        mMsgList.add(chatMessage);
                    }

                    @Override
                    public void onError(String msg) {
                        ToastUtils.showLongToast(ChatActivity.this, "发送消息失败！");
                    }
                });
                mAdapter.notifyItemInserted(mAdapter.getItemCount());
                rvChat.smoothScrollToPosition(mAdapter.getItemCount());
                edtMsgContent.setText(null);
            }
        });
    }

    private void initRxbus() {
        Observable<ChatMessage> observable = RxBus.getInstance().register(this);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ChatMessage>() {
            @Override
            public void call(ChatMessage s) {
                mMsgList.add(s);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

//    @OnClick(R.id.btn_send_msg)
//    public void onClick() {
//        mAsmackModel.sendMessage(mOtherUser, mOtherName, edtMsgContent.getText().toString(), null, new SubscriberOnNextListener<Integer>() {
//            @Override
//            public void onSuccess(Integer integer) {
//                ChatMessage chatMessage = new ChatMessage();
//                chatMessage.setFrom(mMeUser);
//                chatMessage.setTo(mOtherUser);
//                chatMessage.setBody(edtMsgContent.getText().toString());
//                chatMessage.setTime(System.currentTimeMillis());
//                chatMessage.setIsSendOk(true);
//                chatMessage.setIsRead(true);
////                mMsgList.add(chatMessage);
////                mAdapter.notifyItemInserted(mAdapter.getItemCount());
////                rvChat.smoothScrollToPosition(mAdapter.getItemCount() - 1);
////                showSoftInputFromWindow(ChatActivity.this, edtMsgContent);
//            }
//
//            @Override
//            public void onError(String msg) {
//                ToastUtils.showLongToast(ChatActivity.this, "发送消息失败！");
//            }
//        });
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unregister(this);
    }
}
