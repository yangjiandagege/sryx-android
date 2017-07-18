package com.yj.sryx.view.im;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yj.sryx.R;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.view.game.QuizActivity;

import butterknife.ButterKnife;

/**
 * Created by  on 2017/7/18.
 */

public class SessionsFragment extends Fragment{
    private Activity mActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sessions, container, false);
        mActivity = getActivity();
        return view;
    }

}