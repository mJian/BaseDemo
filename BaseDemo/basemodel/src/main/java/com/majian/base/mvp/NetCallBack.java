package com.majian.base.mvp;

/**
 *
 * Created by majian on 2019/6/26.
 */

public interface NetCallBack<T> {
    void onStart();
    void onSuccess(T t);
    void onFailure(String reason);
    void onCompleted();
}
