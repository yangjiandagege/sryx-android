package com.yj.sryx.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.SryxConfig;
import com.yj.sryx.common.Category;
import com.yj.sryx.manager.StatusBarUtil;
import com.yj.sryx.model.LoginModel;
import com.yj.sryx.model.LoginModelImpl;
import com.yj.sryx.utils.TransitionHelper;
import com.yj.sryx.widget.CircleImageView;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mLoginModel = new LoginModelImpl(this);
        initLayout();
    }

    private void initLayout() {
        StatusBarUtil.StatusBarLightMode(this);
        setSupportActionBar(toolbar);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_shared:
                break;
            case R.id.action_qrscan:
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
}
