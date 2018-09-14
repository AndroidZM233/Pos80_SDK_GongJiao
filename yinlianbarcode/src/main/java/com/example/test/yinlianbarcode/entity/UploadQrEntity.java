package com.example.test.yinlianbarcode.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author :Reginer in  2018/8/2 13:57.
 * 联系方式:QQ:282921012
 * 功能描述:
 */
public class UploadQrEntity {
    /**
     * 终端交易流水
     */
    @SerializedName("trans_seq")
    private String transSeq;
    /**
     * 二维码凭证
     */
    @SerializedName("voucher_no")
    private String voucherNo;
    /**
     * 行程单号
     */
    @SerializedName("trip_no")
    private String tripNo;
    /**
     * 服务商移动应用id
     */
    @SerializedName("app_id")
    private String appId;
    /**
     * 业务标识
     */
    @SerializedName("service_id")
    private String serviceId;
    /**
     * 用户标识
     */
    @SerializedName("user_id")
    private String userId;
    /**
     * 用户凭证类型
     */
    @SerializedName("voucher_type")
    private String voucherType;
    /**
     * 凭证生成时间
     */
    @SerializedName("create_time")
    private String createTime;
    /**
     * 交通运营商id
     */
    @SerializedName("op_id")
    private String opId;
    /**
     * 线路号
     */
    @SerializedName("line_no")
    private String lineNo;
    /**
     * 扫码站点号
     */
    @SerializedName("station_no")
    private String stationNo;
    /**
     * 进出站标识
     */
    private String direction;
    /**
     * 扫码终端机构号
     */
    @SerializedName("terminal_no")
    private String terminalNo;
    /**
     * 终端ip地址
     */
    @SerializedName("terminal_ip")
    private String terminalIp;
    /**
     * 扫码时间
     */
    @SerializedName("scan_time")
    private String scanTime;
    /**
     * 扫码确认类型
     */
    @SerializedName("scan_confirm_type")
    private String scanConfirmType;
    /**
     * 二维码元数据
     */
    @SerializedName("qrcode_data")
    private String qrCodeData;
    /**
     * 经度
     */
    private double lan;
    /**
     * 维度
     */
    private double lat;
    /**
     * 金额
     */
    private String amount;

    /**
     * 保留域
     */
    private String reserved;

    public String getTransSeq() {
        return transSeq;
    }

    public void setTransSeq(String transSeq) {
        this.transSeq = transSeq;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    public String getTripNo() {
        return tripNo;
    }

    public void setTripNo(String tripNo) {
        this.tripNo = tripNo;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOpId() {
        return opId;
    }

    public void setOpId(String opId) {
        this.opId = opId;
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public String getStationNo() {
        return stationNo;
    }

    public void setStationNo(String stationNo) {
        this.stationNo = stationNo;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public String getTerminalIp() {
        return terminalIp;
    }

    public void setTerminalIp(String terminalIp) {
        this.terminalIp = terminalIp;
    }

    public String getScanTime() {
        return scanTime;
    }

    public void setScanTime(String scanTime) {
        this.scanTime = scanTime;
    }

    public String getScanConfirmType() {
        return scanConfirmType;
    }

    public void setScanConfirmType(String scanConfirmType) {
        this.scanConfirmType = scanConfirmType;
    }

    public String getQrCodeData() {
        return qrCodeData;
    }

    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }

    public double getLan() {
        return lan;
    }

    public void setLan(double lan) {
        this.lan = lan;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
