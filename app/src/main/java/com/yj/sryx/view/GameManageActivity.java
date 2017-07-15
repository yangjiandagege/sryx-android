package com.yj.sryx.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.yj.sryx.R;
import com.yj.sryx.common.GrayscaleTransformation;
import com.yj.sryx.common.RecycleViewDivider;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.model.beans.Role;
import com.yj.sryx.utils.CountDownTimerUtil;
import com.yj.sryx.utils.TimeUtils;
import com.yj.sryx.widget.AcceBar;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by eason.yang on 2017/7/13.
 */

public class GameManageActivity extends BaseActivity {
    public final static String KEY_GAME_ID = "game_id";
    @Bind(R.id.rv_list_roles_justice)
    RecyclerView rvListRolesJustice;
    @Bind(R.id.rv_list_roles_evil)
    RecyclerView rvListRolesEvil;
    @Bind(R.id.toolbar)
    AcceBar toolbar;
    private int mGameId;
    private SryxModel mSryxModel;
    private List<Role> mRoleListEvil;
    private List<Role> mRoleListJustice;
    private CommonAdapter mEvilAdapter;
    private CommonAdapter mJusticeAdapter;
    private CountDownTimerUtil mTimeCounter;
    private int mCounter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_manage);
        ButterKnife.bind(this);
        mGameId = getIntent().getExtras().getInt(KEY_GAME_ID);
        mSryxModel = new SryxModelImpl(this);
        initLayout();
        getRolesInGame();
    }

    private void initLayout() {
        mCounter = 0;
        mTimeCounter = new CountDownTimerUtil(3600 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                toolbar.setManagementText(TimeUtils.secToTime(mCounter++));
            }

            @Override
            public void onFinish() {
                this.cancel();
            }
        }.start();

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
        holder.getView(R.id.iv_kill_out).setOnClickListener(new VoteOrKillClickListener(position, role));
        holder.getView(R.id.iv_vote_out).setOnClickListener(new VoteOrKillClickListener(position, role));
        switch (role.getDeath()){
            case 0:
                holder.setTextColor(R.id.tv_nickname, getResources().getColor(R.color.color_grey_800));
                holder.setTextColor(R.id.tv_role, getResources().getColor(R.color.color_grey_800));
                holder.setVisible(R.id.iv_kill_out, true);
                holder.setVisible(R.id.iv_vote_out, true);
                holder.setVisible(R.id.tv_death_state, false);
                break;
            case 1:
                holder.setTextColor(R.id.tv_nickname, getResources().getColor(R.color.color_grey_500));
                holder.setTextColor(R.id.tv_role, getResources().getColor(R.color.color_grey_500));
                holder.setVisible(R.id.iv_kill_out, false);
                holder.setVisible(R.id.iv_vote_out, false);
                holder.setVisible(R.id.tv_death_state, true);
                holder.setText(R.id.tv_death_state, "死亡");
                break;
            case 2:
                holder.setTextColor(R.id.tv_nickname, getResources().getColor(R.color.color_grey_500));
                holder.setTextColor(R.id.tv_role, getResources().getColor(R.color.color_grey_500));
                holder.setVisible(R.id.iv_kill_out, false);
                holder.setVisible(R.id.iv_vote_out, false);
                holder.setVisible(R.id.tv_death_state, true);
                holder.setText(R.id.tv_death_state, "出局");
                break;
        }
        setRoleHeaderPic(role, holder);
    }

    private void setRoleHeaderPic(Role role, ViewHolder holder){
        if (role.getPlayerAvatarUrl() != null) {
            switch (role.getDeath()){
                case 0:
                    Glide.with(GameManageActivity.this)
                            .load(role.getPlayerAvatarUrl())
                            .into((ImageView) holder.getView(R.id.iv_header));
                    break;
                case 1:
                case 2:
                    Glide.with(GameManageActivity.this)
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

    class VoteOrKillClickListener implements View.OnClickListener{
        private int mPosition;
        private Role mRole;

        public VoteOrKillClickListener(int position, Role role){
            mPosition = position;
            mRole = role;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_kill_out:
                    showKillOrVoteRoleDialog(0, mRole);
                    break;
                case R.id.iv_vote_out:
                    showKillOrVoteRoleDialog(1, mRole);
                    break;
            }
        }
    }

    private void showKillOrVoteRoleDialog(final int operateType, final Role role) {
        final NormalDialog dialog = new NormalDialog(this);
        String msgContent;
        if(operateType == 0){
            msgContent = "大人，您确定" + role.getPlayerNickName() + "("+role.getRoleName()+")已经被杀死了吗？";
        }else {
            msgContent = "大人，您确定" + role.getPlayerNickName() + "("+role.getRoleName()+")已经被大家公投出局了吗？";
        }
        dialog.content(msgContent)
                .style(NormalDialog.STYLE_TWO)
                .titleTextSize(23)
                .btnText("我再去确认一下", "我确定")
                .btnTextColor(Color.parseColor("#D4D4D4"), Color.parseColor("#383838"))
                .btnTextSize(16f, 16f)
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        mSryxModel.setRoleOut(operateType == 0?1:2, role.getRoleId(), role.getGameId(), new SubscriberOnNextListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                if(!s.equals("continue")){
                                    showResultDailog(s);
                                }
                                getRolesInGame();
                            }

                            @Override
                            public void onError(String msg) {
                            }
                        });
                        dialog.dismiss();
                    }
                });
    }

    private void showResultDailog(String result) {
        String resultContent = null;
        switch (result){
            case "0":
                resultContent = "警察全部死亡，杀手集团获得胜利！";
                break;
            case "1":
                resultContent = "杀手全部死亡，正义联盟获得胜利！";
                break;
            case "2":
                resultContent = "平民全部死亡，双方平局！";
                break;
        }
        final NormalDialog dialog = new NormalDialog(this);
        dialog.setCanceledOnTouchOutside(true);
        dialog.content(resultContent)
                .title("比赛结束")
                .btnNum(1)
                .btnText("好的")
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                finish();
                dialog.superDismiss();
            }
        });
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
            }

            @Override
            public void onError(String msg) {
            }
        });
    }


    private void showGameNotOverDialog() {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("亲，本局游戏还未结束，请您游戏结束后再退出！")
                .btnNum(1)
                .btnText("继续游戏")
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        showGameNotOverDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimeCounter.cancel();
    }
}
