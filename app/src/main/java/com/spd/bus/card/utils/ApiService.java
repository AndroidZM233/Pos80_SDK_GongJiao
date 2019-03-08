package com.spd.bus.card.utils;

import com.spd.bus.card.methods.bean.AppSercetBackBean;
import com.spd.bus.card.methods.bean.AliWhiteBlackBackBean;
import com.spd.bus.card.methods.bean.BaseInfoBackBean;
import com.spd.bus.card.methods.bean.GetMacBackBean;
import com.spd.bus.card.methods.bean.GetPublicBackBean;
import com.spd.bus.card.methods.bean.NetBackBean;
import com.spd.bus.card.methods.bean.PosInfoBackBean;
import com.spd.bus.card.methods.bean.PosKeysBackBean;
import com.spd.bus.card.methods.bean.UnqrkeyBackBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

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

    //银联二维码秘钥
    @POST("pos/unqrkey")
    Observable<UnqrkeyBackBean> unqrkey(@Body RequestBody route);

    //银联双免POSKEY
    @POST("pos/posKeys?data=17510418")
    Observable<PosKeysBackBean> posKeys(@Body RequestBody route);

    //微信秘钥
    @POST("pos/wxPay/getPublic")
    Observable<GetPublicBackBean> getPublic(@Body RequestBody route);

    //微信MAC
    @POST("pos/wxPay/getMac")
    Observable<GetMacBackBean> getMac(@Body RequestBody route);

    //支付宝黑名单
    @POST("pos/black")
    Observable<AliWhiteBlackBackBean> black(@Body RequestBody route);

    //支付宝白名单
    @POST("pos/white")
    Observable<AliWhiteBlackBackBean> white(@Body RequestBody route);

    //车载机心跳 10分钟一次
    @POST("pos/baseinfo")
    Observable<BaseInfoBackBean> baseinfo(@Body RequestBody route);


    //车载机程序下载
    @Streaming
    @GET("pos/download")
    Observable<BaseInfoBackBean> download(@Body RequestBody route);
}
