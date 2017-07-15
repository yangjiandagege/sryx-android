package com.yj.sryx.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yj.sryx.SryxApp;
import com.yj.sryx.manager.ActivityStackManager;
import com.yj.sryx.utils.LogUtils;
import com.yj.sryx.utils.ToastUtils;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * Created by eason.yang on 2016/10/19.
 */
public class BaseActivity extends AppCompatActivity {
    private static final String LAYOUT_LINEARLAYOUT = "LinearLayout";
    private static final String LAYOUT_FRAMELAYOUT = "FrameLayout";
    private static final String LAYOUT_RELATIVELAYOUT = "RelativeLayout";

    protected boolean useAutoLayout = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(null != SryxApp.sActivityThemeMap.get(this.getClass().getSimpleName())){
            setTheme(SryxApp.sActivityThemeMap.get(this.getClass().getSimpleName()).getStyleId());
        }
        super.onCreate(savedInstanceState);
        //Activity栈管理，启动时添加到栈中
        ActivityStackManager.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        if(useAutoLayout) {
            View view = null;
            if (name.equals(LAYOUT_FRAMELAYOUT)) {
                view = new AutoFrameLayout(context, attrs);
            }

            if (name.equals(LAYOUT_LINEARLAYOUT)) {
                view = new AutoLinearLayout(context, attrs);
            }

            if (name.equals(LAYOUT_RELATIVELAYOUT)) {
                view = new AutoRelativeLayout(context, attrs);
            }

            if (view != null) return view;
        }
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity栈管理，销毁时从栈中删除
        ActivityStackManager.getInstance().removeActivity(this);
    }
}
