package com.majian.base.model;

import com.majian.base.util.SystemUtil;

/**
 * Create by maJian on 2020/9/2
 */
public class MRequestBase {
    private String deviceType = "Android";//
    private String deviceBrand = SystemUtil.getDeviceBrand();//手机厂商
    private String deviceModel = SystemUtil.getSystemModel();//设备型号
    private String deviceSystemVersion = SystemUtil.getSystemVersion();//设备系统版本
}
