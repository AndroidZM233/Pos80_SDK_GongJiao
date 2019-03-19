package com.spd.base.been.tianjin;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 张明_ on 2019/3/18.
 * Email 741183142@qq.com
 */
@Entity
public class TStaffTb {
    public byte ucCardClass;
    public byte[] ucIssuerCode = new byte[2];
    public byte[] ucCityCode = new byte[2];
    public byte[] ucVocCode = new byte[2];
    public byte[] ucAppSnr = new byte[8];
    public byte ucMainCardType;
    public byte ucSubCardType;
    public byte[] ucAppStartYYMMDD = new byte[3];
    public byte[] ulUTC;
    @Generated(hash = 577605041)
    public TStaffTb(byte ucCardClass, byte[] ucIssuerCode, byte[] ucCityCode,
            byte[] ucVocCode, byte[] ucAppSnr, byte ucMainCardType,
            byte ucSubCardType, byte[] ucAppStartYYMMDD, byte[] ulUTC) {
        this.ucCardClass = ucCardClass;
        this.ucIssuerCode = ucIssuerCode;
        this.ucCityCode = ucCityCode;
        this.ucVocCode = ucVocCode;
        this.ucAppSnr = ucAppSnr;
        this.ucMainCardType = ucMainCardType;
        this.ucSubCardType = ucSubCardType;
        this.ucAppStartYYMMDD = ucAppStartYYMMDD;
        this.ulUTC = ulUTC;
    }
    @Generated(hash = 35185255)
    public TStaffTb() {
    }
    public byte getUcCardClass() {
        return this.ucCardClass;
    }
    public void setUcCardClass(byte ucCardClass) {
        this.ucCardClass = ucCardClass;
    }
    public byte[] getUcIssuerCode() {
        return this.ucIssuerCode;
    }
    public void setUcIssuerCode(byte[] ucIssuerCode) {
        this.ucIssuerCode = ucIssuerCode;
    }
    public byte[] getUcCityCode() {
        return this.ucCityCode;
    }
    public void setUcCityCode(byte[] ucCityCode) {
        this.ucCityCode = ucCityCode;
    }
    public byte[] getUcVocCode() {
        return this.ucVocCode;
    }
    public void setUcVocCode(byte[] ucVocCode) {
        this.ucVocCode = ucVocCode;
    }
    public byte[] getUcAppSnr() {
        return this.ucAppSnr;
    }
    public void setUcAppSnr(byte[] ucAppSnr) {
        this.ucAppSnr = ucAppSnr;
    }
    public byte getUcMainCardType() {
        return this.ucMainCardType;
    }
    public void setUcMainCardType(byte ucMainCardType) {
        this.ucMainCardType = ucMainCardType;
    }
    public byte getUcSubCardType() {
        return this.ucSubCardType;
    }
    public void setUcSubCardType(byte ucSubCardType) {
        this.ucSubCardType = ucSubCardType;
    }
    public byte[] getUcAppStartYYMMDD() {
        return this.ucAppStartYYMMDD;
    }
    public void setUcAppStartYYMMDD(byte[] ucAppStartYYMMDD) {
        this.ucAppStartYYMMDD = ucAppStartYYMMDD;
    }
    public byte[] getUlUTC() {
        return this.ulUTC;
    }
    public void setUlUTC(byte[] ulUTC) {
        this.ulUTC = ulUTC;
    }
}
