package com.spd.base.utils;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.spd.base.view.SignalView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Datautils {


    public static int btyeTo16Int(byte x) {
        return ((x) >> 4) * 10 + ((x) & 0x0f);
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

    /**
     * @param str16
     * @return
     * @Date:2014-3-18
     * @Author:lulei
     * @Description: 将16进制的字符串转化为long值
     */
    public static long parseString16ToLong(String str16) {
        if (str16 == null) {
            throw new NumberFormatException("null");
        }
        //先转化为小写
        str16 = str16.toLowerCase();
        //如果字符串以0x开头，去掉0x
        str16 = str16.startsWith("0x") ? str16.substring(2) : str16;
        if (str16.length() > 16) {
            throw new NumberFormatException("For input string '" + str16 + "' is to long");
        }
        return parseMd5L16ToLong(str16);

    }

    /**
     * @param md5L16
     * @return
     * @Date:2014-3-18
     * @Author:lulei
     * @Description: 将16位的md5转化为long值
     */
    public static long parseMd5L16ToLong(String md5L16) {
        if (md5L16 == null) {
            throw new NumberFormatException("null");
        }
        md5L16 = md5L16.toLowerCase();
        byte[] bA = md5L16.getBytes();
        long re = 0L;
        for (int i = 0; i < bA.length; i++) {
            //加下一位的字符时，先将前面字符计算的结果左移4位
            re <<= 4;
            //0-9数组
            byte b = (byte) (bA[i] - 48);
            //A-F字母
            if (b > 9) {
                b = (byte) (b - 39);
            }
            //非16进制的字符
            if (b > 15 || b < 0) {
                throw new NumberFormatException("For input string '" + md5L16);
            }
            re += b;
        }
        return re;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
     * 0xD9}
     *
     * @param temp String
     * @return byte[]
     */
    public static byte[] HexString2Bytes(String temp) {
//		src.replace("\\s","");
        String src = temp.replace(" ", "");
        System.out.println(" src= " + src);
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }


    /**
     * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        try {
            byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}))
                    .byteValue();
            _b0 = (byte) (_b0 << 4);
            byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}))
                    .byteValue();
            byte ret = (byte) (_b0 ^ _b1);
            return ret;
        } catch (Exception e) {
            // TODO: handle exception
            return 0;
        }

    }

    public static byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }

    public static char byteToChar(byte[] b) {
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }

    /**
     * hexString-byte[] "130632199104213021"->{0x13,0x06....,0x21}
     *
     * @param hex
     * @return byte[]
     */
    public static byte[] hexStringToByteArray(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }


    /**
     * byte[]->String {0x23,0x32,0x12}-->"233212" 比如从卡里解析出身份证
     *
     * @param src
     * @return String
     */
    public static String byteArrayToString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * byte[]->String {0x23,0x32,0x12}-->"23 32 12" 查看log
     *
     * @param res
     * @param len
     * @return String
     */
    public static String byteArrayToStringLog(byte[] res, int len) {
        String result = "";
        for (int i = 0; i < len; i++) {
            result += String.format("%02x ", res[i]);
        }
        return result;
    }

    /**
     * byte[]->ascii String {0x71,0x72,0x73,0x41,0x42}->"qrsAB"
     *
     * @param cmds
     * @return String
     */
    public static String byteArrayToAscii(byte[] cmds) {
        int tRecvCount = cmds.length;
        StringBuffer tStringBuf = new StringBuffer();
        String nRcvString;
        char[] tChars = new char[tRecvCount];
        for (int i = 0; i < tRecvCount; i++) {
            tChars[i] = (char) cmds[i];
        }
        tStringBuf.append(tChars);
        nRcvString = tStringBuf.toString(); // nRcvString从tBytes转成了String类型的"123"
        return nRcvString;
    }


    /**
     * byte[]转int
     *
     * @param bytes
     * @return int
     */
    public static int byteArrayToInt(byte[] bytes, boolean high_) {
        int value = 0;
        // 由高位到低位   低位在前
        if (high_) {
            for (int i = 0; i < bytes.length; i++) {
                int shift = (bytes.length - 1 - i) * 8;
                value += (bytes[i] & 0x000000FF) << shift;// 低位在前
            }
        } else {
            for (int i = bytes.length - 1; i >= 0; i--) {
                int shift = i * 8;
                value += (bytes[i] & 0x000000FF) << shift;// 高位在前
            }
        }
        return value;
    }

    /**
     * byte[]转int
     *
     * @param bytes
     * @return int
     */
    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        //   低位在前
        for (int i = 0; i < bytes.length; i++) {
            int shift = (bytes.length - 1 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;// 往高位游
        }
        return value;
    }

    public static void main(String[] args) {
    }

    /**
     * 截取数组
     *
     * @param bytes  被截取数组
     * @param start  被截取数组开始截取位置
     * @param length 新数组的长度
     * @return 新数组
     */
    public static byte[] cutBytes(byte[] bytes, int start, int length) {
        byte[] res = new byte[length];
        System.arraycopy(bytes, start, res, 0, length);
        return res;
    }

    /**
     * @param format example:yyyyMMddHHmmss
     * @return String
     */

    public static String getCurrentTime(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date());
    }

    /**
     * 获取当前时间 yyyyMMddHHmmss
     *
     * @return String
     */

    public static String getDefautCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date());
    }

    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }

    public static String getData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        return dateFormat.format(new Date());
    }

    /**
     * 获取系统时间
     *
     * @return
     */
    public static byte[] getDateTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = dateFormat.format(now);
        // 赋值当前日期和时间
        byte[] nowTimes = Datautils.HexString2Bytes(currentTime);
        return nowTimes;
    }

    //必须把我们要的值弄到最低位去，有人说不移位这样做也可以， result[0] = (byte)(i  & 0xFF000000);
    //，这样虽然把第一个字节取出来了，但是若直接转换为byte类型，会超出byte的界限，出现error。
    // 再提下数//之间转换的原则（不管两种类型的字节大小是否一样，原则是不改变值，内存内容可能会变，
    // 比如int转为//float肯定会变）所以此时的int转为byte会越界，只有int的前三个字节都为0的时候转byte才不会越界。
    // 虽//然 result[0] = (byte)(i  & 0xFF000000); 这样不行，
    // 但是我们可以这样 result[0] = (byte)((i  & //0xFF000000) >>24);

    public static byte[] intToByteArray1(int i) {
        byte[] result = new byte[4];
//        result[3] = (byte) ((i >> 24) & 0xFF);
//        result[2] = (byte) ((i >> 16) & 0xFF);
//        result[1] = (byte) ((i >> 8) & 0xFF);
//        result[0] = (byte) (i & 0xFF);

        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }


    //apdu指令运行
    //例如输入0x00,0x84,0x00,0x00,0x08
    //输出完整byte[]指令
    public static byte[] execCmd(byte[] bysshortCmd) {
        String cmd = Datautils.byteArrayToString(bysshortCmd);
        byte[] bysRealCmd = Datautils.packageCommand(cmd);
        return bysRealCmd;
    }

    //String型apdu指令如0084000008转成完整指令

    /**
     * 针对psam3310 的方法  指令打包
     *
     * @param strCmd02 apdu指令
     * @return 3310格式的指令
     */
    public static byte[] packageCommand(String strCmd02) {
        //长度 changdu
        int changdu01 = strCmd02.length() / 2 + 3;//长度
        String zhentou = "02";//帧头
        String zhenwei = "03";//帧尾
        int iStrLen;
        String mingling = "3226";//命令
        String feijie = "ff";//非接卡FF
        String changdu02 = Integer.toHexString(changdu01);//长度转16进制数
        int changdu03 = changdu02.length();//判断长度
        if (changdu03 == 2) {//长度为2
            changdu02 = "00" + changdu02;//0088
        } else if (changdu03 == 1) {//长度为一
            changdu02 = "000" + changdu02;//0008
        }
        //主要内容,不含帧头帧尾效验位
        String neirong = changdu02 + mingling + feijie + strCmd02;
        String neirong1 = mingling + feijie + strCmd02;
        strCmd02 = neirong1;//新的内容不带3不带命令长度用来算效验
        iStrLen = strCmd02.length();//新的长度,无头无尾无效验
        //异或求效验
        int sub = 0;
        int a;
        for (int x2 = 0; x2 < iStrLen; x2 += 2) {
            a = Integer.parseInt(strCmd02.substring(x2, x2 + 2), 16);
            sub = a ^ sub;
        }
        String xiaoyan = Integer.toHexString(sub);
        neirong = neirong + xiaoyan;
        String new1 = "";//带3
        for (int x1 = 0; x1 < neirong.length(); x1++) {
            new1 = new1 + "3" + neirong.substring(x1, x1 + 1);
        }
        strCmd02 = zhentou + new1 + zhenwei;

        iStrLen = strCmd02.length();
        int i = 0;
        int j = 0;
        byte[] bysRealCmd = new byte[iStrLen / 2];
        for (i = 0; i < iStrLen; i += 2) {
            bysRealCmd[j] = ascToHex(strCmd02.charAt(i));
            bysRealCmd[j] <<= 4;
            bysRealCmd[j] += ascToHex(strCmd02.charAt(i + 1));
            j++;
        }
        return bysRealCmd;
    }


    //打包指令
    public static byte ascToHex(char _ch) {
        byte byHex = 0;

        switch (_ch) {
            case '0':
                byHex = 0x00;
                break;
            case '1':
                byHex = 0x01;
                break;
            case '2':
                byHex = 0x02;
                break;
            case '3':
                byHex = 0x03;
                break;
            case '4':
                byHex = 0x04;
                break;
            case '5':
                byHex = 0x05;
                break;
            case '6':
                byHex = 0x06;
                break;
            case '7':
                byHex = 0x07;
                break;
            case '8':
                byHex = 0x08;
                break;
            case '9':
                byHex = 0x09;
                break;
            case 'a':
            case 'A':
                byHex = 0x0A;
                break;
            case 'b':
            case 'B':
                byHex = 0x0B;
                break;
            case 'c':
            case 'C':
                byHex = 0x0C;
                break;
            case 'd':
            case 'D':
                byHex = 0x0D;
                break;
            case 'e':
            case 'E':
                byHex = 0x0E;
                break;
            case 'f':
            case 'F':
                byHex = 0x0F;
                break;
            default:
                byHex = 0;
        }

        return byHex;
    }


    /**
     * 将一个4byte的数组转换成32位的int
     *
     * @param buf bytes buffer
     * @param pos byte[]中开始转换的位置
     * @return convert result
     */
    protected long unsigned4BytesToInt(byte[] buf, int pos) {
        int firstByte = 0;
        int secondByte = 0;
        int thirdByte = 0;
        int fourthByte = 0;
        int index = pos;
        firstByte = (0x000000FF & ((int) buf[index]));
        secondByte = (0x000000FF & ((int) buf[index + 1]));
        thirdByte = (0x000000FF & ((int) buf[index + 2]));
        fourthByte = (0x000000FF & ((int) buf[index + 3]));
        index = index + 4;
        return ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
    }


    /**
     * 将一个字符串命令反序排列
     * <p>
     * 如：a8d1c8df4edf64---->64df4edfc8d1a8
     *
     * @param ss
     * @return 反序的字符串
     */
    private String reverseOrder(String ss) {
        String sum = "";
        for (int i = 0; i < ss.length(); i = i + 2) {
            String s = ss.substring(ss.length() - (2 + i), ss.length() - i);
            sum = sum + s;
        }
        return sum;
    }


    /**
     * 将byte值转化为int
     * btoi(buf[0]) == 0x68
     *
     * @param a
     * @return
     */
    private static int btoi(byte a) {
        return (a < 0 ? a + 256 : a);
    }

    //判断扫描的内容是否是UTF8的中文内容
    public static boolean isUTF8(byte[] sx) {
        //Log.d(TAG, "begian to set codeset");
        boolean IsUtf8 = false;
        for (int i = 0; i < sx.length; ) {
            if (sx[i] < 0) {
                if ((sx[i] >>> 5) == 0x7FFFFFE) {
                    if (((i + 1) < sx.length) && ((sx[i + 1] >>> 6) == 0x3FFFFFE)) {
                        i = i + 2;
                        IsUtf8 = true;
                    } else {
                        if (IsUtf8) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else if ((sx[i] >>> 4) == 0xFFFFFFE) {
                    if (((i + 2) < sx.length) && ((sx[i + 1] >>> 6) == 0x3FFFFFE) && ((sx[i + 2] >>> 6) == 0x3FFFFFE)) {
                        i = i + 3;
                        IsUtf8 = true;
                    } else {
                        if (IsUtf8) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else {
                    if (IsUtf8) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                i++;
            }
        }
        return true;
    }


    /**
     * 得到当前的手机蜂窝网络信号强度
     * 获取LTE网络和3G/2G网络的信号强度的方式有一点不同，
     * LTE网络强度是通过解析字符串获取的，
     * 3G/2G网络信号强度是通过API接口函数完成的。
     * asu 与 dbm 之间的换算关系是 dbm=-113 + 2*asu
     */
    public static void getCurrentNetDBM(Context context, final SignalView mSignalView) {
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener mylistener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                String signalInfo = signalStrength.toString();
                String[] params = signalInfo.split(" ");

                if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
                    //4G网络 最佳范围   >-90dBm 越大越好
                    int Itedbm = Integer.parseInt(params[9]);
//                    mTv.setText("信号：：：" + Itedbm);
                    int sss = signalStrength.getCdmaDbm();
                    int sss1 = signalStrength.getCdmaEcio();
                    int sss2 = signalStrength.getEvdoDbm();
                    int sss5 = signalStrength.getEvdoEcio();
                    int sss7 = signalStrength.getEvdoSnr();
                    int sss8 = signalStrength.getGsmBitErrorRate();
                    int sss9 = signalStrength.getGsmBitErrorRate();

                    Log.i("tws", "onSignalStrengthsChanged: " + sss);
                    // 设置信号强度
                    if (Itedbm > -100 && Itedbm < 0) {
                        mSignalView.setSignalValue(5);
                    } else if (Itedbm < -100 && Itedbm > -110) {
                        mSignalView.setSignalValue(4);
                    } else if (Itedbm < -110 && Itedbm > -115) {
                        mSignalView.setSignalValue(3);
                    } else if (Itedbm < -115) {
                        mSignalView.setSignalValue(2);
                    } else {
                        mSignalView.setSignalValue(1);
                    }
//                    mSignalView.setSignalValue(0);
                    // 设置信号类型
//                    mSignalView.setSignalTypeText("×");
                    mSignalView.setSignalTypeText("4G");

                } else if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA ||
                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPA ||
                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSUPA ||
                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS) {
                    //3G网络最佳范围  >-90dBm  越大越好  ps:中国移动3G获取不到  返回的无效dbm值是正数（85dbm）
                    //在这个范围的已经确定是3G，但不同运营商的3G有不同的获取方法，故在此需做判断 判断运营商与网络类型的工具类在最下方
//                    String yys = IntenetUtil.getYYS(getApplication());//获取当前运营商
//                    if (yys == "中国移动") {
//                        setDBM(0 + "");//中国移动3G不可获取，故在此返回0
//                    } else if (yys == "中国联通") {
//                        int cdmaDbm = signalStrength.getCdmaDbm();
//                        setDBM(cdmaDbm + "");
//                    } else if (yys == "中国电信") {
//                        int evdoDbm = signalStrength.getEvdoDbm();
//                        setDBM(evdoDbm + "");
//                    }
                    // 设置信号强度
                    mSignalView.setSignalValue(3);
                    // 设置信号类型
                    mSignalView.setSignalTypeText("3G");
                } else {
                    //2G网络最佳范围>-90dBm 越大越好
                    int asu = signalStrength.getGsmSignalStrength();
                    int dbm = -113 + 2 * asu;
//                    setDBM(dbm + "");
                    // 设置信号强度
                    mSignalView.setSignalValue(0);
                    // 设置信号类型
//                    mSignalView.setSignalTypeText("2G");
                    mSignalView.setSignalTypeText("×");
                }

            }
        };
        //开始监听
        tm.listen(mylistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    public static byte[] double2Bytes(double d) {
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return byteRet;
    }

    public static double bytes2Double(byte[] arr) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }
}

