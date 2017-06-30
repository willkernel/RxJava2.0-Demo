package com.willkernel.www.rxjavademo.net.response;

/**
 * Created by willkernel on 2017/6/27.
 * mail:willkerneljc@gmail.com
 */

public class RegisterResponse {
    public String code;
    public String msg;

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
