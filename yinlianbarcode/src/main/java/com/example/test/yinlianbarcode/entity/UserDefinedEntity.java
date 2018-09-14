package com.example.test.yinlianbarcode.entity;

/**
 * 乘车二维码行业自定义数据格式
 * Created by 张明_ on 2018/7/26.
 * Email 741183142@qq.com
 */

public class UserDefinedEntity {
    //凭证类型
    private byte[] voucherType;
    //进出站使用限制
    private byte[] useRestriction;
    //站点限制
    private byte[] stationRestriction;
    //行程单号
    private byte[] itinerary;
    //本行程关联的进站或上车站点编码
    private byte[] encoding;
    //本行程扫码进站或上车时间
    private byte[] time;

    public byte[] getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(byte[] voucherType) {
        this.voucherType = voucherType;
    }

    public byte[] getUseRestriction() {
        return useRestriction;
    }

    public void setUseRestriction(byte[] useRestriction) {
        this.useRestriction = useRestriction;
    }

    public byte[] getStationRestriction() {
        return stationRestriction;
    }

    public void setStationRestriction(byte[] stationRestriction) {
        this.stationRestriction = stationRestriction;
    }

    public byte[] getItinerary() {
        return itinerary;
    }

    public void setItinerary(byte[] itinerary) {
        this.itinerary = itinerary;
    }

    public byte[] getEncoding() {
        return encoding;
    }

    public void setEncoding(byte[] encoding) {
        this.encoding = encoding;
    }

    public byte[] getTime() {
        return time;
    }

    public void setTime(byte[] time) {
        this.time = time;
    }
}
