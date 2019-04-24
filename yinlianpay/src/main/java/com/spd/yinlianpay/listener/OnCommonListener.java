package com.spd.yinlianpay.listener;

import com.spd.yinlianpay.iso8583.Msg;

/**
 * Created by Tommy on 2015/12/3.
 */
public interface OnCommonListener {

    void onSuccess();

    void onProgress(String progress);

    void onError(int errorCode, String errorMsg);

    void onDataBack();
}
