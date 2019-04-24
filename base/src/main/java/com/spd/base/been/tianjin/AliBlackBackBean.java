package com.spd.base.been.tianjin;

import org.greenrobot.greendao.annotation.Entity;

/**
 * Created by 张明_ on 2019/2/21.
 * Email 741183142@qq.com
 */
public class AliBlackBackBean {


    /**
     * crc16 : FFFF
     * code : 01
     * data : 11
     * count : 4000
     * version : 20190415
     */

    private String crc16;
    private String code;
    private String data;
    private int count;
    private String version;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
