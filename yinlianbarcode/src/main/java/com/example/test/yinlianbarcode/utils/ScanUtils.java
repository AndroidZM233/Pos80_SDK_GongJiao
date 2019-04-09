package com.example.test.yinlianbarcode.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Camera;
import android.util.Log;

import com.example.test.yinlianbarcode.interfaces.OnBackListener;
import com.honeywell.barcode.HSMDecoder;
import com.honeywell.license.ActivationManager;
import com.honeywell.license.ActivationResult;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 张明_ on 2018/7/23.
 * Email 741183142@qq.com
 */

public class ScanUtils {
    public static String finalDecrypt = "trial-speed-tjian-05232018";

    /**
     * 激活扫描
     */
    public static void activateScan(final Context context, final OnBackListener onBackListener, boolean isLocal) {
//        final String key = "37837B0642FE2D76714D44A02B7B916FD0DADE468C4DE621622D6EEDE3DBF406";
//        Log.d("ZM", "activateScan: " + key);
//        String decrypt = null;
//        try {
//            decrypt = AESUtil.decrypt(key, "speedata_speedta");000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
//        } catch (Exception e) {
//            e.printStackTrace();
//            onBackListener.onError(new Throwable("Key decrypt: " + e.toString()));
//            return;
//        }


        Observable.create(new ObservableOnSubscribe<ActivationResult>() {
            @Override
            public void subscribe(ObservableEmitter<ActivationResult> e) throws Exception {
                try {
                    ActivationResult toString = getActivationResultString(isLocal, context, finalDecrypt);
                    e.onNext(toString);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    e.onError(e1);
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<ActivationResult>() {
                    private Disposable d;

                    @Override
                    public void onSubscribe(Disposable d) {
                        this.d = d;
                    }

                    @Override
                    public void onNext(ActivationResult value) {
                        Log.d("ZM", "激活: " + value);
                        System.out.println("激活" + value);
                        if (!value.toString().contains("SUCCESS")) {
//                            closeCamera(context);

                            onBackListener.onError(new Throwable("扫描服务激活" + value));
                            delFile(context);
                        } else {
                            SharedXmlUtil.getInstance(context).write("deactivateScan", false);
                            Log.d("ZM", "激活成功");
                            onBackListener.onBack();
                        }

                        d.dispose();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onBackListener.onError(e);
                        d.dispose();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 删除文件
     */
    public static void delFile(final Context context) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                try {
                    boolean b = FileUtils.deleteFile("data/data/"
                            + FileUtils.getAppProcessName(context) + "/anchoring-0");
                    boolean b1 = FileUtils.deleteFile("data/data/"
                            + FileUtils.getAppProcessName(context) + "/storage-0");
                    Log.d("ZM", "删除文件: " + b + b1);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    Log.d("zm", "删除文件: " + e1.toString());
                }
            }
        })
                .subscribeOn(Schedulers.newThread()).subscribe();
    }


    /**
     * @param isLocal 本地激活判断
     * @param context
     * @param decrypt key
     * @return
     */
    public static ActivationResult getActivationResultString(final Boolean isLocal, final Context context, String decrypt) {
        ActivationResult activationResult = null;
        if (isLocal) {
            activationResult = ActivationManager.activate(context, decrypt);
            System.out.println("激活" + context.toString());
        } else {
            byte[] frameBuffer = new byte[3980];
            try {
                AssetManager assetManager = context.getResources().getAssets();
                InputStream inputStream = null;
                try {
                    inputStream = assetManager.open("IdentityClient.bin");
                    if (inputStream != null) {
                        System.out.println("It worked!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int len = inputStream.read(frameBuffer);
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String localLicenseServerURL = "http://218.247.237.138:7070";
            activationResult = com.honeywell.license.ActivationManager.activate(context,
                    decrypt, localLicenseServerURL, frameBuffer);
            System.out.println("激活" + activationResult.toString());
        }
        String toString = activationResult.toString();
        if (toString.contains("SUCCESS")) {
            return activationResult;
        } else {
            activationResult = ActivationManager.deactivate(context, decrypt);
            System.out.println("退订" + context.toString());
        }
        return activationResult;
    }

    public static void closeCamera(Context context) {
        HSMDecoder hsmDecoder = HSMDecoder.getInstance(context);
        Camera camera = hsmDecoder.getCamera();
        camera.release();
        System.out.println("关闭相机");
        HSMDecoder.disposeInstance();
    }

}
