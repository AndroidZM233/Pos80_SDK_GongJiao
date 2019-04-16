package com.spd.yinlianpay.comm;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;


import com.spd.yinlianpay.TransactionInfo;
import com.spd.yinlianpay.WeiPassGlobal;
import com.spd.yinlianpay.card.TLV;
import com.spd.yinlianpay.context.MyContext;
import com.spd.yinlianpay.iso8583.Body;
import com.spd.yinlianpay.iso8583.Field;
import com.spd.yinlianpay.iso8583.Msg;
import com.spd.yinlianpay.iso8583.PayException;
import com.spd.yinlianpay.iso8583.PayInputStream;
import com.spd.yinlianpay.iso8583.PayOutputStream;
import com.spd.yinlianpay.listener.OnCommonListener;
import com.spd.yinlianpay.logutil.LogWriter;
import com.spd.yinlianpay.trade.TradeInfo;
import com.spd.yinlianpay.util.DataConversionUtils;
import com.spd.yinlianpay.util.MacEcbUtils;
import com.spd.yinlianpay.util.PrefUtil;
import com.spd.yinlianpay.util.Reversal;
import com.spd.yinlianpay.util.TripleDes;
import com.spd.yinlianpay.util.UtilMac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ui.wangpos.com.utiltool.ByteUtil;
import ui.wangpos.com.utiltool.HEXUitl;
import ui.wangpos.com.utiltool.MoneyUtil;
import ui.wangpos.com.utiltool.Util;
import wangpos.sdk4.libbasebinder.Core;


/**
 * Created by guoxiaomeng on 2017/6/23.
 */

public class ChanelPacket {
    private final static String FORMAT =
            "2:主账号,LLVAR-N19;"// P91 该账号最多19个数字字符
                    + "3:交易处理码,N6;"//
                    + "4:交易金额,N12;"//
                    + "11:受卡方系统跟踪号,N6;"//
                    + "12:受卡方所在地时间,N6;"//
                    + "13:受卡方所在地日期,N4;"//
                    + "14:卡有效期,N4;"//
                    + "15:清算日期,N4;"//
                    + "22:服务点输入方式码,N3;"// P96
                    + "23:卡序列号,N4;"// TODO 右靠BCD码, 所以修改 P96 // + "23:卡序列号,N3;"// P96
                    + "25:服务点条件码,N2;"// P97
                    + "26:服务点PIN获取码,N2;"// P97
                    + "32:受理机构标识码,LLVAR-N11;"// P98
                    + "35:2磁道数据,LLVAR-N999;"// P98 //TODO Z37
                    + "36:3磁道数据,LLLVAR-N104;"// P98 //TODO Z104
                    + "37:检索参考号,AN12;"//
                    + "38:授权标识应答码,AN6;"//
                    + "39:应答码,AN2;"// P99
                    + "41:受卡机终端标识码,ANS8;"// P100
                    + "42:受卡方标识码,ANS15;"// P100
                    + "43:商户名称 ,ANS8;"//
                    + "44:附加响应数据,LLVAR-ANS40;"// P100,数据类型存疑,长度出错了 // TODO AN25
                    + "45:自定义域,LLVAR-ANS999;"// P112 //自定义域
                    + "46:自定义域,LLLVAR-ANS999;"// P112 //自定义域
                    + "47:自定义域,LLLVAR-ANS999;"// P112 //自定义域
                    + "48:附加数据－私有,LLLVAR-N322;"// P100
                    + "49:交易货币代码,AN3;"//
                    + "50:电子签名,LLLVAR-ANS999;"//
                    + "52:个人标识码数据,B128;"// P103
                    + "53:安全控制信息,N16;"// P103
                    + "54:余额,LLLVAR-AN40;"// P103 LLLVAR-AN20 // TODO AN20
                    + "55:IC卡数据域,LLLVAR-ANS512;"// P104 格式存疑
                    + "57:便民宝数据参数域,LLLVAR-ANS999;"// P104 格式存疑
                    + "58:PBOC电子钱包标准的交易信息,LLLVAR-ANS100;"//
                    + "59:银联钱包信息域,LLLVAR-ANS999;"// P112 //自定义域
                    + "60:自定义域,LLLVAR-N999;"// P109 // TODO N17
                    + "61:原始信息域,LLLVAR-N29;"// P112
                    + "62:自定义域,LLLVAR-ANS512;"// P112 //自定义域 中消金特有类型 hex length
                    + "63:自定义域,LLLVAR-ANS163;"// P127 //自定义域
                    + "64:MAC,B64;"// P128 //自定义域 //报文鉴别码
            ;

    public ChanelPacket() {
    }

    public final static Field ITEMS[] = Field.makeItems(FORMAT, true);

    //
    public TradeInfo doSettlement(String operatorNo, List<TransactionInfo> settleList, OnCommonListener listener) {
        long debitAmount = 0, creditAmount = 0;
        int debitTimes = 0, creditTimes = 0;
        int tradeType;
        ArrayList<TransactionInfo> tcList = new ArrayList<TransactionInfo>();
        for (TransactionInfo info : settleList) {
            tradeType = info.getTransType();
            if (tradeType == TradeInfo.Type_Sale || tradeType == TradeInfo.Type_AuthComplete) { // 借记
                debitAmount += Double.parseDouble(info.getAmount());
                debitTimes++;
            } else {
                creditAmount += Double.parseDouble(info.getAmount());
                creditTimes++;
            }
            if (info.getServiceCode().startsWith("05") && info.getIcData() != null) {
                Log.i("doSettlement", "getServiceCode " + info.getServiceCode());
                if (info.getTransType() != TradeInfo.Type_Void
                        && info.getTransType() != TradeInfo.Type_Refund
                        && info.getTransType() != TradeInfo.Type_CompleteVoid
                ) {
                    tcList.add(info);
                }
            }
        }
        sendProgress("Batch settlement", listener);
        TradeInfo tradeInfo = new TradeInfo();
        try {
            byte[] up = makeSettlement(operatorNo, debitAmount, debitTimes, creditAmount, creditTimes);
            byte[] down = CommunAction.doNet(up);
            Msg msg = decode(down);
            System.out.println("----settlement result-----" + msg);
            Body b = msg.body;
            if (msg.getReqCode().getFlag() == 'A' || msg.getReqCode().getCode().equalsIgnoreCase("77")) {
                String field48 = b.getField(48);
                Log.i("ChanelPacket", "doSettlement: field48" + field48);
                boolean valid = field48 != null && field48.length() >= 31;
                int result = valid ? field48.charAt(30) - '0' : -1;
                if (result == 1 || result == 2 || result == 0) {
                    String f63 = msg.body.getField(63);
                    if (!TextUtils.isEmpty(f63) && f63.length() >= 41) {
                        String ver = f63.substring(39, 41);
                        String verNo = f63.substring(7, 39).trim();
                        Log.i("login", "login: " + ver + "--<" + verNo);
                        try {
                            if (!MyContext.Ver.equalsIgnoreCase(verNo)) {
                                ;
                            }
                        } catch (Exception ignored) {
                        }
                    } else {
                    }
                    tradeInfo.setExtEx(field48);
                    tradeInfo.setSerialNo(b.get(11));
                    tradeInfo.setTime(b.get(12));
                    tradeInfo.setDate(b.get(13));
                    tradeInfo.setInstiNo(b.get(32));
                    tradeInfo.setReferNo(b.get(37));
                    tradeInfo.setBatchNo(b.get(60).substring(2, 8));
                    tradeInfo.isOK = true;
                    if (!tcList.isEmpty()) {
                        piShangSong(result, tcList, listener);
                    }
                } else {
                    tradeInfo.isOK = true;

                }
            } else {
                tradeInfo.errorMsg = msg.getField39Code();
            }
        } catch (Exception e) {
            e.printStackTrace();
            tradeInfo.errorMsg = e.getMessage();
        }

        return tradeInfo;
    }

    private void doTcup(String netCode, ArrayList<TransactionInfo> tcList, OnCommonListener listener) {
        ArrayList<TransactionInfo> list = tcList;
        TransactionInfo temp;
        int count = -1;
        while (++count < 3 && list.size() > 0) {
            for (int i = 0; i < list.size(); ) {
                temp = list.get(i);
                try {
                    byte[] up, down;
                    up = makeTcUp(temp, netCode);
                    down = CommunAction.doNet(up);
                    Msg msg = decode(down);
                    System.out.println("----Send response on TC\n" + msg);
                    if (msg.getReqCode().getFlag() == 'A') {
                        System.out.println("-----Successfully sent---------" + temp.getTransaceNo());
                        list.remove(temp);
                    } else {
                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    i++;
                }
            }
        }
    }

    private void piShangSong(int result, ArrayList<TransactionInfo> tcList, OnCommonListener listener) {
        int tcSize = tcList.size();
        String tip = "Reconciliation，";
        String netCode = "203"; // IC批上送
        if (result > 1) {
            tip = "Unbalanced reconciliation，";
            netCode = "205";
        }
        sendProgress(tip + "Batch delivery...", listener);
        doTcup(netCode, tcList, listener);
        if (tcList.isEmpty()) {
            System.out.println("TC batch sent successfully---------");
        } else {
            System.out.println("TC batch delivery is not completed-----" + tcList.size());
        }
        sendProgress("Batch delivery completed", listener);

    }

    private void sendProgress(String progress, OnCommonListener listener) {
        if (listener != null) {
            listener.onProgress(progress);
        }
    }

    private boolean doUpFinish(String netCode, int count) {
        try {
            byte[] up = makeUpFinish(netCode, count);
            byte[] down = CommunAction.doNet(up);
            Msg msg = decode(down);
            System.out.println("---Batch send end message response\n" + msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 批上送金融交易/批上送结束报文
     *
     * @param netCode
     * @param count
     * @return
     * @throws Exception
     */
    private byte[] makeUpFinish(String netCode, int count) throws Exception {
        Body b = new Body(ITEMS);
        b.setType("0320");
        b.setField(11, Util.to(PrefUtil.getSerialNo(), 6));
        b.setField(41, PrefUtil.getTerminalNo());
        b.setField(42, PrefUtil.getMerchantNo());
        b.setField(48, String.valueOf(count));
        b.setField(60, "00" + PrefUtil.getBatchNo() + netCode);

        return makeMsg(b, true);
    }

    /**
     * 批上送金融交易/批上送结束报文
     *
     * @param info
     * @param code
     * @return
     * @throws PayException
     */
    private byte[] makeTcUp(TransactionInfo info, String code) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0320");
        if (info.getCardNo() != null) {
            b.setField(2, info.getCardNo());
        }
        b.setField(4, String.valueOf(info.getAmount()));
        b.setField(11, info.getTransaceNo());
        b.setField(22, info.getServiceCode());
        b.setField(23, info.getCardSn());
        b.setField(41, PrefUtil.getTerminalNo());
        b.setField(42, PrefUtil.getMerchantNo());
        b.setField(55, Util.bytes2string(HEXUitl.hexToBytes(info.getIcData())));
        b.setField(60, "00" + PrefUtil.getBatchNo() + code + 5 + "0");
        b.setField(60, "00" + PrefUtil.getBatchNo() + code + "62");

        b.setField(62, "61" + "00" + "00" + MoneyUtil.toCent(info.getAmount()) + "156");

        return makeMsg(b, false);
    }

    private byte[] makeSettlement(String operatorNo, long debitAmount, int debitTimes,
                                  long creditAmount, int creditTimes) throws PayException {
        Log.i("setTransType", "makeSettlement: " + WeiPassGlobal.getTransactionInfo().getTransType());
        Body b = new Body(ITEMS);
        b.setType("0500");
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());
        b.setField(41, PrefUtil.getTerminalNo());
        b.setField(42, PrefUtil.getMerchantNo());
        b.setField(48, longTo(debitAmount, 12) + longTo(debitTimes, 3)
                + longTo(creditAmount, 12) + longTo(creditTimes, 3) + "0"
                + "0000000000000000000000000000000");
        b.setField(48, "00000000000000000000000000000000000000000000000000000000000000");
        b.setField(49, "156");
        b.setField(60, "00" + PrefUtil.getBatchNo() + "201");
        b.setField(63, "0" + operatorNo);
        b.setField(63, "0" + "1 9999" + MyContext.Ver + "                  ");
        return makeMsg(b, false);
    }

    private String longTo(long data, int digit) {
        long t = 1000000000000000L;
        String s = String.valueOf(t + data);
        return s.substring(s.length() - digit);
    }

    private class loginBody {
//        位 域名定义 属性 格式 类型 请求 响应 备 注
//        消息类型 n4 BCD 0800 0810 MSG-TYPE-ID
//        位元表 b64 BINARY M M BIT MAP
//11 受卡方系统跟踪号 n6 BCD M M POS 终端交易流水
//12 受卡方所在地时间 n6 hhmmss BCD M
//13 受卡方所在地日期 n4 MMDD BCD M
//32 受理方标识码 n..11 LLVAR BCD M
//37 检索参考号 an12 ASCII M
//39 应答码 an2 ASCII M
//41 受卡机终端标识码 ans8 ASCII M M 终端代码
//        中国银联
//        版权所有Q/CUP 009.1—2015
//                176
//        位 域名定义 属性 格式 类型 请求 响应 备 注
//42 受卡方标识码 ans15 ASCII M M 商户代码
//60 自定义域 n…017 LLLVAR BCD M M
//60.1 交易类型码 n2 BCD M M 填“00”
//                60.2 批次号 n6 BCD M M
//60.3 网络管理信息码 n3 BCD M M
//        单倍长密钥算法终端用 001/
//        双倍长密钥算法终端用 003/
//        双倍长密钥算法（含磁道密钥）终
//        端用 004
//                62
//        终端信息/
//        终端密钥
//        b...084 LLLVAR BINARY C C
//        终端可获取设备信息且在请求报
//        文中出现，使用用法十九；
//        应答报文按照下列要求填写：
//        当 39 域为“00”时必选，
//        当 60.3 域填写 001 时包含 PIK、
//        MAK，共 24 字节；
//        当 60.3 域填写 003 时包含 PIK、
//        MAK，共 40 字节；
//        当 60.3 域填写 004 时包含 PIK、
//        MAK 和 TDK，共 60 字节
//63 自定义域 ans...003 LLLVAR ASCII M
//63.1 操作员代码 an3 ASCII M
//9.4.

        private String type;
        private String bmpFile;
        private String posDealNub;
        private String times;
        private String dates;
        private String shouliFlag;

    }

    /**
     * 组签到 报文
     */

    public static byte[] login(String operatorNo) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0800");// 0620/0630
        WeiPassGlobal.getTransactionInfo().setTransaceNo(Util.to(PrefUtil.getSerialNo(), 6));
        //设置11域数据
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(60, "00" + PrefUtil.getBatchNo() + (PrefUtil.getISSUPPORTSM() ? "005" : "003"));
        b.setField(63, "0" + operatorNo);
        Log.i("stw", "login:=================="+ PrefUtil.getBatchNo()+"==");
        return makeMsg(b, false);
    }

    /**
     * 签退
     */
    public static byte[] loginOut(String operatorNo) throws PayException {
        WeiPassGlobal.transactionClear();
        Log.i("loginOut", "loginOut: " + WeiPassGlobal.getTransactionInfo().getTransType());
        Body b = new Body(ITEMS);
        b.setType("0820");// 0620/0630
        WeiPassGlobal.getTransactionInfo().setTransaceNo(Util.to(PrefUtil.getSerialNo(), 6));
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(60, "00" + PrefUtil.getBatchNo() + "002");
        return makeMsg(b, false);
    }

    private void parsePublicKey(String f62, ArrayList<String> keyList) {
        int start = 2;
        int end = start + 46;
        int length = f62.length();
        String temp;
        while (end <= length) {
            temp = f62.substring(start, end);
            if (!keyList.contains(temp)) {
                keyList.add(temp);
            }
            start = end;
            end = start + 46;
        }
    }

    /**
     * 解析报文
     * @param b
     * @param mac
     * @return
     * @throws PayException
     */
    private static byte[] makeMsg(Body b, boolean mac) throws PayException {
        long time = System.currentTimeMillis();
        if (PrefUtil.getTPDU() == null || PrefUtil.getTPDU().length() <= 0) {
            throw new PayException("TPDU is null!");
        }
        byte[] rs = null;
        try {
            PayOutputStream baos = new PayOutputStream();
            if (mac) {
                Log.i("AllinPya2", "64----------");
                b.setField(64, "0000000000000000");
            }

            baos.writeBCD_c(PrefUtil.getTPDU());
            baos.writeBCD_c(PrefUtil.getHead());
            int pos = baos.size();
            b.toByteArray(baos);
            if (!b.getType().equalsIgnoreCase("0400") && !b.getType().startsWith("08") && (WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_Sale
                    || WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_Void
                    || WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_Auth
                    || WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_Cancel
                    || WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_AuthComplete
                    || WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_CompleteVoid)
            ) {
                //保存冲正记录
                byte[] reversalData = Arrays.copyOfRange(baos.toByteArray(), 11, baos.toByteArray().length);
                PrefUtil.putReversal(new Reversal(WeiPassGlobal.getTransactionInfo().getTransType(), reversalData));
                Log.i("rev", b.getType() + " makeMsg: " + HEXUitl.bytesToHex(reversalData) + "type" + WeiPassGlobal.getTransactionInfo().getTransType());
            }


            if (mac) {
                byte[] bmac = baos.toByteArray(pos, baos.size() - pos - 8);
                try {

                    byte[] pbOutdata = new byte[20];
                    int[] pbOutdataLen = new int[2];
                    //int ret = MyContext.mCore.getMacEx(MyContext.keyPacketName,0,new byte[8],bmac.length,bmac,4,pbOutdata,pbOutdataLen);
                    byte[] vectordata = new byte[8];
                    int vectorLen = 8;
                    //int algorithmType = Core.ALGORITHM_3DES;
                   /* if (PrefUtil.getISSUPPORTSM())
                        algorithmType = 0x10;*/
//                    int ret = MyContext.mCore.getMacWithAlgorithm(MyContext.keyPacketName, Core.ALGORITHM_3DES, vectorLen, vectordata, bmac.length, bmac, 4, pbOutdata, pbOutdataLen);
//                    Log.i("stw", "makeMsg: &&&&&&&&&&微智结果===" + DataConversionUtils.byteArrayToString(pbOutdata));

//                    Log.i("stw", "makeMsg微智 mac值==" + DataConversionUtils.byteArrayToString(bmac));
                    Log.i("stw", "makeMsg:保存的工作秘钥===" + PrefUtil.getMacKey());
                    String macDecrept = TripleDes.decryptDES(PrefUtil.getMasterKey(), PrefUtil.getMacKey()).substring(0, 16);
                    String macsss = UtilMac.bcd2Str(MacEcbUtils.getMac(DataConversionUtils.hexStringToByteArray(macDecrept), bmac));
                    pbOutdata = DataConversionUtils.hexStringToByteArray(macsss);
                    pbOutdataLen[0] = pbOutdata.length;
                    time = System.currentTimeMillis();
                    if (pbOutdataLen[0] % 8 != 0) {
                        System.arraycopy(pbOutdata, 2, bmac, 0, pbOutdataLen[0] - 2);
                    } else {
                        System.arraycopy(pbOutdata, 0, bmac, 0, pbOutdataLen[0]);
                    }
                    baos.set(baos.size() - 8, bmac);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            rs = baos.toByteArray();
            Msg msg;
            time = System.currentTimeMillis();
            {
                byte[] dcodeRs = Arrays.copyOf(rs, rs.length);
//                msg = decode(dcodeRs);
            }
            return rs;
        } catch (PayException ex) {
            ex.printStackTrace();
            LogWriter.print("send:" + HEXUitl.bytesToHex(rs) + "/r/n" + Log.getStackTraceString(ex) + "/r/n");
            throw ex;
        }
    }

    public static byte[] odaMack(String amount) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0200");// mess type 0200/0210
        if (WeiPassGlobal.getTransactionInfo().getCardNo() != null && WeiPassGlobal.getTransactionInfo().getCardNo().length() > 0) {
            b.setField(2, WeiPassGlobal.getTransactionInfo().getCardNo());// account
        }

        b.setField(3, "000000");//
        if (WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.TYPE_HELP_FARMERS_DRAW) {
            b.setField(3, "010000");//
        }
        b.setField(4, amount);// amount
        //serialNo = pos.getSerialNo();
        WeiPassGlobal.getTransactionInfo().setTransaceNo(Util.to(PrefUtil.getSerialNo(), 6));
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());//serial no
        b.setField(14, WeiPassGlobal.getTransactionInfo().getExpireDate());// expire date
        b.setField(22, WeiPassGlobal.getTransactionInfo().getServiceCode());// service code
        b.setField(23, WeiPassGlobal.getTransactionInfo().getCardSn());// card sn
        b.setField(25, "00");
//         if (WeiPassGlobal.getTransactionInfo().getPin() != null && WeiPassGlobal.getTransactionInfo().getPin().length() > 0) {
//             b.setField(26, PrefUtil.getISSUPPORTSM() ? "12" : "06");// max pin
//             b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());// PIN data
//         }
        if (WeiPassGlobal.getTransactionInfo().getTrack2() != null && WeiPassGlobal.getTransactionInfo().getTrack2().length() > 0) {
            b.setField(35, WeiPassGlobal.getTransactionInfo().getTrack2());
        }

        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());// 受卡机终端标识码
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());// 受卡方标识码
        b.setField(43, "0000HNGJ");// 前置系统标识
        /*if (WeiPassGlobal.getTransactionInfo().getTrack1() != null && WeiPassGlobal.getTransactionInfo().getTrack1().length() > 0) {
            b.setField(45,WeiPassGlobal.getTransactionInfo().getTrack1());
        }*/
        if (WeiPassGlobal.getTransactionInfo().getDevicetype() != null && WeiPassGlobal.getTransactionInfo().getDevicetype().length() == 2) {
            b.setField(48, WeiPassGlobal.getTransactionInfo().getDevicetype());// 交易货币 代码
        }
        b.setField(49, "156");// 交易货币 代码
        Log.i("info", "makeSale: " + WeiPassGlobal.getTransactionInfo().getPin());
//         if (WeiPassGlobal.getTransactionInfo().getPin() != null) {
//             b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());// PIN data
//         }
//         //b.setField(53, "2" + (PrefUtil.getISSUPPORTSM() ? "3" : "6") + "00000000000000");
//         b.setField(53, "0600000000000000");
        if (WeiPassGlobal.getTransactionInfo().getIcData() != null && WeiPassGlobal.getTransactionInfo().getIcData().length() > 0) {
            b.setFieldData(55, HEXUitl.hexToBytes(WeiPassGlobal.getTransactionInfo().getIcData()));// IC card data
            //b.setField(55,"9F2608E7B33BDAB05F4D2F9F2701809F100807010103A02002019F3704BB9E542C9F36020006950500800080009A031703029C01199F02060000000000015F2A02034482027C009F1A0203449F03060000000000009F3303E0E1C89F34031E03009F3501229F1E0839303632323336348408A0000003330101029F090200009F410400000077");
        }
        b.setField(60, "22" + WeiPassGlobal.getTransactionInfo().getBacthNo() + "11160");
        if (WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.TYPE_HELP_FARMERS_DRAW) {
            b.setField(60, "01" + WeiPassGlobal.getTransactionInfo().getBacthNo() + "11160");
        }
//        b.setField(63, "001                                                            A058310010000000000        0000000078172A5F20181211095006");
        return makeMsg(b, true);
    }

    /**
     * 组消费记录 报文
     *
     * @param amount
     * @return
     * @throws PayException
     */
    public static byte[] makeSale(String amount) throws PayException {
        long time = System.currentTimeMillis();
        Body b = new Body(ITEMS);
        b.setType("0200");// mess type 0200/0210
        if (WeiPassGlobal.getTransactionInfo().getCardNo() != null && WeiPassGlobal.getTransactionInfo().getCardNo().length() > 0) {
            b.setField(2, WeiPassGlobal.getTransactionInfo().getCardNo());// account
        }

        b.setField(3, "000000");//
        if (WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.TYPE_HELP_FARMERS_DRAW) {
            b.setField(3, "010000");//
        }
        b.setField(4, amount);// amount
        //serialNo = pos.getSerialNo();
        WeiPassGlobal.getTransactionInfo().setTransaceNo(Util.to(PrefUtil.getSerialNo(), 6));
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());//serial no
        b.setField(14, WeiPassGlobal.getTransactionInfo().getExpireDate());// expire date
        b.setField(22, WeiPassGlobal.getTransactionInfo().getServiceCode());// service code
        b.setField(23, WeiPassGlobal.getTransactionInfo().getCardSn());// card sn
        b.setField(25, "00");
        if (WeiPassGlobal.getTransactionInfo().getPin() != null && WeiPassGlobal.getTransactionInfo().getPin().length() > 0) {
            b.setField(26, PrefUtil.getISSUPPORTSM() ? "12" : "06");// max pin
            b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());// PIN data
        }
        if (WeiPassGlobal.getTransactionInfo().getTrack2() != null && WeiPassGlobal.getTransactionInfo().getTrack2().length() > 0) {
            b.setField(35, WeiPassGlobal.getTransactionInfo().getTrack2());
        }

        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());// 受卡机终端标识码
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());// 受卡方标识码
        /*if (WeiPassGlobal.getTransactionInfo().getTrack1() != null && WeiPassGlobal.getTransactionInfo().getTrack1().length() > 0) {
            b.setField(45,WeiPassGlobal.getTransactionInfo().getTrack1());
        }*/
        if (WeiPassGlobal.getTransactionInfo().getDevicetype() != null && WeiPassGlobal.getTransactionInfo().getDevicetype().length() == 2) {
            b.setField(48, WeiPassGlobal.getTransactionInfo().getDevicetype());// 交易货币 代码
        }
        b.setField(49, "156");// 交易货币 代码
        Log.i("info", "makeSale: " + WeiPassGlobal.getTransactionInfo().getPin());
        if (WeiPassGlobal.getTransactionInfo().getPin() != null) {
            b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());// PIN data
        }
        //b.setField(53, "2" + (PrefUtil.getISSUPPORTSM() ? "3" : "6") + "00000000000000");
        b.setField(53, "0600000000000000");
        if (WeiPassGlobal.getTransactionInfo().getIcData() != null && WeiPassGlobal.getTransactionInfo().getIcData().length() > 0) {
            b.setFieldData(55, HEXUitl.hexToBytes(WeiPassGlobal.getTransactionInfo().getIcData()));// IC card data
            //b.setField(55,"9F2608E7B33BDAB05F4D2F9F2701809F100807010103A02002019F3704BB9E542C9F36020006950500800080009A031703029C01199F02060000000000015F2A02034482027C009F1A0203449F03060000000000009F3303E0E1C89F34031E03009F3501229F1E0839303632323336348408A0000003330101029F090200009F410400000077");
        }
        b.setField(60, "22" + WeiPassGlobal.getTransactionInfo().getBacthNo() + "11160");
        if (WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.TYPE_HELP_FARMERS_DRAW) {
            b.setField(60, "01" + WeiPassGlobal.getTransactionInfo().getBacthNo() + "11160");
        }
        Log.i("stw", "makeSale:组报文@@@@@@@@@@=== " + (System.currentTimeMillis() - time));

//        b.setField(63, "001                                                            A058310010000000000        0000000078172A5F20181211095006");
        return makeMsg(b, true);
    }

    //35
    private static String encryption(String cardinfo) {
        try {
            byte[] vectordata = new byte[8];
            int vectorLen = 8;
            String str = "";
            cardinfo = cardinfo.replace("=", "D");
            str = cardinfo.length() + cardinfo;
            Log.d("35--->", str);
            int count = 48 - str.length();
            for (int i = 0; i < count; i++) {
                str += "0";
            }
            byte[] cardbyte = HEXUitl.hexToBytes(str);
            byte[] cardinfobyte = new byte[((int) Math.ceil(cardbyte.length / 8.0)) * 8];
            System.arraycopy(cardbyte, 0, cardinfobyte, 0, cardbyte.length);
            byte[] outtype = new byte[1024];
            int[] outlen = new int[1];
            int algorithmType = Core.ALGORITHM_3DES;
            if (PrefUtil.getISSUPPORTSM()) {
                algorithmType = 0x10;
            }
            int ret = MyContext.mCore.dataEnDecryptEx(algorithmType, 0, MyContext.keyPacketName, 1, vectordata.length, vectordata, cardinfobyte.length, cardinfobyte, 0, outtype, outlen);
            if (ret == 0) {
                byte[] retbyte = new byte[outlen[0]];
                System.arraycopy(outtype, 0, retbyte, 0, outlen[0]);
                str = HEXUitl.bytesToHex(retbyte);
                byte[] tmpbyte = new byte[1024];

            }

            return str;
        } catch (Exception ex) {
            ex.printStackTrace();
            return cardinfo;
        }
    }


    public static byte[] getReverseUp(int flag, Body msg) throws Exception {
        switch (flag) {
            case TradeInfo.Type_Sale:
            case TradeInfo.Type_Auth:
            case TradeInfo.Type_AuthComplete:
                return makeSaleReversal(msg);//消费冲正
            /*case TradeInfo.Type_Auth:
                return */
            case TradeInfo.Type_Void:
            case TradeInfo.Type_CompleteVoid:
            case TradeInfo.Type_Cancel:
                return ConsumptionRevokedReversal(msg);
            /*case TradeInfo.Type_Auth:
                return makePreAuthorReverseData(msg);
            case TradeInfo.Type_Cancel:
                return makePreAuthorCancelReverseData(msg);
            case TradeInfo.Type_AuthComplete:
                return makePreAuthorFinishReqReverseData(msg);
            case TradeInfo.Type_CompleteVoid:
                return makePreAuthFinishCancelReverse(msg);
            case TradeInfo.Type_IntegralUndoDownVoid:
                return makeIntegralUndoDown(msg);
            case TradeInfo.Type_IntegralUndoDown:
                return  makeIntegralUndoDown(msg);
            case  TradeInfo.Type_HelpFarmersunDown:
                return makeHelpFarmersunDown(msg);
            case TradeInfo.Type_CouponSale:
            case TradeInfo.Type_CouponVoid:
            case TradeInfo.Type_PointSale:
            case TradeInfo.Type_PointVoid:
            case TradeInfo.Type_ETicketSale:
            case TradeInfo.Type_ETicketVoid:
                return makeVASChongZheng(msg);*/
            default:
                System.err.println("不支持的冲正类型：" + flag);
        }
        return null;
    }

    /**
     * 组 冲正报文
     *
     * @param map
     * @return
     * @throws PayException
     */
    private static byte[] makeSaleReversal(Body map) throws PayException {
        // 2010-P137
        Body b = new Body(ITEMS);
        b.setType("0400");//  0200/0210
        b.setField(2, map.get(2));
        b.setField(3, map.get(3));
        b.setField(4, map.get(4));
        b.setField(11, map.get(11));
        b.setField(14, map.get(14));
        b.setField(22, map.get(22));
        b.setField(23, map.get(23));
        b.setField(25, map.get(25));
        b.setField(35, map.get(35));
        b.setField(36, map.get(36));
        b.setField(38, map.get(38));
        b.setField(39, "98");//  2010-P138
        b.setField(41, map.get(41));
        b.setField(42, map.get(42));
        b.setField(46, map.get(46));
        b.setField(47, map.get(47));
        b.setField(49, map.get(49));

        String f55 = map.get(55);
        if (f55 != null) {
            try {
                byte[] b55 = Util.string2bytes(f55);
                HashMap<String, String> fmap = new HashMap<String, String>();
                TLV.anaTag(b55, fmap);
                String r = TLV.pack(fmap, "95,9F1E,9F10,9F36,DF31");
                b.setField(55, Util.bytes2string(HEXUitl.hexToBytes(r)));// IC卡数据域
            } catch (Exception ex) {
                ex.printStackTrace();
                b.setField(55, f55);
            }
        }

        String f60 = map.get(60);
        String type = f60.substring(0, 2);
        String netCode = f60.substring(8);
        b.setField(60, type + WeiPassGlobal.getTransactionInfo().getBacthNo() + netCode);

        b.setField(61, f60.substring(2, 8) + map.get(11) + map.get(13));

        b.setField(62, map.get(62));
        return makeMsg(b, true);
    }

    public static Msg decode(byte[] bs) throws PayException {
        return decode(bs, false);
    }

    public static Body decodeRev(byte[] bs) throws PayException {
        Body b = new Body(ITEMS);
        PayInputStream ins = new PayInputStream(bs);
        b.read(ins);
        return b;
    }

    public static Msg decode(byte[] bs, boolean flag) throws PayException {
        try {
            Body b = new Body(ITEMS);
            PayInputStream ins = new PayInputStream(bs);
            String tpdu = ins.readBCD_c(PrefUtil.getTPDU().length());
            String head;

            if (PrefUtil.getFullMess_isencry()) {
                head = ins.readBCD_c(94);
            } else {
                head = ins.readBCD_c(12);
            }

            b.read(ins);

            Msg r = new Msg();
            r.tpdu = tpdu;
            r.head = head;
            r.body = b;
            r.macResult = Msg.MAC_NONE;
            Log.d("decode MSG", r.toString());
            if (r.body.get(64) != null) {
                // check MAC
                String macStr = r.body.getField(64);
                String bs8583Str = HEXUitl.bytesToHex(bs);
                int index = bs8583Str.lastIndexOf(macStr);
                Log.i("mac data ", "decode: " + bs8583Str + "\n" + macStr + "\n" + index + "\n" + r.body.getField(64) + "\n" + bs8583Str.substring(0, index) + "\n" + ByteUtil.hexString2Bytes(bs8583Str.substring(index)).length + "\n" + bs8583Str.substring(index));
                byte[] ts;

                ts = Arrays.copyOfRange(bs, 11, bs.length - ByteUtil.hexString2Bytes(bs8583Str.substring(index)).length);

                int macLenIndex = ByteUtil.hexString2Bytes(bs8583Str.substring(index)).length;
                byte[] tmac = Arrays.copyOfRange(bs, bs.length - macLenIndex, bs.length - (macLenIndex - 8));
                try {
                    byte[] mac = new byte[8];
                    byte[] pbOutdata = new byte[20];
                    int[] pbOutdataLen = new int[2];
                    byte[] vectordata = new byte[8];
                    int vectorLen = 8;
                    Log.d("mac", HEXUitl.bytesToHex(ts));
                    // int algorithmType = ;
                   /* if (PrefUtil.getISSUPPORTSM())
                        algorithmType = 0x10;*/
                    int ret = MyContext.mCore.getMacWithAlgorithm(MyContext.keyPacketName, Core.ALGORITHM_3DES, vectorLen, vectordata, ts.length, ts, 4, pbOutdata, pbOutdataLen);
                    if (ret == 0) {
                        if (pbOutdata[0] == 0x00 && pbOutdata[1] % 8 == 0) {
                            System.arraycopy(pbOutdata, 2, mac, 0, mac.length);
                        } else {
                            System.arraycopy(pbOutdata, 0, mac, 0, pbOutdataLen[0] - 2);
                        }
                    } else {
                        Log.d("mac", "exception");
                    }

                    Log.d("mac", HEXUitl.bytesToHex(tmac) + " mac" + HEXUitl.bytesToHex(mac).substring(0, 16));
                    if (HEXUitl.bytesToHex(tmac).equals(HEXUitl.bytesToHex(mac).substring(0, 16))) {
                        r.macResult = Msg.MAC_OK;
                    } else {
                        r.macResult = Msg.MAC_ERR;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return r;
        } catch (Exception ex) {
            ex.printStackTrace();
            Msg r = new Msg();
            r.macResult = Msg.MAC_NONE;
            return r;
        }
    }


    public static byte[] queryPKey(String infocode) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("");// 0620/0630
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(60, "96" + PrefUtil.getBatchNo() + infocode);
        b.setField(62, "9F0605DF000000039F220101");
        return makeMsg(b, false);
    }


    public static byte[] updateMasterKey(String termKeyInfo) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0800");// 0620/0630
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(60, "99" + PrefUtil.getBatchNo() + "0030");
        b.setField(62, termKeyInfo);
        b.setField(63, "001" + Build.SERIAL.substring(1, 9));
        return makeMsg(b, false);
    }

    /**
     * 回响测试
     *
     * @param type
     * @param n
     * @return
     * @throws PayException
     */
    public static byte[] queryparam(int type, int n) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0820");
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(60, "00" + PrefUtil.getBatchNo() + type);
        b.setField(62, String.valueOf(100 + n));
        return makeMsg(b, false);
    }

    /**
     * 组pos参数传递 报文
     *
     * @param n
     * @param type
     * @param keyList
     * @return
     * @throws PayException
     */
    public static byte[] downparam(int n, int type, List<String> keyList) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0800");
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(60, "00" + PrefUtil.getBatchNo() + type);
        String info = keyList.get(n);
        if (370 == type) {
//            info = info.substring(0, 38);
            Log.e("info", info);
        }

        if (380 == type) {
            Log.e("info", info);
        }
        b.setFieldData(62, HEXUitl.hexToBytes(info));

        return makeMsg(b, false);
    }

    public static boolean downFinish(int type) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0800");
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(60, "00" + PrefUtil.getBatchNo() + type);

        try {
            Msg msg = ChanelPacket.decode(CommunAction.doNet(makeMsg(b, false)));
            if (msg.getReqCode().getFlag() == 'A') {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    //
    public static byte[] makePreAuth(String amount) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0100");//  0100/0110
        if (WeiPassGlobal.getTransactionInfo().getCardNo() != null && WeiPassGlobal.getTransactionInfo().getCardNo().length() > 0) {
            b.setField(2, WeiPassGlobal.getTransactionInfo().getCardNo());
        }
        b.setField(3, "030000");
        b.setField(4, amount);
        //serialNo = pos.getSerialNo();
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());
        if (WeiPassGlobal.getTransactionInfo().getExpireDate() != null && WeiPassGlobal.getTransactionInfo().getExpireDate().length() > 0) {
            b.setField(14, WeiPassGlobal.getTransactionInfo().getExpireDate());
        }
        b.setField(22, WeiPassGlobal.getTransactionInfo().getServiceCode());
        if (WeiPassGlobal.getTransactionInfo().getCardSn() != null && WeiPassGlobal.getTransactionInfo().getCardSn().length() > 0) {
            b.setField(23, WeiPassGlobal.getTransactionInfo().getCardSn());
        }
        b.setField(25, "06");
        if (WeiPassGlobal.getTransactionInfo().getPin() != null && WeiPassGlobal.getTransactionInfo().getPin().length() > 0) {
            b.setField(26, PrefUtil.getISSUPPORTSM() ? "12" : "06");
            b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());
        }
        if (WeiPassGlobal.getTransactionInfo().getTrack2() != null && WeiPassGlobal.getTransactionInfo().getTrack2().length() > 0) {
            b.setField(35, WeiPassGlobal.getTransactionInfo().getTrack2());
        }

        if (WeiPassGlobal.getTransactionInfo().getTrack3() != null && WeiPassGlobal.getTransactionInfo().getTrack3().length() > 0) {
            b.setField(36, WeiPassGlobal.getTransactionInfo().getTrack3());
        }
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(49, "156");
        /*if (card.pin != null) {
            b.setField(52, card.pin);// PIN data
        }*/
        b.setField(53, "2" + (PrefUtil.getISSUPPORTSM() ? "3" : "6") + "00000000000000");//  P161 2010-P104
        if (WeiPassGlobal.getTransactionInfo().getIcData() != null && WeiPassGlobal.getTransactionInfo().getIcData().length() > 0) {
            b.setField(55, Util.bytes2string(HEXUitl.hexToBytes(WeiPassGlobal.getTransactionInfo().getIcData())));// ic card data
        }
        b.setField(60, "10" + WeiPassGlobal.getTransactionInfo().getBacthNo() + "111" + "6" + "0");
        return makeMsg(b, true);
    }


    public static byte[] ConsumptionRevoked(TransactionInfo tradeInfo) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0200");//  0200/0210
        if (!TextUtils.isEmpty(WeiPassGlobal.getTransactionInfo().getCardNo())) {
            b.setField(2, WeiPassGlobal.getTransactionInfo().getCardNo());
        }
        b.setField(3, "200000");
        b.setField(4, String.valueOf(tradeInfo.getAmount()));


        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());
        b.setField(14, WeiPassGlobal.getTransactionInfo().getExpireDate());
        b.setField(22, WeiPassGlobal.getTransactionInfo().getServiceCode());
        b.setField(23, WeiPassGlobal.getTransactionInfo().getCardSn());
        b.setField(25, "00");
        if (WeiPassGlobal.getTransactionInfo().getPin() != null) {
            b.setField(26, PrefUtil.getISSUPPORTSM() ? "12" : "06");
        }
        if (WeiPassGlobal.getTransactionInfo().getTrack2() != null) {
            b.setField(35, WeiPassGlobal.getTransactionInfo().getTrack2());
        }

        b.setField(37, tradeInfo.getReferNo());
        if (!TextUtils.isEmpty(tradeInfo.getAuthNo())) {
            b.setField(38, tradeInfo.getAuthNo());
        }
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(49, "156");
        if (WeiPassGlobal.getTransactionInfo().getPin() != null) {
            b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());
        }
        b.setField(53, "2" + (PrefUtil.getISSUPPORTSM() ? "3" : "6") + "00000000000000");

        String str = !WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("07") && !WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("91") && !WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("96") && !WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("98") ? "5" : "6";
        b.setField(60, "23" + WeiPassGlobal.getTransactionInfo().getBacthNo() + "111" + str + "0");
        b.setField(61, WeiPassGlobal.getTransactionInfo().getBacthNo() + WeiPassGlobal.getTransactionInfo().getOldTrace());
        return makeMsg(b, true);
    }

    public static byte[] reFound() throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0220");//  0200/0210
        if (!TextUtils.isEmpty(WeiPassGlobal.getTransactionInfo().getCardNo())) {
            b.setField(2, WeiPassGlobal.getTransactionInfo().getCardNo());
        }
        b.setField(3, "200000");
        b.setField(4, WeiPassGlobal.getTransactionInfo().getAmount());
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());
        b.setField(14, WeiPassGlobal.getTransactionInfo().getExpireDate());
        b.setField(22, WeiPassGlobal.getTransactionInfo().getServiceCode());
        b.setField(23, WeiPassGlobal.getTransactionInfo().getCardSn());

        b.setField(25, "00");
        if (WeiPassGlobal.getTransactionInfo().getPin() != null) {
            b.setField(26, PrefUtil.getISSUPPORTSM() ? "12" : "06");
        }
        if (WeiPassGlobal.getTransactionInfo().getTrack2() != null) {
            b.setField(35, WeiPassGlobal.getTransactionInfo().getTrack2());
        }
        if (WeiPassGlobal.getTransactionInfo().getTrack3() != null) {
            b.setField(36, WeiPassGlobal.getTransactionInfo().getTrack3());
        }
        b.setField(37, WeiPassGlobal.getTransactionInfo().getReferNo());

        b.setField(41, PrefUtil.getTerminalNo());
        b.setField(42, PrefUtil.getMerchantNo());
        if (!TextUtils.isEmpty(WeiPassGlobal.getTransactionInfo().getAuthNo())) {
            b.setField(42, PrefUtil.getMerchantNo());
        }
        b.setField(49, "156");
        if (WeiPassGlobal.getTransactionInfo().getPin() != null) {
            b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());
        }
        b.setField(53, "2" + (PrefUtil.getISSUPPORTSM() ? "3" : "6") + "00000000000000");// P161 2010-P104

//        b.setField(60, "25" + PrefUtil.getBatchNo() + "000" + getTerminalCapability() + "0" + "0000" + "00");
        b.setField(60, "25" + WeiPassGlobal.getTransactionInfo().getBacthNo() + (WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("02") ? "111" : "031") + "6" + "0");
        b.setField(63, "000");

        b.setField(61, "000000" + "000000" + WeiPassGlobal.getTransactionInfo().getTransDate());

        return makeMsg(b, true);
    }


    public static byte[] makePreAuthComplete(TransactionInfo tradeInfo) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0200");//  0200/0210
        if (WeiPassGlobal.getTransactionInfo().getCardNo() != null && WeiPassGlobal.getTransactionInfo().getCardNo().length() > 0) {
            b.setField(2, WeiPassGlobal.getTransactionInfo().getCardNo());
        }
        b.setField(3, "000000");
        b.setField(4, tradeInfo.getAmount());
        //6 DCC
        //serialNo = pos.getSerialNo();
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());
        if (WeiPassGlobal.getTransactionInfo().getExpireDate() != null && WeiPassGlobal.getTransactionInfo().getExpireDate().length() > 0) {
            b.setField(14, WeiPassGlobal.getTransactionInfo().getExpireDate());
        }
        b.setField(22, WeiPassGlobal.getTransactionInfo().getServiceCode());

        b.setField(25, "06");
        if (WeiPassGlobal.getTransactionInfo().getPin() != null && WeiPassGlobal.getTransactionInfo().getPin().length() > 0) {
            b.setField(26, PrefUtil.getISSUPPORTSM() ? "12" : "06");
            b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());// PIN
        }
        if (WeiPassGlobal.getTransactionInfo().getTrack2() != null && WeiPassGlobal.getTransactionInfo().getTrack2().length() > 0) {
            b.setField(35, WeiPassGlobal.getTransactionInfo().getTrack2());
        }

        if (WeiPassGlobal.getTransactionInfo().getTrack3() != null && WeiPassGlobal.getTransactionInfo().getTrack3().length() > 0) {
            b.setField(36, WeiPassGlobal.getTransactionInfo().getTrack3());
        }
        b.setField(38, tradeInfo.getAuthNo());
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(49, "156");

        b.setField(53, "2" + (PrefUtil.getISSUPPORTSM() ? "3" : "6") + "00000000000000");//  P161 2010-P104
        if (WeiPassGlobal.getTransactionInfo().getIcData() != null && WeiPassGlobal.getTransactionInfo().getIcData().length() > 0) {
            b.setField(55, Util.bytes2string(HEXUitl.hexToBytes(WeiPassGlobal.getTransactionInfo().getIcData())));// IC card data
        }
        b.setField(60, "20" + WeiPassGlobal.getTransactionInfo().getBacthNo() + "111" + "6" + "0");
        b.setField(61, "000000" + WeiPassGlobal.getTransactionInfo().getOldTrace() + tradeInfo.getTransDate());
        return makeMsg(b, true);
    }


    public static byte[] makePreAuthVoid(TransactionInfo tradeInfo) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0100");//  0100/0110
        if (WeiPassGlobal.getTransactionInfo().getCardNo() != null && WeiPassGlobal.getTransactionInfo().getCardNo().length() > 0) {
            b.setField(2, WeiPassGlobal.getTransactionInfo().getCardNo());// account
        }
        b.setField(3, "200000");
        b.setField(4, tradeInfo.getAmount());// amount
        //6 DCC
        //serialNo = pos.getSerialNo();
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());
        if (WeiPassGlobal.getTransactionInfo().getExpireDate() != null && WeiPassGlobal.getTransactionInfo().getExpireDate().length() > 0) {
            b.setField(14, WeiPassGlobal.getTransactionInfo().getExpireDate());
        }
        b.setField(22, WeiPassGlobal.getTransactionInfo().getServiceCode());

        b.setField(25, "06");
        if (WeiPassGlobal.getTransactionInfo().getPin() != null && WeiPassGlobal.getTransactionInfo().getPin().length() > 0) {
            b.setField(26, PrefUtil.getISSUPPORTSM() ? "12" : "06");
            b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());
        }
        if (WeiPassGlobal.getTransactionInfo().getTrack2() != null && WeiPassGlobal.getTransactionInfo().getTrack2().length() > 0) {
            b.setField(35, WeiPassGlobal.getTransactionInfo().getTrack2());
        }

        if (WeiPassGlobal.getTransactionInfo().getTrack3() != null && WeiPassGlobal.getTransactionInfo().getTrack3().length() > 0) {
            b.setField(36, WeiPassGlobal.getTransactionInfo().getTrack3());
        }
        b.setField(38, tradeInfo.getAuthNo());
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(49, "156");

        b.setField(53, "2" + (PrefUtil.getISSUPPORTSM() ? "3" : "6") + "00000000000000");//  P161 2010-P104

        b.setField(60, "11" + WeiPassGlobal.getTransactionInfo().getBacthNo() + "111" + "6" + "0");
        b.setField(61, "000000" + "000000" + tradeInfo.getTransDate());
        return makeMsg(b, true);
    }

    private static byte[] ConsumptionRevokedReversal(Body body) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0400");//
        b.setField(2, body.get(2));
        b.setField(3, body.get(3));
        b.setField(4, body.get(4));
        b.setField(11, body.get(11));
        b.setField(14, body.get(14));
        b.setField(22, body.get(22));
        b.setField(23, body.get(23));
        b.setField(25, body.get(25));
        b.setField(35, body.get(35));
        b.setField(38, body.get(38));
        b.setField(39, "98");//  2010-P138
        b.setField(41, body.get(41));
        b.setField(42, body.get(42));
        b.setField(46, body.get(46));
        b.setField(47, body.get(47));
        b.setField(49, body.get(49));
        b.setField(53, body.get(53));

        String f60 = body.get(60);
        String type = f60.substring(0, 2);
        String netCode = f60.substring(8);
        b.setField(60, type + WeiPassGlobal.getTransactionInfo().getBacthNo() + netCode);

        b.setField(61, body.get(61));
        return makeMsg(b, true);
    }

    private static byte[] makePreAuthReversal(Msg msg) throws PayException {
        // 2010-P137
        Body map = msg.body;
        Body b = new Body(ITEMS);
        b.setType("0400");//  0200/0210
        b.setField(2, map.get(2));
        b.setField(3, map.get(3));
        b.setField(4, map.get(4));
        b.setField(11, map.get(11));
        b.setField(14, map.get(14));
        b.setField(22, map.get(22));
        b.setField(23, map.get(23));
        b.setField(25, map.get(25));
        b.setField(38, map.get(38));
        b.setField(39, "98");//  2010-P138
        b.setField(41, map.get(41));
        b.setField(42, map.get(42));
        b.setField(49, map.get(49));
        b.setField(53, map.get(53));
        String f55 = map.get(55);
        if (f55 != null) {
            try {
                byte[] b55 = Util.string2bytes(f55);
                HashMap<String, String> fmap = new HashMap<String, String>();
                TLV.anaTag(b55, fmap);
                String r = TLV.pack(fmap, "95,9F1E,9F10,9F36,DF31");
                b.setField(55, Util.bytes2string(HEXUitl.hexToBytes(r)));// IC card data
            } catch (Exception ex) {
                ex.printStackTrace();
                b.setField(55, f55);
            }
        }
        String f60 = map.get(60);
        String type = f60.substring(0, 2);
        String netCode = f60.substring(8);
        b.setField(60, type + WeiPassGlobal.getTransactionInfo().getBacthNo() + netCode);
        return makeMsg(b, true);
    }

    //查询余额：2，3，11，14，22，23，25，26，35，36，41，42，49，52，53，55，60，64
    public static byte[] queryBalance() throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0200");// 消息类型 0200/0210
        if (WeiPassGlobal.getTransactionInfo().getCardNo() != null && WeiPassGlobal.getTransactionInfo().getCardNo().length() > 0) {
            b.setField(2, WeiPassGlobal.getTransactionInfo().getCardNo());
        }
        b.setField(3, "310000");
        //serialNo = pos.getSerialNo();
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());
        b.setField(14, WeiPassGlobal.getTransactionInfo().getExpireDate());
        b.setField(22, WeiPassGlobal.getTransactionInfo().getServiceCode());
        b.setField(23, WeiPassGlobal.getTransactionInfo().getCardSn());
        b.setField(25, "00");
        if (WeiPassGlobal.getTransactionInfo().getPin() != null && WeiPassGlobal.getTransactionInfo().getPin().length() > 0) {
            b.setField(26, PrefUtil.getISSUPPORTSM() ? "12" : "06");
            b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());
        }
        if (WeiPassGlobal.getTransactionInfo().getTrack2() != null && WeiPassGlobal.getTransactionInfo().getTrack2().length() > 0) {
            b.setField(35, WeiPassGlobal.getTransactionInfo().getTrack2());
        }
        if (WeiPassGlobal.getTransactionInfo().getTrack3() != null && WeiPassGlobal.getTransactionInfo().getTrack3().length() > 0) {
            b.setField(36, WeiPassGlobal.getTransactionInfo().getTrack3());
        }
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(49, "156");
        Log.i("info", "queryBalance: " + WeiPassGlobal.getTransactionInfo().getPin());
        if (WeiPassGlobal.getTransactionInfo().getPin() != null) {
            b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());
        }
        b.setField(53, "2" + (PrefUtil.getISSUPPORTSM() ? "3" : "6") + "00000000000000");//  P161 2010-P104
        if (WeiPassGlobal.getTransactionInfo().getIcData() != null && WeiPassGlobal.getTransactionInfo().getIcData().length() > 0) {
            b.setField(55, Util.bytes2string(HEXUitl.hexToBytes(WeiPassGlobal.getTransactionInfo().getIcData())));
        }

        b.setField(60, "01" + WeiPassGlobal.getTransactionInfo().getBacthNo() + "111" + "6" + "0");

        return makeMsg(b, true);
    }

    //2，3，4，11，22，25，41，42，53，49，60，62，63，64
    public static byte[] makeQRSale(String amount) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0200");//  0200/0210
        if (WeiPassGlobal.getTransactionInfo().getCardNo() != null && WeiPassGlobal.getTransactionInfo().getCardNo().length() > 0) {
            b.setField(2, WeiPassGlobal.getTransactionInfo().getCardNo());
        }
        b.setField(3, "000000");
        b.setField(4, amount);
        //serialNo = pos.getSerialNo();
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());
        b.setField(14, WeiPassGlobal.getTransactionInfo().getExpireDate());
        b.setField(22, "032");
        b.setField(23, WeiPassGlobal.getTransactionInfo().getCardSn());
        b.setField(25, "00");
        if (WeiPassGlobal.getTransactionInfo().getPin() != null && WeiPassGlobal.getTransactionInfo().getPin().length() > 0) {
            b.setField(26, PrefUtil.getISSUPPORTSM() ? "12" : "06");
            b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());
        }
        if (WeiPassGlobal.getTransactionInfo().getTrack2() != null && WeiPassGlobal.getTransactionInfo().getTrack2().length() > 0) {
            b.setField(35, WeiPassGlobal.getTransactionInfo().getTrack2());
        }
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(49, "156");
        Log.i("info", "makeSale: " + WeiPassGlobal.getTransactionInfo().getPin());
        if (WeiPassGlobal.getTransactionInfo().getPin() != null) {
            b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());// PIN data
        }
        b.setField(53, "2" + (PrefUtil.getISSUPPORTSM() ? "3" : "6") + "00000000000000");//  P161 2010-P104

        b.setField(60, "22" + WeiPassGlobal.getTransactionInfo().getBacthNo() + "11160");

        b.setField(63, WeiPassGlobal.getTransactionInfo().getMerchantId() + WeiPassGlobal.getTransactionInfo().getTermId() + WeiPassGlobal.getTransactionInfo().getBacthNo()
                + WeiPassGlobal.getTransactionInfo().getTransaceNo() + WeiPassGlobal.getTransactionInfo().getTransDate() + WeiPassGlobal.getTransactionInfo().getDateTime());
        return makeMsg(b, true);
    }

    //：2，3，4，11，22，25，41，42，53，49，60，62，63，64
    public static byte[] makeQRSaleResultQuery(String amount) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0200");//  0200/0210
        if (WeiPassGlobal.getTransactionInfo().getCardNo() != null && WeiPassGlobal.getTransactionInfo().getCardNo().length() > 0) {
            b.setField(2, WeiPassGlobal.getTransactionInfo().getCardNo());
        }
        b.setField(3, "000000");
        b.setField(4, amount);
        //serialNo = pos.getSerialNo();
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());
        b.setField(14, WeiPassGlobal.getTransactionInfo().getExpireDate());
        b.setField(22, "032");
        b.setField(23, WeiPassGlobal.getTransactionInfo().getCardSn());
        b.setField(25, "00");
        if (WeiPassGlobal.getTransactionInfo().getPin() != null && WeiPassGlobal.getTransactionInfo().getPin().length() > 0) {
            b.setField(26, PrefUtil.getISSUPPORTSM() ? "12" : "06");
            b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());
        }
        if (WeiPassGlobal.getTransactionInfo().getTrack2() != null && WeiPassGlobal.getTransactionInfo().getTrack2().length() > 0) {
            b.setField(35, WeiPassGlobal.getTransactionInfo().getTrack2());
        }
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(49, "156");
        Log.i("info", "makeSale: " + WeiPassGlobal.getTransactionInfo().getPin());
        if (WeiPassGlobal.getTransactionInfo().getPin() != null) {
            b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());
        }
        b.setField(53, "2" + (PrefUtil.getISSUPPORTSM() ? "3" : "6") + "00000000000000");

        b.setField(60, "22" + WeiPassGlobal.getTransactionInfo().getBacthNo() + "11160");
        b.setField(63, WeiPassGlobal.getTransactionInfo().getMerchantId() + WeiPassGlobal.getTransactionInfo().getTermId() + WeiPassGlobal.getTransactionInfo().getBacthNo()
                + WeiPassGlobal.getTransactionInfo().getTransaceNo() + WeiPassGlobal.getTransactionInfo().getTransDate() + WeiPassGlobal.getTransactionInfo().getDateTime() +
                WeiPassGlobal.getTransactionInfo().getReferNo());
        return makeMsg(b, true);
    }


    public static byte[] makePreAuthCompleteCancel(TransactionInfo tradeInfo) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0200");//  0200/0210
        if (WeiPassGlobal.getTransactionInfo().getCardNo() != null && WeiPassGlobal.getTransactionInfo().getCardNo().length() > 0) {
            b.setField(2, WeiPassGlobal.getTransactionInfo().getCardNo());
        }
        b.setField(3, "200000");
        b.setField(4, tradeInfo.getAmount());
        //serialNo = pos.getSerialNo();
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());
        if (WeiPassGlobal.getTransactionInfo().getExpireDate() != null && WeiPassGlobal.getTransactionInfo().getExpireDate().length() > 0) {
            b.setField(14, WeiPassGlobal.getTransactionInfo().getExpireDate());
        }
        b.setField(22, WeiPassGlobal.getTransactionInfo().getServiceCode());
        if (WeiPassGlobal.getTransactionInfo().getCardSn() != null && WeiPassGlobal.getTransactionInfo().getCardSn().length() > 0) {
            b.setField(23, WeiPassGlobal.getTransactionInfo().getCardSn());
        }
        b.setField(25, "06");
        if (WeiPassGlobal.getTransactionInfo().getPin() != null && WeiPassGlobal.getTransactionInfo().getPin().length() > 0) {
            b.setField(26, PrefUtil.getISSUPPORTSM() ? "12" : "06");
            b.setField(52, WeiPassGlobal.getTransactionInfo().getPin());
        }
        if (WeiPassGlobal.getTransactionInfo().getTrack2() != null && WeiPassGlobal.getTransactionInfo().getTrack2().length() > 0) {
            b.setField(35, WeiPassGlobal.getTransactionInfo().getTrack2());
        }

        if (WeiPassGlobal.getTransactionInfo().getTrack3() != null && WeiPassGlobal.getTransactionInfo().getTrack3().length() > 0) {
            b.setField(36, WeiPassGlobal.getTransactionInfo().getTrack3());
        }
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(49, "156");
        b.setField(53, "2" + (PrefUtil.getISSUPPORTSM() ? "3" : "6") + "00000000000000");// P161 2010-P104
        if (WeiPassGlobal.getTransactionInfo().getIcData() != null && WeiPassGlobal.getTransactionInfo().getIcData().length() > 0) {
            b.setField(55, Util.bytes2string(HEXUitl.hexToBytes(WeiPassGlobal.getTransactionInfo().getIcData())));// IC card data
        }
        b.setField(60, "21" + WeiPassGlobal.getTransactionInfo().getBacthNo() + "111" + "6" + "0");
        b.setField(61, "000000" + WeiPassGlobal.getTransactionInfo().getOldTrace() + tradeInfo.getTransDate());
        return makeMsg(b, true);
    }

    public static byte[] makeParamePassing() throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0800");// 0620/0630
        WeiPassGlobal.getTransactionInfo().setTransaceNo(Util.to(PrefUtil.getSerialNo(), 6));
//        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        b.setField(60, "00" + PrefUtil.getBatchNo() + "360");
//        b.setField(63, operatorNo);
//        b.setField(63, "01WP10023
        return makeMsg(b, false);
    }


    public static byte[] makeSignUpload(Msg msg) throws PayException {
        Body b = new Body(ITEMS);
        b.setType("0820");// 0200/0210
        if (WeiPassGlobal.getTransactionInfo().getCardNo() != null && WeiPassGlobal.getTransactionInfo().getCardNo().length() > 0) {
            b.setField(2, WeiPassGlobal.getTransactionInfo().getCardNo());
        }
        b.setField(4, WeiPassGlobal.getTransactionInfo().getAmount());
        //serialNo = pos.getSerialNo();
        b.setField(11, WeiPassGlobal.getTransactionInfo().getTransaceNo());

//        b.setField(22, WeiPassGlobal.getTransactionInfo().getServiceCode());
        if (WeiPassGlobal.getTransactionInfo().getCardSn() != null && WeiPassGlobal.getTransactionInfo().getCardSn().length() > 0) {
            b.setField(23, WeiPassGlobal.getTransactionInfo().getCardSn());
        }
        b.setField(37, WeiPassGlobal.getTransactionInfo().getReferNo());
        b.setField(41, WeiPassGlobal.getTransactionInfo().getTermId());
        b.setField(42, WeiPassGlobal.getTransactionInfo().getMerchantId());
        /*if (card.pin != null) {
            b.setField(52, card.pin);// PIN
        }*/
        if (WeiPassGlobal.getTransactionInfo().singeData != null) {
            b.setFieldData(50, WeiPassGlobal.getTransactionInfo().singeData);
        }
        HashMap<String, String> fmap = new HashMap<String, String>();
        String ff01 = "";
        switch (WeiPassGlobal.getTransactionInfo().getTransType()) {
            case TradeInfo.Type_Sale:
                ff01 = "1021";
                break;
            case TradeInfo.Type_Void:
                ff01 = "3021";
                break;
            case TradeInfo.Type_Auth:
                ff01 = "1011";
                break;
            case TradeInfo.Type_AuthComplete:
                ff01 = "1031";
                break;
            case TradeInfo.Type_Cancel:
                ff01 = "3011";
                break;
            case TradeInfo.Type_CompleteVoid:
                ff01 = "3031";
                break;
            case TradeInfo.Type_Refund:
                ff01 = "1051";
                break;
            default:
                break;
        }
        fmap.put("FF01", ff01);
        fmap.put("FF02", "01");
        if (!TextUtils.isEmpty(WeiPassGlobal.getTransactionInfo().getExpireDate())) {
            ;
        }
        fmap.put("FF05", WeiPassGlobal.getTransactionInfo().getExpireDate());
        fmap.put("FF06", "2017" + WeiPassGlobal.getTransactionInfo().getTransDate() + WeiPassGlobal.getTransactionInfo().getTransTime());
        if (!TextUtils.isEmpty(WeiPassGlobal.getTransactionInfo().getAuthNo())) {
            fmap.put("FF07", HEXUitl.bytesToHex(WeiPassGlobal.getTransactionInfo().getAuthNo().getBytes()));
        }
        if (!TextUtils.isEmpty(WeiPassGlobal.getTransactionInfo().getField63()) && WeiPassGlobal.getTransactionInfo().getField63().length() > 3) {
            fmap.put("FF40", WeiPassGlobal.getTransactionInfo().getField63().substring(3));
        }

//        TLV.anaTag(outData, fmap);
        String f55 = TLV.pack(fmap, "FF01,FF02,FF05,FF06,FF07,FF40");
        if (!TextUtils.isEmpty(f55)) {
            b.setField(55, Util.bytes2string(HEXUitl.hexToBytes(f55)));
        }
        b.setField(60, "07" + WeiPassGlobal.getTransactionInfo().getBacthNo() + "0");
        b.setField(63, "435550");
        return makeMsg(b, true);
    }


}
