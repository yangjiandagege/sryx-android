package com.yj.sryx.view.im;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.yj.sryx.R;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.widget.AcceBar;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    AcceBar toolbar;
    @Bind(android.R.id.tabhost)
    FragmentTabHost tabhost;

    private int selectedId = 0;

    private String[] arrModelName =new String[]{"消息","联系人"};

    private int[] arrImgNormal = new int[]{R.mipmap.message_normal,
            R.mipmap.contacts_normal};

    private int[] arrImgPressed = new int[]{R.mipmap.message_selected,
            R.mipmap.contacts_selected};

    private Class[] fragments = new Class[]{SessionsFragment.class, ContactsFragment.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im);
        ButterKnife.bind(this);
        initFragment();
    }

    private void initFragment() {
        tabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        for (int i = 0; i < fragments.length; i++){
            tabhost.addTab(tabhost.newTabSpec(""+i).setIndicator(getIndicator(i)), fragments[i], null);
        }

        tabhost.getTabWidget().setDividerDrawable(null);

        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                int clickedId = Integer.valueOf(tabId);
                tabClick(clickedId,true);
                tabClick(selectedId,false);
                selectedId = clickedId;
                if(clickedId == 0){
                    toolbar.setTitleText("消息");
                }else {
                    toolbar.setTitleText("联系人");
                }
            }
        });

        tabClick(selectedId,true);
        tabhost.setCurrentTab(selectedId);
    }

    private View getIndicator(int position){
        View rootVIew = getLayoutInflater().inflate(R.layout.item_tab_host,null);
        TextView tv = (TextView)rootVIew.findViewById(R.id.item_tv);
        ImageView iv = (ImageView)rootVIew.findViewById(R.id.item_iv);
        tv.setText(arrModelName[position]);
        iv.setImageResource(arrImgNormal[position]);
        return rootVIew;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_im, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_friend:
                startActivity(new Intent(ImActivity.this, SearchFriendActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *  切换tab颜色和图标
     */
    private void tabClick(int tabId , boolean isSelected){
        View view = tabhost.getTabWidget().getChildTabViewAt(tabId);
        TextView textView = ((TextView)view.findViewById(R.id.item_tv));
        ImageView imageView = ((ImageView)view.findViewById(R.id.item_iv));
        if (isSelected){
            textView.setTextColor(getResources().getColor(R.color.text_selected));
            imageView.setImageResource(arrImgPressed[tabId]);
        }else {
            textView.setTextColor(getResources().getColor(R.color.text_dark));
            imageView.setImageResource(arrImgNormal[tabId]);
        }
    }
}
