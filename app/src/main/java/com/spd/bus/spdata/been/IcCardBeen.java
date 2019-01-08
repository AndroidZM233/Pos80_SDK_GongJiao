package com.spd.bus.spdata.been;

/**
 * Created by on 2018/8/29.
 */

public class IcCardBeen {
    private byte[] snr;//卡芯片号
    private byte[] issueSnr;//卡流水号
    private byte[] cityNr;//城市代码
    private byte[] vocCode;//行业代码
    private byte[] issueCode;//发行流水号
    private byte[] mackNr;//卡认证码
    private byte[] fStartUse;//启用标志
    private int fBlackCard = 0;//黑名单标志
    private byte[] cardType;//卡类型
    private byte[] issueDate;//发行日期
    private byte[] endUserDate;//有效截止日期
    private byte[] startUserDate;//开始日期日期
    private byte[] purInNr;//钱包充值计数器
    private byte[] purIncUtc;//充值时间
    private byte[] purIncMoney;//充值金额
    private byte[] PurOriMoney;//原额
    private byte[] PurSub;//消费金额


    public byte[] getPurOriMoney() {
        return PurOriMoney;
    }

    public void setPurOriMoney(byte[] purOriMoney) {
        PurOriMoney = purOriMoney;
    }

    public byte[] getPurSub() {
        return PurSub;
    }

    public void setPurSub(byte[] purSub) {
        PurSub = purSub;
    }

    public int getfBlackCard() {
        return fBlackCard;
    }

    public void setfBlackCard(int fBlackCard) {
        this.fBlackCard = fBlackCard;
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

    public byte[] getCardType() {
        return cardType;
    }

    public void setCardType(byte[] cardType) {
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
}
