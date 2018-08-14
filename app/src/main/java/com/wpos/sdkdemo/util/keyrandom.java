package com.wpos.sdkdemo.util;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wpos.sdkdemo.R;

import wangpos.sdk4.libbasebinder.Core;

public class keyrandom extends Activity {
    Button btnrandstart, btnrandquit;
    TextView tvrandomshow;
    private Core mCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standardpage);
        btnrandstart = (Button) findViewById(R.id.button);
        btnrandquit = (Button) findViewById(R.id.buttonquit);
        tvrandomshow = (TextView) findViewById(R.id.textViewresult);
        findViewById(R.id.note).setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {
                mCore = new Core(getApplicationContext());
            }
        }.start();
        btnrandstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 获取随机数 请求参数 1	期望获取长度	N	HEX2	高字节在前，支持最大255，高字节预留
                                应答参数 1	执行结果	N	HEX1	00：成功
                                2	随机数	N	HEX
                * */
                int length = 16;
                byte[] randombytes = new byte[255];
                int ret = -1;
                try {
                    ret = mCore.genereateRandomNum(length, randombytes);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                String s = bytesToHexString(randombytes);
                if (ret == 0) {
                    tvrandomshow.setText(getString(R.string.execute_success) + s);
                } else {
                    tvrandomshow.setText(getString(R.string.execute_fail));
                }
            }
        });

        btnrandquit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
