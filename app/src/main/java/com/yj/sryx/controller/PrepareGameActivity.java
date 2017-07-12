package com.yj.sryx.controller;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yj.sryx.R;
import com.yj.sryx.common.RecycleViewDivider;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.model.beans.Game;
import com.yj.sryx.model.beans.Role;
import com.yj.sryx.utils.EncodingHandler;
import com.yj.sryx.utils.SizeUtils;
import com.yj.sryx.widget.AcceBar;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by eason.yang on 2017/7/11.
 */

public class PrepareGameActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    AcceBar toolbar;
    @Bind(R.id.rv_grid_roles)
    RecyclerView rvGridRoles;

    public final static String KEY_GAME_ID = "game_id";
    @Bind(R.id.tv_game_code)
    TextView tvGameCode;
    @Bind(R.id.iv_qrcode)
    ImageView ivQrcode;
    @Bind(R.id.btn_cancle_game)
    Button btnCancleGame;
    @Bind(R.id.btn_start_game)
    Button btnStartGame;
    private List<Role> mRoleList;
    private SryxModel mSryxModel;
    private CommonAdapter mAdapter;
    private String mGameCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_game);
        ButterKnife.bind(this);
        mSryxModel = new SryxModelImpl(this);
        mRoleList = new ArrayList<>();
        initLayout();
    }

    private void initLayout() {
        mGameCode = (String) getIntent().getExtras().get(KEY_GAME_ID);

        mSryxModel.getGameById(mGameCode, new SubscriberOnNextListener<Game>() {
            @Override
            public void onSuccess(Game s) {
                tvGameCode.setText(s.getInviteCode());
            }

            @Override
            public void onError(String msg) {
            }
        });

        rvGridRoles.addItemDecoration(new RecycleViewDivider(PrepareGameActivity.this, LinearLayoutManager.VERTICAL));
        rvGridRoles.addItemDecoration(new RecycleViewDivider(PrepareGameActivity.this, LinearLayoutManager.HORIZONTAL));
        mAdapter = new CommonAdapter<Role>(PrepareGameActivity.this, R.layout.item_grid_role, mRoleList) {
            @Override
            protected void convert(final ViewHolder holder, final Role role, int position) {
                if (role.getPlayerAvatarUrl() != null) {
                    Glide.with(PrepareGameActivity.this)
                            .load(role.getPlayerAvatarUrl())
                            .into((ImageView) holder.getView(R.id.iv_header));
                } else {
                    holder.setImageResource(R.id.iv_header, R.mipmap.header_pic);
                }
                if (role.getPlayerNickName() != null) {
                    holder.setText(R.id.tv_nickname, role.getPlayerNickName());
                } else {
                    holder.setText(R.id.tv_nickname, "未连接");
                }
            }
        };
        rvGridRoles.setAdapter(mAdapter);

        mSryxModel.getRolesInGame(mGameCode, new SubscriberOnNextListener<List<Role>>() {
            @Override
            public void onSuccess(List<Role> roles) {
                if (roles.size() > 9) {
                    ViewGroup.LayoutParams lp = rvGridRoles.getLayoutParams();
                    lp.height = SizeUtils.dp2px(PrepareGameActivity.this, 330);
                    rvGridRoles.setLayoutParams(lp);
                    rvGridRoles.requestLayout();
                }
                mRoleList.clear();
                mRoleList.addAll(roles);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String msg) {
            }
        });
    }

    @OnClick({R.id.iv_qrcode, R.id.btn_cancle_game, R.id.btn_start_game})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_qrcode:
                View layout = getLayoutInflater().inflate(R.layout.dialog_show_qr_code,(ViewGroup) findViewById(R.id.dialog));
                ImageView imgQrCode = (ImageView)layout.findViewById(R.id.img_qr_code);
                try {
                    Bitmap barBitmap = EncodingHandler.create2Code("game_code="+mGameCode, 800);
                    BitmapDrawable bd = new BitmapDrawable(barBitmap);
                    imgQrCode.setBackground(bd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(layout);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.btn_cancle_game:
                break;
            case R.id.btn_start_game:
                break;
        }
    }
}
