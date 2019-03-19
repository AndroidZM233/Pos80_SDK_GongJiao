package com.spd.bus.card.methods;

import android.os.RemoteException;
import android.util.Log;

import com.spd.base.been.tianjin.TDutyStatFile;
import com.spd.base.been.tianjin.TCardOpDU;
import com.spd.base.been.tianjin.TStaffTb;
import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.Datautils;
import com.spd.base.utils.DesUtil;
import com.spd.bus.card.utils.LogUtils;
import com.spd.bus.spdata.been.TCommInfo;

import java.util.Arrays;
import java.util.List;

import wangpos.sdk4.libbasebinder.BankCard;

import static com.spd.bus.card.methods.ReturnVal.CAD_OK;
import static com.spd.bus.card.methods.ReturnVal.CAD_READ;
import static com.spd.bus.card.methods.ReturnVal.CAD_SETCERR;

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

    public static final byte M150 = 0x51;
    public static final byte M170 = 0x71;

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

    public static M1CardManager getInstance() {
        if (jtbCardManager == null) {
            synchronized (LOCK) {
                jtbCardManager = new M1CardManager();
            }
        }
        return jtbCardManager;
    }


    public int mainMethod(BankCard mBankCard, int card, int devMode) throws Exception {
        this.mBankCard = mBankCard;
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession()
                .getRunParaFileDao().loadAll();
        if (runParaFiles.size() == 0) {
            return ReturnVal.CAD_READ;
        } else {
            runParaFile = runParaFiles.get(0);
        }

        secOffset = 0;
        cardOpDU = new TCardOpDU();
        if (card == 71) {
            secOffset = 16;
        } else {
            secOffset = 0;
        }

        int ret;

        if (checkMifcardclass(mBankCard) != CAD_OK) {
            return 255;
        }
        switch (cardOpDU.cardClass) {
            case 0x01:
                ret = busCard(mBankCard, cardOpDU.cardClass, devMode);
                break;
            case 0x51:
            case 0x71:
//                ret = CITY_card();
                ret = 255;
                break;
            default:
                ret = 255;
        }
        return ret;


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
    private int busCard(BankCard mBankCard, int cardClass, int devMode) throws Exception {
        TCommInfo cinfoz = new TCommInfo();
        TCommInfo cinfof = new TCommInfo();
        TCommInfo cinfo = new TCommInfo();
        cardClass = 1;
        cardOpDU.ucOtherCity = (byte) 0x00;
        cardOpDU.fUseHC = (byte) 0xff;

        //系统时间
        byte[] systemTime = Datautils.getDateTime();
        cardOpDU.ucDateTime = systemTime;
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
                return 255;
            case 0x02:  //Start
                break;
            case 0x04:
                cardOpDU.fBlackCard = true;//Black
                break;
            default:
                return ReturnVal.CAD_BROKEN;
        }

        byte[] ucDateTimeByte = Datautils.cutBytes(cardOpDU.ucDateTime, 1, 3);
        int ucDateTimeInt = Integer.parseInt(Datautils.byteArrayToString(ucDateTimeByte));
        int ucAppStartDateInt = Integer.parseInt(Datautils.byteArrayToString(cardOpDU.startUserDate));
        //当前时间小于开始时间
        if (ucDateTimeInt < ucAppStartDateInt) {
            return ReturnVal.CAD_SELL;
        }
        int ucAppEndDateInt = Integer.parseInt(Datautils.byteArrayToString(cardOpDU.endUserDate));
        if (ucDateTimeInt > ucAppEndDateInt) {
            return ReturnVal.CAD_EXPIRE;
        }


        // 卡禁止，但允许管理卡刷卡
        if ((fLockCard == 1) && (cardOpDU.cardType < (byte) 0x80)) {
            return 255;
        }

        //第6扇区24 块认证
        byte[] lodKey6 = lodkey[6];
        retvalue = mBankCard.m1CardKeyAuth(0x41, 24 + secOffset, lodKey6.length, lodKey6, snUid.length, snUid);
        if (retvalue != 0) {
            LogUtils.e("===第6扇区24 块认证错误===");
            return CAD_READ;
        }
        //读6扇区第24块
        retvalue = mBankCard.m1CardReadBlockData(24 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("===读6扇区第24块失败===");
            return CAD_READ;
        }
        byte[] bytes24 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);


        LogUtils.d("===读6扇区第24块返回===" + Datautils.byteArrayToString(bytes24));
        byte[] dtZ = bytes24;
        byte chk = 0;
        //异或操作
        for (int i = 0; i < 16; i++) {
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
        //交易记录指针
        cinfoz.cPtr = dtZ[0];
        //钱包计数,2,3
        cinfoz.iPurCount = Datautils.cutBytes(dtZ, 1, 2);
        //进程标志
        cinfoz.fProc = dtZ[3];
        cinfoz.iYueCount = Datautils.cutBytes(dtZ, 4, 2);
        cinfoz.fBlack = dtZ[6];
        cinfoz.fFileNr = dtZ[7];
        //副本  有效性
        //读6扇区第25块
        retvalue = mBankCard.m1CardReadBlockData(25 + secOffset * 4, respdata, resplen);
        if (retvalue != 0) {
            LogUtils.e("===读6扇区第25块失败===");
            return CAD_READ;
        }
        byte[] bytes25 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
        byte[] dtF = bytes25;
        for (int i = 0; i < 16; i++) {
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
        cinfof.cPtr = dtF[0];
        cinfof.iPurCount = Datautils.cutBytes(dtF, 1, 2);
        cinfof.fProc = dtF[3];
        cinfof.iYueCount = Datautils.cutBytes(dtF, 4, 2);
        cinfof.fBlack = dtF[6];
        cinfof.fFileNr = dtF[7];

        if (cinfoz.fValid == 1) {
            cinfo = cinfoz;
        } else if (cinfof.fValid == 1) {
            cinfo = cinfof;
        } else {
            LogUtils.e("===24 25块有效标志错误 返回0===");
            return CAD_READ;
        }

        if ((cinfoz.fValid == 1 && (cinfoz.fBlack == 4)) || (cinfof.fValid == 1 && (cinfof.fBlack == 4))) {
            //黑名单 报语音
            cardOpDU.fBlackCard = true;
            LogUtils.e("m1ICCard: 黑名单");
            return ReturnVal.CAD_BLK;
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
                    return ReturnVal.CAD_WRITE;
                }
                if (!CardMethods.modifyInfoArea(25, cinfo, mBankCard, lodkey, snUid)) {
                    return ReturnVal.CAD_WRITE;
                }
//                cardOpDU.ucRcdType = 0xE0;
//                PrepareRecordTrade(cardOpDU.ucRcdType);
//                OnAppendRecordTrade(cardOpDU.ucRcdType);
                // TODO: 2019/3/18 写消费记录
                return ReturnVal.CAD_BLK;
            }
        }

        //////////////////End Card_Pretreat///////////////////////////////////
        if (cardOpDU.cardType == (byte) 0x99) {
            return ReturnVal.CAD_TEST_C;
        }

        if (cardOpDU.cardType == (byte) 0x91) {

        }

        if (cardOpDU.cardType == 0x0e) {
            if (devMode == 2) {
                boolean equals = Arrays.equals(tStaffTb.ucCityCode, Datautils.cutBytes(cardOpDU.snr
                        , 0, 4));
                if (equals) {
                    cardOpDU.purIncMoneyInt = 0;
//                    MachStatusFile.fSysSta = CAD_NORMAL;
                    return ReturnVal.CAD_EMPTY;
                }
            } else {
                byte[] lodkey6 = lodkey[6];
                retvalue = mBankCard.m1CardKeyAuth(0x41, 26 + secOffset,
                        lodkey6.length, lodkey6, snUid.length, snUid);
                if (retvalue != 0) {
                    LogUtils.e("===认证6扇区失败===");
                    return ReturnVal.CAD_ACCESS;
                }
                retvalue = mBankCard.m1CardReadBlockData(26 + secOffset * 4, respdata, resplen);
                if (retvalue != 0) {
                    LogUtils.e("=== 读取6扇区第26块失败==");
                    return CAD_READ;
                }

                byte[] bytes06 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
                if ((bytes06[10] & 0x40) == 0) {
                    return CAD_SETCERR;
                }
                tStaffTb = new TStaffTb();
                tStaffTb.ucCardClass = cardOpDU.cardClass;
                tStaffTb.ucIssuerCode = Datautils.cutBytes(cardOpDU.issueSnr, 0, 2);
                tStaffTb.ucCityCode = Datautils.cutBytes(cardOpDU.snr, 0, 4);
                tStaffTb.ucAppSnr = Datautils.cutBytes(cardOpDU.issueSnr, 0, 8);
                tStaffTb.ucMainCardType = cardOpDU.cardType;
                tStaffTb.ucSubCardType = 0;
                tStaffTb.ucAppStartYYMMDD = Datautils.cutBytes(cardOpDU.startUserDate, 0, 3);
                tStaffTb.ulUTC = cardOpDU.ucDateTime;

                dutyStatFile = new TDutyStatFile();
                dutyStatFile.rcdNr = 0;                                        // 清除当班司机运营统计区
                dutyStatFile.totalPerson = 0;                                     // 总记录数，总有效人次
                dutyStatFile.totalMoney = 0;                                      // 总金额
                dutyStatFile.totalYuePerson = 0;  // 总月票人次
                dutyStatFile.fWork = 1;
                dutyStatFile.ucLogOK = 0;
                dutyStatFile.tickcnt = 0;
                dutyStatFile.fCreateMode = 1;
//                OnAppendRecordSelf(0xE1);
                DbDaoManage.getDaoSession().getTStaffTbDao().deleteAll();
                DbDaoManage.getDaoSession().getTStaffTbDao().insert(tStaffTb);
                DbDaoManage.getDaoSession().getTDutyStatFileDao().deleteAll();
                DbDaoManage.getDaoSession().getTDutyStatFileDao().insert(dutyStatFile);
                // TODO: 2019/3/18 写消费记录
                return ReturnVal.CAD_LOGON;
            }
        }

        if (dutyStatFile.fWork == 0) {
            return CAD_SETCERR;
        }


        /////本机恢复//////////////////////////////////////////////////////////////
        // TODO: 2019/3/18 恢复


        int ret = CardMethods.fIsUseYue(cardOpDU, runParaFile);
        if (ret == 255) {
            return CAD_READ;
        }
        if (ret == 1)//月票有效
        {
            ret = mifareCardGetYueBasePos(mBankCard);
            if (ret == 255) {
                return 255;
            }
            if (ret == 0) {
                cardOpDU.procSec = 2;
            } else {
                cardOpDU.procSec = cardOpDU.yueSec;
            }
        } else {
            cardOpDU.procSec = 2;
        }

        return CAD_OK;
    }

    private int checkMifcardclass(BankCard mBankCard) throws Exception {
        LogUtils.d("===m1卡消费开始===");
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
        retvalue = mBankCard.m1CardKeyAuth(0x41, 4 + secOffset, key.length, key, snUid.length, snUid);
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
        retvalue = mBankCard.m1CardKeyAuth(0x41, 1 + secOffset,
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

        //des解密
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
            byte[] resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM2_APDU
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
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, Datautils.HexString2Bytes(sendCmd), Datautils.HexString2Bytes(sendCmd).length, respdata, resplen);
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
        cardOpDU.uiIncYueCount = Datautils.cutBytes(bytes28, 8, 2);

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
//                    if (carddu.YuePosition > 0) {
//                        carddu.YuePosition -= 1;
//                        AddYueDate(YueDate, NextTime);
//                        memcpy(carddu.YueUsingDate, YueDate, 2);//year month
//                        carddu.YueUsingDate[2] = 0x32;
//                        valid = 1;
//                    }
                }
            }
        } else {

        }
//        if (YueStartDate[2] == 0x32)//Month,Season,Year
//        {
//            memcpy( & YueDate[0], YueEndDate, 2);
//            for (valid = 0, i = 0; ; i++) {
//            }
//            //////////////Add Of TianJin////////////////////
//            if (valid == 1) {
//                ret = JudgeYueScope();
//                if (ret == 255) return 255;
//                if (ret == F_OVERFLOW) return F_OVERFLOW;
//                if (ret == F_LEAST) {
//                    valid = 0;
//                    if (carddu.YuePosition > 0) {
//                        carddu.YuePosition -= 1;
//                        AddYueDate(YueDate, NextTime);
//                        memcpy(carddu.YueUsingDate, YueDate, 2);//year month
//                        carddu.YueUsingDate[2] = 0x32;
//                        valid = 1;
//                    }
//                }
//            }
//        } else {
//            carddu.YueFlag = 1;                //10 day card
//            if ((memcmp( & pAppDateTime[1],YueStartDate, 3)>=0) &&
//            (memcmp( & pAppDateTime[1], YueEndDate, 3)<=0))
//            {
//                carddu.YuePosition = 0;
//                memcpy(carddu.YueUsingDate, YueStartDate, 3);   // year month
//                valid = 1;
//            }else{
//                valid = 0;
//            }
//        }
        return valid = 0;
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
        byte[] dtZ = new byte[16];
        byte[] dtF = new byte[16];
        int ActRemaining = 0;

        cardOpDU.procSec = cardOpDU.yueSec;
        try {
            ret = BackupManage(cardOpDU.procSec + secOffset);          //_dt and _backup are all wrong
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
            ActRemaining <<= 8;
            ActRemaining += dtZ[3 - i];
        }
        cardOpDU.actYueOriMoney = ActRemaining;
        cardOpDU.yueSub = 1;

        MifareCard_YueMoneyAdjust(cardOpDU.actYueOriMoney, cardOpDU.yueSub);

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
    private int BackupManage(int blk) {
        //第二扇区08 块认证
        try {
            byte[] lodKey2 = lodkey[blk / 4];
            retvalue = mBankCard.m1CardKeyAuth(0x41, blk + secOffset, lodKey2.length, lodKey2, snUid.length, snUid);
            if (retvalue != 0) {
                LogUtils.e("===认证2扇区第9块失败===");
                return 255;
            }
            //读2扇区第9块
            retvalue = mBankCard.m1CardReadBlockData(9 + secOffset * 4, respdata, resplen);
            if (retvalue != 0) {
                LogUtils.e("===读2扇区第9块失败===");
                return 255;
            }
            byte[] bytes09 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
            LogUtils.d("===读2扇区第9块返回===" + Datautils.byteArrayToString(bytes09));

            //读2扇区第10块
            retvalue = mBankCard.m1CardReadBlockData(10, respdata, resplen);
            if (retvalue != 0) {
                LogUtils.e("===读2扇区第10块失败===");
                return 255;
            }
            byte[] bytes10 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
            LogUtils.d("===读2扇区第10块返回===" + Datautils.byteArrayToString(bytes10));

            if (ValueBlockValid(bytes09)) {
                LogUtils.d("=== 2区09块过===");
                //判断2区9块10块数据是否一致
                if (!Arrays.equals(bytes09, bytes10)) {
                    retvalue = mBankCard.m1CardValueOperation(0x3E, 9, 0, 10);
                    if (retvalue != 0) {
                        LogUtils.e("===更新2区09块失败===");
                        return 255;
                    }
                    return 1;
                }

                return 0;
            } else {
                if (ValueBlockValid(bytes10)) {
                    LogUtils.d("===2区10块过===");
                    if (!Arrays.equals(bytes09, bytes10)) {
                        bytes09 = bytes10;
                        retvalue = mBankCard.m1CardValueOperation(0x3E, 10, 0, 9);
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
            //原额
//            byte[] yue09 = Datautils.cutBytes(bytes09, 0, 4);
//            cardOpDU.setPurorimoney(yue09);
//            //定义消费金额
//            cardOpDU.setPursub(new byte[]{0x00, 0x00, 0x00, (byte) 0x01});
//            LogUtils.d("===原额===" + Datautils.byteArrayToString(yue09));
//            return true;
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
    public boolean ValueBlockValid(byte[] dts) {//
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
    void MifareCard_YueMoneyAdjust(int Money, int YueSub) {
        int yueBaseInt = Datautils.byteArrayToInt(cardOpDU.yueBase);
        if (Money <= (yueBaseInt * cardOpDU.yuePosition))   // 月票计数器调整
        {
            cardOpDU.yueOriMoney = 0;
            cardOpDU.actYueSub = YueSub;
        } else if (Money <= (yueBaseInt * (cardOpDU.yuePosition + 1))) {
            cardOpDU.yueOriMoney = Money - yueBaseInt * cardOpDU.yuePosition;
            cardOpDU.actYueSub = YueSub;
        } else {
            cardOpDU.yueOriMoney = yueBaseInt;
            cardOpDU.actYueSub = Money - yueBaseInt * (cardOpDU.yuePosition + 1) + YueSub;
        }
    }
}
