package com.spd.bus.spdata.setbusnr;


import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.spd.bus.R;
import com.spd.bus.spdata.PsamIcActivity;
import com.spd.bus.spdata.mvp.MVPBaseActivity;
import com.spd.bus.sql.SqlStatement;
import com.spd.bus.util.Configurations;
import com.spd.bus.util.CreateJsonConfig;
import com.spd.bus.util.DatabaseTabInfo;
import com.spd.bus.util.PlaySound;

import java.util.ArrayList;
import java.util.List;

import static com.spd.bus.MyApplication.FILENAME_INFO;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SetBusNrActivity extends MVPBaseActivity<SetBusNrContract.View, SetBusNrPresenter> implements SetBusNrContract.View {

    private TextView mEtNo1;
    private TextView mEtNo2;
    private TextView mEtNo3;
    private TextView mEtNo4;
    private TextView mEtNo5;
    private TextView mEtNo6;
    /**
     * 当前选中的输入框
     */
    private int selectNum = 0;
    private TextView[] editTexts;
    private boolean setKey = true;
    private String busNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_bus);
        initView();
        initData();
    }

    private void initData() {
        editTexts = new TextView[]{mEtNo1, mEtNo2, mEtNo3, mEtNo4, mEtNo5, mEtNo6};
        mEtNo1.setTextColor(getResources().getColor(R.color.red));
        DatabaseTabInfo.getIntence("info");
        busNo = DatabaseTabInfo.busno;
        if (!TextUtils.isEmpty(busNo)) {
            if (busNo.length() == 6) {
                mEtNo1.setText(busNo.substring(0, 1));
                mEtNo2.setText(busNo.substring(1, 2));
                mEtNo3.setText(busNo.substring(2, 3));
                mEtNo4.setText(busNo.substring(3, 4));
                mEtNo5.setText(busNo.substring(4, 5));
                mEtNo6.setText(busNo.substring(5, 6));
            }
        }

        new SetThread().start();
    }


    class SetThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (setKey) {
                String resultCard = com.yht.q6jni.Jni.Rfidcard();
                if (resultCard.length() > 1) {
                    if ("00".equalsIgnoreCase(resultCard.substring(0, 2))) {
                        handlerSet.sendMessage(handlerSet.obtainMessage(2, resultCard));
                    }
                    if ("09".equalsIgnoreCase(resultCard.substring(0, 2)) && "90".equals(resultCard.substring(2, 4))) {
                        //快捷下载apk
//                        setKey = false;
//                        startActivity(intent.setClass(SettingActivity.this, DownloadApk.class));
                    }
                }
            }
        }
    }

    Handler handlerSet = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    break;
                case 2:
                    if ("91".equals(msg.obj.toString().substring(2, 4))) {
                        setKey = false;
                        String buf = new StringBuffer().append(mEtNo1.getText().toString().trim())
                                .append(mEtNo2.getText().toString().trim())
                                .append(mEtNo3.getText().toString().trim())
                                .append(mEtNo4.getText().toString().trim())
                                .append(mEtNo5.getText().toString().trim())
                                .append(mEtNo6.getText().toString().trim()).toString();
                        SqlStatement.updateBusNo(buf);
                        com.yht.q6jni.Jni.ReadDeviceInfo();
                        if (!busNo.equals(buf)) {
                            SharedXmlUtil.getInstance(getApplicationContext())
                                    .write("TAGS", "");
                        }
                        PlaySound.play(PlaySound.setSuccess, 0);
                        handlerSet.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(SetBusNrActivity.this
                                        , PsamIcActivity.class));
                                finish();
                            }
                        }, 1000);

                        //备份新修改的参数信息Info
                        if ("00".equalsIgnoreCase(DatabaseTabInfo.info)) {
                            return;
                        }
                        boolean resultWriteInfo = Configurations.writlog(CreateJsonConfig.jsonInfo(buf
                                , DatabaseTabInfo.deviceNo
                                , DatabaseTabInfo.price
                                , DatabaseTabInfo.info)
                                , FILENAME_INFO);


                    } else if ("90".equals(msg.obj.toString().substring(2, 4))) {
//                        setKey = false;
//                        startActivity(intent.setClass(SettingActivity.this, DownloadApk.class));
//                        finish();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                editTexts[selectNum].setTextColor(getResources().getColor(R.color.black));
                selectNum = selectNum + 1;
                if (selectNum > 5) {
                    selectNum = selectNum - 6;
                }

                editTexts[selectNum].setTextColor(getResources().getColor(R.color.red));
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                int num = Integer.parseInt(editTexts[selectNum].getText().toString());
                num++;
                if (num > 9) {
                    num = num - 10;
                }
                editTexts[selectNum].setText(String.valueOf(num));
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                int num = Integer.parseInt(editTexts[selectNum].getText().toString());
                num--;
                if (num < 0) {
                    num = num + 10;
                }
                editTexts[selectNum].setText(String.valueOf(num));
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        mEtNo1 = findViewById(R.id.et_no1);
        mEtNo2 = findViewById(R.id.et_no2);
        mEtNo3 = findViewById(R.id.et_no3);
        mEtNo4 = findViewById(R.id.et_no4);
        mEtNo5 = findViewById(R.id.et_no5);
        mEtNo6 = findViewById(R.id.et_no6);
    }
}
