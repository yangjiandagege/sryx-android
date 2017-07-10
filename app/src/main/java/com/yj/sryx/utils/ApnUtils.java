package com.yj.sryx.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangjian on 2017/2/18.
 */

public class ApnUtils {
    private Context mContext;
    public static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
    public static final Uri APN_LIST_URI = Uri.parse("content://telephony/carriers");

    public ApnUtils(Context context) {
        mContext = context;
    }

    public int getApnIdByApn(String apn){
        int apnId = -1;
        List<ApnNode> apnNodeList = getAPNList();
        for (ApnNode node:apnNodeList){
            if(apn.equals(node.getApn())){
                apnId = Integer.valueOf(node.get_id());
                break;
            }
        }
        return apnId;
    }

    /**
     * 获取apn列表
     * @return
     */
    private List<ApnNode> getAPNList(){
        //current不为空表示可以使用的APN
        String  projection[] = {"_id,apn,type,current"};
        Cursor cr = mContext.getContentResolver().query(APN_LIST_URI, projection, null, null, null);

        List<ApnNode> list = new ArrayList<>();

        while(cr!=null && cr.moveToNext()){
            ApnNode a = new ApnNode();
            a.set_id(cr.getString(cr.getColumnIndex("_id")));
            a.setApn(cr.getString(cr.getColumnIndex("apn")));
            a.setAuthtype(cr.getString(cr.getColumnIndex("type")));
            list.add(a);
        }
        if(cr!=null)
            cr.close();
        return list;
    }

    /**
     * 根据apnId将设置的APN选中
     * @param apnId
     * @return
     */
    public boolean setDefaultApn(int apnId) {
        boolean res = false;
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("apn_id", apnId);

        try {
            resolver.update(PREFERRED_APN_URI, values, null, null);
            Cursor c = resolver.query(PREFERRED_APN_URI, new String[] { "name",
                    "apn" }, "_id=" + apnId, null, null);
            if (c != null) {
                res = true;
                c.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }
}
