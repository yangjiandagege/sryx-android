package com.yj.sryx.model;

import com.yj.sryx.SryxConfig;
import com.yj.sryx.utils.LogUtils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by eason.yang on 2017/7/10.
 */

public class RetrofitSingleton {
    private static Retrofit sRetrofit;

    public RetrofitSingleton() {}

    public static synchronized Retrofit getInstance(){
        if(sRetrofit == null){
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    LogUtils.logout(message);
                }
            });
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .build();

            sRetrofit = new Retrofit.Builder()
                    .baseUrl(SryxConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return sRetrofit;
    }
}
