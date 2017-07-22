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
import android.widget.TextView;

import com.yj.sryx.R;
import com.yj.sryx.common.DividerItemDecoration;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.model.beans.Contact;
import com.yj.sryx.widget.indexlib.IndexBar.widget.IndexBar;
import com.yj.sryx.widget.indexlib.suspension.SuspensionDecoration;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.RosterPacket;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by  on 2017/7/18.
 */

public class ContactsFragment extends Fragment implements ContactsAdapter.OnItemClickListener{
    private static final String INDEX_STRING_TOP = "↑";

    @Bind(R.id.rv_list_friend)
    RecyclerView rvListFriend;
    @Bind(R.id.indexBar)
    IndexBar indexBar;
    @Bind(R.id.tvSideBarHint)
    TextView tvSideBarHint;
    private Activity mActivity;
    private AsmackModel mAsmackModel;
    private List<Contact> mContactList;
    private ContactsAdapter mAdapter;
    private SuspensionDecoration mDecoration;
    private LinearLayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, view);
        mActivity = getActivity();
        mAsmackModel = new AsmackModelImpl(mActivity);
        mContactList = new ArrayList<>();
//        mContactList.add((Contact)new Contact("新的朋友", "新的朋友", true).setBaseIndexTag(INDEX_STRING_TOP));
        mContactList.add((Contact)new Contact("群聊", "群聊", true).setBaseIndexTag(INDEX_STRING_TOP));
        initLayout();
        getAllEntries();
        return view;
    }

    private void getAllEntries() {
        mAsmackModel.getAllRosterEntries(new SubscriberOnNextListener<List<RosterEntry>>() {
            @Override
            public void onSuccess(List<RosterEntry> rosterEntries) {
                for (int i = 0; i < rosterEntries.size(); i++) {
                    Contact contact = new Contact(rosterEntries.get(i).getUser(), rosterEntries.get(i).getName(), false);
                    mContactList.add(contact);
                }

                indexBar.setmSourceDatas(mContactList)//设置数据
                        .invalidate();
                mDecoration.setmDatas(mContactList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String msg) {

            }
        });
    }

    private void initLayout() {
        rvListFriend.setLayoutManager(mLayoutManager = new LinearLayoutManager(mActivity));
        mAdapter = new ContactsAdapter(mActivity, mContactList);
        mAdapter.setOnItemClickListener(this);
        rvListFriend.setAdapter(mAdapter);
        rvListFriend.addItemDecoration(mDecoration = new SuspensionDecoration(mActivity, mContactList));
        rvListFriend.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL_LIST));

        //indexbar初始化
        indexBar.setmPressedShowTextView(tvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(mLayoutManager);//设置RecyclerView的LayoutManager
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void OnNewFriendsClick() {
        startActivity(new Intent(mActivity, NewFriendsActivity.class));
    }

    @Override
    public void OnGroupChatClick() {

    }

    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_USER, mContactList.get(position).getUser());
        intent.putExtra(ChatActivity.EXTRA_NAME, mContactList.get(position).getName());
        startActivity(intent);
    }
}
