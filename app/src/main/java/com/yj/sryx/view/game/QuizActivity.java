package com.yj.sryx.view.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.SryxConfig;
import com.yj.sryx.common.TextSharedElementCallback;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.widget.AcceBar;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yangjian on 2016/10/18.
 */

public class QuizActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    public AcceBar toolbar;

    private String mFragName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mFragName = getIntent().getExtras().getString(SryxConfig.Key.CATEGORY_ID);
        setTheme(SryxApp.sActivityThemeMap.get(mFragName).getStyleId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);
        initLayoutView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fg_container);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initLayoutView() {
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);
        int categoryNameTextSize = getResources()
                .getDimensionPixelSize(R.dimen.category_item_text_size);
        int paddingStart = getResources().getDimensionPixelSize(R.dimen.spacing_double);
        final int startDelay = getResources().getInteger(R.integer.toolbar_transition_duration);

        ActivityCompat.setEnterSharedElementCallback(this,
                new TextSharedElementCallback(categoryNameTextSize, paddingStart) {
                    @Override
                    public void onSharedElementStart(List<String> sharedElementNames,
                                                     List<View> sharedElements,
                                                     List<View> sharedElementSnapshots) {
                        super.onSharedElementStart(sharedElementNames,
                                sharedElements,
                                sharedElementSnapshots);
                        toolbar.getPressBackView().setScaleX(0f);
                        toolbar.getPressBackView().setScaleY(0f);
                        toolbar.getManagementView().setScaleX(0f);
                        toolbar.getManagementView().setScaleY(0f);
                    }

                    @Override
                    public void onSharedElementEnd(List<String> sharedElementNames,
                                                   List<View> sharedElements,
                                                   List<View> sharedElementSnapshots) {
                        super.onSharedElementEnd(sharedElementNames,
                                sharedElements,
                                sharedElementSnapshots);
                        // Make sure to perform this animation after the transition has ended.
                        ViewCompat.animate(toolbar.getPressBackView()).setStartDelay(startDelay).scaleX(1f).scaleY(1f).alpha(1f).start();
                        ViewCompat.animate(toolbar.getManagementView()).setStartDelay(startDelay).scaleX(1f).scaleY(1f).alpha(1f).start();
                    }
                });

        initFragment();
    }

    private void initFragment() {
        Fragment fragment;
        if(mFragName.equals(CreateGameFragment.class.getSimpleName())){
            fragment = new CreateGameFragment();
            toolbar.setTitleText("创建游戏");
        }else if(mFragName.equals(JoinGameFragment.class.getSimpleName())){
            fragment = new JoinGameFragment();
            toolbar.setTitleText("加入游戏");
        }else {
            fragment = new GameRecordsFragment();
            toolbar.setTitleText("我的记录");
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fg_container, fragment)
                .commit();
    }
}
