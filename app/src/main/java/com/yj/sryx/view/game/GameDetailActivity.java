package com.yj.sryx.view.game;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yj.sryx.R;
import com.yj.sryx.common.GrayscaleTransformation;
import com.yj.sryx.common.RecycleViewDivider;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.model.beans.Game;
import com.yj.sryx.model.beans.Role;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by eason.yang on 2017/7/13.
 */

public class GameDetailActivity extends BaseActivity {
    public final static String KEY_GAME_ID = "game_id";
    @Bind(R.id.rv_list_roles_justice)
    RecyclerView rvListRolesJustice;
    @Bind(R.id.rv_list_roles_evil)
    RecyclerView rvListRolesEvil;
    @Bind(R.id.tv_game_config)
    TextView tvGameConfig;
    @Bind(R.id.tv_game_result)
    TextView tvGameResult;
    @Bind(R.id.tv_duration)
    TextView tvDuration;

    private int mGameId;
    private SryxModel mSryxModel;
    private List<Role> mRoleListEvil;
    private List<Role> mRoleListJustice;
    private CommonAdapter mEvilAdapter;
    private CommonAdapter mJusticeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        ButterKnife.bind(this);
        mGameId = getIntent().getExtras().getInt(KEY_GAME_ID);
        mSryxModel = new SryxModelImpl(this);
        initLayout();
        getRolesInGame();
    }

    private void initLayout() {
        mRoleListEvil = new ArrayList<>();
        rvListRolesEvil.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mEvilAdapter = new CommonAdapter<Role>(this, R.layout.item_list_role, mRoleListEvil) {
            @Override
            protected void convert(final ViewHolder holder, final Role role, int position) {
                setRoleItemView(holder, role, position);
            }
        };
        rvListRolesEvil.setAdapter(mEvilAdapter);

        mRoleListJustice = new ArrayList<>();
        rvListRolesJustice.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mJusticeAdapter = new CommonAdapter<Role>(this, R.layout.item_list_role, mRoleListJustice) {
            @Override
            protected void convert(final ViewHolder holder, final Role role, int position) {
                setRoleItemView(holder, role, position);
            }
        };
        rvListRolesJustice.setAdapter(mJusticeAdapter);
    }

    private void setRoleItemView(ViewHolder holder, Role role, int position) {
        holder.setText(R.id.tv_nickname, role.getPlayerNickName());
        holder.setText(R.id.tv_role, role.getRoleName());
        holder.setVisible(R.id.iv_kill_out, false);
        holder.setVisible(R.id.iv_vote_out, false);
        switch (role.getDeath()) {
            case 0:
                holder.setTextColor(R.id.tv_nickname, getResources().getColor(R.color.color_grey_800));
                holder.setTextColor(R.id.tv_role, getResources().getColor(R.color.color_grey_800));
                holder.setVisible(R.id.tv_death_state, false);
                break;
            case 1:
                holder.setTextColor(R.id.tv_nickname, getResources().getColor(R.color.color_grey_500));
                holder.setTextColor(R.id.tv_role, getResources().getColor(R.color.color_grey_500));
                holder.setVisible(R.id.tv_death_state, true);
                holder.setText(R.id.tv_death_state, "死亡");
                break;
            case 2:
                holder.setTextColor(R.id.tv_nickname, getResources().getColor(R.color.color_grey_500));
                holder.setTextColor(R.id.tv_role, getResources().getColor(R.color.color_grey_500));
                holder.setVisible(R.id.tv_death_state, true);
                holder.setText(R.id.tv_death_state, "出局");
                break;
        }
        setRoleHeaderPic(role, holder);
    }

    private void setRoleHeaderPic(Role role, ViewHolder holder) {
        if (role.getPlayerAvatarUrl() != null) {
            switch (role.getDeath()) {
                case 0:
                    Glide.with(GameDetailActivity.this)
                            .load(role.getPlayerAvatarUrl())
                            .into((ImageView) holder.getView(R.id.iv_header));
                    break;
                case 1:
                case 2:
                    Glide.with(GameDetailActivity.this)
                            .load(role.getPlayerAvatarUrl())
                            .bitmapTransform(new GrayscaleTransformation(this))
                            .into((ImageView) holder.getView(R.id.iv_header));
                    break;
                default:
                    break;
            }
        } else {
            holder.setImageResource(R.id.iv_header, R.mipmap.header_pic);
        }
    }

    private void getRolesInGame() {
        mRoleListEvil.clear();
        mRoleListJustice.clear();
        mSryxModel.getRolesInGame(String.valueOf(mGameId), new SubscriberOnNextListener<List<Role>>() {
            @Override
            public void onSuccess(List<Role> roles) {
                for (int i = 0; i < roles.size(); i++) {
                    switch (roles.get(i).getRoleType()) {
                        case 0:
                            mRoleListEvil.add(roles.get(i));
                            break;
                        case 1:
                        case 2:
                            mRoleListJustice.add(roles.get(i));
                            break;
                        default:
                            break;
                    }
                }
                mEvilAdapter.notifyDataSetChanged();
                mJusticeAdapter.notifyDataSetChanged();

                tvGameConfig.setText("游戏配置："+roles.get(0).getRemark());
                tvGameResult.setText("游戏结果："+roles.get(0).getGameResult());
            }

            @Override
            public void onError(String msg) {
            }
        });
        mSryxModel.getGameById(String.valueOf(mGameId), new SubscriberOnNextListener<Game>() {
            @Override
            public void onSuccess(Game game) {
                tvDuration.setText("游戏时间："+game.getGameDate()+" "+game.getStartTime()+" ~ "+game.getEndTime());
            }

            @Override
            public void onError(String msg) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
