package com.spd.bus.net;

import com.spd.base.been.tianjin.AliWhiteBackBean;
import com.spd.base.been.tianjin.AppSercetBackBean;
import com.spd.base.been.tianjin.AliBlackBackBean;
import com.spd.base.been.tianjin.BaseInfoBackBean;
import com.spd.base.been.tianjin.GetMacBackBean;
import com.spd.base.been.tianjin.GetPublicBackBean;
import com.spd.base.been.tianjin.GetZhiFuBaoKey;
import com.spd.base.been.tianjin.NetBackBean;
import com.spd.base.been.tianjin.PosInfoBackBean;
import com.spd.base.been.tianjin.PosKeysBackBean;
import com.spd.base.been.tianjin.UnqrkeyBackBean;
import com.spd.base.been.tianjin.YinLianBlackBack;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by 张明_ on 2019/2/18.
 * Email 741183142@qq.com
 */
public interface ApiService {
    //数据回收
    @POST("produce/produce")
    Observable<NetBackBean> produce(@Body RequestBody route);

    //支付宝 appSercet
    @FormUrlEncoded
    @POST("pos/appSercet")
    Observable<AppSercetBackBean> appSercet(@FieldMap Map<String, String> params);

    //支付宝POSINFO
    @FormUrlEncoded
    @POST("pos/posInfo")
    Observable<PosInfoBackBean> posInfo(@FieldMap Map<String, String> params);

    //支付宝秘钥
    @GET("pos/publicKey")
    Observable<GetZhiFuBaoKey> publicKey();


    //银联二维码秘钥
    @GET("pos/unqrkey")
    Observable<UnqrkeyBackBean> unqrkey();

    //银联双免POSKEY
    @GET
    Observable<PosKeysBackBean> posKeys(@Url String url);

    //微信秘钥
    @POST("pos/wxPay/getPublic")
    Observable<GetPublicBackBean> getPublic(@Body RequestBody route);

    //微信MAC
    @POST("pos/wxPay/getMac")
    Observable<GetMacBackBean> getMac(@Body RequestBody route);

    //黑名单
    @POST("pos/black")
    Observable<AliBlackBackBean> black(@Body RequestBody route);

    //银联黑名单
    @FormUrlEncoded
    @POST("pos/blankDownload")
    Observable<YinLianBlackBack> blankDownload(@FieldMap Map<String, String> params);

    //白名单
    @POST("pos/white")
    Observable<AliWhiteBackBean> white(@Body RequestBody route);

    //车载机心跳 10分钟一次
    @FormUrlEncoded
    @POST("pos/baseinfo")
    Observable<BaseInfoBackBean> baseinfo(@FieldMap Map<String, String> params);


    //车载机程序下载
    @GET
    Observable<ResponseBody> download(@Url String url);

    //错误日志上传
    @FormUrlEncoded
    @POST("log/postLog")
    Observable<ResponseBody> postLog(@FieldMap Map<String, String> params);

    //支付宝白名单
    @POST("log/syTime")
    Observable<ResponseBody> syTime(@Body RequestBody route);
}
