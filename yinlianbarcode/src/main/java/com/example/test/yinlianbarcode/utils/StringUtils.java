/*
java基础数据类型转换：
简单数据类型之间的转换又分为自动类型转换（低级到高级）、强制类型转换（高级到低级以及同级之间）、包装类过度类型转换；
注意事项：
 1、boolean类型不能转换成其它数据类型
 2、低级别自动转换为高级别 ：byte,short,char->int->long->float->double
 3、同级之间不会自动转换，可以强制类型转换
 4、强制类型转换是由高级别像低级别之间转化，会降低精度准确性
 5、包装类：Boolean、Character、Byte、Short、Integer、Long、Float、Double
 */

package com.example.test.yinlianbarcode.utils;


import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String数据判断、转换
 * Created by 张明_ on 2017/8/18.
 * Email 741183142@qq.com
 */

public class StringUtils {


    /**
     * 判断是否是中文名字
     *
     * @param str String
     * @return 验证通过返回true
     */
    public static boolean isChineseName(String str) {
        String regEx = "^[\\u4e00-\\u9fa5]{2,4}+$";
        return Pattern.matches(regEx, str);
    }

    /**
     * 通过判断是否是http开始,来判断是否是 url
     *
     * @param string String
     * @return 验证通过返回true
     */
    public static boolean isUrlString(String string) {
        return !TextUtils.isEmpty(string) && string.startsWith("http");
    }

    /**
     * 字符串都是数字吗
     *
     * @param str String
     * @return 验证通过返回true
     */
    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    /**
     * 判断字符串是否是邮箱
     *
     * @param email email
     * @return 字符串是否是邮箱
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(" +
                "([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 判断手机号字符串是否合法
     *
     * @param phoneNumber 手机号字符串
     * @return 手机号字符串是否合法
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "^1[3|4|5|7|8]\\d{9}$";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * 判断手机号字符串是否合法
     *
     * @param areaCode    区号
     * @param phoneNumber 手机号字符串
     * @return 手机号字符串是否合法
     */
    public static boolean isPhoneNumberValid(String areaCode, String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return false;
        }

        if (phoneNumber.length() < 5) {
            return false;
        }

        if (TextUtils.equals(areaCode, "+86") || TextUtils.equals(areaCode, "86")) {
            return isPhoneNumberValid(phoneNumber);
        }

        boolean isValid = false;
        String expression = "^[0-9]*$";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * 16进制String  --> byte[]
     *
     * @param hexString String 十六进制
     * @return byte[]
     */
    public static byte[] hexStringToByteArray(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * String转char数组
     *
     * @param value String
     * @return char[]
     */
    public static char[] toCharArray(String value) {
        char[] WriteText = new char[value.length() / 4];
        byte[] b_temp = new byte[value.length()];
        int i;
        for (i = 0; i < b_temp.length; ++i) {
            b_temp[i] = Byte.parseByte(value.substring(i, i + 1), 16);
        }
        for (i = 0; i < WriteText.length; ++i) {
            WriteText[i] = (char) ((b_temp[i * 4] & 15) << 12 | (b_temp[i * 4 + 1] & 15) << 8 | (b_temp[i * 4 + 2] & 15) << 4 | b_temp[i * 4 + 3] & 15);
        }
        return WriteText;
    }

    /**
     * String转16进制String
     *
     * @param value String
     * @return 16进制String
     */
    public static String toHexString(String value) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < value.length(); ++i) {
            char ch = value.charAt(i);
            String s4 = Integer.toHexString(ch);
            str.append(s4);
        }
        return str.toString();
    }


    /**
     * utf-8 转换成 unicode
     *
     * @param inStr
     * @return
     */
    public static String utf8ToUnicode(String inStr) {
        char[] myBuffer = inStr.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(myBuffer[i]);
            if (ub == Character.UnicodeBlock.BASIC_LATIN) {
                //英文及数字等
                sb.append(myBuffer[i]);
            } else if (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                //全角半角字符
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                //汉字
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u" + hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * unicode 转换成 utf-8
     *
     * @param theString
     * @return
     */
    public static String unicodeToUtf8(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }
}
