package com.spd.yinlianpay.listener;

/**
 * Created by Tommy on 2015/12/3.
 */
public interface OnCommonListener {

    void onSuccess();

    void onProgress(String progress);

    void onError(int errorCode, String errorMsg);
}
