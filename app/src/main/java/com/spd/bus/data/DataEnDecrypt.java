package com.spd.bus.data;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.spd.bus.R;

import wangpos.sdk4.libbasebinder.Core;

public class DataEnDecrypt extends Activity {
    Button btnstart, btnexit;
    TextView tvshow;
    private Core mCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standardpage);
        btnstart = (Button) findViewById(R.id.button);
        btnexit = (Button) findViewById(R.id.buttonquit);
        tvshow = (TextView) findViewById(R.id.textViewresult);

        new Thread() {
            @Override
            public void run() {
                mCore = new Core(getApplicationContext());
            }
        }.start();

        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int len = 512;
                byte[] pbOut = new byte[len];
                int[] pwOutLen = new int[1];

                byte[] vectordata = new byte[8];
                int vectorLen = 8;
                for (int i = 0; i < vectordata.length; i++)
                    vectordata[i] = 0;

                byte[] datain = new byte[len];
                try {
                    for (int i = 0; i < datain.length; i++)
                        datain[i] = 8;//(byte)i;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                int datalen = len;

                int pddmode = 0;
                int encryptmode = 1;

                int operationmode = 0;

                int ret = -1;
                try {
                    //加密
                    ret = mCore.dataEnDecryptEx(Core.ALGORITHM_3DES, operationmode, "app1", encryptmode, vectorLen, vectordata, datalen, datain,
                            pddmode, pbOut, pwOutLen);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                StringBuilder builder = new StringBuilder();
                if (ret == 0) {
                    builder.append("success");
                    for (int i = 0; i < datain.length; i++) {
                        builder.append(datain[i] + ",");
                    }
                    for (int i = 0; i < pbOut.length; i++) {
                        builder.append(pbOut[i] + ",");
                    }
                    tvshow.setText(builder.toString());
                } else {
                    tvshow.setText("fail");
                    return;
                }

                byte[] datainDe = pbOut;
                int datalenDe = datain.length;

                pbOut = new byte[len];
                pwOutLen = new int[1];

                operationmode = 1;

                try {
                    //解密
                    ret = mCore.dataEnDecryptEx(Core.ALGORITHM_3DES, operationmode, "app1", encryptmode, vectorLen, vectordata, datalenDe, datainDe,
                            pddmode, pbOut, pwOutLen);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                if (ret == 0) {
                    builder.append("\nsuccess");
                    for (int i = 0; i < datain.length; i++) {
                        builder.append(datain[i] + ",");
                    }
                    for (int i = 0; i < pbOut.length; i++) {
                        builder.append(pbOut[i] + ",");
                    }
                    tvshow.setText(builder.toString());
                } else {
                    tvshow.setText("fail");
                    return;
                }

                for (int i = 0; i < len; i++) {
                    if (datain[i] != pbOut[i]) {
                        tvshow.setText("The No." + (i + 1) + " item is different from the original data");
                        return;
                    }
                }
                tvshow.setText("success");
            }
        });

        btnexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
