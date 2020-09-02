package com.majian.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.majian.base.util.statusbar.StatusBarUtil;

import butterknife.ButterKnife;

/**
 *
 * Created by majian on 2018/7/31.
 */

public abstract class BaseFragment extends Fragment {

    private ProgressDialog progressBar;
    public Context mContext;
    public int page = 0;
    public boolean isRefresh;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        initProgress();
        initLocalData();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getViewResource(), container, false);
    }

    public abstract int getViewResource();

    /**
     * 初始化本地数据
     * 已在baseActivity调用
     */
    public abstract void initLocalData();

    /**
     * 获取网络数据
     */
    public abstract void getNetData();

    /**
     * 初始化进度条
     */
    private void initProgress(){
        progressBar=new ProgressDialog(getContext(), R.style.loading_dialog);
        progressBar.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressBar.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
    }

    /**
     * 显示进度条
     */
    public void showWaitDialog() {
        if (progressBar.isShowing()) return;
        progressBar.show();
        View v= LayoutInflater.from(getContext()).inflate(R.layout.mj_loading_dialog, null);
        progressBar.setContentView(v);
    }

    /**
     * 显示进度条
     */
    public void showWaitDialog(String msg) {
        progressBar.show();
        View v= LayoutInflater.from(getContext()).inflate(R.layout.mj_loading_dialog, null);
        TextView tvTips = v.findViewById(R.id.tipTextView);
        if (TextUtils.isEmpty(msg)){
            tvTips.setVisibility(View.GONE);
        }else {
            tvTips.setVisibility(View.VISIBLE);
            tvTips.setText(msg);
        }
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
     * @param isSetTitleBlack   是否让状态栏文字变为黑色
     */
    public void initStatusBar(boolean isSetTitleBlack) {
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(getActivity(),false);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(getActivity());

        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (isSetTitleBlack){
            if (!StatusBarUtil.setStatusBarDarkTheme(getActivity(), true)) {
                //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
                //这样半透明+白=灰, 状态栏的文字能看得清
                StatusBarUtil.setStatusBarColor(getActivity(),0x55000000);
            }
        }
    }

    public void initStatusBar2(boolean isSetTitleBlack) {
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(getActivity(),true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(getActivity());

        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (isSetTitleBlack){
            if (!StatusBarUtil.setStatusBarDarkTheme(getActivity(), true)) {
                //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
                //这样半透明+白=灰, 状态栏的文字能看得清
                StatusBarUtil.setStatusBarColor(getActivity(),0x55000000);
            }
        }
    }
}
