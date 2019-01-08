package speedata.com.face;

import com.spd.base.net.LogInterceptor;
import com.spd.base.net.QrcodeUrl;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import speedata.com.face.bean.ResponseData;

/**
 * //                            _ooOoo_
 * //                           o8888888o
 * //                           88" . "88
 * //                           (| -_- |)
 * //                            O\ = /O
 * //                        ____/`---'\____
 * //                      .   ' \\| |// `.
 * //                       / \\||| : |||// \
 * //                     / _||||| -:- |||||- \
 * //                       | | \\\ - /// | |
 * //                     | \_| ''\---/'' | |
 * //                      \ .-\__ `-` ___/-. /
 * //                   ___`. .' /--.--\ `. . __
 * //                ."" '< `.___\_<|>_/___.' >'"".
 * //               | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * //                 \ \ `-. \_ __\ /__ _/ .-` / /
 * //         ======`-.____`-.___\_____/___.-`____.-'======
 * //                            `=---='
 * //
 * //         .............................................
 * //                  佛祖镇楼                  BUG辟易
 *
 * @author :EchoXBR in  2018/12/25 下午5:13.
 * 功能描述:TODO
 */
public class FaceApi {
    private IFaceApi service;

    private FaceApi(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.mskaikai.com:8081/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        service = retrofit.create(IFaceApi.class);
    }

    private static class NetApiHolder {
        private static final FaceApi INSTANCE = new FaceApi(getOkHttpClient());
    }

    /**
     * getInstance .
     *
     * @return NetApi
     */
    public static FaceApi getInstance() {
        return FaceApi.NetApiHolder.INSTANCE;
    }

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new LogInterceptor())
                .retryOnConnectionFailure(true);
        return builder.build();
    }

//    public Observable<String> addTrade(HashMap<String ,Object> data){
//        return service.addTrade(data);
//    }

    Observable<ResponseData> addTrade(String data){
        return service.addTrade(data);
    }

}
