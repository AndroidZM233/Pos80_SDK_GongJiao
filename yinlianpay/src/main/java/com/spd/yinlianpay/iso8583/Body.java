package com.spd.yinlianpay.iso8583;

import java.util.Arrays;
import java.util.HashMap;

import ui.wangpos.com.utiltool.HEXUitl;
import ui.wangpos.com.utiltool.Util;

/**
 * Created by guoxiaomeng on 2017/6/23.
 */

public class Body {
    public final String[] ds = new String[128];
    public String type;
    private final Field[] items;

    public Body(Field[] items) {
        this.items = items;
    }

    public void getFields(HashMap<Integer, String> map) {
        for(int i = 0; i < this.ds.length; ++i) {
            if(this.ds[i] != null) {
                map.put(Integer.valueOf(i + 1), this.ds[i]);
            }
        }

    }

    public String[] getFields() {
        String[] rs = new String[129];

        for(int i = 0; i < this.ds.length; ++i) {
            rs[i + 1] = this.ds[i];
        }

        return rs;
    }

    public void setType(String s) {
        this.type = s;
    }
    public String getType()
    {
       return this.type;
    }

    public byte[] getFieldData(int i) {
        --i;
        if(i >= 0 && i < this.ds.length && this.ds[i] != null) {
            String s = this.ds[i];
            return Util.string2bytes(s);
        } else {
            return null;
        }
    }

    public String getField(int i) {
        return this.get(i);
    }

    public String get(int i) {
        --i;
        if(i >= 0 && i < this.ds.length && this.ds[i] != null) {
            String s = this.ds[i];
            return s;
        } else {
            return null;
        }
    }

    public void setFieldData(int i, byte[] bs) {
        this.setField(i, Util.bytes2string(bs));
    }

    public static byte[] makeMap(String s) {
        String[] ss = s.split(",");
        byte[] bs = new byte[16];
        String[] var6 = ss;
        int var5 = ss.length;

        for(int var4 = 0; var4 < var5; ++var4) {
            String t = var6[var4];
            int p = Integer.parseInt(t.trim());
            --p;
            bs[p >> 3] = (byte)(bs[p >> 3] | 1 << 7 - (p & 7));
        }

        return bs;
    }

    public void read(PayInputStream ins) throws PayException {
        int pos = ins.getPos();
        if(this.items[2].compress) {
            this.type = ins.readBCD_c(4);
        } else {
            this.type = ins.readASCII(4);
        }

        Arrays.fill(this.ds, (Object)null);
        byte[] ms = new byte[16];
        ins.read(ms, 0, 8);
        if((ms[0] & 128) != 0) {
            ins.read(ms, 8, 8);
        }

        for(int i = 1; i < 128; ++i) {
            if(i == 63) {
                ins.getdata(pos, ins.getPos());
            }

            if((ms[i >> 3] & 1 << 7 - (i & 7)) != 0) {
                try {
                    this.ds[i] = this.items[i + 1].decode(ins);
                } catch (Exception var6) {
                    throw new PayException(var6.getMessage() + " [" + (i + 1) + "]域解析失败", var6);
                }
            }
        }
    }

    private static boolean isChars(String s) {
        for(int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if(c < 32) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TYPE:" + this.type + "\n");

        for(int i = 1; i < this.ds.length; ++i) {
            if(this.ds[i] != null) {
                String name = this.items[i + 1].name;
                String ts = this.ds[i];
                if(!isChars(ts)) {
                    ts = "HEX:" + HEXUitl.bytesToHex(Util.string2bytes(ts));
                }

                int index = i + 1;
                if(index == 2) {
                    sb.append(i + 1 + ":" + ts.toString()+"//" + name + "\n");
                    //sb.append(i + 1 + ":" + ts.substring(0, 6) + "******" + ts.substring(ts.length() - 4) + "//" + name + "\n");
                } else if(index != 35 && index != 52 && index != 53 && index != 55) {
                    sb.append(index + ":" + ts + "//" + name + "\n");
                } else {
                    sb.append(i + 1 + ":" + ts.toString()+"//" + name + "\n");
                    //sb.append(index + ":" + ts.substring(0, 2) + "****" + ts.substring(ts.length() - 2) + "//" + name + "\n");
                }
            }
        }

        return sb.toString();
    }

    public void setField(int i, String n) {
        if(n == null) {
            this.ds[i - 1] = null;
        } else {
            this.ds[i - 1] = n;
        }

    }

    public void toByteArray(PayOutputStream os) throws PayException {
        byte[] bs = new byte[16];
        boolean is128 = false;

        int i;
        for(i = 0; i < this.ds.length; ++i) {
            if(this.ds[i] != null) {
                bs[i >> 3] = (byte)(bs[i >> 3] | 1 << 7 - (i & 7));
                if(i >= 64) {
                    is128 = true;
                }
            }
        }

        if(is128) {
            bs[0] = (byte)(bs[0] | 128);
        } else {
            bs = Arrays.copyOf(bs, 8);
        }

        if(this.items[2].compress) {
            os.write(Util.toBCD(this.type));
        } else {
            os.writeASCII(this.type);
        }

        os.write(bs);

        for(i = 1; i < this.ds.length; ++i) {
            if(this.ds[i] != null) {
                this.items[i + 1].encode(this.ds[i], os);
            }
        }

    }
}
