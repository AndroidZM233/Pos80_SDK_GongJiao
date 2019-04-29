package com.spd.bus.spdata.spdbuspay;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.test.yinlianbarcode.entity.QrEntity;
import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.example.test.yinlianbarcode.utils.ValidationUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.spd.alipay.AlipayJni;
import com.spd.alipay.been.TianjinAlipayRes;

import com.spd.base.been.tianjin.CardBackBean;
import com.spd.base.been.tianjin.GetMacBackBean;
import com.spd.base.been.tianjin.GetPublicBackBean;
import com.spd.base.been.tianjin.GetZhiFuBaoKey;
import com.spd.base.been.tianjin.KeysBean;
import com.spd.base.been.tianjin.TCardOpDU;
import com.spd.base.been.tianjin.produce.shuangmian.ProduceShuangMian;
import com.spd.base.been.tianjin.produce.shuangmian.ShuangMianBean;
import com.spd.base.been.tianjin.produce.shuangmian.UploadSMDB;
import com.spd.base.been.tianjin.produce.shuangmian.UploadSMDBDao;
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
import com.spd.base.been.WechatQrcodeKey;
import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.dbbeen.WeichatKeyDb;
import com.spd.base.utils.Datautils;
import com.spd.bus.Info;
import com.spd.base.been.tianjin.NetBackBean;
import com.spd.base.been.tianjin.PosInfoBackBean;
import com.spd.base.been.tianjin.produce.ProducePost;
import com.spd.bus.card.methods.ReturnVal;
import com.spd.base.utils.DateUtils;
import com.spd.bus.net.HttpMethods;
import com.spd.base.utils.LogUtils;
import com.spd.bus.spdata.been.ErroCode;
import com.spd.bus.spdata.mvp.BasePresenterImpl;
import com.spd.bus.util.SaveDataUtils;
import com.tencent.wlxsdk.WlxSdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

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
    private List<UploadSMDB> uploadSMDBList;
    //    private List<CardRecord> cardRecordList;

    //===============支付宝二维码==============


    @Override
    public void aliPayInitJni() {

        List<GetZhiFuBaoKey> getZhiFuBaoKeys = DbDaoManage.getDaoSession()
                .getGetZhiFuBaoKeyDao().loadAll();
        if (getZhiFuBaoKeys.size() == 0) {
            return;
        }
        GetZhiFuBaoKey getZhiFuBaoKey = getZhiFuBaoKeys.get(0);

        alipayJni = new AlipayJni();
        int result = alipayJni.initAliDev(getZhiFuBaoKey.getPublicKeys(), getZhiFuBaoKey.getCards());
        Log.e(TAG, "showAliPayInit: " + mView);
        mView.showAliPayInit(result);
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
     * 验码
     *
     * @param code 扫描到的二维码
     */
    @Override
    public void checkAliQrCode(String code) {
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
        if (runParaFiles.size() == 0) {
            mView.erro(ReturnVal.CODE_PLEASE_SET);
            return;
        }
        RunParaFile runParaFile = runParaFiles.get(0);
        String lineNr = Datautils.byteArrayToString(runParaFile.getLineNr());
        String devNr = Datautils.byteArrayToString(runParaFile.getDevNr());
        String outTradeNo = lineNr + "_" + devNr + "_"
                + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss) + "_"
                + ((Math.random() * 9 + 1) * 1000);
        TianjinAlipayRes tianjinAlipayRes = new TianjinAlipayRes();

        tianjinAlipayRes = alipayJni.checkAliQrCode(tianjinAlipayRes,
                code, devNr, lineNr, Datautils.byteArrayToInt(runParaFile.getKeyV1())
                , "SINGLE", outTradeNo);
        LogUtils.v("onHSMDecodeResult: " + tianjinAlipayRes.toString());

        if (tianjinAlipayRes.result != 1) {
            mView.erro(ReturnVal.CAD_EMPTY);
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
                    mView.erro(ReturnVal.CAD_EMPTY);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mView.showCheckAliQrCode(tianjinAlipayRes, runParaFile, outTradeNo);
    }

    @Override
    public void uploadAlipayRe(Context context) {
        String aLiUploadData = getALiUploadData(context);

        if (TextUtils.isEmpty(aLiUploadData)) {
            return;
        }
        aLiUploadData = aLiUploadData.replace("\\\"", "'");
        HttpMethods.getInstance().produce(aLiUploadData, new Observer<NetBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NetBackBean netBackBean) {
                String msg = netBackBean.getMsg();
                if ("success".equalsIgnoreCase(msg)) {
                    Log.i(TAG, "onNext: " + netBackBean.toString());
                    TCardOpDU tCardOpDU = new TCardOpDU();
                    tCardOpDU.ulHCSub = Integer.parseInt(netBackBean.getCode());
//                    mView.successCode(new CardBackBean(ReturnVal.CODE_ZHIFUBAO_SUCCESS, tCardOpDU));
                    for (UploadInfoZFBDB uploadInfoZFBDB : uploadInfoZFBDBS) {
                        uploadInfoZFBDB.setIsUpload(true);
                        DbDaoManage.getDaoSession().getUploadInfoZFBDBDao().update(uploadInfoZFBDB);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
//                mView.erro(e.toString());
                Log.i(TAG, "uploadWechatRe: " + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });


    }

    private String getALiUploadData(Context context) {
        final Gson gson = new GsonBuilder().serializeNulls().create();
        //上传记录到天津后台
        ProducePost producePost = new ProducePost();
        producePost.setType("2200AQ");
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
        if (runParaFiles.size() == 0) {
            producePost.setRoute("11");
        } else {
            byte[] lineNr = runParaFiles.get(0).getLineNr();
            producePost.setRoute(Datautils.byteArrayToString(lineNr));
        }

        String posId = SharedXmlUtil.getInstance(context).read(Info.POS_ID, Info.POS_ID_INIT);
        producePost.setPosId(posId);
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
        } else {
            return null;
        }

        produceZhiFuBao.setReqData(reqDataBeans);
        String secret = SharedXmlUtil.getInstance(context).read(Info.ZFB_APP_SERCET
                , "DB623AFEBF6B5CA8DC500449D0B59AA7");
        produceZhiFuBao.setSecret(secret);
        producePost.setData(gson.toJson(produceZhiFuBao));
        return gson.toJson(producePost).toString();
    }


    @Override
    public void releseAlipayJni() {
        if (alipayJni != null) {
            int re = alipayJni.release();
        }
    }

    //===============腾讯（微信）二维码==============
    @Override
    public void wechatInitJin() {
        wlxSdk = new WlxSdk();
    }


    @Override
    public void checkWechatTianJin(String code, byte scene,
                                   byte scantype, String posId, String posTrxId) {
        if (wlxSdk == null) {
            wlxSdk = new WlxSdk();
        }
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
        if (runParaFiles.size() == 0) {
            mView.showCheckWechatQrCode(ReturnVal.CODE_PLEASE_SET, null);
            return;
        }
        RunParaFile runParaFile = runParaFiles.get(0);
        String lineNr = Datautils.byteArrayToString(runParaFile.getLineNr());
        String devNr = Datautils.byteArrayToString(runParaFile.getDevNr());
        String outTradeNo = lineNr + "_" + devNr + "_"
                + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss) + "_"
                + ((Math.random() * 9 + 1) * 1000);

        int result = 0;
        result = wlxSdk.init(code);
        if (result != ErroCode.EC_SUCCESS) {
            LogUtils.d(result + "");
            mView.showCheckWechatQrCode(result, runParaFile);
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
                    mView.erro(ReturnVal.CAD_EMPTY);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d(e.toString());
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

        result = wlxSdk.verify(openId, pubKey, Datautils.byteArrayToInt(runParaFile.getKeyV1())
                , scene, scantype, posId, outTradeNo, aesMacRoot);
        if (result != ErroCode.EC_SUCCESS) {
            LogUtils.d(result + "");
            mView.showCheckWechatQrCode(result, runParaFile);
            return;
        }
        try {
            SaveDataUtils.saveWeiXinDataBean(wlxSdk, runParaFile);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("微信数据保存失败:" + e.toString());
        }
        String record = wlxSdk.get_record();
        mView.showCheckWechatQrCode(result, runParaFile);

    }


    @Override
    public void uploadWechatRe(Context context) {
        String weiXinUploadData = getWeiXinUploadData(context);

        if (TextUtils.isEmpty(weiXinUploadData)) {
            return;
        }
        weiXinUploadData = weiXinUploadData.replace("\\\"", "'");
        HttpMethods.getInstance().produce(weiXinUploadData, new Observer<NetBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NetBackBean netBackBean) {
                String msg = netBackBean.getMsg();
                if ("success".equalsIgnoreCase(msg)) {
                    LogUtils.d("onNext: " + netBackBean.toString());
                    TCardOpDU tCardOpDU = new TCardOpDU();
                    tCardOpDU.ulHCSub = Integer.parseInt(netBackBean.getCode());
//                    mView.successCode(new CardBackBean(ReturnVal.CODE_WEIXIN_SUCCESS, tCardOpDU));
                    for (UploadInfoDB uploadInfoDB : uploadInfoDBS) {
                        uploadInfoDB.setIsUpload(true);
                        DbDaoManage.getDaoSession().getUploadInfoDBDao().update(uploadInfoDB);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
//                mView.erro(e.toString());
                LogUtils.d("uploadWechatRe: " + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });

    }


    private String getWeiXinUploadData(Context context) {
        final Gson gson = new GsonBuilder().serializeNulls().create();
        //上传记录到天津后台
        ProducePost producePost = new ProducePost();
        producePost.setType("2200WQ");
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
        if (runParaFiles.size() == 0) {
            producePost.setRoute("11");
        } else {
            byte[] lineNr = runParaFiles.get(0).getLineNr();
            producePost.setRoute(Datautils.byteArrayToString(lineNr));
        }
        String posId = SharedXmlUtil.getInstance(context).read(Info.POS_ID, Info.POS_ID_INIT);
        producePost.setPosId(posId);
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
        } else {
            return null;
        }

        produceWeiXin.setPayinfo(weiXinPayinfo);
        producePost.setData(gson.toJson(produceWeiXin));
        return gson.toJson(producePost).toString();
    }


    ///////////////////////////////////////////////////银联//////////////////////////


    @Override
    public void checkYinLianCode(Context context, String qrcode) {
        //银联二维码
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
        if (runParaFiles.size()==0){
            mView.successCode(new CardBackBean(ReturnVal.CODE_PLEASE_SET
                    , null));
            return;
        }
        RunParaFile runParaFile = runParaFiles.get(0);
        QrEntity qrEntity = new QrEntity(qrcode);
        try {
            List<KeysBean> keysBeans = DbDaoManage.getDaoSession().getKeysBeanDao().loadAll();
            if (keysBeans.size() > 0) {
                boolean validation = ValidationUtils.validationTianJin(qrEntity, keysBeans);
                if (validation) {
                    SaveDataUtils.saveYinLianDataBean(context, qrcode, qrEntity,runParaFile);
                    mView.successCode(new CardBackBean(ReturnVal.CODE_YINLAIN_SUCCESS
                            , null));
                    uploadYinLian(context);
                } else {
                    mView.successCode(new CardBackBean(ReturnVal.CAD_EMPTY
                            , null));
                }
            } else {
                mView.successCode(new CardBackBean(ReturnVal.CAD_EMPTY
                        , null));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void uploadYinLian(Context context) {
        String yinLianUploadData = getYinLianUploadData(context);

        if (TextUtils.isEmpty(yinLianUploadData)) {
            return;
        }
        yinLianUploadData = yinLianUploadData.replace("\\\"", "'");
        HttpMethods.getInstance().produce(yinLianUploadData, new Observer<NetBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NetBackBean netBackBean) {
                String msg = netBackBean.getMsg();
                if ("success".equalsIgnoreCase(msg)) {
                    Log.i(TAG, "onNext: " + netBackBean.toString());
                    TCardOpDU tCardOpDU = new TCardOpDU();
                    tCardOpDU.ulHCSub = Integer.parseInt(netBackBean.getCode());
//                    mView.successCode(new CardBackBean(ReturnVal.CODE_YINLAIN_SUCCESS, tCardOpDU));
                    for (UploadInfoYinLianDB yinLianDB : yinLianDBS) {
                        yinLianDB.setIsUpload(true);
                        DbDaoManage.getDaoSession().getUploadInfoYinLianDBDao().update(yinLianDB);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
//                mView.erro(e.toString());
                Log.i(TAG, "uploadWechatRe: " + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public String getYinLianUploadData(Context context) {
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        //上传记录到天津后台
        ProducePost producePost = new ProducePost();
        producePost.setType("2200UQ");
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
        if (runParaFiles.size() == 0) {
            producePost.setRoute("11");
        } else {
            byte[] lineNr = runParaFiles.get(0).getLineNr();
            producePost.setRoute(Datautils.byteArrayToString(lineNr));
        }

        String posId = SharedXmlUtil.getInstance(context).read(Info.POS_ID, Info.POS_ID_INIT);
        producePost.setPosId(posId);
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
        } else {
            return null;
        }

        produceYinLian.setPayinfo(yinLianList);
        producePost.setData(gson.toJson(produceYinLian));
        return gson.toJson(producePost).toString();
    }

    @Override
    public void uploadSM(Context context) {
        String smData = getSMUploadData(context);
        if (TextUtils.isEmpty(smData)) {
            return;
        }
        smData = smData.replace("\\\"", "'");
        HttpMethods.getInstance().produce(smData, new Observer<NetBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NetBackBean netBackBean) {
                String msg = netBackBean.getMsg();
                if ("success".equalsIgnoreCase(msg)) {
                    Log.i(TAG, "onNext: " + netBackBean.toString());
                    for (UploadSMDB uploadSMDB : uploadSMDBList) {
                        uploadSMDB.setIsUpload(true);
                        DbDaoManage.getDaoSession().getUploadSMDBDao().update(uploadSMDB);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
//                mView.erro(e.toString());
                Log.i(TAG, "uploadWechatRe: " + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private String getSMUploadData(Context context) {
        final Gson gson = new GsonBuilder().serializeNulls().create();
        //上传记录到天津后台
        ProducePost producePost = new ProducePost();
        producePost.setType("2200UC");
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
        if (runParaFiles.size() == 0) {
            producePost.setRoute("11");
        } else {
            byte[] lineNr = runParaFiles.get(0).getLineNr();
            producePost.setRoute(Datautils.byteArrayToString(lineNr));
        }

        String posId = SharedXmlUtil.getInstance(context).read(Info.POS_ID, Info.POS_ID_INIT);
        producePost.setPosId(posId);
        ProduceShuangMian produceShuangMian = new ProduceShuangMian();
        List<ShuangMianBean> shuangMianBeans = new ArrayList<>();
        uploadSMDBList = DbDaoManage.getDaoSession().getUploadSMDBDao()
                .queryBuilder().where(UploadSMDBDao.Properties.IsUpload.eq(false)).list();
        if (uploadSMDBList.size() != 0) {
            for (UploadSMDB uploadSMDB : uploadSMDBList) {
                ShuangMianBean shuangMianBean = new ShuangMianBean();
                shuangMianBean.setBusNo(uploadSMDB.getBusNo());
                shuangMianBean.setCardSerialNum(uploadSMDB.getCardSerialNumber());
                shuangMianBean.setBatchNumber(uploadSMDB.getBatchNumber());
                shuangMianBean.setResponseCode(uploadSMDB.getResponseCode());
                shuangMianBean.setIsPay(uploadSMDB.getIsPay());
                shuangMianBean.setDriver(uploadSMDB.getDriver());
                shuangMianBean.setTransactionTime(uploadSMDB.getTransactionTime());
                shuangMianBean.setTowTrackData(uploadSMDB.getTowTrackData());
                shuangMianBean.setTerminalCode(uploadSMDB.getTerminalCode());
                shuangMianBean.setSerialNumber(uploadSMDB.getSerialNumber());
                shuangMianBean.setTransactionAmount(uploadSMDB.getTransactionAmount());
                shuangMianBean.setTeam(uploadSMDB.getTeam());
                shuangMianBean.setThreeTrackData(uploadSMDB.getThreeTrackData());
                shuangMianBean.setRoute(uploadSMDB.getRoute());
                shuangMianBean.setPosId(uploadSMDB.getPosId());
                shuangMianBean.setRetrievingNum(uploadSMDB.getRetrievingNum());
                shuangMianBean.setDept(uploadSMDB.getDept());
                shuangMianBean.setField(uploadSMDB.getField());
                shuangMianBean.setTransactionCode(uploadSMDB.getTransactionCode());
                shuangMianBean.setType(uploadSMDB.getType());
                shuangMianBean.setCardNo(uploadSMDB.getCardNo());
                shuangMianBeans.add(shuangMianBean);
            }
        } else {
            return null;
        }

        produceShuangMian.setPayinfo(shuangMianBeans);
        producePost.setData(gson.toJson(produceShuangMian));
        return gson.toJson(producePost).toString();
    }
}
