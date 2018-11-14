package com.spd.alipay.net;


import com.spd.alipay.been.AlipayPublicKey;
import com.spd.alipay.been.AlipayUploadBeen;
import com.spd.alipay.been.PayUploadResult;

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
     * 上传结果
     *
     * @param requestBody
     * @return
     */
    @POST("/")
    Observable<PayUploadResult> alipayUpload(@Body RequestBody requestBody);

}
