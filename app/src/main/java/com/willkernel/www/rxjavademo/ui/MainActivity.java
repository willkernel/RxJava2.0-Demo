package com.willkernel.www.rxjavademo.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.willkernel.www.rxjavademo.R;
import com.willkernel.www.rxjavademo.net.http.RetrofitHelper;
import com.willkernel.www.rxjavademo.net.request.LoginRequest;
import com.willkernel.www.rxjavademo.net.request.RegisterRequest;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 10;
    private RetrofitHelper retrofitHelper = new RetrofitHelper();
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        text = findViewById(R.id.text);
//        test1();
//        test2();
//        test3();
//        test4();
//        test5();
//        test6();
//        test7();
//        test8();
//        test9();
//        test10();
//        test11();
//        test12();
//        test13();
//        test14();
//        test15();
//        test16();
//        test17();
//        test18();
//        test19();
//        test20();
//        test21();
//        test22();
//        test23();
//        test24();
//        test25();
//        test26();
//        test27();
//        test28();
//        test29();
//        test30();
//        test31();
//        test32();
        test33();

    }

    /**
     * 例子是读取一个文本文件，需要一行一行读取，然后处理并输出，如果文本文件很大的时候，
     * 比如几十M的时候，全部先读入内存肯定不是明智的做法，因此我们可以一边读取一边处理，实现的代码如下
     */
    private void test33() {
        Flowable.create((FlowableOnSubscribe<String>) e -> {
            try {
//                    URL url=getClass().getResource("test.txt");
//                    Log.e(TAG,"url="+url);
//                    FileReader reader =new FileReader(url.getFile());
//                    FileReader reader =new FileReader("file:///android_asset/test.txt");
                InputStream inputStream = getResources().getAssets().open("test.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String str;
                while ((str = bufferedReader.readLine()) != null && !e.isCancelled()) {
                    while (e.requested() == 0) {
                        if (e.isCancelled()) break;
                    }
                    e.onNext(str);
                }
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
                e.onComplete();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<String>() {
                    Subscription mSubscription;

                    @Override
                    public void onSubscribe(Subscription s) {
                        mSubscription = s;
                        s.request(1);
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "onNext=" + s);
                        try {
                            Thread.sleep(2000);
                            mSubscription.request(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println(t);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void test32() {
        Flowable.create((FlowableOnSubscribe<Integer>) emitter -> {
            Log.d(TAG, "First requested = " + emitter.requested());
            boolean flag;
            for (int i = 0; ; i++) {
                flag = false;
                while (emitter.requested() == 0) {
                    if (!flag) {
                        Log.d(TAG, "Oh no! I can't emit value!");
                        flag = true;
                    }
                }
                emitter.onNext(i);
                Log.d(TAG, "emit " + i + " , requested = " + emitter.requested());
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(final Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        text.postDelayed(() -> {
                            //在处理完96个事件后，上游可以继续发送emit 128-223 共96个
                            //95s时，上游不发送事件
                            s.request(95);
                        }, 1000);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * 当上下游工作在不同的线程里时，每一个线程里都有一个requested，
     * 而我们调用request（1000）时，实际上改变的是下游主线程中的requested，
     * 而上游中的requested的值是由RxJava内部调用request(n)去设置的，这个调用会在合适的时候自动触发
     */
    private void test31() {
        Flowable
                .create((FlowableOnSubscribe<Integer>) emitter -> {
                    Log.d(TAG, "current requested: " + emitter.requested());//还是128
                }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        s.request(1000);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * 下游调用request(n) 告诉上游它的处理能力，上游每发送一个next事件之后，requested就减一，注意是next事件，complete和error事件不会消耗requested，
     * 当减到0时，则代表下游没有处理能力了，这个时候你如果继续发送事件，会发生什么后果呢？当然是MissingBackpressureException啦
     * 当上下游在同一个线程中的时候，在下游调用request(n)就会直接改变上游中的requested的值，多次调用便会叠加这个值，
     * 而上游每发送一个事件之后便会去减少这个值，当这个值减少至0的时候，继续发送事件便会抛异常了
     */
    private void test30() {
        Flowable.create((FlowableOnSubscribe<Integer>) e -> {
            Log.e(TAG, "current request=" + e.requested());
            Log.e(TAG, "emitter 1");
            e.onNext(1);
            Log.e(TAG, "current request=" + e.requested());

            Log.e(TAG, "emitter 2");
            e.onNext(2);
            Log.e(TAG, "current request=" + e.requested());

            Log.e(TAG, "emitter 3");
            e.onNext(3);
            Log.e(TAG, "current request=" + e.requested());

            e.onComplete();
        }, BackpressureStrategy.ERROR)
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.e(TAG, "onSubscribe");
                        s.request(10);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });
    }

    /**
     * 在上游中打印出当前的request数量，下游什么也不做,current request=0
     */
    private void test29() {
        Flowable.create((FlowableOnSubscribe<Integer>) e ->
                        Log.e(TAG, "current request=" + e.requested()),
                BackpressureStrategy.ERROR)
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.e(TAG, "onSubscribe");
                        s.request(10);
                        s.request(100);// current request 110;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });
    }

    /**
     * interval操作符发送Long型的事件, 从0开始, 每隔指定的时间就把数字加1并发送出来,
     * 在这个例子里, 我们让它每隔1毫秒就发送一次事件, 在下游延时1秒去接收处理,报错  io.reactivex.exceptions.MissingBackpressureException: Can't deliver value 128 due to lack of requests
     * 虽然不是我们自己创建的interval , 但是RxJava给我们提供了其他的方法:
     * <p>
     * onBackpressureBuffer()
     * onBackpressureDrop()
     * onBackpressureLatest()
     */
    private void test28() {
        Flowable.interval(1, TimeUnit.MICROSECONDS)
//                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e(TAG, "onNext: " + aLong);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * Latest 获取到最后最新的事件
     */
    private void test27() {
        Flowable.create((FlowableOnSubscribe<Integer>) e -> {
            for (int i = 0; ; i++) {
                e.onNext(i);
            }
        }, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    Subscription mSubscription;
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            mSubscription.request(128);
                            text.postDelayed(runnable, 2000);
                        }
                    };

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        text.postDelayed(runnable, 2000);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * FLowable换一个大水缸还有没有其他的办法呢, 因为更大的水缸也只是缓兵之计啊, 动不动就OOM给你看.
     * 想想看我们之前学习Observable的时候说到的如何解决上游发送事件太快的, 有一招叫从数量上取胜, 同样的FLowable中也有这种方法, 对应的就是BackpressureStrategy.DROP和BackpressureStrategy.LATEST这两种策略.
     * 从名字上就能猜到它俩是干啥的, Drop就是直接把存不下的事件丢弃,Latest就是只保留最新的事件
     * <p>
     * Drop
     * 为什么request(128)呢, 因为之前不是已经说了吗, FLowable内部的默认的水缸大小为128,
     * 因此, 它刚开始肯定会把0-127这128个事件保存起来, 然后丢弃掉其余的事件,
     * 当我们request(128)的时候,下游便会处理掉这128个事件, 那么上游水缸中又会重新装进新的128个事件, 以此类推
     */
    private void test26() {
        Flowable.create((FlowableOnSubscribe<Integer>) e -> {
            for (int i = 0; ; i++) {
                e.onNext(i);
            }
        }, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    Subscription mSubscription;
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            mSubscription.request(128);
                            text.postDelayed(runnable, 2000);
                        }
                    };

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        text.postDelayed(runnable, 2000);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * 上游无限发送事件
     * 可能有朋友也注意到了, 之前使用Observable测试的时候内存增长非常迅速, 几秒钟就OOM,
     * 但这里增长速度却比较缓慢, 可以翻回去看之前的文章中的GIF图进行对比, 这也看出FLowable相比Observable,
     * 在性能方面有些不足, 毕竟FLowable内部为了实现响应式拉取做了更多的操作, 性能有所丢失也是在所难免,
     * 因此单单只是说因为FLowable是新兴产物就盲目的使用也是不对的, 也要具体分场景,
     */
    private void test25() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {
                    Log.d(TAG, "emitter=" + i);
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * BackpressureStrategy.BUFFER
     */
    private void test24() {
        Flowable.create((FlowableOnSubscribe<Integer>) e -> {
            for (int i = 0; i < 1000; i++) {
                Log.d(TAG, "emit " + i);
                e.onNext(i);
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.e(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: " + t);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete ");
                    }
                });
    }

    /**
     * 缓存大小128，超过128报错 onError: io.reactivex.exceptions.MissingBackpressureException: create: could not emit value due to lack of requests
     */
    private void test23() {
        Flowable.create((FlowableOnSubscribe<Integer>) e -> {
            for (int i = 0; i < 129; i++) {
                Log.d(TAG, "emit " + i);
                e.onNext(i);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.e(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: " + t);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete ");
                    }
                });
    }

    /**
     * 先发送事件，等2秒后再
     */
    private void test22() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                Log.e(TAG, "Emitter 1");
                e.onNext(1);
                Log.e(TAG, "Emitter 2");
                e.onNext(2);
                Log.e(TAG, "Emitter 3");
                e.onNext(3);
                Log.e(TAG, "Emitter onComplete");
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(final Subscription s) {
                        Log.e(TAG, "onSubscribe");
                        text.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                s.request(2);
                            }
                        }, 2000);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext=" + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });
    }

    /**
     * 不同线程
     * 因为在Flowable里默认有一个大小为128的水缸, 当上下游工作在不同的线程中时,
     * 上游就会先把事件发送到这个水缸中,因此, 下游虽然没有调用request,
     * 但是上游在水缸中保存着这些事件, 只有当下游调用request时, 才从水缸里取出事件发给下游.
     */
    private void test21() {
        Flowable.create((FlowableOnSubscribe<Integer>) e -> {
            Log.e(TAG, "Emitter 1");
            e.onNext(1);
            Log.e(TAG, "Emitter 2");
            e.onNext(2);
            Log.e(TAG, "Emitter 3");
            e.onNext(3);
            Log.e(TAG, "Emitter onComplete");
            e.onComplete();
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(final Subscription s) {
                        Log.e(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext=" + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });
    }

    /**
     * 因为下游没有调用request, 上游就认为下游没有处理事件的能力,
     * 而这又是一个同步的订阅, 既然下游处理不了, 那上游不可能一直等待吧,
     * 如果是这样, 万一这两根水管工作在主线程里, 界面不就卡死了吗, 因此只能抛个异常来提醒我们.
     * 那如何解决这种情况呢, 很简单啦, 下游直接调用request(Long.MAX_VALUE)就行了,
     * 或者根据上游发送事件的数量来request就行了, 比如这里request(3)就可以了
     */
    private void test20() {
        Flowable.create((FlowableOnSubscribe<Integer>) emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onComplete();
        }, BackpressureStrategy.ERROR)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe ");
//                        s.request(3);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });
    }

    private void test19() {
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            for (int i = 0; ; i++) {
                emitter.onNext(i);
                Thread.sleep(2000);  //每次发送完事件延时2秒
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> Log.d(TAG, "" + integer));
    }

    /**
     * 2秒采样
     */
    private void test18() {
        Observable.create((ObservableOnSubscribe<Integer>) e -> {
            for (int i = 0; ; i++) {
                e.onNext(i);
            }
        }).subscribeOn(Schedulers.io())
                .sample(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> Log.d(TAG, "" + integer));
    }

    /**
     * 过滤
     */
    private void test17() {
        Observable.create((ObservableOnSubscribe<Integer>) e -> {
            for (int i = 0; ; i++) {
                e.onNext(i);
            }
        }).subscribeOn(Schedulers.io())
                .filter(integer -> integer % 100 == 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> Log.d(TAG, String.valueOf(integer)));
    }

    private void test16() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {  //无限循环发送事件
                    emitter.onNext(i);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "" + integer);
                    }
                });
    }

    private void test15() {
        Observable<Integer> observable1 = Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            for (int i = 0; ; i++) {   //无限循环发事件
                emitter.onNext(i);
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create((ObservableOnSubscribe<String>)
                emitter -> emitter.onNext("A")).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2,
                (integer, s) -> integer + s)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> Log.d(TAG, s), throwable -> Log.w(TAG, throwable));
    }

    /**
     * 不在同一个线程
     */
    private void test14() {
        Observable<Integer> observable1 = Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            Log.d(TAG, "emit 1");
            emitter.onNext(1);
            Thread.sleep(1000);

            Log.d(TAG, "emit 2");
            emitter.onNext(2);
            Thread.sleep(1000);

            Log.d(TAG, "emit 3");
            emitter.onNext(3);

            Log.d(TAG, "emit 4");
            emitter.onNext(4);

            Log.d(TAG, "emit complete1");
            emitter.onComplete();
        }).subscribeOn(Schedulers.io());
        Observable<String> observable2 = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            Log.d(TAG, "emit A");
            emitter.onNext("A");
            Thread.sleep(1000);

            Log.d(TAG, "emit B");
            emitter.onNext("B");
            Thread.sleep(1000);

            Log.d(TAG, "emit C");
            emitter.onNext("C");
            Thread.sleep(1000);

            Log.d(TAG, "emit complete2");
            emitter.onComplete();
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2,
                (integer, string) -> integer + string)
                .subscribe(string -> Log.e(TAG, string),
                        throwable -> Log.e(TAG, throwable.getMessage()),
                        () -> Log.e(TAG, "onComplete"));
    }

    /**
     * http://www.jianshu.com/p/bb58571cdb64
     * Zip通过一个函数将多个Observable发送的事件结合到一起，然后发送这些组合到一起的事件. 它按照严格的顺序应用这个函数。它只发射与发射数据项最少的那个Observable一样多的数据。
     * 组合的过程是分别从 两根水管里各取出一个事件 来进行组合, 并且一个事件只能被使用一次, 组合的顺序是严格按照事件发送的顺利 来进行的, 也就是说不会出现圆形1 事件和三角形B 事件进行合并, 也不可能出现圆形2 和三角形A 进行合并的情况.
     * 最终下游收到的事件数量 是和上游中发送事件最少的那一根水管的事件数量 相同. 这个也很好理解, 因为是从每一根水管 里取一个事件来进行合并, 最少的 那个肯定就最先取完 , 这个时候其他的水管尽管还有事件 , 但是已经没有足够的事件来组合了, 因此下游就不会收到剩余的事件了.
     * <p>
     * 先发送的水管一再发送的水管二呢, 为什么会有这种情况呢? 因为我们两根水管都是运行在同一个线程里, 同一个线程里执行代码肯定有先后顺序呀
     */
    private void test13() {
        Observable<Integer> o1 = Observable.create(emitter -> {
            emitter.onNext(1);
            Log.e(TAG, "onNext 1");
            emitter.onNext(2);
            Log.e(TAG, "onNext 2");
            emitter.onNext(3);
            Log.e(TAG, "onNext 3");
            emitter.onNext(4);
            Log.e(TAG, "onNext 4");
            emitter.onComplete();
        });
        Observable<String> o2 = Observable.create(emitter -> {
            emitter.onNext("A");
            Log.e(TAG, "onNext A");
            emitter.onNext("B");
            Log.e(TAG, "onNext B");
            emitter.onNext("C");
            Log.e(TAG, "onNext C");
            emitter.onComplete();
        });
        Observable.zip(o1, o2,
                (integer, string) -> integer + string)
                .subscribe(string -> Log.e(TAG, string),
                        throwable -> Log.e(TAG, throwable.getMessage()),
                        () -> Log.e(TAG, "onComplete"));
    }

    private void test12() {
        retrofitHelper.register(new RegisterRequest(), this);
    }


    /**
     * FlatMap将一个发送事件的上游Observable变换为多个发送事件的Observables，
     * 然后将它们发送的事件合并成一个单独的Observable.flatMap并不保证事件的顺序，
     * 如果需要保证顺序则需要使用concatMap
     */
    private void test11() {
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onComplete();
        }).flatMap(integer -> {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                list.add("I am value " + integer);
            }
            return Observable.fromIterable(list);
        }).subscribe(string -> Log.e(TAG, string),
                throwable -> Log.e(TAG, throwable.getMessage()),
                () -> Log.e(TAG, "onComplete"));
    }

    /**
     * map操作符 通过Map, 可以将上游发来的事件转换为任意的类型, 可以是一个Object, 也可以是一个集合
     */
    private void test10() {
        Observable.just("Hellow", "Wrold").subscribe(string -> Log.e(TAG, string));

        Observable.create((ObservableOnSubscribe<Integer>) e -> {
            e.onNext(1);
            e.onNext(2);
            e.onNext(3);
            e.onComplete();
        }).map(integer -> {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                list.add("I am value " + integer);
            }
            return list;
        })
                .subscribe(stringList -> Log.e(TAG, "map " + stringList),
                        throwable -> Log.e(TAG, throwable.getMessage()),
                        () -> Log.e(TAG, "onComplete"));
    }

    /**
     * 数据库
     */
    private void test9() {
        Observable.create((ObservableOnSubscribe<List<String>>) e -> {
//            Cursor cursor = null;
//            try {
//                cursor = getReadableDatabase().rawQuery("select * from " + TABLE_NAME, new String[]{});
//                List<Record> result = new ArrayList<>();
//                while (cursor.moveToNext()) {
//                    result.add(Db.Record.read(cursor));
//                }
//                emitter.onNext(result);
//                emitter.onComplete();
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
            List<String> result = new ArrayList<>();
            result.add("1");
            result.add("2");
            result.add("3");
            e.onNext(result);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> Log.e(TAG, strings.toString()));
    }

    /**
     * 网络请求
     */
    private void test8() {
        LoginRequest loginRequest = new LoginRequest();
        retrofitHelper.login(loginRequest, this);
    }

    /**
     * 每调用一次observeOn() 线程便会切换一次, 因此如果我们有类似的需求时, 便可知道如何处理了.
     * Schedulers.io()代表io操作的线程,通常用于网络,读写文件等io密集型的操作
     * Schedulers.computation()代表CPU计算密集型的操作,例如需要大量计算的操作
     * Schedulers.newThread()代表一个常规的新线程
     * AndroidSchedulers.mainThread()代表Android的主线程
     */
    private void test7() {
        Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            Log.e(TAG, "emitter =" + Thread.currentThread().getName());
        }).subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(object -> {
                    Log.e(TAG, "observeOn mainThread=" + Thread.currentThread().getName());
                })
                .observeOn(Schedulers.io())
                .doOnNext(object -> {
                    Log.e(TAG, "observeOn io=" + Thread.currentThread().getName());
                }).subscribe(object -> {
            Log.e(TAG, "subscribe=" + Thread.currentThread().getName());
            Log.e(TAG, "subscribe=" + object);
        });
    }

    /**
     * Observable 子线程
     * Observer 主线程
     * 改变上游发送事件的线程, 让它去子线程中发送事件, 然后再改变下游的线程, 让它去主线程接收事件.RxJava内置的线程调度器
     * 多次指定上游的线程只有第一次指定的有效, 也就是说多次调用subscribeOn() 只有第一次的有效, 其余的会被忽略.
     * 多次指定下游的线程是可以的, 也就是说每调用一次observeOn() , 下游的线程就会切换一次.
     */
    private void test6() {
        Observable.create(emitter -> {
            Log.e(TAG, Thread.currentThread().getName());
            emitter.onNext(1);
            emitter.onNext("2");
            emitter.onNext(3);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())//指定上游线程，调用多次，只有第一次设置有效
                .observeOn(AndroidSchedulers.mainThread())//多次调用下游线程，每次都会切换
                .subscribe(object -> {
                    Log.e(TAG, Thread.currentThread().getName());
                    Log.e(TAG, String.valueOf(object));
                }, throwable -> {
                    Log.e(TAG, throwable.getMessage());
                }, () -> {
                    Log.e(TAG, "onComplete");
                });
    }

    /**
     * Observable 主线程
     * Observer 主线程
     */
    private void test5() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.e(TAG, "Observable thread name=" + Thread.currentThread().getName());
                e.onNext(1);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "Consumer thread name=" + Thread.currentThread().getName());
                Log.e(TAG, "Consumer accept=" + integer);
            }
        };

        observable.subscribe(consumer);
    }

    private void test4() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                emitter(e);
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "accept=" + integer);
            }
        });
    }

    private void test3() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                emitter(e);
            }
        }).subscribe(new Observer<Integer>() {
            private Disposable mDisposable;
            private int i;

            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
                Log.e(TAG, "onSubscribe d=" + d);
            }

            @Override
            public void onNext(Integer value) {
                i++;
                if (i == 2) {
                    Log.d(TAG, "dispose");
                    mDisposable.dispose();
                    Log.d(TAG, "isDisposed : " + mDisposable.isDisposed());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "error=" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete");
            }
        });
    }

    private void emitter(ObservableEmitter<Integer> e) {
        e.onNext(1);
        e.onNext(2);
        Log.e(TAG, "2");
        e.onNext(3);
        Log.e(TAG, "3");
        e.onComplete();
        Log.e(TAG, "ObservableEmitter onComplete");
        e.onNext(4);
        Log.e(TAG, "4");
    }


// ObservableEmitter：
// Emitter是发射器的意思，那就很好猜了，这个就是用来发出事件的，它可以发出三种类型的事件，
// 通过调用emitter的onNext(T value)、onComplete()和onError(Throwable error)就可以分别发出next事件、complete事件和error事件。
// 但是，请注意，并不意味着你可以随意乱七八糟发射事件，需要满足一定的规则：
// 上游可以发送无限个onNext, 下游也可以接收无限个onNext.
// 当上游发送了一个onComplete后, 上游onComplete之后的事件将会继续发送, 而下游收到onComplete事件之后将不再继续接收事件.
// 当上游发送了一个onError后, 上游onError之后的事件将继续发送, 而下游收到onError事件之后将不再继续接收事件.
// 上游可以不发送onComplete或onError.
// 最为关键的是onComplete和onError必须唯一并且互斥, 即不能发多个onComplete, 也不能发多个onError, 也不能先发一个onComplete, 然后再发一个onError, 反之亦然

// 注: 关于onComplete和onError唯一并且互斥这一点, 是需要自行在代码中进行控制,
// 如果你的代码逻辑中违背了这个规则, 并不一定会导致程序崩溃.
// 比如发送多个onComplete是可以正常运行的, 依然是收到第一个onComplete就不再接收了,
// 但若是发送多个onError, 则收到第二个onError事件会导致程序会崩溃.

    // 注意: 调用dispose()并不会导致上游不再继续发送事件, 上游会继续发送剩余的事件.
    private void test2() {
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onComplete();
            emitter.onNext(3);
        }).subscribe(new Observer<Integer>() {
            Disposable mD;

            @Override
            public void onSubscribe(Disposable d) {
                mD = d;
                Log.e(TAG, "onSubscribe " + d);
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "onNext=" + integer);
                if (integer == 2) {
                    mD.dispose();
                    Log.e(TAG, "mD=" + mD.isDisposed());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError=" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete");
            }
        });
    }

    private void test1() {
        Observable<Integer> observable = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onComplete();
        });
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "" + value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete");
            }
        };
        observable.subscribe(observer);

        Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext("2");
            emitter.onNext(3);
            emitter.onComplete();
//            emitter.onError(new NullPointerException("ERROR"));
//            emitter.onError(new NullPointerException("ERROR"));
        }).subscribe(object -> Log.d(TAG, String.valueOf(object)),
                throwable -> Log.e(TAG, throwable.getMessage()),
                () -> Log.e(TAG, "onComplete"));

        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onComplete();
        }).subscribe(integer -> Log.d(TAG, String.valueOf(integer)),
                throwable -> Log.e(TAG, throwable.getMessage()),
                () -> Log.e(TAG, "onComplete"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        retrofitHelper.clear();
    }
}
