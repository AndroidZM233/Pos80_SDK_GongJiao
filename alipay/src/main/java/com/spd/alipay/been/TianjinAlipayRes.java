package com.spd.alipay.been;

import java.util.Arrays;

public class TianjinAlipayRes {
    public int result;
    public String uid;
    public int uidLen;
    public String record;
    public int recordLen;
    public String cardNo;
    public int cardNoLen;
    public byte[] cardData;
    public int cardDataLen;
    public String cardType;
    public int cardTypeLen;

    public TianjinAlipayRes() {
        super();
    }

    public TianjinAlipayRes(int result, String uid, int uidLen, String record, int recordLen, String cardNo, int cardNoLen, byte[] cardData, int cardDataLen, String cardType, int cardTypeLen) {
        this.result = result;
        this.uid = uid;
        this.uidLen = uidLen;
        this.record = record;
        this.recordLen = recordLen;
        this.cardNo = cardNo;
        this.cardNoLen = cardNoLen;
        this.cardData = cardData;
        this.cardDataLen = cardDataLen;
        this.cardType = cardType;
        this.cardTypeLen = cardTypeLen;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getUidLen() {
        return uidLen;
    }

    public void setUidLen(int uidLen) {
        this.uidLen = uidLen;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public int getRecordLen() {
        return recordLen;
    }

    public void setRecordLen(int recordLen) {
        this.recordLen = recordLen;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public int getCardNoLen() {
        return cardNoLen;
    }

    public void setCardNoLen(int cardNoLen) {
        this.cardNoLen = cardNoLen;
    }

    public byte[] getCardData() {
        return cardData;
    }

    public void setCardData(byte[] cardData) {
        this.cardData = cardData;
    }

    public int getCardDataLen() {
        return cardDataLen;
    }

    public void setCardDataLen(int cardDataLen) {
        this.cardDataLen = cardDataLen;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public int getCardTypeLen() {
        return cardTypeLen;
    }

    public void setCardTypeLen(int cardTypeLen) {
        this.cardTypeLen = cardTypeLen;
    }

    @Override
    public String toString() {
        return "TianjinAlipayRes{" +
                "result=" + result +
                ", uid='" + uid + '\'' +
                ", uidLen=" + uidLen +
                ", record='" + record + '\'' +
                ", recordLen=" + recordLen +
                ", cardNo='" + cardNo + '\'' +
                ", cardNoLen=" + cardNoLen +
                ", cardData=" + Arrays.toString(cardData) +
                ", cardDataLen=" + cardDataLen +
                ", cardType='" + cardType + '\'' +
                ", cardTypeLen=" + cardTypeLen +
                '}';
    }
}
