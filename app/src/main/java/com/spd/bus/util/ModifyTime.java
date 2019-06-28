package com.spd.bus.util;

import android.annotation.SuppressLint;
import android.util.Log;

import com.spd.base.utils.LogUtils;
import com.yht.q6jni.Jni;

import java.util.Calendar;

/**
 * Class: ModifyTime
 * package：com.yihuatong.tjgongjiaos.activity
 * Created by hzjst on 2018/3/2.
 * E_mail：hzjstning@163.com
 * Description：修改k21时间以及修改android时间
 */
public class ModifyTime {
    public static int years;
    public static int month;
    public static int date;
    public static int hour;
    public static int min;

    public static final String LOGs = "utils-ModifyTime.java：";

    @SuppressLint("LongLogTag")
    public static String JudgmentTime() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        byte month = (byte) (c.get(Calendar.MONTH) + 1);
        byte date = (byte) c.get(Calendar.DATE);
        byte hour = (byte) c.get(Calendar.HOUR_OF_DAY);
        byte min = (byte) c.get(Calendar.MINUTE);
        byte sec = (byte) c.get(Calendar.SECOND);
        Log.i(LOGs + " :", year + "-" + month + "-" + date + "  " + hour
                + ":" + min + ":" + sec);
        String years = String.valueOf(year);
        String year_l2 = years.substring(0, 2);
        String year_r2 = years.substring(2, 4);
        byte[] setTime = new byte[8];
        setTime[0] = Byte.parseByte(year_l2);
        setTime[1] = Byte.parseByte(year_r2);
        LogUtils.i("year_l2==" + setTime[0] + "\nyear_r2==" + setTime[1]);
        setTime[2] = month;
        setTime[3] = date;
        setTime[4] = hour;
        setTime[5] = min;
        setTime[6] = sec;
        int time = Jni.SetTime(setTime);
        if (time == 0) {
            return "Setting success";
        } else {
            return "Setting failed";
        }

    }

    public static String getTimes() {
        byte[] gettime = new byte[8];
        int retTime = com.yht.q6jni.Jni.GetTime(gettime);
        if (retTime != 0) {
            years = gettime[0] * 100 + gettime[1];
            month = gettime[2];
            date = gettime[3];
            hour = gettime[4];
            min = gettime[5];
        }
        return years + month + date + hour + min + "00";
    }

}
