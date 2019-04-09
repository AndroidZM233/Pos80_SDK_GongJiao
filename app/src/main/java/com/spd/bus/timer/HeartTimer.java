package com.spd.bus.timer;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spd.base.been.tianjin.AliBlackBackBean;
import com.spd.base.been.tianjin.AliWhiteBackBean;
import com.spd.base.been.tianjin.AliWhiteBlackPost;
import com.spd.base.been.tianjin.BaseInfoBackBean;
import com.spd.base.been.tianjin.BaseInfoDataPost;
import com.spd.base.db.DbDaoManage;
import com.spd.bus.Info;
import com.spd.bus.card.utils.HttpMethods;
import com.spd.bus.card.utils.LogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 心跳
 * Created by 张明_ on 2019/4/4.
 * Email 741183142@qq.com
 */
public class HeartTimer {
    private Disposable mDisposable;//定时器
    @SuppressLint("StaticFieldLeak")
    private static HeartTimer intance;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;


    public static HeartTimer getIntance(Context context) {
        mContext = context;
        if (intance == null) {
            intance = new HeartTimer();
        }
        return intance;
    }

    public void initTimer() {
        long period = 60 * 1000;//时间间隔
        mDisposable = Observable.interval(period, TimeUnit.MILLISECONDS)
//                .observeOn(Schedulers.newThread())
                .subscribe(aLong -> heart());
    }

    //取消订阅
    public void dispose() {
        mDisposable.dispose();
    }

    private void heart() {
        final Gson gson = new GsonBuilder().serializeNulls().create();
        Map<String, String> map = new HashMap<>();
        String posID = SharedXmlUtil.getInstance(mContext).read(Info.POS_ID, Info.POS_ID_INIT);
        List<BaseInfoBackBean> beanList = DbDaoManage.getDaoSession()
                .getBaseInfoBackBeanDao().loadAll();
        if (beanList.size() != 0) {
            BaseInfoBackBean baseInfoBackBean = beanList.get(0);
            map.put("black", baseInfoBackBean.getBlack());
            map.put("white", baseInfoBackBean.getWhite());
            map.put("program", baseInfoBackBean.getProgram());
        } else {
            map.put("black", "20180927");
            map.put("white", "20180927");
            map.put("program", "1.2.79");
        }

        map.put("posId", posID);
        BaseInfoDataPost baseInfoDataPost = new BaseInfoDataPost();
        map.put("data", gson.toJson(baseInfoDataPost));

        HttpMethods.getInstance().baseinfo(map, new Observer<BaseInfoBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseInfoBackBean baseInfoBackBean) {
                if (beanList.size() != 0) {
                    BaseInfoBackBean backBean = beanList.get(0);
                    String black = baseInfoBackBean.getBlack();
                    if (!black.equals(backBean.getBlack())) {
                        getBlack(black);
                    }
                    String white = baseInfoBackBean.getWhite();
                    if (!white.equals(backBean.getWhite())) {
                        getWhite(white);
                    }
                } else {
                    getBlack(baseInfoBackBean.getBlack());
                    getWhite(baseInfoBackBean.getWhite());
                }
                DbDaoManage.getDaoSession().getBaseInfoBackBeanDao().deleteAll();
                DbDaoManage.getDaoSession().getBaseInfoBackBeanDao().insert(baseInfoBackBean);

                LogUtils.d("成功" + gson.toJson(baseInfoBackBean));
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.d("失败" + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void getBlack(String version) {
        final Gson gson = new GsonBuilder().serializeNulls().create();
        AliWhiteBlackPost aliWhiteBlackPost = new AliWhiteBlackPost();
        String posID = SharedXmlUtil.getInstance(mContext).read(Info.POS_ID, Info.POS_ID_INIT);
        aliWhiteBlackPost.setPosid(posID);
        aliWhiteBlackPost.setVersion(version);
        HttpMethods.getInstance().black(gson.toJson(aliWhiteBlackPost)
                , new Observer<AliBlackBackBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AliBlackBackBean aliBlackBackBean) {
                        DbDaoManage.getDaoSession().getAliBlackBackBeanDao().deleteAll();
                        DbDaoManage.getDaoSession().getAliBlackBackBeanDao().insert(aliBlackBackBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.d("失败" + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void getWhite(String version) {
        final Gson gson = new GsonBuilder().serializeNulls().create();
        AliWhiteBlackPost aliWhiteBlackPost = new AliWhiteBlackPost();
        String posID = SharedXmlUtil.getInstance(mContext).read(Info.POS_ID, Info.POS_ID_INIT);
        aliWhiteBlackPost.setPosid(posID);
        aliWhiteBlackPost.setVersion(version);
        HttpMethods.getInstance().white(gson.toJson(aliWhiteBlackPost)
                , new Observer<AliWhiteBackBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AliWhiteBackBean aliWhiteBackBean) {
                        DbDaoManage.getDaoSession().getAliWhiteBackBeanDao().deleteAll();
                        DbDaoManage.getDaoSession().getAliWhiteBackBeanDao().insert(aliWhiteBackBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.d("失败" + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
