package com.spd.alipay.been;

import java.util.List;

public class AlipayUploadBeen {


    /**
     * recordType : QR
     * record : [{"acquirer":"6410001","bizType":"09","transSeqId":"sh001_20160514140218_000033","terminalId":"100000000057","driverId":"0000001111111111","transTime":"20181112135621","transCityCode":"6410","lineId":"0","station":"000010","currency":"156","chargeType":0,"totalFee":1,"payFee":1,"userId":"2088112186820566","vehicleType":"BUS","cardType":"T0460100","cardId":"2088112186820566","qrCode":"02010057323038383131323138363832303536365bf26843025807d00000008200000000000000000303edf854b5ccdc4032b1cea1b410da039b58099307338f2754303436303130301032303838313132313836383230353636004630440220133933e9a1a4e299ed4fbee04d6acd531f2a39f28f32ad18b9973ecb6a71d86302205af066799a5e5e39703833ccbed2c0cc525c42b088efbeac6e1b3358ddea92d4045be92dc3383036021900e4080a784700fda33e1a3019ad339e202f175f5be480f5ca021900f977195d70f649eb2de6496b5ed0fabb6a1293fdf122a191","record":"0000000100000100E002010057323038383131323138363832303536365BF26843025807D00000008200000000000000000303EDF854B5CCDC4032B1CEA1B410DA039B58099307338F2754303436303130301032303838313132313836383230353636004630440220133933E9A1A4E299ED4FBEE04D6ACD531F2A39F28F32AD18B9973ECB6A71D86302205AF066799A5E5E39703833CCBED2C0CC525C42B088EFBEAC6E1B3358DDEA92D4045BE92DC3383036021900E4080A784700FDA33E1A3019AD339E202F175F5BE480F5CA021900F977195D70F649EB2DE6496B5ED0FABB6A1293FDF122A1910002000910EF6AB790B16FB71000030011B8AC77B7F82683B728425D7996B4D3F31800040011B8AC77B7F82683B728425D7996B4D3F3180005001140B1ECB640B1ECB6B8730D1BEFD36450670006000910EF6AB790B16FB71000070001000008000910EF6AB790B16FB710000900020001000A000910EF6AB790B16FB710000B000910EF6AB790B16FB710000C000910EF6AB790B16FB710000D000910EF6AB790B16FB710000E000910EF6AB790B16FB710000F000910EF6AB790B16FB710001000045BE92DC60012000910EF6AB790B16FB71000110010D452A30298C8104CDFDA5CD5FCCB833C"}]*/

    private String recordType;
    private List<RecordBean> record;

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public List<RecordBean> getRecord() {
        return record;
    }

    public void setRecord(List<RecordBean> record) {
        this.record = record;
    }

    public static class RecordBean {
        /**
         * acquirer : 6410001
         * bizType : 09
         * transSeqId : sh001_20160514140218_000033
         * terminalId : 100000000057
         * driverId : 0000001111111111
         * transTime : 20181112135621
         * transCityCode : 6410
         * lineId : 0
         * station : 000010
         * currency : 156
         * chargeType : 0
         * totalFee : 1
         * payFee : 1
         * userId : 2088112186820566
         * vehicleType : BUS
         * cardType : T0460100
         * cardId : 2088112186820566
         * qrCode : 02010057323038383131323138363832303536365bf26843025807d00000008200000000000000000303edf854b5ccdc4032b1cea1b410da039b58099307338f2754303436303130301032303838313132313836383230353636004630440220133933e9a1a4e299ed4fbee04d6acd531f2a39f28f32ad18b9973ecb6a71d86302205af066799a5e5e39703833ccbed2c0cc525c42b088efbeac6e1b3358ddea92d4045be92dc3383036021900e4080a784700fda33e1a3019ad339e202f175f5be480f5ca021900f977195d70f649eb2de6496b5ed0fabb6a1293fdf122a191
         * record : 0000000100000100E002010057323038383131323138363832303536365BF26843025807D00000008200000000000000000303EDF854B5CCDC4032B1CEA1B410DA039B58099307338F2754303436303130301032303838313132313836383230353636004630440220133933E9A1A4E299ED4FBEE04D6ACD531F2A39F28F32AD18B9973ECB6A71D86302205AF066799A5E5E39703833CCBED2C0CC525C42B088EFBEAC6E1B3358DDEA92D4045BE92DC3383036021900E4080A784700FDA33E1A3019AD339E202F175F5BE480F5CA021900F977195D70F649EB2DE6496B5ED0FABB6A1293FDF122A1910002000910EF6AB790B16FB71000030011B8AC77B7F82683B728425D7996B4D3F31800040011B8AC77B7F82683B728425D7996B4D3F3180005001140B1ECB640B1ECB6B8730D1BEFD36450670006000910EF6AB790B16FB71000070001000008000910EF6AB790B16FB710000900020001000A000910EF6AB790B16FB710000B000910EF6AB790B16FB710000C000910EF6AB790B16FB710000D000910EF6AB790B16FB710000E000910EF6AB790B16FB710000F000910EF6AB790B16FB710001000045BE92DC60012000910EF6AB790B16FB71000110010D452A30298C8104CDFDA5CD5FCCB833C
         */

        private String acquirer;
        private String bizType;
        private String transSeqId;
        private String terminalId;
        private String driverId;
        private String transTime;
        private String transCityCode;
        private String lineId;
        private String station;
        private String currency;
        private int chargeType;
        private int totalFee;
        private int payFee;
        private String userId;
        private String vehicleType;
        private String cardType;
        private String cardId;
        private String qrCode;
        private String record;

        public String getAcquirer() {
            return acquirer;
        }

        public void setAcquirer(String acquirer) {
            this.acquirer = acquirer;
        }

        public String getBizType() {
            return bizType;
        }

        public void setBizType(String bizType) {
            this.bizType = bizType;
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

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }

        public String getRecord() {
            return record;
        }

        public void setRecord(String record) {
            this.record = record;
        }
    }
}
