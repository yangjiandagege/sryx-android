package com.yj.sryx.view.im;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.common.RecycleViewDivider;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.model.beans.ChatMessage;
import com.yj.sryx.model.beans.ChatSession;
import com.yj.sryx.model.beans.ChatSessionDao;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import org.jivesoftware.smack.RosterEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by  on 2017/7/18.
 */

public class SessionsFragment extends Fragment {
    @Bind(R.id.rv_list_session)
    RecyclerView rvListSession;
    private Activity mActivity;
    private List<ChatSession> mChatSessionList;
    private CommonAdapter<ChatSession> mAdapter;
    private AsmackModel mAsmackModel;
    private ChatSessionDao mChatSessionDao;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sessions, container, false);
        ButterKnife.bind(this, view);
        mActivity = getActivity();
        mAsmackModel = new AsmackModelImpl(mActivity);
        mChatSessionDao = SryxApp.sDaoSession.getChatSessionDao();
        mChatSessionList = new ArrayList<>();
        initLayout();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(null != mChatSessionDao.loadAll()) {
                    mChatSessionList.clear();
                    mChatSessionList.addAll(mChatSessionDao.loadAll());
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }

    private void initLayout() {
        rvListSession.addItemDecoration(new RecycleViewDivider(mActivity, LinearLayoutManager.HORIZONTAL));
        mAdapter = new CommonAdapter<ChatSession>(mActivity, R.layout.item_list_session, mChatSessionList) {
            @Override
            protected void convert(final ViewHolder holder, final ChatSession chatSession, int position) {
                holder.setText(R.id.tv_name, chatSession.getSessionName());
                holder.setText(R.id.tv_msg, chatSession.getLastBody());
                mAsmackModel.getHeaderPic(chatSession.getSessionId(), new HeaderPicSetListener(position, (ImageView) holder.getView(R.id.iv_header)));
                holder.setOnClickListener(R.id.ll_contact, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, ChatActivity.class);
                        intent.putExtra(ChatActivity.EXTRA_USER, chatSession.getSessionId());
                        intent.putExtra(ChatActivity.EXTRA_NAME, chatSession.getSessionName());
                        startActivity(intent);
                    }
                });
            }
        };
        rvListSession.setAdapter(mAdapter);
    }

    class HeaderPicSetListener implements SubscriberOnNextListener<Drawable> {
        int position;
        ImageView imageHeaderPic;

        public HeaderPicSetListener(int position, ImageView imageView) {
            this.position = position;
            this.imageHeaderPic = imageView;
        }

        @Override
        public void onSuccess(Drawable drawable) {
            imageHeaderPic.setImageDrawable(drawable);
        }

        @Override
        public void onError(String msg) {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
