package com.spd.base.been.tianjin.produce.shuangmian;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 张明_ on 2019/4/17.
 * Email 741183142@qq.com
 */
@Entity
public class UploadSMDB {

//    {
//        "type":"2200UC",
//            "posId":"123",
//            "route":"11",
//            "data":
//            “{
//                  "payinfo": [
//                      {
//                          "busNo": "011491",//车辆自编号
//                          "cardSerialNum": "00",//卡序号
//                          "batchNumber": "000000",//批次号
//                          "responseCode": "",//请款应答码
//                          "isPay": "0",//是否支付 1：已支付 0： 未支付
//                          "driver": "00003000000151653565",//司机卡号
//                          "transactionTime": "20180823083032",//交易时间
//                          "towTrackData": "6235290300010078383d251122000000725",//二磁道数据
//                          "terminalCode": "17380059",//终端编号
//                          "serialNumber": "000001",//序号
//                          "transactionAmount": "000000000200",//交易金额
//                          "team": "0015",//路队
//                          "threeTrackData": "",//三磁道数据
//                          "route": "1608",//线路
//                          "posId": "17380059",//机具号
//                          "retrievingNum": "",//追踪码
//                          "dept": "01",//公司
//                          "field": "9f3303e0f9c8950500000000009f1e0831373338303035399f101607020103a00000010d0701000000000080b04dda61f19f26081dfe827afdd7e2139f3602000182027c009c01009f1a0201569a031808239f02060000000002005f2a0201569f03060000000000009f3501229f34035e03009f37043c96df7e9f2701809f410400000001",//55域
//                          "transactionCode": "000001",//交易序号
//                          "type": "04",//类型 03：云闪付成功 04： 云闪付失败，转ODA
//                          "cardNo": "6235290300010078383"//卡号
//                      }
//                  ]
//    }”
//    }

    private String busNo;
    private String cardSerialNum;
    private String batchNumber;
    private String responseCode;
    private String isPay;
    private String driver;

    @Id(autoincrement = false)
    private String transactionTime;

    private String towTrackData;
    private String terminalCode;
    private String serialNumber;
    private String transactionAmount;
    private String team;
    private String threeTrackData;
    private String route;
    private String posId;
    private String retrievingNum;
    private String dept;
    private String field;
    private String transactionCode;
    private String type;
    private String cardNo;
    //是否上传
    private boolean isUpload;
    @Generated(hash = 1338108654)
    public UploadSMDB(String busNo, String cardSerialNum, String batchNumber, String responseCode, String isPay, String driver, String transactionTime, String towTrackData, String terminalCode, String serialNumber, String transactionAmount, String team, String threeTrackData, String route, String posId,
            String retrievingNum, String dept, String field, String transactionCode, String type, String cardNo, boolean isUpload) {
        this.busNo = busNo;
        this.cardSerialNum = cardSerialNum;
        this.batchNumber = batchNumber;
        this.responseCode = responseCode;
        this.isPay = isPay;
        this.driver = driver;
        this.transactionTime = transactionTime;
        this.towTrackData = towTrackData;
        this.terminalCode = terminalCode;
        this.serialNumber = serialNumber;
        this.transactionAmount = transactionAmount;
        this.team = team;
        this.threeTrackData = threeTrackData;
        this.route = route;
        this.posId = posId;
        this.retrievingNum = retrievingNum;
        this.dept = dept;
        this.field = field;
        this.transactionCode = transactionCode;
        this.type = type;
        this.cardNo = cardNo;
        this.isUpload = isUpload;
    }
    @Generated(hash = 983680894)
    public UploadSMDB() {
    }
    public String getBusNo() {
        return this.busNo;
    }
    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }
    public String getCardSerialNum() {
        return this.cardSerialNum;
    }
    public void setCardSerialNum(String cardSerialNum) {
        this.cardSerialNum = cardSerialNum;
    }
    public String getBatchNumber() {
        return this.batchNumber;
    }
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
    public String getResponseCode() {
        return this.responseCode;
    }
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }
    public String getIsPay() {
        return this.isPay;
    }
    public void setIsPay(String isPay) {
        this.isPay = isPay;
    }
    public String getDriver() {
        return this.driver;
    }
    public void setDriver(String driver) {
        this.driver = driver;
    }
    public String getTransactionTime() {
        return this.transactionTime;
    }
    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }
    public String getTowTrackData() {
        return this.towTrackData;
    }
    public void setTowTrackData(String towTrackData) {
        this.towTrackData = towTrackData;
    }
    public String getTerminalCode() {
        return this.terminalCode;
    }
    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }
    public String getSerialNumber() {
        return this.serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public String getTransactionAmount() {
        return this.transactionAmount;
    }
    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
    public String getTeam() {
        return this.team;
    }
    public void setTeam(String team) {
        this.team = team;
    }
    public String getThreeTrackData() {
        return this.threeTrackData;
    }
    public void setThreeTrackData(String threeTrackData) {
        this.threeTrackData = threeTrackData;
    }
    public String getRoute() {
        return this.route;
    }
    public void setRoute(String route) {
        this.route = route;
    }
    public String getPosId() {
        return this.posId;
    }
    public void setPosId(String posId) {
        this.posId = posId;
    }
    public String getRetrievingNum() {
        return this.retrievingNum;
    }
    public void setRetrievingNum(String retrievingNum) {
        this.retrievingNum = retrievingNum;
    }
    public String getDept() {
        return this.dept;
    }
    public void setDept(String dept) {
        this.dept = dept;
    }
    public String getField() {
        return this.field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public String getTransactionCode() {
        return this.transactionCode;
    }
    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getCardNo() {
        return this.cardNo;
    }
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
    public boolean getIsUpload() {
        return this.isUpload;
    }
    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }

}
