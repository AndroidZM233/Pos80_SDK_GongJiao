package com.spd.yinlianpay.util;

/**
 * Created by Administrator on 2017/4/10.
 */

public class StringUtils {

    /**
     * String 非空判断
     * @param msg
     * @return
     */
    public static boolean isEmpty(String msg){
        boolean is;
        if (msg != null&&!"".equals(msg)) {
            is = false;
        }else {
            is = true;
        }
        return is;
    }

    // 字符串转BCD，只传字符串即可
    public static byte[] string2BCD(String str) {

        return string2BCD(str, str.length());
    }

    // 字符串转BCD
    public static byte[] string2BCD(String str, int numlen) {
        if (numlen % 2 != 0)
            numlen++;

        while (str.length() < numlen) {
            str = "0" + str;
        }

        byte[] bStr = new byte[str.length() / 2];
        char[] cs = str.toCharArray();
        int i = 0;
        int iNum = 0;
        for (i = 0; i < cs.length; i += 2) {

            int iTemp = 0;
            if (cs[i] >= '0' && cs[i] <= '9') {
                iTemp = (cs[i] - '0') << 4;
            } else {

                if (cs[i] >= 'a' && cs[i] <= 'f') {
                    cs[i] -= 32;
                }
                iTemp = (cs[i] - '0' - 7) << 4;
            }

            if (cs[i + 1] >= '0' && cs[i + 1] <= '9') {
                iTemp += cs[i + 1] - '0';
            } else {

                if (cs[i + 1] >= 'a' && cs[i + 1] <= 'f') {
                    cs[i + 1] -= 32;
                }
                iTemp += cs[i + 1] - '0' - 7;
            }
            bStr[iNum] = (byte) iTemp;
            iNum++;

        }
        return bStr;

    }
}
