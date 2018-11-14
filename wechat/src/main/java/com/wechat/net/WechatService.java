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

}
