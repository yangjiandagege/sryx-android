package com.yj.sryx.view.im;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yj.sryx.R;
import com.yj.sryx.common.RecycleViewDivider;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.model.beans.SearchContact;
import com.yj.sryx.utils.ToastUtils;
import com.yj.sryx.view.game.PlayerInfoActivity;
import com.yj.sryx.widget.SearchEditText;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GroupMembersActivity extends AppCompatActivity {
    @Bind(R.id.edt_search_friend)
    SearchEditText edtSearchFriend;
    @Bind(R.id.rv_list_friend)
    RecyclerView rvListFriend;

    private AsmackModel mAsmackModel;
    private List<SearchContact> mContactList;
    private CommonAdapter<SearchContact> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        ButterKnife.bind(this);
        mAsmackModel = new AsmackModelImpl(this);
        initLayout();
    }

    private void initLayout() {
        mContactList = new ArrayList<>();
        rvListFriend.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mAdapter = new CommonAdapter<SearchContact>(this, R.layout.item_list_user_search, mContactList) {
            @Override
            protected void convert(final ViewHolder holder, final SearchContact contact, int position) {
                if (contact.getAvatar() != null) {
                    holder.setImageDrawable(R.id.iv_header, contact.getAvatar());
                } else {
                    holder.setImageResource(R.id.iv_header, R.mipmap.header_pic);
                }
                holder.setText(R.id.tv_name, contact.getName());
                holder.setText(R.id.tv_info, contact.getSex());
                holder.setOnClickListener(R.id.ll_contact, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GroupMembersActivity.this, PlayerInfoActivity.class);
                        intent.putExtra(PlayerInfoActivity.EXTRAS_PLAYER_ID, contact.getAccount().split("@")[0]);
                        startActivity(intent);
                    }
                });
            }
        };
        rvListFriend.setAdapter(mAdapter);

        edtSearchFriend.setOnSearchClickListener(new SearchEditText.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view, String keyword) {
                mAsmackModel.searchFriends(keyword, new SubscriberOnNextListener<List<SearchContact>>() {
                    @Override
                    public void onSuccess(List<SearchContact> contacts) {
                        mContactList.clear();
                        mContactList.addAll(contacts);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String msg) {
                        ToastUtils.showLongToast(GroupMembersActivity.this, msg);
                    }
                });
            }
        });
    }


}
