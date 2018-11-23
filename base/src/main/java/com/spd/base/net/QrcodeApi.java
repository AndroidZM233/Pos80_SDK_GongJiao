package com.spd.base.net;


import com.spd.base.beenali.AlipayQrcodekey;
import com.spd.base.beenbosi.BosiQrcodeKey;
import com.spd.base.beenres.QrcodeUploadResult;
import com.spd.base.beenupload.QrcodeUpload;
import com.spd.base.beenwechat.WechatQrcodeKey;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class QrcodeApi {
    private QrcodeService service;

    private QrcodeApi(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(QrcodeUrl.ALIPAY_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        service = retrofit.create(QrcodeService.class);
    }

    private static class NetApiHolder {
        private static final QrcodeApi INSTANCE = new QrcodeApi(getOkHttpClient());
    }

    /**
     * getInstance .
     *
     * @return NetApi
     */
    public static QrcodeApi getInstance() {
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


    public Observable<AlipayQrcodekey> getAlipayPubKey(RequestBody body) {
        return service.getAlipayPubKey(body);
    }

    public Observable<WechatQrcodeKey> getWechatPubKey(RequestBody body) {
        return service.getWechatPubKey(body);
    }

    public Observable<BosiQrcodeKey> getBosiPubKey(RequestBody body) {

        return service.getBosiPubKey(body);
    }

    public Observable<QrcodeUploadResult> alipayUpload(RequestBody body) {
        return service.alipayResUpload(body);
    }

}
