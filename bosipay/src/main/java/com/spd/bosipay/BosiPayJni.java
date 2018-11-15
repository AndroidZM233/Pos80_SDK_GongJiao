package com.spd.bosipay;

public class BosiPayJni {

    public native int initdev();

    static {
        System.loadLibrary("bosipay");
    }
}
