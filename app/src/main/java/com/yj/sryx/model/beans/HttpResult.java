package com.yj.sryx.model.beans;

/**
 * Created by eason.yang on 2017/7/10.
 */

public class HttpResult<T> {
    private String returnCode;
    private String returnMsg;
    private T result;

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
