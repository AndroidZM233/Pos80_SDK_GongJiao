package com.example.test.yinlianbarcode.utils;

import android.util.Base64;


import com.example.test.yinlianbarcode.AppYinLian;
import com.example.test.yinlianbarcode.entity.PubKeyEntity;
import com.example.test.yinlianbarcode.entity.QrEntity;
import com.example.test.yinlianbarcode.smcrypto.SM2Cipher;
import com.spd.base.been.tianjin.KeysBean;

import java.util.List;


/**
 * 二维码有效性验证
 * Created by 张明_ on 2018/7/27.
 * Email 741183142@qq.com
 */

public class ValidationUtils {
    /**
     * 验签
     *
     * @param qrEntity
     * @return
     * @throws Exception
     */
    public static boolean validation(QrEntity qrEntity) throws Exception {
        Logcat.d(qrEntity.toString());
        String scope = qrEntity.getScope();
        String substring = scope.substring(0, 2);
        if (!"02".equals(substring)) {
            return false;
        }

        String index = qrEntity.getIndex();
        PubKeyEntity pubKeyEntity = AppYinLian.getInstance().getPubKeyEntity();
        Logcat.d(pubKeyEntity.toString());
        List<PubKeyEntity.ResultEntity.CertEntity> cert = pubKeyEntity.getResult().getCert();
        for (PubKeyEntity.ResultEntity.CertEntity certEntity : cert) {
            String cert_no = certEntity.getCert_no();
            Logcat.d(cert_no);
            if (cert_no.equals(index)) {
                String public_key = certEntity.getPublic_key();
                String signature = qrEntity.getSignature();
                String signatureResult = signature.substring(2);
                // 国密规范测试用户ID
                int length = qrEntity.getLength();
                String userId = "1234567812345678";
                byte[] publicKey = Base64.decode(public_key, Base64.NO_WRAP);
                String publicKeyHex = "04" + ParseUtils.bytes2Hex(publicKey);
//                String publicKeyHex = "044aa5e6e3091fdb5afb40bc4f08706fc26b2e1c062ab415cf111e908024fc9c42e2d18273484582b171c41472597f76010c3ce5030a1ee9bf2fb0f1cfaaf79ade";
                Logcat.d(publicKeyHex);
                SM2Cipher cipher = new SM2Cipher(SM2Cipher.Type.C1C3C2);
                byte[] userIdBytes = userId.getBytes();
                byte[] publicKeyBytes = ParseUtils.hex2Bytes(publicKeyHex);
                byte[] sourceDataBytes = qrEntity.getSourceData();
                String sourceDataHex = ParseUtils.bytes2Hex(sourceDataBytes);
                byte[] signDataBytes = ParseUtils.hex2Bytes(signatureResult);
                boolean verifyBoolean = cipher.verifySignByBytes(userIdBytes, publicKeyBytes, sourceDataBytes, signDataBytes);
                System.out.println("验签结果: " + verifyBoolean);
                if (!verifyBoolean) {
                    return false;
                } else {
                    break;
                }

            }
        }

        long valid = qrEntity.getValid();
        long createTime = qrEntity.getCreateTime();
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > (createTime + valid)) {
            return false;
        }


        return true;
    }


    /**
     * 天津验签
     *
     * @param qrEntity
     * @param list
     * @return
     * @throws Exception
     */
    public static int validationTianJin(QrEntity qrEntity, List<KeysBean> list) throws Exception {
        String scope = qrEntity.getScope();
        String substring = scope.substring(0, 2);
        if (!"02".equals(substring)) {
            return -1;
        }

        String index = qrEntity.getIndex();
        for (KeysBean keysBean : list) {
            String certNo = keysBean.getCert_no();
            if (certNo.equals(index)) {
                String public_key = keysBean.getPublic_key();
                String signature = qrEntity.getSignature();
                String signatureResult = signature.substring(2);
                // 国密规范测试用户ID
                int length = qrEntity.getLength();
//                String userId = qrEntity.getUserMark().toLowerCase();
                String userId = "1234567812345678";
                byte[] publicKey = Base64.decode(public_key, Base64.NO_WRAP);
                String publicKeyHex = "04" + ParseUtils.bytes2Hex(publicKey);
//                String publicKeyHex = "044aa5e6e3091fdb5afb40bc4f08706fc26b2e1c062ab415cf111e908024fc9c42e2d18273484582b171c41472597f76010c3ce5030a1ee9bf2fb0f1cfaaf79ade";
                SM2Cipher cipher = new SM2Cipher(SM2Cipher.Type.C1C3C2);
                byte[] userIdBytes = userId.getBytes();
                byte[] publicKeyBytes = ParseUtils.hex2Bytes(publicKeyHex);
                byte[] sourceDataBytes = qrEntity.getSourceData();
                String sourceDataHex = ParseUtils.bytes2Hex(sourceDataBytes);
                byte[] signDataBytes = ParseUtils.hex2Bytes(signatureResult);
                boolean verifyBoolean = cipher.verifySignByBytes(userIdBytes, publicKeyBytes, sourceDataBytes, signDataBytes);
                System.out.println("验签结果: " + verifyBoolean);
                if (!verifyBoolean) {
                    return -1;
                } else {
                    break;
                }

            }
        }
        long valid = qrEntity.getValid();
        long createTime = qrEntity.getCreateTime();
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > (createTime + valid)) {
            return -2;
        }

        return 0;
    }

}
