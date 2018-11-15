package com.spd.bosipay.net;


import com.spd.bosipay.been.BosipayPublicKey;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BosiipayService {
    /**
     * 获取公钥
     *
     * @return
     */
    @POST(Url.PUB_KEY)
    Observable<BosipayPublicKey> getPublicKey(@Body RequestBody requestBody);

    /**
     * 上传结果
     *
     * @param requestBody
     * @return
     */
    @POST("/")
    Observable<BosipayPublicKey> alipayUpload(@Body RequestBody requestBody);

}
