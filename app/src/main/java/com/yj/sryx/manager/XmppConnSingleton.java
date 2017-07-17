package com.yj.sryx.manager;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by eason.yang on 2017/7/17.
 */

public class XmppConnSingleton {
    private static XMPPConnection sXmppConnection;

    public static synchronized XMPPConnection getInstance(){
        if(null == sXmppConnection){
            try {
                throw new Exception("XMPPConnection未初始化！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sXmppConnection;
    }

    public static void setXMPPConnection(XMPPConnection connection){
        if(null == sXmppConnection) {
            sXmppConnection = connection;
        }else {
            try {
                throw new Exception("XMPPConnection仅允许初始化一次！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
