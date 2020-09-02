package com.majian.base.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.majian.base.BaseActivity;


/**
 * MVP
 * Created by majian on 2019/9/10.
 */

public abstract class MVPBaseActivity<T extends BasePresenterImpl> extends BaseActivity implements BaseView {
    public T mPresenter;

    public abstract T initPresenter();

    @Override
    public Context getMContext() {
        return mContext;
    }

    @Override
    public void showLoadingView() {
        showWaitDialog();
    }

    @Override
    public void hideLoadingView() {
        dismissWaitDialog();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = initPresenter();
        mPresenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null){
            mPresenter.detachView();
        }
    }
}
