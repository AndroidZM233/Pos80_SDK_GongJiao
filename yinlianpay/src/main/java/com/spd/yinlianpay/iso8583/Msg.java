package com.spd.yinlianpay.iso8583;


import com.spd.yinlianpay.comm.Field39Code;

/**
 * Created by guoxiaomeng on 2017/6/23.
 */
//解决报文
public class Msg {
    public String tpdu;
    public String head;
    public Body body;
    public static final int MAC_NONE = 0;
    public static final int MAC_OK = 1;
    public static final int MAC_ERR = 2;
    public int macResult;

    public Msg() {
    }

    @Override
    public String toString() {
        String mac = "MAC:NONE\n";
        if (this.macResult == 1) {
            mac = "MAC:OK\n";
        } else if (this.macResult == 2) {
            mac = "MAC:ERR\n";
        }
        return "TPDU:" + this.tpdu + "\nHEAD:" + this.head + "\n" + mac + this.body;
    }

    public byte[] toByteArray() throws PayException {
        PayOutputStream baos = new PayOutputStream();
        baos.writeBCD_c(this.tpdu);
        if (this.head != null) {
            baos.writeBCD_c(this.head);
        }

        this.body.toByteArray(baos);
        return baos.toByteArray();
    }

    public RespCode getReqCode() {
        String s = this.body.getField(39);
        return new RespCode(s);
    }

    public RespCode getReqCode2() {
        String s = this.body.getField(39);
        RespCode respCode = new RespCode(s);
        respCode.QRcode = this.body.getField(62);
        respCode.OrderNo = this.body.getField(20);
        return respCode;
    }
    public String getField39Code()
    {
       return new Field39Code().getErrCode(this.body.get(39));
    }
}
