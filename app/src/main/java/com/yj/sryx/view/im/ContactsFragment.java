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
import com.yj.sryx.common.RecycleViewDivider;
import com.yj.sryx.manager.XmppConnSingleton;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by  on 2017/7/18.
 */

public class ContactsFragment extends Fragment {
    @Bind(R.id.rv_list_friend)
    RecyclerView rvListFriend;
    private Activity mActivity;
    private AsmackModel mAsmackModel;
    private List<RosterEntry> mRosterEntryList;
    private CommonAdapter<RosterEntry> mAdapter;
    private XMPPConnection mConnection;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, view);
        mActivity = getActivity();
        mAsmackModel = new AsmackModelImpl(mActivity);
        mConnection = XmppConnSingleton.getInstance();
        mRosterEntryList = new ArrayList<>();
        initLayout();
        getAllEntries();
        return view;
    }

    private void getAllEntries() {
        mAsmackModel.getAllRosterEntries(new SubscriberOnNextListener<List<RosterEntry>>() {
            @Override
            public void onSuccess(List<RosterEntry> rosterEntries) {
                mRosterEntryList.clear();
                mRosterEntryList.addAll(rosterEntries);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String msg) {

            }
        });
    }

    private void initLayout() {
        rvListFriend.addItemDecoration(new RecycleViewDivider(mActivity, LinearLayoutManager.HORIZONTAL));
        mAdapter = new CommonAdapter<RosterEntry>(mActivity, R.layout.item_list_contact, mRosterEntryList) {
            @Override
            protected void convert(final ViewHolder holder, final RosterEntry entry, int position) {
                holder.setText(R.id.tv_name, entry.getName());
                mAsmackModel.getHeaderPic(entry.getUser(), new HeaderPicSetListener(position, (ImageView) holder.getView(R.id.iv_header)));
                holder.setOnClickListener(R.id.ll_contact, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, ChatActivity.class);
                        intent.putExtra(ChatActivity.EXTRA_USER, entry.getUser());
                        intent.putExtra(ChatActivity.EXTRA_NAME, entry.getName());
                        startActivity(intent);
                    }
                });
            }
        };
        rvListFriend.setAdapter(mAdapter);
    }

    class HeaderPicSetListener implements SubscriberOnNextListener<Drawable>{
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
