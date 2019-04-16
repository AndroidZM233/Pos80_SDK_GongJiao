package com.spd.bus.spdata.configcheck;


import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.spd.base.been.tianjin.CardBackBean;
import com.spd.bus.MainActivity;
import com.spd.bus.MyApplication;
import com.spd.bus.R;
import com.spd.bus.card.methods.ReturnVal;
import com.spd.bus.card.utils.ConfigUtils;
import com.spd.bus.spdata.PsamIcActivity;
import com.spd.bus.spdata.mvp.MVPBaseActivity;
import com.spd.bus.spdata.utils.PlaySound;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ConfigCheckActivity extends MVPBaseActivity<ConfigCheckContract.View, ConfigCheckPresenter> implements ConfigCheckContract.View {

    private TextView mTvInfo;
    private KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_check);
        initView();
        ConfigUtils.loadTxtConfig();


        MyApplication.setInitDevListener(new MyApplication.InitDevListener() {
            @Override
            public void onSuccess() {
                kProgressHUD = KProgressHUD.create(ConfigCheckActivity.this);
                kProgressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("初始化中...")
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
                mPresenter.initPsam(getApplicationContext());
            }

            @Override
            public void onError() {
                if (kProgressHUD!=null){
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
        mTvInfo = findViewById(R.id.tv_info);
        mTvInfo.setText("");
    }

    @Override
    public void setTextView(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvInfo.append(msg);
                if (kProgressHUD!=null){
                    kProgressHUD.dismiss();
                }
            }
        });

    }

    @Override
    public void openActivity() {
        if (kProgressHUD!=null){
            kProgressHUD.dismiss();
        }
        this.finish();
        Intent intent = new Intent(ConfigCheckActivity.this, PsamIcActivity.class);
        startActivity(intent);

    }
}
