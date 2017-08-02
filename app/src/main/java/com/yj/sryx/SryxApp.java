package com.yj.sryx;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.leakcanary.LeakCanary;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yj.sryx.common.Theme;
import com.yj.sryx.model.beans.DaoMaster;
import com.yj.sryx.model.beans.DaoSession;
import com.yj.sryx.model.beans.WxUser;
import com.yj.sryx.view.game.CreateGameFragment;
import com.yj.sryx.view.game.GameChatActivity;
import com.yj.sryx.view.game.GameDetailActivity;
import com.yj.sryx.view.game.GameManageActivity;
import com.yj.sryx.view.game.GameRecordsFragment;
import com.yj.sryx.view.game.JoinGameFragment;
import com.yj.sryx.view.game.MainActivity;
import com.yj.sryx.view.game.MyRoleActivity;
import com.yj.sryx.view.game.PrepareGameActivity;
import com.yj.sryx.view.game.QrCodeScanActivity;
import com.yj.sryx.view.game.RuleActivity;
import com.yj.sryx.view.im.ImActivity;
import com.zhy.autolayout.config.AutoLayoutConifg;

import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by eason.yang on 2017/3/3.
 */
public class SryxApp extends Application {
    public static Context sContext;
    public static IWXAPI sWxApi;
    public static WxUser sWxUser;;
    public static DaoSession sDaoSession;
    public static HashMap<String, Theme> sActivityThemeMap;
    public static boolean isOpenfireRegisterNeed = false;

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
        //初始化greendao数据库
        setDatabase();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    private void setDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "sryx-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        sDaoSession = new DaoMaster(db).newSession();
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

        sActivityThemeMap.put(ImActivity.class.getSimpleName(), Theme.spe);
        sActivityThemeMap.put(GameChatActivity.class.getSimpleName(), Theme.blue);
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
