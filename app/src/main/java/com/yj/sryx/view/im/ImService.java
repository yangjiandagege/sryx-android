package com.yj.sryx.view.im;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.manager.RxBus;
import com.yj.sryx.manager.XmppConnSingleton;
import com.yj.sryx.model.beans.ChatMessage;
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
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import cn.jpush.android.api.JPushInterface;

public class ImService extends Service {
    private static final int NOTIFICATION_ID_1 = 1;
    private Context mContext;
    private XMPPConnection mConnection;
    private Roster mRoster;
    private NotificationManager myManager = null;
    private Notification myNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mConnection = XmppConnSingleton.getInstance();
        mRoster = mConnection.getRoster();
        mRoster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        myManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        addPacketListener();
        addMessageListener();
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
                            sendAddFriendApplyNotification();
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
                        LogUtils.logout("Sub    : "+message.toXML());
                        LogUtils.logout("body   : "+message.getBody());
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setBody(message.getBody());
                        chatMessage.setFrom(message.getFrom());
                        chatMessage.setTo(message.getTo());
                        RxBus.getInstance().post(chatMessage);
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

        Notification.Builder myBuilder = new Notification.Builder(mContext);
        myBuilder.setContentTitle("天黑请闭眼")
                .setContentText("您收到了联系人申请")
                .setTicker("您收到新的消息")
                .setSmallIcon(R.mipmap.sryx_logo)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)//点击后取消
                .setWhen(System.currentTimeMillis())//设置通知时间
                .setPriority(Notification.PRIORITY_HIGH)//高优先级
                //android5.0加入了一种新的模式Notification的显示等级，共有三种：
                //VISIBILITY_PUBLIC  只有在没有锁屏时会显示通知
                //VISIBILITY_PRIVATE 任何情况都会显示通知
                //VISIBILITY_SECRET  在安全锁和没有锁屏的情况下显示通知
                .setContentIntent(pi);  //3.关联PendingIntent
        myNotification = myBuilder.build();
        myManager.notify(NOTIFICATION_ID_1, myNotification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.logout("onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

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
