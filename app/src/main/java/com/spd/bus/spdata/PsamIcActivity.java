package com.spd.bus.spdata;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
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
import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.Datautils;
import com.spd.base.view.SignalView;
import com.spd.bus.Info;
import com.spd.bus.MyApplication;
import com.spd.bus.R;
import com.spd.bus.card.methods.JTBCardManager;
import com.spd.bus.card.methods.M1CardManager;
import com.spd.bus.card.methods.ReturnVal;
import com.spd.bus.card.utils.ConfigUtils;
import com.spd.bus.card.utils.LogUtils;
import com.spd.bus.spdata.been.ErroCode;
import com.spd.bus.spdata.mvp.MVPBaseActivity;
import com.spd.bus.spdata.spdbuspay.SpdBusPayContract;
import com.spd.bus.spdata.spdbuspay.SpdBusPayPresenter;
import com.spd.bus.spdata.utils.PlaySound;
import com.spd.bus.util.SaveDataUtils;
import com.spd.yinlianpay.context.MyContext;
import com.spd.yinlianpay.util.PrefUtil;

import java.io.UnsupportedEncodingException;
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
    private YinLianPayManage yinLianPayManage;
    private String balance = "2元";
    private LinearLayout mLlDriver;
    private LinearLayout mLlMain;
    private boolean isDriverUI = false;
    private LinearLayout mLlShowData;
    private boolean isShowDataUI = false;

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
        updateConfigUI();
        mLlDriver = findViewById(R.id.ll_driver);
        mLlMain = findViewById(R.id.ll_main);
        mLlShowData = findViewById(R.id.ll_show_data);
    }


    private void initCard() {
        try {
            MyApplication.getHSMDecoder().addResultListener(PsamIcActivity.this);
            //注册系统时间广播 只能动态注册
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            yinLianPayManage = new YinLianPayManage(getApplicationContext());
            yinLianPayManage.yinLianLogin();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    startTimer(true);
                }
            }).start();
            //获取支付宝微信key
            mPresenter.getZhiFuBaoAppSercet(getApplicationContext());
            mPresenter.getZhiFuBaoBlack(getApplicationContext());
            mPresenter.getZhiFuBaoWhite(getApplicationContext());
            mPresenter.getAliPubKeyTianJin();
            mPresenter.getYinLianPubKey(getApplicationContext());
            mPresenter.getWechatPublicKeyTianJin();
            String read = SharedXmlUtil.getInstance(getApplicationContext())
                    .read(Info.POS_ID, Info.POS_ID_INIT);
            mPresenter.getShuangMianPubKey(getApplicationContext(), "pos/posKeys?data=" + read);

            updateTime();
        } catch (Exception e) {
            Toast.makeText(this, "请检查网络连接", Toast.LENGTH_SHORT).show();
        }
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mLayoutLineInfo.setVisibility(View.VISIBLE);
            mLayoutFace.setVisibility(View.GONE);
            mLayoutXiaofei.setVisibility(View.GONE);
            mTvBalanceTitle.setText("票价");
            mTvBalance.setText(balance + "元");
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


    private long ltime = 0;

    private void startTimer(boolean isStart) {
        while (isStart) {
            try {
                //非接卡在位检测;
                int result = MyApplication.mBankCard.piccDetect();
                if (result == 0) {
                    isFlag = 2;
                } else if (result == 1 && isFlag == 2) {
                    ltime = System.currentTimeMillis();
                    LogUtils.d("开始本次读卡等待");
                    //切换到非接卡读取
                    retvalue = MyApplication.mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC, 1, respdata, resplen, "app1");
                    Log.i("stw", "=== ===" + (System.currentTimeMillis() - ltime));
                    if (retvalue != 0) {
                        isFlag = 1;
                        return;
                    }
                    //检测到非接IC卡

                    if (respdata[0] == 0x07) {
//                        PlaySound.play(XUESHENGKA, 0);
                        CardBackBean cardBackBean = null;
                        try {
//                            if (MyApplication.psamDatas.size() != 2) {
////                                doVal(new CardBackBean(ReturnVal.CAD_PSAM_ERROR, null));
////                                break;
////                            }

                            if (isDriverUI || isShowDataUI) {
                                continue;
                            }
                            cardBackBean = JTBCardManager.getInstance()
                                    .mainMethod(getApplicationContext(), MyApplication.mBankCard
                                            , MyApplication.psamDatas, yinLianPayManage, handler);
                            if (cardBackBean == null) {
                                continue;
                            } else {
                                doVal(cardBackBean);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            ConfigUtils.logWrite(e.toString());
                        }

                        isFlag = 0;
//                        icExpance();//执行等待读卡消费
//                        if (isFlag == 1) {
//                            PlaySound.play(PlaySound.qingchongshua, 0);
//                        }
                    } else if (respdata[0] == 0x37) {
                        //检测到 M1-S50 卡
                        if (MyApplication.psamDatas.size() != 2) {
                            doVal(new CardBackBean(ReturnVal.CAD_PSAM_ERROR, null));
                            continue;
                        }
                        if (isShowDataUI) {
                            continue;
                        }
                        Log.i("stw", "m1结束寻卡===" + (System.currentTimeMillis() - ltime));
                        try {
                            if (isDriverUI){
                                CardBackBean cardBackBean = M1CardManager.getInstance()
                                        .mainMethod(getApplicationContext(), MyApplication.mBankCard
                                                , M1CardManager.M150, 0, MyApplication.psamDatas);
                                doVal(cardBackBean);
                            }else {
                                CardBackBean cardBackBean = M1CardManager.getInstance()
                                        .mainMethod(getApplicationContext(), MyApplication.mBankCard
                                                , M1CardManager.M150, 2, MyApplication.psamDatas);
                                doVal(cardBackBean);
                            }

                        } catch (Exception e) {
                            LogUtils.v(e.toString());
                            ConfigUtils.logWrite(e.toString());
                            e.printStackTrace();
                        }
                        isFlag = 0;
//                        if (isFlag == 1) {
//                            PlaySound.play(PlaySound.qingchongshua, 0);
//                        }
                    } else if (respdata[0] == 0x47) {
                        // 检测到 M1-S70 卡
                        if (MyApplication.psamDatas.size() != 2) {
                            doVal(new CardBackBean(ReturnVal.CAD_PSAM_ERROR, null));
                            continue;
                        }
                        if (isShowDataUI) {
                            continue;
                        }
                        try {
                            if (isDriverUI){
                                CardBackBean cardBackBean = M1CardManager.getInstance()
                                        .mainMethod(getApplicationContext(), MyApplication.mBankCard
                                                , M1CardManager.M170, 0, MyApplication.psamDatas);
                                doVal(cardBackBean);
                            }else {
                                CardBackBean cardBackBean = M1CardManager.getInstance()
                                        .mainMethod(getApplicationContext(), MyApplication.mBankCard
                                                , M1CardManager.M170, 2, MyApplication.psamDatas);
                                doVal(cardBackBean);
                            }

                        } catch (Exception e) {
                            LogUtils.v(e.toString());
                            ConfigUtils.logWrite(e.toString());
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
                        LogUtils.d("CAD_READ");
                        break;
                    case ReturnVal.CAD_EXPIRE:
                    case ReturnVal.CAD_SELL:
                        Toast.makeText(PsamIcActivity.this, "未启用及过期卡"
                                , Toast.LENGTH_SHORT).show();
                        PlaySound.play(PlaySound.QINGTOUBI, 0);
                        break;
                    case ReturnVal.CAD_OK:
                        PlaySound.play(PlaySound.dang, 0);
                        updateUI(cardBackBean);
//                        mPresenter.uploadCardData();
                        break;
                    case ReturnVal.CAD_RETRY:
                        PlaySound.play(PlaySound.qingchongshua, 0);
                        Toast.makeText(PsamIcActivity.this, "请重刷"
                                , Toast.LENGTH_SHORT).show();
                        break;
                    case ReturnVal.CAD_SETCOK:
                        PlaySound.play(PlaySound.setSuccess, 0);
                        Toast.makeText(PsamIcActivity.this, "设置成功"
                                , Toast.LENGTH_SHORT).show();
                        updateConfigUI();

                        break;
                    case ReturnVal.CODE_WEIXIN_SUCCESS:
                        PlaySound.play(PlaySound.MASHANGYIXING, 0);
                        break;
                    case ReturnVal.CODE_ZHIFUBAO_SUCCESS:
//                        int ulHCSub = cardBackBean.getCardOpDU().ulHCSub;
//                        if (ulHCSub == 100) {
                        PlaySound.play(PlaySound.ZHIFUBAO, 0);
//                        } else if (ulHCSub == -2) {
//                            PlaySound.play(PlaySound.ERWEIMASHIXIAO, 0);
//                        } else {
//                            PlaySound.play(PlaySound.QINGTOUBI, 0);
//                        }
                        LogUtils.v("支付宝消费成功");
                        break;
                    case ReturnVal.CODE_YINLAIN_SUCCESS:
                        LogUtils.d("CODE_YINLAIN_SUCCESS");
                        PlaySound.play(PlaySound.xiaofeiSuccse, 0);
                        break;
                    case ReturnVal.CAD_REUSE:
                        LogUtils.d("CAD_REUSE");
                        PlaySound.play(PlaySound.QINGTOUBI, 0);
                        break;
                    case ReturnVal.CAD_LOGON:
                        PlaySound.play(PlaySound.SIJISHANGBAN, 0);
                        Toast.makeText(PsamIcActivity.this, "司机签到成功"
                                , Toast.LENGTH_SHORT).show();
                        break;
                    case ReturnVal.CODE_PLEASE_SET:
                        PlaySound.play(PlaySound.QINGSHEZHI, 0);
                        break;
                    case ReturnVal.CAD_PSAM_ERROR:
                        Toast.makeText(PsamIcActivity.this, "PSAM-0"
                                , Toast.LENGTH_SHORT).show();
                        break;
                    case ReturnVal.CAD_MAC1:
                    case ReturnVal.CAD_MAC2:
                        Toast.makeText(PsamIcActivity.this, "MAC错误"
                                , Toast.LENGTH_SHORT).show();
                        PlaySound.play(PlaySound.QINGTOUBI, 0);
                        break;
                    case ReturnVal.CAD_BL1:
                    case ReturnVal.CAD_BL2:
                        Toast.makeText(PsamIcActivity.this, "黑名单"
                                , Toast.LENGTH_SHORT).show();
                        PlaySound.play(PlaySound.QINGTOUBI, 0);
                        break;

                    case ReturnVal.CAD_EMPTY:
                        Toast.makeText(PsamIcActivity.this, "请投币"
                                , Toast.LENGTH_SHORT).show();
                        PlaySound.play(PlaySound.QINGTOUBI, 0);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void updateConfigUI() {
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession()
                .getRunParaFileDao().loadAll();
        if (runParaFiles.size() > 0) {
            RunParaFile runParaFile = runParaFiles.get(0);

            String line = Datautils.byteArrayToString(runParaFile.getLineNr());
            mTvLine.setText(Integer.parseInt(line) + "路");
            balance = (double) Datautils.byteArrayToInt(runParaFile.getKeyV1()) / 100 + "元";

            mTvBalance.setText(balance);
            String busNr = SharedXmlUtil.getInstance(getApplicationContext())
                    .read(Info.BUS_NO, "");
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
            int num = cardOpDU.yueOriMoney - cardOpDU.yueSub;
            mTvXiaofeiTitle.setText("剩余次数");
            mTvXiaofeiMoney.setText(num + "");
            mTvBalanceTitle.setText("月票");
            mTvBalance.setText("1");
        } else {
            mTvBalanceTitle.setText("余额");
            int balance = cardOpDU.purorimoneyInt - cardOpDU.pursubInt;
            mTvBalance.setText(((double) balance / 100) + "元");
            mTvXiaofeiMoney.setText(((double) cardOpDU.pursubInt / 100) + "元");
        }
        handler.postDelayed(runnable, 3000);
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
//                    PlaySound.play(PlaySound.dang, 0);
//                    int blance = Datautils.byteArrayToInt(icCardBeen.getPurorimoney(), false) - Datautils.byteArrayToInt(icCardBeen.getPursub());
//                    mTvBalance.setVisibility(View.VISIBLE);
//                    mTvBalance.setText("余额：" + (double) blance / 100 + "元");
//                    handler.postDelayed(runnable, 2000);
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
                case 123:
                    Log.i(TAG, "handleMessage:  返回 卡號");
                    Log.i(TAG, "卡号: " + msg.obj.toString());
                    break;
                case MyContext.Hide_Progress:

                    Log.i(TAG, "handleMessage:  返回 Hide_Progress" + msg.obj.toString());
                    break;
                case MyContext.MSG_ERROR:
                    Log.i("stw", "主界面==交易失败银联时间111111== " + (System.currentTimeMillis() - ltime));
//                    LedUtils.getInstance().error();
                    PlaySound.play(PlaySound.qingchongshua, 0);
                    Log.i(TAG, "handleMessage:showErrDialo " + msg);
                    isFlag = 1;
                    try {
                        MyApplication.mBankCard.breakOffCommand();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case MyContext.MSG_PROGRESS:
                    Log.e(TAG, "银联双免MSG_PROGRESS==" + msg.obj.toString());
                    break;
                case 12313:
                    mTvDeviceMessage.setText(msg.obj.toString());
                    break;

                case MyContext.RESULT_SUCCESS:
                    Log.i("stw", "主界面==交易成功银联时间22222== " + (System.currentTimeMillis() - ltime));
                    PrefUtil.putReversal(null);
                    PlaySound.play(PlaySound.xiaofeiSuccse, 0);
                    mLayoutLineInfo.setVisibility(View.GONE);
                    mTvBalance.setText("1.00元");
                    mLayoutXiaofei.setVisibility(View.VISIBLE);
                    mTvXiaofeiMoney.setVisibility(View.GONE);
                    mTvXiaofeiTitle.setText("消费成功!");
                    handler.postDelayed(runnable, 2000);
                    isFlag = 0;
                    break;

                case MyContext.RESULT_OUTCOME:
                    if (msg.obj != null) {
                        Log.d("SwipCard RESULT_OUTCOME", msg.obj.toString());
                    }
                    break;
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
        if (hsmDecodeResults.length > 0) {
            HSMDecodeResult firstResult = hsmDecodeResults[0];
            if (Datautils.isUTF8(firstResult.getBarcodeDataBytes())) {
                try {
                    decodeDate = new String(firstResult.getBarcodeDataBytes(), "utf8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                decodeDate = Datautils.byteArrayToString(firstResult.getBarcodeDataBytes());
            }

            if (decodeDate.equals(codes)) {
//                LogUtils.v("二维码重复"+decodeDate);
                if ("sp".equals(decodeDate.substring(0, 2))) {
                    Toast.makeText(this, "二维码重复", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            codes = decodeDate;
            LogUtils.v("二维码: " + decodeDate);
            ltime = System.currentTimeMillis();
            Log.e("stw", "微信解码：" + (System.currentTimeMillis() - ltime));
            switch (decodeDate.substring(0, 2)) {
                case "TX":
                    //腾讯（微信）
//                    decodeDate = "TXACn3tk21bPvjQAMBT3ZQdwY0NjAxMDATMzEwNTAwOTkwMTAwMDU1NjgyNQEFAAIQAABhIGG7Yu9CGRVcLW/QXDaqUADmAAALuAAAAAAAAAQCXtB3JtUiF/DEjJ4Gsxh2CUhK5MLNgU TzJbr8K7DzSQrLVzUtVteLeGsJvsixWimDyvzkMIg71PQAXC19rv+rQKkAAAAA3pVPmg\u003d\u003d";
                    LogUtils.d("识别到微信二维码");
                    mPresenter.checkWechatTianJin(decodeDate, 1, (byte) 1, (byte) 1
                            , "17430597", "12");
                    break;
                case "Ah":
                    mPresenter.checkYinLianCode(getApplicationContext(), decodeDate);
                    break;
                case "sp":
                    String read = SharedXmlUtil.getInstance(getApplicationContext())
                            .read(Info.BUS_NO, "");
                    String[] split = decodeDate.split(":");
                    if (read.equals(split[1])) {
                        LogUtils.v(split[1]);
                        Toast.makeText(this, "车辆号相同无需设置", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SharedXmlUtil.getInstance(getApplicationContext()).write(Info.BUS_NO, split[1]);
                    Toast.makeText(this, "车辆号设置完成", Toast.LENGTH_SHORT).show();
                    PlaySound.play(PlaySound.setSuccess, 0);
                    List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
                    if (runParaFiles.size() > 0) {
                        RunParaFile runParaFile = runParaFiles.get(0);
                        runParaFile.setBusNr(Datautils.HexString2Bytes(split[1]));
                        DbDaoManage.getDaoSession().getRunParaFileDao().deleteAll();
                        DbDaoManage.getDaoSession().getRunParaFileDao().insert(runParaFile);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String busNr = SharedXmlUtil.getInstance(getApplicationContext())
                                    .read(Info.BUS_NO, "");
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append("车辆号：" + busNr + "\n");
                            String posID = SharedXmlUtil.getInstance(getApplicationContext())
                                    .read(Info.POS_ID, Info.POS_ID_INIT);
                            stringBuffer.append("设备号：" + posID);
                            mTvDeviceMessage.setText(stringBuffer + "");
                        }
                    });

                    break;
                default:
                    //支付宝 二维码
                    mPresenter.checkAliQrCode(decodeDate);
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
        if (codeinfoData.result == ErroCode.SUCCESS) {
            // TODO: 2019/3/11 存储天津公交需要的数据
            try {
                SaveDataUtils.saveZhiFuBaoReqDataBean(tianjinAlipayRes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            doVal(new CardBackBean(ReturnVal.CODE_ZHIFUBAO_SUCCESS, null));
            mPresenter.uploadAlipayRe(getApplicationContext());
//            handler.sendMessage(handler.obtainMessage(3));
        } else {
            doVal(new CardBackBean(ReturnVal.CAD_RETRY, null));
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
            LogUtils.i("微信结果: " + "openID" + openId + "结果" + wechatResult);
            doVal(new CardBackBean(ReturnVal.CODE_WEIXIN_SUCCESS, null));
            mPresenter.uploadWechatRe(getApplicationContext());
        } else {
            LogUtils.i("微信校验结果错误 " + result);
            doVal(new CardBackBean(ReturnVal.CAD_EMPTY, null));
        }
    }

    @Override
    public void doCheckWechatTianJin() {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (isDriverUI) {
                    mLlShowData.setVisibility(View.VISIBLE);
                    isShowDataUI = true;
                    isDriverUI = false;
                    mLlDriver.setVisibility(View.GONE);
                } else if (isShowDataUI) {
                    mLlShowData.setVisibility(View.GONE);
                    isShowDataUI = false;
                    isDriverUI = false;
                    mLlDriver.setVisibility(View.GONE);
                } else {
                    mLlShowData.setVisibility(View.GONE);
                    isShowDataUI = false;
                    isDriverUI = true;
                    mLlDriver.setVisibility(View.VISIBLE);
                }
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (isDriverUI) {
                    mLlShowData.setVisibility(View.GONE);
                    isShowDataUI = false;
                    isDriverUI = false;
                    mLlDriver.setVisibility(View.GONE);
                } else if (isShowDataUI) {
                    mLlShowData.setVisibility(View.GONE);
                    isShowDataUI = false;
                    isDriverUI = true;
                    mLlDriver.setVisibility(View.VISIBLE);
                } else {
                    mLlShowData.setVisibility(View.VISIBLE);
                    isShowDataUI = true;
                    isDriverUI = false;
                    mLlDriver.setVisibility(View.GONE);
                }
            }
        }


        return super.onKeyDown(keyCode, event);
    }
}
