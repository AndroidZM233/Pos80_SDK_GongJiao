package com.spd.bus.listener;

/**
 * Created by Administrator on 2018/1/22.
 */

public interface OnPinPadListener  {
    void onUpDate();
    void onCancel();
    void onByPass();
    void onSuccess(String pin);
    void onError(int errorCode,String errorMsg);
}
