package com.yj.sryx.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.model.beans.WxUser;
import com.yj.sryx.utils.LogUtils;
import com.yj.sryx.utils.ToastUtils;
import com.yj.sryx.widget.AmountView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangjian on 2017/3/6.
 */

public class CreateGameFragment extends Fragment {
    @Bind(R.id.av_killer_num)
    AmountView avKillerNum;
    @Bind(R.id.av_police_num)
    AmountView avPoliceNum;
    @Bind(R.id.av_citizen_num)
    AmountView avCitizenNum;
    @Bind(R.id.btn_create_game)
    Button btnCreateGame;
    private QuizActivity mActivity;

    private SryxModel mSryxModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_game, container, false);
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

    @OnClick(R.id.btn_create_game)
    public void onClick() {
        WxUser user = SryxApp.sWxUser;
        LogUtils.logout(user.toString()+avKillerNum.getAmount());
        mSryxModel.createGame(user.getOpenid(),
                user.getHeadimgurl(),
                user.getNickname(),
                avKillerNum.getAmount(),
                avPoliceNum.getAmount(),
                avCitizenNum.getAmount(),
                new SubscriberOnNextListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        mActivity.finish();
                        Intent intent = new Intent(mActivity, PrepareGameActivity.class);
                        intent.putExtra(PrepareGameActivity.KEY_GAME_ID, s);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String msg) {
                    }
                });
    }
}
