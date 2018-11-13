package com.spd.bus.spdata;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.spd.bus.DataConversionUtils;
import com.spd.bus.R;
import com.spd.bus.spdata.been.IcCardBeen;
import com.spd.bus.spdata.been.TCommInfo;
import com.spd.bus.spdata.been.PsamBeen;
import com.spd.bus.spdata.utils.DataUtils;
import com.spd.bus.util.TLV;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libbasebinder.HEX;

public class oldPsamIcActivityold extends AppCompatActivity implements View.OnClickListener {


    private Button btnOneExcute, btnLooperExcute, btnBarCode;
    private TextView tvShowMsg;
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

//    private final byte[] random = {0x00, (byte) 0x84, 0x00, 0x00, 0x08};
//    private final byte[] dir1 = {0x00, (byte) 0xa4, 0x04, 0x00, 0x08, (byte) 0xa0, 0x00, 0x00, (byte) 0xd6, 0x32, 0x01, 0x01, 0x05};
//    //PSAM卡选择互联互通应用
//    private final byte[] psam2_select_dir2 = {0x00, (byte) 0xA4, 0x04, 0x00, 0x06, (byte) 0xBD, (byte) 0xA8, (byte) 0xC9, (byte) 0xE8, (byte) 0xB2, (byte) 0xBF};
//    //获取PSAM卡密钥索引指令
//
//    //                          00          A4      04  00      0E 32       50 41 59        2E 53 59        53 2E 44            44 46 30 31
//    private final byte[] ic4_dir1 = {0x00, (byte) 0xa4, 0x04, 0x00, 0x0e, 0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2e, 0x44, 0x44, 0x46, 0x30, 0x31};
//    //x000xA40x040x000x0e0x320x500x410x590x2e0x530x590x530x2e0x440x440x460x300x31 返回9000 之后发0x000xA40x040x000x080xA00x000x000x060x320x010x010x05
//    private final byte[] ic4_dir2 = {0x00, (byte) 0xa4, 0x04, 0x00, 0x08, (byte) 0xa0, 0x00, 0x00, 0x06, 0x32, 0x01, 0x01, 0x05};
//
//    //\x00\xB2\x01\xF4\x00
//    private final byte[] fuhe_read_jiaoyi_msg = {0x00, (byte) 0xB2, 0x01, (byte) 0xF4, 0x00};
//    // 80 50 01 02 0B XX（密钥索引 PSAM卡00B0970001指令返回）XX XX XX XX（本次交易金额）XX XX XX XX XX XX（终端机编号 PSAM卡00B0960006指令返回）
//    //    byte[] init_ic = {(byte) 0x80, 0x50, 0x01, 0x02, 0x0B, 0x01, 0x00, 0x00, 0x00, 0x02, 0x41, 0x31, 0x01, 0x21, 0x01, (byte) 0x97, 0x0F};
//    private final byte[] init_ic = {(byte) 0x80, 0x50, 0x01, 0x02, 0x0B, 0x01, 0x00, 0x00, 0x00, 0x02, 0x41, 0x31, 0x01, 0x21, 0x01, (byte) 0x98, 0x0F};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initCard();
    }


    private void initCard() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBankCard = new BankCard(getApplicationContext());
                mCore = new Core(getApplicationContext());
                psamInit();
                psamzhujianbuInit();
//                handler.postDelayed(runnable, 0);
            }
        }).start();

    }

    private void initUI() {
        setContentView(R.layout.activity_psam_ic);
        btnOneExcute = findViewById(R.id.btn_excute);
        btnLooperExcute = findViewById(R.id.btn_start);
        CheckBox checkBox = findViewById(R.id.checkbox);
        btnBarCode = findViewById(R.id.btn_barcode);
        btnBarCode.setOnClickListener(this);
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
        btnOneExcute.setOnClickListener(this);
        btnLooperExcute.setOnClickListener(this);
        tvShowMsg = findViewById(R.id.tv_show_msg);
        tvShowMsg.setText("互联互通消费流程\n");
    }


    @Override
    public void onClick(View v) {
        if (v == btnLooperExcute) {
//            psamInit();
        } else if (v == btnOneExcute) {
//            handler.postDelayed(runnable, 0);
        } else if (v == btnBarCode) {
            M1ICCard();
//            ICExpance();
//            Intent intent = new Intent(this, ScanActivity.class);
//            startActivity(intent);
        }
    }


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
    private void psamzhujianbuInit() {
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PSAM2, 60, respdata, resplen, "app1");
            Log.d(TAG, "住建部切换psam " + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
            if (retvalue == 0) {
                if (respdata[0] == (byte) 0x05) {//IC卡已经插入
                    retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, psam1_get_id, psam1_get_id.length, respdata, resplen);
                    if (retvalue == 0) {
                        Log.d(TAG, "住建部16文件return" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
                        if (DataConversionUtils.byteArrayToString(cutBytes(respdata, resplen[0] - 2, 2)).equals("9000")) {
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
                                                Log.d(TAG, "psamzhujianbuInit: 秘钥" + DataConversionUtils.byteArrayToString(psamDatas.get(i).getKeyID()) + "终端编号：" + DataConversionUtils.byteArrayToString(psamDatas.get(i).getTermBumber()));
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
                        } else {
                            mCore.buzzer();
                            handler.sendMessage(handler.obtainMessage(1, "住建部获取终端编号错误:" + cutBytes(respdata, resplen[0] - 2, 2)));
                            Log.d(TAG, "住建部获取终端编号错误:" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)));
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
//                    mBankCard.breakOffCommand();
//                    Log.i("stw", "run: 销毁本次读卡");
                    Log.i("stw", "run: 开始本次读卡等待");
                    ICExpance();//执行等待读卡消费
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            handler.postDelayed(runnable, 100);
        }
    };
    private IcCardBeen icCardBeen = new IcCardBeen();

    private int m1Flag = -1;

    private void M1ICCard() {
        CInfoF = new TCommInfo();
        CInfoZ = new TCommInfo();
        CInfo = new TCommInfo();
        try {
            Log.d(TAG, "M1ICCard:m1卡消费开始 ");
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC, 60, respdata, resplen, "app1");//切换到非接卡读取
            if (retvalue != 0) {
                m1Flag = 1;
                return;
            }
            Log.d(TAG, "test: 微智pose切换到非接卡读取：return" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
            if (respdata[0] == 0x07) {//检测到非接IC卡
                tvShowMsg.append("检测到非接IC卡\n");
            } else if (respdata[0] == 0x37) {//检测到 M1-S50 卡
                handler.sendMessage(handler.obtainMessage(1, "检测到 M1-S50 卡\n"));
                //读取非接卡 SN(UID)信息
                retvalue = mBankCard.getCardSNFunction(respdata, resplen);
                if (retvalue != 0) {
                    m1Flag = 1;
                    return;
                }
                Log.d(TAG, "M1ICCard: " + HEX.bytesToHex(respdata));

                snUid = cutBytes(respdata, 0, resplen[0]);
                icCardBeen.setSnr(snUid);
                byte[] key = new byte[6];
                System.arraycopy(snUid, 0, key, 0, 4);
                System.arraycopy(snUid, 0, key, 4, 2);
                //认证1扇区第4块
                retvalue = mBankCard.m1CardKeyAuth(0x41, 0x04, key.length, key, snUid.length, snUid);
                if (retvalue != 0) {
                    m1Flag = 1;
                    return;
                }
                Log.d(TAG, "M1ICCard:开始操作1扇区\n M1卡秘钥认证");
                retvalue = mBankCard.m1CardReadBlockData(0x04, respdata, resplen);
                if (retvalue != 0) {
                    m1Flag = 1;
                    return;
                }
                byte[] bytes04 = cutBytes(respdata, 1, resplen[0] - 1);
                Log.d(TAG, "M1ICCard: 读04块返回：" + HEX.bytesToHex(bytes04));


                icCardBeen.setIssueSnr(cutBytes(bytes04, 0, 8));
                icCardBeen.setCityNr(cutBytes(bytes04, 0, 2));
                icCardBeen.setVocCode(cutBytes(bytes04, 2, 2));
                icCardBeen.setIssueCode(cutBytes(bytes04, 4, 4));
                icCardBeen.setMackNr(cutBytes(bytes04, 8, 4));
                icCardBeen.setfStartUse(cutBytes(bytes04, 12, 1));
                icCardBeen.setCardType(cutBytes(bytes04, 13, 1));//卡类型判断表格中没有return
                icCardBeen.setfBlackCard(0);
                switch (icCardBeen.getfStartUse()[0]) {//判断启用标志
                    case (byte) 0x01://未启用
                        m1Flag = 1;
                        return;
                    case (byte) 0x02://正常
                        // TODO: 2018/8/29
                        break;
                    case (byte) 0x03://停用
                        m1Flag = 1;
                        return;
                    case (byte) 0x04://黑名单
                        m1Flag = 1;
                        icCardBeen.setfBlackCard(1);
                        return;
                }

                //读1扇区05块数据
                retvalue = mBankCard.m1CardReadBlockData(0x05, respdata, resplen);
                if (retvalue != 0) {
                    return;
                }
                byte[] bytes05 = cutBytes(respdata, 1, resplen[0] - 1);
                Log.d(TAG, "M1ICCard: 读05块返回：" + HEX.bytesToHex(bytes05));
                icCardBeen.setIssueDate(cutBytes(bytes05, 0, 4));
                icCardBeen.setEndUserDate(cutBytes(bytes05, 4, 4));
                icCardBeen.setStartUserDate(cutBytes(bytes05, 8, 4));

                //读1扇区06块数据
                retvalue = mBankCard.m1CardReadBlockData(0x06, respdata, resplen);
                if (retvalue != 0) {
                    m1Flag = 1;
                    return;
                }
                byte[] bytes06 = cutBytes(respdata, 1, resplen[0] - 1);
                Log.d(TAG, "M1ICCard: 读06块返回：" + HEX.bytesToHex(bytes06));
                icCardBeen.setPurIncUtc(cutBytes(bytes06, 0, 6));//转UTC时间
                icCardBeen.setPurIncMoney(cutBytes(bytes06, 9, 2));

                //第0扇区 01块认证
                retvalue = mBankCard.m1CardKeyAuth(0x41, 0x01,
                        6, new byte[]{(byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3, (byte) 0xA4, (byte) 0xA5}, snUid.length, snUid);
                if (retvalue != 0) {
                    m1Flag = 1;
                    return;
                }
                retvalue = mBankCard.m1CardReadBlockData(0x01, respdata, resplen);//读第0扇区第一块秘钥
                if (retvalue != 0) {
                    m1Flag = 1;
                    return;
                }

                byte[] bytes01 = cutBytes(respdata, 1, resplen[0] - 1);
                //扇区标识符
                secF = bytes01;
                //算秘钥指令
                String sendCmd = "80fc010110" + HEX.bytesToHex(icCardBeen.getCityNr()) + DataConversionUtils.byteArrayToString(icCardBeen.getSnr()) + HEX.bytesToHex(cutBytes(icCardBeen.getIssueSnr(), 6, 2)) + HEX.bytesToHex(icCardBeen.getMackNr())
                        + HEX.bytesToHex(cutBytes(secF, 2, 2)) + HEX.bytesToHex(cutBytes(secF, 6, 2));
                Log.d(TAG, "M1ICCard:  " + sendCmd);
                //psam卡计算秘钥
                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, DataConversionUtils.HexString2Bytes(sendCmd), DataConversionUtils.HexString2Bytes(sendCmd).length, respdata, resplen);
                if (retvalue != 0) {
                    m1Flag = 1;
                    return;
                }
                if (!Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                    m1Flag = 1;
                    return;
                }
                byte[] result = cutBytes(respdata, 0, resplen[0] - 2);
                Log.d(TAG, "M1ICCard: " + HEX.bytesToHex(result));
                //4/5扇区秘钥相同
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
                    m1Flag = 1;
                    return;
                }

                //读6扇区第24块
                retvalue = mBankCard.m1CardReadBlockData(24, respdata, resplen);
                if (retvalue != 0) {
                    m1Flag = 1;
                    return;
                }
                byte[] bytes24 = cutBytes(respdata, 1, resplen[0] - 1);
                byte[] dtZ = bytes24;
                byte chk;
                int i;
                for (chk = 0, i = 0; i < 16; i++) {
                    chk ^= dtZ[i];
                }
                if (Arrays.equals(cutBytes(dtZ, 8, 7),
                        new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,}) && chk == 0) {
                    CInfoZ.fValid = 1;
                }
                if (Arrays.equals(cutBytes(dtZ, 0, 8), new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff,
                        (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff})) {
                    CInfoZ.fValid = 0;
                }
                if (dtZ[0] > 8) {
                    CInfoZ.fValid = 0;
                }
                CInfoZ.cPtr = dtZ[0];
                CInfoZ.iPurCount = cutBytes(dtZ, 1, 2);
                CInfoZ.fProc = dtZ[3];
                CInfoZ.iYueCount = cutBytes(dtZ, 4, 2);
                ;
                CInfoZ.fBlack = dtZ[6];
                CInfoZ.fFileNr = dtZ[7];
                //副本  有效性
                //读6扇区第25块
                retvalue = mBankCard.m1CardReadBlockData(25, respdata, resplen);
                if (retvalue != 0) {
                    m1Flag = 1;
                    return;
                }
                byte[] bytes25 = cutBytes(respdata, 1, resplen[0] - 1);
                byte[] dtF = bytes25;
                for (chk = 0, i = 0; i < 16; i++) {
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
                ;
                CInfoF.fBlack = dtF[6];
                CInfoF.fFileNr = dtF[7];

                if (CInfoZ.fValid == 1) {
                    CInfo = CInfoZ;
                } else if (CInfoF.fValid == 1) {
                    CInfo = CInfoF;
                } else {
//                    return;
                }

                if ((CInfoZ.fValid == 1 && (CInfoZ.fBlack == 4)) || (CInfoF.fValid == 1 && (CInfoF.fBlack == 4))) {
                    icCardBeen.setfBlackCard(1);//黑名单 报语音
                    return;
                }

                //第二扇区08 块认证
                byte[] lodKey2 = lodkey[2];
                retvalue = mBankCard.m1CardKeyAuth(0x41, 0x08, lodKey2.length, lodKey2, snUid.length, snUid);
                if (retvalue != 0) {
                    m1Flag = 1;
                    return;
                }
                byte[] bytes09;
                byte[] bytes10;

                retvalue = mBankCard.m1CardReadBlockData(10, respdata, resplen);
                retvalue = mBankCard.m1CardValueOperation(0x2D, 10, 1, 10);
                //读2扇区第9块
                retvalue = mBankCard.m1CardReadBlockData(10, respdata, resplen);
                if (retvalue != 0) {
                    m1Flag = 1;
                    return;
                }
                bytes09 = cutBytes(respdata, 1, resplen[0] - 1);
                Log.d(TAG, "M1ICCard:2区09块 " + HEX.bytesToHex(bytes09));
                //测试验证微智写接口
//                retvalue = mBankCard.m1CardKeyAuth(0x41, 0x08, gg.length, gg, snUid.length, snUid);
//                retvalue = mBankCard.m1CardWriteBlockData(0x09, bytes09.length, bytes09);
//                Log.d(TAG, "M1ICCard:" + retvalue + "写2区09块 " + HEX.bytesToHex(bytes09));
//                retvalue = mBankCard.m1CardReadBlockData(0x09, respdata, resplen);
//                Log.d(TAG, "M1ICCard:" + retvalue + " 读09块" + HEX.bytesToHex(respdata));
                //读2扇区第10块
                retvalue = mBankCard.m1CardReadBlockData(10, respdata, resplen);
                if (retvalue != 0) {
                    m1Flag = 1;
                    return;
                }
                bytes10 = cutBytes(respdata, 1, resplen[0] - 1);
                Log.d(TAG, "M1ICCard:2区10块 " + HEX.bytesToHex(bytes10));
                if (ValueBlockValid(bytes09)) {
                    Log.d(TAG, "M1ICCard: 2区10块过");
                    //判断2区9块10块数据是否一致
                    if (!Arrays.equals(bytes09, bytes10)) {
                        bytes10 = bytes09;
                        retvalue = mBankCard.m1CardWriteBlockData(0x0A, bytes09.length, bytes09);
                    }
                } else {
                    if (ValueBlockValid(bytes10)) {
                        Log.d(TAG, "M1ICCard: 2区10块过 ");
                        if (!Arrays.equals(bytes09, bytes10)) {
                            bytes09 = bytes10;
                            retvalue = mBankCard.m1CardWriteBlockData(0x09, bytes10.length, bytes10);
                        }
                    } else {
                        Log.d(TAG, "M1ICCard: 2区10块错返回 ");
                        return;
                    }
                }
                byte[] yue09 = cutBytes(bytes09, 0, 4);
                byte[] ActRemaining = testReverseSelf(yue09);//原额
                icCardBeen.setPurOriMoney(ActRemaining);
                icCardBeen.setPurSub(new byte[]{0x00, 0x00, 0x00, 0x01});//定义消费金额
                retvalue = mBankCard.m1CardValueOperation(0x2D, 0x09, 1, 0x09);



                Log.d(TAG, "M1ICCard: " + HEX.bytesToHex(ActRemaining));
                if (writeCardRcd()) {

                }


                tvShowMsg.append("检测到 M1-S50 卡\n");
            } else if (respdata[0] == 0x47) {// 检测到 M1-S70 卡
                tvShowMsg.append("检测到 M1-S70 卡\n");
//                    handler.sendMessage(handler.obtainMessage(1, "检测到 M1-S70 卡\n"));
            }


        } catch (
                RemoteException e)

        {
            e.printStackTrace();
        }


    }

    private TCommInfo CInfoZ, CInfoF, CInfo;

    private byte RcdBlkIndex[] = {12, 13, 14, 16, 17, 18, 20, 21, 22};//所有交易记录块

    public boolean writeCardRcd() {

        //step 0
        CInfo.fFileNr = secF[2];
        if (CInfo.cPtr > 8) {
            CInfo.cPtr = 0;
        }
        int blk = RcdBlkIndex[CInfo.cPtr];//当前交易记录块
        CInfo.cPtr = (byte) (CInfo.cPtr == 8 ? 0 : CInfo.cPtr + 1);//
        byte[] ulDevUTC = DataConversionUtils.HexString2Bytes(DataUtils.getUTCtimes());//获取UTC时间
//        if (tCardOpDu.ucSec != 2) {
//            VarToArr( & RcdToCard[4], tCardOpDu.YueOriMoney, 4);
//            VarToArr( & RcdToCard[8], tCardOpDu.YueSub, 3);
//            RcdToCard[11] = 2;
//            CInfo.fProc = 3;
//            CInfo.iYueCount = CInfo.iYueCount + 1;
//        } else {
        byte[] RcdToCard = new byte[16];
        System.arraycopy(ulDevUTC, 0, RcdToCard, 0, 4);
        System.arraycopy(icCardBeen.getPurOriMoney(), 0, RcdToCard, 4, 4);//获取消费前原额
        System.arraycopy(icCardBeen.getPurSub(), 1, RcdToCard, 8, 3);//获取本次消费金额
        RcdToCard[11] = 1;
        CInfo.fProc = 1;

//        CInfo.iPurCount = CInfo.iPurCount[1] += 1;
        char count = 0;
        for (int i = 0; i < 2; i++) {
            count <<= 8;
            count += CInfo.iPurCount[i];
        }
        int is = DataConversionUtils.byteArrayToInt(CInfo.iPurCount);
        byte[] result = new byte[2];
//        result[3] = (byte) ((is >> 24) & 0xFF);
//        result[2] = (byte) ((is >> 16) & 0xFF);
        result[0] = (byte) ((is >> 8) & 0xFF);
        result[1] = (byte) (is & 0xFF);
        Log.d(TAG, "Modify_InfoArea: " + HEX.bytesToHex(result));


//        }
        //设备号写死
        RcdToCard[12] = 0x64;
        RcdToCard[13] = 0x10;
        RcdToCard[14] = 0x00;
        RcdToCard[15] = 0x01;

        for (; ; ) {
            //step 1 改写24 25块数据
            if (!Modify_InfoArea(24)) {
                return false;
            }
            //step 2
            if (!m1CardKeyAuth(blk, blk / 4)) {//blk/4区 blk块
                return false;
            }
            try {
                //写卡  将消费记录写入消费记录区
                retvalue = mBankCard.m1CardWriteBlockData(blk, RcdToCard.length, RcdToCard);
                if (retvalue != 0) {
                    return false;
                }
                //消费记录区读取
                retvalue = mBankCard.m1CardReadBlockData(blk, respdata, resplen);
                if (retvalue != 0) {
                    return false;
                }
                byte[] RcdInCard = cutBytes(respdata, 1, resplen[0] - 1);
                if (!Arrays.equals(RcdInCard, RcdToCard)) {
                    return false;
                }
                byte[] bytes = new byte[16];
                if (Arrays.equals(RcdInCard, bytes)) {//判断是否 读回==00
                    return false;
                }

            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }

            //step 3

//            PrepareRecord(tCardOpDu.ucSec == 2 ? 1 : 3);
            fErr = 1;
            if (!Modify_InfoArea(25)) {
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
            byte[] dtZ = testReverseSelf(icCardBeen.getPurSub());//获取消费金额
//            }
            try {
                //执行消费 将消费金额带入
                retvalue = mBankCard.m1CardValueOperation(0x2D, 0x09, DataConversionUtils.byteArrayToInt(dtZ), 0x09);
                if (retvalue != 0) {
                    return false;
                }
                //执行 读出 现在原额
                retvalue = mBankCard.m1CardReadBlockData(0x09, respdata, resplen);
                if (retvalue != 0) {
                    return false;
                }
                dtZ = cutBytes(respdata, 1, resplen[0] - 1);//本次消费后的原额
                //判断消费前金额-消费金额=消费后金额
                if (DataConversionUtils.byteArrayToInt(icCardBeen.getPurIncMoney()) - DataConversionUtils.byteArrayToInt(dtZ)
                        == DataConversionUtils.byteArrayToInt(cutBytes(dtZ, 0, 4))) {
                }
                //step 6
                retvalue = mBankCard.m1CardWriteBlockData(0x0A, dtZ.length, dtZ);
                if (retvalue != 0) {
                    return false;
                }
                retvalue = mBankCard.m1CardReadBlockData(0x0A, respdata, resplen);
                if (retvalue != 0) {
                    return false;
                }
                byte[] dtF = cutBytes(respdata, 1, resplen[0] - 1);//本次消费后的原额
                if (!Arrays.equals(dtF, dtZ)) {
                    return false;
                }

            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }

            //step 7
            CInfo.fProc = (byte) ((CInfo.fProc + 1) & 0xfe);
            if (!Modify_InfoArea(24)) {
                return false;
            }
            //step 8
            fErr = 0;
            if (!Modify_InfoArea(25)) {
                return false;
            }
        }
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
                0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,}, 0, info, 4, 2);
        for (chk = 0, i = 0; i < 15; i++) {
            chk ^= info[i];
        }
        info[15] = chk;
        try {
            //认证地24或25块
            byte[] lodKeys = lodkey[6];
            retvalue = mBankCard.m1CardKeyAuth(0x41, blk, lodKeys.length, lodKeys, snUid.length, snUid);
            if (retvalue != 0) {
                return false;
            }
            retvalue = mBankCard.m1CardWriteBlockData(blk, info.length, info);
            if (retvalue != 0) {
                return false;
            }
            retvalue = mBankCard.m1CardReadBlockData(blk, respdata, resplen);
            if (retvalue != 0) {
                return false;
            }
            tpdt = cutBytes(respdata, 1, resplen[0] - 1);
            if (!Arrays.equals(info, tpdt)) {
                return false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public byte[] testReverseSelf(byte[] strings) {
        for (int start = 0, end = strings.length - 1; start < end; start++, end--) {
            byte temp = strings[end];
            strings[end] = strings[start];
            strings[start] = temp;
        }
        return strings;
    }

    boolean ValueBlockValid(byte[] dts) {// 钱包/月票正本或副本余额有效性检测
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

    private void ICExpance() {
        try {
            mCore.buzzer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            Log.d("times", "ICExpance: ===== 开始消费=====");
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC, 60, respdata, resplen, "app1");//切换到非接卡读取
            if (retvalue == 0) {
                Log.d(TAG, "test: 微智pose切换到非接卡读取：return" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
                if (respdata[0] == 0x07) {//检测到非接IC卡
//                    handler.sendMessage(handler.obtainMessage(1, "检测到非接IC卡\n"));
                    tvShowMsg.append("检测到非接IC卡\n");
                } else if (respdata[0] == 0x37) {//检测到 M1-S50 卡
//                    handler.sendMessage(handler.obtainMessage(1, "检测到 M1-S50 卡\n"));
                    tvShowMsg.append("检测到 M1-S50 卡\n");
                } else if (respdata[0] == 0x47) {// 检测到 M1-S70 卡
                    tvShowMsg.append("检测到 M1-S70 卡\n");
//                    handler.sendMessage(handler.obtainMessage(1, "检测到 M1-S70 卡\n"));
                }
            }

            Log.d(TAG, "fuhexiaofei: 消费记录 tlv 3031 send " + DataConversionUtils.byteArrayToString(fuhe_tlv));
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, fuhe_tlv, fuhe_tlv.length, respdata, resplen);
            if (retvalue == 0) {
                byte[] testTlv = cutBytes(respdata, 0, resplen[0] - 2);
                Log.d(TAG, resplen[0] + "消费记录 tlv 3031 return :  " + HEX.bytesToHex(testTlv));
                tvShowMsg.append("解析 TLV0105 return ：" + HEX.bytesToHex(testTlv) + "\n");
//                handler.sendMessage(handler.obtainMessage(1, "解析 TLV0105 return ：" + HEX.bytesToHex(testTlv) + "\n"));
                if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
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
                            tvShowMsg.append("解析到TLV发送0105 send： " + select_ic + "\n");
//                            handler.sendMessage(handler.obtainMessage(1, "解析到TLV发送0105 send： " + select_ic));
                            byte[] ELECT_DIANZIQIANBAO = DataConversionUtils.HexString2Bytes(select_ic);
                            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, ELECT_DIANZIQIANBAO, ELECT_DIANZIQIANBAO.length, respdata, resplen);//选择电子钱包应用
                        }
                    }
                    if (isFlag) {
                        systemTime = getDateTime();//获取交易时间
                        tvShowMsg.append("默认发送 0105 send： " + HEX.bytesToHex(ic_file) + "\n");
//                        handler.sendMessage(handler.obtainMessage(1, "默认发送 0105 send： " + HEX.bytesToHex(ic_file)));
                        Log.d(TAG, "test: 默认发送 0105 send ：" + HEX.bytesToHex(ic_file));
                        retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, ic_file, ic_file.length, respdata, resplen);//选择电子钱包应用
                    }
                    if (retvalue == 0) {
                        Log.d(TAG, "test: 0105 return： " + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
                        if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                            tvShowMsg.append("发送（0105）选电子钱包 返回：" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "\n");
//                            handler.sendMessage(handler.obtainMessage(1, "发送（0105）选电子钱包 返回：" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0]))));
                            Log.d(TAG, "===读15文件 === sebd" + HEX.bytesToHex(ic_read_file));
                            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, ic_read_file, ic_read_file.length, respdata, resplen);//读应用下公共应用基本信息文件指令
                            if (retvalue == 0) {
                                Log.d(TAG, "===IC读15文件 === retur:" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])));
                                if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                                    tvShowMsg.append("IC读15文件 return ：" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "\n");
//                                    handler.sendMessage(handler.obtainMessage(1, "IC读15文件 return ：" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0]))));
                                    //02313750FFFFFFFF0201 031049906000000000062017010820991231000090000000000000000000
                                    Log.e(TAG, "ICExpance:arraycopy开始 ");
                                    System.arraycopy(respdata, 12, cardId, 0, 8);
                                    Log.e(TAG, "ICExpance:arraycopy 停止");
                                    System.arraycopy(respdata, 0, file15_8, 0, 8);
                                    System.arraycopy(respdata, 2, city, 0, 2);
                                    Log.d(TAG, "===卡应用序列号 ===" + HEX.bytesToHex(cardId));

                                    //读17文件
                                    byte[] IC_READ17_FILE = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x00};
                                    Log.d(TAG, "test: 读17文件 00b0 send:" + HEX.bytesToHex(IC_READ17_FILE));
                                    retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, IC_READ17_FILE, IC_READ17_FILE.length, respdata, resplen);
                                    if (retvalue == 0) {
                                        Log.d(TAG, resplen[0] + "===IC读17文件 === return:" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "维智" + retvalue);
                                        if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                                            tvShowMsg.append("IC读17文件返回：" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "\n");

                                            Log.d(TAG, "test: IC读1E文件 00b2 send  00B201F400");
                                            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, DataConversionUtils.HexString2Bytes("00B201F400"), DataConversionUtils.HexString2Bytes("00B201F400").length, respdata, resplen);
                                            if (retvalue == 0) {
                                                Log.d(TAG, "test: IC读1E文件 00b2 return：" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "维智" + retvalue + "\n");
                                                if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                                                    tvShowMsg.append("IC读1E文件返回：" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)) + "\n");


                                                    Log.d(TAG, "===805c IC余额===  send   :805C030204");
                                                    retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, DataConversionUtils.HexString2Bytes("805C030204"), DataConversionUtils.HexString2Bytes("805C030204").length, respdata, resplen);
                                                    if (retvalue == 0) {
                                                        Log.d(TAG, "===IC余额 (805c)===  return  :" + HEX.bytesToHex(respdata) + "维智" + retvalue);
                                                        if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                                                            tvShowMsg.append("IC卡余额返回：" + HEX.bytesToHex(cutBytes(respdata, resplen[0] - 2, 2)) + "\n");


                                                            byte[] INIT_IC_FILE = initICcard();
                                                            Log.d(TAG, "===IC卡初始化=== 8050 send   :" + HEX.bytesToHex(INIT_IC_FILE));
                                                            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, INIT_IC_FILE, INIT_IC_FILE.length, respdata, resplen);
                                                            if (retvalue == 0) {
                                                                Log.d(TAG, "===IC卡初始化=== 8050  return:" + HEX.bytesToHex(respdata));
                                                                if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {

                                                                    tvShowMsg.append("IC卡初始化返回: " + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "维智" + retvalue + "\n");
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
                                                                    if (retvalue == 0) {
                                                                        Log.d(TAG, "===获取MAC1 8070 return:" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "   " + retvalue);
                                                                        if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                                                                            praseMAC1(respdata);
                                                                            tvShowMsg.append("IC获取MAC1返回：" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "\n");


                                                                            //80dc
                                                                            String ss = "80DC00F030060000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
                                                                            Log.d(TAG, ss.length() + "===更新1E文件 80dc  send===" + ss);
                                                                            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, DataConversionUtils.HexString2Bytes(ss), DataConversionUtils.HexString2Bytes(ss).length, respdata, resplen);
                                                                            if (retvalue == 0) {
                                                                                Log.d(TAG, "===更新1E文件 return===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "   " + retvalue);
                                                                                if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                                                                                    tvShowMsg.append("更新1E文件 80dc  return: " + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "\n");

                                                                                    byte[] cmd = getIcPurchase();
                                                                                    Log.d(TAG, "===IC卡 8054消费发送===" + HEX.bytesToHex(cmd));
                                                                                    retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, cmd, cmd.length, respdata, resplen);
                                                                                    if (retvalue == 0) {
                                                                                        Log.d(TAG, "===IC 卡 8054消费返回===" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + " " + retvalue);
                                                                                        if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                                                                                            tvShowMsg.append("IC 卡 8054消费返回:" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "\n");
                                                                                            byte[] mac2 = cutBytes(respdata, 0, 8);
                                                                                            byte[] PSAM_CHECK_MAC2 = checkPsamMac2(mac2);


                                                                                            Log.d(TAG, "===psam卡 8072校验 send===: " + HEX.bytesToHex(PSAM_CHECK_MAC2) + "微智结果：" + retvalue);
//                                                                                        retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, PSAM_CHECK_MAC2, PSAM_CHECK_MAC2.length, respdata, resplen);
                                                                                            if (retvalue == 0) {
                                                                                                Log.d(TAG, "===psam卡 8072校验返回===: " + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "微智结果：" + retvalue);
                                                                                                if (Arrays.equals(APDU_RESULT_SUCCESS, cutBytes(respdata, resplen[0] - 2, 2))) {
                                                                                                    tvShowMsg.append("=psam卡 8072校验返回 ：" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "\n");

                                                                                                } else {
                                                                                                    tvShowMsg.append("=psam卡 8072校验返回失败：\n");
//                                                                                                    isExpense = 0;
                                                                                                }
                                                                                            }


                                                                                        } else {
                                                                                            tvShowMsg.append("IC 卡 8054消费返回失败:\n");
//                                                                                            isExpense = 0;

                                                                                        }
                                                                                    }


                                                                                } else {
                                                                                    tvShowMsg.append("更新1E文件 80dc错误：\n");
                                                                                }

                                                                            }

                                                                        } else {
                                                                            tvShowMsg.append("IC获取MAC1失败:\n");
                                                                        }
                                                                    }
                                                                } else {
                                                                    tvShowMsg.append("IC卡初始化失败:\n");
                                                                }
                                                            }

                                                        } else {
                                                            tvShowMsg.append("IC卡余额失败：\n");
                                                        }

                                                    }
                                                } else {
                                                    tvShowMsg.append("IC读1E文件错误：\n");
                                                }
                                            }
                                        } else {
                                            tvShowMsg.append("IC读17文件错误：\n");
                                        }
                                    }

                                } else {
                                    tvShowMsg.append("IC读15文件 错误 ：" + HEX.bytesToHex(cutBytes(respdata, 0, resplen[0])) + "\n");
                                }
                            }

                        } else {
                            tvShowMsg.append("切换电子钱包应用失败（0105）\n");
                        }
                    }

                } else if (Arrays.equals(cutBytes(respdata, resplen[0] - 2, 2), APDU_RESULT_FAILE)) {//黑名单
                    tvShowMsg.append("黑名单（3031）\n");

                } else {
                    tvShowMsg.append("切换电子钱包应用失败（0105）\n");
                }
            }
            mBankCard.breakOffCommand();
            isExpense = 1;
            Log.d("times", "ICExpance:=====  消费完成=======");
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvShowMsg.append((String) msg.obj + "\n");
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
//        String start = "805001020B";
        if (CAPP == 1) {
            stringBuilder.replace(5, 6, "3");
//            start = "805003020B";
        }
        stringBuilder.append(DataConversionUtils.byteArrayToString(psamKey)).append("00000002").append(DataConversionUtils.byteArrayToString(deviceCode)).append("0F");
//        String key = DataConversionUtils.byteArrayToString(psamKey);
//        String vanlce = "00000002";//金额固定
//        String deviceID = DataConversionUtils.byteArrayToString(deviceCode);
//        String end = "0F";
//        String initIC = start + key + vanlce + deviceID + end;
        return DataConversionUtils.HexString2Bytes(stringBuilder.toString());
    }

    /**
     * PSAM卡产生MAC1指令 8070
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
        if (CAPP == 1) {//是否为符合消费
            cmd[15] = (byte) 0x09;
        } else {
            cmd[15] = (byte) 0x06;
        }
        System.arraycopy(systemTime, 0, cmd, 16, 7);//系统时间
        cmd[23] = 0x01;
        cmd[24] = 0x00;//arithmeticFlog
        System.arraycopy(cardId, 0, cmd, 25, 8);// 8 ->35字节
        System.arraycopy(file15_8, 0, cmd, 33, 8);// 2 ->36字节
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
        //PSAM_ATC 4
        System.arraycopy(systemTime, 0, cmd, 9, 7);//系统时间
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

    private byte[] PSAM_ATC = new byte[4];
    private byte[] MAC1 = new byte[4];

    private void praseMAC1(byte[] data) {
        if (data.length <= 2) {
            Log.e(TAG, "===获取MAC1失败===" + HEX.bytesToHex(data));
            return;
        }
        System.arraycopy(data, 0, PSAM_ATC, 0, 4);
        System.arraycopy(data, 4, MAC1, 0, 4);
    }


    private byte[] intToByteArray(int data) {
        byte[] result = new byte[2];
//        result[0] = (byte) ((data >> 24) & 0xFF);
//        result[1] = (byte) ((data >> 16) & 0xFF);
        result[0] = (byte) ((data >> 8) & 0xFF);
        result[1] = (byte) (data & 0xFF);
        return result;
    }

    private byte[] getDateTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");//可以方便地修改日期格式
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
}
