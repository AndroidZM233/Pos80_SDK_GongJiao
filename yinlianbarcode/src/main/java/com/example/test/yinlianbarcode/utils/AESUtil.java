package com.example.test.yinlianbarcode.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    private static final String KEY_AES = "AES";


    public static String KEY="speedata_speedta";
    /**
     * 加密
     *
     * @param src
     * @param key
     * @return
     * @throws Exception
     */
    public static String encrypt(String src, String key) throws Exception {
        if (key == null || key.length() != 16) {
            throw new Exception("key格式不正确");
        }
        byte[] raw = key.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);
        Cipher cipher = Cipher.getInstance(KEY_AES);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(src.getBytes());
        return byte2hex(encrypted);
    }

    /**
     * 解密
     *
     * @param src
     * @param key
     * @return
     * @throws Exception
     */
    public static String decrypt(String src, String key) throws Exception {
        if (key == null || key.length() != 16) {
            throw new Exception("key长度不正确");
        }
        byte[] raw = key.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);
        Cipher cipher = Cipher.getInstance(KEY_AES);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] encrypted1 = hex2byte(src);
        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original);
        return originalString;
    }

    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
        }
        return b;
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    public static void main(String[] args) throws Exception {
        String encrypt = encrypt("ddspeedatadsfsffdfwefwe1212301212312321", "speedata_speedta");
        String decrypt = decrypt(encrypt, "speedata_speedta");
        System.out.println("加密===" + encrypt);
        System.out.println("解密===" + decrypt);
    }
}
