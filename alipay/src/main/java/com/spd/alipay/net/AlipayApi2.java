package com.spd.alipay.net;


import com.spd.alipay.been.AlipayPublicKey;
import com.spd.alipay.been.AlipayUploadBeen;
import com.spd.alipay.been.PayUploadResult;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlipayApi2 {
    private AlipayService service;

    private AlipayApi2(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Url.ALIPAY_BASE_URL2)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        service = retrofit.create(AlipayService.class);
    }

    private static class NetApiHolder {
        private static final AlipayApi2 INSTANCE = new AlipayApi2(getOkHttpClient());
    }

    /**
     * getInstance .
     *
     * @return NetApi
     */
    public static AlipayApi2 getInstance() {
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


    public Observable<PayUploadResult> alipayUpload(RequestBody body) {
        return service.alipayUpload(body);
    }

}
