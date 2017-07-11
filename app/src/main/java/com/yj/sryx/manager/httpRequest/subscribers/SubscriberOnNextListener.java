package com.yj.sryx.manager.httpRequest.subscribers;

/**
 * Created by eason.yang on 2017/7/10.
 */
public interface SubscriberOnNextListener<T> {
    void onSuccess(T t);
    void onError(String msg);
}
