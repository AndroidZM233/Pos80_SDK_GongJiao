package com.spd.base.been.tianjin.produce.zhifubao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by 张明_ on 2019/3/12.
 * Email 741183142@qq.com
 */
@Entity
public class UploadInfoZFBDB {
    @Id(autoincrement = false)
    // 交易流水
    private String outTradeNo;
    // 设备号
    private String deviceId;
    // 司机卡号
    private String driverCardNo;
    // 卡类型
    private String cardType;
    // 用户号
    private String userId;
    // 车辆号
    private String carryCode;
    // 路队号
    private String busGroupCode;
    // 公司号
    private String companyCode;
    // 站点名称
    private String stationName;
    // 电子公交卡卡号
    private String cardId;
    // 售票员签到时间
    private String sellerSignTime;
    // 机具内扫码序号
    private String seq;
    // 司机签到时间
    private String driverSignTime;
    // 区域号
    private String areaCode;
    // 票价
    private String price;
    // 站点号
    private String stationId;
    // 交易时间
    private String actualOrderTime;
    private String cardData;
    // 真实票价
    private String actualPrice;
    // 线路号
    private String lineCode;
    // 记录内容
    private String record;

    //是否上传
    private boolean isUpload;

    @Generated(hash = 1493679937)
    public UploadInfoZFBDB(String outTradeNo, String deviceId, String driverCardNo,
            String cardType, String userId, String carryCode, String busGroupCode,
            String companyCode, String stationName, String cardId,
            String sellerSignTime, String seq, String driverSignTime,
            String areaCode, String price, String stationId, String actualOrderTime,
            String cardData, String actualPrice, String lineCode, String record,
            boolean isUpload) {
        this.outTradeNo = outTradeNo;
        this.deviceId = deviceId;
        this.driverCardNo = driverCardNo;
        this.cardType = cardType;
        this.userId = userId;
        this.carryCode = carryCode;
        this.busGroupCode = busGroupCode;
        this.companyCode = companyCode;
        this.stationName = stationName;
        this.cardId = cardId;
        this.sellerSignTime = sellerSignTime;
        this.seq = seq;
        this.driverSignTime = driverSignTime;
        this.areaCode = areaCode;
        this.price = price;
        this.stationId = stationId;
        this.actualOrderTime = actualOrderTime;
        this.cardData = cardData;
        this.actualPrice = actualPrice;
        this.lineCode = lineCode;
        this.record = record;
        this.isUpload = isUpload;
    }

    @Generated(hash = 1585259627)
    public UploadInfoZFBDB() {
    }

    public String getOutTradeNo() {
        return this.outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDriverCardNo() {
        return this.driverCardNo;
    }

    public void setDriverCardNo(String driverCardNo) {
        this.driverCardNo = driverCardNo;
    }

    public String getCardType() {
        return this.cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCarryCode() {
        return this.carryCode;
    }

    public void setCarryCode(String carryCode) {
        this.carryCode = carryCode;
    }

    public String getBusGroupCode() {
        return this.busGroupCode;
    }

    public void setBusGroupCode(String busGroupCode) {
        this.busGroupCode = busGroupCode;
    }

    public String getCompanyCode() {
        return this.companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getStationName() {
        return this.stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getCardId() {
        return this.cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getSellerSignTime() {
        return this.sellerSignTime;
    }

    public void setSellerSignTime(String sellerSignTime) {
        this.sellerSignTime = sellerSignTime;
    }

    public String getSeq() {
        return this.seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getDriverSignTime() {
        return this.driverSignTime;
    }

    public void setDriverSignTime(String driverSignTime) {
        this.driverSignTime = driverSignTime;
    }

    public String getAreaCode() {
        return this.areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStationId() {
        return this.stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getActualOrderTime() {
        return this.actualOrderTime;
    }

    public void setActualOrderTime(String actualOrderTime) {
        this.actualOrderTime = actualOrderTime;
    }

    public String getCardData() {
        return this.cardData;
    }

    public void setCardData(String cardData) {
        this.cardData = cardData;
    }

    public String getActualPrice() {
        return this.actualPrice;
    }

    public void setActualPrice(String actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getLineCode() {
        return this.lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getRecord() {
        return this.record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public boolean getIsUpload() {
        return this.isUpload;
    }

    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }


}
