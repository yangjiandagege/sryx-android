package com.yj.sryx.manager.httpRequest;

import com.yj.sryx.SryxConfig;
import com.yj.sryx.utils.LogUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by eason.yang on 2017/7/10.
 */

public class RetrofitSingleton {
    private static Retrofit sRetrofit;
    private static final int DEFAULT_TIMEOUT = 5;

    public RetrofitSingleton() {}

    public static Retrofit getInstance(){
        return getInstance(SryxConfig.BASE_URL);
    }

    public static Retrofit getWxInstance(){
        return getInstance(SryxConfig.WX_URL);
    }

    public static synchronized Retrofit getInstance(String url){
        if(sRetrofit == null || !sRetrofit.baseUrl().toString().equals(url)){
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    LogUtils.logout(message);
                }
            });
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .build();

            sRetrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return sRetrofit;
    }
}
