package com.yj.sryx;

import android.app.Application;
import android.content.Context;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yj.sryx.model.beans.WxUser;
import com.zhy.autolayout.config.AutoLayoutConifg;

/**
 * Created by eason.yang on 2017/3/3.
 */
public class SryxApp extends Application {
    public static Context sContext;
    public static IWXAPI sWxApi;
    public static WxUser sWxUser;
    public static        int                   currentThemeId   = 0;
    public static        int                   toolbarTextColor = 0;
    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        //初始化androidAutoLayout
        initAutoLayout();
        //初始化微信API
        initWxApi();
    }

    private void initAutoLayout() {
        AutoLayoutConifg.getInstance().useDeviceSize();
    }

    private void initWxApi() {
        sWxApi = WXAPIFactory.createWXAPI(this, SryxConfig.WX_APP_ID, true);
        sWxApi.registerApp(SryxConfig.WX_APP_ID);
    }

}
