package com.spd.yinlianpay.card;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.spd.base.utils.LogUtils;
import com.spd.yinlianpay.WeiPassGlobal;
import com.spd.yinlianpay.comm.ChannelTool;
import com.spd.yinlianpay.context.MyContext;
import com.spd.yinlianpay.iso8583.Msg;
import com.spd.yinlianpay.listener.OnTraditionListener;
import com.spd.yinlianpay.trade.ErrorMsg;
import com.spd.yinlianpay.trade.ErrorMsgType;
import com.spd.yinlianpay.trade.TradeInfo;

import com.spd.yinlianpay.util.LedUtils;
import com.spd.yinlianpay.util.PrefUtil;
import com.spd.yinlianpay.util.Utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import sdk4.wangpos.libemvbinder.EmvAppList;
import sdk4.wangpos.libemvbinder.EmvCore;
import sdk4.wangpos.libemvbinder.EmvParam;
import ui.wangpos.com.utiltool.ByteUtil;
import ui.wangpos.com.utiltool.DESUitl;
import ui.wangpos.com.utiltool.HEXUitl;
import ui.wangpos.com.utiltool.MoneyUtil;
import ui.wangpos.com.utiltool.Util;
import wangpos.sdk4.emv.ICallbackListener;
import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.Core;


public class UnionPayCard {
    //处理非接
    public static int contantlessOnlineRet = 0;
    public static final String TAG = "stw";
    private static Context context;
    private static EmvCore emvCore = null;
    private static Handler handler;
    public static List<EmvAppList> emvAIDList;
    private static boolean onlinePinPad = false;
    public static ICallbackListener iCallbackListener = null;

    /**
     * IC卡 EMV complete process
     *
     * @return
     */
    private static int result = 0;
    private static Long systemlongtime;

    //交易开始时初始化
    public static void init(Context context, Handler handler) {
        UnionPayCard.emvCore = MyContext.emvCore;
        Log.v("UnionPayCard", MyContext.emvCore + "");
        UnionPayCard.context = context;
        UnionPayCard.handler = handler;
        onlinePinPad = false;
    }

    public static void setContext(final Activity context) {
        UnionPayCard.emvCore = MyContext.emvCore;
        Log.v("UnionPayCard", MyContext.emvCore + "");
        UnionPayCard.context = context;

    }

    public static int inPutPinCode = 2;
    private final static Random RAN = new Random();
    private final static String F55 = "9F26,9F27,9F10,9F37,9F36,95,9A,9C,9F02,5F2A,82,9F1A,9F03,9F33,9F34,9F35,9F1E,9F41,9F63,"//
            ;//

    private final static String F55_EX = "5F24,57,5A,5F34,50,9F12,4F,9F06,84,9F09,8C,8D,9F4E,9F21,9B";

    private static int path = 0;
    // TVR terminal check
    //

    /**
     * merchant name
     *
     * @param s
     * @return
     */
    final static String getMerchantName20(String s) {
        byte[] bs = new byte[20];
        Arrays.fill(bs, (byte) ' ');
        try {
            byte[] ns = s.getBytes("GBK");
            int len = Math.min(bs.length, ns.length);
            System.arraycopy(ns, 0, bs, 0, len);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return HEXUitl.bytesToHex(bs);
    }


    /**
     * 处理联机返回结果  第四执行
     *
     * @param callBackData
     * @param callBackLen
     * @return
     * @throws RemoteException
     */
    public static int EMV_OnlineProc(byte[] callBackData, int[] callBackLen) throws RemoteException {
        byte[] outRspCod, outAutchCode, outAutchData, outScript;
        int[] outAutachCodeLen, outAutchDataLen, outScriptLen;
        int onlineProcResult = ErrorMsgType.SUCCESS;
        byte[] outData = new byte[512];
        int[] outDataLen = new int[1];
        handler.sendMessage(handler.obtainMessage(MyContext.MSG_PROGRESS, "Online transaction"));
       /* if (WeiPassGlobal.getTransactionInfo().getAid().equals("A000000333010106")) {
            Log.i("ZM", "EMV_OnlineProc 7");
            onlineProcResult = ErrorMsgType.ERR_NOTPROC;
            return TradeInfo.ONLINE_FAILED;
        }*/
        byte[] icData = new byte[512];
        int[] icDataLen = new int[1];
        setField55Dara(icData, 512);
        //String icDataStr = WeiPassGlobal.getTransactionInfo().getIcData();
        LogUtils.i("EMV_OnlineProc 2");
        //发送0200 联机交易
        int result = transOnlineProcess(callBackData, callBackLen);
        callBackData[0] = (byte) result;
        return result;
    }

    /**
     * 发送0200 联机交易
     *
     * @param outData
     * @param outDataLen
     * @return
     */
    private static int transOnlineProcess(byte[] outData, int[] outDataLen) {
        int ret = -1;
        final String[] errMsg = new String[1];
        errMsg[0] = "Network Error";
        int appResult = 0;
        handler.obtainMessage(MyContext.MSG_PROGRESS, "Start sending messages").sendToTarget();

        final byte[][] inputData = {new byte[512]};
        int pos = 0;
        final Msg[] msg = new Msg[1];
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        long amout = Long.parseLong(MoneyUtil.toCent(WeiPassGlobal.getTransactionInfo().getAmount()));
        try {
            if (path == 5) {
                //如果是mag卡重取，1 磁，2，磁
                int rest = emvCore.getMagTrackData_MC(0x02, outData, outDataLen);
                if (rest == 0) {
                    String tagTLV57 = ByteUtil.fromBytes(Arrays.copyOf(outData, outDataLen[0]));
                    if (TextUtils.isEmpty(WeiPassGlobal.getTransactionInfo().getCardNo())) {
                        WeiPassGlobal.getTransactionInfo().setCardNo(tagTLV57.split("D")[0]);
                    }
                    int track2Len = tagTLV57.indexOf("F");
                    if (track2Len >= 0) {
                        WeiPassGlobal.getTransactionInfo().setTrack2(tagTLV57.substring(0, track2Len));
                    } else {
                        WeiPassGlobal.getTransactionInfo().setTrack2(tagTLV57);
                    }
                } else {
                    Log.e(TAG, "getMagTrackData_MC error result = " + rest);
                }
                rest = emvCore.getMagTrackData_MC(0x01, outData, outDataLen);
                if (rest == 0) {
                    String tagTLV57 = ByteUtil.fromBytes(Arrays.copyOf(outData, outDataLen[0]));
                    int track2Len = tagTLV57.indexOf("F");
                    if (track2Len >= 0) {
                        WeiPassGlobal.getTransactionInfo().setTrack1(tagTLV57.substring(0, track2Len));
                    } else {
                        WeiPassGlobal.getTransactionInfo().setTrack1(tagTLV57);
                    }
                } else {
                    Log.e(TAG, "getMagTrackData_MC error result = " + rest);
                }
            }

            LogUtils.i("2821准备发送交易== " + (System.currentTimeMillis() - systemlongtime));
            systemlongtime = System.currentTimeMillis();
            //发起联机
            ChannelTool.doSale(WeiPassGlobal.getTransactionInfo().getAmount(), new OnTraditionListener() {
                @Override
                public void onResult(TradeInfo info) {
                    if ("ODA".equals(info.errorMsg)) {
                        handler.sendMessage(handler.obtainMessage(MyContext.DO_ODA, info.msg));
                    } else {
                        msg[0] = info.msg;
                        WeiPassGlobal.tradeInfo = info;
                    }
                    LogUtils.i("EMV_OnlineProc 6");
                    countDownLatch.countDown();
                    Log.i(TAG, "返回0200发送返回11111111111== " + (System.currentTimeMillis() - systemlongtime));
                }

                @Override
                public void onSuccess() {
                    LogUtils.i("EMV_OnlineProc 5");
                    countDownLatch.countDown();
                }

                @Override
                public void onProgress(String progress) {
                    handler.sendMessage(handler.obtainMessage(MyContext.MSG_PROGRESS, progress));
                    handler.sendMessage(handler.obtainMessage(12313, progress));
                }

                @Override
                public void onError(int errorCode, String errorMsg) {
                    msg[0] = null;
                    errMsg[0] = errorMsg;
                    LogUtils.i("EMV_OnlineProc 4");
                    countDownLatch.countDown();
                    LogUtils.d("errorMsg: " + errorMsg);
                }

                @Override
                public void onDataBack(Msg msg1) {
                    if (msg1 == null) {
                        handler.sendMessage(handler.obtainMessage(MyContext.YuYin_ChuLi));
                    } else {
                        handler.sendMessage(handler.obtainMessage(MyContext.BackMsg, msg1));
                    }

                }

                @Override
                public void onToastError(int errorCode, String errorMsg) {
                    handler.sendMessage(handler.obtainMessage(MyContext.MSG_ERROR
                            , errorMsg));
                    countDownLatch.countDown();
                }

            });
            countDownLatch.await();
            LogUtils.i("EMV_OnlineProc 3");
            systemlongtime = System.currentTimeMillis();
            if (msg[0] == null) {
                handler.sendMessage(handler.obtainMessage(MyContext.Hide_Progress, "msg[0] == null"));
//                handler.sendMessage(handler.obtainMessage(MyContext.MSG_ERROR, errMsg[0]));
                appResult = -1;
                return appResult;
            }
            handler.sendMessage(handler.obtainMessage(MyContext.MSG_PROGRESS, "The message was successfully received."));
        } catch (Exception e) {
            e.printStackTrace();
            appResult = -1;
            System.arraycopy(inputData[0], 0, outData, 1, 2);
            handler.sendMessage(handler.obtainMessage(MyContext.Hide_Progress, "catch"));
//            handler.obtainMessage(MyContext.MSG_ERROR, e.getMessage()).sendToTarget();
            return appResult;
        }
        if (msg[0].getReqCode().getFlag() == 'A') {
            handler.sendMessage(handler.obtainMessage(MyContext.MSG_PROGRESS, "Verify server data"));
            if (WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("02") || onlinePinPad) {
                return 0;
            }
            System.arraycopy(msg[0].body.getFieldData(39), 0, inputData[0], 0, msg[0].body.getFieldData(39).length);
            pos += msg[0].body.getFieldData(39).length;
            if (msg[0].body.getFieldData(38) == null || msg[0].body.getFieldData(38).length == 0) {
                inputData[0][pos++] = 0x00;
            } else {
                inputData[0][pos++] = (byte) ByteUtil.hexString2Bytes(msg[0].body.getField(38)).length;
                System.arraycopy(ByteUtil.hexString2Bytes(msg[0].body.getField(38)), 0, inputData[0], pos, ByteUtil.hexString2Bytes(msg[0].body.getField(38)).length);
                pos += ByteUtil.hexString2Bytes(msg[0].body.getField(38)).length;
            }
            if (msg[0].body.getFieldData(55) == null || msg[0].body.getFieldData(55).length <= 0) {
                System.arraycopy(inputData[0], 0, outData, 1, pos + 1);
                outDataLen[0] = pos + 1;
                return 0;
            }
            TLVList tlvList = TLVList.fromBinary(msg[0].body.getFieldData(55));
            TLV tlv = tlvList.getTLV("91");
            byte[] gettlv91 = new byte[0];
            if (tlv == null) {
                inputData[0][pos++] = 0x00;
            } else {
                gettlv91 = HEXUitl.hexToBytes(tlv.getValue());
                inputData[0][pos++] = (byte) gettlv91.length;
                System.arraycopy(gettlv91, 0, inputData[0], pos, gettlv91.length);
                pos += gettlv91.length;
            }
            TLV tlv71 = tlvList.getTLV("71");
            TLV tlv72 = tlvList.getTLV("72");
            byte tlv71Len = 0;
            if (tlv71 == null) {
                tlv71Len = 0x00;
            } else {
                tlv71Len = (byte) tlv71.getRawData().length;
//				System.arraycopy(tlv71.getBytesValue(), 0, inputData[0], pos, tlv71.getLength());
//				pos+=tlv71.getLength();
            }
            if (tlv72 == null) {
                tlv71Len += 0x00;
            } else {
                tlv71Len += (byte) tlv72.getRawData().length;
            }

            inputData[0][pos++] = tlv71Len;
            if (tlv71Len != 0) {
                if (tlv71 != null) {
                    System.arraycopy(tlv71.getRawData(), 0, inputData[0], pos, tlv71.getRawData().length);
                    pos += tlv71.getRawData().length;
                }
                if (tlv72 != null) {
                    System.arraycopy(tlv72.getRawData(), 0, inputData[0], pos, tlv72.getRawData().length);
                    pos += tlv72.getRawData().length;
                }
            }
            System.arraycopy(inputData[0], 0, outData, 1, pos + 1);
            outDataLen[0] = pos + 1;

        } else {
            return -1;
        }
        Log.i(TAG, "结束正确==== " + (System.currentTimeMillis() - systemlongtime));
        return appResult;
    }

    //交易开始 根据不同状态可以使用 Handler 通知主线程更新 UI,请参考 wPos_SDKDemo
    public static String readBankCardInfo(BankCard mBankCard, final String terminalNo, final String merchantName) {
        result = 0;//重置结果
        path = 0;
        //回调函数 处理内核数据
        iCallbackListener = new ICallbackListener.Stub() {
            @Override
            public int emvCoreCallback(final int i, final byte[] bytes, final byte[] bytes1, final int[] ints) throws RemoteException {
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                LogUtils.i("EMV_OnlineProc start" + i);

                switch (i) {
                    case Core.CALLBACK_ADVICE:
                        //2822
                        LogUtils.i("emvCoreCallback: 返回2822");
                        countDownLatch.countDown();
                        break;
                    case Core.CALLBACK_ONLINE:
                        // 2821  联机请求  Core.CALLBACK_ONLINE 发起联机交易
                        LogUtils.i("EMV_OnlineProc begin");
                        contantlessOnlineRet = EMV_OnlineProc(bytes1, ints);
                        LogUtils.i("EMV_OnlineProc end");
                        countDownLatch.countDown();
                        break;
                    case Core.CALLBACK_AMOUNT:
                        //Core.CALLBACK_AMOUNT 设置金额
                        Log.i(TAG, "emvCoreCallback: 返回向内核返回金额");
                        //向内核返回金额
                        bytes1[0] = 0;
                        byte[] tmp = long2Bytes(Long.parseLong(WeiPassGlobal.getTransactionInfo().getAmount()));
                        byte[] am = new byte[8];
                        am[0] = tmp[7];
                        am[1] = tmp[6];
                        am[2] = tmp[5];
                        am[3] = tmp[4];
                        am[4] = tmp[3];
                        am[5] = tmp[2];
                        am[6] = tmp[1];
                        am[7] = tmp[0];
                        System.arraycopy(am, 0, bytes1, 1, 5);
                        ints[0] = 10;
                        LogUtils.i("非接交易预处理时间== " + (System.currentTimeMillis() - systemlongtime));
                        countDownLatch.countDown();
                        break;
                    default:
                        /*if (i != 1048) {
                            LogUtils.i("emvCoreCallback: 返回2822");
                            countDownLatch.countDown();
                        }*/
                        break;
                }
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("emvCoreCallback threed", "end");
                return 0;
            }
        };
        systemlongtime = System.currentTimeMillis();
        try {
            int retresult = -1;
            //非接交易处理
            if (WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("07")) {
                //非接交易预处理
                retresult = QPBOC_PreProcess();
                if (retresult != ErrorMsgType.SUCCESS) {
                    handler.sendMessage(handler.obtainMessage(MyContext.MSG_ERROR
                            , "transaction failed：" + ErrorMsg.getEmvError(result)));
                    return "";
                } else {
                    //非接交易简化流程
                    retresult = PBOC_Simple(terminalNo, merchantName);
                    Log.i(TAG, "非接交易简化流程时间111111111== " + (System.currentTimeMillis() - systemlongtime));
                }
                if (retresult == ErrorMsgType.SUCCESS) {
                    //交易处理
                    int transResult = EMV_TransProcess(mBankCard);
                    if (transResult == 0) {
                        handler.sendMessage(handler.obtainMessage(MyContext.RESULT_SUCCESS, "transaction success" + result));
                    } else {
                        handler.sendEmptyMessage(transResult);
                    }
                } else {
                    if (retresult != 8200) {
                        handler.sendMessage(handler.obtainMessage(MyContext.MSG_ERROR
                                , "transcation failed：" + ErrorMsg.getEmvError(result)));
                        return "";
                    } else {
                        handler.sendEmptyMessage(8200);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }


    /**
     * 交易处理 第三执行
     *
     * @return
     * @throws RemoteException
     */
    public static int EMV_TransProcess(BankCard mBankCard) throws RemoteException {
        systemlongtime = System.currentTimeMillis();
        boolean reversalFlag = false;
        int[] outlen = new int[1];
        byte[] outcomeData = new byte[128];
        result = emvCore.cardAuth();
        if (result != ErrorMsgType.SUCCESS) {
            emvCore.getOutCome(outcomeData, outlen);
            byte[] outComeData = Arrays.copyOf(outcomeData, outlen[0]);
            //System.arraycopy(outData,0,outComeData,0,len[0]);
            if (outComeData != null && outComeData.length > 0) {
                TLVList tlvList = TLVList.fromBinary(outComeData);
                String value = tlvList.getTLV("DF8129").getValue();
                handler.sendMessage(handler.obtainMessage(MyContext.RESULT_OUTCOME, ParamDefine.getOutCome(HEXUitl.hexToBytes(value)[0])));
            }
            return result;
        }
        /*switch (WeiPassGlobal.getTransactionInfo().getTransType()) {
            case TradeInfo.Type_Sale:
                result = emvCore.cardAuth();
                if (result != ErrorMsgType.SUCCESS) {
                    emvCore.getOutCome(outcomeData, outlen);
                    byte[] outComeData = Arrays.copyOf(outcomeData, outlen[0]);
                    //System.arraycopy(outData,0,outComeData,0,len[0]);
                    if (outComeData != null && outComeData.length > 0) {
                        TLVList tlvList = TLVList.fromBinary(outComeData);
                        String value = tlvList.getTLV("DF8129").getValue();
                        handler.sendMessage(handler.obtainMessage(MyContext.RESULT_OUTCOME, ParamDefine.getOutCome(HEXUitl.hexToBytes(value)[0])));
                    }
                    return result;
                }
                break;
            default:
                break;
        }*/
        LogUtils.d("EMV_TransProcess:" + result);
        if (result == ErrorMsgType.SUCCESS) {
            //AnalyseTVRTSI();
            if (path == 5) {
                WeiPassGlobal.getTransactionInfo().setServiceCode("091");
            } else {
                WeiPassGlobal.getTransactionInfo().setServiceCode(WeiPassGlobal.getTransactionInfo().getServiceCode() + inPutPinCode);
            }
            if (path == TradeInfo.PATH_QPBOC || path == TradeInfo.PATH_MAG || path == TradeInfo.PATH_CHIP) {
                //非接交易  非接 IC 卡有此过程，回调 2821 发起联机
                result = emvCore.procQPBOCTrans(iCallbackListener);
                if (contantlessOnlineRet != 0) {
                    result = contantlessOnlineRet;
                }
//                mBankCard.openCloseCardReader(2,2);
                handler.sendMessage(handler.obtainMessage(MyContext.Hide_Progress, "非接交易"));
                if (contantlessOnlineRet != ErrorMsgType.SUCCESS) {
//                    handler.sendMessage(handler.obtainMessage(MyContext.MSG_ERROR
//                            , contantlessOnlineRet + ""));
                    LogUtils.v("交易 error" + contantlessOnlineRet);
                    return -1;
                }
                // saveEMVTransInfo();
                // AnalyseTVRTSI();
                //QPBOC tvr
                //emvCore.setTLV(0x95, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00});
                //  emvCore.setTLV(0x9B, new byte[]{0x00, 0x00});
                // getEMVTransResult();

            } else {
                // TODO: 2019/4/3  接触交易
            }
        }
        Log.i(TAG, "返回 2821联机请 Core.CALLBACK_ONLINE 发起联机交易3333== " + (System.currentTimeMillis() - systemlongtime));
        return 0;
    }


    private static int saveScriptResult() throws RemoteException {
        Log.v("saveScriptResult", "Save script");
        byte[] outData = new byte[256];
        int[] outDataLen = new int[1];
        //获取发卡行脚本处理结果
        int result = emvCore.getScriptResult(outData, outDataLen);
        Log.e("emvCore", "getScriptResult: " + result);
        if (result == ErrorMsgType.SUCCESS) {
            emvCore.setTLV(0xDF31, outData);

        }
        return result;
    }

    /**
     * EMV
     * 非接交易简化流程  第二执行
     *
     * @param TerminalNo
     * @param MerchantName
     * @return 0  success ： 1 fail
     */
    public static int PBOC_Simple(String TerminalNo, String MerchantName) throws Exception {
        systemlongtime = System.currentTimeMillis();
        long time = System.currentTimeMillis();
        byte[] outData = new byte[1024];
        int[] outStaus = {1024};
        WeiPassGlobal.getTransactionInfo().setTermId(TerminalNo);
        WeiPassGlobal.getTransactionInfo().setMerchantName(MerchantName);
        /*emvCore.getParam(outData, outStaus);
        EmvParam emvParam = new EmvParam(context);
        emvParam.setTransType(WeiPassGlobal.getTransactionInfo().getTransType());
        emvParam.parseByteArray(outData);
        emvParam.setSupportPSESel(1);
        emvParam.setTermCapab(0xE060C8);
        emvCore.setParam(emvParam.toByteArray());*/
        handler.obtainMessage(MyContext.MSG_PROGRESS, "Read card data, please wait。。").sendToTarget();
        // 流水号
        int traceNo = PrefUtil.getSerialNo();
        WeiPassGlobal.getTransactionInfo().setTransaceNo(Util.to(traceNo, 6));
        //应用选择   交易流水号 非接： 没有应用选择过程，此命令直接返回结果；
        //接触： 多应用 IC 卡回调 2817 选择应用，选择后再回调 2819 设置金额;
        //普通 IC 卡直接回调 2819 设置金额;
        Log.i("stw", "应用选择00000000000000000=====" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        int result = emvCore.appSel(getCardType(), traceNo, iCallbackListener);

        if (result != 0) {
            return result;
        }
       /* //EMV参数设置
        result = setEMVConfig();
        if (result != 0) {
            return result;
        }*/
        handler.obtainMessage(MyContext.MSG_PROGRESS, "Getting card number。").sendToTarget();
        // 读应用数据 非接： 没有应用选择过程，此命令直接返回结果；
        //接触： 普通 IC 卡直接返回结果;
        //暂无；
        Log.i("stw", "应用选择11111111111111=====" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        result = emvCore.readAppData(iCallbackListener);

        if (result != 0) {
            return result;
        }
        Log.i("stw", "应用选择22222222222222=====" + (System.currentTimeMillis() - time));
        /*time = System.currentTimeMillis();
        emvCore.getTLV(0x9F6e, outData, outStaus);
        if (outStaus[0] != 0x00 && (outData[2] & (byte) 0x80) == (byte) 0x00) {
            byte[] devicetype = new byte[2];
            System.arraycopy(outData, 4, devicetype, 0, 2);
            WeiPassGlobal.getTransactionInfo().setDevicetype(new String(devicetype));
        }*/
        result = getEMVTransInfo();
        if (result != ErrorMsgType.SUCCESS) {
            return result;
        }
       /* if (WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_Void
                || WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_CompleteVoid) {
            if (!WeiPassGlobal.getTransactionInfo().getCardNo().equals(oldCardNo)) {
//                handler.obtainMessage(MyContext.MSG_ERROR, "Undo card number is inconsistent").sendToTarget();
                return -1;
            }
        }*/
        Log.i("stw", "应用选择3333333333333=====" + (System.currentTimeMillis() - time));
        return result;
    }

    public static String oldCardNo;

    private int checkCardSupportEC() throws RemoteException {
        byte[] outData = new byte[100];
        int[] outDataLen = new int[1];
        int result = emvCore.getTLV(0x9F74, outData, outDataLen);
        if (result == ErrorMsgType.SUCCESS && outDataLen[0] > 0) {
            WeiPassGlobal.getTransactionInfo().setIsECTrans(1);
        } else {

            if (WeiPassGlobal.getTransactionInfo().getAid().equals("A000000333010106")) {
                WeiPassGlobal.getTransactionInfo().setIsECTrans(1);
            } else {
                WeiPassGlobal.getTransactionInfo().setIsECTrans(0);
                return ErrorMsgType.ERR_CANCEL;
            }

        }
        return ErrorMsgType.SUCCESS;
    }

    /**
     * @throws RemoteException
     */
    private static void getEMVTransResult() throws RemoteException {
        byte[] outByteData = new byte[2];
        int[] outDataLen = new int[1];
        emvCore.getTLV(0x8A, outByteData, outDataLen);
        String _0x8A = ByteUtil.fromBytes(outByteData);
        if (outByteData[0] > 0) {
            if (_0x8A.equals(TradeInfo.ARC_OFFLINEAPPROVED)) {
                WeiPassGlobal.getTransactionInfo().setICResult(TradeInfo.OFFLINEAPPROVED);
            } else if (_0x8A.equals(TradeInfo.ARC_OFFLINEDECLINED)) {
                WeiPassGlobal.getTransactionInfo().setICResult(TradeInfo.OFFLINEDECLINED);
            } else if (_0x8A.equals(TradeInfo.ARC_ONLINEFAILOFFLINEAPPROVED)) {
                WeiPassGlobal.getTransactionInfo().setICResult(TradeInfo.UNABLEONLINE_OFFLINEAPPROVED);
            } else if (_0x8A.equals(TradeInfo.ARC_ONLINEFAILOFFLINEDECLINED)) {
                WeiPassGlobal.getTransactionInfo().setICResult(TradeInfo.UNABLEONINE_OFFLINEDECLINED);
            } else if (_0x8A.equals("00")) {
                WeiPassGlobal.getTransactionInfo().setICResult(TradeInfo.ONLINEAPPROVED);
            } else if (_0x8A.equals("01")) {
            } else {
                WeiPassGlobal.getTransactionInfo().setICResult(TradeInfo.ONLINE_FAILED);
            }

        }

        emvCore.getTLV(0x95, WeiPassGlobal.getTransactionInfo().getTVR(), outDataLen);
        // tsi
        emvCore.getTLV(0x9B, WeiPassGlobal.getTransactionInfo().getTSI(), outDataLen);
        return;
    }

    private static int saveEMVTransInfo() throws RemoteException {
        int EmvAppResult = TradeInfo.SUCCESS;
        if (WeiPassGlobal.getTransactionInfo().getICResult() == TradeInfo.OFFLINEDECLINED || WeiPassGlobal.getTransactionInfo().getICResult() == TradeInfo.UNABLEONLINE_OFFLINEAPPROVED) {
            if (WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_QueryBalance) {

            } else {
                WeiPassGlobal.getTransactionInfo().setTransType(TradeInfo.Type_OffLine_Sale);
                getAIDAndCardType();
            }
        }
        byte[] outData = new byte[512];
        int[] outDataLen = new int[1];

        emvCore.getCoreTLVMessage(outData, outDataLen);
        outData = Arrays.copyOf(outData, outDataLen[0]);

        String icData = HEXUitl.bytesToHex(outData);
        if (WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_Sale ||
                WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_Auth ||
                WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_QueryBalance
                ) {

            emvCore.getTLV(0x9F63, outData, outDataLen);

        }

        if (!icData.contains(HEXUitl.bytesToHex(Arrays.copyOf(outData, outDataLen[0])))) {
            icData += HEXUitl.bytesToHex(Arrays.copyOf(outData, outDataLen[0]));
        }
        Log.i(TAG, "saveEMVTransInfo: " + icData);
        icData += "9F4104" + WeiPassGlobal.getTransactionInfo().getTransaceNo();
//		WeiPassGlobal.getTransactionInfo().setIcData(icData);
        switch (WeiPassGlobal.getTransactionInfo().getICResult()) {
            case TradeInfo.ONLINEAPPROVED:
            case TradeInfo.UNABLEONLINE_OFFLINEAPPROVED:
                EmvAppResult = TradeInfo.SUCCESS;
                break;
            case TradeInfo.OFFLINEAPPROVED:
                byte[] field55Data = new byte[10];
                setField55Dara(field55Data, field55Data.length);
                EmvAppResult = TradeInfo.SUCCESS;
                break;
            default:
                EmvAppResult = TradeInfo.CANCEL;
                break;
        }
        outData = new byte[100];
        outDataLen = new int[1];
//		emvCore.getTLV(0x9F5D,outData,outDataLen);
//		WeiPassGlobal.getTransactionInfo().setEC_Balance(ByteUtil.bytes2HexString(outData));
        int ret = emvCore.getTLV(0x9F26, WeiPassGlobal.getTransactionInfo().getAppCrypt(), outDataLen);
        Log.i(TAG, ret + "saveEMVTransInfo: Application ciphertext 9F26 getAppCrypt" + HEXUitl.bytesToHex(WeiPassGlobal.getTransactionInfo().getAppCrypt()) +
                "\n outdata" + HEXUitl.bytesToHex(Arrays.copyOf(outData, outDataLen[0])));
        ret = emvCore.getTLV(0x4F, WeiPassGlobal.getTransactionInfo().getAid(), outDataLen);
        WeiPassGlobal.getTransactionInfo().setAidLen(outDataLen[0]);
        Log.i(TAG, ret + "saveEMVTransInfo:  0x4F getAid" + HEXUitl.bytesToHex(WeiPassGlobal.getTransactionInfo().getAid()) +
                "\n outdata" + HEXUitl.bytesToHex(Arrays.copyOf(outData, outDataLen[0])));
        //  TVR 0x95
        ret = emvCore.getTLV(0x95, WeiPassGlobal.getTransactionInfo().getTVR(), outDataLen);
        Log.i(TAG, ret + "saveEMVTransInfo:  0x95 getTVR"
                + HEXUitl.bytesToHex(WeiPassGlobal.getTransactionInfo().getTVR()) +
                "\n outdata" + HEXUitl.bytesToHex(Arrays.copyOf(outData, outDataLen[0])));
        // TSI 0x9B
        emvCore.getTLV(0x9B, WeiPassGlobal.getTransactionInfo().getTSI(), outDataLen);
        Log.i(TAG, ret + "saveEMVTransInfo: TSI 0x9B getTSI"
                + HEXUitl.bytesToHex(WeiPassGlobal.getTransactionInfo().getTSI()) +
                "\n outdata" + HEXUitl.bytesToHex(Arrays.copyOf(outData, outDataLen[0])));
        //  9F36
        ret = emvCore.getTLV(0x9F36, WeiPassGlobal.getTransactionInfo().getATC(), outDataLen);
        Log.i(TAG, ret + "saveEMVTransInfo:  0x9F36 getATC"
                + HEXUitl.bytesToHex(WeiPassGlobal.getTransactionInfo().getATC()) +
                "\n outdata" + HEXUitl.bytesToHex(Arrays.copyOf(outData, outDataLen[0])));
        //ATC 9F36
        ret = emvCore.getTLV(0x9F34, WeiPassGlobal.getTransactionInfo().getCVM(), outDataLen);
        Log.i(TAG, ret + "saveEMVTransInfo:  9F34 getCVM"
                + HEXUitl.bytesToHex(WeiPassGlobal.getTransactionInfo().getCVM()) +
                "\n outdata" + HEXUitl.bytesToHex(Arrays.copyOf(outData, outDataLen[0])));
        //  ATC 50
        ret = emvCore.getTLV(0x50, WeiPassGlobal.getTransactionInfo().getAucAppLabel(), outDataLen);
        Log.i(TAG, ret + "saveEMVTransInfo:  0x50 getAucAppLabel"
                + HEXUitl.bytesToHex(WeiPassGlobal.getTransactionInfo().getAucAppLabel()) +
                "\n outdata" + HEXUitl.bytesToHex(Arrays.copyOf(outData, outDataLen[0])));
        // 9F12
        ret = emvCore.getTLV(0x9F12, WeiPassGlobal.getTransactionInfo().getAucAppPreferName(), outDataLen);
//		WeiPassGlobal.getTransactionInfo().setAucAppLabel(Arrays.copyOf(WeiPassGlobal.getTransactionInfo().getAucAppLabel(),outDataLen[0]));
        Log.i(TAG, ret + "saveEMVTransInfo:  0x9F12 getAucAppPreferName"
                + HEXUitl.bytesToHex(WeiPassGlobal.getTransactionInfo().getAucAppPreferName()) +
                "\n outdata" + new String(WeiPassGlobal.getTransactionInfo().getAucAppLabel()));
        if (WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_Sale &&
                (!WeiPassGlobal.getTransactionInfo().isOnLine() ||
                        WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_OffLine_Sale)) {
            emvCore.getTLV(0x9F37, WeiPassGlobal.getTransactionInfo().getAucUnPredNum(), outDataLen);
            //  82
            emvCore.getTLV(0x82, WeiPassGlobal.getTransactionInfo().getAucAIP(), outDataLen);

            emvCore.getTLV(0x9F10, WeiPassGlobal.getTransactionInfo().getAucCVR(), outDataLen);
        }
        //  89
        AnalyseTVRTSI();

        return 0;
    }


    private static void AnalyseTVRTSI() throws RemoteException {
        byte[] outData = new byte[10];
        int[] outDateLen = new int[1];
        char ucMask = 0x80;
        int result = emvCore.getTLV(0x9B, outData, outDateLen);
        if (result == TradeInfo.SUCCESS) {
            for (int i = 8; i >= 3; i--) {
                if ((outData[0] & ucMask) > 0) {
                    switch (i) {
                        case 8:
                            Log.v("AnalyseTVRTSI", "Offline data authentication has been carried out");
                            break;

                        case 7:
                            Log.v("AnalyseTVRTSI", "Cardholder certification has been carried out");
                            break;

                        case 6:
                            Log.v("AnalyseTVRTSI", "Card risk management has been carried out");
                            break;

                        case 5:
                            Log.v("AnalyseTVRTSI", "Issuing card certification has been carried out");
                            break;

                        case 4:
                            Log.v("AnalyseTVRTSI", "Terminal risk management has been carried out");
                            break;

                        case 3:
                            Log.v("AnalyseTVRTSI", "Script processing has been performed");
                            break;
                        default:
                            break;
                    }

                    Log.v("AnalyseTVRTSI", "\n");
                }

                ucMask >>= 1;


            }
        }
        outData = new byte[10];
        outDateLen = new int[1];

        result = emvCore.getTLV(0x95, outData, outDateLen);
        Log.v("AnalyseTVRTSI 95 result", result + "---0x95-" + ByteUtil.bytes2HexString(outData) + "--" + ByteUtil.fromUtf8(outData) + "\n" + Arrays.toString(outData));
        ucMask = 0x80;
        if (result == TradeInfo.SUCCESS) {
            for (int i = 8; i >= 3; i--) {
                if ((outData[0] & ucMask) > 0) {
                    switch (i) {
                        case 8:
                            Log.v("AnalyseTVRTSI", "Offline data authentication not performed");
                            break;

                        case 7:
                            Log.v("AnalyseTVRTSI", "Offline static data authentication failed");
                            break;

                        case 6:
                            Log.v("AnalyseTVRTSI", "IC Card data missing");
                            break;

                        case 5:
                            Log.v("AnalyseTVRTSI", "The card appears in the terminal exception file");
                            break;

                        case 4:
                            Log.v("AnalyseTVRTSI", "Offline dynamic data authentication failed");
                            break;

                        case 3:
                            Log.v("AnalyseTVRTSI", "Composite dynamic data authentication/Application password generation failed");
                            break;

                        default:
                            break;
                    }
                    Log.v("AnalyseTVRTSI", "\n");
                }

                ucMask >>= 1;
            }
        }
        ucMask = 0x80;
        if (result == TradeInfo.SUCCESS) {
            for (int i = 8; i >= 4; i--) {
                if ((outData[1] & ucMask) > 0) {
                    switch (i) {
                        case 8:
                            Log.v("AnalyseTVRTSI", "IC Card and terminal application versions are inconsistent");
                            break;

                        case 7:
                            Log.v("AnalyseTVRTSI", "App has expired");
                            break;

                        case 6:
                            Log.v("AnalyseTVRTSI", "App has not yet taken effect");
                            break;

                        case 5:
                            Log.v("AnalyseTVRTSI", "Card products do not allow the requested service");
                            break;

                        case 4:
                            Log.v("AnalyseTVRTSI", "New card");
                            break;

                        default:
                            break;

                    }
                    Log.v("AnalyseTVRTSI", "\n");
                }

                ucMask >>= 1;
            }
        }

        //
        Log.v("AnalyseTVRTSI 95 result", result + "");
        ucMask = 0x80;
        if (result == TradeInfo.SUCCESS) {
            for (int i = 8; i >= 3; i--) {
                if ((outData[2] & ucMask) > 0) {
                    switch (i) {
                        case 8:
                            Log.v("AnalyseTVRTSI", "Cardholder verification was unsuccessful");
                            break;

                        case 7:
                            Log.v("AnalyseTVRTSI", "Unknown CVM");
                            break;

                        case 6:
                            Log.v("AnalyseTVRTSI", "PIN Retry count exceeded");
                            break;

                        case 5:
                            Log.v("AnalyseTVRTSI", "Request PIN, but no PIN pad or PIN pad failure");
                            break;

                        case 4:
                            Log.v("AnalyseTVRTSI", "Request PIN, PIN pad, but no PIN");
                            break;

                        case 3:
                            Log.v("AnalyseTVRTSI", "Enter online PIN");
                            break;

                        default:
                            break;

                    }
                    Log.v("AnalyseTVRTSI", "\n");
                }

                ucMask >>= 1;
            }
        }
        //
        ucMask = 0x80;
        if (result == TradeInfo.SUCCESS) {
            for (int i = 8; i >= 3; i--) {
                if ((outData[3] & ucMask) > 0) {
                    switch (i) {
                        case 8:
                            Log.v("AnalyseTVRTSI", "Transaction exceeds minimum");
                            break;

                        case 7:
                            Log.v("AnalyseTVRTSI", "Exceeding the limit of continuous offline transactions");
                            break;

                        case 6:
                            Log.v("AnalyseTVRTSI", "Exceeded the continuous offline transaction limit");
                            break;

                        case 5:
                            Log.v("AnalyseTVRTSI", "Transactions are randomly selected for online processing");
                            break;

                        case 4:
                            Log.v("AnalyseTVRTSI", "Merchants request online transactions");
                            break;

                        default:
                            break;

                    }
                    Log.v("AnalyseTVRTSI", "\n");
                }

                ucMask >>= 1;
            }
        }
        ucMask = 0x80;
        if (result == TradeInfo.SUCCESS) {
            for (int i = 8; i >= 5; i--) {
                if ((outData[4] & ucMask) > 0) {
                    switch (i) {
                        case 8:
                            Log.v("AnalyseTVRTSI", "Use default TDOL");
                            break;

                        case 7:
                            Log.v("AnalyseTVRTSI", "Issuer authentication failed");
                            break;

                        case 6:
                            Log.v("AnalyseTVRTSI", "Last generated app password(GENERATE AC)Script processing failed before command");
                            break;

                        case 5:
                            Log.v("AnalyseTVRTSI", "Last generated app password(AC)Script processing failed after command");
                            break;

                        default:
                            break;

                    }
                    Log.v("AnalyseTVRTSI", "\n");
                }
                //右移一位
                ucMask >>= 1;
            }
        }
        return;
    }


    /**
     * 设置55域
     */
    private static void setField55Dara(byte[] field55Data, int length) throws RemoteException {
        byte[] outData = new byte[6];
        int[] outDataLen = new int[1];
        int result, appResult = 0;
        result = emvCore.getTLV(0x9F03, outData, outDataLen);
        if (result != ErrorMsgType.SUCCESS) {
            emvCore.setTLV(0x9F03, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        }

        outData = Arrays.copyOf(outData, outDataLen[0]);
        byte[] crypt = WeiPassGlobal.getTransactionInfo().getAppCrypt();
        if (TextUtils.isEmpty(Arrays.toString(crypt)) && Arrays.toString(crypt).startsWith("000000")) {
            outData = new byte[8];
            outDataLen = new int[1];
            emvCore.getTLV(0x9F26, outData, outDataLen);
            WeiPassGlobal.getTransactionInfo().setAppCrypt(outData);
        }
        outData = new byte[12];
        outDataLen = new int[1];
        result = emvCore.getTLV(0x9F02, outData, outDataLen);
        outData = Arrays.copyOf(outData, outDataLen[0]);
        if (result != ErrorMsgType.SUCCESS) {
            appResult = ErrorMsgType.ERR_END;
        } else {
            //if (ByteUtil.bytes2HexString(outData).startsWith("000000"))
            //appResult = ErrorMsgType.ERR_END;
        }
        if (appResult == ErrorMsgType.ERR_END) {
            String amount = MoneyUtil.toCent(WeiPassGlobal.getTransactionInfo().getAmount());
            Log.e("MoneyUtil.toCent", amount);
            byte[] inData = ByteUtil.ascii2Bcd(amount);
            emvCore.setTLV(0x9F02, inData);

        }
        byte transType = 0x00;
        switch (WeiPassGlobal.getTransactionInfo().getTransType()) {
            case TradeInfo.Type_QueryBalance:
                transType = 0x30;
                break;
            case TradeInfo.Type_Sale:
            case TradeInfo.Type_AuthComplete:
            case TradeInfo.Type_OffLine_Sale:
                transType = 0x00;
                break;
            case TradeInfo.Type_Auth:
                transType = 0x03;
                break;
            case TradeInfo.Type_Refund:
            case TradeInfo.Type_Void:
            case TradeInfo.Type_CompleteVoid:
                transType = 0x20;
                break;
            case TradeInfo.Type_CoilingSale:
                transType = 0x60;
                break;
            default:
                break;
        }
        emvCore.setTLV(0x9C, new byte[]{transType});
        outData = new byte[512];
        outDataLen = new int[1];
        emvCore.getCoreTLVMessage(outData, outDataLen);
        outData = Arrays.copyOfRange(outData, 4, outDataLen[0]);
        String tlvMsg = ByteUtil.fromBytes(outData);
        HashMap<String, String> map = new HashMap<String, String>();
        TLV.anaTag(outData, map);
        if (!map.containsKey("9F41")) {
            map.put("9F41", WeiPassGlobal.getTransactionInfo().getTransaceNo());
        }
        if (!map.containsKey("9F09")) {
            map.put("9F09", "0020");
        }
        emvCore.getTLV(0x4F, outData, outDataLen);
        if (!map.containsKey("84")) {
            map.put("84", HEXUitl.bytesToHex(Arrays.copyOf(outData, outDataLen[0])));
        }
//        if (WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_Sale ||
//                WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_Auth ||
//                WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_QueryBalance
//                ) {
//            emvCore.getTLV(0x9F63, outData, outDataLen);
//        }
//
//        if (!map.containsKey("9F63")) {
//            map.put("9F63", HEXUitl.bytesToHex(Arrays.copyOf(outData, outDataLen[0])));
//        }


        if (WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("07")) {
//            map.put("9F27", "00");
            emvCore.getTLV(0x9F27, outData, outDataLen);
            map.put("9F27", HEXUitl.bytesToHex(Arrays.copyOf(outData, outDataLen[0])));
            if (!map.containsKey("9F03")) {
                map.put("9F03", "000000000000");
            }
        }
//        emvCore.getTLV(0x9F34, outData, outDataLen);
//        map.put("9F34", HEXUitl.bytesToHex(Arrays.copyOf(outData, outDataLen[0])));
//        map.put("9F34", "5E0300");

        final String F55 = "9F26,9F27,9F10,9F37,9F36,95,9A,9C,9F02,5F2A,82,9F1A,9F03,9F33";
//        String ex_title = "9F34,9F35,9F1E,9F63,9F41,9F09,84";
        String ex_title = "9F34,9F35,9F1E,9F41,9F09,84";
        final String onLineTag = "74,8A";
        if (WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_OffLine_Sale &&
                tlvMsg.contains("74") || tlvMsg.contains("8A")) {
            ex_title += onLineTag;
        }
        String f55 = TLV.pack(map, F55 + "," + ex_title);
//        f55 = f55.replace("9F270100", "9F270180");
        WeiPassGlobal.getTransactionInfo().setIcData(f55);
//        System.out.println(map);
        // String ex = TLV.pack(map, ex_title);
    }

    private static int getAIDAndCardType() throws RemoteException {
        TERMAPP[] termappList = new TERMAPP[]{
                new TERMAPP(0, 5, "D156000001"),                //cpu
                new TERMAPP(0, 5, "A000000333"),                //cpu
                new TERMAPP(0, 7, "A0000000031010"),            //visa
                new TERMAPP(0, 7, "A0000000999090"),            //emvCore test
                new TERMAPP(0, 7, "A0000000032010"),            //Visa - Electron
                new TERMAPP(0, 7, "A0000000041010"),            //Mastercard - M/Chip
                new TERMAPP(0, 7, "A0000000043060"),            //Mastercard - Maestro
                new TERMAPP(0, 7, "A0000000046000"),            //Mastercard - Cirrus
                new TERMAPP(0, 7, "A0000000046010"),            //Mastercard - app
                new TERMAPP(0, 7, "A0000000101030"),            //Mastercard - eurocheque
                new TERMAPP(0, 7, "A0000000651010"),            //JCB - J/Smart AID.
        };
        byte[] outData = new byte[100];
        int[] outLen = new int[1];
        int result = emvCore.getTLV(0x4F, outData, outLen);
        WeiPassGlobal.getTransactionInfo().setAid(outData);
        WeiPassGlobal.getTransactionInfo().setAidLen(outLen[0]);
        //判断 数组 第一个第二个 aid
        if (result == 0) //需要定义常量类
        {
            if (termappList[0].AIDdata.equals(WeiPassGlobal.getTransactionInfo().getAid()) ||
                    termappList[1].AIDdata.equals(WeiPassGlobal.getTransactionInfo().getAid())) {
                WeiPassGlobal.getTransactionInfo().setCardType(TradeInfo.TRANS_CARDTYPE_INTERNAL);
                WeiPassGlobal.getTransactionInfo().setCardGroup("CUP");
            } else if (termappList[2].AIDdata.equals(WeiPassGlobal.getTransactionInfo().getAid())) {
                WeiPassGlobal.getTransactionInfo().setCardType(TradeInfo.TRANS_CARDTYPE_VISA);
                WeiPassGlobal.getTransactionInfo().setCardGroup("VIS");
            } else if (termappList[5].AIDdata.equals(WeiPassGlobal.getTransactionInfo().getAid())) {
                WeiPassGlobal.getTransactionInfo().setCardType(TradeInfo.TRANS_CARDTYPE_MASTER);
                WeiPassGlobal.getTransactionInfo().setCardGroup("MAE");
            } else if (termappList[10].AIDdata.equals(WeiPassGlobal.getTransactionInfo().getAid())) {
                WeiPassGlobal.getTransactionInfo().setCardType(TradeInfo.TRANS_CARDTYPE_JCB);
                WeiPassGlobal.getTransactionInfo().setCardGroup("JCB");
            } else if (termappList[9].AIDdata.equals(WeiPassGlobal.getTransactionInfo().getAid())) {
                WeiPassGlobal.getTransactionInfo().setCardType(TradeInfo.TRANS_CARDTYPE_MASTER);
                WeiPassGlobal.getTransactionInfo().setCardGroup("MAE");
            } else {
                WeiPassGlobal.getTransactionInfo().setCardType(TradeInfo.TRANS_CARDTYPE_FOREIGN);
                WeiPassGlobal.getTransactionInfo().setCardGroup("0");
            }
            return 0;
        } else {
            return -1;
        }
    }

    //比较 aid
    boolean isAIDEquals(int flag, String data, String equestData) {
        if (flag == 0) {
            return data.startsWith(equestData);
        } else if (flag == 1) {
            return equestData.startsWith(data);
        } else {
            return false;
        }
    }

    /**
     * emvCore TRACK MESS
     *
     * @return
     */
    private static int getEMVTransInfo() throws RemoteException {
        byte[] outData = new byte[100];
        int[] outDataLen = new int[1];
        emvCore.getTLV(0x5A, outData, outDataLen);
        WeiPassGlobal.getTransactionInfo().setCardNo(ByteUtil.bytes2HexString(Arrays.copyOf(outData, outDataLen[0])));
        if (WeiPassGlobal.getTransactionInfo().getCardNo().endsWith("F")) {
            WeiPassGlobal.getTransactionInfo().setCardNo(WeiPassGlobal.getTransactionInfo().getCardNo().replace("F", ""));
        }
        Log.e("card no", WeiPassGlobal.getTransactionInfo().getCardNo());
        //card serial no
        outData = new byte[100];
        outDataLen = new int[1];
        emvCore.getTLV(0x5F34, outData, outDataLen);
        WeiPassGlobal.getTransactionInfo().setCardSn(String.valueOf(outData[0]));

        Log.e("card sn", WeiPassGlobal.getTransactionInfo().getCardSn());
        path = emvCore.getPath();
        LogUtils.d("emvCore.getPath：" + path);
        if (path == 5) {//PayPass Mag

            int rest = emvCore.getMagTrackData_MC(0x02, outData, outDataLen);
            if (rest == 0) {
                Log.i("PAYPASSTEST", "GetDataRecord: track 2 :" + outDataLen[0]);
                Log.i("PAYPASSTEST", "GetDataRecord: track 2 :" + ByteUtil.fromBytes(outData));
                String tagTLV57 = ByteUtil.fromBytes(Arrays.copyOf(outData, outDataLen[0]));
                Log.d("getEMVTransInfo", "track info: tagTLV57==" + tagTLV57 + "\n" + ByteUtil.bytes2HexString(Arrays.copyOf(outData, outDataLen[0])));
                if (TextUtils.isEmpty(WeiPassGlobal.getTransactionInfo().getCardNo())) {
                    WeiPassGlobal.getTransactionInfo().setCardNo(tagTLV57.split("D")[0]);
                }
                int track2Len = tagTLV57.indexOf("F");
                if (track2Len >= 0) {
                    WeiPassGlobal.getTransactionInfo().setTrack2(tagTLV57.substring(0, track2Len));
                } else {
                    WeiPassGlobal.getTransactionInfo().setTrack2(tagTLV57);
                }
            } else {
                Log.e("PAYPASSTEST", "getMagTrackData_MC error result = " + rest);
            }
            rest = emvCore.getMagTrackData_MC(0x01, outData, outDataLen);
            if (rest == 0) {
                Log.i("PAYPASSTEST", "GetDataRecord: track 1 :" + outDataLen[0]);
                Log.i("PAYPASSTEST", "GetDataRecord: track 1 :" + ByteUtil.fromBytes(outData));
                String tagTLV57 = ByteUtil.fromBytes(Arrays.copyOf(outData, outDataLen[0]));
                int track2Len = tagTLV57.indexOf("F");
                if (track2Len >= 0) {
                    WeiPassGlobal.getTransactionInfo().setTrack1(tagTLV57.substring(0, track2Len));
                } else {
                    WeiPassGlobal.getTransactionInfo().setTrack1(tagTLV57);
                }
            } else {
                Log.e("PAYPASSTEST", "getMagTrackData_MC error result = " + rest);
            }
        } else {

            //磁道信息
            outData = new byte[100];
            outDataLen = new int[1];
            int result = emvCore.getTLV(0x57, outData, outDataLen);
            String tagTLV57 = ByteUtil.bytes2HexString(Arrays.copyOf(outData, outDataLen[0]));
            Log.e("磁道信息 tagTLV57", tagTLV57 + "\n" + ByteUtil.bytes2HexString(Arrays.copyOf(outData, outDataLen[0])));
            if (TextUtils.isEmpty(WeiPassGlobal.getTransactionInfo().getCardNo())) {
                WeiPassGlobal.getTransactionInfo().setCardNo(tagTLV57.split("D")[0]);
            }
            Log.i(TAG, "getEMVTransInfo: cardNo" + WeiPassGlobal.getTransactionInfo().getCardNo());
            int track2Len = tagTLV57.indexOf("F");
            if (track2Len >= 0) {
                String track2 = tagTLV57.substring(0, track2Len);
                WeiPassGlobal.getTransactionInfo().setTrack2(track2);
            } else {
                WeiPassGlobal.getTransactionInfo().setTrack2(tagTLV57);
            }

        }
        //card expire data
        outData = new byte[100];
        outDataLen = new int[1];
        emvCore.getTLV(0x5F24, outData, outDataLen);
        if (outDataLen[0] != 0) {
            String expireDate = HEXUitl.bytesToHex(Arrays.copyOf(outData, outDataLen[0])).substring(0, 4);
            WeiPassGlobal.getTransactionInfo().setExpireDate(expireDate);
            Log.e("expire date", WeiPassGlobal.getTransactionInfo().getExpireDate());
        }
        return 0;
    }

    /**
     * EMV参数设置
     *
     * @return
     * @throws Exception
     */
    private static int setEMVConfig() throws Exception {
        byte[] aid = new byte[17];
        int[] aidLen = new int[1];
        int result = emvCore.getTLV(0x4F, aid, aidLen);
        Log.v("emvCore setEmvConfig", "aid-->" + ByteUtil.bytes2HexString(aid) + "\naidLen" + aidLen[0] + "result-->" + result);
        if (result != 0) {
            return -1;
        }
        WeiPassGlobal.getTransactionInfo().setAid(Arrays.copyOfRange(aid, 0, aidLen[0]));
        WeiPassGlobal.getTransactionInfo().setAidLen(aidLen[0]);
       /* byte[] byteParam = new byte[1024];
        int[] intParamLen = new int[]{1024};
        emvCore.getParam(byteParam, intParamLen);
        EmvParam emvParam = new EmvParam(context);
        emvParam.parseByteArray(byteParam);
        emvCore.setParam(emvParam.toByteArray());*/
        return 0;
    }

    public static List<EmvAppList> getAIDList() throws RemoteException {
        if (emvAIDList == null || emvAIDList.size() <= 0) {
            emvAIDList = new ArrayList<EmvAppList>();
            for (int i = 0; i < 20; i++) {
                byte[] outAIDdata = new byte[512];
                int[] outAIDLen = new int[1];
                int result = emvCore.getAID(i, outAIDdata, outAIDLen);
                Log.e("getAid", i + "--->" + result);
                outAIDdata = Arrays.copyOf(outAIDdata, outAIDLen[0]);
                if (result != 0) {
                    continue;
                }
                EmvAppList emvAppList = new EmvAppList(context);
                emvAppList.parseByteArray(outAIDdata);
                emvAIDList.add(emvAppList);
            }
        }
        return emvAIDList;
    }

    /**
     * @param bAid matchAid
     * @return
     */
    private static EmvAppList getAIDCompare(byte[] bAid) throws RemoteException {
        List<EmvAppList> emvAIDList = getAIDList();
        String mAid = ByteUtil.bytes2HexString(bAid);
        for (EmvAppList aid : emvAIDList) {
            Log.e("getAIDCompare", "mAid" + mAid + "AID" + aid.getAID().toUpperCase() + "--aid.getSelFlag" + aid.getSelFlag());
            if (aid.getSelFlag().equals("00") && mAid.toUpperCase().startsWith(aid.getAID().toUpperCase())) {
                Log.e("AIDCompare", mAid);
                return aid;
            } else if (aid.getSelFlag().equals("01") && aid.getAID().toUpperCase().startsWith(mAid.toUpperCase())) {
                Log.e("AIDCompare", mAid);
                return aid;
            }
        }
        return null;
    }

    private static int getCardType() {
        if (WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("05")) {
            return 0x01;
        } else {
            return 0x02;
        }
    }

    // TODO: 2019/4/12  

    /**
     * qpboc  第一执行
     * 交易初始化
     *
     * @return
     */
    private static int QPBOC_PreProcess() throws RemoteException {
        byte[] outData = new byte[1024];
        int[] outStaus = new int[1];
        emvCore.getParam(outData, outStaus);
        EmvParam emvParam = new EmvParam(context);
        emvParam.parseByteArray(outData);
        emvParam.setTransType(WeiPassGlobal.getTransactionInfo().getTransType());
        emvParam.setMerchId(WeiPassGlobal.getTransactionInfo().getMerchantId());
        emvParam.setMerchName("测试emv");
        emvParam.setTermId(WeiPassGlobal.getTransactionInfo().getTermId());
        //if (WeiPassGlobal.getTransactionInfo().getTransType() == TradeInfo.Type_KaQuanQuery) {
        emvParam.setForceOnline(1);
        // }
        emvParam.setTerminalType(0x22);
        emvParam.setMerchName("emv");
//        emvParam.setTermTransQuali("A7004000");//9f66
        emvParam.setTermTransQuali("27804080");//9f66
        emvParam.setCountryCode("0156");
        emvParam.setTransCurrCode("0156");

        emvParam.setTransType(WeiPassGlobal.getTransactionInfo().getTransType());
        emvParam.parseByteArray(outData);
        emvParam.setSupportPSESel(1);
        emvParam.setTermCapab(0xE060C8);

        emvCore.setParam(emvParam.toByteArray());
        emvCore.transInit();//交易初始化
        int result = emvCore.qPBOCPreProcess(iCallbackListener);//非接预处理  非接 IC 卡有此过程，回调 2819 设置金额
        Log.v("qPBOCPreProcess_Reslut", "---" + result);
        return result;
    }


    public static String get91Value(byte[] ALLdata) {
        //DGI:8000   UDK、MACUDK 、DEKUDK 此处用UDK
        try {
            byte[] bATC = new byte[2];
            byte[] g_AuthCode = new byte[8];
            byte[] GAC = new byte[8];

            System.arraycopy(ALLdata, 0, bATC, 0, 2);
            System.arraycopy(ALLdata, 2, g_AuthCode, 0, 2);
            System.arraycopy(ALLdata, 4, GAC, 0, 8);

            /////// 算法1，直接按照途中流程计算
            //12345678901234567890123456789012
            //4664942FE615FB02E5D57F292AA2B3B6
            //>9E15204313F7318ACB79B90BD986AD29
            Log.d("91 value == ATC ", HEXUitl.bytesToHex(bATC));
            Log.d("91 value == ALLData ", HEXUitl.bytesToHex(ALLdata));
            Log.d("91 value == 2 ", "9E15204313F7318ACB79B90BD986AD29");
            byte[] AllKey = ComputeSessionKey(bATC, "9E15204313F7318ACB79B90BD986AD29");
            Log.d("91 value == 2 ", HEXUitl.bytesToHex(AllKey));
            byte[] KeyA = new byte[8];
            byte[] KeyB = new byte[8];
            System.arraycopy(AllKey, 0, KeyA, 0, 8);
            System.arraycopy(AllKey, 8, KeyB, 0, 8);
            byte[] XorData = Utils.xor(GAC, g_AuthCode);
            byte[] Data01 = DESUitl.autoEncrypt(KeyA, XorData);
            Log.d("91 value == A ", HEXUitl.bytesToHex(KeyA));
            Log.d("91 value == A ", HEXUitl.bytesToHex(Data01));
            byte[] Data02 = DESUitl.autoDecrypt(KeyB, Data01);
            Log.d("91 value == B ", HEXUitl.bytesToHex(KeyB));
            Log.d("91 value == B ", HEXUitl.bytesToHex(Data02));
            byte[] ret = DESUitl.autoEncrypt(KeyA, Data02); //注意此处和PBOC规范中图不一样（图中流程有误）
            Log.d("91 value ==", HEXUitl.bytesToHex(ret));
            return HEXUitl.bytesToHex(ret);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static byte[] ComputeSessionKey(byte[] ATC, String UDEey) throws Exception//计算过程密钥
    {
        byte[] ATCCounter = new byte[16];
        ATCCounter[6] = ATC[0];
        ATCCounter[7] = ATC[1];
        ATCCounter[14] = (byte) (ATC[0] ^ 0xFF);
        ATCCounter[15] = (byte) (ATC[1] ^ 0xFF);
        return DESUitl.autoEncrypt(HEXUitl.hexToBytes(UDEey), ATCCounter);
    }
}
