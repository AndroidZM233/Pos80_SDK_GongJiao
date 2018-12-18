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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.yinlianbarcode.entity.QrEntity;
import com.example.test.yinlianbarcode.utils.Logcat;
import com.example.test.yinlianbarcode.utils.ValidationUtils;
import com.honeywell.barcode.HSMDecodeResult;
import com.honeywell.plugins.decode.DecodeResultListener;
import com.spd.alipay.been.AliCodeinfoData;
import com.spd.base.Datautils;
import com.spd.base.been.AlipayDatabaseBeen;
import com.spd.base.beenali.AlipayQrcodekey;
import com.spd.base.beenbosi.BosiQrcodeKey;
import com.spd.base.beenupload.QrcodeUpload;
import com.spd.base.beenwechat.WechatQrcodeKey;
import com.spd.base.database.BoxStorManage;
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

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;
import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libbasebinder.HEX;

import static com.spd.bus.spdata.been.ErroCode.ILLEGAL_PARAM;
import static com.spd.bus.spdata.been.ErroCode.NO_ENOUGH_MEMORY;
import static com.spd.bus.spdata.been.ErroCode.SYSTEM_ERROR;

public class PsamIcActivity extends com.spd.bus.spdata.mvp.MVPBaseActivity<SpdBusPayContract.View, SpdBusPayPresenter> implements SpdBusPayContract.View, DecodeResultListener {
    private BankCard mBankCard;
    private Core mCore;
    /**
     * 普通交易（CAPP=0）或复合交易（CAPP=1）
     */
    private int CAPP = 0;
    private static final String TAG = "SPEEDATA_BUS";
    /**
     * 终端编号
     */
    private byte[] deviceCode;
    /**
     * 秘钥索引
     */
    private byte[] psamKey;

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
     * 返回错误结果
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
     * //获取PSAM卡终端机编号指令
     */
    private final byte[] psam1_get_id = {0x00, (byte) 0xB0, (byte) 0x96, 0x00, 0x06};
    /**
     * //交通部
     */
    private final byte[] psam2_select_dir = {0x00, (byte) 0xA4, 0x00, 0x00, 0x02, (byte) 0x80, 0x11};

    /**
     * //住建部
     */
    private final byte[] psamzhujian_select_dir = {0x00, (byte) 0xA4, 0x00, 0x00, 0x02, (byte) 0x10, 0x01};

    /**
     * //读取psam卡17文件
     */
    private final byte[] psam3_get_index = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x01};

    /**
     * //选择PPSE支付环境
     */
    private final byte[] fuhe_tlv = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0e, 0x32, 0x50, 0x41, 0x59, 0x2e, 0x53, 0x59, 0x53, 0x2e,
            0x44, 0x44, 0x46, 0x30, 0x31};

    /**
     * //选择电子钱包应用
     */
    private final byte[] ic_file = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, (byte) 0xA0, 0x00, 0x00, 0x06, 0x32, 0x01, 0x01, 0x05};

    /**
     * //读ic卡应用下公共应用基本信息文件指令 15文件
     */
    private final byte[] ic_read_file = {0x00, (byte) 0xB0, (byte) 0x95, 0x00, 0x00};
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
        setContentView(R.layout.bus_layout);
        initView();
        initCard();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        mTvTitle = findViewById(R.id.tv_title);
        mTvTitle.setText(getResources().getString(R.string.main_title) + "    " + DataUtils.getNowTime());
        mTvCircuit = findViewById(R.id.tv_circuit);
        mTvCircuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        icExpance();//执行等待读卡消费
                    }
                }).start();
            }
        });
        mTvPrice = findViewById(R.id.tv_price);
        mTvBalance = findViewById(R.id.tv_balance);
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
        //解码库条码返回监听
        MyApplication.getHSMDecoder().addResultListener(this);
        //注册系统时间广播 只能动态注册
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBankCard = new BankCard(getApplicationContext());
                mCore = new Core(getApplicationContext());
                psam1Init();
                psam2Init();
                handler.postDelayed(runnable, 0);
            }
        }).start();
        //获取支付宝微信key
        mPresenter.getAliPubKey();
        mPresenter.getWechatPublicKey();
        mPresenter.getBosikey();
    }


    /**
     * 更新时间
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                mTvTitle.setText(getResources().getString(R.string.main_title) + "    " + DataUtils.getNowTime());
            }
        }
    };

    private boolean checkResuleAPDU(byte[] reByte, int le) {
        return Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(reByte, le - 2, 2));
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
            Log.d(TAG, "===交通部切换psam===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
            if (respdata[0] == (byte) 0x01) {
                Log.e(TAG, "交通部psam初始化 读卡失败,请检查是否插入psam卡 " + HEX.bytesToHex(respdata));
                isFlag = 1;
                return;

            } else if (respdata[0] == (byte) 0x05) {
                //IC卡已经插入
                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, psam1_get_id, psam1_get_id.length, respdata, resplen);
                if (retvalue != 0) {
                    isFlag = 1;
                    return;
                }
                Log.d(TAG, "===交通部16文件===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
                if (!checkResuleAPDU(respdata, resplen[0])) {
                    Log.e(TAG, "交通部获取终端编号错误:" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)));
                    isFlag = 1;
                    return;
                }
                //终端机编号
                deviceCode = cutBytes(respdata, 0, resplen[0] - 2);
                Log.d(TAG, "====交通部PSAM卡终端机编号==== " + HEX.bytesToHex(deviceCode) + "   " + retvalue);

                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, psam2_select_dir, psam2_select_dir.length, respdata, resplen);
                if (retvalue != 0) {
                    isFlag = 1;
                    return;
                }
                Log.d(TAG, "===交通部80 11 ====" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "   " + retvalue);
                if (!checkResuleAPDU(respdata, resplen[0])) {
                    Log.e(TAG, "交通部切换8011错误:" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)));
                    isFlag = 1;
                    return;
                }

                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, psam3_get_index, psam3_get_index.length, respdata, resplen);

                if (retvalue != 0) {
                    isFlag = 1;
                    return;
                }
                Log.d(TAG, "===交通部读17文件获取秘钥索引===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
                if (!checkResuleAPDU(respdata, resplen[0])) {
                    Log.e(TAG, "交通部获取秘钥索引错误:" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)));
                    isFlag = 1;
                    return;
                }
                psamKey = cutBytes(respdata, 0, 1);
                Log.d(TAG, "===交通部秘钥索引=== " + HEX.bytesToHex(psamKey) + "\n" + "PSAM初始化成功！！！请读消费卡\n");
                psamDatas.add(new PsamBeen(1, deviceCode, psamKey));
                // TODO: 2018/12/4  初始化成功等待读消费卡
            } else {
                Log.e(TAG, "交通部psam初始化失败 " + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
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
            Log.d(TAG, "===住建部切换psam===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
            if (respdata[0] == (byte) 0x01) {
                //读卡失败
                Log.e(TAG, "住建部psam初始化 读卡失败,请检查是否插入psam卡 " + HEX.bytesToHex(respdata));
                isFlag = 1;
                return;

            } else if (respdata[0] == (byte) 0x05) {
                //IC卡已经插入
                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, psam1_get_id, psam1_get_id.length, respdata, resplen);
                if (retvalue != 0) {
                    isFlag = 1;
                    return;
                }
                Log.d(TAG, "住建部16文件return" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
                if (!checkResuleAPDU(respdata, resplen[0])) {
                    Log.e(TAG, "住建部获取终端编号错误:" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)));
                    isFlag = 1;
                    return;
                }
                //终端机编号
                deviceCode = cutBytes(respdata, 0, resplen[0] - 2);
                Log.d(TAG, "====住建部PSAM卡终端机编号==== " + HEX.bytesToHex(deviceCode));
                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, psamzhujian_select_dir, psamzhujian_select_dir.length, respdata, resplen);
                if (retvalue != 0) {
                    isFlag = 1;
                    return;
                }
                Log.d(TAG, "===住建部选文件 10 01 ====" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "   " + retvalue);
                if (!checkResuleAPDU(respdata, resplen[0])) {
                    Log.e(TAG, "住建部切换1001错误:" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)));
                    isFlag = 1;
                    return;
                }
                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, psam3_get_index, psam3_get_index.length, respdata, resplen);
                if (retvalue != 0) {
                    isFlag = 1;
                    return;
                }
                Log.d(TAG, "===住建部17文件获取秘钥索引===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
                if (!checkResuleAPDU(respdata, resplen[0])) {
                    Log.e(TAG, "住建部获取秘钥索引错误:" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)));
                }
                psamKey = cutBytes(respdata, 0, 1);
                Log.d(TAG, "===住建部秘钥索引===" + HEX.bytesToHex(psamKey) + "\n" + "PSAM初始化成功！！！请读消费卡\n");
                psamDatas.add(new PsamBeen(2, deviceCode, psamKey));
                // TODO: 2018/12/4  psam卡等待读消费卡
            } else {
                Log.e(TAG, "住建部psam初始化 读卡失败,请检查是否插入psam卡 " + HEX.bytesToHex(respdata));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            isFlag = 1;
        }
    }

    private long ltime = 0;


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
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
                    Log.i("stw", "ic结束寻卡===" + (System.currentTimeMillis() - ltime));
                    if (retvalue != 0) {
                        isFlag = 0;
                    }
                    //检测到非接IC卡
                    if (respdata[0] == 0x07) {
                        icExpance();//执行等待读卡消费
                        if (isFlag == 1) {
                            PlaySound.play(PlaySound.qingchongshua, 0);
                        }
                    } else if (respdata[0] == 0x37) {
                        //检测到 M1-S50 卡
                        Log.i("stw", "m1结束寻卡===" + (System.currentTimeMillis() - ltime));
                        m1ICCard();
                        if (isFlag == 1) {
                            PlaySound.play(PlaySound.qingchongshua, 0);
                        }
                    } else if (respdata[0] == 0x47) {
                        // 检测到 M1-S70 卡
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
        ltime = System.currentTimeMillis();
        CInfoF = new TCommInfo();
        CInfoZ = new TCommInfo();
        CInfo = new TCommInfo();
        try {
            Log.d(TAG, "===m1卡消费开始===");
            //读取非接卡 SN(UID)信息
            retvalue = mBankCard.getCardSNFunction(respdata, resplen);
            if (retvalue != 0) {
                isFlag = 1;
                Log.e(TAG, "===获取UID失败===");
                return;
            }
            snUid = cutBytes(respdata, 0, resplen[0]);
            icCardBeen.setSnr(snUid);
            Log.d(TAG, "===getUID===" + HEX.bytesToHex(snUid));
            byte[] key = new byte[6];
            System.arraycopy(snUid, 0, key, 0, 4);
            System.arraycopy(snUid, 0, key, 4, 2);
            //认证1扇区第4块
            retvalue = mBankCard.m1CardKeyAuth(0x41, 0x04, key.length, key, snUid.length, snUid);
            if (retvalue != 0) {
                isFlag = 1;
                Log.e(TAG, "===认证1扇区第4块失败===");
                return;
            }
            retvalue = mBankCard.m1CardReadBlockData(0x04, respdata, resplen);
            if (retvalue != 0) {
                isFlag = 1;
                Log.e(TAG, "=== 读取1扇区第4块失败==");
                return;
            }
            byte[] bytes04 = cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "===读取1扇区第4块返回===" + HEX.bytesToHex(bytes04));
            icCardBeen.setIssueSnr(cutBytes(bytes04, 0, 8));
            icCardBeen.setCityNr(cutBytes(bytes04, 0, 2));
            icCardBeen.setVocCode(cutBytes(bytes04, 2, 2));
            icCardBeen.setIssueCode(cutBytes(bytes04, 4, 4));
            icCardBeen.setMackNr(cutBytes(bytes04, 8, 4));
            icCardBeen.setfStartUse(cutBytes(bytes04, 12, 1));
            //卡类型判断表格中没有return
            icCardBeen.setCardType(cutBytes(bytes04, 13, 1));
            //黑名单
            icCardBeen.setfBlackCard(0);
            //判断启用标志
            switch (icCardBeen.getfStartUse()[0]) {
                //未启用
                case (byte) 0x01:
                    Log.e(TAG, "m1ICCard: 启用标志未启用");
                    isFlag = 1;
                    return;
                //正常
                case (byte) 0x02:
                    // TODO: 2018/8/29
                    break;
                //停用
                case (byte) 0x03:
                    Log.e(TAG, "m1ICCard: 启用标志停用");
                    isFlag = 1;
                    return;
                //黑名单
                case (byte) 0x04:
                    Log.e(TAG, "m1ICCard: 启用标志黑名单");
                    isFlag = 1;
                    icCardBeen.setfBlackCard(1);
                    return;
                default:
                    break;
            }
            //读1扇区05块数据
            retvalue = mBankCard.m1CardReadBlockData(0x05, respdata, resplen);
            if (retvalue != 0) {
                isFlag = 1;
                Log.e(TAG, "===读1扇区05块数据失败====");
                return;
            }
            byte[] bytes05 = cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "===读1扇区05块数据===" + HEX.bytesToHex(bytes05));
            icCardBeen.setIssueDate(cutBytes(bytes05, 0, 4));
            icCardBeen.setEndUserDate(cutBytes(bytes05, 4, 4));
            icCardBeen.setStartUserDate(cutBytes(bytes05, 8, 4));

            //读1扇区06块数据
            retvalue = mBankCard.m1CardReadBlockData(0x06, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "===读1扇区06块数据失败==");
                isFlag = 1;
                return;
            }
            byte[] bytes06 = cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "===读1扇区06块数据返回===" + HEX.bytesToHex(bytes06));
            //转UTC时间
            icCardBeen.setPurIncUtc(cutBytes(bytes06, 0, 6));
            icCardBeen.setPurIncMoney(cutBytes(bytes06, 9, 2));
            //第0扇区 01块认证
            retvalue = mBankCard.m1CardKeyAuth(0x41, 0x01,
                    6, new byte[]{(byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3, (byte) 0xA4, (byte) 0xA5}, snUid.length, snUid);
            if (retvalue != 0) {
                Log.e(TAG, "===第0扇区01块认证失败==");
                isFlag = 1;
                return;
            }
            //读第0扇区第一块秘钥
            retvalue = mBankCard.m1CardReadBlockData(0x01, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "m1ICCard: 读第0扇区01块失败");
                isFlag = 1;
                return;
            }

            byte[] bytes01 = cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "m1ICCard: 读第0扇区01块：" + HEX.bytesToHex(bytes01));
            //扇区标识符
            secF = bytes01;
            //算秘钥指令
            String sendCmd = "80FC010110" + HEX.bytesToHex(icCardBeen.getCityNr()) + DataConversionUtils.byteArrayToString(icCardBeen.getSnr()) + HEX.bytesToHex(cutBytes(icCardBeen.getIssueSnr(), 6, 2)) + HEX.bytesToHex(icCardBeen.getMackNr())
                    + HEX.bytesToHex(cutBytes(secF, 2, 2)) + HEX.bytesToHex(cutBytes(secF, 6, 2));
            Log.d(TAG, "===psam计算秘钥指令===" + sendCmd);
            //psam卡计算秘钥
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, DataConversionUtils.HexString2Bytes(sendCmd), DataConversionUtils.HexString2Bytes(sendCmd).length, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "===psam计算秘钥指令错误===");
                isFlag = 1;
                return;
            }
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                Log.e(TAG, "=== psam计算秘钥指令错误非9000===");
                isFlag = 1;
                return;
            }
            byte[] result = cutBytes(respdata, 0, resplen[0] - 2);
            Log.d(TAG, "m1ICCard: psam计算秘钥返回：" + HEX.bytesToHex(result));
            //3/4/5扇区秘钥相同
            // 第2扇区秘钥
            lodkey[2] = cutBytes(result, 0, 6);
            //第3扇区秘钥
            lodkey[3] = cutBytes(result, 6, 6);
            //第4扇区秘钥
            lodkey[4] = cutBytes(result, 6, 6);
            //第5扇区秘钥
            lodkey[5] = cutBytes(result, 6, 6);
            //第6扇区秘钥
            lodkey[6] = cutBytes(result, 12, 6);
            //第7扇区秘钥
            lodkey[7] = cutBytes(result, 18, 6);
            //第6扇区24 块认证
            byte[] lodKey6 = lodkey[6];
            retvalue = mBankCard.m1CardKeyAuth(0x41, 24, lodKey6.length, lodKey6, snUid.length, snUid);
            if (retvalue != 0) {
                Log.e(TAG, "===第6扇区24 块认证错误===");
                isFlag = 1;
                return;
            }
            //读6扇区第24块
            retvalue = mBankCard.m1CardReadBlockData(24, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "===读6扇区第24块失败===");
                isFlag = 1;
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
//                isFlag = 1;
//                return;
//            }
//            bytes24 = cutBytes(respdata, 1, resplen[0] - 1);

            Log.d(TAG, "===读6扇区第24块返回===" + HEX.bytesToHex(bytes24));
            byte[] dtZ = bytes24;
            byte chk = 0;
            //异或操作
            for (int i = 0; i < 16; i++) {
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
            //交易记录指针
            CInfoZ.cPtr = dtZ[0];
            //钱包计数,2,3
            CInfoZ.iPurCount = cutBytes(dtZ, 1, 2);
            //进程标志
            CInfoZ.fProc = dtZ[3];
            CInfoZ.iYueCount = cutBytes(dtZ, 4, 2);
            CInfoZ.fBlack = dtZ[6];
            CInfoZ.fFileNr = dtZ[7];
            //副本  有效性
            //读6扇区第25块
            retvalue = mBankCard.m1CardReadBlockData(25, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "===读6扇区第25块失败===");
                isFlag = 1;
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
                Log.e(TAG, "===24 25块有效标志错误 返回0===");
                isFlag = 1;
                return;
            }

            if ((CInfoZ.fValid == 1 && (CInfoZ.fBlack == 4)) || (CInfoF.fValid == 1 && (CInfoF.fBlack == 4))) {
                //黑名单 报语音
                icCardBeen.setfBlackCard(1);
                Log.e(TAG, "m1ICCard: 黑名单");
                isFlag = 1;
                return;
            }
            //比对 9块 10块数据
            if (!BackupManage(8)) {
                isFlag = 1;
                return;
            }
            if (!writeCardRcd()) {
                isFlag = 1;
                return;
            }
            Log.i("stw", "===M1卡消费结束===" + (System.currentTimeMillis() - ltime));
            handler.sendMessage(handler.obtainMessage(2, DataConversionUtils.byteArrayToInt(blance)));
            isFlag = 0;
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
            byte[] bytes09 = cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "===读2扇区第9块返回===" + HEX.bytesToHex(bytes09));

            //读2扇区第10块
            retvalue = mBankCard.m1CardReadBlockData(10, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "===读2扇区第10块失败===");
                isFlag = 1;
                return false;
            }
            byte[] bytes10 = cutBytes(respdata, 1, resplen[0] - 1);
            Log.d(TAG, "===读2扇区第10块返回===" + HEX.bytesToHex(bytes10));

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
            byte[] yue09 = cutBytes(bytes09, 0, 4);
            icCardBeen.setPurOriMoney(yue09);
            //定义消费金额
            icCardBeen.setPurSub(new byte[]{0x00, 0x00, 0x00, (byte) 0xEC});
            Log.d(TAG, "===原额===" + HEX.bytesToHex(yue09));
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
        //step 0//文件标识
        CInfo.fFileNr = secF[2];
        if (CInfo.cPtr > 8) {
            CInfo.cPtr = 0;
        }
        //当前交易记录块
        int blk = RcdBlkIndex[CInfo.cPtr];
        Log.d(TAG, "writeCardRcd: 当前交易记录块：" + blk);

        CInfo.cPtr = (byte) (CInfo.cPtr == 8 ? 0 : CInfo.cPtr + 1);
        //获取UTC时间
        byte[] ulDevUTC = DataConversionUtils.HexString2Bytes(DataUtils.getUTCtimes());
        // 写卡指令
        byte[] RcdToCard = new byte[16];

        System.arraycopy(ulDevUTC, 0, RcdToCard, 0, 4);
        //获取消费前原额
        System.arraycopy(icCardBeen.getPurOriMoney(), 0, RcdToCard, 4, 4);
        //获取本次消费金额
        System.arraycopy(icCardBeen.getPurSub(), 1, RcdToCard, 8, 3);
        RcdToCard[11] = 1;
        //设备号写死
        RcdToCard[12] = 0x64;
        RcdToCard[13] = 0x10;
        RcdToCard[14] = 0x00;
        RcdToCard[15] = 0x01;
        //进程标志
        CInfo.fProc = 1;
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
                //step 2//blk/4 区    blk块
                if (!m1CardKeyAuth(blk, blk / 4)) {
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
                //判断是否 读回==00
                if (Arrays.equals(RcdInCard, bytes)) {
                    Log.e(TAG, "writeCardRcd: 读数据不等于消费返回0错误");
                    return false;
                }

                //step 3
//            PrepareRecord(tCardOpDu.ucSec == 2 ? 1 : 3);   1代表 钱包灰记录 3 月票灰记录
                fErr = 1;
                if (!Modify_InfoArea(25)) {
                    Log.e(TAG, "writeCardRcd: 改写25块错误");
                    // 改写25块，不成功退出
                    return false;
                }
                //step 4//认证2扇区8块
                if (!m1CardKeyAuth(8, 2)) {
                    return false;
                }
                //执行消费 将消费金额带入
                int purSub = DataConversionUtils.byteArrayToInt(icCardBeen.getPurSub());
                retvalue = mBankCard.m1CardValueOperation(0x2D, 9, purSub, 9);

                if (retvalue != 0) {
                    Log.e(TAG, "writeCardRcd: 执行消费错误");
                    return false;
                }
                //执行 读出 现在原额
                retvalue = mBankCard.m1CardReadBlockData(9, respdata, resplen);
                if (retvalue != 0) {
                    Log.e(TAG, "writeCardRcd: 读原额错误");
                    return false;
                }
                //本次消费后的原额;
                byte[] dtZ = cutBytes(respdata, 1, resplen[0] - 1);
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
                //本次消费后的原额
                byte[] dtF = cutBytes(respdata, 1, resplen[0] - 1);
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

    private void icExpance() {
        try {
            Log.d(TAG, "===读卡start=== ");
//            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC, 1, respdata, resplen, "app1");
            Log.d(TAG, "===消费记录3031send=== " + DataConversionUtils.byteArrayToString(fuhe_tlv));
            ltime = System.currentTimeMillis();
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, fuhe_tlv, fuhe_tlv.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                isFlag = 1;
                Log.e(TAG, "消费记录 tlv 3031 error");
                return;
            }
            byte[] testTlv = cutBytes(respdata, 0, resplen[0] - 2);
            Log.d(TAG, resplen[0] + "===消费记录3031return===" + HEX.bytesToHex(testTlv));
            //黑名单
            if (Arrays.equals(cutBytes(respdata, resplen[0] - 2, 2), APDU_RESULT_FAILE)) {

            } else if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                isFlag = 1;
            }
            boolean isFlag = true;
//            List<String> listTlv = new ArrayList<>();
//            TLV.anaTagSpeedata(testTlv, listTlv);
//            for (int i = 0; i < listTlv.size(); i++) {
//                Log.d(TAG, "test: 解析TLV" + i + "&&&&&&&" + listTlv.get(i).toString());
//                //判断解析出来的tlv 61目录里是否 是否存在A000000632010105
//                if (listTlv.get(i).equals("A000000632010105")) {
//                    // TODO: 2018/8/17  APDU
//                    isFlag = false;
//                    //获取交易时间
//                    systemTime = getDateTime();
//                    String select_ic = "00A4040008" + listTlv.get(i);
//                    Log.d(TAG, "test: 解析到TLV发送0105 send：" + select_ic);
//                    byte[] ELECT_DIANZIQIANBAO = DataConversionUtils.HexString2Bytes(select_ic);
//                    //选择电子钱包应用
//                    retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, ELECT_DIANZIQIANBAO, ELECT_DIANZIQIANBAO.length, respdata, resplen);
//                    break;
//                }
//            }
            if (isFlag) {
                //获取交易时间
                systemTime = getDateTime();
                Log.d(TAG, "===默认发送0105send===" + HEX.bytesToHex(ic_file));
                //选择电子钱包应用
                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, ic_file, ic_file.length, respdata, resplen);
            }
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                Log.e(TAG, "icExpance: 获取交易时间错误");
                this.isFlag = 1;
                return;
            }
            Log.d(TAG, "===0105return===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }
            Log.d(TAG, "===读15文件sebd===" + HEX.bytesToHex(ic_read_file));
            //读应用下公共应用基本信息文件指令
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, ic_read_file, ic_read_file.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }
            Log.d(TAG, "===IC读15文件 === retur:" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }
            //02313750FFFFFFFF0201 031049906000000000062017010820991231000090000000000000000000
            System.arraycopy(respdata, 12, cardId, 0, 8);
            System.arraycopy(respdata, 0, file15_8, 0, 8);
            System.arraycopy(respdata, 2, city, 0, 2);
            Log.d(TAG, "===卡应用序列号 ===" + HEX.bytesToHex(cardId));

            //读17文件
            byte[] IC_READ17_FILE = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x00};
            Log.d(TAG, "===读17文件00b0send===" + HEX.bytesToHex(IC_READ17_FILE));
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, IC_READ17_FILE, IC_READ17_FILE.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }
            Log.d(TAG, resplen[0] + "===IC读17文件return===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "维智" + retvalue);
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }

            Log.d(TAG, "===IC读1E文件 00b2send===00B201F400");
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, DataConversionUtils.HexString2Bytes("00B201F400"), DataConversionUtils.HexString2Bytes("00B201F400").length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }
            Log.d(TAG, "===IC读1E文件00b2return===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "维智" + retvalue + "\n");
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }

            Log.d(TAG, "===IC余额)805c)send===  805C030204");
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, DataConversionUtils.HexString2Bytes("805C030204"), DataConversionUtils.HexString2Bytes("805C030204").length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }
            Log.d(TAG, "===IC余额(805c)return===" + HEX.bytesToHex(respdata) + "维智" + retvalue);
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }

            byte[] INIT_IC_FILE = initICcard();
            Log.d(TAG, "===IC卡初始化(8050)send===" + HEX.bytesToHex(INIT_IC_FILE));
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, INIT_IC_FILE, INIT_IC_FILE.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }
            Log.d(TAG, "===IC卡初始化(8050)return=== " + HEX.bytesToHex(respdata));
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }
            System.arraycopy(respdata, 0, blance, 0, 4);
            System.arraycopy(respdata, 4, ATC, 0, 2);
            System.arraycopy(respdata, 6, keyVersion, 0, 4);
            flag = respdata[10];
            System.arraycopy(respdata, 11, rondomCpu, 0, 4);
//            Log.d(TAG, "===余额:  " + HEX.bytesToHex(blance));
//            Log.d(TAG, "===CPU卡脱机交易序号:  " + HEX.bytesToHex(ATC));
//            Log.d(TAG, "===密钥版本 : " + (int) flag);
//            Log.d(TAG, "===随机数 : " + HEX.bytesToHex(rondomCpu));

            byte[] psam_mac1 = initSamForPurchase();
            Log.d(TAG, "===获取MAC1(8070)send===" + HEX.bytesToHex(psam_mac1));
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, psam_mac1, psam_mac1.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }
            Log.d(TAG, "===获取MAC18070return===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "   " + retvalue);
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }
            praseMAC1(respdata);


            //80dc
//            String ss = "80DC00F030060000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
//            Log.d(TAG, "===更新1E文件 80dc  send===" + ss);
//            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, DataConversionUtils.HexString2Bytes(ss), DataConversionUtils.HexString2Bytes(ss).length, respdata, resplen);
//            if (retvalue != 0) {
//                mBankCard.breakOffCommand();
//                isFlag = 1;
//                return;
//            }
//            Log.d(TAG, "===更新1E文件 return===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "   " + retvalue);
//            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
//                mBankCard.breakOffCommand();
//                isFlag = 1;
//                return;
//            }

            byte[] cmd = getIcPurchase();
            Log.d(TAG, "===IC卡(8054)消费发送===" + HEX.bytesToHex(cmd));
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, cmd, cmd.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }
            Log.d(TAG, "===IC卡(8054)消费返回===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + " " + retvalue);
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                this.isFlag = 1;
                return;
            }
            byte[] mac2 = cutBytes(respdata, 0, 8);
            byte[] PSAM_CHECK_MAC2 = checkPsamMac2(mac2);

             retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, PSAM_CHECK_MAC2, PSAM_CHECK_MAC2.length, respdata, resplen);
            Log.d(TAG, "===psam卡 8072校验 send===: " + HEX.bytesToHex(PSAM_CHECK_MAC2) + "微智结果：" + retvalue);
            if (retvalue != 0) {
                this.isFlag = 1;
                mBankCard.breakOffCommand();
                return;
            }
            Log.d(TAG, "===psam卡 8072校验返回===: " + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "微智结果：" + retvalue);
            if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                this.isFlag = 1;
                mBankCard.breakOffCommand();
                return;
            }
            this.isFlag = 0;
            Log.i("stw", "===消费结束===" + (System.currentTimeMillis() - ltime));
            handler.sendMessage(handler.obtainMessage(1, DataConversionUtils.byteArrayToInt(blance)));
            mBankCard.breakOffCommand();
            Log.d("times", "icExpance:=====  消费完成=======");
        } catch (RemoteException e) {
            e.printStackTrace();
            try {
                isFlag = 1;
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
            switch (msg.what) {
                case 1:
                    PlaySound.play(PlaySound.dang, 0);
                    int blances = (int) msg.obj;
                    mTvPrice.setTextSize(40);
                    mTvPrice.setText("票价：0.01元");
                    mTvBalance.setVisibility(View.VISIBLE);
                    Log.i("yuee", "handleMessage:余额：： " + (double) blances / 100 + "元");
                    mTvBalance.setText("余额：" + (double) blances / 100 + "元");
//                    handler.postDelayed(runnable, 500);
                    break;
                case 2:
                    mTvPrice.setTextSize(40);
                    mTvPrice.setText("票价：0.01元");
                    PlaySound.play(PlaySound.dang, 0);
                    int blance = DataConversionUtils.byteArrayToInt(icCardBeen.getPurOriMoney(), false) - DataConversionUtils.byteArrayToInt(icCardBeen.getPurSub());
                    mTvBalance.setVisibility(View.VISIBLE);
                    mTvBalance.setText("余额：" + (double) blance / 100 + "元");
//                    handler.postDelayed(runnable, 3000);
                    break;

                case 5:
                    mTvPrice.setTextSize(35);
                    mTvPrice.setText("PSAM1初始化失败！");
                    break;
                case 6:
                    mTvPrice.setTextSize(35);
                    mTvPrice.append("\nPSAM2初始化失败！");
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
    private byte[] initICcard() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("805001020B");
        if (CAPP == 1) {
            stringBuilder.replace(5, 6, "3");
        }
        stringBuilder.append(DataConversionUtils.byteArrayToString(psamKey)).append("00000002").append(DataConversionUtils.byteArrayToString(psamDatas.get(0).getTermBumber())).append("0F");
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
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.releseAlipayJni();
        MyApplication.getHSMDecoder().removeResultListener(this);
        //停止巡卡
        handler.removeCallbacks(runnable);
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

    private String decodeDate = null;

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
//            if (decodeDate.equals())
            Log.i(TAG, "二维码: " + decodeDate);
            switch (decodeDate.substring(0, 2)) {
                case "TX":
                    //腾讯（微信）
                    mPresenter.checkWechatQrCode(decodeDate, pubKeyListBeans, macKeyListBeans, 1, (byte) 1, (byte) 1, "17430597", "12");
                    break;
                case "BS":
                    //博思二维码
                    break;
                case "Ah":
                    //银联二维码
                    QrEntity qrEntity = new QrEntity(decodeDate);
                    try {
                        boolean validation = ValidationUtils.validation(qrEntity);
                        Logcat.d(validation);
                        if (validation) {
                            Toast.makeText(this, "验证通过", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "验证失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    //支付宝 二维码
                    mPresenter.checkAliQrCode(decodeDate, record_id,
                            pos_id, pos_mf_id, pos_sw_version,
                            merchant_type, currency, amount,
                            vehicle_id, plate_no, driver_id,
                            line_info, station_no, lbs_info,
                            record_type);
                    break;
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


    @Override
    public void showAliPublicKey(AlipayQrcodekey aliQrcodekey) {
        List<AlipayQrcodekey.PublicKeyListBean> aliQrcodekeyList = aliQrcodekey.getPublicKeyList();

        Collections.sort(aliQrcodekeyList);
        if (aliQrcodekeyList != null) {
//            Box<AlipayDatabaseBeen> beenBox = BoxStorManage.getInstance().getBoxDao().boxFor(AlipayDatabaseBeen.class);
//            for (int i = 0; i < aliQrcodekeyList.size(); i++) {
//                AlipayDatabaseBeen alipayDatabaseBeen = new AlipayDatabaseBeen(aliQrcodekey.getKeyType(), aliQrcodekey.getVersion(), aliQrcodekeyList.get(i).getKey_id(), aliQrcodekeyList.get(i).getPub_key());
//                beenBox.put(alipayDatabaseBeen);
//            }
//            List<AlipayDatabaseBeen> dd = BoxStorManage.getInstance().getBoxDao().boxFor(AlipayDatabaseBeen.class).getAll();
//            Log.i(TAG, "showAliPublicKey: " + dd.get(0).getKeyType() + dd.get(0).getKeyType() + dd.get(1).getKeyType());
            mPresenter.aliPayInitJni(aliQrcodekeyList);
        } else {
            Log.i(TAG, "获取支付宝key失败 ");
        }
        Log.i(TAG, "支付宝key：：： " + aliQrcodekeyList.toString());
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

    private AliCodeinfoData codeinfoData;

    @Override
    public void showCheckAliQrCode(AliCodeinfoData aliCodeinfoData) {
        codeinfoData = aliCodeinfoData;
        Log.e(TAG, "checkAliQrCode:不为空 ");
        if (codeinfoData.inforState == ErroCode.SUCCESS) {
            Log.i(TAG, "\n支付宝校验结果:：" + codeinfoData.inforState +
                    "\n卡类型:：" + Datautils.byteArrayToAscii(codeinfoData.cardType) +
                    "\n卡号：:" + Datautils.byteArrayToAscii(codeinfoData.cardNo) +
                    "\nuserId:：" + Datautils.byteArrayToAscii(codeinfoData.userId) +
                    "\n支付宝sdk返回:：" + Datautils.byteArrayToAscii(codeinfoData.alipayResult));


            QrcodeUpload qrcodeUpload = new QrcodeUpload();
            QrcodeUpload.DataBean dataBean = new QrcodeUpload.DataBean();
            dataBean.setRecordType("ALIQR");
            List<QrcodeUpload.DataBean> dataBeans = new ArrayList<>();
            QrcodeUpload.DataBean.RecordBean recordBean = new QrcodeUpload.DataBean.RecordBean("09", DataConversionUtils.getDefautCurrentTime(), "6410", 0, Datautils.byteArrayToAscii(codeinfoData.cardType), line_info, pos_mf_id, "6410001", record_id, Datautils.byteArrayToAscii(codeinfoData.userId), amount, driver_id, decodeDate, 1, Datautils.byteArrayToAscii(codeinfoData.cardNo), Datautils.byteArrayToAscii(codeinfoData.alipayResult), station_no, currency, record_type);
            dataBean.setRecord(recordBean);
            qrcodeUpload.setData(dataBeans);
            mPresenter.uploadAlipayRe(qrcodeUpload);
        } else {
            Log.i(TAG, "\n支付宝校验结果错误:：" + codeinfoData.inforState);
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
//        if (result == ErroCode.EC_SUCCESS) {
//            Log.i(TAG, "微信结果: " + "openID" + openId + "结果" + wechatResult);
//            AlipayUploadBeen alipayUploadBeen = new AlipayUploadBeen();
//            alipayUploadBeen.setRecordType("TXQR");
//            List<AlipayUploadBeen.RecordBean> recordBeans = new ArrayList<>();
//            AlipayUploadBeen.RecordBean recordBean = new AlipayUploadBeen.RecordBean("6410001", "09", record_id, pos_mf_id, driver_id, DataConversionUtils.getDefautCurrentTime(), "6410", line_info, station_no, currency, 0, 1, 1, "0123456", record_type, "20181114032300", openId, decodeDate, wechatResult);
//            recordBeans.add(recordBean);
//            alipayUploadBeen.setRecord(recordBeans);
//            mPresenter.uploadAlipayRe(alipayUploadBeen);
//
//        } else {
//            Log.i(TAG, "微信校验结果错误 " + result);
//
//        }
    }

    @Override
    public void showBosikey(BosiQrcodeKey bosiQrcodeKey) {
        Log.i(TAG, "showBosikey: " + bosiQrcodeKey.toString());
    }

    @Override
    public void showSetBosiCerPath(int state) {
        if (state == 0) {
            Log.i(TAG, "showSetBosiCerPath: 设置路径成功");
        } else {
            Log.i(TAG, "showSetBosiCerPath: 设置路径失败");
        }

    }

    @Override
    public void showBosiCerVersion(String vension) {


    }

    @Override
    public void showUpdataBosiKey(int state) {
        if (state == 0) {
            Log.i(TAG, "showUpdataBosiKey: 更新证书成功");
        } else {
            Log.i(TAG, "showUpdataBosiKey: 更新证书失败");

        }

    }
}
