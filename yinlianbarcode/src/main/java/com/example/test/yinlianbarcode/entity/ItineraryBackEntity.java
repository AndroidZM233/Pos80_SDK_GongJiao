package com.example.test.yinlianbarcode.entity;

import com.google.gson.Gson;

/**
 * Created by 张明_ on 2018/8/7.
 * Email 741183142@qq.com
 */

public class ItineraryBackEntity {

    /**
     * success : true
     * errcode : 0000
     * errmsg : 成功
     * result : {"order_no":"2018080715391850"}
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
         * order_no : 2018080715391850
         */

        private String order_no;

        public String getOrder_no() {
            return order_no;
        }

        public void setOrder_no(String order_no) {
            this.order_no = order_no;
        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
