package com.yj.sryx.view.game;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.yj.sryx.R;
import com.yj.sryx.SryxConfig;
import com.yj.sryx.common.RecycleViewDivider;
import com.yj.sryx.manager.RxBus;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.model.beans.Game;
import com.yj.sryx.model.beans.Role;
import com.yj.sryx.model.beans.WxUser;
import com.yj.sryx.utils.CountDownTimerUtil;
import com.yj.sryx.utils.EncodingHandler;
import com.yj.sryx.utils.LogUtils;
import com.yj.sryx.utils.SizeUtils;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.widget.AcceBar;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

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
    private String mGameId;
    private CountDownTimerUtil mTimeCounter;
    private Game mGame;
    private List<WxUser> mWxUserListForTest = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_game);
        ButterKnife.bind(this);
        mSryxModel = new SryxModelImpl(this);
        mRoleList = new ArrayList<>();
        initData();
        initLayout();
        initRxbus();
    }

    private void initData() {
        mWxUserListForTest.add(new WxUser("oNMwb0bDbMJyl84q5jAddjyBexmA",
                "我是MT",
                "http://wx.qlogo.cn/mmopen/vi_32/hzgRzL39o1b1NVPlhfoFZqapqUNUvnlkKhcIshXkTkxDpJdHnh3LMz7g1eQbhUBnicx2mwndpduiazS39phM6ekg/0"));
        mWxUserListForTest.add(new WxUser("oNMwb0dFoGQ7TvHJd7jbyFdmjgk4",
                "被人追杀的eason",
                "http://wx.qlogo.cn/mmopen/vi_32/GvzrAKyDiboTQJWicAKm5ejicUcMB9txkW9aApDBgvG8avfeA82p6b2ghskI02IJuC2RM1NdzvCAXaCHuACK6oAOQ/0"));
        mWxUserListForTest.add(new WxUser("oNMwb0YSPcT4vXxOjuO75xsbwOyo",
                "边城浪子",
                "http://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI9Nicr0ahll9C3nZibUzsKF1xdBelmT08N0PBT4HfY4BvmEjkjvgJx6BFwFFYqTV8rCKbHzydrKvibQ/0"));
        mWxUserListForTest.add(new WxUser("oNMwb0VrgsjHBw7I6dG64xBwJYuQ",
                "习大喵子",
                "http://wx.qlogo.cn/mmopen/vi_32/5YSF1tBkafDQGIIic9uUqsc3PwYUcoCQWNbD1rD3OlZmEYVSOBFG0UkYNibEUHyxh1icvmQOysqm1zVJdDFzeeUiag/0"));
    }

    private void initRxbus() {
        Observable<String> observable = RxBus.getInstance().register(this);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                LogUtils.logout("received : "+s);
                switch (s){
                    case "0":
                        getRolesInGame();
                        break;
                    case "1":
                        getRolesInGame();
                        mTimeCounter.cancel();
                        btnStartGame.setText("开始游戏");
                        btnStartGame.setClickable(true);
                        btnStartGame.setAlpha(1);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    int counter = 0;
    private void initLayout() {
        btnCancleGame.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new CountDownTimerUtil(mWxUserListForTest.size() * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mSryxModel.joinGameByCode(mGame.getInviteCode(),
                                mWxUserListForTest.get(counter).getOpenid(),
                                mWxUserListForTest.get(counter).getNickname(),
                                mWxUserListForTest.get(counter).getHeadimgurl(), null);
                        counter ++;
                        btnCancleGame.setClickable(false);
                    }
                    @Override
                    public void onFinish() {
                        mSryxModel.joinGameByCode(mGame.getInviteCode(),
                                mWxUserListForTest.get(counter).getOpenid(),
                                mWxUserListForTest.get(counter).getNickname(),
                                mWxUserListForTest.get(counter).getHeadimgurl(), null);
                        mTimeCounter.cancel();
                        btnCancleGame.setClickable(true);
                        counter = 0;
                    }
                }.start();
                return true;
            }
        });
        mGameId = (String) getIntent().getExtras().get(KEY_GAME_ID);
        mTimeCounter = new CountDownTimerUtil(120 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnStartGame.setText("等待中("+millisUntilFinished / 1000+"s)");
                btnStartGame.setClickable(false);
                btnStartGame.setAlpha((float) 0.6);
            }
            @Override
            public void onFinish() {
                this.cancel();
                showGameTimeOutCancelDialog();
            }
        }.start();
        mSryxModel.getGameById(mGameId, new SubscriberOnNextListener<Game>() {
            @Override
            public void onSuccess(Game game) {
                mGame = game;
                tvGameCode.setText(game.getInviteCode());
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
        getRolesInGame();
    }

    private void getRolesInGame(){
        if(mSryxModel != null) {
            mSryxModel.getRolesInGame(mGameId, new SubscriberOnNextListener<List<Role>>() {
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
    }

    @OnClick({R.id.iv_qrcode, R.id.btn_cancle_game, R.id.btn_start_game})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_qrcode:
                View layout = getLayoutInflater().inflate(R.layout.dialog_show_qr_code,(ViewGroup) findViewById(R.id.dialog));
                ImageView imgQrCode = (ImageView)layout.findViewById(R.id.img_qr_code);
                try {
                    Bitmap barBitmap = EncodingHandler.create2Code(SryxConfig.Key.GAME_CODE+"="+mGame.getInviteCode(), 800);
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
                showGameCancelDialog();
                break;
            case R.id.btn_start_game:
                Intent intent = new Intent(PrepareGameActivity.this, GameManageActivity.class);
                intent.putExtra(GameManageActivity.KEY_GAME_ID, mGame.getGameId());
                startActivity(intent);
                finish();
                break;
        }
    }

    private void showGameCancelDialog() {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("亲，您确定要取消本局游戏吗？")
                .style(NormalDialog.STYLE_TWO)
                .titleTextSize(23)
                .btnText("继续游戏", "确认取消")
                .btnTextColor(Color.parseColor("#383838"), Color.parseColor("#D4D4D4"))
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
                        mSryxModel.cancleGame(mGame.getGameId(), new SubscriberOnNextListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                dialog.superDismiss();
                                PrepareGameActivity.this.finish();
                            }

                            @Override
                            public void onError(String msg) {
                            }
                        });
                    }
                });
    }

    private void showGameTimeOutCancelDialog(){
        final NormalDialog dialog = new NormalDialog(PrepareGameActivity.this);
        dialog.content("还有小伙伴没有加入到游戏，本局游戏自动解散。~")
                .btnNum(1)
                .btnText("确定")
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                PrepareGameActivity.this.finish();
                dialog.superDismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        showGameCancelDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimeCounter.cancel();
        RxBus.getInstance().unregister(this);
        mSryxModel = null;
    }
}
