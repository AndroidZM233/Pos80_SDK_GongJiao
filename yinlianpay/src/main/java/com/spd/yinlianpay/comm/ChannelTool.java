package com.spd.yinlianpay.comm;

import android.content.Context;
import android.os.Environment;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.spd.base.utils.Datautils;
import com.spd.base.utils.LogUtils;
import com.spd.yinlianpay.TransactionInfo;
import com.spd.yinlianpay.WeiPassGlobal;
import com.spd.yinlianpay.card.TLV;
import com.spd.yinlianpay.card.TLVList;
import com.spd.yinlianpay.cardparam.AidData;
import com.spd.yinlianpay.cardparam.CAPKData;
import com.spd.yinlianpay.context.MyContext;
import com.spd.yinlianpay.iso8583.Body;
import com.spd.yinlianpay.iso8583.Msg;
import com.spd.yinlianpay.iso8583.ODAException;
import com.spd.yinlianpay.iso8583.PayException;
import com.spd.yinlianpay.iso8583.RespCode;
import com.spd.yinlianpay.listener.OnCommonListener;
import com.spd.yinlianpay.listener.OnTraditionListener;
import com.spd.yinlianpay.net.DataConversionUtils;
import com.spd.yinlianpay.termConig.TermConfigParam;
import com.spd.yinlianpay.trade.TradeInfo;
import com.spd.yinlianpay.util.BytesUtil;
import com.spd.yinlianpay.util.PrefUtil;
import com.spd.yinlianpay.util.Reversal;
import com.spd.yinlianpay.util.StringUtils;
import com.spd.yinlianpay.util.Utils;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import sdk4.wangpos.libemvbinder.CAPK;
import sdk4.wangpos.libemvbinder.EmvAppList;
import sdk4.wangpos.libemvbinder.PayPassTermConfig;
import ui.wangpos.com.utiltool.ByteUtil;
import ui.wangpos.com.utiltool.HEXUitl;
import wangpos.sdk4.libkeymanagerbinder.Key;


/**
 * Created by guoxiaomeng on 2017/6/23.
 */

public class ChannelTool {

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "CustomAsyncTask #" + mCount.getAndIncrement());
        }
    };
    public static final Executor FIXED_EXECUTOR = Executors.newFixedThreadPool(1, sThreadFactory);


    public static void login(final String userid, final String usepwd, final OnCommonListener onCommonListener) {

        FIXED_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                login(userid, onCommonListener);
            }
        });
    }


    //签退
    public static Msg doLoginOut(final OnCommonListener onCommonListener) {

        FIXED_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    byte[] bytes = new byte[0];
                    if (onCommonListener != null) {
                        onCommonListener.onProgress("Automatic sign-off。。");
                    }
                    bytes = ChanelPacket.loginOut("");
                    byte[] ret = CommunAction.doNet(bytes);
                    Msg msg = ChanelPacket.decode(ret);
                    if (msg.getReqCode().getFlag() == 'A') {
                        if (onCommonListener != null) {
                            onCommonListener.onSuccess();
                        }
                    }
                } catch (PayException e) {
                    if (onCommonListener != null) {
                        onCommonListener.onError(-1, e.getMessage());
                    }
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return null;
    }

    ///sign in
    private static Msg login(String usepwd, OnCommonListener onCommonListener) {
        try {
            String errMsg = "";
            PrefUtil.setDoSignRepeatSend(true);
            //签到
            long time = System.currentTimeMillis();
            byte[] bytes = ChanelPacket.login(usepwd);
            Log.i("stw", "login: 签到报文时间===" + (System.currentTimeMillis() - time));
            byte[] ret = CommunAction.doNet(bytes);
            if (ret == null) {
                // TODO: 2019/4/11   签到失败哦
            }
            Msg msg = ChanelPacket.decode(ret);
            String f63 = msg.body.getField(63);
            if (!TextUtils.isEmpty(f63) && f63.length() >= 41) {
                String ver = f63.substring(39, 41);
                String verNo = f63.substring(7, 39).trim();
                Log.i("stw", "login: " + ver + "--<" + verNo);
            }
            if (msg == null) {
                return null;
            }
            if (msg.getReqCode().getFlag() == 'A') {

                if (!TextUtils.isEmpty(msg.body.getField(60))) {
                    String f60 = msg.body.getField(60);
                    String batchNo = f60.substring(2, 8);
                    PrefUtil.putBatchNo(batchNo);
                }
                //***工作秘钥***
                String keys = HEXUitl.bytesToHex(msg.body.getFieldData(62));
                byte[] pek = HEXUitl.hexToBytes(keys.substring(0, 40));
                byte[] mak = HEXUitl.hexToBytes(keys.substring(40, 80));
                byte[] tdk = null;
                if (keys.length() >= 120) {
                    tdk = HEXUitl.hexToBytes(keys.substring(80, 120));
                }
                //更新工作密钥
                byte[] CertData = new byte[8];
                byte[] keyvaluetek = new byte[16];
                byte[] checkvalue = new byte[4];
                boolean flag = true;
                int retr = -1;
                try {
                    if (PrefUtil.getISSUPPORTSM()) {
                        System.arraycopy(mak, 0, keyvaluetek, 0, 16);
                    } else {
                        System.arraycopy(mak, 0, keyvaluetek, 0, 8);
                        System.arraycopy(mak, 0, keyvaluetek, 8, 8);
                    }
                    System.arraycopy(mak, 16, checkvalue, 0, 4);
                    PrefUtil.setMackey(DataConversionUtils.byteArrayToString(keyvaluetek));
                    Log.i("stw", "密文mak: " + HEXUitl.bytesToHex(keyvaluetek) + "--->" + HEXUitl.bytesToHex(checkvalue));

                    int algorithmType = 0;
                    /*if (PrefUtil.getISSUPPORTSM()) {
                        algorithmType = 0x02;
                    }*/

                    retr = MyContext.mKey.updateKeyWithAlgorithm(Key.KEY_REQUEST_MAK, algorithmType, Key.KEY_PROTECT_TMK, CertData, keyvaluetek, false, 0x00, checkvalue, MyContext.keyPacketName, MyContext.specifyId);

                    if (retr != 0) {
                        errMsg += "MAK check error\n";
                        Log.i("key", "mak ret" + retr);
                        flag = false;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    if (tdk == null && flag == true) {
                        flag = true;
                    } else {
                        System.arraycopy(tdk, 0, keyvaluetek, 0, 16);
                        System.arraycopy(tdk, 16, checkvalue, 0, 4);
                        int algorithmType = 0;
                        /*if (PrefUtil.getISSUPPORTSM()) {
                            algorithmType = 0x02;
                        }*/
                        Log.i("info", "tdk: " + HEXUitl.bytesToHex(keyvaluetek) + "--->" + HEXUitl.bytesToHex(checkvalue));
                        retr = MyContext.mKey.updateKeyWithAlgorithm(Key.KEY_REQUEST_DDEK, algorithmType, Key.KEY_PROTECT_TMK,
                                CertData, keyvaluetek, false, 0x00, checkvalue,
                                MyContext.keyPacketName, MyContext.specifyId);
                        retr = MyContext.mKey.updateKeyWithAlgorithm(Key.KEY_REQUEST_DEK, algorithmType, Key.KEY_PROTECT_TMK,
                                CertData, keyvaluetek, false, 0x00, checkvalue,
                                MyContext.keyPacketName, MyContext.specifyId);
                        if (retr != 0) {
                            errMsg += "TDK check error\n";
                            Log.i("key", "mak dek" + retr);
                            flag = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    System.arraycopy(pek, 0, keyvaluetek, 0, 16);
                    System.arraycopy(pek, 16, checkvalue, 0, 4);
                    Log.i("info", "pek: " + HEXUitl.bytesToHex(keyvaluetek) + "--->" + HEXUitl.bytesToHex(checkvalue));

                    int algorithmType = 0;
                    /*if (PrefUtil.getISSUPPORTSM()) {
                        algorithmType = 0x02;
                    }*/
                    retr = MyContext.mKey.updateKeyWithAlgorithm(Key.KEY_REQUEST_PEK, algorithmType, Key.KEY_PROTECT_TMK, CertData, keyvaluetek, false, 0x00, checkvalue, MyContext.keyPacketName, MyContext.specifyId);
                    if (retr != 0) {
                        errMsg += "PEK check error\n";
                        Log.i("key", "mak pink" + retr);
                        flag = false;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (!flag) {
                    if (onCommonListener != null) {
                        onCommonListener.onError(-1, "update work key error\n" + errMsg);

                    }
                } else {
                    WeiPassGlobal.getTransactionInfo().setBacthNo(msg.body.get(60).substring(2, 8));

                    int poscount = PrefUtil.getSerialNo();
                    poscount++;
                    PrefUtil.putSerialNo(poscount);

                    PrefUtil.setDoSignRepeatSend(false);
                    if (onCommonListener != null) {
                        onCommonListener.onSuccess();
                    }

                }

            } else {
                if (onCommonListener != null) {
                    onCommonListener.onError(-1, msg.getReqCode().getErrorMsg());
                }
            }
            return msg;
        } catch (Exception ex) {
            if (onCommonListener != null) {
                onCommonListener.onError(-1, ex.getMessage());
            }
            return null;
        }
    }


    public static boolean IsNeedLogin(OnCommonListener onCommonListener) {
        try {
            if (PrefUtil.getDoSignRepeatSend()) {
                Msg msg = login("01", null);
                if (msg.getReqCode().getFlag() == 'A') {
                    PrefUtil.setDoSignRepeatSend(false);
                } else {
                    return false;
                }
            }
            Reversal reversal = PrefUtil.getReversal();
            if (reversal == null) {
                return true;
            }
            final Body msgg = ChanelPacket.decodeRev(reversal.up);
            int times = PrefUtil.getInt(PrefUtil.KEY_REVERSAL_NUM, 3);
            boolean flag = false;
            for (int i = 0; i < times; i++) {
                try {
                    if (onCommonListener != null) {
//                    onCommonListener.onProgress("Is the " + i + " time to be positive");
                    }
                    Log.d("Reverse", msgg.toString());
                    byte[] reversebyte = ChanelPacket.getReverseUp(reversal.type, msgg);
                    Log.d("Reverse byte[]", HEXUitl.bytesToHex(reversebyte));
                    if (onCommonListener != null) {
                        onCommonListener.onProgress("Correct the previous transaction( " + (times - i) + "/" + times + ") ");
                    }
                    byte[] ret = CommunAction.doNet(reversebyte);
                    Msg msgret = ChanelPacket.decode(ret, true);
                    Log.d("Reverse decode", msgg.toString());
                    if (msgret.getReqCode().toString().startsWith("00") || msgret.getReqCode().toString().startsWith("12") || msgret.getReqCode().toString().startsWith("25")) {
                        PrefUtil.putReversal(null);
//                        try {
//
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
                        flag = true;
//                    if (onCommonListener != null) {
//                        onCommonListener.onSuccess();
//                    }
                        break;
                    }
                } catch (TimeoutException e) {
                    continue;
                } catch (EOFException e) {
                    continue;
                }
            }
            if (!flag) {
                if (onCommonListener != null) {
                    onCommonListener.onProgress("Failure, trading...");
                }
//                return false;
            } else {
                if (onCommonListener != null) {
                    onCommonListener.onProgress("Rush to success, trading...");
                }
//                return true;
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 发送消费0200
     *
     * @param money
     * @param onCommonListener
     */
    public static void doSale(final String money, final OnTraditionListener onCommonListener) {

        FIXED_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = null;
                byte[] Reversal = null;
                try {
                    long times = System.currentTimeMillis();
                    TradeInfo tradeInfo = new TradeInfo();
                    bytes = ChanelPacket.makeSale(money);
                    if (onCommonListener != null) {
                        onCommonListener.onProgress("Message reception。");
                    }
                    Log.i("stw", "报文 组0200=====" + (System.currentTimeMillis() - times));
                    times = System.currentTimeMillis();
                    onCommonListener.onDataBack(null);

                    LogUtils.v("状态返回-正在处理中" + Datautils.byteArrayToString(bytes));
                    byte[] ret = CommunAction.doNet(bytes);
                    int poscount = PrefUtil.getSerialNo();

                    Msg msg = ChanelPacket.decode(ret);
                    Msg msgSend = ChanelPacket.decode(bytes);
                    boolean insertCard = Utils.cardType == Utils.CardTypeIC_ICC && WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("05");
                    RespCode rc = msg.getReqCode();

                    tradeInfo.msg = msg;
                    tradeInfo.errorCode = rc.getCode();
                    tradeInfo.errorMsg = rc.getErrorMsg();
                    poscount++;
                    PrefUtil.putSerialNo(poscount);
                    if (msg.getReqCode().getFlag() == 'A') {
                        Log.i("stw", "發送报文0200报文反回=====" + (System.currentTimeMillis() - times));
                        castResult(tradeInfo);
                        onCommonListener.onResult(tradeInfo);
                        //借用head把消费请求结果传出去
                        msgSend.head = msg.getReqCode().toString();
                        msgSend.body.ds[38] = msg.body.ds[38];
                        onCommonListener.onDataBack(msgSend);
                        onCommonListener.onSuccess();

//                        onCommonListener.onProgress((System.currentTimeMillis() - times) + "ms");
                    } else {

                        Log.i("stw", "發送报文0200报文反回=====" + (System.currentTimeMillis() - times));
                        onCommonListener.onError(0, msg.getField39Code());
                    }
                } catch (ODAException e) {
                    try {
                        byte[] cutBytes = Datautils.cutBytes(bytes, 0, 2);
                        Msg msg;
                        if (Arrays.equals(cutBytes, new byte[]{(byte) 0x60, (byte) 0x00})) {
                            msg = ChanelPacket.decode(bytes);
                        } else {
                            msg = ChanelPacket.decode(Datautils.cutBytes(bytes, 2
                                    , bytes.length - 2));
                        }

                        // TODO: 2019/4/22 ODA
                        String type = msg.body.type;
                        if (!"0800".equals(type)) {
                            TradeInfo tradeInfo = new TradeInfo();
                            tradeInfo.msg = msg;
                            tradeInfo.errorMsg = "ODA";
                            onCommonListener.onResult(tradeInfo);
                        }

                    } catch (PayException e1) {
                        e1.printStackTrace();
                    }

                    e.printStackTrace();
                } catch (PayException ex) {
                    ex.printStackTrace();
                    if (onCommonListener != null) {
                        onCommonListener.onError(0, ex.getMessage());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (!CommunAction.isSendSuccess) {
                        PrefUtil.putReversal(null);
                    }
                    //Abnormal transaction, please contact the administrator.
                    onCommonListener.onError(0, "Abnormal transaction, please contact the administrator.");
                }
            }
        });
    }


    public static void doDownMasterKey(final OnTraditionListener onCommonListener) {
        FIXED_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (onCommonListener != null) {
                        onCommonListener.onProgress("Query public key");
                    }
                    byte[] bytes = ChanelPacket.queryPKey("400");
                    byte[] ret = CommunAction.doNet(bytes);
                    Msg msg = ChanelPacket.decode(ret);
                    if (msg.getReqCode().getFlag() == 'A') {
                        if (onCommonListener != null) {
                            onCommonListener.onProgress("Download the public key");
                        }

                        HashMap<String, String> map = new HashMap<String, String>();
                        TLV.anaTag(HEXUitl.hexToBytes(msg.body.get(62).toString().substring(2)), map);
                        PrefUtil.setPublicKeyInfo(msg.body.get(62).toString().substring(2));

                        UUID uuid = UUID.randomUUID();
                        String tlk = uuid.toString().replace("-", "").toUpperCase();
                        //tlk="EF2AE9F834BFCDD5260B974A70AD1A4A";
                        tlk = HEXUitl.bytesToHex(Utils.parityOfOdd(HEXUitl.hexToBytes(tlk), 0));
                        PrefUtil.setTLK(tlk);
                        byte[] key = HEXUitl.hexToBytes(tlk);
                        byte[] databyte = new byte[2 + key.length + 12];
                        databyte[0] = 0x30;
                        databyte[1] = (byte) (28);
                        databyte[2] = 0x04;
                        databyte[3] = (byte) key.length;
                        System.arraycopy(key, 0, databyte, 4, key.length);
                        databyte[4 + key.length] = 0x04;
                        databyte[5 + key.length] = 0x08;
                        tlk = uuid.toString().replace("-", "").toUpperCase();
                        byte[] randombyte = HEXUitl.hexToBytes(tlk.substring(0, 16).replace("0", "1"));
                        System.arraycopy(randombyte, 0, databyte, 6 + key.length, 8);
                        byte[] retdata = new byte[128];
                        retdata[0] = 0x00;
                        retdata[1] = 0x02;
                        byte[] tmp = new byte[125 - databyte.length];
                        String str = "";
                        int count = tmp.length * 2 % 8;
                        if (count == 0) {
                            count = tmp.length * 2 / 8;
                        } else {
                            count = tmp.length * 2 / 8 + 1;
                        }
                        for (int i = 0; i < count; i++) {
                            uuid = UUID.randomUUID();
                            str += uuid.toString().replace("-", "").toUpperCase();
                        }
                        tmp = HEXUitl.hexToBytes(str.substring(0, tmp.length * 2).replace("0", "A"));
                        System.arraycopy(tmp, 0, retdata, 2, tmp.length);
                        System.arraycopy(databyte, 0, retdata, 3 + tmp.length, databyte.length);
                        System.out.print("=========" + HEXUitl.bytesToHex(retdata));
                        byte[] rsabyte = RSAUtils.encryptData(retdata, new BigInteger(map.get("DF02").toString(), 16), new BigInteger(map.get("DF04").toString(), 16));
                        String termKeyInfo = "DF99" + Integer.toHexString(rsabyte.length + 1) + Integer.toHexString(rsabyte.length) + HEXUitl.bytesToHex(rsabyte);
                        termKeyInfo += "9F06";
                        termKeyInfo += map.get("9F06").length() > 32 ? Integer.toHexString(map.get("9F06").length() / 2) : "0" + Integer.toHexString(map.get("9F06").length() / 2);
                        termKeyInfo += map.get("9F06");
                        termKeyInfo += "9F22";
                        termKeyInfo += map.get("9F22").length() > 32 ? Integer.toHexString(map.get("9F22").length() / 2) : "0" + Integer.toHexString(map.get("9F22").length() / 2);
                        termKeyInfo += map.get("9F22");
                        bytes = ChanelPacket.updateMasterKey(termKeyInfo);
                        ret = CommunAction.doNet(bytes);
                        msg = ChanelPacket.decode(ret);
                        if (msg.getReqCode().getFlag() == 'A') {

                            HashMap<String, String> mastermap = new HashMap<String, String>();
                            TLV.anaTag(HEXUitl.hexToBytes(msg.body.get(62).toString().substring(2)), mastermap);

                            try {
                                byte[] tlkkey = HEXUitl.hexToBytes(PrefUtil.getTLK());
                                byte[] CertData = new byte[8];
                                byte[] checkval = new byte[255];
                                int tlkret = MyContext.mKey.updateKeyEx(Key.KEY_REQUEST_TLK,
                                        Key.KEY_PROTECT_ZERO,
                                        CertData,
                                        tlkkey,
                                        false,
                                        0x00, checkval, MyContext.keyPacketName, MyContext.specifyId);
                                if (tlkret == 0) {
                                    byte[] masterkey = new byte[16];
                                    System.arraycopy(HEXUitl.hexToBytes(mastermap.get("DF02").toString()), 0, masterkey, 0, masterkey.length);
                                    int masterkeyret = MyContext.mKey.updateKeyEx(Key.KEY_REQUEST_TMK,
                                            Key.KEY_PROTECT_TLK,
                                            CertData,
                                            masterkey,
                                            false,
                                            0x00, checkval, MyContext.keyPacketName, MyContext.specifyId);
                                    if (masterkeyret == 0) {
                                        if (onCommonListener != null) {
                                            onCommonListener.onSuccess();
                                        }
                                    } else {
                                        Log.d("master ", "master key error!!!");
                                        if (onCommonListener != null) {
                                            onCommonListener.onError(-1, "master key error");
                                        }
                                    }
                                } else {
                                    Log.d("master ", "tlk error!!!");
                                    if (onCommonListener != null) {
                                        onCommonListener.onError(-1, "tlk error");
                                    }
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (PayException ex) {
                    if (onCommonListener != null) {
                        onCommonListener.onError(0, ex.getMessage());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (onCommonListener != null) {
                        onCommonListener.onError(-1, "Exception :" + ex.getMessage());
                    }
                }

            }
        });
    }

    /**
     * 下载CAPK AID 到内核 正常无更新只需要下载 一次
     *
     * @param type
     * @param onTraditionListener
     */
    public static void doDownParamter(final int type, final OnTraditionListener onTraditionListener) {
        FIXED_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                /*if (!IsNeedLogin(null)) {
                    if(onTraditionListener!=null)
                    onTraditionListener.onError(0, "Abnormal consumption");
                    return;
                }*/
                //查询
                if (onTraditionListener != null) {
                    onTraditionListener.onProgress("Parameter query");
                }
                try {
                    if (type == 2) {
                        MyContext.emvCore.delAllAID();
                    }
                    if (type == 1) {
                        MyContext.emvCore.delAllCAPK();
                    }
                    if (type == 2) {
                        //IC paramter
                        ArrayList<String> posarraylist = new ArrayList<String>();
                        doPosUpSend(0, posarraylist, 382);
//                        UnionPayCard.emvAIDList.clear();
                        paramDownload(0, 380, posarraylist, onTraditionListener);
                        boolean fg = ChanelPacket.downFinish(381);
                        PrefUtil.setICPARAMETER(true);
                        Log.i("info", "run: setICPARAMETER");
                        if (onTraditionListener != null) {
                            Log.i("info", "run: onSuccess");
                            onTraditionListener.onSuccess();

                        }

                    } else if (type == 1) {
                        ArrayList<String> posarraylist = new ArrayList<String>();
                        doPosUpSend(0, posarraylist, 372);
                        Log.i("info", "run: paramDownload");
                        paramDownload(0, 370, posarraylist, onTraditionListener);
                        ChanelPacket.downFinish(371);
                        PrefUtil.setICPASSWORD(true);
                        if (onTraditionListener != null) {
                            onTraditionListener.onSuccess();
                        }
                    } else if (type == 3) {
                        ArrayList<String> posarraylist = new ArrayList<String>();
                        doPosUpSend(0, posarraylist, 352);
                        Log.i("info", "run: paramDownload");
                        paramDownload(0, 350, posarraylist, onTraditionListener);
                        boolean fg = ChanelPacket.downFinish(351);
                        PrefUtil.setICSMD(true);
                        if (onTraditionListener != null) {
                            onTraditionListener.onSuccess();
                        }
                    }

                } catch (PayException ex) {
                    if (onTraditionListener != null) {
                        onTraditionListener.onError(0, ex.getMessage());
                    }
                } catch (Exception ex) {

                    if (onTraditionListener != null) {
                        onTraditionListener.onError(-1, "Exception of download parameters！");
                    }
                }
            }
        });
    }

    private static void paramDownload(int n, int type, ArrayList<String> keyList, OnTraditionListener onTraditionListener) throws Exception {
        try {
            if (n >= keyList.size()) {
                return;
            }
            if (onTraditionListener != null) {
                onTraditionListener.onProgress("downloading(" + keyList.size() + "/" + n + ")");
            }
            byte[] databyte = ChanelPacket.downparam(n, type, keyList);
            byte[] ret = CommunAction.doNet(databyte);
            Msg msg = ChanelPacket.decode(ret);
            if (msg.getReqCode().getFlag() == 'A') {
                byte[] f62 = msg.body.getFieldData(62);
                if (f62 != null && f62.length > 0) {
                    if (f62[0] == 0x31) {
                        TLVList tlvList = TLVList.fromBinary(Arrays.copyOfRange(f62, 1, f62.length));
                        if (type == 380) {
                            addEmvAid(tlvList);
                        } else if (type == 370 || type == 350) {
                            addCapk(tlvList);
                        }
                    }
                }
                Log.i("info", "paramDownload: type" + type + "f62" + f62[0] + "--" + 0x31 + "\n" + HEXUitl.bytesToHex(f62));
                paramDownload(n + 1, type, keyList, onTraditionListener);
            } else {
                throw new Exception("down paramter exception!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("down paramter exception!");
        }
    }

    // 添加 aid ic 卡 参数
    public static void addEmvAid(TLVList tlvList) throws Exception {
        Log.i("info", "addEmvAid: ");
        try {
            EmvAppList emvAppList = new EmvAppList(MyContext.context);
            emvAppList.setAID(tlvList.getTLV("9F06").getValue());//aid
            if (tlvList.getTLV("DF01") != null) {
                emvAppList.setSelFlag(tlvList.getTLV("DF01").getValue());
            }
            if (tlvList.getTLV("9F09") != null) {
                emvAppList.setVersion(tlvList.getTLV("9F09").getValue());
            }
            if (tlvList.getTLV("DF11") != null) {
                emvAppList.setTACDefault(tlvList.getTLV("DF11").getValue());//TAC－default
            }
            if (tlvList.getTLV("DF12") != null) {
                emvAppList.setTACOnline(tlvList.getTLV("DF12").getValue());//TAC－online
            }
            if (tlvList.getTLV("DF13") != null) {
                emvAppList.setTACDenial(tlvList.getTLV("DF13").getValue());//TAC－default
            }
            if (tlvList.getTLV("9F1B") != null) {
                emvAppList.setFloorLimit(Long.parseLong(tlvList.getTLV("9F1B").getValue()));
            }
            emvAppList.setFloorLimitCheck(1);
            emvAppList.setCL_bFloorLimitCheck("1");
            emvAppList.setCL_bAmount0Check("1");
            emvAppList.setCL_bAmount0Option("1");
            emvAppList.setCL_bCVMLimitCheck("1");
            emvAppList.setCL_bTransLimitCheck("1");
            if (tlvList.getTLV("DF15") != null) {
                emvAppList.setThreshold(Long.parseLong(tlvList.getTLV("DF15").getValue()));
            }
            if (tlvList.getTLV("DF16") != null) {
                emvAppList.setMaxTargetPer(Integer.parseInt(tlvList.getTLV("DF16").getValue()));
            }
            if (tlvList.getTLV("DF17") != null) {
                emvAppList.setTargetPer(Integer.parseInt(tlvList.getTLV("DF17").getValue()));
            }
            if (tlvList.getTLV("DF14") != null) {
                emvAppList.setDDOL(tlvList.getTLV("DF14").getValue());
            }
            if (tlvList.getTLV("DF18") != null) {
                emvAppList.setBOnlinePin(Integer.parseInt(tlvList.getTLV("DF18").getValue()));
            }
            if (tlvList.getTLV("9F7B") != null) {
                emvAppList.setEC_TermLimit(Long.parseLong(tlvList.getTLV("9F7B").getValue()));
            }
            if (tlvList.getTLV("9F7B") != null) {
                emvAppList.setEC_bTermLimitCheck(1);
            }
            if (tlvList.getTLV("DF19") != null) {
                emvAppList.setCL_FloorLimit(Long.parseLong(tlvList.getTLV("DF19").getValue()));
            }
            if (tlvList.getTLV("9F33") != null) {
                Log.d("9F33", "  " + BytesUtil.byte2Int(HEXUitl.hexToBytes(tlvList.getTLV("9F33").getValue())));
                emvAppList.setTermCapab(BytesUtil.byte2Int(HEXUitl.hexToBytes(tlvList.getTLV("9F33").getValue())));
            }
            if (tlvList.getTLV("DF20") != null) {
                Log.e("PAYPASSTEST", "DF20" + Long.parseLong(tlvList.getTLV("DF20").getValue()));
                emvAppList.setCL_TransLimit(Long.parseLong(tlvList.getTLV("DF20").getValue()));
            }
            if (tlvList.getTLV("DF21") != null) {
                emvAppList.setCL_CVMLimit(Long.parseLong(tlvList.getTLV("DF21").getValue()));
            } else {
                emvAppList.setCL_CVMLimit(1000);
            }
            if (tlvList.getTLV("DF811A") != null) {
                Log.e("PAYPASSTEST", "UDOL = " + tlvList.getTLV("DF811A").getValue());
                emvAppList.setUDOL(StringUtils.string2BCD(tlvList.getTLV("DF811A").getValue()));
            }
            if (tlvList.getTLV("9F6D") != null) {
                emvAppList.setMagAvn(StringUtils.string2BCD(tlvList.getTLV("9F6D").getValue()));
            }
            if (tlvList.getTLV("DF811A") != null) {
                int len = tlvList.getTLV("DF811A").getLength();
                emvAppList.setUDOLLen(new byte[]{(byte) ((len >> 8) & 0xFF), (byte) (len & 0xFF)});
            }
            if (tlvList.getTLV("DF811E") != null) {
                emvAppList.setUcMagStrCVMCapWithCVM(StringUtils.string2BCD(tlvList.getTLV("DF811E").getValue()));
            }
            if (tlvList.getTLV("DF812C") != null) {
                emvAppList.setUcMagStrCVMCapNoCVM(StringUtils.string2BCD(tlvList.getTLV("DF812C").getValue()));
            }
            if (tlvList.getTLV("DF811B") != null) {
                emvAppList.setUcKernelConfig(StringUtils.string2BCD(tlvList.getTLV("DF811B").getValue()));
            }
            if (tlvList.getTLV("DF8118") != null) {
                emvAppList.setUcCVMCap(StringUtils.string2BCD(tlvList.getTLV("DF8118").getValue()));
            }
            if (tlvList.getTLV("DF8119") != null) {
                emvAppList.setUcCVMCapNoCVM(StringUtils.string2BCD(tlvList.getTLV("DF8119").getValue()));
            }
            if (tlvList.getTLV("DF8117") != null) {
                Log.e("PAYPASSTEST", "DF8117 = " + tlvList.getTLV("DF8117").getValue());
                emvAppList.setUcCardDataInputCap(StringUtils.string2BCD(tlvList.getTLV("DF8117").getValue()));
            }
            if (tlvList.getTLV("DF811F") != null) {
                Log.e("PAYPASSTEST", "DF811F = " + tlvList.getTLV("DF811F").getValue());
                emvAppList.setSecurityCap(StringUtils.string2BCD(tlvList.getTLV("DF811F").getValue()));
            }
            if (tlvList.getTLV("DF811F") != null) {
                Log.e("PAYPASSTEST", "DF811F = " + tlvList.getTLV("DF811F").getValue());
                emvAppList.setSecurityCap(StringUtils.string2BCD(tlvList.getTLV("DF811F").getValue()));
            }
            if (tlvList.getTLV("DF8125") != null) {//payvawe非接交易限额
                emvAppList.setRdClssTxnLmtONdevice(Long.parseLong(tlvList.getTLV("DF8125").getValue()));
            }
            if (tlvList.getTLV("DF8124") != null) {//payvawe非接交易限额
                emvAppList.setRdClssTxnLmtNoONdevice(Long.parseLong(tlvList.getTLV("DF8124").getValue()));
            }

            emvAppList.setUcMagSupportFlg(new byte[]{0x01});
            emvAppList.setTermTransQuali("A6004000");

//            byte[] mcdata = emvAppList.toMCByteArray();
            byte[] mcdata = emvAppList.toByteArray();
            Log.e("PAYPASSTEST", "mcdata = " + ByteUtil.bytes2HexString(mcdata));
            Log.e("PAYPASSTEST", "mcdata = " + ByteUtil.bytes2HexString(emvAppList.toByteArray()));
            Log.e("PAYPASSTEST", "mcdata length= " + mcdata.length);

//            MyContext.emvCore.addAID_MC(emvAppList.toByteArray(), mcdata);
            int result = MyContext.emvCore.addAID(mcdata);
            Log.i("stw", "addEmvAid: " + result);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public static void addCapk(TLVList tlvList) throws Exception {
            /*try {
                MyContext. emvCore.delAllCAPK();//delete capk
            }catch (RemoteException ex){
                ex.printStackTrace();
            }*/
        try {
            CAPK capk = new CAPK(MyContext.context);

            capk.setRID(tlvList.getTLV("9F06").getValue());// rid
            capk.setKeyID(tlvList.getTLV("9F22").getValue());//(CA Public Key Index)

            if (tlvList.getTLV("DF05") != null) {
                capk.setExpDate(tlvList.getTLV("DF05").getValue());//(CA Public Key period of validity)
                Log.e("acpkData", "ExpDate--->" + capk.getExpDate() + "\ntag-->" + tlvList.getTLV("DF05").getValue());
            }
            if (tlvList.getTLV("DF06") != null) {
                capk.setHashInd(tlvList.getTLV("DF06").getValue());//(CA Public Key Hash algorithm identification)
                Log.e("acpkData", "HashInd--->" + capk.getHashInd() + "\ntag-->" + tlvList.getTLV("DF06").getValue());
            }
            if (tlvList.getTLV("DF07") != null) {
                capk.setArithInd(tlvList.getTLV("DF07").getValue());//(CA Public Key Algorithm identification)
                Log.e("acpkData", "ArithInd--->" + capk.getArithInd() + "\ntag-->" + tlvList.getTLV("DF07").getValue());
            }
            if (tlvList.getTLV("DF02") != null) {
                capk.setModul(tlvList.getTLV("DF02").getValue());//(CA Public Key module)
                Log.e("acpkData", "tModul--->" + capk.getModul() + "\ntag-->" + tlvList.getTLV("DF02").getValue());
            }
            if (tlvList.getTLV("DF04") != null) {
                capk.setExponent(tlvList.getTLV("DF04").getValue());//(CA Public Key exponent)
                Log.e("acpkData", "Exponent--->" + capk.getExponent() + "\ntag-->" + tlvList.getTLV("DF04").getValue());
            }
            if (tlvList.getTLV("DF03") != null) {
                capk.setCheckSum(tlvList.getTLV("DF03").getValue().substring(0, 40));//(CA Public Key Check value)
                Log.e("acpkData", tlvList.getTLV("DF03").getLength() + "----" + tlvList.getTLV("DF03").getTLLength() + "CheckSum--->" + capk.getCheckSum() + "\ntag-->" + tlvList.getTLV("DF03").getValue() + "\ndataSize" + capk.toByteArray().length);
            }
            Log.e("addCapk", tlvList.toString() + "\n" + "capkSize-->" + capk.toByteArray().length + "\n" + capk.print());
            byte[] capkByte = capk.toByteArray();
            int result = MyContext.emvCore.addCAPK(capkByte);
            Log.e("addCapk_Result", result + "");
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }

    }

    public static String PARAMETER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

    public static void setCAPKNew(Context context) {
        String capkDataList = null;
        try {
            File file = new File(PARAMETER_PATH + "capk.development.json");
            FileInputStream fis = new FileInputStream(file);
            byte[] ret = new byte[fis.available()];
            fis.read(ret);
            capkDataList = new String(ret, "utf-8");
            Gson gson = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(capkDataList).getAsJsonObject();

            JsonArray jsonArray = jsonObject.getAsJsonArray("capkData");
            Type CapkList = new TypeToken<ArrayList<CAPKData>>() {
            }.getType();
            ArrayList<CAPKData> capkList = gson.fromJson(jsonArray, CapkList);
            try {
                MyContext.emvCore.delAllCAPK();//delete capk
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            for (CAPKData capkData : capkList) {
                try {
                    CAPK capk = new CAPK(context);

                    capk.setRID(capkData.getRid());// rid
                    capk.setKeyID(capkData.getRidIndex());//认证中心公钥索引(CA Public Key Index)

                    String expirationDate = capkData.getExpirationDate();
                    if (expirationDate != null) {
                        capk.setExpDate(expirationDate);//认证中心公钥有效期(CA Public Key period of validity)
                        Log.e("acpkData", "ExpDate--->" + capk.getExpDate() + "\ntag-->" + expirationDate);
                    }
                    String hashAlgorithm = capkData.getHashAlgorithm();
                    if (hashAlgorithm != null) {
                        capk.setHashInd(hashAlgorithm);//认证中心公钥哈什算法标识(CA Public Key Hash algorithm identification)
                        Log.e("acpkData", "HashInd--->" + capk.getHashInd() + "\ntag-->" + hashAlgorithm);
                    }
                    String publicKeyAlgorithm = capkData.getPublicKeyAlgorithm();
                    if (publicKeyAlgorithm != null) {
                        capk.setArithInd(publicKeyAlgorithm);//认证中心公钥算法标识(CA Public Key Algorithm identification)
                        Log.e("acpkData", "ArithInd--->" + capk.getArithInd() + "\ntag-->" + publicKeyAlgorithm);
                    }
                    String modulus = capkData.getModulus();
                    if (modulus != null) {
                        capk.setModul(modulus);//认证中心公钥模(CA Public Key module)
                        Log.e("acpkData", "tModul--->" + capk.getModul() + "\ntag-->" + modulus);
                    }
                    String exponent = capkData.getExponent();
                    if (exponent != null) {
                        capk.setExponent(exponent);//认证中心公钥指数(CA Public Key exponent)
                        Log.e("acpkData", "Exponent--->" + capk.getExponent() + "\ntag-->" + exponent);
                    }
                    String checkSum = capkData.getCheckSum();
                    if (checkSum != null) {
                        capk.setCheckSum(checkSum.substring(0, 40));//认证中心公钥校验值(CA Public Key Check value)
                        Log.e("acpkData", checkSum.length() + "----" + checkSum.length() + "CheckSum--->" + capk.getCheckSum() + "\ntag-->" + checkSum + "\ndataSize" + capk.toByteArray().length);
                    }
                    Log.e("addCapk", capkData.toString() + "\n" + "capkSize-->" + capk.toByteArray().length + "\n" + capk.print());
                    byte[] capkByte = capk.toByteArray();
                    int result = MyContext.emvCore.addCAPK(capkByte);
                    Log.e("addCapk_Result", result + "");
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setAIDNew(Context context) {
        String aidDataList = null;
        try {
            File file = new File(PARAMETER_PATH + "aid.development.json");
            FileInputStream fis = new FileInputStream(file);
            byte[] ret = new byte[fis.available()];
            fis.read(ret);
            aidDataList = new String(ret, "utf-8");
            Gson gson = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(aidDataList).getAsJsonObject();

            JsonArray jsonArray = jsonObject.getAsJsonArray("aids");
            Type AidList = new TypeToken<ArrayList<AidData>>() {
            }.getType();
            ArrayList<AidData> aidList = gson.fromJson(jsonArray, AidList);

            try {
                MyContext.emvCore.delAllAID();//delete Aid
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            int index = 0;
            for (AidData aidData : aidList) {

                Log.e("EmvActivity", "AID Index == " + index++);
                try {
                    EmvAppList emvAppList = new EmvAppList(context);

                    String PpassTermFLmtFlg = aidData.getPpassTermFLmtFlg();
                    if (PpassTermFLmtFlg != null) {
                        emvAppList.setPPassTermFLmtFlg(PpassTermFLmtFlg);
                    } else {
                        emvAppList.setPPassTermFLmtFlg("1");
                    }


                    String PPassRdClssTxnLmtFlg = aidData.getPPassRdClssTxnLmtFlg();
                    if (PPassRdClssTxnLmtFlg != null) {
                        emvAppList.setPPassRdClssTxnLmtFlg(PPassRdClssTxnLmtFlg);
                    } else {
                        emvAppList.setPPassRdClssTxnLmtFlg("1");
                    }


                    String PPassRdCVMLmtFlg = aidData.getPPassRdCVMLmtFlg();
                    if (PPassRdCVMLmtFlg != null) {
                        emvAppList.setPPassRdCVMLmtFlg(PPassRdCVMLmtFlg);
                    } else {
                        emvAppList.setPPassRdCVMLmtFlg("1");
                    }

                    String PPassRdClssFLmtFlg = aidData.getPPassRdClssFLmtFlg();
                    if (PPassRdClssFLmtFlg != null) {
                        emvAppList.setPPassRdClssFLmtFlg(PPassRdClssFLmtFlg);
                    } else {
                        emvAppList.setPPassRdClssFLmtFlg("1");
                    }

                    String PPassRdClssTxnLmtONdeviceFlg = aidData.getPPassRdClssTxnLmtONdeviceFlg();
                    if (PPassRdClssTxnLmtONdeviceFlg != null) {
                        emvAppList.setPPassRdClssTxnLmtONdeviceFlg(PPassRdClssTxnLmtONdeviceFlg);
                    } else {
                        emvAppList.setPPassRdClssTxnLmtONdeviceFlg("1");
                    }

                    String PPRdClssTxnLmtNoONdeviceFlg = aidData.getPPRdClssTxnLmtNoONdeviceFlg();
                    if (PPRdClssTxnLmtNoONdeviceFlg != null) {
                        emvAppList.setPPassRdClssTxnLmtNoONdeviceFlg(PPRdClssTxnLmtNoONdeviceFlg);
                    } else {
                        emvAppList.setPPassRdClssTxnLmtNoONdeviceFlg("1");
                    }
                    String PPassTACDefault = aidData.getPPassTACDefault();
                    if (PPassTACDefault != null) {
                        emvAppList.setPPassTACDefault(PPassTACDefault);
                    }
                    String TradeType = aidData.getTransType();
                    if (TradeType != null) {
                        emvAppList.setTransType(TradeType);
                    } else {
                        emvAppList.setTransType("00");
                    }
                    String PPassTACOnline = aidData.getPPassTACOnline();
                    if (PPassTACOnline != null) {
                        emvAppList.setPPassTACOnline(PPassTACOnline);
                    }
                    String PPassTACDenial = aidData.getPPassTACDenial();
                    if (PPassTACDenial != null) {
                        emvAppList.setPPassTACDenial(PPassTACDenial);
                    }
                    emvAppList.setAID(aidData.getAID());//aid
                    emvAppList.setCL_bAmount0Check(aidData.getCL_bAmount0Check());
                    emvAppList.setCL_bAmount0Option(aidData.getCL_bAmount0Option());
                    String selFag = aidData.getSelFlag();
                    if (selFag != null) {
                        emvAppList.setSelFlag(selFag);//选择应用标识
                    }
                    String version = aidData.getVersion();
                    if (version != null) {
                        emvAppList.setVersion(version);//应用版本
                    }

                    String priority = aidData.getPriority();
                    if (priority != null) {
                        emvAppList.setPriority(Integer.parseInt(priority));
                    }
                    String tacDefault = aidData.getTACDefault();
                    if (tacDefault != null) {
                        emvAppList.setTACDefault(tacDefault);//TAC－缺省
                    }
                    String tacOnline = aidData.getTACOnline();
                    if (tacOnline != null) {
                        emvAppList.setTACOnline(tacOnline);//TAC－联机
                    }
                    String tacDenial = aidData.getTACDenial();
                    if (tacDenial != null) {
                        emvAppList.setTACDenial(tacDenial);//TAC－拒绝
                    }
                    String floorLimit = aidData.getFloorLimit();
                    if (floorLimit != null) {
                        emvAppList.setFloorLimit(Long.parseLong(floorLimit));
                        emvAppList.setFloorLimitCheck(aidData.getFloorLimitCheck());
//                    emvAppList.setFloorLimit(Long.parseLong(""+ByteUtil.bytes2Int(ByteUtil.hexString2Bytes(tlvList.getTLV("9F1B").getValue()))));//最低限额
                    }

                    String threshold = aidData.getThreshold();
                    if (threshold != null) {
                        emvAppList.setThreshold(Long.parseLong((threshold)));//偏置随机选择的阈值
                    }
                    String maxTargetPer = aidData.getMaxTargetPer();
                    if (maxTargetPer != null) {
                        emvAppList.setMaxTargetPer(Integer.parseInt(maxTargetPer));//偏置随机选择的最大目标百分数
                    }
                    String targetPer = aidData.getTargetPer();
                    if (targetPer != null) {
                        emvAppList.setTargetPer(Integer.parseInt(targetPer));//随机选择的目标百分数
                    }
                    String VelocityCheck = aidData.getVelocityCheck();
                    if (VelocityCheck != null) {
                        emvAppList.setVelocityCheck(Integer.parseInt(VelocityCheck));
                    }

                    String DDOL = aidData.getDDOL();
                    if (DDOL != null) {
                        emvAppList.setDDOL(DDOL);//缺省DDOL
                    }
                    String BOnlinePin = aidData.getBOnlinePin();
                    if (BOnlinePin != null) {
                        emvAppList.setBOnlinePin(Integer.parseInt(BOnlinePin));
                    }
                    String EC_TermLimit = aidData.getEC_TermLimit();
                    if (EC_TermLimit != null) {
                        emvAppList.setEC_TermLimit(Long.parseLong(EC_TermLimit));//终端电子现金交易限额
                    }
                    if (EC_TermLimit != null) {
                        emvAppList.setEC_bTermLimitCheck(aidData.getEC_bTermLimitCheck());//电子现金: 是否支持终端交易限额 1是;0否
                    }
                    String CL_FloorLimit = aidData.getCL_FloorLimit();
                    if (CL_FloorLimit != null) {
                        emvAppList.setCL_FloorLimit(Long.parseLong(CL_FloorLimit));//非接触读写器脱机最低限额

                    }
                    if (aidData.getCL_bFloorLimitCheck() != null) {
                        emvAppList.setCL_bFloorLimitCheck(aidData.getCL_bFloorLimitCheck());
                    }
                    String CL_TransLimit = aidData.getCL_TransLimit();
                    if (CL_TransLimit != null) {
                        Log.e("PAYPASSTEST", "DF20" + Long.parseLong(CL_TransLimit));
                        emvAppList.setCL_TransLimit(Long.parseLong(CL_TransLimit));//非接触读写器交易限额

                    }
                    if (aidData.getCL_bTransLimitCheck() != null) {
                        emvAppList.setCL_bTransLimitCheck(aidData.getCL_bTransLimitCheck());
                    }

                    String CL_CVMLimit = aidData.getCL_CVMLimit();
                    if (CL_CVMLimit != null) {
                        emvAppList.setCL_CVMLimit(Long.parseLong(CL_CVMLimit)); //非接触终端CVM限额

                    } else {
                        emvAppList.setCL_CVMLimit(1000);
                    }
                    if (aidData.getCL_bCVMLimitCheck() != null) {
                        emvAppList.setCL_bCVMLimitCheck(aidData.getCL_bCVMLimitCheck());
                    }
                    String UDOL = aidData.getUDOL();
                    if (UDOL != null) {
                        Log.e("PAYPASSTEST", "UDOL = " + UDOL);
                        emvAppList.setUDOL(StringUtils.string2BCD(UDOL));
                    }
                    String MagAvn = aidData.getMagAvn();
                    if (MagAvn != null) {
                        emvAppList.setMagAvn(StringUtils.string2BCD(MagAvn));
                    }
                    if (UDOL != null) {
                        int len = UDOL.length() / 2;
                        emvAppList.setUDOLLen(new byte[]{(byte) ((len >> 8) & 0xFF), (byte) (len & 0xFF)}); //非接触终端CVM限额
                    }
                    String UcMagStrCVMCapWithCVM = aidData.getUcMagStrCVMCapWithCVM();
                    if (UcMagStrCVMCapWithCVM != null) {
                        emvAppList.setUcMagStrCVMCapWithCVM(StringUtils.string2BCD(UcMagStrCVMCapWithCVM));
                    }
                    String UcMagStrCVMCapNoCVM = aidData.getUcMagStrCVMCapNoCVM();
                    if (UcMagStrCVMCapNoCVM != null) {
                        emvAppList.setUcMagStrCVMCapNoCVM(StringUtils.string2BCD(UcMagStrCVMCapNoCVM));
                    }
                    String UcKernelConfig = aidData.getUcKernelConfig();
                    if (UcKernelConfig != null) {
                        emvAppList.setUcKernelConfig(StringUtils.string2BCD(UcKernelConfig));
                    }
                    String UcCVMCap = aidData.getUcCVMCap();
                    if (UcCVMCap != null) {
                        emvAppList.setUcCVMCap(StringUtils.string2BCD(UcCVMCap));
                    }
                    String UcCVMCapNoCVM = aidData.getUcCVMCapNoCVM();
                    if (UcCVMCapNoCVM != null) {
                        emvAppList.setUcCVMCapNoCVM(StringUtils.string2BCD(UcCVMCapNoCVM));
                    }
                    String UcCardDataInputCap = aidData.getUcCardDataInputCap();
                    if (UcCardDataInputCap != null) {
                        Log.e("PAYPASSTEST", "DF8117 = " + UcCardDataInputCap);
                        emvAppList.setUcCardDataInputCap(StringUtils.string2BCD(UcCardDataInputCap));
                    }
                    String SecurityCap = aidData.getSecurityCap();
                    if (SecurityCap != null) {
                        Log.e("PAYPASSTEST", "DF811F = " + SecurityCap);
                        emvAppList.setSecurityCap(StringUtils.string2BCD(SecurityCap));
                    }
                    String RdClssTxnLmtONdevice = aidData.getRdClssTxnLmtONdevice();
                    if (RdClssTxnLmtONdevice != null) {
                        emvAppList.setRdClssTxnLmtONdevice(Long.parseLong(RdClssTxnLmtONdevice));
                    }
                    String RdClssTxnLmtNoONdevice = aidData.getRdClssTxnLmtNoONdevice();
                    if (RdClssTxnLmtNoONdevice != null) {
                        emvAppList.setRdClssTxnLmtNoONdevice(Long.parseLong(RdClssTxnLmtNoONdevice));
                    }
                    String termTransQuali = aidData.getTermTransQuali();
                    if (termTransQuali != null) {
                        emvAppList.setTermTransQuali(termTransQuali);
                    }
                    String UcMagSupportFlg = aidData.getUcMagSupportFlg();
                    if (UcMagSupportFlg != null) {
                        emvAppList.setUcMagSupportFlg(ByteUtil.hexString2Bytes(UcMagSupportFlg));
                    }

                    String MerchId = aidData.getMerchId();
                    if (MerchId != null) {
                        emvAppList.setMerchId(MerchId);
                    }
                    String MerchName = aidData.getMerchName();
                    if (MerchName != null) {
                        emvAppList.setMerchName(MerchName);
                    }
                    String MerchCateCode = aidData.getMerchCateCode();
                    if (MerchCateCode != null) {
                        emvAppList.setMerchCateCode(MerchCateCode);
                    }
                    String TransCurrCode = aidData.getTransCurrCode();
                    if (TransCurrCode != null) {
                        emvAppList.setTransCurrCode(TransCurrCode);
                    }
                    String TransCurrExp = aidData.getTransCurrExp();
                    if (TransCurrExp != null) {
                        emvAppList.setTransCurrExp(TransCurrExp);
                    }
                    String ReferCurrCode = aidData.getReferCurrCode();
                    if (ReferCurrCode != null) {
                        emvAppList.setReferCurrCode(ReferCurrCode);
                    }
                    String ReferCurrExp = aidData.getReferCurrExp();
                    if (ReferCurrExp != null) {
                        emvAppList.setReferCurrExp(ReferCurrExp);
                    }
                    String CLNotAllowFlag = aidData.getCLNotAllowFlag();
                    if (CLNotAllowFlag != null) {
                        emvAppList.setCLNotAllowFlag(CLNotAllowFlag);
                    }
                    String TerminalType = aidData.getTerminalType();
                    if (TerminalType != null) {
                        emvAppList.setTerminalType(Integer.valueOf(TerminalType, 16));
                    }
                    String TermCapab = aidData.getTermCapab();
                    if (TermCapab != null) {
                        Log.e("EmvActivity", "TermCapab = " + Integer.valueOf(TermCapab, 16));
                        emvAppList.setTermCapab(Integer.valueOf(TermCapab, 16));
                    }
                    String ExTermCapab = aidData.getExTermCapab();
                    if (ExTermCapab != null) {
                        emvAppList.setExTermCapab(ExTermCapab);
                    }
                    String CountryCode = aidData.getCountryCode();
                    if (CountryCode != null) {
                        emvAppList.setCountryCode(CountryCode);
                    }
                    if (aidData.getTerminalCapabilities() != null) {
                        emvAppList.setTermCapab(Integer.parseInt(aidData.getTerminalCapabilities()));
                    }
                    if (aidData.getRiskManData() != null) {
                        emvAppList.setRiskManData(aidData.getRiskManData());
                    }
                    byte[] mcdata = emvAppList.toMCByteArray();
                    Log.e("PAYPASSTEST", "mcdata = " + ByteUtil.bytes2HexString(mcdata));
                    Log.e("PAYPASSTEST", "mcdata length= " + mcdata.length);
                    MyContext.emvCore.addAID_MC(emvAppList.toByteArray(), mcdata);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setTermConfigParam(Context context) {
        String aidDataList = null;
        try {
            File file = new File(PARAMETER_PATH + "termConfig.json");
            FileInputStream fis = new FileInputStream(file);
            byte[] ret = new byte[fis.available()];
            fis.read(ret);
            aidDataList = new String(ret, "utf-8");

            MyContext.emvCore.DeleteAllTornRecord();

            Gson gson = new GsonBuilder().create();
            TermConfigParam termConfigParam = gson.fromJson(aidDataList, TermConfigParam.class);
            PayPassTermConfig payPassTermConfig = new PayPassTermConfig(context);
            if (termConfigParam.getAucBalanceAfterGAC() != null && termConfigParam.getAucBalanceAfterGAC().length() > 0) {
                payPassTermConfig.setAucBalanceAfterGAC(termConfigParam.getAucBalanceAfterGAC());
            }

            if (termConfigParam.getUcMaxLifeTimeTornFlg() != null && termConfigParam.getUcMaxLifeTimeTornFlg().length() > 0) {
                payPassTermConfig.setUcMaxLifeTimeTornFlg(termConfigParam.getUcMaxLifeTimeTornFlg());
            }

            if (termConfigParam.getAucMaxLifeTimeTorn() != null && termConfigParam.getAucMaxLifeTimeTorn().length() > 0) {
                payPassTermConfig.setAucMaxLifeTimeTorn(termConfigParam.getAucMaxLifeTimeTorn());
            }

            if (termConfigParam.getUcMaxNumberTornFlg() != null && termConfigParam.getUcMaxNumberTornFlg().length() > 0) {
                payPassTermConfig.setUcMaxNumberTornFlg(termConfigParam.getUcMaxNumberTornFlg());
            }

            if (termConfigParam.getUcMaxNumberTorn() != null && termConfigParam.getUcMaxNumberTorn().length() > 0) {
                payPassTermConfig.setUcMaxNumberTorn(termConfigParam.getUcMaxNumberTorn());
            }

            if (termConfigParam.getUcBalanceBeforeGACFlg() != null && termConfigParam.getUcBalanceBeforeGACFlg().length() > 0) {
                payPassTermConfig.setUcBalanceBeforeGACFlg(termConfigParam.getUcBalanceBeforeGACFlg());
            }

            if (termConfigParam.getAucBalanceBeforeGAC() != null && termConfigParam.getAucBalanceBeforeGAC().length() > 0) {
                payPassTermConfig.setAucBalanceBeforeGAC(termConfigParam.getAucBalanceBeforeGAC());
            }

            if (termConfigParam.getUcBalanceAfterGACFlg() != null && termConfigParam.getUcBalanceAfterGACFlg().length() > 0) {
                payPassTermConfig.setUcBalanceAfterGACFlg(termConfigParam.getUcBalanceAfterGACFlg());
            }

            if (termConfigParam.getAucBalanceAfterGAC() != null && termConfigParam.getAucBalanceAfterGAC().length() > 0) {
                payPassTermConfig.setAucBalanceAfterGAC(termConfigParam.getAucBalanceAfterGAC());
            }

            if (termConfigParam.getUcMobileSupFlg() != null && termConfigParam.getUcMobileSupFlg().length() > 0) {
                payPassTermConfig.setUcMobileSupFlg(termConfigParam.getUcMobileSupFlg());
            }

            if (termConfigParam.getUcMobileSup() != null && termConfigParam.getUcMobileSup().length() > 0) {
                payPassTermConfig.setUcMobileSup(termConfigParam.getUcMobileSup());
            }

            if (termConfigParam.getUcHoldTimeValueFlg() != null && termConfigParam.getUcHoldTimeValueFlg().length() > 0) {
                payPassTermConfig.setUcHoldTimeValueFlg(termConfigParam.getUcHoldTimeValueFlg());
            }

            if (termConfigParam.getUcHoldTimeValue() != null && termConfigParam.getUcHoldTimeValue().length() > 0) {
                payPassTermConfig.setUcHoldTimeValue(termConfigParam.getUcHoldTimeValue());
            }

            if (termConfigParam.getUcInterDevSerNumFlg() != null && termConfigParam.getUcInterDevSerNumFlg().length() > 0) {
                payPassTermConfig.setUcInterDevSerNumFlg(termConfigParam.getUcInterDevSerNumFlg());
            }

            if (termConfigParam.getAucInterDevSerNum() != null && termConfigParam.getAucInterDevSerNum().length() > 0) {
                payPassTermConfig.setAucInterDevSerNum(termConfigParam.getAucInterDevSerNum());
            }

            if (termConfigParam.getUcKernelIDFlg() != null && termConfigParam.getUcKernelIDFlg().length() > 0) {
                payPassTermConfig.setUcKernelIDFlg(termConfigParam.getUcKernelIDFlg());
            }

            if (termConfigParam.getUcKernelID() != null && termConfigParam.getUcKernelID().length() > 0) {
                payPassTermConfig.setUcKernelID(termConfigParam.getUcKernelID());
            }

            if (termConfigParam.getUcMsgHoldTimeFlg() != null && termConfigParam.getUcMsgHoldTimeFlg().length() > 0) {
                payPassTermConfig.setUcMsgHoldTimeFlg(termConfigParam.getUcMsgHoldTimeFlg());
            }

            if (termConfigParam.getAucMsgHoldTime() != null && termConfigParam.getAucMsgHoldTime().length() > 0) {
                payPassTermConfig.setAucMsgHoldTime(termConfigParam.getAucMsgHoldTime());
            }

            if (termConfigParam.getMinimumRRGP() != null && termConfigParam.getMinimumRRGP().length() > 0) {
                payPassTermConfig.setMinimumRRGP(termConfigParam.getMinimumRRGP());
            }

            if (termConfigParam.getMaximumRelayRGP() != null && termConfigParam.getMaximumRelayRGP().length() > 0) {
                payPassTermConfig.setMaximumRelayRGP(termConfigParam.getMaximumRelayRGP());
            }

            if (termConfigParam.getTerminalETTFRRCAPDU() != null && termConfigParam.getTerminalETTFRRCAPDU().length() > 0) {
                payPassTermConfig.setTerminalETTFRRCAPDU(termConfigParam.getTerminalETTFRRCAPDU());
            }

            if (termConfigParam.getTerminalETTFRRRAPDU() != null && termConfigParam.getTerminalETTFRRRAPDU().length() > 0) {
                payPassTermConfig.setTerminalETTFRRRAPDU(termConfigParam.getTerminalETTFRRRAPDU());
            }

            if (termConfigParam.getRelayRAT() != null && termConfigParam.getRelayRAT().length() > 0) {
                payPassTermConfig.setRelayRAT(termConfigParam.getRelayRAT());
            }

            if (termConfigParam.getRelayRTTMT() != null && termConfigParam.getRelayRTTMT().length() > 0) {
                payPassTermConfig.setRelayRTTMT(termConfigParam.getRelayRTTMT());
            }

            if (termConfigParam.getMinimumRRGPfalg() != null && termConfigParam.getMinimumRRGPfalg().length() > 0) {
                payPassTermConfig.setMinimumRRGPFlg(termConfigParam.getMinimumRRGPfalg());
            }

            if (termConfigParam.getMaximumRelayRGPfalg() != null && termConfigParam.getMaximumRelayRGPfalg().length() > 0) {
                payPassTermConfig.setMaximumRelayRGPFlg(termConfigParam.getMaximumRelayRGPfalg());
            }

            if (termConfigParam.getTerminalETTFRRCAPDUfalg() != null && termConfigParam.getTerminalETTFRRCAPDUfalg().length() > 0) {
                payPassTermConfig.setTerminalETTFRRCAPDUFlg(termConfigParam.getTerminalETTFRRCAPDUfalg());
            }

            if (termConfigParam.getTerminalETTFRRRAPDUfalg() != null && termConfigParam.getTerminalETTFRRRAPDUfalg().length() > 0) {
                payPassTermConfig.setTerminalETTFRRRAPDUFlg(termConfigParam.getTerminalETTFRRRAPDUfalg());
            }

            if (termConfigParam.getRelayRATfalg() != null && termConfigParam.getRelayRATfalg().length() > 0) {
                payPassTermConfig.setRelayRATFlg(termConfigParam.getRelayRATfalg());
            }

            if (termConfigParam.getRelayRTTMTfalg() != null && termConfigParam.getRelayRTTMTfalg().length() > 0) {
                payPassTermConfig.setRelayRTTMTFlg(termConfigParam.getRelayRTTMTfalg());
            }

            MyContext.emvCore.setTermConfig(payPassTermConfig.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void doPosUpSend(int n, ArrayList<String> keyList, int type) throws Exception {
        try {
            byte[] databyte = ChanelPacket.queryparam(type, n);
            byte[] ret = CommunAction.doNet(databyte);
            Log.i("info", "doPosUpSend: " + HEXUitl.bytesToHex(ret));
            Msg msg = ChanelPacket.decode(ret);
            if (msg.getReqCode().getFlag() == 'A') {
                Log.i("info", "doPosUpSend: " + msg.body.getField(62) + "-->" + HEXUitl.bytesToHex(msg.body.getFieldData(62)));
                byte[] f62 = msg.body.getFieldData(62);
                if (f62 == null) {
                    return;
                }
                int res = f62[0];
                Log.i("info", "doPosUpSend: res" + res + "---》" + 0x32);
                if (res > 0x30) {
                    if (372 == type) {
                        parsePublicKey(HEXUitl.bytesToHex(f62), keyList);
                    } else if (382 == type) {
                        parseParams(f62, keyList);
                    }
                }
                Log.i("info", "doPosUpSend: n" + n + "--->list size" + keyList.size());
                if (res == 0x32) {
                    doPosUpSend(++n, keyList, type);
                    return;
                }

            } else {
                throw new Exception("doPosUpSend exception");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static void parseParams(byte[] f62, ArrayList<String> list) {
        f62 = Arrays.copyOfRange(f62, 1, f62.length);
        int len = 0, start = 0, length = f62.length;
        while (start < length) {
            len = f62[start + 2];
            String aidPar = HEXUitl.bytesToHex(Arrays.copyOfRange(f62, start, start + len + 3));
            if (!list.contains(aidPar)) {
                list.add(aidPar);
            }
            start += len + 3;
        }
    }

    private static void parsePublicKey(String f62, ArrayList<String> keyList) {
        f62 = f62.replace("9F06", ",9F06");
        String[] f62s = f62.split(",");
        for (int i = 1; i < f62s.length; i++) {
            Log.i("info", "parsePublicKey: f62" + i + f62s[i]);
            if (!keyList.contains(f62s[i])) {
                keyList.add(f62s[i]);
            }
        }
//        int start = 2;
//        int end = start + 38;
//        int length = f62.length();
//        Log.i("info", "parsePublicKey: "+f62);
//        String temp;
//        while (end <= length) {
//            temp = f62.substring(start, end);
//            if (!keyList.contains(temp)) {
//                keyList.add(temp);
//            }
//            start = end;
//            end = start + 38;
//        }
    }

    public static void doSettlement(final String operatorNo, final List<TransactionInfo> settleList, final OnCommonListener listener) {
        FIXED_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (!IsNeedLogin(listener)) {
                    listener.onError(0, "Abnormal consumption");
                    return;
                }
                Log.i("setTransType", "doSettlement: " + WeiPassGlobal.getTransactionInfo().getTransType());
                TradeInfo info = new ChanelPacket().doSettlement(operatorNo, settleList, listener);
                if (info.isOK) {
                    listener.onSuccess();
                } else {
                    listener.onError(1, info.errorMsg);
                }
            }
        });
    }


    public static void doPreAuth(final String amount, final OnTraditionListener onCommonListener) {

        FIXED_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (!IsNeedLogin(null)) {
                    onCommonListener.onError(0, "Automatic check-in failure");
                    return;
                }
                byte[] bytes = null;
                try {
                    TradeInfo tradeInfo = new TradeInfo();

                    bytes = ChanelPacket.makePreAuth(amount);
                    byte[] ret = CommunAction.doNet(bytes);
                    Log.v("=========", "socketaction donet ret:" + ByteUtil.bytes2HexString(ret));
                    Msg msg = ChanelPacket.decode(ret);
                    boolean insertCard = Utils.cardType == Utils.CardTypeIC_ICC && WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("05");
                    RespCode rc = msg.getReqCode();
                    tradeInfo.msg = msg;
                    tradeInfo.errorCode = rc.getCode();
                    tradeInfo.errorMsg = rc.getErrorMsg();
                    int poscount = PrefUtil.getSerialNo();
                    poscount++;
                    PrefUtil.putSerialNo(poscount);
                    if (msg.getReqCode().getFlag() == 'A') {
                        /*if (msg.macResult != Msg.MAC_OK) {
                            onCommonListener.onError(0, "MAC check error");
                        }*/

                        WeiPassGlobal.getTransactionInfo().setIssuerNo(new String(msg.body.getFieldData(44), "GB2312"));
                        tradeInfo.setTradeType(TradeInfo.Type_Auth);
                        castResult(tradeInfo);
//                        WeiPassGlobal.getTransactionInfo().setTransaceNo(msg.body.getField(11));
//                        WeiPassGlobal.getTransactionInfo().setTransTime(msg.body.getField(12));
//                        WeiPassGlobal.getTransactionInfo().setTransDate(msg.body.getField(13));
//                        WeiPassGlobal.getTransactionInfo().setSettleDate(msg.body.getField(15));
//                        WeiPassGlobal.getTransactionInfo().setBacthNo(msg.body.getField(60).substring(2, 8));
//                        WeiPassGlobal.getTransactionInfo().setReferNo(msg.body.get(37));
//                        WeiPassGlobal.getTransactionInfo().setAuthNo(msg.body.get(38));
//                        WeiPassGlobal.getTransactionInfo().setField60(msg.body.get(60));
//                        WeiPassGlobal.getTransactionInfo().setField63(msg.body.get(63));
                        onCommonListener.onResult(tradeInfo);
                    } else {
                        onCommonListener.onError(0, msg.getField39Code());
                    }
                } catch (PayException e) {
                    if (onCommonListener != null) {
                        onCommonListener.onError(-1, e.getMessage());
                    }
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (!CommunAction.isSendSuccess) {
                        PrefUtil.putReversal(null);
                    }
                    //交易异常，请联系管理员
                    onCommonListener.onError(0, "Abnormal transaction, please contact the administrator.");
                }
            }
        });
    }

    public static void ConsumptionRevoked(final TransactionInfo transactionInfo, final OnTraditionListener onCommonListener) {
        FIXED_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (!IsNeedLogin(null)) {
                    onCommonListener.onError(0, "Abnormal consumption");
                    return;
                }
                byte[] bytes = null;
                try {
                    TradeInfo tradeInfo = new TradeInfo();
                    transactionInfo.setTransaceNo(PrefUtil.getSerialNo() + "");
                    bytes = ChanelPacket.ConsumptionRevoked(transactionInfo);
                    byte[] ret = CommunAction.doNet(bytes);
                    Log.v("=========", "socketaction donet ret:" + ByteUtil.bytes2HexString(ret));
                    Msg msg = ChanelPacket.decode(ret);
                    RespCode rc = msg.getReqCode();
                    tradeInfo.msg = msg;
                    tradeInfo.errorCode = rc.getCode();
                    tradeInfo.errorMsg = rc.getErrorMsg();
                    int poscount = PrefUtil.getSerialNo();
                    poscount++;
                    PrefUtil.putSerialNo(poscount);
                    if (msg.getReqCode().getFlag() == 'A') {
                        if (msg.macResult != Msg.MAC_OK) {
                            onCommonListener.onError(0, "MAC check error");
                        }
                        castResult(tradeInfo);
                        onCommonListener.onResult(tradeInfo);
                    } else {
                        onCommonListener.onError(0, "Abnormal consumption");
                    }

                } catch (PayException e) {
                    if (onCommonListener != null) {
                        onCommonListener.onError(-1, e.getMessage());
                    }
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (!CommunAction.isSendSuccess) {
                        PrefUtil.putReversal(null);
                    }
                    onCommonListener.onError(0, "Socket timeout !");
                }
            }
        });
    }

    public static void reFound(final OnTraditionListener onCommonListener) {
        FIXED_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (!IsNeedLogin(null)) {
                    onCommonListener.onError(0, "Abnormal consumption");
                    return;
                }
                byte[] bytes = null;
                try {
                    TradeInfo tradeInfo = new TradeInfo();
                    WeiPassGlobal.getTransactionInfo().setTransaceNo(PrefUtil.getSerialNo() + "");
                    bytes = ChanelPacket.reFound();
                    byte[] ret = CommunAction.doNet(bytes);
                    Log.v("=========", "socketaction donet ret:" + ByteUtil.bytes2HexString(ret));
                    Msg msg = ChanelPacket.decode(ret);
                    RespCode rc = msg.getReqCode();
                    tradeInfo.msg = msg;
                    tradeInfo.errorCode = rc.getCode();
                    tradeInfo.errorMsg = rc.getErrorMsg();
                    int poscount = PrefUtil.getSerialNo();
                    poscount++;
                    PrefUtil.putSerialNo(poscount);
                    if (msg.getReqCode().getFlag() == 'A') {
                        /*if (msg.macResult != Msg.MAC_OK) {
                            onCommonListener.onError(0, "MAC check error");
                        }*/
                        castResult(tradeInfo);
                        onCommonListener.onResult(tradeInfo);
                    } else {
                        onCommonListener.onError(0, "Abnormal consumption");
                    }
                } catch (PayException e) {
                    if (onCommonListener != null) {
                        onCommonListener.onError(-1, e.getMessage());
                    }
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (!CommunAction.isSendSuccess) {
                        PrefUtil.putReversal(null);
                    }
                    onCommonListener.onError(0, "Socket timeout !");
                }
            }
        });
    }

    private static void castResult(TradeInfo tradeInfo) {
//        if (tradeInfo.isOK) {
        Msg msg = tradeInfo.msg;
        if (tradeInfo.msg == null) {
            return;
        }
        if (!TextUtils.isEmpty(msg.body.getField(11))) {
            WeiPassGlobal.getTransactionInfo().setTransaceNo(msg.body.getField(11));
        }
        if (!TextUtils.isEmpty(msg.body.getField(12))) {
            WeiPassGlobal.getTransactionInfo().setTransTime(msg.body.getField(12));
        }
        if (!TextUtils.isEmpty(msg.body.getField(13))) {
            WeiPassGlobal.getTransactionInfo().setTransDate(msg.body.getField(13));
        }
        if (!TextUtils.isEmpty(msg.body.getField(14))) {
            WeiPassGlobal.getTransactionInfo().setExpireDate(msg.body.getField(14));
        }
        if (!TextUtils.isEmpty(msg.body.getField(15))) {
            WeiPassGlobal.getTransactionInfo().setSettleDate(msg.body.getField(15));
        }
        if (!TextUtils.isEmpty(msg.body.getField(60))) {
            WeiPassGlobal.getTransactionInfo().setBacthNo(msg.body.getField(60).substring(2, 8));
        }
        if (!TextUtils.isEmpty(msg.body.getField(37))) {
            WeiPassGlobal.getTransactionInfo().setReferNo(msg.body.get(37));
        }
        if (!TextUtils.isEmpty(msg.body.getField(38))) {
            WeiPassGlobal.getTransactionInfo().setAuthNo(msg.body.get(38));
        }
        try {
            if (!TextUtils.isEmpty(msg.body.getField(44))) {
                WeiPassGlobal.getTransactionInfo().setIssuerNo(new String(msg.body.getFieldData(44), "GB2312"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(msg.body.getField(60))) {
            WeiPassGlobal.getTransactionInfo().setField60(msg.body.get(60));
        }
        if (!TextUtils.isEmpty(msg.body.getField(54))) {
            WeiPassGlobal.getTransactionInfo().setEC_Balance(msg.body.get(54));
        }
        if (!TextUtils.isEmpty(msg.body.getField(63))) {
            WeiPassGlobal.getTransactionInfo().setField63(msg.body.get(63));
        }
//            }
        Log.i("setExpireDate", "castResult: " + WeiPassGlobal.getTransactionInfo().getExpireDate());

    }


    public static void doPreAuthComplete(final TransactionInfo transactionInfo, final OnTraditionListener onCommonListener) {

        FIXED_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (!IsNeedLogin(null)) {
                    onCommonListener.onError(0, "Abnormal consumption");
                    return;
                }
                byte[] bytes = null;
                try {
                    TradeInfo tradeInfo = new TradeInfo();

                    bytes = ChanelPacket.makePreAuthComplete(transactionInfo);
                    byte[] ret = CommunAction.doNet(bytes);
                    Log.v("=========", "socketaction donet ret:" + ByteUtil.bytes2HexString(ret));
                    Msg msg = ChanelPacket.decode(ret);
                    boolean insertCard = Utils.cardType == Utils.CardTypeIC_ICC && WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("05");
                    RespCode rc = msg.getReqCode();
                    tradeInfo.msg = msg;
                    tradeInfo.errorCode = rc.getCode();
                    tradeInfo.errorMsg = rc.getErrorMsg();
                    int poscount = PrefUtil.getSerialNo();
                    poscount++;
                    PrefUtil.putSerialNo(poscount);
                    if (msg.getReqCode().getFlag() == 'A') {
                        if (msg.macResult != Msg.MAC_OK) {
                            onCommonListener.onError(0, "MAC check error");
                        }

                        WeiPassGlobal.getTransactionInfo().setIssuerNo(new String(msg.body.getFieldData(44), "GB2312"));
                        tradeInfo.setTradeType(TradeInfo.Type_Auth);
                        WeiPassGlobal.getTransactionInfo().setTransaceNo(msg.body.getField(11));
                        WeiPassGlobal.getTransactionInfo().setTransTime(msg.body.getField(12));
                        WeiPassGlobal.getTransactionInfo().setTransDate(msg.body.getField(13));
                        WeiPassGlobal.getTransactionInfo().setSettleDate(msg.body.getField(15));
                        WeiPassGlobal.getTransactionInfo().setBacthNo(msg.body.getField(60).substring(2, 8));
                        WeiPassGlobal.getTransactionInfo().setReferNo(msg.body.get(37));
                        WeiPassGlobal.getTransactionInfo().setAuthNo(msg.body.get(38));
                        WeiPassGlobal.getTransactionInfo().setField60(msg.body.get(60));
                        WeiPassGlobal.getTransactionInfo().setField63(msg.body.get(63));
                        onCommonListener.onResult(tradeInfo);
                    } else {
                        onCommonListener.onError(0, msg.getField39Code());
                    }
                } catch (PayException e) {
                    if (onCommonListener != null) {
                        onCommonListener.onError(-1, e.getMessage());
                    }
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (!CommunAction.isSendSuccess) {
                        PrefUtil.putReversal(null);
                    }
                    //交易异常，请联系管理员
                    onCommonListener.onError(0, "Abnormal transaction, please contact the administrator.");
                }
            }
        });
    }

    public static void doPreAuthVoid(final TransactionInfo transactionInfo, final OnTraditionListener onCommonListener) {

        FIXED_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (!IsNeedLogin(null)) {
                    onCommonListener.onError(0, "Abnormal consumption");
                    return;
                }
                byte[] bytes = null;
                try {
                    TradeInfo tradeInfo = new TradeInfo();

                    bytes = ChanelPacket.makePreAuthVoid(transactionInfo);
                    byte[] ret = CommunAction.doNet(bytes);
                    Log.v("=========", "socketaction donet ret:" + ByteUtil.bytes2HexString(ret));
                    Msg msg = ChanelPacket.decode(ret);
                    boolean insertCard = Utils.cardType == Utils.CardTypeIC_ICC && WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("05");
                    RespCode rc = msg.getReqCode();
                    tradeInfo.msg = msg;
                    tradeInfo.errorCode = rc.getCode();
                    tradeInfo.errorMsg = rc.getErrorMsg();
                    int poscount = PrefUtil.getSerialNo();
                    poscount++;
                    PrefUtil.putSerialNo(poscount);
                    if (msg.getReqCode().getFlag() == 'A') {
                        if (msg.macResult != Msg.MAC_OK) {
                            onCommonListener.onError(0, "MAC check error");
                        }

                        WeiPassGlobal.getTransactionInfo().setIssuerNo(new String(msg.body.getFieldData(44), "GB2312"));
                        tradeInfo.setTradeType(TradeInfo.Type_Auth);
                        WeiPassGlobal.getTransactionInfo().setTransaceNo(msg.body.getField(11));
                        WeiPassGlobal.getTransactionInfo().setTransTime(msg.body.getField(12));
                        WeiPassGlobal.getTransactionInfo().setTransDate(msg.body.getField(13));
                        WeiPassGlobal.getTransactionInfo().setSettleDate(msg.body.getField(15));
                        WeiPassGlobal.getTransactionInfo().setBacthNo(msg.body.getField(60).substring(2, 8));
                        WeiPassGlobal.getTransactionInfo().setReferNo(msg.body.get(37));
                        WeiPassGlobal.getTransactionInfo().setAuthNo(msg.body.get(38));
                        WeiPassGlobal.getTransactionInfo().setField60(msg.body.get(60));
                        WeiPassGlobal.getTransactionInfo().setField63(msg.body.get(63));
                        onCommonListener.onResult(tradeInfo);
                    } else {
                        onCommonListener.onError(0, msg.getField39Code());
                    }
                } catch (PayException e) {
                    if (onCommonListener != null) {
                        onCommonListener.onError(-1, e.getMessage());
                    }
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (!CommunAction.isSendSuccess) {
                        PrefUtil.putReversal(null);
                    }
                    //Abnormal transaction, please contact the administrator.
                    onCommonListener.onError(0, "Abnormal transaction, please contact the administrator.");
                }
            }
        });
    }

    public static void doQueryBalance(final String money, final OnTraditionListener onCommonListener) {

        FIXED_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (!IsNeedLogin(null)) {
                    onCommonListener.onError(0, "Abnormal consumption");
                    return;
                }
                byte[] bytes = null;
                try {
                    TradeInfo tradeInfo = new TradeInfo();

                    bytes = ChanelPacket.queryBalance();
                    byte[] ret = CommunAction.doNet(bytes);
                    Msg msg = ChanelPacket.decode(ret);
                    boolean insertCard = Utils.cardType == Utils.CardTypeIC_ICC && WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("05");
                    RespCode rc = msg.getReqCode();
                    tradeInfo.msg = msg;
                    tradeInfo.errorCode = rc.getCode();
                    tradeInfo.errorMsg = rc.getErrorMsg();
                    int poscount = PrefUtil.getSerialNo();
                    poscount++;
                    PrefUtil.putSerialNo(poscount);
                    if (msg.getReqCode().getFlag() == 'A') {
                       /* if (msg.macResult != Msg.MAC_OK) {
                            onCommonListener.onError(0, "MAC check error");
                        }*/
//                        WeiPassGlobal.getTransactionInfo().setIssuerNo(new String(msg.body.getFieldData(44), "GB2312"));
                        tradeInfo.setTradeType(TradeInfo.Type_QueryBalance);
//                        WeiPassGlobal.getTransactionInfo().setTransaceNo(msg.body.getField(11));
//                        WeiPassGlobal.getTransactionInfo().setTransTime(msg.body.getField(12));
//                        WeiPassGlobal.getTransactionInfo().setTransDate(msg.body.getField(13));
//                        WeiPassGlobal.getTransactionInfo().setSettleDate(msg.body.getField(15));
//                        WeiPassGlobal.getTransactionInfo().setBacthNo(msg.body.getField(60).substring(2, 8));
//                        WeiPassGlobal.getTransactionInfo().setReferNo(msg.body.get(37));
//                        WeiPassGlobal.getTransactionInfo().setAuthNo(msg.body.get(38));
//                        WeiPassGlobal.getTransactionInfo().setField60(msg.body.get(60));
//                        WeiPassGlobal.getTransactionInfo().setField63(msg.body.get(63));
//                        WeiPassGlobal.getTransactionInfo().setEC_Balance(msg.body.get(54));
                        castResult(tradeInfo);
                        onCommonListener.onResult(tradeInfo);
                    } else {
                        onCommonListener.onError(0, msg.getField39Code());
                    }
                } catch (PayException e) {
                    if (onCommonListener != null) {
                        onCommonListener.onError(-1, e.getMessage());
                    }
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (!CommunAction.isSendSuccess) {
                        PrefUtil.putReversal(null);
                    }
                    //Abnormal transaction, please contact the administrator.
                    onCommonListener.onError(0, "Abnormal transaction, please contact the administrator.");
                }
            }
        });
    }

    public static void doSignUpload(final OnTraditionListener onCommonListener) {
        FIXED_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] bytes = null;
                    if (onCommonListener != null) {
                        onCommonListener.onProgress("Sending electronic signature..");
                    }
                    TradeInfo tradeInfo = new TradeInfo();
                    bytes = ChanelPacket.makeSignUpload(null);
                    byte[] ret = CommunAction.doNet(bytes);
                    Msg msg = ChanelPacket.decode(ret);
                    RespCode rc = msg.getReqCode();
                    tradeInfo.msg = msg;
                    tradeInfo.errorCode = rc.getCode();
                    tradeInfo.errorMsg = rc.getErrorMsg();
                    Log.i("tradeInfo.errorCode", "run: " + tradeInfo.errorCode);
                    if (msg.getReqCode().getFlag() == 'A') {
                        castResult(tradeInfo);
                        if (onCommonListener != null) {
                            onCommonListener.onResult(tradeInfo);
                            onCommonListener.onSuccess();
                        }
                    } else {
                        if (onCommonListener != null) {
                            onCommonListener.onError(0, msg.getField39Code());
                        }
                    }

                } catch (PayException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void doParamePassing() {
//        doParamePassing(null);
    }

//    private static void doParamePassing(final OnCommonListener onCommonListener) {
//        FIXED_EXECUTOR.execute(new Runnable() {
//            @Override
//            public void run() {
//                byte[] bytes = null;
//                TradeInfo tradeInfo = new TradeInfo();
//
//                try {
//                    if (onCommonListener != null)
//                        onCommonListener.onProgress("Parameter Download");
//                    bytes = ChanelPacket.makeParamePassing();
//                    byte[] ret = CommunAction.doNet(bytes);
//                    Msg msg = ChanelPacket.decode(ret);
//                    if (msg.getReqCode().getFlag() == 'A') {
//                        Log.i("doParamePassing", "sucess: msg" + msg.toString());
////                        if (msg.macResult != Msg.MAC_OK) {
////                            // mac 错
////                            return;
////                        }
//                        String f62 = HEXUitl.bytesToHex(msg.body.getFieldData(62));
//                        Log.i("doParamePassing", "f62: f62" + f62);
//                        castF62(f62);
//                        if (onCommonListener != null)
//                            onCommonListener.onSuccess();
//
//                    } else {
//                        if (onCommonListener != null)
//                            onCommonListener.onError(-1, "Parameter download failed");
//                        Log.i("doParamePassing", "err: msg" + msg.toString());
//                    }
//                } catch (PayException e) {
//                    e.printStackTrace();
//                    if (onCommonListener != null)
//                        onCommonListener.onError(-1, "Parameter download failed");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    if (onCommonListener != null)
//                        onCommonListener.onError(-1, "Parameter download failed");
//                }
//            }
//        });
//    }

//    public static void castF62(String str) throws IllegalAccessException {
//        PassingParame parame = new PassingParame();
////        int[] intlen ={2,1,1,2,1,40,1,1,1,12,12,1,1,1,1,1,8};
////        int tagLen = 2;
//        int[] intlen = {4, 2, 2, 4, 2, 80, 2, 2, 2, 24, 24, 2, 2, 2, 2, 2, 16};
//        int tagLen = 4;
//        Field[] clszzs = parame.getClass().getDeclaredFields();
//        int pos = 0;
//        for (int i = 0; i < intlen.length; i++) {
//            String st = str.substring(pos + tagLen, pos + tagLen + intlen[i]);
//            pos = pos + tagLen + intlen[i];
//            clszzs[i].setAccessible(true);
//            clszzs[i].set(parame, st);
//
//        }
//        if (!TextUtils.isEmpty(new String(HEXUitl.hexToBytes(parame.s16))))
//            PrefUtil.putMerchantName(new String(HEXUitl.hexToBytes(parame.s16)));
//        Log.i("castF62", "castF62: " + parame.toString());
//        Log.i("castF62", "castF62: " + parame.s27 + "\n" + HEXUitl.bytesToHex(parame.s27.getBytes()) + "\n" + Arrays.toString(parame.s27.getBytes()) + "\n bit num " + toBitString(parame.s27));
////        String contral = toBitString(parame.s27);
//        String contral = toBit(HEXUitl.hexToBytes(parame.s27));
//        Log.i("castF62", "contral: " + contral);
//        castControl(contral);
//    }

    public static String toBit(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(getBit(bytes[i]));
        }
        return sb.toString();
    }

    public static String getBit(byte by) {
        StringBuffer sb = new StringBuffer();
        sb.append((by >> 7) & 0x1)
                .append((by >> 6) & 0x1)
                .append((by >> 5) & 0x1)
                .append((by >> 4) & 0x1)
                .append((by >> 3) & 0x1)
                .append((by >> 2) & 0x1)
                .append((by >> 1) & 0x1)
                .append((by >> 0) & 0x1);
        return sb.toString();
    }

    private static void castControl(String contral) {
        if (contral.substring(2, 3).equals("1")) {
            PrefUtil.setPre_Control(true);
        } else {
            PrefUtil.setPre_Control(false);
        }
        if (contral.substring(4, 5).equals("1")) {
            PrefUtil.setPreCancel_Control(true);
        } else {
            PrefUtil.setPreCancel_Control(false);
        }
        if (contral.substring(6, 7).equals("1")) {
            PrefUtil.setPreComplete_Control(true);
        } else {
            PrefUtil.setPreComplete_Control(false);
        }
        if (contral.substring(8, 9).equals("1")) {
            PrefUtil.setPreCompleteCancel_Control(true);
        } else {
            PrefUtil.setPreCompleteCancel_Control(false);
        }

        if (contral.substring(10, 11).equals("1")) {
            PrefUtil.setConsume_Control(true);
        } else {
            PrefUtil.setConsume_Control(false);
        }
        if (contral.substring(12, 13).equals("1")) {
            PrefUtil.setConsumeCancel_Control(true);
        } else {
            PrefUtil.setConsumeCancel_Control(false);
        }
        if (contral.substring(14, 15).equals("1")) {
            PrefUtil.setretfundControl(true);
        } else {
            PrefUtil.setretfundControl(false);
        }

    }

    private static String toBitString(String str) {

        char[] strChar = str.toCharArray();
        String result = "";
        for (int i = 0; i < strChar.length; i++) {
            result += Integer.toBinaryString(strChar[i]);
        }
        return result;
    }

    public static void doPreAuthCompleteVoid(final TransactionInfo transactionInfo, final OnTraditionListener onCommonListener) {

        FIXED_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (!IsNeedLogin(null)) {
                    onCommonListener.onError(0, "Abnormal consumption");
                    return;
                }
                byte[] bytes = null;
                try {
                    TradeInfo tradeInfo = new TradeInfo();

                    bytes = ChanelPacket.makePreAuthCompleteCancel(transactionInfo);
                    byte[] ret = CommunAction.doNet(bytes);
                    Log.v("=========", "socketaction donet ret:" + ByteUtil.bytes2HexString(ret));
                    Msg msg = ChanelPacket.decode(ret);
                    boolean insertCard = Utils.cardType == Utils.CardTypeIC_ICC && WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("05");
                    RespCode rc = msg.getReqCode();
                    tradeInfo.msg = msg;
                    tradeInfo.errorCode = rc.getCode();
                    tradeInfo.errorMsg = rc.getErrorMsg();
                    int poscount = PrefUtil.getSerialNo();
                    poscount++;
                    PrefUtil.putSerialNo(poscount);
                    if (msg.getReqCode().getFlag() == 'A') {
                        if (msg.macResult != Msg.MAC_OK) {
                            onCommonListener.onError(0, "Mac check error");
                        }

                        WeiPassGlobal.getTransactionInfo().setIssuerNo(new String(msg.body.getFieldData(44), "GB2312"));
                        tradeInfo.setTradeType(TradeInfo.Type_Auth);
                        WeiPassGlobal.getTransactionInfo().setTransaceNo(msg.body.getField(11));
                        WeiPassGlobal.getTransactionInfo().setTransTime(msg.body.getField(12));
                        WeiPassGlobal.getTransactionInfo().setTransDate(msg.body.getField(13));
                        WeiPassGlobal.getTransactionInfo().setSettleDate(msg.body.getField(15));
                        WeiPassGlobal.getTransactionInfo().setBacthNo(msg.body.getField(60).substring(2, 8));
                        WeiPassGlobal.getTransactionInfo().setReferNo(msg.body.get(37));
                        WeiPassGlobal.getTransactionInfo().setAuthNo(msg.body.get(38));
                        WeiPassGlobal.getTransactionInfo().setField60(msg.body.get(60));
                        WeiPassGlobal.getTransactionInfo().setField63(msg.body.get(63));
                        onCommonListener.onResult(tradeInfo);
                    } else {
                        onCommonListener.onError(0, msg.getField39Code());
                    }
                } catch (PayException e) {
                    if (onCommonListener != null) {
                        onCommonListener.onError(-1, e.getMessage());
                    }
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (!CommunAction.isSendSuccess) {
                        PrefUtil.putReversal(null);
                    }
                    //Abnormal transaction, please contact the administrator.
                    onCommonListener.onError(0, "Abnormal transaction, please contact the administrator.");
                }
            }
        });
    }

}
