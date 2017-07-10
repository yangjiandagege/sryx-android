package com.yj.sryx.model;

/**
 * Created by eason.yang on 2017/7/10.
 */

public class ApiException extends RuntimeException {

//    public static final int USER_NOT_EXIST = 100;
//    public static final int WRONG_PASSWORD = 101;

    public ApiException(String resultCode, String detailMessage) {
        super(getApiExceptionMessage(resultCode, detailMessage));
    }

    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     * @param code
     * @return
     */
    private static String getApiExceptionMessage(String code, String detailMessage){
        String message = "";
        switch (code) {
            case "201":
                message = "该用户不存在";
                break;
            case "202":
                message = "密码错误";
                break;
            default:
                if(detailMessage != null){
                    message = detailMessage;
                }else {
                    message = "未知错误";
                }
        }
        return message;
    }
}
