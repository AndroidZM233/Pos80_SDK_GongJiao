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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.honeywell.barcode.HSMDecodeResult;
import com.honeywell.plugins.decode.DecodeResultListener;
import com.spd.alipay.Datautils;
import com.spd.alipay.been.AliCodeinfoData;
import com.spd.alipay.been.AlipayPublicKey;
import com.spd.alipay.been.AlipayUploadBeen;
import com.spd.bus.DataConversionUtils;
import com.spd.bus.MyApplication;
import com.spd.bus.R;
import com.spd.bus.spdata.been.ErroCode;
import com.spd.bus.spdata.been.IcCardBeen;
import com.spd.bus.spdata.been.PsamBeen;
import com.spd.bus.spdata.been.TCommInfo;
import com.spd.bus.spdata.spdbuspay.SpdBusPayContract;
import com.spd.bus.spdata.spdbuspay.SpdBusPayPresenter;
import com.spd.bus.spdata.utils.DataUtils;
import com.spd.bus.spdata.utils.PlaySound;
import com.spd.bus.util.TLV;
import com.wechat.been.WechatPublicKey;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libbasebinder.HEX;

import static com.spd.bus.spdata.been.ErroCode.ILLEGAL_PARAM;
import static com.spd.bus.spdata.been.ErroCode.NO_ENOUGH_MEMORY;
import static com.spd.bus.spdata.been.ErroCode.SYSTEM_ERROR;

public class PsamIcActivity extends com.spd.bus.spdata.mvp.MVPBaseActivity<SpdBusPayContract.View, SpdBusPayPresenter> implements SpdBusPayContract.View, DecodeResultListener {
    private BankCard mBankCard;
    private Core mCore;
    private static final String TAG = "PsamIcActivity";
    private byte[] deviceCode;//终端编号
    private byte[] psamKey;//秘钥索引
    private int CAPP = 0; //普通交易（CAPP=0）或复合交易（CAPP=1）
    private List<PsamBeen> psamDatas = new ArrayList<>();

    //=======XXXXXXXX（余额）XXXX（CPU卡脱机交易序号）XXXXXX XX（密钥版本）XX（算法标识）XXXXXXXX（随机数）
    private byte[] blance = new byte[4];
    private byte[] ATC = new byte[2];
    private byte[] keyVersion = new byte[4];
    private byte[] rondomCpu = new byte[4];
    //=======
    private byte flag;
    private byte[] cardId = new byte[8];
    private byte[] city = new byte[2];
    private byte[] file15_8 = new byte[8];

    /**
     * 消费返回流程错误标识
     */
    private int isExpense = 0;

    private final byte[] APDU_RESULT_SUCCESS = {(byte) 0x90, 0x00};//返回正确结果
    private final byte[] APDU_RESULT_FAILE = {(byte) 0x62, (byte) 0x83};//返回错误结果
    private byte[] respdata = new byte[512];//微智接口返回数据
    private int[] resplen = new int[1];//微智接口返回数据长度
    private int retvalue = -1; //微智接口返回状态 非0错误
    private byte[] systemTime; //消费时系统时间


    //获取PSAM卡终端机编号指令
    private final byte[] psam1_get_id = {0x00, (byte) 0xB0, (byte) 0x96, 0x00, 0x06};
    //交通部
    private final byte[] psam2_select_dir = {0x00, (byte) 0xA4, 0x00, 0x00, 0x02, (byte) 0x80, 0x11};
    //住建部
    private final byte[] psamzhujian_select_dir = {0x00, (byte) 0xA4, 0x00, 0x00, 0x02, (byte) 0x10, 0x01};
    //读取psam卡17文件
    private final byte[] psam3_get_index = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x01};


    //选择PPSE支付环境
    private final byte[] fuhe_tlv = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0e, 0x32, 0x50, 0x41, 0x59, 0x2e, 0x53, 0x59, 0x53, 0x2e,
            0x44, 0x44, 0x46, 0x30, 0x31};
    //选择电子钱包应用
    private final byte[] ic_file = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, (byte) 0xA0, 0x00, 0x00, 0x06, 0x32, 0x01, 0x01, 0x05};
    //读ic卡应用下公共应用基本信息文件指令 15文件
    private final byte[] ic_read_file = {0x00, (byte) 0xB0, (byte) 0x95, 0x00, 0x00};
    private byte[] secF;//扇区标识符
    private byte[][] lodkey = new byte[16][6]; //保存读第0扇区 01块返回的 秘钥
    private byte[] snUid;
    private byte[] actRemaining;
    private byte[] PSAM_ATC = new byte[4];
    private byte[] MAC1 = new byte[4];
    /**
     * 产品编号：0123456789  2018/09/25   星期二   16:28
     */
    private TextView mTvTitle;
    /**
     * 公交线路：888路
     */
    private TextView mTvCircuit;
    /**
     * 票价：2元
     */
    private TextView mTvPrice;
    /**
     * 余额：
     */
    private TextView mTvBalance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.attachView(this);
        setContentView(R.layout.bus_layout);
        initView();
//        mPresenter.attachView(this);
        initCard();
    }
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.bus_layout);
//        initView();
//        mPresenter.attachView(this);
//        initCard();
//    }

    private void initView() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setText("产品编号：0123456789    " + DataUtils.getNowTime());
        mTvCircuit = (TextView) findViewById(R.id.tv_circuit);
        mTvPrice = (TextView) findViewById(R.id.tv_price);
        mTvBalance = (TextView) findViewById(R.id.tv_balance);
        CheckBox checkBox = findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CAPP = 1;
                } else {
                    CAPP = 0;
                }
            }
        });
    }

    private void initCard() {
        MyApplication.getHSMDecoder().addResultListener(this);
        //注册系统时间广播 只能动态注册
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mBankCard = new BankCard(getApplicationContext());
//                mCore = new Core(getApplicationContext());
//                psamInit();
//                psamZhujianbuInit();
//                handler.postDelayed(runnable, 0);
//            }
//        }).start();
        //获取支付宝微信key
        mPresenter.getAliPublicKey();
        mPresenter.getWechatPublicKey();
    }


    /**
     * 更新时间
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                mTvTitle.setText("产品编号：0123456789    " + DataUtils.getNowTime());
            }
        }
    };

    /**
     * psam 初始化流程
     */
    private void psamInit() {
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PSAM1, 60, respdata, resplen, "app1");
            Log.d(TAG, "交通部切换psam：" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
            if (retvalue == 0) {
                if (respdata[0] == (byte) 0x05) {//IC卡已经插入
                    retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, psam1_get_id, psam1_get_id.length, respdata, resplen);
                    if (retvalue == 0) {
                        Log.d(TAG, "交通部16文件：" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
                        if (DataConversionUtils.byteArrayToString(cutBytes(respdata, resplen[0] - 2, 2)).equals("9000")) {
                            deviceCode = cutBytes(respdata, 0, resplen[0] - 2);//终端机编号
                            handler.sendMessage(handler.obtainMessage(1, "交通部PSAM卡终端机编号: " + HEX.bytesToHex(deviceCode)));
                            Log.d(TAG, "====交通部PSAM卡终端机编号==== " + HEX.bytesToHex(deviceCode) + "   " + retvalue);
                            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, psam2_select_dir, psam2_select_dir.length, respdata, resplen);
                            if (retvalue == 0) {
                                Log.d(TAG, "===交通部80 11 ====" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "   " + retvalue);
                                if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                                    handler.sendMessage(handler.obtainMessage(1, "交通部8011return: " + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0] - 2))));
                                    retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, psam3_get_index, psam3_get_index.length, respdata, resplen);
                                    if (retvalue == 0) {
                                        Log.d(TAG, "===交通部读17文件获取秘钥索引===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
                                        if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                                            psamKey = cutBytes(respdata, 0, 1);
                                            Log.d(TAG, "交通部秘钥索引: " + HEX.bytesToHex(psamKey) + "\n" + "PSAM初始化成功！！！请读消费卡\n");
                                            handler.sendMessage(handler.obtainMessage(1, "交通部秘钥索引: " + HEX.bytesToHex(psamKey) + "\n" + "PSAM初始化成功！！！请读消费卡\n"));
                                            psamDatas.add(new PsamBeen(new byte[]{0, 0}, deviceCode, psamKey));
                                            //切换等待读消费卡

                                        } else {
                                            mCore.buzzer();
                                            handler.sendMessage(handler.obtainMessage(1, "交通部获取秘钥索引错误:" + cutBytes(respdata, resplen[0] - 2, 2)));

                                            Log.d(TAG, "交通部获取秘钥索引错误:" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)));
                                        }
                                    } else {
                                        mCore.buzzer();
                                    }
                                } else {
                                    mCore.buzzer();
                                    handler.sendMessage(handler.obtainMessage(1, "交通部切换8011错误:" + cutBytes(respdata, resplen[0] - 2, 2)));
                                    Log.d(TAG, "交通部切换8011错误:" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)));
                                }
                            } else {
                                mCore.buzzer();
                            }
                        } else {
                            mCore.buzzer();
                            handler.sendMessage(handler.obtainMessage(1, "交通部获取终端编号错误:" + cutBytes(respdata, resplen[0] - 2, 2)));
                            Log.d(TAG, "交通部获取终端编号错误:" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)));
                        }
                    } else {
                        mCore.buzzer();
                    }
                }
            } else {
                if (respdata[0] == (byte) 0x01) {
                    mCore.buzzer();
                    handler.sendMessage(handler.obtainMessage(1, "交通部psam初始化 读卡失败，,请检查是否插入psam卡 "));
                    Log.d(TAG, "交通部psam初始化 读卡失败,请检查是否插入psam卡 " + HEX.bytesToHex(respdata));
                } else {
                    mCore.buzzer();
                    handler.sendMessage(handler.obtainMessage(1, "psamInit:psam初始化失败微智返回：" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0]))));
                    Log.d(TAG, "交通部psam初始化失败 " + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));

                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 住建部psam初始化
     */
    private void psamZhujianbuInit() {
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PSAM2, 60, respdata, resplen, "app1");
            Log.d(TAG, "住建部切换psam " + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
            if (retvalue == 0) {
                if (respdata[0] == (byte) 0x05) {//IC卡已经插入
                    retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, psam1_get_id, psam1_get_id.length, respdata, resplen);
                    if (retvalue != 0) {
                        mCore.buzzer();
                    }
                    Log.d(TAG, "住建部16文件return" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
                    if (!Arrays.equals(cutBytes(respdata, resplen[0] - 2, 2), APDU_RESULT_SUCCESS)) {
                        mCore.buzzer();
                        handler.sendMessage(handler.obtainMessage(1, "住建部获取终端编号错误:" + cutBytes(respdata, resplen[0] - 2, 2)));
                        Log.d(TAG, "住建部获取终端编号错误:" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)));
                    }
                    deviceCode = cutBytes(respdata, 0, resplen[0] - 2);//终端机编号
                    handler.sendMessage(handler.obtainMessage(1, "住建部PSAM卡终端机编号: " + HEX.bytesToHex(deviceCode)));
                    Log.d(TAG, "====住建部PSAM卡终端机编号==== " + HEX.bytesToHex(deviceCode) + "   " + retvalue);
                    retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, psamzhujian_select_dir, psamzhujian_select_dir.length, respdata, resplen);
                    if (retvalue == 0) {
                        Log.d(TAG, "===住建部选文件 10 01 ====" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "   " + retvalue);
                        if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                            handler.sendMessage(handler.obtainMessage(1, "住建部1001return: " + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0] - 2))));
                            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, psam3_get_index, psam3_get_index.length, respdata, resplen);
                            if (retvalue == 0) {
                                Log.d(TAG, "===住建部17文件获取秘钥索引===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
                                if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                                    psamKey = cutBytes(respdata, 0, 1);
                                    Log.d(TAG, "住建部秘钥索引: " + HEX.bytesToHex(psamKey) + "\n" + "PSAM初始化成功！！！请读消费卡\n");
                                    handler.sendMessage(handler.obtainMessage(1, "住建部秘钥索引: " + HEX.bytesToHex(psamKey) + "\n" + "PSAM初始化成功！！！请读消费卡\n"));
                                    psamDatas.add(new PsamBeen(new byte[]{0, 0}, deviceCode, psamKey));
                                    for (int i = 0; i < psamDatas.size(); i++) {
                                        Log.d(TAG, "psamZhujianbuInit: 秘钥" + DataConversionUtils.byteArrayToString(psamDatas.get(i).getKeyID()) + "终端编号：" + DataConversionUtils.byteArrayToString(psamDatas.get(i).getTermBumber()));
                                    }
                                    //切换等待读消费卡
                                } else {
                                    mCore.buzzer();
                                    handler.sendMessage(handler.obtainMessage(1, "住建部获取秘钥索引错误:" + cutBytes(respdata, resplen[0] - 2, 2)));
                                    Log.d(TAG, "住建部获取秘钥索引错误:" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)));
                                }
                            } else {
                                mCore.buzzer();
                            }
                        } else {
                            mCore.buzzer();
                            handler.sendMessage(handler.obtainMessage(1, "住建部切换1001错误:" + cutBytes(respdata, resplen[0] - 2, 2)));
                            Log.d(TAG, "住建部切换1001错误:" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)));
                        }
                    } else {
                        mCore.buzzer();
                    }


                } else if (respdata[0] == (byte) 0x01) {//读卡失败
                    mCore.buzzer();
                    handler.sendMessage(handler.obtainMessage(1, "住建部psam初始化 读卡失败，,请检查是否插入psam卡 "));
                    Log.d(TAG, "住建部psam初始化 读卡失败,请检查是否插入psam卡 " + HEX.bytesToHex(respdata));
                } else {
                    mCore.buzzer();
                    handler.sendMessage(handler.obtainMessage(1, "住建部psam初始化 读卡失败，,请检查是否插入psam卡 "));
                    Log.d(TAG, "住建部psam初始化 读卡失败,请检查是否插入psam卡 " + HEX.bytesToHex(respdata));
                }
            } else {
                mCore.buzzer();
                handler.sendMessage(handler.obtainMessage(1, "住建部psam初始化失败微智返回：" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0]))));
                Log.d(TAG, "住建部psam初始化失败 " + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));

            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                int result = mBankCard.piccDetect();
                Log.i("stw", "run: 检测非接卡===" + result);
                if (result == 0) {
                    isExpense = 0;
                } else if (result == 1 && isExpense == 0) {
                    Log.i("stw", "run: 开始本次读卡等待");
                    Log.d("times", "icExpance: ===== 开始消费=====");
                    retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC, 60, respdata, resplen, "app1");//切换到非接卡读取
                    if (retvalue != 0) {
                        return;
                    }
                    Log.d(TAG, "test: 微智pose切换到非接卡读取：return" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
                    if (respdata[0] == 0x07) {//检测到非接IC卡
//                        tvShowMsg.append("检测到非接IC卡\n");
                        icExpance();//执行等待读卡消费
                        if (isExpense == 1) {
                            PlaySound.play(PlaySound.qingchongshua, 0);
                        }
                    } else if (respdata[0] == 0x37) {//检测到 M1-S50 卡
//                        tvShowMsg.append("检测到 M1-S50 卡\n");
                        m1ICCard();
                        if (isExpense == 1) {
                            PlaySound.play(PlaySound.qingchongshua, 0);
                        }
                    } else if (respdata[0] == 0x47) {// 检测到 M1-S70 卡
//                        tvShowMsg.append("检测到 M1-S70 卡\n");
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            handler.postDelayed(runnable, 100);
        }
    };
    private IcCardBeen icCardBeen = new IcCardBeen();


    private void m1ICCard() {
        CInfoF = new TCommInfo();
        CInfoZ = new TCommInfo();
        CInfo = new TCommInfo();
        try {
            Log.d(TAG, "m1ICCard:m1卡消费开始 ");
            //读取非接卡 SN(UID)信息
            retvalue = mBankCard.getCardSNFunction(respdata, resplen);
            if (retvalue != 0) {
                isExpense = 1;
                Log.e(TAG, "m1ICCard: 获取UID失败");
                return;
            }
            snUid = cutBytes(respdata, 0, resplen[0]);
            icCardBeen.setSnr(snUid);
            Log.d(TAG, "m1ICCard: getUID==" + HEX.bytesToHex(snUid));
            byte[] key = new byte[6];
            System.arraycopy(snUid, 0, key, 0, 4);
            System.arraycopy(snUid, 0, key, 4, 2);

            //认证1扇区第4块
            retvalue = mBankCard.m1CardKeyAuth(0x41, 0x04, key.length, key, snUid.length, snUid);
            if (retvalue != 0) {
                isExpense = 1;
                Log.e(TAG, "m1ICCard: 认证1扇区第4块失败");
                return;
            }
            retvalue = mBankCard.m1CardReadBlockData(0x04, respdata, resplen);
            if (retvalue != 0) {
                isExpense = 1;
                Log.e(TAG, "m1ICCard: 读取1扇区第4块失败");
                return;
            }
            byte[] bytes04 = cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "m1ICCard: 读取1扇区第4块返回：" + HEX.bytesToHex(bytes04));
            //!!!!!!!!!!!!!!!!!
//                retvalue = mBankCard.m1CardWriteBlockData(4, bytes04.length, bytes04);
//                if (retvalue != 0) {
//                    Log.e(TAG, "m1ICCard: 写失败" );
//                }
//                Log.d(TAG, "m1ICCard:" + retvalue + "写块 " + HEX.bytesToHex(bytes04));
//                retvalue = mBankCard.m1CardReadBlockData(4, respdata, resplen);
//                Log.d(TAG, "m1ICCard:" + retvalue + " 读块" + HEX.bytesToHex(respdata));
            //!!!!!!!!!!!!!!!!!!!


            icCardBeen.setIssueSnr(cutBytes(bytes04, 0, 8));
            icCardBeen.setCityNr(cutBytes(bytes04, 0, 2));
            icCardBeen.setVocCode(cutBytes(bytes04, 2, 2));
            icCardBeen.setIssueCode(cutBytes(bytes04, 4, 4));
            icCardBeen.setMackNr(cutBytes(bytes04, 8, 4));
            icCardBeen.setfStartUse(cutBytes(bytes04, 12, 1));
            icCardBeen.setCardType(cutBytes(bytes04, 13, 1));//卡类型判断表格中没有return
            icCardBeen.setfBlackCard(0);//黑名单
            switch (icCardBeen.getfStartUse()[0]) {//判断启用标志
                case (byte) 0x01://未启用
                    Log.e(TAG, "m1ICCard: 启用标志未启用");
                    isExpense = 1;
                    return;
                case (byte) 0x02://正常
                    // TODO: 2018/8/29
                    break;
                case (byte) 0x03://停用
                    Log.e(TAG, "m1ICCard: 启用标志停用");
                    isExpense = 1;
                    return;
                case (byte) 0x04://黑名单
                    Log.e(TAG, "m1ICCard: 启用标志黑名单");
                    isExpense = 1;
                    icCardBeen.setfBlackCard(1);
                    return;
                default:
                    break;
            }

            //读1扇区05块数据
            retvalue = mBankCard.m1CardReadBlockData(0x05, respdata, resplen);
            if (retvalue != 0) {
                isExpense = 1;
                Log.e(TAG, "m1ICCard: 读1扇区05块数据失败");
                return;
            }
            byte[] bytes05 = cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "m1ICCard: 读1扇区05块数据：" + HEX.bytesToHex(bytes05));
            icCardBeen.setIssueDate(cutBytes(bytes05, 0, 4));
            icCardBeen.setEndUserDate(cutBytes(bytes05, 4, 4));
            icCardBeen.setStartUserDate(cutBytes(bytes05, 8, 4));

            //读1扇区06块数据
            retvalue = mBankCard.m1CardReadBlockData(0x06, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "m1ICCard: 读1扇区06块数据失败");
                isExpense = 1;
                return;
            }
            byte[] bytes06 = cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "m1ICCard: 读1扇区06块数据返回：" + HEX.bytesToHex(bytes06));
            icCardBeen.setPurIncUtc(cutBytes(bytes06, 0, 6));//转UTC时间
            icCardBeen.setPurIncMoney(cutBytes(bytes06, 9, 2));

            //第0扇区 01块认证
            retvalue = mBankCard.m1CardKeyAuth(0x41, 0x01,
                    6, new byte[]{(byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3, (byte) 0xA4, (byte) 0xA5}, snUid.length, snUid);
            if (retvalue != 0) {
                Log.e(TAG, "m1ICCard: 第0扇区01块认证失败");
                isExpense = 1;
                return;
            }
            retvalue = mBankCard.m1CardReadBlockData(0x01, respdata, resplen);//读第0扇区第一块秘钥
            if (retvalue != 0) {
                Log.e(TAG, "m1ICCard: 读第0扇区01块失败");
                isExpense = 1;
                return;
            }

            byte[] bytes01 = cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "m1ICCard: 读第0扇区01块：" + HEX.bytesToHex(bytes01));
            //扇区标识符
            secF = bytes01;
            //算秘钥指令
            String sendCmd = "80FC010110" + HEX.bytesToHex(icCardBeen.getCityNr()) + DataConversionUtils.byteArrayToString(icCardBeen.getSnr()) + HEX.bytesToHex(cutBytes(icCardBeen.getIssueSnr(), 6, 2)) + HEX.bytesToHex(icCardBeen.getMackNr())
                    + HEX.bytesToHex(cutBytes(secF, 2, 2)) + HEX.bytesToHex(cutBytes(secF, 6, 2));
            Log.d(TAG, "m1ICCard:psam计算秘钥指令 ：" + sendCmd);
            //psam卡计算秘钥
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, DataConversionUtils.HexString2Bytes(sendCmd), DataConversionUtils.HexString2Bytes(sendCmd).length, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "m1ICCard: psam计算秘钥指令错误");
                isExpense = 1;
                return;
            }
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                Log.e(TAG, "m1ICCard: psam计算秘钥指令错误非9000");
                isExpense = 1;
                return;
            }
            byte[] result = cutBytes(respdata, 0, resplen[0] - 2);
            Log.d(TAG, "m1ICCard: psam计算秘钥返回：" + HEX.bytesToHex(result));
            //3/4/5扇区秘钥相同
            lodkey[2] = cutBytes(result, 0, 6);//第2扇区秘钥
            lodkey[3] = cutBytes(result, 6, 6);//第3扇区秘钥
            lodkey[4] = cutBytes(result, 6, 6);//第4扇区秘钥
            lodkey[5] = cutBytes(result, 6, 6);//第5扇区秘钥
            lodkey[6] = cutBytes(result, 12, 6);//第6扇区秘钥
            lodkey[7] = cutBytes(result, 18, 6);//第7扇区秘钥

            //第6扇区24 块认证
            byte[] lodKey6 = lodkey[6];
            retvalue = mBankCard.m1CardKeyAuth(0x41, 24, lodKey6.length, lodKey6, snUid.length, snUid);
            if (retvalue != 0) {
                Log.e(TAG, "m1ICCard: 第6扇区24 块认证错误");
                isExpense = 1;
                return;
            }
            //读6扇区第24块
            retvalue = mBankCard.m1CardReadBlockData(24, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "m1ICCard: 读6扇区第24块失败");
                isExpense = 1;
                return;
            }
            byte[] bytes24 = cutBytes(respdata, 1, resplen[0] - 1);

//            System.arraycopy(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}, 0, bytes24, 0, 8);
//            System.arraycopy(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}, 0, bytes24, 8, 7);
//            retvalue = mBankCard.m1CardWriteBlockData(24, bytes24.length, bytes24);
//            retvalue = mBankCard.m1CardWriteBlockData(25, bytes24.length, bytes24);
//            retvalue = mBankCard.m1CardReadBlockData(24, respdata, resplen);
//            if (retvalue != 0) {
//                Log.e(TAG, "m1ICCard: 读6扇区第24块失败");
//                isExpense = 1;
//                return;
//            }
//            bytes24 = cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "m1ICCard: 读6扇区第24块返回：" + HEX.bytesToHex(bytes24));
            byte[] dtZ = bytes24;
            byte chk = 0;
            for (int i = 0; i < 16; i++) {//异或操作
                chk ^= dtZ[i];
            }
            //判断8-15是否都等于0xff
            if (Arrays.equals(cutBytes(dtZ, 8, 7),
                    new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}) && chk == 0) {
                CInfoZ.fValid = 1;
            }
            if (Arrays.equals(cutBytes(dtZ, 0, 8), new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff})) {
                CInfoZ.fValid = 0;
            }
            if (dtZ[0] > 8) {
                CInfoZ.fValid = 0;
            }
            CInfoZ.cPtr = dtZ[0];//交易记录指针
            CInfoZ.iPurCount = cutBytes(dtZ, 1, 2);//钱包计数,2,3
            CInfoZ.fProc = dtZ[3];//进程标志
            CInfoZ.iYueCount = cutBytes(dtZ, 4, 2);
            CInfoZ.fBlack = dtZ[6];
            CInfoZ.fFileNr = dtZ[7];
            //副本  有效性
            //读6扇区第25块
            retvalue = mBankCard.m1CardReadBlockData(25, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "m1ICCard:读6扇区第25块失败 ");
                isExpense = 1;
                return;
            }
            byte[] bytes25 = cutBytes(respdata, 1, resplen[0] - 1);
            byte[] dtF = bytes25;
            for (int i = 0; i < 16; i++) {
                chk ^= dtF[i];
            }
            if (Arrays.equals(cutBytes(dtF, 8, 7),
                    new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,}) && chk == 0) {
                CInfoF.fValid = 1;
            }
            if (Arrays.equals(cutBytes(dtF, 0, 8), new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff})) {
                CInfoF.fValid = 0;
            }
            if (dtF[0] > 8) {
                CInfoF.fValid = 0;
            }
            CInfoF.cPtr = dtF[0];
            CInfoF.iPurCount = cutBytes(dtF, 1, 2);
            CInfoF.fProc = dtF[3];
            CInfoF.iYueCount = cutBytes(dtF, 4, 2);
            CInfoF.fBlack = dtF[6];
            CInfoF.fFileNr = dtF[7];

            if (CInfoZ.fValid == 1) {
                CInfo = CInfoZ;
            } else if (CInfoF.fValid == 1) {
                CInfo = CInfoF;
            } else {
                Log.e(TAG, "m1ICCard: 24 25块有效标志错误 返回0");
                isExpense = 1;
                return;
            }

            if ((CInfoZ.fValid == 1 && (CInfoZ.fBlack == 4)) || (CInfoF.fValid == 1 && (CInfoF.fBlack == 4))) {
                icCardBeen.setfBlackCard(1);//黑名单 报语音
                Log.e(TAG, "m1ICCard: 黑名单");
                isExpense = 1;
                return;
            }
            if (!BackupManage(8)) {
                isExpense = 1;
                return;
            }
            if (!writeCardRcd()) {
                isExpense = 1;
                return;
            }
            isExpense = 0;
        } catch (RemoteException e) {
            e.printStackTrace();

        }
    }

    private boolean BackupManage(int blk) {
        //第二扇区08 块认证
        try {
            byte[] lodKey2 = lodkey[blk / 4];
            retvalue = mBankCard.m1CardKeyAuth(0x41, blk, lodKey2.length, lodKey2, snUid.length, snUid);
            if (retvalue != 0) {
                Log.e(TAG, "m1ICCard:认证2扇区第9块失败：");
                isExpense = 1;
                return false;
            }
            //读2扇区第9块
            retvalue = mBankCard.m1CardReadBlockData(9, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "m1ICCard: 读2扇区第9块失败");
                isExpense = 1;
                return false;
            }
            byte[] bytes09 = cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "m1ICCard:读2扇区第9块返回： " + HEX.bytesToHex(bytes09));

            //读2扇区第10块
            retvalue = mBankCard.m1CardReadBlockData(10, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "m1ICCard:读2扇区第10块失败：");
                isExpense = 1;
                return false;
            }
            byte[] bytes10 = cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "m1ICCard:读2扇区第10块返回： " + HEX.bytesToHex(bytes10));

            if (ValueBlockValid(bytes09)) {
                Log.d(TAG, "m1ICCard: 2区09块过");
                //判断2区9块10块数据是否一致
                if (!Arrays.equals(bytes09, bytes10)) {
                    retvalue = mBankCard.m1CardValueOperation(0x3E, 9, 0, 10);
                    if (retvalue != 0) {
                        Log.e(TAG, "m1ICCard: 更新2区09块失败");
                        isExpense = 1;
                        return false;
                    }
                }
            } else {
                if (ValueBlockValid(bytes10)) {
                    Log.d(TAG, "m1ICCard: 2区10块过 ");
                    if (!Arrays.equals(bytes09, bytes10)) {
                        bytes09 = bytes10;
                        retvalue = mBankCard.m1CardValueOperation(0x3E, 10, 0, 9);
//                        retvalue = mBankCard.m1CardWriteBlockData(0x09, bytes10.length, bytes10);
                        if (retvalue != 0) {
                            Log.e(TAG, "m1ICCard: 写2区9块失败");
                            isExpense = 1;
                            return false;
                        }
                    }
                } else {
                    isExpense = 1;
                    Log.d(TAG, "m1ICCard: 2区10块错返回 ");
                    return false;
                }
            }
            //原额
            byte[] yue09 = cutBytes(bytes09, 0, 4);
            icCardBeen.setPurOriMoney(yue09);
            icCardBeen.setPurSub(new byte[]{0x00, 0x00, 0x00, 0x01});//定义消费金额
            Log.d(TAG, "m1ICCard: " + HEX.bytesToHex(yue09));
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            isExpense = 1;
            return false;
        }

    }

    private TCommInfo CInfoZ, CInfoF, CInfo;

    private byte RcdBlkIndex[] = {12, 13, 14, 16, 17, 18, 20, 21, 22};//所有“交易记录”块

    public boolean writeCardRcd() {
        //step 0
        CInfo.fFileNr = secF[2];//文件标识
        if (CInfo.cPtr > 8) {
            CInfo.cPtr = 0;
        }
        int blk = RcdBlkIndex[CInfo.cPtr];//当前交易记录块
        Log.d(TAG, "writeCardRcd: 当前交易记录块：" + blk);

        CInfo.cPtr = (byte) (CInfo.cPtr == 8 ? 0 : CInfo.cPtr + 1);//
        byte[] ulDevUTC = DataConversionUtils.HexString2Bytes(DataUtils.getUTCtimes());//获取UTC时间
//        if (tCardOpDu.ucSec != 2) {
//            VarToArr( & RcdToCard[4], tCardOpDu.YueOriMoney, 4);
//            VarToArr( & RcdToCard[8], tCardOpDu.YueSub, 3);
//            RcdToCard[11] = 2;
//            CInfo.fProc = 3;
//            CInfo.iYueCount = CInfo.iYueCount + 1;
//        } else {
        byte[] RcdToCard = new byte[16]; //写卡指令

        System.arraycopy(ulDevUTC, 0, RcdToCard, 0, 4);
        System.arraycopy(icCardBeen.getPurOriMoney(), 0, RcdToCard, 4, 4);//获取消费前原额
        System.arraycopy(icCardBeen.getPurSub(), 1, RcdToCard, 8, 3);//获取本次消费金额
        RcdToCard[11] = 1;
        //设备号写死
        RcdToCard[12] = 0x64;
        RcdToCard[13] = 0x10;
        RcdToCard[14] = 0x00;
        RcdToCard[15] = 0x01;
        CInfo.fProc = 1;//进程标志
        Log.d(TAG, "writeCardRcd: 本次交易记录指令：" + HEX.bytesToHex(RcdToCard));
        int count = DataConversionUtils.byteArrayToInt(CInfo.iPurCount) + 1;
        byte[] result = new byte[2];
        result[0] = (byte) ((count >> 8) & 0xFF);
        result[1] = (byte) (count & 0xFF);
        CInfo.iPurCount = result;

        for (; ; ) {
            try {
                //step 1 改写24 25块数据
                if (!Modify_InfoArea(24)) {
                    Log.e(TAG, "writeCardRcd: 改写24块错误");
                    return false;
                }
                //step 2
                if (!m1CardKeyAuth(blk, blk / 4)) {//blk/4 区    blk块
                    return false;
                }
                //写卡  将消费记录写入消费记录区
                retvalue = mBankCard.m1CardWriteBlockData(blk, RcdToCard.length, RcdToCard);
                if (retvalue != 0) {
                    Log.e(TAG, "writeCardRcd: 将消费记录写入消费记录区错误 块为" + blk);
                    return false;
                }
                //消费记录区读取
                retvalue = mBankCard.m1CardReadBlockData(blk, respdata, resplen);
                if (retvalue != 0) {
                    Log.e(TAG, "writeCardRcd: 读取消费记录区错误");
                    return false;
                }
                byte[] RcdInCard = cutBytes(respdata, 1, resplen[0] - 1);
                Log.d(TAG, "writeCardRcd: 读当前消费记录区数据：" + HEX.bytesToHex(RcdInCard));
                if (!Arrays.equals(RcdInCard, RcdToCard)) {
                    Log.e(TAG, "writeCardRcd: 读数据不等于消费返回错误");
                    return false;
                }
                byte[] bytes = new byte[16];
                if (Arrays.equals(RcdInCard, bytes)) {//判断是否 读回==00
                    Log.e(TAG, "writeCardRcd: 读数据不等于消费返回0错误");
                    return false;
                }

                //step 3
//            PrepareRecord(tCardOpDu.ucSec == 2 ? 1 : 3);   1代表 钱包灰记录 3 月票灰记录
                fErr = 1;
                if (!Modify_InfoArea(25)) {
                    Log.e(TAG, "writeCardRcd: 改写25块错误");
                    return false;                  // 改写25块，不成功退出
                }
                //step 4
                if (!m1CardKeyAuth(8, 2)) { //认证2扇区8块
                    return false;
                }

                //step 5
//            if (tCardOpDu.ucSec != 2) {
//                for (i = 0; i < 4; i++)
//                    dtZ[i] = (tCardOpDu.ActYueSub >> (8 * i));
//            } else {
//            }

                //执行消费 将消费金额带入
                int purSub = DataConversionUtils.byteArrayToInt(icCardBeen.getPurSub());
                retvalue = mBankCard.m1CardValueOperation(0x2D, 9, purSub, 9);

                if (retvalue != 0) {
                    Log.e(TAG, "writeCardRcd: 执行消费错误");
                    return false;
                }
                //执行 读出 现在原额
                retvalue = mBankCard.m1CardReadBlockData(10, respdata, resplen);
                if (retvalue != 0) {
                    Log.e(TAG, "writeCardRcd: 读原额错误");
                    return false;
                }
                byte[] dtZ = cutBytes(respdata, 1, resplen[0] - 1);//本次消费后的原额;
                byte[] tempV = cutBytes(dtZ, 0, 4);
                Log.d(TAG, "writeCardRcd:正本读09块返回：" + HEX.bytesToHex(dtZ));
                //判断消费前金额-消费金额=消费后金额
                int s = DataConversionUtils.byteArrayToInt(icCardBeen.getPurOriMoney(), false);
                int s2 = DataConversionUtils.byteArrayToInt(tempV, false);
                if (s - purSub != s2) {
                    return false;
                }
                //step 6
                retvalue = mBankCard.m1CardValueOperation(0x3E, 9, DataConversionUtils.byteArrayToInt(dtZ), 10);
                if (retvalue != 0) {
                    Log.e(TAG, "writeCardRcd: 写10块错误");
                    return false;
                }
                retvalue = mBankCard.m1CardReadBlockData(10, respdata, resplen);
                if (retvalue != 0) {
                    Log.e(TAG, "writeCardRcd: 读10块错误");
                    return false;
                }
                byte[] dtF = cutBytes(respdata, 1, resplen[0] - 1);//本次消费后的原额
                Log.d(TAG, "writeCardRcd: 副本读10块返回：" + HEX.bytesToHex(dtF));
                if (!Arrays.equals(dtF, dtZ)) {
                    Log.d(TAG, "writeCardRcd: 正副本判断返回");
                    return false;
                }
                //step 7
                CInfo.fProc += 1;
                if (!Modify_InfoArea(24)) {
                    Log.e(TAG, "writeCardRcd: 改写24错误");
                    return false;
                }
                //step 8
                fErr = 0;
                if (!Modify_InfoArea(25)) {
                    Log.e(TAG, "writeCardRcd: 改写25错误");
                    return false;
                }
                break;
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (fErr == 1) {
            //添加灰记录 报语音请重刷
            return false;
        }
        //添加正常交易记录 报语音显示界面
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
            tpdt = cutBytes(respdata, 1, resplen[0] - 1);
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
        for (i = 4; i < 12; i++) {// 钱包/月票原码反码比较
            if (dts[i - 4] != ~dts[i]) {
                return false;// 不相符返回假‘。；
            }
        }
        for (i = 13; i < 16; i++) {// 钱包/月票校验字正反码比较
            if (dts[i - 1] != ~dts[i]) {
                return false;// 不相符返回假
            }
        }
        return true;                                            // 钱包/月票正副本有效，返回真
    }

    private void icExpance() {
        try {
            Log.d(TAG, "fuhexiaofei: 消费记录 tlv 3031 send " + DataConversionUtils.byteArrayToString(fuhe_tlv));
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, fuhe_tlv, fuhe_tlv.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                Log.d(TAG, "消费记录 tlv 3031 error ");
                return;
            }
            byte[] testTlv = cutBytes(respdata, 0, resplen[0] - 2);
            Log.d(TAG, resplen[0] + "消费记录 tlv 3031 return :  " + HEX.bytesToHex(testTlv));
            if (Arrays.equals(cutBytes(respdata, resplen[0] - 2, 2), APDU_RESULT_FAILE)) {//黑名单

            } else if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                isExpense = 1;
            }
            boolean isFlag = true;
            List<String> listTlv = new ArrayList<>();
            TLV.anaTagSpeedata(testTlv, listTlv);
            for (int i = 0; i < listTlv.size(); i++) {
                Log.d(TAG, "test: 解析TLV" + i + "&&&&&&&" + listTlv.get(i).toString());
                //判断解析出来的tlv 61目录里是否 是否存在A000000632010105
                if (listTlv.get(i).equals("A000000632010105")) {
                    // TODO: 2018/8/17  APDU
                    isFlag = false;
                    systemTime = getDateTime();//获取交易时间
                    String select_ic = "00A4040008" + listTlv.get(i);
                    Log.d(TAG, "test: 解析到TLV发送0105 send：" + select_ic);
                    byte[] ELECT_DIANZIQIANBAO = DataConversionUtils.HexString2Bytes(select_ic);
                    retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, ELECT_DIANZIQIANBAO, ELECT_DIANZIQIANBAO.length, respdata, resplen);//选择电子钱包应用
                    break;
                }
            }
            if (isFlag) {
                systemTime = getDateTime();//获取交易时间
                Log.d(TAG, "test: 默认发送 0105 send ：" + HEX.bytesToHex(ic_file));
                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, ic_file, ic_file.length, respdata, resplen);//选择电子钱包应用
            }
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                Log.e(TAG, "icExpance: 获取交易时间错误");
                isExpense = 1;
                return;
            }
            Log.d(TAG, "test: 0105 return： " + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }
            Log.d(TAG, "===读15文件 === sebd" + HEX.bytesToHex(ic_read_file));
            //读应用下公共应用基本信息文件指令
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, ic_read_file, ic_read_file.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }
            Log.d(TAG, "===IC读15文件 === retur:" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }
            //02313750FFFFFFFF0201 031049906000000000062017010820991231000090000000000000000000
            Log.e(TAG, "icExpance:arraycopy开始 ");
            System.arraycopy(respdata, 12, cardId, 0, 8);
            Log.e(TAG, "icExpance:arraycopy 停止");
            System.arraycopy(respdata, 0, file15_8, 0, 8);
            System.arraycopy(respdata, 2, city, 0, 2);
            Log.d(TAG, "===卡应用序列号 ===" + HEX.bytesToHex(cardId));

            //读17文件
            byte[] IC_READ17_FILE = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x00};
            Log.d(TAG, "test: 读17文件 00b0 send:" + HEX.bytesToHex(IC_READ17_FILE));
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, IC_READ17_FILE, IC_READ17_FILE.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }
            Log.d(TAG, resplen[0] + "===IC读17文件 === return:" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "维智" + retvalue);
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }

            Log.d(TAG, "test: IC读1E文件 00b2 send  00B201F400");
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, DataConversionUtils.HexString2Bytes("00B201F400"), DataConversionUtils.HexString2Bytes("00B201F400").length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }
            Log.d(TAG, "test: IC读1E文件 00b2 return：" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "维智" + retvalue + "\n");
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }

            Log.d(TAG, "===805c IC余额===  send   :805C030204");
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, DataConversionUtils.HexString2Bytes("805C030204"), DataConversionUtils.HexString2Bytes("805C030204").length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }
            Log.d(TAG, "===IC余额 (805c)===  return  :" + HEX.bytesToHex(respdata) + "维智" + retvalue);
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }

            byte[] INIT_IC_FILE = initICcard();
            Log.d(TAG, "===IC卡初始化=== 8050 send   :" + HEX.bytesToHex(INIT_IC_FILE));
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, INIT_IC_FILE, INIT_IC_FILE.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }
            Log.d(TAG, "===IC卡初始化=== 8050  return:" + HEX.bytesToHex(respdata));
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }
            System.arraycopy(respdata, 0, blance, 0, 4);
            System.arraycopy(respdata, 4, ATC, 0, 2);
            System.arraycopy(respdata, 6, keyVersion, 0, 4);
            flag = respdata[10];
            System.arraycopy(respdata, 11, rondomCpu, 0, 4);
            Log.d(TAG, "===余额:  " + HEX.bytesToHex(blance));
            Log.d(TAG, "===CPU卡脱机交易序号:  " + HEX.bytesToHex(ATC));
            Log.d(TAG, "===密钥版本 : " + (int) flag);
            Log.d(TAG, "===随机数 : " + HEX.bytesToHex(rondomCpu));


            byte[] psam_mac1 = initSamForPurchase();
            Log.d(TAG, "===获取MAC1 8070  send===" + HEX.bytesToHex(psam_mac1));
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, psam_mac1, psam_mac1.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }
            Log.d(TAG, "===获取MAC1 8070 return:" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "   " + retvalue);
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }
            praseMAC1(respdata);


            //80dc
//            String ss = "80DC00F030060000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
//            Log.d(TAG, "===更新1E文件 80dc  send===" + ss);
//            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, DataConversionUtils.HexString2Bytes(ss), DataConversionUtils.HexString2Bytes(ss).length, respdata, resplen);
//            if (retvalue != 0) {
//                mBankCard.breakOffCommand();
//                isExpense = 1;
//                return;
//            }
//            Log.d(TAG, "===更新1E文件 return===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "   " + retvalue);
//            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
//                mBankCard.breakOffCommand();
//                isExpense = 1;
//                return;
//            }

            byte[] cmd = getIcPurchase();
            Log.d(TAG, "===IC卡 8054消费发送===" + HEX.bytesToHex(cmd));
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, cmd, cmd.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }
            Log.d(TAG, "===IC 卡 8054消费返回===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + " " + retvalue);
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                isExpense = 1;
                return;
            }
            byte[] mac2 = cutBytes(respdata, 0, 8);
            byte[] PSAM_CHECK_MAC2 = checkPsamMac2(mac2);


//            Log.d(TAG, "===psam卡 8072校验 send===: " + HEX.bytesToHex(PSAM_CHECK_MAC2) + "微智结果：" + retvalue);
            // retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, PSAM_CHECK_MAC2, PSAM_CHECK_MAC2.length, respdata, resplen);
//            if (retvalue != 0) {
//                mBankCard.breakOffCommand();
//                isExpense = 1;
//                return;
//            }
//            Log.d(TAG, "===psam卡 8072校验返回===: " + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "微智结果：" + retvalue);
//            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
//                mBankCard.breakOffCommand();
//                isExpense = 1;
//                return;
//            }
            mBankCard.breakOffCommand();
            isExpense = 0;
            PlaySound.play(PlaySound.xiaofeiSuccse, 0);
            mCore.buzzer();
            Log.d("times", "icExpance:=====  消费完成=======");
        } catch (RemoteException e) {
            e.printStackTrace();
            try {
                isExpense = 1;
                mBankCard.breakOffCommand();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            tvShowMsg.append((String) msg.obj + "\n");
        }
    };

    /**
     * 用户卡(IC卡)交易初始化指令 8050指令
     *
     * @return 8050指令
     */
    private byte[] initICcard() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("805001020B");
        if (CAPP == 1) {
            stringBuilder.replace(5, 6, "3");
        }
        stringBuilder.append(DataConversionUtils.byteArrayToString(psamKey)).append("00000002").append(DataConversionUtils.byteArrayToString(deviceCode)).append("0F");
        return DataConversionUtils.HexString2Bytes(stringBuilder.toString());
    }

    /**
     * PSAM 卡产生MAC1指令 8070
     *
     * @return 返回结果为：XXXXXXXX（终端脱机交易序号）XXXXXXXX（MAC1）
     */
    private byte[] initSamForPurchase() {
        byte[] cmd = new byte[42];
        cmd[0] = (byte) 0x80;
        cmd[1] = 0x70;
        cmd[2] = 0x00;
        cmd[3] = 0x00;
        cmd[4] = 0x24;
        System.arraycopy(rondomCpu, 0, cmd, 5, 4);
        System.arraycopy(ATC, 0, cmd, 9, 2);
        //交易金额
        cmd[11] = 0x00;
        cmd[12] = 0x00;
        cmd[13] = 0x00;
        cmd[14] = 0x02;
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
        String psam_mac2 = "8072000004" + DataConversionUtils.byteArrayToString(cutBytes(data, 4, 4));
        return DataConversionUtils.HexString2Bytes(psam_mac2);
    }

    /**
     * 获取psam MAC1
     *
     * @param data
     */
    private void praseMAC1(byte[] data) {
        if (data.length <= 2) {
            Log.e(TAG, "===获取MAC1失败===" + HEX.bytesToHex(data));
            return;
        }
        System.arraycopy(data, 0, PSAM_ATC, 0, 4);
        System.arraycopy(data, 4, MAC1, 0, 4);
    }

    /**
     * 获取系统时间
     *
     * @return
     */
    private byte[] getDateTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = dateFormat.format(now);
        // 赋值当前日期和时间
        byte[] nowTimes = DataConversionUtils.HexString2Bytes(currentTime);
        return nowTimes;
    }

    /**
     * 截取数组
     *
     * @param bytes  被截取数组
     * @param start  被截取数组开始截取位置
     * @param length 新数组的长度
     * @return 新数组
     */
    public static byte[] cutBytes(byte[] bytes, int start, int length) {
        byte[] res = new byte[length];
        System.arraycopy(bytes, start, res, 0, length);
        return res;
    }

    @Override
    protected void onStop() {
        mPresenter.releseAlipayJni();
//        mPresenter.detachView();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
//        handler.removeCallbacks(runnable);  //停止Time
        unregisterReceiver(receiver);

//        try {
//            mBankCard.breakOffCommand();
//            mBankCard = null;
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
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
        int fJudge = 0;
        if ((CInfoZ.fValid) == 1 && (CInfoF.fValid) == 1 &&
                ((CInfoZ.fProc & 0x01) == 0) && ((CInfoF.fProc & 0x01) == 0)) {

        } else if ((CInfoZ.fValid == 1) && (CInfoF.fValid == 1) &&
                ((CInfoF.fProc & 0x01) == 0)) {
            if (!Modify_InfoArea(24)) {
                return;
            }
        } else if ((CInfoZ.fValid == 1) && (CInfoF.fValid == 1) &&
                ((CInfoZ.fProc & 0x01) == 1) && ((CInfoF.fProc & 0x01) == 0)) {

            CInfo = CInfoF;
            if (!Modify_InfoArea(24)) {
                return;
            }
        } else if ((CInfoZ.fValid == 1) && (CInfoF.fValid == 0) && ((CInfoZ.fProc & 0x01) == 1)) {
            CInfo.cPtr = (byte) (CInfoZ.cPtr == 0 ? 8 : (CInfoZ.cPtr - 1));
            CInfo.fProc = (byte) (CInfo.fProc + 1);
            if (CInfoZ.fProc == 1) {
                int count = DataConversionUtils.byteArrayToInt(CInfoZ.iPurCount) - 1;
                byte[] result = new byte[2];
                result[0] = (byte) ((count >> 8) & 0xFF);
                result[1] = (byte) (count & 0xFF);
                CInfo.iPurCount = result;

            } else {
                int count = DataConversionUtils.byteArrayToInt(CInfoZ.iYueCount) - 1;
                byte[] result = new byte[2];
                result[0] = (byte) ((count >> 8) & 0xFF);
                result[1] = (byte) (count & 0xFF);
                CInfoZ.iYueCount = result;
            }
            if (!Modify_InfoArea(25)) {
                return;
            }
            if (!Modify_InfoArea(24)) {
                return;
            }
        } else if ((CInfoZ.fValid == 1) && (CInfoF.fValid == 1) && ((CInfoZ.fProc & 0x01) == 1) && ((CInfoF.fProc & 0x01) == 1)) {
            fJudge = 1;
        } else if ((CInfoZ.fValid == 0) && (CInfoF.fValid == 1) && ((CInfoF.fProc & 0x01) == 1)) {
            CInfo.fProc = (byte) (CInfo.fProc + 1);
            if (!Modify_InfoArea(24)) {
                return;
            }
            if (!Modify_InfoArea(25)) {
                return;
            }
        } else if ((CInfoZ.fValid == 1) && (CInfoF.fValid == 1) && ((CInfoZ.fProc & 0x01) == 0) && ((CInfoF.fProc & 0x01) == 1)) {
            if (!Modify_InfoArea(25)) {
                return;
            }
        } else if ((CInfoZ.fValid == 1) && (CInfoF.fValid == 0) && ((CInfoZ.fProc & 0x01) == 0)) {
            if (!Modify_InfoArea(25)) {
                return;
            }
        } else {
            return;
        }
/////////////////////////////////////////////////////////////
        try {
            if (fJudge == 1) {
                byte CardRcdDateTime[] = new byte[8];
                byte[] CardRcdOriMoney = new byte[4];
                byte[] CardRcdSub = new byte[4];
                //代表扇区 2为钱包区 7为月票区
                int CardRcdSec = 0;
                blk = RcdBlkIndex[CInfo.cPtr == 0 ? 8 : CInfo.cPtr - 1];
                if (!m1CardKeyAuth(blk, blk / 4)) {
                    return;
                }
                retvalue = mBankCard.m1CardReadBlockData(blk, respdata, resplen);
                if (retvalue != 0) {
                    return;
                }
                byte[] RcdInCard = cutBytes(respdata, 1, resplen[0] - 1);
                //todo utc时间转bcd时间 UTCtoBCDTime(ArrToVar( & RcdInCard[0], 4), CardRcdDateTime);
                byte[] UTCTimes = cutBytes(RcdInCard, 0, 4);
                CardRcdOriMoney = cutBytes(RcdInCard, 4, 4);
                CardRcdSub = cutBytes(RcdInCard, 8, 3);
                if (RcdInCard[11] == 0x02) {
                    CardRcdSec = 7;
                } else {
                    CardRcdSec = 2;
                }
                //比对 9 块10块
                if (!BackupManage(CardRcdSec)) {
                    return;  //_dt and _backup are all wrong
                }
                //原额倒叙 操作
                actRemaining = icCardBeen.getPurOriMoney();
                if (CardRcdSec == 2) {
                    icCardBeen.setPurOriMoney(actRemaining);
                }
                if (Arrays.equals(CardRcdOriMoney, icCardBeen.getPurOriMoney())) {
                    CInfo.cPtr = (byte) (CInfoZ.cPtr == 0 ? 8 : CInfoZ.cPtr - 1);
                    int count = DataConversionUtils.byteArrayToInt(CInfoZ.iPurCount) - 1;
                    byte[] result = new byte[2];
                    result[0] = (byte) ((count >> 8) & 0xFF);
                    result[1] = (byte) (count & 0xFF);
                    CInfo.iPurCount = result;
                    CInfo.fProc = (byte) (CInfo.fProc + 1);
                    if (!Modify_InfoArea(25)) {
                        return;
                    }
                    if (!Modify_InfoArea(24)) {
                        return;
                    }
                } else if (DataConversionUtils.byteArrayToInt(CardRcdOriMoney) - DataConversionUtils.byteArrayToInt(CardRcdSub) == DataConversionUtils.byteArrayToInt(icCardBeen.getPurOriMoney())) {
                    CInfo.fProc = (byte) (CInfo.fProc + 1);
                    if (!Modify_InfoArea(24)) {
                        return;
                    }
                    if (!Modify_InfoArea(25)) {
                        return;
                    }
                } else {
                    CInfo.fProc = (byte) (CInfo.fProc + 1);
                    if (!Modify_InfoArea(25)) {
                        return;
                    }
                    if (!Modify_InfoArea(24)) {
                        return;
                    }
                }
            } else {
                CInfo.fProc = (byte) (CInfo.fProc + 1);
                if (!Modify_InfoArea(25)) {
                    return;
                }
                if (!Modify_InfoArea(24)) {
                    return;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    //    // TODO: 2018/9/10    读交易指针
//    Get_RcdInfo();
//
//    errptr =RcdDU.EFRcdPtr;
//        for(ErrInfo[0].fErr =0,i =0;i< 10;i++)                                           // 最近的20张卡刷卡信息
//
//    {                                                            // 从第0条开始核对最近记录中有无本卡记录
//        Move_ptr_v( & errptr);                                    // 向后移48字节指针(页号+地址)
//        gsFLASH_ReadBuffer(errptr, 128, & RcdDU.RcdBuffer[0])
//        ;                      // 取后移1条记录后的数据
//        if (RcdDU.RcdBuffer[3] < 0xE0 &&
//                (RcdDU.RcdBuffer[4] & 0xF0) == 0x10 &&
//                RcdDU.RcdBuffer[5] == tCardOpDu.ucCardClass &&
//                !memcmp( & RcdDU.RcdBuffer[68],&tReqDu.ucSnr[0], 4))
//        {
//            if ((RcdDU.RcdBuffer[3] & 0x01) == 0x01)//Same card&&Gray
//                ErrInfo[0].fErr = 1;
//            break;
//        }
//    }
//
//        if(ErrInfo[0].fErr ==1)
//
//    {
//        uint16_t DevRcdCnt;
//        int32_t DevRcdOriMoney, DevRcdSub;
//        int i = 0;
//        for (DevRcdCnt = 0, i = 0; i < 2; i++) {
//            DevRcdCnt <<= 8;
//            DevRcdCnt += RcdDU.RcdBuffer[i + 39];
//        }
//        for (DevRcdOriMoney = 0, i = 0; i < 3; i++) {
//            DevRcdOriMoney <<= 8;
//            DevRcdOriMoney += RcdDU.RcdBuffer[i + 36];
//        }
//        for (DevRcdSub = 0, i = 0; i < 2; i++) {
//            DevRcdSub <<= 8;
//            DevRcdSub += RcdDU.RcdBuffer[i + 27];
//        }
//        switch (RcdDU.RcdBuffer[3] & 0xfe) {
//            case 0://Pur
//                if (CInfo.iPurCount == DevRcdCnt + 1)//向下恢复
//                {
//                    tCardOpDu.ucRcdType = (RcdDU.RcdBuffer[3] & 0xfe);
//                    tCardOpDu.ucSec = 2;
//                    tCardOpDu.PurOriMoney = DevRcdOriMoney;
//                    tCardOpDu.PurSub = DevRcdSub;
//                    ErrInfo[0].fErr = 0;
//                    OnAppendRecord(tCardOpDu.ucRcdType);
//                    return CAD_OK;
//                }
//                break;
//            case 2:
//                if (CInfo.iYueCount == DevRcdCnt + 1) {
//                    tCardOpDu.ucRcdType = (RcdDU.RcdBuffer[3] & 0xfe);
//                    tCardOpDu.ucSec = 7;
//                    tCardOpDu.YueOriMoney = DevRcdOriMoney;
//                    tCardOpDu.YueSub = DevRcdSub;
//                    ErrInfo[0].fErr = 0;
//                    OnAppendRecord(tCardOpDu.ucRcdType);
//                    return CAD_OK;
//                }
//                break;
//        }
//    }
/////////End Restore/////////////////////////////////////////
    String decodeDate = null;

    /**
     * 解码返回数据
     *
     * @param hsmDecodeResults
     */
    @Override
    public void onHSMDecodeResult(HSMDecodeResult[] hsmDecodeResults) {
        if (hsmDecodeResults.length > 0) {
            HSMDecodeResult firstResult = hsmDecodeResults[0];
            if (isUTF8(firstResult.getBarcodeDataBytes())) {
                Log.d(TAG, "is a utf8 string");
                try {
                    decodeDate = new String(firstResult.getBarcodeDataBytes(), "utf8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "is a gbk string");
//                    decodeDate = new String(firstResult.getBarcodeDataBytes(), "gbk");
                decodeDate = DataConversionUtils.byteArrayToString(firstResult.getBarcodeDataBytes());
            }
            Log.i(TAG, "二维码: " + decodeDate);
            if ("TX".equals(decodeDate.substring(0, 2))) {
                mPresenter.checkWechatQrCode(decodeDate, pubKeyListBeans, macKeyListBeans, 1, (byte) 1, (byte) 1, "17430597", "12");
            } else {
                mPresenter.checkAliQrCode(decodeDate, record_id,
                        pos_id, pos_mf_id, pos_sw_version,
                        merchant_type, currency, amount,
                        vehicle_id, plate_no, driver_id,
                        line_info, station_no, lbs_info,
                        record_type);
                //银联二维码
//                QrEntity qrEntity = new QrEntity(barCode);
//                try {
//                    boolean validation = ValidationUtils.validation(qrEntity);
//                    Logcat.d(validation);
//                    if (validation) {
//                        Toast.makeText(this, "验证通过", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(this, "验证失败", Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }

    }

    String record_id = "sh001_" + DataConversionUtils.getDefautCurrentTime() + "_000002";
    String pos_id = "20170000000001";
    String pos_mf_id = "9998112123";
    String pos_sw_version = "2.6.14.03arm";
    String merchant_type = "22";
    String currency = "156";
    int amount = 1;
    String vehicle_id = "vid9702";
    String plate_no = "粤A 095852";
    String driver_id = "0236245394";
    String line_info = "0";
    String station_no = "000010";
    String lbs_info = "aaaa";
    String record_type = "BUS";
    private boolean IsUtf8 = false;

    //判断扫描的内容是否是UTF8的中文内容
    private boolean isUTF8(byte[] sx) {
        //Log.d(TAG, "begian to set codeset");
        for (int i = 0; i < sx.length; ) {
            if (sx[i] < 0) {
                if ((sx[i] >>> 5) == 0x7FFFFFE) {
                    if (((i + 1) < sx.length) && ((sx[i + 1] >>> 6) == 0x3FFFFFE)) {
                        i = i + 2;
                        IsUtf8 = true;
                    } else {
                        if (IsUtf8) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else if ((sx[i] >>> 4) == 0xFFFFFFE) {
                    if (((i + 2) < sx.length) && ((sx[i + 1] >>> 6) == 0x3FFFFFE) && ((sx[i + 2] >>> 6) == 0x3FFFFFE)) {
                        i = i + 3;
                        IsUtf8 = true;
                    } else {
                        if (IsUtf8) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else {
                    if (IsUtf8) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                i++;
            }
        }
        return true;
    }

    @Override
    public void success(String msg) {
        Log.i(TAG, "success::: " + msg);
    }

    @Override
    public void erro(String msg) {
        Log.i(TAG, "erro:::" + msg);
    }

    private List<AlipayPublicKey.PublicKeyListBean> publicKeyListBeans;

    @Override
    public void getAliPublicKey(AlipayPublicKey alipayPublickKey) {
        publicKeyListBeans = alipayPublickKey.getPublicKeyList();
        Collections.sort(publicKeyListBeans);
        if (pubKeyListBeans != null) {
            mPresenter.aliPayInit(publicKeyListBeans);
        } else {
            Log.i(TAG, "获取支付宝key失败 ");
        }
        Log.i(TAG, "支付宝key：：： " + publicKeyListBeans.toString());
    }

    @Override
    public void aliPayInit(int result) {
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

    private AliCodeinfoData codeinfoData;

    @Override
    public void checkAliQrCode(AliCodeinfoData aliCodeinfoData) {
        codeinfoData = aliCodeinfoData;
        if (codeinfoData.inforState == ErroCode.SUCCESS) {
            Log.i(TAG, "\n支付宝校验结果:：" + codeinfoData.inforState +
                    "\n卡类型:：" + Datautils.byteArrayToAscii(codeinfoData.cardType) +
                    "\n卡号：:" + Datautils.byteArrayToAscii(codeinfoData.cardNo) +
                    "\nuserId:：" + Datautils.byteArrayToAscii(codeinfoData.userId) +
                    "\n支付宝sdk返回:：" + Datautils.byteArrayToAscii(codeinfoData.alipayResult));
            AlipayUploadBeen alipayUploadBeen = new AlipayUploadBeen();
            alipayUploadBeen.setRecordType("ALIQR");
            List<AlipayUploadBeen.RecordBean> recordBeans = new ArrayList<>();
            AlipayUploadBeen.RecordBean recordBean = new AlipayUploadBeen.RecordBean("6410001", "09", record_id, pos_mf_id, driver_id, DataConversionUtils.getDefautCurrentTime(), "6410", line_info, station_no, currency, 0, 1, amount, Datautils.byteArrayToAscii(codeinfoData.userId), record_type, Datautils.byteArrayToAscii(codeinfoData.cardType), Datautils.byteArrayToAscii(codeinfoData.cardNo), decodeDate, Datautils.byteArrayToAscii(codeinfoData.alipayResult));
            recordBeans.add(recordBean);
            alipayUploadBeen.setRecord(recordBeans);
            mPresenter.uploadAlipay(alipayUploadBeen);
        } else {
            Log.i(TAG, "\n支付宝校验结果错误:：" + codeinfoData.inforState);
        }
    }

    @Override
    public void releseAlipayJni(int result) {
        if (result == 1) {
            Log.i(TAG, "releseAlipayJni: 支付宝库关闭成功");
        } else {
            Log.e(TAG, "releseAlipayJni: 支付宝库关闭失败");

        }
    }

    private List<WechatPublicKey.MacKeyListBean> macKeyListBeans;
    private List<WechatPublicKey.PubKeyListBean> pubKeyListBeans;

    @Override
    public void getWechatPublicKey(WechatPublicKey wechatPublicKey) {
        if (wechatPublicKey != null) {
            macKeyListBeans = wechatPublicKey.getMacKeyList();
            pubKeyListBeans = wechatPublicKey.getPubKeyList();
            Log.i(TAG, "微信PblicKey: " + pubKeyListBeans.toString());
            Log.i(TAG, "微信macklicKey: " + macKeyListBeans.toString());
            if (macKeyListBeans != null && pubKeyListBeans != null) {
                mPresenter.wechatInit();
            } else {
                Log.i(TAG, "获取微信Key失败 " + macKeyListBeans.toString());
            }
        }
    }

    @Override
    public void checkWechatQrCode(int result, String wechatResult, String openId) {
        if (result == ErroCode.EC_SUCCESS) {
            Log.i(TAG, "微信结果: " + "openID" + openId + "结果" + wechatResult);
            AlipayUploadBeen alipayUploadBeen = new AlipayUploadBeen();
            alipayUploadBeen.setRecordType("TXQR");
            List<AlipayUploadBeen.RecordBean> recordBeans = new ArrayList<>();
            AlipayUploadBeen.RecordBean recordBean = new AlipayUploadBeen.RecordBean("6410001", "09", record_id, pos_mf_id, driver_id, DataConversionUtils.getDefautCurrentTime(), "6410", line_info, station_no, currency, 0, 1, 1, "0123456", record_type, "20181114032300", openId, decodeDate, wechatResult);
            recordBeans.add(recordBean);
            alipayUploadBeen.setRecord(recordBeans);
            mPresenter.uploadAlipay(alipayUploadBeen);

        } else {
            Log.i(TAG, "微信校验结果错误 " + result);

        }
    }
}
