package com.yj.sryx.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.SryxConfig;
import com.yj.sryx.common.Category;
import com.yj.sryx.jpush.LocalAliasAndTags;
import com.yj.sryx.manager.ActivityStackManager;
import com.yj.sryx.manager.StatusBarUtil;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.LoginModel;
import com.yj.sryx.model.LoginModelImpl;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.utils.ToastUtils;
import com.yj.sryx.utils.TransitionHelper;
import com.yj.sryx.widget.CircleImageView;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static com.yj.sryx.SryxApp.sWxApi;
import static com.yj.sryx.SryxApp.sWxUser;

public class MainActivity extends BaseActivity {
    @Bind(R.id.img_header)
    CircleImageView imgHeader;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.categories_rv)
    RecyclerView categoriesRv;
    @Bind(R.id.acce_toolbar)
    Toolbar toolbar;

    private LoginModel mLoginModel;
    private static final int MSG_SET_ALIAS = 1001;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;
    private SryxModel mSryxModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mSryxModel = new SryxModelImpl(this);
        mLoginModel = new LoginModelImpl(this);
        initJpush();
        initLayout();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

        //设置内容
        categoriesRv.setAdapter(new CommonAdapter<Category>(this, R.layout.item_category_simple, SryxConfig.categoryList) {
            @Override
            protected void convert(final ViewHolder holder, final Category category, int position) {
                holder.setText(R.id.tv_category_title, category.getName());
                holder.setTextColor(R.id.tv_category_title, getResources().getColor(category.getTheme().getTextPrimaryColor()));
                holder.setBackgroundColor(R.id.rl_category, getResources().getColor(category.getTheme().getPrimaryColor()));
                holder.setOnClickListener(R.id.rl_category, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startQuizActivityWithTransition(MainActivity.this, holder.getView(R.id.tv_category_title), category.getId());
                    }
                });
            }
        });

        categoriesRv.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        categoriesRv.getViewTreeObserver().removeOnPreDrawListener(this);
                        MainActivity.this.supportStartPostponedEnterTransition();
                        return true;
                    }
                });
    }

    private void startQuizActivityWithTransition(Activity activity, View toolbar, int id) {
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
    public static byte[] bmpToByteArray(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private void wxShared(int flag){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "https://www.ywwxmm.cn/";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "天黑请闭眼";
        msg.description = "天黑请闭眼";
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.sryx_logo);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb,120,120,true);
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
                startActivityForResult(new Intent(this, QrCodeScanActivity.class),1);
                break;
            case R.id.action_rule:
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
        if(data != null){
            String gameCode = data.getExtras().getString(QrCodeScanActivity.QR_SCAN_RESULT);
            ToastUtils.showLongToast(this, gameCode);
            String[] strArray = gameCode.split("=");
            String name = strArray[0];
            String value = strArray[1];
            switch (name){
                case SryxConfig.Key.GAME_CODE:
                    mSryxModel.joinGameByCode(value, sWxUser.getOpenid(), sWxUser.getNickname(), sWxUser.getHeadimgurl(), new SubscriberOnNextListener<String>() {
                        @Override
                        public void onSuccess(String roles) {

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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mClient, getIndexApiAction());
        mClient.disconnect();
    }
}
