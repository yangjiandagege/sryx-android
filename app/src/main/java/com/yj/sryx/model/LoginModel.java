package com.yj.sryx.model;

/**
 * Created by eason.yang on 2017/7/9.
 */

public interface LoginModel {
    void wxLogin(String code, BeanCallback<Void> callback);
    boolean isAlreadyLogin();
}
