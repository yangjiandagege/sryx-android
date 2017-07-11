package com.yj.sryx.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yj.sryx.R;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;

import butterknife.ButterKnife;

/**
 * Created by eason.yang on 2017/3/6.
 */

public class GameRecordFragment extends Fragment {
    private QuizActivity mActivity;

    private SryxModel mSryxModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_record, container, false);
        ButterKnife.bind(this, view);
        mActivity = (QuizActivity) getActivity();
        mSryxModel = new SryxModelImpl(mActivity);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
