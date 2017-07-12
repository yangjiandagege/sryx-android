package com.yj.sryx.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yj.sryx.R;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.model.beans.Role;
import com.yj.sryx.utils.ToastUtils;
import com.yj.sryx.widget.MultiEditText;
import com.yj.sryx.widget.virtualKeyboard.OnTextInputListener;
import com.yj.sryx.widget.virtualKeyboard.VirtualKeyboardView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yj.sryx.SryxApp.sWxUser;

/**
 * Created by eason.yang on 2017/3/6.
 */

public class JoinGameFragment extends Fragment {
    @Bind(R.id.edt_game_code)
    MultiEditText edtGameCode;
    @Bind(R.id.vkv_input_sms)
    VirtualKeyboardView vkvInputSms;
    @Bind(R.id.tv_scan)
    TextView tvScan;
    private QuizActivity mActivity;

    private SryxModel mSryxModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_game, container, false);
        ButterKnife.bind(this, view);
        mActivity = (QuizActivity) getActivity();
        mSryxModel = new SryxModelImpl(mActivity);
        vkvInputSms.bindKeyboard(edtGameCode, mActivity.getWindow(), new OnTextInputListener() {
            @Override
            public void onTextInput(String content) {
                if (content.length() == 4) {
                    ToastUtils.showLongToast(mActivity, "ok");
                    joinGameByCode(content);
                }
            }
        });
        return view;
    }

    private void joinGameByCode(String gameCode) {
        mSryxModel.joinGameByCode(gameCode, sWxUser.getOpenid(), sWxUser.getNickname(), sWxUser.getHeadimgurl(), new SubscriberOnNextListener<String>() {
            @Override
            public void onSuccess(String roles) {

            }

            @Override
            public void onError(String msg) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.tv_scan)
    public void onClick() {
        startActivity(new Intent(mActivity, QrCodeScanActivity.class));
    }
}
