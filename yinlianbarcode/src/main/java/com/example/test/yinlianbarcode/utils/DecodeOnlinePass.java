package com.example.test.yinlianbarcode.utils;


import com.example.test.yinlianbarcode.entity.OnlinePassEntity;
import com.example.test.yinlianbarcode.entity.UserDefinedEntity;

/**
 * 解码联机二维码
 * Created by 张明_ on 2018/7/25.
 * Email 741183142@qq.com
 */

public class DecodeOnlinePass {


    /**
     * 解析二维码数据
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public static OnlinePassEntity decode(byte[] bytes) throws Exception {
        OnlinePassEntity onlinePassEntity = new OnlinePassEntity();
        //二维码版本
        byte[] qrVersion = new byte[1];
        System.arraycopy(bytes, 0, qrVersion, 0, 1);
        onlinePassEntity.setQrVersion(qrVersion);
        //二维码生成类型
        byte[] qrType = new byte[1];
        System.arraycopy(bytes, 1, qrType, 0, 1);
        onlinePassEntity.setQrType(qrType);
        //授权机构证书索引
        byte[] index = new byte[2];
        System.arraycopy(bytes, 2, index, 0, 2);
        onlinePassEntity.setIndex(index);
        //移动应用标识
        byte[] appIdentify = new byte[8];
        System.arraycopy(bytes, 4, appIdentify, 0, 8);
        onlinePassEntity.setAppIdentify(appIdentify);
        //移动应用机构号
        byte[] appAgency = new byte[4];
        System.arraycopy(bytes, 12, appAgency, 0, 4);
        onlinePassEntity.setAppAgency(appAgency);
        //二维码有效时间
        byte[] qrValidity = new byte[2];
        System.arraycopy(bytes, 16, qrValidity, 0, 2);
        onlinePassEntity.setQrValidity(qrValidity);
        //行业使用范围
        byte[] industryScope = new byte[2];
        System.arraycopy(bytes, 18, industryScope, 0, 2);
        onlinePassEntity.setIndustryScope(industryScope);
        //二维码识别号
        byte[] qrIdentify = new byte[8];
        System.arraycopy(bytes, 20, qrIdentify, 0, 8);
        onlinePassEntity.setQrIdentify(qrIdentify);
        //二维码生成时间
        byte[] qrGenerateTime = new byte[4];
        System.arraycopy(bytes, 28, qrGenerateTime, 0, 4);
        onlinePassEntity.setQrGenerateTime(qrGenerateTime);
        //用户标识
        byte[] userIdentify = new byte[8];
        System.arraycopy(bytes, 32, userIdentify, 0, 8);
        onlinePassEntity.setUserIdentify(userIdentify);
        //行业自定义数据长度
        byte[] dataLength = new byte[1];
        System.arraycopy(bytes, 40, dataLength, 0, 1);
        onlinePassEntity.setDataLength(dataLength);
        int dataLengthInt = ByteUtils.bytes2Int(dataLength, 0, 1);
        //行业自定义数据
        byte[] data = new byte[dataLengthInt];
        System.arraycopy(bytes, 41, data, 0, dataLengthInt);
        onlinePassEntity.setData(data);
        //授权签名
        byte[] authorizedSignature = new byte[65];
        System.arraycopy(bytes, 41 + dataLengthInt, authorizedSignature, 0, 65);
        onlinePassEntity.setAuthorizedSignature(authorizedSignature);

        return onlinePassEntity;
    }


    /**
     * 解析行业自定义数据
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public static UserDefinedEntity decodeUserDefinedData(byte[] bytes) throws Exception {
        UserDefinedEntity userDefinedEntity = new UserDefinedEntity();

        //凭证类型
        byte[] voucherType = new byte[1];
        System.arraycopy(bytes, 0, voucherType, 0, 1);
        userDefinedEntity.setVoucherType(voucherType);
        //进出站使用限制
        byte[] useRestriction = new byte[1];
        System.arraycopy(bytes, 1, useRestriction, 0, 1);
        userDefinedEntity.setUseRestriction(useRestriction);
        //站点限制
        byte[] stationRestriction = new byte[8];
        System.arraycopy(bytes, 2, stationRestriction, 0, 8);
        userDefinedEntity.setStationRestriction(stationRestriction);
        //行程单号
        byte[] itinerary = new byte[8];
        System.arraycopy(bytes, 10, itinerary, 0, 8);
        userDefinedEntity.setItinerary(itinerary);
        //本行程关联的进站或上车站点编码
        byte[] encoding = new byte[2];
        System.arraycopy(bytes, 18, encoding, 0, 2);
        userDefinedEntity.setEncoding(encoding);
        //本行程扫码进站或上车时间
        byte[] time = new byte[4];
        System.arraycopy(bytes, 20, time, 0, 4);
        userDefinedEntity.setTime(time);

        return userDefinedEntity;
    }

}
