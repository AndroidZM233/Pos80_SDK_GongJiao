package com.example.test.yinlianbarcode.net;


import com.example.test.yinlianbarcode.entity.ItineraryBackEntity;
import com.example.test.yinlianbarcode.entity.PubKeyEntity;
import com.example.test.yinlianbarcode.entity.SyncDataBackEntity;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author :Reginer in  2017/9/7 23:19.
 * 联系方式:QQ:282921012
 * 功能描述:请求
 */
public class NetApi {
    private NetApiService service;

    private NetApi(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Urls.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        service = retrofit.create(NetApiService.class);
    }

    private static class NetApiHolder {
        private static final NetApi INSTANCE = new NetApi(getOkHttpClient());
    }

    /**
     * getInstance .
     *
     * @return NetApi
     */
    public static NetApi getInstance() {
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


    public Observable<PubKeyEntity> getPubKey(Map<String, String> headers, RequestBody body) {
        return service.getPubKey(headers, body);
    }

    public Observable<ResponseBody> getMobileMark(Map<String, String> headers, RequestBody body) {
        return service.getMobileMark(headers, body);
    }

    public Observable<ResponseBody> uploadQr(Map<String, String> headers, RequestBody body) {
        return service.uploadQr(headers, body);
    }

    public Observable<ItineraryBackEntity> itinerary(Map<String, String> headers, RequestBody body) {
        return service.itinerary(headers, body);
    }

    public Observable<SyncDataBackEntity> syncData(Map<String, String> headers, RequestBody body) {
        return service.syncData(headers, body);
    }
}
