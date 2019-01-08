package com.spd.base.beenupload;

import java.util.List;

public class AlipayQrCodeUpload {
    private List<AlipayQrCodeUpload.DataBean> data;

    public List<AlipayQrCodeUpload.DataBean> getData() {
        return data;
    }

    public void setData(List<AlipayQrCodeUpload.DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String recordType;
        private AlipayQrCodeUpload.DataBean.RecordBean record;

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
            //    	string（2）	“09”
            private String bizType;
            //    	string（7）	交易收单方标识，由公交平台定义，同步给机具前置侧（设备）
            private String acquirer;
            //    	string（10）	交易流水号,由设备前置定义,不可重复
            private String transSeqId;
            //    	string（12）	机具设备终端编号
            private String terminalId;
            //    	string（20）	司机卡Id
            private String driverId;
            //    	string（20）	售票员Id
            private String conductorId;
            //    	string（14）	站点终端刷卡时间yyyyMMddHHMMss
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
            //    	string（n）	传入用户的支付宝user_id
            private String userId;
            //    	string（n）	事件发生lbs，经纬度使用英文逗号分隔
            private String appPosition;
            //    	string（3）	交通工具类型（公交固定取值：BUS）
            private String vehicleType;
            //    	string（n）	以码信息card_type为准
            private String cardType;
            //    	string（n）	卡票号，来自二维码card_no
            private String cardId;
            //    	string（n）	刷卡记录数据，对应刷码后SDK返回的凭证
            private String record;
            //    	string（n）	优惠类型
            private String discountType;
            //    	string（n）	优惠描述
            private String discountDesc;
            //    	hexstr（n）	二维码原始信息
            private String qrCode;

            public RecordBean(String bizType, String acquirer, String transSeqId, String terminalId, String driverId, String conductorId, String transTime, String transCityCode, String lineId, String station, String currency, int chargeType, int totalFee, int payFee, String userId, String appPosition, String vehicleType, String cardType, String cardId, String record, String discountType, String discountDesc, String qrCode) {
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
                this.userId = userId;
                this.appPosition = appPosition;
                this.vehicleType = vehicleType;
                this.cardType = cardType;
                this.cardId = cardId;
                this.record = record;
                this.discountType = discountType;
                this.discountDesc = discountDesc;
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

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getAppPosition() {
                return appPosition;
            }

            public void setAppPosition(String appPosition) {
                this.appPosition = appPosition;
            }

            public String getVehicleType() {
                return vehicleType;
            }

            public void setVehicleType(String vehicleType) {
                this.vehicleType = vehicleType;
            }

            public String getCardType() {
                return cardType;
            }

            public void setCardType(String cardType) {
                this.cardType = cardType;
            }

            public String getCardId() {
                return cardId;
            }

            public void setCardId(String cardId) {
                this.cardId = cardId;
            }

            public String getRecord() {
                return record;
            }

            public void setRecord(String record) {
                this.record = record;
            }

            public String getDiscountType() {
                return discountType;
            }

            public void setDiscountType(String discountType) {
                this.discountType = discountType;
            }

            public String getDiscountDesc() {
                return discountDesc;
            }

            public void setDiscountDesc(String discountDesc) {
                this.discountDesc = discountDesc;
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
