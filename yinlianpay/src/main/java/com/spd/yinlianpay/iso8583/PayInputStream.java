package com.spd.yinlianpay.iso8583;

import java.util.Arrays;

/**
 * Created by guoxiaomeng on 2017/6/23.
 */

public class PayInputStream {
    private final byte[] bs;
    int p = 0;
    private static final char[] CS = "0123456789ABCDEF".toCharArray();

    public int readByte() throws PayException {
        if(this.p >= this.bs.length) {
            throw new PayException("EOF");
        } else {
            return this.bs[this.p++] & 255;
        }
    }

    public PayInputStream(byte[] bs) {
        this.bs = bs;
    }

    public int getPos() {
        return this.p;
    }

    public byte[] getdata(int start, int end) {
        return Arrays.copyOfRange(this.bs, start, end);
    }

    public void read(byte[] r) throws PayException {
        if(this.p + r.length > this.bs.length) {
            throw new PayException("EOF");
        } else {
            System.arraycopy(this.bs, this.p, r, 0, r.length);
            this.p += r.length;
        }
    }

    public void read(byte[] r, int pr, int len) throws PayException {
        if(this.p + len > this.bs.length) {
            throw new PayException("EOF");
        } else {
            System.arraycopy(this.bs, this.p, r, pr, len);
            this.p += len;
        }
    }

    public int readBcdInt_c(int len) throws PayException {
        int r = 0;
        len = len + 1 >> 1;

        for(int i = 0; i < len; ++i) {
            int v = this.readByte();
            r *= 10;
            r += v >> 4;
            r *= 10;
            r += v & 15;
        }

        return r;
    }

    public int readBcdInt(int len) throws PayException {
        int r = 0;

        for(int i = 0; i < len; ++i) {
            int v = this.readByte();
            r *= 10;
            r += v - 48;
        }

        return r;
    }

    public void skip(int len) throws PayException {
        if(this.p + len > this.bs.length) {
            throw new PayException("EOF");
        } else {
            this.p += len;
        }
    }

    public String readBCD_c(int len) throws PayException {
        char[] rs = new char[len];
        boolean b = (len & 1) != 0;
        if(b) {
            --len;
        }

        len /= 2;
        int io = 0;

        int v;
        for(v = 0; v < len; ++v) {
            int v1 = this.readByte();
            rs[io++] = CS[v1 >> 4];
            rs[io++] = CS[v1 & 15];
        }

        if(b) {
            v = this.readByte();
            rs[io++] = CS[v >> 4];
        }

        return new String(rs);
    }

    public String readASCII(int len) throws PayException {
        char[] rs = new char[len];
        if(this.p + len > this.bs.length) {
            throw new PayException("EOF");
        } else {
            for(int i = 0; i < len; ++i) {
                rs[i] = (char)(this.bs[this.p + i] & 255);
            }

            this.p += len;
            return new String(rs);
        }
    }
}

