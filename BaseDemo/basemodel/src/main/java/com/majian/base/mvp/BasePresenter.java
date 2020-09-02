package com.majian.base.mvp;

/**
 */

public interface  BasePresenter <V extends BaseView, M extends BaseModel>{
    M createModel();
    void attachView(V view);
    void detachView();
}
