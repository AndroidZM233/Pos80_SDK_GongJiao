package com.wpos.sdkdemo.util;

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
}
