package im.sdk.debug.activity.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import im.sdk.debug.R;
import im.sdk.debug.RegisterAndLoginActivity;

/**
 * Created by ${chenyn} on 16/3/22.
 *
 * @desc :注册界面
 */
public class RegisterActivity extends Activity {

    private EditText mEd_userName;
    private EditText mEd_password;
    private Button   mBt_register;
    private ProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    //注册功能实现
    private void initData() {
        mBt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(RegisterActivity.this, "提示：", "正在加载中。。。");

                final String userName = mEd_userName.getText().toString();
                final String password = mEd_password.getText().toString();
/**=================     调用SDK注册接口    =================*/
                JMessageClient.register(userName, password, new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String registerDesc) {
                        if (responseCode == 0) {
                            mProgressDialog.dismiss();
                            RegisterAndLoginActivity.mEd_userName.setText(userName);
                            RegisterAndLoginActivity.mEd_password.setText(password);
                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                            Log.i("RegisterActivity", "JMessageClient.register " + ", responseCode = " + responseCode + " ; registerDesc = " + registerDesc);
                            Intent intent = new Intent(RegisterActivity.this, RegisterAndLoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                            Log.i("RegisterActivity", "JMessageClient.register " + ", responseCode = " + responseCode + " ; registerDesc = " + registerDesc);
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_register);
        mEd_userName = (EditText) findViewById(R.id.ed_register_username);
        mEd_password = (EditText) findViewById(R.id.ed_register_password);
        mBt_register = (Button) findViewById(R.id.bt_register);
    }
}
