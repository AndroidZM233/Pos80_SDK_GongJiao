package com.example.test.yinlianbarcode.entity;

/**
 * Created by 张明_ on 2018/7/26.
 * Email 741183142@qq.com
 */

public class OnlinePassEntity {

    //二维码版本
    private byte[] qrVersion;
    //二维码生成类型
    private byte[] qrType;
    //授权机构证书索引
    private byte[] index;
    //移动应用标识
    private byte[] appIdentify;
    //移动应用机构号
    private byte[] appAgency;
    //二维码有效时间
    private byte[] qrValidity;
    //行业使用范围
    private byte[] industryScope;
    //二维码识别号
    private byte[] qrIdentify;
    //二维码生成时间
    private byte[] qrGenerateTime;
    //用户标识
    private byte[] userIdentify;
    //行业自定义数据长度
    private byte[] dataLength;
    //行业自定义数据
    private byte[] data;
    //授权签名
    private byte[] authorizedSignature;

    public byte[] getQrVersion() {
        return qrVersion;
    }

    public void setQrVersion(byte[] qrVersion) {
        this.qrVersion = qrVersion;
    }

    public byte[] getQrType() {
        return qrType;
    }

    public void setQrType(byte[] qrType) {
        this.qrType = qrType;
    }

    public byte[] getIndex() {
        return index;
    }

    public void setIndex(byte[] index) {
        this.index = index;
    }

    public byte[] getAppIdentify() {
        return appIdentify;
    }

    public void setAppIdentify(byte[] appIdentify) {
        this.appIdentify = appIdentify;
    }

    public byte[] getAppAgency() {
        return appAgency;
    }

    public void setAppAgency(byte[] appAgency) {
        this.appAgency = appAgency;
    }

    public byte[] getQrValidity() {
        return qrValidity;
    }

    public void setQrValidity(byte[] qrValidity) {
        this.qrValidity = qrValidity;
    }

    public byte[] getIndustryScope() {
        return industryScope;
    }

    public void setIndustryScope(byte[] industryScope) {
        this.industryScope = industryScope;
    }

    public byte[] getQrIdentify() {
        return qrIdentify;
    }

    public void setQrIdentify(byte[] qrIdentify) {
        this.qrIdentify = qrIdentify;
    }

    public byte[] getQrGenerateTime() {
        return qrGenerateTime;
    }

    public void setQrGenerateTime(byte[] qrGenerateTime) {
        this.qrGenerateTime = qrGenerateTime;
    }

    public byte[] getUserIdentify() {
        return userIdentify;
    }

    public void setUserIdentify(byte[] userIdentify) {
        this.userIdentify = userIdentify;
    }

    public byte[] getDataLength() {
        return dataLength;
    }

    public void setDataLength(byte[] dataLength) {
        this.dataLength = dataLength;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getAuthorizedSignature() {
        return authorizedSignature;
    }

    public void setAuthorizedSignature(byte[] authorizedSignature) {
        this.authorizedSignature = authorizedSignature;
    }
}
