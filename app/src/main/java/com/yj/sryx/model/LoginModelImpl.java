package com.yj.sryx.model;

import android.content.Context;

import com.yj.sryx.SryxApp;
import com.yj.sryx.SryxConfig;
import com.yj.sryx.manager.LocalUserManager;
import com.yj.sryx.manager.httpRequest.RetrofitSingleton;
import com.yj.sryx.manager.httpRequest.BeanCallback;
import com.yj.sryx.manager.httpRequest.HttpResultFunc;
import com.yj.sryx.manager.httpRequest.subscribers.ProgressSubscriber;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.beans.WxGetTokenRes;
import com.yj.sryx.model.beans.WxUser;
import com.yj.sryx.model.service.SryxService;
import com.yj.sryx.model.service.WxLoginService;
import com.yj.sryx.utils.LogUtils;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.yj.sryx.SryxApp.sWxUser;

/**
 * Created by eason.yang on 2016/10/18.
 */

public class LoginModelImpl implements LoginModel{
    private Context mContext;

    public LoginModelImpl(Context context) {
        mContext = context;
    }

    @Override
    public void wxLogin(String code, final SubscriberOnNextListener<WxUser> callback) {
        final WxLoginService service = RetrofitSingleton.getWxInstance().create(WxLoginService.class);
        service.getAccessToken(SryxConfig.WX_APP_ID, SryxConfig.WX_APP_SECRET, code, "authorization_code")
                .flatMap(new Func1<WxGetTokenRes, Observable<WxUser>>() {
                    @Override
                    public Observable<WxUser> call(WxGetTokenRes result) {
                        return service.getWxUserInfo(result.getAccess_token(), result.getOpenid());
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressSubscriber(callback, mContext));
    }

    @Override
    public boolean isAlreadyLogin() {
        return LocalUserManager.isLocalUserExist(mContext);
    }

    @Override
    public void exitWxLogin() {
        LocalUserManager.removeLocalUser(mContext);
    }
}
