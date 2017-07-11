package com.yj.sryx.manager;

import android.content.Context;

import com.yj.sryx.model.beans.WxUser;
import com.yj.sryx.utils.FileUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by eason.yang on 2017/7/10.
 */

public class LocalUserManager {
    private static final String PATH_USER_SERIALIZE = "/user.ser";

    public static boolean isLocalUserExist(Context context){
        return  FileUtils.isFileExists(context.getFilesDir() + PATH_USER_SERIALIZE);
    }

    public static void removeLocalUser(Context context){
        FileUtils.deleteFile(context.getFilesDir() + PATH_USER_SERIALIZE);
    }

    public static WxUser unSerializeUser(Context context) {
        WxUser user =null;
        try {
            ObjectInputStream in;
            in = new ObjectInputStream(new FileInputStream(context.getFilesDir() + PATH_USER_SERIALIZE));
            user = (WxUser) in.readObject();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void serializeUser(Context context, WxUser user) {
        try {
            ObjectOutputStream out;
            out = new ObjectOutputStream(new FileOutputStream(context.getFilesDir()+PATH_USER_SERIALIZE));
            out.writeObject(user);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
