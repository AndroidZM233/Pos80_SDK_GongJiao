package com.example.test.yinlianbarcode.utils;


import java.util.Arrays;

/**
 * byte数据转换
 * Created by 张明_ on 2017/8/21.
 * Email 741183142@qq.com
 */

public class ByteUtils {

    /**
     * byte[] --> 16进制String
     * byte[]{0x2B, 0x44, 0xEF,0xD9} --> "2B44EFD9"
     *
     * @param byteArray byte[]
     * @return 16进制String
     */
    public static String toHexString(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();
        if (byteArray == null || byteArray.length <= 0) {
            return null;
        }
        for (byte aSrc : byteArray) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

    /**
     * byte[] -> ascii String
     * {0x71,0x72,0x73,0x41,0x42}->"qrsAB"
     *
     * @param byteArray byte[]
     * @return String
     */
    public static String toAsciiString(byte[] byteArray) {
        int byteLength = byteArray.length;
        StringBuilder tStringBuf = new StringBuilder();
        String nRcvString;
        char[] tChars = new char[byteLength];
        for (int i = 0; i < byteLength; i++) {
            tChars[i] = (char) byteArray[i];
        }
        tStringBuf.append(tChars);
        nRcvString = tStringBuf.toString();
        return nRcvString;
    }

    /**
     * bytes转int(10)
     *
     * @param bytes
     * @return
     */
    public static int toInt(byte[] bytes) {
        String hexString = toHexString(bytes);
        return Integer.parseInt(hexString, 16);
    }

    public static byte[] int2Bytes(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[len - i - 1] = (byte)((value >> 8 * i) & 0xff);
        }
        return b;
    }

    public static int bytes2Int(byte[] b, int start, int len) {
        int sum = 0;
        int end = start + len;
        for (int i = start; i < end; i++) {
            int n = ((int)b[i]) & 0xff;
            n <<= (--len) * 8;
            sum += n;
        }
        return sum;
    }

    /**
     * 从bytes上截取一段
     *
     * @param bytes  母体
     * @param off    起始
     * @param length 个数
     * @return byte[]
     */
    public static byte[] arrayCopy(byte[] bytes, int off, int length) {
        byte[] bytess = new byte[length];
        System.arraycopy(bytes, off, bytess, 0, length);
        return bytess;
    }

    /**
     * 多个数组合并
     *
     * @param first
     * @param rest
     * @return
     */
    public static byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

}
