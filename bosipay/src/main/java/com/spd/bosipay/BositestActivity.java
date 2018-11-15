package com.spd.bosipay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        mBtnTest = (Button) findViewById(R.id.btn_test);
        mBtnTest.setOnClickListener(this);
        mTvShow = (TextView) findViewById(R.id.tv_show);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_test) {
            int re = bosiPayJni.initdev();
            mTvShow.append(re + "");

        } else {
        }
    }
}
