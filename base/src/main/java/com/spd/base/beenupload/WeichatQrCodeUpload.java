package com.spd.base.beenupload;

import java.util.List;

public class WeichatQrCodeUpload {

    private List<WeichatQrCodeUpload.DataBean> data;

    public List<WeichatQrCodeUpload.DataBean> getData() {
        return data;
    }

    public void setData(List<WeichatQrCodeUpload.DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String recordType;
        private WeichatQrCodeUpload.DataBean.RecordBean record;

        public String getRecordType() {
            return recordType;
        }

        public void setRecordType(String recordType) {
            this.recordType = recordType;
        }

        public WeichatQrCodeUpload.DataBean.RecordBean getRecord() {
            return record;
        }

        public void setRecord(WeichatQrCodeUpload.DataBean.RecordBean record) {
            this.record = record;
        }

        public static class RecordBean {
            //            	string（2）	“08”
            private String bizType;
            //            	string（7）	交易收单方标识
            private String acquirer;
            //            	string（10）	交易流水号,不可重复
            private String transSeqId;
            //            	string（12）	机具设备终端编号
            private String terminalId;
            //            	string（20）	司机卡Id(左补零)
            private String driverId;
            //            	string（20）	售票员Id（左补零）
            private String conductorId;
            //            	string（14）	交易时间，格式为yyyyMMddHHmmss
            private String transTime;
            //            	string（4）	交易发生地城市代码，遵循银联的城市代码。来自pos的个人化数据
            private String transCityCode;
            //            	string（4）	线路Id，来自公交平台下推，并不一定是真正的线路号
            private String lineId;
            //            	string（2）	站点编号
            private String station;
            //            	string（3）	币种，156为人民币
            private String currency;
            //            	unsignedint	计费类型：0-一次性扫码计费，1-分段计费
            private int chargeType;
            //            	unsignedint	交易应收金额，单位为分
            private int totalFee;
            //            	unsignedint	交易实收金额，单位为分
            private int payFee;
            //            	string（16）	腾讯乘车码用户
            private String userId;
            //            	hexstr（n）	交易记录
            private String record;
            //            	hexstr（n）	二维码原始信息
            private String qrCode;

            public RecordBean(String bizType, String acquirer, String transSeqId, String terminalId, String driverId, String conductorId, String transTime, String transCityCode, String lineId, String station, String currency, int chargeType, int totalFee, int payFee, String userId, String record, String qrCode) {
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
                this.record = record;
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

            public String getRecord() {
                return record;
            }

            public void setRecord(String record) {
                this.record = record;
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
