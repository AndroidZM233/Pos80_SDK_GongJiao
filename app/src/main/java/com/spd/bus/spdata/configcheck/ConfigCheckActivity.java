package com.spd.bus.spdata.configcheck;


import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.spd.base.utils.LogUtils;
import com.spd.base.utils.NetWorkUtils;
import com.spd.bus.MyApplication;
import com.spd.bus.R;
import com.spd.bus.spdata.PsamIcActivity;
import com.spd.bus.spdata.YinLianPayManage;
import com.spd.bus.spdata.mvp.MVPBaseActivity;
import com.spd.bus.util.ConfigUtils;
import com.spd.yinlianpay.WeiPassGlobal;
import com.spd.yinlianpay.comm.ChannelTool;
import com.spd.yinlianpay.context.MyContext;
import com.spd.yinlianpay.iso8583.Msg;
import com.spd.yinlianpay.listener.OnCommonListener;
import com.spd.yinlianpay.listener.OnTraditionListener;
import com.spd.yinlianpay.trade.TradeInfo;
import com.spd.yinlianpay.util.PrefUtil;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ConfigCheckActivity extends MVPBaseActivity<ConfigCheckContract.View, ConfigCheckPresenter>
        implements ConfigCheckContract.View, OnTraditionListener {

    private KProgressHUD kProgressHUD;
    private TextView mTvPsam1;
    private TextView mTvPsam2;
    private TextView mTvSercet;
    private TextView mTvAliKey;
    private TextView mTvYlKey;
    private TextView mTvWxKey;
    private TextView mTvYlsmKey;
    private TextView mTvWxMac;
    private YinLianPayManage yinLianPayManage;
    private boolean isDownloadOne = false;
    //第一次打开软件
    private boolean isFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_check);
//        while (!NetWorkUtils.isNetworkConnected(getApplicationContext())) {
//
//        }
        initView();
        ConfigUtils.loadTxtConfig();

        MyApplication.setInitDevListener(new MyApplication.InitDevListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kProgressHUD = KProgressHUD.create(ConfigCheckActivity.this);
                        kProgressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel("初始化中...")
                                .setCancellable(true)
                                .setAnimationSpeed(2)
                                .setDimAmount(0.5f)
                                .show();
                        mPresenter.initPsam(getApplicationContext());
                    }
                });

            }

            @Override
            public void onError() {
                if (kProgressHUD != null) {
                    kProgressHUD.dismiss();
                }

            }
        });
    }


    Runnable tLoginRun = new Runnable() {
        @Override
        public void run() {
            try {

                WeiPassGlobal.transactionClear();
                WeiPassGlobal.getTransactionInfo().setTransType(TradeInfo.Type_Sale);
                ChannelTool.login("01", "0000", new OnCommonListener() {
                    @Override
                    public void onSuccess() {
                        LogUtils.d("onSuccess:签到成功 开始下载 AID CAPK ");
                        //下载 AID CAPK 并写入内核
                        isDownloadOne = false;
                        ChannelTool.doDownParamter(1, ConfigCheckActivity.this);
                        ChannelTool.doDownParamter(2, ConfigCheckActivity.this);
                    }

                    @Override
                    public void onProgress(final String progress) {
                        LogUtils.d("onProgress: " + progress);
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        LogUtils.d("onError: " + errorMsg);
                        MyApplication.setYinLianPayManage(yinLianPayManage);
                        ConfigCheckActivity.this.finish();
                        Intent intent = new Intent(ConfigCheckActivity.this, PsamIcActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onDataBack(Msg msg) {

                    }

                    @Override
                    public void onToastError(int errorCode, String errorMsg) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        mTvPsam1 = findViewById(R.id.tv_psam1);
        mTvPsam2 = findViewById(R.id.tv_psam2);
        mTvSercet = findViewById(R.id.tv_sercet);
        mTvAliKey = findViewById(R.id.tv_ali_key);
        mTvYlKey = findViewById(R.id.tv_yl_key);
        mTvWxKey = findViewById(R.id.tv_wx_key);
        mTvYlsmKey = findViewById(R.id.tv_ylsm_key);
        mTvWxMac = findViewById(R.id.tv_wx_mac);
    }

    @Override
    public void setTextView(int address, String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                switch (address) {
                    case 1:
                        mTvPsam1.setText(msg);
                        break;
                    case 2:
                        mTvPsam2.setText(msg);
                        break;
                    case 3:
                        mTvSercet.setText(msg);
                        break;
                    case 4:
                        mTvAliKey.setText(msg);
                        break;
                    case 5:
                        mTvYlKey.setText(msg);
                        break;
                    case 6:
                        mTvWxKey.setText(msg);
                        break;
                    case 7:
                        mTvYlsmKey.setText(msg);
                        break;
                    case 8:
                        mTvWxMac.setText(msg);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    public void openActivity() {
        yinLianPayManage = new YinLianPayManage(getApplicationContext());
        yinLianPayManage.yinLianLogin(tLoginRun);


    }

    @Override
    public void onResult(TradeInfo info) {

    }

    @Override
    public void onSuccess() {
        if (PrefUtil.getICPARAMETER() && (PrefUtil.getICPASSWORD() || PrefUtil.getICSMD())) {
            PrefUtil.setISFIRSTRUN(true);
        }

        if (isDownloadOne) {
            if (kProgressHUD != null) {
                kProgressHUD.dismiss();
//                MyContext.onCreate(getApplicationContext(), MyApplication.getmKey()
//                        , MyApplication.getmCore(), MyApplication.getEmvCore()
//                        , MyApplication.getBankCardInstance());
//                byte[] outData = new byte[256];
//                int[] outLen = new int[1];
//                int capk = 0;
//                try {
//                    capk = MyContext.emvCore.getCAPK(1, outData, outLen);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//
//                if (capk == 0 && outLen[0] > 0) {
//
//                }
                MyApplication.setYinLianPayManage(yinLianPayManage);
                this.finish();
                Intent intent = new Intent(ConfigCheckActivity.this, PsamIcActivity.class);
                startActivity(intent);
            }
        } else {
            isDownloadOne = true;
        }

    }

    @Override
    public void onProgress(String progress) {

    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        }

        MyApplication.setYinLianPayManage(yinLianPayManage);
        this.finish();
        Intent intent = new Intent(ConfigCheckActivity.this, PsamIcActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDataBack(Msg msg) {

    }

    @Override
    public void onToastError(int errorCode, String errorMsg) {

    }
}
