package com.spd.base.been.tianjin;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 张明_ on 2019/2/21.
 * Email 741183142@qq.com
 */
@Entity
public class AliWhiteBackBean {

    /**
     * crc16 : FFFF
     * code : 01
     * data : 000030000000478f9e9f0
     */

    private String crc16;
    private String code;
    private String data;
    @Generated(hash = 251126654)
    public AliWhiteBackBean(String crc16, String code, String data) {
        this.crc16 = crc16;
        this.code = code;
        this.data = data;
    }
    @Generated(hash = 1988580747)
    public AliWhiteBackBean() {
    }
    public String getCrc16() {
        return this.crc16;
    }
    public void setCrc16(String crc16) {
        this.crc16 = crc16;
    }
    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getData() {
        return this.data;
    }
    public void setData(String data) {
        this.data = data;
    }


}
