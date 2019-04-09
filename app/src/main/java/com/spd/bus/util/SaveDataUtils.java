package com.spd.bus.util;

import android.content.Context;

import com.example.test.yinlianbarcode.entity.QrEntity;
import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spd.alipay.been.AliCodeinfoData;
import com.spd.alipay.been.TianjinAlipayRes;
import com.spd.base.been.tianjin.TStaffTb;
import com.spd.base.been.tianjin.produce.ProducePost;
import com.spd.base.been.tianjin.produce.weixin.PayinfoBean;
import com.spd.base.been.tianjin.produce.weixin.ProduceWeiXin;
import com.spd.base.been.tianjin.produce.weixin.UploadInfoDB;
import com.spd.base.been.tianjin.produce.weixin.UploadInfoDBDao;
import com.spd.base.been.tianjin.produce.yinlian.UploadInfoYinLianDB;
import com.spd.base.been.tianjin.produce.zhifubao.ReqDataBean;
import com.spd.base.been.tianjin.produce.zhifubao.UploadInfoZFBDB;
import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.Datautils;
import com.spd.bus.Info;
import com.spd.bus.card.utils.DateUtils;
import com.tencent.wlxsdk.WlxSdk;

import org.apache.commons.lang3.text.StrBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 张明_ on 2019/3/11.
 * Email 741183142@qq.com
 */
public class SaveDataUtils {

    public static void saveZhiFuBaoReqDataBean(TianjinAlipayRes aliCodeinfoData) throws Exception {
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
        RunParaFile runParaFile = runParaFiles.get(0);
        List<TStaffTb> tStaffTbs = DbDaoManage.getDaoSession().getTStaffTbDao().loadAll();
        TStaffTb tStaffTb = null;
        if (tStaffTbs.size() > 0) {
            tStaffTb = tStaffTbs.get(0);
        }

        UploadInfoZFBDB reqDataBean = new UploadInfoZFBDB();
        String currentTimeMillis = DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_YMDHMS);
        // 交易流水
        reqDataBean.setOutTradeNo("6001_17430805_" + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));
        // 设备号
        reqDataBean.setDeviceId("17430805");
        // 司机卡号
        reqDataBean.setDriverCardNo(tStaffTb == null ? "300000015165068000"
                : Datautils.byteArrayToString(tStaffTb.getUcAppSnr()));
        // 卡类型
        reqDataBean.setCardType(aliCodeinfoData.cardType);
        // 用户号
        reqDataBean.setUserId(aliCodeinfoData.uid);
        // 车辆号
        reqDataBean.setCarryCode(Datautils.byteArrayToString(runParaFile.getBusN()));
        // 路队号
        reqDataBean.setBusGroupCode(Datautils.byteArrayToString(runParaFile.getTeamNr()));
        // 公司号
        reqDataBean.setCompanyCode(Datautils.byteArrayToString(runParaFile.getCorNr()));
        // 站点名称
        reqDataBean.setStationName("1");
        // 电子公交卡卡号
        reqDataBean.setCardId(new String(aliCodeinfoData.cardNo));
        // 售票员签到时间
        reqDataBean.setSellerSignTime("20190313113100");
        // 机具内扫码序号
        reqDataBean.setSeq("001");
        // 司机签到时间
        reqDataBean.setDriverSignTime(tStaffTb == null ? "20190313113100"
                : Datautils.byteArrayToString(tStaffTb.getUlBCD()));
        // 区域号
        reqDataBean.setAreaCode(Datautils.byteArrayToString(runParaFile.getAreaNr()));
        // 票价
        reqDataBean.setPrice(Datautils.byteArrayToInt(runParaFile.getKeyV1()) + "");
        // 站点号
        reqDataBean.setStationId("1");
        // 交易时间
        reqDataBean.setActualOrderTime(currentTimeMillis);

//        reqDataBean.setCardData(Datautils.byteArrayToString(aliCodeinfoData.cardData));
        reqDataBean.setCardData("31");
        // 真实票价
        reqDataBean.setActualPrice("1");
        // 线路号
        reqDataBean.setLineCode(Datautils.byteArrayToString(runParaFile.getLineNr()));
        // 记录内容
        reqDataBean.setRecord(aliCodeinfoData.record);
        DbDaoManage.getDaoSession().getUploadInfoZFBDBDao().insertOrReplace(reqDataBean);
    }


    public static void saveWeiXinDataBean(WlxSdk wlxSdk) throws Exception {
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
        RunParaFile runParaFile = runParaFiles.get(0);
        List<TStaffTb> tStaffTbs = DbDaoManage.getDaoSession().getTStaffTbDao().loadAll();
        TStaffTb tStaffTb = null;
        if (tStaffTbs.size() > 0) {
            tStaffTb = tStaffTbs.get(0);
        }

        UploadInfoDB payinfoBean = new UploadInfoDB();
        payinfoBean.setOpen_id(wlxSdk.get_open_id());
        payinfoBean.setDriverSignTime(tStaffTb == null ? "20190313113100"
                : Datautils.byteArrayToString(tStaffTb.getUlBCD()));
        payinfoBean.setTeam(Datautils.byteArrayToString(runParaFile.getTeamNr()));
        payinfoBean.setRoute("");
        // TODO: 2019/4/9 测试先写一分钱
//        payinfoBean.setAccount(Datautils.byteArrayToInt(runParaFile.getKeyV1()) + "");
        payinfoBean.setAccount("1");

        payinfoBean.setDept(Datautils.byteArrayToString(runParaFile.getCorNr()));
        payinfoBean.setIn_station_time(DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_YMDHMS));
        payinfoBean.setBus_no(Datautils.byteArrayToString(runParaFile.getBusN()));
        payinfoBean.setDriver(tStaffTb == null ? "300000015165068000"
                : Datautils.byteArrayToString(tStaffTb.getUcAppSnr()));
        payinfoBean.setPos_id("");
        payinfoBean.setRecord_in(wlxSdk.get_record());
        payinfoBean.setIsUpload(false);
        DbDaoManage.getDaoSession().getUploadInfoDBDao().insertOrReplace(payinfoBean);
    }

    public static void saveYinLianDataBean(Context context, String code, QrEntity qrEntity) throws Exception {
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
        RunParaFile runParaFile = runParaFiles.get(0);
        List<TStaffTb> tStaffTbs = DbDaoManage.getDaoSession().getTStaffTbDao().loadAll();
        TStaffTb tStaffTb = null;
        if (tStaffTbs.size() > 0) {
            tStaffTb = tStaffTbs.get(0);
        }

        UploadInfoYinLianDB payinfoBean = new UploadInfoYinLianDB();
        //车辆号
        payinfoBean.setBusNo(Datautils.byteArrayToString(runParaFile.getBusN()));
        //交易流水号
        int read = SharedXmlUtil.getInstance(context).read(Info.YL_TRANS_SEQ, 0);
        String valueOf = String.valueOf(read + 1);
        StrBuilder strBuilder = new StrBuilder();
        for (int i = valueOf.length(); i < 16; i++) {
            strBuilder.append("0");
        }
        strBuilder.append(valueOf);
        payinfoBean.setTrans_seq(strBuilder.toString());
        //APPID
        payinfoBean.setApp_id(qrEntity.getMobileMark());
        //业务标识
        payinfoBean.setService_id("02");
        //扫码时间
        payinfoBean.setScan_time(DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));
        String customData = qrEntity.getCustomData().substring(20, 36);
        payinfoBean.setTrip_no(customData);
        //司机号
        payinfoBean.setDriver(tStaffTb == null ? "300000015165068000"
                : Datautils.byteArrayToString(tStaffTb.getUcAppSnr()));
        //线路号
        payinfoBean.setLine_no(Datautils.byteArrayToString(runParaFile.getLineNr()));
        //金额
        payinfoBean.setAmount("1");
        //路队
        payinfoBean.setTeam(Datautils.byteArrayToString(runParaFile.getTeamNr()));
        //线路号
        payinfoBean.setRoute(Datautils.byteArrayToString(runParaFile.getTeamNr()));
        //机具号
        payinfoBean.setPosId("17510418");
        //用户凭证类型
        payinfoBean.setVoucher_type("00");
        payinfoBean.setTerminal_no("1751041817510418");
        //公司号
        payinfoBean.setDept(Datautils.byteArrayToString(runParaFile.getCorNr()));
        payinfoBean.setVoucher_no(qrEntity.getQrCode());
        //用户标识
        payinfoBean.setUser_id(qrEntity.getUserMark());
        //二级码原数据
        payinfoBean.setQrcode_data(code);

        Date date = new Date();
        date.setTime(qrEntity.getCreateTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtils.FORMAT_yyyyMMddHHmmss);
        //生成时间
        payinfoBean.setCreate_time(simpleDateFormat.format(date));
        //扫码确认类型
        payinfoBean.setScan_confirm_type("01");
        DbDaoManage.getDaoSession().getUploadInfoYinLianDBDao().insertOrReplace(payinfoBean);
        SharedXmlUtil.getInstance(context).write(Info.YL_TRANS_SEQ, read + 1);
    }
}
