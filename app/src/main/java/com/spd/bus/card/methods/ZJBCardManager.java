package com.spd.bus.card.methods;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.google.gson.Gson;
import com.spd.base.been.tianjin.CardBackBean;
import com.spd.base.been.tianjin.CardRecord;
import com.spd.base.db.DbDaoManage;
import com.spd.base.utils.Datautils;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.been.tianjin.TCardOpDU;
import com.spd.base.utils.FileUtils;
import com.spd.bus.Info;
import com.spd.bus.MyApplication;
import com.spd.bus.card.utils.DateUtils;
import com.spd.bus.card.utils.LogUtils;
import com.spd.bus.spdata.been.PsamBeen;
import com.spd.bus.spdata.utils.PlaySound;
import com.spd.bus.spdata.utils.TimeDataUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import wangpos.sdk4.libbasebinder.BankCard;

/**
 * 交通部卡
 * Created by 张明_ on 2019/2/18.
 * Email 741183142@qq.com
 */
public class ZJBCardManager {
    private static final Object LOCK = new Object();
    private static ZJBCardManager jtbCardManager;
    private TCardOpDU tCardOpDU;
    private final int METHOD_OK = 99;
    private byte[] problemIssueCode;
    //是否恢复
    private boolean fSysSta = false;
    //是否黑名单
    private boolean fBlackCard = false;
    private RunParaFile runParaFile;
    private int ulHCUTC;
    private byte[] ulDevUTCByte;
    private List<PsamBeen> psamBeenList;
    private Context context;

    public static ZJBCardManager getInstance() {
        if (jtbCardManager == null) {
            synchronized (LOCK) {
                jtbCardManager = new ZJBCardManager();
            }
        }
        return jtbCardManager;
    }


    public CardBackBean mainMethod(Context context, BankCard mBankCard, List<PsamBeen> psamBeenList) {
        this.context = context;
        long ltime = System.currentTimeMillis();
        LogUtils.v("ZJB开始");
        tCardOpDU = new TCardOpDU();
        tCardOpDU.cardClass = (byte) 0x03;
        this.psamBeenList = psamBeenList;
        byte[] resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                , new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x00, (byte) 0x00, (byte) 0x02,
                        (byte) 0x3f, (byte) 0x00});
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===3f00error===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
        }

        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                , new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x09,
                        (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03,
                        (byte) 0x86, (byte) 0x98, (byte) 0x07, (byte) 0x01});
        if (Arrays.equals(resultBytes, CardMethods.APDU_RESULT_FAILE_6283) ||
                Arrays.equals(resultBytes, CardMethods.APDU_RESULT_FAILE_6A81)) {
            // TODO: 2019/1/3  查数据库黑名单报语音
            return new CardBackBean(ReturnVal.CAD_BL1, tCardOpDU);
        } else if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===0701error===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
        }

        tCardOpDU.ucCardClass = CardMethods.ZJBCARD;
        //系统时间
        byte[] systemTime = Datautils.getDateTime();
        tCardOpDU.ucDateTime = systemTime;
        Long timeToLong = DateUtils.convertTimeToLong(DateUtils.FORMAT_yyyyMMddHHmmss
                , Datautils.byteArrayToString(tCardOpDU.ucDateTime));
        String ulUTCString = Long.toHexString(timeToLong / 1000).toUpperCase();
        tCardOpDU.ucDateTimeUTC = Datautils.HexString2Bytes(ulUTCString);
        tCardOpDU.ucCheckDate = new byte[]{(byte) 0x20, (byte) 0x99, (byte) 0x12, (byte) 0x31};
        tCardOpDU.ucOtherCity = (byte) 0x00;
        //***Read 15 File  读应用下公共应用基本信息文件指令
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                , CardMethods.READ_ICCARD_15FILE);
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===读15文件error===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
        }

        setDataStartUcIssuerCode(resultBytes);
        tCardOpDU.ucFile15Top8 = Datautils.concatAll(tCardOpDU.ucIssuerCode, tCardOpDU.ucCityCode
                , tCardOpDU.ucVocCode, tCardOpDU.ucRfu1);
        byte[] ucDateTimeByte = Datautils.cutBytes(tCardOpDU.ucDateTime, 0, 4);
        int ucDateTimeInt = Integer.parseInt(Datautils.byteArrayToString(ucDateTimeByte));
        int ucAppStartDateInt = Integer.parseInt(Datautils.byteArrayToString(tCardOpDU.ucAppStartDate));
        //当前时间小于开始时间
        if (ucDateTimeInt < ucAppStartDateInt) {
            return new CardBackBean(ReturnVal.CAD_SELL, tCardOpDU);
        }

        if (Arrays.equals(tCardOpDU.ucCityCode, CardMethods.ucCityCode))//判断城市代码
        {
            tCardOpDU.ucOtherCity = (byte) 0x00;
        } else {
            tCardOpDU.ucOtherCity = (byte) 0x01;
            if (tCardOpDU.ucAppTypeFlag == (byte) 0x00) {
                return new CardBackBean(ReturnVal.CAD_SELL, tCardOpDU);            //未启用互联互通
            }
        }

        if (tCardOpDU.ucOtherCity != (byte) 0x00) {
            byte[] ucCheckSnr = Datautils.concatAll(new byte[]{(byte) 0x02}, tCardOpDU.ucAppSnr,
                    new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});
        }

        tCardOpDU.ucMainCardType = (byte) 0x00;
        tCardOpDU.ucSubCardType = (byte) 0x00;//DBDat[10];
        setDataStartUcMainCardType(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x20, (byte) 0x99
                , (byte) 0x12, (byte) 0x31, (byte) 0x01, (byte) 0x64, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
                , (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});

        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                , new byte[]{(byte) 0x00, (byte) 0xB0, (byte) 0x85, (byte) 0x00, (byte) 0x12});
        if (Arrays.equals(resultBytes, CardMethods.APDU_RESULT_FAILE_6A82)) {

        } else if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===0012error===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
        } else if (resultBytes.length == 18) {
            setDataStartUcMainCardType(resultBytes);
        }

        if ((tCardOpDU.ucMainCardType == (byte) 0x90) && (tCardOpDU.ucOtherCity == (byte) 0x00)) {
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                    , new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x09,
                            (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03
                            , (byte) 0x86, (byte) 0x98, (byte) 0x07, (byte) 0x03});
            if (resultBytes.length == 2 && !Arrays.equals(resultBytes, CardMethods.APDU_RESULT_FAILE3_6300)) {
                LogUtils.e("===0703error===" + Datautils.byteArrayToString(resultBytes));
                return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
            }

            RunParaFile runParaFile = new RunParaFile();
            String busNr = SharedXmlUtil.getInstance(context).read(Info.BUS_NO, "");
            if (TextUtils.isEmpty(busNr)) {
                runParaFile.setBusNr(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00});
            } else {
                runParaFile.setBusNr(Datautils.HexString2Bytes(busNr));
            }

            //测试 正式时要取SN后八位
            byte[] hexString2Bytes = Datautils.HexString2Bytes(Info.POS_ID_INIT);
            runParaFile.setDevNr(hexString2Bytes);
            for (int i = 1; i < 10; i++) {
                if (i == 6) {
                    continue;
                }
                String dBCmd = "00B20" + i + "3400";
                resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                        , Datautils.hexStringToByteArray(dBCmd));
                if (resultBytes == null || resultBytes.length == 2) {
                    LogUtils.e("===3400error===" + Datautils.byteArrayToString(resultBytes));
                    return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
                }

                if (i == 1) {
                    runParaFile.setAreaNr(new byte[]{resultBytes[12]});
                    runParaFile.setVocNr(new byte[]{resultBytes[13]});
                    runParaFile.setCorNr(new byte[]{resultBytes[14]});
                    runParaFile.setTeamNr(new byte[]{resultBytes[15], resultBytes[16]});
                    runParaFile.setLineNr(new byte[]{resultBytes[17], resultBytes[18]});
                } else if (i == 2) {
                    runParaFile.setUcBusYuePower(new byte[]{resultBytes[3], resultBytes[4], resultBytes[5]});
                    runParaFile.setUcBusPurPower(new byte[]{resultBytes[6], resultBytes[7], resultBytes[8]});
                    runParaFile.setUcCpuYuePower(new byte[]{resultBytes[9], resultBytes[10], resultBytes[11]});
                    runParaFile.setUcCpuPurPower(new byte[]{resultBytes[12], resultBytes[13], resultBytes[14]});
                    runParaFile.setUcCityYuePower(new byte[]{resultBytes[15], resultBytes[16]});
                    runParaFile.setUcCityMainPurPower(new byte[]{resultBytes[17], resultBytes[18]});
                    runParaFile.setUcCitySubPurPower(new byte[]{resultBytes[19], resultBytes[20]});
                } else if (i == 3) {
                    //基本票价
                    runParaFile.setKeyV1(new byte[]{resultBytes[3], resultBytes[4]});
                    runParaFile.setKeyV2(new byte[]{resultBytes[5], resultBytes[6]});
                    runParaFile.setKeyV3(new byte[]{resultBytes[7], resultBytes[8]});
                    runParaFile.setKeyV4(new byte[]{resultBytes[9], resultBytes[10]});
                    runParaFile.setKeyV5(new byte[]{resultBytes[11], resultBytes[12]});
                    runParaFile.setKeyV6(new byte[]{resultBytes[13], resultBytes[14]});
                    runParaFile.setKeyV7(new byte[]{resultBytes[15], resultBytes[16]});
                    runParaFile.setKeyV8(new byte[]{resultBytes[17], resultBytes[18]});
                } else if (i == 4) {
                    runParaFile.setUcBusYueTimeLimit(new byte[]{resultBytes[3]});
                    runParaFile.setUcBusPurTimeLimit(new byte[]{resultBytes[4]});
                    runParaFile.setUcCpuYueTimeLimit(new byte[]{resultBytes[5]});
                    runParaFile.setUcCpuPurTimeLimit(new byte[]{resultBytes[6]});
                    runParaFile.setUcCpuYueTimeLimit(new byte[]{resultBytes[7]});
                    runParaFile.setUcCityPurTimeLimit(new byte[]{resultBytes[8]});
                    runParaFile.setUcOldCardTimeLimit(new byte[]{resultBytes[9]});
                    runParaFile.setUcCardCheckDays(new byte[]{resultBytes[10]});
                    runParaFile.setUcAddValueLowP(new byte[]{resultBytes[11], resultBytes[12]});
                } else if (i == 5) {
                    runParaFile.setUcBusRadioP(Datautils.cutBytes(resultBytes, 3, 24));
                    runParaFile.setUcCpuRadioP(Datautils.cutBytes(resultBytes, 27, 24));
                    runParaFile.setUcCityMainRadioP(Datautils.cutBytes(resultBytes, 51, 16));
                    runParaFile.setUcCitySubRadioP(Datautils.cutBytes(resultBytes, 67, 16));

                } else if (i == 7) {
                    runParaFile.setUcTransferCtrl(new byte[]{resultBytes[3]});
                    runParaFile.setUcTransferCntLimit(new byte[]{resultBytes[4]});
                    runParaFile.setUcTransferTimeLong(new byte[]{resultBytes[5], resultBytes[6]});
                    runParaFile.setUcTransferPower(new byte[]{resultBytes[7], resultBytes[8], resultBytes[9]});
                    runParaFile.setUcTransfer1stRadioYue(new byte[]{resultBytes[10]});
                    runParaFile.setUcTransfer2ndRadioYue(new byte[]{resultBytes[11]});
                    runParaFile.setUcTransferMulRadioYue(new byte[]{resultBytes[12]});
                    runParaFile.setUcTransfer1stRadioPur(new byte[]{resultBytes[13]});
                    runParaFile.setUcTransfer2ndRadioPur(new byte[]{resultBytes[14]});
                    runParaFile.setUcTransferMulRadioPur(new byte[]{resultBytes[15]});
                } else if (i == 8) {
                    runParaFile.setUcJamTime(Datautils.cutBytes(resultBytes, 3, 8));
                    runParaFile.setUcAirDate(Datautils.cutBytes(resultBytes, 11, 8));
                } else if (i == 9) {
                } else if (i == 10) {
                }
            }

            //保存信息
            DbDaoManage.getDaoSession().getRunParaFileDao().deleteAll();
            DbDaoManage.getDaoSession().getRunParaFileDao().insert(runParaFile);
            Gson gson = new Gson();
            String toJson = gson.toJson(runParaFile);
            FileUtils.writeFile(Environment.getExternalStorageDirectory() + "/card.txt"
                    , toJson, false);
            return new CardBackBean(ReturnVal.CAD_SETCOK, tCardOpDU);
        }


        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession()
                .getRunParaFileDao().loadAll();
        if (runParaFiles == null) {
            return new CardBackBean(ReturnVal.CODE_PLEASE_SET, tCardOpDU);
        }
        runParaFile = runParaFiles.get(0);
        //判断启用标志
        if (tCardOpDU.ucAppStartFlag == 0x00) {
            return new CardBackBean(ReturnVal.CAD_SELL, tCardOpDU);
        }

//        ulDevUTC = BCDTimeToUTC(clockString);

        if (fSysSta) {
            byte[] ucAppSnr = Datautils.cutBytes(tCardOpDU.ucAppSnr, 2, 8);
            byte[] problemIssue2 = Datautils.cutBytes(problemIssueCode, 2, 8);
            //不是同一张卡
            if (!Arrays.equals(ucAppSnr, problemIssue2)) {
                fSysSta = false;
            } else {
                byte[] dBCmd;
                if (problemIssueCode[12] == (byte) 0x07) {
                    dBCmd = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04,
                            (byte) 0x00, (byte) 0x09, (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00
                            , (byte) 0x03, (byte) 0x86, (byte) 0x98, (byte) 0x07, (byte) 0x02};
                    resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, dBCmd);
                    if (resultBytes == null || resultBytes.length == 2) {
                        return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
                    }
                } else {

                    dBCmd = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04,
                            (byte) 0x00, (byte) 0x09, (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00
                            , (byte) 0x03, (byte) 0x86, (byte) 0x98, (byte) 0x07, (byte) 0x01};
                    resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, dBCmd);
                    if (resultBytes == null || resultBytes.length == 2) {
                        return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
                    }
                }
                System.arraycopy(new byte[]{(byte) 0x80, (byte) 0x5a, (byte) 0x00, (byte) 0x00, (byte) 0x02}
                        , 0, dBCmd, 0, 5);
                tCardOpDU.uiOffLineCount = Datautils.cutBytes(problemIssueCode, 10, 2);
                char uiOffLineCount = (char) (Datautils.byteToChar(tCardOpDU.uiOffLineCount) + 1);
                dBCmd[5] = (byte) ((uiOffLineCount) >> 8);
                dBCmd[6] = (byte) uiOffLineCount;
                dBCmd[7] = (byte) 0x08;
                dBCmd[3] = problemIssueCode[21] == (byte) 0x00 ? (byte) 0x06 : (byte) 0x09;
//                byte[] dBCmd = Datautils.concatAll(new byte[]{(byte) 0x80, (byte) 0x5a, (byte) 0x00}, value3
//                        , new byte[]{(byte) 0x02}, tCardOpDU.uiOffLineCount, new byte[]{(byte) 0x08});
                resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, dBCmd);
                if (resultBytes == null || resultBytes.length == 2) {
                    LogUtils.e("===恢复error===" + Datautils.byteArrayToString(resultBytes));
                    fSysSta = false;
                } else {
                    tCardOpDU.ucMAC2 = Datautils.cutBytes(resultBytes, 0, 4);
                    tCardOpDU.ucTAC = Datautils.cutBytes(resultBytes, 4, 4);
                    byte[] psamCheckMac2 = CardMethods.checkPsamMac2(tCardOpDU.ucMAC2);
                    resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM2_APDU, psamCheckMac2);
                    if (resultBytes == null || resultBytes.length == 2) {
                        LogUtils.e("===psam卡(8072)校验error===");
                        return new CardBackBean(ReturnVal.CAD_MAC2, tCardOpDU);
                    }
                    fSysSta = false;
                    tCardOpDU.ucProcSec = problemIssueCode[12];
                    // ucProcSec=2代表钱包消费
                    if (tCardOpDU.ucProcSec == (byte) 0x02) {
                        tCardOpDU.ucRcdType = (byte) 0x00;
                    } else {
                        tCardOpDU.ucRcdType = (byte) 0x02;
                    }
//                    tCardOpDU.ucRcdType = tCardOpDU.ucRcdType & 0xfe;

//                    OnAppendRecordTrade(tCardOpDu.ucRcdType);//写记录
                    CardMethods.onAppendRecordTrade(context, tCardOpDU.ucProcSec == 2 ? (byte) 0x00 : (byte) 0x02
                            , tCardOpDU, runParaFile, psamBeenList, mBankCard);

                    return new CardBackBean(ReturnVal.CAD_OK, tCardOpDU);
                }
            }
        }
        tCardOpDU.ucPSAMPOS = (byte) 0x02;
        tCardOpDU.ucDiv = (byte) 0x02;
        tCardOpDU.ucCAPP = (byte) 0x00;
        tCardOpDU.ucKeyID = psamBeenList.get(1).getKeyID();
        tCardOpDU.ucPOSSnr = psamBeenList.get(1).getTermBumber();
        tCardOpDU.ucProcSec = tCardOpDU.ucOtherCity == (byte) 0x00 ? (byte) 0x07 : (byte) 0x02;
        int fOldDisable = 0;
        boolean findRecord = false;

        //读记录
        byte[] rcdbuffer = new byte[128];
        if (MyApplication.cardRecordList.size() != 0) {
            for (int j = 0; j < MyApplication.cardRecordList.size(); j++) {
                CardRecord cardRecord = MyApplication.cardRecordList.get(j);
                if (cardRecord == null) {
                    continue;
                }
                byte[] recordByte = cardRecord.getRecord();
                byte[] snr = Datautils.cutBytes(recordByte, 7, 4);
                if (Arrays.equals(snr, tCardOpDU.snr)) {
                    rcdbuffer = recordByte;
                    findRecord = true;
                    break;
                }
            }
        }

//        LogUtils.d("查库开始" + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));
//        long dbCount = DbDaoManage.getDaoSession().getCardRecordDao().count();
//        if (dbCount > 0) {
//            int count = 20;
//            if (dbCount < 20L) {
//                count = (int) dbCount;
//            }
//            for (int j = 0; j < count; j++) {
//                CardRecord cardRecord = DbDaoManage.getDaoSession().getCardRecordDao()
//                        .loadByRowId(dbCount - j);
//                byte[] recordByte = cardRecord.getRecord();
//                byte[] snr = Datautils.cutBytes(recordByte, 7, 4);
//                if (Arrays.equals(snr, tCardOpDU.snr)) {
//                    rcdbuffer = recordByte;
//                    findRecord = true;
//                    break;
//                }
//            }
//        }
//        LogUtils.d("查库结束" + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));

        if (findRecord) {
            if (rcdbuffer[2] == (byte) 0x00 || rcdbuffer[2] == (byte) 0x02) {

                //上次刷卡时间
//                byte[] busLastBytes = Datautils.cutBytes(rcdbuffer, 24, 7);
//                String busLastStr = Datautils.byteArrayToString(busLastBytes);
                byte[] busLastBytes = Datautils.cutBytes(rcdbuffer, 24, 4);
                long string16ToLong = Datautils.parseString16ToLong(Datautils
                        .byteArrayToString(busLastBytes)) * 1000;
                Date utcToLocal = DateUtils.transferLongToDate("yyyyMMddHHmmss", string16ToLong);
                // 连续刷卡限制时间
                int time = Datautils.byteArrayToInt(runParaFile.getUcCityYueTimeLimit());
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                try {
                    Date busLast = utcToLocal;
                    Date dateTime = format.parse(Datautils.byteArrayToString(tCardOpDU.ucDateTime));
                    Date busLastAfter = new Date(busLast.getTime() + time * 60000);
                    Date dateTimeAfter = new Date(dateTime.getTime() + time * 60000);
                    int compare = dateTime.compareTo(busLastAfter);
                    int compare1 = busLast.compareTo(dateTimeAfter);
                    if (compare < 0 && compare1 < 0) {
                        tCardOpDU.ucProcSec = 2; //5分钟后，且已经正常消费过，消费钱包
                        tCardOpDU.fPermit = 1;
                    }
                    time = Datautils.byteArrayToInt(runParaFile.getUcOldCardTimeLimit());

//                    busLast = format.parse(busLastStr);
                    dateTime = format.parse(Datautils.byteArrayToString(tCardOpDU.ucDateTime));
                    busLastAfter = new Date(busLast.getTime() + time * 60000);
                    dateTimeAfter = new Date(dateTime.getTime() + time * 60000);
                    compare = dateTime.compareTo(busLastAfter);
                    compare1 = busLast.compareTo(dateTimeAfter);
                    if ((tCardOpDU.ucMainCardType == 0x01 || tCardOpDU.ucMainCardType == 0x11) &&
                            (compare < 0 && compare1 < 0)) {
                        fOldDisable = 1;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
                }

            }
        }


        if (tCardOpDU.ucProcSec != (byte) 0x02) {
            int ret = CardMethods.fIsUseYue(tCardOpDU, runParaFile);//判断月票权限
            if (ret == 1)        //月票有效
            {
                resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                        , new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x09,
                                (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03
                                , (byte) 0x86, (byte) 0x98, (byte) 0x07, (byte) 0x02});
                if (resultBytes == null || resultBytes.length == 2) {
                    LogUtils.e("===980702error===" + Datautils.byteArrayToString(resultBytes));
                    return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
                } else if (Arrays.equals(resultBytes, CardMethods.APDU_RESULT_FAILE_6A82)) {
                    tCardOpDU.ucProcSec = 2;
                } else {

                    resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                            , new byte[]{(byte) 0x00, (byte) 0xB0, (byte) 0x95, (byte) 0x00, (byte) 0x00});
                    if (resultBytes == null || resultBytes.length == 2) {
                        LogUtils.e("===B0950000error===" + Datautils.byteArrayToString(resultBytes));
                        return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
                    }

                    ret = CardMethods.cpuCardGetYueBasePos(systemTime, tCardOpDU, resultBytes);
                    if (ret == 0) {
                        tCardOpDU.ucProcSec = 2;
                    }
                }

            } else {
                tCardOpDU.ucProcSec = 2;
            }
        }
        LogUtils.i("===消费结束===" + (System.currentTimeMillis() - ltime));
        return CPUPurse(mBankCard);

    }

    //本次天津不用
    public CardBackBean CPUPurse(BankCard mBankCard) {
        byte[] resultBytes;
        if (tCardOpDU.ucProcSec == 2) {
            int ret = CardMethods.fIsUsePur(tCardOpDU, runParaFile);    //判断钱包权限

            if (ret == 0)        //没有权限
            {
                tCardOpDU.purorimoneyInt = 0;
                tCardOpDU.pursubInt = 0;
                return new CardBackBean(ReturnVal.CAD_EMPTY, tCardOpDU);
            }

            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                    , new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x09,
                            (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03
                            , (byte) 0x86, (byte) 0x98, (byte) 0x07, (byte) 0x01});
            if (resultBytes == null || resultBytes.length == 2) {
                LogUtils.e("===980701error===" + Datautils.byteArrayToString(resultBytes));
                return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
            }
        }
        tCardOpDU.fUseHC = 0;

        if ((tCardOpDU.ucOtherCity == 0) && (tCardOpDU.ucProcSec == 2) && (tCardOpDU.fPermit == 0)) {
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                    , new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x09,
                            (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03
                            , (byte) 0x86, (byte) 0x98, (byte) 0x07, (byte) 0x01});
            if (resultBytes.length == 2 && resultBytes == null) {
                LogUtils.e("===980701error===" + Datautils.byteArrayToString(resultBytes));
                return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
            }
            //读取17上条复合记录命令48字节
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                    , new byte[]{(byte) 0x00, (byte) 0xB2, (byte) 0x01, (byte) 0xB8, (byte) 0x00});
            if (Arrays.equals(resultBytes, CardMethods.APDU_RESULT_FAILE_6A82)) {
            } else if (resultBytes == null || resultBytes.length == 2) {
                LogUtils.e("===980701error===" + Datautils.byteArrayToString(resultBytes));
                return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
            } else {
                System.arraycopy(resultBytes, 0, tCardOpDU.ucDatInCard, 0, 30);
            }
//            if (CardMethods.fIsUseHC(tCardOpDU, runParaFile) == 1)//换乘权限判断
//            {
//                tCardOpDU.fUseHC = 1;
//                tCardOpDU.ucCAPP = (byte) 0x01;
//                byte[] utcBytes = Datautils.cutBytes(tCardOpDU.ucDatInCard, 24, 4);
//                String utcHexString = Datautils.byteArrayToString(utcBytes);
//                String ucDateTime = Datautils.byteArrayToString(tCardOpDU.ucDateTime);
//                Date ucHCDateTime = TimeDataUtils.utcToLocal(utcHexString);
//                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//                int compareTo = 0;
//                Date parse = null;
//                try {
//                    parse = format.parse(ucDateTime);
//                    compareTo = ucHCDateTime.compareTo(parse);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
//                //记录时间
//                String ds1 = sdf.format(ucHCDateTime);
//                //现在时间
//                String ds2 = sdf.format(parse);
//
//                ulHCUTC = Integer.parseInt(ds1);
//                int ulDevUTC = Integer.parseInt(ds2);
//                ulDevUTCByte = Datautils.hexStringToByteArray(ds2);
//                if (TimeDataUtils.isSameDay(ucHCDateTime, parse))//同一天
//                {
//                    byte[] ucJamTime = runParaFile.getUcJamTime();
//                    String ucJamTimeStr1 = Datautils.byteArrayToString(Datautils
//                            .cutBytes(ucJamTime, 0, 2));
//                    String ucJamTimeStr2 = Datautils.byteArrayToString(Datautils
//                            .cutBytes(ucJamTime, 2, 2));
//                    String ucJamTimeStr3 = Datautils.byteArrayToString(Datautils
//                            .cutBytes(ucJamTime, 4, 2));
//                    String ucJamTimeStr4 = Datautils.byteArrayToString(Datautils
//                            .cutBytes(ucJamTime, 6, 2));
//
//                    //记录和现在的时候 分别和runParaFile里面存的时间作比较
//                    if ((ulHCUTC - Integer.parseInt(ucJamTimeStr1) >= 0) &&
//                            ((ulHCUTC - Integer.parseInt(ucJamTimeStr2) < 0))) {
//                        if ((ulDevUTC - Integer.parseInt(ucJamTimeStr1) >= 0) &&
//                                ((ulDevUTC - Integer.parseInt(ucJamTimeStr2) < 0))) {
//                            tCardOpDU.fHCSeg = 1;
//                        } else {
//                            tCardOpDU.fHCSeg = 0;
//                        }
//                    } else if ((ulHCUTC - Integer.parseInt(ucJamTimeStr3) >= 0) &&
//                            ((ulHCUTC - Integer.parseInt(ucJamTimeStr4) < 0)))    //高峰时间
//                    {
//                        if ((ulDevUTC - Integer.parseInt(ucJamTimeStr3) >= 0) &&
//                                ((ulDevUTC - Integer.parseInt(ucJamTimeStr4) < 0)))    //高峰时间
//                        {
//                            tCardOpDU.fHCSeg = 2;
//                        } else {
//                            tCardOpDU.fHCSeg = 0;
//                        }
//                    } else {
//                        tCardOpDU.fHCSeg = 0;
//                    }
//                } else {
//                    tCardOpDU.fHCSeg = 0;
//                }
//                if (tCardOpDU.fHCSeg == 0) {
//                    ulHCUTC = ulDevUTC;
//                    tCardOpDU.fHC = 0;
//                } else {
//                    byte[] ucTransferCntLimit = runParaFile.getUcTransferCntLimit();
//                    byte[] ucTransferTimeLong = runParaFile.getUcTransferTimeLong();
//                    if (ulDevUTC < ulHCUTC + Integer.parseInt(Datautils.byteArrayToString(ucTransferTimeLong))) {
//                        tCardOpDU.fHC = Integer.parseInt(Datautils.byteArrayToString(new byte[]{tCardOpDU.ucDatInCard[28]}));
//                        if (tCardOpDU.fHC < 255) {
//                            tCardOpDU.fHC++;
//                        }
//                        if (tCardOpDU.fHC > Integer.parseInt(Datautils.byteArrayToString(ucTransferCntLimit))) {
//                            tCardOpDU.fUseHC = 0;
//                            tCardOpDU.ucCAPP = 0;
//                        }
//                    } else {
//                        ulHCUTC = ulDevUTC;
//                        tCardOpDU.fHC = 0;
//                    }
//                }
//            }
        }//End 本地钱包本地城市第一次消费

        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                , new byte[]{(byte) 0x80, (byte) 0x5c, (byte) 0x00, (byte) 0x02, (byte) 0x04});
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===805cerror===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
        }
        int actRemaining, i;
        for (actRemaining = 0, i = 0; i < 4; i++) {
            actRemaining <<= 8;
            actRemaining += resultBytes[i] & 0xFF;
        }

        if (tCardOpDU.ucProcSec != 2)//月票
        {
            tCardOpDU.actYueOriMoney = actRemaining;
            tCardOpDU.yueSub = 1;

            int uiYear;
            int ucMonth;
            int ulHi, ulLo;
            int money = tCardOpDU.actYueOriMoney;
            int yueSub = tCardOpDU.yueSub;
            int to16Int = Datautils.btyeTo16Int(tCardOpDU.yueUsingDate[0]);
            int yueTo16Int = Datautils.btyeTo16Int(tCardOpDU.yueUsingDate[1]);
            uiYear = 2000 + (to16Int & 0xFF);
            ucMonth = yueTo16Int & 0xFF;
            ulHi = ((~uiYear) & 0x0fff);
            ulHi <<= 4;
            ulHi += ((~ucMonth) & 0x0f);
            ulHi <<= 8;
            ulLo = ulHi;
            int yueBase = Datautils.byteArrayToInt(tCardOpDU.yueBase);
            ulHi += yueBase;

            if (money <= ulLo)   // 月票计数器调整
            {
                tCardOpDU.yueOriMoney = 0;
                tCardOpDU.actYueSub = yueSub;
            } else if (money <= ulHi) {
                tCardOpDU.yueOriMoney = money - ulLo;
                tCardOpDU.actYueSub = yueSub;
            } else {
                tCardOpDU.yueOriMoney = yueBase;
                tCardOpDU.actYueSub = money - ulHi + yueSub;
            }

            if (tCardOpDU.yueOriMoney > CardMethods.MAXVALUE) {
                return new CardBackBean(ReturnVal.CAD_BROKEN, tCardOpDU);
            }
            if (tCardOpDU.yueOriMoney < tCardOpDU.yueSub) {
                tCardOpDU.ucProcSec = 2;
                return CPUPurse(mBankCard);
            }
        } else {
            if (tCardOpDU.pursubInt <= 0) {
                tCardOpDU.pursubInt = 200;
            }

            tCardOpDU.purorimoneyInt = actRemaining;

            tCardOpDU.pursubInt = CardMethods.getRadioPurSub(tCardOpDU, runParaFile);
            tCardOpDU.pursub = Datautils.intToByteArray1(tCardOpDU.pursubInt);

            if (tCardOpDU.purorimoneyInt > CardMethods.MAXVALUE) {
                return new CardBackBean(ReturnVal.CAD_BROKEN, tCardOpDU);
            }
            if (tCardOpDU.purorimoneyInt < tCardOpDU.pursubInt) {
                return new CardBackBean(ReturnVal.CAD_EMPTY, tCardOpDU);
            }
        }


        //Read Data
        tCardOpDU.ucSimple = 1;    //单票制
        tCardOpDU.ucLastTradeEnd = 1; //上次交易完成
        tCardOpDU.ucTradeType = 6;

        //获取用户卡安全认证识别码指令
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                , new byte[]{(byte) 0x80, (byte) 0xca, (byte) 0x00, (byte) 0x00, (byte) 0x09});
        if (resultBytes.length == 2 && resultBytes == null) {
            LogUtils.e("===0009error===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
        }
        tCardOpDU.ucSafeAuthCode = Datautils.cutBytes(resultBytes, 0, 9);
        //PSAM验证安全认证识别码指令
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM2_APDU
                , Datautils.concatAll(new byte[]{(byte) 0x80, (byte) 0xca, (byte) 0x00, (byte) 0x00, (byte) 0x09}
                        , tCardOpDU.ucSafeAuthCode));
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===PSAM验证安全认证识别码指令error===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_PSAM_ERROR, tCardOpDU);
        }

        tCardOpDU.ulTradeValue = tCardOpDU.ucProcSec == (byte) 0x02 ? tCardOpDU.pursubInt : tCardOpDU.actYueSub;
        tCardOpDU.ucRcdType = tCardOpDU.ucProcSec == (byte) 0x02 ? (byte) 0x00 : (byte) 0x02;

        byte[] first;
        if (tCardOpDU.fUseHC == 1) {
            first = new byte[]{(byte) 0x80, (byte) 0x50, (byte) 0x03, (byte) 0x02, (byte) 0x0b};
        } else {
            first = new byte[]{(byte) 0x80, (byte) 0x50, (byte) 0x01, (byte) 0x02, (byte) 0x0b};
        }

        byte[] ulTradeValue = Datautils.intToByteArray1(tCardOpDU.ulTradeValue);
        byte[] dBCmd = Datautils.concatAll(first, tCardOpDU.ucKeyID, ulTradeValue
                , tCardOpDU.ucPOSSnr, new byte[]{(byte) 0x0f});
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                , dBCmd);
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===error===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
        }
        //805003020B01000000010000010000060F
        tCardOpDU.ulBalanceByte = Datautils.cutBytes(resultBytes, 0, 4);
        tCardOpDU.ulBalance = Datautils.byteArrayToInt(tCardOpDU.ulBalanceByte);
        tCardOpDU.uiOffLineCount = Datautils.cutBytes(resultBytes, 4, 2);
        tCardOpDU.ucKeyVer = resultBytes[9];
        tCardOpDU.ucKeyAlg = resultBytes[10];
        tCardOpDU.rondomCpu = Datautils.cutBytes(resultBytes, 11, 4);


        byte[] psamMac1 = CardMethods.initSamForPurchase(tCardOpDU);
        LogUtils.d("===获取MAC1(8070)send===" + Datautils.byteArrayToString(psamMac1));
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM2_APDU, psamMac1);
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===获取MAC1(8070)error===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_PSAM_ERROR, tCardOpDU);
        }
        LogUtils.d("===获取MAC18070return===" + Datautils.byteArrayToString(resultBytes));
        if (resultBytes.length <= 2) {
            LogUtils.e("===获取MAC1失败===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_PSAM_ERROR, tCardOpDU);
        }
        tCardOpDU.ulPOSTradeCountByte = Datautils.cutBytes(resultBytes, 0, 4);
        tCardOpDU.ulPOSTradeCount = Datautils.byteArrayToInt(tCardOpDU.ulPOSTradeCountByte);
        tCardOpDU.ucMAC1 = Datautils.cutBytes(resultBytes, 4, 4);


        ////////////////////////////////////////////
        if ((tCardOpDU.ucCAPP == (byte) 0x01) || (tCardOpDU.fUseHC == 1)) {
            byte[] bytesFirst = Datautils.concatAll(new byte[]{(byte) 0x80, (byte) 0xDC, (byte) 0x01, (byte) 0xB8
                            , (byte) 0x30, (byte) 0x01, (byte) 0x2E, (byte) 0x00, (byte) 0x01, (byte) 0x00}
                    , tCardOpDU.ucPOSSnr, ulDevUTCByte);
            byte[] bytesSecond = new byte[6];
            if (tCardOpDU.ucProcSec == (byte) 0x02) {
                bytesSecond[0] = (byte) (tCardOpDU.purorimoneyInt >> 16);
                bytesSecond[1] = (byte) (tCardOpDU.purorimoneyInt >> 8);
                bytesSecond[2] = (byte) tCardOpDU.purorimoneyInt;
                bytesSecond[3] = (byte) (tCardOpDU.pursubInt >> 16);
                bytesSecond[4] = (byte) (tCardOpDU.pursubInt >> 8);
                bytesSecond[5] = (byte) tCardOpDU.pursubInt;
            } else {
                bytesSecond[0] = (byte) (tCardOpDU.yueOriMoney >> 16);
                bytesSecond[1] = (byte) (tCardOpDU.yueOriMoney >> 8);
                bytesSecond[2] = (byte) tCardOpDU.yueOriMoney;
                bytesSecond[3] = (byte) (tCardOpDU.yueSub >> 16);
                bytesSecond[4] = (byte) (tCardOpDU.yueSub >> 8);
                bytesSecond[5] = (byte) tCardOpDU.yueSub;
            }

            byte[] bytesThird = Datautils.concatAll(new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x00}
                    , Datautils.intToByteArray1(ulHCUTC), Datautils.intToByteArray1(tCardOpDU.fHC)
                    , new byte[]{tCardOpDU.ucHCRadioP},
                    Datautils.cutBytes(tCardOpDU.ucDatInCard, 0, 18));
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                    , Datautils.concatAll(bytesFirst, bytesSecond, bytesThird));
            if (resultBytes == null || resultBytes.length == 2) {
                LogUtils.e("===更新1E文件(80dc)error===" + Datautils.byteArrayToString(resultBytes));
                return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
            }

        }

        byte[] cmd = CardMethods.getIcPurchase(tCardOpDU);
        LogUtils.d("===IC卡(8054)消费send===" + Datautils.byteArrayToString(cmd));
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, cmd);
        if (Arrays.equals(resultBytes, CardMethods.APDU_8054_FAILE)) {
            return new CardBackBean(ReturnVal.CAD_MAC1, tCardOpDU);
        }
        if (resultBytes == null || resultBytes.length == 2) {
//        if (resultBytes != null) {
            byte[] value = new byte[8];
            value[0] = (byte) (tCardOpDU.ulTradeValue >> 24);
            value[1] = (byte) (tCardOpDU.ulTradeValue >> 16);
            value[2] = (byte) (tCardOpDU.ulTradeValue >> 8);
            value[3] = (byte) tCardOpDU.ulTradeValue;
            value[4] = (byte) ((tCardOpDU.purorimoneyInt) >> 24);
            value[5] = (byte) ((tCardOpDU.purorimoneyInt) >> 16);
            value[6] = (byte) ((tCardOpDU.purorimoneyInt) >> 8);
            value[7] = (byte) (tCardOpDU.purorimoneyInt);
            LogUtils.e("===IC卡(8054)消费error===" + Datautils.byteArrayToString(resultBytes));
            fSysSta = true;
            problemIssueCode = Datautils.concatAll(tCardOpDU.ucAppSnr, tCardOpDU.uiOffLineCount,
                    new byte[]{tCardOpDU.ucProcSec}, value, new byte[]{tCardOpDU.ucCAPP});
            CardMethods.onAppendRecordTrade(context, tCardOpDU.ucProcSec == 2 ? (byte) 0x01 : (byte) 0x03
                    , tCardOpDU, runParaFile, psamBeenList, mBankCard);
            return new CardBackBean(ReturnVal.CAD_RETRY, tCardOpDU);
        }

        LogUtils.d("===IC卡(8054)消费返回===" + Datautils.byteArrayToString(resultBytes));
        tCardOpDU.ucTAC = Datautils.cutBytes(resultBytes, 0, 4);
        tCardOpDU.ucMAC2 = Datautils.cutBytes(resultBytes, 4, 4);
        byte[] psamCheckMac2 = CardMethods.checkPsamMac2(tCardOpDU.ucMAC2);
        LogUtils.d("===psam卡 8072校验 send===: " + Datautils.byteArrayToString(psamCheckMac2));
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM2_APDU, psamCheckMac2);
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===psam卡(8072)校验error===");
            return new CardBackBean(ReturnVal.CAD_PSAM_ERROR, tCardOpDU);
        }
        LogUtils.d("===psam卡 8072校验返回===: " + Datautils.byteArrayToString(resultBytes));
        CardMethods.onAppendRecordTrade(context, tCardOpDU.ucProcSec == 2 ? (byte) 0x00 : (byte) 0x02
                , tCardOpDU, runParaFile, psamBeenList, mBankCard);
        LogUtils.v("ZJB结束");
        return new CardBackBean(ReturnVal.CAD_OK, tCardOpDU);
    }


    public void setDataStartUcIssuerCode(byte[] DBDat) {
        tCardOpDU.ucIssuerCode = Datautils.cutBytes(DBDat, 0, 2);
        tCardOpDU.ucCityCode = Datautils.cutBytes(DBDat, 2, 2);
        tCardOpDU.ucVocCode = Datautils.cutBytes(DBDat, 4, 2);
        tCardOpDU.ucRfu1 = Datautils.cutBytes(DBDat, 6, 2);
        tCardOpDU.ucAppTypeFlag = Datautils.cutBytes(DBDat, 8, 1)[0];
        tCardOpDU.ucAppVer = Datautils.cutBytes(DBDat, 9, 1)[0];
        tCardOpDU.ucAppSnr = Datautils.cutBytes(DBDat, 10, 10);
        tCardOpDU.ucAppStartDate = Datautils.cutBytes(DBDat, 20, 4);
        tCardOpDU.ucAppEndDate = Datautils.cutBytes(DBDat, 24, 4);
        tCardOpDU.ucRfu2 = Datautils.cutBytes(DBDat, 28, 2);
    }


    public void setDataStartUcMainCardType(byte[] DBDat) {
        tCardOpDU.ucMainCardType = DBDat[0];
        tCardOpDU.ucSubCardType = DBDat[1];
        tCardOpDU.ucCardAppFlag = DBDat[2];
        tCardOpDU.ucCheckDate = Datautils.cutBytes(DBDat, 3, 4);
        tCardOpDU.ucAppStartFlag = DBDat[7];
        tCardOpDU.ucRadioInCard = DBDat[8];
        tCardOpDU.uiValidDays = Datautils.cutBytes(DBDat, 9, 2);
        tCardOpDU.ucTimeLimitInCard = DBDat[10];
        tCardOpDU.ucRfu3 = Datautils.cutBytes(DBDat, 11, 6);
    }
}
