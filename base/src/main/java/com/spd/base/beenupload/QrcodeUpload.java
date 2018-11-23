package com.spd.base.beenupload;

import java.util.List;

public class QrcodeUpload {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * recordType : QR
         * record : {"bizType":"09","transTime":"20181113170129","transCityCode":"6410","chargeType":0,"cardType":"T0460100","lineId":"0","terminalId":"100000000057","acquirer":"6410001","transSeqId":"sh001_20160514140218_000321","userId":"2088112186820566","payFee":1,"driverId":"0000001111111111","qrCode":"02010057323038383131323138363832303536365bf3cd28025807d0000000820000000000000000028ba21811db38299e2ef2fc98657b2f1683b4ba8abc6f8974543034363031303010323038383131323138363832303536360048304602210083ffefb3a2f44f413bbc7cd66121bba3072d6dc343ab58487a45055c45a53037022100e38e8c5b266600bf633488eaf34a48b472057e20ddf150e2562ac03ade6276c1045bea92e4383036021900a14b19a67728a524bdec21656bfc19258252a0dbcf2b8ea9021900b689d33ff157d4e6ce680e280fdfa30e3634724ff1fb7222","totalFee":1,"cardId":"2088112186820566","record":"0000000100000100E202010057323038383131323138363832303536365BF3CD28025807D0000000820000000000000000028BA21811DB38299E2EF2FC98657B2F1683B4BA8ABC6F8974543034363031303010323038383131323138363832303536360048304602210083FFEFB3A2F44F413BBC7CD66121BBA3072D6DC343AB58487A45055C45A53037022100E38E8C5B266600BF633488EAF34A48B472057E20DDF150E2562AC03ADE6276C1045BEA92E4383036021900A14B19A67728A524BDEC21656BFC19258252A0DBCF2B8EA9021900B689D33FF157D4E6CE680E280FDFA30E3634724FF1FB722200020004F81534B8000300113811EBB648743DB898AABDD1E6FC132B18000400113811EBB648743DB898AABDD1E6FC132B180005000860DF28B860DF28B800060004F81534B8000700010000080004F81534B8000900020001000A0004F81534B8000B0004F81534B8000C0004F81534B8000D0004F81534B8000E0004F81534B8000F0004F81534B8001000045BEA92E900120004F81534B800110010B7605D245AC7F7A252E673A253381207","station":"000010","currency":"156","vehicleType":"BUS"}
         */

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
            /**
             * bizType : 09
             * transTime : 20181113170129
             * transCityCode : 6410
             * chargeType : 0
             * cardType : T0460100
             * lineId : 0
             * terminalId : 100000000057
             * acquirer : 6410001
             * transSeqId : sh001_20160514140218_000321
             * userId : 2088112186820566
             * payFee : 1
             * driverId : 0000001111111111
             * qrCode : 02010057323038383131323138363832303536365bf3cd28025807d0000000820000000000000000028ba21811db38299e2ef2fc98657b2f1683b4ba8abc6f8974543034363031303010323038383131323138363832303536360048304602210083ffefb3a2f44f413bbc7cd66121bba3072d6dc343ab58487a45055c45a53037022100e38e8c5b266600bf633488eaf34a48b472057e20ddf150e2562ac03ade6276c1045bea92e4383036021900a14b19a67728a524bdec21656bfc19258252a0dbcf2b8ea9021900b689d33ff157d4e6ce680e280fdfa30e3634724ff1fb7222
             * totalFee : 1
             * cardId : 2088112186820566
             * record : 0000000100000100E202010057323038383131323138363832303536365BF3CD28025807D0000000820000000000000000028BA21811DB38299E2EF2FC98657B2F1683B4BA8ABC6F8974543034363031303010323038383131323138363832303536360048304602210083FFEFB3A2F44F413BBC7CD66121BBA3072D6DC343AB58487A45055C45A53037022100E38E8C5B266600BF633488EAF34A48B472057E20DDF150E2562AC03ADE6276C1045BEA92E4383036021900A14B19A67728A524BDEC21656BFC19258252A0DBCF2B8EA9021900B689D33FF157D4E6CE680E280FDFA30E3634724FF1FB722200020004F81534B8000300113811EBB648743DB898AABDD1E6FC132B18000400113811EBB648743DB898AABDD1E6FC132B180005000860DF28B860DF28B800060004F81534B8000700010000080004F81534B8000900020001000A0004F81534B8000B0004F81534B8000C0004F81534B8000D0004F81534B8000E0004F81534B8000F0004F81534B8001000045BEA92E900120004F81534B800110010B7605D245AC7F7A252E673A253381207
             * station : 000010
             * currency : 156
             * vehicleType : BUS
             */

            private String bizType;
            private String transTime;
            private String transCityCode;
            private int chargeType;
            private String cardType;
            private String lineId;
            private String terminalId;
            private String acquirer;
            private String transSeqId;
            private String userId;
            private int payFee;
            private String driverId;
            private String qrCode;
            private int totalFee;
            private String cardId;
            private String record;
            private String station;
            private String currency;
            private String vehicleType;

            public RecordBean(String bizType, String transTime, String transCityCode, int chargeType, String cardType, String lineId, String terminalId, String acquirer, String transSeqId, String userId, int payFee, String driverId, String qrCode, int totalFee, String cardId, String record, String station, String currency, String vehicleType) {
                this.bizType = bizType;
                this.transTime = transTime;
                this.transCityCode = transCityCode;
                this.chargeType = chargeType;
                this.cardType = cardType;
                this.lineId = lineId;
                this.terminalId = terminalId;
                this.acquirer = acquirer;
                this.transSeqId = transSeqId;
                this.userId = userId;
                this.payFee = payFee;
                this.driverId = driverId;
                this.qrCode = qrCode;
                this.totalFee = totalFee;
                this.cardId = cardId;
                this.record = record;
                this.station = station;
                this.currency = currency;
                this.vehicleType = vehicleType;
            }

            public String getBizType() {
                return bizType;
            }

            public void setBizType(String bizType) {
                this.bizType = bizType;
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

            public int getChargeType() {
                return chargeType;
            }

            public void setChargeType(int chargeType) {
                this.chargeType = chargeType;
            }

            public String getCardType() {
                return cardType;
            }

            public void setCardType(String cardType) {
                this.cardType = cardType;
            }

            public String getLineId() {
                return lineId;
            }

            public void setLineId(String lineId) {
                this.lineId = lineId;
            }

            public String getTerminalId() {
                return terminalId;
            }

            public void setTerminalId(String terminalId) {
                this.terminalId = terminalId;
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

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public int getPayFee() {
                return payFee;
            }

            public void setPayFee(int payFee) {
                this.payFee = payFee;
            }

            public String getDriverId() {
                return driverId;
            }

            public void setDriverId(String driverId) {
                this.driverId = driverId;
            }

            public String getQrCode() {
                return qrCode;
            }

            public void setQrCode(String qrCode) {
                this.qrCode = qrCode;
            }

            public int getTotalFee() {
                return totalFee;
            }

            public void setTotalFee(int totalFee) {
                this.totalFee = totalFee;
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

            public String getVehicleType() {
                return vehicleType;
            }

            public void setVehicleType(String vehicleType) {
                this.vehicleType = vehicleType;
            }

            @Override
            public String toString() {
                return "RecordBean{" +
                        "bizType='" + bizType + '\'' +
                        ", transTime='" + transTime + '\'' +
                        ", transCityCode='" + transCityCode + '\'' +
                        ", chargeType=" + chargeType +
                        ", cardType='" + cardType + '\'' +
                        ", lineId='" + lineId + '\'' +
                        ", terminalId='" + terminalId + '\'' +
                        ", acquirer='" + acquirer + '\'' +
                        ", transSeqId='" + transSeqId + '\'' +
                        ", userId='" + userId + '\'' +
                        ", payFee=" + payFee +
                        ", driverId='" + driverId + '\'' +
                        ", qrCode='" + qrCode + '\'' +
                        ", totalFee=" + totalFee +
                        ", cardId='" + cardId + '\'' +
                        ", record='" + record + '\'' +
                        ", station='" + station + '\'' +
                        ", currency='" + currency + '\'' +
                        ", vehicleType='" + vehicleType + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "recordType='" + recordType + '\'' +
                    ", record=" + record +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "QrcodeUpload{" +
                "data=" + data +
                '}';
    }
}
