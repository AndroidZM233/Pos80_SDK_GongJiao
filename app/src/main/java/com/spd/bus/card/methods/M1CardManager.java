package com.spd.bus.card.methods;

import android.os.RemoteException;
import android.util.Log;

import com.spd.base.been.tianjin.CardBackBean;
import com.spd.base.been.tianjin.CardRecord;
import com.spd.base.been.tianjin.TDutyStatFile;
import com.spd.base.been.tianjin.TCardOpDU;
import com.spd.base.been.tianjin.TStaffTb;
import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.Datautils;
import com.spd.base.utils.DesUtil;
import com.spd.bus.card.utils.DateUtils;
import com.spd.bus.card.utils.LogUtils;
import com.spd.bus.spdata.FaceResultAct;
import com.spd.bus.spdata.been.PsamBeen;
import com.spd.bus.spdata.been.TCommInfo;
import com.spd.bus.spdata.utils.TimeDataUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import wangpos.sdk4.libbasebinder.BankCard;

import static com.spd.bus.card.methods.ReturnVal.CAD_BROKEN;
import static com.spd.bus.card.methods.ReturnVal.CAD_OK;
import static com.spd.bus.card.methods.ReturnVal.CAD_READ;
import static com.spd.bus.card.methods.ReturnVal.CAD_SETCERR;
import static com.spd.bus.card.methods.ReturnVal.CAD_WRITE;

/**
 * M1
 * Created by 张明_ on 2019/2/18.
 * Email 741183142@qq.com
 */
public class M1CardManager {
    private static final Object LOCK = new Object();
    private static M1CardManager jtbCardManager;
    /**
     * 微智接口返回数据
     */
    private byte[] respdata = new byte[512];
    /**
     * 微智接口返回数据长度
     */
    private int[] resplen = new int[1];
    /**
     * //扇区标识符
     */
    private byte[] secF;
    /**
     * 微智接口返回状态 非0错误
     */
    private int retvalue = -1;
    private byte[] snUid;
    /**
     * //保存读第0扇区 01块返回的 秘钥
     */
    private byte[][] lodkey = new byte[16][6];

    public static final byte M150 = (byte) 0x51;
    public static final byte M170 = (byte) 0x71;

    private int secOffset;
    private TCardOpDU cardOpDU;
    private int fLockCard;
    private TStaffTb tStaffTb;
    private TDutyStatFile dutyStatFile;
    private RunParaFile runParaFile;

    private final int F_OVERFLOW = 0;
    private final int F_NORMAL = 1;
    private final int F_LITTLE = 2;
    private final int F_LESS = 3;
    private final int F_LEAST = 4;

    private BankCard mBankCard;
    private byte[] recordInfo = new byte[100];
    private byte[] dtZ;
    private byte[] dtF;
    private int actRemaining;
    private TCommInfo cinfoz;
    private TCommInfo cinfof;
    private TCommInfo cinfo;
    List<PsamBeen> psamBeenList;
    private int blk;

    public static M1CardManager getInstance() {
        if (jtbCardManager == null) {
            synchronized (LOCK) {
                jtbCardManager = new M1CardManager();
            }
        }
        return jtbCardManager;
    }


    public CardBackBean mainMethod(BankCard mBankCard, int card, int devMode, List<PsamBeen> psamBeenList) throws Exception {
        this.mBankCard = mBankCard;
        this.psamBeenList = psamBeenList;
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession()
                .getRunParaFileDao().loadAll();
        if (runParaFiles.size() == 0) {
            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
        } else {
            runParaFile = runParaFiles.get(0);
        }

        secOffset = 0;
        cardOpDU = new TCardOpDU();
        if (card == (byte) 0x71) {
            secOffset = 16;
        } else {
            secOffset = 0;
        }

        int ret;

        if (checkMifcardclass(mBankCard) != CAD_OK) {
            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
        }
        switch (cardOpDU.cardClass) {
            case 0x01:
                return busCard(mBankCard, cardOpDU.cardClass, devMode);
            case 0x51:
            case 0x71:
                return cITYCard();
            default:
                return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
        }


        //比对 9块 10块数据
//        if (!BackupManage(8)) {
//            return ReturnVal.CAD_READ;
//        }
//        if (!writeCardRcd()) {
//            return ReturnVal.CAD_READ;
//        }
//        Log.i("stw", "===M1卡消费结束===" + (System.currentTimeMillis() - ltime));
//        handler.sendMessage(handler.obtainMessage(2, Datautils.byteArrayToInt(blance)));
//        isFlag = 0;
    }


    /*****************************************************************************************
     *                 函数名称：unsigned char BUS_card，公交M1卡刷卡处理程序                 *
     *                ========================================================                *
     *参数：    无                                                                            *
     *功能：    1。断点卡的恢复处理                                                           *
     *          2。黑卡或列入黑名单卡的识别处理                                               *
     *          3。测试卡、线路票价卡、设置卡、采集卡、司机卡的识别及功能执行                 *
     *          4。消费卡按月票或钱包进行消费扣款                                             *
     *返回值：  0xff         -  未启用卡                                                      *
     *          CAD_FULL     -  车载机内存储器满（未采集数据>=26624条                         *
     *          CAD_ACCESS   -  卡扇区认证失败                                                *
     *          CAD_READ     -  读卡失败                                                      *
     *          CAD_WRITE    -  写卡失败                                                      *
     *          CAD_BROKEN   -  坏卡                                                          *
     *          CAD_SELL     -  卡没到启用日期                                                *
     *          CAD_EXPIRE   -  卡已过有效期                                                  *
     *          CAD_BL       -  黑卡或黑名单卡                                                *
     *          CAD_TestC    -  测试卡                                                        *
     *          CAD_CaiJiC   -  采集卡                                                        *
     *          CAD_SETCERR  -  设置错误                                                      *
     *          CAD_SETCOK   -  线路票价卡设置成功                                            *
     *          CAD_KEYSETIN -  设置设备号及车辆号键盘开始输入                                *
     *          CAD_KEYSETOK -  设置卡设置成功                                                *
     *          CAD_LOGON    -  司机上班状态                                                  *
     *          CAD_LOGOFF   -  司机下班状态                                                  *
     *          CAD_OK       -  消费卡扣款完成                                                *
     *          CAD_EMPTY    -  消费卡月票及钱包无余额                                        *
     *          CAD_RETRY    -  请重刷                                                        *
     *****************************************************************************************/
    private CardBackBean busCard(BankCard mBankCard, int cardClass, int devMode) throws Exception {
        byte[] rcdbuffer = new byte[64];
        int devRcdCnt, i, devRcdOriMoney, devRcdSub, ret;
        cinfoz = new TCommInfo();
        cinfof = new TCommInfo();
        cinfo = new TCommInfo();
        cardClass = 1;
        cardOpDU.ucOtherCity = (byte) 0x00;
        cardOpDU.fUseHC = (byte) 0xff;
        byte[] record = new byte[80];

        //系统时间
        byte[] systemTime = Datautils.getDateTime();
        cardOpDU.ucDateTime = systemTime;
        Long timeToLong = DateUtils.convertTimeToLong(DateUtils.FORMAT_yyyyMMddHHmmss
                , Datautils.byteArrayToString(cardOpDU.ucDateTime));
        String ulUTCString = Long.toHexString(timeToLong / 1000).toUpperCase();
        cardOpDU.ucDateTimeUTC = Datautils.HexString2Bytes(ulUTCString);
//        Get_time(&RealTime.ClockString[0]); // 读取并显示日期,读取实时钟并判断有效性
//        memcpy(carddu.RealTime,&RealTime.ClockString[0],7);
//        ulDevUTC=BCDTimeToUTC(&RealTime.ClockString[0]);
//        ulBasUTC=BCDTimeToUTC(&ucBasicTime[0]);
//        if((ulBasUTC>ulDevUTC+2592000L)||(ulDevUTC>ulBasUTC+2592000L))//30*24*3600L
//        {
//            return CAD_TIMEERR;
//        }
//        memcpy(ucBasicTime,&RealTime.ClockString[0],7);
        cardOpDU.fBlackCard = false;
        switch (cardOpDU.fStartUse[0]) {
            case 0x01:
            case 0x03:
                return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
            case 0x02:  //Start
                break;
            case 0x04:
                cardOpDU.fBlackCard = true;//Black
                break;
            default:
                return new CardBackBean(ReturnVal.CAD_BROKEN, cardOpDU);
        }

        byte[] ucDateTimeByte = Datautils.cutBytes(cardOpDU.ucDateTime, 1, 3);
        int ucDateTimeInt = Integer.parseInt(Datautils.byteArrayToString(ucDateTimeByte));
        int ucAppStartDateInt = Integer.parseInt(Datautils.byteArrayToString(cardOpDU.startUserDate));
        //当前时间小于开始时间
        if (ucDateTimeInt < ucAppStartDateInt) {
            return new CardBackBean(ReturnVal.CAD_SELL, cardOpDU);
        }
        int ucAppEndDateInt = Integer.parseInt(Datautils.byteArrayToString(cardOpDU.endUserDate));
        if (ucDateTimeInt > ucAppEndDateInt) {
            return new CardBackBean(ReturnVal.CAD_EXPIRE, cardOpDU);
        }


        // 卡禁止，但允许管理卡刷卡
        if ((fLockCard == 1) && (cardOpDU.cardType < (byte) 0x80)) {
            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
        }

        //第6扇区24 块认证
        byte[] lodKey6 = lodkey[6];
        retvalue = mBankCard.m1CardKeyAuth(0x41, 24 + secOffset * 4, lodKey6.length, lodKey6, snUid.length, snUid);
        if (retvalue != 0) {
            LogUtils.e("===第6扇区24 块认证错误===");
            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
        }

        retvalue = mBankCard.m1CardReadBlockData(26 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("===读6扇区第26块失败===");
            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
        }
        byte[] bytes26 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);

        cardOpDU.ucCheckDate = Datautils.concatAll(new byte[]{(byte) 0x20}
                , Datautils.cutBytes(bytes26, 12, 3));

        //读6扇区第24块
        retvalue = mBankCard.m1CardReadBlockData(24 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("===读6扇区第24块失败===");
            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
        }
        byte[] bytes24 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);


        LogUtils.d("===读6扇区第24块返回===" + Datautils.byteArrayToString(bytes24));
        dtZ = bytes24;
        byte chk = 0;
        //异或操作
        for (i = 0; i < 16; i++) {
            chk ^= dtZ[i];
        }
        //判断8-15是否都等于0xff
        if (Arrays.equals(Datautils.cutBytes(dtZ, 8, 7),
                new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}) && chk == 0) {
            cinfoz.fValid = 1;
        }
        if (Arrays.equals(Datautils.cutBytes(dtZ, 0, 8), new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff})) {
            cinfoz.fValid = 0;
        }
        if (dtZ[0] > 8) {
            cinfoz.fValid = 0;
        }

        cinfoz.setData(dtZ);
        cinfoz.iPurCount = (char) ((cinfoz.ucPurCount[0] << 8) + cinfoz.ucPurCount[1]);
        cinfoz.iYueCount = (char) ((cinfoz.ucYueCount[0] << 8) + cinfoz.ucYueCount[1]);
        //副本  有效性
        //读6扇区第25块
        retvalue = mBankCard.m1CardReadBlockData(25 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("===读6扇区第25块失败===");
            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
        }
        byte[] bytes25 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        dtF = bytes25;
        for (i = 0; i < 16; i++) {
            chk ^= dtF[i];
        }
        if (Arrays.equals(Datautils.cutBytes(dtF, 8, 7),
                new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,}) && chk == 0) {
            cinfof.fValid = 1;
        }
        if (Arrays.equals(Datautils.cutBytes(dtF, 0, 8), new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff})) {
            cinfof.fValid = 0;
        }
        if (dtF[0] > 8) {
            cinfof.fValid = 0;
        }

        cinfof.setData(dtF);

        cinfof.iPurCount = (char) ((cinfof.ucPurCount[0] << 8) + cinfof.ucPurCount[1]);
        cinfof.iYueCount = (char) ((cinfof.ucYueCount[0] << 8) + cinfof.ucYueCount[1]);
        if (cinfoz.fValid == 1) {
            cinfo = cinfoz;
        } else if (cinfof.fValid == 1) {
            cinfo = cinfof;
        } else {
            LogUtils.e("===24 25块有效标志错误 返回0===");
            return new CardBackBean(ReturnVal.CAD_BROKEN, cardOpDU);
        }

        if ((cinfoz.fValid == 1 && (cinfoz.fBlack == 4)) || (cinfof.fValid == 1 && (cinfof.fBlack == 4))) {
            //黑名单 报语音
            cardOpDU.fBlackCard = true;
            LogUtils.e("m1ICCard: 黑名单");
            return new CardBackBean(ReturnVal.CAD_BL1, cardOpDU);
        }
        if (cardOpDU.fBlackCard == false) {
            //查询数据库黑名单
//            cardOpDU.fBlackCard = CheckBlacklist(cardOpDU.IssueSnr);
        }

        if (cardOpDU.fBlackCard) {
            if (cardOpDU.cardType >= (byte) 0x80) {
                cinfo.fBlack = 4;
                cardOpDU.procSec = 2;
                cardOpDU.purorimoneyInt = 0;
                cardOpDU.pursubInt = 0;
                cardOpDU.purCount = cinfo.iPurCount;
                cardOpDU.ucIncPurDev = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
                if (!CardMethods.modifyInfoArea(24, cinfo, mBankCard, lodkey, snUid)) {
                    return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                }
                if (!CardMethods.modifyInfoArea(25, cinfo, mBankCard, lodkey, snUid)) {
                    return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                }
//                cardOpDU.ucRcdType = 0xE0;
//                PrepareRecordTrade(cardOpDU.ucRcdType);
//                OnAppendRecordTrade(cardOpDU.ucRcdType);
                CardMethods.onAppendRecordTrade((byte) 0xE0, cardOpDU, runParaFile, psamBeenList);
                // TODO: 2019/3/18 写消费记录
                return new CardBackBean(ReturnVal.CAD_BL1, cardOpDU);
            }
        }

        //////////////////End Card_Pretreat///////////////////////////////////
//        if (cardOpDU.cardType == (byte) 0x99) {
//            return ReturnVal.CAD_TEST_C;
//        }

        if (cardOpDU.cardType == (byte) 0x91) {

        }

        if (cardOpDU.cardType == 0x0e) {
            if (devMode == 2) {
                boolean equals = Arrays.equals(tStaffTb.ucCityCode, Datautils.cutBytes(cardOpDU.snr
                        , 0, 4));
                if (equals) {
                    cardOpDU.purIncMoneyInt = 0;
//                    MachStatusFile.fSysSta = CAD_NORMAL;
                    return new CardBackBean(ReturnVal.CAD_EMPTY, cardOpDU);
                }
            } else {
                byte[] lodkey6 = lodkey[6];
                retvalue = mBankCard.m1CardKeyAuth(0x41, 26 + secOffset,
                        lodkey6.length, lodkey6, snUid.length, snUid);
                if (retvalue != 0) {
                    LogUtils.e("===认证6扇区失败===");
                    return new CardBackBean(ReturnVal.CAD_ACCESS, cardOpDU);
                }
                retvalue = mBankCard.m1CardReadBlockData(26 + secOffset * 4, respdata, resplen);
                if (retvalue != 0) {
                    LogUtils.e("=== 读取6扇区第26块失败==");
                    return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                }

                byte[] bytes06 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
                if ((bytes06[10] & 0x40) == 0) {
                    return new CardBackBean(ReturnVal.CAD_SETCERR, cardOpDU);
                }
                tStaffTb = new TStaffTb();
                tStaffTb.ucCardClass = cardOpDU.cardClass;
                tStaffTb.ucIssuerCode = Datautils.cutBytes(cardOpDU.issueSnr, 0, 2);
                tStaffTb.ucCityCode = Datautils.cutBytes(cardOpDU.snr, 0, 2);
                tStaffTb.ucVocCode = Datautils.cutBytes(cardOpDU.snr, 2, 2);
                tStaffTb.ucAppSnr = Datautils.cutBytes(cardOpDU.issueSnr, 0, 8);
                tStaffTb.ucMainCardType = cardOpDU.cardType;
                tStaffTb.ucSubCardType = 0;
                tStaffTb.ucAppStartYYMMDD = Datautils.cutBytes(cardOpDU.startUserDate, 0, 3);
                tStaffTb.ulUTC = cardOpDU.ucDateTimeUTC;
                tStaffTb.ulBCD = cardOpDU.ucDateTime;
                dutyStatFile = new TDutyStatFile();
                dutyStatFile.rcdNr = 0;                                        // 清除当班司机运营统计区
                dutyStatFile.totalPerson = 0;                                     // 总记录数，总有效人次
                dutyStatFile.totalMoney = 0;                                      // 总金额
                dutyStatFile.totalYuePerson = 0;  // 总月票人次
                dutyStatFile.fWork = 1;
                dutyStatFile.ucLogOK = 0;
                dutyStatFile.tickcnt = 0;
                dutyStatFile.fCreateMode = 1;
                DbDaoManage.getDaoSession().getTStaffTbDao().deleteAll();
                DbDaoManage.getDaoSession().getTStaffTbDao().insert(tStaffTb);
                DbDaoManage.getDaoSession().getTDutyStatFileDao().deleteAll();
                DbDaoManage.getDaoSession().getTDutyStatFileDao().insert(dutyStatFile);
                // TODO: 2019/3/18 写消费记录
                CardMethods.onAppendRecordSelf((byte) 0xE1, cardOpDU, runParaFile, psamBeenList);
                return new CardBackBean(ReturnVal.CAD_LOGON, cardOpDU);
            }
        }

//        if (dutyStatFile.fWork == 0) {
//            return CAD_SETCERR;
//        }

        CardBackBean cardBackBean = mifareCardRestore();
        ret = cardBackBean.getBackValue();
        if (ret != CAD_OK) {
            return cardBackBean;
        }
        /////本机恢复//////////////////////////////////////////////////////////////
        // TODO: 2019/3/18 恢复
        fErr = 0;
        long dbCount = DbDaoManage.getDaoSession().getCardRecordDao().count();
        if (dbCount > 0) {
            int count = 20;
            if (dbCount < 20L) {
                count = (int) dbCount;
            }
            for (int j = 0; j < count; j++) {
                CardRecord cardRecord = DbDaoManage.getDaoSession().getCardRecordDao()
                        .loadByRowId(dbCount - j);
                byte[] recordByte = cardRecord.getRecord();
                byte[] snr = Datautils.cutBytes(recordByte, 7, 4);
                if (Arrays.equals(snr, cardOpDU.snr)) {
                    Log.d("ZM", "rcdbuffer[2] & 0x01: " + (recordByte[2] & 0x01));
                    if ((recordByte[2] & 0x01) == 0x01) {
                        fErr = 1;
                        Log.d("ZM", "有灰记录");
                        rcdbuffer = recordByte;
                        break;
                    }
                }
            }
        }


        // 卡在二十条记录内无错
        if (fErr == 0) {
            //上次刷卡时间
//            byte[] busLastBytes = Datautils.cutBytes(rcdbuffer, 24, 7);
//            String busLastStr = Datautils.byteArrayToString(busLastBytes);
//            // 连续刷卡限制时间
//            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//            try {
//                Date busLast = format.parse(busLastStr);
//                Date dateTime = format.parse(Datautils.byteArrayToString(cardOpDU.ucDateTime));
//                Date cardAfter = new Date(busLast.getTime() + 1000);
//                Date cardBefore = new Date(busLast.getTime() - 1000);
//                int compare = dateTime.compareTo(cardAfter);
//                int compare1 = dateTime.compareTo(cardBefore);
//                if (compare < 0 && compare1 > 0) {
//                    return 255;
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//                return ReturnVal.CAD_READ;
//            }
        } else {
            switch (rcdbuffer[2] & 0xfe) {
                case 0:
                case 0x10:
                case 0x12: //普通,上车,罚款
                    for (devRcdCnt = 0, i = 0; i < 2; i++) {
                        devRcdCnt <<= 8;
                        devRcdCnt += rcdbuffer[i + 33];
                    }
                    for (devRcdOriMoney = 0, i = 0; i < 3; i++) {
                        devRcdOriMoney <<= 8;
                        devRcdOriMoney += rcdbuffer[i + 36];
                    }
                    for (devRcdSub = 0, i = 0; i < 3; i++) {
                        devRcdSub <<= 8;
                        devRcdSub += rcdbuffer[i + 39];
                    }
                    //if(memcmp(CInfo.ucPurCount, &RcdBuffer[33], 2) > 0)
                    if (cinfo.iPurCount == devRcdCnt + 1) {
                        cardOpDU.ucRcdType = (byte) (rcdbuffer[2] - 1);
                        cardOpDU.procSec = 2;
                        cardOpDU.purorimoneyInt = devRcdOriMoney;
                        cardOpDU.pursubInt = devRcdSub;
                        fErr = 0;

//                            memcpy( & RcdDu.RcdBuffer[2], &RcdBuffer[2], 62);
//                            OnAppendRecordTrade(carddu.ucRcdType);
                        cardOpDU.fInBus = 1;
                        if (cardOpDU.ucRcdType == (byte) 0x12) {
                            cardOpDU.fInBus = 0;
                            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                        }
                        return new CardBackBean(ReturnVal.CAD_OK, cardOpDU);
                    } else if (cinfo.iPurCount == devRcdCnt) {
                        fErr = 0;
                    } else {
                        fErr = 0;
                    }
                    break;
                case 2:
                    for (devRcdCnt = 0, i = 0; i < 2; i++) {
                        devRcdCnt <<= 8;
                        devRcdCnt += rcdbuffer[i + 33];
                    }
                    for (devRcdOriMoney = 0, i = 0; i < 3; i++) {
                        devRcdOriMoney <<= 8;
                        devRcdOriMoney += rcdbuffer[i + 36];
                    }
                    for (devRcdSub = 0, i = 0; i < 3; i++) {
                        devRcdSub <<= 8;
                        devRcdSub += rcdbuffer[i + 39];
                    }

                    if (cinfo.iYueCount == devRcdCnt + 1) {
                        cardOpDU.ucRcdType = (byte) (rcdbuffer[2] - 1);
                        cardOpDU.procSec = 7;
                        cardOpDU.yueOriMoney = devRcdOriMoney;
                        cardOpDU.yueSub = devRcdSub;
                        fErr = 0;
//                            memcpy( & RcdDu.RcdBuffer[2], &rcdbuffer[2], 62);
//                            OnAppendRecordTrade(carddu.ucRcdType);
                        cardOpDU.fInBus = 1;
                        return new CardBackBean(ReturnVal.CAD_OK, cardOpDU);
                    }
                    //else if(memcmp(CInfo.ucYueCount, &RcdBuffer[33], 2))
                    else if (cinfo.iYueCount == devRcdCnt) {
                        fErr = 0;
                    } else {
                        fErr = 0;
                        //					return CAD_BROKEN;
                    }
                    break;
                default:
                    break;
            }
        }

        ret = CardMethods.fIsUseYue(cardOpDU, runParaFile);
        if (ret == 1)//月票有效
        {
            ret = mifareCardGetYueBasePos(mBankCard);
            if (ret == 255) {
                return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
            }
            if (ret == 0) {
                cardOpDU.procSec = 2;
            } else {
                cardOpDU.procSec = cardOpDU.yueSec;
            }
        } else {
            cardOpDU.procSec = 2;
        }

        cardOpDU.purCount = cinfo.iPurCount;
        cardOpDU.yueCount = cinfo.iYueCount;

        blk = cinfo.cPtr == 0 ? 8 : cinfo.cPtr - 1;
        blk = blk / 3 * 4 + blk % 3 + 12;

        byte[] lodkey6 = lodkey[blk / 4];
        retvalue = mBankCard.m1CardKeyAuth(0x41, blk + secOffset * 4,
                lodkey6.length, lodkey6, snUid.length, snUid);
        if (retvalue != 0) {
            LogUtils.e(blk + "===认证扇区失败===");
            return new CardBackBean(ReturnVal.CAD_ACCESS, cardOpDU);
        }
        retvalue = mBankCard.m1CardReadBlockData(blk + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("=== 读取失败==");
            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
        }

        cardOpDU.fPermit = 0;
        byte[] timeLimit = runParaFile.getUcBusYueTimeLimit();//5;

        if (recordInfo[0] == (byte) 0x01) {
            System.arraycopy(recordInfo, 1, record, 0, 64);
            //正常消费记录 00 02  灰记录 01 03	月票灰记录不判断
            if (record[2] != (byte) 0x03) {

                //上次刷卡时间
                byte[] busLastBytes = Datautils.cutBytes(record, 24, 7);
                String busLastStr = Datautils.byteArrayToString(busLastBytes);
                // 连续刷卡限制时间
                int time = Datautils.byteArrayToInt(runParaFile.getUcOldCardTimeLimit());
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                try {
                    Date busLast = format.parse(busLastStr);
                    Date dateTime = format.parse(Datautils.byteArrayToString(cardOpDU.ucDateTime));
                    Date busLastAfter = new Date(busLast.getTime() + time * 60000);
                    Date dateTimeAfter = new Date(dateTime.getTime() + time * 60000);
                    int compare = dateTime.compareTo(busLastAfter);
                    int compare1 = busLast.compareTo(dateTimeAfter);
                    if (compare < 0 && compare1 < 0) {
                        cardOpDU.procSec = 2; //5分钟后，且已经正常消费过，消费钱包
                        cardOpDU.fPermit = 1;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                }
            }
        }

        return busPurse();
    }

    private int fErr = -1;
    //所有“交易记录”块
    private byte rcdBlkIndex[] = {12, 13, 14, 16, 17, 18, 20, 21, 22};


    public CardBackBean busPurse() throws Exception {
        if (cardOpDU.procSec == 2) {
            cardOpDU.purorimoneyInt = 0;
            cardOpDU.pursubInt = 0;
            int ret = CardMethods.fIsUsePur(cardOpDU, runParaFile);
            if (ret == 0) {
                // mifs_halt();
                return new CardBackBean(ReturnVal.CAD_EMPTY, cardOpDU);
            }
        }

        byte[] lodkey6 = lodkey[cardOpDU.procSec];
        retvalue = mBankCard.m1CardKeyAuth(0x41, cardOpDU.procSec * 4 + secOffset * 4,
                lodkey6.length, lodkey6, snUid.length, snUid);
        if (retvalue != 0) {
            LogUtils.e(cardOpDU.procSec * 4 + "===认证扇区失败===");
            return new CardBackBean(ReturnVal.CAD_ACCESS, cardOpDU);
        }
        if (cardOpDU.procSec == 2) {
            retvalue = mBankCard.m1CardReadBlockData(cardOpDU.procSec * 4 + secOffset * 4, respdata, resplen);
            if (retvalue != 0) {
                LogUtils.e("=== 读取失败==");
                return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
            }
            byte[] rcdInCard = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
            cardOpDU.ucIncPurDev = Datautils.cutBytes(rcdInCard, 8, 4);
        }

        int ret = backupManage(cardOpDU.procSec + secOffset);//_dt and _backup are all wrong
        if ((ret == 255) || (ret == 3)) {
            return new CardBackBean(ReturnVal.CAD_BROKEN, cardOpDU);
        }

        for (int i = 0; i < 4; i++)                 //_backup must be ok
        {
            actRemaining <<= 8;
            actRemaining += dtZ[3 - i];
        }
        if (cardOpDU.procSec != 2) {
            cardOpDU.actYueOriMoney = actRemaining;
            ret = judgeYueScope();
            if (ret == 255) {
                return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
            }
            if (ret == F_OVERFLOW) {
                return new CardBackBean(ReturnVal.CAD_BROKEN, cardOpDU);
            }
            if (ret == F_LEAST) {
                cardOpDU.procSec = 2;
                busPurse();
            }
//            System.arraycopy(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}
//                    , 0, cardOpDU.ucRcdToCard, 20, 5);


        } else {//carddu.ProcSec==2
            cardOpDU.purorimoneyInt = actRemaining;
            cardOpDU.pursubInt = CardMethods.getRadioPurSub(cardOpDU, runParaFile);
            if (cardOpDU.purorimoneyInt >= 100000) {
                return new CardBackBean(ReturnVal.CAD_BROKEN, cardOpDU);
            }
            if (cardOpDU.purorimoneyInt < cardOpDU.pursubInt) {
                return new CardBackBean(ReturnVal.CAD_EMPTY, cardOpDU);
            }
//            System.arraycopy(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}
//                    , 0, cardOpDU.ucRcdToCard, 20, 5);
        }

        ///////////ExchangeFlow////////////////////////////////////////////////////////////////
        if (cardOpDU.procSec == 2) {
            cardOpDU.ucRcdType = (byte) 0x00;
        } else {
            cardOpDU.ucRcdType = (byte) 0x02;
        }

        //获取UTC时间
        byte[] ulDevUTC = Datautils.HexString2Bytes(TimeDataUtils.getUTCtimes());
        //当前交易记录块
//        int jyk = rcdBlkIndex[cinfo.cPtr];

        System.arraycopy(ulDevUTC, 0, cardOpDU.ucRcdToCard, 0, 4);
        if (cardOpDU.procSec == 2) {
            cardOpDU.ucRcdToCard[4] = (byte) (cardOpDU.purorimoneyInt >> 24);
            cardOpDU.ucRcdToCard[5] = (byte) (cardOpDU.purorimoneyInt >> 16);
            cardOpDU.ucRcdToCard[6] = (byte) (cardOpDU.purorimoneyInt >> 8);
            cardOpDU.ucRcdToCard[7] = (byte) cardOpDU.purorimoneyInt;
            cardOpDU.ucRcdToCard[8] = (byte) (cardOpDU.pursubInt >> 16);
            cardOpDU.ucRcdToCard[9] = (byte) (cardOpDU.pursubInt >> 8);
            cardOpDU.ucRcdToCard[10] = (byte) cardOpDU.pursubInt;
            cinfo.fProc = (byte) 0x01;
            cardOpDU.ucRcdToCard[11] = (byte) ((cinfo.fProc + 1) / 2);
            cinfo.iPurCount = (char) (cinfo.iPurCount + 1);
            cinfo.fFileNr = (byte) 0x10;
        } else {
            cardOpDU.ucRcdToCard[4] = (byte) (cardOpDU.yueOriMoney >> 24);
            cardOpDU.ucRcdToCard[5] = (byte) (cardOpDU.yueOriMoney >> 16);
            cardOpDU.ucRcdToCard[6] = (byte) (cardOpDU.yueOriMoney >> 8);
            cardOpDU.ucRcdToCard[7] = (byte) cardOpDU.yueOriMoney;
            cardOpDU.ucRcdToCard[8] = (byte) (cardOpDU.yueSub >> 16);
            cardOpDU.ucRcdToCard[9] = (byte) (cardOpDU.yueSub >> 8);
            cardOpDU.ucRcdToCard[10] = (byte) cardOpDU.yueSub;
            int fProc = (cardOpDU.procSec - 7) * 2 + 3;
            cinfo.fProc = (byte) fProc;//3,5,7,9
            cardOpDU.ucRcdToCard[11] = (byte) ((cinfo.fProc + 1) / 2);
            cinfo.iYueCount = (char) (cinfo.iYueCount + 1);
            cinfo.fFileNr = (byte) 0x11;
        }
        // TODO: 2019/3/24 测试
        System.arraycopy(new byte[]{(byte) 0x30, (byte) 0x00, (byte) 0x12, (byte) 0x34}, 0
                , cardOpDU.ucRcdToCard, 12, 4);
//        System.arraycopy(runParaFile.getDevNr(), 0
//                , cardOpDU.ucRcdToCard, 12, 4);

        while (true) {
            try {
                if (cinfo.cPtr > 8) {
                    cinfo.cPtr = 0;
                }
                cinfo.cPtr = (byte) (cinfo.cPtr == 8 ? 0 : cinfo.cPtr + 1);
                //step 1 改写24 25块数据
                if (!CardMethods.modifyInfoArea(24, cinfo, mBankCard, lodkey, snUid)) {
                    LogUtils.e("writeCardRcd: 改写24块错误");
                    return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                }

                blk = cinfo.cPtr == 0 ? 8 : cinfo.cPtr - 1;
                blk = blk / 3 * 4 + blk % 3 + 12;
                //step 2//blk/4 区    blk块
                if (!m1CardKeyAuth(blk, blk / 4)) {
                    return new CardBackBean(ReturnVal.CAD_ACCESS, cardOpDU);
                }
                //写卡  将消费记录写入消费记录区
                retvalue = mBankCard.m1CardWriteBlockData(blk + secOffset * 4
                        , cardOpDU.ucRcdToCard.length
                        , cardOpDU.ucRcdToCard);
                if (retvalue != 0) {
                    LogUtils.e("writeCardRcd: 将消费记录写入消费记录区错误 块为" + blk);
                    return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                }
                //消费记录区读取
                retvalue = mBankCard.m1CardReadBlockData(blk + secOffset * 4, respdata, resplen);
                if (retvalue != 0) {
                    LogUtils.e("writeCardRcd: 读取消费记录区错误");
                    return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                }
                byte[] rcdInCard = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
                LogUtils.d("writeCardRcd: 读当前消费记录区数据：" + Datautils.byteArrayToString(rcdInCard));
                if (!Arrays.equals(rcdInCard, cardOpDU.ucRcdToCard)) {
                    LogUtils.e("writeCardRcd: 读数据不等于消费返回错误");
                    return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                }

                if (cardOpDU.procSec == 2) {
                    cardOpDU.ucRcdType = (byte) 0x00;
                } else {
                    cardOpDU.ucRcdType = (byte) 0x02;
                }
//                PrepareRecordTrade(carddu.ucRcdType + 1);
//                fErr = 1;

//                byte[] bytes = new byte[16];
//                //判断是否 读回==00
//                if (Arrays.equals(RcdInCard, bytes)) {
//                    LogUtils.e(TAG, "writeCardRcd: 读数据不等于消费返回0错误");
//                    return false;
//                }

                //step 3
//            PrepareRecord(tCardOpDu.ucSec == 2 ? 1 : 3);   1代表 钱包灰记录 3 月票灰记录
                fErr = 1;
                if (!CardMethods.modifyInfoArea(25, cinfo, mBankCard, lodkey, snUid)) {
                    LogUtils.e("writeCardRcd: 改写25块错误");
                    // 改写25块，不成功退出
                    break;
                }
                //step 4//认证2扇区8块
                if (!m1CardKeyAuth(cardOpDU.procSec * 4 + secOffset * 4
                        , cardOpDU.procSec + secOffset)) {
                    break;
                }
                //执行消费 将消费金额带入
                int money;
                if (cardOpDU.procSec != 2) {
                    money = cardOpDU.actYueSub;
                } else {
                    money = cardOpDU.pursubInt;
                }

//                int purSub = Datautils.byteArrayToInt(icCardBeen.getPursub());
                retvalue = mBankCard.m1CardValueOperation(0x2D
                        , (cardOpDU.procSec * 4) + 1, money, 9);
                if (retvalue != 0) {
                    LogUtils.e("writeCardRcd: 执行消费错误");
                    break;
                }
                //执行 读出 现在原额
                retvalue = mBankCard.m1CardReadBlockData((cardOpDU.procSec * 4) + 1
                        , respdata, resplen);
                if (retvalue != 0) {
                    LogUtils.e("writeCardRcd: 读原额错误");
                    break;
                }
                dtZ = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
                //本次消费后的原额;
                int tempV = 0, expV, i;
                for (i = 0; i < 4; i++) {
                    tempV <<= 8;
                    tempV += dtZ[3 - i];
                }
                if ((cardOpDU.procSec != 2) && (cardOpDU.procSec != 11)) {
                    expV = cardOpDU.actYueOriMoney - cardOpDU.actYueSub;
                } else {
                    expV = cardOpDU.purorimoneyInt - cardOpDU.pursubInt;
                }
                if (tempV != expV) {
                    break;
                }


//                byte[] dtZ = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
//                byte[] tempV = Datautils.cutBytes(dtZ, 0, 4);
//                LogUtils.d("writeCardRcd:正本读09块返回：" + Datautils.byteArrayToString(dtZ));
//                //判断消费前金额-消费金额=消费后金额
//                int s = Datautils.byteArrayToInt(icCardBeen.getPurorimoney(), false);
//                int s2 = Datautils.byteArrayToInt(tempV, false);
//                if (s - purSub != s2) {
//                    break;
//                }
                //step 6
                retvalue = mBankCard.m1CardValueOperation(0x3E,
                        (cardOpDU.procSec * 4) + 1, Datautils.byteArrayToInt(dtZ)
                        , (cardOpDU.procSec * 4) + 2);
                if (retvalue != 0) {
                    LogUtils.e("writeCardRcd: 写10块错误");
                    break;
                }
                retvalue = mBankCard.m1CardReadBlockData((cardOpDU.procSec * 4) + 2
                        , respdata, resplen);
                if (retvalue != 0) {
                    LogUtils.e("writeCardRcd: 读10块错误");
                    break;
                }
                //本次消费后的原额
                byte[] dtF = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
                LogUtils.d("writeCardRcd: 副本读10块返回：" + Datautils.byteArrayToString(dtF));
                if (!Arrays.equals(dtF, dtZ)) {
                    LogUtils.d("writeCardRcd: 正副本判断返回");
                    break;
                }
                //step 7
                cinfo.fProc += 1;
                if (!CardMethods.modifyInfoArea(24, cinfo, mBankCard, lodkey, snUid)) {
                    LogUtils.e("writeCardRcd: 改写24错误");
                    break;
                }
                //step 8
                fErr = 0;
                if (!CardMethods.modifyInfoArea(25, cinfo, mBankCard, lodkey, snUid)) {
                    LogUtils.e("writeCardRcd: 改写25错误");
                    break;
                }
                break;
            } catch (RemoteException e) {
                e.printStackTrace();
                return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
            }
        }
        if (fErr == 1) {
            //添加灰记录 报语音请重刷
            CardMethods.onAppendRecordTrade((byte) (cardOpDU.ucRcdType + 1)
                    , cardOpDU, runParaFile, psamBeenList);
            return new CardBackBean(ReturnVal.CAD_RETRY, cardOpDU);
        }
        //添加正常交易记录 报语音显示界面
        CardMethods.onAppendRecordTrade(cardOpDU.ucRcdType, cardOpDU, runParaFile, psamBeenList);
        return new CardBackBean(ReturnVal.CAD_OK, cardOpDU);
    }


    public CardBackBean cITYCard() throws Exception {
        byte[] rcdbuffer = new byte[64];
        int i, actRemaining = 0, cardOriMoney;
        int money, fYueDisable, fOldDisable;
        int chk, ret;
        int ulV = 0, lVf, lVz;
        int blk = 0;
        cardOpDU.ucOtherCity = (byte) 0x00;
        //系统时间
        byte[] systemTime = Datautils.getDateTime();
        //获取UTC时间
        byte[] ulDevUTC = Datautils.HexString2Bytes(TimeDataUtils.getUTCtimes());
        cardOpDU.ucDateTime = systemTime;
        Long timeToLong = DateUtils.convertTimeToLong(DateUtils.FORMAT_yyyyMMddHHmmss
                , Datautils.byteArrayToString(cardOpDU.ucDateTime));
        String ulUTCString = Long.toHexString(timeToLong / 1000).toUpperCase();
        cardOpDU.ucDateTimeUTC = Datautils.HexString2Bytes(ulUTCString);
        fSysSta = CAD_NORMAL;

        if (fSysSta == CAD_PAUSE) {
            fSysSta = CAD_NORMAL;
        }
        if (fSysSta != CAD_NORMAL) {
            long dbCount = DbDaoManage.getDaoSession().getCardRecordDao().count();
            CardRecord cardRecord = DbDaoManage.getDaoSession().getCardRecordDao().loadByRowId(dbCount);
            rcdbuffer = cardRecord.getRecord();
            if (Arrays.equals(cardOpDU.snr, Datautils.cutBytes(rcdbuffer, 7, 4))) {
                if (fSysSta == CAD_PAUSEA) {
                    while (true) {
                        cinfo.fProc = (byte) ((cinfo.fProc + 1) & 0xfe);
                        if (!pauseA()) {
                            break;
                        }
                        if (!pauseB(blk)) {
                            break;
                        }
                        if (!pauseC()) {
                            break;
                        }
                    }

                    // 当前卡扣款异常,请重刷处理
                    if (fSysSta == CAD_PAUSEA) {
                        // TODO: 2019/3/22 写记录
                        CardMethods.onAppendRecordTrade(cardOpDU.procSec == 2 ? (byte) 0x01 : (byte) 0x03
                                , cardOpDU, runParaFile, psamBeenList);
                        // 添加钱包灰纪录或月票灰纪录
                        // 返回RETRY错，提示请重刷
                        return new CardBackBean(ReturnVal.CAD_RETRY, cardOpDU);
                    }
                } else if (fSysSta == CAD_PAUSEB) {
                    while (true) {

                        if (!pauseB(blk)) {
                            break;
                        }
                        if (!pauseC()) {
                            break;
                        }
                    }

                    // 当前卡扣款异常,请重刷处理
                    if (fSysSta == CAD_PAUSEA) {
                        // TODO: 2019/3/22 写记录
                        CardMethods.onAppendRecordTrade(cardOpDU.procSec == 2 ? (byte) 0x01 : (byte) 0x03
                                , cardOpDU, runParaFile, psamBeenList);
                        // 添加钱包灰纪录或月票灰纪录
                        // 返回RETRY错，提示请重刷
                        return new CardBackBean(ReturnVal.CAD_RETRY, cardOpDU);
                    }
                } else if (fSysSta == CAD_PAUSEC) {
                    while (true) {
                        if (!pauseC()) {
                            break;
                        }
                    }

                    // 当前卡扣款异常,请重刷处理
                    if (fSysSta == CAD_PAUSEA) {
                        // TODO: 2019/3/22 写记录
                        CardMethods.onAppendRecordTrade(cardOpDU.procSec == 2 ? (byte) 0x01 : (byte) 0x03
                                , cardOpDU, runParaFile, psamBeenList);
                        // 添加钱包灰纪录或月票灰纪录
                        // 返回RETRY错，提示请重刷
                        return new CardBackBean(ReturnVal.CAD_RETRY, cardOpDU);
                    }
                } else if (fSysSta == CAD_PAUSE0) {
                    if (!CardMethods.modifyInfoArea(cardOpDU.procSec + secOffset,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_ACCESS, cardOpDU);
                    }

                    //消费记录区读取
                    retvalue = mBankCard.m1CardReadBlockData((cardOpDU.procSec
                            + secOffset) * 4 + 1, respdata, resplen);
                    if (retvalue != 0) {
                        LogUtils.e("writeCardRcd: 读取消费记录区错误");
                        return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                    }
                    dtZ = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
                    if (valueBlockValid(dtZ)) {
                        retvalue = mBankCard.m1CardValueOperation(0x3E
                                , (cardOpDU.procSec + secOffset) * 4 + 2
                                , 0, (cardOpDU.procSec + secOffset) * 4 + 1);
                        if (retvalue != 0) {
                            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                        }
                        //消费记录区读取
                        retvalue = mBankCard.m1CardReadBlockData((cardOpDU.procSec
                                + secOffset) * 4 + 1, respdata, resplen);
                        if (retvalue != 0) {
                            LogUtils.e("writeCardRcd: 读取消费记录区错误");
                            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                        }
                        dtF = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
                        if (!Arrays.equals(dtF, dtZ)) {
                            LogUtils.d("writeCardRcd: 正副本判断返回");
                            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                        }
                    }

                    for (i = 0; i < 4; i++) {
                        actRemaining <<= 8;
                        actRemaining += dtZ[3 - i];
                    }    //_backup must be ok
                    cardOriMoney = actRemaining;
                    if (cardOpDU.procSec != 2) {
                        if (cardOriMoney == cardOpDU.actYueOriMoney - cardOpDU.actYueSub)//减成功
                        {
                            while (true) {
                                if (!pause1(blk)) {
                                    break;
                                }
                                if (!pause2()) {
                                    break;
                                }
                            }
                            // 当前卡扣款异常,请重刷处理
                            if (fSysSta == CAD_PAUSE0) {
                                // 添加钱包灰记录或月票灰记录
                                // TODO: 2019/3/22
                                CardMethods.onAppendRecordTrade(cardOpDU.procSec == 2 ? (byte) 0x01 : (byte) 0x03
                                        , cardOpDU, runParaFile, psamBeenList);
                                //返回RETRY错，提示请重刷
                                return new CardBackBean(ReturnVal.CAD_RETRY, cardOpDU);
                            }
                        } else if (cardOriMoney == cardOpDU.actYueOriMoney)//未减成功
                        {
                            while (true) {
                                if (!pause0()) {
                                    break;
                                }
                                if (!pause1(blk)) {
                                    break;
                                }
                                if (!pause2()) {
                                    break;
                                }
                            }
                            // 当前卡扣款异常,请重刷处理
                            if (fSysSta == CAD_PAUSE0) {
                                // 添加钱包灰记录或月票灰记录
                                // TODO: 2019/3/22
                                CardMethods.onAppendRecordTrade(cardOpDU.procSec == 2 ? (byte) 0x01 : (byte) 0x03
                                        , cardOpDU, runParaFile, psamBeenList);
                                //返回RETRY错，提示请重刷
                                return new CardBackBean(ReturnVal.CAD_RETRY, cardOpDU);
                            }
                        }
                    } else {
                        if (cardOriMoney == cardOpDU.purorimoneyInt - cardOpDU.pursubInt)//减成功
                        {
                            while (true) {
                                if (!pause1(blk)) {
                                    break;
                                }
                                if (!pause2()) {
                                    break;
                                }
                            }
                            // 当前卡扣款异常,请重刷处理
                            if (fSysSta == CAD_PAUSE0) {
                                // 添加钱包灰记录或月票灰记录
                                // TODO: 2019/3/22
                                CardMethods.onAppendRecordTrade(cardOpDU.procSec == 2 ? (byte) 0x01 : (byte) 0x03
                                        , cardOpDU, runParaFile, psamBeenList);
                                //返回RETRY错，提示请重刷
                                return new CardBackBean(ReturnVal.CAD_RETRY, cardOpDU);
                            }
                        } else if (cardOriMoney == cardOpDU.purorimoneyInt)//未减成功
                        {
                            while (true) {
                                if (!pause0()) {
                                    break;
                                }
                                if (!pause1(blk)) {
                                    break;
                                }
                                if (!pause2()) {
                                    break;
                                }
                            }
                            // 当前卡扣款异常,请重刷处理
                            if (fSysSta == CAD_PAUSE0) {
                                // 添加钱包灰记录或月票灰记录
                                // TODO: 2019/3/22
                                CardMethods.onAppendRecordTrade(cardOpDU.procSec == 2 ? (byte) 0x01 : (byte) 0x03
                                        , cardOpDU, runParaFile, psamBeenList);
                                //返回RETRY错，提示请重刷
                                return new CardBackBean(ReturnVal.CAD_RETRY, cardOpDU);
                            }
                        }
                    }

                } else if (fSysSta == CAD_PAUSE1) {
                    while (true) {
                        if (!pause1(blk)) {
                            break;
                        }
                        if (!pause2()) {
                            break;
                        }
                    }
                    // 当前卡扣款异常,请重刷处理
                    if (fSysSta == CAD_PAUSE0) {
                        // 添加钱包灰记录或月票灰记录
                        // TODO: 2019/3/22
                        CardMethods.onAppendRecordTrade(cardOpDU.procSec == 2 ? (byte) 0x01 : (byte) 0x03
                                , cardOpDU, runParaFile, psamBeenList);
                        //返回RETRY错，提示请重刷
                        return new CardBackBean(ReturnVal.CAD_RETRY, cardOpDU);
                    }
                } else if (fSysSta == CAD_PAUSE2) {
                    while (true) {
                        if (!pause2()) {
                            break;
                        }
                    }
                    // 当前卡扣款异常,请重刷处理
                    if (fSysSta == CAD_PAUSE0) {
                        // 添加钱包灰记录或月票灰记录
                        // TODO: 2019/3/22
                        CardMethods.onAppendRecordTrade(cardOpDU.procSec == 2 ? (byte) 0x01 : (byte) 0x03
                                , cardOpDU, runParaFile, psamBeenList);
                        //返回RETRY错，提示请重刷
                        return new CardBackBean(ReturnVal.CAD_RETRY, cardOpDU);
                    }
                }
            } else {
                //非同一张卡
                fSysSta = CAD_NORMAL;
//                return new CardBackBean(ReturnVal.CAD_NOTSAME, cardOpDU);
            }

        }

        //主卡类型不再限定,0x00普通卡,0x01老人卡0x02~0x05备用,0x06测试卡0x07联名卡0x08纪念卡09~0xFF备用
        // 卡发行日期
        cardOpDU.issueDate = Datautils.cutBytes(cardOpDU.ucBlk5, 0, 4);
        cardOpDU.endUserDate = Datautils.cutBytes(cardOpDU.ucBlk5, 4, 4);
        cardOpDU.startUserDate = Datautils.cutBytes(cardOpDU.ucBlk5, 8, 4);
        cardOpDU.ucCheckDate = Datautils.concatAll(new byte[]{(byte) 0x20}
                , Datautils.cutBytes(cardOpDU.ucBlk5, 12, 3));
        cardOpDU.fStartUsePur = (byte) 0x01;
        //老人卡,残疾人卡
        if ((cardOpDU.cardType == (byte) 0x01) || (cardOpDU.cardType == (byte) 0x02)
                || (cardOpDU.cardType == (byte) 0x11)) {

        } else {

            byte[] ucDateTimeByte = Datautils.cutBytes(cardOpDU.ucDateTime, 1, 3);
            int ucDateTimeInt = Integer.parseInt(Datautils.byteArrayToString(ucDateTimeByte));
            int ucAppStartDateInt = Integer.parseInt(Datautils.byteArrayToString(cardOpDU.startUserDate));
            //当前时间小于开始时间
            if (ucDateTimeInt < ucAppStartDateInt) {
                return new CardBackBean(ReturnVal.CAD_SELL, cardOpDU);
            }
            int ucAppEndDateInt = Integer.parseInt(Datautils.byteArrayToString(cardOpDU.endUserDate));
            if (ucDateTimeInt > ucAppEndDateInt) {
                return new CardBackBean(ReturnVal.CAD_EXPIRE, cardOpDU);
            }

        }

        cardOpDU.fBlackCard = false;
        cardOpDU.fStartUsePur = (byte) 0x01;
        fYueDisable = 0;
        fOldDisable = 0;
        //查询数据库最近一条记录，是否是相同的卡号
        long dbCount = DbDaoManage.getDaoSession().getCardRecordDao().count();
        CardRecord cardRecord = DbDaoManage.getDaoSession().getCardRecordDao().loadByRowId(dbCount);
        rcdbuffer = cardRecord.getRecord();
        if (Arrays.equals(cardOpDU.snr, Datautils.cutBytes(rcdbuffer, 7, 4))) {
            if ((rcdbuffer[2] & (byte) 0x01) == (byte) 0x00) {

                //上次刷卡时间
                byte[] busLastBytes = Datautils.cutBytes(rcdbuffer, 24, 7);
                String busLastStr = Datautils.byteArrayToString(busLastBytes);
                // 连续刷卡限制时间
                int time = Datautils.byteArrayToInt(runParaFile.getUcCityYueTimeLimit());
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                try {
                    Date busLast = format.parse(busLastStr);
                    Date dateTime = format.parse(Datautils.byteArrayToString(cardOpDU.ucDateTime));
                    Date busLastAfter = new Date(busLast.getTime() + time * 60000);
                    Date dateTimeAfter = new Date(dateTime.getTime() + time * 60000);
                    int compare = dateTime.compareTo(busLastAfter);
                    int compare1 = busLast.compareTo(dateTimeAfter);
                    if (compare < 0 && compare1 < 0) {
                        fYueDisable = 1;
                    }
                    time = Datautils.byteArrayToInt(runParaFile.getUcOldCardTimeLimit());

                    busLast = format.parse(busLastStr);
                    dateTime = format.parse(Datautils.byteArrayToString(cardOpDU.ucDateTime));
                    busLastAfter = new Date(busLast.getTime() + time * 60000);
                    dateTimeAfter = new Date(dateTime.getTime() + time * 60000);
                    compare = dateTime.compareTo(busLastAfter);
                    compare1 = busLast.compareTo(dateTimeAfter);
                    if (compare < 0 && compare1 < 0) {
                        fOldDisable = 1;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        cardOpDU.ucIncPurDate = Datautils.cutBytes(cardOpDU.ucBlk6, 0, 3);
        cardOpDU.ucIncPurDev = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        cardOpDU.uiIncPurCount = new byte[]{(byte) 0x00, (byte) 0x00};

        if (!m1CardKeyAuth(6 * 4 + secOffset * 4, 6)) {
            return new CardBackBean(ReturnVal.CAD_ACCESS, cardOpDU);
        }
        //消费记录区读取
        retvalue = mBankCard.m1CardReadBlockData(26 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("writeCardRcd: 读取消费记录区错误");
            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
        }

        retvalue = mBankCard.m1CardReadBlockData(24 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("writeCardRcd: 读取消费记录区错误");
            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
        }
        // 读信息区0块,失败返回CAD_READ
        dtZ = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        for (chk = 0, i = 0; i < 16; i++) {
            // 计算BCC,判断信息正本有效性
            chk ^= dtZ[i];
        }
        cinfoz.fValid = (byte) (((chk == 0) && (dtZ[7] != 0)) ? 1 : 0);
        byte[] cPtrZ = Datautils.cutBytes(dtZ, 0, 16);

        cinfoz.iPurCount = (char) ((cinfoz.ucPurCount[0] << 8) + cinfoz.ucPurCount[1]);
        cinfoz.iYueCount = (char) ((cinfoz.ucYueCount[0] << 8) + cinfoz.ucYueCount[1]);

        retvalue = mBankCard.m1CardReadBlockData(25 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("writeCardRcd: 读取消费记录区错误");
            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
        }
        dtF = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        for (chk = 0, i = 0; i < 16; i++) {
            chk ^= dtF[i];
        }
        cinfof.fValid = (byte) (((chk == 0) && (dtF[7] != 0)) ? 1 : 0);
        byte[] cPtrF = Datautils.cutBytes(dtF, 0, 16);
        cinfof.iPurCount = (char) ((cinfof.ucPurCount[0] << 8) + cinfof.ucPurCount[1]);
        cinfof.iYueCount = (char) ((cinfof.ucYueCount[0] << 8) + cinfof.ucYueCount[1]);

        int fJudge = 0;                                                  //预置恢复判断标志 = 0
        if (cinfoz.fValid == (byte) 0x01)                                      //信息正本有效：
        {
            fJudge |= 0x80;                                               // 恢复判断标志第7位=1；
            if ((dtZ[3] & 0x01) == 0x01) {
                fJudge |= 0x20;                       // 进程=奇数，恢复判断标志第5位=1；
            }
            // 使用信息区正本为信息工作区
            cinfo = cinfoz;

        }

        if (cinfof.fValid == 1)                                      // 信息副本有效：
        {
            fJudge |= 0x40;                                               // 恢复判断标志第6位=1；
            if (cinfoz.fValid == (byte) 0x00) {
                cinfo = cinfof;
            }
            // 如正本无效，则使用信息区副本为信息工作区
        }

        if ((cinfoz.fValid == (byte) 0x01) && Arrays.equals(dtZ, dtF)) {
            fJudge |= 0x10;
        }

        if ((fJudge & 0x10) != 0x10) {
            // 信息正本副本均无效，坏卡
            if (fJudge == 0) {
                // 初始恢复失败才返回CAD_BROKEN坏卡
                return new CardBackBean(ReturnVal.CAD_BROKEN, cardOpDU);
            } else if (fJudge == 0xE0)//A2,正本进程标志为奇数
            {
                if (cinfoz.fFileNr == (byte) 0x11) {
                    cardOpDU.procSec = 7;
                } else if (cinfoz.fFileNr == (byte) 0x12) {
                    cardOpDU.procSec = 8;
                } else {
                    cardOpDU.procSec = 2;
                }
                //正本有可能无效,副本默认有效
                ret = sToBManage(cardOpDU.procSec + secOffset, dtZ, dtF);
                if (ret == 255) {
                    return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                }
                if ((ret == 0) || (ret == 2))//正本大于等于副本或副本有效,以副本恢复了正本
                {
                    cinfo = cinfof;
                    if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_ACCESS, cardOpDU);
                    }
                    cinfoz = cinfo;
                } else {//ret==1 正本小于副本
                    //写卡片交易记录
                    for (i = 0; i < 4; i++) {
                        ulV <<= 8;
                        ulV += dtZ[3 - i];
                    }
                    lVz = ulV;
                    for (i = 0; i < 4; i++) {
                        ulV <<= 8;
                        ulV += dtF[3 - i];
                    }
                    lVf = ulV;
                    // 计算卡记录块号
                    blk = rcdBlkIndex[cinfo.cPtr - 1];
                    // 认证卡内记录扇区
                    if (!m1CardKeyAuth(blk + secOffset * 4, blk / 4 + secOffset)) {
                        return new CardBackBean(ReturnVal.CAD_ACCESS, cardOpDU);
                    }
                    // 刷卡日时分秒
                    byte[] rcdToCard = new byte[16];
                    System.arraycopy(cardOpDU.ucDateTime, 2, rcdToCard, 0, 4);
                    // 刷卡设备号
                    System.arraycopy(runParaFile.getDevNr(), 0, rcdToCard, 12, 4);
                    // 取原额，逆序，4B
                    rcdToCard[4] = (byte) lVf;
                    rcdToCard[5] = (byte) (lVf >> 8);
                    rcdToCard[6] = (byte) (lVf >> 16);
                    rcdToCard[7] = (byte) (lVf >> 24);
                    rcdToCard[8] = (byte) (lVf - lVz);                                        // 消费额，逆序，3B
                    rcdToCard[9] = (byte) ((lVf - lVz) >> 8);
                    rcdToCard[10] = (byte) ((lVf - lVz) >> 16);
                    if (cardOpDU.procSec == 8)                                        // 消费月票
                    {
                        rcdToCard[11] = (byte) 0x12;                                          // 记录类型=0x12
                    } else if (cardOpDU.procSec == 7) {
                        rcdToCard[11] = (byte) 0x10;                                          // 记录类型=0x10
                    } else                               // 钱包消费
                    {
                        rcdToCard[11] = (byte) 0x01;                                          // 记录类型=0x01
                    }

                    // 写卡内记录
                    retvalue = mBankCard.m1CardWriteBlockData(blk + secOffset * 4
                            , cardOpDU.ucRcdToCard.length, cardOpDU.ucRcdToCard);
                    if (retvalue != 0) {
                        return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                    }
                    //消费记录区读取
                    retvalue = mBankCard.m1CardReadBlockData(blk + secOffset * 4
                            , respdata, resplen);
                    if (retvalue != 0) {
                        LogUtils.e("writeCardRcd: 读取消费记录区错误");
                        return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                    }
                    byte[] rcdInCard = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
                    if (!Arrays.equals(rcdToCard, rcdInCard)) {
                        return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                    }


                    cinfo.fProc = (byte) ((cinfo.fProc + 1) & 0xfe);// 进程+1

                    if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                    }

                    // 认证卡内记录扇区
                    if (!m1CardKeyAuth((cardOpDU.procSec + secOffset) * 4,
                            cardOpDU.procSec + secOffset)) {
                        return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                    }

                    // 取钱包/月票正本余额
                    retvalue = mBankCard.m1CardValueOperation(0x3E
                            , (cardOpDU.procSec + secOffset) * 4 + 1
                            , 0, (cardOpDU.procSec + secOffset) * 4 + 2);
                    if (retvalue != 0) {
                        return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                    }
                    // 传输到钱包/月票副本

                    retvalue = mBankCard.m1CardReadBlockData(4 * cardOpDU.procSec
                            + secOffset * 4 + 2, respdata, resplen);
                    if (retvalue != 0) {
                        LogUtils.e("writeCardRcd: 读取消费记录区错误");
                        return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                    }
                    dtF = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
                    // 不相符,退出,不再改写信息副本
                    if (!Arrays.equals(dtZ, dtF)) {
                        return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                    }
                    if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                    }
                    cinfoz = cinfo;
                }
            } else if (fJudge == 0xC0)//A1,正本进程标志为偶数
            {
                if (cinfoz.fFileNr == 0x11) {
                    cardOpDU.procSec = 7;
                } else if (cinfoz.fFileNr == 0x12) {
                    cardOpDU.procSec = 8;
                } else {
                    cardOpDU.procSec = 2;
                }
                ret = backupManage(cardOpDU.procSec + secOffset);//正常情况下正本,副本默认有效
                if (ret == 255) {
                    return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                }

                if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                        cinfo, mBankCard, lodkey, snUid)) {
                    return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                }
                //A0,正本副本只有一个有效
            } else if (fJudge == 0x80)//A0,1正本有效,副本无效,奇数位不考虑,正副本相同不考虑
            {
                if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                        cinfo, mBankCard, lodkey, snUid)) {
                    return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                }
                cinfof = cinfo;
                fJudge |= 0x10;
            } else {//fJudge==0x40//A0,2正本无效,副本有效,奇数位不考虑,正副本相同不考虑
                ret = 0;
                if (ret == 0) {
                    //正常情况下正本,副本默认有效
                    ret = backupManage(2 + secOffset);
                    if (ret == 255) {
                        return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                    }
                    if (ret == 1) {
                        cinfo.cPtr += 1;                                                  // 卡记录指针+1
                        if (cinfo.cPtr > 9) {
                            cinfo.cPtr = 1;
                        }
                        // 卡钱包计数器+1
                        cinfo.iPurCount = (char) (cinfo.iPurCount + 1);
                        if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                                cinfo, mBankCard, lodkey, snUid)) {
                            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                        }
                        if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                                cinfo, mBankCard, lodkey, snUid)) {
                            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                        }
                    }

                }
                if (ret == 0) {
                    ret = backupManage(7 + secOffset);//正常情况下正本,副本默认有效
                    if (ret == 255) {
                        return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                    }
                    if (ret == 1) {
                        cinfo.cPtr += 1;           // 卡记录指针+1
                        if (cinfo.cPtr > 9) {
                            cinfo.cPtr = 1;
                        }
                        // 卡钱包计数器+1
                        cinfo.iPurCount = (char) (cinfo.iPurCount + 1);
                        // 卡月票计数器+1
                        cinfo.iYueCount = (char) (cinfo.iYueCount + 1);
                        if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                                cinfo, mBankCard, lodkey, snUid)) {
                            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                        }
                        if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                                cinfo, mBankCard, lodkey, snUid)) {
                            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                        }
                    }

                }
                if (ret == 0) {
                    ret = backupManage(8 + secOffset);//正常情况下正本,副本默认有效
                    if (ret == 255) {
                        return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                    }
                    if (ret == 1) {
                        cinfo.cPtr += 1;                                                  // 卡记录指针+1
                        if (cinfo.cPtr > 9) {
                            cinfo.cPtr = 1;
                        }
                        // 卡钱包计数器+1
                        cinfo.iPurCount = (char) (cinfo.iPurCount + 1);
                        // 卡月票计数器+1
                        cinfo.iYueCount = (char) (cinfo.iYueCount + 1);
                        if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                                cinfo, mBankCard, lodkey, snUid)) {
                            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                        }
                        if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                                cinfo, mBankCard, lodkey, snUid)) {
                            return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                        }
                    }
                }
                // 使用信息区正本为信息工作区
                cinfoz = cinfo;
                if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                        cinfo, mBankCard, lodkey, snUid)) {
                    return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                }
            }

        } else {//公共信息区相等
            ret = 0;
            if (ret == 0) {
                ret = backupManage(7 + secOffset);//正常情况下正本,副本默认有效
                if (ret == 255) {
                    return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                }
            }
            if (ret == 0) {
                ret = backupManage(8 + secOffset);//正常情况下正本,副本默认有效
                if (ret == 255) {
                    return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                }
            }
        }

        if ((cinfoz.fValid == (byte) 0x01 && (cinfoz.fBlack == (byte) 0x04))
                || (cinfof.fValid == (byte) 0x01 && (cinfof.fBlack == (byte) 0x04))) {
            cardOpDU.fBlackCard = false;
            return new CardBackBean(ReturnVal.CAD_BL1, cardOpDU);
        }
        cardOpDU.purCount = cinfo.iPurCount;
        cardOpDU.yueCount = cinfo.iYueCount;

        if ((cardOpDU.cardType == (byte) 0x01) || (cardOpDU.cardType == (byte) 0x02)
                || (cardOpDU.cardType == (byte) 0x11))//老人卡
        {
            //不再涉及月票区，公交类型为0x01
            cardOpDU.subType = cardOpDU.cardType;
            cardOpDU.ucCheckDate = new byte[]{(byte) 0x20, (byte) 0x99, (byte) 0x12, (byte) 0x01};
            //判断年检期是否已过
            byte[] ucDateTimeByte = Datautils.cutBytes(cardOpDU.ucDateTime, 0, 4);
            int ucDateTimeInt = Integer.parseInt(Datautils.byteArrayToString(ucDateTimeByte));
            int ucCheckDateInt = Integer.parseInt("20991201");
            if (ucDateTimeInt <= ucCheckDateInt) {
                // 取信息正本数组
                cinfo = cinfoz;
                cinfo.fFileNr = (byte) 0x10;
                if (cinfo.cPtr > (byte) 0x09) {
                    cinfo.cPtr = (byte) 0x01;
                }
                blk = rcdBlkIndex[cinfo.cPtr - 1];
                if (!m1CardKeyAuth(blk + secOffset * 4, blk / 4 + secOffset)) {
                    return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                }
                //消费记录区读取
                retvalue = mBankCard.m1CardReadBlockData(blk + secOffset * 4
                        , respdata, resplen);
                if (retvalue != 0) {
                    LogUtils.e("writeCardRcd: 读取消费记录区错误");
                    return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                }

                byte[] rcdInCard = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
                byte[] rcdDevNr = Datautils.cutBytes(rcdInCard, 12, 4);
                // 刷卡设备号
                if (!Arrays.equals(rcdDevNr, runParaFile.getDevNr())) {
                    byte[] carddt = cardOpDU.ucDateTime;
                    System.arraycopy(rcdInCard, 0, carddt, 2, 4);
                    String busLastStr = Datautils.byteArrayToString(carddt);
                    // 连续刷卡限制时间
                    int time = Datautils.byteArrayToInt(runParaFile.getUcOldCardTimeLimit());
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    try {
                        Date busLast = format.parse(busLastStr);
                        Date dateTime = format.parse(Datautils.byteArrayToString(cardOpDU.ucDateTime));
                        Date busLastAfter = new Date(busLast.getTime() + time * 60000);
                        Date dateTimeAfter = new Date(dateTime.getTime() + time * 60000);
                        int compare = dateTime.compareTo(busLastAfter);
                        int compare1 = busLast.compareTo(dateTimeAfter);
                        if (compare < 0 && compare1 < 0) {
                            fOldDisable = 1;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                    }
                }
                // 判断卡的钱包权限,1=有权限
                ret = CardMethods.fIsUsePur(cardOpDU, runParaFile);
                if (ret != 0)//有权限
                {
                    cardOpDU.pursubInt = 0;
                    if (fOldDisable == 1) {
                        return new CardBackBean(ReturnVal.CAD_REUSE, cardOpDU);
                    }
                } else {
                    // 按相应折扣率计算消费额
                    cardOpDU.pursubInt = CardMethods.getRadioPurSub(cardOpDU, runParaFile);
                }

            } else {
                // 按相应折扣率计算消费额
                cardOpDU.pursubInt = CardMethods.getRadioPurSub(cardOpDU, runParaFile);
                cardOpDU.pursubInt = 0;
            }
            cardOpDU.procSec = 2;
        } else {
            cardOpDU.procSec = 2;
            if (!m1CardKeyAuth((7 + secOffset) * 4, 7 + secOffset)) {
                return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
            }
            //消费记录区读取
            retvalue = mBankCard.m1CardReadBlockData(28 + secOffset * 4, respdata, resplen);
            if (retvalue != 0) {
                return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
            }
            byte[] carddt = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
            for (chk = 0, i = 0; i < 16; i++) {
                chk ^= carddt[i];
            }
            // 月票扇区正常,读月票区0块正常:
            if (chk == 0) {
                // 取卡的公交分类型字
                cardOpDU.subType = carddt[14];
                if (carddt[14] > (byte) 0x0f) {
                    // 卡的公交分类型>16种卡，视为0类型卡
                    cardOpDU.subType = (byte) 0x00;
                }
                ret = CardMethods.fIsUseYue(cardOpDU, runParaFile);
                if (ret == 1)//有权限
                {
                    ret = mifareCardGetYueBasePos(mBankCard);
                    if (ret == 255) {
                        return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                    }
                    if (ret == 0) {
                        cardOpDU.procSec = 2;
                    } else {
                        cardOpDU.procSec = cardOpDU.yueSec;
                    }
                } else {
                    cardOpDU.procSec = 2;
                }
                if (fYueDisable == 1) {
                    cardOpDU.procSec = 2;
                }
            }
            if (cardOpDU.procSec == 2) {
                // 按相应折扣率计算消费额
                cardOpDU.pursubInt = CardMethods.getRadioPurSub(cardOpDU, runParaFile);
                ret = CardMethods.fIsUsePur(cardOpDU, runParaFile);                                                // 判断钱包的使用权限
                //ret = 1;	//DEBUG
                if (ret == 0) {
                    // 钱包区原额=0
                    cardOpDU.purorimoneyInt = 0;
                    cardOpDU.pursubInt = 0;
                    // 请投币
                    return new CardBackBean(ReturnVal.CAD_EMPTY, cardOpDU);
                }
            }
        }


        if (cardOpDU.procSec != 2)//月票
        {

        } else {//钱包
            ret = backupManage(cardOpDU.procSec + secOffset);
            if (ret == 255) {
                return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
            }
            if (ret == 3) {
                return new CardBackBean(ReturnVal.CAD_BROKEN, cardOpDU);
            }
            retvalue = mBankCard.m1CardReadBlockData(8 + secOffset * 4, respdata, resplen);
            if (retvalue != 0) {
                return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
            }

            byte[] rcdInCard = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
            // 取钱包充值设备号
            cardOpDU.ucIncPurDev = Datautils.cutBytes(rcdInCard, 8, 4);
            for (i = 0; i < 4; i++)                                                 // 取钱包余额
            {
                actRemaining <<= 8;                                                 // 转为顺序排列
                actRemaining += dtZ[3 - i];
            }
            cardOpDU.purorimoneyInt = actRemaining;                                 // 原额存卡工作区

            if (cardOpDU.purorimoneyInt >= 100000) {
                return new CardBackBean(ReturnVal.CAD_BROKEN, cardOpDU);              // 卡原额异常，坏卡
            }
            if ((cardOpDU.purorimoneyInt < cardOpDU.pursubInt) && (cardOpDU.pursubInt > 0)) {
                return new CardBackBean(ReturnVal.CAD_EMPTY, cardOpDU);           // 卡余额不足，请投币
            }
        }

        // 取信息正本信息数组
        cinfo = cinfoz;
        cinfo.fFileNr = (byte) 0x10;
        // 卡记录指针+1
        cinfo.cPtr += 1;

        if (cinfo.cPtr > 9) {
            cinfo.cPtr = 1;
        }
        blk = rcdBlkIndex[cinfo.cPtr - 1];                                  // 计算卡记录块号

        if (cardOpDU.procSec == cardOpDU.yueSec)                               // 月票
        {
            cinfo.fProc = (byte) 0x05;                                                // 进程=05
            cinfo.iYueCount = (char) (cinfo.iYueCount + 1);                               // 月票计数器+1
            cinfo.iPurCount = (char) (cinfo.iPurCount + 1);                                 // 钱包计数器+1
        } else                                                            // 钱包消费:
        {
            cinfo.fProc = (byte) 0x01;                                                // 进程=01
            cinfo.iPurCount = (char) (cinfo.iPurCount + 1);                                 // 钱包计数器+1
        }

        if ((cardOpDU.procSec == 2) && (cardOpDU.pursubInt == 0)) {
            while (true) {
                cinfo.fProc = (byte) ((cinfo.fProc + 1) & 0xfe);
                if (!pauseA()) {
                    break;
                }
                if (!pauseB(blk)) {
                    break;
                }
                if (!pauseC()) {
                    break;
                }
            }

            // 当前卡扣款异常,请重刷处理
            if (fSysSta == CAD_PAUSEA) {
                // TODO: 2019/3/22 写记录
                CardMethods.onAppendRecordTrade(cardOpDU.procSec == 2 ? (byte) 0x01 : (byte) 0x03
                        , cardOpDU, runParaFile, psamBeenList);
                // 添加钱包灰纪录或月票灰纪录
                // 返回RETRY错，提示请重刷
                return new CardBackBean(ReturnVal.CAD_EMPTY, cardOpDU);
            }
        } else {
            // 改写24块信息正本，准备扣款
            if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                    cinfo, mBankCard, lodkey, snUid)) {
                return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
            }
            while (true) {
                fSysSta = CAD_PAUSE0;
                if (!pause0()) {
                    break;
                }
                if (!pause1(blk)) {
                    break;
                }
                if (!pause2()) {
                    break;
                }
            }
            // 当前卡扣款异常,请重刷处理
            if (fSysSta == CAD_PAUSE0) {
                // 添加钱包灰记录或月票灰记录
                // TODO: 2019/3/22
                CardMethods.onAppendRecordTrade(cardOpDU.procSec == 2 ? (byte) 0x01 : (byte) 0x03
                        , cardOpDU, runParaFile, psamBeenList);
                //返回RETRY错，提示请重刷
                return new CardBackBean(ReturnVal.CAD_RETRY, cardOpDU);
            }
        }

        fSysSta = CAD_NORMAL;
        // TODO: 2019/3/22
        CardMethods.onAppendRecordTrade(cardOpDU.procSec == 2 ? (byte) 0x00 : (byte) 0x02
                , cardOpDU, runParaFile, psamBeenList);
        //添加钱包记录或月票记录
        return new CardBackBean(ReturnVal.CAD_OK, cardOpDU);

    }


    private final byte CAD_NORMAL = (byte) 0x01;
    private final byte CAD_PAUSE = (byte) 0x40;
    private final byte CAD_PAUSE0 = (byte) 0xF0;
    private final byte CAD_PAUSE1 = (byte) 0xF1;
    private final byte CAD_PAUSE2 = (byte) 0xF2;
    private final byte CAD_PAUSE3 = (byte) 0xF3;
    private final byte CAD_PAUSE4 = (byte) 0xF4;
    private final byte CAD_PAUSE5 = (byte) 0xF5;
    private final byte CAD_PAUSEA = (byte) 0xFA;
    private final byte CAD_PAUSEB = (byte) 0xFB;
    private final byte CAD_PAUSEC = (byte) 0xFC;
    private final byte CAD_GRAYLOCK = (byte) 0xFD;
    private byte fSysSta;

    private boolean pauseA() {
        // 改写24块信息正本
        fSysSta = CAD_PAUSEA;
        if (!CardMethods.modifyInfoArea(24 + secOffset * 4, cinfo, mBankCard, lodkey, snUid)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean pauseB(int blk) throws Exception {
        fSysSta = CAD_PAUSEB;
        //step 4//认证2扇区8块
        if (!m1CardKeyAuth(blk + secOffset * 4, blk / 4 + secOffset)) {
            return false;
        }
        // 刷卡日时分秒
        byte[] rcdToCard = new byte[16];
        System.arraycopy(cardOpDU.ucDateTime, 2, rcdToCard, 0, 4);
        // 刷卡设备号
        System.arraycopy(runParaFile.getDevNr(), 0, rcdToCard, 12, 4);
//        byte[] purorimoney = Datautils.intToByteArray1(cardOpDU.purorimoneyInt);
//        System.arraycopy(purorimoney, 0, rcdToCard, 4, 4);
//        byte[] purSub = Datautils.intToByteArray1(cardOpDU.pursubInt);
//        System.arraycopy(purSub, 0, rcdToCard, 8, 4);
        rcdToCard[4] = (byte) cardOpDU.purorimoneyInt;                                // 取钱包区原额，逆序，4B
        rcdToCard[5] = (byte) (cardOpDU.purorimoneyInt >> 8);
        rcdToCard[6] = (byte) (cardOpDU.purorimoneyInt >> 16);
        rcdToCard[7] = (byte) (cardOpDU.purorimoneyInt >> 24);
        rcdToCard[8] = (byte) cardOpDU.pursubInt;                                    // 消费额，逆序，3B
        rcdToCard[9] = (byte) (cardOpDU.pursubInt >> 8);
        rcdToCard[10] = (byte) (cardOpDU.pursubInt >> 16);
        rcdToCard[11] = (byte) 0x01;                                            // 记录类型=0x10
        // 写卡内记录,改24块,传输钱包副本,改25块
        retvalue = mBankCard.m1CardWriteBlockData(blk + secOffset * 4
                , cardOpDU.ucRcdToCard.length, cardOpDU.ucRcdToCard);
        if (retvalue != 0) {
            return false;
        }
        //消费记录区读取
        retvalue = mBankCard.m1CardReadBlockData(blk + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            return false;
        }
        byte[] rcdInCard = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        if (!Arrays.equals(rcdToCard, rcdInCard)) {
            return false;
        }

        return true;
    }

    private boolean pauseC() {
        fSysSta = CAD_PAUSEC;
        // 改写信息副本(25块)
        if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                cinfo, mBankCard, lodkey, snUid)) {
            return false;
        }
        fSysSta = CAD_NORMAL;
        return false;
    }


    private boolean pause0() throws Exception {
        int money;
        if (cardOpDU.procSec != 2)                                       // 月票消费：
        {
            money = cardOpDU.actYueSub;                                // 取月票消费额
        } else                                                       // 钱包消费：
        {
            money = cardOpDU.pursubInt;                                     // 取钱包消费额
        }

        // 认证钱包或月票扇
        if (!m1CardKeyAuth((cardOpDU.procSec + secOffset) * 4, cardOpDU.procSec + secOffset)) {
            return false;
        }
        //钱包月票正本扣款准备,失败返回CAD_WRITE
        retvalue = mBankCard.m1CardValueOperation(0x2D
                , (cardOpDU.procSec + secOffset) * 4 + 1
                , money, (cardOpDU.procSec + secOffset) * 4 + 1);
        if (retvalue != 0) {
            return false;
        }
        retvalue = mBankCard.m1CardReadBlockData((cardOpDU.procSec + secOffset) * 4 + 1
                , respdata, resplen);
        if (retvalue != 0) {
            return false;
        }

        dtZ = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        int i, tempV = 0, expV;
        for (i = 0; i < 4; i++) {
            tempV <<= 8;
            tempV += dtZ[3 - i];
        }                   // 计算卡内扣款后余额,顺序排列

        if (cardOpDU.procSec != 2) {
            expV = cardOpDU.actYueOriMoney - cardOpDU.actYueSub;                // 判断卡内余额是否正确
        } else {
            expV = cardOpDU.purorimoneyInt - cardOpDU.pursubInt;
        }
        if (tempV != expV) {
            return false;
        }

        fSysSta = CAD_PAUSE1;
        return true;
    }

    private boolean pause1(int blk) throws Exception {
        // 认证卡内记录扇区
        if (!m1CardKeyAuth(blk + secOffset * 4, blk / 4 + secOffset)) {
            return false;
        }
        // 刷卡日时分秒
        byte[] rcdToCard = new byte[16];
        System.arraycopy(cardOpDU.ucDateTime, 2, rcdToCard, 0, 4);
        System.arraycopy(runParaFile.getDevNr(), 0, rcdToCard, 12, 4);

        if (cardOpDU.procSec == cardOpDU.yueSec)                                // 消费月票
        {
//            byte[] purorimoney = Datautils.intToByteArray1(cardOpDU.actYueOriMoney);
//            System.arraycopy(purorimoney, 0, rcdToCard, 4, 4);
//            byte[] purSub = Datautils.intToByteArray1(cardOpDU.actYueSub);
//            System.arraycopy(purSub, 0, rcdToCard, 8, 4);
            rcdToCard[4] = (byte) cardOpDU.actYueOriMoney;                            // 取月票原额,逆序,4B
            rcdToCard[5] = (byte) (cardOpDU.actYueOriMoney >> 8);
            rcdToCard[6] = (byte) (cardOpDU.actYueOriMoney >> 16);
            rcdToCard[7] = (byte) (cardOpDU.actYueOriMoney >> 24);
            rcdToCard[8] = (byte) cardOpDU.actYueSub;                                 // 消费额，逆序，3B
            rcdToCard[9] = (byte) (cardOpDU.actYueSub >> 8);
            rcdToCard[10] = (byte) (cardOpDU.actYueSub >> 16);
            if (cardOpDU.procSec == 7) {
                rcdToCard[11] = (byte) 0x10;
            } else {
                rcdToCard[11] = (byte) 0x12;
            }
        } else if (cardOpDU.procSec == 2)                                     // 钱包消费
        {
//            byte[] purorimoney = Datautils.intToByteArray1(cardOpDU.purorimoneyInt);
//            System.arraycopy(purorimoney, 0, rcdToCard, 4, 4);
//            byte[] purSub = Datautils.intToByteArray1(cardOpDU.pursubInt);
//            System.arraycopy(purSub, 0, rcdToCard, 8, 4);
            rcdToCard[4] = (byte) cardOpDU.purorimoneyInt;                            //取钱包区原额，逆序，4B
            rcdToCard[5] = (byte) (cardOpDU.purorimoneyInt >> 8);
            rcdToCard[6] = (byte) (cardOpDU.purorimoneyInt >> 16);
            rcdToCard[7] = (byte) (cardOpDU.purorimoneyInt >> 24);
            rcdToCard[8] = (byte) cardOpDU.pursubInt;                                 // 消费额，逆序，3B
            rcdToCard[9] = (byte) (cardOpDU.pursubInt >> 8);
            rcdToCard[10] = (byte) (cardOpDU.pursubInt >> 16);
            rcdToCard[11] = (byte) 0x01;
        }
        // 写卡内记录,改24块,传输钱包副本,改25块
        retvalue = mBankCard.m1CardWriteBlockData(blk + secOffset * 4, cardOpDU.ucRcdToCard.length
                , cardOpDU.ucRcdToCard);
        if (retvalue != 0) {
            return false;
        }
        retvalue = mBankCard.m1CardReadBlockData(blk + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            return false;
        }

        byte[] rcdInCard = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        if (!Arrays.equals(rcdToCard, rcdInCard)) {
            return false;
        }
        cinfo.fProc = (byte) ((cinfo.fProc + 1) & 0xfe);// ??+1
        fSysSta = CAD_PAUSE2;
        return true;
    }

    private boolean pause2() throws Exception {
        // 改写信息正本(24块)
        if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                cinfo, mBankCard, lodkey, snUid)) {
            return false;
        }
        fSysSta = CAD_NORMAL;
        // 认证钱包/月票区
        if (!m1CardKeyAuth(cardOpDU.procSec * 4 + secOffset * 4, cardOpDU.procSec + secOffset)) {
            return false;
        }
        retvalue = mBankCard.m1CardValueOperation(0x3E
                , (cardOpDU.procSec + secOffset) * 4 + 1
                , 0, (cardOpDU.procSec + secOffset) * 4 + 2);
        if (retvalue != 0) {
            return false;
        }
        //消费记录区读取
        retvalue = mBankCard.m1CardReadBlockData(4 * cardOpDU.procSec + secOffset * 4 + 2
                , respdata, resplen);
        if (retvalue != 0) {
            return false;
        }

        dtF = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        // 不相符,退出,不再改写信息副本
        if (!Arrays.equals(dtZ, dtF)) {
            return false;
        }
        if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                cinfo, mBankCard, lodkey, snUid)) {
            return false;
        }
        return false;
    }

    /**
     * 认证扇区
     *
     * @param blk 扇区块
     * @param key 秘钥
     * @return
     */
    private boolean m1CardKeyAuth(int blk, int key) {
        try {
            byte[] lodKeys = lodkey[key];
            retvalue = mBankCard.m1CardKeyAuth(0x41, blk, lodKeys.length, lodKeys, snUid.length, snUid);
            if (retvalue != 0) {
                return false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * buscard 恢复
     */
    private CardBackBean mifareCardRestore() throws Exception {
        int fJudge, blk;
        byte[] rcdInCard = new byte[16];
        int cardRcdSec = 0, cardRcdType;
        byte[] cardRcdDateTime = new byte[7];
        int cardRcdOriMoney, cardRcdSub, cardRcdMoney;
        int purOriMoney;
        int actYueOriMoney, yueOriMoney = 0, actYueSub;
        int actRemaining = 0;
        int ret, i;

        fJudge = 0;
        // case A
        if ((cinfoz.fValid == 1) && (cinfof.fValid == 1) &&
                ((cinfoz.fProc & 0x01) == 0) && ((cinfof.fProc & 0x01) == 0)) {
        } else if ((cinfoz.fValid == 0) && (cinfof.fValid == 1) &&
                ((cinfof.fProc & 0x01) == 0)) {
            // case B
            if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                    cinfo, mBankCard, lodkey, snUid)) {
                return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
            }
        } else if ((cinfoz.fValid == 1) && (cinfof.fValid == 1) &&
                ((cinfoz.fProc & 0x01) == 1) && ((cinfof.fProc & 0x01) == 0)) {
            // case C
            cinfo.cPtr = cinfof.cPtr;
            cinfo.ucPurCount = cinfof.ucPurCount;
            cinfo.fProc = cinfof.fProc;
            cinfo.ucYueCount = cinfof.ucYueCount;
            cinfo.fBlack = cinfof.fBlack;
            cinfo.fFileNr = cinfof.fFileNr;
            cinfo.fSubWay = cinfof.fSubWay;

            cinfo.iPurCount = (char) ((cinfo.ucPurCount[0] << 8) + cinfo.ucPurCount[1]);
            cinfo.iYueCount = (char) ((cinfo.ucYueCount[0] << 8) + cinfo.ucYueCount[1]);
            if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                    cinfo, mBankCard, lodkey, snUid)) {
                return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
            }
        } else if ((cinfoz.fValid == 1) && (cinfof.fValid == 0) &&
                ((cinfoz.fProc & 0x01) == 1)) {
            // case D
            cinfo.cPtr = (byte) (cinfoz.cPtr == 0 ? 8 : (cinfoz.cPtr - 1));
            cinfo.fProc = (byte) (cinfo.fProc & 0xfe);
            if (cinfoz.fProc == 1) {
                cinfo.iPurCount = (char) (cinfoz.iPurCount - 1);
            } else {//3,5,...
                cinfo.iYueCount = (char) (cinfoz.iYueCount - 1);
            }
            if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                    cinfo, mBankCard, lodkey, snUid)) {
                return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
            }
            if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                    cinfo, mBankCard, lodkey, snUid)) {
                return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
            }
        } else if ((cinfoz.fValid == 1) && (cinfof.fValid == 1) &&
                ((cinfoz.fProc & 0x01) == 1) && ((cinfof.fProc & 0x01) == 1))// case E
        {
            fJudge = 1;
        } else if ((cinfoz.fValid == 0) && (cinfof.fValid == 1) &&
                ((cinfof.fProc & 0x01) == 1))                          // case F
        {
            cinfo.fProc = (byte) (cinfo.fProc & 0xfe);
            if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                    cinfo, mBankCard, lodkey, snUid)) {
                return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
            }
            if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                    cinfo, mBankCard, lodkey, snUid)) {
                return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
            }
        } else if ((cinfoz.fValid == 1) && (cinfof.fValid == 1) &&
                ((cinfoz.fProc & 0x01) == 0) && ((cinfof.fProc & 0x01) == 1))// case G
        {
            if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                    cinfo, mBankCard, lodkey, snUid)) {
                return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
            }
        } else if ((cinfoz.fValid == 1) && (cinfof.fValid == 0) &&
                ((cinfoz.fProc & 0x01) == 0))                        // case H
        {
            if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                    cinfo, mBankCard, lodkey, snUid)) {
                return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
            }
        } else {                                            // other;
            cinfo.fProc = (byte) (cinfo.fProc & 0xfe);
            if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                    cinfo, mBankCard, lodkey, snUid)) {
                return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
            }
            if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                    cinfo, mBankCard, lodkey, snUid)) {
                return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
            }
        }
        /////////////////////////////////////////////////////////////
        if (fJudge == 1) {
            blk = cinfo.cPtr == 0 ? 8 : cinfo.cPtr - 1;
            blk = blk / 3 * 4 + blk % 3 + 12;
            if (!m1CardKeyAuth(blk + secOffset * 4, blk / 4)) {
                return new CardBackBean(ReturnVal.CAD_ACCESS, cardOpDU);
            }
            retvalue = mBankCard.m1CardReadBlockData(blk + secOffset * 4, respdata, resplen);
            if (retvalue != 0) {
                return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
            }

            rcdInCard = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
//            CardRcdUTC = ByteToLong(RcdInCard);
//            UTCToBCDTime(CardRcdUTC, CardRcdDateTime);
            switch (rcdInCard[11] & 0x0f) {
                case 1:
                    cardRcdSec = 2;
                    break;
                case 2:
                    cardRcdSec = 7;
                    break;
                default:
                    break;
            }
            cardRcdType = (rcdInCard[11] >> 4) == 1 ? 1 : 0;//Add,1:Sub,0
            for (cardRcdOriMoney = 0, i = 0; i < 4; i++) {
                cardRcdOriMoney <<= 8;
                cardRcdOriMoney += rcdInCard[i + 4];
            }
            for (cardRcdSub = 0, i = 0; i < 3; i++) {
                cardRcdSub <<= 8;
                cardRcdSub += rcdInCard[i + 8];
            }
            if (backupManage(cardRcdSec) == 255) {
                return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);      //_dt and _backup are all wrong
            }
            for (i = 0; i < 4; i++) {
                actRemaining <<= 8;
                actRemaining += dtZ[3 - i];
            }    //_backup must be ok
            if (cardRcdSec == 2)//Pur
            {
                purOriMoney = actRemaining;
                if (cardRcdType == 0) {
                    cardRcdMoney = cardRcdOriMoney - cardRcdSub;
                } else {
                    cardRcdMoney = cardRcdOriMoney + cardRcdSub;
                }
                //向上恢复
                if (cardRcdOriMoney == purOriMoney) {
                    cinfo.cPtr = (byte) (cinfoz.cPtr == 0 ? 8 : cinfoz.cPtr - 1);
                    cinfo.iPurCount = (char) (cinfoz.iPurCount - 1);
                    cinfo.fProc = (byte) (cinfo.fProc & 0xfe);
                    if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                    }
                    if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                    }

                }
                //向下恢复
                else if (cardRcdMoney == purOriMoney) {
                    cinfo.fProc = (byte) (cinfo.fProc & 0xfe);
                    if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                    }
                    if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                    }
                } else {
                    cinfo.fProc = (byte) (cinfo.fProc & 0xfe);
                    if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                    }
                    if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                    }
                }
            } else {//Yue
                ret = mifareCardGetYueBasePos(mBankCard);
                if (ret == 255) {
                    return new CardBackBean(ReturnVal.CAD_READ, cardOpDU);
                }
                if (ret == 0) {
                    return new CardBackBean(ReturnVal.CAD_BROKEN, cardOpDU);
                }
                actYueOriMoney = actRemaining;
                mifareCardYueMoneyAdjust(actYueOriMoney, cardRcdSub);
                if (cardRcdType == 0) {
                    cardRcdMoney = cardRcdOriMoney - cardRcdSub;
                } else {
                    cardRcdMoney = cardRcdOriMoney + cardRcdSub;
                }
                if (cardRcdOriMoney == yueOriMoney) {
                    cinfo.cPtr = (byte) (cinfoz.cPtr == 0 ? 8 : cinfoz.cPtr - 1);
                    cinfo.iYueCount = (char) (cinfoz.iYueCount - 1);
                    cinfo.fProc = (byte) (cinfo.fProc & 0xfe);
                    if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                    }
                    if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                    }
                } else if (cardRcdMoney == yueOriMoney) {
                    cinfo.fProc = (byte) (cinfo.fProc & 0xfe);
                    if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                    }
                    if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                    }
                } else {
                    cinfo.iYueCount = (char) (cinfoz.iYueCount - 1);
                    cinfo.fProc = (byte) (cinfo.fProc & 0xfe);
                    if (!CardMethods.modifyInfoArea(25 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                    }
                    if (!CardMethods.modifyInfoArea(24 + secOffset * 4,
                            cinfo, mBankCard, lodkey, snUid)) {
                        return new CardBackBean(ReturnVal.CAD_WRITE, cardOpDU);
                    }
                }
            }
        }


        cardOpDU.purCount = cinfo.iPurCount;
        cardOpDU.yueCount = cinfo.iYueCount;
        return new CardBackBean(ReturnVal.CAD_OK, cardOpDU);
    }


    /**
     * 判断卡种
     *
     * @param mBankCard
     * @return
     * @throws Exception
     */
    private int checkMifcardclass(BankCard mBankCard) throws Exception {
        //读取非接卡 SN(UID)信息
        retvalue = mBankCard.getCardSNFunction(respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("===获取UID失败===");
            return CAD_READ;
        }
        snUid = Datautils.cutBytes(respdata, 0, resplen[0]);
        cardOpDU.setSnr(snUid);
        LogUtils.d("===getUID===" + Datautils.byteArrayToString(snUid));
        byte[] key = new byte[6];
        System.arraycopy(snUid, 0, key, 0, 4);
        System.arraycopy(snUid, 0, key, 4, 2);
        //认证1扇区第4块
        retvalue = mBankCard.m1CardKeyAuth(0x41, 4 + secOffset * 4, key.length, key, snUid.length, snUid);
        if (retvalue != 0) {
            LogUtils.e("===认证1扇区第4块失败===");
            return CAD_READ;
        }
        retvalue = mBankCard.m1CardReadBlockData(4 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("=== 读取1扇区第4块失败==");
            return CAD_READ;
        }
        byte[] bytes04 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        LogUtils.d("===读取1扇区第4块返回===" + Datautils.byteArrayToString(bytes04));
        cardOpDU.setIssueSnr(Datautils.cutBytes(bytes04, 0, 8));
        cardOpDU.setCityNr(Datautils.cutBytes(bytes04, 0, 2));
        cardOpDU.setVocCode(Datautils.cutBytes(bytes04, 2, 2));
        cardOpDU.setIssueCode(Datautils.cutBytes(bytes04, 4, 4));
        cardOpDU.setMackNr(Datautils.cutBytes(bytes04, 8, 4));
        cardOpDU.setfStartUse(Datautils.cutBytes(bytes04, 12, 1));
        //卡类型判断表格中没有return
        cardOpDU.setCardType(Datautils.cutBytes(bytes04, 13, 1)[0]);
        //黑名单
        cardOpDU.setfBlackCard(false);
        //判断启用标志
        switch (cardOpDU.fStartUse[0]) {
            //未启用
            case (byte) 0x01:
                LogUtils.e("m1ICCard: 启用标志未启用");
                return CAD_READ;
            //正常
            case (byte) 0x02:
                // TODO: 2018/8/29
                break;
            //停用
            case (byte) 0x03:
                LogUtils.e("m1ICCard: 启用标志停用");
                return CAD_READ;
            //黑名单
            case (byte) 0x04:
                LogUtils.e("m1ICCard: 启用标志黑名单");
                cardOpDU.setfBlackCard(true);
                return CAD_READ;
            default:
                break;
        }

        //读1扇区05块数据
        retvalue = mBankCard.m1CardReadBlockData(5 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("===读1扇区05块数据失败====");
            return CAD_READ;
        }
        byte[] bytes05 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        LogUtils.d("===读1扇区05块数据===" + Datautils.byteArrayToString(bytes05));
//        cardOpDU.setIssueDate(Datautils.cutBytes(bytes05, 0, 4));
//        cardOpDU.setEndUserDate(Datautils.cutBytes(bytes05, 4, 4));
//        cardOpDU.setStartUserDate(Datautils.cutBytes(bytes05, 8, 4));

        //天津M1卡05块数据保存
        cardOpDU.issueDate = Datautils.cutBytes(bytes05, 1, 3);
        cardOpDU.endUserDate = Datautils.cutBytes(bytes05, 5, 3);
        cardOpDU.startUserDate = Datautils.cutBytes(bytes05, 9, 3);
        cardOpDU.uiIncPurCount = Datautils.cutBytes(bytes05, 12, 2);

        cardOpDU.fStartUsePur = Datautils.cutBytes(bytes05, 14, 1)[0];


        //读1扇区06块数据
        retvalue = mBankCard.m1CardReadBlockData(6 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("===读1扇区06块数据失败==");
            return CAD_READ;
        }
        byte[] bytes06 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        LogUtils.d("===读1扇区06块数据返回===" + Datautils.byteArrayToString(bytes06));
        //转UTC时间
        cardOpDU.setPurIncUtc(Datautils.cutBytes(bytes06, 0, 6));
        cardOpDU.setPurIncMoney(Datautils.cutBytes(bytes06, 9, 2));
        //第0扇区 01块认证
        retvalue = mBankCard.m1CardKeyAuth(0x41, 1 + secOffset * 4,
                6, new byte[]{(byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3, (byte) 0xA4, (byte) 0xA5}, snUid.length, snUid);
        if (retvalue != 0) {
            LogUtils.e("===第0扇区01块认证失败==");
            return CAD_READ;
        }
        //读第0扇区第一块秘钥
        retvalue = mBankCard.m1CardReadBlockData(1 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("m1ICCard: 读第0扇区01块失败");
            return CAD_READ;
        }

        byte[] bytes01 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        LogUtils.d("m1ICCard: 读第0扇区01块：" + Datautils.byteArrayToString(bytes01));
        //扇区标识符
        secF = bytes01;

        //单des加密
        String encryptKey = Datautils.byteArrayToString(new byte[]{(byte) 0x83, (byte) 0xdb, (byte) 0x4e
                , (byte) 0x03, (byte) 0x45, (byte) 0x55, (byte) 0xc5, (byte) 0x75});
        byte[] encrypt = Datautils.concatAll(cardOpDU.getSnr(), Datautils.cutBytes(cardOpDU.getIssueCode()
                , 2, 2), cardOpDU.getCityNr());
        byte[] decryptDES = DesUtil.encrypt(encrypt, encryptKey);
        decryptDES = Datautils.cutBytes(decryptDES, 0, 4);
        //MAC==0,软密钥,公交
        if (Arrays.equals(decryptDES, cardOpDU.getMackNr())) {
            for (int i = 2; i < 12; i++) {
                byte[] bytes = Datautils.concatAll(cardOpDU.getSnr()
                        , Datautils.cutBytes(cardOpDU.getIssueCode(), 2, 2)
                        , new byte[]{cardOpDU.mackNr[0]}
                        , new byte[]{secF[i]});
                String enKey = Datautils.byteArrayToString(new byte[]{(byte) 0x83, (byte) 0xdb, (byte) 0x4e
                        , (byte) 0x03, (byte) 0x45, (byte) 0x55, (byte) 0xc5, (byte) 0x75});
                byte[] encryptSave = DesUtil.encrypt(bytes, enKey);
                lodkey[i] = Datautils.cutBytes(encryptSave, 0, 6);
            }
            cardOpDU.cardClass = 0x01;
            if (secF[15] == (byte) 0x51) {
                cardOpDU.cardClass = (byte) 0x51;
            }
        } else {
            byte[] resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM1_APDU
                    , new byte[]{(byte) 0x00, (byte) 0xa4, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x10, (byte) 0x03});
            if (resultBytes == null || resultBytes.length == 2) {
                LogUtils.e("===00a4校验error===");
                return CAD_READ;
            }

            //算秘钥指令
            String sendCmd = "80FC01011" + Datautils.byteArrayToString(cardOpDU.getCityNr())
                    + Datautils.byteArrayToString(cardOpDU.getSnr())
                    + Datautils.byteArrayToString(Datautils.cutBytes(cardOpDU.getIssueSnr(), 6, 2))
                    + Datautils.byteArrayToString(cardOpDU.getMackNr())
                    + Datautils.byteArrayToString(Datautils.cutBytes(secF, 2, 2))
                    + Datautils.byteArrayToString(Datautils.cutBytes(secF, 6, 2));
            LogUtils.d("===psam计算秘钥指令===" + sendCmd);
            //psam卡计算秘钥
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, Datautils.HexString2Bytes(sendCmd), Datautils.HexString2Bytes(sendCmd).length, respdata, resplen);
            if (retvalue != 0) {
                LogUtils.e("===psam计算秘钥指令错误===");
                return CAD_READ;
            }
            if (!Arrays.equals(CardMethods.APDU_RESULT_SUCCESS, Datautils.cutBytes(respdata, resplen[0] - 2, 2))) {
                LogUtils.e("=== psam计算秘钥指令错误非9000===");
                return CAD_READ;
            }

            byte[] result = Datautils.cutBytes(respdata, 0, resplen[0] - 2);
            LogUtils.d("m1ICCard: psam计算秘钥返回：" + Datautils.byteArrayToString(result));
            //3/4/5扇区秘钥相同
            // 第2扇区秘钥
            lodkey[2] = Datautils.cutBytes(result, 0, 6);
            //第3扇区秘钥
            lodkey[3] = Datautils.cutBytes(result, 6, 6);
            //第4扇区秘钥
            lodkey[4] = Datautils.cutBytes(result, 6, 6);
            //第5扇区秘钥
            lodkey[5] = Datautils.cutBytes(result, 6, 6);
            //第6扇区秘钥
            lodkey[6] = Datautils.cutBytes(result, 12, 6);
            //第7扇区秘钥
            lodkey[7] = Datautils.cutBytes(result, 18, 6);
        }
        return CAD_OK;
    }


    private int mifareCardGetYueBasePos(BankCard mBankCard) throws Exception {
        int valid, ret;
        int chk, i;
        byte[] yueStartDate = new byte[3];
        byte[] yueEndDate = new byte[3];
        byte[] yueDate = new byte[2];
        byte[] nextTime = new byte[2];
//        unsigned char xdata carddt[16];
//        unsigned char xdata ucDate[3], ucYear;

        cardOpDU.yueSec = (byte) 0x07;
        byte[] lodkey6 = lodkey[6];
        retvalue = mBankCard.m1CardKeyAuth(0x41, 28 + secOffset,
                lodkey6.length, lodkey6, snUid.length, snUid);
        if (retvalue != 0) {
            LogUtils.e("===认证7扇区失败===");
            return 255;
        }
        retvalue = mBankCard.m1CardReadBlockData(28 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("=== 读取7扇区第28块失败==");
            return 255;
        }
        byte[] bytes28 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        for (chk = 0, i = 0; i < 16; i++) {
            chk ^= bytes28[i];
        }
        if (chk != 0) {
            return 0;
        }

        cardOpDU.yueBase = Datautils.cutBytes(bytes28, 0, 2);
        yueStartDate = Datautils.cutBytes(bytes28, 2, 3);
        //学生和军人,月票类型是学生票和军人票，当前日期减去启用日期超过一年，视为普通卡，钱包消费
        if ((cardOpDU.subType == (byte) 0x03) || (cardOpDU.subType == (byte) 0x0A)) {
            int start = Integer.parseInt(Datautils.byteArrayToString(yueStartDate));
            int nowTime = Integer.parseInt(Datautils.byteArrayToString(cardOpDU.ucDateTime).substring(2));
            if (nowTime > start + 10000) {
                return 0;
            }
        }
        yueEndDate = Datautils.cutBytes(bytes28, 5, 3);
        byte[] uiIncYueCount = Datautils.cutBytes(bytes28, 8, 2);
        cardOpDU.uiIncYueCount = Datautils.byteArrayToInt(uiIncYueCount);

        if (Arrays.equals(Datautils.cutBytes(yueStartDate, 0, 2)
                , new byte[]{(byte) 0x00, (byte) 0x00})) {
            return 0;
        }

        if (yueStartDate[2] > (byte) 0x32) {
            return 0;
        }
        if (yueStartDate[2] == (byte) 0x00) {
            return 0;
        }

        if (yueStartDate[2] == (byte) 0x32) {
            yueDate = Datautils.cutBytes(yueEndDate, 0, 2);
            for (valid = 0, i = 0; ; i++) {
                if (dateValid(yueDate, cardOpDU.ucDateTime) != 0) {
                    cardOpDU.yuePositionInt = i;
                    cardOpDU.yueUsingDate = Datautils.cutBytes(yueDate, 0, 2);
                    cardOpDU.yueUsingDate[2] = 0x32;
                    valid = 1;
                    break;
                }
                yueDate = subYueDate(yueDate);
                int yueDateInt = Integer.parseInt(Datautils.byteArrayToString(yueDate));
                int yueStartDateInt = Integer.parseInt(Datautils.byteArrayToString(yueStartDate));
                if (yueDateInt < yueStartDateInt) {
                    break;
                }
            }

            //////////////Add Of TianJin////////////////////
            if (valid == 1) {
                ret = judgeYueScope();
                if (ret == 255) {
                    return 255;
                }
                if (ret == F_OVERFLOW) {
                    return F_OVERFLOW;
                }
                if (ret == F_LEAST) {
                    valid = 0;
                    if (cardOpDU.yuePosition > 0) {
                        cardOpDU.yuePosition -= 1;
                        nextTime = addYueDate(yueDate);
                        cardOpDU.yueUsingDate = yueDate;
                        cardOpDU.yueUsingDate[2] = (byte) 0x32;
                        valid = 1;
                    }
                }
            }
        } else {
            cardOpDU.yueFlag = 1;                //10 day card
            int ucDateTimeInt = Integer.parseInt(Datautils.byteArrayToString(cardOpDU.ucDateTime));
            int yueStartDateInt = Integer.parseInt(Datautils.byteArrayToString(yueStartDate));
            int yueEndDateInt = Integer.parseInt(Datautils.byteArrayToString(yueEndDate));

            if ((ucDateTimeInt >= yueStartDateInt) && (ucDateTimeInt <= yueEndDateInt)) {
                cardOpDU.yuePosition = 0;
                cardOpDU.yueUsingDate = yueStartDate;
                valid = 1;
            } else {
                valid = 0;
            }
        }
        return valid = 0;
    }

    /*************************************************************************
     *   StoBManage() :  钱包/月票的正副本，以数值小的块恢复数值大的块        *
     *  ===================================================================== *
     *   参数 ：secnr -- 钱包/月票扇区号                                      *
     *          *_dt  -- 正本读出存放区                                       *
     *          *_backup -- 副本读出存放区                                    *
     *   功能 :                                                               *
     *   返回值 :   0 -- 正副本正确相等										 *
     *              1 -- 正本小，暂不恢复                                     *
     *              2 -- 副本恢复正本成功                                     *
     *              255 -- 恢复失败                                           *
     *************************************************************************/
    private int sToBManage(int secnr, byte[] dt, byte[] backup) throws Exception {
        int i;
        byte[] ucDt;
        int ulV = 0;
        int lVz, lVf;
        int dR0 = secnr * 4 + 1;
        int dR1 = dR0 + 1;

        if (!m1CardKeyAuth(secnr * 4 + secOffset, secnr)) {
            return 255;
        }
        retvalue = mBankCard.m1CardReadBlockData(dR1 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            return 255;
        }
        backup = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        retvalue = mBankCard.m1CardReadBlockData(dR0 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            return 255;
        }
        dt = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        if (valueBlockValid(dt) == false)      //_dt is not ok
        {
            retvalue = mBankCard.m1CardValueOperation(0x3E
                    , dR1, 0, dR0);
            if (retvalue != 0) {
                return 255;
            }
            retvalue = mBankCard.m1CardReadBlockData(dR0 + secOffset * 4, respdata, resplen);
            if (retvalue != 0) {
                LogUtils.e("writeCardRcd: 读取消费记录区错误");
                return 255;
            }
            ucDt = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
            if (!Arrays.equals(ucDt, backup)) {
                return 255;
            }

            dt = Datautils.cutBytes(backup, 0, 16);
            return 2;
        }

        for (i = 0; i < 4; i++) {
            ulV <<= 8;
            ulV += dt[3 - i];
        }
        lVz = ulV;
        for (i = 0; i < 4; i++) {
            ulV <<= 8;
            ulV += backup[3 - i];
        }
        lVf = ulV;
        if (lVz == lVf) {
            return 0;
        } else if (lVz < lVf) {
            return 1;
        } else {//lVz>lVf
            retvalue = mBankCard.m1CardValueOperation(0x3E
                    , dR1, 0, dR0);
            if (retvalue != 0) {
                return 255;
            }
            retvalue = mBankCard.m1CardReadBlockData(dR0 + secOffset * 4, respdata, resplen);
            if (retvalue != 0) {
                LogUtils.e("writeCardRcd: 读取消费记录区错误");
                return 255;
            }
            ucDt = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
            if (!Arrays.equals(ucDt, backup)) {
                return 255;
            }

            dt = Datautils.cutBytes(backup, 0, 16);
            return 2;
        }
    }

    /*********************************************************************
     *         DateValid ： 月票，季票，年票标识的有效性判断              *
     *        ===============================================             *
     *  参数：* _date                                                     *
     *        *_time                                                      *
     *  功能：判断月票，季票，年票标识的有效性                            *
     *********************************************************************/
    private int dateValid(byte[] date, byte[] time) {
        // 月票，季票，年票标识的有效性判断
        int temp;

        //  if(_date[1]==0x18)return 1;                      // year not concern
        if (date[0] != time[0]) {
            return 0;                     // year not same
        }
        if (date[1] == (byte) 0x17) {
            cardOpDU.yueFlag = 4;
            return 1;
        }       // month not concern
        if (date[1] == time[1]) {
            cardOpDU.yueFlag = 2;
            return 1;
        }   // month is same
        temp = (time[1] >> 4) * 10 + (time[1] & 0x0f) - 1;
        if (temp / 3 + 0x13 == date[1]) {
            cardOpDU.yueFlag = 3;
            return 1;
        }// season is same
        if (temp / 6 + 0x18 == date[1]) {
            cardOpDU.yueFlag = 5;
            return 1;
        }// HalfYear is same
        return 0;
    }

    /************************************************************************
     *      SubYueDate ： 计算月票标识的上一月，或上一季，或上一年,或上半年  *
     *     ========================================================          *
     *  参数：*input                                                         *
     *        *output                                                        *
     *  功能：计算月票标识的上一月，或上一季，或上一年,或上半年并返回结果    *
     *************************************************************************/
    private byte[] subYueDate(byte[] input) {
        int ucYY, ucMM;

        if ((input[1] < 1) || (input[1] > 0x19))                       // 月票标识=1-23：1-12=月票，13-16=季票，17=年票,18-19半年票
        {
            return new byte[]{(byte) 0x00, (byte) 0x00};
        }
        ucYY = (input[0] & 0x0f) + (input[0] >> 4) * 10;                  // 年份由BCD码化为10进制
        ucMM = (input[1] & 0x0f) + (input[1] >> 4) * 10;                  // 月份由BCD码化为10进制
        if (input[1] < 0x13)       // 1-12月，月份--
        {
            if (ucMM == 1) {
                ucMM = 13;
                ucYY--;
            }
            ucMM--;
        } else if (input[1] < 0x17) // 13、14、15、16，季票,季度--
        {
            if (ucMM == 13) {
                ucMM = 17;
                ucYY--;
            }
            ucMM--;
        } else if (input[1] < 0x18)// 17,年票,年--
        {
            ucYY--;
        } else if (input[1] < 0x1a)// 18、19，半年票,
        {
            if (ucMM == 18) {
                ucMM = 20;
                ucYY--;
            }
            ucMM--;
        }
        byte[] output = new byte[2];
        output[0] = (byte) (((ucYY / 10) << 4) + (ucYY % 10)); // 新的月季年标识重新化为BCD码
        output[1] = (byte) (((ucMM / 10) << 4) + (ucMM % 10));
        return output;
    }


    private int judgeYueScope() {
        int ret, i;
        dtZ = new byte[16];
        dtF = new byte[16];
        actRemaining = 0;

        cardOpDU.procSec = cardOpDU.yueSec;
        try {
            ret = backupManage(cardOpDU.procSec + secOffset);          //_dt and _backup are all wrong
        } catch (Exception e) {
            e.printStackTrace();
            ret = 255;
        }
        if (ret == 255) {
            return CAD_READ;
        } else if (ret == 3) {
            return F_LEAST;
        }

        for (i = 0; i < 4; i++) {
            actRemaining <<= 8;
            actRemaining += dtZ[3 - i];
        }
        cardOpDU.actYueOriMoney = actRemaining;
        cardOpDU.yueSub = 1;

        mifareCardYueMoneyAdjust(cardOpDU.actYueOriMoney, cardOpDU.yueSub);

        if (cardOpDU.yueOriMoney > 100000) {
            ret = F_OVERFLOW;    //OnRefresh(D_MONEYNOTVALID);
        } else if (cardOpDU.yueOriMoney > 0) {
            ret = F_NORMAL;
        } else {
            ret = F_LEAST;
        }
        return ret;
    }

    /*************************************************************************
     *   BackupManage() :  钱包/月票的正副本，以数值有效的块恢复数值无效的块  *
     *************************************************************************/
    private int backupManage(int blk) {
        //第二扇区08 块认证
        try {
            byte[] lodKey2 = lodkey[blk];
            retvalue = mBankCard.m1CardKeyAuth(0x41, blk * 4 + secOffset * 4
                    , lodKey2.length, lodKey2, snUid.length, snUid);
            if (retvalue != 0) {
                LogUtils.e("===认证2扇区第9块失败===");
                return 255;
            }
            //读2扇区第9块
            retvalue = mBankCard.m1CardReadBlockData(blk * 4 + secOffset * 4 + 1
                    , respdata, resplen);
            if (retvalue != 0) {
                LogUtils.e("===读2扇区第9块失败===");
                return 255;
            }
            dtZ = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
            LogUtils.d("===读2扇区第9块返回===" + Datautils.byteArrayToString(dtZ));

            //读2扇区第10块
            retvalue = mBankCard.m1CardReadBlockData(blk * 4 + secOffset * 4 + 2
                    , respdata, resplen);
            if (retvalue != 0) {
                LogUtils.e("===读2扇区第10块失败===");
                return 255;
            }
            dtF = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
            LogUtils.d("===读2扇区第10块返回===" + Datautils.byteArrayToString(dtF));

            //原额
            byte[] yue09 = Datautils.cutBytes(dtZ, 0, 4);

            cardOpDU.setPurorimoney(yue09);
            LogUtils.d("===原额===" + Datautils.byteArrayToString(yue09));

            if (valueBlockValid(dtZ)) {
                LogUtils.d("=== 2区09块过===");
                //判断2区9块10块数据是否一致
                if (!Arrays.equals(dtZ, dtF)) {
                    retvalue = mBankCard.m1CardValueOperation(0x3E
                            , blk * 4 + secOffset * 4 + 1
                            , 0, blk * 4 + secOffset * 4 + 2);
                    if (retvalue != 0) {
                        LogUtils.e("===更新2区09块失败===");
                        return 255;
                    }
                    return 1;
                }

                return 0;
            } else {
                if (valueBlockValid(dtF)) {
                    LogUtils.d("===2区10块过===");
                    if (!Arrays.equals(dtZ, dtF)) {
                        dtZ = dtF;
                        retvalue = mBankCard.m1CardValueOperation(0x3E
                                , blk * 4 + secOffset * 4 + 2
                                , 0, blk * 4 + secOffset * 4 + 1);
//                        retvalue = mBankCard.m1CardWriteBlockData(0x09, bytes10.length, bytes10);
                        if (retvalue != 0) {
                            LogUtils.e("===写2区9块失败===");
                            return 255;
                        }
                    }

                    return 2;
                } else {
                    LogUtils.d("===2区10块错返回===");
                    return 3;
                }
            }

        } catch (RemoteException e) {
            e.printStackTrace();
            return 255;
        }

    }


    /**
     * 钱包/月票正本或副本余额有效性检测
     *
     * @param dts
     * @return
     */
    private boolean valueBlockValid(byte[] dts) {//
        int i;
        // 钱包/月票原码反码比较
        for (i = 4; i < 12; i++) {
            if (dts[i - 4] != ~dts[i]) {
                // 不相符返回假
                return false;
            }
        }
        // 钱包/月票校验字正反码比较
        for (i = 13; i < 16; i++) {
            if (dts[i - 1] != ~dts[i]) {
                // 不相符返回假
                return false;
            }
        }
        // 钱包/月票正副本有效，返回真
        return true;
    }


    /*********************************************************************
     *               YueMoneyAdjust ：月票计数器调整                      *
     *              =================================                     *
     *  参数：Money                                                       *
     *        * Rem                                                       *
     *        *ActSub                                                     *
     *  功能：
     *********************************************************************/
    private void mifareCardYueMoneyAdjust(int money, int yueSub) {
        int yueBaseInt = Datautils.byteArrayToInt(cardOpDU.yueBase);
        if (money <= (yueBaseInt * cardOpDU.yuePosition))   // 月票计数器调整
        {
            cardOpDU.yueOriMoney = 0;
            cardOpDU.actYueSub = yueSub;
        } else if (money <= (yueBaseInt * (cardOpDU.yuePosition + 1))) {
            cardOpDU.yueOriMoney = money - yueBaseInt * cardOpDU.yuePosition;
            cardOpDU.actYueSub = yueSub;
        } else {
            cardOpDU.yueOriMoney = yueBaseInt;
            cardOpDU.actYueSub = money - yueBaseInt * (cardOpDU.yuePosition + 1) + yueSub;
        }
    }

    /*********************************************************************
     *      AddYueDate ： 计算月票标识的下一月，或下一季，或下一年        *
     *     ========================================================       *
     *  参数：*input                                                      *
     *        *output                                                     *
     *  功能：计算月票标识的下一月，或下一季，或下一年并返回结果          *
     *********************************************************************/
    private byte[] addYueDate(byte[] input) {
        int year, mon;
        byte[] output = new byte[2];
        // 月票标识=1-23：1-12=月票，13-16=季票，17=年票,18-19半年票
        if ((input[1] < 0) || (input[1] > 0x19)) {
            return new byte[]{(byte) 0x00, (byte) 0x00};
        }

        year = (input[0] & 0x0f) + (input[0] >> 4) * 10;                  // 年份由BCD码化为10进制
        mon = (input[1] & 0x0f) + (input[1] >> 4) * 10;                   // 月份由BCD码化为10进制
        if (mon < 13)                                              // 1-12月，月份+1
        {
            mon++;                                                  // 下一月
            if (mon == 13)                                             // 过年底
            {
                mon = 1;                                                // 重指向1月
                year++;                                               // 年份+1
            }
        } else if (input[1] < 0x17)                                  // 13、14、15、16，季票
        {
            mon++;                                                  // 下一季
            if (mon == 17)                                             // 过4季，重归1季
            {
                mon = 13;
                year++;                                               // 年份+1
            }
        } else if ((input[1] > 0x17) & (input[1] < 0x1a))                // 18、19，半年票
        {
            mon++;                                                   // 下半年
            if (mon == 0x1a)                                            // 过年，重归上半年
            {
                mon = 18;
                year++;                                                // 年份+1
            }
        } else                                                    // 17：年票
        {
            year++;                                                  // 下一年
        }
        output[0] = (byte) (((year / 10) << 4) + (year % 10));                     // 新的月季年标识重新化为BCD码
        output[1] = (byte) (((mon / 10) << 4) + (mon % 10));
        return output;
    }
}
