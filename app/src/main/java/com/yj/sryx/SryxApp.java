package com.yj.sryx;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yj.sryx.common.Category;
import com.yj.sryx.common.Theme;
import com.yj.sryx.model.beans.WxUser;
import com.yj.sryx.utils.LogUtils;
import com.yj.sryx.view.CreateGameFragment;
import com.yj.sryx.view.GameDetailActivity;
import com.yj.sryx.view.GameManageActivity;
import com.yj.sryx.view.GameRecordsFragment;
import com.yj.sryx.view.JoinGameFragment;
import com.yj.sryx.view.MainActivity;
import com.yj.sryx.view.MyRoleActivity;
import com.yj.sryx.view.PrepareGameActivity;
import com.yj.sryx.view.QrCodeScanActivity;
import com.yj.sryx.view.RuleActivity;
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
    public static HashMap<String, Theme> sActivityThemeMap;

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
        sActivityThemeMap.put(MainActivity.class.getSimpleName(), Theme.yellow);
        sActivityThemeMap.put(RuleActivity.class.getSimpleName(), Theme.green);
        sActivityThemeMap.put(QrCodeScanActivity.class.getSimpleName(), Theme.green);

        sActivityThemeMap.put(CreateGameFragment.class.getSimpleName(), Theme.green);
        sActivityThemeMap.put(PrepareGameActivity.class.getSimpleName(), Theme.green);
        sActivityThemeMap.put(GameManageActivity.class.getSimpleName(), Theme.green);

        sActivityThemeMap.put(JoinGameFragment.class.getSimpleName(), Theme.blue);
        sActivityThemeMap.put(MyRoleActivity.class.getSimpleName(), Theme.blue);

        sActivityThemeMap.put(GameRecordsFragment.class.getSimpleName(), Theme.spe);
        sActivityThemeMap.put(GameDetailActivity.class.getSimpleName(), Theme.spe);
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
