package com.example.test.yinlianbarcode.interfaces;

import android.support.annotation.Keep;

/**
 * Created by 张明_ on 2018/1/15.
 * Email 741183142@qq.com
 * 注册回调
 */
@Keep
public interface OnBackListener {

    void onBack();

    void onError(Throwable e);
}
