package com.wpos.sdkdemo.key;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wpos.sdkdemo.R;
import com.wpos.sdkdemo.util.ByteUtil;

import wangpos.sdk4.libkeymanagerbinder.Key;

public class KeyEx extends AppCompatActivity {
    Button btnkeyexstart , btnkeyexquit;
    TextView tvkeyexshow;
    private Key mKey;

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
            }
        }.start();

        btnkeyexstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originalImportKey_3();
//                originalImportKey_2();
//                importKeyWithAlgorithm();
            }
        });

        btnkeyexquit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**originalImportKey
     * The default is 3DES algorithm import
     * Three - level key structure(TMK have protect key[TLK],is ciphertext)
     */
    private void originalImportKey_3 () {
        byte[] CertData = new byte[8];//Reserved.
        for(int i = 0 ; i < 8 ;i ++){
            CertData[i] = 0x00;
        }
        int ret = -1;
        //TLK
        String TLK = "11111111111111111111111111111111";
        byte[] key = ByteUtil.hexString2Bytes(TLK);
        byte[] checkval = new byte[1];//TLK no check
        try {
            ret = mKey.updateKeyEx(Key.KEY_REQUEST_TLK ,
                Key.KEY_PROTECT_ZERO ,
                CertData ,
                key ,
                false,
                checkval.length, checkval, "app1", 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(ret == 0){
            ((TextView) findViewById(R.id.textViewresult)).setText("TLK import success");
        }else {
            ((TextView) findViewById(R.id.textViewresult)).setText("TLK import fail");
            return;
        }
        //TMK
        String TMK = "87432C07DA6BC82DCB48C1168061F6FE";
        String tmkCheckVal = "C8F7C5A8";//length can change
        key = ByteUtil.hexString2Bytes(TMK);
        checkval = ByteUtil.hexString2Bytes(tmkCheckVal);
        try {
            ret = mKey.updateKeyEx(Key.KEY_REQUEST_TMK ,
                Key.KEY_PROTECT_TLK ,
                CertData ,
                key ,
                true,
                checkval.length, checkval, "app1", 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(ret == 0){
            tvkeyexshow.setText("TMK import success");
        }else {
            tvkeyexshow.setText("TMK import fail");
            return;
        }
        //DEK
        String DEK = "5CCFD5353C42FFC7F64F92D112575212";
        String dekCheckVal = "795319D9";
        key = ByteUtil.hexString2Bytes(DEK);
        checkval = ByteUtil.hexString2Bytes(dekCheckVal);
        try {
            ret = mKey.updateKeyEx(Key.KEY_REQUEST_DEK ,
                Key.KEY_PROTECT_TMK ,
                CertData ,
                key ,
                true,
                checkval.length, checkval, "app1", 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(ret == 0){
            ((TextView) findViewById(R.id.textViewdek)).setText("DEK import success");
        }else {
            ((TextView) findViewById(R.id.textViewdek)).setText("DEK import fail");
            return;
        }
        //DDEK
        String DDEK = "5CCFD5353C42FFC7F64F92D112575212";
        String ddekCheckVal = "795319D9";
        key = ByteUtil.hexString2Bytes(DDEK);
        checkval = ByteUtil.hexString2Bytes(ddekCheckVal);
        try {
            ret = mKey.updateKeyEx(Key.KEY_REQUEST_DDEK ,
                Key.KEY_PROTECT_TMK ,
                CertData,
                key ,
                true,
                checkval.length, checkval, "app1", 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(ret == 0){
            ((TextView) findViewById(R.id.textViewddek)).setText("DDEK import success");
        }else {
            ((TextView) findViewById(R.id.textViewddek)).setText("DDEK import fail");
            return;
        }

        //PEK
        String PEK = "815C023BC84F16CCB8453CA21C808263";
        String pekCheckVal = "C35EF51E";
        key = ByteUtil.hexString2Bytes(PEK);
        checkval = ByteUtil.hexString2Bytes(pekCheckVal);
        try {
            ret = mKey.updateKeyEx(Key.KEY_REQUEST_PEK ,
                Key.KEY_PROTECT_TMK ,
                CertData ,
                key ,
                true,
                checkval.length, checkval, "app1", 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(ret == 0){
            ((TextView) findViewById(R.id.textViewpek)).setText("PEK import success");
        }else {
            ((TextView) findViewById(R.id.textViewpek)).setText("PEK import fail");
            return;
        }

        //MAK
        String MAK = "28EBDF2B72A32B15D7399E33B4C3876B";
        String makCheckVal = "949ED390";
        key = ByteUtil.hexString2Bytes(MAK);
        checkval = ByteUtil.hexString2Bytes(makCheckVal);
        try {
            ret = mKey.updateKeyEx(Key.KEY_REQUEST_MAK ,
                    Key.KEY_PROTECT_TMK ,
                    CertData,
                    key ,
                    true,
                    checkval.length, checkval, "app1", 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(ret == 0){
            ((TextView) findViewById(R.id.textViewmak)).setText("MAK import success");
        }else {
            ((TextView) findViewById(R.id.textViewmak)).setText("MAK import fail");
        }
    }

    /**originalImportKey
     * The default is 3DES algorithm import
     * Two - level key structure(TMK don`t have protect key,is Plaintext)
     */
    private void originalImportKey_2() {
        byte[] CertData = new byte[8];//Reserved.
        for(int i = 0 ; i < 8 ;i ++){
            CertData[i] = 0x00;
        }
        // TMK
        String TMK = "BA6710D04A625D389D46024525FB7F10";
        byte[] key = ByteUtil.hexString2Bytes(TMK);
        byte[] checkval = new byte[1];
        int ret = -1;
        try {
            ret = mKey.updateKeyEx(Key.KEY_REQUEST_TMK ,
                    Key.KEY_PROTECT_ZERO ,
                    CertData ,
                    key ,
                    false,
                    checkval.length, checkval, "app2", 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if(ret == 0){
            tvkeyexshow.setText("TMK import success");
        }else {
            tvkeyexshow.setText("TMK import fail");
        }

        //DEK
        String DEK = "5CCFD5353C42FFC7F64F92D112575212";
        String dekCheckVal = "795319D9";
        key = ByteUtil.hexString2Bytes(DEK);
        checkval = ByteUtil.hexString2Bytes(dekCheckVal);
        try {
            ret = mKey.updateKeyEx(Key.KEY_REQUEST_DEK ,
                    Key.KEY_PROTECT_TMK ,
                    CertData ,
                    key ,
                    true,
                    checkval.length, checkval, "app2", 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(ret == 0){
            ((TextView) findViewById(R.id.textViewdek)).setText("DEK import success");
        }else {
            ((TextView) findViewById(R.id.textViewdek)).setText("DEK import fail");
        }
        //DDEK、PEK、MAK(operation as above)

    }

    /**
     * importKeyWithAlgorithm
     * can choose algorithm type import
     */
    private void importKeyWithAlgorithm() {
        String TAG = "importKeyWithAlgorithm";
        String packageName = "app3";
        int algorithm_3des = 0x00;//00:3DES 01:AES 02:SM4 03:DES {note:The selected algorithm type must match the algorithm type of the KeyEx}
        int encryptionMode = 0x01; // 0x01 ECB/ 0x02 CBC
        int paddingMode = 0x00; //0x00: MODE NONe 0x01: MODE 9797 0x02: MODE 2

        byte[] CertData = new byte[8];//Reserved.
        int ret = -1;

        try {
            //TLK
            String TLK = "11111111111111111111111111111111";//(note:if key is Two - level structure,don`t need TLK and TMK is Plaintext)
            byte[] key = ByteUtil.hexString2Bytes(TLK);
            byte[] checkval = new byte[1];//TLK no check
            ret = mKey.updateKeyWithAlgorithm(Key.KEY_REQUEST_TLK,
                    algorithm_3des,
                    Key.KEY_PROTECT_ZERO,
                    CertData,
                    key,
                    false,
                    checkval.length, checkval, packageName, 1);
            Log.d(TAG,"TLK=="+ret);
            //TMK
            String TMK = "87432C07DA6BC82DCB48C1168061F6FE";
            String tmkCheckVal = "C8F7C5A8";//length can change
            key = ByteUtil.hexString2Bytes(TMK);
            checkval = ByteUtil.hexString2Bytes(tmkCheckVal);
            ret = mKey.updateKeyWithAlgorithm(Key.KEY_REQUEST_TMK,
                    algorithm_3des,
                    Key.KEY_PROTECT_TLK,
                    CertData,
                    key,
                    true,
                    checkval.length, checkval,packageName, 1);
            Log.d(TAG,"TMK=="+ret);
            //DEK
            String DEK = "5CCFD5353C42FFC7F64F92D112575212";
            String dekCheckVal = "795319D9";
            key = ByteUtil.hexString2Bytes(DEK);
            checkval = ByteUtil.hexString2Bytes(dekCheckVal);
            ret = mKey.updateKeyWithAlgorithm(Key.KEY_REQUEST_DEK,
                    algorithm_3des,
                    Key.KEY_PROTECT_TMK,
                    CertData,
                    key,
                    true,
                    checkval.length, checkval, packageName, 1);
            Log.d(TAG,"DEK=="+ret);
            // DDEK
            String DDEK = "5CCFD5353C42FFC7F64F92D112575212";
            String ddekCheckVal = "795319D9";
            key = ByteUtil.hexString2Bytes(DDEK);
            checkval = ByteUtil.hexString2Bytes(ddekCheckVal);
            ret = mKey.updateKeyWithAlgorithm(Key.KEY_REQUEST_DDEK,
                    algorithm_3des,
                    Key.KEY_PROTECT_TMK,
                    CertData,
                    key,
                    true,
                    checkval.length, checkval, packageName, 1);
            Log.d(TAG,"DDEK=="+ret);
            // PEK
            String PEK = "815C023BC84F16CCB8453CA21C808263";
            String pekCheckVal = "C35EF51E";
            key = ByteUtil.hexString2Bytes(PEK);
            checkval = ByteUtil.hexString2Bytes(pekCheckVal);
            ret = mKey.updateKeyWithAlgorithm(Key.KEY_REQUEST_PEK,
                    algorithm_3des,
                    Key.KEY_PROTECT_TMK,
                    CertData,
                    key,
                    true,
                    checkval.length, checkval, packageName, 1);
            Log.d(TAG,"PEK=="+ret);
            //MAK
            String MAK = "28EBDF2B72A32B15D7399E33B4C3876B";
            String makCheckVal = "949ED390";
            key = ByteUtil.hexString2Bytes(MAK);
            checkval = ByteUtil.hexString2Bytes(makCheckVal);
            ret = mKey.updateKeyWithAlgorithm(Key.KEY_REQUEST_MAK,
                    algorithm_3des,
                    Key.KEY_PROTECT_TMK,
                    CertData,
                    key,
                    true,
                    checkval.length, checkval, packageName, 1);
            Log.d(TAG,"MAK=="+ret);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
