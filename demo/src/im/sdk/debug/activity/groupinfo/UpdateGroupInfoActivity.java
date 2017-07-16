package im.sdk.debug.activity.groupinfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import im.sdk.debug.R;

/**
 * Created by ${chenyn} on 16/3/30.
 *
 * @desc :修改群组信息
 */
public class UpdateGroupInfoActivity extends Activity {

    private EditText mEt_groupId;
    private EditText mEt_groupName;
    private Button   mBt_updateGroupName;
    private ProgressDialog mProgressDialog = null;
    private long     mAddID;
    private EditText mEt_groupDesc;
    private TextView mTv_updateInfo;
    private Button   mBt_updateGroupDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        mBt_updateGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mEt_groupId.getText().toString();
                String name = mEt_groupName.getText().toString();
                if (!TextUtils.isEmpty(id)) {
                    mProgressDialog = ProgressDialog.show(UpdateGroupInfoActivity.this, "提示：", "正在加载中。。。");
                    mProgressDialog.setCanceledOnTouchOutside(true);
                    mAddID = Long.parseLong(id);
                    JMessageClient.updateGroupName(mAddID, name, new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                mTv_updateInfo.setText("");
                                mTv_updateInfo.append("修改群组名成功" + "\n");
                                mProgressDialog.dismiss();
                            } else {
                                mTv_updateInfo.setText("");
                                mTv_updateInfo.append("修改群组名失败" + "responseCode = " + i + " ; Desc = " + s + "\n");
                                Log.i("UpdateGroupInfoActivity", "JMessageClient.updateGroupName " + ", responseCode = " + i + " ; Desc = " + s);
                                mProgressDialog.dismiss();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "输入准备修改的群组id", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBt_updateGroupDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mEt_groupId.getText().toString();
                if (!TextUtils.isEmpty(id)) {
                    mProgressDialog = ProgressDialog.show(UpdateGroupInfoActivity.this, "提示：", "正在加载中。。。");
                    mProgressDialog.setCanceledOnTouchOutside(true);
                    mAddID = Long.parseLong(id);
                    String desc = mEt_groupDesc.getText().toString();
                    JMessageClient.updateGroupDescription(mAddID, desc, new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                mTv_updateInfo.setText("");
                                mProgressDialog.dismiss();
                                mTv_updateInfo.append("修改群描述成功" + "\n");
                            } else {
                                mTv_updateInfo.setText("");
                                mProgressDialog.dismiss();
                                mTv_updateInfo.append("修改群描述失败" + "responseCode = " + i + " ; Desc = " + s);
                                Log.i("UpdateGroupInfoActivity", "JMessageClient.updateGroupDescription " + ", responseCode = " + i + " ; Desc = " + s);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "输入准备修改的群组id", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_update_group_info);

        mEt_groupId = (EditText) findViewById(R.id.et_group_id);
        mEt_groupName = (EditText) findViewById(R.id.et_group_name);
        mBt_updateGroupName = (Button) findViewById(R.id.bt_update_group_name);
        mEt_groupDesc = (EditText) findViewById(R.id.et_group_desc);
        mTv_updateInfo = (TextView) findViewById(R.id.tv_update_info);
        mBt_updateGroupDesc = (Button) findViewById(R.id.bt_update_group_desc);
    }
}
