package com.wechat.net;


import com.wechat.been.WechatPublicKey;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WechatApi {
    private WechatService service;

    private WechatApi(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Url.WECHATS_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        service = retrofit.create(WechatService.class);
    }

    private static class NetApiHolder {
        private static final WechatApi INSTANCE = new WechatApi(getOkHttpClient());
    }

    /**
     * getInstance .
     *
     * @return NetApi
     */
    public static WechatApi getInstance() {
        return NetApiHolder.INSTANCE;
    }

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new LogInterceptor())
                .retryOnConnectionFailure(true);
        return builder.build();
    }


    public Observable<WechatPublicKey> getPublicKey(RequestBody body) {
        return service.getPublicKey(body);
    }

}
