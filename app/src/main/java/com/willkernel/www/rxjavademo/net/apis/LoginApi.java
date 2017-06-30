package com.willkernel.www.rxjavademo.net.apis;

import com.willkernel.www.rxjavademo.net.request.LoginRequest;
import com.willkernel.www.rxjavademo.net.request.RegisterRequest;
import com.willkernel.www.rxjavademo.net.response.LoginResponse;
import com.willkernel.www.rxjavademo.net.response.RegisterResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by willkernel on 2017/6/27.
 * mail:willkerneljc@gmail.com
 */

public interface LoginApi {
    @POST("index.php?s=Api/App/push_message")
    Observable<LoginResponse> login(@Body LoginRequest request);

    @POST("index.php?s=Api/App/push_message")
    Observable<RegisterResponse> register(@Body RegisterRequest request);
}