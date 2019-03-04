package speedata.com.tianjin.utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by 张明_ on 2019/2/18.
 * Email 741183142@qq.com
 */
public class HttpMethods {
    public static String BASE_URL = "http://scansdk.speedata.cn/sdklic/";
    public static final String NONCE = "ddfd";
    public static final String PRICE_ID = "32e55b4057ee40f58de68883c8bc1d1f";
    private static final Object LOCK = new Object();
    private static HttpMethods httpMethods;

    public static HttpMethods getInstance() {
        if (httpMethods == null) {
            synchronized (LOCK) {
                if (httpMethods == null) {
                    httpMethods = new HttpMethods();
                }
            }
        }
        return httpMethods;
    }


    /**
     * 设备注册
     *
     * @param sendData
     * @param observer
     */
//    public void authorization(String sendData, Observer<AuthorizationBackData> observer) {
//        RequestBody requestBody = RequestBody.create(
//                MediaType.parse("application/json;charset=UTF-8"), sendData);
//        RetrofitCreateHelper.createApi(ApiService.class, BASE_URL)
//                .authorization(requestBody)
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(observer);
//    }
}
