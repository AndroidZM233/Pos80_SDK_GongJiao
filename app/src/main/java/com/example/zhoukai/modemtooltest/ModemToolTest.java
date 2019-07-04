package com.example.zhoukai.modemtooltest;

/**
 * Created by hzjst on 2018/2/9.
 */

public class ModemToolTest {
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native String stringFromJNI();

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is used to get the value of nv items listed in {@link NvConstants}.
     */
    public static native String getItem(int nvid);
}
