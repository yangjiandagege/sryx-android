package com.yj.sryx.view.im;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.manager.RxBus;
import com.yj.sryx.manager.XmppConnSingleton;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.model.OpenfireLoginModel;
import com.yj.sryx.model.OpenfireLoginModelImpl;
import com.yj.sryx.model.beans.ChatMessage;
import com.yj.sryx.model.beans.ChatMessageDao;
import com.yj.sryx.model.beans.ChatSession;
import com.yj.sryx.model.beans.ChatSessionDao;
import com.yj.sryx.utils.ActivityUtils;
import com.yj.sryx.utils.CountDownTimerUtil;
import com.yj.sryx.utils.LogUtils;
import com.yj.sryx.view.game.MainActivity;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class ImService extends Service {
    private static final int NOTIFICATION_ID_1 = 1;
    private static final int NOTIFICATION_ID_2 = 2;
    private Context mContext;
    private XMPPConnection mConnection;
    private Roster mRoster;
    private NotificationManager mNotiManager = null;
    private ChatMessageDao mChatMessageDao;
    private ChatSessionDao mChatSessionDao;
    private OpenfireLoginModel mOpenfireLoginModel;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.logout("onStartCommand() executed");
        mNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mChatMessageDao = SryxApp.sDaoSession.getChatMessageDao();
        mChatSessionDao = SryxApp.sDaoSession.getChatSessionDao();

        mOpenfireLoginModel = new OpenfireLoginModelImpl(this);
        if(SryxApp.isOpenfireRegisterNeed) {
            mOpenfireLoginModel.connectThenRegisterThenLogin(SryxApp.sWxUser.getOpenid(), SryxApp.sWxUser.getOpenid(), SryxApp.sWxUser.getNickname(), mLoginFinishListener);
        }else {
            mOpenfireLoginModel.connectThenLogin(SryxApp.sWxUser.getOpenid(), SryxApp.sWxUser.getOpenid(), SryxApp.sWxUser.getNickname(),mLoginFinishListener);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private SubscriberOnNextListener mLoginFinishListener = new SubscriberOnNextListener() {
        @Override
        public void onSuccess(Object o) {
            mConnection = XmppConnSingleton.getInstance();
            mRoster = mConnection.getRoster();
            mRoster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            addPacketListener();
            addMessageListener();
            initHeartBeat();
            initOfflineMessage();
        }

        @Override
        public void onError(String msg) {
            LogUtils.logout(msg);
        }
    };



    private void initOfflineMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OfflineMessageManager offlineManager = new OfflineMessageManager(mConnection);
                    List<Message> offlineMessageList = offlineManager.getMessages();
                    for(int i = 0; i < offlineMessageList.size(); i++){
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setBody(offlineMessageList.get(i).getBody());
                        chatMessage.setFrom(offlineMessageList.get(i).getFrom().split("/")[0]);
                        chatMessage.setTo(offlineMessageList.get(i).getTo());
                        chatMessage.setTime(System.currentTimeMillis());
                        chatMessage.setIsSendOk(true);
                        chatMessage.setIsRead(false);
                        mChatMessageDao.insert(chatMessage);
                    }
                    offlineManager.deleteMessages();
                    mConnection.sendPacket(new Presence(Presence.Type.available));
                } catch (XMPPException | SmackException.NotConnectedException | SmackException.NoResponseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addPacketListener() {
        if(mConnection!=null&&mConnection.isConnected()&&mConnection.isAuthenticated()) {
            PacketFilter filter = new AndFilter(new PacketTypeFilter(Presence.class));
            PacketListener listener = new PacketListener() {
                @Override
                public void processPacket(Packet packet) throws SmackException.NotConnectedException {
                    if (packet instanceof Presence) {
                        Presence presence = (Presence)packet;
                        String from = presence.getFrom();//发送方
                        String to = presence.getTo();//接收方
                        if(from.contains(SryxApp.sWxUser.getOpenid())){
                            return;
                        }
                        if (presence.getType().equals(Presence.Type.subscribe)) {
                            LogUtils.logout("收到添加请求！");
//                            sendAddFriendApplyNotification();
                        } else if (presence.getType().equals(Presence.Type.subscribed)) {
                            LogUtils.logout("恭喜，对方同意添加好友！");
                        } else if (presence.getType().equals(Presence.Type.unsubscribe)) {
                            LogUtils.logout("对方将你从好友列表移除！");
                        } else if (presence.getType().equals(Presence.Type.unsubscribed)){
                            LogUtils.logout("抱歉，对方拒绝添加好友，将你从好友列表移除！");
                        } else if (presence.getType().equals(Presence.Type.unavailable)) {
                            LogUtils.logout("好友下线！");
                        } else {
                            LogUtils.logout("好友上线！");
                        }
                    }
                }
            };
            //添加监听
            mConnection.addPacketListener(listener, filter);
        }
    }

    private void addMessageListener() {
        ChatManager manager = ChatManager.getInstanceFor(XmppConnSingleton.getInstance());
        manager.addChatListener(new ChatManagerListener() {
            public void chatCreated(final Chat chat, boolean arg1) {
                chat.addMessageListener(new MessageListener() {
                    public void processMessage(Chat arg0, Message message) {
                        //若是聊天窗口已存在，将消息转往目前窗口
                        //若是窗口不存在，开新的窗口并注册
                        LogUtils.logout("From   : "+message.getFrom());
                        LogUtils.logout("To     : "+message.getTo());
                        LogUtils.logout("Type   : "+message.getType());
                        LogUtils.logout("body   : "+message.getBody());
                        String fromUser = message.getFrom().split("/")[0];

                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setBody(message.getBody());
                        chatMessage.setFrom(fromUser);
                        chatMessage.setTo(message.getTo());
                        chatMessage.setTime(System.currentTimeMillis());
                        chatMessage.setIsSendOk(true);
                        chatMessage.setIsRead(false);
                        mChatMessageDao.insert(chatMessage);
                        VCard vCard = new VCard();
                        try {
                            vCard.load(mConnection, fromUser);
                        } catch (SmackException.NoResponseException | SmackException.NotConnectedException | XMPPException.XMPPErrorException e) {
                            e.printStackTrace();
                        }
                        ChatSession chatSession = mChatSessionDao.load(fromUser);
                        if(null == chatSession){
                            chatSession = new ChatSession();
                            chatSession.setSessionId(fromUser);
                            chatSession.setSessionName(vCard.getNickName());
                            chatSession.setLastTime(System.currentTimeMillis());
                            chatSession.setLastBody(message.getBody());
                            chatSession.setUnreadCount(1);
                            mChatSessionDao.insert(chatSession);
                        }else {
                            chatSession.setLastTime(System.currentTimeMillis());
                            chatSession.setLastBody(message.getBody());
                            chatSession.setUnreadCount(chatSession.getUnreadCount()+1);
                            mChatSessionDao.update(chatSession);
                        }

                        if(ActivityUtils.isTopActivity(mContext, ChatActivity.class.getName()) && message.getFrom().contains(ChatActivity.OTHER_USER_ID)) {
                            RxBus.getInstance().post(chatMessage);
                        }else {
                            sendTopMsgNotification(fromUser, vCard.getNickName());
                        }
                    }
                });
            }
        });
    }


    private void sendAddFriendApplyNotification() {
        PendingIntent pi = PendingIntent.getActivity(
                mContext,
                100,
                new Intent(mContext, MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentTitle("天黑请闭眼")
                .setContentText("您收到了联系人申请")
                .setTicker("您收到新的消息")
                .setSmallIcon(R.mipmap.sryx_logo)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)//点击后取消
                .setWhen(System.currentTimeMillis())//设置通知时间
                .setPriority(Notification.PRIORITY_HIGH)//高优先级
                .setContentIntent(pi);
        mNotiManager.notify(NOTIFICATION_ID_1, builder.build());
    }

    private void sendTopMsgNotification(String user, String name) {
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_NAME, name);
        intent.putExtra(ChatActivity.EXTRA_USER, user);
        PendingIntent pi = PendingIntent.getActivity(
                mContext,
                100,
                new Intent(mContext, ChatActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentTitle("天黑请闭眼")
                .setContentText("您收到了新消息")
                .setTicker("您收到新的消息")
                .setSmallIcon(R.mipmap.sryx_logo)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)//点击后取消
                .setWhen(System.currentTimeMillis())//设置通知时间
                .setPriority(Notification.PRIORITY_HIGH)//高优先级
                .setContentIntent(pi);
        mNotiManager.notify(NOTIFICATION_ID_2, builder.build());
    }

    private void initHeartBeat() {
        mHandler.postDelayed(mTask,3*000);
    }

    private Handler mHandler = new Handler();

    private Runnable mTask =new Runnable() {
        public void run() {
            mHandler.postDelayed(this,3*1000);//设置延迟时间，此处是5秒
            if(!mConnection.isConnected() || !mConnection.isAuthenticated()){
                mOpenfireLoginModel.connectThenLogin(SryxApp.sWxUser.getOpenid(), SryxApp.sWxUser.getOpenid(), SryxApp.sWxUser.getNickname(), new SubscriberOnNextListener<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        LogUtils.logout("asmack reconect ok!");
                    }

                    @Override
                    public void onError(String msg) {
                        LogUtils.logout("asmack reconect fail!");
                    }
                });
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.logout("onDestroy() executed");
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
