package com.yj.sryx.view.game;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.model.beans.Role;
import com.yj.sryx.utils.LogUtils;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.view.im.GroupChatActivity;
import com.yj.sryx.widget.AcceBar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyRoleActivity extends BaseActivity {
    @Bind(R.id.iv_role)
    ImageView ivRole;
    @Bind(R.id.tv_role_name)
    TextView tvRoleName;
    @Bind(R.id.iv_press_back)
    ImageView ivPressBack;
    @Bind(R.id.toolbar)
    AcceBar toolbar;
    private SryxModel mSryxModel;
    public static final String GAME_CODE = "game_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_role);
        String gameCode = getIntent().getExtras().getString(GAME_CODE);
        ButterKnife.bind(this);
        mSryxModel = new SryxModelImpl(this);
        LogUtils.logout(SryxApp.sWxUser.getOpenid());
        mSryxModel.getRoleByCode(gameCode, SryxApp.sWxUser.getOpenid(), new SubscriberOnNextListener<Role>() {
            @Override
            public void onSuccess(final Role role) {
                switch (role.getRoleType()) {
                    case 0:
                        Glide.with(MyRoleActivity.this)
                                .load("https://www.ywwxmm.cn/image/killer.jpg")
                                .into(ivRole);
                        tvRoleName.setText("杀手");
                        break;
                    case 1:
                        Glide.with(MyRoleActivity.this)
                                .load("https://www.ywwxmm.cn/image/police.jpg")
                                .into(ivRole);
                        tvRoleName.setText("警察");
                        break;
                    case 2:
                        Glide.with(MyRoleActivity.this)
                                .load("https://www.ywwxmm.cn/image/citizen.jpg")
                                .into(ivRole);
                        tvRoleName.setText("平民");
                        break;
                }

                toolbar.setManagement("游戏聊天室", new AcceBar.OnManageListener() {
                    @Override
                    public void OnManageClick() {
                        Intent intent = new Intent(MyRoleActivity.this, GroupChatActivity.class);
                        intent.putExtra(GroupChatActivity.EXTRA_ROOM_JID, role.getGameId()+"@conference.39.108.82.35");
                        intent.putExtra(GroupChatActivity.EXTRA_ROOM_NAME, ""+role.getGameId());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onError(String msg) {
            }
        });
        initImmersive();

    }

    private void initImmersive(){
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        toolbar.setBackgroundColor(Color.TRANSPARENT);
    }


//    真正的沉浸式模式
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }
}
