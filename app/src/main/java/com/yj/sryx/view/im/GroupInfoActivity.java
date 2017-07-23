package com.yj.sryx.view.im;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yj.sryx.R;
import com.yj.sryx.common.RecycleViewDivider;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.model.beans.SearchContact;
import com.yj.sryx.view.BaseActivity;
import com.yj.sryx.view.game.PlayerInfoActivity;
import com.yj.sryx.widget.CircleImageView;
import com.yj.sryx.widget.adapterrv.CommonAdapter;
import com.yj.sryx.widget.adapterrv.ViewHolder;

import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.Occupant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GroupInfoActivity extends BaseActivity {
    public static final String EXTRA_ROOM_JID = "room_jid";
    public static final String EXTRA_ROOM_NAME = "room_name";
    @Bind(R.id.acce_toolbar)
    Toolbar acceToolbar;
    @Bind(R.id.iv_up_bg)
    ImageView ivUpBg;
    @Bind(R.id.iv_header)
    CircleImageView ivHeader;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.btn_attention)
    Button btnAttention;
    @Bind(R.id.rv_list_member)
    RecyclerView rvListMember;

    private AsmackModel mAsmackModel;
    private String mRoomJid;
    private String mRoomName;
    private List<Occupant> mAffiliateList;
    private CommonAdapter<Occupant> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        setSupportActionBar(acceToolbar);
        ButterKnife.bind(this);
        initImmersive();
        mRoomJid = getIntent().getExtras().getString(EXTRA_ROOM_JID);
        mRoomName = getIntent().getExtras().getString(EXTRA_ROOM_NAME);
        mAsmackModel = new AsmackModelImpl(this);
        initLayout();
        loadData();
    }

    private void initLayout() {
        ivHeader.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.sryx_logo));
        tvName.setText(mRoomName);
        btnAttention.setText("退出该聊天群");
        setRemoveGroupListener();

        mAffiliateList = new ArrayList<>();
        rvListMember.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mAdapter = new CommonAdapter<Occupant>(this, R.layout.item_list_user_search, mAffiliateList) {
            @Override
            protected void convert(final ViewHolder holder, final Occupant occupant, int position) {
                holder.setText(R.id.tv_name, occupant.getNick());
            }
        };
        rvListMember.setAdapter(mAdapter);
    }


    private void loadData() {
        mAsmackModel.getMembersForGroup(mRoomJid, new SubscriberOnNextListener<List<Occupant>>() {
            @Override
            public void onSuccess(List<Occupant> occupants) {
                mAffiliateList.clear();
                mAffiliateList.addAll(occupants);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String msg) {

            }
        });
    }

    private void setRemoveGroupListener() {
        btnAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAsmackModel.leaveGroup(mRoomJid, new SubscriberOnNextListener<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        btnAttention.setText("加入该聊天群");
                    }

                    @Override
                    public void onError(String msg) {
                    }
                });
            }
        });
    }

    private void initImmersive() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        acceToolbar.setBackgroundColor(Color.TRANSPARENT);
    }

}
