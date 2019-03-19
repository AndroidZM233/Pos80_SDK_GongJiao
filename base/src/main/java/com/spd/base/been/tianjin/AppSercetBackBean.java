package com.spd.base.been.tianjin;

/**
 * Created by 张明_ on 2019/2/21.
 * Email 741183142@qq.com
 */
public class AppSercetBackBean {
    /**
     * sid : 10007
     * tracingId : W16796246BEE8E5F68
     * data : {"deviceId":"17340086","appKey":"6FF5BB054CE88470","appSercet":"DB623AFEBF6B5CA8DC500449D0B59AA7"}
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
         * deviceId : 17340086
         * appKey : 6FF5BB054CE88470
         * appSercet : DB623AFEBF6B5CA8DC500449D0B59AA7
         */

        private String deviceId;
        private String appKey;
        private String appSercet;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getAppSercet() {
            return appSercet;
        }

        public void setAppSercet(String appSercet) {
            this.appSercet = appSercet;
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
