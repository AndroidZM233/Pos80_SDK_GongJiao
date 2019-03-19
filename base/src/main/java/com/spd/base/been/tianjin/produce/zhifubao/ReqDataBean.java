package com.spd.base.been.tianjin.produce.zhifubao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 张明_ on 2019/3/11.
 * Email 741183142@qq.com
 */
public class ReqDataBean {
    /**
     * outTradeNo : 0645_17510645_20180311050443
     * deviceId : 17510645
     * driverCardNo : 3000000151654258
     * cardType : T0120000
     * userId : 2088422423867101
     * carryCode : 031364
     * busGroupCode : 0025
     * companyCode : 03
     * stationName :
     * cardId : 1200000846039941
     * sellerSignTime :
     * seq : 71b9
     * driverSignTime : 20180310051132
     * areaCode :
     * price : 150
     * stationId :
     * actualOrderTime : 2018-03-11 05:04:43
     * cardData : 31
     * actualPrice : 150
     * lineCode : 0645
     * record : 10E202010058323038383432323432333836373130315AAD82EA025807D000000000000000000000000002BA57F29F424209612D7545D3B0E6A492E937E7F59BC4D442543031323030303010313230303030303834363033393934310131483046022100E96C5D2BEEFCB24F55F64D04D76CE7195DBCE922164FAB2A30A80B8E88EE157602210087417B2D0919CF4F7ED6B727CDB5DAA2849E023BE89440B65F8CDA3C46A2CD97045AA4486A373035021900F6FE020E25E511BCE109720BD84963D9B819E24AAD91C12102182C59868123F42B1E68A15281479BA8037E09DAC7BB272F2200617B22706F735F6964223A223137353130363435222C2274797065223A2253494E474C45222C227375626A656374223A2230363435222C227265636F72645F6964223A22303634355F31373531303634355F3230313830333131303530343433227D00045AA4486B0010D2A54278A724311BFD83D2D38719C59C
     */
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

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDriverCardNo() {
        return driverCardNo;
    }

    public void setDriverCardNo(String driverCardNo) {
        this.driverCardNo = driverCardNo;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCarryCode() {
        return carryCode;
    }

    public void setCarryCode(String carryCode) {
        this.carryCode = carryCode;
    }

    public String getBusGroupCode() {
        return busGroupCode;
    }

    public void setBusGroupCode(String busGroupCode) {
        this.busGroupCode = busGroupCode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getSellerSignTime() {
        return sellerSignTime;
    }

    public void setSellerSignTime(String sellerSignTime) {
        this.sellerSignTime = sellerSignTime;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getDriverSignTime() {
        return driverSignTime;
    }

    public void setDriverSignTime(String driverSignTime) {
        this.driverSignTime = driverSignTime;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getActualOrderTime() {
        return actualOrderTime;
    }

    public void setActualOrderTime(String actualOrderTime) {
        this.actualOrderTime = actualOrderTime;
    }

    public String getCardData() {
        return cardData;
    }

    public void setCardData(String cardData) {
        this.cardData = cardData;
    }

    public String getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(String actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }
}
