package com.spd.bus.spdata.configcheck;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.spd.base.utils.LogUtils;
import com.spd.bus.MyApplication;
import com.spd.bus.R;
import com.spd.bus.spdata.PsamIcActivity;
import com.spd.bus.spdata.mvp.MVPBaseActivity;
import com.spd.bus.util.ConfigUtils;


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
//        while (!NetWorkUtils.isNetworkConnected(getApplicationContext())) {
//
//        }
        initView();
        ConfigUtils.loadTxtConfig();
        mPresenter.initPsam(getApplicationContext());
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

    }

}
