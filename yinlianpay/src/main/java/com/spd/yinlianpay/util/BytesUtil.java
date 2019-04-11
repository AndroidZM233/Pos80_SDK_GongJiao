//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.spd.yinlianpay.util;

import java.util.ArrayList;
import java.util.List;

public class BytesUtil {
    public BytesUtil() {
    }

    public static byte[] subByte(byte[] srcBytes, int offset, int len) {
        if(srcBytes == null) {
            return null;
        } else if(len <= srcBytes.length && offset + len <= srcBytes.length && offset < srcBytes.length) {
            byte[] bytes;
            if(len == -1) {
                bytes = new byte[srcBytes.length - offset];
                System.arraycopy(srcBytes, offset, bytes, 0, srcBytes.length - offset);
            } else {
                bytes = new byte[len];
                System.arraycopy(srcBytes, offset, bytes, 0, len);
            }

            return bytes;
        } else {
            return null;
        }
    }

    public static byte[] mergeBytes(byte[] bytesA, byte[] bytesB) {
        if(bytesA != null && bytesA.length != 0) {
            if(bytesB != null && bytesB.length != 0) {
                byte[] bytes = new byte[bytesA.length + bytesB.length];
                System.arraycopy(bytesA, 0, bytes, 0, bytesA.length);
                System.arraycopy(bytesB, 0, bytes, bytesA.length, bytesB.length);
                return bytes;
            } else {
                return bytesA;
            }
        } else {
            return bytesB;
        }
    }

    public static byte[] merge(byte[]... data) {
        if(data == null) {
            return null;
        } else {
            byte[] bytes = null;

            for(int i = 0; i < data.length; ++i) {
                bytes = mergeBytes(bytes, data[i]);
            }

            return bytes;
        }
    }

    public static int bytecmp(byte[] hex1, byte[] hex2, int len) {
        for(int i = 0; i < len; ++i) {
            if(hex1[i] != hex2[i]) {
                return 1;
            }
        }

        return 0;
    }

    public static byte[] hexString2ByteArray(String hexStr) {
        if(hexStr == null) {
            return null;
        } else if(hexStr.length() % 2 != 0) {
            return null;
        } else {
            byte[] data = new byte[hexStr.length() / 2];

            for(int i = 0; i < hexStr.length() / 2; ++i) {
                char hc = hexStr.charAt(2 * i);
                char lc = hexStr.charAt(2 * i + 1);
                byte hb = hexChar2Byte(hc);
                byte lb = hexChar2Byte(lc);
                if(hb < 0 || lb < 0) {
                    return null;
                }

                int n = hb << 4;
                data[i] = (byte)(n + lb);
            }

            return data;
        }
    }

    public static byte hexChar2Byte(char c) {
        return c >= 48 && c <= 57?(byte)(c - 48):(c >= 97 && c <= 102?(byte)(c - 97 + 10):(c >= 65 && c <= 70?(byte)(c - 65 + 10):-1));
    }

    public static String byteArray2HexString(byte[] arr) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < arr.length; ++i) {
            sb.append(String.format("%02x", new Object[]{Byte.valueOf(arr[i])}).toUpperCase());
        }

        return sb.toString();
    }

    public static String byteArray2HexString(List<Byte> arrList) {
        byte[] arr = new byte[arrList.size()];

        for(int i = 0; i < arrList.size(); ++i) {
            arr[i] = ((Byte)arrList.get(i)).byteValue();
        }

        return byteArray2HexString(arr);
    }

    public static String byteArray2HexStringWithSpace(byte[] arr) {
        StringBuilder sbd = new StringBuilder();
        byte[] var5 = arr;
        int var4 = arr.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            byte b = var5[var3];
            String tmp = Integer.toHexString(255 & b);
            if(tmp.length() < 2) {
                tmp = "0" + tmp;
            }

            sbd.append(tmp);
            sbd.append(" ");
        }

        return sbd.toString();
    }

    public static String getBCDString(byte[] data, int start, int end) {
        byte[] t = new byte[end - start + 1];
        System.arraycopy(data, start, t, 0, t.length);
        return byteArray2HexString(t);
    }

    public static String getHexString(byte[] data, int start, int end) {
        byte[] t = new byte[end - start + 1];
        System.arraycopy(data, start, t, 0, t.length);
        return byteArray2HexStringWithSpace(t);
    }

    public static byte[] toByteArray(int source, int len) {
        byte[] bLocalArr = new byte[len];

        for(int i = 0; i < 4 && i < len; ++i) {
            bLocalArr[len - 1 - i] = (byte)(source >> 8 * i & 255);
        }

        return bLocalArr;
    }

    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;

        for(int i = 0; i < (asc_len + 1) / 2; ++i) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte)((j >= asc_len?0:asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }

        return bcd;
    }

    private static byte asc_to_bcd(byte asc) {
        byte bcd;
        if(asc >= 48 && asc <= 57) {
            bcd = (byte)(asc - 48);
        } else if(asc >= 65 && asc <= 70) {
            bcd = (byte)(asc - 65 + 10);
        } else if(asc >= 97 && asc <= 102) {
            bcd = (byte)(asc - 97 + 10);
        } else {
            bcd = (byte)(asc - 48);
        }

        return bcd;
    }

    public static boolean[] getBooleanArray(byte b) {
        boolean[] array = new boolean[8];

        for(int i = 7; i >= 0; --i) {
            array[i] = (b & 1) == 1;
            b = (byte)(b >> 1);
        }

        return array;
    }

    public static int booleanArray2i(boolean[] b) {
        int res = 0;
        int len = b.length;

        for(int i = 0; i < len; ++i) {
            if(b[i]) {
                res += (int) Math.pow(2.0D, (double)(len - i - 1));
            }
        }

        return res;
    }

    public static int byte2Int(byte[] b) {
        int temp = 0;

        for(int i = 0; i < b.length; ++i) {
            temp += (b[i] & 255) << 8 * (b.length - i - 1);
        }

        return temp;
    }

    public static int byte2Int(byte[] b, int bytesNum) {
        int intValue = 0;

        for(int i = 0; i < b.length; ++i) {
            intValue += (b[i] & 255) << 8 * (bytesNum - 1 - i);
        }

        return intValue;
    }

    public static byte[] int2Bytes(int length, int bytesNum) {
        if(bytesNum > 4) {
            bytesNum = 4;
        } else if(bytesNum <= 0) {
            bytesNum = 1;
        }

        return bytesNum == 4?new byte[]{(byte)(length >> 24 & 255), (byte)(length >> 16 & 255), (byte)(length >> 8 & 255), (byte)(length & 255)}:(bytesNum == 3?new byte[]{(byte)(length >> 16 & 255), (byte)(length >> 8 & 255), (byte)(length & 255)}:(bytesNum == 2?new byte[]{(byte)(length >> 8 & 255), (byte)(length & 255)}:new byte[]{(byte)(length & 255)}));
    }

    public static String asc2Bcd(String str) {
        byte[] bcd = ASCII_To_BCD(str.getBytes(), str.length());
        return bcd2Str(bcd);
    }

    private static String bcd2Str(byte[] bytes) {
        char[] temp = new char[bytes.length * 2];

        for(int i = 0; i < bytes.length; ++i) {
            char val = (char)((bytes[i] & 240) >> 4 & 15);
            temp[i * 2] = (char)(val > 9?val + 65 - 10:val + 48);
            val = (char)(bytes[i] & 15);
            temp[i * 2 + 1] = (char)(val > 9?val + 65 - 10:val + 48);
        }

        return new String(temp);
    }

    public static byte[] hex2byte(String str) {
        int len = str.length();
        String stmp = null;
        byte[] bt = new byte[len / 2];

        for(int n = 0; n < len / 2; ++n) {
            stmp = str.substring(n * 2, n * 2 + 2);
            bt[n] = (byte) Integer.parseInt(stmp, 16);
        }

        return bt;
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";

        for(int n = 0; n < b.length; ++n) {
            stmp = Integer.toHexString(b[n] & 255);
            if(stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }

            if(n < b.length - 1) {
                hs = hs;
            }
        }

        return hs.toUpperCase();
    }

    public static List<Byte> hexString2ByteList(String dataStr) {
        byte[] dataArray = hexString2ByteArray(dataStr);
        ArrayList result = new ArrayList();

        for(int i = 0; i < dataArray.length; ++i) {
            result.add(Byte.valueOf(dataArray[i]));
        }

        return result;
    }

    public static boolean isBitSet(byte val, int bitPos) {
        if(bitPos >= 1 && bitPos <= 8) {
            return (val >> bitPos - 1 & 1) == 1;
        } else {
            throw new IllegalArgumentException("parameter \'bitPos\' must be between 1 and 8. bitPos=" + bitPos);
        }
    }

    public static byte[] makeDistanceBytes(int distance) {
        boolean fiveKm = false;
        boolean fourKm = false;
        boolean threeKm = false;
        boolean twoKm = false;
        boolean oneKm = false;
        byte halfOfOneKm = 0;
        int var10 = distance / 5000;
        distance -= 5000 * var10;
        int var11 = distance / 4000;
        distance -= 4000 * var11;
        int var12 = distance / 3000;
        distance -= 3000 * var12;
        int var13 = distance / 2000;
        distance -= 2000 * var13;
        int var14 = distance / 1000;
        distance -= 1000 * var14;
        if(distance != 0) {
            halfOfOneKm = 1;
        }

        byte[] distanceBytes = new byte[var10 + var11 + var12 + var13 + var14 + halfOfOneKm];
        int k = 0;
        int i;
        if(var10 > 0) {
            for(i = 0; i < var10; ++i) {
                distanceBytes[k++] = 5;
            }
        }

        if(var11 > 0) {
            for(i = 0; i < var11; ++i) {
                distanceBytes[k++] = 4;
            }
        }

        if(var12 > 0) {
            for(i = 0; i < var12; ++i) {
                distanceBytes[k++] = 3;
            }
        }

        if(var13 > 0) {
            for(i = 0; i < var13; ++i) {
                distanceBytes[k++] = 2;
            }
        }

        if(var14 > 0) {
            for(i = 0; i < var14; ++i) {
                distanceBytes[k++] = 1;
            }
        }

        if(halfOfOneKm > 0) {
            for(i = 0; i < halfOfOneKm; ++i) {
                distanceBytes[k++] = 0;
            }
        }

        return distanceBytes;
    }

    public static int getDistanceFromBytes(byte[] addBytes) {
        if(addBytes != null && addBytes.length != 0) {
            int km = 0;

            for(int i = 0; i < addBytes.length; ++i) {
                if((addBytes[i] & 255) == 0) {
                    km += 500;
                } else if((addBytes[i] & 255) == 1) {
                    km += 1000;
                } else if((addBytes[i] & 255) == 2) {
                    km += 2000;
                } else if((addBytes[i] & 255) == 3) {
                    km += 3000;
                } else if((addBytes[i] & 255) == 4) {
                    km += 4000;
                } else if((addBytes[i] & 255) == 5) {
                    km += 5000;
                }
            }

            return km;
        } else {
            return 0;
        }
    }
}
