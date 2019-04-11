package com.spd.yinlianpay.iso8583;

/**
 * Created by guoxiaomeng on 2017/6/23.
 */

public class PayOutputStream {
    public byte[] buf;
    public int len;

    public PayOutputStream() {
        this.buf = new byte[1024];
    }

    public PayOutputStream(int n) {
        this.buf = new byte[n];
    }

    public PayOutputStream(byte[] bs) {
        if(bs != null && bs.length > 0) {
            this.buf = bs;
            this.len = bs.length;
        } else {
            bs = new byte[1024];
        }

    }

    public int bufSize() {
        return this.buf.length;
    }

    private void newSize(int n) {
        int nlen = this.buf.length * 2;
        if(nlen < n) {
            nlen = n;
        }

        byte[] pre = this.buf;
        this.buf = new byte[nlen];
        System.arraycopy(pre, 0, this.buf, 0, pre.length);
    }

    public void reset() {
        this.len = 0;
    }

    public int size() {
        return this.len;
    }

    public byte[] toByteArray() {
        byte[] r = new byte[this.len];
        System.arraycopy(this.buf, 0, r, 0, this.len);
        return r;
    }

    public byte[] toByteArray(int pos, int len) {
        byte[] r = new byte[len];
        System.arraycopy(this.buf, pos, r, 0, len);
        return r;
    }

    public void write(byte[] b) {
        if(this.len + b.length > this.buf.length) {
            this.newSize(this.len + b.length);
        }

        System.arraycopy(b, 0, this.buf, this.len, b.length);
        this.len += b.length;
    }

    public void write(byte[] b, int pos, int blen) {
        if(this.len + blen > this.buf.length) {
            this.newSize(this.len + blen);
        }

        System.arraycopy(b, pos, this.buf, this.len, blen);
        this.len += blen;
    }

    public void writeByte(int b) {
        if(this.buf.length < this.len + 1) {
            this.newSize(this.len + 1);
        }

        this.buf[this.len++] = (byte)b;
    }

    public void writeBcdLen_c(int nLen, int len) throws PayException {
        int n1 = nLen % 10;
        nLen /= 10;
        int n2 = nLen % 10;
        nLen /= 10;
        int n3 = nLen % 10;
        if(len == 1) {
            this.writeByte(n1);
        } else if(len == 2) {
            this.writeByte(n1 | n2 << 4);
        } else {
            if(len != 3) {
                throw new PayException("err len:" + len);
            }

            this.writeByte(n3);
            this.writeByte(n1 | n2 << 4);
        }

    }

    public void writeBcdLen(int nLen, int len) throws PayException {
        String ts = Integer.toString(nLen + 100000000);
        ts = ts.substring(ts.length() - len);

        for(int i = 0; i < ts.length(); ++i) {
            this.writeByte(ts.charAt(i));
        }

    }

    public void writeASCII(String s) {
        int len = s.length();

        for(int i = 0; i < len; ++i) {
            this.writeByte(s.charAt(i));
        }

    }

    private static int getV(char c) {
        return c >= 48 && c <= 57?c - 48:(c >= 65 && c <= 70?c - 65 + 10:(c >= 97 && c <= 102?c - 97 + 10:0));
    }

    public void writeBCD_c(String s) {
        int len = s.length();
        boolean tb = (len & 1) != 0;
        if(tb) {
            --len;
        }

        int i;
        for(i = 0; i < len; i += 2) {
            this.writeByte(getV(s.charAt(i)) << 4 | getV(s.charAt(i + 1)));
        }

        if(tb) {
            this.writeByte(getV(s.charAt(i)) << 4);
        }

    }

    public void writeBCD_c(String s, int fill) {
        int len = s.length();
        boolean tb = (len & 1) != 0;
        if(tb) {
            --len;
        }

        int i;
        for(i = 0; i < len; i += 2) {
            this.writeByte(getV(s.charAt(i)) << 4 | getV(s.charAt(i + 1)));
        }

        if(tb) {
            this.writeByte(getV(s.charAt(i)) << 4 | fill);
        }

    }
    public void writeBCD_Hex(String s, int fill) {
        int len = s.length();
        boolean tb = (len & 1) != 0;
        if(tb) {
            --len;
        }

        int i;
        for(i = 0; i < len; i += 2) {
            this.writeByte(getV(s.charAt(i)) << 4 | getV(s.charAt(i + 1)));
        }

        if(tb) {
            this.writeByte(getV(s.charAt(i)) << 4 | fill);
        }
    }

    public void writeBeInt(int n) {
        this.writeByte(n >> 24);
        this.writeByte(n >> 16);
        this.writeByte(n >> 8);
        this.writeByte(n >> 0);
    }

    public void writeBcdInt(int n, int len) {
        byte[] bs = new byte[len];

        for(int i = 0; i < len; ++i) {
            bs[len - i - 1] = (byte)(48 + n % 10);
            n /= 10;
        }

        this.write(bs);
    }

    public void writeLeInt(int n) {
        this.writeByte(n >> 0);
        this.writeByte(n >> 8);
        this.writeByte(n >> 16);
        this.writeByte(n >> 24);
    }

    public void setBeInt(int p, int n) {
        this.buf[p++] = (byte)(n >> 24);
        this.buf[p++] = (byte)(n >> 16);
        this.buf[p++] = (byte)(n >> 8);
        this.buf[p++] = (byte)(n >> 0);
    }

    public void set(int p, byte[] bs) {
        System.arraycopy(bs, 0, this.buf, p, bs.length);
    }
}
