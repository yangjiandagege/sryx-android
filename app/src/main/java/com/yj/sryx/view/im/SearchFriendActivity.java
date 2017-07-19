package com.yj.sryx.view.im;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yj.sryx.R;
import com.yj.sryx.common.RecycleViewDivider;
import com.yj.sryx.manager.XmppConnSingleton;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.model.beans.Contact;
import com.yj.sryx.utils.ToastUtils;
import com.yj.sryx.widget.SearchEditText;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchFriendActivity extends AppCompatActivity {
    @Bind(R.id.edt_search_friend)
    SearchEditText edtSearchFriend;
    @Bind(R.id.rv_list_friend)
    RecyclerView rvListFriend;

    private AsmackModel mAsmackModel;
    private List<Contact> mContactList;
    private CommonAdapter<Contact> mAdapter;
    private XMPPConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        ButterKnife.bind(this);
        mAsmackModel = new AsmackModelImpl(this);
        mConnection = XmppConnSingleton.getInstance();
        initLayout();
    }

    private void initLayout() {
        mContactList = new ArrayList<>();
        rvListFriend.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mAdapter = new CommonAdapter<Contact>(this, R.layout.item_list_user_search, mContactList) {
            @Override
            protected void convert(final ViewHolder holder, final Contact contact, int position) {
                if (contact.getAvatar() != null) {
                    holder.setImageDrawable(R.id.iv_header, contact.getAvatar());
                } else {
                    holder.setImageResource(R.id.iv_header, R.mipmap.header_pic);
                }
                holder.setText(R.id.tv_name, contact.getName());
                holder.setText(R.id.tv_info, contact.getSex()+" "+contact.getProvince()+" "+contact.getCity());
                holder.setOnClickListener(R.id.tv_add_friend, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAsmackModel.addFriend(contact.getAccount(), contact.getName(), new SubscriberOnNextListener<Integer>() {
                            @Override
                            public void onSuccess(Integer integer) {
                                ToastUtils.showLongToast(SearchFriendActivity.this, "发送添加好友申请成功！");
                                holder.setText(R.id.tv_add_friend, "已发送申请");
                            }

                            @Override
                            public void onError(String msg) {
                                ToastUtils.showLongToast(SearchFriendActivity.this, "发送添加好友申请失败！");
                            }
                        });
                    }
                });
            }
        };
        rvListFriend.setAdapter(mAdapter);

        edtSearchFriend.setOnSearchClickListener(new SearchEditText.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view, String keyword) {
                mAsmackModel.searchFriends(keyword, new SubscriberOnNextListener<List<Contact>>() {
                    @Override
                    public void onSuccess(List<Contact> contacts) {
                        mContactList.clear();
                        mContactList.addAll(contacts);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String msg) {
                        ToastUtils.showLongToast(SearchFriendActivity.this, msg);
                    }
                });
            }
        });
    }


}
