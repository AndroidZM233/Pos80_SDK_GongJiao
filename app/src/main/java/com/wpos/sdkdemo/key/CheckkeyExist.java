package com.wpos.sdkdemo.key;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wpos.sdkdemo.R;

import wangpos.sdk4.libkeymanagerbinder.Key;

public class CheckkeyExist extends Activity {
    TextView textViewresult;
    Handler mHandler;
    private Key mKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standardpage);
        mHandler = new EventHandler();
        textViewresult = (TextView) findViewById(R.id.textViewresult);

        new Thread() {
            @Override
            public void run() {
                mKey = new Key(getApplicationContext());
            }
        }.start();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ret = 0;
                try {
                    ret = mKey.checkKeyExist("app1", Key.KEY_REQUEST_TMK);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (ret == 0) {
                    Message msg = new Message();
                    msg.what = 1000;
                    msg.obj = ret + "";
                    mHandler.sendMessage(msg);
                } else if (ret == 1|| ret ==2 || ret == -1) {
                    Message msg = new Message();
                    msg.what = 1001;
                    msg.obj = ret + "";
                    mHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = 1002;
                    msg.obj = ret + "";
                    mHandler.sendMessage(msg);
                }
            }
        });
        findViewById(R.id.buttonquit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public class EventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    Log.v("------str-----", (String) msg.obj);
                    textViewresult.setText("The key is exist" + msg.obj);
                    break;
                case 1001:
                    textViewresult.setText("The key is not exist " + msg.obj);
                    break;
                case 1002:
                    textViewresult.setText("error " + msg.obj);
                    break;
                default:
                    break;
            }
        }
    }
}
