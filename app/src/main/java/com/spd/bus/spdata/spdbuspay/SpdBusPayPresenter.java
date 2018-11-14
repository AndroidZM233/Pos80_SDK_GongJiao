package com.spd.bus.spdata.spdbuspay;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spd.alipay.AlipayJni;
import com.spd.alipay.Datautils;
import com.spd.alipay.been.AliCodeinfoData;
import com.spd.alipay.been.AlipayPublicKey;
import com.spd.alipay.been.AlipayUploadBeen;
import com.spd.alipay.been.PayUploadResult;
import com.spd.alipay.net.AlipayApi;
import com.spd.alipay.net.AlipayApi2;
import com.spd.bus.spdata.been.ErroCode;
import com.spd.bus.spdata.mvp.BasePresenterImpl;
import com.tencent.wlxsdk.WlxSdk;
import com.wechat.been.WechatPublicKey;
import com.wechat.net.WechatApi;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.spd.bus.spdata.been.ErroCode.ILLEGAL_PARAM;
import static com.spd.bus.spdata.been.ErroCode.NO_ENOUGH_MEMORY;
import static com.spd.bus.spdata.been.ErroCode.SYSTEM_ERROR;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SpdBusPayPresenter extends BasePresenterImpl<SpdBusPayContract.View> implements SpdBusPayContract.Presenter {
    private AlipayJni alipayJni;
    private WlxSdk wlxSdk;
    private String TAG = "PsamIcActivity";

    @Override
    public void getAliPublicKey() {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "".toString());
        AlipayApi.getInstance().getPublicKey(requestBody)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<AlipayPublicKey>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AlipayPublicKey alipayPublickKey) {
                        mView.getAliPublicKey(alipayPublickKey);
                        mView.success("获取支付宝KEY成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.erro(e.toString());
                        Log.i("PsamIcActivity", "onError:  获取 錯誤 " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void aliPayInit(List<AlipayPublicKey.PublicKeyListBean> publicKeyListBeans) {
        alipayJni = new AlipayJni();
        int result = alipayJni.initAliDev(publicKeyListBeans);
        mView.aliPayInit(result);
    }

    @Override
    public void checkAliQrCode(String code, String recordId, String posId, String posMfId, String posSwVersion, String merchantType, String currency, int amount, String vehicleId, String plateNo, String driverId, String lineInfo, String stationNo, String lbsInfo, String recordType) {
        AliCodeinfoData aliCodeinfoData = new AliCodeinfoData();
        aliCodeinfoData = alipayJni.checkAliQrCode(aliCodeinfoData, code, recordId,
                posId, posMfId, posSwVersion,
                merchantType, currency, amount,
                vehicleId, plateNo, driverId,
                lineInfo, stationNo, lbsInfo,
                recordType);
        mView.checkAliQrCode(aliCodeinfoData);
    }

    @Override
    public void uploadAlipay(AlipayUploadBeen alipayUploadBeen) {
        final Gson gson = new GsonBuilder().serializeNulls().create();
        String rusultData = gson.toJson(alipayUploadBeen);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), rusultData);
        AlipayApi2.getInstance().alipayUpload(requestBody)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Observer<PayUploadResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PayUploadResult payUploadResult) {
                        Log.i(TAG, "onNext: " + payUploadResult.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "上传结果onError :" + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void releseAlipayJni() {
        int re = alipayJni.release();
        mView.releseAlipayJni(re);
    }


    @Override
    public void getWechatPublicKey() {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "".toString());
        WechatApi.getInstance().getPublicKey(requestBody)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<WechatPublicKey>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WechatPublicKey wechatPublicKey) {
                        mView.getWechatPublicKey(wechatPublicKey);
                        mView.success("微信获取key成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.erro(e.toString());
                        Log.i(TAG, "onError:  微信获取key錯誤：： " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void wechatInit() {
        wlxSdk = new WlxSdk();

    }

    /**
     *
     * @param code
     * @param pbKeyList
     * @param macKeyList
     * @param amount
     * @param posId
     */

    /**
     * @param code
     * @param pbKeyList
     * @param macKeyList
     * @param payfee     单位分
     * @param scene      一次扫码计费
     * @param scantype   一次性扫码计费 scan_type=1
     * @param posId      机具流水号
     * @param posTrxId
     */
    @Override
    public void checkWechatQrCode(String code, List<WechatPublicKey.PubKeyListBean> pbKeyList, List<WechatPublicKey.MacKeyListBean> macKeyList, int payfee, byte scene, byte scantype, String posId, String posTrxId) {
        int result = 0;
        result = wlxSdk.init(code);
        if (result != ErroCode.EC_SUCCESS) {
            mView.checkWechatQrCode(result, "", "");
            return;
        }
        Log.i(TAG, "key_id:" + wlxSdk.get_key_id());
        Log.i(TAG, "mac_root_id:" + wlxSdk.get_mac_root_id());
        Log.i(TAG, "opne_id:" + wlxSdk.get_open_id());
        Log.i(TAG, "biz_data:" + wlxSdk.get_biz_data_hex());
        String openId = wlxSdk.get_open_id();
        String pubKey = "";
        String aesMacRoot = "";
        for (int i = 0; i < pbKeyList.size(); i++) {
            if (wlxSdk.get_key_id() == pbKeyList.get(i).getKey_id()) {
                pubKey = pbKeyList.get(i).getPub_key();
                break;
            }
        }
        for (int i = 0; i < macKeyList.size(); i++) {
            if (String.valueOf(wlxSdk.get_mac_root_id()).equals(macKeyList.get(i).getKey_id())) {
                aesMacRoot = macKeyList.get(i).getMac_key();
                break;
            }
        }
        result = wlxSdk.verify(openId, pubKey, payfee, scene, scantype, posId, posTrxId, aesMacRoot);
        if (result != ErroCode.EC_SUCCESS) {
            mView.checkWechatQrCode(result, "", "");
        }
        String record = wlxSdk.get_record();
        mView.checkWechatQrCode(result, record, openId);
        String resInfo = "二维码:" + code + "\r\nkey_id:" + wlxSdk.get_key_id() + "\r\nmac_root_id:" + wlxSdk.get_mac_root_id() + "\r\nopne_id:" + openId + "\r\nbiz_data:" + wlxSdk.get_biz_data_hex() + "\r\npub_Key:" + pubKey + "\r\nmac_key:" + aesMacRoot + "\r\n验码结果:" + result + "\r\n扫码记录:" + record;
        Log.i(TAG, "验码记录:" + resInfo);
        Log.i(TAG, "验码结果:" + result + "$$$$" + record);
    }
}
