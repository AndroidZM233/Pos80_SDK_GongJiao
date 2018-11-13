package com.spd.bus.util;

import android.text.TextUtils;

/**
 * 数值处理工具函数
 * @author baoxl
 *
 */
public class NumberUtil {
    private NumberUtil() {}

    /**
     * 字符串转长整型数值表示
     * @param sLong 数值字符串
     * @return 数值表示
     */
    public static long parseLong(String sLong) {
        if (TextUtils.isEmpty(sLong)) {
            return 0;
        }

        long result = 0;
        try {
            result = Long.parseLong(sLong);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 字符串转整型数值表示
     * @param sInt 数值字符串
     * @return 数值表示
     */
    public static int parseInt(String sInt) {
        if (TextUtils.isEmpty(sInt)) {
            return 0;
        }

        int result = 0;
        try {
            result = Integer.parseInt(sInt);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }
}

