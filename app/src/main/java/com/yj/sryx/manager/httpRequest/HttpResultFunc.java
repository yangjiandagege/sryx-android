package com.yj.sryx.manager.httpRequest;

import com.yj.sryx.model.beans.HttpResult;

import rx.functions.Func1;
/**
 * Created by eason.yang on 2017/7/10.
 * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
 *
 * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
 */
public class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {
    @Override
    public T call(HttpResult<T> httpResult) {
        if (httpResult.getReturnCode().equals("200")) {
            throw new ApiException(httpResult.getReturnCode(), httpResult.getReturnMsg());
        }
        return httpResult.getResult();
    }
}
