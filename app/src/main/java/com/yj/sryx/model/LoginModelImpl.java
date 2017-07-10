package com.yj.sryx.model;

import com.yj.sryx.SryxApp;
import com.yj.sryx.SryxConfig;
import com.yj.sryx.manager.LocalUserManager;
import com.yj.sryx.manager.RetrofitSingleton;
import com.yj.sryx.model.beans.HttpResult;
import com.yj.sryx.model.beans.WxGetTokenRes;
import com.yj.sryx.model.beans.WxUser;
import com.yj.sryx.model.service.SryxService;
import com.yj.sryx.model.service.WxLoginService;
import com.yj.sryx.utils.LogUtils;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.yj.sryx.SryxApp.sWxUser;

/**
 * Created by eason.yang on 2016/10/18.
 */

public class LoginModelImpl implements LoginModel{

    @Override
    public void wxLogin(String code, BeanCallback<Void> callback) {
        getAccessToken(code, callback);
    }

    @Override
    public boolean isAlreadyLogin() {
        return LocalUserManager.isLocalUserExist(SryxApp.sContext);
    }

    public void getAccessToken(String code, final BeanCallback<Void> callback) {
        WxLoginService service = RetrofitSingleton.getWxInstance().create(WxLoginService.class);
        service.getAccessToken(SryxConfig.WX_APP_ID, SryxConfig.WX_APP_SECRET, code, "authorization_code")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WxGetTokenRes>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(WxGetTokenRes result) {
                        getWxUserInfo(result.getAccess_token(), result.getOpenid(), callback);
                    }
                });
    }

    public void getWxUserInfo(String access_token, String openid, final BeanCallback<Void> callback) {
        WxLoginService service = RetrofitSingleton.getWxInstance().create(WxLoginService.class);
        service.getWxUserInfo(access_token, openid)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WxUser>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(WxUser result) {
                        sWxUser = result;
                        LocalUserManager.serializeUser(SryxApp.sContext, sWxUser);
                        updatePlayer(sWxUser, callback);
                        callback.onSuccess(null);
                    }
                });
    }

    public void updatePlayer(WxUser user, final BeanCallback<Void> callback){
        SryxService service = RetrofitSingleton.getInstance().create(SryxService.class);
        service.updatePlayer(user.getOpenid(),user.getHeadimgurl(),
                user.getNickname(), user.getSex(), user.getLanguage(),
                user.getCountry(), user.getProvince(), user.getCity())
                .map(new HttpResultFunc<String>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.logout(e.getMessage());
                    }

                    @Override
                    public void onNext(String result) {
                    }
                });
    }

}
