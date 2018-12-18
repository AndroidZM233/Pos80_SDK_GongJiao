package com.spd.bus.spdata.spdbuspay;

import android.content.Context;
import android.util.Log;

import com.bluering.pos.sdk.qr.QrCodeInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spd.alipay.AlipayJni;
import com.spd.alipay.been.AliCodeinfoData;
import com.spd.base.beenali.AlipayQrcodekey;
import com.spd.base.beenbosi.BosiQrcodeKey;
import com.spd.base.beenres.QrcodeUploadResult;
import com.spd.base.beenupload.QrcodeUpload;
import com.spd.base.beenwechat.WechatQrcodeKey;
import com.spd.base.net.QrcodeApi;
import com.spd.bosi.BosiQrManage;
import com.spd.bus.spdata.been.ErroCode;
import com.spd.bus.spdata.mvp.BasePresenterImpl;
import com.tencent.wlxsdk.WlxSdk;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SpdBusPayPresenter extends BasePresenterImpl<SpdBusPayContract.View> implements SpdBusPayContract.Presenter {
    private AlipayJni alipayJni;
    private WlxSdk wlxSdk;
    //    private BosiPayJni bosiPayJni;
    private String TAG = "PsamIcActivity";


    //===============支付宝二维码==============

    @Override
    public void getAliPubKey() {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "".toString());
        QrcodeApi.getInstance().getAlipayPubKey(requestBody)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<AlipayQrcodekey>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AlipayQrcodekey aliQrcodekey) {
                        mView.showAliPublicKey(aliQrcodekey);
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
    public void aliPayInitJni(List<AlipayQrcodekey.PublicKeyListBean> publicKeyListBeans) {
        alipayJni = new AlipayJni();
        int result = alipayJni.initAliDev(publicKeyListBeans);
        Log.e(TAG, "showAliPayInit: " + mView);
        mView.showAliPayInit(result);
    }

    @Override
    public void checkAliQrCode(String code, String recordId, String posId, String posMfId, String posSwVersion, String merchantType, String currency, int amount, String vehicleId, String plateNo, String driverId, String lineInfo, String stationNo, String lbsInfo, String recordType) {
        Log.e(TAG, "mView11111: " + mView);
        AliCodeinfoData aliCodeinfoData = new AliCodeinfoData();
        aliCodeinfoData = alipayJni.checkAliQrCode(aliCodeinfoData, code, recordId,
                posId, posMfId, posSwVersion,
                merchantType, currency, amount,
                vehicleId, plateNo, driverId,
                lineInfo, stationNo, lbsInfo,
                recordType);
        Log.e(TAG, "mView: " + mView);
        mView.showCheckAliQrCode(aliCodeinfoData);
    }

    @Override
    public void uploadAlipayRe(QrcodeUpload qrcodeUpload) {
        final Gson gson = new GsonBuilder().serializeNulls().create();
        String rusultData = gson.toJson(qrcodeUpload);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), rusultData);
        QrcodeApi.getInstance().alipayUpload(requestBody)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Observer<QrcodeUploadResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(QrcodeUploadResult payUploadResult) {
                        Log.i(TAG, "onNext: " + payUploadResult.toString());
                        mView.success("支付宝上传成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "上传结果onError :" + e.toString());
                        mView.erro(e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void releseAlipayJni() {
        if (alipayJni != null) {
            int re = alipayJni.release();
        }
//        mView.showReleseAlipayJni(re);
    }
//===============腾讯（微信）二维码==============

    @Override
    public void getWechatPublicKey() {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "".toString());
        QrcodeApi.getInstance().getWechatPubKey(requestBody)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<WechatQrcodeKey>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WechatQrcodeKey wechatQrcodeKey) {
                        mView.showWechatPublicKey(wechatQrcodeKey);
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
    public void wechatInitJin() {
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
    public void checkWechatQrCode(String code, List<WechatQrcodeKey.PubKeyListBean> pbKeyList, List<WechatQrcodeKey.MacKeyListBean> macKeyList, int payfee, byte scene, byte scantype, String posId, String posTrxId) {
        int result = 0;
        result = wlxSdk.init(code);
        if (result != ErroCode.EC_SUCCESS) {
            mView.showCheckWechatQrCode(result, "", "");
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
            mView.showCheckWechatQrCode(result, "", "");
        }
        String record = wlxSdk.get_record();
        mView.showCheckWechatQrCode(result, record, openId);
        String resInfo = "二维码:" + code + "\r\nkey_id:" + wlxSdk.get_key_id() + "\r\nmac_root_id:" + wlxSdk.get_mac_root_id() + "\r\nopne_id:" + openId + "\r\nbiz_data:" + wlxSdk.get_biz_data_hex() + "\r\npub_Key:" + pubKey + "\r\nmac_key:" + aesMacRoot + "\r\n验码结果:" + result + "\r\n扫码记录:" + record;
        Log.i(TAG, "验码记录:" + resInfo);
        Log.i(TAG, "验码结果:" + result + "$$$$" + record);
    }

    //=============博思二维码============

    @Override
    public void bosiInitJin(Context context, String filePath) {
        //"/storage/sdcard0/bosicer/"
        BosiQrManage.bosiQrInit(context, filePath);
    }


    @Override
    public void getBosikey() {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "".toString());
        QrcodeApi.getInstance().getBosiPubKey(requestBody)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<BosiQrcodeKey>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BosiQrcodeKey bosiQrcodeKey) {
                        mView.showBosikey(bosiQrcodeKey);
                        mView.success("获取博思KEY成功");
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
    public int getBosiCerVersion() {
        return BosiQrManage.bosiQrQueryCertVer();
    }

    @Override
    public int updataBosiKey(String cer) {
        return Integer.parseInt(BosiQrManage.bosiQrUpdateCert(cer));
    }

    @Override
    public void checkBosiQrCode(String qrcode) {
        QrCodeInfo qrCodeInfo = BosiQrManage.bosiQrVerifyCode(qrcode);
    }
}
