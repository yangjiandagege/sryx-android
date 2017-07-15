package com.yj.sryx;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yj.sryx.common.Category;
import com.yj.sryx.common.Theme;
import com.yj.sryx.model.beans.WxUser;
import com.yj.sryx.view.MainActivity;
import com.zhy.autolayout.config.AutoLayoutConifg;

import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by eason.yang on 2017/3/3.
 */
public class SryxApp extends Application {
    public static Context sContext;
    public static IWXAPI sWxApi;
    public static WxUser sWxUser;
    public static HashMap<Object, Integer> sActivityThemeMap;

    public static        int                   currentThemeId   = 0;
    public static        int                   toolbarTextColor = 0;
    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        //初始化极光推送
        initJpush();
        //初始化androidAutoLayout
        initAutoLayout();
        //初始化微信API
        initWxApi();
        //初始化主题map
        initThemeMap();
    }

    private void initThemeMap() {
        sActivityThemeMap = new HashMap<>();
        sActivityThemeMap.put(MainActivity.class, R.style.AppTheme_Yellow);
    }

    private void initJpush() {
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }

    private void initAutoLayout() {
        AutoLayoutConifg.getInstance().useDeviceSize();
    }

    private void initWxApi() {
        sWxApi = WXAPIFactory.createWXAPI(this, SryxConfig.WX_APP_ID, true);
        sWxApi.registerApp(SryxConfig.WX_APP_ID);
    }

}
