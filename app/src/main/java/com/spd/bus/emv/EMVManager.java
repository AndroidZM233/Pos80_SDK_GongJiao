package com.spd.bus.emv;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.spd.bus.bean.TradeInfo;
import com.spd.bus.util.ByteUtil;
import com.spd.bus.util.MoneyUtil;
import com.spd.bus.util.StringUtils;
import com.spd.bus.util.TLV;
import com.spd.bus.util.TLVList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import sdk4.wangpos.libemvbinder.EmvCore;
import sdk4.wangpos.libemvbinder.EmvParam;
import wangpos.sdk4.emv.ICallbackListener;


public class EMVManager {
    private static String TAG = "checkArpc";
    private static EmvCore emv;
    private static Context context;
    private static Handler handler;
    public static void setEMVManager(Context con, Handler hand,EmvCore emvCore) {
        emv = emvCore;
        context = con;
        handler = hand;

    }
    public static int PBOC_Simple(ICallbackListener iCallbackListener) throws RemoteException {
        byte[] outData = new byte[1024];
        int[] outStaus = {1024};
        int result = -1;
        if (TradeInfo.getInstance().getPosInputType().startsWith("07")) {
            if (TradeInfo.getInstance().getTradeType() == TradeInfo.Type_KaQuanQuery) {
                result = QPBOC_PreProcess(iCallbackListener);
            }
        } else {
            emv.transInit();
            emv.getParam(outData, outStaus);
            EmvParam emvParam = new EmvParam(context);
            emvParam.parseByteArray(outData);
            emvParam.setTransType(0x02);
            Log.v("PBOC_Simple", "PBOC_Simple.TradeInfo.toString()= " + TradeInfo.getInstance().toString());
            emvParam.setMerchId(TradeInfo.getInstance().getMerchantNo());
            emvParam.setMerchName("emv");
            emvParam.setTermId(TradeInfo.getInstance().getTerminalNo());
            emvParam.setTermTransQuali("26800080");
            emvParam.setCountryCode("156");
            emvParam.setTransCurrCode("156");
            switch (TradeInfo.getInstance().getTradeType()) {
                case TradeInfo.Type_Sale:
                    if (!TradeInfo.getInstance().isOnLine()) {//如果是脱机执行
                        emvParam.setECTSI(0x01);
                    }
                    break;
                case TradeInfo.Type_CoilingSale:
                case TradeInfo.Type_QueryBalance:
                    emvParam.setForceOnline(0x01);
                    emvParam.setECTSI(0x00);
                    break;
                case TradeInfo.Type_Auth:
                case TradeInfo.Type_AuthComplete:
                case TradeInfo.Type_Cancel:
                    emvParam.setECTSI(0x00);
                    break;
                default:
                    emvParam.setECTSI(0x00);
                    break;
            }
            emv.setParam(emvParam.toByteArray());
            Log.i("PBOC_Simple", "emvCore.setParam()");
        }
        // 流水号
        int ser = TradeInfo.getInstance().getSerialNo();
        int cardType = getCardType();
        Log.v("emvCore simple", "appSel.cardType==" + cardType + "： appSel.serial==" + ser+"\niCallbackListener = "+iCallbackListener);
        result = emv.appSel(cardType, ser, iCallbackListener);
        Log.i("PBOC_Simple", "end emvCore.appSel(), result = " + result);
        if (result != 0) {
            Log.i("PBOC_Simple", "fail==" + result);
            return result;
        }
        result = setEMVConfig();
        if (result != 0)
            return result;
        result = emv.readAppData(iCallbackListener);
        Log.i("PBOC_Simple", "start emvCore.readAppData(), result = " + result);
        result = getEMVTransInfo();
        return result;
    }

    public static int EMV_OnlineProc(final byte[] callBackData, final int[] callBackLen, final CountDownLatch countDownLatch,Handler handler) throws RemoteException {
        Log.d("EMV_OnlineProc", "start" + 1);
        byte[] icData = new byte[512];
        int[] icDataLen = new int[1];

        setField55Dara(icData, 512);
        try {
            JSONObject json = new JSONObject();
            json.put("tradeType", TradeInfo.getInstance().getTradeType());
            json.put("readCradType", TradeInfo.getInstance().getPosInputType());
            json.put("cardNo", TradeInfo.getInstance().getId());
            json.put("cardSerialNo", TradeInfo.getInstance().getCardSerialNumber());
            json.put("magnetic2", TradeInfo.getInstance().getMagneticCardData2());
            json.put("cardAvailPeriod", TradeInfo.getInstance().getValidityPeriod());
            json.put("icCardData", TradeInfo.getInstance().getIcCardData());
            Log.d("EMV_OnlineProc", "EMV_OnlineProc.json.toString()==" + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //start trading
        if (TradeInfo.getInstance().getPosInputType().equals("05")) {//The verification of that result returned by the ICC transaction is proces according to different regions
            countDownLatch.countDown();
            handler.sendEmptyMessage(2);
        }else {//PICC
            countDownLatch.countDown();
            handler.sendEmptyMessage(2);
        }
        return 0;
    }

    /**
     * 插卡交易验证55域
     *
     */
    private static void checkCardInfo(byte[] outData, int[] callBackLen, String rF55, String code38, String code39) {
        byte[][] inputData = {new byte[512]};
        int pos = 0;
        Log.d("checkCardInfo_1", "rF55=="+rF55+"code38=="+code38+"code39=="+code39);
        rF55 = ByteUtil.str2HexStr(rF55);
        if (!StringUtils.isEmpty(code38)) {
            code38 = ByteUtil.str2HexStr(code38);
        }
        code39 = ByteUtil.str2HexStr(code39);

        System.arraycopy(ByteUtil.hexString2Bytes(code39), 0, inputData[0], 0, ByteUtil.hexString2Bytes(code39).length);
        pos += ByteUtil.hexString2Bytes(code39).length;
        if (code38 == null || "".equals(code38)) {
            inputData[0][pos++] = 0x00;
        } else {
            inputData[0][pos++] = (byte) ByteUtil.hexString2Bytes(code38).length;
            System.arraycopy(ByteUtil.hexString2Bytes(code38), 0, inputData[0], pos, ByteUtil.hexString2Bytes(code38).length);
            pos += ByteUtil.hexString2Bytes(code38).length;
        }
        Log.d("tc55result1", "rF55=="+rF55);
        TLVList tlvList = TLVList.fromBinary(ByteUtil.hexString2Bytes(rF55));
        TLV tlv = tlvList.getTLV("91");// 发卡行认证数据
        if (tlv == null) {
            inputData[0][pos++] = 0x00;
        } else {
            inputData[0][pos++] = (byte) tlv.getLength();
            System.arraycopy(tlv.getBytesValue(), 0, inputData[0], pos, tlv.getLength());
            pos += tlv.getLength();
        }

        TLV tlv71 = tlvList.getTLV("71");// 脚本数据
        TLV tlv72 = tlvList.getTLV("72");// 脚本数据
        byte tlv71Len = 0;
        if (tlv71 == null) {
            tlv71Len = 0x00;
        } else {
            tlv71Len = (byte) tlv71.getRawData().length;
//            System.arraycopy(tlv71.getBytesValue(), 0, inputData[0], pos, tlv71.getLength());
//            pos += tlv71.getLength();
        }
        if (tlv72 == null)
            tlv71Len += 0x00;
        else {
            tlv71Len += (byte) tlv72.getRawData().length;
        }

        inputData[0][pos++] = tlv71Len;
        if (tlv71Len != 0) {
            if(tlv71!=null) {
                System.arraycopy(tlv71.getRawData(), 0, inputData[0], pos, tlv71.getRawData().length);
                pos+=tlv71.getRawData().length;
            }
            if(tlv72!=null) {
                System.arraycopy(tlv72.getRawData(), 0, inputData[0], pos, tlv72.getRawData().length);
                pos+=tlv72.getRawData().length;
            }
        }
        System.arraycopy(inputData[0], 0, outData, 1, pos + 1);
        callBackLen[0] = pos + 1;
        Log.d("tc55result2_b", ByteUtil.bytes2HexString(outData));
    }

    private static int getCardType() {
        if (TradeInfo.getInstance().getPosInputType().startsWith("05"))
            return 0x01;
        else
            return 0x02;
    }

    /**
     * IC卡 EMV
     *
     * @return
     */
    public static int EMV_TransProcess(ICallbackListener iCallbackListener) throws RemoteException {
        boolean reversalFlag = false;
        int result = 0;
        int type = TradeInfo.getInstance().getTradeType();
        Log.i("EMV_TransProcess", "EMV_TransProcess.type.===" + type);
        switch (type) {
            case TradeInfo.Type_Sale:
            case TradeInfo.Type_CoilingSale:
            case TradeInfo.Type_QueryBalance:
            case TradeInfo.Type_Auth:
                result = emv.cardAuth();
                Log.i("EMV_TransProcess", "emvCore.cardAuth().result==" + result);
                if (result != TradeInfo.SUCCESS)
                    return result;
                break;
            default:
                break;
        }

        int resProc = -1;
        if (result == TradeInfo.SUCCESS) {
            int path = emv.getPath();
            Log.i("EMV_TransProcess", "emvCore.getPath() = " + path);
            if (path == TradeInfo.PATH_QPBOC) {
                resProc = emv.procQPBOCTrans(iCallbackListener);
                if (resProc == TradeInfo.SUCCESS) {
                    //QPBOC设置TVR|TSI 全零
                    emv.setTLV(0x95, new byte[]{0x00,0x00,0x00,0x00,0x00});
                    emv.setTLV(0x9B, new byte[]{0x00,0x00});
                    getEMVTransResult();
                }
            } else {
                Log.i("EMV_TransProcess", "start emvCore.procTrans()");
                resProc = emv.procTrans(iCallbackListener);
                if (resProc == TradeInfo.SUCCESS) {
                    getEMVTransResult();
                    saveScriptResult();
                    saveEMVTransInfo();
                }
            }
            Log.i("EMV_TransProcess", "resProc.result==" + resProc);
        }
        return resProc;
    }
    /**
     * get icCard data
     */
    private static void setField55Dara(byte[] field55Data, int length) throws RemoteException {
        byte[] outData = new byte[6];
        int[] outDataLen = new int[1];

        int result, appResult = 0;
        result = emv.getTLV(0x9F03, outData, outDataLen);
        if (result != TradeInfo.SUCCESS) {
            byte[] tlv = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            emv.setTLV(0x9F03, tlv);
        }
        outData = Arrays.copyOf(outData, outDataLen[0]);
        byte[] crypt = TradeInfo.getInstance().getAppCrypt();
        if (TextUtils.isEmpty(Arrays.toString(crypt)) && Arrays.toString(crypt).startsWith("000000")) {
            outData = new byte[8];
            outDataLen = new int[1];
            emv.getTLV(0x9F26, outData, outDataLen);
            TradeInfo.getInstance().setAppCrypt(outData);
        }
        outData = new byte[12];
        outDataLen = new int[1];
        result = emv.getTLV(0x9F02, outData, outDataLen);
        outData = Arrays.copyOf(outData, outDataLen[0]);
        if (result != TradeInfo.SUCCESS) {
            appResult = TradeInfo.ERR_END;
        }
        if (appResult == TradeInfo.ERR_END) {
            String amount = MoneyUtil.toCent(TradeInfo.getInstance().getAmount()+"");
            Log.i("setField55Data", "MoneyUtil.toCent = " + amount);
            byte[] inData = ByteUtil.ascii2Bcd(amount);
            emv.setTLV(0x9F02, inData);
        }

        byte transType = 0x00;
        switch (TradeInfo.getInstance().getTradeType()) {
            case TradeInfo.Type_QueryBalance:
                transType = 0x31;
                break;
            case TradeInfo.Type_Sale:
            case TradeInfo.Type_AuthComplete:
            case TradeInfo.Type_OffLine_Sale:
                transType = 0x00;
                break;
            case TradeInfo.Type_Auth:
                transType = 0x03;
                break;
            case TradeInfo.Type_Refund: //退货
            case TradeInfo.Type_Void://消费撤销
            case TradeInfo.Type_CompleteVoid://预授权完成撤销
                transType = 0x20;
                break;
            case TradeInfo.Type_CoilingSale://指定账户圈存
                transType = 0x60;
                break;
            default:
                break;
        }
        byte[] transT = new byte[]{transType};
        emv.setTLV(0x9C, transT);

        outData = new byte[512];
        outDataLen = new int[1];
        emv.getCoreTLVMessage(outData, outDataLen);
        outData = Arrays.copyOfRange(outData, 4, outDataLen[0]);
        Log.i("setField55Data", "outData = " + ByteUtil.bytes2HexString(outData));
        String tlvMsg = ByteUtil.fromBytes(outData);
        HashMap<String, String> map = new HashMap<String, String>();
        TLV.anaTag(outData, map);

        if(!map.containsKey("9F09"))
            map.put("9F09","0020");
        emv.getTLV(0x4F,outData,outDataLen);
        if(!map.containsKey("84"))
            map.put("84",ByteUtil.bytes2HexString(Arrays.copyOf(outData,outDataLen[0])));
        if(TradeInfo.getInstance().getTradeType()==TradeInfo.Type_Sale||
                TradeInfo.getInstance().getTradeType()==TradeInfo.Type_Auth||
                TradeInfo.getInstance().getTradeType()==TradeInfo.Type_QueryBalance
                ){
            emv.getTLV(0x9F63,outData,outDataLen);
        }
        if(!map.containsKey("9F63"))
            map.put("9F63",ByteUtil.bytes2HexString(Arrays.copyOf(outData,outDataLen[0])));
        if(TradeInfo.getInstance().getPosInputType().startsWith("07")){
            map.put("9F27","00");
            if(!map.containsKey("9F03")){
                map.put("9F03","000000000000");
            }
        }

        final String F55 = "9F26,9F27,9F10,9F37,9F36,95,9A,9C,9F02,5F2A,82,9F1A,9F03,9F33";
        String ex_title = "9F34,9F35,9F1E,91,9F63";
        final String onLineTag = ",74,8A";
        final String F55_EX = "5F24,57,5A,5F34,50,9F12,4F,9F06,84,9F09,8C,8D,9F4E,9F21,9B,9F41,EFA0,EFA1";

        if (TradeInfo.getInstance().getTradeType() == TradeInfo.Type_OffLine_Sale &&
                tlvMsg.contains("74") || tlvMsg.contains("8A"))
            ex_title += onLineTag;
        String f55 = TLV.pack(map, F55 + "," + ex_title + ","+ F55_EX);
        TradeInfo.getInstance().setIcCardData(f55);
        Log.i("setField55Data", "f55 = " + f55);
        String ex = TLV.pack(map, ex_title);
    }

    public static int QPBOC_PreProcess(ICallbackListener iCallbackListener) throws RemoteException {
        byte[] outData = new byte[1024];
        int[] outStaus = new int[1];
        emv.getParam(outData, outStaus);
        EmvParam emvParam = new EmvParam(context);
        emvParam.parseByteArray(outData);
        Log.v("QPBOC_PreProcess", "QPBOC_PreProcess.TradeInfo.toString()= " + TradeInfo.getInstance().toString());
        emvParam.setTransType(TradeInfo.getInstance().getTradeType());
        emvParam.setMerchId(TradeInfo.getInstance().getMerchantNo());
        emvParam.setMerchName("emv");
        emvParam.setTermId(TradeInfo.getInstance().getTerminalNo());
        emvParam.setTermTransQuali("26800080");
        emvParam.setCountryCode("156");
        emvParam.setTransCurrCode("156");
        emv.setParam(emvParam.toByteArray());
        int result = emv.qPBOCPreProcess(iCallbackListener);
        Log.i("send--", "emvCore.qPBOCPreProcess, result = " + result);
        return result;
    }


    private static int setEMVConfig() throws RemoteException {
        byte[] aid = new byte[17];
        int[] aidLen = new int[1];
        int result = emv.getTLV(0x4F, aid, aidLen);
        Log.i("setEMVConfig", "aid = " + ByteUtil.bytes2HexString(aid) + "\naidLen = " + aidLen[0] + ", result = " + result);
        if (result != 0)
            return -1;
        TradeInfo.getInstance().setAid(aid);
        TradeInfo.getInstance().setAidLen(aidLen[0]);
        byte[] byteParam = new byte[1024];
        int[] intParamLen = new int[]{1024};
        emv.getParam(byteParam,intParamLen);
        EmvParam emvParam = new EmvParam(context);
        emvParam.parseByteArray(byteParam);
        emv.setParam(emvParam.toByteArray());
        return result;
    }

    /**
     * get card information
     */
    private static int getEMVTransInfo() throws RemoteException {
        byte[] outData = new byte[100];
        int[] outDataLen = new int[1];
        emv.getTLV(0x5A, outData, outDataLen);
        String cardNo = ByteUtil.bytes2HexString(Arrays.copyOf(outData, outDataLen[0]));
        if (cardNo.endsWith("F"))
            cardNo = cardNo.replace("F", "");
        TradeInfo.getInstance().setId(cardNo);
        //卡序列号
        outData = new byte[100];
        outDataLen = new int[1];
        emv.getTLV(0x5F34, outData, outDataLen);
        TradeInfo.getInstance().setCardSerialNumber(String.valueOf(outData[0]));
        //磁道信息
        outData = new byte[100];
        outDataLen = new int[1];
        int result = emv.getTLV(0x57, outData, outDataLen);
        String tagTLV57 = ByteUtil.bytes2HexString(Arrays.copyOf(outData, outDataLen[0]));
        Log.d("getEMVTransInfo", "track info: tagTLV57==" + tagTLV57 + "\n" + ByteUtil.bytes2HexString(Arrays.copyOf(outData, outDataLen[0])));
        if (TextUtils.isEmpty(cardNo))
            TradeInfo.getInstance().setId(tagTLV57.split("D")[0]);
        int track2Len = tagTLV57.indexOf("F");
        if (track2Len >= 0) {
            TradeInfo.getInstance().setMagneticCardData2(tagTLV57.substring(0, track2Len));
        } else {
            TradeInfo.getInstance().setMagneticCardData2(tagTLV57);
        }
        //卡片有效期
        outData = new byte[100];
        outDataLen = new int[1];
        emv.getTLV(0x5F24, outData, outDataLen);
        if (outDataLen[0] != 0) {
            String validityPeriod = ByteUtil.bytes2HexString(Arrays.copyOf(outData, outDataLen[0])).substring(0, 4);
            TradeInfo.getInstance().setValidityPeriod(validityPeriod);
            Log.d("getEMVTransInfo", "ValidityPeriod = " + validityPeriod);
        }
        Log.d("getEMVTransInfo", "MagneticCardData2 = " + TradeInfo.getInstance().getMagneticCardData2());
        handler.sendEmptyMessage(1);
        return 0;
    }

    /**
     * 解析终端验证结果 TVR
     */
    private static void AnalyseTVRTSI() throws RemoteException {
        byte[] outData = new byte[10];
        int[] outDateLen = new int[1];
        char ucMask = 0x80;
        int result = emv.getTLV(0x9B, outData, outDateLen);
        if (result == TradeInfo.SUCCESS)
            for (int i = 8; i >= 3; i--) {
                if ((outData[0] & ucMask) > 0) {
                    switch (i) {
                        case 8:
                            Log.v("AnalyseTVRTSI", "脱机数据认证已进行");
                            break;

                        case 7:
                            Log.v("AnalyseTVRTSI", "持卡人认证已进行");
                            break;

                        case 6:
                            Log.v("AnalyseTVRTSI", "卡片风险管理已进行");
                            break;

                        case 5:
                            Log.v("AnalyseTVRTSI", "发卡行认证已进行");
                            break;

                        case 4:
                            Log.v("AnalyseTVRTSI", "终端风险管理已进行");
                            break;

                        case 3:
                            Log.v("AnalyseTVRTSI", "脚本处理已进行");
                            break;

                        //				case 2:
                        //					my_sprintf("%s", "");
                        //					break;
                        //
                        //				case 1:
                        //					my_sprintf("%s", "");
                        //					break;

                        default:
                            break;
                    }

                    Log.v("AnalyseTVRTSI", "\n");
                }
                //右移一位
                ucMask >>= 1;
            }
        outData = new byte[10];
        outDateLen = new int[1];

        result = emv.getTLV(0x95, outData, outDateLen);
        Log.v("AnalyseTVRTSI 95 result", result + "---0x95-" + ByteUtil.bytes2HexString(outData) + "--" + ByteUtil.fromUtf8(outData) + "\n" + Arrays.toString(outData));
        ucMask = 0x80;
        if (result == TradeInfo.SUCCESS) {
            for (int i = 8; i >= 3; i--) {
                if ((outData[0] & ucMask) > 0) {
                    switch (i) {
                        case 8:
                            Log.v("AnalyseTVRTSI", "未进行脱机数据认证");
                            break;

                        case 7:
                            Log.v("AnalyseTVRTSI", "脱机静态数据认证失败");
                            break;

                        case 6:
                            Log.v("AnalyseTVRTSI", "IC 卡数据缺失");
                            break;

                        case 5:
                            Log.v("AnalyseTVRTSI", "卡片出现在终端异常文件中");
                            break;

                        case 4:
                            Log.v("AnalyseTVRTSI", "脱机动态数据认证失败");
                            break;

                        case 3:
                            Log.v("AnalyseTVRTSI", "复合动态数据认证/应用密码生成失败");
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
        ucMask = 0x80;
        if (result == TradeInfo.SUCCESS) {
            for (int i = 8; i >= 4; i--) {
                if ((outData[1] & ucMask) > 0) {
                    switch (i) {
                        case 8:
                            Log.v("AnalyseTVRTSI", "IC 卡和终端应用版本不一致");
                            break;

                        case 7:
                            Log.v("AnalyseTVRTSI", "应用已过期");
                            break;

                        case 6:
                            Log.v("AnalyseTVRTSI", "应用尚未生效");
                            break;

                        case 5:
                            Log.v("AnalyseTVRTSI", "卡片产品不允许所请求的服务");
                            break;

                        case 4:
                            Log.v("AnalyseTVRTSI", "新卡");
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

        //
        Log.v("AnalyseTVRTSI 95 result", result + "");
        ucMask = 0x80;
        if (result == TradeInfo.SUCCESS) {
            for (int i = 8; i >= 3; i--) {
                if ((outData[2] & ucMask) > 0) {
                    switch (i) {
                        case 8:
                            Log.v("AnalyseTVRTSI", "持卡人验证未成功");
                            break;

                        case 7:
                            Log.v("AnalyseTVRTSI", "未知的CVM");
                            break;

                        case 6:
                            Log.v("AnalyseTVRTSI", "PIN 重试次数超限");
                            break;

                        case 5:
                            Log.v("AnalyseTVRTSI", "要求输入 PIN, 但无 PIN pad或 PIN pad故障");
                            break;

                        case 4:
                            Log.v("AnalyseTVRTSI", "要求输入 PIN, 有 PIN pad, 但未输入 PIN");
                            break;

                        case 3:
                            Log.v("AnalyseTVRTSI", "输入联机 PIN");
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
        //
        ucMask = 0x80;
        if (result == TradeInfo.SUCCESS) {
            for (int i = 8; i >= 3; i--) {
                if ((outData[3] & ucMask) > 0) {
                    switch (i) {
                        case 8:
                            Log.v("AnalyseTVRTSI", "交易超过最低限额");
                            break;

                        case 7:
                            Log.v("AnalyseTVRTSI", "超过连续脱机交易下限");
                            break;

                        case 6:
                            Log.v("AnalyseTVRTSI", "超过连续脱机交易上限");
                            break;

                        case 5:
                            Log.v("AnalyseTVRTSI", "交易被随机选择联机处理");
                            break;

                        case 4:
                            Log.v("AnalyseTVRTSI", "商户要求联机交易");
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
        ucMask = 0x80;
        if (result == TradeInfo.SUCCESS) {
            for (int i = 8; i >= 5; i--) {
                if ((outData[4] & ucMask) > 0) {
                    switch (i) {
                        case 8:
                            Log.v("AnalyseTVRTSI", "使用缺省 TDOL");
                            break;

                        case 7:
                            Log.v("AnalyseTVRTSI", "发卡行认证失败");
                            break;

                        case 6:
                            Log.v("AnalyseTVRTSI", "最后一次生成应用密码(GENERATE AC)命令之前脚本处理失败");
                            break;

                        case 5:
                            Log.v("AnalyseTVRTSI", "最后一次生成应用密码(AC)命令之后脚本处理失败");
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
     * 获取 emvCore 执行结果
     */
    private static void getEMVTransResult() throws RemoteException {
        byte[] outByteData = new byte[2];
        int[] outDataLen = new int[1];
        emv.getTLV(0x8A,outByteData,outDataLen);
        String _0x8A = ByteUtil.fromBytes(outByteData);
        if(outByteData[0]>0){
            if (_0x8A.equals(TradeInfo.ARC_OFFLINEAPPROVED))
                TradeInfo.getInstance().setICResult(TradeInfo.OFFLINEAPPROVED);//脱机成功
            else if(_0x8A.equals(TradeInfo.ARC_OFFLINEDECLINED))
                TradeInfo.getInstance().setICResult(TradeInfo.OFFLINEDECLINED);//脱机拒绝
            else if(_0x8A.equals(TradeInfo.ARC_ONLINEFAILOFFLINEAPPROVED))
                TradeInfo.getInstance().setICResult(TradeInfo.UNABLEONLINE_OFFLINEAPPROVED);//联机失败脱机成功
            else if(_0x8A.equals(TradeInfo.ARC_ONLINEFAILOFFLINEDECLINED))
                TradeInfo.getInstance().setICResult(TradeInfo.UNABLEONINE_OFFLINEDECLINED);//联机失败，脱机拒绝
            else if(_0x8A.equals("00"))
                TradeInfo.getInstance().setICResult(TradeInfo.ONLINEAPPROVED);//联机成功
            else if(_0x8A.equals("01"));
//				if(false) //需要改
//					TradeInfo.getInstance().setICResult(TradeInfo.ONLINEAPPROVED);// 联机成功
//				else
//					TradeInfo.getInstance().setICResult(TradeInfo.ONLINEDECLINED);//联机失败
            else
                TradeInfo.getInstance().setICResult(TradeInfo.ONLINE_FAILED);//联机拒绝

        }
        //终端验证结果
        emv.getTLV(0x95, TradeInfo.getInstance().getTVR(),outDataLen);
        // tsi
        emv.getTLV(0x9B, TradeInfo.getInstance().getTSI(),outDataLen);
    }

    /**
     * 保存脚本
     */
    private static int saveScriptResult() throws RemoteException {
        Log.v("saveScriptResult","保存脚本");
        byte[] outData = new byte[256];
        int[]  outDataLen = new int[1];
        int result = emv.getScriptResult(outData,outDataLen);
        Log.e("emvCore", "getScriptResult: "+result);
        if(result== TradeInfo.SUCCESS){
            emv.setTLV(0xDF31,outData);
            //保存脚本上送报文  需要改
        }
        return result;
    }

    /**
     * emvCore 交易是否成功，并保存交易信息
     */
    private static int saveEMVTransInfo() throws RemoteException {
        int EmvAppResult = TradeInfo.SUCCESS;
        if(TradeInfo.getInstance().getICResult()== TradeInfo.OFFLINEDECLINED|| TradeInfo.getInstance().getICResult()== TradeInfo.UNABLEONLINE_OFFLINEAPPROVED){
            if(TradeInfo.getInstance().getTradeType()== TradeInfo.Type_QueryBalance){

            }else {
                TradeInfo.getInstance().setTradeType(TradeInfo.Type_OffLine_Sale);
//                getAIDAndCardType();
            }
        }
        byte[]  outData = new byte[512];
        int[] outDataLen = new int[1];
        emv.getCoreTLVMessage(outData,outDataLen);
        outData = Arrays.copyOf(outData,outDataLen[0]);
        String icData= ByteUtil.bytes2HexString(outData);
//                HEXUitl.bytesToHex(outData);
        if(TradeInfo.getInstance().getTradeType()==TradeInfo.Type_Sale||
                TradeInfo.getInstance().getTradeType()==TradeInfo.Type_Auth||
                TradeInfo.getInstance().getTradeType()==TradeInfo.Type_QueryBalance
                ){
            //建行
            emv.getTLV(0x9F63,outData,outDataLen);
        }

        if(!icData.contains(ByteUtil.bytes2HexString(Arrays.copyOf(outData,outDataLen[0]))))
            icData+=ByteUtil.bytes2HexString(Arrays.copyOf(outData,outDataLen[0]));
        Log.i(TAG, "saveEMVTransInfo: "+icData);
//        icData+="9F4104"+TradeInfo.getInstance().getSerialNo();
//        tradeCallBackListener.onCheckTLV55(icData);
        switch (TradeInfo.getInstance().getICResult()){
            case TradeInfo.ONLINEAPPROVED :
            case TradeInfo.UNABLEONLINE_OFFLINEAPPROVED:
                EmvAppResult= TradeInfo.SUCCESS ;
                break;
            case TradeInfo.OFFLINEAPPROVED:
                byte[] field55Data = new byte[10]; // 需要改
                setField55Dara(field55Data,field55Data.length);
                EmvAppResult= TradeInfo.SUCCESS ;
                break;
            default:
                EmvAppResult= TradeInfo.CANCEL ;
                break;
        }
        //电子现金
        outData = new byte[100];
        outDataLen = new int[1];
//		emv.getTLV(0x9F5D,outData,outDataLen);
//		TradeInfo.getInstance().setEC_Balance(ByteUtil.bytes2HexString(outData));
        //应用密文
        int ret = emv.getTLV(0x9F26, TradeInfo.getInstance().getAppCrypt(),outDataLen);
        Log.i(TAG, ret +"saveEMVTransInfo: 应用密文 9F26 getAppCrypt"+ByteUtil.bytes2HexString(TradeInfo.getInstance().getAppCrypt())+
                "\n outdata"+ByteUtil.bytes2HexString(Arrays.copyOf(outData,outDataLen[0])));
        //终端性能
//		emv.EmvLib_GetTLV(0x9F33,WeiPassGlobal.getTransactionInfo().getAppCrypt(),outDataLen);
        //应用标识符 aid
        ret =emv.getTLV(0x4F, TradeInfo.getInstance().getAid(),outDataLen);
        TradeInfo.getInstance().setAidLen(outDataLen[0]);
        Log.i(TAG, ret +"saveEMVTransInfo: 应用标识符 0x4F getAid"+ByteUtil.bytes2HexString(TradeInfo.getInstance().getAid())+
                "\n outdata"+ByteUtil.bytes2HexString(Arrays.copyOf(outData,outDataLen[0])));
        // 终端验证结果 TVR 0x95
        ret = emv.getTLV(0x95, TradeInfo.getInstance().getTVR(),outDataLen);
        Log.i(TAG, ret +"saveEMVTransInfo: 终端验证结果 0x95 getTVR"
                +ByteUtil.bytes2HexString(TradeInfo.getInstance().getTVR())+
                "\n outdata"+ByteUtil.bytes2HexString(Arrays.copyOf(outData,outDataLen[0])));
        // TSI 0x9B
        emv.getTLV(0x9B, TradeInfo.getInstance().getTSI(),outDataLen);
        Log.i(TAG, ret +"saveEMVTransInfo: TSI 0x9B getTSI"
                +ByteUtil.bytes2HexString(TradeInfo.getInstance().getTSI())+
                "\n outdata"+ByteUtil.bytes2HexString(Arrays.copyOf(outData,outDataLen[0])));
        // 应用交易序号CVM 9F36
        ret = emv.getTLV(0x9F36, TradeInfo.getInstance().getATC(),outDataLen);
        Log.i(TAG, ret +"saveEMVTransInfo: 应用交易序号 0x9F36 getATC"
                +ByteUtil.bytes2HexString(TradeInfo.getInstance().getATC())+
                "\n outdata"+ByteUtil.bytes2HexString(Arrays.copyOf(outData,outDataLen[0])));
        // 持卡人验证方法结果ATC 9F36
        ret =emv.getTLV(0x9F34, TradeInfo.getInstance().getCVM(),outDataLen);
        Log.i(TAG, ret +"saveEMVTransInfo: 持卡人验证方法结果 9F34 getCVM"
                +ByteUtil.bytes2HexString(TradeInfo.getInstance().getCVM())+
                "\n outdata"+ByteUtil.bytes2HexString(Arrays.copyOf(outData,outDataLen[0])));
        // 应用标签 ATC 50
        ret =emv.getTLV(0x50, TradeInfo.getInstance().getAucAppLabel(),outDataLen);
        Log.i(TAG, ret +"saveEMVTransInfo: 应用标签 0x50 getAucAppLabel"
                +ByteUtil.bytes2HexString(TradeInfo.getInstance().getAucAppLabel())+
                "\n outdata"+ByteUtil.bytes2HexString(Arrays.copyOf(outData,outDataLen[0])));
        //应用首选名 9F12
        ret =emv.getTLV(0x9F12, TradeInfo.getInstance().getAucAppPreferName(),outDataLen);
//		WeiPassGlobal.getTransactionInfo().setAucAppLabel(Arrays.copyOf(WeiPassGlobal.getTransactionInfo().getAucAppLabel(),outDataLen[0]));
        Log.i(TAG, ret +"saveEMVTransInfo: 应用首选名 0x9F12 getAucAppPreferName"
                +ByteUtil.bytes2HexString(TradeInfo.getInstance().getAucAppPreferName())+
                "\n outdata"+new String(TradeInfo.getInstance().getAucAppLabel()));
        //应用失效日期 有效期 5F24
//		outData = new byte[4];
//		emv.getTLV(0x5F24,outData,outDataLen);
//		TradeInfo.getInstance().setExpireDate(HEXUitl.bytesToHex(outData));
        if(TradeInfo.getInstance().getTradeType()== TradeInfo.Type_Sale&&
                (!TradeInfo.getInstance().isOnLine()||
                        TradeInfo.getInstance().getTradeType()== TradeInfo.Type_OffLine_Sale)){
            // 不可预知数 9F37
            emv.getTLV(0x9F37, TradeInfo.getInstance().getAucUnPredNum(),outDataLen);
            //应用交互特征  82
            emv.getTLV(0x82, TradeInfo.getInstance().getAucAIP(),outDataLen);
            //发卡行数据
            emv.getTLV(0x9F10, TradeInfo.getInstance().getAucCVR(),outDataLen);
        }
        // 获取授权码 89
//		emv.EmvLib_GetTLV(0x9F10,TradeInfo.getInstance().getAucCVR(),outDataLen);
        AnalyseTVRTSI();
        return 0; // 需要根据gettlv 结果进行返回
    }
}
