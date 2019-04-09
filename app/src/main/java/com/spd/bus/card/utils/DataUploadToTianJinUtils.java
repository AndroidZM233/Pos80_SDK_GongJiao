package com.spd.bus.card.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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
import com.spd.bus.Info;
import com.spd.bus.MyApplication;

import org.apache.commons.lang3.text.StrBuilder;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by 张明_ on 2019/3/28.
 * Email 741183142@qq.com
 */
public class DataUploadToTianJinUtils {
    private static List<CardRecord> cardRecordList;

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
                    for (CardRecord cardRecord : cardRecordList) {
                        cardRecord.setIsUpload(true);
                        DbDaoManage.getDaoSession().getCardRecordDao().update(cardRecord);
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
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        //上传记录到天津后台
        ProducePost producePost = new ProducePost();
        producePost.setType("2200C");
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
        if (runParaFiles.size() != 0) {
            byte[] lineNr = runParaFiles.get(0).getLineNr();
            producePost.setRoute(Datautils.byteArrayToString(lineNr));
        } else {
            producePost.setRoute("11");
        }
        producePost.setPosId("30001234");

        cardRecordList = DbDaoManage.getDaoSession().getCardRecordDao().queryBuilder()
                .where(CardRecordDao.Properties.IsUpload.eq(false)).list();
        if (cardRecordList.size() == 0) {
            return null;
        }
        StrBuilder strBuilder = new StrBuilder();
//        String bus = SharedXmlUtil.getInstance(context).read(Info.BUS_RECORD, "");
        String busRecord = cardRecordList.get(0).getBusRecord();
        strBuilder.append(busRecord);
        for (CardRecord cardRecord : cardRecordList) {
            if (!busRecord.equals(cardRecord.getBusRecord())) {
                break;
            }

            byte[] record = cardRecord.getRecord();
            if (record.length == 128) {
                byte[] secondBytes = Datautils.cutBytes(record, 64, 64);
                byte[] bytes = new byte[64];
                if (Arrays.equals(secondBytes, bytes)) {
                    byte[] firstBytes = Datautils.cutBytes(record, 0, 64);
                    strBuilder.append(Datautils.byteArrayToString(firstBytes));
                } else {
                    strBuilder.append(Datautils.byteArrayToString(record));
                }
            } else {
                strBuilder.append(Datautils.byteArrayToString(record));
            }

        }

        producePost.setData(String.valueOf(strBuilder));
        return gson.toJson(producePost).toString();
    }
}
