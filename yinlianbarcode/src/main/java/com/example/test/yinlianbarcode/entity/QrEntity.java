package com.example.test.yinlianbarcode.entity;

import android.util.Base64;

import com.example.test.yinlianbarcode.utils.ParseUtils;
import com.google.gson.Gson;

/**
 * @author :Reginer in  2018/7/26 15:37.
 *         联系方式:QQ:282921012
 *         功能描述:
 */
public class QrEntity {
    /**
     * 二维码版本
     */
    private String version;
    /**
     * 二维码类型
     */
    private String type;
    /**
     * 授权机构证书索引
     */
    private String index;
    /**
     * 移动应用标识
     */
    private String mobileMark;
    /**
     * 移动应用机构号
     */
    private String mobileNumber;
    /**
     * 二维码有效时间
     */
    private long valid;
    /**
     * 行业使用范围
     */
    private String scope;
    /**
     * 二维码识别号
     */
    private String qrCode;
    /**
     * 二维码生成时间
     */
    private long createTime;
    /**
     * 用户标识
     */
    private String userMark;

    /**
     * 行业自定义数据长度
     */
    private String customLength;

    /**
     * 行业自定义数据
     */
    private String customData;


    /**
     * 授权签名
     */
    private String signature;

    /**
     * 密文信息
     */
    private byte[] sourceData;
    private transient byte[] base64;

    public QrEntity(String qr) {
        base64 = Base64.decode(qr, Base64.NO_WRAP);
//        base64 = ParseUtils.hex2Bytes("02111003a12012018000003312000001003c1000265122179186790402f00d4400000000839065511800000000000000000000265122179186790400000000000015306b5036bb226c9664606d7faa1e1128da82bfbb3008f92619cc345050b8e3875cab6f9e993df13ecfd4fea4be6291ae9fda76a968a2a03c5b4e2f9b115e8c25");;
        this.version = getVersion();
        this.type = getType();
        this.index = getIndex();
        this.mobileMark = getMobileMark();
        this.mobileNumber = getMobileNumber();
        this.valid = getValid();
        this.scope = getScope();
        this.qrCode = getQrCode();
        this.createTime = getCreateTime();
        this.userMark = getUserMark();
        this.customLength = getCustomLength();
        this.customData = getCustomData();
        this.signature = getSignature();
        this.sourceData = getSourceData();
    }

    public String getVersion() {
        return ParseUtils.bytes2Hex(ParseUtils.sub(base64, 0, 1));
    }

    public String getType() {
        return ParseUtils.bytes2Hex(ParseUtils.sub(base64, 1, 1));
    }

    public String getIndex() {
        return ParseUtils.bytes2Hex(ParseUtils.sub(base64, 2, 2));
    }

    public String getMobileMark() {
        return ParseUtils.bytes2Hex(ParseUtils.sub(base64, 4, 8));
    }

    public String getMobileNumber() {
        return ParseUtils.bytes2Hex(ParseUtils.sub(base64, 12, 4));
    }

    public long getValid() {
        return Long.parseLong(ParseUtils.binary(ParseUtils.sub(base64, 16, 2), 10)) * 1000;
    }

    public String getScope() {
        return ParseUtils.bytes2Hex(ParseUtils.sub(base64, 18, 2));
    }

    public String getQrCode() {
        return ParseUtils.bytes2Hex(ParseUtils.sub(base64, 20, 8));
    }

    public long getCreateTime() {
        long passTime = Long.parseLong(ParseUtils.binary(ParseUtils.sub(base64, 28, 4), 10));
        long time20170101 = 1483200000;
        return (passTime + time20170101) * 1000;
    }

    public String getUserMark() {
        return ParseUtils.bytes2Hex(ParseUtils.sub(base64, 32, 8));
    }

    public String getCustomLength() {
        return ParseUtils.bytes2Hex(ParseUtils.sub(base64, 40, 1));
    }

    public int getLength() {
        return Integer.parseInt(getCustomLength(), 16);
    }

    public String getCustomData() {
        return ParseUtils.bytes2Hex(ParseUtils.sub(base64, 41, getLength()));
    }

    public String getSignature() {
        return ParseUtils.bytes2Hex(ParseUtils.sub(base64, 41 + getLength(), 65));
    }

    public byte[] getSourceData() {
        return ParseUtils.sub(base64, 4, base64.length - 69);
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
