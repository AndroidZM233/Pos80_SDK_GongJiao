package com.spd.bus.spdata;

import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.test.yinlianbarcode.entity.QrEntity;
import com.example.test.yinlianbarcode.utils.Logcat;
import com.example.test.yinlianbarcode.utils.ValidationUtils;
import com.honeywell.barcode.HSMDecodeResult;
import com.honeywell.barcode.HSMDecoder;
import com.honeywell.plugins.decode.DecodeResultListener;
import com.spd.bus.R;

import static com.honeywell.barcode.Symbology.CODE128;
import static com.honeywell.barcode.Symbology.CODE39;
import static com.honeywell.barcode.Symbology.CODE93;
import static com.honeywell.barcode.Symbology.EAN13;
import static com.honeywell.barcode.Symbology.EAN8;
import static com.honeywell.barcode.Symbology.QR;

//import com.honeywell.license.ActivationManager;


public class ScanActivity extends AppCompatActivity implements DecodeResultListener {//}, DecodeResultListener {

    private final static String TAG = "sssssActivity";
    private HSMDecoder hsmDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_demo);
        initView();
        setSettings();
//        ScanUtils.activateScan(this, new OnBackListener() {
//            @Override
//            public void onBack() {
//                Toast.makeText(ScanActivity.this, "激活成功！", Toast.LENGTH_SHORT).show();
////                setSettings();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Toast.makeText(ScanActivity.this, "激活失败！" + e.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hsmDecoder.removeResultListener(this);
        Camera camera = hsmDecoder.getCamera();
        camera.release();
        HSMDecoder.disposeInstance();
        System.out.println("关闭相机");
    }


    public void setSettings() {
        hsmDecoder = HSMDecoder.getInstance(this);
        hsmDecoder.addResultListener(this);
        //初始为默认前置摄像头扫码

        hsmDecoder.enableSymbology(EAN13);
        hsmDecoder.enableSymbology(CODE128);
        hsmDecoder.enableSymbology(CODE39);
        hsmDecoder.enableSymbology(CODE93);
        hsmDecoder.enableSymbology(EAN8);
        hsmDecoder.enableSymbology(QR);
        hsmDecoder.enableAimer(true);
        hsmDecoder.setAimerColor(Color.RED);

        hsmDecoder.setOverlayText("ceshi");
        hsmDecoder.setOverlayTextColor(Color.RED);
        hsmDecoder.enableSound(true);

    }


    @Override
    public void onHSMDecodeResult(HSMDecodeResult[] hsmDecodeResults) {
        try {
            byte[] barcodeDataBytes = hsmDecodeResults[0].getBarcodeDataBytes();

            String qr = new String(barcodeDataBytes);
            QrEntity qrEntity = new QrEntity(qr);
            try {
                boolean validation = ValidationUtils.validation(qrEntity);
                Logcat.d(validation);
                if (validation) {
                    Toast.makeText(this, "验证通过", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "验证失败", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initView() {
    }


}
