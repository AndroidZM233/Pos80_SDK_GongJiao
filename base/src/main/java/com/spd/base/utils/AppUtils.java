package com.spd.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

/**
 * Created by 张明_ on 2019/4/27.
 * Email 741183142@qq.com
 */
public class AppUtils {
    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }


    public static String getDeviceSN() {

        String serialNumber = android.os.Build.SERIAL;

        return serialNumber;

    }

    public static String getDeviceSN2(){

        String serial = null;

        try {

            Class<?> c =Class.forName("android.os.SystemProperties");
            Method get =c.getMethod("get", String.class);
            serial = (String)get.invoke(c, "ro.serialno");
        } catch (Exception e) {

            e.printStackTrace();

        }

        return serial;

    }

    /**
     * 设备IMEI(355546057471164)
     * 6.0上没有权限会崩溃
     * IMEI是International Mobile Equipment Identity （国际移动设备标识）的简称
     * IMEI由15位数字组成的"电子串号"，它与每台手机一一对应，而且该码是全世界唯一的
     * 其组成为：
     * 1. 前6位数(TAC)是"型号核准号码"，一般代表机型
     * 2. 接着的2位数(FAC)是"最后装配号"，一般代表产地
     * 3. 之后的6位数(SNR)是"串号"，一般代表生产顺序号
     * 4. 最后1位数(SP)通常是"0"，为检验码，目前暂备用
     *
     * @return String
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getIMEI(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getApplicationContext().
                getSystemService(Context.TELEPHONY_SERVICE);

        String imei = null;
        if (mTelephonyManager != null) {
            try {
                imei = mTelephonyManager.getDeviceId();
            } catch (Exception e) {
                return "";
            }
        }
        return imei;
    }

    /**
     * SIM卡序列号(89860113871048601206)
     *
     * @return String
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getSIMCardSerial(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getApplicationContext().
                getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyManager != null) {
            return mTelephonyManager.getSimSerialNumber();
        }
        return null;
    }

    /**
     * SIM卡Id(460013242301689)
     *
     * @return String
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getSIMCardId(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getApplicationContext().
                getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyManager != null) {
            return mTelephonyManager.getSubscriberId();
        }
        return null;
    }
}
