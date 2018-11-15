package com.spd.bosipay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spd.base.Datautils;

public class BositestActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 测试
     */
    private Button mBtnTest;
    private TextView mTvShow;
    BosiPayJni bosiPayJni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bositest);
        initView();
        bosiPayJni = new BosiPayJni();
    }

    private void initView() {
        mBtnTest = findViewById(R.id.btn_test);
        mBtnTest.setOnClickListener(this);
        mTvShow = findViewById(R.id.tv_show);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_test) {
            String re = bosiPayJni.getCertVer();
            mTvShow.append("获取证书：" + re + "\n");
            int res = bosiPayJni.VerifyCode("");
            mTvShow.append("验码结果：" + res + "\n");
            res = bosiPayJni.UpdateCert(uCerts);
            mTvShow.append("更新证书结果：" + res + "\n" );

        } else {
        }
    }

    byte uCerts[] = {0x43, 0x45, 0x52, 0x54, 0x01, 0x02, 0x01, 0x01, 0x01, (byte) 0xA1, 0x74,
            0x77, 0x4F, 0x2D, (byte) 0xC4, 0x6B, (byte) 0xA8, (byte) 0xEF, 0x74, 0x4A, 0x7A, (byte) 0xE6,
            (byte) 0x83, 0x3C, 0x2E, (byte) 0x92, 0x40, (byte) 0xFF, 0x11, (byte) 0x8C, 0x54, 0x1E, 0x5D,
            (byte) 0xF3, (byte) 0xE5, 0x70, (byte) 0x8F, 0x44, (byte) 0xCE, (byte) 0xBD, (byte) 0xA6, (byte) 0xB0, (byte) 0xA5,(byte) 0x98, (byte) 0x1C, 0x4D};
}
