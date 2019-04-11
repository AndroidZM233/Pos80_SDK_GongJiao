package com.spd.yinlianpay.card;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import ui.wangpos.com.utiltool.ByteUtil;
import ui.wangpos.com.utiltool.HEXUitl;


public class TLV {
    public static final String TAG = "TLV====";
    private static final HashSet<Integer> dirSet = new HashSet();
    private byte[] data;
    private String tag;
    private int length = -1;
    private byte[] value;
    private static final char[] CS;

    private static final HashMap<String, String> nameMap ;
    private static final HashSet<Integer> len1Set = new HashSet<>();
    private static final String[] TVR;
    private static final String[] AIP_INFO;
    private static final String[] XINGNENG_INFO;
    static {
        String[] var3;
        int var2 = (var3 = "6F,A5,BF0C,70,61,77".split(",")).length;

        String s;
        int var1;
        int i;
        for(var1 = 0; var1 < var2; ++var1) {
            s = var3[var1];
            i = Integer.parseInt(s, 16);
            dirSet.add(Integer.valueOf(i));
        }

        var2 = (var3 = "6F,4F,8F".split(",")).length;

        for(var1 = 0; var1 < var2; ++var1) {
            s = var3[var1];
            i = Integer.parseInt(s, 16);
            len1Set.add(Integer.valueOf(i));
        }

        CS = "0123456789ABCDEF".toCharArray();
        nameMap = new HashMap();
        var2 = (var3 = "9F01:收单行标识;9F40:附加终端性能;81:授权金额(二进制);9F02:授权金额(数值型);9F04:其它金额(二进制);9F03:其它金额(数值型);9F3A:参考货币金额;9F06:应用标识(AID);9F09:应用版本号;8A:授权响应代码;9F34:持卡人验证方法(CVM)结果;9F22:认证中心公钥索引;83:命令模版;9F1E:接口设备(IFD)序列号;9F15:商户分类码;9F16:商户标识;9F39:销售点(POS)输入方式;9F33:终端性能(TERM_CAPA);9F1A:终端国家代码;9F1B:终端最低限额;9F1C:终端标识;9F35:终端类型;95:终端验证结果(TVR);98:交易证书(TC)哈希值;5F2A:交易货币代码;5F36:交易货币指数;9A:交易日期;99:交易PIN数据;9F3C:交易参考货币代码;9F3D:交易参考货币指数;9F41:终端维护的交易序列计数器;9B:交易状态信息(TSI);9F21:交易时间;9C:交易类型;9F37:不可预知数(UNPR_NO);5F57:账户类型;6F:文件控制信息(FCI)模板;9F26:应用密文(AC)(ARQC);9F42:应用货币代码;9F51:应用货币代码;9F44:应用货币指数;9F52:应用缺省行为(ADA);9F05:应用自定义数据;5F25:应用生效日期;5F24:应用失效日期;94:应用文件定位器(AFL);4F:应用标识符(AID);82:应用交互特征(AIP);50:应用标签;9F12:应用首选名称;5A:应用主账号(PAN);5F34:应用主账号序列号(CSN);87:应用优先指示器;61:应用模板;9F36:应用交易计数器(ATC);9F07:应用用途控制;9F08:应用版本号;8A:授权响应码;8C:卡片风险管理数据对象列表1(CDOL1);8D:卡片风险管理数据对象列表2(CDOL2);5F20:持卡人姓名;9F0B:持卡人姓名扩展;9F61:持卡人证件号;9F62:持卡人证件类型;8E:持卡人验证方法(CVM)列表;8F:CA公钥索引(PKI);9F53:连续脱机交易限制数(国际-货币);9F72:连续脱机交易限制数(国际-国家);9F27:应用信息数据;9F54:累计脱机交易金额限制数;9F75:累计脱机交易金额限制数(双货币);9F5C:累计脱机交易金额上限;9F73:货币转换因子;9F45:数据认证码;84:专用文件(DF)名称;73:目录自定义模板;9F49:动态数据认证数据对象列表(DDOL);BF0C:文件控制信息(FCI)发卡行自定义数据;A5:文件控制信息(FCI)专用模板;6F:文件控制信息(FCI)模板;9F4C:IC动态数;9F47:IC卡RSA公钥指数;9F46:IC卡公钥证书;9F48:IC卡RSA公钥余项;9F0D:发卡行行为代码(IAC)-缺省;9F0E:发卡行行为代码(IAC)-拒绝;9F0F:发卡行行为代码(IAC)-联机;9F10:发卡行应用数据(IAD);91:发卡行认证数据;9F56:发卡行认证指示位;9F11:发卡行代码表索引;5F28:发卡行国家代码;9F57:发卡行国家代码;90:发卡行公钥证书;9F32:发卡行RSA公钥指数;92:发卡行RSA公钥余项;86:发卡行脚本命令;72:发卡行脚本模板2;5F50:发卡行URL;9F5A:发卡行URL2;5F2D:首选语言;9F13:上次联机应用交易计数器(ATC)寄存器;9F4D:交易日志入口;9F4F:交易日志格式;9F14:连续脱机交易下限;9F58:连续脱机交易下限;9F66:终端交易属性;9F17:PIN尝试计数器;9F38:处理选项数据对象列表(PDOL);80:响应报文模板格式1;77:响应报文模板格式2;9F76:第2应用货币代码;5F30:服务码;88:短文件标识符(SFI);9F4B:签名的动态应用数据;93:签名的静态应用数据(SAD);9F4A:静态数据认证标签列表;9F1F:磁条1自定义数据;57:磁条2等效数据;97:交易证书数据对象列表(TDOL);9F23:连续脱机交易上限;9F59:连续脱机交易上限;9F63:产品标识信息;DF69:SM2算法支持指示器;70:响应报文的数据域;DF32:芯片序列号;DF33:过程密钥数据;DF34:终端读取时间;9F79:电子现金余额;9F77:电子现金余额上限;9F74:电子现金发卡行授权码;9F78:电子现金单笔交易限额;9F6D:电子现金重置阈值;DF4D:电子现金圈存日志入口;DF4F:电子现金圈存日志格式;9F7A:电子现金终端支持指示器;9F7B:电子现金终端交易限额;9F4E:商户名称;9F6B:卡片 CVM 限额;DF71:第二币种电子现金应用货币代码;DF79:第二币种电子现金余额;DF77:第二币种电子现金余额上限;DF78:第二币种电子现金单笔交易限额;DF76:第二币种电子现金重置阈值;DF72:第二币种卡片 CVM 限额;DF02:认证中心公钥模值;DF04:认证中心公钥指数;DF03:认证中心公钥校验值;DF05:认证中心公钥有效期;DF06:认证中心公钥HASH算法标识;DF07:认证中心公钥算法标识;DF11:交易可以联机完成但终端没有联机交易能力时，拒绝交易的收单行条件;DF12:联机交易的收单行条件;DF13:不作联机尝试即拒绝交易的收单行条件;DF14:卡片中无DDOL时用于构造内部认证命令的DDOL;DF15:在终端风险管理中用于随机交易选择的值;DF16:偏置随机选择的最大目标百分数;DF17:用于随机选择的目标百分数;DF18:是否支持联机PIN的输入;DF19:非接触交易的最低限额;DF20:非接交易最大限额;9F69:Fdda为01返回;DF60:CAPP扩展应用交易指示位;DF61:分段扣费应用标识;DF62:分段扣费抵扣限额;DF63:分段扣费已抵扣金额;EFA0:自定义:输入方式;EFA1:自定义:卡类型;EFA2:自定义:脚本执行结果;".split(";")).length;

        for(var1 = 0; var1 < var2; ++var1) {
            s = var3[var1];
            i = s.indexOf(58);
            if(i > 0) {
                nameMap.put(s.substring(0, i), s.substring(i + 1));
            }
        }

        TVR = new String[]{"", "", "复合动态数据认证/应用密文生成失败", "脱机动态数据认证失败", "卡片出现在终端异常文件中", "IC 卡数据缺失", "脱机静态数据认证失败", "未进行脱机数据认证", "", "", "", "新卡", "卡片不允许所请求的服务", "应用尚未生效", "应用已过期", "IC 卡和终端应用版本不一致", "", "", "输入联机 PIN", "要求输入 PIN，密码键盘存在，但未输入 PIN", "要求输入 PIN，但密码键盘不存在或工作不正常", "PIN 重试次数超限", "未知的 CVM", "持卡人验证失败", "", "", "", "商户要求联机交易", "交易被随机选择联机处理", "超过连续脱机交易上限", "超过连续脱机交易下限", "交易超过最低限额", "", "", "", "", "最后一次 GENERATE AC 命令之后脚本处理失败", "最后一次 GENERATE AC 命令之前脚本处理失败", "发卡行认证失败", "使用缺省 TDOL"};
        AIP_INFO = new String[]{"支持CDA—不支持", "", "支持发卡行认证—支持", "执行终端风险管理—支持", "支持持卡人认证—支持", "支持DDA—支持", "支持SDA—支持", ""};
        XINGNENG_INFO = new String[]{"", "", "", "", "", "接触式IC卡", "磁条", "手工键盘输入", "持卡人证件验证", "", "", "无需CVM", "", "签名（纸）", "加密 PIN联机验证", "IC卡明文PIN验证", "", "", "", "复合动态数据认证/应用密文生成（CDA）", "", "吞卡", "动态数据认证（DDA）", "静态数据认证（SDA"};
    }
    public static String pack(HashMap<String, String> map, String tags) {
        String[] ss = tags.split(",");
        StringBuilder sb = new StringBuilder();
        String[] var7 = ss;
        int var6 = ss.length;

        for(int var5 = 0; var5 < var6; ++var5) {
            String s = var7[var5];
            String v = (String)map.get(s);
            if(v == null) {
                System.out.println("not fount tag:" + s + " " + (String)nameMap.get(s));
            } else {
                sb.append(s);
                int len = v.length() / 2;
                if(len >= 128) {
                    boolean count = false;
                    int var11;
                    if(len > 255) {
                        var11 = 2;
                    } else {
                        var11 = 1;
                    }

                    appendV(sb, 128 | var11);

                    while(true) {
                        --var11;
                        if(var11 < 0) {
                            break;
                        }

                        appendV(sb, len >> var11 * 8);
                    }
                } else {
                    sb.append(CS[len >> 4 & 15]);
                    sb.append(CS[len >> 0 & 15]);
                }

                sb.append(v);
                System.out.println(s + ":" + v + " " + (String)nameMap.get(s));
            }
        }

        return sb.toString();
    }
    public static String makePol(DolItem[] pols, HashMap<String, String> map) {
        StringBuilder sb = new StringBuilder();
        DolItem[] var6 = pols;
        int var5 = pols.length;

        for(int var4 = 0; var4 < var5; ++var4) {
            DolItem d = var6[var4];
            String r = (String)map.get(d.tag);
            System.out.println("POL:" + d.tag + " " + r + " //" + (String)nameMap.get(d.tag));
            if(r == null) {
                System.out.println("makePol=====unknow tag=:" + d.tag);
                r = "00000000000000000000000000000000000000000000000000000000000".substring(0, d.len * 2);
                map.put(d.tag, r);
            }

            if(r.length() != d.len * 2) {
                throw new RuntimeException("tag len err:" + d.len + " " + r);
            }

            System.out.println(d);
            sb.append(r);
            map.put(d.tag, r);
        }

        return sb.toString();
    }

    private static void appendV(StringBuilder sb, int v) {
        sb.append(CS[v >> 4 & 15]);
        sb.append(CS[v >> 0 & 15]);
    }
    public static String makeLinkPinOK(String s) {
        s = s.substring(16);

        for(int i = 0; i < s.length() - 3; i += 4) {
            String sub = s.substring(i, i + 4);
            byte[] bs = HEXUitl.hexToBytes(sub);
            if((bs[0] & 63) == 2) {
                return sub + "00";
            }
        }

        return "3F0000";
    }
    public static TLV fromRawData(byte[] tlData, int tlOffset, byte[] vData, int vOffset)
    {
        int tLen = getTLength(tlData, tlOffset);
        int lLen = getLLength(tlData, tlOffset + tLen);
        int vLen = calcValueLength(tlData, tlOffset + tLen, lLen);
        TLV d = new TLV();
        d.data = ByteUtil.merage(new byte[][] { ByteUtil.subBytes(tlData, tlOffset, tLen + lLen), ByteUtil.subBytes(vData, vOffset, vLen) });
        d.getTag();
        d.getLength();
        d.getBytesValue();

        return d;
    }

    public static TLV fromData(String tagName, byte[] value) {
        byte[] tag = ByteUtil.hexString2Bytes(tagName);
        TLV d = new TLV();
        d.data = ByteUtil.merage(new byte[][] { tag, makeLengthData(value.length), value });
        d.tag = tagName;
        d.length = value.length;
        d.value = value;
        return d;
    }
    public static TLV fromData(String tagName, byte[] value1, int length) {
        byte[] value = new byte[length];
        System.arraycopy(value1,0,value,0,length);

        byte[] tag = ByteUtil.hexString2Bytes(tagName);
        TLV d = new TLV();
        d.data = ByteUtil.merage(new byte[][] { tag, makeLengthData(length), value });
        d.tag = tagName;
        d.length = length;
        d.value = value;
        return d;
    }

    public static void anaTag(byte[] bs, HashMap<String, String> map) {
        toString(bs);
        anaTag(bs, 0, bs.length, (Map)map);
    }
    public static void anaTag(byte[] bs, int pos, int len, Map<String, String> map) {
        int end = pos + len;

        while(pos < end) {
            int t = bs[pos++] & 255;
            if((t & 15) == 15 && !len1Set.contains(Integer.valueOf(t))) {
                t <<= 8;
                if(pos + 1 >= end) {
                    return;
                }

                t |= bs[pos++] & 255;
            }

            String tag = Integer.toHexString(t).toUpperCase(Locale.getDefault());
            boolean tlen = false;
            if(pos >= end) {
                return;
            }

            int var10 = bs[pos++] & 255;
            if((var10 & 128) != 0) {
                int lenCount = var10 & 127;
                var10 = 0;

                for(int i = 0; i < lenCount; ++i) {
                    var10 <<= 8;
                    if(pos >= end - 1) {
                        return;
                    }

                    var10 |= bs[pos++] & 255;
                }
            }

            if(pos + var10 > end) {
                return;
            }

            if(dirSet.contains(Integer.valueOf(t))) {
                map.put(tag, HEXUitl.bytesToHex(bs, pos, var10));
            } else {
                map.put(tag, HEXUitl.bytesToHex(bs, pos, var10));
                pos += var10;
            }
        }

    }
    private static void appendByte(StringBuilder sb, int n) {
        sb.append(CS[n >> 4 & 15]);
        sb.append(CS[n >> 0 & 15]);
    }

    private static void appendBytes(StringBuilder sb, byte[] bs, int p, int len) {
        for(int i = p; i < p + len; ++i) {
            byte n = bs[i];
            sb.append(CS[n >> 4 & 15]);
            sb.append(CS[n >> 0 & 15]);
        }

    }
    public static void toString(byte[] bs, int pos, int len, StringBuilder sb, String pre) {
        int var12;
        for(int end = pos + len; pos < end; pos += var12) {
            sb.append(pre);
            int t = bs[pos++] & 255;
            int p = sb.length();
            appendByte(sb, t);
            if((t & 15) == 15 && !len1Set.contains(Integer.valueOf(t))) {
                t <<= 8;
                if(pos + 1 >= end) {
                    return;
                }

                t |= bs[pos++] & 255;
                appendByte(sb, t);
            }

            String name = sb.substring(p);
            System.out.println(name);
            sb.append('[').append((String)nameMap.get(name)).append(']');
            sb.append(":");
            boolean tlen = false;
            if(pos >= end - 1) {
                return;
            }

            var12 = bs[pos++] & 255;
            if((var12 & 128) != 0) {
                int lenCount = var12 & 127;
                var12 = 0;

                for(int i = 0; i < lenCount; ++i) {
                    var12 <<= 8;
                    if(pos >= end - 1) {
                        return;
                    }

                    var12 |= bs[pos++] & 255;
                }
            }

            if(pos + var12 > end) {
                return;
            }

            if(dirSet.contains(Integer.valueOf(t))) {
                sb.append("\n");
                toString(bs, pos, end - pos, sb, pre + "  ");
            } else {
                appendBytes(sb, bs, pos, var12);
                sb.append("\n");
            }
        }

    }
    public static String toString(byte[] bs) {
        StringBuilder sb = new StringBuilder();
        toString(bs, 0, bs.length, sb, "");
        return sb.toString();
    }

    public static byte[] findByTag(byte[] bs, int tag) {
        return findByTag(bs, 0, bs.length, tag);
    }

    public static byte[] findByTag(byte[] bs, int pos, int len, int tag) {
        int end = pos + len;

        while(pos < end) {
            int t = bs[pos++] & 255;
            if((t & 15) == 15 && !len1Set.contains(Integer.valueOf(t))) {
                t <<= 8;
                if(pos + 1 >= end) {
                    return null;
                }

                t |= bs[pos++] & 255;
            }

            boolean tlen = false;
            if(pos >= end - 1) {
                return null;
            }

            int var9 = bs[pos++] & 255;
            if((var9 & 128) != 0) {
                int r = var9 & 127;
                var9 = 0;

                for(int i = 0; i < r; ++i) {
                    var9 <<= 8;
                    if(pos >= end - 1) {
                        return null;
                    }

                    var9 |= bs[pos++] & 255;
                }
            }

            if(pos + var9 > end) {
                return null;
            }

            if(t == tag) {
                return Arrays.copyOfRange(bs, pos, pos + var9);
            }

            if(dirSet.contains(Integer.valueOf(t))) {
                byte[] var10 = findByTag(bs, pos, end - pos, tag);
                if(var10 != null) {
                    return var10;
                }
            } else {
                pos += var9;
            }
        }

        return null;
    }
    public static TLV fromRawData(byte[] data, int offset) {
        int len = getDataLength(data, offset);
        TLV d = new TLV();
        d.data = ByteUtil.subBytes(data, offset, len);
        d.getTag();
        d.getLength();
        d.getBytesValue();
        return d;
    }

    public String getTag() {
        if (this.tag != null) {
            return this.tag;
        }
        int tLen = getTLength(this.data, 0);
        return this.tag = ByteUtil.bytes2HexString(ByteUtil.subBytes(this.data, 0, tLen));
    }

    public int getLength() {
        if (this.length > -1) {
            return this.length;
        }
        int offset = getTLength(this.data, 0);
        int l = getLLength(this.data, offset);
        if (l == 1) {
            return this.data[offset] & 0xFF;
        }

        int afterLen = 0;
        for (int i = 1; i < l; i++) {
            afterLen <<= 8;
            afterLen |= this.data[(offset + i)] & 0xFF;
        }
        return this.length = afterLen;
    }

    public int getTLLength() {
        if (this.data == null) {
            return -1;
        }
        return this.data.length - getBytesValue().length;
    }

    public String getValue() {
        byte[] temp = getBytesValue();
        return ByteUtil.bytes2HexString(temp == null ? new byte[0] : temp);
    }

    public byte getByteValue() {
        return getBytesValue()[0];
    }

    public String getGBKValue() {
        try {
            return new String(getBytesValue(), "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getNumberValue() {
        String num = getValue();
        return String.valueOf(Integer.parseInt(num));
    }

    public byte[] getGBKNumberValue() {
        try {
            return getNumberValue().getBytes("GBK");
        } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
        }
        return null;
    }

    public byte[] getBCDValue() {
        return ByteUtil.hexString2Bytes(getGBKValue());
    }

    public byte[] getRawData() {
        return this.data;
    }

    public byte[] getBytesValue() {
        if (this.value != null) {
            return this.value;
        }
        int l = getLength();
        Log.i(TAG, "getBytesValue: data:"+ByteUtil.bytes2HexString(data));
        return this.value = ByteUtil.subBytes(this.data, this.data.length - l, l);
    }

    public boolean isValid() {
        return this.data != null;
    }

    private static int getTLength(byte[] data, int offset) {
        if ((data[offset] & 0x1F) == 31) {
            if((data[offset+1] & 0x80) == 0x80){
                return 3;
            }else{
                return 2;
            }
        }
        return 1;
    }

    private static int getLLength(byte[] data, int offset) {
        if ((data[offset] & 0x80) == 0) {
            return 1;
        }
        return (data[offset] & 0x7F) + 1;
    }

    private static int getDataLength(byte[] data, int offset) {
        int tLen = getTLength(data, offset);
        int lLen = getLLength(data, offset + tLen);
        int vLen = calcValueLength(data, offset + tLen, lLen);
        return tLen + lLen + vLen;
    }

    private static int calcValueLength(byte[] l, int offset, int lLen) {
        if (lLen == 1) {
            return l[offset] & 0xFF;
        }

        int vLen = 0;
        for (int i = 1; i < lLen; i++) {
            vLen <<= 8;
            vLen |= l[(offset + i)] & 0xFF;
        }
        return vLen;
    }

    private static byte[] makeLengthData2(int len) {
        Log.i(TAG, "makeLengthData: len:"+len);
        if (len > 127) {
            byte[] lenData = new byte[4];
            int validIndex = -1;
            for (int i = 0; i < lenData.length; i++) {
                lenData[i] = ((byte)(len >> 8 * (3 - i) & 0xFF));
                Log.i(TAG, "makeLengthData: "+lenData[i]);
                if ((lenData[(i)] != 0) && (validIndex < 0)) {
                    validIndex = i;
                }
            }
            lenData = ByteUtil.subBytes(lenData, validIndex, -1);
            Log.i(TAG, "makeLengthData: lenData"+lenData);
            lenData = ByteUtil.merage(new byte[][] { { (byte)(0x80 | lenData.length) }, lenData });
            Log.i(TAG, "makeLengthData: lenData::"+ByteUtil.bytes2HexString(lenData));
            return lenData;
        }

        return new byte[] { (byte)len };
    }
    public static byte[] makeLengthData(int len) {
        if (len > 127) {
            //转化长度 to byte
            int validIndex = -1;
            byte[] lenData = int2Bytes(len);
            for(int i = 0;i<lenData.length;i++){
                if(lenData[i] != 0){
                    //有数字
                    validIndex = i;
                    break;
                }
            }
            byte[] lenresult = new byte[4-validIndex+1];
            byte l = (byte) (4-validIndex);
            lenresult[0] = (byte) (0x80 | l);
            System.arraycopy(lenData,validIndex,lenresult,1,4-validIndex);

            return lenresult;
        }

        return new byte[] { (byte)len };
    }

    public static byte[] int2Bytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) (value >> 24 & 0xFF);
        src[1] = (byte) (value >> 16 & 0xFF);
        src[2] = (byte) (value >> 8 & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
    public boolean equals(Object obj)
    {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof TLV)) {
            return false;
        }

        if ((this.data == null) || (((TLV)obj).data == null)) {
            return false;
        }

        return Arrays.equals(this.data, ((TLV)obj).data);
    }

    public String toString()
    {
        if (this.data == null) {
            return super.toString();
        }
        return ByteUtil.bytes2HexString(this.data);
    }
    public static class DolItem {
        public String tag;
        public int len;

        public DolItem() {
        }

        public String toString() {
            return this.tag + "[" + this.len + "]";
        }
    }
    public static DolItem[] decodeDOL(byte[] bs) {
        return decodeDOL(bs, 0, bs.length);
    }

    public static DolItem[] decodeDOL(byte[] bs, int pos, int len) {
        ArrayList r = new ArrayList();
        int end = pos + len;

        while(pos < end) {
            int t = bs[pos++] & 255;
            if((t & 15) == 15 && !len1Set.contains(Integer.valueOf(t))) {
                t <<= 8;
                if(pos >= end) {
                    break;
                }

                t |= bs[pos++] & 255;
            }

            boolean tlen = false;
            if(pos >= end) {
                break;
            }

            int var8 = bs[pos++] & 255;
            DolItem it = new DolItem();
            it.tag = Integer.toHexString(t).toUpperCase(Locale.getDefault());
            it.len = var8;
            r.add(it);
        }

        return (DolItem[])r.toArray(new DolItem[0]);
    }
    private static void printBitInfo(String s, String[] ss) {
        byte[] bs = HEXUitl.hexToBytes(s);
        int len = Math.min(bs.length * 8, ss.length);

        for(int i = 0; i < len; ++i) {
            if((bs[i >> 3] & 1 << (i & 7)) != 0) {
                System.out.println(i + ":" + ss[i]);
            }
        }

    }
    public static void printAIP(String ns) {
        printBitInfo(ns, AIP_INFO);
    }

    public static void printXinneng(String ns) {
        printBitInfo(ns, XINGNENG_INFO);
    }

    public static void printCvmList(String s) {
        StringBuilder sb = new StringBuilder();
        String x = s.substring(0, 8);
        String y = s.substring(8, 16);
        sb.append(" X:" + x);
        sb.append(" Y:" + y + "\n");
        byte io = 0;

        for(int i = 16; i < s.length() - 3; i += 4) {
            int need = Math.min(4, s.length() - i);
            String sub = s.substring(i, i + need);
            sb.append(io + ":" + sub + " ");
            byte[] bs = HEXUitl.hexToBytes(sub);
            if((bs[0] & 128) != 0) {
                sb.append("[如果此 CVM 失败，应用后续的]");
            } else {
                sb.append("[如果此 CVM 失败，则持卡人验证失败]");
            }

            String type = Integer.toBinaryString(bs[0] & 255 | 192);
            type = type.substring(2);
            if("000000".equals(type)) {
                sb.append("[CVM 失败处理]");
            } else if("000001".equals(type)) {
                sb.append("[卡片执行明文 PIN 核对]");
            } else if("000010".equals(type)) {
                sb.append("[联机加密 PIN 验证]");
            } else if("000011".equals(type)) {
                sb.append("[卡片执行明文 PIN 核对+签名纸上]");
            } else if("000100".equals(type)) {
                sb.append("[保留]");
            } else if("000101".equals(type)) {
                sb.append("[保留]");
            } else if("011110".equals(type)) {
                sb.append("[签名（纸上）]");
            } else if("011111".equals(type)) {
                sb.append("[无需 CVM]");
            } else if(type.compareTo("000110") >= 0 && type.compareTo("011101") <= 0) {
                sb.append("[保留给加入的支付系统]");
            } else if(type.compareTo("100000") >= 0 && type.compareTo("101111") <= 0) {
                sb.append("[保留给各自独立的支付系统]");
            } else if(type.compareTo("110000") >= 0 && type.compareTo("111110") <= 0) {
                sb.append("[保留给发卡行]");
            } else if("111111".equals(type)) {
                sb.append("[RFU]");
            } else if("100000".equals(type)) {
                sb.append("[持卡人证件出示]");
            }

            String condition = sub.substring(2);
            if("00".equals(condition)) {
                sb.append("[总是]");
            } else if("01".equals(condition)) {
                sb.append("[如果是 ATM 现金交易]");
            } else if("02".equals(condition)) {
                sb.append("[如果不是 ATM 现金或有人值守现金或返现交易]");
            } else if("03".equals(condition)) {
                sb.append("[如果终端支持这个 CVM]");
            } else if("04".equals(condition)) {
                sb.append("[如果是人工值守现金交易]");
            } else if("05".equals(condition)) {
                sb.append("[如果是返现交易]");
            } else if("06".equals(condition)) {
                sb.append("[如果交易货币等于应用货币代码而且小于 X值]");
            } else if("07".equals(condition)) {
                sb.append("[如果交易货币等于应用货币代码而且大于 X值]");
            } else if("08".equals(condition)) {
                sb.append("[如果交易货币等于应用货币代码而且小于 Y值]");
            } else if("09".equals(condition)) {
                sb.append("[如果交易货币等于应用货币代码而且大于 Y值]");
            } else if(condition.compareTo("0A") >= 0 && condition.compareTo("7F") <= 0) {
                sb.append("[RFU]");
            } else if(condition.compareTo("80") >= 0 && condition.compareTo("FF") <= 0) {
                sb.append("[保留给各个支付系统]");
            } else {
                sb.append("[未知]");
            }

            sb.append("\n");
        }

        System.out.println(sb);
    }
}
