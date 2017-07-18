package com.yj.sryx.view.im;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.yj.sryx.R;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.model.beans.Contact;
import com.yj.sryx.utils.LogUtils;
import com.yj.sryx.utils.ToastUtils;
import com.yj.sryx.widget.SearchEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchFriendActivity extends AppCompatActivity {
    @Bind(R.id.edt_search_friend)
    SearchEditText edtSearchFriend;
    @Bind(R.id.test)
    ImageView test;

    private AsmackModel mAsmackModel;
    private List<Contact> mContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        ButterKnife.bind(this);
        mAsmackModel = new AsmackModelImpl(this);
        mContactList = new ArrayList<>();
        edtSearchFriend.setOnSearchClickListener(new SearchEditText.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view, String keyword) {
                mAsmackModel.searchFriends(keyword, new SubscriberOnNextListener<List<Contact>>() {
                    @Override
                    public void onSuccess(List<Contact> contacts) {
                        ToastUtils.showLongToast(SearchFriendActivity.this, "搜到：" + contacts.size());
                        if(contacts.get(0).getAvatar() != null){
                            LogUtils.logout("");
                            test.setImageDrawable(contacts.get(0).getAvatar());
                        }

                        mContactList.addAll(contacts);
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
