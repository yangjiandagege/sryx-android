package com.yj.sryx.view.im;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.yj.sryx.R;
import com.yj.sryx.common.RecycleViewDivider;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.model.beans.Contact;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.RosterPacket;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by eason.yang on 2017/7/20.
 */

public class NewFriendsActivity extends BaseActivity {
    @Bind(R.id.rv_list_friend)
    RecyclerView rvListFriend;
    private List<Contact> mContactList;
    private CommonAdapter<Contact> mAdapter;
    private AsmackModel mAsmackModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends);
        ButterKnife.bind(this);
        mAsmackModel = new AsmackModelImpl(this);
        initLayout();
        loadData();
    }

    private void initLayout() {
        mContactList = new ArrayList<>();
        rvListFriend.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mAdapter = new CommonAdapter<Contact>(this, R.layout.item_list_contact, mContactList) {
            @Override
            protected void convert(final ViewHolder holder, final Contact contact, int position) {
                holder.setText(R.id.tv_name, contact.getName());
                mAsmackModel.getHeaderPic(contact.getUser(), new HeaderPicSetListener(position, (ImageView) holder.getView(R.id.iv_header)));
            }
        };
        rvListFriend.setAdapter(mAdapter);
    }

    private void loadData() {
        mAsmackModel.getAllRosterEntries(new SubscriberOnNextListener<List<RosterEntry>>() {
            @Override
            public void onSuccess(List<RosterEntry> rosterEntries) {
                for (int i = 0; i < rosterEntries.size(); i++) {
                    Contact contact = new Contact(rosterEntries.get(i).getUser(), rosterEntries.get(i).getName(), false);
                    if(rosterEntries.get(i).getType() != RosterPacket.ItemType.both) {
                        mContactList.add(contact);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String msg) {

            }
        });
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
}
