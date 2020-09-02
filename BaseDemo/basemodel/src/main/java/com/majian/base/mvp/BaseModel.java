package com.majian.base.mvp;



import io.reactivex.Observable;

/**
 *
 * Created by majian on 2019/6/26.
 */

public interface BaseModel {
    void cancelTask();
    <T> void  loadData(Observable<T> observable, final NetCallBack<T> callBack);
}
