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
import com.yj.sryx.SryxApp;
import com.yj.sryx.view.MainActivity;
import com.yj.sryx.view.SplashActivity;
import com.yj.sryx.manager.LocalUserManager;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.LoginModel;
import com.yj.sryx.model.LoginModelImpl;
import com.yj.sryx.manager.ActivityStackManager;
import com.yj.sryx.model.SryxModel;
import com.yj.sryx.model.SryxModelImpl;
import com.yj.sryx.model.beans.WxUser;
import com.yj.sryx.utils.LogUtils;

import static com.yj.sryx.SryxApp.sWxApi;
import static com.yj.sryx.SryxApp.sWxUser;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private LoginModel mLoginModel;
    private SryxModel mSryxModel;
    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mLoginModel = new LoginModelImpl(this);
        mSryxModel = new SryxModelImpl(this);
        sWxApi.handleIntent(this.getIntent(), this);
        LogUtils.logout("WXEntryActivity");
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
        LogUtils.logout("WXEntryActivity "+resp.errCode+ resp.transaction);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if(resp.transaction == null){ //登录
                    LogUtils.logout("");
                    SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                    //微信登录
                    mLoginModel.wxLogin(sendResp.code, new SubscriberOnNextListener<WxUser>() {
                        @Override
                        public void onSuccess(WxUser result) {
                            sWxUser = result;
                            LocalUserManager.serializeUser(SryxApp.sContext, sWxUser);
                            mActivity.finish();
                            ActivityStackManager.getInstance().finishActivity(SplashActivity.class);
                            Intent gotoMain = new Intent(mActivity, MainActivity.class);
                            mActivity.startActivity(gotoMain);
                            //更新玩家信息
                            mSryxModel.updatePlayer(sWxUser, null);
                        }

                        @Override
                        public void onError(String msg) {}
                    });
                }else {
                    finish();
                }
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
