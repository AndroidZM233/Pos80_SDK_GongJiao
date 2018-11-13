package com.wechat.net;


import com.wechat.been.WechatPublicKey;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WechatService {
    /**
     * 获取公钥
     *
     * @return
     */
    @POST(Url.PUB_KEY)
    Observable<WechatPublicKey> getPublicKey(@Body RequestBody requestBody);

    /**
     * 获取私钥
     *
     * @param requestBody
     * @return
     */
    @POST(Url.PUB_KEY)
    Observable<WechatPublicKey> getPrivateKey(@Body RequestBody requestBody);

//    /**
//     * 获取公钥
//     *
//     * @param headers -
//     * @param body    -
//     * @return -
//     */
//    @POST(Url.PUB_KEY)
//    Observable<PubKeyEntity> getPubKey(@HeaderMap Map<String, String> headers, @Body RequestBody body);
}
