package com.spd.base.been.tianjin;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 司机卡
 * Created by 张明_ on 2019/3/18.
 * Email 741183142@qq.com
 */
@Entity
public class TDutyStatFile {
    //当班司机
    public int rcdNr;

    public int totalPerson;
    public int totalMoney;
    public int totalYuePerson;
    public byte chk;

    public int fWork;
    public int ucLogOK;
    public int fCreateMode;
    public int tickcnt;
    public boolean fSysSta;
    @Generated(hash = 137626650)
    public TDutyStatFile(int rcdNr, int totalPerson, int totalMoney,
            int totalYuePerson, byte chk, int fWork, int ucLogOK, int fCreateMode,
            int tickcnt, boolean fSysSta) {
        this.rcdNr = rcdNr;
        this.totalPerson = totalPerson;
        this.totalMoney = totalMoney;
        this.totalYuePerson = totalYuePerson;
        this.chk = chk;
        this.fWork = fWork;
        this.ucLogOK = ucLogOK;
        this.fCreateMode = fCreateMode;
        this.tickcnt = tickcnt;
        this.fSysSta = fSysSta;
    }
    @Generated(hash = 1045619304)
    public TDutyStatFile() {
    }
    public int getRcdNr() {
        return this.rcdNr;
    }
    public void setRcdNr(int rcdNr) {
        this.rcdNr = rcdNr;
    }
    public int getTotalPerson() {
        return this.totalPerson;
    }
    public void setTotalPerson(int totalPerson) {
        this.totalPerson = totalPerson;
    }
    public int getTotalMoney() {
        return this.totalMoney;
    }
    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }
    public int getTotalYuePerson() {
        return this.totalYuePerson;
    }
    public void setTotalYuePerson(int totalYuePerson) {
        this.totalYuePerson = totalYuePerson;
    }
    public byte getChk() {
        return this.chk;
    }
    public void setChk(byte chk) {
        this.chk = chk;
    }
    public int getFWork() {
        return this.fWork;
    }
    public void setFWork(int fWork) {
        this.fWork = fWork;
    }
    public int getUcLogOK() {
        return this.ucLogOK;
    }
    public void setUcLogOK(int ucLogOK) {
        this.ucLogOK = ucLogOK;
    }
    public int getFCreateMode() {
        return this.fCreateMode;
    }
    public void setFCreateMode(int fCreateMode) {
        this.fCreateMode = fCreateMode;
    }
    public int getTickcnt() {
        return this.tickcnt;
    }
    public void setTickcnt(int tickcnt) {
        this.tickcnt = tickcnt;
    }
    public boolean getFSysSta() {
        return this.fSysSta;
    }
    public void setFSysSta(boolean fSysSta) {
        this.fSysSta = fSysSta;
    }
}
