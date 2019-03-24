package com.spd.bus.spdata;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluering.pos.sdk.qr.QrCodeInfo;
import com.honeywell.barcode.HSMDecodeResult;
import com.honeywell.barcode.HSMDecoder;
import com.honeywell.plugins.decode.DecodeResultListener;
import com.spd.alipay.been.AliCodeinfoData;
import com.spd.alipay.been.TianjinAlipayRes;
import com.spd.base.been.BosiQrcodeKey;
import com.spd.base.been.WechatQrcodeKey;
import com.spd.base.been.tianjin.TCardOpDU;
import com.spd.base.beenupload.BosiQrCodeUpload;
import com.spd.base.utils.Datautils;
import com.spd.base.view.SignalView;
import com.spd.bus.MyApplication;
import com.spd.bus.R;
import com.spd.bus.card.methods.JTBCardManager;
import com.spd.base.been.tianjin.CardBackBean;
import com.spd.bus.card.methods.M1CardManager;
import com.spd.bus.card.methods.ReturnVal;
import com.spd.bus.card.utils.DateUtils;
import com.spd.bus.spdata.been.ErroCode;
import com.spd.bus.spdata.been.IcCardBeen;
import com.spd.bus.spdata.been.PsamBeen;
import com.spd.bus.spdata.been.TCommInfo;
import com.spd.bus.spdata.mvp.MVPBaseActivity;
import com.spd.bus.spdata.spdbuspay.SpdBusPayContract;
import com.spd.bus.spdata.spdbuspay.SpdBusPayPresenter;
import com.spd.bus.spdata.utils.PlaySound;
import com.spd.bus.spdata.utils.TimeDataUtils;
import com.spd.bus.util.SaveDataUtils;
import com.spd.bus.util.TLV;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import speedata.com.face.Contants;
import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.Core;

import static com.spd.bus.spdata.been.ErroCode.ILLEGAL_PARAM;
import static com.spd.bus.spdata.been.ErroCode.NO_ENOUGH_MEMORY;
import static com.spd.bus.spdata.been.ErroCode.SYSTEM_ERROR;
import static com.spd.bus.spdata.utils.PlaySound.xiaofeiSuccse;

public class PsamIcActivity extends MVPBaseActivity<SpdBusPayContract.View, SpdBusPayPresenter> implements SpdBusPayContract.View, DecodeResultListener {
    private BankCard mBankCard;
    private Core mCore;
    private boolean isStart = true;
    /**
     * 普通交易（CAPP=0）或复合交易（CAPP=1）
     */
    private int CAPP = 1;
    private static final String TAG = "SPEEDATA_BUS";
    private List<PsamBeen> psamDatas = new ArrayList<>();
    private byte[] blance = new byte[4];
    private byte[] ATC = new byte[2];
    private byte[] keyVersion = new byte[4];
    private byte[] rondomCpu = new byte[4];
    private byte[] cardId = new byte[8];
    private byte[] city = new byte[2];
    private byte[] file15_8 = new byte[8];
    /**
     * 秘钥版本
     */
    private byte flag;

    /**
     * 消费返回流程错误标识
     */
    private int isFlag = 0;
    /**
     * 返回正确结果
     */
    private final byte[] APDU_RESULT_SUCCESS = {(byte) 0x90, 0x00};
    /**
     * CPU卡黑名单结果
     */
    private final byte[] APDU_RESULT_FAILE = {(byte) 0x62, (byte) 0x83};

    /**
     * 微智接口返回数据
     */
    private byte[] respdata = new byte[512];
    /**
     * 微智接口返回数据长度
     */
    private int[] resplen = new int[1];
    /**
     * 微智接口返回状态 非0错误
     */
    private int retvalue = -1;
    /**
     * 消费时系统时间
     */
    private byte[] systemTime;


    /**
     * //获取PSAM卡序列号
     */
    private final byte[] PSAM_15FILE = {0x00, (byte) 0xB0, (byte) 0x95, 0x00, 0x0A};
    /**
     * //获取PSAM卡终端机编号指令
     */
    private final byte[] PSAN_GET_ID = {0x00, (byte) 0xB0, (byte) 0x96, 0x00, 0x06};
    /**
     * //交通部
     */
    private final byte[] PSAM_SELECT_DIR = {0x00, (byte) 0xA4, 0x00, 0x00, 0x02, (byte) 0x80, 0x11};

    /**
     * //读取psam卡17文件
     */
    private final byte[] PSAM_GET_17FILE = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x01};

    /**
     * //选择PPSE支付环境
     */
    private final byte[] SELEC_PPSE = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0e, 0x32, 0x50, 0x41, 0x59, 0x2e, 0x53, 0x59, 0x53, 0x2e, 0x44, 0x44, 0x46, 0x30, 0x31};

    /**
     * //选择电子钱包应用
     */
    private final byte[] SELECT_ICCARD_QIANBAO = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, (byte) 0xA0, 0x00, 0x00, 0x06, 0x32, 0x01, 0x01, 0x05};

    /**
     * //读CPU卡应用下公共应用基本信息文件指令 15文件
     */
    private final byte[] READ_ICCARD_15FILE = {0x00, (byte) 0xB0, (byte) 0x95, 0x00, 0x00};

    /**
     * 读CPU17文件
     */

    byte[] READ_ICCARD_17FILE = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x00};

    /**
     * //住建部
     */
    private final byte[] psamzhujian_select_dir = {0x00, (byte) 0xA4, 0x00, 0x00, 0x02, (byte) 0x10, 0x01};


    /**
     * //扇区标识符
     */
    private byte[] secF;
    /**
     * //保存读第0扇区 01块返回的 秘钥
     */
    private byte[][] lodkey = new byte[16][6];
    private byte[] snUid;
    private byte[] actRemaining;
    private byte[] PSAM_ATC = new byte[4];
    private byte[] MAC1 = new byte[4];
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
    private HSMDecoder hsmDecoder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spd_bus_layout);
        initView();
        initCard();
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
    }


    private void initCard() {
        try {
            MyApplication.getHSMDecoder().addResultListener(PsamIcActivity.this);
            //注册系统时间广播 只能动态注册
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Contants.ACTION_RECE_FACE);
            filter.addAction(Contants.ACTION_UPLOAD_STATUS);
            registerReceiver(receiver, filter);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mBankCard = new BankCard(getApplicationContext());
                    mCore = new Core(getApplicationContext());
                    psam1Init();
                    psam2Init();
                    startTimer(true);
                }
            }).start();
            //获取支付宝微信key
//            mPresenter.getAliPubKey();
            mPresenter.getZhiFuBaoAppSercet(getApplicationContext());
            mPresenter.getZhiFuBaoBlack(getApplicationContext());
            mPresenter.getZhiFuBaoWhite(getApplicationContext());
            mPresenter.getAliPubKeyTianJin();
            mPresenter.getYinLianPubKey();
//            mPresenter.getWechatPublicKey();
//            mPresenter.bosiInitJin(this, "/storage/sdcard0/bosicer/");
//            mPresenter.getBosikey();
            mPresenter.getWechatPublicKeyTianJin();
            updateTime();
        } catch (Exception e) {

        }
    }

    /**
     * 人脸识别广播
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Contants.ACTION_RECE_FACE)) {
                boolean isSuccess = intent.getBooleanExtra("issuccess", false);
                if (isSuccess) {
                    mLayoutLineInfo.setVisibility(View.GONE);
                    mLayoutFace.setVisibility(View.VISIBLE);
                    mTvBalanceTitle.setText("消费额");
                    mTvBalance.setText("1.00元");
                    PlaySound.play(xiaofeiSuccse, 0);
                    handler.postDelayed(runnable, 2000);
                }
                Log.i(TAG, "人脸返回===" + intent.getStringExtra("msg"));
            } else if (action.equals(Contants.ACTION_UPLOAD_STATUS)) {
                boolean isSuccess = intent.getBooleanExtra("issuccess", false);
                if (isSuccess) {
                    Log.e(TAG, "onReceive:上传成功");
                } else {
                    Log.e(TAG, "onReceive:上传失败");
                }
                Log.i(TAG, "人脸返回===" + intent.getStringExtra("msg"));
            }
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mLayoutLineInfo.setVisibility(View.VISIBLE);
            mLayoutFace.setVisibility(View.GONE);
            mLayoutXiaofei.setVisibility(View.GONE);
            mTvBalanceTitle.setText("票价");
            mTvBalance.setText("2.00元");
        }
    };

    @SuppressLint("CheckResult")
    private void updateTime() {
        Flowable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
//                        //设置sp时间
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//                        Date date = new Date(System.currentTimeMillis());
//                        String str = simpleDateFormat.format(date);// 1971 < year < 2099
//                        try {
//                            mCore.setDateTime(str.getBytes("UTF-8"));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            byte[] dateTime = new byte[14];
//
//                            // Get SP system clock in format ASCII14 yyyyMMddHHmmss
//                            mCore.getDateTime(dateTime);
//                            String strDate = new String(dateTime);
//                            String Date = strDate.substring(0, 4) + "-" + strDate.substring(4, 6) + "-" +
//                                    strDate.substring(6, 8);
//                            String times = strDate.substring(8, 10) + ":" +
//                                    strDate.substring(10, 12) + ":" + strDate.substring(12, 14);
//                            mTvDate.setText(Date);
//                            mTvTime.setText(times);
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                        }
                        mTvDate.setText(Datautils.getData());
                        mTvTime.setText(Datautils.getTime());
                    }
                });

    }

    private boolean checkResuleAPDU(byte[] reByte, int le) {
        return Arrays.equals(APDU_RESULT_SUCCESS, Datautils.cutBytes(reByte, le - 2, 2));
    }

    /**
     * psam 初始化流程
     */
    private void psam1Init() {
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PSAM1, 60, respdata, resplen, "app1");
            if (retvalue != 0) {
                isFlag = 1;
                return;
            }
            Log.d(TAG, "===交通部切换psam===" + Datautils.byteArrayToString(Datautils.cutBytes(respdata, 0, resplen[0])));
            if (respdata[0] == (byte) 0x01) {
                Log.e(TAG, "交通部psam初始化 读卡失败,请检查是否插入psam卡 " + Datautils.byteArrayToString(respdata));
                isFlag = 1;
                return;
            } else if (respdata[0] == (byte) 0x05) {
                byte[] resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, PSAM_15FILE);
                if (resultBytes == null || resultBytes.length == 2) {
                    Log.e(TAG, "===交通部获取15文件错误===" + Datautils.byteArrayToString(resultBytes));
                    isFlag = 1;
                    return;
                }
                byte[] snr = resultBytes;
                //IC卡已经插入
                resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, PSAN_GET_ID);
                if (resultBytes == null || resultBytes.length == 2) {
                    Log.e(TAG, "===交通部获取16文件错误===" + Datautils.byteArrayToString(resultBytes));
                    isFlag = 1;
                    return;
                }
                Log.d(TAG, "===交通部获取16文件===" + Datautils.byteArrayToString(resultBytes));
                //终端机编号
                byte[] deviceCode = resultBytes;
                Log.d(TAG, "====交通部PSAM卡终端机编号==== " + Datautils.byteArrayToString(deviceCode));

                resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, PSAM_SELECT_DIR);
                if (resultBytes == null || resultBytes.length == 2) {
                    Log.e(TAG, "===交通部(8011)错误===" + Datautils.byteArrayToString(resultBytes));
                    isFlag = 1;
                    return;
                }
                Log.d(TAG, "===交通部(8011)===" + Datautils.byteArrayToString(resultBytes));
                resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, PSAM_GET_17FILE);
                if (resultBytes == null || resultBytes.length == 2) {
                    Log.e(TAG, "===交通部获取17文件错误===" + Datautils.byteArrayToString(resultBytes));
                    isFlag = 1;
                    return;
                }
                Log.d(TAG, "===交通部获取17文件===" + Datautils.byteArrayToString(resultBytes));
                byte[] psamKey = Datautils.cutBytes(resultBytes, 0, 1);
                Log.d(TAG, "===交通部秘钥索引=== " + Datautils.byteArrayToString(psamKey) + "\n" + "PSAM初始化成功！！！请读消费卡\n");
                psamDatas.add(new PsamBeen(1, deviceCode, psamKey, snr));
                // TODO: 2018/12/4  初始化成功等待读消费卡
            } else {
                Log.e(TAG, "交通部psam初始化失败 " + Datautils.byteArrayToString(Datautils.cutBytes(respdata, 0, resplen[0])));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            isFlag = 1;
        }
    }

    /**
     * 住建部psam初始化
     */
    private void psam2Init() {
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PSAM2, 60, respdata, resplen, "app1");
            if (retvalue != 0) {
                isFlag = 1;
                return;
            }
            Log.d(TAG, "===住建部切换psam===" + Datautils.byteArrayToString(Datautils.cutBytes(respdata, 0, resplen[0])));
            if (respdata[0] == (byte) 0x01) {
                //读卡失败
                Log.e(TAG, "住建部psam初始化 读卡失败,请检查是否插入psam卡 " + Datautils.byteArrayToString(respdata));
                isFlag = 1;
                return;

            } else if (respdata[0] == (byte) 0x05) {
                byte[] resultBytes = sendApdus(BankCard.CARD_MODE_PSAM2_APDU, PSAM_15FILE);
                if (resultBytes == null || resultBytes.length == 2) {
                    Log.e(TAG, "===交通部获取15文件错误===" + Datautils.byteArrayToString(resultBytes));
                    isFlag = 1;
                    return;
                }
                byte[] snr = resultBytes;
                //IC卡已经插入
                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, PSAN_GET_ID, PSAN_GET_ID.length, respdata, resplen);
                if (retvalue != 0) {
                    isFlag = 1;
                    return;
                }
                Log.d(TAG, "住建部16文件return" + Datautils.byteArrayToString(Datautils.cutBytes(respdata, 0, resplen[0])));
                if (!checkResuleAPDU(respdata, resplen[0])) {
                    Log.e(TAG, "住建部获取终端编号错误:" + Datautils.byteArrayToString(Datautils.cutBytes(respdata, resplen[0] - 2, 2)));
                    isFlag = 1;
                    return;
                }
                //终端机编号
                byte[] deviceCode = Datautils.cutBytes(respdata, 0, resplen[0] - 2);
                Log.d(TAG, "====住建部PSAM卡终端机编号==== " + Datautils.byteArrayToString(deviceCode));
                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, psamzhujian_select_dir, psamzhujian_select_dir.length, respdata, resplen);
                if (retvalue != 0) {
                    isFlag = 1;
                    return;
                }
                Log.d(TAG, "===住建部选文件 10 01 ====" + Datautils.byteArrayToString(Datautils.cutBytes(respdata, 0, resplen[0])) + "   " + retvalue);
                if (!checkResuleAPDU(respdata, resplen[0])) {
                    Log.e(TAG, "住建部切换1001错误:" + Datautils.byteArrayToString(Datautils.cutBytes(respdata, resplen[0] - 2, 2)));
                    isFlag = 1;
                    return;
                }
                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, PSAM_GET_17FILE, PSAM_GET_17FILE.length, respdata, resplen);
                if (retvalue != 0) {
                    isFlag = 1;
                    return;
                }
                Log.d(TAG, "===住建部17文件获取秘钥索引===" + Datautils.byteArrayToString(Datautils.cutBytes(respdata, 0, resplen[0])));
                if (!checkResuleAPDU(respdata, resplen[0])) {
                    Log.e(TAG, "住建部获取秘钥索引错误:" + Datautils.byteArrayToString(Datautils.cutBytes(respdata, resplen[0] - 2, 2)));
                }
                byte[] psamKey = Datautils.cutBytes(respdata, 0, 1);
                Log.d(TAG, "===住建部秘钥索引===" + Datautils.byteArrayToString(psamKey) + "\n" + "PSAM初始化成功！！！请读消费卡\n");
                psamDatas.add(new PsamBeen(2, deviceCode, psamKey, snr));
                // TODO: 2018/12/4  psam卡等待读消费卡
            } else {
                Log.e(TAG, "住建部psam初始化 读卡失败,请检查是否插入psam卡 " + Datautils.byteArrayToString(respdata));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            isFlag = 1;
        }
    }

    private long ltime = 0;

    private void startTimer(boolean isStart) {
        while (isStart) {
            try {
                //非接卡在位检测;
                int result = mBankCard.piccDetect();
                if (result == 0) {
                    isFlag = 2;
                } else if (result == 1 && isFlag == 2) {
                    ltime = System.currentTimeMillis();
                    Log.i("stw", "run: 开始本次读卡等待");
                    //切换到非接卡读取
                    retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC, 1, respdata, resplen, "app1");
                    Log.i("stw", "=== ===" + (System.currentTimeMillis() - ltime));
                    if (retvalue != 0) {
                        isFlag = 1;
                        return;
                    }
                    //检测到非接IC卡
                    if (respdata[0] == 0x07) {
                        CardBackBean cardBackBean = JTBCardManager.getInstance()
                                .mainMethod(mBankCard, psamDatas, cpuCardInit(), pursub);
                        doVal(cardBackBean);
                        isFlag = 0;
//                        icExpance();//执行等待读卡消费
//                        if (isFlag == 1) {
//                            PlaySound.play(PlaySound.qingchongshua, 0);
//                        }
                    } else if (respdata[0] == 0x37) {
                        //检测到 M1-S50 卡
                        Log.i("stw", "m1结束寻卡===" + (System.currentTimeMillis() - ltime));
//                        m1ICCard();
                        try {
                            CardBackBean cardBackBean = M1CardManager.getInstance()
                                    .mainMethod(mBankCard, M1CardManager.M150, 0, psamDatas);
                            doVal(cardBackBean);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        isFlag = 0;
//                        if (isFlag == 1) {
//                            PlaySound.play(PlaySound.qingchongshua, 0);
//                        }
                    } else if (respdata[0] == 0x47) {
                        // 检测到 M1-S70 卡
                        try {
                            CardBackBean cardBackBean = M1CardManager.getInstance()
                                    .mainMethod(mBankCard, M1CardManager.M170, 0, psamDatas);
                            doVal(cardBackBean);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        isFlag = 0;
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    private void doVal(CardBackBean cardBackBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int value = cardBackBean.getBackValue();
                switch (value) {
                    case ReturnVal.CAD_READ:
                        break;
                    case ReturnVal.CAD_EXPIRE:
                        break;
                    case ReturnVal.CAD_SELL:
                        break;
                    case ReturnVal.CAD_OK:
                        PlaySound.play(PlaySound.xiaofeiSuccse, 0);
                        TCardOpDU cardOpDU = cardBackBean.getCardOpDU();
                        if (cardOpDU.procSec != 2) {

                        } else {
                            mLayoutLineInfo.setVisibility(View.GONE);
                            mLayoutXiaofei.setVisibility(View.VISIBLE);
                            mTvBalanceTitle.setText("余额");
                            mTvXiaofeiMoney.setText(((double) cardOpDU.pursubInt / 100) + "元");
                        }
                        handler.postDelayed(runnable, 2000);
                        break;
                    case ReturnVal.CAD_MAC2:
                        break;
                    case ReturnVal.CAD_RETRY:
                        PlaySound.play(PlaySound.qingchongshua, 0);
                        break;
                    default:
                        break;
                }
            }
        });

    }


    private IcCardBeen icCardBeen = new IcCardBeen();


    private void m1ICCard() {
//        ltime = System.currentTimeMillis();
//        CInfoF = new TCommInfo();
//        CInfoZ = new TCommInfo();
//        CInfo = new TCommInfo();
//        try {
//            Log.d(TAG, "===m1卡消费开始===");
//            //读取非接卡 SN(UID)信息
//            retvalue = mBankCard.getCardSNFunction(respdata, resplen);
//            if (retvalue != 0) {
//                isFlag = 1;
//                Log.e(TAG, "===获取UID失败===");
//                return;
//            }
//            snUid = Datautils.cutBytes(respdata, 0, resplen[0]);
//            icCardBeen.setSnr(snUid);
//            Log.d(TAG, "===getUID===" + Datautils.byteArrayToString(snUid));
//            byte[] key = new byte[6];
//            System.arraycopy(snUid, 0, key, 0, 4);
//            System.arraycopy(snUid, 0, key, 4, 2);
//            //认证1扇区第4块
//            retvalue = mBankCard.m1CardKeyAuth(0x41, 0x04, key.length, key, snUid.length, snUid);
//            if (retvalue != 0) {
//                isFlag = 1;
//                Log.e(TAG, "===认证1扇区第4块失败===");
//                return;
//            }
//            retvalue = mBankCard.m1CardReadBlockData(0x04, respdata, resplen);
//            if (retvalue != 0) {
//                isFlag = 1;
//                Log.e(TAG, "=== 读取1扇区第4块失败==");
//                return;
//            }
//            byte[] bytes04 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
//            Log.d(TAG, "===读取1扇区第4块返回===" + Datautils.byteArrayToString(bytes04));
//            icCardBeen.setIssueSnr(Datautils.cutBytes(bytes04, 0, 8));
//            icCardBeen.setCityNr(Datautils.cutBytes(bytes04, 0, 2));
//            icCardBeen.setVocCode(Datautils.cutBytes(bytes04, 2, 2));
//            icCardBeen.setIssueCode(Datautils.cutBytes(bytes04, 4, 4));
//            icCardBeen.setMackNr(Datautils.cutBytes(bytes04, 8, 4));
//            icCardBeen.setfStartUse(Datautils.cutBytes(bytes04, 12, 1));
//            //卡类型判断表格中没有return
//            icCardBeen.setCardType(Datautils.cutBytes(bytes04, 13, 1));
//            //黑名单
//            icCardBeen.setfBlackCard(0);
//            //判断启用标志
//            switch (icCardBeen.getfStartUse()[0]) {
//                //未启用
//                case (byte) 0x01:
//                    Log.e(TAG, "m1ICCard: 启用标志未启用");
//                    isFlag = 1;
//                    return;
//                //正常
//                case (byte) 0x02:
//                    // TODO: 2018/8/29
//                    break;
//                //停用
//                case (byte) 0x03:
//                    Log.e(TAG, "m1ICCard: 启用标志停用");
//                    isFlag = 1;
//                    return;
//                //黑名单
//                case (byte) 0x04:
//                    Log.e(TAG, "m1ICCard: 启用标志黑名单");
//                    isFlag = 1;
//                    icCardBeen.setfBlackCard(1);
//                    return;
//                default:
//                    break;
//            }
//            //读1扇区05块数据
//            retvalue = mBankCard.m1CardReadBlockData(0x05, respdata, resplen);
//            if (retvalue != 0) {
//                isFlag = 1;
//                Log.e(TAG, "===读1扇区05块数据失败====");
//                return;
//            }
//            byte[] bytes05 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
//            Log.d(TAG, "===读1扇区05块数据===" + Datautils.byteArrayToString(bytes05));
//            icCardBeen.setIssueDate(Datautils.cutBytes(bytes05, 0, 4));
//            icCardBeen.setEndUserDate(Datautils.cutBytes(bytes05, 4, 4));
//            icCardBeen.setStartUserDate(Datautils.cutBytes(bytes05, 8, 4));
//
//            //读1扇区06块数据
//            retvalue = mBankCard.m1CardReadBlockData(0x06, respdata, resplen);
//            if (retvalue != 0) {
//                Log.e(TAG, "===读1扇区06块数据失败==");
//                isFlag = 1;
//                return;
//            }
//            byte[] bytes06 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
//            Log.d(TAG, "===读1扇区06块数据返回===" + Datautils.byteArrayToString(bytes06));
//            //转UTC时间
//            icCardBeen.setPurIncUtc(Datautils.cutBytes(bytes06, 0, 6));
//            icCardBeen.setPurIncMoney(Datautils.cutBytes(bytes06, 9, 2));
//            //第0扇区 01块认证
//            retvalue = mBankCard.m1CardKeyAuth(0x41, 0x01,
//                    6, new byte[]{(byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3, (byte) 0xA4, (byte) 0xA5}, snUid.length, snUid);
//            if (retvalue != 0) {
//                Log.e(TAG, "===第0扇区01块认证失败==");
//                isFlag = 1;
//                return;
//            }
//            //读第0扇区第一块秘钥
//            retvalue = mBankCard.m1CardReadBlockData(0x01, respdata, resplen);
//            if (retvalue != 0) {
//                Log.e(TAG, "m1ICCard: 读第0扇区01块失败");
//                isFlag = 1;
//                return;
//            }
//
//            byte[] bytes01 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
//            Log.d(TAG, "m1ICCard: 读第0扇区01块：" + Datautils.byteArrayToString(bytes01));
//            //扇区标识符
//            secF = bytes01;
//            //算秘钥指令
//            String sendCmd = "80FC010110" + Datautils.byteArrayToString(icCardBeen.getCityNr()) + Datautils.byteArrayToString(icCardBeen.getSnr()) + Datautils.byteArrayToString(Datautils.cutBytes(icCardBeen.getIssueSnr(), 6, 2)) + Datautils.byteArrayToString(icCardBeen.getMackNr())
//                    + Datautils.byteArrayToString(Datautils.cutBytes(secF, 2, 2)) + Datautils.byteArrayToString(Datautils.cutBytes(secF, 6, 2));
//            Log.d(TAG, "===psam计算秘钥指令===" + sendCmd);
//            //psam卡计算秘钥
//            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, Datautils.HexString2Bytes(sendCmd), Datautils.HexString2Bytes(sendCmd).length, respdata, resplen);
//            if (retvalue != 0) {
//                Log.e(TAG, "===psam计算秘钥指令错误===");
//                isFlag = 1;
//                return;
//            }
//            if (!Arrays.equals(APDU_RESULT_SUCCESS, Datautils.cutBytes(respdata, resplen[0] - 2, 2))) {
//                Log.e(TAG, "=== psam计算秘钥指令错误非9000===");
//                isFlag = 1;
//                return;
//            }
//            byte[] result = Datautils.cutBytes(respdata, 0, resplen[0] - 2);
//            Log.d(TAG, "m1ICCard: psam计算秘钥返回：" + Datautils.byteArrayToString(result));
//            //3/4/5扇区秘钥相同
//            // 第2扇区秘钥
//            lodkey[2] = Datautils.cutBytes(result, 0, 6);
//            //第3扇区秘钥
//            lodkey[3] = Datautils.cutBytes(result, 6, 6);
//            //第4扇区秘钥
//            lodkey[4] = Datautils.cutBytes(result, 6, 6);
//            //第5扇区秘钥
//            lodkey[5] = Datautils.cutBytes(result, 6, 6);
//            //第6扇区秘钥
//            lodkey[6] = Datautils.cutBytes(result, 12, 6);
//            //第7扇区秘钥
//            lodkey[7] = Datautils.cutBytes(result, 18, 6);
//            //第6扇区24 块认证
//            byte[] lodKey6 = lodkey[6];
//            retvalue = mBankCard.m1CardKeyAuth(0x41, 24, lodKey6.length, lodKey6, snUid.length, snUid);
//            if (retvalue != 0) {
//                Log.e(TAG, "===第6扇区24 块认证错误===");
//                isFlag = 1;
//                return;
//            }
//            //读6扇区第24块
//            retvalue = mBankCard.m1CardReadBlockData(24, respdata, resplen);
//            if (retvalue != 0) {
//                Log.e(TAG, "===读6扇区第24块失败===");
//                isFlag = 1;
//                return;
//            }
//            byte[] bytes24 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
//
////            System.arraycopy(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}, 0, bytes24, 0, 8);
////            System.arraycopy(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}, 0, bytes24, 8, 7);
////            retvalue = mBankCard.m1CardWriteBlockData(24, bytes24.length, bytes24);
////            retvalue = mBankCard.m1CardWriteBlockData(25, bytes24.length, bytes24);
////            retvalue = mBankCard.m1CardReadBlockData(24, respdata, resplen);
////            if (retvalue != 0) {
////                Log.e(TAG, "m1ICCard: 读6扇区第24块失败");
////                isFlag = 1;
////                return;
////            }
////            bytes24 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
//
//            Log.d(TAG, "===读6扇区第24块返回===" + Datautils.byteArrayToString(bytes24));
//            byte[] dtZ = bytes24;
//            byte chk = 0;
//            //异或操作
//            for (int i = 0; i < 16; i++) {
//                chk ^= dtZ[i];
//            }
//            //判断8-15是否都等于0xff
//            if (Arrays.equals(Datautils.cutBytes(dtZ, 8, 7),
//                    new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}) && chk == 0) {
//                CInfoZ.fValid = 1;
//            }
//            if (Arrays.equals(Datautils.cutBytes(dtZ, 0, 8), new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff,
//                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff})) {
//                CInfoZ.fValid = 0;
//            }
//            if (dtZ[0] > 8) {
//                CInfoZ.fValid = 0;
//            }
//            //交易记录指针
//            CInfoZ.cPtr = dtZ[0];
//            //钱包计数,2,3
//            CInfoZ.iPurCount = Datautils.cutBytes(dtZ, 1, 2);
//            //进程标志
//            CInfoZ.fProc = dtZ[3];
//            CInfoZ.iYueCount = Datautils.cutBytes(dtZ, 4, 2);
//            CInfoZ.fBlack = dtZ[6];
//            CInfoZ.fFileNr = dtZ[7];
//            //副本  有效性
//            //读6扇区第25块
//            retvalue = mBankCard.m1CardReadBlockData(25, respdata, resplen);
//            if (retvalue != 0) {
//                Log.e(TAG, "===读6扇区第25块失败===");
//                isFlag = 1;
//                return;
//            }
//            byte[] bytes25 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
//            byte[] dtF = bytes25;
//            for (int i = 0; i < 16; i++) {
//                chk ^= dtF[i];
//            }
//            if (Arrays.equals(Datautils.cutBytes(dtF, 8, 7),
//                    new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,}) && chk == 0) {
//                CInfoF.fValid = 1;
//            }
//            if (Arrays.equals(Datautils.cutBytes(dtF, 0, 8), new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff,
//                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff})) {
//                CInfoF.fValid = 0;
//            }
//            if (dtF[0] > 8) {
//                CInfoF.fValid = 0;
//            }
//            CInfoF.cPtr = dtF[0];
//            CInfoF.iPurCount = Datautils.cutBytes(dtF, 1, 2);
//            CInfoF.fProc = dtF[3];
//            CInfoF.iYueCount = Datautils.cutBytes(dtF, 4, 2);
//            CInfoF.fBlack = dtF[6];
//            CInfoF.fFileNr = dtF[7];
//
//            if (CInfoZ.fValid == 1) {
//                CInfo = CInfoZ;
//            } else if (CInfoF.fValid == 1) {
//                CInfo = CInfoF;
//            } else {
//                Log.e(TAG, "===24 25块有效标志错误 返回0===");
//                isFlag = 1;
//                return;
//            }
//
//            if ((CInfoZ.fValid == 1 && (CInfoZ.fBlack == 4)) || (CInfoF.fValid == 1 && (CInfoF.fBlack == 4))) {
//                //黑名单 报语音
//                icCardBeen.setfBlackCard(1);
//                Log.e(TAG, "m1ICCard: 黑名单");
//                isFlag = 1;
//                return;
//            }
//            //比对 9块 10块数据
//            if (!BackupManage(8)) {
//                isFlag = 1;
//                return;
//            }
//            if (!writeCardRcd()) {
//                isFlag = 1;
//                return;
//            }
//            Log.i("stw", "===M1卡消费结束===" + (System.currentTimeMillis() - ltime));
//            handler.sendMessage(handler.obtainMessage(2, Datautils.byteArrayToInt(blance)));
//            isFlag = 0;
//        } catch (RemoteException e) {
//            e.printStackTrace();
//
//        }
    }

    private boolean BackupManage(int blk) {
        //第二扇区08 块认证
        try {
            byte[] lodKey2 = lodkey[blk / 4];
            retvalue = mBankCard.m1CardKeyAuth(0x41, blk, lodKey2.length, lodKey2, snUid.length, snUid);
            if (retvalue != 0) {
                Log.e(TAG, "===认证2扇区第9块失败===");
                isFlag = 1;
                return false;
            }
            //读2扇区第9块
            retvalue = mBankCard.m1CardReadBlockData(9, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "===读2扇区第9块失败===");
                isFlag = 1;
                return false;
            }
            byte[] bytes09 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "===读2扇区第9块返回===" + Datautils.byteArrayToString(bytes09));

            //读2扇区第10块
            retvalue = mBankCard.m1CardReadBlockData(10, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "===读2扇区第10块失败===");
                isFlag = 1;
                return false;
            }
            byte[] bytes10 = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "===读2扇区第10块返回===" + Datautils.byteArrayToString(bytes10));

            if (ValueBlockValid(bytes09)) {
                Log.d(TAG, "=== 2区09块过===");
                //判断2区9块10块数据是否一致
                if (!Arrays.equals(bytes09, bytes10)) {
                    retvalue = mBankCard.m1CardValueOperation(0x3E, 9, 0, 10);
                    if (retvalue != 0) {
                        Log.e(TAG, "===更新2区09块失败===");
                        isFlag = 1;
                        return false;
                    }
                }
            } else {
                if (ValueBlockValid(bytes10)) {
                    Log.d(TAG, "===2区10块过===");
                    if (!Arrays.equals(bytes09, bytes10)) {
                        bytes09 = bytes10;
                        retvalue = mBankCard.m1CardValueOperation(0x3E, 10, 0, 9);
//                        retvalue = mBankCard.m1CardWriteBlockData(0x09, bytes10.length, bytes10);
                        if (retvalue != 0) {
                            Log.e(TAG, "===写2区9块失败===");
                            isFlag = 1;
                            return false;
                        }
                    }
                } else {
                    isFlag = 1;
                    Log.d(TAG, "===2区10块错返回===");
                    return false;
                }
            }
            //原额
            byte[] yue09 = Datautils.cutBytes(bytes09, 0, 4);
            icCardBeen.setPurorimoney(yue09);
            //定义消费金额
            icCardBeen.setPursub(new byte[]{0x00, 0x00, 0x00, (byte) 0x01});
            Log.d(TAG, "===原额===" + Datautils.byteArrayToString(yue09));
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            isFlag = 1;
            return false;
        }

    }

    private TCommInfo CInfoZ, CInfoF, CInfo;

    //所有“交易记录”块
    private byte RcdBlkIndex[] = {12, 13, 14, 16, 17, 18, 20, 21, 22};

    public boolean writeCardRcd() {
//        //step 0//文件标识
//        CInfo.fFileNr = secF[2];
//        if (CInfo.cPtr > 8) {
//            CInfo.cPtr = 0;
//        }
//        //当前交易记录块
//        int blk = RcdBlkIndex[CInfo.cPtr];
//        Log.d(TAG, "writeCardRcd: 当前交易记录块：" + blk);
//
//        CInfo.cPtr = (byte) (CInfo.cPtr == 8 ? 0 : CInfo.cPtr + 1);
//        //获取UTC时间
//        byte[] ulDevUTC = Datautils.HexString2Bytes(TimeDataUtils.getUTCtimes());
//        // 写卡指令
//        byte[] RcdToCard = new byte[16];
//
//        System.arraycopy(ulDevUTC, 0, RcdToCard, 0, 4);
//        //获取消费前原额
//        System.arraycopy(icCardBeen.getPurorimoney(), 0, RcdToCard, 4, 4);
//        //获取本次消费金额
//        System.arraycopy(icCardBeen.getPursub(), 1, RcdToCard, 8, 3);
//        RcdToCard[11] = 1;
//        //设备号写死
//        RcdToCard[12] = 0x64;
//        RcdToCard[13] = 0x10;
//        RcdToCard[14] = 0x00;
//        RcdToCard[15] = 0x01;
//        //进程标志
//        CInfo.fProc = 1;
//        Log.d(TAG, "writeCardRcd: 本次交易记录指令：" + Datautils.byteArrayToString(RcdToCard));
//        int count = Datautils.byteArrayToInt(CInfo.iPurCount) + 1;
//        byte[] result = new byte[2];
//        result[0] = (byte) ((count >> 8) & 0xFF);
//        result[1] = (byte) (count & 0xFF);
//        CInfo.iPurCount = result;
//
//        for (; ; ) {
//            try {
//                //step 1 改写24 25块数据
//                if (!Modify_InfoArea(24)) {
//                    Log.e(TAG, "writeCardRcd: 改写24块错误");
//                    return false;
//                }
//                //step 2//blk/4 区    blk块
//                if (!m1CardKeyAuth(blk, blk / 4)) {
//                    return false;
//                }
//                //写卡  将消费记录写入消费记录区
//                retvalue = mBankCard.m1CardWriteBlockData(blk, RcdToCard.length, RcdToCard);
//                if (retvalue != 0) {
//                    Log.e(TAG, "writeCardRcd: 将消费记录写入消费记录区错误 块为" + blk);
//                    return false;
//                }
//                //消费记录区读取
//                retvalue = mBankCard.m1CardReadBlockData(blk, respdata, resplen);
//                if (retvalue != 0) {
//                    Log.e(TAG, "writeCardRcd: 读取消费记录区错误");
//                    return false;
//                }
//                byte[] RcdInCard = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
//                Log.d(TAG, "writeCardRcd: 读当前消费记录区数据：" + Datautils.byteArrayToString(RcdInCard));
//                if (!Arrays.equals(RcdInCard, RcdToCard)) {
//                    Log.e(TAG, "writeCardRcd: 读数据不等于消费返回错误");
//                    return false;
//                }
//                byte[] bytes = new byte[16];
//                //判断是否 读回==00
//                if (Arrays.equals(RcdInCard, bytes)) {
//                    Log.e(TAG, "writeCardRcd: 读数据不等于消费返回0错误");
//                    return false;
//                }
//
//                //step 3
////            PrepareRecord(tCardOpDu.ucSec == 2 ? 1 : 3);   1代表 钱包灰记录 3 月票灰记录
//                fErr = 1;
//                if (!Modify_InfoArea(25)) {
//                    Log.e(TAG, "writeCardRcd: 改写25块错误");
//                    // 改写25块，不成功退出
//                    return false;
//                }
//                //step 4//认证2扇区8块
//                if (!m1CardKeyAuth(8, 2)) {
//                    return false;
//                }
//                //执行消费 将消费金额带入
//                int purSub = Datautils.byteArrayToInt(icCardBeen.getPursub());
//                retvalue = mBankCard.m1CardValueOperation(0x2D, 9, purSub, 9);
//
//                if (retvalue != 0) {
//                    Log.e(TAG, "writeCardRcd: 执行消费错误");
//                    return false;
//                }
//                //执行 读出 现在原额
//                retvalue = mBankCard.m1CardReadBlockData(9, respdata, resplen);
//                if (retvalue != 0) {
//                    Log.e(TAG, "writeCardRcd: 读原额错误");
//                    return false;
//                }
//                //本次消费后的原额;
//                byte[] dtZ = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
//                byte[] tempV = Datautils.cutBytes(dtZ, 0, 4);
//                Log.d(TAG, "writeCardRcd:正本读09块返回：" + Datautils.byteArrayToString(dtZ));
//                //判断消费前金额-消费金额=消费后金额
//                int s = Datautils.byteArrayToInt(icCardBeen.getPurorimoney(), false);
//                int s2 = Datautils.byteArrayToInt(tempV, false);
//                if (s - purSub != s2) {
//                    return false;
//                }
//                //step 6
//                retvalue = mBankCard.m1CardValueOperation(0x3E, 9, Datautils.byteArrayToInt(dtZ), 10);
//                if (retvalue != 0) {
//                    Log.e(TAG, "writeCardRcd: 写10块错误");
//                    return false;
//                }
//                retvalue = mBankCard.m1CardReadBlockData(10, respdata, resplen);
//                if (retvalue != 0) {
//                    Log.e(TAG, "writeCardRcd: 读10块错误");
//                    return false;
//                }
//                //本次消费后的原额
//                byte[] dtF = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
//                Log.d(TAG, "writeCardRcd: 副本读10块返回：" + Datautils.byteArrayToString(dtF));
//                if (!Arrays.equals(dtF, dtZ)) {
//                    Log.d(TAG, "writeCardRcd: 正副本判断返回");
//                    return false;
//                }
//                //step 7
//                CInfo.fProc += 1;
//                if (!Modify_InfoArea(24)) {
//                    Log.e(TAG, "writeCardRcd: 改写24错误");
//                    return false;
//                }
//                //step 8
//                fErr = 0;
//                if (!Modify_InfoArea(25)) {
//                    Log.e(TAG, "writeCardRcd: 改写25错误");
//                    return false;
//                }
//                break;
//            } catch (RemoteException e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
//        if (fErr == 1) {
//            //添加灰记录 报语音请重刷
//            return false;
//        }
//        //添加正常交易记录 报语音显示界面
        return true;
    }

    private int fErr = -1;

    private boolean m1CardKeyAuth(int blk, int key) {
        try {
            byte[] lodKeys = lodkey[key];
            retvalue = mBankCard.m1CardKeyAuth(0x41, blk, lodKeys.length, lodKeys, snUid.length, snUid);
            if (retvalue != 0) {
                return false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 改写24 25块数据
     *
     * @param blk 块号
     * @return
     */
    private boolean Modify_InfoArea(int blk) {
        byte[] info = new byte[16];
        byte[] tpdt = new byte[16];
        byte chk;
        int i;
        info[0] = CInfo.cPtr;
        System.arraycopy(CInfo.iPurCount, 0, info, 1, 2);
        info[3] = CInfo.fProc;
        System.arraycopy(CInfo.iYueCount, 0, info, 4, 2);
        info[6] = CInfo.fBlack;
        info[7] = CInfo.fFileNr;

        System.arraycopy(new byte[]{(byte) 0xff, (byte) 0xff, (byte)
                0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}, 0, info, 8, 7);
        for (chk = 0, i = 0; i < 15; i++) {
            chk ^= info[i];
        }
        info[15] = chk;
        try {
            //认证6扇区24块
            byte[] lodKeys = lodkey[6];
            retvalue = mBankCard.m1CardKeyAuth(0x41, blk, lodKeys.length, lodKeys, snUid.length, snUid);
            if (retvalue != 0) {
                Log.e(TAG, "Modify_InfoArea: 认证6扇区24块失败");
                return false;
            }
            retvalue = mBankCard.m1CardWriteBlockData(blk, info.length, info);
            if (retvalue != 0) {
                Log.e(TAG, "Modify_InfoArea: 写6扇区24块错误");
                return false;
            }
            retvalue = mBankCard.m1CardReadBlockData(blk, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "Modify_InfoArea: 读6扇区24块错误");
                return false;
            }
            tpdt = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
            if (!Arrays.equals(info, tpdt)) {
                Log.e(TAG, "Modify_InfoArea: ");
                return false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * byte数组取倒叙
     *
     * @param strings
     * @return
     */
    public byte[] ReverseSelf(byte[] strings) {
        for (int start = 0, end = strings.length - 1; start < end; start++, end--) {
            byte temp = strings[end];
            strings[end] = strings[start];
            strings[start] = temp;
        }
        return strings;
    }

    /**
     * 钱包/月票正本或副本余额有效性检测
     *
     * @param dts
     * @return
     */
    public boolean ValueBlockValid(byte[] dts) {//
        int i;
        // 钱包/月票原码反码比较
        for (i = 4; i < 12; i++) {
            if (dts[i - 4] != ~dts[i]) {
                // 不相符返回假
                return false;
            }
        }
        // 钱包/月票校验字正反码比较
        for (i = 13; i < 16; i++) {
            if (dts[i - 1] != ~dts[i]) {
                // 不相符返回假
                return false;
            }
        }
        // 钱包/月票正副本有效，返回真
        return true;
    }

    /**
     * 封装接口自定义
     *
     * @param cardType 卡片类型
     * @param sendApdu 发送指令
     * @return 结果
     */
    private byte[] sendApdus(int cardType, byte[] sendApdu) {
        byte[] reBytes = null;
        //微智接口返回数据
        byte[] respdata = new byte[512];
        //微智接口返回数据长度
        int[] resplen = new int[1];
        try {
            int retvalue = mBankCard.sendAPDU(cardType, sendApdu, sendApdu.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                Log.e(TAG, "微智接口返回错误码" + retvalue);
                return reBytes;
            }
            if (!Arrays.equals(APDU_RESULT_SUCCESS, Datautils.cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                return Datautils.cutBytes(respdata, resplen[0] - 2, 2);
            }
            reBytes = Datautils.cutBytes(respdata, 0, resplen[0] - 2);
        } catch (RemoteException e) {
            e.printStackTrace();
            try {
                mBankCard.breakOffCommand();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
        return reBytes;
    }

    private byte[] pursub = {0x00, 0x00, 0x00, 0x01};

    private void icExpance() {
        ltime = System.currentTimeMillis();
        Log.d(TAG, "===start--消费记录3031send=== " + Datautils.byteArrayToString(SELEC_PPSE));
        byte[] resultBytes = sendApdus(BankCard.CARD_MODE_PICC, SELEC_PPSE);
        if (resultBytes == null) {
            isFlag = 1;
            Log.d(TAG, "===消费记录3031error=== " + Datautils.byteArrayToString(resultBytes));
            return;
        } else if (Arrays.equals(resultBytes, APDU_RESULT_FAILE)) {
            // TODO: 2019/1/3  查数据库黑名单报语音
        }
        List<String> listTlv = new ArrayList<>();
        TLV.anaTagSpeedata(resultBytes, listTlv);
        //获取交易时间
        systemTime = Datautils.getDateTime();
        if (listTlv.contains("A000000632010105")) {
            Log.d(TAG, "xiaofeichenggong: 解析到TLV发送0105 send：" + Datautils.byteArrayToString(SELECT_ICCARD_QIANBAO));
            //选择电子钱包应用
            resultBytes = sendApdus(BankCard.CARD_MODE_PICC, SELECT_ICCARD_QIANBAO);
            if (resultBytes == null || resultBytes.length == 2) {
                isFlag = 1;
                Log.e(TAG, "===解析到TLV发送0105 return===" + Datautils.byteArrayToString(resultBytes));
                return;
            }
        } else {
            Log.d(TAG, "===默认发送0105send===" + Datautils.byteArrayToString(SELECT_ICCARD_QIANBAO));
            //选择电子钱包应用
            resultBytes = sendApdus(BankCard.CARD_MODE_PICC, SELECT_ICCARD_QIANBAO);
            if (resultBytes == null) {
                Log.e(TAG, "icExpance: 获取交易时间错误");
                isFlag = 1;
                return;
            }
        }
        Log.d(TAG, "===0105 return===" + Datautils.byteArrayToString(resultBytes));
        Log.d(TAG, "===读15文件send===" + Datautils.byteArrayToString(READ_ICCARD_15FILE));
        //读应用下公共应用基本信息文件指令
        resultBytes = sendApdus(BankCard.CARD_MODE_PICC, READ_ICCARD_15FILE);
        if (resultBytes == null || resultBytes.length == 2) {
            Log.e(TAG, "===读15文件error===" + Datautils.byteArrayToString(resultBytes));
            isFlag = 1;
            return;
        }
        Log.d(TAG, "===IC读15文件 === retur:" + Datautils.byteArrayToString(resultBytes));
        System.arraycopy(resultBytes, 12, cardId, 0, 8);
        System.arraycopy(resultBytes, 0, file15_8, 0, 8);
        System.arraycopy(resultBytes, 2, city, 0, 2);
        Log.d(TAG, "===卡应用序列号 ===" + Datautils.byteArrayToString(cardId));

        Log.d(TAG, "===读17文件00b0send===" + Datautils.byteArrayToString(READ_ICCARD_17FILE));
        resultBytes = sendApdus(BankCard.CARD_MODE_PICC, READ_ICCARD_17FILE);
        if (resultBytes == null || resultBytes.length == 2) {
            Log.e(TAG, "===读17文件error===" + Datautils.byteArrayToString(resultBytes));
            isFlag = 1;
            return;
        }
        Log.d(TAG, resplen[0] + "===IC读17文件return===" + Datautils.byteArrayToString(resultBytes));
        Log.d(TAG, "===IC读1E文件 00b2send===00B201F400");
        resultBytes = sendApdus(BankCard.CARD_MODE_PICC, Datautils.HexString2Bytes("00B201F400"));
        if (resultBytes == null || resultBytes.length == 2) {
            Log.e(TAG, "===IC读1E文件error===" + Datautils.byteArrayToString(resultBytes));
            isFlag = 1;
            return;
        }
        Log.d(TAG, "===IC读1E文件00b2return===" + Datautils.byteArrayToString(resultBytes));


        Log.d(TAG, "===IC余额(805c)send===  805C030204");
        resultBytes = sendApdus(BankCard.CARD_MODE_PICC, Datautils.HexString2Bytes("805C030204"));
        if (resultBytes == null || resultBytes.length == 2) {
            Log.e(TAG, "===IC卡初始化(8050)error===" + Datautils.byteArrayToString(resultBytes));
            isFlag = 1;
            return;
        }
        Log.d(TAG, "===IC余额(805c)return===" + Datautils.byteArrayToString(resultBytes));
        Log.d(TAG, "===IC卡初始化(8050)send===" + Datautils.byteArrayToString(cpuCardInit()));
        resultBytes = sendApdus(BankCard.CARD_MODE_PICC, cpuCardInit());
        if (resultBytes == null || resultBytes.length == 2) {
            Log.e(TAG, "===IC卡初始化(8050)error===" + Datautils.byteArrayToString(resultBytes));
            isFlag = 1;
            return;
        }
        System.arraycopy(resultBytes, 0, blance, 0, 4);
        System.arraycopy(resultBytes, 4, ATC, 0, 2);
        System.arraycopy(resultBytes, 6, keyVersion, 0, 4);
        flag = resultBytes[10];
        System.arraycopy(resultBytes, 11, rondomCpu, 0, 4);
        Log.d(TAG, "===IC卡初始化(8050)return=== " + Datautils.byteArrayToString(resultBytes) + "\n" +
                "===电子钱包余额:" + Datautils.byteArrayToInt(blance) + "\n" +
                "===CPU卡脱机交易序号:  " + Datautils.byteArrayToString(ATC) + "\n" +
                "===密钥版本 : " + (int) flag + "\n" + "===随机数 : " + Datautils.byteArrayToString(rondomCpu));
        // TODO: 2019/1/8  设定本次消费额
        if ((Datautils.byteArrayToInt(blance) / 100) > 1000) {
            // TODO: 2019/1/8  语音请投币  界面显示卡片已损坏
        }
        if ((Datautils.byteArrayToInt(blance) / 100) < 0.01) {
            // TODO: 2019/1/8 语音/界面  余额不足
        }
        byte[] psam_mac1 = initSamForPurchase(pursub);

        Log.d(TAG, "===获取MAC1(8070)send===" + Datautils.byteArrayToString(psam_mac1));
        resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, psam_mac1);
        if (resultBytes == null || resultBytes.length == 2) {
            Log.e(TAG, "===获取MAC1(8070)error===" + Datautils.byteArrayToString(resultBytes));
            isFlag = 1;
            return;
        }
        Log.d(TAG, "===获取MAC18070return===" + Datautils.byteArrayToString(resultBytes));
        praseMAC1(resultBytes);
        if (CAPP == 1) {
            //80dc
            String update80Dc = "80DC00F030060000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
            Log.d(TAG, "===更新1E文件(80dc)send===" + update80Dc);
            resultBytes = sendApdus(BankCard.CARD_MODE_PICC, Datautils.HexString2Bytes(update80Dc));
            if (resultBytes == null || resultBytes.length == 2) {
                Log.e(TAG, "===更新1E文件(80dc)error===" + Datautils.byteArrayToString(resultBytes));
                isFlag = 1;
                return;
            }
            Log.d(TAG, "===更新1E文件return===" + Datautils.byteArrayToString(resultBytes));
        }
        byte[] cmd = getIcPurchase();
        Log.d(TAG, "===IC卡(8054)消费send===" + Datautils.byteArrayToString(cmd));
        resultBytes = sendApdus(BankCard.CARD_MODE_PICC, cmd);
        if (resultBytes == null || resultBytes.length == 2) {
            Log.e(TAG, "===IC卡(8054)消费error===" + Datautils.byteArrayToString(resultBytes));
            isFlag = 1;
            return;
        }
        Log.d(TAG, "===IC卡(8054)消费返回===" + Datautils.byteArrayToString(resultBytes));
        byte[] mac2 = Datautils.cutBytes(resultBytes, 0, 8);
        byte[] PSAM_CHECK_MAC2 = checkPsamMac2(mac2);
        Log.d(TAG, "===psam卡 8072校验 send===: " + Datautils.byteArrayToString(PSAM_CHECK_MAC2));
        resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, PSAM_CHECK_MAC2);
        if (resultBytes == null) {
            Log.e(TAG, "===psam卡(8072)校验error===");
            isFlag = 1;
            return;
        }
        Log.d(TAG, "===psam卡 8072校验返回===: " + Datautils.byteArrayToString(resultBytes));
        isFlag = 0;
        handler.sendMessage(handler.obtainMessage(1, Datautils.byteArrayToInt(blance)));
        Log.i("stw", "===消费结束===" + (System.currentTimeMillis() - ltime));
    }


    public static byte[] double2Bytes(double d) {
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return byteRet;
    }

    public static double bytes2Double(byte[] arr) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: //CPU卡消费成功UI更新
                    int balances = (int) msg.obj;
                    PlaySound.play(PlaySound.dang, 0);
                    mLayoutLineInfo.setVisibility(View.GONE);
                    mLayoutXiaofei.setVisibility(View.VISIBLE);
                    mTvBalanceTitle.setText("余额");
                    mTvXiaofeiMoney.setText("0.01元");
                    Log.i("rerer", "余额：：：" + ((double) balances / 100));
                    mTvBalance.setText(((double) balances / 100) + "元");
                    handler.postDelayed(runnable, 2000);
                    break;
                case 2: //M1卡消费成功
                    PlaySound.play(PlaySound.dang, 0);
                    int blance = Datautils.byteArrayToInt(icCardBeen.getPurorimoney(), false) - Datautils.byteArrayToInt(icCardBeen.getPursub());
                    mTvBalance.setVisibility(View.VISIBLE);
                    mTvBalance.setText("余额：" + (double) blance / 100 + "元");
                    handler.postDelayed(runnable, 2000);
                    break;
                case 3:
                    //扫码支付成功
                    PlaySound.play(PlaySound.xiaofeiSuccse, 0);
                    mLayoutLineInfo.setVisibility(View.GONE);
                    mTvBalance.setText("1.00元");
                    mLayoutXiaofei.setVisibility(View.VISIBLE);
                    mTvXiaofeiMoney.setVisibility(View.GONE);
                    mTvXiaofeiTitle.setText("消费成功!");
                    handler.postDelayed(runnable, 2000);
                    break;
                case 6:
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 用户卡(IC卡)交易初始化指令 8050指令
     *
     * @return 8050指令
     */
    private byte[] cpuCardInit() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("805001020B");
        if (CAPP == 1) {
            stringBuilder.replace(5, 6, "3");
        }
        stringBuilder.append(Datautils.byteArrayToString(psamDatas.get(0).getKeyID())).append(Datautils.byteArrayToString(pursub)).append(Datautils.byteArrayToString(psamDatas.get(0).getTermBumber())).append("0F");
        return Datautils.HexString2Bytes(stringBuilder.toString());
    }

    /**
     * PSAM 卡产生MAC1指令 8070
     *
     * @param balance
     * @return 返回结果为：XXXXXXXX（终端脱机交易序号）XXXXXXXX（MAC1）
     */
    private byte[] initSamForPurchase(byte[] balance) {
        byte[] cmd = new byte[42];
        cmd[0] = (byte) 0x80;
        cmd[1] = 0x70;
        cmd[2] = 0x00;
        cmd[3] = 0x00;
        cmd[4] = 0x24;
        System.arraycopy(rondomCpu, 0, cmd, 5, 4);
        System.arraycopy(ATC, 0, cmd, 9, 2);
        System.arraycopy(balance, 0, cmd, 11, 4);
        //是否为复合消费
        if (CAPP == 1) {
            cmd[15] = (byte) 0x09;
        } else {
            cmd[15] = (byte) 0x06;
        }
        //系统时间
        System.arraycopy(systemTime, 0, cmd, 16, 7);
        cmd[23] = 0x01;
        cmd[24] = 0x00;
        System.arraycopy(cardId, 0, cmd, 25, 8);
        System.arraycopy(file15_8, 0, cmd, 33, 8);
        cmd[41] = (byte) 0x08;

        return cmd;
    }

    /**
     * 用户卡扣款指令[终端向用户卡发送] 8054
     *
     * @return 8054指令
     */
    private byte[] getIcPurchase() {
        byte[] cmd = new byte[21];
        cmd[0] = (byte) 0x80;
        cmd[1] = (byte) 0x54;
        cmd[2] = (byte) 0x01;
        cmd[3] = (byte) 0x00;
        cmd[4] = (byte) 0x0F;
        System.arraycopy(PSAM_ATC, 0, cmd, 5, 4);
        //PSAM_ATC 4 //系统时间
        System.arraycopy(systemTime, 0, cmd, 9, 7);
        System.arraycopy(MAC1, 0, cmd, 16, 4);
        cmd[20] = 0x08;
        return cmd;
    }

    /**
     * PSAM卡校验MAC2指令[终端向PSAM卡发送]：8072 000004 XXXXXXXX（MAC2 用户卡8054指令返回）
     *
     * @param data 8070返回 mac2
     * @return 返回 8072校验mac2
     */
    private byte[] checkPsamMac2(byte[] data) {
        String psam_mac2 = "8072000004" + Datautils.byteArrayToString(Datautils.cutBytes(data, 4, 4));
        return Datautils.HexString2Bytes(psam_mac2);
    }

    /**
     * 获取psam MAC1
     *
     * @param data
     */
    private void praseMAC1(byte[] data) {
        if (data.length <= 2) {
            Log.e(TAG, "===获取MAC1失败===" + Datautils.byteArrayToString(data));
            return;
        }
        System.arraycopy(data, 0, PSAM_ATC, 0, 4);
        System.arraycopy(data, 4, MAC1, 0, 4);
    }


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
        unregisterReceiver(receiver);
        try {
            mBankCard.breakOffCommand();
            mBankCard = null;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        try {
        //会闪退
//            mBankCard.openCloseCardReader(BankCard.CARD_MODE_PSAM1,0x02);
//            mBankCard.breakOffCommand();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    public void test(int blk) {
        ///////////////////////Start Consuming//////////////////////////
//        int fJudge = 0;
//        if ((CInfoZ.fValid) == 1 && (CInfoF.fValid) == 1 &&
//                ((CInfoZ.fProc & 0x01) == 0) && ((CInfoF.fProc & 0x01) == 0)) {
//
//        } else if ((CInfoZ.fValid == 1) && (CInfoF.fValid == 1) &&
//                ((CInfoF.fProc & 0x01) == 0)) {
//            if (!Modify_InfoArea(24)) {
//                return;
//            }
//        } else if ((CInfoZ.fValid == 1) && (CInfoF.fValid == 1) &&
//                ((CInfoZ.fProc & 0x01) == 1) && ((CInfoF.fProc & 0x01) == 0)) {
//
//            CInfo = CInfoF;
//            if (!Modify_InfoArea(24)) {
//                return;
//            }
//        } else if ((CInfoZ.fValid == 1) && (CInfoF.fValid == 0) && ((CInfoZ.fProc & 0x01) == 1)) {
//            CInfo.cPtr = (byte) (CInfoZ.cPtr == 0 ? 8 : (CInfoZ.cPtr - 1));
//            CInfo.fProc = (byte) (CInfo.fProc + 1);
//            if (CInfoZ.fProc == 1) {
//                int count = Datautils.byteArrayToInt(CInfoZ.iPurCount) - 1;
//                byte[] result = new byte[2];
//                result[0] = (byte) ((count >> 8) & 0xFF);
//                result[1] = (byte) (count & 0xFF);
//                CInfo.iPurCount = result;
//
//            } else {
//                int count = Datautils.byteArrayToInt(CInfoZ.iYueCount) - 1;
//                byte[] result = new byte[2];
//                result[0] = (byte) ((count >> 8) & 0xFF);
//                result[1] = (byte) (count & 0xFF);
//                CInfoZ.iYueCount = result;
//            }
//            if (!Modify_InfoArea(25)) {
//                return;
//            }
//            if (!Modify_InfoArea(24)) {
//                return;
//            }
//        } else if ((CInfoZ.fValid == 1) && (CInfoF.fValid == 1) && ((CInfoZ.fProc & 0x01) == 1) && ((CInfoF.fProc & 0x01) == 1)) {
//            fJudge = 1;
//        } else if ((CInfoZ.fValid == 0) && (CInfoF.fValid == 1) && ((CInfoF.fProc & 0x01) == 1)) {
//            CInfo.fProc = (byte) (CInfo.fProc + 1);
//            if (!Modify_InfoArea(24)) {
//                return;
//            }
//            if (!Modify_InfoArea(25)) {
//                return;
//            }
//        } else if ((CInfoZ.fValid == 1) && (CInfoF.fValid == 1) && ((CInfoZ.fProc & 0x01) == 0) && ((CInfoF.fProc & 0x01) == 1)) {
//            if (!Modify_InfoArea(25)) {
//                return;
//            }
//        } else if ((CInfoZ.fValid == 1) && (CInfoF.fValid == 0) && ((CInfoZ.fProc & 0x01) == 0)) {
//            if (!Modify_InfoArea(25)) {
//                return;
//            }
//        } else {
//            return;
//        }
///////////////////////////////////////////////////////////////
//        try {
//            if (fJudge == 1) {
//                byte CardRcdDateTime[] = new byte[8];
//                byte[] CardRcdOriMoney = new byte[4];
//                byte[] CardRcdSub = new byte[4];
//                //代表扇区 2为钱包区 7为月票区
//                int CardRcdSec = 0;
//                blk = RcdBlkIndex[CInfo.cPtr == 0 ? 8 : CInfo.cPtr - 1];
//                if (!m1CardKeyAuth(blk, blk / 4)) {
//                    return;
//                }
//                retvalue = mBankCard.m1CardReadBlockData(blk, respdata, resplen);
//                if (retvalue != 0) {
//                    return;
//                }
//                byte[] RcdInCard = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
//                //todo utc时间转bcd时间 UTCtoBCDTime(ArrToVar( & RcdInCard[0], 4), CardRcdDateTime);
//                byte[] UTCTimes = Datautils.cutBytes(RcdInCard, 0, 4);
//                CardRcdOriMoney = Datautils.cutBytes(RcdInCard, 4, 4);
//                CardRcdSub = Datautils.cutBytes(RcdInCard, 8, 3);
//                if (RcdInCard[11] == 0x02) {
//                    CardRcdSec = 7;
//                } else {
//                    CardRcdSec = 2;
//                }
//                //比对 9 块10块
//                if (!BackupManage(CardRcdSec)) {
//                    return;  //_dt and _backup are all wrong
//                }
//                //原额倒叙 操作
//                actRemaining = icCardBeen.getPurorimoney();
//                if (CardRcdSec == 2) {
//                    icCardBeen.setPurorimoney(actRemaining);
//                }
//                if (Arrays.equals(CardRcdOriMoney, icCardBeen.getPurorimoney())) {
//                    CInfo.cPtr = (byte) (CInfoZ.cPtr == 0 ? 8 : CInfoZ.cPtr - 1);
//                    int count = Datautils.byteArrayToInt(CInfoZ.iPurCount) - 1;
//                    byte[] result = new byte[2];
//                    result[0] = (byte) ((count >> 8) & 0xFF);
//                    result[1] = (byte) (count & 0xFF);
//                    CInfo.iPurCount = result;
//                    CInfo.fProc = (byte) (CInfo.fProc + 1);
//                    if (!Modify_InfoArea(25)) {
//                        return;
//                    }
//                    if (!Modify_InfoArea(24)) {
//                        return;
//                    }
//                } else if (Datautils.byteArrayToInt(CardRcdOriMoney) - Datautils.byteArrayToInt(CardRcdSub) == Datautils.byteArrayToInt(icCardBeen.getPurorimoney())) {
//                    CInfo.fProc = (byte) (CInfo.fProc + 1);
//                    if (!Modify_InfoArea(24)) {
//                        return;
//                    }
//                    if (!Modify_InfoArea(25)) {
//                        return;
//                    }
//                } else {
//                    CInfo.fProc = (byte) (CInfo.fProc + 1);
//                    if (!Modify_InfoArea(25)) {
//                        return;
//                    }
//                    if (!Modify_InfoArea(24)) {
//                        return;
//                    }
//                }
//            } else {
//                CInfo.fProc = (byte) (CInfo.fProc + 1);
//                if (!Modify_InfoArea(25)) {
//                    return;
//                }
//                if (!Modify_InfoArea(24)) {
//                    return;
//                }
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
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
        if (hsmDecodeResults.length > 0) {
            HSMDecodeResult firstResult = hsmDecodeResults[0];
            if (Datautils.isUTF8(firstResult.getBarcodeDataBytes())) {
                try {
                    decodeDate = new String(firstResult.getBarcodeDataBytes(), "utf8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
//                    decodeDate = new String(firstResult.getBarcodeDataBytes(), "gbk");
                decodeDate = Datautils.byteArrayToString(firstResult.getBarcodeDataBytes());
            }

            if (decodeDate.equals(codes)) {
                return;
            }
            codes = decodeDate;
            Log.i(TAG, "二维码: " + decodeDate);
            ltime = System.currentTimeMillis();
            Log.e("stw", "微信解码：" + (System.currentTimeMillis() - ltime));
            switch (decodeDate.substring(0, 2)) {
                case "TX":
                    //腾讯（微信）
//                    decodeDate = "TXACn3tk21bPvjQAMBT3ZQdwY0NjAxMDATMzEwNTAwOTkwMTAwMDU1NjgyNQEFAAIQAABhIGG7Yu9CGRVcLW/QXDaqUADmAAALuAAAAAAAAAQCXtB3JtUiF/DEjJ4Gsxh2CUhK5MLNgU TzJbr8K7DzSQrLVzUtVteLeGsJvsixWimDyvzkMIg71PQAXC19rv+rQKkAAAAA3pVPmg\u003d\u003d";
                    mPresenter.checkWechatTianJin(decodeDate, 1, (byte) 1, (byte) 1
                            , "17430597", "12");
                    break;
                case "BS":
                    //博思二维码
                    mPresenter.checkBosiQrCode(decodeDate);
                    break;
                case "Ah":
                    mPresenter.checkYinLianCode(getApplicationContext(), decodeDate);
                    break;
                default:
                    //支付宝 二维码
                    mPresenter.checkAliQrCode(decodeDate, record_id,
                            pos_id, pos_mf_id, pos_sw_version,
                            merchant_type, currency, amount,
                            vehicle_id, plate_no, driver_id,
                            lineNumber, station_no, lbs_info,
                            record_type);
                    break;
            }


        }

    }

    String record_id = Datautils.getDefautCurrentTime() + "000002";
    String pos_id = "20170000000001";
    //机具设备终端编号
    String pos_mf_id = "9998112123";
    String pos_sw_version = "2.6.14.03arm";
    String merchant_type = "22";
    String currency = "156";
    int amount = 1;
    String vehicle_id = "vid9702";
    String plate_no = "粤A 095852";
    String driver_id = "0236245394";
    String lineNumber = "0";
    String station_no = "000010";
    String lbs_info = "aaaa";
    String record_type = "BUS";


    @Override
    public void success(String msg) {
        Log.i(TAG, "success::: " + msg);
    }

    @Override
    public void erro(String msg) {
        Log.i(TAG, "erro:::" + msg);
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

    private TianjinAlipayRes codeinfoData;

    @Override
    public void showCheckAliQrCode(TianjinAlipayRes tianjinAlipayRes) {
        codeinfoData = tianjinAlipayRes;
        Log.e(TAG, "checkAliQrCode:不为空 ");
        if (codeinfoData.result == ErroCode.SUCCESS) {
            // TODO: 2019/3/11 存储天津公交需要的数据
            try {
                SaveDataUtils.saveZhiFuBaoReqDataBean(tianjinAlipayRes);
            } catch (Exception e) {
                e.printStackTrace();
            }


//            AlipayQrCodeUpload alipayQrCodeUpload = new AlipayQrCodeUpload();
//            AlipayQrCodeUpload.DataBean dataBean = new AlipayQrCodeUpload.DataBean();
//            dataBean.setRecordType("ALIQR");
//            List<AlipayQrCodeUpload.DataBean> dataBeans = new ArrayList<>();
//            AlipayQrCodeUpload.DataBean.RecordBean recordBean = new AlipayQrCodeUpload.DataBean
//                    .RecordBean("09", "6410001", "1234567890",
//                    "123456789123", "12345678912345678912",
//                    "98765432198765432198", Datautils.getDefautCurrentTime(),
//                    "6410", "0", "12", "156", 0,
//                    1, 1, Datautils.byteArrayToAscii(codeinfoData.userId),
//                    "", "BUS", Datautils.byteArrayToAscii(codeinfoData
//                    .cardType), Datautils.byteArrayToAscii(codeinfoData.cardNo),
//                    Datautils.byteArrayToAscii(codeinfoData.alipayResult), "",
//                    "", decodeDate);
//            dataBean.setRecord(recordBean);
//            dataBeans.add(dataBean);
//            alipayQrCodeUpload.setData(dataBeans);
//            mPresenter.uploadAlipayRe(alipayQrCodeUpload);

            mPresenter.uploadAlipayRe();
            handler.sendMessage(handler.obtainMessage(3));
        } else {
            Log.i(TAG, "\n支付宝校验结果错误:" + codeinfoData.result);
        }
    }

    @Override
    public void showReleseAlipayJni(int result) {
        if (result == 1) {
            Log.i(TAG, "showReleseAlipayJni: 支付宝库关闭成功");
        } else {
            Log.e(TAG, "showReleseAlipayJni: 支付宝库关闭失败");
        }
    }

    private List<WechatQrcodeKey.MacKeyListBean> macKeyListBeans;
    private List<WechatQrcodeKey.PubKeyListBean> pubKeyListBeans;

    @Override
    public void showWechatPublicKey(WechatQrcodeKey wechatQrcodeKey) {
        if (wechatQrcodeKey != null) {
            macKeyListBeans = wechatQrcodeKey.getMacKeyList();
            pubKeyListBeans = wechatQrcodeKey.getPubKeyList();
            Log.i(TAG, "微信PblicKey: " + pubKeyListBeans.toString());
            Log.i(TAG, "微信macklicKey: " + macKeyListBeans.toString());
            if (macKeyListBeans != null && pubKeyListBeans != null) {
                mPresenter.wechatInitJin();
            } else {
                Log.i(TAG, "获取微信Key失败 " + macKeyListBeans.toString());
            }
        }
    }

    @Override
    public void showCheckWechatQrCode(int result, String wechatResult, String openId) {
        if (result == ErroCode.EC_SUCCESS) {
            Log.i(TAG, "微信结果: " + "openID" + openId + "结果" + wechatResult);
//            WeichatQrCodeUpload weichatQrCodeUpload = new WeichatQrCodeUpload();
//            WeichatQrCodeUpload.DataBean dataBean = new WeichatQrCodeUpload.DataBean();
//            dataBean.setRecordType("WECHATQR");
//            List<WeichatQrCodeUpload.DataBean> dataBeans = new ArrayList<>();
//            WeichatQrCodeUpload.DataBean.RecordBean recordBean = new WeichatQrCodeUpload.DataBean.RecordBean("08", "6410001", "1234567890", "123456789123", "12345678912345678912", "98765432198765432198", Datautils.getDefautCurrentTime(), "6410", "0", "12", "156", 0, 1, 1, openId, wechatResult, decodeDate);
//            dataBean.setRecord(recordBean);
//            dataBeans.add(dataBean);
//            weichatQrCodeUpload.setData(dataBeans);
            mPresenter.uploadWechatRe();
            Log.e("stw", "微信时间：" + (System.currentTimeMillis() - ltime));
            handler.sendMessage(handler.obtainMessage(3));
        } else {
            Log.i(TAG, "微信校验结果错误 " + result);

        }
    }

    @Override
    public void showBosikey(BosiQrcodeKey bosiQrcodeKey) {
        Log.i(TAG, "showBosikey: " + bosiQrcodeKey.toString());
        if (bosiQrcodeKey != null) {
            for (int i = 0; i < bosiQrcodeKey.getKeyList().size(); i++) {
                int re = mPresenter.updataBosiKey(bosiQrcodeKey.getKeyList().get(i).getCert());
                if (re == 0) {
                    Log.i(TAG, "showBosikey: 博思更新证书成功");
                } else {
                    Log.e(TAG, "showBosikey: 博思更新证书失败");
                }
            }
        }
    }


    @Override
    public void showBosiCerVersion(String vension) {

    }

    @Override
    public void showCheckBosiQrCode(QrCodeInfo qrCodeInfo) {
        if (qrCodeInfo != null) {
            Log.i(TAG, "checkBosiQrCode: " + qrCodeInfo.toString());
            Log.i(TAG, "checkBosiQrCode:n二维码具体信息 " + qrCodeInfo.toDetailString());
            BosiQrCodeUpload bosiQrCodeUpload = new BosiQrCodeUpload();
            BosiQrCodeUpload.DataBean dataBean = new BosiQrCodeUpload.DataBean();
            dataBean.setRecordType("BOSIQR");
            List<BosiQrCodeUpload.DataBean> dataBeans = new ArrayList<>();
            BosiQrCodeUpload.DataBean.RecordBean recordBean = new BosiQrCodeUpload.DataBean.RecordBean("03", "6410001", "1234567890", "123456789123", "12345678912345678912", "98765432198765432198", Datautils.getDefautCurrentTime(), "6410", "0", "12", "156", 0, 1, 1, qrCodeInfo.getQrCodeVersion(), qrCodeInfo.getQrCodeType(), qrCodeInfo.getQrCodeIssuer(), qrCodeInfo.getChannelId(), qrCodeInfo.getCardIssuer(), qrCodeInfo.getCardId(), qrCodeInfo.getCardType(), qrCodeInfo.getAppPosition(), qrCodeInfo.getPaymentType(), qrCodeInfo.getPaymentMode(), Integer.parseInt(qrCodeInfo.getCardBalance()), qrCodeInfo.getChannelExtends(), decodeDate);
            dataBean.setRecord(recordBean);
            dataBeans.add(dataBean);
            bosiQrCodeUpload.setData(dataBeans);
            Log.i(TAG, "showCheckBosiQrCode: " + recordBean.toString());
            mPresenter.uploadBosiRe(bosiQrCodeUpload);
            handler.sendMessage(handler.obtainMessage(3));
        }
    }

    @Override
    public void showUpdataBosiKey(int state) {
        if (state == 0) {
            Log.i(TAG, "showUpdataBosiKey: 更新证书成功");
        } else {
            Log.i(TAG, "showUpdataBosiKey: 更新证书失败");
        }
    }

    @Override
    public void doCheckWechatTianJin() {

    }
}
