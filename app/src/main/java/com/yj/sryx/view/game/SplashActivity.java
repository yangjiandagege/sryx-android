package com.yj.sryx.view.game;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.manager.LocalUserManager;
import com.yj.sryx.model.LoginModelImpl;
import com.yj.sryx.view.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.yj.sryx.SryxApp.sWxApi;

public class SplashActivity extends BaseActivity {
    @Bind(R.id.wxlogin_btn)
    Button mWxloginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        init();
                    }
                }, 1000);
            }
        });
    }

    private void init() {
        LoginModelImpl model = new LoginModelImpl(this);
        if (model.isAlreadyLogin()) {
            SryxApp.sWxUser = LocalUserManager.unSerializeUser(this);
            jumpMainView();
        } else {
            showLoginView();
            mWxloginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginToWeiXin();
                }
            });
        }
    }

    private void showLoginView() {
        mWxloginBtn.setVisibility(View.VISIBLE);
    }

    private void jumpMainView() {
        Intent gotoMain = new Intent(this, MainActivity.class);
        gotoMain.putExtra(MainActivity.IS_ALREADY_LOGIN, false);
        startActivity(gotoMain);
        finish();
    }

    private void loginToWeiXin(){
        if (sWxApi != null && sWxApi.isWXAppInstalled()) {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo_test_neng";
            sWxApi.sendReq(req);
        } else
            Toast.makeText(this, "用户未安装微信", Toast.LENGTH_SHORT).show();
    }
}
