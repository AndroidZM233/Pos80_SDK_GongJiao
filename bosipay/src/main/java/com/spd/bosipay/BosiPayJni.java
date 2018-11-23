package com.spd.bosipay;

public class BosiPayJni {
    public native int SetCertFilePath(String path);
    public native String getCertVer();

    public native int UpdateCert(byte[] cerBytes);

    public native int VerifyCode(String qrCode);

    static {
        System.loadLibrary("bosipay");
    }
}
