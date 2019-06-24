package com.spd.bus.spdata;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.honeywell.barcode.HSMDecodeResult;
import com.honeywell.plugins.decode.DecodeResultListener;
import com.spd.alipay.been.TianjinAlipayRes;
import com.spd.base.been.WechatQrcodeKey;
import com.spd.base.been.tianjin.CardBackBean;
import com.spd.base.been.tianjin.TCardOpDU;
import com.spd.base.been.tianjin.TStaffTb;
import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.AppUtils;
import com.spd.base.utils.Datautils;
import com.spd.base.utils.LogUtils;
import com.spd.base.utils.ToastUtil;
import com.spd.base.view.SignalView;
import com.spd.bus.Info;
import com.spd.bus.MyApplication;
import com.spd.bus.R;
import com.spd.bus.card.methods.JTBCardManager;
import com.spd.bus.card.methods.M1CardManager;
import com.spd.bus.card.methods.ReturnVal;
import com.spd.bus.spdata.been.ErroCode;
import com.spd.bus.spdata.mvp.MVPBaseActivity;
import com.spd.bus.spdata.showdata.ShowDataActivity;
import com.spd.bus.spdata.spdbuspay.SpdBusPayContract;
import com.spd.bus.spdata.spdbuspay.SpdBusPayPresenter;
import com.spd.bus.util.ConfigUtils;
import com.spd.bus.util.DataUploadToTianJinUtils;
import com.spd.bus.util.PlaySound;
import com.spd.bus.util.SaveDataUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import wangpos.sdk4.libbasebinder.BankCard;

import static com.spd.bus.spdata.been.ErroCode.ILLEGAL_PARAM;
import static com.spd.bus.spdata.been.ErroCode.NO_ENOUGH_MEMORY;
import static com.spd.bus.spdata.been.ErroCode.SYSTEM_ERROR;

public class PsamIcActivity extends MVPBaseActivity<SpdBusPayContract.View, SpdBusPayPresenter> implements SpdBusPayContract.View, DecodeResultListener {
    private static final String TAG = "SPEEDATA_BUS";

    /**
     * 消费返回流程错误标识
     */
    private int isFlag = 0;

    private SignalView mXinhao;
    /**
     * 636路
     */
    private TextView mTvLine;
    /**
     * 10:52:50
     */
    private TextView mTvTime;
    /**
     * 2018年12月06日
     */
    private TextView mTvDate;
    /**
     * 票价
     */
    private TextView mTvBalanceTitle;
    /**
     * 1.00元
     */
    private TextView mTvBalance;
    private TextView mTvDeviceMessage;
    private LinearLayout mLayoutFace;
    private LinearLayout mLayoutLineInfo;
    /**
     * 消费额
     */
    private TextView mTvXiaofeiTitle;
    /**
     * 0.99元
     */
    private TextView mTvXiaofeiMoney;
    private LinearLayout mLayoutXiaofei;

    private String balance = "2元";
    private float balanceFloat = 0.0f;
    private LinearLayout mLlDriver;
    private LinearLayout mLlMain;
    private boolean isDriverUI = false;
    private LinearLayout mLlShowData;
    private boolean isShowDataUI = false;
    private boolean isConfigChange;
    private TextView mTvTitle;
    private boolean isQianDao = false;
    //——————————————————————————————————————分界线 单片机方案
    boolean key;
    public static int unionTag = 0;
    private int intPrices;//扣款价格
    private double priceDou;//double price
    private String driversNo = "";
    private String busNo = "";
    private int fieldLength = 0;
    private int countLength = 0;
    private String uninonSign;
    private String signRecord;//司机记录
    private String driverSignTime;//司机签到时间
    private String grcardcode = "00000000000000000000";
    private String getIcCardResult;//尋卡結果
    private String stateRfid;//尋卡結果状态
    private String rfidDectValue;//消费寻卡结果
    private String rfidDectState;//消費結果狀態
    private int whitelists;//白名單查詢結果
    private int unionSignResult;//union签到结果
    private int len;//银联卡数据总长度---暂时未用到
    private String smRecord;//双免记录
    private String primaryAcountNum;//主账号
    private String amount;//交易金额
    private String tradingFlow;//交易流水
    private String stationTime;//进站时间
    private String cardSerial;//卡片序号
    private String TwoTrackData;//二磁道数据
    private String ICCardDataDomain;//ic卡数据域
    private String batchNumber;//批次号
    private String cardType;//ic卡類型
    private String cardCode;//卡号

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spd_bus_layout);
        isConfigChange = SharedXmlUtil.getInstance(getApplicationContext()).read(
                Info.IS_CONFIG_CHANGE, false);

        List<TStaffTb> tStaffTbs = DbDaoManage.getDaoSession().getTStaffTbDao().loadAll();
        if (tStaffTbs.size() == 0) {
            isQianDao = false;
        } else {
            isQianDao = true;
        }
        initView();
        initCard();
        init();
    }

    private void init() {
        priceDou = intPrices / 100.00;
    }

    @Override
    protected void onResume() {
        super.onResume();
        key = true;
        MyThread myThread = new MyThread();
        myThread.start();
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (key) {
                {
                    getIcCardResult = com.yht.q6jni.Jni.Rfidcard();
                    if (getIcCardResult.length() > 38) {
                        stateRfid = getIcCardResult.substring(0, 2);
                        // Logger.i( "stateRfid"+stateRfid );
                        if (stateRfid.equals("00")) {
                            if ("90".equals(getIcCardResult.substring(44, 46))) {
                                if (intPrices != 0 && intPrices < 100000 && !"000000".equals(busNo)) {
                                    if (driversNo.length() > 16) {
                                        unionTag = 1;
                                        PlaySound.play(PlaySound.ZHENGZAICHULI, 0);
                                        len = Integer.parseInt(getIcCardResult.substring(46, 50), 16) * 2 + 50;
                                        //双免记录长度
                                        fieldLength = Integer.parseInt(getIcCardResult.substring(50, 54), 16);
                                        //双免记录
                                        smRecord = getIcCardResult.substring(54, 54 + fieldLength * 2);
                                        countLength = fieldLength * 2 + 54;
                                        //主賬號長度
                                        fieldLength = Integer.parseInt(getIcCardResult.substring(countLength, countLength + 4), 16);
                                        countLength = countLength + 4;
                                        //主賬號
                                        primaryAcountNum = getIcCardResult.substring(countLength, countLength + fieldLength * 2);
                                        if (primaryAcountNum.indexOf("f") != -1) {
                                            primaryAcountNum = primaryAcountNum.substring(0, primaryAcountNum.lastIndexOf("f"));
                                        }
                                        countLength = countLength + fieldLength * 2;
                                        //交易金额长度
                                        fieldLength = Integer.parseInt(getIcCardResult.substring(countLength, countLength + 4), 16);
                                        countLength = countLength + 4;
                                        //交易金额
                                        amount = getIcCardResult.substring(countLength, countLength + fieldLength * 2);
                                        countLength = countLength + fieldLength * 2;
                                        //交易流水长度
                                        fieldLength = Integer.parseInt(getIcCardResult.substring(countLength, countLength + 4), 16);
                                        countLength = countLength + 4;
                                        // 交易流水
                                        tradingFlow = getIcCardResult.substring(countLength, countLength + fieldLength * 2);
                                        countLength = countLength + fieldLength * 2;
                                        // 进站时间长度
                                        fieldLength = Integer.parseInt(getIcCardResult.substring(countLength, countLength + 4), 16);
                                        countLength = countLength + 4;
                                        // 进站时间
                                        stationTime = getIcCardResult.substring(countLength, countLength + fieldLength * 2);
                                        countLength = countLength + fieldLength * 2;
                                        // 卡片序列号长度
                                        fieldLength = Integer.parseInt(getIcCardResult.substring(countLength, countLength + 4), 16);
                                        countLength = countLength + 4;
                                        // 卡片序列号
                                        cardSerial = getIcCardResult.substring(countLength, countLength + fieldLength * 2);
                                        countLength = countLength + fieldLength * 2;
                                        //二磁道數據長度
                                        fieldLength = Integer.parseInt(getIcCardResult.substring(countLength, countLength + 4), 16);
                                        countLength = countLength + 4;
                                        // 二磁道数据

                                        TwoTrackData = getIcCardResult.substring(countLength, countLength + fieldLength * 2);
                                        if (TwoTrackData.indexOf("f") != -1) {
                                            TwoTrackData = TwoTrackData.substring(0, TwoTrackData.lastIndexOf("f"));
                                        }
                                        countLength = countLength + fieldLength * 2;
                                        //IC卡數據與長度
                                        fieldLength = Integer.parseInt(getIcCardResult.substring(countLength, countLength + 4), 16);
                                        countLength = countLength + 4;
                                        //IC卡数据域
                                        ICCardDataDomain = getIcCardResult.substring(countLength, countLength + fieldLength * 2);
                                        countLength = countLength + fieldLength * 2;
                                        //批次號長度
                                        fieldLength = Integer.parseInt(getIcCardResult.substring(countLength, countLength + 4), 16);
                                        countLength = countLength + 4;
                                        //批次號
                                        batchNumber = getIcCardResult.substring(countLength, countLength + fieldLength * 2);
                                        // TODO: 2019/6/24 记录存储

                                        // TODO: 2019/6/24 判断走双免还是ODA
                                        if (uninonSign.equals("1")) {
//                                            new UnionSocketThread(tradingFlow, smRecord, primaryAcountNum).start();
                                            // TODO: 2019/6/24 上传记录
                                        } else {
                                            // TODO: 2019/6/24 判断是不是黑名单
//                                            if (SqlStatement.SelectUnionBlack(primaryAcountNum) == 0) {
//                                                SqlStatement.updataUnionODASUC(tradingFlow);
//                                                handler.sendMessage(handler.obtainMessage(7, "ODA"));
//                                                SystemClock.sleep(650);
//                                                unionTag = 0;
//                                            } else {
//                                                unionTag = 0;
//                                                handler.sendMessage(handler
//                                                        .obtainMessage(8, "无效卡号"));
//                                                SystemClock.sleep(550);
//                                            }
                                        }
                                    } else {
                                        PlaySound.play(PlaySound.QINGQIANDAO, 0);
                                    }
                                } else {
                                    PlaySound.play(PlaySound.QINGSHEZHI, 0);
                                }
                                continue;
                            }
                            cardType = getIcCardResult.substring(2, 4);
                            cardCode = getIcCardResult.substring(4, 24);
                            // 0本地卡，1外地卡.住建部 0 交通部 1
                            if ("01".equals(getIcCardResult.substring(26, 28))) {
//                                if (("01".equals(getIcCardResult.substring(24, 26)))) {
//                                    whitelists = SqlStatement
//                                            .SelectCardWhite(new StringBuffer().append("003").append(
//                                                    getIcCardResult.substring(30, 38)).toString());
//                                    Logger.i("whitelists=" + whitelists);
//                                    if ((whitelists != 1)) {
//                                        handler.sendMessage(handler
//                                                .obtainMessage(8,
//                                                        "无效卡bai"));
//                                        continue;
//                                    }
//                                } else if ("00".equals(getIcCardResult.substring(24, 26))) {
//                                    whitelists = SqlStatement
//                                            .SelectCardWhite(new StringBuffer().append("001").append(getIcCardResult.substring(30, 38)).toString());
//                                    if ((whitelists != 1)) {
//                                        handler.sendMessage(handler
//                                                .obtainMessage(8,
//                                                        "无效卡bai"));
//                                        continue;
//                                    }
//                                }
                            }
                            if (driversNo.equals(cardCode)) {
                                // 当班司机卡不能消费
                                handler.sendMessage(handler
                                        .obtainMessage(12, "当班司机"));
                                continue;
                            }
                            if (cardType.equals("91")) {
                                // 请设置车辆号
                                key = false;
//                                startActivity(intent.setClass(MainActivity.this, SettingActivity.class));
                                continue;
                            }
                            if (signRecord.length() < 120) {
                                PlaySound.play(PlaySound.QINGQIANDAO, 0);
                                continue;
                            }
                            //消费
                            if (intPrices != 0 && intPrices < 100000 && !"000000".equals(busNo)) {
                                if (driversNo.length() > 16) {
                                    // TODO: 2019/6/24  1.查询本次交易卡是否是黑名单
//                                    rfidDectValue = com.yht.q6jni.Jni.RfidDectValue(SqlStatement.SelectCard(cardCode));
                                    if (rfidDectValue.length() > 2) {
                                        rfidDectState = rfidDectValue.substring(0, 2);
                                        if ("00".equals(rfidDectState)) {
//                                            if (cardCode.equalsIgnoreCase(grcardcode)) {
//                                                counts++;
//                                            } else {
//                                                counts = 1;
//                                            }
                                            grcardcode = cardCode;
                                            handler.sendMessage(handler.obtainMessage(2, rfidDectValue));
                                            SystemClock.sleep(350);
                                            continue;
                                        }
                                        if ("0a".equalsIgnoreCase(rfidDectState)) {
                                            double tb_yue_xs1 = (Integer.parseInt(rfidDectValue.substring(8, 12), 16) / 100.00);
                                            if (tb_yue_xs1 > priceDou) {
                                                tb_yue_xs1 = 0.0;
                                            }
                                            handler.sendMessage(handler.obtainMessage(12, "消费：0元\n剩余：" + tb_yue_xs1 + "元"));
                                            continue;
                                        }
                                        if ("0b".equalsIgnoreCase(rfidDectState)) {
                                            handler.sendMessage(handler.obtainMessage(9, rfidDectValue));
                                            continue;
                                        }
                                        if ("0d".equalsIgnoreCase(rfidDectState)) {
                                            handler.sendMessage(handler.obtainMessage(12, "投币0d"));
                                            continue;
                                        }
                                        if ("09".equals(rfidDectState)) {
                                            handler.sendMessage(handler.obtainMessage(12, "未启用09"));
                                            continue;
                                        }
                                        if ("08".equals(rfidDectState)) {
                                            handler.sendMessage(handler.obtainMessage(12, "卡过期08"));
                                            continue;
                                        }
                                        if ("07".equals(rfidDectState)) {
                                            handler.sendMessage(handler.obtainMessage(8, "warning07"));
                                            continue;
                                        }
                                        if ("06".equals(rfidDectState) || "05".equals(rfidDectState)) {
                                            handler.sendMessage(handler.obtainMessage(8, rfidDectValue));
                                            continue;
                                        }
                                        if ("02".equals(rfidDectState) || "03".equals(rfidDectState) || "04".equals(rfidDectState)) {
                                            handler.sendMessage(handler.obtainMessage(9, "warning234"));
                                            continue;
                                        }
                                        if ("fb".equalsIgnoreCase(rfidDectState)) {
                                            handler.sendMessage(handler.obtainMessage(9, "fb"));
                                            continue;
                                        }
                                        if ("90".equals(rfidDectState)) {
                                            PlaySound.play(PlaySound.ZHENGZAICHULI, 0);
                                            len = Integer.parseInt(rfidDectValue.substring(2, 6), 16) * 2 + 2;
                                            //双免记录长度
                                            fieldLength = Integer.parseInt(rfidDectValue.substring(6, 10), 16);
                                            //双免记录
                                            smRecord = rfidDectValue.substring(10, 10 + fieldLength * 2);
                                            countLength = fieldLength * 2 + 10;
                                            //主賬號長度
                                            fieldLength = Integer.parseInt(rfidDectValue.substring(countLength, countLength + 4), 16);
                                            countLength = countLength + 4;
                                            //主賬號
                                            primaryAcountNum = rfidDectValue.substring(countLength, countLength + fieldLength * 2);
                                            if (primaryAcountNum.indexOf("f") != -1) {
                                                primaryAcountNum = primaryAcountNum.substring(0, primaryAcountNum.lastIndexOf("f"));
                                            }
                                            countLength = countLength + fieldLength * 2;
                                            //交易金额长度
                                            fieldLength = Integer.parseInt(rfidDectValue.substring(countLength, countLength + 4), 16);
                                            countLength = countLength + 4;
                                            //交易金额
                                            amount = rfidDectValue.substring(countLength, countLength + fieldLength * 2);
                                            countLength = countLength + fieldLength * 2;
                                            //交易流水长度
                                            fieldLength = Integer.parseInt(rfidDectValue.substring(countLength, countLength + 4), 16);
                                            countLength = countLength + 4;
                                            // 交易流水
                                            tradingFlow = rfidDectValue.substring(countLength, countLength + fieldLength * 2);
                                            countLength = countLength + fieldLength * 2;
                                            // 进站时间长度
                                            fieldLength = Integer.parseInt(rfidDectValue.substring(countLength, countLength + 4), 16);
                                            countLength = countLength + 4;
                                            // 进站时间
                                            stationTime = rfidDectValue.substring(countLength, countLength + fieldLength * 2);
                                            countLength = countLength + fieldLength * 2;
                                            // 卡片序列号长度
                                            fieldLength = Integer.parseInt(rfidDectValue.substring(countLength, countLength + 4), 16);
                                            countLength = countLength + 4;
                                            // 卡片序列号
                                            cardSerial = rfidDectValue.substring(countLength, countLength + fieldLength * 2);
                                            countLength = countLength + fieldLength * 2;
                                            //二磁道數據長度
                                            fieldLength = Integer.parseInt(rfidDectValue.substring(countLength, countLength + 4), 16);
                                            countLength = countLength + 4;
                                            // 二磁道数据

                                            TwoTrackData = rfidDectValue.substring(countLength, countLength + fieldLength * 2);
                                            if (TwoTrackData.indexOf("f") != -1) {
                                                TwoTrackData = TwoTrackData.substring(0, TwoTrackData.lastIndexOf("f"));
                                            }
                                            countLength = countLength + fieldLength * 2;
                                            //IC卡數據與長度
                                            fieldLength = Integer.parseInt(rfidDectValue.substring(countLength, countLength + 4), 16);
                                            countLength = countLength + 4;
                                            //IC卡数据域
                                            ICCardDataDomain = rfidDectValue.substring(countLength, countLength + fieldLength * 2);
                                            countLength = countLength + fieldLength * 2;
                                            //批次號長度
                                            fieldLength = Integer.parseInt(rfidDectValue.substring(countLength, countLength + 4), 16);
                                            countLength = countLength + 4;
                                            //批次號
                                            batchNumber = rfidDectValue.substring(countLength, countLength + fieldLength * 2);
                                            // TODO: 2019/6/24 ODA存储
                                            if (uninonSign.equals("1")) {
                                                // TODO: 2019/6/24 上传记录
//                                                new UnionSocketThread(tradingFlow, smRecord, primaryAcountNum).start();
                                            } else {
                                                // TODO: 2019/6/24 黑名单判断
//                                                if (SqlStatement.SelectUnionBlack(primaryAcountNum) == 0) {
//                                                    SqlStatement.updataUnionODASUC(tradingFlow);
//                                                    handler.sendMessage(handler.obtainMessage(7, "ODA"));
//                                                    SystemClock.sleep(650);
//                                                    unionTag = 0;
//                                                } else {
//                                                    unionTag = 0;
//                                                    handler.sendMessage(handler
//                                                            .obtainMessage(8, "无效卡号"));
//                                                    SystemClock.sleep(550);
//                                                }
                                            }
                                            continue;
                                        }
                                    }
                                } else {
                                    PlaySound.play(PlaySound.QINGQIANDAO, 0);
                                }
                            } else {
                                PlaySound.play(PlaySound.QINGSHEZHI, 0);
                            }
                            continue;
                        }
                        if ("7f".equalsIgnoreCase(stateRfid)) {
                            // TODO: 2019/6/24 银联签到
//                            new UnionSign(getIcCardResult.substring(6, 6 + Integer.parseInt(getIcCardResult.substring(2, 6), 16) * 2)).start();
                            SystemClock.sleep(300);
                            continue;
                        }
                        if ("09".equalsIgnoreCase(stateRfid)) {
                            handler.sendMessage(handler.obtainMessage(12, "未启用09"));
                            continue;
                        }
                        if ("08".equalsIgnoreCase(stateRfid)) {
                            handler.sendMessage(handler.obtainMessage(12, "卡过期08"));
                            continue;
                        }
                        if ("07".equalsIgnoreCase(stateRfid)) {
                            handler.sendMessage(handler.obtainMessage(8, "warning07"));
                            continue;
                        }
                        if ("06".equalsIgnoreCase(stateRfid) || "05".equalsIgnoreCase(stateRfid)) {
                            handler.sendMessage(handler.obtainMessage(8, "无效卡56"));
                            continue;
                        }
                        if ("03".equalsIgnoreCase(stateRfid) || "02".equalsIgnoreCase(stateRfid) || "04".equalsIgnoreCase(stateRfid)) {
                            handler.sendMessage(handler.obtainMessage(9, "warning234"));
                            continue;
                        }
                        if ("fb".equalsIgnoreCase(stateRfid)) {
                            handler.sendMessage(handler.obtainMessage(9, "warningfb"));
                            continue;
                        }
                        if ("26".equalsIgnoreCase(stateRfid)) {
                            handler.sendMessage(handler.obtainMessage(9, "warningfb"));
                            continue;
                        }

                    }

                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void initView() {

        mTvBalance = findViewById(R.id.tv_balance);
        mXinhao = (SignalView) findViewById(R.id.xinhao);
        mTvLine = (TextView) findViewById(R.id.tv_line);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvDate = (TextView) findViewById(R.id.tv_date);
        mTvBalanceTitle = (TextView) findViewById(R.id.tv_balance_title);
        mTvBalance = (TextView) findViewById(R.id.tv_balance);
        mTvDeviceMessage = (TextView) findViewById(R.id.tv_device_message);
        mLayoutFace = (LinearLayout) findViewById(R.id.layout_face);
        mLayoutLineInfo = (LinearLayout) findViewById(R.id.layout_line_info);
        mTvXiaofeiTitle = (TextView) findViewById(R.id.tv_xiaofei_title);
        mTvXiaofeiMoney = (TextView) findViewById(R.id.tv_xiaofei_money);
        mLayoutXiaofei = (LinearLayout) findViewById(R.id.layout_xiaofei);
        Datautils.getCurrentNetDBM(this, mXinhao);
        updateConfigUI();
        mLlDriver = findViewById(R.id.ll_driver);
        mLlMain = findViewById(R.id.ll_main);
        mLlShowData = findViewById(R.id.ll_show_data);
//        mLlSetConfig = findViewById(R.id.ll_set_config);
        mTvTitle = findViewById(R.id.tv_title);
        mTvTitle.setText("版本号：" + AppUtils.getVerName(getApplicationContext()));
    }


    private void initCard() {
        try {
            MyApplication.getHSMDecoder().addResultListener(PsamIcActivity.this);
            //注册系统时间广播 只能动态注册
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    startTimer(true);
                }
            }).start();

            updateTime();
            mPresenter.wechatInitJin();
            mPresenter.aliPayInitJni();
            mPresenter.uploadSM(getApplicationContext());
            mPresenter.uploadAlipayRe(getApplicationContext());
            mPresenter.uploadWechatRe(getApplicationContext());
            mPresenter.uploadYinLian(getApplicationContext());
            DataUploadToTianJinUtils.uploadCardData(getApplicationContext());

        } catch (Exception e) {
            e.printStackTrace();
//            DataUploadToTianJinUtils.postLog(getApplicationContext(), LogUtils.generateTag()
//                    + e.toString());
        }
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mLayoutLineInfo.setVisibility(View.VISIBLE);
            mLayoutFace.setVisibility(View.GONE);
            mLayoutXiaofei.setVisibility(View.GONE);
            mTvBalanceTitle.setText("票价");
            mTvBalance.setText(balance);
        }
    };

    Runnable runnableScan = new Runnable() {
        @Override
        public void run() {
            MyApplication.getHSMDecoder().addResultListener(PsamIcActivity.this);
        }
    };

    @SuppressLint("CheckResult")
    private void updateTime() {
        Flowable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        mTvDate.setText(Datautils.getData());
                        mTvTime.setText(Datautils.getTime());
                    }
                });

    }


    private long ltime = 0;

    private void startTimer(boolean isStart) {
//        while (isStart) {
//            try {
//                //非接卡在位检测;
//                int result = MyApplication.mBankCard.piccDetect();
//                if (result == 0) {
//                    isFlag = 2;
//                } else if (result == 1 && isFlag == 2) {
//                    ltime = System.currentTimeMillis();
//                    LogUtils.v("开始本次读卡等待");
//                    //切换到非接卡读取
//                    retvalue = MyApplication.mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC, 1, respdata, resplen, "app1");
//                    if (retvalue != 0) {
//                        isFlag = 1;
//                        return;
//                    }
//                    if (!isDriverUI) {
//                        if (!isQianDao) {
//                            doVal(new CardBackBean(ReturnVal.CAD_QINGQIANDAO, null));
//                            isFlag = 0;
//                            continue;
//                        }
//                    }
//                    ToastUtil.cancelToast();
//                    //检测到非接IC卡
//                    if (respdata[0] == 0x07) {
//                        CardBackBean cardBackBean = null;
//                        try {
////                            if (MyApplication.psamDatas.size() != 2) {
////                                doVal(new CardBackBean(ReturnVal.CAD_PSAM_ERROR, null));
////                                isFlag = 0;
////                                continue;
////                            }
//
//                            if (isConfigChange) {
//                                doVal(new CardBackBean(ReturnVal.CAD_QINGQIANDAO, null));
//                                isFlag = 0;
//                                continue;
//                            }
//                            if (isDriverUI || isShowDataUI) {
//                                isFlag = 0;
//                                continue;
//                            }
//
//                            LogUtils.v("CPU结束寻卡===" + (System.currentTimeMillis() - ltime));
//                            cardBackBean = JTBCardManager.getInstance()
//                                    .mainMethod(getApplicationContext(), MyApplication.mBankCard
//                                            , MyApplication.psamDatas, MyApplication.getYinLianPayManage()
//                                            , handler);
//                            if (cardBackBean == null) {
//                                continue;
//                            } else {
//                                doVal(cardBackBean);
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
////                            ConfigUtils.logWrite(e.toString());
////                            DataUploadToTianJinUtils.postLog(getApplicationContext(),
////                                    LogUtils.generateTag() + e.toString());
//                        }
//
//                        isFlag = 0;
////                        if (cardBackBean != null) {
////                            if (cardBackBean.getBackValue() != ReturnVal.CAD_SM) {
////                                isFlag = 0;
////                            }
////                        } else {
////                            isFlag = 0;
////                        }
//                    } else if (respdata[0] == 0x37) {
//                        //检测到 M1-S50 卡
////                        if (MyApplication.psamDatas.size() != 2) {
////                            doVal(new CardBackBean(ReturnVal.CAD_PSAM_ERROR, null));
////                            isFlag = 0;
////                            continue;
////                        }
//                        if (isShowDataUI) {
//                            continue;
//                        }
//                        LogUtils.v("m1结束寻卡===" + (System.currentTimeMillis() - ltime));
//                        CardBackBean cardBackBean = null;
//                        try {
//                            if (isDriverUI) {
//                                cardBackBean = M1CardManager.getInstance()
//                                        .mainMethod(getApplicationContext(), MyApplication.mBankCard
//                                                , M1CardManager.M150, 0
//                                                , MyApplication.psamDatas, isConfigChange);
//                                doVal(cardBackBean);
//                            } else {
//                                cardBackBean = M1CardManager.getInstance()
//                                        .mainMethod(getApplicationContext(), MyApplication.mBankCard
//                                                , M1CardManager.M150, 2
//                                                , MyApplication.psamDatas, isConfigChange);
//                                doVal(cardBackBean);
//                            }
//
//                        } catch (Exception e) {
//                            LogUtils.v(e.toString());
////                            ConfigUtils.logWrite(e.toString());
////                            DataUploadToTianJinUtils.postLog(getApplicationContext(),
////                                    LogUtils.generateTag() + e.toString());
//                            e.printStackTrace();
//                        }
//                        isFlag = 0;
//
//
//                    } else if (respdata[0] == 0x47) {
//                        // 检测到 M1-S70 卡
////                        if (MyApplication.psamDatas.size() != 2) {
////                            doVal(new CardBackBean(ReturnVal.CAD_PSAM_ERROR, null));
////                            isFlag = 0;
////                            continue;
////                        }
//                        if (isShowDataUI) {
//                            continue;
//                        }
//                        try {
//                            if (isDriverUI) {
//                                CardBackBean cardBackBean = M1CardManager.getInstance()
//                                        .mainMethod(getApplicationContext(), MyApplication.mBankCard
//                                                , M1CardManager.M170, 0
//                                                , MyApplication.psamDatas, isConfigChange);
//                                doVal(cardBackBean);
//                            } else {
//                                CardBackBean cardBackBean = M1CardManager.getInstance()
//                                        .mainMethod(getApplicationContext(), MyApplication.mBankCard
//                                                , M1CardManager.M170, 2
//                                                , MyApplication.psamDatas, isConfigChange);
//                                doVal(cardBackBean);
//                            }
//
//                        } catch (Exception e) {
//                            LogUtils.v(e.toString());
//                            ConfigUtils.logWrite(e.toString());
//                            e.printStackTrace();
//                        }
//                        isFlag = 0;
//                    }
//                }
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
    }


    private void doVal(CardBackBean cardBackBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int value = cardBackBean.getBackValue();

                switch (value) {
                    case ReturnVal.CAD_READ:
//                        logToNet(cardBackBean);
                        break;
                    case ReturnVal.CAD_EXPIRE:
                        ToastUtil.customToastView(PsamIcActivity.this, "卡过期"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        PlaySound.play(PlaySound.QINGTOUBI, 0);
                        logToNet(cardBackBean);
                        break;
                    case ReturnVal.CAD_SELL:
                        ToastUtil.customToastView(PsamIcActivity.this, "未启用"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        PlaySound.play(PlaySound.QINGTOUBI, 0);
                        logToNet(cardBackBean);
                        break;
                    case ReturnVal.CAD_OK:
//                        MediaPlayer player = MediaPlayer.create(getApplication(), R.raw.xueshengka);
//                        player.setVolume(1, 1);
//                        player.start();//开始播放
//                        PlaySound.play(PlaySound.initerro, 0);
                        handler.removeCallbacks(runnable);
                        updateUI(cardBackBean);
                        break;
                    case ReturnVal.CAD_RETRY:
                        PlaySound.play(PlaySound.qingchongshua, 0);
                        ToastUtil.customToastView(PsamIcActivity.this, "请重刷"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        logToNet(cardBackBean);
                        break;
                    case ReturnVal.CAD_SETCOK:
                        PlaySound.play(PlaySound.setSuccess, 0);

                        ToastUtil.customToastView(PsamIcActivity.this, "设置成功"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        SharedXmlUtil.getInstance(getApplicationContext()).write(
                                Info.IS_CONFIG_CHANGE, true);
                        isConfigChange = true;

                        updateConfigUI();
                        mTvBalanceTitle.setText("");
                        mTvBalance.setText("请签到");
                        break;
                    case ReturnVal.CODE_WEIXIN_SUCCESS:
                        PlaySound.play(PlaySound.MASHANGYIXING, 0);
                        codeChangeUI();
                        break;
                    case ReturnVal.CODE_ZHIFUBAO_SUCCESS:
                        PlaySound.play(PlaySound.ZHIFUBAO, 0);
                        codeChangeUI();
                        LogUtils.v("支付宝消费成功");
                        break;
                    case ReturnVal.CODE_YINLAIN_SUCCESS:
                        LogUtils.d("CODE_YINLAIN_SUCCESS");
                        PlaySound.play(PlaySound.YINLIAN, 0);
                        codeChangeUI();
                        break;
                    case ReturnVal.CAD_REUSE:
                        LogUtils.d("CAD_REUSE");
                        ToastUtil.customToastView(PsamIcActivity.this, "请投币"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        PlaySound.play(PlaySound.QINGTOUBI, 0);
                        break;
                    case ReturnVal.CAD_LOGON:
                        PlaySound.play(PlaySound.SIJISHANGBAN, 0);
                        ToastUtil.customToastView(PsamIcActivity.this, "司机签到成功"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        SharedXmlUtil.getInstance(getApplicationContext()).write(
                                Info.IS_CONFIG_CHANGE, false);
                        SharedXmlUtil.getInstance(getApplicationContext())
                                .write(Info.DRIVER_YUE, 0);
                        SharedXmlUtil.getInstance(getApplicationContext())
                                .write(Info.DRIVER_PEOPLE, 0);
                        SharedXmlUtil.getInstance(getApplicationContext())
                                .write(Info.DRIVER_MONEY, 0.0f);
                        isConfigChange = false;
                        isQianDao = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mTvBalanceTitle.setText("票价");
                                mTvBalance.setText(balance);
                                mLlShowData.setVisibility(View.GONE);
                                isShowDataUI = false;
                                isDriverUI = false;
                                mLlDriver.setVisibility(View.GONE);
                            }
                        }, 1000);
                        break;
                    case ReturnVal.CODE_PLEASE_SET:
                        PlaySound.play(PlaySound.QINGSHEZHI, 0);
                        ToastUtil.customToastView(PsamIcActivity.this, "请先设置"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        break;
                    case ReturnVal.CAD_PSAM_ERROR:
                        ToastUtil.customToastView(PsamIcActivity.this, "PSAM-0"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        logToNet(cardBackBean);
                        break;
                    case ReturnVal.CAD_MAC1:
                    case ReturnVal.CAD_MAC2:
                        ToastUtil.customToastView(PsamIcActivity.this, "MAC错误"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        PlaySound.play(PlaySound.QINGTOUBI, 0);
                        logToNet(cardBackBean);
                        break;
                    case ReturnVal.CAD_BL1:
                    case ReturnVal.CAD_BL2:
                    case ReturnVal.CAD_BROKEN:
                        ToastUtil.customToastView(PsamIcActivity.this, "无效卡"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        PlaySound.play(PlaySound.WUXIAOKA, 0);
                        break;

                    case ReturnVal.CAD_EMPTY:
                        ToastUtil.customToastView(PsamIcActivity.this, "请投币"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        PlaySound.play(PlaySound.QINGTOUBI, 0);
                        logToNet(cardBackBean);
                        break;
                    case ReturnVal.CAD_DUTY:
                        ToastUtil.customToastView(PsamIcActivity.this, "当班司机"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        PlaySound.play(PlaySound.QINGTOUBI, 0);
                        logToNet(cardBackBean);
                        break;
                    case ReturnVal.CAD_QINGQIANDAO:
                        ToastUtil.customToastView(PsamIcActivity.this, "请签到"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        PlaySound.play(PlaySound.QINGQIANDAO, 0);
                        break;
                    case ReturnVal.CAD_WRITE:
                        logToNet(cardBackBean);
                        break;
                    case ReturnVal.CAD_HLERR:
                        ToastUtil.customToastView(PsamIcActivity.this, "请投币-1"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        PlaySound.play(PlaySound.QINGTOUBI, 0);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void codeChangeUI() {
        mTvXiaofeiTitle.setText("消费");
        mTvXiaofeiMoney.setText(balance);
        mLayoutLineInfo.setVisibility(View.GONE);
        mLayoutXiaofei.setVisibility(View.VISIBLE);
        statisticalAddition();

        handler.postDelayed(runnable, 3000);
    }

    /**
     * 要统计的数据进行相加
     */
    private void statisticalAddition() {
        float allMoney = SharedXmlUtil.getInstance(getApplicationContext())
                .read(Info.ALL_MONEY, 0.0f);
        float driverMoney = SharedXmlUtil.getInstance(getApplicationContext())
                .read(Info.DRIVER_MONEY, 0.0f);
        SharedXmlUtil.getInstance(getApplicationContext())
                .write(Info.ALL_MONEY, allMoney + balanceFloat);
        SharedXmlUtil.getInstance(getApplicationContext())
                .write(Info.DRIVER_MONEY, driverMoney + balanceFloat);
        int allPeople = SharedXmlUtil.getInstance(getApplicationContext())
                .read(Info.ALL_PEOPLE, 0);
        int driverPeople = SharedXmlUtil.getInstance(getApplicationContext())
                .read(Info.DRIVER_PEOPLE, 0);
        SharedXmlUtil.getInstance(getApplicationContext())
                .write(Info.ALL_YUE, allPeople + 1);
        SharedXmlUtil.getInstance(getApplicationContext())
                .write(Info.DRIVER_YUE, driverPeople + 1);
    }

    private void logToNet(CardBackBean cardBackBean) {
        TCardOpDU cardOpDU = cardBackBean.getCardOpDU();
        if (cardOpDU != null) {
            String log = cardOpDU.log;
            if (!TextUtils.isEmpty(log)) {
                //上传网络
                DataUploadToTianJinUtils.postLog(getApplicationContext(), log);
            }
        }
    }

    private void updateConfigUI() {
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession()
                .getRunParaFileDao().loadAll();
        if (runParaFiles.size() > 0) {
            RunParaFile runParaFile = runParaFiles.get(0);

            String line = Datautils.byteArrayToString(runParaFile.getLineNr());
            mTvLine.setText(Integer.parseInt(line) + "路");
            balance = (double) Datautils.byteArrayToInt(runParaFile.getKeyV1()) / 100 + "元";
            balanceFloat = ((float) Datautils.byteArrayToInt(runParaFile.getKeyV1()));
            mTvBalance.setText(balance);
            String busNr = SharedXmlUtil.getInstance(getApplicationContext())
                    .read(Info.BUS_NO, "000000");
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("车辆号：" + busNr + "\n");
            String posID = SharedXmlUtil.getInstance(getApplicationContext())
                    .read(Info.POS_ID, Info.POS_ID_INIT);
            stringBuffer.append("设备号：" + posID);
            mTvDeviceMessage.setText(stringBuffer + "");
        }
    }

    private void updateUI(CardBackBean cardBackBean) {
        TCardOpDU cardOpDU = cardBackBean.getCardOpDU();


        mLayoutLineInfo.setVisibility(View.GONE);
        mLayoutXiaofei.setVisibility(View.VISIBLE);
        if (cardOpDU.ucProcSec == (byte) 0x07) {
            if (cardOpDU.ucMainCardType == (byte) 0x0e
                    || cardOpDU.ucMainCardType == (byte) 0x0f
                    || cardOpDU.ucMainCardType == (byte) 0x0b
                    || cardOpDU.ucMainCardType == (byte) 0x0c) {
                PlaySound.play(PlaySound.DIDI, 0);
            } else if (cardOpDU.ucMainCardType == (byte) 0x03) {
                PlaySound.play(PlaySound.XUESHENGKA, 0);
            } else {
                PlaySound.play(PlaySound.dang, 0);
            }

            int num = cardOpDU.yueOriMoney - cardOpDU.yueSub;
            mTvXiaofeiTitle.setText("月票");
            mTvXiaofeiMoney.setText("1");
            int allYue = SharedXmlUtil.getInstance(getApplicationContext())
                    .read(Info.ALL_YUE, 0);
            int driverYue = SharedXmlUtil.getInstance(getApplicationContext())
                    .read(Info.DRIVER_YUE, 0);
            SharedXmlUtil.getInstance(getApplicationContext())
                    .write(Info.ALL_YUE, allYue + 1);
            SharedXmlUtil.getInstance(getApplicationContext())
                    .write(Info.DRIVER_YUE, driverYue + 1);
            mTvBalanceTitle.setText("剩余次数");
            mTvBalance.setText(num + "");
        } else {
            if (cardOpDU.purorimoneyInt < 500 && cardOpDU.pursubInt != 0) {
                PlaySound.play(PlaySound.QINGCHOGNZHI, 0);
            } else {
                if (cardOpDU.ucMainCardType == (byte) 0x01 || cardOpDU.ucMainCardType == (byte) 0x02) {
                    if (cardOpDU.pursubInt == 0) {
                        PlaySound.play(PlaySound.JINGLAOKA, 0);
                    } else {
                        PlaySound.play(PlaySound.dang, 0);
                    }

                } else if (cardOpDU.ucMainCardType == (byte) 0x11) {
                    if (cardOpDU.pursubInt == 0) {
                        PlaySound.play(PlaySound.AIXINKA, 0);
                    } else {
                        PlaySound.play(PlaySound.dang, 0);
                    }

                } else {
                    PlaySound.play(PlaySound.dang, 0);
                }
            }

            mTvXiaofeiTitle.setText("消费");
            mTvBalanceTitle.setText("余额");
            int balance = cardOpDU.purorimoneyInt - cardOpDU.pursubInt;
            mTvBalance.setText(((double) balance / 100) + "元");
            mTvXiaofeiMoney.setText(((double) cardOpDU.pursubInt / 100) + "元");
            float allMoney = SharedXmlUtil.getInstance(getApplicationContext())
                    .read(Info.ALL_MONEY, 0.0f);
            float driverMoney = SharedXmlUtil.getInstance(getApplicationContext())
                    .read(Info.DRIVER_MONEY, 0.0f);
            SharedXmlUtil.getInstance(getApplicationContext())
                    .write(Info.ALL_MONEY, allMoney + ((float) cardOpDU.pursubInt));
            float driverMoneyAll = driverMoney + ((float) cardOpDU.pursubInt);
            SharedXmlUtil.getInstance(getApplicationContext())
                    .write(Info.DRIVER_MONEY, driverMoneyAll);


        }

        int allPeople = SharedXmlUtil.getInstance(getApplicationContext())
                .read(Info.ALL_PEOPLE, 0);
        int driverPeople = SharedXmlUtil.getInstance(getApplicationContext())
                .read(Info.DRIVER_PEOPLE, 0);
        SharedXmlUtil.getInstance(getApplicationContext())
                .write(Info.ALL_PEOPLE, allPeople + 1);
        SharedXmlUtil.getInstance(getApplicationContext())
                .write(Info.DRIVER_PEOPLE, driverPeople + 1);
        handler.postDelayed(runnable, 3000);
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                default:
                    Log.i(TAG, "handleMessage:  返回 default");
                    try {
                        Log.d("SwipCard", msg.toString());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.releseAlipayJni();
//        hsmDecoder.removeResultListener(this);
//        HSMDecoder.disposeInstance();
        MyApplication.getHSMDecoder().removeResultListener(this);

        //停止巡卡
        startTimer(false);
//        handler.removeCallbacks(runnable);

    }


    private String decodeDate = null;
    private String codes = "";

    /**
     * 解码返回数据
     *
     * @param hsmDecodeResults
     */
    @Override
    public void onHSMDecodeResult(HSMDecodeResult[] hsmDecodeResults) {
        MyApplication.getHSMDecoder().removeResultListener(PsamIcActivity.this);
        handler.postDelayed(runnableScan, 500);
        if (hsmDecodeResults.length > 0) {
            HSMDecodeResult firstResult = hsmDecodeResults[0];
            if (Datautils.isUTF8(firstResult.getBarcodeDataBytes())) {
                try {
                    decodeDate = new String(firstResult.getBarcodeDataBytes(), "utf8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                decodeDate = Datautils.byteArrayToString(firstResult.getBarcodeDataBytes());
            }

            if (decodeDate.equals(codes)) {
//                if ("sp".equals(decodeDate.substring(0, 2))) {
                ToastUtil.customToastView(PsamIcActivity.this, "二维码重复"
                        , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                .from(PsamIcActivity.this)
                                .inflate(R.layout.layout_toast, null));
                PlaySound.play(PlaySound.ERWEIMASHIXIAO, 0);
//                }
                return;
            }
            codes = decodeDate;
            if (!"sp".equals(decodeDate.substring(0, 2))) {
                if (isConfigChange || !isQianDao) {
                    doVal(new CardBackBean(ReturnVal.CAD_QINGQIANDAO, null));
                    return;
                }
            }


            LogUtils.v("二维码: " + decodeDate);
            ltime = System.currentTimeMillis();
            switch (decodeDate.substring(0, 2)) {
                case "TX":
                    //腾讯（微信）
                    String posID = SharedXmlUtil.getInstance(getApplicationContext())
                            .read(Info.POS_ID, Info.POS_ID_INIT);
                    mPresenter.checkWechatTianJin(decodeDate, (byte) 1, (byte) 1
                            , posID, "12");
                    break;
                case "Ah":
                    mPresenter.checkYinLianCode(getApplicationContext(), decodeDate);
                    break;
                case "sp":
                    String read = SharedXmlUtil.getInstance(getApplicationContext())
                            .read(Info.BUS_NO, "000000");
                    String[] split = decodeDate.split(":");
                    if (read.equals(split[1])) {
                        LogUtils.v(split[1]);
                        ToastUtil.customToastView(PsamIcActivity.this, "车辆号相同无需设置"
                                , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                        .from(PsamIcActivity.this)
                                        .inflate(R.layout.layout_toast, null));
                        return;
                    }
                    SharedXmlUtil.getInstance(getApplicationContext()).write(Info.BUS_NO, split[1]);
                    ToastUtil.customToastView(PsamIcActivity.this, "车辆号设置完成"
                            , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                                    .from(PsamIcActivity.this)
                                    .inflate(R.layout.layout_toast, null));
                    PlaySound.play(PlaySound.setSuccess, 0);
                    List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
                    if (runParaFiles.size() > 0) {
                        RunParaFile runParaFile = runParaFiles.get(0);
                        runParaFile.setBusNr(Datautils.HexString2Bytes(split[1]));
                        DbDaoManage.getDaoSession().getRunParaFileDao().deleteAll();
                        DbDaoManage.getDaoSession().getRunParaFileDao().insert(runParaFile);
                    }
                    SharedXmlUtil.getInstance(getApplicationContext()).write(
                            Info.IS_CONFIG_CHANGE, true);
                    isConfigChange = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String busNr = SharedXmlUtil.getInstance(getApplicationContext())
                                    .read(Info.BUS_NO, "000000");
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append("车辆号：" + busNr + "\n");
                            String posID = SharedXmlUtil.getInstance(getApplicationContext())
                                    .read(Info.POS_ID, Info.POS_ID_INIT);
                            stringBuffer.append("设备号：" + posID);
                            mTvDeviceMessage.setText(stringBuffer + "");

                            mTvBalanceTitle.setText("");
                            mTvBalance.setText("请签到");
                        }
                    });

                    break;
                default:
                    //支付宝 二维码
                    mPresenter.checkAliQrCode(Datautils
                            .byteArrayToString(firstResult.getBarcodeDataBytes()));

                    break;
            }


        }

    }


    @Override
    public void successCode(CardBackBean cardBackBean) {
        doVal(cardBackBean);
    }

    @Override
    public void success(String msg) {
        Log.i(TAG, "success::: " + msg);
    }

    @Override
    public void erro(int msg) {
        doVal(new CardBackBean(msg, null));
    }


    @Override
    public void showAliPublicKey(int result) {
        if (result == 0) {
            mPresenter.aliPayInitJni();
        } else {
            Log.i(TAG, "获取支付宝key失败 ");
        }
    }

    @Override
    public void showAliPayInit(int result) {
        switch (result) {
            case ILLEGAL_PARAM:
                Log.i(TAG, "初始化参数格式错误！请检查参数各字段是否正确");
                break;
            case NO_ENOUGH_MEMORY:
                Log.i(TAG, "内存不足，极端错误，请检查程序运行空间是否足够");
                break;
            case SYSTEM_ERROR:
                Log.i(TAG, "系统异常！请联系支付宝技术人员");
                break;
            default:
                Log.i(TAG, "支付宝库初始化成功");
                break;
        }
    }


    @Override
    public void showCheckAliQrCode(TianjinAlipayRes tianjinAlipayRes, RunParaFile runParaFile
            , String orderNr) {
        tianjinAlipayRes = tianjinAlipayRes;
        if (tianjinAlipayRes.result == ErroCode.SUCCESS) {
            // TODO: 2019/3/11 存储天津公交需要的数据
            try {
                SaveDataUtils.saveZhiFuBaoReqDataBean(tianjinAlipayRes, runParaFile, orderNr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            doVal(new CardBackBean(ReturnVal.CODE_ZHIFUBAO_SUCCESS, null));
            mPresenter.uploadAlipayRe(getApplicationContext());
        } else if (tianjinAlipayRes.result == ErroCode.QRCODE_INFO_EXPIRED
                || tianjinAlipayRes.result == ErroCode.QRCODE_KEY_EXPIRED) {
            PlaySound.play(PlaySound.CODESHIXIAO, 0);
            ToastUtil.customToastView(PsamIcActivity.this, "二维码失效"
                    , Toast.LENGTH_SHORT, (TextView) LayoutInflater
                            .from(PsamIcActivity.this)
                            .inflate(R.layout.layout_toast, null));
        } else {
            doVal(new CardBackBean(ReturnVal.CAD_RETRY, null));
            Log.i(TAG, "\n支付宝校验结果错误:" + tianjinAlipayRes.result);
        }
    }


    @Override
    public void showCheckWechatQrCode(int result, RunParaFile runParaFile) {
        if (result == ErroCode.EC_SUCCESS) {
            doVal(new CardBackBean(ReturnVal.CODE_WEIXIN_SUCCESS, null));
            mPresenter.uploadWechatRe(getApplicationContext());
        } else if (result == ReturnVal.CODE_PLEASE_SET) {
            doVal(new CardBackBean(ReturnVal.CODE_PLEASE_SET, null));
        } else {
            LogUtils.i("微信校验结果错误 " + result);
            doVal(new CardBackBean(ReturnVal.CAD_EMPTY, null));
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                Intent intent = new Intent(PsamIcActivity.this, ShowDataActivity.class);
                startActivity(intent);
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (isDriverUI) {
                    mLlShowData.setVisibility(View.VISIBLE);
                    isShowDataUI = true;
                    isDriverUI = false;
                    mLlDriver.setVisibility(View.GONE);
//                    isSetConfigUI = false;
//                    mLlSetConfig.setVisibility(View.GONE);
                } else if (isShowDataUI) {
                    mLlShowData.setVisibility(View.GONE);
                    isShowDataUI = false;
                    isDriverUI = false;
                    mLlDriver.setVisibility(View.GONE);
//                    isSetConfigUI = false;
//                    mLlSetConfig.setVisibility(View.GONE);
                }
//                else if (isSetConfigUI) {
//                    mLlShowData.setVisibility(View.GONE);
//                    isShowDataUI = false;
//                    isDriverUI = true;
//                    mLlDriver.setVisibility(View.VISIBLE);
//                    isSetConfigUI = false;
//                    mLlSetConfig.setVisibility(View.GONE);
//                }
                else {
                    mLlShowData.setVisibility(View.GONE);
                    isShowDataUI = false;
                    isDriverUI = true;
                    mLlDriver.setVisibility(View.VISIBLE);
//                    isSetConfigUI = true;
//                    mLlSetConfig.setVisibility(View.VISIBLE);
                }
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (isDriverUI) {
                    mLlShowData.setVisibility(View.GONE);
                    isShowDataUI = false;
                    isDriverUI = false;
                    mLlDriver.setVisibility(View.GONE);
//                    isSetConfigUI=true;
//                    mLlSetConfig.setVisibility(View.VISIBLE);
                } else if (isShowDataUI) {
                    mLlShowData.setVisibility(View.GONE);
                    isShowDataUI = false;
                    isDriverUI = true;
                    mLlDriver.setVisibility(View.VISIBLE);
//                    isSetConfigUI=false;
//                    mLlSetConfig.setVisibility(View.GONE);
                }
//                else if (isSetConfigUI) {
//                    mLlShowData.setVisibility(View.GONE);
//                    isShowDataUI = false;
//                    isDriverUI = false;
//                    mLlDriver.setVisibility(View.GONE);
//                    isSetConfigUI = false;
//                    mLlSetConfig.setVisibility(View.GONE);
//                }
                else {
                    mLlShowData.setVisibility(View.VISIBLE);
                    isShowDataUI = true;
                    isDriverUI = false;
                    mLlDriver.setVisibility(View.GONE);
//                    isSetConfigUI = false;
//                    mLlSetConfig.setVisibility(View.GONE);
                }
            }
        }


        return super.onKeyDown(keyCode, event);
    }
}
