package com.majian.base.model;

import android.util.Log;

import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import com.majian.base.util.ToolUtil;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.platform.Platform;

/**
 * Created by majian on 2018/7/23.
 */

public class MHttpClientHelper {

    public final static int CONNECT_TIMEOUT =5;
    public final static int READ_TIMEOUT=20;
    public final static int WRITE_TIMEOUT=20;

    public static OkHttpClient getOkHttpClient(){
        //设置缓存 10M
        //创建OkHttpClient，并添加拦截器和缓存代码
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                .addInterceptor(AccountPort)
                .addNetworkInterceptor(new MyLoggingInterceptor())
                .addInterceptor(new LoggingInterceptor.Builder()
//                        .loggable(BuildConfig.DEBUG)
                        .setLevel(Level.BASIC)
                        .log(Platform.INFO)
                        .request("Request")
                        .response("Response")
                        .addHeader("deviceType", "Android")
                        .build())
                .build();
        return client;
    }

    private static final Interceptor AccountPort = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse;
            Request request = chain.request();
            originalResponse = chain.proceed(request);
            String content= originalResponse.body().string();
            MediaType mediaType = originalResponse.body().contentType();
            return originalResponse.newBuilder().body(ResponseBody.create(mediaType, content)).build();
        }
    };


    /**
     * 请求日志拦截器（form表单提交参数）
     * Created by baishixian on 2017/3/13.
     */

    public static class MyLoggingInterceptor implements Interceptor {

        @Override public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            Response response = chain.proceed(chain.request());

            ResponseBody responseBody = response.body();
            if (responseBody == null){
                return response;
            }
            MediaType mediaType = responseBody.contentType();
            String content = responseBody.string();
            String method=request.method();
            if("POST".equals(method)){
                StringBuilder sb = new StringBuilder();
                Map<String, String> map = new HashMap<>();
                if (request.body() instanceof FormBody) {
                    FormBody body = (FormBody) request.body();
                    if (body != null) {
                        for (int i = 0; i < body.size(); i++) {
                            map.put(body.encodedName(i), body.encodedValue(i));
                            sb.append(body.encodedName(i)).append("=").append(body.encodedValue(i)).append(",");
                        }
                    }
                    sb.delete(sb.length() - 1, sb.length());
//                    Log.i("Request", "请求参数:{"+sb.toString()+"}");URLDecoder.decode(ToolUtil.mapToJson(map), "utf-8")
                    Log.i("Request", "请求参数:"+ URLDecoder.decode(ToolUtil.mapToJson(map), "utf-8")+"");
//                    LogUtils.i("请求参数:{"+sb.toString()+"}");
                }
            }
            return response.newBuilder()
                    .body(ResponseBody.create(mediaType, content))
                    .build();
        }
    }
}
