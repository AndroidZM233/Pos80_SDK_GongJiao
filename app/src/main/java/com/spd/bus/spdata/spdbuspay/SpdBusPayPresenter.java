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
import com.spd.bus.entity.UnionPay;
import com.spd.bus.net.HttpMethods;
import com.spd.base.utils.LogUtils;
import com.spd.bus.spdata.been.ErroCode;
import com.spd.bus.spdata.mvp.BasePresenterImpl;
import com.spd.bus.sql.SqlStatement;
import com.spd.bus.util.DatabaseTabInfo;
import com.spd.bus.util.SaveDataUtils;
import com.tencent.wlxsdk.WlxSdk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private List<UnionPay> updataUnionRecord = new ArrayList<UnionPay>();

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
//        int result = alipayJni.initAliDev("{\"public_key\":\"02AB2FDB0AB23506C48012642E1A572FC46B5D8B8EE3B92A602CC1109921F84B0E\",\"key_id\":0},{\"public_key\":\"02EA95A096BB5BE9693635DCD2231D210E15B8803C10FFE5293B29A67251C3605B\",\"key_id\":1},{\"public_key\":\"038824D92AEFA2B1EE8F349FA2C38DBB0D0EAB057B99FF2F1899BBFA1F29F1162B\",\"key_id\":2},{\"public_key\":\"03B41B517513E03EBEE3DB4D57594E5A0F19688E9A5067AB23B6C61366634AF572\",\"key_id\":3},{\"public_key\":\"0282ABB881685EC8082B816C84F0AF7BA714674B027617AA9C0BEA540F50B61245\",\"key_id\":4},{\"public_key\":\"0275FE4E714229B1732E2CFDA44011F6718E9E2261B74AE3886C667D23AB21EB7F\",\"key_id\":5},{\"public_key\":\"02D4ABD6277C42125EC93811FB75F02E8D29CE19F17B0118A06FB64DDDAD8F151D\",\"key_id\":6},{\"public_key\":\"03C833BCD6FE464F2F923D3B87C4945E7F661D686A379B454306C79000ED3778B1\",\"key_id\":7},{\"public_key\":\"0270E69E1719C835C307D16050141E0527DD5B99E025F7D5A91AEAB6DF95DC7606\",\"key_id\":8},{\"public_key\":\"0230866CB7FA9800E779E873B8E9786295E851CF327905FF290123B91F2E4E4DE3\",\"key_id\":9},{\"public_key\":\"03B469F0764B2CB93FAEDD604FA1CECB7AA33AAC6E217E06A4D6FA86749A51E6CC\",\"key_id\":10},{\"public_key\":\"0365C74E6B3D53D2742CB7EF60DA78F3931BE28AC12E25DF05C449CD04D0AE096F\",\"key_id\":11},{\"public_key\":\"0310109AED9B06D38AF10FA1729119E05C465578A3ADBCDFBEDDA723112E2DB13C\",\"key_id\":12},{\"public_key\":\"034CB34A4D15F9114A3868CC6B6C9003F89682A8C45D8BBE44BD388CC0BDA2C1CE\",\"key_id\":13},{\"public_key\":\"0366B8E3A4BBE12ED11C54B48A9E9B724A07CF8E98F78522B23EFD95D6A2EC00DD\",\"key_id\":14},{\"public_key\":\"02E7079580CE071A928446E81428EC4873A2F8C879311687A466FDACD13F9DD29E\",\"key_id\":15},{\"public_key\":\"02396D50BBCF15A40D3BBD06F7D7763D3E5795128F3FB1FEC0C95085CC1FE636E1\",\"key_id\":16},{\"public_key\":\"02C06EA706FCC49F200BAE60FE5F85C519EE21620821B370CB815AB849BE22A9D5\",\"key_id\":17},{\"public_key\":\"03383F60535555C5277BC5163F087C7EAE0D79CCC86B9DB96565D9A333404D638C\",\"key_id\":18},{\"public_key\":\"039CB571D5C1398F340D01C380BE676B51B7BA4DACED0D22879A27403BB3F49D59\",\"key_id\":19},{\"public_key\":\"03F6677074424BF61A9EF90663D91D9CC97A02E5462D0386FBCBED7FC111ECF12A\",\"key_id\":20},{\"public_key\":\"028DC1E334FA617A711B9E5A5060E29DADA03AF0DB642B9264903224A3A92509A2\",\"key_id\":21},{\"public_key\":\"02DF95E6C7491E0F90A2322075BD973FBCB2D163B92623BBE153F65814583F17C9\",\"key_id\":22},{\"public_key\":\"0399F5E924A3C5B8ECFE6F3D1BE9B7C176BEBE6857F428849CB8E8BCDCE689D827\",\"key_id\":23},{\"public_key\":\"03CA2BFD5C6B52D0E8D826378EF23A01839D36C60F76ECDB1BCC0B2E55E04251F0\",\"key_id\":24},{\"public_key\":\"02557D5AF16A345815C2F3896535A7F969AA7BE9F0A300386FF0D637A7891001B0\",\"key_id\":25},{\"public_key\":\"029AD06148C81E0025CB1685591513BE657A6A9A9BE9E83B2EE110221B26306EFC\",\"key_id\":26},{\"public_key\":\"03871839999D6003D907E9884DC18928261845480C213E480D530E03CE46084087\",\"key_id\":27},{\"public_key\":\"0379BE1176150C83D256F020D5B0A409A7615C99EBC7070F27E762900DA760F32F\",\"key_id\":28},{\"public_key\":\"034BFD4A1E3E39C51BCC60E62DD3F80D8392A4418155309E50F2CDD94985FAD026\",\"key_id\":29},{\"public_key\":\"03999A50E628878F0670DBBFE20F98919E4FEEBB00CFED98F0867C5313546693D4\",\"key_id\":30},{\"public_key\":\"03079B27692B20A315D95A48DA2F7F2CCC0B80723F8A65DD13B3999706DE75F559\",\"key_id\":31}]", "[\"T0120000\",\"ANT00001\"]");
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
        DatabaseTabInfo.getIntence("info");
        String lineNr = DatabaseTabInfo.line;
        String devNr = DatabaseTabInfo.deviceNo;
        String outTradeNo = lineNr + "_" + devNr + "_"
                + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss) + "_"
                + (int) ((Math.random() * 9 + 1) * 1000);
        TianjinAlipayRes tianjinAlipayRes = new TianjinAlipayRes();


        if (alipayJni==null){
            mView.erro(ReturnVal.CAD_NO_KEY);
            return;
        }
        tianjinAlipayRes = alipayJni.checkAliQrCode(tianjinAlipayRes,
                code, devNr, lineNr, Integer.parseInt(DatabaseTabInfo.price, 16)
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

        mView.showCheckAliQrCode(tianjinAlipayRes, outTradeNo);
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
        DatabaseTabInfo.getIntence("info");
        producePost.setRoute(DatabaseTabInfo.line);

        String posId = DatabaseTabInfo.deviceNo;
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
    public void checkWechatTianJin(Context context, String code, byte scene,
                                   byte scantype, String posTrxId, String driverTime) {
        if (wlxSdk == null) {
            wlxSdk = new WlxSdk();
        }
        DatabaseTabInfo.getIntence("info");
        String lineNr = DatabaseTabInfo.line;
        String devNr = DatabaseTabInfo.deviceNo;
        String outTradeNo = lineNr + "_" + devNr + "_"
                + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss) + "_"
                + (int) ((Math.random() * 9 + 1) * 1000);

        int result = 0;
        result = wlxSdk.init(code);
        if (result != ErroCode.EC_SUCCESS) {
            LogUtils.d(result + "");
            mView.showCheckWechatQrCode(result);
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
        List<GetPublicBackBean> getPublicBackBeans = DbDaoManage.getDaoSession()
                .getGetPublicBackBeanDao().loadAll();
        if (getPublicBackBeans == null || getPublicBackBeans.size() == 0) {
            mView.erro(ReturnVal.CAD_NO_KEY);
            return;
        }
        GetPublicBackBean publicBackBean = getPublicBackBeans.get(0);
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

        result = wlxSdk.verify(openId, pubKey, Integer.parseInt(DatabaseTabInfo.price, 16)
                , scene, scantype, DatabaseTabInfo.deviceNo, outTradeNo, aesMacRoot);
        if (result != ErroCode.EC_SUCCESS) {
            LogUtils.d(result + "");
            mView.showCheckWechatQrCode(result);
            return;
        }
        try {
            //司机号
            String driversNo = SharedXmlUtil.getInstance(context)
                    .read("TAGS", "0");
            SaveDataUtils.saveWeiXinDataBean(wlxSdk, driversNo, driverTime);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("微信数据保存失败:" + e.toString());
        }
        String record = wlxSdk.get_record();
        mView.showCheckWechatQrCode(result);

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
        DatabaseTabInfo.getIntence("info");
        producePost.setRoute(DatabaseTabInfo.line);
        String posId = DatabaseTabInfo.deviceNo;
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
        QrEntity qrEntity = new QrEntity(qrcode);
        try {
            List<KeysBean> keysBeans = DbDaoManage.getDaoSession().getKeysBeanDao().loadAll();
            if (keysBeans.size() > 0) {
                boolean validation = ValidationUtils.validationTianJin(qrEntity, keysBeans);
                if (validation) {
                    SaveDataUtils.saveYinLianDataBean(context, qrcode, qrEntity);
                    mView.successCode(new CardBackBean(ReturnVal.CODE_YINLAIN_SUCCESS
                            , null));
                    uploadYinLian(context);
                } else {
                    mView.successCode(new CardBackBean(ReturnVal.CAD_EMPTY
                            , null));
                }
            } else {
                mView.successCode(new CardBackBean(ReturnVal.CAD_NO_KEY
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
        DatabaseTabInfo.getIntence("info");
        producePost.setRoute(DatabaseTabInfo.line);
        String posId = DatabaseTabInfo.deviceNo;
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
                    SqlStatement.updataICUnion(updataUnionRecord);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "uploadWechatRe: " + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private String getSMUploadData(Context context) {
        DatabaseTabInfo.getIntence("info");
        updataUnionRecord.clear();
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

        String posId = DatabaseTabInfo.deviceNo;
        producePost.setPosId(posId);
        ProduceShuangMian produceShuangMian = new ProduceShuangMian();
        List<ShuangMianBean> shuangMianBeans = new ArrayList<>();
        List<UnionPay> listUnionPay = SqlStatement.getUnionPayReocrdTag();
        if (listUnionPay.size() != 0) {
            for (UnionPay unionPay : listUnionPay) {
                ShuangMianBean shuangMianBean = new ShuangMianBean();
                shuangMianBean.setBusNo(DatabaseTabInfo.busno);
                shuangMianBean.setCardSerialNum(unionPay.getCardSerial());
                shuangMianBean.setBatchNumber(unionPay.getBatchNumber());
                shuangMianBean.setResponseCode(unionPay.getResponseCode());
                shuangMianBean.setIsPay(unionPay.getIsPay());
                shuangMianBean.setDriver(SharedXmlUtil.getInstance(context)
                        .read("TAGS", "00003000000151650680"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String stationTime = sdf.format(new Date(Long.parseLong(unionPay
                        .getStationTime(), 16) * 1000));
                shuangMianBean.setTransactionTime(stationTime);
                shuangMianBean.setTowTrackData(unionPay.getTwoTrackData());
                shuangMianBean.setTerminalCode(DatabaseTabInfo.deviceNo);
                shuangMianBean.setSerialNumber(unionPay.getTradingFlow());
                shuangMianBean.setTransactionAmount(unionPay.getAmount());
                shuangMianBean.setTeam(DatabaseTabInfo.tream);
                shuangMianBean.setThreeTrackData("");
                shuangMianBean.setRoute(DatabaseTabInfo.line);
                shuangMianBean.setPosId(DatabaseTabInfo.deviceNo);
                shuangMianBean.setRetrievingNum(unionPay.getRetrievingNum());
                shuangMianBean.setDept(DatabaseTabInfo.dept);
                shuangMianBean.setField(unionPay.getICCardDataDomain());
                shuangMianBean.setTransactionCode(unionPay.getTradingFlow());
                shuangMianBean.setType(unionPay.getType());
                shuangMianBean.setCardNo(unionPay.getPrimaryAcountNum());
                shuangMianBeans.add(shuangMianBean);

                updataUnionRecord.add(unionPay);
            }
        } else {
            return null;
        }

        produceShuangMian.setPayinfo(shuangMianBeans);
        producePost.setData(gson.toJson(produceShuangMian));
        return gson.toJson(producePost).toString();
    }
}
