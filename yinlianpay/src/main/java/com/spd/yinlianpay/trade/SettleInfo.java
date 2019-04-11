package com.spd.yinlianpay.trade;

import java.io.Serializable;

/**
 * Created by Tommy on 2016/6/12.
 */
public class SettleInfo implements Serializable {
    private String merchantName;//商户名称
    private int cardType;// 刷卡类型
    private int tradeType; // 交易类型
    private long amount; // 交易金额
    private long discount; // 优惠金额

    private String cardNo; // 卡号
    private String cardSerialNo; // 卡序列号
    private String serialNo; // 流水号（凭证号）
    private String batchNo; // 批次号
    private String inputType; // 卡输入类型
    private String field55; // 55域信息
    private String terminalCapability; // 终端读取能力

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("cardType=").append(cardType).append(", tradeType=").append(tradeType)
                .append(", amount=").append(amount).append(", cardNo=").append(cardNo).append(", discount=").append(discount)
                .append(", cardSerialNo=").append(cardSerialNo).append(", serialNo=").append(serialNo)
                .append(", inputType=").append(inputType).append(", terminalCapability=").append(terminalCapability)
                .append(", field55=").append(field55);
        return builder.toString();
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public long getDiscount() {
        return discount;
    }

    public void setDiscount(long discount) {
        this.discount = discount;
    }

    public String getField55() {
        return field55;
    }

    public void setField55(String field55) {
        this.field55 = field55;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardSerialNo() {
        return cardSerialNo;
    }

    public void setCardSerialNo(String cardSerialNo) {
        this.cardSerialNo = cardSerialNo;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getTerminalCapability() {
        return terminalCapability;
    }

    public void setTerminalCapability(String terminalCapability) {
        this.terminalCapability = terminalCapability;
    }

    public int getTradeType() {
        return tradeType;
    }

    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }

}
