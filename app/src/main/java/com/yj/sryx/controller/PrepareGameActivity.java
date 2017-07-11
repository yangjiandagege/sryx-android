package com.yj.sryx.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.common.RecycleViewDivider;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.model.beans.Game;
import com.yj.sryx.model.beans.Role;
import com.yj.sryx.utils.SizeUtils;
import com.yj.sryx.widget.AcceBar;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

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
    private List<Role> mRoleList;
    private SryxModel mSryxModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_game);
        ButterKnife.bind(this);
        mSryxModel = new SryxModelImpl(this);
        initLayout();
    }

    private void initLayout() {
        String gameCode = (String) getIntent().getExtras().get(KEY_GAME_ID);

        mSryxModel.getGameById(gameCode, new SubscriberOnNextListener<Game>() {
            @Override
            public void onSuccess(Game s) {
                tvGameCode.setText(s.getInviteCode());
            }

            @Override
            public void onError(String msg) {
            }
        });

        mSryxModel.getRolesInGame(gameCode, new SubscriberOnNextListener<List<Role>>() {
            @Override
            public void onSuccess(List<Role> roles) {
                if(roles.size() > 9){
                    ViewGroup.LayoutParams lp = rvGridRoles.getLayoutParams();
                    lp.height = SizeUtils.dp2px(PrepareGameActivity.this, 330);
                    rvGridRoles.setLayoutParams(lp);
                    rvGridRoles.requestLayout();
                }
                rvGridRoles.addItemDecoration(new RecycleViewDivider(PrepareGameActivity.this, LinearLayoutManager.VERTICAL));
                rvGridRoles.addItemDecoration(new RecycleViewDivider(PrepareGameActivity.this, LinearLayoutManager.HORIZONTAL));
                rvGridRoles.setAdapter(new CommonAdapter<Role>(PrepareGameActivity.this, R.layout.item_grid_role, roles) {
                    @Override
                    protected void convert(final ViewHolder holder, final Role role, int position) {
                        if(role.getPlayerAvatarUrl() != null) {
                            Glide.with(PrepareGameActivity.this)
                                    .load(role.getPlayerAvatarUrl())
                                    .into((ImageView) holder.getView(R.id.iv_header));
                        }
                        if(role.getPlayerNickName() != null){
                            holder.setText(R.id.tv_nickname, role.getPlayerNickName());
                        }
                    }
                });
            }

            @Override
            public void onError(String msg) {
            }
        });
    }
}
