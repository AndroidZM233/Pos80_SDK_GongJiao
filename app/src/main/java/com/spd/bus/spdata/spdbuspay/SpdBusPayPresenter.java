package com.spd.bus.spdata.spdbuspay;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bluering.pos.sdk.qr.QrCodeInfo;
import com.example.test.yinlianbarcode.entity.QrEntity;
import com.example.test.yinlianbarcode.utils.Logcat;
import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.example.test.yinlianbarcode.utils.ValidationUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.spd.alipay.AlipayJni;
import com.spd.alipay.been.AliCodeinfoData;
import com.spd.alipay.been.TianjinAlipayRes;
import com.spd.base.been.AlipayQrcodekey;

import com.spd.base.been.BosiQrcodeKey;
import com.spd.base.been.tianjin.GetMacBackBean;
import com.spd.base.been.tianjin.GetPublicBackBean;
import com.spd.base.been.tianjin.GetZhiFuBaoKey;
import com.spd.base.been.tianjin.KeysBean;
import com.spd.base.been.tianjin.UnqrkeyBackBean;
import com.spd.base.been.tianjin.ZhiFuBaoPubKey;
import com.spd.base.been.tianjin.produce.weixin.PayinfoBean;
import com.spd.base.been.tianjin.produce.weixin.ProduceWeiXin;
import com.spd.base.been.tianjin.produce.weixin.UploadInfoDB;
import com.spd.base.been.tianjin.produce.weixin.UploadInfoDBDao;
import com.spd.base.been.tianjin.produce.yinlian.ProduceYinLian;
import com.spd.base.been.tianjin.produce.yinlian.UploadInfoYinLianDB;
import com.spd.base.been.tianjin.produce.yinlian.UploadInfoYinLianDBDao;
import com.spd.base.been.tianjin.produce.zhifubao.ProduceZhiFuBao;
import com.spd.base.been.tianjin.produce.zhifubao.ReqDataBean;
import com.spd.base.been.tianjin.produce.zhifubao.UploadInfoZFBDB;
import com.spd.base.been.tianjin.produce.zhifubao.UploadInfoZFBDBDao;
import com.spd.base.beenresult.QrcodeUploadResult;
import com.spd.base.beenupload.AlipayQrCodeUpload;
import com.spd.base.beenupload.BosiQrCodeUpload;
import com.spd.base.been.WechatQrcodeKey;
import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.AlipayKeyDb;
import com.spd.base.dbbeen.AlipayKeyDbDao;
import com.spd.base.dbbeen.WeichatKeyDb;
import com.spd.base.dbbeen.WeichatKeyDbDao;
import com.spd.base.net.QrcodeApi;
import com.spd.base.utils.Datautils;
import com.spd.bosi.BosiQrManage;
import com.spd.bus.Info;
import com.spd.base.been.tianjin.AliWhiteBlackBackBean;
import com.spd.base.been.tianjin.AliWhiteBlackPost;
import com.spd.base.been.tianjin.AppSercetBackBean;
import com.spd.base.been.tianjin.NetBackBean;
import com.spd.base.been.tianjin.PosInfoBackBean;
import com.spd.base.been.tianjin.produce.ProducePost;
import com.spd.bus.card.utils.DateUtils;
import com.spd.bus.card.utils.HttpMethods;
import com.spd.bus.spdata.been.ErroCode;
import com.spd.bus.spdata.mvp.BasePresenterImpl;
import com.spd.bus.util.SaveDataUtils;
import com.tencent.wlxsdk.WlxSdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String TAG = "SPEEDATA_BUS";
    private List<UploadInfoDB> uploadInfoDBS;
    private List<UploadInfoZFBDB> uploadInfoZFBDBS;
    private List<UploadInfoYinLianDB> yinLianDBS;

    //===============支付宝二维码==============

    @Override
    public void produce() {
        ProducePost producePost = new ProducePost();

        Gson gson = new GsonBuilder().serializeNulls().create();
        String sendData = gson.toJson(producePost);
        HttpMethods.getInstance().produce(sendData, new Observer<NetBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NetBackBean netBackBean) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

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
                        AlipayKeyDbDao alipayKeyDbDao = DbDaoManage.getDaoSession().getAlipayKeyDbDao();
                        List<AlipayQrcodekey.PublicKeyListBean> publicKeyListBeans = aliQrcodekey.getPublicKeyList();
                        Collections.sort(publicKeyListBeans);
                        for (int i = 0; i < publicKeyListBeans.size(); i++) {
                            AlipayKeyDb alipayKeyDb = new AlipayKeyDb(aliQrcodekey.getVersion(), aliQrcodekey.getKeyType(), publicKeyListBeans.get(i).getKey_id() + "", publicKeyListBeans.get(i).getPub_key());
                            alipayKeyDbDao.insertOrReplace(alipayKeyDb);
                        }
                        mView.showAliPublicKey(0);
                        mView.success("获取支付宝KEY成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showAliPublicKey(1);
                        mView.erro(e.toString());
                        Log.i("PsamIcActivity", "onError:支付宝获取错误 " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getAliPubKeyTianJin() {
        HttpMethods.getInstance().publicKey(new Observer<GetZhiFuBaoKey>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(GetZhiFuBaoKey getZhiFuBaoKey) {
                String publicKeys = getZhiFuBaoKey.getPublicKeys();
                if (!TextUtils.isEmpty(publicKeys)) {
                    DbDaoManage.getDaoSession().getGetZhiFuBaoKeyDao().deleteAll();
                    DbDaoManage.getDaoSession().getGetZhiFuBaoKeyDao().insert(getZhiFuBaoKey);
                    mView.showAliPublicKey(0);
                    mView.success("获取支付宝KEY成功");
                }

            }

            @Override
            public void onError(Throwable e) {
                mView.erro(e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void aliPayInitJni() {

        List<GetZhiFuBaoKey> getZhiFuBaoKeys = DbDaoManage.getDaoSession()
                .getGetZhiFuBaoKeyDao().loadAll();
        if (getZhiFuBaoKeys.size() == 0) {
            return;
        }
        GetZhiFuBaoKey getZhiFuBaoKey = getZhiFuBaoKeys.get(0);
//        List<ZhiFuBaoPubKey> zhiFuBaoPubKeys = DbDaoManage.getDaoSession()
//                .getZhiFuBaoPubKeyDao().loadAll();
//        List<AlipayQrcodekey.PublicKeyListBean> publicKeyLists = new ArrayList<>();
//        if (zhiFuBaoPubKeys.size() > 0) {
//            for (int i = 0; i < zhiFuBaoPubKeys.size(); i++) {
//                AlipayQrcodekey.PublicKeyListBean keyListBean = new AlipayQrcodekey
//                        .PublicKeyListBean(zhiFuBaoPubKeys.get(i).getKey_id()
//                        , zhiFuBaoPubKeys.get(i).getPublic_key());
//                publicKeyLists.add(keyListBean);
//            }
//        }
        alipayJni = new AlipayJni();
        int result = alipayJni.initAliDev(getZhiFuBaoKey.getPublicKeys(), getZhiFuBaoKey.getCards());
        Log.e(TAG, "showAliPayInit: " + mView);
        mView.showAliPayInit(result);
    }

    /**
     * 调用天津后台接口AppSercet
     */
    @Override
    public void getZhiFuBaoAppSercet(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("data", "{\"deviceId\":\"17340086\"}");
        HttpMethods.getInstance().appSercet(map, new Observer<AppSercetBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AppSercetBackBean appSercetBackBean) {
                AppSercetBackBean.DataBean data = appSercetBackBean.getData();
                SharedXmlUtil.getInstance(context).write(Info.ZFB_APP_KEY, data.getAppKey());
                SharedXmlUtil.getInstance(context).write(Info.ZFB_APP_SERCET, data.getAppSercet());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 调用天津后台接口posInfo
     */
    @Override
    public void getZhiFuBaoPosInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("data", "{\"deviceId\":\"17340086\",\"appVersion\":\"20181127\"}");
        HttpMethods.getInstance().posInfo(map, new Observer<PosInfoBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(PosInfoBackBean posInfoBackBean) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 调用天津后台接口black 获取支付宝黑名单
     */
    @Override
    public void getZhiFuBaoBlack(Context context) {
        AliWhiteBlackPost aliWhiteBlackPost = new AliWhiteBlackPost();
        aliWhiteBlackPost.setPosid("17510803");
        aliWhiteBlackPost.setVersion("20161208");
        final Gson gson = new GsonBuilder().serializeNulls().create();
        String sendData = gson.toJson(aliWhiteBlackPost);
        HttpMethods.getInstance().black(sendData, new Observer<AliWhiteBlackBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AliWhiteBlackBackBean aliWhiteBlackBackBean) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 调用天津后台接口white  获取支付宝白名单
     */
    @Override
    public void getZhiFuBaoWhite(Context context) {
        AliWhiteBlackPost aliWhiteBlackPost = new AliWhiteBlackPost();
        aliWhiteBlackPost.setPosid("17510803");
        aliWhiteBlackPost.setVersion("20161208");
        final Gson gson = new GsonBuilder().serializeNulls().create();
        String sendData = gson.toJson(aliWhiteBlackPost);
        HttpMethods.getInstance().white(sendData, new Observer<AliWhiteBlackBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AliWhiteBlackBackBean aliWhiteBlackBackBean) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 验码
     *
     * @param code         扫描到的二维码
     * @param recordId
     * @param posId
     * @param posMfId
     * @param posSwVersion
     * @param merchantType
     * @param currency
     * @param amount
     * @param vehicleId
     * @param plateNo
     * @param driverId
     * @param lineInfo
     * @param stationNo
     * @param lbsInfo
     * @param recordType
     */
    @Override
    public void checkAliQrCode(String code, String recordId, String posId, String posMfId
            , String posSwVersion, String merchantType, String currency, int amount
            , String vehicleId, String plateNo, String driverId, String lineInfo
            , String stationNo, String lbsInfo, String recordType) {
        Log.e(TAG, "mView11111: " + mView);
//        AliCodeinfoData aliCodeinfoData = new AliCodeinfoData();
//        aliCodeinfoData = alipayJni.checkAliQrCode(aliCodeinfoData, code, recordId,
//                posId, posMfId, posSwVersion,
//                merchantType, currency, amount,
//                vehicleId, plateNo, driverId,
//                lineInfo, stationNo, lbsInfo,
//                recordType);
        TianjinAlipayRes tianjinAlipayRes = new TianjinAlipayRes();
        tianjinAlipayRes = alipayJni.checkAliQrCode(tianjinAlipayRes,
                code, "17430805", "1", 1, "SINGLE",
                "123123132");
        Log.i(TAG, "onHSMDecodeResult: " + tianjinAlipayRes.toString());

        if (tianjinAlipayRes.result != 1) {
            mView.erro("二维码检测失败");
            return;
        }
        String userId = tianjinAlipayRes.uid;
        //判断是否连刷
        List<UploadInfoZFBDB> checklist = DbDaoManage.getDaoSession().getUploadInfoZFBDBDao()
                .queryBuilder().where(UploadInfoZFBDBDao.Properties.UserId.eq(userId)).list();
        if (checklist.size() > 0) {
            UploadInfoZFBDB uploadInfoDB = checklist.get(checklist.size() - 1);
            String inStationTime = uploadInfoDB.getActualOrderTime();
            try {
                boolean brush = DateUtils.isBrush(inStationTime, 5);
                if (!brush) {
                    mView.erro("请不要连刷");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.e(TAG, "mView: " + mView);
        mView.showCheckAliQrCode(tianjinAlipayRes);
    }

    @Override
    public void uploadAlipayRe() {
        String aLiUploadData = getALiUploadData().replace("\\\"", "'");
        Log.i("SPEEDATA_BUS", "uploadBosiRe: json=====" + aLiUploadData);

        HttpMethods.getInstance().produce(aLiUploadData, new Observer<NetBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NetBackBean netBackBean) {
                String msg = netBackBean.getMsg();
                if ("success".equalsIgnoreCase(msg)) {
                    Log.i(TAG, "onNext: " + netBackBean.toString());
                    mView.success("支付宝上传成功");
                    for (UploadInfoZFBDB uploadInfoZFBDB : uploadInfoZFBDBS) {
                        uploadInfoZFBDB.setIsUpload(true);
                        DbDaoManage.getDaoSession().getUploadInfoZFBDBDao().update(uploadInfoZFBDB);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                mView.erro(e.toString());
                Log.i(TAG, "uploadWechatRe: " + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });


//        RequestBody requestBody = RequestBody.create(MediaType
//                .parse("application/json; charset=utf-8"), rusultData);
//        QrcodeApi.getInstance().alipayUpload(requestBody)
//                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
//                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
//                .subscribe(new Observer<QrcodeUploadResult>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(QrcodeUploadResult payUploadResult) {
//                        Log.i(TAG, "onNext: " + payUploadResult.toString());
//                        mView.success("支付宝上传成功");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "上传结果onError :" + e.toString());
//                        mView.erro(e.toString());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    private String getALiUploadData() {
        final Gson gson = new GsonBuilder().serializeNulls().create();
        //上传记录到天津后台
        ProducePost producePost = new ProducePost();
        producePost.setType("2200AQ");
        producePost.setRoute("11");
        producePost.setPosId("123");
        ProduceZhiFuBao produceZhiFuBao = new ProduceZhiFuBao();
        List<ReqDataBean> reqDataBeans = new ArrayList<>();
        uploadInfoZFBDBS = DbDaoManage.getDaoSession().getUploadInfoZFBDBDao()
                .queryBuilder().where(UploadInfoZFBDBDao.Properties.IsUpload.eq(false)).list();
        if (uploadInfoZFBDBS.size() != 0) {
            for (UploadInfoZFBDB uploadInfoDB : uploadInfoZFBDBS) {
                ReqDataBean reqDataBean = new ReqDataBean();
                // 交易流水
                reqDataBean.setOutTradeNo(uploadInfoDB.getOutTradeNo());
                // 设备号
                reqDataBean.setDeviceId(uploadInfoDB.getDeviceId());
                // 司机卡号
                reqDataBean.setDriverCardNo(uploadInfoDB.getDriverCardNo());
                // 卡类型
                reqDataBean.setCardType(uploadInfoDB.getCardType());
                // 用户号
                reqDataBean.setUserId(uploadInfoDB.getUserId());
                // 车辆号
                reqDataBean.setCarryCode(uploadInfoDB.getCarryCode());
                // 路队号
                reqDataBean.setBusGroupCode(uploadInfoDB.getBusGroupCode());
                // 公司号
                reqDataBean.setCompanyCode(uploadInfoDB.getCompanyCode());
                // 站点名称
                reqDataBean.setStationName(uploadInfoDB.getStationName());
                // 电子公交卡卡号
                reqDataBean.setCardId(uploadInfoDB.getCardId());
                // 售票员签到时间
                reqDataBean.setSellerSignTime(uploadInfoDB.getSellerSignTime());
                // 机具内扫码序号
                reqDataBean.setSeq(uploadInfoDB.getSeq());
                // 司机签到时间
                reqDataBean.setDriverSignTime(uploadInfoDB.getDriverSignTime());
                // 区域号
                reqDataBean.setAreaCode(uploadInfoDB.getAreaCode());
                // 票价
                reqDataBean.setPrice(uploadInfoDB.getPrice());
                // 站点号
                reqDataBean.setStationId(uploadInfoDB.getStationId());
                // 交易时间
                reqDataBean.setActualOrderTime(uploadInfoDB.getActualOrderTime());
                reqDataBean.setCardData(uploadInfoDB.getCardData());
                // 真实票价
                reqDataBean.setActualPrice(uploadInfoDB.getActualPrice());
                // 线路号
                reqDataBean.setLineCode(uploadInfoDB.getLineCode());
                // 记录内容
                reqDataBean.setRecord(uploadInfoDB.getRecord());
                reqDataBeans.add(reqDataBean);
            }
        }

        produceZhiFuBao.setReqData(reqDataBeans);
        produceZhiFuBao.setSecret("DB623AFEBF6B5CA8DC500449D0B59AA7");
        producePost.setData(gson.toJson(produceZhiFuBao));
        return gson.toJson(producePost).toString();
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
    public void wechatInitJin() {
        wlxSdk = new WlxSdk();
    }

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
                        WeichatKeyDb weichatKeyDb = null;
                        String key = "key";
                        int cont = 0;
                        WeichatKeyDbDao weichatKeyDbDao = DbDaoManage.getDaoSession().getWeichatKeyDbDao();
                        List<WechatQrcodeKey.MacKeyListBean> macKeyList = wechatQrcodeKey.getMacKeyList();
                        for (int i = 0; i < macKeyList.size(); i++) {
                            weichatKeyDb = new WeichatKeyDb(key + cont++, wechatQrcodeKey.getCurVersion(), wechatQrcodeKey.getKeyType(), macKeyList.get(i).getKey_id(), macKeyList.get(i).getMac_key(), "", "");
                            weichatKeyDbDao.insertOrReplace(weichatKeyDb);
                        }
                        List<WechatQrcodeKey.PubKeyListBean> pubKeyList = wechatQrcodeKey.getPubKeyList();
                        for (int i = 0; i < pubKeyList.size(); i++) {
                            weichatKeyDbDao.insertOrReplace(new WeichatKeyDb(key + cont++, wechatQrcodeKey.getCurVersion(), wechatQrcodeKey.getKeyType(), "", "", pubKeyList.get(i).getKey_id() + "", pubKeyList.get(i).getPub_key()));
                        }
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

    /**
     * 获取微信的秘钥
     */
    @Override
    public void getWechatPublicKeyTianJin() {
        HttpMethods.getInstance().getPublic("", new Observer<GetPublicBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(GetPublicBackBean getPublicBackBean) {
                DbDaoManage.getDaoSession().getGetPublicBackBeanDao().deleteAll();
                DbDaoManage.getDaoSession().getGetPublicBackBeanDao()
                        .insertOrReplace(getPublicBackBean);
                getWechatMacTianJin();
            }

            @Override
            public void onError(Throwable e) {
                mView.erro(e.toString());
                Log.d(TAG, "onError: " + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 获取微信mac
     */
    @Override
    public void getWechatMacTianJin() {
        HttpMethods.getInstance().getMac("", new Observer<GetMacBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(GetMacBackBean getMacBackBean) {
                DbDaoManage.getDaoSession().getGetMacBackBeanDao().deleteAll();
                DbDaoManage.getDaoSession().getGetMacBackBeanDao()
                        .insertOrReplace(getMacBackBean);
                wechatInitJin();
            }

            @Override
            public void onError(Throwable e) {
                mView.erro(e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void checkWechatTianJin(String code, int payfee, byte scene,
                                   byte scantype, String posId, String posTrxId) {
        int result = 0;
        result = wlxSdk.init(code);
        if (result != ErroCode.EC_SUCCESS) {
            mView.showCheckWechatQrCode(result, "", "");
            return;
        }

        String openId = wlxSdk.get_open_id();

        //判断是否连刷
        List<UploadInfoDB> checklist = DbDaoManage.getDaoSession().getUploadInfoDBDao()
                .queryBuilder().where(UploadInfoDBDao.Properties.Open_id.eq(openId)).list();
        if (checklist.size() > 0) {
            UploadInfoDB uploadInfoDB = checklist.get(checklist.size() - 1);
            String inStationTime = uploadInfoDB.getIn_station_time();
            try {
                boolean brush = DateUtils.isBrush(inStationTime, 5);
                if (!brush) {
                    mView.erro("请不要连刷");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String pubKey = "";
        String aesMacRoot = "";
        GetPublicBackBean publicBackBean = DbDaoManage.getDaoSession()
                .getGetPublicBackBeanDao().loadAll().get(0);
        String pubkeyList = publicBackBean.getPubkeyList();
        GetMacBackBean macBackBean = DbDaoManage.getDaoSession()
                .getGetMacBackBeanDao().loadAll().get(0);
        String mac = macBackBean.getMacKeyList();
        JsonArray asJsonArray = new JsonParser().parse(pubkeyList).getAsJsonArray();
        for (int i = 0; i < asJsonArray.size(); i++) {
            String num = String.valueOf(i + 1);
            if (String.valueOf(wlxSdk.get_key_id()).equals(num)) {
                JsonElement jsonElement = asJsonArray.get(i).getAsJsonObject().get(num);
                String keyStr = jsonElement.getAsString();
                pubKey = keyStr;
                continue;
            }

        }
        JsonArray jsonArray = new JsonParser().parse(mac).getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            String num = String.valueOf(i + 1);
            if (String.valueOf(wlxSdk.get_mac_root_id()).equals(num)) {
                String macStr = jsonArray.get(i).getAsJsonObject().get(num).getAsString();
                aesMacRoot = macStr;
                continue;
            }
        }

        result = wlxSdk.verify(openId, pubKey, payfee, scene, scantype, posId, posTrxId, aesMacRoot);
        if (result != ErroCode.EC_SUCCESS) {
            mView.showCheckWechatQrCode(result, wlxSdk.get_record(), "");
            return;
        }
        try {
            SaveDataUtils.saveWeiXinDataBean(wlxSdk);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "微信数据保存失败:" + e.toString());
        }
        String record = wlxSdk.get_record();
        mView.showCheckWechatQrCode(result, record, openId);
        String resInfo = "二维码:" + code + "\r\nkey_id:" + wlxSdk.get_key_id() + "\r\nmac_root_id:" + wlxSdk.get_mac_root_id() + "\r\nopne_id:" + openId + "\r\nbiz_data:" + wlxSdk.get_biz_data_hex() + "\r\npub_Key:" + pubKey + "\r\nmac_key:" + aesMacRoot + "\r\n验码结果:" + result + "\r\n扫码记录:" + record;
        Log.i(TAG, "验码记录:" + resInfo);
        Log.i(TAG, "验码结果:" + result + "$$$$" + record);

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
        String openId = wlxSdk.get_open_id();
        String pubKey = "";
        String aesMacRoot = "";
        List<WeichatKeyDb> weichatKeyDbs = DbDaoManage.getDaoSession().getWeichatKeyDbDao().loadAll();
        for (int i = 0; i < weichatKeyDbs.size(); i++) {
            if (String.valueOf(wlxSdk.get_key_id()).equals(weichatKeyDbs.get(i).getPubkeyId())) {
                pubKey = weichatKeyDbs.get(i).getPubKey();
                continue;
            }
            if (String.valueOf(wlxSdk.get_mac_root_id()).equals(weichatKeyDbs.get(i).getMackeyId())) {
                aesMacRoot = weichatKeyDbs.get(i).getMacKey();
                continue;
            }
        }


//        WeichatKeyDb weichatKeyDb = DbDaoManage.getDaoSession().getWeichatKeyDbDao().queryBuilder().where(WeichatKeyDbDao.Properties.PubkeyId.eq(String.valueOf(wlxSdk.get_key_id()))).build().unique();
//        pubKey = weichatKeyDb.getPubKey();
//        for (int i = 0; i < pbKeyList.size(); i++) {
//            if (wlxSdk.get_key_id() == pbKeyList.get(i).getKey_id()) {
//                pubKey = pbKeyList.get(i).getPub_key();
//                break;
//            }
//        }
//        WeichatKeyDb weichatKeyDb2 = DbDaoManage.getDaoSession().getWeichatKeyDbDao().queryBuilder().where(WeichatKeyDbDao.Properties.MackeyId.eq(wlxSdk.get_mac_root_id())).build().unique();
//        pubKey = weichatKeyDb2.getMacKey();
//        for (int i = 0; i < macKeyList.size(); i++) {
//            if (String.valueOf(wlxSdk.get_mac_root_id()).equals(macKeyList.get(i).getKey_id())) {
//                aesMacRoot = macKeyList.get(i).getMac_key();
//                break;
//            }
//        }
        result = wlxSdk.verify(openId, pubKey, payfee, scene, scantype, posId, posTrxId, aesMacRoot);
        if (result != ErroCode.EC_SUCCESS) {
            mView.showCheckWechatQrCode(result, wlxSdk.get_record(), "");
        }

        String record = wlxSdk.get_record();
        mView.showCheckWechatQrCode(result, record, openId);

        String resInfo = "二维码:" + code + "\r\nkey_id:" + wlxSdk.get_key_id() + "\r\nmac_root_id:" + wlxSdk.get_mac_root_id() + "\r\nopne_id:" + openId + "\r\nbiz_data:" + wlxSdk.get_biz_data_hex() + "\r\npub_Key:" + pubKey + "\r\nmac_key:" + aesMacRoot + "\r\n验码结果:" + result + "\r\n扫码记录:" + record;
        Log.i(TAG, "验码记录:" + resInfo);
        Log.i(TAG, "验码结果:" + result + "$$$$" + record);
    }

    @Override
    public void uploadWechatRe() {
        String weiXinUploadData = getWeiXinUploadData().replace("\\\"", "'");

        HttpMethods.getInstance().produce(weiXinUploadData, new Observer<NetBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NetBackBean netBackBean) {
                String msg = netBackBean.getMsg();
                if ("success".equalsIgnoreCase(msg)) {
                    Log.i(TAG, "onNext: " + netBackBean.toString());
                    mView.success("微信上传成功");
                    for (UploadInfoDB uploadInfoDB : uploadInfoDBS) {
                        uploadInfoDB.setIsUpload(true);
                        DbDaoManage.getDaoSession().getUploadInfoDBDao().update(uploadInfoDB);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                mView.erro(e.toString());
                Log.i(TAG, "uploadWechatRe: " + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });

//        final Gson gson = new GsonBuilder().serializeNulls().create();
//        String rusultData = gson.toJson(qrcodeUpload);
//        Log.i("SPEEDATA_BUS", "uploadBosiRe: json=====" + rusultData);
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), rusultData);
//        QrcodeApi.getInstance().weichatUpload(requestBody)
//                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
//                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
//                .subscribe(new Observer<QrcodeUploadResult>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(QrcodeUploadResult payUploadResult) {
//                        Log.i(TAG, "onNext: " + payUploadResult.toString());
//                        mView.success("微信上传成功");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "微信上传结果onError :" + e.toString());
//                        mView.erro(e.toString());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }


    private String getWeiXinUploadData() {
        final Gson gson = new GsonBuilder().serializeNulls().create();
        //上传记录到天津后台
        ProducePost producePost = new ProducePost();
        producePost.setType("2200WQ");
        producePost.setRoute("11");
        producePost.setPosId("123");
        ProduceWeiXin produceWeiXin = new ProduceWeiXin();
        List<PayinfoBean> weiXinPayinfo = new ArrayList<>();
        uploadInfoDBS = DbDaoManage.getDaoSession().getUploadInfoDBDao()
                .queryBuilder().where(UploadInfoDBDao.Properties.IsUpload.eq(false)).list();
        if (uploadInfoDBS.size() != 0) {
            for (UploadInfoDB uploadInfoDB : uploadInfoDBS) {
                PayinfoBean payinfoBean = new PayinfoBean();
                payinfoBean.setOpen_id(uploadInfoDB.getOpen_id());
                payinfoBean.setDriverSignTime(uploadInfoDB.getDriverSignTime());
                payinfoBean.setTeam(uploadInfoDB.getTeam());
                payinfoBean.setRoute(uploadInfoDB.getRoute());
                payinfoBean.setAccount(uploadInfoDB.getAccount());
                payinfoBean.setDept(uploadInfoDB.getDept());
                payinfoBean.setIn_station_time(uploadInfoDB.getIn_station_time());
                payinfoBean.setBus_no(uploadInfoDB.getBus_no());
                payinfoBean.setDriver(uploadInfoDB.getDriver());
                payinfoBean.setPos_id(uploadInfoDB.getPos_id());
                payinfoBean.setRecord_in(uploadInfoDB.getRecord_in());
                weiXinPayinfo.add(payinfoBean);
            }
        }

        produceWeiXin.setPayinfo(weiXinPayinfo);
        producePost.setData(gson.toJson(produceWeiXin));
        return gson.toJson(producePost).toString();
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
                        Log.i("PsamIcActivity", "onError:  获取错误 " + e.toString());
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
        mView.showCheckBosiQrCode(qrCodeInfo);
    }

    @Override
    public void uploadBosiRe(BosiQrCodeUpload qrcodeUpload) {
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String rusultData = gson.toJson(qrcodeUpload);
        Log.i("SPEEDATA_BUS", "uploadBosiRe: json=====" + rusultData);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), rusultData);
        QrcodeApi.getInstance().bosiUpload(requestBody)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Observer<QrcodeUploadResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(QrcodeUploadResult payUploadResult) {
                        Log.i(TAG, "onNext: " + payUploadResult.toString());
                        mView.success("bosi上传成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "bosi上传结果onError :" + e.toString());
                        mView.erro(e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    ///////////////////////////////////////////////////银联//////////////////////////
    @Override
    public void getYinLianPubKey() {
        HttpMethods.getInstance().unqrkey(new Observer<UnqrkeyBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UnqrkeyBackBean unqrkeyBackBean) {
                List<KeysBean> keys = unqrkeyBackBean.getKeys();
                if (keys.size() > 0) {
                    DbDaoManage.getDaoSession().getKeysBeanDao().deleteAll();
                    for (KeysBean key : keys) {
                        DbDaoManage.getDaoSession().getKeysBeanDao().insertOrReplace(key);
                    }
                }

            }

            @Override
            public void onError(Throwable e) {
                mView.erro(e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void checkYinLianCode(Context context, String qrcode) {
        //银联二维码
        QrEntity qrEntity = new QrEntity(qrcode);
        try {
            List<KeysBean> keysBeans = DbDaoManage.getDaoSession().getKeysBeanDao().loadAll();
            if (keysBeans.size() > 0) {
                boolean validation = ValidationUtils.validationTianJin(qrEntity, keysBeans);
                if (validation) {
                    SaveDataUtils.saveYinLianDataBean(context, qrcode, qrEntity);
                    uploadYinLian();
                } else {
                    mView.erro("验证失败");
                }
            } else {
                mView.erro("没有银联秘钥");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadYinLian() {
        String weiXinUploadData = getYinLianUploadData().replace("\\\"", "'");

        HttpMethods.getInstance().produce(weiXinUploadData, new Observer<NetBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NetBackBean netBackBean) {
                String msg = netBackBean.getMsg();
                if ("success".equalsIgnoreCase(msg)) {
                    Log.i(TAG, "onNext: " + netBackBean.toString());
                    mView.success("银联上传成功");
                    for (UploadInfoYinLianDB yinLianDB : yinLianDBS) {
                        yinLianDB.setIsUpload(true);
                        DbDaoManage.getDaoSession().getUploadInfoYinLianDBDao().update(yinLianDB);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                mView.erro(e.toString());
                Log.i(TAG, "uploadWechatRe: " + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public String getYinLianUploadData() {
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        //上传记录到天津后台
        ProducePost producePost = new ProducePost();
        producePost.setType("2200UQ");
        producePost.setRoute("11");
        producePost.setPosId("123");
        ProduceYinLian produceYinLian = new ProduceYinLian();
        List<ProduceYinLian.PayinfoBean> yinLianList = new ArrayList<>();
        yinLianDBS = DbDaoManage.getDaoSession().getUploadInfoYinLianDBDao()
                .queryBuilder().where(UploadInfoYinLianDBDao.Properties.IsUpload
                        .eq(false)).list();
        if (yinLianDBS.size() != 0) {
            for (UploadInfoYinLianDB yinLianDB : yinLianDBS) {
                ProduceYinLian.PayinfoBean payinfoBean = new ProduceYinLian.PayinfoBean();
                //车辆号
                payinfoBean.setBusNo(yinLianDB.getBusNo());
                //交易流水号
                payinfoBean.setTrans_seq(yinLianDB.getTrans_seq());
                //APPID
                payinfoBean.setApp_id(yinLianDB.getApp_id());
                //业务标识
                payinfoBean.setService_id(yinLianDB.getService_id());
                //扫码时间
                payinfoBean.setScan_time(yinLianDB.getScan_time());
                payinfoBean.setTrip_no(yinLianDB.getTrip_no());
                //司机号
                payinfoBean.setDriver(yinLianDB.getDriver());
                //线路号
                payinfoBean.setLine_no(yinLianDB.getLine_no());
                //金额
                payinfoBean.setAmount(yinLianDB.getAmount());
                //路队
                payinfoBean.setTeam(yinLianDB.getTeam());
                //线路号
                payinfoBean.setRoute(yinLianDB.getRoute());
                //机具号
                payinfoBean.setPosId(yinLianDB.getPosId());
                //用户凭证类型
                payinfoBean.setVoucher_type(yinLianDB.getVoucher_type());
                //机具号
                payinfoBean.setTerminal_no(yinLianDB.getTerminal_no());
                //公司号
                payinfoBean.setDept(yinLianDB.getDept());
                payinfoBean.setVoucher_no(yinLianDB.getVoucher_no());
                //用户标识
                payinfoBean.setUser_id(yinLianDB.getUser_id());
                //二级码原数据
                payinfoBean.setQrcode_data(yinLianDB.getQrcode_data());
                //生成时间
                payinfoBean.setCreate_time(yinLianDB.getCreate_time());
                //扫码确认类型
                payinfoBean.setScan_confirm_type(yinLianDB.getScan_confirm_type());
                yinLianList.add(payinfoBean);
            }
        }

        produceYinLian.setPayinfo(yinLianList);
        producePost.setData(gson.toJson(produceYinLian));
        return gson.toJson(producePost).toString();
    }
}
