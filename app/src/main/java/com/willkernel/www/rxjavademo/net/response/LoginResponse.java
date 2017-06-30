package com.willkernel.www.rxjavademo.net.response;

/**
 * Created by willkernel on 2017/6/27.
 * mail:willkerneljc@gmail.com
 */

public class LoginResponse {
    public String code;
    public String msg;

    @Override
    public String toString() {
        return "LoginResponse{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
