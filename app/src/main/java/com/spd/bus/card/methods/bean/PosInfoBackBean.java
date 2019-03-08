package com.spd.bus.card.methods.bean;

/**
 * Created by 张明_ on 2019/2/21.
 * Email 741183142@qq.com
 */
public class PosInfoBackBean {

    /**
     * sid : 10001
     * tracingId : W167962680DC85546F
     * data : {"payMoney":"200","keyVersion":457,"vehNum":"121301","lineId":"1621","lineName":"K621路","lineShort":"1621","version":20181127}
     * status : {"code":0,"msg":"SUCCESS"}
     */

    private int sid;
    private String tracingId;
    private DataBean data;
    private StatusBean status;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getTracingId() {
        return tracingId;
    }

    public void setTracingId(String tracingId) {
        this.tracingId = tracingId;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public static class DataBean {
        /**
         * payMoney : 200
         * keyVersion : 457
         * vehNum : 121301
         * lineId : 1621
         * lineName : K621路
         * lineShort : 1621
         * version : 20181127
         */

        private String payMoney;
        private int keyVersion;
        private String vehNum;
        private String lineId;
        private String lineName;
        private String lineShort;
        private int version;

        public String getPayMoney() {
            return payMoney;
        }

        public void setPayMoney(String payMoney) {
            this.payMoney = payMoney;
        }

        public int getKeyVersion() {
            return keyVersion;
        }

        public void setKeyVersion(int keyVersion) {
            this.keyVersion = keyVersion;
        }

        public String getVehNum() {
            return vehNum;
        }

        public void setVehNum(String vehNum) {
            this.vehNum = vehNum;
        }

        public String getLineId() {
            return lineId;
        }

        public void setLineId(String lineId) {
            this.lineId = lineId;
        }

        public String getLineName() {
            return lineName;
        }

        public void setLineName(String lineName) {
            this.lineName = lineName;
        }

        public String getLineShort() {
            return lineShort;
        }

        public void setLineShort(String lineShort) {
            this.lineShort = lineShort;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }
    }

    public static class StatusBean {
        /**
         * code : 0
         * msg : SUCCESS
         */

        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
