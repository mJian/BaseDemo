package com.majian.base.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.majian.base.BaseFragment;


/**
 *
 * Created by majian on 2019/9/23.
 */

public abstract class MVPBaseFragment<T extends BasePresenterImpl> extends BaseFragment implements BaseView{
    public T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = initPresenter();
        mPresenter.attachView(this);
    }

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
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null)
            mPresenter.detachView();
    }
}
