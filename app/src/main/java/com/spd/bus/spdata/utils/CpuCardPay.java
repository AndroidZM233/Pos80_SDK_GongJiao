package com.spd.bus.spdata.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.spd.base.utils.Datautils;
import com.spd.bus.MyApplication;
import com.spd.bus.spdata.been.PsamBeen;
import com.spd.bus.util.TLV;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wangpos.sdk4.libbasebinder.BankCard;

public class CpuCardPay {
    private String TAG = "speedata_bus";
    private long ltime = 0;

    private BankCard mBankCard = MyApplication.getBankCardInstance();
    private int retvalue = -1;
    private int[] resplen = new int[1];

    /**
     * 微智接口返回数据
     */
    private byte[] respdata = new byte[512];


    /**
     * //获取PSAM卡终端机编号指令
     */
    private final byte[] PSAN_GET_ID = {0x00, (byte) 0xB0, (byte) 0x96, 0x00, 0x06};
    /**
     * //交通部
     */
    private final byte[] PSAM_SELECT_DIR = {0x00, (byte) 0xA4, 0x00, 0x00, 0x02, (byte) 0x80, 0x11};

    /**
     * //读取psam卡17文件
     */
    private final byte[] PSAM_GET_17FILE = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x01};

    /**
     * //选择PPSE支付环境
     */
    private final byte[] SELEC_PPSE = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0e, 0x32, 0x50, 0x41, 0x59, 0x2e, 0x53, 0x59, 0x53, 0x2e,
            0x44, 0x44, 0x46, 0x30, 0x31};
    /**
     * //选择电子钱包应用
     */
    private final byte[] SELECT_ICCARD_QIANBAO = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, (byte) 0xA0, 0x00, 0x00, 0x06, 0x32, 0x01, 0x01, 0x05};

//    /**
//     * psam 初始化流程
//     */
//    private void psam1Init() {
//        try {
//            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PSAM1, 60, respdata, resplen, "app1");
//            if (retvalue != 0) {
//
//                return false;
//            }
//            Log.d(TAG, "===交通部切换psam===" + Datautils.byteArrayToString(Datautils.cutBytes(respdata, 0, resplen[0])));
//            if (respdata[0] == (byte) 0x01) {
//                Log.e(TAG, "交通部psam初始化 读卡失败,请检查是否插入psam卡 " + Datautils.byteArrayToString(respdata));
//
//                return false;
//            } else if (respdata[0] == (byte) 0x05) {
//                //IC卡已经插入
//                byte[] resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, PSAN_GET_ID);
//                if (resultBytes == null || resultBytes.length == 2) {
//                    Log.e(TAG, "===交通部获取16文件错误===" + Datautils.byteArrayToString(resultBytes));
//
//                    return false;
//                }
//                Log.d(TAG, "===交通部获取16文件===" + Datautils.byteArrayToString(resultBytes));
//                //终端机编号
//                deviceCode = resultBytes;
//                Log.d(TAG, "====交通部PSAM卡终端机编号==== " + Datautils.byteArrayToString(deviceCode));
//
//                resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, PSAM_SELECT_DIR);
//                if (resultBytes == null || resultBytes.length == 2) {
//                    Log.e(TAG, "===交通部(8011)错误===" + Datautils.byteArrayToString(resultBytes));
//
//                    return false;
//                }
//                Log.d(TAG, "===交通部(8011)===" + Datautils.byteArrayToString(resultBytes));
//                resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, PSAM_GET_17FILE);
//                if (resultBytes == null || resultBytes.length == 2) {
//                    Log.e(TAG, "===交通部获取17文件错误===" + Datautils.byteArrayToString(resultBytes));
//
//                    return false;
//                }
//                Log.d(TAG, "===交通部获取17文件===" + Datautils.byteArrayToString(resultBytes));
//                psamKey = Datautils.cutBytes(resultBytes, 0, 1);
//                Log.d(TAG, "===交通部秘钥索引=== " + Datautils.byteArrayToString(psamKey) + "\n" + "PSAM初始化成功！！！请读消费卡\n");
//                psamDatas.add(new PsamBeen(1, deviceCode, psamKey));
//                // TODO: 2018/12/4  初始化成功等待读消费卡
//            } else {
//                Log.e(TAG, "交通部psam初始化失败 " + Datautils.byteArrayToString(Datautils.cutBytes(respdata, 0, resplen[0])));
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//
//        }
//    }

    /**
     * 封装接口自定义
     *
     * @param cardType 卡片类型
     * @param sendApdu 发送指令
     * @return 结果
     */
//    private byte[] sendApdus(int cardType, byte[] sendApdu) {
//        byte[] reBytes = null;
//        //微智接口返回数据
//        byte[] respdata = new byte[512];
//        //微智接口返回数据长度
//        int[] resplen = new int[1];
//        try {
//            int retvalue = mBankCard.sendAPDU(cardType, sendApdu, sendApdu.length, respdata, resplen);
//            if (retvalue != 0) {
//                mBankCard.breakOffCommand();
//                Log.e(TAG, "微智接口返回错误码" + retvalue);
//                return reBytes;
//            }
//            if (!Arrays.equals(APDU_RESULT_SUCCESS, Datautils.cutBytes(respdata, resplen[0] - 2, 2))) {
//                mBankCard.breakOffCommand();
//                return Datautils.cutBytes(respdata, resplen[0] - 2, 2);
//            }
//            reBytes = Datautils.cutBytes(respdata, 0, resplen[0] - 2);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//            try {
//                mBankCard.breakOffCommand();
//            } catch (RemoteException e1) {
//                e1.printStackTrace();
//            }
//        }
//        return reBytes;
//    }

//    private boolean CpuPay() {
//        ltime = System.currentTimeMillis();
//        Log.d(TAG, "===start--消费记录3031send=== " + Datautils.byteArrayToString(SELEC_PPSE));
//        byte[] resultBytes = sendApdus(BankCard.CARD_MODE_PICC, SELEC_PPSE);
//        if (resultBytes == null) {
//            Log.d(TAG, "===消费记录3031error=== " + Datautils.byteArrayToString(resultBytes));
//            return false;
//        } else if (Arrays.equals(resultBytes, APDU_RESULT_FAILE)) {
//            // TODO: 2019/1/3  查数据库黑名单报语音
//        }
//        List<String> listTlv = new ArrayList<>();
//        TLV.anaTagSpeedata(resultBytes, listTlv);
//        //获取交易时间
//        systemTime = Datautils.getDateTime();
//        if (listTlv.contains("A000000632010105")) {
//            Log.d(TAG, "test: 解析到TLV发送0105 send：" + SELECT_ICCARD_QIANBAO);
//            //选择电子钱包应用
//            resultBytes = sendApdus(BankCard.CARD_MODE_PICC, SELECT_ICCARD_QIANBAO);
//            if (resultBytes == null || resultBytes.length == 2) {
//
//                Log.e(TAG, "===解析到TLV发送0105 return===" + Datautils.byteArrayToString(resultBytes));
//                return false;
//            }
//        } else {
//            Log.d(TAG, "===默认发送0105send===" + Datautils.byteArrayToString(SELECT_ICCARD_QIANBAO));
//            //选择电子钱包应用
//            resultBytes = sendApdus(BankCard.CARD_MODE_PICC, SELECT_ICCARD_QIANBAO);
//            if (resultBytes == null) {
//                Log.e(TAG, "icExpance: 获取交易时间错误");
//
//                return false;
//            }
//        }
//        Log.d(TAG, "===0105 return===" + Datautils.byteArrayToString(resultBytes));
//        Log.d(TAG, "===读15文件send===" + Datautils.byteArrayToString(READ_ICCARD_15FILE));
//        //读应用下公共应用基本信息文件指令
//        resultBytes = sendApdus(BankCard.CARD_MODE_PICC, READ_ICCARD_15FILE);
//        if (resultBytes == null || resultBytes.length == 2) {
//            Log.e(TAG, "===读15文件error===" + Datautils.byteArrayToString(resultBytes));
//
//            return false;
//        }
//        Log.d(TAG, "===IC读15文件 === retur:" + Datautils.byteArrayToString(resultBytes));
//        System.arraycopy(resultBytes, 12, cardId, 0, 8);
//        System.arraycopy(resultBytes, 0, file15_8, 0, 8);
//        System.arraycopy(resultBytes, 2, city, 0, 2);
//        Log.d(TAG, "===卡应用序列号 ===" + Datautils.byteArrayToString(cardId));
//        //读17文件
//        byte[] IC_READ17_FILE = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x00};
//        Log.d(TAG, "===读17文件00b0send===" + Datautils.byteArrayToString(IC_READ17_FILE));
//        resultBytes = sendApdus(BankCard.CARD_MODE_PICC, IC_READ17_FILE);
//        if (resultBytes == null || resultBytes.length == 2) {
//            Log.e(TAG, "===读17文件error===" + Datautils.byteArrayToString(resultBytes));
//
//            return false;
//        }
//        Log.d(TAG, resplen[0] + "===IC读17文件return===" + Datautils.byteArrayToString(resultBytes));
//        Log.d(TAG, "===IC读1E文件 00b2send===00B201F400");
//        resultBytes = sendApdus(BankCard.CARD_MODE_PICC, Datautils.HexString2Bytes("00B201F400"));
//        if (resultBytes == null || resultBytes.length == 2) {
//            Log.e(TAG, "===IC读1E文件error===" + Datautils.byteArrayToString(resultBytes));
//
//            return false;
//        }
//        Log.d(TAG, "===IC读1E文件00b2return===" + Datautils.byteArrayToString(resultBytes));
//
//
//        Log.d(TAG, "===IC余额(805c)send===  805C030204");
//        resultBytes = sendApdus(BankCard.CARD_MODE_PICC, Datautils.HexString2Bytes("805C030204"));
//        if (resultBytes == null || resultBytes.length == 2) {
//            Log.e(TAG, "===IC卡初始化(8050)error===" + Datautils.byteArrayToString(resultBytes));
//
//            return false;
//        }
//        Log.d(TAG, "===IC余额(805c)return===" + Datautils.byteArrayToString(resultBytes));
//
//        byte[] INIT_IC_FILE = initICcard();
//        Log.d(TAG, "===IC卡初始化(8050)send===" + Datautils.byteArrayToString(INIT_IC_FILE));
//        resultBytes = sendApdus(BankCard.CARD_MODE_PICC, INIT_IC_FILE);
//        if (resultBytes == null || resultBytes.length == 2) {
//            Log.e(TAG, "===IC卡初始化(8050)error===" + Datautils.byteArrayToString(resultBytes));
//
//            return false;
//        }
//        System.arraycopy(resultBytes, 0, blance, 0, 4);
//        System.arraycopy(resultBytes, 4, ATC, 0, 2);
//        System.arraycopy(resultBytes, 6, keyVersion, 0, 4);
//        flag = resultBytes[10];
//        System.arraycopy(resultBytes, 11, rondomCpu, 0, 4);
//        Log.d(TAG, "===IC卡初始化(8050)return=== " + Datautils.byteArrayToString(resultBytes) + "\n" +
//                "===余额:" + Datautils.byteArrayToString(blance) + "\n" +
//                "===CPU卡脱机交易序号:  " + Datautils.byteArrayToString(ATC) + "\n" +
//                "===密钥版本 : " + (int) flag + "\n" + "===随机数 : " + Datautils.byteArrayToString(rondomCpu));
//        byte[] psam_mac1 = initSamForPurchase();
//        Log.d(TAG, "===获取MAC1(8070)send===" + Datautils.byteArrayToString(psam_mac1));
//        resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, psam_mac1);
//        if (resultBytes == null || resultBytes.length == 2) {
//            Log.e(TAG, "===获取MAC1(8070)error===" + Datautils.byteArrayToString(resultBytes));
//
//            return false;
//        }
//        Log.d(TAG, "===获取MAC18070return===" + Datautils.byteArrayToString(resultBytes));
//        praseMAC1(resultBytes);
//        //80dc
//        String ss = "80DC00F030060000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
//        Log.d(TAG, "===更新1E文件(80dc)send===" + ss);
//        resultBytes = sendApdus(BankCard.CARD_MODE_PICC, Datautils.HexString2Bytes(ss));
//        if (resultBytes == null || resultBytes.length == 2) {
//            Log.e(TAG, "===更新1E文件(80dc)error===" + Datautils.byteArrayToString(resultBytes));
//
//            return false;
//        }
//        Log.d(TAG, "===更新1E文件return===" + Datautils.byteArrayToString(resultBytes));
//
//
//        byte[] cmd = getIcPurchase();
//        Log.d(TAG, "===IC卡(8054)消费send===" + Datautils.byteArrayToString(cmd));
//        resultBytes = sendApdus(BankCard.CARD_MODE_PICC, cmd);
//        if (resultBytes == null || resultBytes.length == 2) {
//            Log.e(TAG, "===IC卡(8054)消费error===" + Datautils.byteArrayToString(resultBytes));
//
//            return false;
//        }
//        Log.d(TAG, "===IC卡(8054)消费返回===" + Datautils.byteArrayToString(resultBytes));
//        byte[] mac2 = Datautils.cutBytes(resultBytes, 0, 8);
//        byte[] PSAM_CHECK_MAC2 = checkPsamMac2(mac2);
//        Log.d(TAG, "===psam卡 8072校验 send===: " + Datautils.byteArrayToString(PSAM_CHECK_MAC2));
//        resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, PSAM_CHECK_MAC2);
//        if (resultBytes == null) {
//            Log.e(TAG, "===psam卡(8072)校验error===");
//            return false;
//        }
//        Log.d(TAG, "===psam卡 8072校验返回===: " + Datautils.byteArrayToString(resultBytes));
//        handler.sendMessage(handler.obtainMessage(1, Datautils.byteArrayToInt(blance)));
//        Log.i("stw", "===消费结束===" + (System.currentTimeMillis() - ltime));
//        return true;
//    }

}
