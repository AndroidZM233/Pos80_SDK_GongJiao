package com.spd.base.been.tianjin;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 交易记录数据库
 * Created by 张明_ on 2019/2/20.
 * Email 741183142@qq.com
 */
@Entity
public class Save128CodeBean {
    //记录号
    @Id(autoincrement = false)
    private byte[] id = new byte[2];

    //预留
    private byte[] reserved1 = new byte[1];
    //记录类型
    private byte[] recordType = new byte[1];
    //预留
    private byte[] reserved2 = new byte[1];
    //卡种
    private byte[] cards = new byte[1];
    //卡主信息结构体
    private byte[] cardStructure = new byte[14];
    //卡主类型
    private byte[] cardMainType = new byte[1];
    //卡子类型
    private byte[] cardSonType = new byte[1];
    //交易时间
    private byte[] tradingHour = new byte[4];
    //交易类型标识
    private byte[] transactionTypeFlag = new byte[1];
    //交易实际金额
    private byte[] actualAmount = new byte[3];
    //交易应收金额
    private byte[] transactionReceivable = new byte[3];
    //交易虚拟金额
    private byte[] transactionVirtualAmount = new byte[3];
    //交易原额
    private byte[] originalAmount = new byte[3];
    //消费序号
    private byte[] consumptionNum = new byte[2];
    //TAC码
    private byte[] TAC = new byte[4];
    //消费密钥版本号
    private byte[] cKeyVersion = new byte[1];
    //消费密钥索引
    private byte[] cKeyIndex = new byte[1];
    //PSAM卡终端机编号
    private byte[] psamTerminalNum = new byte[6];
    //PSAM卡终端交易序号
    private byte[] psamTransactionNum = new byte[4];
    //PSAM卡卡号
    private byte[] psamCardNum = new byte[10];
    //个性化数据
    private byte[] personalizedData = new byte[26];
    //公司代码
    private byte[] companyCode = new byte[4];
    //路队线路代码
    private byte[] routeCode = new byte[4];
    //车辆代码
    private byte[] vehicleCode = new byte[4];
    //卡机设备号
    private byte[] cardDeviceNum = new byte[4];
    //司机卡号
    private byte[] driveNum = new byte[10];
    //换乘标志
    private byte[] changeCarLogo = new byte[1];
    //上下行标识
    private byte[] upDownLogo = new byte[1];
    //上车站点
    private byte[] onSite = new byte[1];
    //上车时间
    private byte[] onTime = new byte[4];
    //下车站点
    private byte[] offSite = new byte[1];
    //校验码
    private byte[] checkCode = new byte[1];
    //是否上传
    private boolean isUpload = false;
    @Generated(hash = 156631897)
    public Save128CodeBean(byte[] id, byte[] reserved1, byte[] recordType,
            byte[] reserved2, byte[] cards, byte[] cardStructure,
            byte[] cardMainType, byte[] cardSonType, byte[] tradingHour,
            byte[] transactionTypeFlag, byte[] actualAmount,
            byte[] transactionReceivable, byte[] transactionVirtualAmount,
            byte[] originalAmount, byte[] consumptionNum, byte[] TAC,
            byte[] cKeyVersion, byte[] cKeyIndex, byte[] psamTerminalNum,
            byte[] psamTransactionNum, byte[] psamCardNum, byte[] personalizedData,
            byte[] companyCode, byte[] routeCode, byte[] vehicleCode,
            byte[] cardDeviceNum, byte[] driveNum, byte[] changeCarLogo,
            byte[] upDownLogo, byte[] onSite, byte[] onTime, byte[] offSite,
            byte[] checkCode, boolean isUpload) {
        this.id = id;
        this.reserved1 = reserved1;
        this.recordType = recordType;
        this.reserved2 = reserved2;
        this.cards = cards;
        this.cardStructure = cardStructure;
        this.cardMainType = cardMainType;
        this.cardSonType = cardSonType;
        this.tradingHour = tradingHour;
        this.transactionTypeFlag = transactionTypeFlag;
        this.actualAmount = actualAmount;
        this.transactionReceivable = transactionReceivable;
        this.transactionVirtualAmount = transactionVirtualAmount;
        this.originalAmount = originalAmount;
        this.consumptionNum = consumptionNum;
        this.TAC = TAC;
        this.cKeyVersion = cKeyVersion;
        this.cKeyIndex = cKeyIndex;
        this.psamTerminalNum = psamTerminalNum;
        this.psamTransactionNum = psamTransactionNum;
        this.psamCardNum = psamCardNum;
        this.personalizedData = personalizedData;
        this.companyCode = companyCode;
        this.routeCode = routeCode;
        this.vehicleCode = vehicleCode;
        this.cardDeviceNum = cardDeviceNum;
        this.driveNum = driveNum;
        this.changeCarLogo = changeCarLogo;
        this.upDownLogo = upDownLogo;
        this.onSite = onSite;
        this.onTime = onTime;
        this.offSite = offSite;
        this.checkCode = checkCode;
        this.isUpload = isUpload;
    }
    @Generated(hash = 1297223391)
    public Save128CodeBean() {
    }
    public byte[] getId() {
        return this.id;
    }
    public void setId(byte[] id) {
        this.id = id;
    }
    public byte[] getReserved1() {
        return this.reserved1;
    }
    public void setReserved1(byte[] reserved1) {
        this.reserved1 = reserved1;
    }
    public byte[] getRecordType() {
        return this.recordType;
    }
    public void setRecordType(byte[] recordType) {
        this.recordType = recordType;
    }
    public byte[] getReserved2() {
        return this.reserved2;
    }
    public void setReserved2(byte[] reserved2) {
        this.reserved2 = reserved2;
    }
    public byte[] getCards() {
        return this.cards;
    }
    public void setCards(byte[] cards) {
        this.cards = cards;
    }
    public byte[] getCardStructure() {
        return this.cardStructure;
    }
    public void setCardStructure(byte[] cardStructure) {
        this.cardStructure = cardStructure;
    }
    public byte[] getCardMainType() {
        return this.cardMainType;
    }
    public void setCardMainType(byte[] cardMainType) {
        this.cardMainType = cardMainType;
    }
    public byte[] getCardSonType() {
        return this.cardSonType;
    }
    public void setCardSonType(byte[] cardSonType) {
        this.cardSonType = cardSonType;
    }
    public byte[] getTradingHour() {
        return this.tradingHour;
    }
    public void setTradingHour(byte[] tradingHour) {
        this.tradingHour = tradingHour;
    }
    public byte[] getTransactionTypeFlag() {
        return this.transactionTypeFlag;
    }
    public void setTransactionTypeFlag(byte[] transactionTypeFlag) {
        this.transactionTypeFlag = transactionTypeFlag;
    }
    public byte[] getActualAmount() {
        return this.actualAmount;
    }
    public void setActualAmount(byte[] actualAmount) {
        this.actualAmount = actualAmount;
    }
    public byte[] getTransactionReceivable() {
        return this.transactionReceivable;
    }
    public void setTransactionReceivable(byte[] transactionReceivable) {
        this.transactionReceivable = transactionReceivable;
    }
    public byte[] getTransactionVirtualAmount() {
        return this.transactionVirtualAmount;
    }
    public void setTransactionVirtualAmount(byte[] transactionVirtualAmount) {
        this.transactionVirtualAmount = transactionVirtualAmount;
    }
    public byte[] getOriginalAmount() {
        return this.originalAmount;
    }
    public void setOriginalAmount(byte[] originalAmount) {
        this.originalAmount = originalAmount;
    }
    public byte[] getConsumptionNum() {
        return this.consumptionNum;
    }
    public void setConsumptionNum(byte[] consumptionNum) {
        this.consumptionNum = consumptionNum;
    }
    public byte[] getTAC() {
        return this.TAC;
    }
    public void setTAC(byte[] TAC) {
        this.TAC = TAC;
    }
    public byte[] getCKeyVersion() {
        return this.cKeyVersion;
    }
    public void setCKeyVersion(byte[] cKeyVersion) {
        this.cKeyVersion = cKeyVersion;
    }
    public byte[] getCKeyIndex() {
        return this.cKeyIndex;
    }
    public void setCKeyIndex(byte[] cKeyIndex) {
        this.cKeyIndex = cKeyIndex;
    }
    public byte[] getPsamTerminalNum() {
        return this.psamTerminalNum;
    }
    public void setPsamTerminalNum(byte[] psamTerminalNum) {
        this.psamTerminalNum = psamTerminalNum;
    }
    public byte[] getPsamTransactionNum() {
        return this.psamTransactionNum;
    }
    public void setPsamTransactionNum(byte[] psamTransactionNum) {
        this.psamTransactionNum = psamTransactionNum;
    }
    public byte[] getPsamCardNum() {
        return this.psamCardNum;
    }
    public void setPsamCardNum(byte[] psamCardNum) {
        this.psamCardNum = psamCardNum;
    }
    public byte[] getPersonalizedData() {
        return this.personalizedData;
    }
    public void setPersonalizedData(byte[] personalizedData) {
        this.personalizedData = personalizedData;
    }
    public byte[] getCompanyCode() {
        return this.companyCode;
    }
    public void setCompanyCode(byte[] companyCode) {
        this.companyCode = companyCode;
    }
    public byte[] getRouteCode() {
        return this.routeCode;
    }
    public void setRouteCode(byte[] routeCode) {
        this.routeCode = routeCode;
    }
    public byte[] getVehicleCode() {
        return this.vehicleCode;
    }
    public void setVehicleCode(byte[] vehicleCode) {
        this.vehicleCode = vehicleCode;
    }
    public byte[] getCardDeviceNum() {
        return this.cardDeviceNum;
    }
    public void setCardDeviceNum(byte[] cardDeviceNum) {
        this.cardDeviceNum = cardDeviceNum;
    }
    public byte[] getDriveNum() {
        return this.driveNum;
    }
    public void setDriveNum(byte[] driveNum) {
        this.driveNum = driveNum;
    }
    public byte[] getChangeCarLogo() {
        return this.changeCarLogo;
    }
    public void setChangeCarLogo(byte[] changeCarLogo) {
        this.changeCarLogo = changeCarLogo;
    }
    public byte[] getUpDownLogo() {
        return this.upDownLogo;
    }
    public void setUpDownLogo(byte[] upDownLogo) {
        this.upDownLogo = upDownLogo;
    }
    public byte[] getOnSite() {
        return this.onSite;
    }
    public void setOnSite(byte[] onSite) {
        this.onSite = onSite;
    }
    public byte[] getOnTime() {
        return this.onTime;
    }
    public void setOnTime(byte[] onTime) {
        this.onTime = onTime;
    }
    public byte[] getOffSite() {
        return this.offSite;
    }
    public void setOffSite(byte[] offSite) {
        this.offSite = offSite;
    }
    public byte[] getCheckCode() {
        return this.checkCode;
    }
    public void setCheckCode(byte[] checkCode) {
        this.checkCode = checkCode;
    }
    public boolean getIsUpload() {
        return this.isUpload;
    }
    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }
}
