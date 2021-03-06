package com.yj.sryx.view.game;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.manager.XmppConnSingleton;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.model.beans.ChatMessage;
import com.yj.sryx.model.beans.Role;
import com.yj.sryx.utils.LogUtils;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.widget.AcceBar;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyRoleActivity extends BaseActivity {
    @Bind(R.id.iv_role)
    ImageView ivRole;
    @Bind(R.id.tv_role_name)
    TextView tvRoleName;
    @Bind(R.id.iv_press_back)
    ImageView ivPressBack;
    @Bind(R.id.toolbar)
    AcceBar toolbar;
    @Bind(R.id.rv_chat)
    RecyclerView rvChat;
    @Bind(R.id.edt_msg_content)
    EditText edtMsgContent;
    @Bind(R.id.btn_send_msg)
    Button btnSendMsg;
    private SryxModel mSryxModel;
    private AsmackModel mAsmackModel;
    private String mMeUser;
    private List<ChatMessage> mMsgList;
    private GameChatAdapter mAdapter;

    public static final String GAME_CODE = "game_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_role);
        String gameCode = getIntent().getExtras().getString(GAME_CODE);
        ButterKnife.bind(this);
        mMeUser = SryxApp.sWxUser.getOpenid() + "@" + XmppConnSingleton.getInstance().getServiceName();
        mAsmackModel = new AsmackModelImpl(this);
        mSryxModel = new SryxModelImpl(this);
        mMsgList = new ArrayList<>();
        LogUtils.logout(SryxApp.sWxUser.getOpenid());
        mSryxModel.getRoleByCode(gameCode, SryxApp.sWxUser.getOpenid(), new SubscriberOnNextListener<Role>() {
            @Override
            public void onSuccess(final Role role) {
                switch (role.getRoleType()) {
                    case 0:
                        Glide.with(MyRoleActivity.this)
                                .load("https://www.ywwxmm.cn/image/killer.jpg")
                                .into(ivRole);
                        tvRoleName.setText("杀手");
                        break;
                    case 1:
                        Glide.with(MyRoleActivity.this)
                                .load("https://www.ywwxmm.cn/image/police.jpg")
                                .into(ivRole);
                        tvRoleName.setText("警察");
                        break;
                    case 2:
                        Glide.with(MyRoleActivity.this)
                                .load("https://www.ywwxmm.cn/image/citizen.jpg")
                                .into(ivRole);
                        tvRoleName.setText("平民");
                        break;
                }
                initListener(role.getGameId() + "@conference.39.108.82.35");
            }

            @Override
            public void onError(String msg) {
            }
        });
        initLayout();

    }


    private void initLayout() {
        initImmersive();

        mAdapter = new GameChatAdapter(this, mMsgList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) rvChat.getItemAnimator()).setSupportsChangeAnimations(false);
        rvChat.setAdapter(mAdapter);
    }

    private void initListener(final String roomJid) {
        mAsmackModel.joinGroup(mMeUser, roomJid, "wuhan123", null, new PacketListener() {
            @Override
            public void processPacket(Packet packet) throws SmackException.NotConnectedException {
                Message message = (Message) packet;
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setFrom(message.getBody().split("@&")[0]);
                chatMessage.setBody(message.getBody().split("@&")[1]);
                chatMessage.setTo(message.getTo());
                chatMessage.setTime(System.currentTimeMillis());
                LogUtils.logout(chatMessage.getFrom() + " " + chatMessage.getTo() + " : " + chatMessage.getBody());
                mMsgList.add(chatMessage);
                MyRoleActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = edtMsgContent.getText().toString();
                mAsmackModel.sendGroupMessage(mMeUser, roomJid, content, new SubscriberOnNextListener<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        LogUtils.logout("");
                    }

                    @Override
                    public void onError(String msg) {
                        LogUtils.logout("");
                    }
                });
                LogUtils.logout("");
                mAdapter.notifyItemInserted(mAdapter.getItemCount());
                rvChat.smoothScrollToPosition(mAdapter.getItemCount());
                edtMsgContent.setText(null);
            }
        });
    }

    private void initImmersive() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        toolbar.setBackgroundColor(Color.TRANSPARENT);
    }


//    真正的沉浸式模式
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }
}
