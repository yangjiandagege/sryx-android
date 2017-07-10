package com.yj.sryx.model;

import com.yj.sryx.SryxApp;
import com.yj.sryx.SryxConfig;
import com.yj.sryx.model.beans.WxGetTokenRes;
import com.yj.sryx.model.beans.WxUser;
import com.yj.sryx.manager.LocalUserManager;
import com.yj.sryx.model.service.WxLoginService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private void getAccessToken(String code, final BeanCallback<Void> callback) {
        WxLoginService service = RetrofitSingleton.getInstance().create(WxLoginService.class);
        Call<WxGetTokenRes> call = service.getAccessToken(SryxConfig.WX_APP_ID, SryxConfig.WX_APP_SECRET, code, "authorization_code");
        call.enqueue(new Callback<WxGetTokenRes>() {
            @Override
            public void onResponse(Call<WxGetTokenRes> call, Response<WxGetTokenRes> response) {
                WxGetTokenRes result = response.body();
                getWxUserInfo(result.getAccess_token(), result.getOpenid(), callback);
            }

            @Override
            public void onFailure(Call<WxGetTokenRes> call, Throwable t) {

            }
        });
    }

    public void getWxUserInfo(String access_token, String openid, final BeanCallback<Void> callback) {
        WxLoginService service = RetrofitSingleton.getInstance().create(WxLoginService.class);
        Call<WxUser> call = service.getWxUserInfo(access_token, openid);
        call.enqueue(new Callback<WxUser>() {
            @Override
            public void onResponse(Call<WxUser> call, Response<WxUser> response) {
                sWxUser = response.body();
                LocalUserManager.serializeUser(SryxApp.sContext, sWxUser);
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Call<WxUser> call, Throwable t) {

            }
        });
    }
}
