package com.spd.base.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 张明_ on 2019/2/18.
 * Email 741183142@qq.com
 */
public class DateUtils {
    /**
     * 无符号
     */
    public static String FORMAT_yyyyMMddHHmmss = "yyyyMMddHHmmss";

    /**
     * 英文简写如：2010
     */
    public static String FORMAT_Y = "yyyy";

    /**
     * 英文简写如：12:01
     */
    public static String FORMAT_HM = "HH:mm";

    /**
     * 英文简写如：1-12 12:01
     */
    public static String FORMAT_MDHM = "MM-dd HH:mm";

    /**
     * 英文简写（默认）如：2010-12-01
     */
    public static String FORMAT_YMD = "yyyy-MM-dd";

    /**
     * 英文全称  如：2010-12-01 23:15
     */
    public static String FORMAT_YMDHM = "yyyy-MM-dd HH:mm";

    /**
     * 英文全称  如：2010-12-01 23:15:06
     */
    public static String FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 精确到毫秒的完整时间    如：2017-08-22 16:06:59.735
     */
    public static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 中文简写  如：2010年12月01日
     */
    public static String FORMAT_YMD_CN = "yyyy年MM月dd日";

    /**
     * 中文简写  如：2010年12月01日  12时
     */
    public static String FORMAT_YMDH_CN = "yyyy年MM月dd日 HH时";

    /**
     * 中文简写  如：2010年12月01日  12时12分
     */
    public static String FORMAT_YMDHM_CN = "yyyy年MM月dd日 HH时mm分";

    /**
     * 中文全称  如：2010年12月01日  23时15分06秒
     */
    public static String FORMAT_YMDHMS_CN = "yyyy年MM月dd日  HH时mm分ss秒";

    /**
     * 精确到毫秒的完整中文时间
     */
    public static String FORMAT_FULL_CN = "yyyy年MM月dd日  HH时mm分ss秒SSS毫秒";

    /**
     * 时间到秒+周几 如：2017-08-22 16:02:38 周二
     */
    public static String FORMAT_E = "yyyy-MM-dd HH:mm:ss E";


    /**
     * 获取当前日期
     *
     * @param format 日期格式
     * @return String
     */
    public static String getCurrentTimeMillis(String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        long currentTimeMillis = System.currentTimeMillis();
        Date date = new Date(currentTimeMillis);
        return dateFormat.format(date);
    }

    /**
     * 把long 转换成 日期
     */
    public static Date transferLongToDate(String dateFormat, Long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return new Date(millSec);
    }

    /**
     * 把long 转换成 日期 再转换成String类型
     */
    public static String transferLongToString(String dateFormat, Long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    /**
     * 转换时间日期格式字串为long型
     *
     * @param time 格式为：yyyy-MM-dd HH:mm的时间日期类型
     */
    public static Long convertTimeToLong(String format, String time) {
        Date date = null;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf =
                    new SimpleDateFormat(format);
            date = sdf.parse(time);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }


    /**
     * 功能描述：返回年
     *
     * @param date Date 日期
     * @return 返回年
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 功能描述：返回当前年
     *
     * @return 返回当前年
     */
    public static int getYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }


    /**
     * 功能描述：返回月
     *
     * @param date Date 日期
     * @return 返回月份
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 功能描述：返回月
     *
     * @return 返回月份
     */
    public static int getMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }


    /**
     * 功能描述：返回日
     *
     * @param date Date 日期
     * @return 返回日份
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 功能描述：返回当前日
     *
     * @return 返回日份
     */
    public static int getDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 功能描述：返回小
     *
     * @param date 日期
     * @return 返回小时
     */
    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 功能描述：返回当前小时
     *
     * @return 返回当前小时
     */
    public static int getHour() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 功能描述：返回分
     *
     * @param date 日期
     * @return 返回分钟
     */
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 功能描述：返回当前分钟
     *
     * @return 返回当前分钟
     */
    public static int getMinute() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 返回秒钟
     *
     * @param date Date 日期
     * @return 返回秒钟
     */
    public static int getSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 返回当前秒钟
     *
     * @return 返回秒钟
     */
    public static int getSecond() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 功能描述：返回毫
     *
     * @param date 日期
     * @return 返回毫
     */
    public static long getMillis(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MILLISECOND);
    }

    /**
     * 功能描述：返回当前毫秒
     *
     * @return 返回当前毫秒
     */
    public static long getMillis() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MILLISECOND);
    }

     /*

    获取当前时间之前或之后几分钟 minute

    */

    public static String getTimeByMinute(int minute, String format) {

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MINUTE, minute);

        return new SimpleDateFormat(format).format(calendar.getTime());

    }

    /*

    获取当前时间之前或之后几小时 hour

   */

    public static String getTimeByHour(int hour, String format) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + hour);

        return new SimpleDateFormat(format).format(calendar.getTime());

    }

    /**
     * 是否在时间区域内
     *
     * @param date
     * @param s
     * @return
     * @throws Exception
     */
    public static boolean isBrush(String date, int s) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtils.FORMAT_YMDHMS);
        Date parse = simpleDateFormat.parse(date);
        Calendar c1 = Calendar.getInstance();
        Calendar c3 = Calendar.getInstance();
        c1.setTime(parse);//要判断的日期
        String format1 = simpleDateFormat.format(c1.getTime());
        c3.setTime(new Date());//也给初始日期 把分钟加五
        c1.add(Calendar.SECOND, s);
        String format2 = simpleDateFormat.format(c1.getTime());
        String format3 = simpleDateFormat.format(c3.getTime());
        if (c1.after(c3)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 是否在时间区域内
     *
     * @param date
     * @param s
     * @return
     * @throws Exception
     */
    public static boolean isBrush2(String date, int s) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtils.FORMAT_yyyyMMddHHmmss);
        Date parse = simpleDateFormat.parse(date);
        Calendar c1 = Calendar.getInstance();
        Calendar c3 = Calendar.getInstance();
        c1.setTime(parse);//要判断的日期
        String format1 = simpleDateFormat.format(c1.getTime());
        c3.setTime(new Date());//也给初始日期 把分钟加五
        c1.add(Calendar.SECOND, s);
        String format2 = simpleDateFormat.format(c1.getTime());
        String format3 = simpleDateFormat.format(c3.getTime());
        if (c1.after(c3)) {
            return false;
        } else {
            return true;
        }
    }
}
