package com.yj.sryx.view.game;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yj.sryx.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yangjian on 2017/2/16.
 */

public class GameRecordsFragment extends Fragment {
    public final static String RECORD_TYPE_PLAYER = "玩家局";
    public final static String RECORD_TYPE_JUDGE = "裁判局";

    private static final int REQUEST_RETURN = 1;
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.vp_content)
    ViewPager vpContent;

    private List<String> mRecordTypeList;
    private QuizActivity mActivity;
    private List<Fragment> mFragList;
    private MyAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_record_manager, container, false);
        ButterKnife.bind(this, view);
        initData();
        initLayoutView();
        return view;
    }

    private void initData() {
        mActivity = (QuizActivity) getActivity();

        mRecordTypeList = new ArrayList<>();
        mRecordTypeList.add(RECORD_TYPE_PLAYER);
        mRecordTypeList.add(RECORD_TYPE_JUDGE);

        mFragList = new ArrayList<>();
        mFragList.add(new GameRecordPlayerFragment());
        mFragList.add(new GameRecordJudgeFragment());
    }

    private void initLayoutView() {
        mAdapter = new MyAdapter(mActivity.getSupportFragmentManager(), mFragList, mRecordTypeList);
        vpContent.setAdapter(mAdapter);
        tabs.setupWithViewPager(vpContent);
    }



    private class MyAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;
        private List<String> mRecordTypes;

        MyAdapter(FragmentManager fm, List<Fragment> fragments, List<String> recordTypes) {
            super(fm);
            this.mFragmentList = fragments;
            this.mRecordTypes = recordTypes;
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
            return mRecordTypes.get(position);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
