package com.yj.sryx.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.yj.sryx.manager.ActivityStackManager;
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

    private ProgressDialog mProgressDialog          = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Activity栈管理，启动时添加到栈中
        ActivityStackManager.getInstance().addActivity(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);
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

    public void showProgressDialog(String msg, boolean shieldKey) {
        mProgressDialog.setMessage(msg);
        if (shieldKey) {
            mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity栈管理，销毁时从栈中删除
        ActivityStackManager.getInstance().removeActivity(this);
    }
}
