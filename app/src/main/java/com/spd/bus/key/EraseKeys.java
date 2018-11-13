package com.spd.bus.key;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spd.bus.R;
import com.spd.bus.util.ByteUtil;

import wangpos.sdk4.libbasebinder.RspCode;
import wangpos.sdk4.libkeymanagerbinder.Key;

public class EraseKeys extends Activity {
    Button btnpedstart, btnpedquit;
    TextView tvpedshow;
    private Key mKey;
    private String flag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standardpage);

        Intent intent  = getIntent();
        flag = intent.getStringExtra("flag");


        btnpedstart = (Button) findViewById(R.id.button);
        btnpedquit = (Button) findViewById(R.id.buttonquit);
        tvpedshow = (TextView) findViewById(R.id.textViewresult);
        findViewById(R.id.note).setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {
                mKey = new Key(getApplicationContext());
            }
        }.start();
        btnpedstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ret = -1;
                if ("KCV".equals(flag)) {
                    ret = getKcv();
                }else {
                    ret = erase();
                    if (ret == 0) {
                        tvpedshow.setText(getString(R.string.execute_success));
                    } else {
                        tvpedshow.setText(getString(R.string.execute_fail));
                    }
                }
            }
        });
        btnpedquit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private int erase() {
        try {
            return mKey.erasePED();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return RspCode.ERROR;
    }

    private int getKcv(){
        try {
            byte[] out = new byte[64];
            int[] len = new int[1];
            int res = -1;
            res = mKey.getKeyKCV("app1",Key.KEY_REQUEST_TMK ,1,out,len);
            if (res == 0) {
                byte[] result = new byte[len[0]];
                System.arraycopy(out,0,result,0,len[0]);
                String resMsg = ByteUtil.bytes2HexString(result);
                tvpedshow.setText(getString(R.string.execute_success)+"--KCV=="+resMsg);
            }else {
                tvpedshow.setText(getString(R.string.execute_fail));
            }
            return res;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return RspCode.ERROR;
    }
}
