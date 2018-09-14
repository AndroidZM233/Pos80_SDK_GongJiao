package com.example.test.yinlianbarcode.net;

import android.support.annotation.NonNull;


import com.example.test.yinlianbarcode.utils.Logcat;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * @author :Reginer in  2017/9/11 17:51.
 *         联系方式:QQ:282921012
 *         功能描述:日志
 */
public class LogInterceptor implements Interceptor {
    @SuppressWarnings("ConstantConditions")
    @Override
    public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Logcat.d("request:" + request.toString());
        okhttp3.Response response = chain.proceed(chain.request());
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        Logcat.d("response :" + content);
        return response.newBuilder().body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }
}

