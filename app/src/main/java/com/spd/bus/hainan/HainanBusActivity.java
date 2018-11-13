package com.spd.bus.hainan;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spd.bus.DataConversionUtils;
import com.spd.bus.R;
import com.spd.bus.spdata.utils.PlaySound;

import java.util.Arrays;

import wangpos.sdk4.libbasebinder.BankCard;

public class HainanBusActivity extends AppCompatActivity implements View.OnClickListener {
    private BankCard mBankCard = null;
    private byte[] resultBytes = new byte[512];
    private int[] resultLen = new int[1];
    private int resultValue = -1;
    /**
     * APDU正确结果值
     */
    private final byte[] APDU_RESULT_SUCCESS = {(byte) 0x90, 0x00};
    private String TAG = "hainandemo";
    private final byte[] SELECT_MENU = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0F, 0x4D, 0x4F, 0x54, 0x2E, 0x49, 0x4E, 0x54, 0x45, 0x52, 0x43, 0x49, 0x54, 0x59, 0x30, 0x31};
    private final byte[] READ_FILE17 = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x00};
    private final byte[] READ_FILE14 = {0x00, (byte) 0xB0, (byte) 0x94, 0x00, 0x00};
    /**
     * 开始
     */
    private Button mBtnStart;
    private TextView mTvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hainan_bus);
        initView();
        initDev();
    }

    private void initDev() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                mBankCard = new BankCard(getApplicationContext());
            }
        }).start();

    }

    private void initView() {
        mBtnStart = findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(this);
        mTvShow = findViewById(R.id.tv_show);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_start:

                try {
                    resultValue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC, 30, resultBytes, resultLen, "hainan");
                    if (resultValue != 0) {
                        Log.e(TAG, "读卡失败");
                        return;
                    }
                    if (resultBytes[0] == (byte) 0x07) {
                        /*检测到非接IC卡*/

                    } else if (resultBytes[0] == (byte) 0x37) {
                        /*检测到 M1-S50 卡*/

                    } else if (resultBytes[0] == (byte) 0x47) {
                        /* 检测到 M1-S70 卡*/
                    }

                    resultValue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, SELECT_MENU, SELECT_MENU.length, resultBytes, resultLen);
                    if (resultValue != 0) {
                        Log.e(TAG, "切换目录（00A4）失败");
                    }
                    if (!Arrays.equals(APDU_RESULT_SUCCESS, DataConversionUtils.cutBytes(resultBytes, resultLen[0] - 2, 2))) {
                        Log.e(TAG, "切换目录（00A4）非9000===" + DataConversionUtils.cutBytes(resultBytes, resultLen[0] - 2, 2));
                        return;
                    }
                    byte[] result = DataConversionUtils.cutBytes(resultBytes, 0, resultLen[0] - 2);
                    Log.i(TAG, "切换目录（00A4）===" + DataConversionUtils.byteArrayToString(result));


                    resultValue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, READ_FILE17, READ_FILE17.length, resultBytes, resultLen);
                    if (resultValue != 0) {
                        Log.e(TAG, "读17（00B0）失败");
                    }
                    if (!Arrays.equals(APDU_RESULT_SUCCESS, DataConversionUtils.cutBytes(resultBytes, resultLen[0] - 2, 2))) {
                        Log.e(TAG, "读17（00B0）非9000===" + DataConversionUtils.cutBytes(resultBytes, resultLen[0] - 2, 2));
                        return;
                    }
                    result = DataConversionUtils.cutBytes(resultBytes, 0, resultLen[0] - 2);
                    Log.i(TAG, "读17（00B0）===" + DataConversionUtils.byteArrayToString(result));
                    switch (result[8]) {
                        case (byte) 0xF1:
                            //司机卡
                            result = readCard14File();
                            if (result == null) {
                                return;
                            }
                            String driverID = DataConversionUtils.byteArrayToString(DataConversionUtils.cutBytes(result, 0, 16));
                            String runCode = DataConversionUtils.byteArrayToString(DataConversionUtils.cutBytes(result, 16, 24));
                            mTvShow.setText("司机ID：" + driverID + "\n运营公司代码：" + runCode);
                            break;
                        case (byte) 0xF2:
                            //售票员卡
                            result = readCard14File();
                            if (result == null) {
                                return;
                            }
                            driverID = DataConversionUtils.byteArrayToString(DataConversionUtils.cutBytes(result, 0, 16));
                            runCode = DataConversionUtils.byteArrayToString(DataConversionUtils.cutBytes(result, 16, 24));
                            mTvShow.setText("售票员ID：" + driverID + "\n运营公司代码：" + runCode);
                            break;
                        case (byte) 0xFF:
                            //线路票价卡
                            result = readCard14File();
                            if (result == null) {
                                return;
                            }
                            String lineNumber = DataConversionUtils.byteArrayToString(DataConversionUtils.cutBytes(result, 0, 3));
                            runCode = DataConversionUtils.byteArrayToString(DataConversionUtils.cutBytes(result, 3, 11));
                            mTvShow.setText("线路编号：" + lineNumber + "\n运营公司代码：" + runCode);
                            break;
                        default:
                            break;
                    }


                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private byte[] readCard14File() {
        try {
            resultValue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, READ_FILE14, READ_FILE14.length, resultBytes, resultLen);
            if (resultValue != 0) {
                Log.e(TAG, "读14（00B0）失败");
                return null;
            }
            if (!Arrays.equals(APDU_RESULT_SUCCESS, DataConversionUtils.cutBytes(resultBytes, resultLen[0] - 2, 2))) {
                Log.e(TAG, "读14（00B0）非9000===" + DataConversionUtils.cutBytes(resultBytes, resultLen[0] - 2, 2));
                return null;
            }
            byte[] result = DataConversionUtils.cutBytes(resultBytes, 0, resultLen[0] - 2);
            Log.i(TAG, "读14（00B0）===" + DataConversionUtils.byteArrayToString(result));
            return result;
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }
}
