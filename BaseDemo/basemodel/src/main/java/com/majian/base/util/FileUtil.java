package com.majian.base.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;

/**
 *
 * Created by majian on 2018/9/29.
 */

public class FileUtil {
    private static String downloadFilePath;
    private static String videoFilePath;
    private static String audioFilePath;
    private static String cacheFilePath;

    private FileUtil() {
    }

    /**
     * 获取下载文件本地路径
     * @return
     */
    public static String getDownloadFilePath(Context mContext) {
        if (TextUtils.isEmpty(downloadFilePath)){
            downloadFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                    + File.separator + mContext.getPackageName() + File.separator;
        }
        File file = new File(downloadFilePath);
        if (!file.exists()){
            file.mkdirs();
        }
        return downloadFilePath;
    }

    /**
     * 获取视频缓存文件路径（DCIM）
     * @return
     */
    public static String getVideoCachePath(Context mContext) {
        if (TextUtils.isEmpty(videoFilePath)){
            videoFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + mContext.getPackageName() + File.separator + "video" + File.separator;
        }
        File file = new File(videoFilePath);
        if (!file.exists()){
            file.mkdirs();
        }
        return videoFilePath;
    }

    /**
     * 获取视频缓存路径URI
     * @return
     */
    public static Uri getVideoCacheUri(Context mContext) {
        if (TextUtils.isEmpty(videoFilePath)){
            videoFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + mContext.getPackageName() + File.separator + "video" + File.separator;
        }
        File file = new File(videoFilePath);
        if (!file.exists()){
            file.mkdirs();
        }
        return getUriFromFile(mContext, file);
    }

    /**
     * 获取音频缓存文件路径（DCIM）
     * @return
     */
    public static String getAudioCachePath(Context mContext) {
        if (TextUtils.isEmpty(audioFilePath)){
            audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + mContext.getPackageName() + File.separator + "audio" + File.separator;
        }
        File file = new File(audioFilePath);
        if (!file.exists()){
            file.mkdirs();
        }
        return audioFilePath;
    }

    /**
     * 获取缓存文件路径
     * @param mContext
     * @return
     */
    public static String getCacheFilePath(Context mContext) {
        if (TextUtils.isEmpty(cacheFilePath)){
            cacheFilePath = mContext.getExternalCacheDir().getAbsolutePath() + File.separator;
        }
        return cacheFilePath;
    }

    public static Uri getUriFromFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".FileProvider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }
}
