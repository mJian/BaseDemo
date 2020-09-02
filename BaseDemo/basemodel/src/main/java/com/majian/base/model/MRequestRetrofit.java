package com.majian.base.model;


import com.majian.base.util.LogUtils;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by majian on 2018/7/23.
 * update by maJian on 2019-5-22
 */

public class MRequestRetrofit {
    private static MIRequestService request;
    static {
        LogUtils.e("=======RequestRetrofit初始化==========");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("")
                .client(MHttpClientHelper.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        request = retrofit.create(MIRequestService.class);
    }

    public static MIRequestService getInstance(){
        return request;
    }
}
