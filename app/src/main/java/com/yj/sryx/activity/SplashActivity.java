package com.yj.sryx.activity;

import android.app.Activity;
import android.content.Intent;
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

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.yj.sryx.SryxApp.sWxApi;

public class SplashActivity extends BaseActivity {
    @Bind(R.id.wxlogin_btn)
    Button mWxloginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        ButterKnife.bind(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
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
        LoginModelImpl model = new LoginModelImpl();
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
        startActivity(new Intent(this, MainActivity.class));
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
