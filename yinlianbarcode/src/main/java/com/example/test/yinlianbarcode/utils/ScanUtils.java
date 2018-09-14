package com.example.test.yinlianbarcode.utils;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import com.example.test.yinlianbarcode.interfaces.OnBackListener;
import com.honeywell.barcode.HSMDecoder;
import com.honeywell.license.ActivationManager;
import com.honeywell.license.ActivationResult;

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

    /**
     * 激活扫描
     */
    public static void activateScan(final Context context, final OnBackListener onBackListener) {
//        final String key = "37837B0642FE2D76714D44A02B7B916FD0DADE468C4DE621622D6EEDE3DBF406";
//        Log.d("ZM", "activateScan: " + key);
//        String decrypt = null;
//        try {
//            decrypt = AESUtil.decrypt(key, "speedata_speedta");
//        } catch (Exception e) {
//            e.printStackTrace();
//            onBackListener.onError(new Throwable("Key decrypt: " + e.toString()));
//            return;
//        }

        final String finalDecrypt = "trial-speed-tjian-05232018";
        Observable.create(new ObservableOnSubscribe<ActivationResult>() {
            @Override
            public void subscribe(ObservableEmitter<ActivationResult> e) throws Exception {
                try {
                    ActivationResult toString = getActivationResultString(true, context, finalDecrypt);
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
     * 得到激活结果
     *
     * @param context
     * @param decrypt
     * @return
     */
    public static ActivationResult getActivationResultString(final Boolean booleans, final Context context, String decrypt) {
        ActivationResult activationResult = null;
        if (booleans) {
            activationResult = ActivationManager.activate(context, decrypt);
            System.out.println("激活" + context.toString());
        } else {
            ActivationResult jihuoResult = ActivationManager.activate(context, decrypt);
            System.out.println("激活" + jihuoResult.toString());
            String toString = jihuoResult.toString();
            if (!toString.contains("SUCCESS")) {
                return jihuoResult;
            } else {
                activationResult = ActivationManager.deactivate(context, decrypt);
                System.out.println("退订" + context.toString());
//                closeCamera(context);
            }
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
