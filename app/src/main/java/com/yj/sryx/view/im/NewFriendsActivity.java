package com.yj.sryx.view.im;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yj.sryx.R;
import com.yj.sryx.common.RecycleViewDivider;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.utils.FormatTools;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.ByteArrayInputStream;
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
    private List<RosterEntry> mContactList;
    private CommonAdapter<RosterEntry> mAdapter;
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
        mAdapter = new CommonAdapter<RosterEntry>(this, R.layout.item_list_user_search, mContactList) {
            @Override
            protected void convert(final ViewHolder holder, final RosterEntry entry, int position) {
                mAsmackModel.getVCard(entry.getUser(), new SubscriberOnNextListener<VCard>() {
                    @Override
                    public void onSuccess(final VCard vCard) {
                        holder.setText(R.id.tv_name, vCard.getNickName());
                        holder.setImageDrawable(R.id.iv_header, FormatTools.getInstance().InputStream2Drawable(new ByteArrayInputStream(vCard.getAvatar())));
                        holder.setText(R.id.tv_info, vCard.getField("sex"));
                        holder.setText(R.id.tv_add_friend, "接受请求");
                        holder.setOnClickListener(R.id.tv_add_friend, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAsmackModel.addFriend(entry.getUser(), vCard.getNickName(), new SubscriberOnNextListener<Integer>() {
                                    @Override
                                    public void onSuccess(Integer integer) {
                                        holder.setText(R.id.tv_add_friend, "已添加");
                                    }

                                    @Override
                                    public void onError(String msg) {

                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(String msg) {
                    }
                });
            }
        };
        rvListFriend.setAdapter(mAdapter);
    }

    private void loadData() {
        mAsmackModel.getAllRosterEntries(new SubscriberOnNextListener<List<RosterEntry>>() {
            @Override
            public void onSuccess(List<RosterEntry> rosterEntries) {
                for (int i = 0; i < rosterEntries.size(); i++) {
                    if(rosterEntries.get(i).getType() == RosterPacket.ItemType.from) {
                        mContactList.add(rosterEntries.get(i));
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
