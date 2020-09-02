package com.majian.base.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import java.util.Locale;

/**
 * Create by maJian on 2020/7/31
 */
public class SystemUtil {
    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 判断我们的应用是否在白名单中（电池管理是否优化）
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isIgnoringBatteryOptimizations(Context mContext) {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(mContext.getPackageName());
        }
        return isIgnoring;
    }

    /**
     * 申请白名单（电池优化-不优化）
     * 需要权限：<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestIgnoreBatteryOptimizations(Activity mContext, int requestCode) {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
            mContext.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转厂商后台管理，自启动
     * @return  是否打开成功
     */
    public static boolean jumpToMobileManager(Context mContext){
        String deviceBrand = getDeviceBrand();
        if(TextUtils.isEmpty(deviceBrand))return false;
        deviceBrand = deviceBrand.toLowerCase();
        switch (deviceBrand){
            case "huawei"://华为手机
            case "honor"://华为手机
                return goHuaweiSetting(mContext);
            case "xiaomi"://小米手机
                return goXiaomiSetting(mContext);
            case "oppo"://oppo手机
                return goOPPOSetting(mContext);
            case "vivo"://vivo手机
                return goVIVOSetting(mContext);
            case "meizu"://魅族手机
                return goMeizuSetting(mContext);
            case "samsung"://三星手机
                return goSamsungSetting(mContext);
            case "smartisan"://锤子手机
                return goSmartisanSetting(mContext);
        }
        return false;
    }

    /**
     * 跳转到指定应用的首页
     * @param packageName
     */
    private static void showActivity(Context mContext, @NonNull String packageName) {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        mContext.startActivity(intent);
    }


    /**
     * 跳转到指定应用的指定页面
     * @param packageName   包名
     * @param activityDir   activity对应的完整路径
     */
    private static void showActivity(Context mContext, @NonNull String packageName, @NonNull String activityDir) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityDir));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * 是否华为手机
     * @return
     */
    public static boolean isHuawei() {
        if (Build.BRAND == null) {
            return false;
        } else {
            return Build.BRAND.toLowerCase().equals("huawei") || Build.BRAND.toLowerCase().equals("honor");
        }
    }

    /**
     * 跳转华为手机管家的启动管理页
     * 操作步骤：应用启动管理 -> 关闭应用开关 -> 打开允许自启动
     * @return 是否跳转成功
     */
    public static boolean goHuaweiSetting(Context mContext) {
        try {
            showActivity(mContext, "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
            return true;
        } catch (Exception e1) {
            try {
                showActivity(mContext, "com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.bootstart.BootStartActivity");
                return true;
            }catch (Exception e2){
            }
        }
        return true;
    }

    /**
     * 是否小米手机
     * @return
     */
    public static boolean isXiaomi() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("xiaomi");
    }

    /**
     * 跳转小米安全中心的自启动管理页面
     * 操作步骤：授权管理 -> 自启动管理 -> 允许应用自启动
     * @return 是否跳转成功
     */
    public static boolean goXiaomiSetting(Context mContext) {
        try {
            showActivity(mContext, "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 是否oppo手机
     * @return
     */
    public static boolean isOPPO() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("oppo");
    }

    /**
     * 跳转 OPPO 手机管家
     * 操作步骤：权限隐私 -> 自启动管理 -> 允许应用自启动
     * @return
     */
    public static boolean goOPPOSetting(Context mContext) {
        try {
            showActivity(mContext, "com.coloros.phonemanager");
            return true;
        } catch (Exception e1) {
            try {
                showActivity(mContext, "com.oppo.safe");
                return true;
            } catch (Exception e2) {
                try {
                    showActivity(mContext, "com.coloros.oppoguardelf");
                    return true;
                } catch (Exception e3) {
                    try {
                        showActivity(mContext, "com.coloros.safecenter");
                        return true;
                    }catch (Exception e4){
                        return false;
                    }
                }
            }
        }
    }

    /**
     * 是否vivo手机
     * @return
     */
    public static boolean isVIVO() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("vivo");
    }

    /**
     * 跳转 VIVO 手机管家
     * 操作步骤：权限管理 -> 自启动 -> 允许应用自启动
     * @param mContext
     * @return
     */
    public static boolean goVIVOSetting(Context mContext) {
        try {
            showActivity(mContext, "com.iqoo.secure");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 是否魅族手机
     * @return
     */
    public static boolean isMeizu() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("meizu");
    }

    /**
     * 跳转魅族手机管家
     * 操作步骤：权限管理 -> 后台管理 -> 点击应用 -> 允许后台运行
     * @param mContext
     * @return
     */
    public static boolean goMeizuSetting(Context mContext) {
        try {
            showActivity(mContext, "com.meizu.safe");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 是否三星手机
     * @return
     */
    public static boolean isSamsung() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("samsung");
    }

    /**
     * 跳转三星智能管理器
     * 操作步骤：自动运行应用程序 -> 打开应用开关 -> 电池管理 -> 未监视的应用程序 -> 添加应用
     * @param mContext
     * @return
     */
    public static boolean goSamsungSetting(Context mContext) {
        try {
            showActivity(mContext, "com.samsung.android.sm_cn");
            return true;
        } catch (Exception e1) {
            try {
                showActivity(mContext, "com.samsung.android.sm");
                return true;
            }catch (Exception e2){
                return false;
            }
        }
    }

    /**
     * 是否锤子手机
     * @return
     */
    public static boolean isSmartisan() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("smartisan");
    }

    /**
     * 跳转锤子手机管家
     * 操作步骤：权限管理 -> 自启动权限管理 -> 点击应用 -> 允许被系统启动
     * @param mContext
     * @return
     */
    public static boolean goSmartisanSetting(Context mContext) {
        try {
            showActivity(mContext, "com.smartisanos.security");
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
