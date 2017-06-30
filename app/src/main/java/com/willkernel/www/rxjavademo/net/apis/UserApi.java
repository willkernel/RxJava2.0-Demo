package com.willkernel.www.rxjavademo.net.apis;

import com.willkernel.www.rxjavademo.net.request.UserBaseInfoRequest;
import com.willkernel.www.rxjavademo.net.request.UserExtraInfoRequest;
import com.willkernel.www.rxjavademo.net.response.UserBaseInfoResponse;
import com.willkernel.www.rxjavademo.net.response.UserExtraInfoResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;

/**
 * Created by willkernel on 2017/6/28.
 * mail:willkerneljc@gmail.com
 */
public interface UserApi {
    @GET
    Observable<UserBaseInfoResponse> getUserBaseInfo(@Body UserBaseInfoRequest request);

    @GET
    Observable<UserExtraInfoResponse> getUserExtraInfo(@Body UserExtraInfoRequest request);
}
