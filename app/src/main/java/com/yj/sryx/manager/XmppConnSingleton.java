package com.yj.sryx.manager;

import com.yj.sryx.utils.LogUtils;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by eason.yang on 2017/7/17.
 */

public class XmppConnSingleton {
    private static XMPPConnection sXmppConnection;

    public static synchronized XMPPConnection getInstance(){
        if(null == sXmppConnection){
            LogUtils.logout("XMPPConnection未初始化！");
        }
        return sXmppConnection;
    }

    public static void setXMPPConnection(XMPPConnection connection){
        sXmppConnection = connection;
    }
}
