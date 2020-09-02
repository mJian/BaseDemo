package com.majian.base.mvp;

import com.google.gson.JsonSyntaxException;
import com.majian.base.util.LogUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 *
 * Created by majian on 2019/6/26.
 */

public class BaseModelImpl implements BaseModel {

    private Disposable disposable;
    @Override
    public <T> void loadData(Observable<T> observable, final NetCallBack<T> callBack){
        final long startTime = System.currentTimeMillis();//接口开始请求开始时间按
        observable
                .flatMap(new Function<T, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(T t) throws Exception {
                        //保证至少300毫秒的反应时间，避免接口请求太快，进度条展示、隐藏过快，导致页面闪烁
                        long timeDelay = System.currentTimeMillis() - startTime;
                        if (300 - timeDelay <= 0){
                            return Observable.just(t);
                        }
                        return Observable.just(t).delay(300 - timeDelay, TimeUnit.MILLISECONDS);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(T t) {
                        if (callBack != null){
                            callBack.onSuccess(t);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callBack != null){
                            callBack.onFailure(getException(e));
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (callBack != null){
                            callBack.onCompleted();
                        }
                    }
                });
    }

    public static String getException(Throwable e) {
        String msg = e.toString();
        if (e instanceof SocketTimeoutException) {
            msg = "网络不稳定，链接超时";
        } else if (e instanceof ConnectException) {
            msg = "网络不稳定,请稍后再试";
        } else  if(e instanceof HttpException){
            msg="错误码"+((HttpException) e).code()+" 请求失败";
        }else if ( e instanceof UnknownHostException){
            msg="请保持网络通畅";
        } else if (e instanceof  IllegalStateException){
            msg = e.getMessage();
        } else if (e instanceof JsonSyntaxException){
            msg = "数据解析出错";
        } else if (e instanceof MyException){
            msg = "" + e.getMessage();
        }else {
            msg = "接口请求失败";
        }
        LogUtils.e("" + e.getMessage());
        e.printStackTrace();
        return msg;
    }

    public void cancelTask(){
        if (disposable == null)return;
        disposable.dispose();
    }
}
