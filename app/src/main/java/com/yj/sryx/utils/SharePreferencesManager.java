package com.yj.sryx.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Hanker on 2015/08/25.
 */
public class SharePreferencesManager {
    private Context m_context = null;

    public SharePreferencesManager(Context context) {
        this.m_context = context;
    }

    public void writeBooleanSharedPreferences(String key, Boolean value) {
        SharedPreferences sp = m_context.getSharedPreferences("ACCE_SHARED_PREfERENCES",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean readBooleanSharedPreferences(String key) {
        SharedPreferences sp = m_context.getSharedPreferences("ACCE_SHARED_PREfERENCES",
                Activity.MODE_PRIVATE);
        Boolean value = sp.getBoolean(key, false);
        return value;
    }

    public void writeStringSharedPreferences(String key, String value) {
        SharedPreferences sp = m_context.getSharedPreferences("ACCE_SHARED_PREfERENCES",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String readStringSharedPreferences(String key) {
        SharedPreferences sp = m_context.getSharedPreferences("ACCE_SHARED_PREfERENCES",
                Activity.MODE_PRIVATE);
        String value = sp.getString(key, "");
        return value;
    }

    public void clear() {
        SharedPreferences sp = m_context.getSharedPreferences("ACCE_SHARED_PREfERENCES", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
}
