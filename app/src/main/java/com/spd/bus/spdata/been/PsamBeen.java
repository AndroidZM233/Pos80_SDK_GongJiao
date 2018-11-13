package com.spd.bus.spdata.been;

import java.lang.invoke.CallSite;
import java.util.List;

/**
 * Created by 张明_ on 2018/8/29.
 * Email 741183142@qq.com
 */

public class PsamBeen {


    private byte[] snr;//psam卡号
    private byte[] termBumber;//终端机编号
    private byte[] keyID;//秘钥索引

    public PsamBeen(byte[] snr, byte[] termBumber, byte[] keyID) {
        this.snr = snr;
        this.termBumber = termBumber;
        this.keyID = keyID;
    }

    public byte[] getSnr() {
        return snr;
    }

    public void setSnr(byte[] snr) {
        this.snr = snr;
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

}
