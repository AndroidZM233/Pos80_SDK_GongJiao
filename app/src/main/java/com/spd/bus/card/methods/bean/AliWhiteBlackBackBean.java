package com.spd.bus.card.methods.bean;

/**
 * Created by 张明_ on 2019/2/21.
 * Email 741183142@qq.com
 */
public class AliWhiteBlackBackBean {

    /**
     * crc16 : FFFF
     * code : 01
     * data : 000030000000478f9e9f0
     */

    private String crc16;
    private String code;
    private String data;

    public String getCrc16() {
        return crc16;
    }

    public void setCrc16(String crc16) {
        this.crc16 = crc16;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
