package com.spd.yinlianpay.card;

/**

 * AID

 */

public class TERMAPP {
    public int asi;
    public int AIDLen;
    public String AIDdata;

    public TERMAPP(int asi, int AIDLen, String AIDdata) {
        this.asi = asi;
        this.AIDdata = AIDdata;
        this.AIDLen = AIDLen;
    }

    public TERMAPP() {
    }
}
