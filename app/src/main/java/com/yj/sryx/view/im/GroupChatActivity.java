package com.yj.sryx.view.im;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Menu;
import android.view.MenuItem;
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
import com.yj.sryx.model.beans.ChatMessageDao;
import com.yj.sryx.model.beans.ChatMessageDao.Properties;
import com.yj.sryx.model.beans.ChatSession;
import com.yj.sryx.model.beans.ChatSessionDao;
import com.yj.sryx.utils.LogUtils;
import com.yj.sryx.widget.AcceBar;

import org.greenrobot.greendao.query.QueryBuilder;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class GroupChatActivity extends AppCompatActivity {
    public static final String EXTRA_ROOM_JID = "room_jid";
    public static final String EXTRA_ROOM_NAME = "room_name";
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
    private GroupChatAdapter mAdapter;
    private Drawable mMeHeaderDrawable;
    private Drawable mOtherHeaderDrawable;
    private String mRoomJid;
    private String mRoomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        mMeUser = SryxApp.sWxUser.getOpenid() + "@" + XmppConnSingleton.getInstance().getServiceName();
        mAsmackModel = new AsmackModelImpl(this);
        mMsgList = new ArrayList<>();
        mRoomJid = getIntent().getExtras().getString(EXTRA_ROOM_JID);
        mRoomName = getIntent().getExtras().getString(EXTRA_ROOM_NAME);
        toolbar.setTitleText(mRoomName);
        initLayout();
        initListener();
    }

    private void initLayout() {
        mAdapter = new GroupChatAdapter(this, mMsgList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) rvChat.getItemAnimator()).setSupportsChangeAnimations(false);
        rvChat.setAdapter(mAdapter);
    }

    private void initListener() {
        mAsmackModel.joinGroup(mMeUser, mRoomJid, "wuhan123", null, new PacketListener() {
            @Override
            public void processPacket(Packet packet) throws SmackException.NotConnectedException {
                Message message = (Message) packet;
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setFrom(message.getBody().split("@&")[0]);
                chatMessage.setBody(message.getBody().split("@&")[1]);
                chatMessage.setTo(message.getTo());
                chatMessage.setTime(System.currentTimeMillis());
                LogUtils.logout(chatMessage.getFrom() +" " + chatMessage.getTo() + " : " + chatMessage.getBody());
                mMsgList.add(chatMessage);
                GroupChatActivity.this.runOnUiThread(new Runnable() {
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
                mAsmackModel.sendGroupMessage(mMeUser, mRoomJid, content, new SubscriberOnNextListener<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                    }

                    @Override
                    public void onError(String msg) {
                    }
                });
                mAdapter.notifyItemInserted(mAdapter.getItemCount());
                rvChat.smoothScrollToPosition(mAdapter.getItemCount());
                edtMsgContent.setText(null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_group_members:
                Intent intent = new Intent(GroupChatActivity.this, GroupInfoActivity.class);
                intent.putExtra(GroupInfoActivity.EXTRA_ROOM_JID, mRoomJid);
                intent.putExtra(GroupInfoActivity.EXTRA_ROOM_NAME, mRoomName);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unregister(this);
    }
}
