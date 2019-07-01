package com.spd.bus.timer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.activeandroid.ActiveAndroid;
import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spd.base.been.tianjin.AliBlackBackBean;
import com.spd.base.been.tianjin.AliWhiteBackBean;
import com.spd.base.been.tianjin.AliWhiteBlackPost;
import com.spd.base.been.tianjin.BaseInfoBackBean;
import com.spd.base.been.tianjin.BaseInfoDataPost;
import com.spd.base.been.tianjin.BlackDB;
import com.spd.base.db.DbDaoManage;
import com.spd.base.silentinstall.ReflectUtils;
import com.spd.base.utils.AppUtils;
import com.spd.bus.Info;
import com.spd.bus.entity.Blacklist;
import com.spd.bus.net.HttpMethods;
import com.spd.base.utils.LogUtils;
import com.spd.bus.sql.SqlStatement;
import com.spd.bus.util.DatabaseTabInfo;
import com.spd.bus.util.download.DownloadUtils;
import com.spd.bus.util.download.JsDownloadListener;

import java.io.File;
import java.util.ArrayList;
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
        long period = 5 * 60 * 1000;//时间间隔
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
        DatabaseTabInfo.getIntence("info");
        Map<String, String> map = new HashMap<>();
        String posID = DatabaseTabInfo.deviceNo;
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
            map.put("program", AppUtils.getVerName(mContext));
        }

        map.put("posId", posID);
        BaseInfoDataPost baseInfoDataPost = new BaseInfoDataPost();
        baseInfoDataPost.setSim_imsi("");
        baseInfoDataPost.setBusNo("");
        baseInfoDataPost.setX_version("");
        baseInfoDataPost.setBlack(map.get("black"));
        baseInfoDataPost.setDevNumber("");
        String driversNo = SharedXmlUtil.getInstance(mContext)
                .read("TAGS", "0");
        baseInfoDataPost.setDriver(driversNo);
        baseInfoDataPost.setWhite(map.get("white"));
        baseInfoDataPost.setPsamtricardon("");
        baseInfoDataPost.setLineCardNO("");
        baseInfoDataPost.setBinVersion("");
        baseInfoDataPost.setRoute(DatabaseTabInfo.line);
        baseInfoDataPost.setSim_serial("");
        baseInfoDataPost.setPosId(posID);
        baseInfoDataPost.setPos_imei("");
        baseInfoDataPost.setDept(DatabaseTabInfo.dept);
        baseInfoDataPost.setProgram(AppUtils.getVerName(mContext));
        baseInfoDataPost.setBusCardNo("");
        baseInfoDataPost.setPsamurdcardon("");
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
                        getBlack(black, baseInfoBackBean);
                    }
                    String white = baseInfoBackBean.getWhite();
                    if (!white.equals(backBean.getWhite())) {
                        getWhite(white);
                    }

                } else {
                    getBlack(baseInfoBackBean.getBlack(), baseInfoBackBean);
                    getWhite(baseInfoBackBean.getWhite());
                }

                String program = baseInfoBackBean.getProgram();
                String[] vs = program.split("_V");
                if (vs.length >= 2) {
                    String[] split = vs[1].split(".apk");
                    if (!split[0].equals(AppUtils.getVerName(mContext))) {
                        downloadAPK(program);
                    }

                }

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

    private void getBlack(String version, BaseInfoBackBean baseInfoBackBean) {
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
                        LogUtils.v("开始存储黑名单");
                        List<BlackDB> list = new ArrayList<>();
                        String data = aliBlackBackBean.getData();

                        SqlStatement.deleteBlackCardcode();
                        ActiveAndroid.beginTransaction();
                        try {
                            for (int i = 0; i < (data.length()); ) {
                                String card_code = data.substring(i,
                                        i + 20);
                                Blacklist blacklist = new Blacklist();
                                blacklist.setBlackcode(card_code);
                                blacklist.save();
                                i = i + 20;
                            }
                            SqlStatement.updata_black(version);
                            ActiveAndroid.setTransactionSuccessful();
                            LogUtils.i("h黑名单下载安装完成");
                        } finally {
                            ActiveAndroid.endTransaction();
                        }
                        SharedXmlUtil.getInstance(mContext).write(Info.BLACK, version);
                        DbDaoManage.getDaoSession().getBaseInfoBackBeanDao().deleteAll();
                        DbDaoManage.getDaoSession().getBaseInfoBackBeanDao().insert(baseInfoBackBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.v("失败" + e.toString());
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

    public void downloadAPK(String url) {
        DownloadUtils downloadUtils = new DownloadUtils(HttpMethods.BASE_URL, new JsDownloadListener() {
            @Override
            public void onStartDownload(long length) {
                LogUtils.d(length + "");
            }

            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onFail(String errorInfo) {
                LogUtils.e(errorInfo);
            }

            @Override
            public void onSuccess() {
                LogUtils.v("下载完成");
                //静默安装
                ReflectUtils.installApk(getApkPath() + "/BUS.apk", mContext);
            }
        });
        File file = new File(getApkPath(), "BUS.apk");
        downloadUtils.download(url, file);
    }

    //文件路径
    private String getApkPath() {
        String directoryPath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {//判断外部存储是否可用
            directoryPath = mContext.getExternalFilesDir("apk").getAbsolutePath();
        } else {//没外部存储就使用内部存储
            directoryPath = mContext.getFilesDir() + File.separator + "apk";
        }
        File file = new File(directoryPath);
        LogUtils.v("测试路径" + directoryPath);
        if (!file.exists()) {//判断文件目录是否存在
            file.mkdirs();
        }
        return directoryPath;
    }
}
