package com.spd.yinlianpay.util;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class TripleDes {

    private static final int iSelePM1[] = { // 置换选择1的矩阵
            57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43,
            35, 27, 19, 11, 3, 60, 52, 44, 36, 63, 55, 47, 39, 31, 23, 15, 7,
            62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28,
            20, 12, 4 };

    private static final int iSelePM2[] = { // 置换选择2的矩阵
            14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7,
            27, 20, 13, 2, 41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44,
            49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32 };

    private static final int iROLtime[] = { // 循环左移位数表
            1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };

    private static final int iInitPM[] = { // 初始置换IP
            58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46,
            38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33,
            25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29,
            21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7 };

    private static final int iInvInitPM[] = { // 初始逆置换
            40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46,
            14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12,
            52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50,
            18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25 };

    private static final int iEPM[] = { // 选择运算E
            32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15,
            16, 17, 16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26,
            27, 28, 29, 28, 29, 30, 31, 32, 1 };

    private static final int iPPM[] = { // 置换运算P
            16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14,
            32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25 };

    private static final int iSPM[][] = { // 8个S盒
            { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 0, 15, 7,
                    4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 4, 1, 14, 8,
                    13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 8, 2, 4,
                    9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 },
            { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, 3, 13, 4,
                    7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5, 0, 14, 7, 11,
                    10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15, 13, 8, 10, 1, 3,
                    15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 },
            { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, 13, 7, 0,
                    9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1, 13, 6, 4, 9, 8,
                    15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7, 1, 10, 13, 0, 6, 9,
                    8, 7, 4, 15, 14, 3, 11, 5, 2, 12 },
            { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, 13, 8, 11,
                    5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9, 10, 6, 9, 0, 12,
                    11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4, 3, 15, 0, 6, 10, 1,
                    13, 8, 9, 4, 5, 11, 12, 7, 2, 14 },
            { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, 14, 11, 2,
                    12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6, 4, 2, 1, 11, 10,
                    13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14, 11, 8, 12, 7, 1, 14,
                    2, 13, 6, 15, 0, 9, 10, 4, 5, 3 },
            { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, 10, 15, 4,
                    2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8, 9, 14, 15, 5, 2,
                    8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6, 4, 3, 2, 12, 9, 5, 15,
                    10, 11, 14, 1, 7, 6, 0, 8, 13 },
            { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, 13, 0, 11,
                    7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6, 1, 4, 11, 13,
                    12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2, 6, 11, 13, 8, 1, 4,
                    10, 7, 9, 5, 0, 15, 14, 2, 3, 12 },
            { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, 1, 15, 13,
                    8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2, 7, 11, 4, 1, 9,
                    12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8, 2, 1, 14, 7, 4, 10,
                    8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } };

    private static int[] iCipherKey = new int[64];
    private static int[] iCKTemp = new int[56];
    private static int[] iPlaintext = new int[64];
    private static int[] iCiphertext = new int[64];
    private static int[] iPKTemp = new int[64];
    private static int[] iL = new int[32];
    private static int[] iR = new int[32];

    // 数组置换
    // iSource与iDest的大小不一定相等
    private static void permu(int[] iSource, int[] iDest, int[] iPM) {
        for (int i = 0; i < iPM.length; i++)
            iDest[i] = iSource[iPM[i] - 1];
    }

    // 将字节数组进行 位-〉整数 压缩
    // 例如：{0x35,0xf3}->{0,0,1,1,0,1,0,1,1,1,1,1,0,0,1,1}, bArray->iArray
    private static void arrayBitToI(byte[] bArray, int[] iArray) {
        for (int i = 0; i < iArray.length; i++) {
            iArray[i] = (int) (bArray[i / 8] >> (7 - i % 8) & 0x01);
        }
    }

    // 将整形数组进行 整数-〉位 压缩
    // arrayBitToI的逆变换,iArray->bArray
    private static void arrayIToBit(byte[] bArray, int[] iArray) {
        for (int i = 0; i < bArray.length; i++) {
            bArray[i] = (byte) iArray[8 * i];
            for (int j = 1; j < 8; j++) {
                bArray[i] = (byte) (bArray[i] << 1);
                bArray[i] += (byte) iArray[8 * i + j];
            }
        }
    }

    // 数组的逐项模2加
    // array1[i]=array1[i]^array2[i]
    private static void arrayM2Add(int[] array1, int[] array2) {
        for (int i = 0; i < array2.length; i++) {
            array1[i] ^= array2[i];
        }
    }

    // 一个数组等分成两个数组-数组切割
    private static void arrayCut(int[] iSource, int[] iDest1, int[] iDest2) {
        int k = iSource.length;
        for (int i = 0; i < k / 2; i++) {
            iDest1[i] = iSource[i];
            iDest2[i] = iSource[i + k / 2];
        }
    }

    // 两个等大的数组拼接成一个
    // arrayCut的逆变换
    private static void arrayComb(int[] iDest, int[] iSource1, int[] iSource2) {
        int k = iSource1.length;
        for (int i = 0; i < k; i++) {
            iDest[i] = iSource1[i];
            iDest[i + k] = iSource2[i];
        }
    }

    // 子密钥产生算法中的循环左移
    private static void ROL(int[] array) {
        int temp = array[0];
        for (int i = 0; i < 27; i++) {
            array[i] = array[i + 1];
        }
        array[27] = temp;

        temp = array[28];
        for (int i = 0; i < 27; i++) {
            array[28 + i] = array[28 + i + 1];
        }
        array[55] = temp;
    }

    // 16个子密钥完全倒置
    private static int[][] invSubKeys(int[][] iSubKeys) {
        int[][] iInvSubKeys = new int[16][48];
        for (int i = 0; i < 16; i++)
            for (int j = 0; j < 48; j++)
                iInvSubKeys[i][j] = iSubKeys[15 - i][j];
        return iInvSubKeys;
    }

    // S盒代替
    // 输入输出皆为部分数组，因此带偏移量
    private static void Sbox(int[] iInput, int iOffI, int[] iOutput, int iOffO,
                             int[] iSPM) {
        int iRow = iInput[iOffI] * 2 + iInput[iOffI + 5]; // S盒中的行号
        int iCol = iInput[iOffI + 1] * 8 + iInput[iOffI + 2] * 4
                + iInput[iOffI + 3] * 2 + iInput[iOffI + 4];
        // S盒中的列号
        int x = iSPM[16 * iRow + iCol];
        iOutput[iOffO] = x >> 3 & 0x01;
        iOutput[iOffO + 1] = x >> 2 & 0x01;
        iOutput[iOffO + 2] = x >> 1 & 0x01;
        iOutput[iOffO + 3] = x & 0x01;
    }

    // 加密函数f
    private static int[] encFunc(int[] iInput, int[] iSubKey) {
        int iTemp1[] = new int[48];
        int iTemp2[] = new int[32];
        int iOutput[] = new int[32];
        permu(iInput, iTemp1, iEPM);
        arrayM2Add(iTemp1, iSubKey);
        for (int i = 0; i < 8; i++)
            Sbox(iTemp1, i * 6, iTemp2, i * 4, iSPM[i]);
        permu(iTemp2, iOutput, iPPM);
        return iOutput;
    }

    // 子密钥生成
    private static int[][] makeSubKeys(byte[] bCipherKey) {
        int[][] iSubKeys = new int[16][48];
        arrayBitToI(bCipherKey, iCipherKey);
        //int[] tmp = iCipherKey;
        permu(iCipherKey, iCKTemp, iSelePM1);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < iROLtime[i]; j++)
                ROL(iCKTemp);
            permu(iCKTemp, iSubKeys[i], iSelePM2);
        }
        return iSubKeys;
    }

    // 加密
    private static byte[] encrypt(byte[] bPlaintext, int[][] iSubKeys) {
        byte bCiphertext[] = new byte[8];
        arrayBitToI(bPlaintext, iPlaintext);
        permu(iPlaintext, iPKTemp, iInitPM);
        arrayCut(iPKTemp, iL, iR);
        for (int i = 0; i < 16; i++) {
            if (i % 2 == 0) {
                arrayM2Add(iL, encFunc(iR, iSubKeys[i]));
            } else {
                arrayM2Add(iR, encFunc(iL, iSubKeys[i]));
            }
        }
        arrayComb(iPKTemp, iR, iL);
        permu(iPKTemp, iCiphertext, iInvInitPM);
        arrayIToBit(bCiphertext, iCiphertext);
        return bCiphertext;
    }

    // 解密
    private static byte[] decrypt(byte[] bCiphertext, int[][] iSubKeys) {
        int[][] iInvSubKeys = invSubKeys(iSubKeys);
        return encrypt(bCiphertext, iInvSubKeys);
    }

    // Bit XOR
    private static byte[] BitXor(byte[] Data1, byte[] Data2, int Len) {
        int i;
        byte Dest[] = new byte[Len];

        for (i = 0; i < Len; i++)
            Dest[i] = (byte) (Data1[i] ^ Data2[i]);

        return Dest;
    }

    private static String byte2hex(byte[] b) { //一个字节的数，
        // 转成16进制字符串
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            //整数转成十六进制表示
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toUpperCase(); //转成大写
    }

    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("长度不是偶数");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }
    /**
     * 3DES加密
     * @see [类、类#方法、类#成员]
     * @since [1.0]
     * @param strKey
     * @param strEncData
     * @return
     */
    public static String encryptDES(String strKey, String strEncData)
    {
        String strKey1;
        String strKey2;
        String strTemp1;
        String strTemp2;

        if ((strKey.length()) != 32) {
            throw new IllegalArgumentException("密钥长度不正确,必须为32");
        }
        if ((strEncData.length()) != 32) {
            throw new IllegalArgumentException("数据明文长度不正确,必须为32");
        }

        strKey1 = strKey.substring(0, 16);
        strKey2 = strKey.substring(16, 32);
        strTemp1 = strEncData.substring(0, 16);
        strTemp2 = strEncData.substring(16, 32);

        byte[] cipherKey1 = hex2byte(strKey1.getBytes()); //3DES的密钥K1
        byte[] cipherKey2 = hex2byte(strKey2.getBytes()); //3DES的密钥K2

        byte[] bCiphertext1 = hex2byte(strTemp1.getBytes()); //数据1
        byte[] bCiphertext2 = hex2byte(strTemp2.getBytes()); //数据1

        int[][] subKeys1 = new int[16][48]; //用于存放K1产生的子密钥
        int[][] subKeys2 = new int[16][48]; //用于存放K2产生的子密钥
        subKeys1 = makeSubKeys(cipherKey1);
        subKeys2 = makeSubKeys(cipherKey2);

        byte[] bTemp11 = encrypt(bCiphertext1, subKeys1);
        byte[] bTemp21 = decrypt(bTemp11, subKeys2);
        byte[] bPlaintext11 = encrypt(bTemp21, subKeys1);

        byte[] bTemp12 = encrypt(bCiphertext2, subKeys1);
        byte[] bTemp22 = decrypt(bTemp12, subKeys2);
        byte[] bPlaintext12 = encrypt(bTemp22, subKeys1);

        return byte2hex(bPlaintext11) + byte2hex(bPlaintext12);
    }
    /**
     * 3DES解密
     * @see [类、类#方法、类#成员]
     * @since [1.0]
     * @param strKey
     * @param strEncData
     * @return
     */
    public static String decryptDES(String strKey, String strEncData)
    {
        String strKey1;
        String strKey2;
        String strTemp1;
        String strTemp2;

        if ((strKey.length()) != 32) {
            throw new IllegalArgumentException("密钥长度不正确,必须为32");
        }
        if ((strEncData.length()) != 32) {
            throw new IllegalArgumentException("数据密文长度不正确,必须为32");
        }

        strKey1 = strKey.substring(0, 16);
        strKey2 = strKey.substring(16, 32);
        strTemp1 = strEncData.substring(0, 16);
        strTemp2 = strEncData.substring(16, 32);

        byte[] cipherKey1 = hex2byte(strKey1.getBytes()); //3DES的密钥K1
        byte[] cipherKey2 = hex2byte(strKey2.getBytes()); //3DES的密钥K2

        byte[] bCiphertext1 = hex2byte(strTemp1.getBytes()); //数据1
        byte[] bCiphertext2 = hex2byte(strTemp2.getBytes()); //数据1

        int[][] subKeys1 = new int[16][48]; //用于存放K1产生的子密钥
        int[][] subKeys2 = new int[16][48]; //用于存放K2产生的子密钥
        subKeys1 = makeSubKeys(cipherKey1);
        subKeys2 = makeSubKeys(cipherKey2);

        byte[] bTemp11 = decrypt(bCiphertext1, subKeys1);
        byte[] bTemp21 = encrypt(bTemp11, subKeys2);
        byte[] bPlaintext11 = decrypt(bTemp21, subKeys1);

        byte[] bTemp12 = decrypt(bCiphertext2, subKeys1);
        byte[] bTemp22 = encrypt(bTemp12, subKeys2);
        byte[] bPlaintext12 = decrypt(bTemp22, subKeys1);

        return byte2hex(bPlaintext11) + byte2hex(bPlaintext12);
    }
}
