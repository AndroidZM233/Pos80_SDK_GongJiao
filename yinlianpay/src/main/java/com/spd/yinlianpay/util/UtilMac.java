package com.spd.yinlianpay.util;

public class UtilMac {
    public static String stringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu.append(Integer.toHexString((int)chars[i]));
        }
        return sbu.toString();
    }

    /**
     *
     * @param oldStr 待填充字符串
     * @param padNum 填充完成后的总长度
     * @param padStr 填充字符
     * @param isLeft 是否左填充 0：右填充 1：左填充
     * @return 返回填充后的字符串
     */
    public static String padStr(String oldStr,int padNum,String padStr,int isLeft)
    {
        String newStr = oldStr;
        if(oldStr.length() >= padNum)
        {
            return oldStr;
        }
        if(isLeft == 0)
        {
            for(int i = 0;i < padNum - oldStr.length();i++)
            {
                newStr = newStr + padStr;
            }
        }
        else
        {
            for(int i = 0;i < padNum - oldStr.length();i++)
            {
                newStr = padStr + newStr;
            }
        }
        return newStr;
    }
    public static String bcd2Str(byte[] b) {
        char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder sb = new StringBuilder(b.length * 2);

        for (int i = 0; i < b.length; ++i) {
            sb.append(HEX_DIGITS[(b[i] & 240) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 15]);
        }

        return sb.toString();
    }

    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }

        byte[] abt = new byte[len];
        if (len >= 2) {
            len /= 2;
        }

        byte[] bbt = new byte[len];
        abt = asc.getBytes();

        for (int p = 0; p < asc.length() / 2; ++p) {
            int j;
            if (abt[2 * p] >= 97 && abt[2 * p] <= 122) {
                j = abt[2 * p] - 97 + 10;
            } else if (abt[2 * p] >= 65 && abt[2 * p] <= 90) {
                j = abt[2 * p] - 65 + 10;
            } else {
                j = abt[2 * p] - 48;
            }

            int k;
            if (abt[2 * p + 1] >= 97 && abt[2 * p + 1] <= 122) {
                k = abt[2 * p + 1] - 97 + 10;
            } else if (abt[2 * p + 1] >= 65 && abt[2 * p + 1] <= 90) {
                k = abt[2 * p + 1] - 65 + 10;
            } else {
                k = abt[2 * p + 1] - 48;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }
    public static byte[] hexString2Bytes(String data) {
        if (data == null) {
            return null;
        } else {
            byte[] result = new byte[(data.length() + 1) / 2];
            if ((data.length() & 1) == 1) {
                data = data + "0";
            }

            for(int i = 0; i < result.length; ++i) {
                result[i] = (byte)(hex2byte(data.charAt(i * 2 + 1)) | hex2byte(data.charAt(i * 2)) << 4);
            }

            return result;
        }
    }

    public static byte hex2byte(char hex) {
        if (hex <= 'f' && hex >= 'a') {
            return (byte)(hex - 97 + 10);
        } else if (hex <= 'F' && hex >= 'A') {
            return (byte)(hex - 65 + 10);
        } else {
            return hex <= '9' && hex >= '0' ? (byte)(hex - 48) : 0;
        }
    }
}
