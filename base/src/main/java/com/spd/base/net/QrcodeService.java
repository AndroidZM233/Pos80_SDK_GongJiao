package com.spd.base.net;

import com.spd.base.beenali.AlipayQrcodekey;
import com.spd.base.beenbosi.BosiQrcodeKey;
import com.spd.base.beenres.QrcodeUploadResult;
import com.spd.base.beenupload.QrcodeUpload;
import com.spd.base.beenwechat.WechatQrcodeKey;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface QrcodeService {
    /**
     * 获取支付宝秘钥
     *
     * @return
     */
    @POST(QrcodeUrl.PUB_ALI_KEY)
    Observable<AlipayQrcodekey> getAlipayPubKey(@Body RequestBody requestBody);

    /**
     * 获取腾讯（微信）秘钥
     *
     * @return
     */
    @POST(QrcodeUrl.PUB_WECHAT_KEY)
    Observable<WechatQrcodeKey> getWechatPubKey(@Body RequestBody requestBody);

    /**
     * 获取博思秘钥
     *
     * @return
     */
    @POST(QrcodeUrl.PUB_BOSI_KEY)
    Observable<BosiQrcodeKey> getBosiPubKey(@Body RequestBody requestBody);


    /**
     * 上传支付宝二维码结果
     *
     * @param requestBody
     * @return
     */
    @POST(QrcodeUrl.UPLOAD_ALI)
    Observable<QrcodeUploadResult> alipayResUpload(@Body RequestBody requestBody);


    /**
     * 上传腾讯（微信）二维码结果
     *
     * @param requestBody
     * @return
     */
    @POST(QrcodeUrl.UPLOAD_WECHAT)
    Observable<QrcodeUploadResult> wechartResUpload(@Body RequestBody requestBody);


    /**
     * 上传博思二维码结果
     *
     * @param requestBody
     * @return
     */
    @POST(QrcodeUrl.UPLOAD_BOSI)
    Observable<QrcodeUploadResult> bosiResUpload(@Body RequestBody requestBody);

}
