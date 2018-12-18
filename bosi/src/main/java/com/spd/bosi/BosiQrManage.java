package com.spd.bosi;

import android.content.Context;

import com.bluering.pos.sdk.qr.CertVerInfo;
import com.bluering.pos.sdk.qr.PosQR;
import com.bluering.pos.sdk.qr.QrCodeInfo;
import com.spd.base.utils.Datautils;

public class BosiQrManage {
    public BosiQrManage() {
    }

    public static void bosiQrInit(Context context, String filePath) {
        PosQR.init(context, filePath);
    }

    public static String bosiQrUpdateCert(String bosiCer) {
        return PosQR.updateCert(bosiCer);
    }

    public static int bosiQrQueryCertVer() {
        CertVerInfo certVerInfo = PosQR.queryCertVer();
        int info = Datautils.byteArrayToInt(certVerInfo.getCertVerInfo());
        if (certVerInfo.getResult().equals("0")) {
            return info;
        } else {
            return -1;
        }
    }

    public static QrCodeInfo bosiQrVerifyCode(String qrCode) {
        return PosQR.verifyQrCode(qrCode);
    }
}
