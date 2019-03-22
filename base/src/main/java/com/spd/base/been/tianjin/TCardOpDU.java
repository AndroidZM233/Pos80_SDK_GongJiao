package com.spd.base.been.tianjin;

import com.spd.base.utils.Datautils;

/**
 * Created by 张明_ on 2019/2/19.
 * Email 741183142@qq.com
 */
public class TCardOpDU {
    public byte[] ucPsamATC = new byte[4];

    public byte[] ucDateTime = new byte[8];
    public byte ucRcdType;
    public byte ucCardClass;
    public byte ucCAPP;
    public byte ucProcSec;//消费类型标识 2钱包 7月票
    //CPU
    public byte[] ucFile15Top8 = new byte[8];
    public byte[] ucIssuerCode = new byte[2];//0,1
    public byte[] ucCityCode = new byte[2];//2,3
    public byte[] ucVocCode = new byte[2];//4,5
    public byte[] ucRfu1 = new byte[2];//6,7
    public byte ucAppTypeFlag;//8
    public byte ucAppVer; //9
    public byte[] ucAppSnr = new byte[10];//10-19
    public byte[] ucAppStartDate = new byte[4];//20-23
    public byte[] ucAppEndDate = new byte[4];//24-27
    public byte[] ucRfu2 = new byte[2];//28-29

    public byte ucMainCardType;//30
    public byte ucSubCardType;//31
    public byte ucCardAppFlag;//32
    public byte[] ucCheckDate = new byte[4];//33-36
    public byte ucAppStartFlag;//37
    public byte ucRadioInCard; //38
    public byte[] uiValidDays = new byte[2]; //39-40
    public byte ucTimeLimitInCard;//41
    public byte[] ucRfu3 = new byte[6];//42-47
    /////////////////////////////////////////////////////JT
//Mifare
    public byte[] ucBlk5 = new byte[16];
    public byte[] ucBlk6 = new byte[16];

    public byte ucfBLACK;
    public byte ucPurStartFlag;
    public byte[] ucIssueDate = new byte[4];
    public byte ucBlackCard;
    public byte[] purIncApprovalNr = new byte[6];// 79-84: PurIncDev4+PurIncCount2
    public byte[] yueIncApprovalNr = new byte[6];// 85-90: YueIncDev4+YueIncCount2

    /////////////////////////////////////////////
    public byte ucCardTicket;
    public byte ucCardRadioP;
    public byte fToCheck;//年检
    public int fPermit;//带人
    public byte ucTickCnt;
    public byte fInBus;

    public byte[] ucIncPurDev = new byte[4];
    public int uiIncPurCount;
    public byte[] ucIncPurDate = new byte[4];

    public int lPurOriMoney;
    public int lPurSub;
    public byte[] lPurSubByte = new byte[4];
    public byte[] uiPurCount = new byte[2];

    public byte fYueAsPur;
    public byte ucYueSec;
    public byte ucYueFlag;
    public byte[] ucIncYueDev = new byte[4];
    public byte[] uiIncYueCount;
    public byte[] ucYueUsingDate = new byte[3];
    public byte ucYuePosition;
    public int ulYueBase;
    public int[] ulYueBase2 = new int[4];

    public int lYueOriMoney;
    public int lActYueOriMoney;
    public int lYueSub;
    public int lActYueSub;
    public byte[] uiYueCount = new byte[2];

    public byte ucOtherCity;
    public byte fUsePSAM;
    public byte ucPSAMPOS;
    public byte[] ucSafeAuthCode = new byte[9];
    public byte ucDiv;
    public byte[] ucKeyID = new byte[1];


    public int ulTradeValue;
    public byte[] ulTradeValueByte = new byte[4];
    public int ulBalance;
    public byte[] ulBalanceByte = new byte[4];
    public byte[] uiOffLineCount = new byte[2];
    public byte ucKeyVer;
    public byte ucKeyAlg;
    public byte[] rondomCpu = new byte[4];//伪随机数
    public byte[] ucPOSSnr = new byte[6];
    public int ulPOSTradeCount;
    public byte[] ucRandom = new byte[4];
    public byte[] ucMAC1 = new byte[4];
    public byte[] ucMAC2 = new byte[4];
    public byte[] ucTAC = new byte[4];

    public byte[] ucDatToCard = new byte[150];
    public byte[] ucDatInCard = new byte[150];
    public byte[] ucFKDevNr = new byte[3];
    public byte[] ucFKDriSnr = new byte[4];
    public byte[] ucFKCorNr = new byte[4];
    public byte[] ucFKLineNr = new byte[3];
    public byte[] ucFKBusNr = new byte[3];
    public byte fErr;
    //Use for Record
    public byte[] ucRcdCorNr = new byte[4];
    public byte[] ucRcdLineNr = new byte[4];
    public byte[] ucRcdBusNr = new byte[4];
    public byte[] ucRcdDevNr = new byte[4];
    public byte ucRcdStopIDUp;
    public byte ucRcdStopIDDn;
    //PBOC
    public byte[] uiATC = new byte[2];
    public byte ucValue5F34;
    //JTB
    public byte ucSimple;
    public byte ucLastTradeEnd;
    public byte ucTradeType;
    public int ulFKPurSub;

    //Other City
    public int fUseHC;//天津
    public int fHCSeg;
    public int fHC;
    public byte ucHCRadioP;

    public int hCQYueSub;
    public int hCQPurSub;
    public byte ucHCRcdValid;
    public int ulHCCadUTC;
    public byte fHCInCard;
    public byte fHCTradeMode;
    public byte[] hCPsamNr = new byte[4];
    public byte fHCMode;

    public byte first;
    public int yueSec;

    public byte[] ucYueStartDate = new byte[4];
    public byte[] ucYueEndDate = new byte[4];

    public byte dBYueFlag;

    //  uint8_t fTimeLimit;
    public int ulHCUTC;
    public byte[] uiHCDUTC = new byte[2];
    public int ulHCStartUTC;
    public int ulHCOriMoney;
    public int ulHCSub;
    public byte[] uiHCTimes = new byte[2];
    public byte[] ucHCBusNr = new byte[3];
    public byte[] ucHCPsamNevNo = new byte[6];
    public byte[] ucHCLineNr = new byte[2];
    public byte ucHCRcdType;
    public byte[] uiHCPrice = new byte[2];
    public byte ucHCBusType;

    public byte ucCardVer;

    //天津M1卡
    public byte fStartUsePur;
    public byte[] snr;//卡芯片号
    public byte[] issueSnr;//卡流水号
    public byte[] cityNr;//城市代码
    public byte[] vocCode;//行业代码
    public byte[] issueCode;//发行流水号
    public byte[] mackNr;//卡认证码
    public byte[] fStartUse;//启用标志
    public boolean fBlackCard;//黑名单标志
    public byte cardType;//卡类型
    public byte[] issueDate;//发行日期
    public byte[] endUserDate;//有效截止日期
    public byte[] startUserDate;//开始日期日期
    public byte[] purInNr;//钱包充值计数器
    public byte[] purIncUtc;//充值时间
    public byte[] purIncMoney;//充值金额
    public int purIncMoneyInt;//充值金额
    public byte[] purorimoney;//原额
    public int purorimoneyInt;//原额
    public byte[] pursub;//消费金额
    public int pursubInt;//消费金额
    public byte cardClass;
    public int purCount;
    public int procSec;

    public byte subType;
    public byte[] yueBase;
    public byte[] yueUsingDate;
    public byte yuePosition;
    public int yueFlag;
    public int yuePositionInt;
    public int actYueOriMoney;
    public int yueSub;
    public int yueOriMoney;
    public int actYueSub;
    public int yueCount;
    public byte[] ucRcdToCard;


    public void setDataStartUcIssuerCode(byte[] DBDat) {
        ucIssuerCode = Datautils.cutBytes(DBDat, 0, 2);
        ucCityCode = Datautils.cutBytes(DBDat, 2, 2);
        ucVocCode = Datautils.cutBytes(DBDat, 4, 2);
        ucRfu1 = Datautils.cutBytes(DBDat, 6, 2);
        ucAppTypeFlag = Datautils.cutBytes(DBDat, 8, 1)[0];
        ucAppVer = Datautils.cutBytes(DBDat, 9, 1)[0];
        ucAppSnr = Datautils.cutBytes(DBDat, 10, 10);
        ucAppStartDate = Datautils.cutBytes(DBDat, 20, 4);
        ucAppEndDate = Datautils.cutBytes(DBDat, 24, 4);
        ucRfu2 = Datautils.cutBytes(DBDat, 28, 2);
    }


    public void setDataStartUcMainCardType(byte[] DBDat) {
        ucMainCardType = DBDat[0];
        ucSubCardType = DBDat[1];
        ucCardAppFlag = DBDat[2];
        ucCheckDate = Datautils.cutBytes(DBDat, 3, 4);
        ucAppStartFlag = DBDat[7];
        ucRadioInCard = DBDat[8];
        uiValidDays = Datautils.cutBytes(DBDat, 9, 2);
        ucTimeLimitInCard = DBDat[10];
        ucRfu3 = Datautils.cutBytes(DBDat, 11, 6);
    }

    public byte getfStartUsePur() {
        return fStartUsePur;
    }

    public void setfStartUsePur(byte fStartUsePur) {
        this.fStartUsePur = fStartUsePur;
    }

    public byte[] getSnr() {
        return snr;
    }

    public void setSnr(byte[] snr) {
        this.snr = snr;
    }

    public byte[] getIssueSnr() {
        return issueSnr;
    }

    public void setIssueSnr(byte[] issueSnr) {
        this.issueSnr = issueSnr;
    }

    public byte[] getCityNr() {
        return cityNr;
    }

    public void setCityNr(byte[] cityNr) {
        this.cityNr = cityNr;
    }

    public byte[] getVocCode() {
        return vocCode;
    }

    public void setVocCode(byte[] vocCode) {
        this.vocCode = vocCode;
    }

    public byte[] getIssueCode() {
        return issueCode;
    }

    public void setIssueCode(byte[] issueCode) {
        this.issueCode = issueCode;
    }

    public byte[] getMackNr() {
        return mackNr;
    }

    public void setMackNr(byte[] mackNr) {
        this.mackNr = mackNr;
    }

    public byte[] getfStartUse() {
        return fStartUse;
    }

    public void setfStartUse(byte[] fStartUse) {
        this.fStartUse = fStartUse;
    }

    public boolean isfBlackCard() {
        return fBlackCard;
    }

    public void setfBlackCard(boolean fBlackCard) {
        this.fBlackCard = fBlackCard;
    }

    public byte getCardType() {
        return cardType;
    }

    public void setCardType(byte cardType) {
        this.cardType = cardType;
    }

    public byte[] getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(byte[] issueDate) {
        this.issueDate = issueDate;
    }

    public byte[] getEndUserDate() {
        return endUserDate;
    }

    public void setEndUserDate(byte[] endUserDate) {
        this.endUserDate = endUserDate;
    }

    public byte[] getStartUserDate() {
        return startUserDate;
    }

    public void setStartUserDate(byte[] startUserDate) {
        this.startUserDate = startUserDate;
    }

    public byte[] getPurInNr() {
        return purInNr;
    }

    public void setPurInNr(byte[] purInNr) {
        this.purInNr = purInNr;
    }

    public byte[] getPurIncUtc() {
        return purIncUtc;
    }

    public void setPurIncUtc(byte[] purIncUtc) {
        this.purIncUtc = purIncUtc;
    }

    public byte[] getPurIncMoney() {
        return purIncMoney;
    }

    public void setPurIncMoney(byte[] purIncMoney) {
        this.purIncMoney = purIncMoney;
    }

    public byte[] getPurorimoney() {
        return purorimoney;
    }

    public void setPurorimoney(byte[] purorimoney) {
        this.purorimoney = purorimoney;
    }

    public byte[] getPursub() {
        return pursub;
    }

    public void setPursub(byte[] pursub) {
        this.pursub = pursub;
    }
}
