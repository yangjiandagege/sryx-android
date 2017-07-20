package com.yj.sryx.view.im;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.yj.sryx.R;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.view.game.GameRecordJudgeFragment;
import com.yj.sryx.view.game.GameRecordPlayerFragment;
import com.yj.sryx.view.game.GameRecordsFragment;
import com.yj.sryx.view.game.QuizActivity;

import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImActivity extends BaseActivity {
    public final static String TAB_SESSION = "消息";
    public final static String TAB_CONTACT = "联系人";
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.vp_content)
    ViewPager vpContent;

    private Activity mActivity;
    private List<String> mTabList;
    private List<Fragment> mFragList;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im);
        ButterKnife.bind(this);
        mActivity = this;
        initFragment();
    }

    private void initFragment() {
        mTabList = new ArrayList<>();
        mTabList.add(TAB_SESSION);
        mTabList.add(TAB_CONTACT);

        mFragList = new ArrayList<>();
        mFragList.add(new SessionsFragment());
        mFragList.add(new ContactsFragment());

        mAdapter = new MyAdapter(getSupportFragmentManager(), mFragList, mTabList);
        vpContent.setAdapter(mAdapter);
        tabs.setupWithViewPager(vpContent);
    }

    private class MyAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;
        private List<String> mTabList;

        MyAdapter(FragmentManager fm, List<Fragment> fragments, List<String> tabList) {
            super(fm);
            this.mFragmentList = fragments;
            this.mTabList = tabList;
        }

        @Override
        public Fragment getItem(int position) {
            return (mFragmentList == null || mFragmentList.size() == 0) ? null : mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList == null ? 0 : mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabList.get(position);
        }
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
}
