package com.spd.bus.timer;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.spd.bus.entity.Payrecord;
import com.spd.bus.entity.UnionPay;
import com.spd.bus.sql.SqlStatement;

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
        String driverNo = SharedXmlUtil.getInstance(mContext)
                .read("TAGS", "00003000000151650680");
        List<UnionPay> listUnionPay = SqlStatement.getUnionPayReocrdTag();
        List<Payrecord> payRecords = SqlStatement.selectTagRecord();

        if (payRecords.size() > 0) {

        }

        if (listUnionPay.size() > 0 && driverNo.length() > 18) {

        }
    }
}
