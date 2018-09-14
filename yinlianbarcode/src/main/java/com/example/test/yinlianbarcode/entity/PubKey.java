package com.example.test.yinlianbarcode.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author :Reginer in  2018/7/25 14:27.
 * 联系方式:QQ:282921012
 * 功能描述:
 */
public class PubKey {

    @SerializedName("terminal_no")
    private String terminalNo;
    @SerializedName("op_id")
    private String opId;
    
    public PubKey(String terminalNo, String opId) {
        this.terminalNo = terminalNo;
        this.opId = opId;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
