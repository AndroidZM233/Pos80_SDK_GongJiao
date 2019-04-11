package com.spd.yinlianpay.iso8583;

/**
 * Created by guoxiaomeng on 2017/6/23.
 */

public class Field {
    private static final String ZS = "0000000000000000000000000000000000000";
    private static final String ES = "                                     ";
    public int index;
    public String name;
    public int maxLen;
    public int varLen;
    public char type;
    private final String pre;
    public final boolean compress;

    public String toString() {
        return this.pre;
    }

    public Field(String s, boolean compress) {
        this.compress = compress;
        this.pre = s;
        int i = s.indexOf(58);
        this.index = Integer.parseInt(s.substring(0, i));
        String[] ss = s.substring(i + 1).split(",");
        this.name = ss[0];
        s = ss[1];
        if(s.startsWith("LLLVAR-")) {
            this.varLen = 3;
            s = s.substring(7);
        } else if(s.startsWith("LLVAR-")) {
            this.varLen = 2;
            s = s.substring(6);
        } else if(s.startsWith("LVAR-")) {
            this.varLen = 1;
            s = s.substring(5);
        } else {
            this.varLen = 0;
        }

        if(s.startsWith("ANS")) {
            this.type = 83;
            s = s.substring(3);
        } else if(s.startsWith("AN")) {
            this.type = 65;
            s = s.substring(2);
        } else if(s.startsWith("N")) {
            this.type = 78;
            s = s.substring(1);
        } else if(s.startsWith("B")) {
            this.type = 66;
            s = s.substring(1);
        }else if(s.startsWith("SN")) {
            this.type = 67;
            s = s.substring(2);
        } else {
            if(!s.startsWith("E")) {
                throw new RuntimeException("type error:" + i);
            }
            this.type = 69;
            s = s.substring(1);
        }
        this.maxLen = Integer.parseInt(s);
        if(this.type == 66) {
            this.maxLen /= 8;
            if(compress) {
                this.maxLen *= 2;
            }
        }

    }

    public void encode(String o, PayOutputStream os) throws PayException {
        if(this.index == 55) {
            System.out.println("55");
        }

        if(this.index == 52) {
            System.out.println("52:" + o);
        }

        if(this.type == 69) {
            throw new PayException("不支持的类型:" + this.index);
        } else {
            int need = this.maxLen - o.length();
            if(need < 0) {
                throw new PayException("数据太长:" + o.length() + ">" + this.maxLen + " " + o + " filed:" + this.index);
            } else {
                if(this.compress) {
                    if(this.varLen > 0) {

                        if(this.type == 78) {
                            os.writeBcdLen_c(o.length(), this.varLen);
                            os.writeBCD_c(o);
                        }else if(this.type == 67) {
                            os.writeBcdLen_c(o.length()/2, this.varLen);
                            os.writeBCD_Hex(o,this.varLen);
                        }else {
                            os.writeBcdLen_c(o.length(), this.varLen);
                            os.writeASCII(o);
                        }
                    } else if(this.type == 78) {
                        if(need > 0) {
                            o = "0000000000000000000000000000000000000".substring(0, need) + o;
                        }

                        os.writeBCD_c(o);
                    } else if(this.type == 66) {
                        if(need != 0) {
                            throw new PayException(this.index + "长度异常");
                        }

                        os.writeBCD_c(o);
                    } else {
                        if(need > 0) {
                            o = o + "                                     ".substring(0, need);
                        }

                        os.writeASCII(o);
                    }
                } else if(this.varLen > 0) {
                    os.writeBcdLen(o.length(), this.varLen);
                    os.writeASCII(o);
                } else if(this.type == 78) {
                    if(need > 0) {
                        o = "0000000000000000000000000000000000000".substring(0, need) + o;
                    }
                    os.writeASCII(o);
                }
                else if(this.type == 67) {
                    os.writeBcdLen_c(o.length()/2, this.varLen);
                    os.writeBCD_Hex(o,this.varLen);
                }else if(this.type == 66) {
                    if(need != 0) {
                        throw new PayException(this.index + "域长度异常:" + need + "; ");
                    }
                    os.writeASCII(o);
                } else {
                    if(need > 0) {
                        o = o + "                                     ".substring(0, need);
                    }

                    os.writeASCII(o);
                }

            }
        }
    }

    public String decode(PayInputStream ins) throws PayException {
        if(this.type == 69) {
            throw new PayException("不支持的类型:" + this.index);
        } else {
            int len;
            if(this.compress) {
                if(this.varLen > 0) {
                    len = ins.readBcdInt_c(this.varLen);
                    if(len > this.maxLen) {
                        throw new PayException(this.index + "长度太长:" + len + ">" + this.maxLen);
                    } else if(this.type == 78)
                    {
                        return ins.readBCD_c(len);
                        //return (this.type == 78 || this.type==67)?ins.readBCD_c(len):ins.readASCII(len);
                    }else if(this.type == 67)
                    {
                        return  ins.readBCD_c(len*2);
                    }
                    else
                    {
                       return ins.readASCII(len);
                    }
                } else {
                    return this.type == 78?ins.readBCD_c(this.maxLen):(this.type == 66?ins.readBCD_c(this.maxLen):ins.readASCII(this.maxLen));
                }
            } else if(this.varLen > 0) {
                len = ins.readBcdInt(this.varLen);
                if(len > this.maxLen) {
                    throw new PayException("长度太长:" + len + ">" + this.maxLen);
                } else {
                    return ins.readASCII(len);
                }
            } else {
                return ins.readASCII(this.maxLen);
            }
        }
    }

    public static Field[] makeItems(String FORMAT, boolean isCompress) {
        Field[] fitems = new Field[129];
        String[] ss = FORMAT.split(";");
        String[] var7 = ss;
        int var6 = ss.length;

        for(int var5 = 0; var5 < var6; ++var5) {
            String i = var7[var5];
            Field f = new Field(i, isCompress);
            fitems[f.index] = f;
        }

        for(int var9 = 0; var9 < fitems.length; ++var9) {
            if(fitems[var9] == null) {
                fitems[var9] = new Field(var9 + ":未定义,E1", isCompress);
            }
        }

        return fitems;
    }
}
