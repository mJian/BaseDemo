package com.majian.base.mvp;


import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 */

public abstract class BasePresenterImpl<V extends BaseView, M extends BaseModel> implements BasePresenter<V, M>{
    /**弱引用, 防止内存泄漏*/
    private WeakReference<V> weakReference;
    public V mView;
    protected M mModel;
    @Override
    public void attachView(V view) {
//        mView=view;
//        if (mModel == null){
//            mModel = createModel();
//        }

        weakReference = new WeakReference<>(view);
        MvpViewHandler viewHandler = new MvpViewHandler(weakReference.get());
        mView = (V) Proxy.newProxyInstance(view.getClass().getClassLoader(), view.getClass().getInterfaces(), viewHandler);
        if (mModel == null){
            mModel = createModel();
        }
    }


    @Override
    public void detachView() {
        if (isViewAttached()) {
            weakReference.clear();
            weakReference = null;
            if (mModel != null){
                mModel.cancelTask();
            }
        }
//        if (mView != null){
//            mView.hideLoadingView();
//        }
//        if (mModel != null){
//            mModel.cancelTask();
//        }
//        mView=null;
//        mModel = null;
    }

    /**
     * @return P层和V层是否关联.
     */
    public boolean isViewAttached() {
        return weakReference != null && weakReference.get() != null;
    }

    private class MvpViewHandler implements InvocationHandler {
        private BaseView mvpView;

        MvpViewHandler(BaseView mvpView) {
            this.mvpView = mvpView;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //如果V层没被销毁, 执行V层的方法.
            if (isViewAttached()) {
                return method.invoke(mvpView, args);
            }
            //P层不需要关注V层的返回值
            return null;
        }
    }
}
