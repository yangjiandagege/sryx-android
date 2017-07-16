package com.yj.sryx.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yj.sryx.R;
import com.yj.sryx.SryxApp;

import static android.graphics.PorterDuff.Mode.SRC_IN;

/**
 * Created by eason.yang on 2017/3/6.
 */
public class AcceBar extends LinearLayout {
    private ImageView ivPressBack;
    private TextView tvTitle;
    private TextView tvManagement;
    private ImageView ivSearch;
    private Toolbar toolbar;

    private Context mContext;

    private boolean isSearchVisible;

    private String mTitle;
    private String mManagement;
    private String mCustomizeText;
    private String mPageCode;
    private boolean mIsBgTransparent;
    private OnManageListener mManageListener;
    private OnSearchListener mSearchListener;

    public AcceBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        findView(context, attrs);
        analyticConfig(context, attrs);
        setUpView();
    }

    private void findView(Context context, AttributeSet attrs) {
        View view;
        view = View.inflate(context, R.layout.acce_toolbar_dark, this);
        mContext = context;

        ivPressBack = (ImageView) view.findViewById(R.id.iv_press_back);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvManagement = (TextView) view.findViewById(R.id.tv_management);
        ivSearch = (ImageView) view.findViewById(R.id.iv_search);
        toolbar = (Toolbar) view.findViewById(R.id.acce_toolbar);
    }

    private void analyticConfig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AcceBar);
        isSearchVisible = a.getBoolean(R.styleable.AcceBar_searchVisible, false);
        mIsBgTransparent = a.getBoolean(R.styleable.AcceBar_isBgTransparent, false);
        mTitle = a.getString(R.styleable.AcceBar_toolbarTitle);
        mManagement = a.getString(R.styleable.AcceBar_management);
        mPageCode =  a.getString(R.styleable.AcceBar_pageCode);
        a.recycle();
    }

    private void setUpView() {
        ((AppCompatActivity)mContext).setSupportActionBar(toolbar);

        ivSearch.setVisibility(isSearchVisible?VISIBLE:GONE);
        if(mIsBgTransparent){
            toolbar.setBackgroundColor(Color.TRANSPARENT);
        }
        tvTitle.setText(mTitle);
        if(mManagement != null){
            tvManagement.setVisibility(VISIBLE);
            tvManagement.setText(mManagement);
        }

        ivPressBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity)mContext).onBackPressed();
            }
        });

        tvManagement.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mManageListener != null) {
                    mManageListener.OnManageClick();
                }
            }
        });
        ivSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSearchListener != null) {
                    mSearchListener.OnSearchClick();
                }
            }
        });
    }

    public void setTitleText(String title){
        mTitle = title;
        tvTitle.setText(mTitle);
    }

    public String getTitleText(){
        return mTitle;
    }

    public void setManagement(String managementText, OnManageListener listener){
        setManagementText(managementText);
        setManagementClickListener(listener);
    }

    public void setManagementText(String managementText){
        mManagement = managementText;
        tvManagement.setVisibility(VISIBLE);
        tvManagement.setText(mManagement);
    }

    public String getManagementText(){
        return mManagement;
    }

    public TextView getManagementView(){
        return tvManagement;
    }

    public ImageView getPressBackView(){
        return ivPressBack;
    }

    public void setManagementClickListener(OnManageListener listener){
        mManageListener = listener;
    }

    public void setSearchClickListener(OnSearchListener listener){
        mSearchListener = listener;
    }

    public interface OnManageListener {
        void OnManageClick();
    }

    public interface OnSearchListener {
        void OnSearchClick();
    }
}
