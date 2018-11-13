package com.example.test.yinlianbarcode;

import android.app.Application;
import android.content.Intent;

import com.example.test.yinlianbarcode.entity.PubKey;
import com.example.test.yinlianbarcode.entity.PubKeyEntity;
import com.example.test.yinlianbarcode.net.NetApi;
import com.example.test.yinlianbarcode.utils.Logcat;
import com.example.test.yinlianbarcode.utils.SdkTool;
import com.google.gson.Gson;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.test.yinlianbarcode.utils.SdkTool.OP_ID;
import static com.example.test.yinlianbarcode.utils.SdkTool.TERMINAL_NO;


/**
 * @author :Reginer in  2018/7/26 12:20.
 *         联系方式:QQ:282921012
 *         功能描述:
 */
public class AppYinLian extends Application {
    private static AppYinLian sInstance;
    private PubKeyEntity pubKeyEntity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Logcat.init(this);

        getPubKey();
//        getMobileMark();
    }

    public static AppYinLian getInstance() {
        return sInstance;
    }


    private void getPubKey() {
        PubKey pubKey = new PubKey(TERMINAL_NO, OP_ID);
        String message = pubKey.toString();
        NetApi.getInstance().getPubKey(SdkTool.getHeader(message), SdkTool.getPubKeyBody()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<PubKeyEntity>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(PubKeyEntity pubKeyEntity) {
                setPubKeyEntity(pubKeyEntity);
                Logcat.d(new Gson().toJson(pubKeyEntity));
            }

            @Override
            public void onError(Throwable e) {
                Logcat.e(e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void getMobileMark() {
//        NetApi.getInstance().getMobileMark(SdkTool.getHeader(), SdkTool.getPubKeyBody()).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBody>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(ResponseBody response) {
//                try {
//                    String result = response.string();
//                    Logcat.d(result);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Logcat.e(e);
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
    }

    public PubKeyEntity getPubKeyEntity() {
        return pubKeyEntity;
    }

    public void setPubKeyEntity(PubKeyEntity pubKeyEntity) {
        this.pubKeyEntity = pubKeyEntity;
    }
}
