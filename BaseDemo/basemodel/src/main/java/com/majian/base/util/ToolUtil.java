package com.majian.base.util;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Paint;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolUtil {

    private static final int MIN_DELAY_TIME = 500;  // 两次点击间隔不能少于1000ms
    private static long lastClickTime = 0;
    private static int lastViewId = -1;
    private static Gson gson;
    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<>();

    // 可以自己随意添加
    private static String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };


    /**
     * 获取手机号
     *
     * @param context
     * @return 手机号
     */
    @SuppressLint("HardwareIds")
    public static String getPhoneNum(Context context) {
        //品牌
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission")
            String tel = telephonyManager.getLine1Number();
            if (!TextUtils.isEmpty(tel) && tel.length() > 11) {
                tel = tel.substring(tel.length() - 11);
            }
            return tel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取手机品牌和 IMEI号
     *
     * @param context
     * @return 手机品牌_IMEI
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getPhoneTypeAndImei(Context context) {
        //品牌
        String phoneBrand = Build.BRAND;
        if (TextUtils.isEmpty(phoneBrand)) {
            phoneBrand = "phoneBrand";
        }
        String phoneType = Build.MODEL;
        if (TextUtils.isEmpty(phoneType)) {
            phoneBrand = "phoneType";
        }
        String imei = "IMEI";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null && !TextUtils.isEmpty(telephonyManager.getDeviceId())) {
                imei = telephonyManager.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phoneBrand + "_" + phoneType + "_" + imei;
    }

    @SuppressLint("MissingPermission")
    public static String getIMEIAndMEID(Context context) {
        String res = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String imei1 = telephonyManager.getImei(0);
            String imei2 = telephonyManager.getImei(1);

            String meid1 = telephonyManager.getMeid(0);
            String meid2 = telephonyManager.getMeid(1);

            if (!TextUtils.isEmpty(imei1)) {
                res += imei1;
            }
            if (!TextUtils.isEmpty(imei2)) {
                res += imei2;
            }
            if (!TextUtils.isEmpty(meid1)) {
                res += meid1;
            }
            if (!TextUtils.isEmpty(meid2)) {
                res += meid2;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String imei1 = telephonyManager.getDeviceId(0);
            String imei2 = telephonyManager.getDeviceId(1);

            if (!TextUtils.isEmpty(imei1)) {
                res += imei1;
            }
            if (!TextUtils.isEmpty(imei2)) {
                res += imei2;
            }
        } else {
            String imei = telephonyManager.getDeviceId();

            if (!TextUtils.isEmpty(imei)) {
                res += imei;
            }
        }
        return res;
    }

    public static Map<String, String> jsonToMap(String json) {
        Map<String, String> map = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> it = jsonObject.keys();
            while (it.hasNext()) {
                String key = it.next();
                map.put(key, jsonObject.getString(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String mapToJson(Map<String, String> map) {
        Set<String> keys = map.keySet();
        String key = "";
        String value = "";
        StringBuffer jsonBuffer = new StringBuffer();
        jsonBuffer.append("{");
        for (Iterator<String> it = keys.iterator(); it.hasNext(); ) {
            key = (String) it.next();
            value = map.get(key);
            jsonBuffer.append("\"");
            jsonBuffer.append(key + "\":\"" + value + "\"");
            if (it.hasNext()) {
                jsonBuffer.append(",");
            }
        }
        jsonBuffer.append("}");
        return jsonBuffer.toString();
    }

    /**
     * 是否有网络
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            LogUtils.i("Unavailabel");
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        LogUtils.i("Availabel");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 打开网络设置界面
     *
     * @param context
     */
    public static void startNetSetting(Context context) {
        // 跳转到系统的网络设置界面
        Intent intent = null;
        // 先判断当前系统版本
        if (Build.VERSION.SDK_INT > 10) {  // 3.0以上
            intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
        }
        context.startActivity(intent);
    }

    /**
     * 打开设置界面
     *
     * @param context
     */
    public static void startSetting(Context context) {
        // 跳转到系统的网络设置界面
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        // 先判断当前系统版本
        context.startActivity(intent);
    }

    /**
     * 打开App设置界面
     *
     * @param context
     */
    public static void startAppSetting(Context context) {
        Uri packageURI = Uri.parse("package:" + context.getPackageName());
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        context.startActivity(intent);
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
//        int height = dm.heightPixels;       // 屏幕高度（像素）
//        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
//        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
//        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
//        int screenHeight = (int) (height / density);// 屏幕高度(dp)
        return width;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
//        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
//        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
//        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
//        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
//        int screenHeight = (int) (height / density);// 屏幕高度(dp)
        return height;
    }

    /**
     * 将字符串时间按指定格式转换成字符串
     *
     * @param time
     * @param fromTemplate
     * @param toTemplate
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatTimeByTemplate(String time, String fromTemplate, String toTemplate) {
        if (TextUtils.isEmpty(time)) return "";
        String res = "";
        SimpleDateFormat sdf = new SimpleDateFormat(fromTemplate);
        try {
            Date date = sdf.parse(time);
            sdf = new SimpleDateFormat(toTemplate);
            res = sdf.format(date);
        } catch (ParseException e) {
//            e.printStackTrace();
            return time;
        }
        return res;
    }


    /**
     * 按照 template 格式 格式化 日期 date
     *
     * @param date      时间
     * @param template  格式
     */
    public static String formatDateByTemplate(Date date, String template) {
        String res = "";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(template);
        res = sdf.format(date);
        return res;
    }

    /**
     * 时间dateStr 是否在当前时间之前
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss格式的时间字符串
     */
    public static boolean isDateBeforeNow(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) return false;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(dateStr);
            Date now = new Date();

            return date.before(now);
//            return !date.after(now);
        } catch (ParseException e) {
            LogUtils.e("时间格式化错误");
//            e.printStackTrace();
        }
        return false;
    }

    /**
     * 时间dateStr 是否在当前时间之前
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss格式的时间字符串
     */
    public static boolean isDateAfterNow(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) return false;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(dateStr);
            Date now = new Date();

            return date.after(now);
//            return !date.after(now);
        } catch (ParseException e) {
            LogUtils.e("时间格式化错误");
//            e.printStackTrace();
        }
        return false;
    }

    /**
     * 时间dateStr 是否在当前时间之前
     *
     * @param dateStr1 yyyy-MM-dd HH:mm:ss格式的时间字符串
     * @param dateStr2 yyyy-MM-dd HH:mm:ss格式的时间字符串
     * @return
     */
    public static boolean isDate1BeforeDate2(String dateStr1, String dateStr2) {
        if (TextUtils.isEmpty(dateStr1)) return false;
        if (TextUtils.isEmpty(dateStr2)) return false;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = sdf.parse(dateStr1);
            Date date2 = sdf.parse(dateStr2);
            return date1.before(date2);
        } catch (ParseException e) {
            LogUtils.e("时间格式化错误");
//            e.printStackTrace();
        }
        return false;
    }

    /**
     * 时间dateStr 是否在当前时间之前
     *
     * @param dateStr1 yyyy-MM-dd HH:mm:ss格式的时间字符串
     * @param dateStr2 yyyy-MM-dd HH:mm:ss格式的时间字符串
     * @return
     */
    public static boolean isDate1BeforeDate2(String dateStr1, String dateStr2, String template) {
        if (TextUtils.isEmpty(dateStr1)) return false;
        if (TextUtils.isEmpty(dateStr2)) return false;
        if (TextUtils.isEmpty(template)) return false;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(template);
        try {
            Date date1 = sdf.parse(dateStr1);
            Date date2 = sdf.parse(dateStr2);
            return date1.before(date2);
        } catch (ParseException e) {
            LogUtils.e("时间格式化错误");
//            e.printStackTrace();
        }
        return false;
    }

    /**
     * 时间dateStr 是否在当前时间之前
     *
     * @param dateStr1 yyyy-MM-dd HH:mm:ss格式的时间字符串
     * @param dateStr2 yyyy-MM-dd HH:mm:ss格式的时间字符串
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static boolean isDate1BeforeDate2(String dateStr1, String dateStr2, String template1, String template2) {
        if (TextUtils.isEmpty(dateStr1)) return false;
        if (TextUtils.isEmpty(dateStr2)) return false;
        if (TextUtils.isEmpty(template1)) return false;
        if (TextUtils.isEmpty(template2)) return false;

        SimpleDateFormat sdf1 = new SimpleDateFormat(template1);
        SimpleDateFormat sdf2 = new SimpleDateFormat(template2);
        try {
            Date date1 = sdf1.parse(dateStr1);
            Date date2 = sdf2.parse(dateStr2);
            return date1.before(date2);
        } catch (ParseException e) {
            LogUtils.e("时间格式化错误");
//            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取apk的版本号 currentVersionCode
     *
     * @param ctx
     * @return
     */
    public static int getAPPVersionCode(Context ctx) {
        int currentVersionCode = 0;
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            String appVersionName = info.versionName; // 版本名
            currentVersionCode = info.versionCode; // 版本号
            LogUtils.d("" + currentVersionCode + " " + appVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersionCode;
    }

    /**
     * 获取apk的版本号 currentVersionName
     *
     * @param ctx
     * @return
     */
    public static String getAPPVersionName(Context ctx) {
        String currentVersionName = "";
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            currentVersionName = info.versionName; // 版本名
            LogUtils.d("" + currentVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersionName;
    }

    /**
     * 是否快速点击
     *
     * @return
     */
    public static boolean isFastClick(int viewId) {
        boolean flag = false;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) < MIN_DELAY_TIME && lastViewId == viewId) {
            flag = true;
        }
        lastViewId = viewId;
        lastClickTime = currentClickTime;
        return flag;
    }

    /**
     * 判断GPS是否开启
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPenGPS(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps;
    }

    /**
     * 强制帮用户打开GPS
     *
     * @param context
     */
    public static final void openGPS(Context context) {
        // 转到手机设置界面，用户设置GPS
        Intent intent = new Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent); // 设置完成后返回到原来的界面
    }

    /**
     * 跳转WiFi设置界面
     *
     * @param context
     */
    public static final void jumpToWifi(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        context.startActivity(intent);
    }

    /***
     * MD5加密 生成32位md5码
     * @param inStr
     * @return 返回32位md5码
     */
    public static String md5(String inStr) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] byteArray = inStr.getBytes("UTF-8");
            //获得加密后的数据
            byte[] md5Bytes = md5.digest(byteArray);
            //将加密后的数据转换为16进制数字
            String md5Code = new BigInteger(1, md5Bytes).toString(16);
            // 如果生成数字未满32位，需要前面补0
            for (; md5Code.length() < 32; md5Code = "0" + md5Code) {
            }
            return md5Code;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将指定格式的日期转换为指定格式的日期
     *
     * @param time     待转换的日期
     * @param fromTemp 待转换日期的格式
     * @param toTemp   转换后的日期格式
     * @return 按照toTemp的日期格式返回日期字符串
     */
    public static String getDateByTemplate(String time, String fromTemp, String toTemp) {
        String res = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(fromTemp);
            Date date = sdf.parse(time);
            sdf.applyPattern(toTemp);
            res = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 调用第三方应用打开文件
     *
     * @param file
     * @return
     */
    public static void openFile(Context context, File file) {
        LogUtils.e("file Name = " + file.getName());
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //安卓8.0手机没有任何反应？ 需要添加权限：android.permission.REQUEST_INSTALL_PACKAGES
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, "com.sslab.zkjy.FileProvider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        String type = getMIMEType(file);
        LogUtils.e("文件类型：" + type);
        intent.setDataAndType(uri, type);
        context.startActivity(intent);
        Intent.createChooser(intent, "请选择对应的软件打开该附件！");
    }

    private static String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (TextUtils.isEmpty(end)) return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (String[] aMIME_MapTable : MIME_MapTable) {
            if (end.equals(aMIME_MapTable[0]))
                type = aMIME_MapTable[1];
        }
        return type;
    }

    /**
     * 验证手机号格式是否正确
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobile(String mobiles) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,6,7,8,9][0-9]{9}$"); // 验证手机号
        m = p.matcher(mobiles);
        b = m.matches();
        return b;
    }

    /**
     * 验证电话号正确
     *
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        String regexp = "^(((\\+\\d{2}-)?0\\d{2,3}-\\d{7,8})|((\\+\\d{2}-)?(\\d{2,3}-)?([1][3,4,5,6,7,8,9][0-9]\\d{8})))$";
        p = Pattern.compile(regexp); // 验证电话
        m = p.matcher(phone);
        b = m.matches();
        return b;
    }

    /**
     * 检查邮箱是否合法
     */
    public static Boolean checkEmail(String email) {
        return email.matches("^[a-z0-9A-Z]+[- | a-z0-9A-Z . _]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$");
    }


    /*** 验证固定电话号码
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean IsTelephone(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^0(10|2[0-5789]-|\\d{3})-?\\d{7,8}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 显示软键盘
     *
     * @param context
     * @param view
     */
    public static void showSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param view
     */
    public static void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
        }
    }


    /**
     * 得到几天前的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 得到几天后的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    public static Intent getVideoIntent() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
        }
        return intent;
    }


    public static void install(Context mContext, String filePath) {
        LogUtils.e("开始执行安装: " + filePath);
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //安卓8.0手机没有任何反应？ 需要添加权限：android.permission.REQUEST_INSTALL_PACKAGES
            LogUtils.e("版本大于 N ，开始使用 fileProvider 进行安装");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".FileProvider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            LogUtils.e("正常进行安装");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }

    /**
     * 验证银行卡号
     *
     * @param cardId 银行卡号
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    //从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
    private static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }


    /**
     * 检测是否安装微信
     *
     * @param context
     * @return
     */
    public static boolean isWxInstall(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 检测是否安装微博
     *
     * @param context
     * @return
     */
    public static boolean isWeiboInstall(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.sina.weibo")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 检测是否安装QQ
     *
     * @param context
     * @return
     */
    public static boolean isQQInstall(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 检测是否安装钉钉
     *
     * @return
     */
    public static boolean isDingInstall(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.alibaba.android.rimet")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * get file md5
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file) throws NoSuchAlgorithmException, IOException {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        digest = MessageDigest.getInstance("MD5");
        in = new FileInputStream(file);
        while ((len = in.read(buffer, 0, 1024)) != -1) {
            digest.update(buffer, 0, len);
        }
        in.close();
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 设置密码可见和不可见
     *
     * @param editText 要设置的EditText
     */
    public static void setPasswordEye(EditText editText) {
        if (editText.getTransformationMethod() instanceof PasswordTransformationMethod) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        if (TextUtils.isEmpty(editText.getText().toString().trim())) return;
        //执行上面的代码后光标会处于输入框的最前方,所以把光标位置挪到文字的最后面
        editText.setSelection(editText.getText().toString().length());
    }

    /**
     * 检查包是否存在
     *
     * @param context
     * @param packname
     * @return
     */
    public static boolean checkPackInfo(Context context, String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    /**
     * 通过包名打开第三方应用
     * 若第三方应用已打开，直接显示已打开页面
     *
     * @param context
     * @param packageName
     * @return
     */
    public static Intent getAppOpenIntentByPackageName(Context context, String packageName) {
        String mainAct = null;
        PackageManager pkgMag = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

        @SuppressLint("WrongConstant") List<ResolveInfo> list = pkgMag.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get(i);
            if (info.activityInfo.packageName.equals(packageName)) {
                mainAct = info.activityInfo.name;
                break;
            }
        }
        if (TextUtils.isEmpty(mainAct)) {
            return null;
        }
        intent.setComponent(new ComponentName(packageName, mainAct));
        return intent;
    }

    /**
     * 是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isApkInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制内容到剪切板
     *
     * @param copyStr
     * @return
     */
    public static boolean copy(Context context, String copyStr) {
        try {
            //获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 截取字符串中的所有中文字符
     *
     * @param str
     * @return
     */
    public static String chineseStr(String str) {
        Pattern p = null;
        Matcher m = null;
        String value = "";
        p = Pattern.compile("[\u4e00-\u9fa5]");
        m = p.matcher(str);
        if (!TextUtils.isEmpty(str)) {
            while (m.find()) {
                if (m.group(0) != null) {
                    value += m.group(0);
                }
            }
        }
        return value;
    }

    /**
     * 字符串显示到textView, textView maxLines=2
     * 如果字符串太长显示不下，则用省略号代替
     * 省略号的位置在第一行末尾
     *
     * @param textView 显示字符串的view
     * @param str      要显示的字符串
     * @param width    显示字符串的view的宽
     * @return 处理后带省略号的字符串
     */
    public static String ellipsizeString(TextView textView, String str, int width) {
        Paint paint = textView.getPaint();
        //文字总宽小于2倍的view宽，说明小于2行，直接返回
        if (paint.measureText(str) < 2 * width) {
            return str;
        }

        //存储显示到view的每行文字
        List<String> list = new ArrayList<>();

        int len = 0;
        int start, end = 0;

        while (len < str.length()) {
            len += end;
            int count = paint.breakText(str, end, str.length(), true, width, null);
            start = end;
            end = end + count;
            list.add(str.substring(start, end));
        }

        //第一行文字末尾三个字符替换成省略号
        String line1 = list.get(0);
        line1 = line1.substring(0, line1.length() - 3) + "...";

        //最后一行半的文字从末尾向前截取一行文字
        String endLine = list.get(list.size() - 1);
        int endLineWidth = (int) paint.measureText(endLine);
        String minorEndLine = list.get(list.size() - 2);
        int minorCuteCount = paint.breakText(minorEndLine, 0, minorEndLine.length(), true, endLineWidth, null);
        String line2 = minorEndLine.substring(minorCuteCount, minorEndLine.length()) + endLine;

        return line1 + line2;
    }

    /**
     * 银行卡脱敏
     *
     * @param bankAcct
     * @return
     */
    public static String encryptBankAcct(String bankAcct) {
        if (TextUtils.isEmpty(bankAcct)) {
            return "";
        }
        if (bankAcct.length() < 4)
            return bankAcct;

        return "****" + bankAcct.substring(bankAcct.length() - 4);
    }

    /**
     * 手机号码前三后四脱敏
     *
     * @param mobile
     * @return
     */
    public static String mobileEncrypt(String mobile) {
        if (TextUtils.isEmpty(mobile) || (mobile.length() != 11)) {
            return mobile;
        }
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 不能全是相同的数字或者字母（如：000000、111111、aaaaaa）
     *
     * @param numOrStr str.length()>0
     * @return 全部相同返回true
     */
    public static boolean equalStr(String numOrStr) {
        boolean flag = true;
        char str = numOrStr.charAt(0);
        for (int i = 0; i < numOrStr.length(); i++) {
            if (str != numOrStr.charAt(i)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 不能是连续的数字--递增（如：123456、12345678）
     *
     * @param numOrStr
     * @return 连续数字返回true
     */
    public static boolean isOrderNumeric(String numOrStr) {
        boolean flag = true;//如果全是连续数字返回true
        boolean isNumeric = true;//如果全是数字返回true
        for (int i = 0; i < numOrStr.length(); i++) {
            if (!Character.isDigit(numOrStr.charAt(i))) {
                isNumeric = false;
                break;
            }
        }
        if (isNumeric) {//如果全是数字则执行是否连续数字判断
            for (int i = 0; i < numOrStr.length(); i++) {
                if (i > 0) {//判断如123456
                    int num = Integer.parseInt(numOrStr.charAt(i) + "");
                    int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") + 1;
                    if (num != num_) {
                        flag = false;
                        break;
                    }
                }
            }
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * 不能是连续的数字--递减（如：987654、876543）
     *
     * @param numOrStr
     * @return 连续数字返回true
     */
    public static boolean isOrderNumeric_(String numOrStr) {
        boolean flag = true;//如果全是连续数字返回true
        boolean isNumeric = true;//如果全是数字返回true
        for (int i = 0; i < numOrStr.length(); i++) {
            if (!Character.isDigit(numOrStr.charAt(i))) {
                isNumeric = false;
                break;
            }
        }
        if (isNumeric) {//如果全是数字则执行是否连续数字判断
            for (int i = 0; i < numOrStr.length(); i++) {
                if (i > 0) {//判断如654321
                    int num = Integer.parseInt(numOrStr.charAt(i) + "");
                    int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") - 1;
                    if (num != num_) {
                        flag = false;
                        break;
                    }
                }
            }
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * 是否全是数字
     *
     * @param numOrStr
     * @return
     */
    public static boolean isNumeric(String numOrStr) {
        boolean isNumeric = true;//如果全是数字返回true
        for (int i = 0; i < numOrStr.length(); i++) {
            if (!Character.isDigit(numOrStr.charAt(i))) {
                isNumeric = false;
                break;
            }
        }
        return isNumeric;
    }

    /**
     * 限制输入字符长度
     *
     * @param str
     * @param maxLen
     * @return
     */
    public static String handleText(String str, int maxLen) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        int count = 0;
        int endIndex = 0;
        for (int i = 0; i < str.length(); i++) {
            char item = str.charAt(i);
            if (item < 128) {
                count = count + 1;
            } else {
                count = count + 2;
            }
            if (maxLen == count || (item >= 128 && maxLen + 1 == count)) {
                endIndex = i;
            }
        }
        if (count <= maxLen) {
            return str;
        } else {
            return str.substring(0, endIndex + 1);
        }

    }


    /**
     * 验证身份证号码
     *
     * @param idCard 居民身份证号码18位，第一位不能为0，最后一位可能是数字或字母，中间16位为数字 \d同[0-9]
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkIdCard(String idCard) {
        String regex = "[1-9]\\d{16}[a-zA-Z0-9]{1}";
        return Pattern.matches(regex, idCard);
    }

    /**
     * 根据视频url获取视频时间
     */
    public static String getVideoTimeByUrl(Context context, String videoUrl){
        if (TextUtils.isEmpty(videoUrl))return "";
        String time = "";
        MediaMetadataRetriever retriever = new MediaMetadataRetriever(); //获取网络视频
        try {
            retriever.setDataSource(videoUrl, new HashMap<String, String>());
            time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return time;
    }

    /**
     * 检测是否安装
     *
     * @param packageName 应用包名
     */
    public static boolean isPackageInstall(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 启动到应用商店app详情界面
     * @param appPkg    目标App的包名
     * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败
     */
    public static void launchAppDetail(Context mContext, String appPkg, String marketPkg) {
        try {
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否为今天(效率比较高)
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     */
    public static boolean isToday(String day) {
        try {

            Calendar pre = Calendar.getInstance();
            Date predate = new Date(System.currentTimeMillis());
            pre.setTime(predate);
            Calendar cal = Calendar.getInstance();
            Date date = getDateFormat().parse(day);
            cal.setTime(date);

            if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
                int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                        - pre.get(Calendar.DAY_OF_YEAR);

                if (diffDay == 0) {
                    return true;
                }
            }
        } catch (ParseException e) {
            LogUtils.e(e.getMessage());
        }
        return false;
    }

    private static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }

    /**
     * 获取drawable目录下图片路径
     * @param id
     * @return
     */
    public static String getResourcesUri(@DrawableRes int id, Context context) {
        Resources resources = context.getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
        return uriPath;
    }
}
