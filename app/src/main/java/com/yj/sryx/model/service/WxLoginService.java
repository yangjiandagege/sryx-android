package com.yj.sryx.model.service;

import com.yj.sryx.model.beans.WxGetTokenRes;
import com.yj.sryx.model.beans.WxUser;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by eason.yang on 2017/7/9.
 */

public interface WxLoginService {
    @GET("sns/oauth2/access_token")
    Call<WxGetTokenRes> getAccessToken(
            @Query("appid") String appid,
            @Query("secret") String secret,
            @Query("code") String code,
            @Query("grant_type") String grant_type);

    @GET("sns/userinfo")
    Call<WxUser> getWxUserInfo(
            @Query("access_token") String access_token,
            @Query("openid") String openid);
}
