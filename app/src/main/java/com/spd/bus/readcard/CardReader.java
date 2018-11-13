package com.spd.bus.readcard;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.spd.bus.R;
import com.spd.bus.util.ByteUtil;
import com.spd.bus.util.keyrandom;

import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libbasebinder.HEX;


public class CardReader extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CardReader";
    private static final String WANG_POS_TAB = "WPOS-TAB";
    private static final String WANG_POS_MIN = "WPOS-MINI";
    private TextView tvcardreadershow;
    private Button mBtnPicc;
    private Button mBtnIc;
    private Button mBtnMag;
    private Button mBtnPsam1;
    private Button mBtnPsam2;
    private Button buttonM0;
    private Button buttonM1;
    private Button mBtnAT24;
    private Button button4428;
    private Button button4442;
    private Button buttonDesFire;
    private Button mBtnStop;
    private Button mBtnExit;

    private Core mCore;
    private BankCard mBankCard;

    private static final int PICC = 1;
    private static final int IC = 2;
    private static final int MAG = 3;
    private static final int PSAM1 = 4;
    private static final int PSAM2 = 5;
    private static final int AT24 = 6;
    private static final int M0 = 7;
    private static final int M1 = 8;
    private static final int C4428 = 9;
    private static final int C4442 = 10;
    private static final int DESFIRE = 11;

    private Button[] mBtnArray;
    private StringBuilder stringBuilder = null;

    private boolean mDetect = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mThread = null;
            switch (msg.what) {
                case PICC:
                    if (msg.arg1 == 0) {
                        tvcardreadershow.setText("PICC read card and send apdu success");
                    } else if (msg.arg1 == 3) {
                        tvcardreadershow.setText("time out");
                    } else if (msg.arg1 == 4) {
                        Toast.makeText(CardReader.this, "cancel readcard", Toast.LENGTH_SHORT).show();
                    } else {
                        tvcardreadershow.setText("PICC fail");
                    }
                    break;
                case IC:
                    if (msg.arg1 == 0) {
                        tvcardreadershow.setText("IC read card and send apdu success");
                    } else if (msg.arg1 == 3) {
                        tvcardreadershow.setText("time out");
                    } else if (msg.arg1 == 4) {
                        Toast.makeText(CardReader.this, "cancel readcard", Toast.LENGTH_SHORT).show();
                    } else {
                        tvcardreadershow.setText("IC error");
                    }
                    break;
                case MAG:
                    if (msg.arg1 == 0) {
                        tvcardreadershow.setText("MAG Test success");
                    } else if (msg.arg1 == 3) {
                        tvcardreadershow.setText("time out");
                    } else if (msg.arg1 == 4) {
                        Toast.makeText(CardReader.this, "cancel readcard", Toast.LENGTH_SHORT).show();
                    } else {
                        tvcardreadershow.setText("MAG Test error");
                    }
                    break;
                case PSAM1:
                    if (msg.arg1 == 0) {
                        tvcardreadershow.setText("PSAM1 send apdu success");
                    } else if (msg.arg1 == 3) {
                        tvcardreadershow.setText("time out");
                    } else {
                        tvcardreadershow.setText("PSAM1 error");
                    }
                    break;
                case PSAM2:
                    if (msg.arg1 == 0) {
                        tvcardreadershow.setText("PSAM2 send apdu success");
                    } else if (msg.arg1 == 3) {
                        tvcardreadershow.setText("time out");
                    } else {
                        tvcardreadershow.setText("PSAM2 fail");
                    }
                    break;
                case AT24:
                    Log.e("zys", "mAT24 = " + mAT24);
                    if (msg.arg1 == 0) {
                        tvcardreadershow.setText(mAT24);
                    } else if (msg.arg1 == 3) {
                        tvcardreadershow.setText("time out");
                    } else {
                        tvcardreadershow.setText("AT24CXX read fail");
                    }
                    break;
                case M0:
                    if (msg.arg1 == 0) {
                        tvcardreadershow.setText(stringBuilder.toString());
                    } else if (msg.arg1 == 3) {
                        tvcardreadershow.setText("time out");
                    } else {
                        tvcardreadershow.setText(stringBuilder.toString());
                    }
                    break;
                case M1:
                    if (msg.arg1 == 0) {
                        tvcardreadershow.setText(stringBuilder.toString());
                    } else if (msg.arg1 == 3) {
                        tvcardreadershow.setText("time out");
                    } else {
                        tvcardreadershow.setText(stringBuilder.toString());
                    }
                    break;

                case C4428:
                    if (msg.arg1 == 0) {
                        tvcardreadershow.setText("4428Card read/Write card and Verify success");
                    } else if (msg.arg1 == 3) {
                        tvcardreadershow.setText("time out");
                    } else {
                        tvcardreadershow.setText("4428Card error");
                    }
                    break;
                case C4442:
                    if (msg.arg1 == 0) {
                        tvcardreadershow.setText("4442Card read/Write card and Verify success");
                    } else if (msg.arg1 == 3) {
                        tvcardreadershow.setText("time out");
                    } else {
                        tvcardreadershow.setText("4442Card error");
                    }
                    break;
                case DESFIRE:
                    if (msg.arg1 == 0) {
                        tvcardreadershow.setText(stringBuilder.toString());
                    } else if (msg.arg1 == 3) {
                        tvcardreadershow.setText("time out");
                    } else {
                        tvcardreadershow.setText(stringBuilder.toString());
                    }
                    break;
            }
            refreshButton(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);


        new Thread() {
            @Override
            public void run() {
                mBankCard = new BankCard(getApplicationContext());
                mCore = new Core(getApplicationContext());


            }
        }.start();

        tvcardreadershow = (TextView) findViewById(R.id.cardtextview);
        mBtnPicc = (Button) findViewById(R.id.card);
        mBtnIc = (Button) findViewById(R.id.iccard);
        mBtnMag = (Button) findViewById(R.id.mag);
        mBtnPsam1 = (Button) findViewById(R.id.buttonpsm1);
        mBtnPsam2 = (Button) findViewById(R.id.buttonpsm2);
        buttonM0 = (Button) findViewById(R.id.buttonM0);
        buttonM1 = (Button) findViewById(R.id.buttonM1);
        mBtnAT24 = (Button) findViewById(R.id.atx24);
        button4428 = (Button) findViewById(R.id.button4428);
        button4442 = (Button) findViewById(R.id.button4442);
        buttonDesFire = (Button) findViewById(R.id.buttonDesFire);
        mBtnExit = (Button) findViewById(R.id.exit);
        mBtnStop = (Button) findViewById(R.id.btn_stop);

        mBtnPicc.setOnClickListener(this);
        mBtnIc.setOnClickListener(this);
        mBtnMag.setOnClickListener(this);
        mBtnPsam1.setOnClickListener(this);
        mBtnPsam2.setOnClickListener(this);
        buttonM0.setOnClickListener(this);
        buttonM1.setOnClickListener(this);
        mBtnAT24.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        button4428.setOnClickListener(this);
        button4442.setOnClickListener(this);
        buttonDesFire.setOnClickListener(this);


        mBtnArray = new Button[]{mBtnPicc, mBtnIc, mBtnMag, mBtnPsam1, mBtnPsam2, buttonM0, buttonM1, mBtnAT24, button4428, button4442, buttonDesFire};
        refreshButton(false);
        setMBtnPsam2Visibility();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mBankCard.breakOffCommand();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Thread mThread = null;

    @Override
    public void onClick(View v) {
        tvcardreadershow.setText(R.string.ditips);
        stringBuilder = new StringBuilder();
        switch (v.getId()) {
            case R.id.card:
                if (mThread == null) {
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            mDetect = true;
                            piccCardTest();
                        }
                    };
                    mThread.start();
                }
                break;
            //IC 卡
            case R.id.iccard:
                if (mThread == null) {
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            mDetect = true;
                            icCardTest();
                        }
                    };
                    mThread.start();
                }
                break;
            case R.id.mag:
                if (mThread == null) {
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            mDetect = true;
                            magTest();
                        }
                    };
                    mThread.start();
                }
                break;
            case R.id.buttonpsm1:
                if (mThread == null) {
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            mDetect = true;
                            psam1();
                        }
                    };
                    mThread.start();
                }
                break;
            case R.id.buttonpsm2:
                if (mThread == null) {
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            mDetect = true;
                            psam2();
                        }
                    };
                    mThread.start();
                }
                break;
            case R.id.buttonM0:
                if (mThread == null) {
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            mDetect = true;
                            M0();
                        }
                    };
                    mThread.start();
                }
                break;
            case R.id.buttonM1:
                if (mThread == null) {
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            mDetect = true;
                            M1();
                        }
                    };
                    mThread.start();
                }
                break;
            case R.id.atx24:
                if (mThread == null) {
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            mDetect = true;
                            at24CardTest();
                        }
                    };
                    mThread.start();
                }
                break;
            case R.id.button4428:
                if (mThread == null) {
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            mDetect = true;
                            Card4428Test();
                        }
                    };
                    mThread.start();
                }
                break;
            case R.id.button4442:
                if (mThread == null) {
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            mDetect = true;
                            Card4442Test();
                        }
                    };
                    mThread.start();
                }
                break;
            case R.id.buttonDesFire:
                if (mThread == null) {
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            mDetect = true;
                            desFireTest();
                        }
                    };
                    mThread.start();
                }
                break;
            case R.id.btn_stop:
                try {
                    mDetect = false;
                    mThread.interrupt();
                    mThread = null;
                    mBankCard.breakOffCommand();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                tvcardreadershow.setText(R.string.test_please);
                break;
            case R.id.exit:
                finish();
                break;
        }

        if (v.getId() != R.id.exit) {
            if (v.getId() == R.id.btn_stop)
                refreshButton(false);
            else
                refreshButton(true);
        }
    }

    private void refreshButton(boolean isRead) {
        for (int i = 0; i < mBtnArray.length; i++) {
            mBtnArray[i].setEnabled(!isRead);
        }
        mBtnStop.setEnabled(isRead);
    }

    public void psam1() {
        //蜂鸣器
        try {
            mCore.buzzer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        byte[] respdata = new byte[100];
        int[] resplen = new int[1];
        int retvalue = -1;
        Log.v(TAG, "readcard");
        try {
            //打开psam
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PSAM1, 60, respdata, resplen, "app1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!mDetect) {
            // Don't do the other process because press stop button.
            return;
        }

        if (respdata[0] != 0x05) {
            Message msg = mHandler.obtainMessage(PSAM1);
            if (respdata[0] == 0x03) {
                msg.arg1 = 3;
            } else {
                msg.arg1 = 1;
            }
            mHandler.sendMessage(msg);
            return;
        }

        String s1 = keyrandom.bytesToHexString(respdata);
        Log.v(TAG, "" + s1);
        Log.v(TAG, "" + resplen);
        Log.v(TAG, "send apdu");
        byte[] sendapdu = new byte[13];
        sendapdu[0] = (byte) 0x00;
        sendapdu[1] = (byte) 0xa4;
        sendapdu[2] = (byte) 0x00;
        sendapdu[3] = (byte) 0x00;
        sendapdu[4] = (byte) 0x02;
        sendapdu[5] = (byte) 0x3f;
        sendapdu[6] = (byte) 0x01;
        Log.v(TAG, "" + keyrandom.bytesToHexString(sendapdu));
        byte[] resp = new byte[100];
        int retapde = -1;
        try {
            retapde = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, sendapdu, 7, resp, resplen);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "" + resplen);
        Log.v(TAG, "" + keyrandom.bytesToHexString(resp));
        Log.v(TAG, "ret" + retapde);
        Message msg = mHandler.obtainMessage(PSAM1);
        if (retapde == 0 && retvalue == 0) {
            Log.v(TAG, "\nret" + retapde);
            msg.arg1 = 0;
        } else {
            Log.v(TAG, "\nret" + retapde);
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }

    public void psam2() {
        //蜂鸣器
        try {
            mCore.buzzer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "bankcard readcard");
        byte[] respdata = new byte[255];
        int[] resplen = new int[1];
        int retvalue = 0;
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PSAM2, 60, respdata, resplen, "app1");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (!mDetect) {
            // Don't do the other process because press stop button.
            return;
        }

        if (respdata[0] != 0x05) {
            Message msg = mHandler.obtainMessage(PSAM2);
            if (respdata[0] == 0x03) {
                msg.arg1 = 3;
            } else {
                msg.arg1 = 1;
            }
            mHandler.sendMessage(msg);
            return;
        }

        String s1 = HEX.bytesToHex(respdata);
        Log.v(TAG, "" + retvalue);
        Log.v(TAG, "" + s1);
        Log.v(TAG, "" + resplen);

        Log.v(TAG, "send apdu");
        byte[] sendapdu = new byte[256];
        sendapdu[0] = (byte) 0x00;
        sendapdu[1] = (byte) 0xa4;
        sendapdu[2] = (byte) 0x00;
        sendapdu[3] = (byte) 0x00;
        sendapdu[4] = (byte) 0x02;
        sendapdu[5] = (byte) 0x3f;
        sendapdu[6] = (byte) 0x01;
        Log.v(TAG, "" + keyrandom.bytesToHexString(sendapdu));
        byte[] resp = new byte[256];
        int psm2apdu = -1;
        try {
            //BankCard.CARD_MODE_PSAM1_APDU
            psm2apdu = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, sendapdu, 7, resp, resplen);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "" + resplen);
        Log.v(TAG, "" + keyrandom.bytesToHexString(resp));
        Message msg = mHandler.obtainMessage(PSAM2);
        if (psm2apdu == 0) {
            msg.arg1 = 0;
        } else {
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }

    public void piccCardTest() {
        Log.v(TAG, "CardReader, piccCardTest");
        try {
            mCore.buzzer();
            Log.v(TAG, "CardReader, piccCardTest, buzzer--->>>");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        byte[] respdata = new byte[1024];
        int[] resplen = new int[1];
        int retvalue = -1;
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC, 60, respdata, resplen, "app1");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (respdata[0] == 0x03) {
            Message msg = mHandler.obtainMessage(PICC);
            msg.arg1 = 3;
            mHandler.sendMessage(msg);
            return;
        } else if (respdata[0] == 0x04) {
            Message msg = mHandler.obtainMessage(PICC);
            msg.arg1 = 4;
            mHandler.sendMessage(msg);
            return;
        }

        if (!mDetect) {
            // Don't do the other process because press stop button.
            return;
        }

        String s1 = HEX.bytesToHex(respdata);
        Log.v(TAG, "CardReader, piccCardTest, s1 = " + s1);
        Log.v(TAG, "CardReader, piccCardTest, resplen = " + resplen[0]);

        Log.v(TAG, "getcardsnfunction");
        byte[] outdata = new byte[512];
        int[] len = new int[1];
        try {
            mBankCard.getCardSNFunction(outdata, len);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "CardReader, piccCardTest, outdata = " + HEX.bytesToHex(outdata));
        Log.v(TAG, "CardReader, piccCardTest, len = " + len[0]);

        byte[] sendapdu = new byte[256];
        sendapdu[0] = (byte) 0x00;
        sendapdu[1] = (byte) 0xa4;
        sendapdu[2] = (byte) 0x00;
        sendapdu[3] = (byte) 0x00;
        sendapdu[4] = (byte) 0x02;
        sendapdu[5] = (byte) 0x3f;
        sendapdu[6] = (byte) 0x01;
        byte[] resp = new byte[256];
        int retpicc = -1;
        try {
            retpicc = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, sendapdu, 7, resp, resplen);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "CardReader, piccCardTest, resplen = " + resplen[0]);
        Log.v(TAG, "CardReader, piccCardTest, resp = " + HEX.bytesToHex(resp));

        boolean whileCondition = false;
        do {
            SystemClock.sleep(200);
            Log.v(TAG, "CardReader, piccCardTest, picc detecting--------------mDetect = " + mDetect);
            //picc 卡在位检测
            try {
                whileCondition = BankCard.CARD_DETECT_EXIST != mBankCard.piccDetect();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } while (whileCondition && mDetect);
        Message msg = mHandler.obtainMessage(PICC);
        if (retvalue == 0 && retpicc == 0) {
            msg.arg1 = 0;
        } else {
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }

    public void icCardTest() {
        Log.v(TAG, "CardReader, icCardTest");
        try {
            mCore.buzzer();
            Log.v(TAG, "CardReader, icCardTest, buzzer--->>>");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        byte[] respdata = new byte[256];
        int[] resplen = new int[1];
        int retvalue = -1;
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_ICC, 60, respdata, resplen, "app1");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (respdata[0] == 0x03) {
            Message msg = mHandler.obtainMessage(IC);
            msg.arg1 = 3;
            mHandler.sendMessage(msg);
            return;
        } else if (respdata[0] == 0x04) {
            Message msg = mHandler.obtainMessage(IC);
            msg.arg1 = 4;
            mHandler.sendMessage(msg);
            return;
        }
        if (!mDetect) {
            // Don't do the other process because press stop button.
            return;
        }
        Log.d(TAG, "CardReader, icCardTest, retvalue = " + retvalue);
//        String s1 = new String(respdata);
        Log.v(TAG, "CardReader, icCardTest, s1 = " + HEX.bytesToHex(respdata));
        Log.v(TAG, "CardReader, icCardTest, resplen = " + resplen[0]);

        byte[] sendapdu = new byte[13];
        sendapdu[0] = (byte) 0x00;
        sendapdu[1] = (byte) 0xa4;
        sendapdu[2] = (byte) 0x00;
        sendapdu[3] = (byte) 0x00;
        sendapdu[4] = (byte) 0x02;
        sendapdu[5] = (byte) 0x3f;
        sendapdu[6] = (byte) 0x01;
        Log.v(TAG, "CardReader, icCardTest, sendapdu = " + new String(sendapdu));
        byte[] resp = new byte[100];
        int apduret = -1;
        try {
            apduret = mBankCard.sendAPDU(BankCard.CARD_MODE_ICC, sendapdu, 7, resp, resplen);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "CardReader, icCardTest, apduret = " + apduret);
        Log.v(TAG, "CardReader, icCardTest, resplen = " + resplen);
        Log.v(TAG, "CardReader, icCardTest, resp = " + resp);

        boolean whileCondition = false;
        do {
            SystemClock.sleep(200);
            Log.v(TAG, "CardReader, icCardTest, detecting---------mDetect = " + mDetect);
            //IC 卡在位检测
            try {
                whileCondition = BankCard.CARD_DETECT_EXIST != mBankCard.iccDetect();
                Log.d(TAG, "-=-=-= whileCondition = " + whileCondition);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } while (whileCondition && mDetect);
        Message msg = mHandler.obtainMessage(IC);
        if (apduret == 0 && retvalue == 0) {
            msg.arg1 = 0;
        } else {
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }

    private String mAT24;

    public void at24CardTest() {
        Log.v(TAG, "CardReader, at24CardTest");
        try {
            mCore.buzzer();
            Log.v(TAG, "CardReader, at24CardTest, buzzer--->>>");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        byte[] respdata = new byte[100];
        int[] resplen = new int[1];
        int retvalue = -1;
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, 0x0140, 60, respdata, resplen, "app1");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (respdata[0] == 0x03) {
            Message msg = mHandler.obtainMessage(AT24);
            msg.arg1 = 3;
            mHandler.sendMessage(msg);
            return;
        } else if (respdata[0] == 0x04) {
            Message msg = mHandler.obtainMessage(IC);
            msg.arg1 = 4;
            mHandler.sendMessage(msg);
            return;
        }
        if (!mDetect) {
            // Don't do the other process because press stop button.
            return;
        }
        Log.d(TAG, "CardReader, icCardTest, retvalue = " + retvalue);
        Log.v(TAG, "CardReader, icCardTest, s1 = " + HEX.bytesToHex(respdata));
        Log.v(TAG, "CardReader, icCardTest, resplen = " + resplen[0]);

        Message msg = mHandler.obtainMessage(AT24);
        if (respdata[0] == 0x45 && respdata[1] == 0x01) {
            msg.arg1 = 0;
            switch (respdata[2]) {
                case 0x01:
                    mAT24 = "AT24C01";
                    break;
                case 0x02:
                    mAT24 = "AT24C02";
                    break;
                case 0x03:
                    mAT24 = "AT24C04";
                    break;
                case 0x04:
                    mAT24 = "AT24C08";
                    break;
                case 0x05:
                    mAT24 = "AT24C16";
                    break;
                case 0x06:
                    mAT24 = "AT24C32";
                    break;
                case 0x07:
                    mAT24 = "AT24C64";
                    break;
                case 0x08:
                    mAT24 = "AT24C128";
                    break;
                case 0x09:
                    mAT24 = "AT24C256";
                    break;
                case 0x0a:
                    mAT24 = "AT24C512";
                    break;
            }

            byte[] pwd = new byte[3];

            mAT24 += "\nWrite data is: ";
            String ori = "0102030405060708";
            mAT24 += "\n" + ori;
            byte[] data = new byte[8];
            data[0] = (byte) 0x01;
            data[1] = (byte) 0x02;
            data[2] = (byte) 0x03;
            data[3] = (byte) 0x04;
            data[4] = (byte) 0x05;
            data[5] = (byte) 0x06;
            data[6] = (byte) 0x07;
            data[7] = (byte) 0x08;

            try {
                mBankCard.WriteLogicCardData(pwd, 0, data.length, data);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }

            mAT24 += "\nRead Data is: ";

            byte[] outdata = new byte[8];
            int[] len = new int[1];

            try {
                mBankCard.ReadLogicCardData(0, 8, outdata, len);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            mAT24 += ByteUtil.bytes2HexString(outdata);
        } else {
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }

    private String mC4428;

    public void Card4428Test() {
        Log.v(TAG, "CardReader, Card4428Test");
        try {
            mCore.buzzer();
            Log.v(TAG, "CardReader, Card4428Test, buzzer--->>>");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        byte[] respdata = new byte[100];
        int[] resplen = new int[1];
        int retvalue = -1;
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, 320, 60, respdata, resplen, "app1");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (respdata[0] == 0x03) {
            Message msg = mHandler.obtainMessage(C4428);
            msg.arg1 = 3;
            mHandler.sendMessage(msg);
            return;
        } else if (respdata[0] == 0x04) {
            Message msg = mHandler.obtainMessage(IC);
            msg.arg1 = 4;
            mHandler.sendMessage(msg);
            return;
        }
        if (!mDetect) {
            // Don't do the other process because press stop button.
            return;
        }
        Log.d(TAG, "CardReader, icCardTest, retvalue = " + retvalue);
        Log.v(TAG, "CardReader, icCardTest, s1 = " + HEX.bytesToHex(respdata));
        Log.v(TAG, "CardReader, icCardTest, resplen = " + resplen[0]);

        Message msg = mHandler.obtainMessage(C4428);
        if (respdata[0] == 0x25) {
            msg.arg1 = 0;
            mC4428 += "4428 ";

            byte[] pwd = new byte[3];
            pwd[0] = (byte) 0xff;
            pwd[1] = (byte) 0xff;
            pwd[2] = (byte) 0xff;

            try {
                mBankCard.VerifyLogicCardPwd(pwd);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }

            byte[] data = new byte[8];

            mC4428 += "\nWrite data is: ";
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) i;
                mC4428 += data[i];

            }

            try {
                mBankCard.WriteLogicCardData(pwd, 0x0A, data.length, data);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }

            mC4428 += "\nRead Data is: ";

            byte[] outdata = new byte[16];
            int[] len = new int[1];

            try {
                mBankCard.ReadLogicCardData(0x0A, 8, outdata, len);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            mC4428 += HEX.bytesToHex(outdata);
            Log.v(TAG, mC4428);

            //4428卡 APDU Verify
            byte[] APDU_CMD = new byte[7];
            APDU_CMD[0] = (byte) 0xFF;
            APDU_CMD[1] = (byte) 0x20;
            APDU_CMD[2] = (byte) 0x00;
            APDU_CMD[3] = (byte) 0x00;
            APDU_CMD[4] = (byte) 0x02;
            APDU_CMD[5] = (byte) 0xFF;
            APDU_CMD[6] = (byte) 0xFF;
            Log.v(TAG, "CardReader, 4428 Verify, send APDU = " + ByteUtil.bytes2HexString(APDU_CMD));
            byte[] APDU_Result = new byte[2];
            int[] APDU_Result_Len = new int[1];
            int apduret = -1;
            try {
                apduret = mBankCard.sendAPDU(BankCard.CARD_MODE_ICC, APDU_CMD, APDU_CMD.length, APDU_Result, APDU_Result_Len);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "CardReader, 4428 Verify, apduret = " + apduret);
            Log.v(TAG, "CardReader, 4428 Verify, result data = " + ByteUtil.bytes2HexString(APDU_Result));
            Log.v(TAG, "CardReader, 4428 Verify, result data length = " + APDU_Result_Len[0]);
            //Read
            APDU_CMD = new byte[7];
            APDU_CMD[0] = (byte) 0xFF;
            APDU_CMD[1] = (byte) 0xB0;
            APDU_CMD[2] = (byte) 0x00;
            APDU_CMD[3] = (byte) 0x0A;
            APDU_CMD[4] = (byte) 0x08;
            Log.v(TAG, "CardReader, 4428 Read, send APDU = " + ByteUtil.bytes2HexString(APDU_CMD));
            APDU_Result = new byte[10];
            APDU_Result_Len = new int[1];
            apduret = -1;
            try {
                apduret = mBankCard.sendAPDU(BankCard.CARD_MODE_ICC, APDU_CMD, APDU_CMD.length, APDU_Result, APDU_Result_Len);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "CardReader, 4428 Read, apduret = " + apduret);
            Log.v(TAG, "CardReader, 4428 Read, result data = " + ByteUtil.bytes2HexString(APDU_Result));
            Log.v(TAG, "CardReader, 4428 Read, result data length = " + APDU_Result_Len[0]);

            //Whrite
            APDU_CMD = new byte[13];
            APDU_CMD[0] = (byte) 0xFF;
            APDU_CMD[1] = (byte) 0xD6;
            APDU_CMD[2] = (byte) 0x00;
            APDU_CMD[3] = (byte) 0x0A;
            APDU_CMD[4] = (byte) 0x08;
            APDU_CMD[5] = (byte) 0x01;
            APDU_CMD[6] = (byte) 0x02;
            APDU_CMD[7] = (byte) 0x03;
            APDU_CMD[8] = (byte) 0x04;
            APDU_CMD[9] = (byte) 0x05;
            APDU_CMD[10] = (byte) 0x06;
            APDU_CMD[11] = (byte) 0x07;
            APDU_CMD[12] = (byte) 0x08;
            Log.v(TAG, "CardReader, 4428 whrite, send APDU = " + ByteUtil.bytes2HexString(APDU_CMD));
            APDU_Result = new byte[2];
            APDU_Result_Len = new int[1];
            apduret = -1;
            try {
                apduret = mBankCard.sendAPDU(BankCard.CARD_MODE_ICC, APDU_CMD, APDU_CMD.length, APDU_Result, APDU_Result_Len);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "CardReader, 4428 whrite, apduret = " + apduret);
            Log.v(TAG, "CardReader, 4428 whrite, result data = " + ByteUtil.bytes2HexString(APDU_Result));
            Log.v(TAG, "CardReader, 4428 whrite, result data length = " + APDU_Result_Len[0]);
        } else {
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }

    private String mC4442;

    private void Card4442Test() {
        Log.v(TAG, "CardReader, Card4442Test");
        try {
            mCore.buzzer();
            Log.v(TAG, "CardReader, Card4442Test, buzzer--->>>");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        byte[] respdata = new byte[100];
        int[] resplen = new int[1];
        int retvalue = -1;
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, 320, 60, respdata, resplen, "app1");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (respdata[0] == 0x03) {
            Message msg = mHandler.obtainMessage(C4442);
            msg.arg1 = 3;
            mHandler.sendMessage(msg);
            return;
        } else if (respdata[0] == 0x04) {
            Message msg = mHandler.obtainMessage(IC);
            msg.arg1 = 4;
            mHandler.sendMessage(msg);
            return;
        }
        if (!mDetect) {
            // Don't do the other process because press stop button.
            return;
        }
        Log.d(TAG, "CardReader, icCardTest, retvalue = " + retvalue);
        Log.v(TAG, "CardReader, icCardTest, s1 = " + HEX.bytesToHex(respdata));
        Log.v(TAG, "CardReader, icCardTest, resplen = " + resplen[0]);

        Message msg = mHandler.obtainMessage(C4442);
        if (respdata[0] == 0x15) {
            msg.arg1 = 0;
            mC4442 += "4442 ";

            byte[] pwd = new byte[3];
            pwd[0] = (byte) 0xff;
            pwd[1] = (byte) 0xff;
            pwd[2] = (byte) 0xff;

            try {
                mBankCard.VerifyLogicCardPwd(pwd);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }

            byte[] data = new byte[8];

            mC4442 += "\nWrite data is: ";
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) i;
                mC4442 += data[i];

            }

            try {
                mBankCard.WriteLogicCardData(pwd, 0x0A, data.length, data);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }

            mC4442 += "\nRead Data is: ";

            byte[] outdata = new byte[8];
            int[] len = new int[1];

            try {
                mBankCard.ReadLogicCardData(0x0A, 8, outdata, len);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            mC4442 += HEX.bytesToHex(outdata);
            Log.v(TAG, mC4442);
        } else {
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }

    public void M0() {
        //蜂鸣器
        try {
            mCore.buzzer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "bankcard readcard");
        byte[] respdata = new byte[255];
        int[] resplen = new int[1];
        int retvalue = 0;
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC, 60, respdata, resplen, "app1");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (respdata[0] == 0x03) {
            Message msg = mHandler.obtainMessage(M0);
            msg.arg1 = 3;
            mHandler.sendMessage(msg);
            return;
        } else if (respdata[0] == 0x04) {
            Message msg = mHandler.obtainMessage(IC);
            msg.arg1 = 4;
            mHandler.sendMessage(msg);
            return;
        }
        if (!mDetect) {
            return;
        }
        Message msg = mHandler.obtainMessage(M0);
        if (retvalue == 0x00) {
            if (respdata[0] == 0x57) {
                stringBuilder.append("detecte M0 success\n");
                try {
//                    byte[] sn = new byte[16];
//                    int[] pes = new int[1];
//                    int resSn = mBankCard.getCardSNFunction(sn,pes);
//                    Log.v(TAG, "m1CardSNFunction, respes--->>>"+pes[0]);
//                    Log.v(TAG, "m1CardSNFunction, resSn--->>>"+HEX.bytesToHex(sn));
//
//                    byte[] res = new byte[pes[0]];
//                    System.arraycopy(sn,0,res,0,pes[0]);
//                    String result = HEX.bytesToHex(res);
//                    Log.d("xxx","获取ID"+result);

                    byte[] indata = new byte[4];
                    indata[0] = (byte) 0x00;
                    indata[1] = (byte) 0x01;
                    indata[2] = (byte) 0x02;
                    indata[3] = (byte) 0x03;
                    int resIn = mBankCard.NFCTagWriteBlock(0x0A, indata);
                    Log.v(TAG, "CardReader, resIn--->>>" + resIn);
                    if (resIn == 0) {
                        stringBuilder.append("M0 Write success\n");
                        byte[] outdata = new byte[20];
                        int[] oulen = new int[1];
                        int resOut = mBankCard.NFCTagReadBlock(0x0A, outdata, oulen);
                        if (resOut == 0) {
                            byte[] snData = new byte[oulen[0] - 1];
                            System.arraycopy(outdata, 1, snData, 0, oulen[0] - 1);
                            msg.arg1 = 0;
                            stringBuilder.append("M0 read success\n");
                            Log.v(TAG, "CardReader, M0--->>>" + HEX.bytesToHex(snData));
                        } else {
                            stringBuilder.append("M0 read fail\n");
                        }
                    } else {
                        stringBuilder.append("M0 write fail\n");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                stringBuilder.append("detecte M0 fail\n");
                msg.arg1 = 1;
            }
        } else {
            stringBuilder.append("detecte M0 fail\n");
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }

    public void M1() {
        //蜂鸣器
        try {
            mCore.buzzer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "bankcard readcard");
        byte[] respdata = new byte[255];
        int[] resplen = new int[1];
        int retvalue = 0;
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC, 60, respdata, resplen, "app1");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (respdata[0] == 0x03) {
            Message msg = mHandler.obtainMessage(M1);
            msg.arg1 = 3;
            mHandler.sendMessage(msg);
            return;
        } else if (respdata[0] == 0x04) {
            Message msg = mHandler.obtainMessage(IC);
            msg.arg1 = 4;
            mHandler.sendMessage(msg);
            return;
        }
        if (!mDetect) {
            return;
        }
        Message msg = mHandler.obtainMessage(M1);
        if (retvalue == 0x00) {
            Log.v(TAG, "m1CardSNFunction, respdata--->>>" + respdata[0]);
            if (respdata[0] == 0x37 || respdata[0] == 0x47) {//0x37:M1-S50卡;0x47:M1-S70卡
                try {
                    byte[] keyData = new byte[6];
                    for (int i = 0; i < keyData.length; i++) {
                        keyData[i] = (byte) 0xFF;
                    }
//                    byte[] keyData = {(byte) 0x82, (byte) 0xF0, (byte) 0xAC, (byte) 0xB4, 0x20,(byte) 0x8C};
                    byte[] sn = new byte[16];
                    int[] pes = new int[1];
                    int resSn = mBankCard.getCardSNFunction(sn, pes);
                    if (resSn == 0) {
                        stringBuilder.append("M1 CardSNFunction success\n");
                    } else {
                        stringBuilder.append("M1 CardSNFunction fail\n");
                    }
                    Log.v(TAG, "m1CardSNFunction, respes--->>>" + pes[0]);
                    Log.v(TAG, "m1CardSNFunction, resSn--->>>" + HEX.bytesToHex(sn));
                    byte[] snData = new byte[pes[0]];
                    System.arraycopy(sn, 0, snData, 0, pes[0]);
                    Log.v(TAG, "m1CardSNFunction, ressnData--->>>" + HEX.bytesToHex(snData));
                    int resKeyAuth = mBankCard.m1CardKeyAuth(0x41, 0x0A, keyData.length, keyData, snData.length, snData);
                    Log.v(TAG, "m1CardKeyAuth, resKeyAuth--->>>" + resKeyAuth);
                    if (resKeyAuth == 0) {
                        stringBuilder.append("M1 CardKeyAuth success\n");
                        byte[] writeData = new byte[16];
                        writeData[0] = (byte) 0x00;
                        writeData[1] = (byte) 0x01;
                        writeData[2] = (byte) 0x02;
                        writeData[3] = (byte) 0x03;
                        writeData[4] = (byte) 0x55;
                        int resWrite = mBankCard.m1CardWriteBlockData(0x0A, writeData.length, writeData);
                        if (resWrite == 0) {
                            stringBuilder.append("M1 Write success\n");
                            byte[] readData = new byte[20];
                            int[] readLen = new int[1];
                            int resRead = mBankCard.m1CardReadBlockData(10, readData, readLen);
                            if (resRead == 0) {
                                stringBuilder.append("M1 Read success\n");
                                Log.v(TAG, "m1CardReadBlockData, readDataOrl--->>>" + HEX.bytesToHex(readData));
                                byte[] read = new byte[readLen[0] - 1];
                                System.arraycopy(readData, 1, read, 0, read.length / 2);
                                Log.v(TAG, "m1CardReadBlockData, readData--->>>" + HEX.bytesToHex(read));
                                msg.arg1 = 0;
                                int resValue = mBankCard.m1CardValueOperation(0x2B, 0x0A, 1, 0x0A);
                                if (resValue == 0) {
                                    msg.arg1 = 0;
                                    stringBuilder.append("M1 ValueOperation success\n");
                                } else {
                                    stringBuilder.append("M1 ValueOperation fail\n");
                                    msg.arg1 = 1;
                                }
                            } else {
                                stringBuilder.append("M1 read fail\n");
                                msg.arg1 = 1;
                            }
                        } else {
                            stringBuilder.append("M1 Write fail\n");
                            msg.arg1 = 1;
                        }
                    } else {
                        stringBuilder.append("M1 CardKeyAuth fail\n");
                        msg.arg1 = 1;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                stringBuilder.append("detecte M1 fail\n");
                msg.arg1 = 1;
            }
        } else {
            stringBuilder.append("detecte M1 fail\n");
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }

    public void magTest() {
        byte[] respdata = new byte[1024];
        int[] resplen = new int[1];
        int retvalue = -1;
        try {
            mCore.buzzer();
            Log.v(TAG, "CardReader, magTest, buzzer--->>>");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_MAG, 60, respdata, resplen, "app1");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (respdata[0] == 0x03) {
            Message msg = mHandler.obtainMessage(MAG);
            msg.arg1 = 3;
            mHandler.sendMessage(msg);
            return;
        } else if (respdata[0] == 0x04) {
            Message msg = mHandler.obtainMessage(MAG);
            msg.arg1 = 4;
            mHandler.sendMessage(msg);
            return;
        } else if (respdata[0] == 0x00) {
            String s1 = HEX.bytesToHex(respdata);
            Log.v(TAG, "CardReader, magTest, s1 = " + s1);
            Log.v(TAG, "CardReader, magTest, resplen = " + resplen[0]);

            byte[] mag1 = new byte[128];
            int[] magLen1 = new int[1];
            byte[] mag2 = new byte[64];
            int[] magLen2 = new int[1];
            byte[] mag3 = new byte[128];
            int[] magLen3 = new int[1];
            retvalue = mBankCard.parseMagnetic(respdata, respdata.length, mag1, magLen1, mag2, magLen2, mag3, magLen3);
            if (retvalue == 0) {
                String m1 = HEX.bytesToHex(mag1);
                Log.v(TAG, "CardReader, magTest, HEX-m1 = " + m1);
                Log.v(TAG, "CardReader, magTest, resplen1 = " + magLen1[0]);
                Log.v(TAG, "CardReader, magTest, m1 = " + new String(mag1).substring(0, magLen1[0]));

                String m2 = HEX.bytesToHex(mag2);
                Log.v(TAG, "CardReader, magTest, HEX-m2 = " + m2);
                Log.v(TAG, "CardReader, magTest, resplen2 = " + magLen2[0]);
                Log.v(TAG, "CardReader, magTest, m2 = " + new String(mag2).substring(0, magLen2[0]));

                String m3 = HEX.bytesToHex(mag3);
                Log.v(TAG, "CardReader, magTest, HEX-m3 = " + m3);
                Log.v(TAG, "CardReader, magTest, resplen3 = " + magLen3[0]);
                Log.v(TAG, "CardReader, magTest, m3 = " + new String(mag3).substring(0, magLen3[0]));
            }
        }
        if (!mDetect) {
            // Don't do the other process because press stop button.
            return;
        }

        Message msg = mHandler.obtainMessage(MAG);
        if (retvalue == 0) {
            msg.arg1 = 0;
        } else {
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }

    private void desFireTest() {

        Log.v(TAG, "CardReader, DesFire Card Test");
        try {
            mCore.buzzer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        byte[] respdata = new byte[100];
        int[] resplen = new int[1];
        int retvalue = -1;
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC, 60, respdata, resplen, "app1");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (respdata[0] == 0x03) {
            Message msg = mHandler.obtainMessage(C4442);
            msg.arg1 = 3;
            mHandler.sendMessage(msg);
            return;
        } else if (respdata[0] == 0x04) {
            Message msg = mHandler.obtainMessage(IC);
            msg.arg1 = 4;
            mHandler.sendMessage(msg);
            return;
        }
        if (!mDetect) {
            // Don't do the other process because press stop button.
            return;
        }
        Log.d(TAG, "CardReader, icCardTest, retvalue = " + retvalue);
        Log.v(TAG, "CardReader, icCardTest, s1 = " + HEX.bytesToHex(respdata));
        Log.v(TAG, "CardReader, icCardTest, resplen = " + resplen[0]);

        Message msg = mHandler.obtainMessage(DESFIRE);
        String code = ByteUtil.intToHexString(respdata[0]);
        if ("0087".equals(code)) {
//            byte[] apdu = new byte[5];
//            apdu[0] = (byte) 0x00;
//            apdu[1] = (byte) 0x84;
//            apdu[2] = (byte) 0x00;
//            apdu[3] = (byte) 0x00;
//            apdu[4] = (byte) 0x08;
//            try {
//                byte[] outData = new byte[256];
//                int [] outDataLen = new int[1];
//                retvalue = mBankCard.DesFire_ISO7816(apdu, outData, outDataLen);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//            if (retvalue != 0){
//                    msg.arg1 = 1;
//                    Msg = "DesFire_ISO7816 fail=="+retvalue;
//                    mHandler.sendMessage(msg);
//                    return;
//                }
//
//            if (true) {
//                return;
//            }

            try {
                byte[] aidData = {0x00, 0x00, 0x01};
                byte[] outData = new byte[256];
                int[] outDataLen = new int[1];
                retvalue = mBankCard.DesFire_SelApp(aidData.length, aidData, outData, outDataLen);
                if (retvalue != 0) {
                    msg.arg1 = 1;
                    stringBuilder.append("DesFire_SelApp fail\n");
                    mHandler.sendMessage(msg);
                    return;
                } else {
                    stringBuilder.append("DesFire_SelApp success\n");
                }
                Log.d(TAG, "Data=" + ByteUtil.bytes2HexString(outData) + "\noutDataLen=" + outDataLen[0]);
                int keyNo = 0;
                int keyType = 0;
                byte[] keyData = {0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x22, 0x22, 0x22, 0x22, 0x22, 0x22, 0x22, 0x22};
                int keyLen = keyData.length;
                retvalue = mBankCard.DesFire_Auth(keyNo, keyType, keyLen, keyData);
                if (retvalue != 0) {
                    msg.arg1 = 1;
                    stringBuilder.append("DesFire_Auth fail\n");
                    mHandler.sendMessage(msg);
                    return;
                } else {
                    stringBuilder.append("DesFire_Auth success\n");
                }
                int mode = 0;
                int id = 2;
                retvalue = mBankCard.DesFire_GetCardInfo(mode, id, outData, outDataLen);
                if (retvalue != 0) {
                    msg.arg1 = 1;
                    stringBuilder.append("DesFire_GetCardInfo fail\n");
                    mHandler.sendMessage(msg);
                    return;
                } else {
                    stringBuilder.append("DesFire_GetCardInfo success\n");
                }
                Log.d(TAG, "Data=" + ByteUtil.bytes2HexString(outData) + "\noutDataLen=" + outDataLen[0]);
                int fileType = 0;
                int fileId = 0;
                int offset = 0;
                byte[] writeData = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
                int writeLen = writeData.length;
                retvalue = mBankCard.DesFire_WriteFile(fileType, fileId, offset, writeLen, writeData, outData, outDataLen);
                if (retvalue != 0) {
                    msg.arg1 = 1;
                    stringBuilder.append("DesFire_WriteFile fail\n");
                    mHandler.sendMessage(msg);
                    return;
                } else {
                    stringBuilder.append("DesFire_WriteFile success\n");
                }
                int readLen = 16;
                retvalue = mBankCard.DesFire_ReadFile(fileType, fileId, offset, readLen, outData, outDataLen);
                if (retvalue != 0) {
                    msg.arg1 = 1;
                    stringBuilder.append("DesFire_ReadFile fail\n");
                    mHandler.sendMessage(msg);
                    return;
                } else {
                    stringBuilder.append("DesFire_ReadFile success\n");
                }
                readLen = 0;
                retvalue = mBankCard.DesFire_ReadFile(fileType + 2, fileId + 1, offset, readLen, outData, outDataLen);
                if (retvalue != 0) {
                    msg.arg1 = 1;
                    stringBuilder.append("DesFire_ReadFile fail\n");
                    mHandler.sendMessage(msg);
                    return;
                } else {
                    stringBuilder.append("DesFire_ReadFile success\n");
                }
                Log.d(TAG, "Data1=" + ByteUtil.bytes2HexString(outData) + "\noutDataLen=" + outDataLen[0]);

                byte[] operateValue = {0x00, 0x00, 0x00, 0x01};
                retvalue = mBankCard.DesFire_ValueFileOpr(fileType + 1, fileId + 1, operateValue, outData, outDataLen);
                if (retvalue != 0) {
                    msg.arg1 = 1;
                    stringBuilder.append("DesFire_ValueFileOpration fail\n");
                    mHandler.sendMessage(msg);
                    return;
                } else {
                    stringBuilder.append("DesFire_ValueFileOpration success\n");
                }
                Log.d(TAG, "Data2=" + ByteUtil.bytes2HexString(outData) + "\noutDataLen=" + outDataLen[0]);
                retvalue = mBankCard.DesFire_Comfirm_Cancel(fileType + 1, outData, outDataLen);
                if (retvalue != 0) {
                    msg.arg1 = 1;
                    stringBuilder.append("DesFire_Comfirm_Cancel fail\n");
                    mHandler.sendMessage(msg);
                    return;
                } else {
                    stringBuilder.append("DesFire_Comfirm_Cancel success\n");
                }
                readLen = 0;
                retvalue = mBankCard.DesFire_ReadFile(fileType + 2, fileId + 1, offset, readLen, outData, outDataLen);
                if (retvalue != 0) {
                    msg.arg1 = 1;
                    stringBuilder.append("DesFire_ReadFile fail\n");
                    mHandler.sendMessage(msg);
                    return;
                } else {
                    stringBuilder.append("DesFire_ReadFile success\n");
                }
                Log.d(TAG, "Data3=" + ByteUtil.bytes2HexString(outData) + "\noutDataLen=" + outDataLen[0]);
                msg.arg1 = 0;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            msg.arg1 = 1;
            stringBuilder.append("detecte DesFire fail\n");
        }
        mHandler.sendMessage(msg);
    }

    private void setMBtnPsam2Visibility() {
        String model = Build.MODEL;
        if (WANG_POS_TAB.equals(model) || WANG_POS_MIN.equals(model)) {
            mBtnPsam2.setVisibility(View.GONE);
        }
    }
}
