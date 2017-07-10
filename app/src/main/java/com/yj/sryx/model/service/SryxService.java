package com.yj.sryx.model.service;

import com.yj.sryx.model.beans.HttpResult;
import com.yj.sryx.model.beans.WxGetTokenRes;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by eason.yang on 2017/7/10.
 */

public interface SryxService {
    @GET("sryx/updateplayer")
    Observable<HttpResult<String>> updatePlayer(
            @Query("playerId") String playerId,
            @Query("avatarUrl") String avatarUrl,
            @Query("nickName") String nickName,
            @Query("gender") Integer gender,
            @Query("language") String language,
            @Query("country") String country,
            @Query("province") String province,
            @Query("city") String city);
}
