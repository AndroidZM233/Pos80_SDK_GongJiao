package com.example.test.yinlianbarcode.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author :Reginer in  2018/8/2 14:51.
 * 联系方式:QQ:282921012
 * 功能描述:行程扣款
 */
public class ItineraryEntity {
    @SerializedName("order_no")
    private String orderNo;
    @SerializedName("app_id")
    private String appId = "A310120180000029";
    @SerializedName("trip_no")
    private String tripNo;
    @SerializedName("fellow_no")
    private String fellowNo = "0000000000000000";
    @SerializedName("fee_mode")
    private String feeMode;
    @SerializedName("trans_time")
    private String transTime;
    @SerializedName("trans_amount")
    private String transAmount;
    @SerializedName("base_amount")
    private String baseAmount;
    @SerializedName("discount_amount")
    private String discountAmount;
    @SerializedName("discount_desc")
    private String discountDesc;
    @SerializedName("fine_amount")
    private String fineAmount;
    @SerializedName("fine_desc")
    private String fineDesc;
    @SerializedName("settlement_mode")
    private String settlementMode = "1";
    @SerializedName("settlement_amount")
    private String settlementAmount;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTripNo() {
        return tripNo;
    }

    public void setTripNo(String tripNo) {
        this.tripNo = tripNo;
    }

    public String getFellowNo() {
        return fellowNo;
    }

    public void setFellowNo(String fellowNo) {
        this.fellowNo = fellowNo;
    }

    public String getFeeMode() {
        return feeMode;
    }

    public void setFeeMode(String feeMode) {
        this.feeMode = feeMode;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(String transAmount) {
        this.transAmount = transAmount;
    }

    public String getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(String baseAmount) {
        this.baseAmount = baseAmount;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getDiscountDesc() {
        return discountDesc;
    }

    public void setDiscountDesc(String discountDesc) {
        this.discountDesc = discountDesc;
    }

    public String getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(String fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getFineDesc() {
        return fineDesc;
    }

    public void setFineDesc(String fineDesc) {
        this.fineDesc = fineDesc;
    }

    public String getSettlementMode() {
        return settlementMode;
    }

    public void setSettlementMode(String settlementMode) {
        this.settlementMode = settlementMode;
    }

    public String getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(String settlementAmount) {
        this.settlementAmount = settlementAmount;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
