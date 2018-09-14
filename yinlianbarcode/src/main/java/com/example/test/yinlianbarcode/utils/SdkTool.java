package com.example.test.yinlianbarcode.utils;

import android.support.annotation.NonNull;


import com.example.test.yinlianbarcode.constant.RsaConstant;
import com.example.test.yinlianbarcode.entity.ItineraryEntity;
import com.example.test.yinlianbarcode.entity.PubKey;
import com.example.test.yinlianbarcode.entity.SyncEntity;
import com.example.test.yinlianbarcode.entity.UploadQrEntity;

import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author :Reginer in  2018/7/26 11:43.
 *         联系方式:QQ:282921012
 *         功能描述:
 */
public class SdkTool {

    public static final String OP_ID = "2238712440816640";
    public static final String TERMINAL_NO = "0000000000000000";
    private static final String BC_PROV_ALGORITHM_SHA1RSA = "SHA1withRSA";
    private static final String FORMAT = "yyyyMMddHHmmss";

    public static HashMap<String, String> getHeader(String message) {
        int random = new Random().nextInt();
        SimpleDateFormat sdfTime = new SimpleDateFormat(FORMAT, Locale.CHINA);
        String sequence = sdfTime.format(new Date());
        HashMap<String, String> header = new HashMap<>(16);
        header.put("version", "0100");
        header.put("appId", OP_ID);
        header.put("sequence", sequence + "02200000" + random);
        header.put("random", "");
        header.put("timestamp", sequence);
        header.put("nonce", random + sequence);
        header.put("Content-Type", "application/json;charset=utf-8");
        message = "appid=" + OP_ID + "&message=" + message + "&nonce=" + random + sequence + "&timestamp=" + sequence;
        byte[] sign = new byte[0];
        try {
            sign = signBySoft(getPrivateKey(RsaConstant.PRIVATE_KEY), message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        header.put("signature", new String(base64Encode(sign)));
        return header;
    }


    private static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes = android.util.Base64.decode(key, android.util.Base64.NO_WRAP);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private static byte[] signBySoft(PrivateKey privateKey, byte[] data)
            throws Exception {
        byte[] result;
        Signature st = Signature.getInstance(BC_PROV_ALGORITHM_SHA1RSA);
        st.initSign(privateKey);
        st.update(data);
        result = st.sign();
        return result;
    }


    /**
     * 公钥获取
     *
     * @return 获取公钥请求参数
     */
    public static RequestBody getPubKeyBody() {
        PubKey pubKey = new PubKey(TERMINAL_NO, OP_ID);
        return RequestBody.create(MediaType.parse("application/json"), pubKey.toString());

    }

    /**
     * 上传二维码数据
     *
     * @return 上传二维码数据请求参数
     */
    public static RequestBody uploadQr(UploadQrEntity uploadQrEntity) {
        return RequestBody.create(MediaType.parse("application/json"), uploadQrEntity.toString());

    }

    @NonNull
    public static UploadQrEntity getUploadQrEntity(String transSeq, String voucherNo, String tripNo, String appId,
                                                   String serviceId, String userId, String voucherType, String createTime,String opId,
                                                   String lineNo, String stationNo, String direction, String terminalNo,
                                                   String terminalIp, String scanTime, String scanConfirmType, String qrCodeData,
                                                   double lan, double lat, String amount, String reserved) {
        UploadQrEntity uploadQrEntity = new UploadQrEntity();
        uploadQrEntity.setTransSeq(transSeq);
        uploadQrEntity.setVoucherNo(voucherNo);
        uploadQrEntity.setTripNo(tripNo);
        uploadQrEntity.setAppId(appId);
        uploadQrEntity.setServiceId(serviceId);
        uploadQrEntity.setUserId(userId);
        uploadQrEntity.setVoucherType(voucherType);
        uploadQrEntity.setCreateTime(createTime);
        uploadQrEntity.setOpId(opId);
        uploadQrEntity.setLineNo(lineNo);
        uploadQrEntity.setStationNo(stationNo);
        uploadQrEntity.setDirection(direction);
        uploadQrEntity.setTerminalNo(terminalNo);
        uploadQrEntity.setTerminalIp(terminalIp);
        uploadQrEntity.setScanTime(scanTime);
        uploadQrEntity.setScanConfirmType(scanConfirmType);
        uploadQrEntity.setQrCodeData(qrCodeData);
        uploadQrEntity.setLan(lan);
        uploadQrEntity.setLat(lat);
        uploadQrEntity.setAmount(amount);
        uploadQrEntity.setReserved(reserved);
        return uploadQrEntity;
    }


    /**
     * 行程扣款
     *
     * @return 行程扣款请求参数
     */
    public static RequestBody itinerary(ItineraryEntity itineraryEntity) {
        return RequestBody.create(MediaType.parse("application/json"), itineraryEntity.toString());

    }

    @NonNull
    public static ItineraryEntity getItineraryEntity(String tripNo, String feeMode,
                                                     String transAmount, String baseAmount, String discountAmount, String discountDesc,
                                                     String fineAmount, String fineDesc, String settlementAmount) {
        ItineraryEntity itineraryEntity = new ItineraryEntity();
        SimpleDateFormat sdfTime1 = new SimpleDateFormat("yyyyMMddHHmmss");
        int random = (int) (Math.random() * 100);
        String time = sdfTime1.format(new Date());
        String order_no = time + random;

        itineraryEntity.setOrderNo(order_no);
        itineraryEntity.setTripNo(tripNo);
        itineraryEntity.setFeeMode(feeMode);
        itineraryEntity.setTransTime(time);
        itineraryEntity.setTransAmount(transAmount);
        itineraryEntity.setBaseAmount(baseAmount);
        itineraryEntity.setDiscountAmount(discountAmount);
        itineraryEntity.setDiscountDesc(discountDesc);
        itineraryEntity.setFineAmount(fineAmount);
        itineraryEntity.setFineDesc(fineDesc);
        itineraryEntity.setSettlementAmount(settlementAmount);
        return itineraryEntity;
    }


    /**
     * 行程数据同步
     *
     * @return
     */
    public static RequestBody syncData(SyncEntity syncEntity) {
        return RequestBody.create(MediaType.parse("application/json"), syncEntity.toString());
    }

    @NonNull
    public static SyncEntity getSyncEntity(String voucherNo, String tripNo, String appId, String serviceId, String userId
            , String createTime, String lineNo, String stationNo, String scanTime, String qrCode) {
        SyncEntity syncEntity = new SyncEntity();
        syncEntity.setIn_voucher_no(voucherNo);
        syncEntity.setTrip_no(tripNo);
        syncEntity.setApp_id(appId);
        syncEntity.setService_id(serviceId);
        syncEntity.setUser_id(userId);
        syncEntity.setIn_sys_time(createTime);
        syncEntity.setIn_line_no(lineNo);
        syncEntity.setIn_station_no(stationNo);
        syncEntity.setIn_time(scanTime);
        syncEntity.setOut_qrcode_data(qrCode);
        return syncEntity;

    }

    /**
     * BASE64编码
     *
     * @param inputByte 待编码数据
     * @return 解码后的数据
     */
    private static byte[] base64Encode(byte[] inputByte) {
        return Base64.encodeBase64(inputByte);
    }
}
