package com.spd.bus.card.methods;

import android.content.Context;
import android.util.Log;

import com.spd.base.been.tianjin.CardRecord;
import com.spd.base.been.tianjin.CardRecordDao;
import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.Datautils;
import com.spd.base.been.tianjin.CardBackBean;
import com.spd.base.been.tianjin.TCardOpDU;
import com.spd.bus.MyApplication;
import com.spd.bus.card.utils.DateUtils;
import com.spd.bus.card.utils.LogUtils;
import com.spd.bus.spdata.been.PsamBeen;
import com.spd.bus.spdata.utils.PlaySound;
import com.spd.bus.util.TLV;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import wangpos.sdk4.libbasebinder.BankCard;

import static com.spd.bus.card.methods.CardMethods.JTBCARD;

/**
 * 交通部卡
 * Created by 张明_ on 2019/2/18.
 * Email 741183142@qq.com
 */
public class JTBCardManager {
    private static final Object LOCK = new Object();
    private static JTBCardManager jtbCardManager;
    private TCardOpDU tCardOpDU;
    private final int METHOD_OK = 99;
    private byte[] problemIssueCode;
    //是否恢复
    private boolean fSysSta = false;
    //是否黑名单
    private boolean fBlackCard = false;
    private RunParaFile runParaFile;
    private int purDisable = 0;
    private int fOldDisable = 0;
    private List<PsamBeen> psamBeenList;
    private Context context;

    public static JTBCardManager getInstance() {

        if (jtbCardManager == null) {
            synchronized (LOCK) {
                jtbCardManager = new JTBCardManager();
            }
        }
        return jtbCardManager;
    }


    public CardBackBean mainMethod(Context context, BankCard mBankCard, List<PsamBeen> psamBeenList) throws Exception {
        this.context = context;
        LogUtils.v("JTB开始");
        this.psamBeenList = psamBeenList;
        long ltime = System.currentTimeMillis();
        tCardOpDU = new TCardOpDU();
        tCardOpDU.cardClass = (byte) 0x07;
        //复合交易
        tCardOpDU.ucCAPP = 1;
        tCardOpDU.ucTradeType = (byte) 0x06;
        CardBackBean first = getFirst(mBankCard, psamBeenList);
        if (first.getBackValue() != METHOD_OK) {
            return first;
        }
        int snr = getSnr(mBankCard);
        if (snr != METHOD_OK) {
            return new CardBackBean(snr, tCardOpDU);
        }

        int fSysSta = doFSysSta(mBankCard);
        if (fSysSta != METHOD_OK) {
            return new CardBackBean(fSysSta, tCardOpDU);
        }
        CardBackBean cardBackBean = consumption(mBankCard, psamBeenList);
        LogUtils.v("JTB结束");
        return cardBackBean;
    }

    public CardBackBean getFirst(BankCard mBankCard, List<PsamBeen> psamBeenList) {
        LogUtils.d("First");
        byte[] resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, CardMethods.SELEC_PPSE);
        LogUtils.d("First 1");
        if (resultBytes == null || resultBytes.length == 2) {
            return mustToSend(mBankCard, psamBeenList);
        } else {
            List<String> listTlv = new ArrayList<>();
            TLV.anaTagSpeedata(resultBytes, listTlv);
            if (listTlv.contains("A000000632010105")) {
                //选择电子钱包应用
                resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, CardMethods.SELECT_ICCARD_QIANBAO);
                if (resultBytes == null || resultBytes.length == 2) {
                    return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
                }
            } else {
                return mustToSend(mBankCard, psamBeenList);

            }
        }
        return new CardBackBean(METHOD_OK, tCardOpDU);
    }

    private CardBackBean mustToSend(BankCard mBankCard, List<PsamBeen> psamBeenList) {
        byte[] resultBytes;//选择电子钱包应用
        LogUtils.d("First 2");
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, CardMethods.SELECT_ICCARD_QIANBAO);
        LogUtils.d("First 3");
        if (resultBytes == null) {
            return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
        } else if (Arrays.equals(resultBytes, CardMethods.APDU_RESULT_FAILE_6283) ||
                Arrays.equals(resultBytes, CardMethods.APDU_RESULT_FAILE_6284) ||
                Arrays.equals(resultBytes, CardMethods.APDU_RESULT_FAILE3_9303)) {
            // TODO: 2019/1/3  查数据库黑名单报语音
            return new CardBackBean(ReturnVal.CAD_BL1, tCardOpDU);
        } else {
            LogUtils.d("First end");
            return ZJBCardManager.getInstance().mainMethod(context, mBankCard, psamBeenList);
        }
    }

    /**
     * 交通部读15 17文件
     *
     * @return
     */
    public int getSnr(BankCard mBankCard) {
        byte[] resultBytes;
        tCardOpDU.cardClass = (byte) 0x07;
        tCardOpDU.fInBus = (byte) 0x01;
        //系统时间
        byte[] systemTime = Datautils.getDateTime();
        tCardOpDU.ucDateTime = systemTime;
        Long timeToLong = DateUtils.convertTimeToLong(DateUtils.FORMAT_yyyyMMddHHmmss
                , Datautils.byteArrayToString(tCardOpDU.ucDateTime));
        String ulUTCString = Long.toHexString(timeToLong / 1000).toUpperCase();
        tCardOpDU.ucDateTimeUTC = Datautils.HexString2Bytes(ulUTCString);
        tCardOpDU.ucCardClass = JTBCARD;
        tCardOpDU.ucCheckDate = new byte[]{(byte) 0x20, (byte) 0x99, (byte) 0x12, (byte) 0x31};
        tCardOpDU.ucOtherCity = (byte) 0x00;

        //***Read 15 File  读应用下公共应用基本信息文件指令
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                , CardMethods.READ_ICCARD_15FILE);
        if (resultBytes == null || resultBytes.length == 2) {
            return ReturnVal.CAD_READ;
        }
        setDataStartUcIssuerCode(resultBytes);
        tCardOpDU.ucFile15Top8 = Datautils.concatAll(tCardOpDU.ucIssuerCode, tCardOpDU.ucCityCode
                , tCardOpDU.ucVocCode, tCardOpDU.ucRfu1);
        if (tCardOpDU.ucAppVer == (byte) 0xff) {
            return ReturnVal.CAD_EXPIRE;
        }
        //本地卡类型  00普通卡 01敬老卡 11残疾人卡
        tCardOpDU.ucMainCardType = resultBytes[28];
        tCardOpDU.ucSubCardType = (byte) 0x00;
        //代码翻译
        //memset(ucCheckSnr, 0, 10);
        //ucCheckSnr[0]=0x0;memcpy(ucCheckSnr+1,tCardOpDu.ucIssuerCode, 4);
        //memset(ucCheckSnr+5,0xFF,3);
        byte[] ucCheckSnr = Datautils.concatAll(new byte[]{(byte) 0x00}, tCardOpDU.ucIssuerCode,
                tCardOpDU.ucCityCode, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x00});
        //卡里的城市码不是天津码时
        if (!Arrays.equals(Datautils.concatAll(tCardOpDU.ucIssuerCode, tCardOpDU.ucCityCode)
                , new byte[]{(byte) 0x01, (byte) 0x13, (byte) 0x11, (byte) 0x21})) {
            tCardOpDU.ucOtherCity = (byte) 0x01;
        }

        byte[] ucDateTimeByte = Datautils.cutBytes(tCardOpDU.ucDateTime, 0, 4);
        int ucDateTimeInt = Integer.parseInt(Datautils.byteArrayToString(ucDateTimeByte));
        int ucAppStartDateInt = Integer.parseInt(Datautils.byteArrayToString(tCardOpDU.ucAppStartDate));
        //当前时间小于开始时间
        if (ucDateTimeInt < ucAppStartDateInt) {
            return ReturnVal.CAD_SELL;
        }
        int ucAppEndDateInt = Integer.parseInt(Datautils.byteArrayToString(tCardOpDU.ucAppEndDate));
        //当前时间大于结束时间
        if (ucDateTimeInt > ucAppEndDateInt) {
            return ReturnVal.CAD_EXPIRE;
        }

        //***Read 17 File
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, CardMethods.READ_ICCARD_17FILE);
        if (resultBytes == null || resultBytes.length == 2) {
            return ReturnVal.CAD_READ;
        }
        //9-10	互通卡种（互通标志，可确认挂失与否）
        // 2	n	与电子现金08文件数据同步  0000：非互通卡0001：互通卡
        if (tCardOpDU.ucOtherCity == (byte) 0x01) {
            tCardOpDU.ucMainCardType = (byte) 0x00;
            //=DBDat[10]; 异地卡都认为是普通卡
            tCardOpDU.ucSubCardType = (byte) 0x00;
        }
        return METHOD_OK;
    }

    public int doFSysSta(BankCard mBankCard) {
        if (fSysSta) {
            byte[] ucAppSnr = Datautils.cutBytes(tCardOpDU.ucAppSnr, 2, 8);
            byte[] problemIssue2 = Datautils.cutBytes(problemIssueCode, 2, 8);
            //不是同一张卡
            if (!Arrays.equals(ucAppSnr, problemIssue2)) {
                fSysSta = false;
            } else {
                byte[] dBCmd = new byte[8];
                System.arraycopy(new byte[]{(byte) 0x80, (byte) 0x5a, (byte) 0x00, (byte) 0x00, (byte) 0x02}
                        , 0, dBCmd, 0, 5);
                tCardOpDU.uiOffLineCount = Datautils.cutBytes(problemIssueCode, 10, 2);
                char uiOffLineCount = (char) (Datautils.byteToChar(tCardOpDU.uiOffLineCount) + 1);
                dBCmd[5] = (byte) ((uiOffLineCount) >> 8);
                dBCmd[6] = (byte) uiOffLineCount;
                dBCmd[7] = (byte) 0x08;
                dBCmd[3] = problemIssueCode[21] == (byte) 0x00 ? (byte) 0x06 : (byte) 0x09;

                byte[] resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, dBCmd);
                if (resultBytes == null || resultBytes.length == 2) {
                    fSysSta = false;
                } else {
                    tCardOpDU.ucMAC2 = Datautils.cutBytes(resultBytes, 0, 4);
                    tCardOpDU.ucTAC = Datautils.cutBytes(resultBytes, 4, 4);
                    //psam卡(8072)校验
                    byte[] psamCheckMac2 = CardMethods.checkPsamMac2(tCardOpDU.ucMAC2);
                    resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM1_APDU, psamCheckMac2);
                    if (resultBytes == null || resultBytes.length == 2) {
                        return ReturnVal.CAD_MAC2;
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

                    //写记录
                    CardMethods.onAppendRecordTrade(context, tCardOpDU.ucProcSec == 2 ? (byte) 0x00 : (byte) 0x02
                            , tCardOpDU, runParaFile, psamBeenList, mBankCard);
                    return ReturnVal.CAD_OK;
                }
            }
        }

        // TODO: 2019/2/20 去获取当前卡号是否在黑名单
//        fBlackCard==CheckBlacklist(tCardOpDu.ucAppSnr);

        if (fBlackCard) {
            //801A指令
            byte[] cmd801A = Datautils.concatAll(new byte[]{(byte) 0x80, (byte) 0x1a, (byte) 0x45, (byte) 0x02, (byte) 0x10},
                    Datautils.cutBytes(tCardOpDU.ucAppSnr, 2, 8), tCardOpDU.ucFile15Top8);
            byte[] resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM1_APDU, cmd801A);
            if (resultBytes == null || resultBytes.length == 2) {
                return ReturnVal.CAD_PSAM_ERROR;
            }
            //0084000004指令
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, Datautils.HexString2Bytes("0084000004"));
            if (resultBytes == null || resultBytes.length == 2) {
                return ReturnVal.CAD_READ;
            }
            //80fa指令
            byte[] cmd80fa = Datautils.concatAll(new byte[]{(byte) 0x80, (byte) 0xfa, (byte) 0x05, (byte) 0x00, (byte) 0x10}
                    , Datautils.cutBytes(resultBytes, 0, 4), new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
                            , (byte) 0x84, (byte) 0x1e, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x80, (byte) 0x00, (byte) 0x00});
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM1_APDU, cmd80fa);
            if (resultBytes == null || resultBytes.length == 2) {
                return ReturnVal.CAD_PSAM_ERROR;
            }
            //841e000004指令
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, Datautils.HexString2Bytes("841e000004"));
            if (resultBytes == null || resultBytes.length == 2) {
                return ReturnVal.CAD_READ;
            }


            tCardOpDU.ucProcSec = (byte) 0x02;
            tCardOpDU.uiOffLineCount = new byte[]{(byte) 0x00, (byte) 0x00};
            tCardOpDU.purorimoneyInt = 0;
            tCardOpDU.pursubInt = 0;
            tCardOpDU.ulPOSTradeCount = 0;
            tCardOpDU.ucTAC = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            // TODO: 2019/2/20
//            PrepareRecord(0xE0);
//            OnAppendRecord(0xE0);
            return ReturnVal.CAD_BL1;
        }

        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession()
                .getRunParaFileDao().loadAll();
        if (runParaFiles.size() < 1) {
            return ReturnVal.CODE_PLEASE_SET;
        }
        runParaFile = runParaFiles.get(0);


        return METHOD_OK;
    }

    public CardBackBean consumption(BankCard mBankCard, List<PsamBeen> psamBeenList) {
        tCardOpDU.ucDiv = 2;
        tCardOpDU.ucCAPP = 1;
        fOldDisable = 0;
        purDisable = 0;
        tCardOpDU.ucKeyID = psamBeenList.get(0).getKeyID();
        tCardOpDU.ucPOSSnr = psamBeenList.get(0).getTermBumber();
        tCardOpDU.ucProcSec = (byte) 0x02;
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


        byte[] resultBytes;
        if (tCardOpDU.ucProcSec == 2) {
            int ret = CardMethods.fIsUsePur(tCardOpDU, runParaFile);    //判断钱包权限

            if ((tCardOpDU.ucOtherCity == 0) && (tCardOpDU.ucMainCardType == 0x01
                    || tCardOpDU.ucMainCardType == 0x11) && (ret == 0)) {
                purDisable = 1;
            } else if (ret == 0)        //没有权限
            {
                tCardOpDU.purorimoneyInt = 0;
                tCardOpDU.pursubInt = 0;
                return new CardBackBean(ReturnVal.CAD_EMPTY, tCardOpDU);
            }

        }
        tCardOpDU.fUseHC = 0;
        //本地 老年卡 残疾人卡  第一次刷
        if ((tCardOpDU.ucOtherCity == 0) && (tCardOpDU.ucMainCardType == 0x01
                || tCardOpDU.ucMainCardType == 0x11) && (fOldDisable == 0)) {
            //读05文件30字节
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                    , new byte[]{(byte) 0x00, (byte) 0xB0, (byte) 0x85, (byte) 0x00, (byte) 0x00});
            if (resultBytes == null || resultBytes.length == 2) {
                return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
            }
            byte[] ucDateTimeByte = Datautils.cutBytes(tCardOpDU.ucDateTime, 0, 4);
            int ucDateTimeInt = Integer.parseInt(Datautils.byteArrayToString(ucDateTimeByte));
            int dBDatInt = Integer.parseInt(Datautils
                    .byteArrayToString(Datautils.cutBytes(resultBytes, 36, 4)));
            //判断是否在有效期之内
            if (dBDatInt < ucDateTimeInt) {
                //超过有效期
                fOldDisable = 1;
            }

            //读取17上条复合记录命令48字节
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                    , new byte[]{(byte) 0x00, (byte) 0xB2, (byte) 0x01, (byte) 0xF4, (byte) 0x00});
            if (resultBytes == null || resultBytes.length == 2) {
                return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
            }

            //判断是否是同一辆车
            byte[] busBytes = Datautils.cutBytes(resultBytes, 3, 6);

            if (Arrays.equals(busBytes, tCardOpDU.ucPOSSnr)) {
                //上次刷卡时间
//                byte[] busLastBytes = Datautils.cutBytes(resultBytes, 25, 7);
//                String busLastStr = Datautils.byteArrayToString(busLastBytes);
                byte[] busLastBytes = Datautils.cutBytes(rcdbuffer, 24, 4);
                long string16ToLong = Datautils.parseString16ToLong(Datautils
                        .byteArrayToString(busLastBytes)) * 1000;
                Date utcToLocal = DateUtils.transferLongToDate("yyyyMMddHHmmss", string16ToLong);
                // 连续刷卡限制时间
                int timeLimit = Datautils.byteArrayToInt(runParaFile.getUcOldCardTimeLimit());
//                int timeLimit = 10;
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                try {
                    Date busLast = utcToLocal;
                    Date dateTime = format.parse(Datautils.byteArrayToString(
                            Datautils.cutBytes(tCardOpDU.ucDateTime, 0, 7)));
                    Date busLastAfter = new Date(busLast.getTime() + timeLimit * 60000);
                    Date dateTimeAfter = new Date(dateTime.getTime() + timeLimit * 60000);
                    int compare = dateTime.compareTo(busLastAfter);
                    int compare1 = busLast.compareTo(dateTimeAfter);
                    if (compare < 0 && compare1 < 0) {
                        fOldDisable = 1;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }


        //805c 读余额
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                , new byte[]{(byte) 0x80, (byte) 0x5c, (byte) 0x03, (byte) 0x02, (byte) 0x04});
        if (resultBytes == null || resultBytes.length == 2) {
            return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
        }

        int actRemaining, i;
        for (actRemaining = 0, i = 0; i < 4; i++) {
            actRemaining <<= 8;
            actRemaining += resultBytes[i] & 0xFF;
        }
        tCardOpDU.purorimoneyInt = actRemaining;

        tCardOpDU.pursubInt = CardMethods.getRadioPurSub(tCardOpDU, runParaFile);
        tCardOpDU.pursub = Datautils.intToByteArray1(tCardOpDU.pursubInt);

        //老年卡 残疾人卡  有钱包权限不能连刷  无钱包权限扣钱包，可以连刷
        if ((tCardOpDU.ucOtherCity == 0) &&
                (tCardOpDU.ucMainCardType == 0x01 || tCardOpDU.ucMainCardType == 0x11) &&
                (tCardOpDU.ucCardClass == JTBCARD) && (purDisable == 0)) {
            // fOldDisable=0 第一次刷卡  不扣钱
            if (fOldDisable == 0) {
                tCardOpDU.pursubInt = 0;
            } else {
                //有权限 第二次刷卡 扣钱
                return new CardBackBean(ReturnVal.CAD_REUSE, tCardOpDU);
            }

        }

        if (tCardOpDU.purorimoneyInt > CardMethods.MAXVALUE) {
            return new CardBackBean(ReturnVal.CAD_BROKEN, tCardOpDU);
        }
        if (tCardOpDU.purorimoneyInt < tCardOpDU.pursubInt) {
            return new CardBackBean(ReturnVal.CAD_EMPTY, tCardOpDU);
        }

        tCardOpDU.ulTradeValue = tCardOpDU.ucProcSec == (byte) 0x02 ? tCardOpDU.pursubInt : tCardOpDU.actYueSub;
        tCardOpDU.ucRcdType = tCardOpDU.ucProcSec == (byte) 0x02 ? (byte) 0x00 : (byte) 0x02;

        //IC卡初始化(805C)
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                , Datautils.HexString2Bytes("805C030204"));
        if (resultBytes == null || resultBytes.length == 2) {
            return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
        }
        byte[] first = new byte[]{(byte) 0x80, (byte) 0x50, (byte) 0x03, (byte) 0x02, (byte) 0x0b};

        byte[] ulTradeValue = Datautils.intToByteArray1(tCardOpDU.ulTradeValue);
        byte[] dB8050Cmd = Datautils.concatAll(first, tCardOpDU.ucKeyID, ulTradeValue
                , tCardOpDU.ucPOSSnr, new byte[]{(byte) 0x0f});

        //8050IC卡初始化
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, dB8050Cmd);
        if (resultBytes == null || resultBytes.length == 2) {
            return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
        }
        tCardOpDU.ulBalanceByte = Datautils.cutBytes(resultBytes, 0, 4);
        tCardOpDU.ulBalance = Datautils.byteArrayToInt(tCardOpDU.ulBalanceByte);
        tCardOpDU.uiOffLineCount = Datautils.cutBytes(resultBytes, 4, 2);
        tCardOpDU.ucKeyVer = resultBytes[9];
        tCardOpDU.ucKeyAlg = resultBytes[10];
        tCardOpDU.rondomCpu = Datautils.cutBytes(resultBytes, 11, 4);
        // TODO: 2019/1/8  设定本次消费额
        if ((tCardOpDU.ulBalance / 100) > 1000) {
            // TODO: 2019/1/8  语音请投币  界面显示卡片已损坏
        }
        if ((tCardOpDU.ulBalance / 100) < 0.01) {
            // TODO: 2019/1/8 语音/界面  余额不足
        }
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM1_APDU
                , CardMethods.PSAM_SELECT_DIR);
        if (resultBytes == null || resultBytes.length == 2) {
            return new CardBackBean(ReturnVal.CAD_PSAM_ERROR, tCardOpDU);
        }

        //8070
        byte[] psamMac1 = CardMethods.initSamForPurchase(tCardOpDU);
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM1_APDU, psamMac1);
        if (resultBytes == null || resultBytes.length == 2) {
            return new CardBackBean(ReturnVal.CAD_PSAM_ERROR, tCardOpDU);
        }
        if (resultBytes.length <= 2) {
            return new CardBackBean(ReturnVal.CAD_PSAM_ERROR, tCardOpDU);
        }
        tCardOpDU.ulPOSTradeCountByte = Datautils.cutBytes(resultBytes, 0, 4);
        tCardOpDU.ulPOSTradeCount = Datautils.byteArrayToInt(tCardOpDU.ulPOSTradeCountByte);
        tCardOpDU.ucMAC1 = Datautils.cutBytes(resultBytes, 4, 4);

        if (tCardOpDU.ucCAPP == 1) {
            //80dc
            byte[] value = new byte[8];
            value[0] = (byte) (tCardOpDU.ulTradeValue >> 24);
            value[1] = (byte) (tCardOpDU.ulTradeValue >> 16);
            value[2] = (byte) (tCardOpDU.ulTradeValue >> 8);
            value[3] = (byte) tCardOpDU.ulTradeValue;
            value[4] = (byte) ((tCardOpDU.ulBalance - tCardOpDU.ulTradeValue) >> 24);
            value[5] = (byte) ((tCardOpDU.ulBalance - tCardOpDU.ulTradeValue) >> 16);
            value[6] = (byte) ((tCardOpDU.ulBalance - tCardOpDU.ulTradeValue) >> 8);
            value[7] = (byte) (tCardOpDU.ulBalance - tCardOpDU.ulTradeValue);
            byte[] concatAll = Datautils.concatAll(new byte[]{(byte) 0x80, (byte) 0xDC
                            , (byte) 0x00, (byte) 0xF0, (byte) 0x30, tCardOpDU.ucTradeType
                            , (byte) 0x00, (byte) 0x00}
                    , tCardOpDU.ucPOSSnr
                    , new byte[]{(byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00
                            , (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}
                    , value
                    , tCardOpDU.ucDateTime
                    , CardMethods.ucCityCodeII
                    , CardMethods.ucShouLiCodeII
                    , new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
                            , (byte) 0x00, (byte) 0x00});

            LogUtils.d("80dc: " + Datautils.byteArrayToString(concatAll));
            //更新1E文件80dc
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                    , concatAll);
            if (resultBytes == null || resultBytes.length == 2) {
                return new CardBackBean(ReturnVal.CAD_READ, tCardOpDU);
            }
        }
        //(8054)消费
        byte[] cmd = CardMethods.getIcPurchase(tCardOpDU);
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
            fSysSta = true;
            problemIssueCode = Datautils.concatAll(tCardOpDU.ucAppSnr, tCardOpDU.uiOffLineCount,
                    new byte[]{tCardOpDU.ucProcSec}, value, new byte[]{tCardOpDU.ucCAPP});
            CardMethods.onAppendRecordTrade(context, tCardOpDU.ucProcSec == 2 ? (byte) 0x01 : (byte) 0x03
                    , tCardOpDU, runParaFile, psamBeenList, mBankCard);
            return new CardBackBean(ReturnVal.CAD_RETRY, tCardOpDU);
        }

        tCardOpDU.ucTAC = Datautils.cutBytes(resultBytes, 0, 4);
        tCardOpDU.ucMAC2 = Datautils.cutBytes(resultBytes, 4, 4);

        //8072校验
        byte[] psamCheckMac2 = CardMethods.checkPsamMac2(tCardOpDU.ucMAC2);
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM1_APDU, psamCheckMac2);
        if (resultBytes == null || resultBytes.length == 2) {
            return new CardBackBean(ReturnVal.CAD_MAC2, tCardOpDU);
        }
        //添加正常交易记录 报语音显示界面
        CardMethods.onAppendRecordTrade(context, tCardOpDU.ucProcSec == 2 ? (byte) 0x00 : (byte) 0x02
                , tCardOpDU, runParaFile, psamBeenList, mBankCard);
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
