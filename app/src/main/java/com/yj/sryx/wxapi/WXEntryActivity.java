package com.yj.sryx.wxapi;

/**
 * Created by eason.yang on 2017/7/9.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.yj.sryx.activity.MainActivity;
import com.yj.sryx.activity.SplashActivity;
import com.yj.sryx.model.LoginModel;
import com.yj.sryx.model.LoginModelImpl;
import com.yj.sryx.model.BeanCallback;
import com.yj.sryx.manager.ActivityStackManager;
import com.yj.sryx.utils.LogUtils;

import static com.yj.sryx.SryxApp.sWxApi;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private LoginModel mLoginModel;
    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mLoginModel = new LoginModelImpl();
        sWxApi.handleIntent(this.getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        sWxApi.handleIntent(intent, this);
    }

    //微信发送的请求将回调到onReq方法
    @Override
    public void onReq(BaseReq req) {
        LogUtils.logout("onReq");
    }

    //发送到微信请求的响应结果
    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //发送成功
                SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                mLoginModel.wxLogin(sendResp.code, new BeanCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mActivity.finish();
                        ActivityStackManager.getInstance().finishActivity(SplashActivity.class);
                        Intent gotoMain = new Intent(mActivity, MainActivity.class);
                        mActivity.startActivity(gotoMain);
                    }

                    @Override
                    public void onError(String msg) {
                        mActivity.finish();
                    }
                });
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                LogUtils.logout("ERR_USER_CANCEL");
                //发送取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                LogUtils.logout("ERR_AUTH_DENIED");
                //发送被拒绝
                break;
            default:
                //发送返回
                break;
        }
    }
}
