package com.yj.sryx.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.model.beans.Role;
import com.yj.sryx.widget.CircleImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyRoleActivity extends BaseActivity {
    @Bind(R.id.iv_role)
    ImageView ivRole;
    private SryxModel mSryxModel;
    public static final String GAME_CODE = "game_code";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_role);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        String gameCode = getIntent().getExtras().getString(GAME_CODE);
        ButterKnife.bind(this);
        mSryxModel = new SryxModelImpl(this);
        mSryxModel.getRoleByCode(gameCode, SryxApp.sWxUser.getOpenid(), new SubscriberOnNextListener<Role>() {
            @Override
            public void onSuccess(Role role) {
                switch (role.getRoleType()){
                    case 0:
                        Glide.with(MyRoleActivity.this)
                                .load("https://www.ywwxmm.cn/image/killer.jpg")
                                .into(ivRole);
                        break;
                    case 1:
                        Glide.with(MyRoleActivity.this)
                                .load("https://www.ywwxmm.cn/image/police.jpg")
                                .into(ivRole);
                        break;
                    case 2:
                        Glide.with(MyRoleActivity.this)
                                .load("https://www.ywwxmm.cn/image/citizen.jpg")
                                .into(ivRole);
                        break;
                }
            }

            @Override
            public void onError(String msg) {
            }
        });

    }
}
