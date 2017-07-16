package im.sdk.debug.activity.groupinfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.CreateGroupCallback;
import im.sdk.debug.R;

/**
 * Created by ${chenyn} on 16/3/29.
 *
 * @desc :创建群组
 */
public class CreateGroupActivity extends Activity {

    private Button         mBt_create;
    private EditText       mEt_groupDesc;
    private EditText       mEt_groupName;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        mBt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(CreateGroupActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                String name = mEt_groupName.getText().toString();
                String desc = mEt_groupDesc.getText().toString();
//创建群组
                JMessageClient.createGroup(name, desc, new CreateGroupCallback() {
                    @Override
                    public void gotResult(int i, String s, long l) {
                        if (i == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "创建成功", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "创建失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_create_group);

        mEt_groupName = (EditText) findViewById(R.id.et_group_name);
        mEt_groupDesc = (EditText) findViewById(R.id.et_group_desc);

        mBt_create = (Button) findViewById(R.id.bt_create_group);
    }
}
