package com.spd.bus.card.utils;

import com.spd.bus.card.methods.bean.AliWhiteBlackBackBean;
import com.spd.bus.card.methods.bean.AppSercetBackBean;
import com.spd.bus.card.methods.bean.BaseInfoBackBean;
import com.spd.bus.card.methods.bean.GetMacBackBean;
import com.spd.bus.card.methods.bean.GetPublicBackBean;
import com.spd.bus.card.methods.bean.NetBackBean;
import com.spd.bus.card.methods.bean.PosInfoBackBean;
import com.spd.bus.card.methods.bean.PosKeysBackBean;
import com.spd.bus.card.methods.bean.UnqrkeyBackBean;

import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.http.FieldMap;

/**
 * Created by 张明_ on 2019/2/18.
 * Email 741183142@qq.com
 */
public class HttpMethods {
    public static String BASE_URL = "http://42.81.133.17:18056/";
    public static String PRODUCE_URL = "http://42.81.133.17:18058/";
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
     * 数据回收
     *
     * @param sendData
     * @param observer
     */
    public void produce(String sendData, Observer<NetBackBean> observer) {
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"), sendData);
        RetrofitCreateHelper.createApi(ApiService.class, BASE_URL)
                .produce(requestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 支付宝 appSercet
     *
     * @param sendData
     * @param observer
     */
    public void appSercet(Map<String, String> params, Observer<AppSercetBackBean> observer) {
        RetrofitCreateHelper.createApi(ApiService.class, BASE_URL)
                .appSercet(params)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 支付宝POSINFO
     *
     * @param sendData
     * @param observer
     */
    public void posInfo(Map<String, String> params, Observer<PosInfoBackBean> observer) {
        RetrofitCreateHelper.createApi(ApiService.class, BASE_URL)
                .posInfo(params)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 银联二维码秘钥
     *
     * @param sendData
     * @param observer
     */
    public void unqrkey(String sendData, Observer<UnqrkeyBackBean> observer) {
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"), sendData);
        RetrofitCreateHelper.createApi(ApiService.class, BASE_URL)
                .unqrkey(requestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 银联双免POSKEY
     *
     * @param sendData
     * @param observer
     */
    public void posKeys(String sendData, Observer<PosKeysBackBean> observer) {
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"), sendData);
        RetrofitCreateHelper.createApi(ApiService.class, BASE_URL)
                .posKeys(requestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 微信秘钥
     *
     * @param sendData
     * @param observer
     */
    public void getPublic(String sendData, Observer<GetPublicBackBean> observer) {
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"), sendData);
        RetrofitCreateHelper.createApi(ApiService.class, BASE_URL)
                .getPublic(requestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 微信MAC
     *
     * @param sendData
     * @param observer
     */
    public void getMac(String sendData, Observer<GetMacBackBean> observer) {
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"), sendData);
        RetrofitCreateHelper.createApi(ApiService.class, BASE_URL)
                .getMac(requestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 支付宝黑名单
     *
     * @param sendData
     * @param observer
     */
    public void black(String sendData, Observer<AliWhiteBlackBackBean> observer) {
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"), sendData);
        RetrofitCreateHelper.createApi(ApiService.class, BASE_URL)
                .black(requestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 支付宝白名单
     *
     * @param sendData
     * @param observer
     */
    public void white(String sendData, Observer<AliWhiteBlackBackBean> observer) {
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"), sendData);
        RetrofitCreateHelper.createApi(ApiService.class, BASE_URL)
                .white(requestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    /**
     * 车载机心跳
     *
     * @param sendData
     * @param observer
     */
    public void baseinfo(String sendData, Observer<BaseInfoBackBean> observer) {
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"), sendData);
        RetrofitCreateHelper.createApi(ApiService.class, BASE_URL)
                .baseinfo(requestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
