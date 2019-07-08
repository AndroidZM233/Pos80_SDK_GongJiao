package com.szxb.jni;

import android.content.res.AssetManager;
import android.util.Log;

/**
 * 作者：hzjst on 2018/5/4 23:27
 * <p>
 * 邮箱：17602657100@163.com
 */
public class libszxb {

    static {
        try {
            System.loadLibrary("ymodem");
        } catch (Throwable e) {
            Log.e("jni", "i can't find ymodem so!");
            e.printStackTrace();
        }
    }

    //更新固件
    public static native int ymodemUpdate(AssetManager ass, String filename);

}
