package com.spd.bus.spdata.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.net.ssl.SSLContext;

/**
 * Created by 张明_ on 2018/8/30.
 * Email 741183142@qq.com
 */

public class TimeDataUtils {

    public static String getUTCtimes() {
        long times = System.currentTimeMillis() / 1000L;
        String bytetime = Long.toHexString(times).toUpperCase();
        Log.i("sss", "main: " + bytetime);
        return bytetime;
    }

    public static String getNowTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR));
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        String mHh = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mMm = String.valueOf(c.get(Calendar.MINUTE));
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        return format.format(c.getTime()) + "    " + "星期" + mWay + "    " + format2.format(c.getTime());
    }
}
