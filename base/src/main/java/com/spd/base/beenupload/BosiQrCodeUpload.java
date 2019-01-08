package com.spd.base.beenupload;

import java.util.List;

/**
 * 博思二维码 上传信息
 */
public class BosiQrCodeUpload {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }


    public static class DataBean {
        private String recordType;
        private RecordBean record;

        public String getRecordType() {
            return recordType;
        }

        public void setRecordType(String recordType) {
            this.recordType = recordType;
        }

        public RecordBean getRecord() {
            return record;
        }

        public void setRecord(RecordBean record) {
            this.record = record;
        }

        public static class RecordBean {
            //  string（2）	“03”
            private String bizType;
            // acquirer	string（7）	交易收单方标识，由公交平台定义，同步给机具前置侧（设备）
            private String acquirer;
            //    	string（10）	交易流水号,由设备前置定义,不可重复
            private String transSeqId;
            //    	string（12）	机具设备终端编号
            private String terminalId;
            //    	string（20）	司机卡Id
            private String driverId;
            //    	string（20）	售票员Id
            private String conductorId;
            //    	string（14）	交易时间，格式为yyyyMMddHHmmss
            private String transTime;
            //    	string（4）	交易发生地城市代码，遵循银联的城市代码。来自pos的个人化数据
            private String transCityCode;
            //    	string（4）	线路Id，来自公交平台下推，并不一定是真正的线路号
            private String lineId;
            //    	string（2）	站点编号
            private String station;
            //    	string（3）	币种，156为人民币
            private String currency;
            //    	unsignedint	计费类型：0-一次性扫码计费，1-分段计费
            private int chargeType;
            //    	unsignedint	交易应收金额，单位为分
            private int totalFee;
            //    	unsignedint	交易实收金额，单位为分
            private int payFee;
            //    	string（2）	二维码版本号
            private String qrCodeVersion;
            //    	string（2）	二维码类型
            private String qrCodeType;
            //    	string（8）	二维码的发码方编码
            private String qrCodeIssuer;
            //    	string（5）	渠道Id
            private String channelId;
            //    	string（8）	卡发行机构编码，如03081910、20206410
            private String cardIssuer;
            //    	string（20）	虚拟卡交通卡Id
            private String cardId;
            //    	string（2）	卡类型，如0C
            private String cardType;
            //    	string（16）	交易位置信息，由经纬度BCD码组成
            private String appPosition;
            //    	string（2）	支付类型，渠道自定义
            private String paymentType;
            //    	string（2）	支付模式：00-账户余额付，01-信用付
            private String paymentMode;
            //    	unsignedint	卡余额，单位为分
            private int cardBalance;
            //    	string（60）	渠道扩展数据
            private String channelExtends;
            //    	hexstr（n）	二维码原始信息
            private String qrCode;

            public RecordBean(String bizType, String acquirer, String transSeqId, String terminalId, String driverId, String conductorId, String transTime, String transCityCode, String lineId, String station, String currency, int chargeType, int totalFee, int payFee, String qrCodeVersion, String qrCodeType, String qrCodeIssuer, String channelId, String cardIssuer, String cardId, String cardType, String appPosition, String paymentType, String paymentMode, int cardBalance, String channelExtends, String qrCode) {
                this.bizType = bizType;
                this.acquirer = acquirer;
                this.transSeqId = transSeqId;
                this.terminalId = terminalId;
                this.driverId = driverId;
                this.conductorId = conductorId;
                this.transTime = transTime;
                this.transCityCode = transCityCode;
                this.lineId = lineId;
                this.station = station;
                this.currency = currency;
                this.chargeType = chargeType;
                this.totalFee = totalFee;
                this.payFee = payFee;
                this.qrCodeVersion = qrCodeVersion;
                this.qrCodeType = qrCodeType;
                this.qrCodeIssuer = qrCodeIssuer;
                this.channelId = channelId;
                this.cardIssuer = cardIssuer;
                this.cardId = cardId;
                this.cardType = cardType;
                this.appPosition = appPosition;
                this.paymentType = paymentType;
                this.paymentMode = paymentMode;
                this.cardBalance = cardBalance;
                this.channelExtends = channelExtends;
                this.qrCode = qrCode;
            }

            public String getBizType() {
                return bizType;
            }

            public void setBizType(String bizType) {
                this.bizType = bizType;
            }

            public String getAcquirer() {
                return acquirer;
            }

            public void setAcquirer(String acquirer) {
                this.acquirer = acquirer;
            }

            public String getTransSeqId() {
                return transSeqId;
            }

            public void setTransSeqId(String transSeqId) {
                this.transSeqId = transSeqId;
            }

            public String getTerminalId() {
                return terminalId;
            }

            public void setTerminalId(String terminalId) {
                this.terminalId = terminalId;
            }

            public String getDriverId() {
                return driverId;
            }

            public void setDriverId(String driverId) {
                this.driverId = driverId;
            }

            public String getConductorId() {
                return conductorId;
            }

            public void setConductorId(String conductorId) {
                this.conductorId = conductorId;
            }

            public String getTransTime() {
                return transTime;
            }

            public void setTransTime(String transTime) {
                this.transTime = transTime;
            }

            public String getTransCityCode() {
                return transCityCode;
            }

            public void setTransCityCode(String transCityCode) {
                this.transCityCode = transCityCode;
            }

            public String getLineId() {
                return lineId;
            }

            public void setLineId(String lineId) {
                this.lineId = lineId;
            }

            public String getStation() {
                return station;
            }

            public void setStation(String station) {
                this.station = station;
            }

            public String getCurrency() {
                return currency;
            }

            public void setCurrency(String currency) {
                this.currency = currency;
            }

            public int getChargeType() {
                return chargeType;
            }

            public void setChargeType(int chargeType) {
                this.chargeType = chargeType;
            }

            public int getTotalFee() {
                return totalFee;
            }

            public void setTotalFee(int totalFee) {
                this.totalFee = totalFee;
            }

            public int getPayFee() {
                return payFee;
            }

            public void setPayFee(int payFee) {
                this.payFee = payFee;
            }

            public String getQrCodeVersion() {
                return qrCodeVersion;
            }

            public void setQrCodeVersion(String qrCodeVersion) {
                this.qrCodeVersion = qrCodeVersion;
            }

            public String getQrCodeType() {
                return qrCodeType;
            }

            public void setQrCodeType(String qrCodeType) {
                this.qrCodeType = qrCodeType;
            }

            public String getQrCodeIssuer() {
                return qrCodeIssuer;
            }

            public void setQrCodeIssuer(String qrCodeIssuer) {
                this.qrCodeIssuer = qrCodeIssuer;
            }

            public String getChannelId() {
                return channelId;
            }

            public void setChannelId(String channelId) {
                this.channelId = channelId;
            }

            public String getCardIssuer() {
                return cardIssuer;
            }

            public void setCardIssuer(String cardIssuer) {
                this.cardIssuer = cardIssuer;
            }

            public String getCardId() {
                return cardId;
            }

            public void setCardId(String cardId) {
                this.cardId = cardId;
            }

            public String getCardType() {
                return cardType;
            }

            public void setCardType(String cardType) {
                this.cardType = cardType;
            }

            public String getAppPosition() {
                return appPosition;
            }

            public void setAppPosition(String appPosition) {
                this.appPosition = appPosition;
            }

            public String getPaymentType() {
                return paymentType;
            }

            public void setPaymentType(String paymentType) {
                this.paymentType = paymentType;
            }

            public String getPaymentMode() {
                return paymentMode;
            }

            public void setPaymentMode(String paymentMode) {
                this.paymentMode = paymentMode;
            }

            public int getCardBalance() {
                return cardBalance;
            }

            public void setCardBalance(int cardBalance) {
                this.cardBalance = cardBalance;
            }

            public String getChannelExtends() {
                return channelExtends;
            }

            public void setChannelExtends(String channelExtends) {
                this.channelExtends = channelExtends;
            }

            public String getQrCode() {
                return qrCode;
            }

            public void setQrCode(String qrCode) {
                this.qrCode = qrCode;
            }
        }
    }
}
