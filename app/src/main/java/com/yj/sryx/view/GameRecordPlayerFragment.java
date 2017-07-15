package com.yj.sryx.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.model.beans.Role;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by eason.yang on 2017/3/6.
 */

public class GameRecordPlayerFragment extends Fragment {
    @Bind(R.id.hg_record_list)
    StickyGridHeadersGridView hgRecordList;
    private QuizActivity mActivity;

    private SryxModel mSryxModel;
    private GameRecordPlayerStickyGridAdapter mRecordAdapter;
    private List<Role> mRoleList;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLayout();
        getGameRecordList();
    }

    private void getGameRecordList() {
        mSryxModel.getMyGameRecordList(SryxApp.sWxUser.getOpenid(), new SubscriberOnNextListener<List<Role>>() {
            @Override
            public void onSuccess(List<Role> roles) {
                mRoleList.clear();
                for(int i = 0; i < roles.size(); i++){
                    if(roles.get(i).getRoleType() != 3){
                        mRoleList.add(roles.get(i));
                    }
                }
                mRecordAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String msg) {
            }
        });
    }

    private void initLayout() {
        mRoleList = new ArrayList<>();
        mRecordAdapter = new GameRecordPlayerStickyGridAdapter(mActivity, mRoleList);
        hgRecordList.setAdapter(mRecordAdapter);
        hgRecordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mActivity, GameDetailActivity.class);
                intent.putExtra(GameManageActivity.KEY_GAME_ID, mRoleList.get(position).getGameId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
