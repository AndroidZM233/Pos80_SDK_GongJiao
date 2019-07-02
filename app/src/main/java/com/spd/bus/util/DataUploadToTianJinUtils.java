package com.spd.bus.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spd.base.been.tianjin.NetBackBean;
import com.spd.base.been.tianjin.produce.ProducePost;
import com.spd.base.been.tianjin.produce.shuangmian.ProduceShuangMian;
import com.spd.base.been.tianjin.produce.shuangmian.ShuangMianBean;
import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.Datautils;
import com.spd.base.utils.LogUtils;
import com.spd.bus.Info;
import com.spd.bus.entity.Payrecord;
import com.spd.bus.entity.UnionPay;
import com.spd.bus.net.HttpMethods;
import com.spd.bus.sql.SqlStatement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by 张明_ on 2019/3/28.
 * Email 741183142@qq.com
 */
public class DataUploadToTianJinUtils {
    private static List<Payrecord> listUpdateRecord = new ArrayList<Payrecord>();
    private static List<UnionPay> updataUnionRecord = new ArrayList<UnionPay>();

    public static void uploadCardData(Context context) {
        String uploadData = getCardData(context);
        LogUtils.d("上传信息：" + uploadData);
        if (TextUtils.isEmpty(uploadData)) {
            return;
        }
        uploadData = uploadData.replace("\\\"", "'");
        HttpMethods.getInstance().produce(uploadData, new Observer<NetBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NetBackBean netBackBean) {
                String msg = netBackBean.getMsg();
                if ("success".equalsIgnoreCase(msg)) {
                    SqlStatement.UpdataSubmit(listUpdateRecord);
                    List<Payrecord> payRecords = SqlStatement.selectTagRecord();
                    if (payRecords.size() > 0) {
                        uploadCardData(context);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.d(e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static String getCardData(Context context) {
        List<Payrecord> payRecords = SqlStatement.selectTagRecord();
        if (payRecords.size() == 0) {
            return null;
        }
        listUpdateRecord.clear();
        DatabaseTabInfo.getIntence("info");
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        //上传记录到天津后台
        ProducePost producePost = new ProducePost();
        producePost.setType("2200C");
        String line = DatabaseTabInfo.line;
        if (!TextUtils.isEmpty(line)) {
            producePost.setRoute(line);
        } else {
            producePost.setRoute("11");
        }
        producePost.setPosId(DatabaseTabInfo.deviceNo);

        String payRecordCount = "";
        int count = 30;
        if (payRecords.size() < 30) {
            count = payRecords.size();
        }
        for (int i = 0; i < count; i++) {
            if (payRecords.get(i).getRecord().substring(124, 128).equalsIgnoreCase(GetDriverRecord
                    .dirverRecordData().substring(0, 4))) {
                payRecordCount = payRecordCount + payRecords.get(i).getRecord();
            } else {
                String driverRecord = GetDriverRecord.dirverRecordData2(payRecords.get(i).getRecord());
                payRecordCount = new StringBuffer().append(payRecordCount).append(driverRecord).append(payRecords.get(i).getRecord()).toString();
            }

            listUpdateRecord.add(payRecords.get(i));
        }

        StringBuffer stringBuff = new StringBuffer();
        stringBuff.append(GetDriverRecord
                .dirverRecordData());
        stringBuff.append(payRecordCount);
        producePost.setData(String.valueOf(stringBuff));
        return gson.toJson(producePost).toString();
    }

    public static void uploadSM(Context context) {
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
                    LogUtils.i("onNext: " + netBackBean.toString());
                    SqlStatement.updataICUnion(updataUnionRecord);
                    List<UnionPay> listUnionPay = SqlStatement.getUnionPayReocrdTag();
                    if (listUnionPay.size() > 0) {
                        uploadSM(context);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e("uploadWechatRe: " + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private static String getSMUploadData(Context context) {
        List<UnionPay> listUnionPay = SqlStatement.getUnionPayReocrdTag();
        if (listUnionPay.size() == 0) {
            return null;
        }
        DatabaseTabInfo.getIntence("info");
        updataUnionRecord.clear();
        final Gson gson = new GsonBuilder().serializeNulls().create();
        //上传记录到天津后台
        ProducePost producePost = new ProducePost();
        producePost.setType("2200UC");
        producePost.setRoute(DatabaseTabInfo.line);

        String posId = DatabaseTabInfo.deviceNo;
        producePost.setPosId(posId);
        ProduceShuangMian produceShuangMian = new ProduceShuangMian();
        List<ShuangMianBean> shuangMianBeans = new ArrayList<>();
        int count = 30;
        if (listUnionPay.size() < 30) {
            count = listUnionPay.size();
        }

        for (int i = 0; i < count; i++) {
            UnionPay unionPay = listUnionPay.get(i);
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


        produceShuangMian.setPayinfo(shuangMianBeans);
        producePost.setData(gson.toJson(produceShuangMian));
        return gson.toJson(producePost).toString();
    }


    public static void postLog(Context context, String log) {
        String logString = SharedXmlUtil.getInstance(context).read(Info.POS_ID, Info.POS_ID_INIT)
                + "," + log;
        Map<String, String> map = new HashMap<>();
        map.put("log", logString);
        HttpMethods.getInstance().postLog(map, new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                LogUtils.d(responseBody.toString());
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.d(e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
