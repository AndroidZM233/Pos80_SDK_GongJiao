package com.example.test.yinlianbarcode.net;

import com.example.test.yinlianbarcode.entity.ItineraryBackEntity;
import com.example.test.yinlianbarcode.entity.PubKeyEntity;
import com.example.test.yinlianbarcode.entity.SyncDataBackEntity;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * @author :Reginer in  2018/4/9 15:35.
 * 联系方式:QQ:282921012
 * 功能描述:网络请求接口
 */
public interface NetApiService {
    /**
     * 获取公钥
     *
     * @param headers -
     * @param body    -
     * @return -
     */
    @POST(Urls.PUB_KEY)
    Observable<PubKeyEntity> getPubKey(@HeaderMap Map<String, String> headers, @Body RequestBody body);


    /**
     * 授权移动应用机构编码下载
     *
     * @param headers -
     * @param body    -
     * @return -
     */
    @POST(Urls.MOBILE_MARK)
    Observable<ResponseBody> getMobileMark(@HeaderMap Map<String, String> headers, @Body RequestBody body);

    /**
     * 二维码数据上传
     *
     * @param headers -
     * @param body    -
     * @return -
     */
    @POST(Urls.QR_CODE_SCAN)
    Observable<ResponseBody> uploadQr(@HeaderMap Map<String, String> headers, @Body RequestBody body);
    /**
     * 行程扣款
     *
     * @param headers -
     * @param body    -
     * @return -
     */
    @POST(Urls.ITINERARY)
    Observable<ItineraryBackEntity> itinerary(@HeaderMap Map<String, String> headers, @Body RequestBody body);

    /**
     * 行程数据同步
     *
     * @param headers -
     * @param body    -
     * @return -
     */
    @POST(Urls.SYNC_DATA)
    Observable<SyncDataBackEntity> syncData(@HeaderMap Map<String, String> headers, @Body RequestBody body);
}
