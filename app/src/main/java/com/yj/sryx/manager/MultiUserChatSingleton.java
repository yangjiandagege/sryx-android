package com.yj.sryx.manager;

import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * Created by eason.yang on 2017/7/22.
 */

public class MultiUserChatSingleton {
    private static MultiUserChat sMultiUserChat;

    public static synchronized MultiUserChat getInstance(String roomJid){
        if(null == sMultiUserChat){
            sMultiUserChat = new MultiUserChat(XmppConnSingleton.getInstance(), roomJid);
        }
        return sMultiUserChat;
    }
}
