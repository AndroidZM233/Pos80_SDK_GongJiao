package com.wpos.sdkdemo.dukpt;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wpos.sdkdemo.util.ByteUtil;
import com.wpos.sdkdemo.R;

import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libkeymanagerbinder.Key;

public class DUKPT extends AppCompatActivity {
    Button btnkeyexstart , btnkeyexquit;
    TextView tvkeyexshow;
    private Key mKey;
    private Core mCore;
    private String mResultString;
    private static final int UPDATE_TEXTVIEW = 1;
    private String TAG = "DUKPT";

    private MyHandler mHandler = new MyHandler();

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case UPDATE_TEXTVIEW:
                    if(tvkeyexshow!=null) {
                        tvkeyexshow.setText(mResultString);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standardpage);
        btnkeyexstart = (Button)findViewById(R.id.button);
        btnkeyexquit = (Button)findViewById(R.id.buttonquit);
        tvkeyexshow = (TextView) findViewById(R.id.textViewtmk);
        findViewById(R.id.note).setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {
                mKey = new Key(getApplicationContext());
                mCore = new Core(getApplicationContext());
            }
        }.start();
        btnkeyexstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(importIPEK()==0 && injectKSN()==0){
                                if(IncreaseKSN()==0){
                                    GetKSN();
                                    EnDecrypt_getMAC();
                            }
                }
            }
        });

        btnkeyexquit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private int importIPEK() {
        // Import IPEK
        byte[] CertData = new byte[8];
        for(int i = 0 ; i < 8 ;i ++){
            CertData[i] = 0x00;
        }
        byte[] ipek = new byte[16];

        ipek[0] = (byte)0x54;
        ipek[1] = (byte)0xce;
        ipek[2] = (byte)0x79;
        ipek[3] = (byte)0x29;
        ipek[4] = (byte)0x7e;
        ipek[5] = (byte)0x4c;
        ipek[6] = (byte)0x54;
        ipek[7] = (byte)0xf8;
        ipek[8] = (byte)0x04;
        ipek[9] = (byte)0xb3;
        ipek[10] = (byte)0x0f;
        ipek[11] = (byte)0x3d;
        ipek[12] = (byte)0xc9;
        ipek[13] = (byte)0x5e;
        ipek[14] = (byte)0x38;
        ipek[15] = (byte)0xe1;
        byte[] checkval = new byte[1];
        int ret = -1;
        try {
            ret = mKey.updateKeyEx(Key.KEY_REQUEST_IPEK ,
                Key.KEY_PROTECT_ZERO ,
                CertData ,
                ipek ,
                false,
                0x00, checkval, "app1", 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if(ret == 0){
            mResultString = "IPEK import success";
            tvkeyexshow.setText(mResultString);
        }else {
            mResultString = "IPEK import fail";
            tvkeyexshow.setText(mResultString);
            return -1;
        }

        return 0;
    }

    private int injectKSN(){
        // Inject KSN
        byte[] ksn = new byte[10];

        ksn[0] = (byte)0x54;
        ksn[1] = (byte)0xce;
        ksn[2] = (byte)0x79;
        ksn[3] = (byte)0x29;
        ksn[4] = (byte)0x7e;
        ksn[5] = (byte)0x4c;
        ksn[6] = (byte)0x54;
        ksn[7] = (byte)0xf8;
        ksn[8] = (byte)0x04;
        ksn[9] = (byte)0xb3;
        int ret = -1;
        try {
            ret = mKey.InjectIKSN("app1", ksn.length, ksn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if(ret == 0){
            mResultString += "\n"+"InjectIKSN success";
            tvkeyexshow.setText(mResultString);
        }else {
            mResultString += "\n"+"InjectIKSN failed";
            tvkeyexshow.setText(mResultString);
            return -1;
        }

        return 0;
    }

    private int IncreaseKSN(){
        int ret = -1;
        try {
            ret = mKey.IncreaseKSN("app1");
            if(ret==0){
                mResultString += "\n"+"Increase KSN success";
            } else {
                mResultString += "\n"+"Increase KSN failed";
            }
            mHandler.sendEmptyMessage(UPDATE_TEXTVIEW);
        } catch (RemoteException ex){
            ex.printStackTrace();
        }
        return ret;
    }

    private int GetKSN(){
        int ret = -1;
        try {
            byte[] outData = new byte[16];
            int[] outDataLen = new int[1];
            ret = mKey.GetKSN("app1", outData, outDataLen);
            if(ret==0){
                Log.d(TAG,"GetKSN=="+ ByteUtil.bytes2HexString(outData)+"\noutDataLen=="+outDataLen[0]);
                mResultString += "\n"+"Get KSN success";
            } else {
                mResultString += "\n"+"Get KSN failed";
            }
            mHandler.sendEmptyMessage(UPDATE_TEXTVIEW);
        } catch (RemoteException ex){
            ex.printStackTrace();
        }
        return ret;
    }

    private int EnDecrypt_getMAC(){
        int len = 16;
        int dataLen = 0;
        byte[] data;
        dataLen = len;
        data = new byte[len];

        data[0] = (byte)0x05;
        data[1] = (byte)0x06;
        data[2] = (byte)0x07;
        data[3] = (byte)0x08;
        data[4] = (byte)0x01;
        data[5] = (byte)0x02;
        data[6] = (byte)0x05;
        data[7] = (byte)0x05;
        data[8] = (byte)0x05;
        data[9] = (byte)0x06;
        data[10] = (byte)0x07;
        data[11] = (byte)0x08;
        data[12] = (byte)0x01;
        data[13] = (byte)0x02;
        data[14] = (byte)0x05;
        data[15] = (byte)0x05;

        int encryptmode = 1;
        byte[] vectordata = new byte[8];
        int vectorLen = 8;
        byte[] pbOutdata = new byte[100];
        int[] pbOutdataLen = new int[50];
        int ret = -1;
        try {
            //en_decrypt
            ret = mCore.dataEnDecryptForIPEK(Core.ALGORITHM_3DES,
                    Core.ENCRYPT_MODE,
                    "app1",
                    encryptmode,
                    vectorLen,
                    vectordata,
                    dataLen,
                    data,
                    0x00,
                    pbOutdata,
                    pbOutdataLen);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(ret==0){
            Log.d(TAG,"EnDecrypt=="+ ByteUtil.bytes2HexString(pbOutdata)+"\npbOutdataLen=="+pbOutdataLen[0]);
            mResultString += "\n"+"EnDecrypt success";
        } else {
            mResultString += "\n"+"EnDecrypt failed";
        }

        String mac = "1234567890123456";
        byte[] macByte = ByteUtil.hexString2Bytes(mac);
        byte[] Outdata = new byte[100];
        int[] OutdataLen = new int[1];
        try {
            //get MAC
            ret = mCore.getMacForIPEK("app1",Core.ALGORITHM_3DES,vectorLen,vectordata,macByte.length,macByte,0x02,Outdata,OutdataLen);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(ret==0){
            Log.d(TAG,"getMac=="+ ByteUtil.bytes2HexString(Outdata)+"\nOutdataLen=="+OutdataLen[0]);
            mResultString += "\n"+"getMac success";
        } else {
            mResultString += "\n"+"getMac failed";
        }
        mHandler.sendEmptyMessage(UPDATE_TEXTVIEW);
        return ret;
    }
}
