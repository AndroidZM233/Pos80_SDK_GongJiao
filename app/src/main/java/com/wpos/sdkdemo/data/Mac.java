package com.wpos.sdkdemo.data;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wpos.sdkdemo.R;
import com.wpos.sdkdemo.util.keyrandom;

import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libbasebinder.HEX;

public class Mac extends AppCompatActivity {
    Button btnmacstart, btnmacexit;
    TextView tvshow;
    private Core mCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mac);
        btnmacstart = (Button) findViewById(R.id.buttonstartmac);
        btnmacexit = (Button) findViewById(R.id.buttonquitmac);
        tvshow = (TextView) findViewById(R.id.textViewresultmac);
        new Thread() {
            @Override
            public void run() {
                mCore = new Core(getApplicationContext());
            }
        }.start();
        btnmacstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGetMac();
            }
        });

        btnmacexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void newGetMac(){
        final int len = 24;
        byte[] pbOutdata = new byte[100];
        int[] pbOutdataLen = new int[50];
        int macMode = 0x02;
        byte[] vectordata = new byte[8];
        int vectorLen = 8;
        for (int i = 0; i < vectordata.length; i++)
            vectordata[i] = 0;
        int dataLen = len;
        byte[] data = new byte[len];
        data[0] = (byte)0x4E;
        data[1] = (byte)0x6F;
        data[2] = (byte)0x77;
        data[3] = (byte)0x20;
        data[4] = (byte)0x69;
        data[5] = (byte)0x73;
        data[6] = (byte)0x20;
        data[7] = (byte)0x74;
        data[8] = (byte)0x68;
        data[9] = (byte)0x65;
        data[10] = (byte)0x20;
        data[11] = (byte)0x74;
        data[12] = (byte)0x69;
        data[13] = (byte)0x6D;
        data[14] = (byte)0x65;
        data[15] = (byte)0x20;
        data[16] = (byte)0x66;
        data[17] = (byte)0x6F;
        data[18] = (byte)0x72;
        data[19] = (byte)0x20;
        data[20] = (byte)0x61;
        data[21] = (byte)0x6C;
        data[22] = (byte)0x6C;
        data[23] = (byte)0x20;
        int ret = -1;
        try {
            ret = mCore.getMacWithAlgorithm("app1", Core.ALGORITHM_3DES, vectorLen, vectordata, dataLen, data, macMode, pbOutdata, pbOutdataLen);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (ret == 0) {
            byte[] dataRe = new byte[pbOutdataLen[0]-2];
            System.arraycopy(pbOutdata,2,dataRe,0,pbOutdataLen[0]-2);
            tvshow.setText(getString(R.string.execute_success) + HEX.bytesToHex(dataRe));
        } else {
            tvshow.setText(getString(R.string.execute_fail));
        }
    }

    private void originalGetMac(){
        final int len = 16;
        byte[] pbOutdata = new byte[100];
        int[] pbOutdataLen = new int[50];
        int macMode = 0;
        byte[] vectordata = new byte[8];
        int vectorLen = 8;
        for (int i = 0; i < vectordata.length; i++)
            vectordata[i] = 0;
        int dataLen = len;
        byte[] data = new byte[len];
        for (int i = 0; i < len; i++)
            data[i] = (byte) i;
        int ret = -1;
        try {
            ret = mCore.getMacEx("app1", vectorLen, vectordata, dataLen, data, macMode, pbOutdata, pbOutdataLen);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (ret == 0) {
            tvshow.setText(getString(R.string.execute_success) + keyrandom.bytesToHexString(pbOutdata) + "\n" + pbOutdataLen);
        } else {
            tvshow.setText(getString(R.string.execute_fail));
        }
    }

}
