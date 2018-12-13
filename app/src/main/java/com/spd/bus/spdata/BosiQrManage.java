package com.spd.bus.spdata;

import android.content.Context;

import com.bluering.pos.sdk.qr.CertVerInfo;
import com.bluering.pos.sdk.qr.PosQR;
import com.bluering.pos.sdk.qr.QrCodeInfo;

public class BosiQrManage {
    public BosiQrManage() {
    }

    public void bosiQrInit(Context context) {
        PosQR.init(context, "/storage/sdcard0/bosicer/");
    }

    public String bosiQrUpdateCert(String bosiCer) {
        return PosQR.updateCert(bosiCer);
    }

    public CertVerInfo bosiQrQueryCertVer() {
        return PosQR.queryCertVer();
    }

    public QrCodeInfo bosiQrVerifyCode(String qrCode) {
        return PosQR.verifyQrCode(qrCode);
    }
}
