package com.yj.sryx.view.game;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.SryxConfig;
import com.yj.sryx.jpush.LocalAliasAndTags;
import com.yj.sryx.manager.ActivityStackManager;
import com.yj.sryx.manager.StatusBarUtil;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.model.LoginModel;
import com.yj.sryx.model.LoginModelImpl;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.model.beans.Game;
import com.yj.sryx.utils.LogUtils;
import com.yj.sryx.utils.ToastUtils;
import com.yj.sryx.utils.TransitionHelper;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.view.im.ImActivity;
import com.yj.sryx.widget.CircleImageView;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static com.yj.sryx.SryxApp.sWxApi;
import static com.yj.sryx.SryxApp.sWxUser;

public class MainActivity extends BaseActivity {
    public static final java.lang.String IS_ALREADY_LOGIN = "is_already_login";
    @Bind(R.id.img_header)
    CircleImageView imgHeader;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.acce_toolbar)
    Toolbar toolbar;
    @Bind(R.id.rl_create_game)
    RelativeLayout rlCreateGame;
    @Bind(R.id.rl_join_game)
    RelativeLayout rlJoinGame;
    @Bind(R.id.rl_my_record)
    RelativeLayout rlMyRecord;
    @Bind(R.id.tv_create_game)
    TextView tvCreateGame;
    @Bind(R.id.tv_join_game)
    TextView tvJoinGame;
    @Bind(R.id.tv_my_record)
    TextView tvMyRecord;
    @Bind(R.id.iv_friend)
    ImageView ivFriend;


    private static final int MSG_SET_ALIAS = 1001;

    private SryxModel mSryxModel;
    private LoginModel mLoginModel;
    private AsmackModel mAsmackModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mSryxModel = new SryxModelImpl(this);
        mLoginModel = new LoginModelImpl(this);
        mAsmackModel = new AsmackModelImpl(this);
        initJpush();
        initLayout();
        if(!getIntent().getExtras().getBoolean(IS_ALREADY_LOGIN, false)){
            openfireLogin();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLastGameState();
    }

    private void openfireLogin(){
        mAsmackModel.login(sWxUser.getOpenid(), sWxUser.getOpenid(), sWxUser.getNickname(), new SubscriberOnNextListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                LogUtils.logout("登录Openfire成功！");
                ToastUtils.showLongToast(MainActivity.this, "登录Openfire成功！");
            }

            @Override
            public void onError(String msg) {
                LogUtils.logout("登录Openfire失败！");
                ToastUtils.showLongToast(MainActivity.this, "登录Openfire失败！");
            }
        });
    }

    private void checkLastGameState() {
        mSryxModel.getMyLastGame(sWxUser.getOpenid(), new SubscriberOnNextListener<Game>() {
            @Override
            public void onSuccess(Game game) {
                if(null != game) {
                    if (game.getGameOwnerId().equals(sWxUser.getOpenid())) {
                        if (game.getState() == 0) {
                            showJudgeGamePrepareDialog(game);
                        } else if (game.getState() == 1) {
                            showJudgeGameProcessDialog(game);
                        }
                    } else {
                        if (game.getState() == 0 || game.getState() == 1) {
                            showPlayerGameProcessDialog(game);
                        }
                    }
                }
            }

            @Override
            public void onError(String msg) {
            }
        });
    }

    private void showPlayerGameProcessDialog(final Game game) {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("亲，您还有一局游戏正在进行中！")
                .btnNum(1)
                .btnText("继续")
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, MyRoleActivity.class);
                intent.putExtra(MyRoleActivity.GAME_CODE, game.getInviteCode());
                startActivity(intent);
            }
        });
    }

    private void showJudgeGameProcessDialog(final Game game) {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("亲，您有一局游戏正在进行中！")
                .btnNum(1)
                .btnText("继续")
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, GameManageActivity.class);
                intent.putExtra(GameManageActivity.KEY_GAME_ID, String.valueOf(game.getGameId()));
                startActivity(intent);
            }
        });
    }

    private void showJudgeGamePrepareDialog(final Game game) {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("亲，您有一局游戏正在准备中！")
                .btnNum(1)
                .btnText("继续")
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, PrepareGameActivity.class);
                intent.putExtra(PrepareGameActivity.KEY_GAME_ID, String.valueOf(game.getGameId()));
                startActivity(intent);
            }
        });
    }

    private void initJpush() {
        String alias = SryxApp.sWxUser.getOpenid();
        Set<String> tags = new HashSet<>();
        tags.add("sryx");
        LocalAliasAndTags localAliasAndTags = new LocalAliasAndTags(alias, tags);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, localAliasAndTags));
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    // 调用 JPush 接口来设置别名。
                    LocalAliasAndTags localAliasAndTags = (LocalAliasAndTags) msg.obj;
                    JPushInterface.setAliasAndTags(getApplicationContext(), localAliasAndTags.alias,
                            localAliasAndTags.tags, mAliasCallback);
                    break;
            }
        }
    };

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            switch (code) {
                case 0:
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS,
                            new LocalAliasAndTags(alias, tags)), 1000 * 60);
                    break;
                default:
                    break;
            }
        }
    };

    private void initLayout() {
        StatusBarUtil.StatusBarLightMode(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Glide.with(this)
                .load(SryxApp.sWxUser.getHeadimgurl())
                .into(imgHeader);
        tvNickname.setText(SryxApp.sWxUser.getNickname());
    }

    private void startQuizActivityWithTransition(Activity activity, View toolbar, String id) {
        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                new Pair<>(toolbar, activity.getString(R.string.transition_toolbar)));
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat sceneTransitionAnimation = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs);
        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
        Intent startIntent = new Intent(MainActivity.this, QuizActivity.class);
        startIntent.putExtra(SryxConfig.Key.CATEGORY_ID, id);
        ActivityCompat.startActivity(activity,
                startIntent,
                transitionBundle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public static byte[] bmpToByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private void wxShared(int flag) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "https://www.ywwxmm.cn/";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "天黑请闭眼";
        msg.description = "天黑请闭眼";
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.sryx_logo);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb, 120, 120, true);
        msg.thumbData = bmpToByteArray(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = (flag == 0) ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        sWxApi.sendReq(req);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_shared:
                final String[] stringItems = {"分享给微信好友", "分享到朋友圈"};
                final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
                dialog.title("选择分享途径")
                        .titleTextSize_SP(14.5f)
                        .show();
                dialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                        wxShared(position);
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.action_qrscan:
                startActivityForResult(new Intent(this, QrCodeScanActivity.class), 1);
                break;
            case R.id.action_rule:
                startActivity(new Intent(this, RuleActivity.class));
                break;
            case R.id.action_exit:
                mLoginModel.exitWxLogin();
                startActivity(new Intent(this, SplashActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("大爷,真的要走吗?再玩会儿嘛\n~(●—●)~")//
                .style(NormalDialog.STYLE_TWO)//
                .titleTextSize(23)//
                .btnText("再玩会儿", "残忍退出")//
                .btnTextColor(Color.parseColor("#383838"), Color.parseColor("#D4D4D4"))//
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
                        dialog.superDismiss();
                        ActivityStackManager.getInstance().AppExit();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            final String gameCode = data.getExtras().getString(QrCodeScanActivity.QR_SCAN_RESULT);
            final String[] strArray = gameCode.split("=");
            String name = strArray[0];
            String value = strArray[1];
            switch (name) {
                case SryxConfig.Key.GAME_CODE:
                    mSryxModel.joinGameByCode(value, sWxUser.getOpenid(), sWxUser.getNickname(), sWxUser.getHeadimgurl(), new SubscriberOnNextListener<String>() {
                        @Override
                        public void onSuccess(String roles) {
                            Intent intent = new Intent(MainActivity.this, MyRoleActivity.class);
                            intent.putExtra(MyRoleActivity.GAME_CODE, gameCode);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(String msg) {
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    @OnClick({R.id.rl_create_game, R.id.rl_join_game, R.id.rl_my_record, R.id.iv_friend})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_create_game:
                startQuizActivityWithTransition(MainActivity.this, tvCreateGame, CreateGameFragment.class.getSimpleName());
                break;
            case R.id.rl_join_game:
                startQuizActivityWithTransition(MainActivity.this, tvJoinGame, JoinGameFragment.class.getSimpleName());
                break;
            case R.id.rl_my_record:
                startQuizActivityWithTransition(MainActivity.this, tvMyRecord, GameRecordsFragment.class.getSimpleName());
                break;
            case R.id.iv_friend:
                startActivity(new Intent(MainActivity.this, ImActivity.class));
                break;
        }
    }
}
