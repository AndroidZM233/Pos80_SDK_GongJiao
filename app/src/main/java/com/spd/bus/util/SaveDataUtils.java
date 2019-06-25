package com.spd.bus.util;

import android.content.Context;

import com.example.test.yinlianbarcode.entity.QrEntity;
import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.spd.alipay.been.TianjinAlipayRes;
import com.spd.base.been.tianjin.BlackDB;
import com.spd.base.been.tianjin.BlackDBDao;
import com.spd.base.been.tianjin.CardRecord;
import com.spd.base.been.tianjin.TStaffTb;
import com.spd.base.been.tianjin.White;
import com.spd.base.been.tianjin.WhiteDao;
import com.spd.base.been.tianjin.produce.shuangmian.UploadSMDB;
import com.spd.base.been.tianjin.produce.weixin.UploadInfoDB;
import com.spd.base.been.tianjin.produce.yinlian.UploadInfoYinLianDB;
import com.spd.base.been.tianjin.produce.zhifubao.UploadInfoZFBDB;
import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.Datautils;
import com.spd.base.utils.LogUtils;
import com.spd.bus.Info;
import com.spd.base.utils.DateUtils;
import com.spd.bus.MyApplication;
import com.tencent.wlxsdk.WlxSdk;

import org.apache.commons.lang3.text.StrBuilder;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by 张明_ on 2019/3/11.
 * Email 741183142@qq.com
 */
public class SaveDataUtils {
    /**
     * 黑名单中是否有当前卡片
     *
     * @param snr
     * @return 0非黑名单 1黑名单
     */
    public static int queryBlackDB(String snr) {
        List<BlackDB> list = DbDaoManage.getDaoSession().getBlackDBDao().queryBuilder()
                .where(BlackDBDao.Properties.Data.eq(snr)).list();
        if (list != null && list.size() > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 黑名单中是否有当前卡片
     *
     * @param snr
     * @return 0非白名单 1白名单
     */
    public static int queryWhiteDB(String snr) {
        List<White> list = DbDaoManage.getDaoSession().getWhiteDao().queryBuilder()
                .where(WhiteDao.Properties.Data.eq(snr)).list();
        if (list != null && list.size() > 0) {
            return 1;
        } else {
            return 0;
        }
    }


    /**
     * 保存CPU卡记录
     */
    public static void saveCardRecord(Context context, boolean isJTB, String driverRecord, byte[] rcdBuffer) {
        CardRecord cardRecord = new CardRecord();
        cardRecord.setIsUpload(false);
        cardRecord.setBusRecord(driverRecord);
        LogUtils.d("查库开始" + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));
        long count = DbDaoManage.getDaoSession().getCardRecordDao().count();
        Long id = 0L;
        if (count != 0L) {
            CardRecord record = DbDaoManage.getDaoSession().getCardRecordDao().loadByRowId(count);
            if (record.getRecord() != null) {
                if (record.getRecord().length == 128) {
                    byte[] secondBytes = Datautils.cutBytes(record.getRecord()
                            , 64, 64);
                    byte[] bytes = new byte[64];
                    if (Arrays.equals(secondBytes, bytes)) {
                        id = record.getRecordId() + 1;
                    } else {
                        id = record.getRecordId() + 2;
                    }
                } else {
                    id = record.getRecordId() + 1;
                }
            }
        }
        LogUtils.d("查库开始2" + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));
        char mID = (char) (id & 0xffff);
        byte[] charToByte = Datautils.charToByte(mID);
        System.arraycopy(charToByte, 0, rcdBuffer, 0, 2);
        if (isJTB) {
            Long secondId = id + 1;
            char mID2 = (char) (secondId & 0xffff);
            byte[] charToByte2 = Datautils.charToByte(mID2);
            System.arraycopy(charToByte2, 0, rcdBuffer, 64, 2);
            LogUtils.d("查库开始3" + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));
        }
        cardRecord.setRecord(rcdBuffer);
        cardRecord.setRecordId(id);
        MyApplication.setCardRecordList(cardRecord);

        if (cardRecord == null || cardRecord.getRecord() == null) {
            LogUtils.d("记录信息为空");
            return;
        }
        DbDaoManage.getDaoSession().getCardRecordDao().insert(cardRecord);
        DataUploadToTianJinUtils.uploadCardData(context);
        LogUtils.d("写记录结束" + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));
    }

    public static void saveZhiFuBaoReqDataBean(TianjinAlipayRes aliCodeinfoData
            , RunParaFile runParaFile, String orderNr) throws Exception {

        List<TStaffTb> tStaffTbs = DbDaoManage.getDaoSession().getTStaffTbDao().loadAll();
        TStaffTb tStaffTb = null;
        if (tStaffTbs.size() > 0) {
            tStaffTb = tStaffTbs.get(0);
        }

        UploadInfoZFBDB reqDataBean = new UploadInfoZFBDB();
        String currentTimeMillis = DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_YMDHMS);
        byte[] lineNr = runParaFile.getLineNr();
        byte[] devNr = runParaFile.getDevNr();
        // 交易流水
        reqDataBean.setOutTradeNo(orderNr);
        // 设备号
        reqDataBean.setDeviceId(Datautils.byteArrayToString(devNr));
        // 司机卡号
        reqDataBean.setDriverCardNo(tStaffTb == null ? "300000015165068000"
                : Datautils.byteArrayToString(tStaffTb.getUcAppSnr()));
        // 卡类型
        reqDataBean.setCardType(aliCodeinfoData.cardType);
        // 用户号
        reqDataBean.setUserId(aliCodeinfoData.uid);
        // 车辆号
        reqDataBean.setCarryCode(Datautils.byteArrayToString(runParaFile.getBusNr()));
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
        reqDataBean.setActualPrice(Datautils.byteArrayToInt(runParaFile.getKeyV1()) + "");
        // 线路号
        reqDataBean.setLineCode(Datautils.byteArrayToString(lineNr));
        // 记录内容
        reqDataBean.setRecord(aliCodeinfoData.record);
        DbDaoManage.getDaoSession().getUploadInfoZFBDBDao().insertOrReplace(reqDataBean);
    }

    public static void saveSMDataBean(String cardSerial, String batchNumber, String responseCode, String isPay
            , String twoTrackData, String amount, String retrievingNum, String iCCardDataDomain, String type, String primaryAcountNum) throws Exception {
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
        if (runParaFiles.size() == 0) {
            return;
        }
        RunParaFile runParaFile = runParaFiles.get(0);
        List<TStaffTb> tStaffTbs = DbDaoManage.getDaoSession().getTStaffTbDao().loadAll();
        TStaffTb tStaffTb = null;
        if (tStaffTbs.size() > 0) {
            tStaffTb = tStaffTbs.get(0);
        }

        UploadSMDB uploadSMDB = new UploadSMDB();
        uploadSMDB.setBusNo(Datautils.byteArrayToString(runParaFile.getBusNr()));
        //卡序号
        uploadSMDB.setCardSerialNumber(cardSerial);
        //批次号
        uploadSMDB.setBatchNumber(batchNumber);
        //请款应答码
        uploadSMDB.setResponseCode(responseCode);
        //是否支付 1：已支付 0： 未支付
        uploadSMDB.setIsPay(isPay);
        //司机卡号
        uploadSMDB.setDriver(tStaffTb == null ? "300000015165068000"
                : Datautils.byteArrayToString(tStaffTb.getUcAppSnr()));
        //交易时间
        uploadSMDB.setTransactionTime(DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));
        //二磁道数据
        uploadSMDB.setTowTrackData(twoTrackData);
        //终端编号
        uploadSMDB.setTerminalCode(Datautils.byteArrayToString(runParaFile.getDevNr()));
        //序号
        long count = DbDaoManage.getDaoSession().getUploadSMDBDao().count();
        Long id = 0L;
        String serialNumber = "000001";
        if (count != 0L) {
            UploadSMDB record = DbDaoManage.getDaoSession().getUploadSMDBDao().loadByRowId(count);
            if (record != null) {
                int num = Integer.parseInt(record.getSerialNumber()) + 1;
                serialNumber = String.valueOf(num);
                for (int i = serialNumber.length(); i < 6; i++) {
                    serialNumber = "0" + serialNumber;
                }
            }
        }

        uploadSMDB.setSerialNumber(serialNumber);
        //交易金额
        uploadSMDB.setTransactionAmount(amount);
        //路队
        uploadSMDB.setTeam(Datautils.byteArrayToString(runParaFile.getTeamNr()));
        //三磁道数据
        uploadSMDB.setThreeTrackData("");
        //线路
        uploadSMDB.setRoute(Datautils.byteArrayToString(runParaFile.getLineNr()));
        //机具号
        uploadSMDB.setPosId(Datautils.byteArrayToString(runParaFile.getDevNr()));
        //追踪码
        uploadSMDB.setRetrievingNum(retrievingNum);
        //公司
        uploadSMDB.setDept(Datautils.byteArrayToString(runParaFile.getCorNr()));
        //55域
        uploadSMDB.setField(iCCardDataDomain);
        //交易序号
        uploadSMDB.setTransactionCode(serialNumber);
        //类型 03：云闪付成功 04： 云闪付失败，转ODA
        uploadSMDB.setType(type);
        //卡号
        uploadSMDB.setCardNo(primaryAcountNum);
        //是否上传
        uploadSMDB.setIsUpload(false);
        DbDaoManage.getDaoSession().getUploadSMDBDao().insertOrReplace(uploadSMDB);
    }


    public static void saveWeiXinDataBean(WlxSdk wlxSdk, RunParaFile runParaFile) throws Exception {
//        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
//        RunParaFile runParaFile = runParaFiles.get(0);
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
        payinfoBean.setRoute(Datautils.byteArrayToString(runParaFile.getLineNr()));
        // TODO: 2019/4/9 测试先写一分钱
        payinfoBean.setAccount(Datautils.byteArrayToInt(runParaFile.getKeyV1()) + "");
//        payinfoBean.setAccount("1");

        payinfoBean.setDept(Datautils.byteArrayToString(runParaFile.getCorNr()));
        payinfoBean.setIn_station_time(DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_YMDHMS));
        payinfoBean.setBus_no(Datautils.byteArrayToString(runParaFile.getBusNr()));
        payinfoBean.setDriver(tStaffTb == null ? "300000015165068000"
                : Datautils.byteArrayToString(tStaffTb.getUcAppSnr()));
        payinfoBean.setPos_id(Datautils.byteArrayToString(runParaFile.getDevNr()));
        payinfoBean.setRecord_in(wlxSdk.get_record());
        payinfoBean.setIsUpload(false);
        DbDaoManage.getDaoSession().getUploadInfoDBDao().insertOrReplace(payinfoBean);
    }

    public static void saveYinLianDataBean(Context context, String code, QrEntity qrEntity
            , RunParaFile runParaFile) throws Exception {
        List<TStaffTb> tStaffTbs = DbDaoManage.getDaoSession().getTStaffTbDao().loadAll();
        TStaffTb tStaffTb = null;
        if (tStaffTbs.size() > 0) {
            tStaffTb = tStaffTbs.get(0);
        }

        UploadInfoYinLianDB payinfoBean = new UploadInfoYinLianDB();
        //车辆号
        payinfoBean.setBusNo(Datautils.byteArrayToString(runParaFile.getBusNr()));
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
        payinfoBean.setAmount(Datautils.byteArrayToInt(runParaFile.getKeyV1()) + "");
        //路队
        payinfoBean.setTeam(Datautils.byteArrayToString(runParaFile.getTeamNr()));
        //线路号
        payinfoBean.setRoute(Datautils.byteArrayToString(runParaFile.getTeamNr()));
        //机具号
        payinfoBean.setPosId(Datautils.byteArrayToString(runParaFile.getDevNr()));
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
