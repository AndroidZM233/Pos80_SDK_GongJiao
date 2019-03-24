package com.spd.bus.spdata.been;

import java.lang.invoke.CallSite;
import java.util.List;

public class PsamBeen {
    private int id;
    //终端机编号16文件下读出卡号

    private byte[] termBumber;
    //秘钥索引
    private byte[] keyID;

    private byte[] snr;

    public PsamBeen(int id, byte[] termBumber, byte[] keyID, byte[] snr) {
        this.id = id;
        this.termBumber = termBumber;
        this.keyID = keyID;
        this.snr = snr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public byte[] getTermBumber() {
        return termBumber;
    }

    public void setTermBumber(byte[] termBumber) {
        this.termBumber = termBumber;
    }

    public byte[] getKeyID() {
        return keyID;
    }

    public void setKeyID(byte[] keyID) {
        this.keyID = keyID;
    }

    public byte[] getSnr() {
        return snr;
    }

    public void setSnr(byte[] snr) {
        this.snr = snr;
    }
}
