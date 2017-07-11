package com.yj.sryx.controller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.SryxConfig;
import com.yj.sryx.common.Category;
import com.yj.sryx.common.TextSharedElementCallback;
import com.yj.sryx.utils.ApiLevelHelper;
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
    @Bind(R.id.fg_container)
    FrameLayout fgContainer;

    private Category mCategory;
    private Fragment mFragment;
    private String fragmentTag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);
        initData();
        initLayoutView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fg_container);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mCategory = SryxConfig.categoryList.get(bundle.getInt(SryxConfig.Key.CATEGORY_ID));
        }
        SryxApp.currentThemeId = mCategory.getTheme().getStyleId();
        SryxApp.toolbarTextColor = ContextCompat.getColor(this,
                mCategory.getTheme().getTextPrimaryColor());
    }

    private void initLayoutView() {
        setTheme(SryxApp.currentThemeId);
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this,
                    mCategory.getTheme().getPrimaryDarkColor()));
        }
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);
        fgContainer.setBackgroundColor(ContextCompat.
                getColor(this, mCategory.getTheme().getWindowBackgroundColor()));
        toolbar.setTitleText(mCategory.getName());
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

        findFragment();
        initFragment();
    }

    private void findFragment() {
        switch (mCategory.getId()) {
            case SryxConfig.ID_0:
                newFragmentAc0();
                break;
            case SryxConfig.ID_1:
                newFragmentAc1();
                break;
            case SryxConfig.ID_2:
                newFragmentAc2();
                break;
            default:
                break;
        }
    }

    public void newFragmentAc0(){
        fragmentTag = "id_0";
        mFragment = new CreateGameFragment();
    }

    public void newFragmentAc1(){
        fragmentTag = "id_1";
        mFragment = new JoinGameFragment();
    }

    public void newFragmentAc2(){
        fragmentTag = "id_2";
        mFragment = new GameRecordFragment();
    }

    private void initFragment() {
        if (mFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fg_container, mFragment, fragmentTag)
                    .commit();
        }
    }
}
