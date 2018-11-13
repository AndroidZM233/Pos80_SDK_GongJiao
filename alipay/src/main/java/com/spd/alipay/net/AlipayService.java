package com.spd.alipay.net;


import com.spd.alipay.been.AlipayPublicKey;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AlipayService {
    /**
     * 获取公钥
     *
     * @return
     */
    @POST(Url.PUB_KEY)
    Observable<AlipayPublicKey> getPublicKey(@Body RequestBody requestBody);

    /**
     * 获取私钥
     *
     * @param requestBody
     * @return
     */
    @POST(Url.PUB_KEY)
    Observable<AlipayPublicKey> getPrivateKey(@Body RequestBody requestBody);

}
