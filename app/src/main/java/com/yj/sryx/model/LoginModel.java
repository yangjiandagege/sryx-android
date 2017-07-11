package com.yj.sryx.model;

import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.beans.WxUser;

/**
 * Created by eason.yang on 2017/7/9.
 */

public interface LoginModel {
    void wxLogin(String code, SubscriberOnNextListener<WxUser> callback);
    boolean isAlreadyLogin();
    void exitWxLogin();
}
