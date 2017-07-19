package com.yj.sryx.view.im;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.manager.RxBus;
import com.yj.sryx.manager.XmppConnSingleton;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.model.beans.ChatMessage;
import com.yj.sryx.model.beans.Role;
import com.yj.sryx.utils.LogUtils;
import com.yj.sryx.utils.ToastUtils;
import com.yj.sryx.view.game.PrepareGameActivity;
import com.yj.sryx.widget.AcceBar;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ChatActivity extends AppCompatActivity {
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
        mAsmackModel = new AsmackModelImpl(this);
        mMsgList = new ArrayList<>();

        initRxbus();
        initDrawable();
    }

    private void initDrawable() {
        mAsmackModel.getHeaderPic(SryxApp.sWxUser.getOpenid() + "@" + XmppConnSingleton.getInstance().getServiceName(), new SubscriberOnNextListener<Drawable>() {
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
        btnSendMsg.setBackgroundColor(ContextCompat.getColor(this, R.color.theme_spe_primary));

        mAdapter = new ChatAdapter(this, mMsgList, mMeHeaderDrawable, mOtherHeaderDrawable);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(mAdapter);
    }

    private void initRxbus() {
        Observable<ChatMessage> observable = RxBus.getInstance().register(this);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ChatMessage>() {
            @Override
            public void call(ChatMessage s) {
                LogUtils.logout(s.getBody());
                mMsgList.add(s);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick(R.id.btn_send_msg)
    public void onClick() {
        mAsmackModel.sendMessage(mOtherUser, mOtherName, edtMsgContent.getText().toString(), null, new SubscriberOnNextListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                ToastUtils.showLongToast(ChatActivity.this, "发送消息成功！");
            }

            @Override
            public void onError(String msg) {
                ToastUtils.showLongToast(ChatActivity.this, "发送消息失败！");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unregister(this);
    }
}
