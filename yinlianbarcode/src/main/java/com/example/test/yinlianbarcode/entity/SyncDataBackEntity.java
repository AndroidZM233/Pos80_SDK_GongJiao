package com.example.test.yinlianbarcode.entity;

import com.google.gson.Gson;

/**
 * Created by 张明_ on 2018/8/7.
 * Email 741183142@qq.com
 */

public class SyncDataBackEntity {

    /**
     * success : true
     * errcode : 0000
     * errmsg : 成功
     * result : {"trip_no":"2370822086755328","fellow_no":"0000000000000000"}
     */

    private boolean success;
    private String errcode;
    private String errmsg;
    private ResultBean result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * trip_no : 2370822086755328
         * fellow_no : 0000000000000000
         */

        private String trip_no;
        private String fellow_no;

        public String getTrip_no() {
            return trip_no;
        }

        public void setTrip_no(String trip_no) {
            this.trip_no = trip_no;
        }

        public String getFellow_no() {
            return fellow_no;
        }

        public void setFellow_no(String fellow_no) {
            this.fellow_no = fellow_no;
        }
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
