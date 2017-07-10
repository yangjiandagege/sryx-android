package com.yj.sryx.model;

/**
 * Created by eason.yang on 2017/7/10.
 */

public interface BeanCallback<T> {
    void onSuccess(T t);
    void onError(String msg);
}
