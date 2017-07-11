package com.yj.sryx.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.yj.sryx.R;

import butterknife.ButterKnife;

/**
 * Created by yangjian on 2017/3/6.
 */

public class CreateGameFragment extends Fragment {
    private QuizActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_game, container, false);
        ButterKnife.bind(this, view);
        mActivity = (QuizActivity) getActivity();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
