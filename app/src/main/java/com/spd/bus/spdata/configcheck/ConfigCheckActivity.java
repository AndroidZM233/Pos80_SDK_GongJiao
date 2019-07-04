package com.spd.bus.spdata.configcheck;


import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.example.zhoukai.modemtooltest.ModemToolTest;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.spd.base.utils.LogUtils;
import com.spd.base.utils.NetWorkUtils;
import com.spd.bus.MyApplication;
import com.spd.bus.R;
import com.spd.bus.entity.TransportCard;
import com.spd.bus.spdata.PsamIcActivity;
import com.spd.bus.spdata.mvp.MVPBaseActivity;
import com.spd.bus.sql.SqlStatement;
import com.spd.bus.util.ConfigUtils;
import com.spd.bus.util.Configurations;
import com.spd.bus.util.FileConfData;

import java.io.File;
import java.util.List;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ConfigCheckActivity extends MVPBaseActivity<ConfigCheckContract.View, ConfigCheckPresenter>
        implements ConfigCheckContract.View {

    private KProgressHUD kProgressHUD;
    private TextView mTvPsam1;
    private TextView mTvPsam2;
    private TextView mTvSercet;
    private TextView mTvAliKey;
    private TextView mTvYlKey;
    private TextView mTvWxKey;
    private TextView mTvYlsmKey;
    private TextView mTvWxMac;
    private boolean isDownloadOne = false;
    //第一次打开软件
    private boolean isFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_check);
        initView();
        kProgressHUD = KProgressHUD.create(ConfigCheckActivity.this);
        kProgressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        while (!NetWorkUtils.isNetworkConnected(getApplicationContext())) {
            kProgressHUD.setLabel("没有网络，请检测网络连接");
        }


//        String sn = ModemToolTest.getItem(7);
//        if (!"000000".equals(sn)) {
//            String deviceNId = sn.substring(7, 15);
//            SqlStatement.updataSN(deviceNId);
//        }


//        ConfigUtils.loadTxtConfig();

        kProgressHUD.setLabel("初始化中...");
        MyApplication.setInitDevListener(new MyApplication.InitDevListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kProgressHUD.setLabel("初始化中...");
                        mPresenter.initPsam(getApplicationContext());
                    }
                });

            }

            @Override
            public void onError() {
                kProgressHUD.setLabel("激活失败！准备重新激活...");
                MyApplication.getInstance().initScanBards(getApplicationContext());
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getInstance().initScanBards(getApplicationContext());
            }
        }).start();

        List<TransportCard> listInfo = SqlStatement.getParameterAll();
        File file = new File(MyApplication.FILENAME_INFO);
        if (file.exists() && Configurations.read_config(MyApplication.FILENAME_INFO).length() > 20 && "00".equals(listInfo.get(0).getInfo())) {
            try {
                FileConfData.writeDB();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                LogUtils.i("备份恢复失败");
            }
        }
    }


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
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                startActivity(new Intent(ConfigCheckActivity.this, PsamIcActivity.class));
            }
        }).start();

    }

}
