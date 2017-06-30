package com.willkernel.www.rxjavademo.net.http;

import android.content.Context;
import android.support.compat.BuildConfig;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.willkernel.www.rxjavademo.bean.UserInfo;
import com.willkernel.www.rxjavademo.net.apis.LoginApi;
import com.willkernel.www.rxjavademo.net.apis.UserApi;
import com.willkernel.www.rxjavademo.net.request.LoginRequest;
import com.willkernel.www.rxjavademo.net.request.RegisterRequest;
import com.willkernel.www.rxjavademo.net.request.UserBaseInfoRequest;
import com.willkernel.www.rxjavademo.net.request.UserExtraInfoRequest;
import com.willkernel.www.rxjavademo.net.response.LoginResponse;
import com.willkernel.www.rxjavademo.net.response.RegisterResponse;
import com.willkernel.www.rxjavademo.net.response.UserBaseInfoResponse;
import com.willkernel.www.rxjavademo.net.response.UserExtraInfoResponse;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by willkernel on 2017/6/27.
 * mail:willkerneljc@gmail.com
 */

public final class RetrofitHelper {
    private static final String TAG = "RetrofitHelper";
    private LoginApi loginApi;
    private UserApi userApi;
    private static RetrofitHelper retrofitHelper;
    private OkHttpClient okHttpClient;
    private CompositeDisposable compositeDisposable;

    public RetrofitHelper() {
        initOkHttp();
        compositeDisposable = new CompositeDisposable();
        loginApi = getApiService("http://a.ypt5566.com/", LoginApi.class);
        userApi = getApiService("http://a.ypt5566.com/", UserApi.class);
    }

    private void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        okHttpClient = builder.build();
    }

    private <T> T getApiService(String baseUrl, Class<T> clz) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(clz);
    }

    public void login(LoginRequest loginRequest, final Context context) {
        loginApi.login(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(LoginResponse value) {
                        Log.e(TAG, "value=" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                        Toast.makeText(context, "onComplete", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void register(RegisterRequest registerRequest, final Context context) {
        loginApi.register(registerRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<RegisterResponse>() {
                    @Override
                    public void accept(RegisterResponse registerResponse) throws Exception {
                        Toast.makeText(context, registerResponse.toString(), Toast.LENGTH_LONG).show();
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<RegisterResponse, ObservableSource<LoginResponse>>() {
                    @Override
                    public ObservableSource<LoginResponse> apply(RegisterResponse registerResponse) throws Exception {
                        if (registerResponse.code.equals("success")) {
                            return loginApi.login(new LoginRequest());
                        } else {
                            return null;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LoginResponse>() {
                    @Override
                    public void accept(LoginResponse loginResponse) throws Exception {
                        Toast.makeText(context, loginResponse.toString(), Toast.LENGTH_LONG).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
        ;
    }

    public void fetchUserInfo(UserBaseInfoRequest baseInfoRequest, UserExtraInfoRequest extraInfoRequest) {
        Observable<UserBaseInfoResponse> observable1 = userApi.getUserBaseInfo(baseInfoRequest).subscribeOn(Schedulers.io());
        Observable<UserExtraInfoResponse> observable2 = userApi.getUserExtraInfo(extraInfoRequest).subscribeOn(Schedulers.io());
        Observable.zip(observable1, observable2, new BiFunction<UserBaseInfoResponse, UserExtraInfoResponse, UserInfo>() {
            @Override
            public UserInfo apply(UserBaseInfoResponse userBaseInfoResponse, UserExtraInfoResponse userExtraInfoResponse) throws Exception {
                return new UserInfo(userBaseInfoResponse, userExtraInfoResponse);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    public void clear() {
        Log.e(TAG, "clear()");
        compositeDisposable.clear();
    }
}