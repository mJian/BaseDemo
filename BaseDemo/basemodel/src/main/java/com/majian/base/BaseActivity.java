package com.majian.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.majian.base.util.statusbar.StatusBarUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.ButterKnife;

/**
 * Created by majian on 2018/7/31.
 */

public abstract class BaseActivity extends FragmentActivity {
    public Context mContext;
    private ProgressDialog progressBar;
    public int page = 0;
    public boolean isRefresh = true;//是否刷新

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //如果是透明的Activity，则不能固定它的方向，因为它的方向其实是依赖其父Activity的（因为透明）。然而这个bug只有在8.0中有，8.1中已经修复
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            boolean result = fixOrientation();
        }
        super.onCreate(savedInstanceState);
        setContentView(getView());
        mContext = this;
        ButterKnife.bind(this);
        initProgress();
        initLocalData();
    }

    private boolean fixOrientation(){
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo)field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isTranslucentOrFloating(){
        boolean isTranslucentOrFloating = false;
        try {
            @SuppressLint("PrivateApi") int [] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean)m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }


    public abstract int getView();

    /**
     * 初始化本地数据（或者intent传参）
     * 已在baseActivity调用
     */
    public abstract void initLocalData();

    /**
     * 获取网络数据
     */
    public abstract void getNetData();

    public void openActivity(Class c) {
        Intent intent = new Intent(mContext, c);
        startActivity(intent);
    }

    public void openActivityForResult(Class c, int code) {
        Intent intent = new Intent(mContext, c);
        startActivityForResult(intent, code);
    }

    @Override
    public void finish() {
        // 隐藏软键盘，避免软键盘引发的内存泄露
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.finish();
    }


    /**
     * 初始化进度条
     */
    private void initProgress() {
        progressBar = new ProgressDialog(this, R.style.loading_dialog);
        progressBar.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressBar.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
    }

    /**
     * 显示进度条
     */
    public void showWaitDialog() {
        if (isFinishing()) return;
        if (progressBar.isShowing()) return;
        progressBar.show();
        View v = LayoutInflater.from(this).inflate(R.layout.mj_loading_dialog, null);
        progressBar.setContentView(v);
    }

    /**
     * 隐藏进度条
     */
    public void dismissWaitDialog() {
        progressBar.dismiss();
    }


    /**
     * 初始化状态栏相关，
     * PS: 设置全屏需要在调用super.onCreate(arg0);之前设置setIsFullScreen(true);否则在Android 6.0下非全屏的activity会出错;
     * SDK19：可以设置状态栏透明，但是半透明的SYSTEM_BAR_BACKGROUNDS会不好看；
     * SDK21：可以设置状态栏颜色，并且可以清除SYSTEM_BAR_BACKGROUNDS，但是不能设置状态栏字体颜色（默认的白色字体在浅色背景下看不清楚）；
     * SDK23：可以设置状态栏为浅色（SYSTEM_UI_FLAG_LIGHT_STATUS_BAR），字体就回反转为黑色。
     *
     * @param isSetTitleBlack 是否让状态栏文字变为黑色
     */
    public void initStatusBar(boolean isSetTitleBlack) {
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        //取消padding,  因为layout_title是使用图片，这个padding 我们用代码实现了,不需要系统帮我
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);

        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (isSetTitleBlack) {
            if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
                //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
                //这样半透明+白=灰, 状态栏的文字能看得清
                StatusBarUtil.setStatusBarColor(this, 0x55000000);
            }
        }
    }

    /**
     * 沉浸式状态栏（纯色）
     *
     * @param isSetTitleBlack
     */
    public void initStatusBar2(boolean isSetTitleBlack) {
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);

        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (isSetTitleBlack) {
            if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
                //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
                //这样半透明+白=灰, 状态栏的文字能看得清
                StatusBarUtil.setStatusBarColor(this, 0x55000000);
            }
        }
    }

    /**
     * 设置 app 字体不随系统字体设置改变
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res != null) {
            Configuration config = res.getConfiguration();
            if (config != null && config.fontScale != 1.0f) {
                config.fontScale = 1.0f;
                res.updateConfiguration(config, res.getDisplayMetrics());
            }
        }
        return res;
    }


    @Override
    protected void onDestroy() {
        if (progressBar != null){
            progressBar.dismiss();
        }
        super.onDestroy();
    }
}
