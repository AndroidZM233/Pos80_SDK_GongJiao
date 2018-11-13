package com.spd.bus;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.test.yinlianbarcode.interfaces.OnBackListener;
import com.example.test.yinlianbarcode.utils.ScanUtils;
import com.honeywell.barcode.ActiveCamera;
import com.honeywell.barcode.HSMDecodeComponent;
import com.honeywell.barcode.HSMDecoder;
import com.honeywell.camera.CameraManager;
import com.spd.bus.spdata.utils.PlaySound;

import java.io.IOException;

import static com.honeywell.barcode.Symbology.QR;

public class MyApplication extends Application {
    String TAG = "sc100r6";
    private static HSMDecoder hsmDecoder;
    private HSMDecodeComponent hsmDecodeComponent;

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreate() {
        super.onCreate();
        PlaySound.initSoundPool(this);
        initScanBards();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.d(TAG, "onActivityCreated: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d(TAG, "onActivityStarted: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.d(TAG, "onActivityResumed: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.d(TAG, "onActivityPaused: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d(TAG, "onActivityStopped: " + activity.getLocalClassName());
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d(TAG, "onActivityDestroyed: " + activity.getLocalClassName());
//                hsmDecodeComponent.enableScanning(false);
//                hsmDecodeComponent.dispose();
            }
        });

    }

    public static HSMDecoder getHSMDecoder() {
        return hsmDecoder;
    }

    /**
     * 初始化银联二维码支付
     */
    private void initScanBards() {
        hsmDecoder = HSMDecoder.getInstance(this);
        hsmDecoder.enableAimer(true);
        hsmDecoder.setAimerColor(Color.RED);
        hsmDecoder.setOverlayText("ceshi");
        hsmDecoder.setOverlayTextColor(Color.RED);
        hsmDecoder.enableSound(true);
        //初始为默认前置摄像头扫码
        hsmDecoder.setActiveCamera(ActiveCamera.FRONT_FACING);
        hsmDecoder.enableSymbology(QR);

//        CameraManager cameraManager = CameraManager.getInstance(getApplicationContext());
//        hsmDecodeComponent = new HSMDecodeComponent(getApplicationContext());
//        cameraManager.closeCamera();

        ScanUtils.activateScan(this, new OnBackListener() {
            @Override
            public void onBack() {
                hsmDecoder.enableSymbology(QR);
//                cameraManager.openCamera();
//                hsmDecodeComponent.enableScanning(true);
                Toast.makeText(getApplicationContext(), "激活成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
//                hsmDecoder.enableSymbology(QR);
//                cameraManager.openCamera();
//                hsmDecodeComponent.enableScanning(true);
                Toast.makeText(getApplicationContext(), "激活失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTerminate() {
        Log.i(TAG, "onTerminate:    application 结束");
        HSMDecoder.disposeInstance();
        super.onTerminate();
    }

}