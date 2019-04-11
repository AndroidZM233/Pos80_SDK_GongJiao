package com.spd.yinlianpay.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.util.Arrays;

public class PrefUtil {
    private static boolean isSocket = true;
    private static String KEY_SCRIPT = "script";
    private static String KEY_SERIAL_NO = "serialNo";
    private static String KEY_BATCH_NO = "batchNo";
    private static String KEY_MERCHANT_NO = "merchantNo";
    private static String KEY_TERMINAL_NO = "terminalNo";
    private static String KEY_MERCHANT_NAME = "merchantName";
    private static String KEY_IP = "ip";
    private static String KEY_PORT = "port";
    private static String KEY_TPDU = "tpdu";
    private static String KEY_KEY_INDEX = "keyIndex";
    private static String KEY_SIGNED = "signed";
    private static String KEY_OPERATOR_NO = "operatorNo";
    private static String KEY_REVERSAL = "reversal";
    public static String KEY_REVERSAL_NUM = "reversalNum";
    private static String KEY_COMMUN_WAY = "communWay";
    private static String ISSUPPORTSM = "isSupportSM";
    private static String ISFIRSTRUN = "isFirstRun";
    //ic公钥

    private static String ICPASSWORD = "icPassword";
    //ic参数

    private static String ICPARAMETER = "icParameter";
    //国密ic参数

    private static String ICSMD = "icSMD";


    private static String KEY_OVER_TIME = "overtime";
    private static String KEY_DO_SIGN_REPEAT_SEND = "doSignRepeatSend";
    private static String KEY_CHONG_ZHENG_REPEAT_SEND = "chongZhengRepeatSend";
    private static String KEY_MAX_TRADE_NUM = "maxTradeNum";
    private static String GPSFLG = "gps";

    //操作员ID
    private static String KEY_OPERATION = "Operation";
    private static String dekFlag = "DEKFLAG";

    public static void setDekFlag(boolean key1) {
        sharedPreferences.edit().putBoolean(dekFlag, key1).commit();
    }

    public static boolean getDekFlag() {
        return sharedPreferences.getBoolean(dekFlag, false);
    }

    //传统类交易
    public static boolean Consume_Control = true;
    public static boolean ConsumeCancel_Control = true;
    public static boolean Retfund_Control = true;
    public static boolean BalanceInquiry_Control = true;
    public static boolean Pre_Control = true;
    public static boolean PreCancel_Control = true;
    public static boolean PreComplete_Control = true;
    public static boolean PreCompleteCancel_Control = true;
    public static boolean UnionPayQRCode_Control = true;
    public static boolean FullMess_isencry = true;
    //交易输密控制
    public static boolean ConsumeCancel_IsPwd = true;
    public static boolean PreCancel_IsPwd = true;
    public static boolean PreCompleteCancel_IsPwd = true;
    public static boolean ConsumeComleteRequest_Ispwd = true;
    //交易刷卡控制
    public static boolean ConsumeCancel_IsSwipCard = true;
    public static boolean PreCompleteCancel_IsSwipCard = true;
    //结算交易控制
    public static boolean Settlement_IsAutoSignout = true;
    //其他交易控制
    public static boolean Director_IsPwd = true;
    public static boolean HandInput_IsPwd = true;
    public static boolean Tips_IsSupport = true;
    public static boolean Title_IsLogo = true;
    public static boolean Print_Control = true;
    //是否电子签名
    public static boolean Electric_Signatur = true;
    //ip1005 key1密文
    public static String key1 = "";
    //ip1005 随机数明文
    public static String Random = "";
    //激活状态标志位
    public static boolean activation = true;
    //签到重发次数
    public static int relogin = 3;
    //冲正重发次数
    public static int rechongzheng = 3;
    public static int tradetimeout = 60;
    /*
     * 打印联数
     * 1.商户存根
     * 2.持卡人存根
     * 3.银行存根
     * */
    public static int Print_Pieces = 1;
    public static boolean Print_Repeat = false;
    //签名数据字符串格式
    public static String SignDataStr = "";
    //锁定终端false/解锁状态true
    public static boolean IsLock = false;
    private static String TPDU = "tpdu";

    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            //Context.MODE_PRIVATE: 指定该SharedPreferences数据只能被本应用程序读、写。
            // Context.MODE_WORLD_READABLE:  指定该SharedPreferences数据能被其他应用程序读，但不能写。
            // Context.MODE_WORLD_WRITEABLE:  指定该SharedPreferences数据能被其他应用程序读，写
            sharedPreferences = context.getSharedPreferences("ui.wangpos.com.ccbbank_preferences", Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
//            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        }
        return sharedPreferences;
    }

    public static void setIsSocket(boolean isSocket) {
        sharedPreferences.edit().putBoolean("isSocket", isSocket).commit();
    }

    //设置主密钥
    public static void setMasterkey(String masterkey) {
        sharedPreferences.edit().putString("masterkey", masterkey).commit();
    }

    //获取主密钥
    public static String getMasterKey() {
        return sharedPreferences.getString("masterkey", "");
    }

    //设置工作秘钥  签到处设置
    public static void setMackey(String mackey) {
        sharedPreferences.edit().putString("mackey", mackey).commit();
    }

    //获取工作秘钥
    public static String getMacKey() {
        return sharedPreferences.getString("mackey", "");
    }

    public static boolean getIsSocket() {
        return sharedPreferences.getBoolean("isSocket", true);
    }

    public static boolean getICPARAMETER() {
        return getBoolean(ICPARAMETER, false);
    }

    public static void setICPARAMETER(boolean ipvalue) {
        sharedPreferences.edit().putBoolean(ICPARAMETER, ipvalue).commit();
    }

    public static boolean getGPSFLG() {
        return getBoolean(GPSFLG, false);
    }

    public static void setGPS(boolean ipvalue) {
        sharedPreferences.edit().putBoolean(GPSFLG, ipvalue).commit();
    }

    public static boolean getICPASSWORD() {
        return getBoolean(ICPASSWORD, false);
    }

    public static void setICPASSWORD(boolean ipvalue) {
        sharedPreferences.edit().putBoolean(ICPASSWORD, ipvalue).commit();
    }

    public static boolean getICSMD() {
        return getBoolean(ICSMD, false);
    }

    public static void setICSMD(boolean ipvalue) {
        sharedPreferences.edit().putBoolean(ISFIRSTRUN, ipvalue).commit();
    }

    //第一次运行
    public static boolean getISFIRSTRUN() {
        return getBoolean(ISFIRSTRUN, false);
    }

    public static void setISFIRSTRUN(boolean ipvalue) {
        sharedPreferences.edit().putBoolean(ISFIRSTRUN, ipvalue).commit();
    }

    public static void setISSUPPORTSM(boolean ipvalue) {
        sharedPreferences.edit().putBoolean(ISSUPPORTSM, ipvalue).commit();
    }

    public static boolean getISSUPPORTSM() {
        return getBoolean(ISSUPPORTSM, false);
    }

    public static void setTPDU(String ipvalue) {
        sharedPreferences.edit().putString(TPDU, ipvalue).commit();
    }

    public static String getTPDU() {
        return getString(TPDU, "");
    }

    public static String Head = "Head";

    public static void setHead(String ipvalue) {
        sharedPreferences.edit().putString(Head, ipvalue).commit();
    }

    public static String getHead() {
        String head = getString(Head, "");
        if (head == null || head.length() <= 0) {
            return getTPDU();
        } else {
            return head;
        }
    }

    //转账汇款临时使用
    public static String TransterCardNo = "TransterCardNo";

    public static String getKeyCommunWay() {
        return getString(KEY_COMMUN_WAY, "SOCKET");
    }

    public static void setKeyCommunWay(String value) {
        sharedPreferences.edit().putString(KEY_COMMUN_WAY, value).commit();
    }

    public static void setOperatID(String ipvalue) {
        sharedPreferences.edit().putString(KEY_OPERATION, ipvalue).commit();
    }

    public static String getOperatID() {
        return getString(KEY_OPERATION, "");
    }

    //传输密钥
    public static String TmpTLK = "TmpTLK";

    public static void setTLK(String ipvalue) {
        sharedPreferences.edit().putString(TmpTLK, ipvalue).commit();
    }

    public static String getTLK() {
        return getString(TmpTLK, "");
    }

    //公钥信息
    public static String Key_PublicKeyInfo = "PublicKeyInfo";

    public static void setPublicKeyInfo(String ipvalue) {
        sharedPreferences.edit().putString(Key_PublicKeyInfo, ipvalue).commit();
    }

    public static String getPublicKeyInfo() {
        return getString(Key_PublicKeyInfo, "");
    }

    public static void setIP(String ipvalue) {
        sharedPreferences.edit().putString(KEY_IP, ipvalue).commit();
    }

    public static String getIP() {
        return getString(KEY_IP, "");
    }

    public static void setPort(int portvalue) {
        sharedPreferences.edit().putInt(KEY_PORT, portvalue).commit();
    }

    public static int getPort() {
        return getInt(KEY_PORT, 0);
    }

    public static void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }


    public static String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public static void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public static int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public static void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).commit();
    }

    public static long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public static void putSerialNo(int serialNo) {
        sharedPreferences.edit().putInt(KEY_SERIAL_NO, serialNo).commit();
    }

    public static int getSerialNo() {
        int serNo = sharedPreferences.getInt(KEY_SERIAL_NO, 1);
        if (serNo >= 1000000) {
            putSerialNo(1);
        }
        if (serNo == 0) {
            putSerialNo(1);
        }
        return sharedPreferences.getInt(KEY_SERIAL_NO, 1234);
    }

    public static String getSerialNoForHttp() {
        String str = "0000000000000000";
        putSerialNo(getSerialNo() + 1);

        String no = String.format("%016d", getSerialNo());
        Log.i("", "getSerialNoForHttp: " + no);
        return no;
    }

    public static void putBatchNo(String batchNo) {
        sharedPreferences.edit().putString(KEY_BATCH_NO, batchNo).commit();
    }

    public static String getBatchNo() {
        return sharedPreferences.getString(KEY_BATCH_NO, "000001");
    }

    public static void putMerchantNo(String merchantNo) {
        sharedPreferences.edit().putString(KEY_MERCHANT_NO, merchantNo).commit();
    }

    public static String getMerchantNo() {
        return sharedPreferences.getString(KEY_MERCHANT_NO, null);
    }

    public static void putTerminalNo(String terminalNo) {
        sharedPreferences.edit().putString(KEY_TERMINAL_NO, terminalNo).commit();
    }

    public static String getTerminalNo() {
        return sharedPreferences.getString(KEY_TERMINAL_NO, null);
    }

    public static void putMerchantName(String merchantName) {
        sharedPreferences.edit().putString(KEY_MERCHANT_NAME, merchantName).commit();
    }

    public static String getMerchantName() {
        return sharedPreferences.getString(KEY_MERCHANT_NAME, null);
    }

    public static boolean put(String merchantNo, String terminalNo) {
        return sharedPreferences.edit().putString(KEY_MERCHANT_NO, merchantNo)
                .putString(KEY_TERMINAL_NO, terminalNo).commit();
    }

    public static boolean putReversal(Reversal reversal) {
        String str = null;
        byte[] up = reversal != null ? reversal.up : null;
        if (up != null && up.length > 0) {
            int type = reversal.type;
            byte[] t = Arrays.copyOf(up, up.length + 1);
            t[t.length - 1] = (byte) type;
            str = Base64.encodeToString(t, Base64.DEFAULT);
        }
        return sharedPreferences.edit().putString(KEY_REVERSAL, str).commit();
    }

    public static Reversal getReversal() {
        String str = sharedPreferences.getString(KEY_REVERSAL, null);
        if (str != null) {
            byte[] t = Base64.decode(str, Base64.DEFAULT);
            int type = t[t.length - 1];
            byte[] up = Arrays.copyOfRange(t, 0, t.length - 1);
            Reversal reversal = new Reversal(type, up);
            return reversal;
        }
        return null;
    }

    public static boolean putScript(String str) {
        return sharedPreferences.edit().putString(KEY_SCRIPT, str).commit();
    }

    public static String getScript() {
        String str = sharedPreferences.getString(KEY_SCRIPT, null);
        if (!TextUtils.isEmpty(str)) {
            return str;
        }
        return null;
    }

    public static boolean putInstiNo(String instiNo) {
        return sharedPreferences.edit().putString("instiNo", instiNo).commit();
    }

    public static String getInstiNo() {
        return sharedPreferences.getString("instiNo", null);
    }


    /**
     * 获取指定位权限
     *
     * @param location 从1开始
     * @return 权限标识 0为关 1为开
     */
    public static boolean getAppointLocationManagerLimit(int location) {
        String managerLimitString = sharedPreferences.getString("managerLimitString", "11111111111111111111111111111111111111111111111111");

        return String.valueOf(managerLimitString.charAt(location - 1)).equals("1");
    }

    public static String getManagerLimitString() {
        String managerLimitString = sharedPreferences.getString("managerLimitString", null);
        if (managerLimitString == null) {
            managerLimitString = "11111111111111111111111111111111111111111111111111";
            sharedPreferences.edit().putString("managerLimitString", managerLimitString).commit();
        }

        return managerLimitString;
    }

    public static void setManagerLimitString(String managerLimitString) {
        sharedPreferences.edit().putString("managerLimitString", managerLimitString).commit();
    }

    /**
     * 设置指定位权限
     *
     * @param location 从1开始
     */
    public static void setAppointLocationManagerLimit(int location, boolean limitFlag) {
        String managerLimitString = sharedPreferences.getString("managerLimitString", "11111111111111111111111111111111111111111111111111");

        StringBuilder stringBuilder = new StringBuilder(managerLimitString);
        stringBuilder.replace(location - 1, location, limitFlag ? "1" : "0");

        sharedPreferences.edit().putString("managerLimitString", stringBuilder.toString()).commit();
    }


    /**
     * 获取主IP地址
     *
     * @return 主IP地址
     */
    public static String getMainIPAndPortString() {
        String mainIPAndPort = sharedPreferences.getString("mainIPAndPort", null);
        if (mainIPAndPort == null) {
//            mainIPAndPort = "96.0.42.220:20000";
//            mainIPAndPort = "172.16.1.192:10000";
            mainIPAndPort = "10.30.4.182:10000";
            sharedPreferences.edit().putString("mainIPAndPort", mainIPAndPort).commit();
        }

        return mainIPAndPort;
    }

    /**
     * 设置主IP地址
     */
    public static void setMainIPAndPortString(String mainIPAndPort) {
        sharedPreferences.edit().putString("mainIPAndPort", mainIPAndPort).commit();
    }

    /**
     * 获取备份IP地址
     *
     * @return 备份IP地址
     */
    public static String getBackupIPAndPortString() {
        return sharedPreferences.getString("backupIPAndPort", null);
    }

    /**
     * 设置备份IP
     */
    public static void setBackupIPAndPortString(String backupIPAndPort) {
        sharedPreferences.edit().putString("backupIPAndPort", backupIPAndPort).commit();
    }

    /**
     * POS终端应用类型	                   N2	60
     * 超时时间	                       N2	2位数字表示的秒数
     * 拨号重拨次数（终端不解析）	       N1	1位数字
     * 三个交易电话号码之一（终端不解析）  N14	14位数字
     * 三个交易电话号码之二（终端不解析）  N14	14位数字
     * 三个交易电话号码之三（终端不解析）  N14	14位数字
     * 一个管理电话号码（终端不解析）	  N14	14位数字
     * 是否支持小费	                  N1	1—支持，0—不支持
     * 小费百分比	                      N2	2位数字，00—99
     * 是否支持手工输入卡号              N1	    1—支持，0—不支持
     * 是否自动签退	                 N1	    1—自动，0—不自动
     * 冲正重发次数	                 N1	    1位数字
     * 离线交易上送方式               	N1	    1—随下笔联机交易上送一笔离线交易，0—批结算之前一并上送
     * 撤销交易控制信息               	N5
     * 消费撤销交易是否需要刷卡（缺省1）
     * 消费撤销交易是否需要输入密码（缺省1）
     * 预授权完成撤销交易是否需要刷卡/手输卡号（缺省1）
     * 预授权完成撤销交易是否需要输入密码（缺省1）
     * 预授权撤销交易是否需要输入密码（缺省1）
     * 支持的交易类型	                N32
     * 1 查询 2 预授权 3 预授权撤销 4 预授权完成联机 5 预授权完成撤销
     * 6 消费 7 消费撤销 8 退货 9 离线结算 10 离线结算调整
     * 11	预授权完成离线
     * 12	EMV
     * 13	离线消费
     * 14	追加预授权
     * 15	电子钱包
     * 16	微信支付
     * 17	支付宝支付
     * 18	百度钱包
     * 19	积分兑换
     * 20	转账
     * 21	充值
     * 22	公共缴费
     * 23	信用卡还款
     * 24	云账户
     * 25	延期
     * 26	激活
     * 27	修改密码
     * 28	重置密码
     * 29	挂失
     * 30	解挂
     * 31	手机绑定
     * 32	取消手机绑定
     * 热敏打印联数	                 N1	    默认为2
     * 是否屏蔽卡号	                 N1	    1-屏蔽，0-不屏蔽（默认屏蔽）
     * 是否显示LOGO	                 N1	    1-显示，0-不显示（默认显示）
     */
    public static String getParams() {
        return sharedPreferences.getString("publicParams", null);
    }

    public static void setParmas(String parmas) {
        sharedPreferences.edit().putString("publicParams", parmas).commit();
    }

    public static int getOverTimeInt() {
        return sharedPreferences.getInt(KEY_OVER_TIME, 60);
    }

    public static void setOverTimeInt(int overTime) {
        sharedPreferences.edit().putInt(KEY_OVER_TIME, overTime).commit();
    }

    public static int getChongZhengRepeatSend() {
        return sharedPreferences.getInt(KEY_CHONG_ZHENG_REPEAT_SEND, 3);
    }

    public static void setChongZhengRepeatSend(int chongZhengRepeatSend) {
        sharedPreferences.edit().putInt(KEY_CHONG_ZHENG_REPEAT_SEND, chongZhengRepeatSend).commit();
    }

    public static Boolean getDoSignRepeatSend() {
        return sharedPreferences.getBoolean(KEY_DO_SIGN_REPEAT_SEND, true);
    }

    public static void setDoSignRepeatSend(boolean doSignRepeatSend) {
        sharedPreferences.edit().putBoolean(KEY_DO_SIGN_REPEAT_SEND, doSignRepeatSend).commit();
    }

    public static int getMaxTradeNum() {
        return sharedPreferences.getInt(KEY_MAX_TRADE_NUM, 9999);
    }

    public static void setMaxTradeNum(int maxTradeNum) {
        sharedPreferences.edit().putInt(KEY_MAX_TRADE_NUM, maxTradeNum).commit();
    }

    public static String getLastSettle() {
        return sharedPreferences.getString("lastSettle", null);
    }

    public static void setLastSettle(String lastSettle) {
        sharedPreferences.edit().putString("lastSettle", lastSettle).commit();
    }

    public static boolean getConsume_Control() {
        return sharedPreferences.getBoolean("Consume_Control", true);
    }

    public static void setConsume_Control(boolean Consume_Control) {
        sharedPreferences.edit().putBoolean("Consume_Control", Consume_Control).commit();
    }

    public static boolean getConsumeCancel_Control() {
        return sharedPreferences.getBoolean("ConsumeCancel_Control", true);
    }

    public static void setConsumeCancel_Control(boolean ConsumeCancel_Control) {
        sharedPreferences.edit().putBoolean("ConsumeCancel_Control", ConsumeCancel_Control).commit();
    }

    public static boolean getretfundControl() {
        return sharedPreferences.getBoolean("Retfund_Control", true);
    }

    public static void setretfundControl(boolean retfundControl) {
        sharedPreferences.edit().putBoolean("Retfund_Control", retfundControl).commit();
    }

    public static boolean getbalanceinquiryControl() {
        return sharedPreferences.getBoolean("BalanceInquiry_Control", true);
    }

    public static void setbalanceinquiryControl(boolean balanceinquiryControl) {
        sharedPreferences.edit().putBoolean("BalanceInquiry_Control", balanceinquiryControl).commit();
    }

    public static boolean getPreCancel_Control() {
        return sharedPreferences.getBoolean("PreCancel_Control", true);
    }

    public static void setPreCancel_Control(boolean PreCancel_Control) {
        sharedPreferences.edit().putBoolean("PreCancel_Control", PreCancel_Control).commit();
    }

    public static boolean getPreComplete_Control() {
        return sharedPreferences.getBoolean("PreComplete_Control", true);
    }

    public static void setPreComplete_Control(boolean PreComplete_Control) {
        sharedPreferences.edit().putBoolean("PreComplete_Control", PreComplete_Control).commit();
    }

    public static boolean getPre_Control() {
        return sharedPreferences.getBoolean("Pre_Control", true);
    }

    public static void setPre_Control(boolean Pre_Control) {
        sharedPreferences.edit().putBoolean("Pre_Control", Pre_Control).commit();
    }

    public static boolean getPreCompleteCancel_Control() {
        return sharedPreferences.getBoolean("PreCompleteCancel_Control", true);
    }

    public static void setPreCompleteCancel_Control(boolean PreCompleteCancel_Control) {
        sharedPreferences.edit().putBoolean("PreCompleteCancel_Control", PreCompleteCancel_Control).commit();
    }

    public static boolean getUnionPayQRCode_Control() {
        return sharedPreferences.getBoolean("UnionPayQRCode_Control", true);
    }

    public static void setUnionPayQRCode_Control(boolean UnionPayQRCode_Control) {
        sharedPreferences.edit().putBoolean("UnionPayQRCode_Control", UnionPayQRCode_Control).commit();
    }

    public static boolean getFullMess_isencry() {
        return sharedPreferences.getBoolean("FullMess_isencry", false);
    }

    public static void setFullMess_isencry(boolean FullMess_isencry) {
        sharedPreferences.edit().putBoolean("FullMess_isencry", FullMess_isencry).commit();
    }


    public static boolean getPreCompleteCancel_IsSwipCard() {
        return sharedPreferences.getBoolean("PreCompleteCancel_IsSwipCard", true);
    }

    public static void setPreCompleteCancel_IsSwipCard(boolean PreCompleteCancel_IsSwipCard) {
        sharedPreferences.edit().putBoolean("PreCompleteCancel_IsSwipCard", PreCompleteCancel_IsSwipCard).commit();
    }

    public static boolean getConsumeCancel_IsSwipCard() {
        return sharedPreferences.getBoolean("ConsumeCancel_IsSwipCard", true);
    }

    public static void setConsumeCancel_IsSwipCard(boolean ConsumeCancel_IsSwipCard) {
        sharedPreferences.edit().putBoolean("ConsumeCancel_IsSwipCard", ConsumeCancel_IsSwipCard).commit();
    }

    public static boolean getSettlement_IsAutoSignout() {
        return sharedPreferences.getBoolean("Settlement_IsAutoSignout", true);
    }

    public static void setSettlement_IsAutoSignout(boolean Settlement_IsAutoSignout) {
        sharedPreferences.edit().putBoolean("Settlement_IsAutoSignout", Settlement_IsAutoSignout).commit();
    }

    public static boolean getDirector_IsPwd() {
        return sharedPreferences.getBoolean("Director_IsPwd", true);
    }

    public static void setDirector_IsPwd(boolean Director_IsPwd) {
        sharedPreferences.edit().putBoolean("Director_IsPwd", Director_IsPwd).commit();
    }

    public static boolean getHandInput_IsPwd() {
        return sharedPreferences.getBoolean("HandInput_IsPwd", true);
    }

    public static void setHandInput_IsPwd(boolean HandInput_IsPwd) {
        sharedPreferences.edit().putBoolean("HandInput_IsPwd", HandInput_IsPwd).commit();
    }

    public static boolean getTips_IsSupport() {
        return sharedPreferences.getBoolean("Tips_IsSupport", true);
    }

    public static void setTips_IsSupport(boolean Tips_IsSupport) {
        sharedPreferences.edit().putBoolean("Tips_IsSupport", Tips_IsSupport).commit();
    }


    public static boolean getConsumeCancel_IsPwd() {
        return sharedPreferences.getBoolean("ConsumeCancel_IsPwd", true);
    }

    public static void setConsumeCancel_IsPwd(boolean ConsumeCancel_IsPwd) {
        sharedPreferences.edit().putBoolean("ConsumeCancel_IsPwd", ConsumeCancel_IsPwd).commit();
    }

    public static boolean getPreCancel_IsPwd() {
        return sharedPreferences.getBoolean("PreCancel_IsPwd", true);
    }

    public static void setPreCancel_IsPwd(boolean PreCancel_IsPwd) {
        sharedPreferences.edit().putBoolean("PreCancel_IsPwd", PreCancel_IsPwd).commit();
    }

    public static boolean getPreCompleteCancel_IsPwd() {
        return sharedPreferences.getBoolean("PreCompleteCancel_IsPwd", true);
    }

    public static void setPreCompleteCancel_IsPwd(boolean PreCompleteCancel_IsPwd) {
        sharedPreferences.edit().putBoolean("PreCompleteCancel_IsPwd", PreCompleteCancel_IsPwd).commit();
    }

    public static boolean getConsumeComleteRequest_Ispwd() {
        return sharedPreferences.getBoolean("ConsumeComleteRequest_Ispwd", true);
    }

    public static void setConsumeComleteRequest_Ispwd(boolean ConsumeComleteRequest_Ispwd) {
        sharedPreferences.edit().putBoolean("ConsumeComleteRequest_Ispwd", ConsumeComleteRequest_Ispwd).commit();
    }

    //Print_Pieces
    public static int getPrint_Pieces() {
        return sharedPreferences.getInt("Print_Pieces", 1);
    }

    public static void setPrint_Pieces(int Print_Pieces) {
        sharedPreferences.edit().putInt("Print_Pieces", Print_Pieces).commit();
    }

    public static boolean getTitle_IsLogo() {
        return sharedPreferences.getBoolean("Title_IsLogo", true);
    }

    public static void setTitle_IsLogo(boolean Title_IsLogo) {
        sharedPreferences.edit().putBoolean("Title_IsLogo", Title_IsLogo).commit();
    }

    //Print_Control

    public static boolean getPrint_Control() {
        return sharedPreferences.getBoolean("Print_Control", true);
    }

    public static void setPrint_Control(boolean Print_Control) {
        sharedPreferences.edit().putBoolean("Print_Control", Print_Control).commit();
    }

    //SignDataStr
    public static String getSignDataStr() {
        return sharedPreferences.getString("SignDataStr", null);
    }

    public static void setSignDataStr(String SignDataStr) {
        sharedPreferences.edit().putString("SignDataStr", SignDataStr).commit();
    }

    //Print_Repeat
    public static boolean getPrint_Repeat() {
        return sharedPreferences.getBoolean("Print_Repeat", false);
    }

    public static void setPrint_Repeat(boolean Print_Repeat) {
        sharedPreferences.edit().putBoolean("Print_Repeat", Print_Repeat).commit();
    }

    //Electric_Signatur
    public static boolean getElectric_Signatur() {
        return sharedPreferences.getBoolean("Electric_Signatur", true);
    }

    public static void setElectric_Signatur(boolean Electric_Signatur) {
        sharedPreferences.edit().putBoolean("Electric_Signatur", Electric_Signatur).commit();
    }

    //key1
    public static String getkey1() {
        return sharedPreferences.getString("key1", "");
    }

    public static void setkey1(String key1) {
        sharedPreferences.edit().putString("key1", key1).commit();
    }

    //Random
    public static String getRandom() {
        return sharedPreferences.getString("Random", "");
    }

    public static void setRandom(String Random) {
        sharedPreferences.edit().putString("Random", Random).commit();
    }

    //activation
    public static boolean getActivation() {
        return sharedPreferences.getBoolean("activation", true);
    }

    public static void setActivation(boolean activation) {
        sharedPreferences.edit().putBoolean("activation", activation).commit();
    }

    // relogin = 3;
    public static int getRelogin() {
        return sharedPreferences.getInt("relogin", 3);
    }

    public static void setRelogin(int relogin) {
        sharedPreferences.edit().putInt("relogin", relogin).commit();
    }

    // rechongzheng
    public static int getRechongzheng() {
        return sharedPreferences.getInt("rechongzheng", 3);
    }

    public static void setRechongzheng(int rechongzheng) {
        sharedPreferences.edit().putInt("rechongzheng", rechongzheng).commit();
    }

    //tradetimeout
    public static int getTradetimeout() {
        return sharedPreferences.getInt("tradetimeout", 60);
    }

    public static void setTradetimeout(int tradetimeout) {
        sharedPreferences.edit().putInt("tradetimeout", tradetimeout).commit();
    }

    //IsLock
    public static boolean getIsLock() {
        return sharedPreferences.getBoolean("IsLock", false);//默认为锁定
    }

    public static void setIsLock(boolean IsLock) {
        sharedPreferences.edit().putBoolean("IsLock", IsLock).commit();
    }
}
