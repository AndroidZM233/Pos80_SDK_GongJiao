package com.spd.bus.util;

import android.content.Context;
import android.text.TextUtils;

import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spd.base.been.tianjin.CardRecord;
import com.spd.base.been.tianjin.CardRecordDao;
import com.spd.base.been.tianjin.NetBackBean;
import com.spd.base.been.tianjin.produce.ProducePost;
import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.Datautils;
import com.spd.base.utils.LogUtils;
import com.spd.bus.Info;
import com.spd.bus.entity.Payrecord;
import com.spd.bus.net.HttpMethods;
import com.spd.bus.sql.SqlStatement;

import org.apache.commons.lang3.text.StrBuilder;

import java.util.ArrayList;
import java.util.Arrays;
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
                    listUpdateRecord.clear();
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

        List<Payrecord> payRecords = SqlStatement.selectTagRecord();
        if (payRecords.size() == 0) {
            return null;
        }
        String payRecordCount = "";
        for (int i = 0; i < payRecords.size(); i++) {
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
