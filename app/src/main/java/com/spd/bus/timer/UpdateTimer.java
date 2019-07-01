package com.spd.bus.timer;

import android.annotation.SuppressLint;
import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.spd.base.utils.NetWorkUtils;
import com.spd.bus.Info;
import com.spd.bus.entity.Payrecord;
import com.spd.bus.entity.UnionPay;
import com.spd.bus.sql.SqlStatement;
import com.spd.bus.util.DatabaseTabInfo;
import com.spd.bus.util.GetDriverRecord;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 记录上传
 * Created by 张明_ on 2019/6/26.
 * Email 741183142@qq.com
 */
public class UpdateTimer {
    private Disposable mDisposable;//定时器
    @SuppressLint("StaticFieldLeak")
    private static UpdateTimer intance;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private List<UnionPay> listUnionPay;
    private List<UnionPay> updataUnionRecord = new ArrayList<UnionPay>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private String driverNo;
    private GetDriverRecord gdr = new GetDriverRecord();

    public static UpdateTimer getIntance(Context context) {
        mContext = context;
        if (intance == null) {
            intance = new UpdateTimer();
        }
        return intance;
    }

    public void initTimer() {
        long period = 2 * 60 * 1000;//时间间隔
        mDisposable = Observable.interval(period, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                .subscribe(aLong -> update());
    }

    //上传
    private void update() {
        driverNo = SharedXmlUtil.getInstance(mContext)
                .read("TAGS", "00003000000151650680");
        listUnionPay = SqlStatement.getUnionPayReocrdTag();
        List<Payrecord> payRecords = SqlStatement.selectTagRecord();

        if (payRecords.size() > 0) {

        }

        if (listUnionPay.size() > 0 && driverNo.length() > 18) {
            new UploadUnionPayRecorddata().start();
        }
    }


    class UploadUnionPayRecorddata extends Thread {
        @Override
        public void run() {
            DatabaseTabInfo.getIntence("info");
            List jsonobj = new ArrayList();
            for (int i = 0; i < listUnionPay.size(); i++) {
                updataUnionRecord.add(listUnionPay.get(i));
                String stationTime = sdf.format(new Date(Long.parseLong(listUnionPay.get(i)
                        .getStationTime(), 16) * 1000));
                jsonobj.add(JSONObject.parseObject(createJsonUnionData(listUnionPay.get(i).getIsPay(), stationTime, listUnionPay.get(i).getPrimaryAcountNum(), listUnionPay.get(i).getTradingFlow()
                        , listUnionPay.get(i).getAmount(), listUnionPay.get(i).getTradingFlow(), listUnionPay.get(i).getTwoTrackData()
                        , DatabaseTabInfo.deviceNo, listUnionPay.get(i).getICCardDataDomain(), driverNo, DatabaseTabInfo.dept, DatabaseTabInfo.deviceNo
                        , DatabaseTabInfo.tream, DatabaseTabInfo.line, DatabaseTabInfo.busno, listUnionPay.get(i).getCardSerial(), listUnionPay.get(i).getResponseCode()
                        , listUnionPay.get(i).getRetrievingNum(), listUnionPay.get(i).getType(), listUnionPay.get(i).getBatchNumber())));
                if ((i % 20 == 0) || ((i + 1) >= listUnionPay.size())) {
                    if (true == NetWorkUtils.urlIsReach(Info.url1)) {
                        SoapObject rpc = new SoapObject(Info.UNION_NAMESPACE, "unionpay");
                        rpc.addProperty("data", createJsonUnion(jsonobj));
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                                SoapEnvelope.VER11);
                        envelope.dotNet = false;
                        envelope.setOutputSoapObject(rpc);
                        HttpTransportSE ht = new HttpTransportSE(Info.URL_UNION_PAY);
                        try {
                            ht.call(null, envelope);
                            if (envelope.getResponse() != null) {
                                System.out.println("銀聯ODA上傳狀態：" + envelope.getResponse());
                                org.json.JSONObject jObject = new org.json.JSONObject(
                                        String.valueOf(envelope.getResponse()));
                                String code = jObject.getString("code");
                                if (code.equals("00")) {
                                    SqlStatement.updataICUnion(updataUnionRecord);
//                                    spUtil.putString( "DOWNLOAD", "1" );
                                    updataUnionRecord.clear();
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            super.run();
        }
    }

    public String createJsonUnion(List list) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("payinfo", list);
        System.out.println("jsonObject.toString()==" + jsonObject.toString());
        return jsonObject.toString();
    }

    private String createJsonUnionData(String isPay, String transactionTime,
                                       String cardNo, String transactionCode, String transactionAmount,
                                       String serialNumber, String towTrackData, String terminalCode,
                                       String field, String driver, String dept, String posId,
                                       String team, String route, String busNo, String cardSerialNum,
                                       String responseCode, String retrievingNum, String type, String batchNumber) {
        JSONObject jsObject = new JSONObject();
        jsObject.put("isPay", isPay);// 是否付款成功 1成功 0不成功
        jsObject.put("transactionTime", transactionTime);// 交易时间
        jsObject.put("cardNo", cardNo);// 卡号
        jsObject.put("transactionCode", transactionCode);// 交易序号
        jsObject.put("transactionAmount", transactionAmount);// 交易金额
        jsObject.put("serialNumber", serialNumber);// 流水号
        jsObject.put("towTrackData", towTrackData);// 二磁道数据
        jsObject.put("threeTrackData", "");// 三磁道
        jsObject.put("terminalCode", terminalCode);// 机具POS号
        jsObject.put("field", field);// 56域
        jsObject.put("driver", driver);
        jsObject.put("dept", dept);
        jsObject.put("posId", posId);
        jsObject.put("team", team);
        jsObject.put("route", route);
        jsObject.put("busNo", busNo);
        jsObject.put("cardSerialNum", cardSerialNum);
        jsObject.put("responseCode", responseCode);
        jsObject.put("retrievingNum", retrievingNum);
        jsObject.put("type", type);
        jsObject.put("batchNumber", batchNumber);
        jsObject.put("driverSignTime", gdr.getDrivertime());
        return jsObject.toString();
    }
}
