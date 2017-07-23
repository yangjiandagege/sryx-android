package com.yj.sryx.view.im;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.common.RecycleViewDivider;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.utils.ToastUtils;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.widget.AcceBar;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import org.jivesoftware.smackx.disco.packet.DiscoverItems;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by eason.yang on 2017/7/20.
 */

public class GroupChatListActivity extends BaseActivity {
    @Bind(R.id.rv_list_friend)
    RecyclerView rvListFriend;
    @Bind(R.id.toolbar)
    AcceBar toolbar;
    private List<DiscoverItems.Item> mRoomList;
    private CommonAdapter<DiscoverItems.Item> mAdapter;
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
        toolbar.setManagement("新建", new AcceBar.OnManageListener() {
            @Override
            public void OnManageClick() {
                showCreateGroupDialog();
            }
        });

        mRoomList = new ArrayList<>();
        rvListFriend.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mAdapter = new CommonAdapter<DiscoverItems.Item>(this, R.layout.item_list_room, mRoomList) {
            @Override
            protected void convert(final ViewHolder holder, final DiscoverItems.Item room, int position) {
                holder.setText(R.id.tv_name, room.getName());
                holder.setBackgroundRes(R.id.iv_header, R.mipmap.sryx_logo);
                holder.setOnClickListener(R.id.ll_room, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GroupChatListActivity.this, GroupChatActivity.class);
                        intent.putExtra(GroupChatActivity.EXTRA_ROOM_JID, room.getEntityID());
                        intent.putExtra(GroupChatActivity.EXTRA_ROOM_NAME, room.getName());
                        startActivity(intent);
                    }
                });
            }
        };
        rvListFriend.setAdapter(mAdapter);
    }

    private void showCreateGroupDialog() {
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_input_group_name,
                (ViewGroup) findViewById(R.id.dialog));
        final EditText edtGroupName = (EditText) layout.findViewById(R.id.edt_group_name);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.setView(layout)
                .setTitle("填写群名称")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAsmackModel.createRoom(edtGroupName.getText().toString(), "wuhan123", new SubscriberOnNextListener<Integer>() {
                            @Override
                            public void onSuccess(Integer integer) {
                                ToastUtils.showLongToast(GroupChatListActivity.this, "创建成功!");
                                loadData();
                            }

                            @Override
                            public void onError(String msg) {

                            }
                        });
                    }
                }).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


    }

    private void loadData() {
        mAsmackModel.getChatRooms(new SubscriberOnNextListener<List<DiscoverItems.Item>>() {
            @Override
            public void onSuccess(List<DiscoverItems.Item> rooms) {
                mRoomList.clear();
                mRoomList.addAll(rooms);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String msg) {

            }
        });
    }
}
