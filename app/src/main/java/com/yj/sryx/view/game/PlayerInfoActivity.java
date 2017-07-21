package com.yj.sryx.view.game;

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
import com.yj.sryx.model.beans.Player;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.widget.CircleImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlayerInfoActivity extends BaseActivity {
    public static final String EXTRAS_PLAYER_ID = "player_id";
    @Bind(R.id.iv_press_back)
    ImageView ivPressBack;
    @Bind(R.id.acce_toolbar)
    Toolbar acceToolbar;
    @Bind(R.id.iv_up_bg)
    ImageView ivUpBg;
    @Bind(R.id.iv_header)
    CircleImageView ivHeader;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_info)
    TextView tvInfo;

    private String mPlayerId;
    private SryxModel mSryxModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);
        setSupportActionBar(acceToolbar);
        ButterKnife.bind(this);
        if(null != getIntent().getExtras()){
            mPlayerId = getIntent().getExtras().getString(EXTRAS_PLAYER_ID);
            mSryxModel = new SryxModelImpl(this);
            mSryxModel.getPlayerById(mPlayerId, new SubscriberOnNextListener<Player>() {
                @Override
                public void onSuccess(Player player) {
                    Glide.with(PlayerInfoActivity.this)
                            .load(player.getAvatarUrl())
                            .into(ivHeader);
                    tvName.setText(player.getNickName());
                    tvInfo.setText(player.getGender()==0?"女":"男"+" "+player.getProvince()+" "+player.getCity());
                    initImmersive();
                }

                @Override
                public void onError(String msg) {

                }
            });
        }
    }

    private void initImmersive() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        acceToolbar.setBackgroundColor(Color.TRANSPARENT);
    }

}
