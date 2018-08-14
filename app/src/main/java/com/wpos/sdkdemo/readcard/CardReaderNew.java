package com.wpos.sdkdemo.readcard;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wpos.sdkdemo.R;

import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libbasebinder.RspCode;

public class CardReaderNew extends Activity implements View.OnClickListener {
    private static final String TAG = "CardReaderNew";
    public TextView tvcardreadernewshow;
    private Button mBtnPicc;
    private Button mBtnIc;
    private Button mBtnMag;
    private Button mReadInfo;
    private Button buttonFelica;
    private Button mBtnStop;
    private Button mBtnExit;

    private String mText = "";
    private static final int UPDATE_TEXT = 0;
    private boolean mPICCFlag = false;
    private boolean mICCFlag = false;
    private boolean mMagFlag = false;
    private boolean mReadNonContactFlag = false;
    private boolean mFelicaFlag = false;
    private MainHandler mHandler = new MainHandler();

    private BankCard mBankCard;
    private Core mCore;

    private Button[] mBtnArray;
    private boolean mCanRead = true;

    private class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT: {
                    if (mCanRead)
                        tvcardreadernewshow.setText(mText);
                    else
                        tvcardreadernewshow.setText(R.string.test_please);
                    break;
                }
            }
            refreshButton(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardnew);

        tvcardreadernewshow = (TextView) findViewById(R.id.cardtextview);
        mBtnPicc = (Button) findViewById(R.id.card);
        mBtnIc = (Button) findViewById(R.id.iccard);
        mBtnMag = (Button) findViewById(R.id.mag);
        mReadInfo = (Button) findViewById(R.id.button_read_non_contact);
        buttonFelica = (Button)findViewById(R.id.buttonFelica);
        mBtnExit = (Button) findViewById(R.id.exit);
        mBtnStop = (Button) findViewById(R.id.btn_stop);

        mBtnPicc.setOnClickListener(this);
        mBtnIc.setOnClickListener(this);
        mBtnMag.setOnClickListener(this);
        mReadInfo.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        buttonFelica.setOnClickListener(this);

        new Thread() {
            @Override
            public void run() {
                mCore = new Core(getApplicationContext());
                mBankCard = new BankCard(getApplicationContext());
            }
        }.start();

        mBtnArray = new Button[]{mBtnPicc, mBtnIc, mBtnMag, mReadInfo,buttonFelica};

        refreshButton(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card:
                mCanRead = true;
                mPICCFlag = true;
                mICCFlag = false;
                mMagFlag = false;
                mReadNonContactFlag = false;
                mFelicaFlag = false;
                tvcardreadernewshow.setText(R.string.ditips);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newcard();
                    }
                }).start();
                break;
            case R.id.iccard:
                mCanRead = true;
                mPICCFlag = false;
                mICCFlag = true;
                mMagFlag = false;
                mReadNonContactFlag = false;
                mFelicaFlag = false;
                tvcardreadernewshow.setText(R.string.ditips);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newiccard();
                    }
                }).start();
                break;
            case R.id.mag:
                mCanRead = true;
                mPICCFlag = false;
                mICCFlag = false;
                mMagFlag = true;
                mReadNonContactFlag = false;
                mFelicaFlag = false;
                tvcardreadernewshow.setText(R.string.ditips);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newmag();
                    }
                }).start();
                break;
            case R.id.button_read_non_contact:
                mCanRead = true;
                mPICCFlag = false;
                mICCFlag = false;
                mMagFlag = false;
                mReadNonContactFlag = true;
                mFelicaFlag = false;
                tvcardreadernewshow.setText(R.string.ditips);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        readNonContactInfo();
                    }
                }).start();
                break;
            case R.id.btn_stop:
                mCanRead = false;
                break;
            case R.id.exit:
                mPICCFlag = false;
                mICCFlag = false;
                mMagFlag = false;
                mReadNonContactFlag = false;
                mCanRead = false;
                mFelicaFlag = false;
                finish();
                break;
            case R.id.buttonFelica:
                mCanRead = true;
                mICCFlag = false;
                mPICCFlag = false;
                mMagFlag = false;
                mReadNonContactFlag = false;
                mFelicaFlag = true;
                tvcardreadernewshow.setText(R.string.ditips);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        felicaTest();
                    }
                }).start();
                break;
        }

        refreshButton(mCanRead);
    }

    private void refreshButton(boolean isRead) {
        for (int i = 0; i < mBtnArray.length; i++) {
            mBtnArray[i].setEnabled(!isRead);
        }
        mBtnStop.setEnabled(isRead);
    }

    //PICC
    public void newcard() {
        try {
            mCore.buzzer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "bankcard readcard");
        byte[] respdata = new byte[100];
        int[] resplen = new int[10];
        int retvalueoc = -1;
        int retvaluedete = -1;
        do {
            try {
                retvalueoc = mBankCard.openCloseCardReader(0x02, BankCard.CARD_READ_OPEN);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "SDK_OpenCloseCardReader:" + retvalueoc);
        } while (mCanRead && retvalueoc != RspCode.OK && mPICCFlag);
        do {
            try {
                retvaluedete = mBankCard.cardReaderDetact(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_NMODE_PICC, BankCard.CARD_READ_BANKCARD, respdata, resplen, "app1");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "SDK_CardReaderDetact:" + retvaluedete);
        } while (mCanRead && retvaluedete != 7 && mPICCFlag);
        Log.v(TAG, "getcardsnfunction");
        byte[] outdata = new byte[512];
        int[] len = new int[100];
        try {
            mBankCard.getCardSNFunction(outdata, len);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "" + outdata);
        Log.v(TAG, "" + len[0]);


        Log.v(TAG, "send apdu");
        //\x00\x84\x00\x00\04
        byte[] sendapdu = new byte[256];
        sendapdu[0] = (byte) 0x00;
        sendapdu[1] = (byte) 0x84;
        sendapdu[2] = (byte) 0x00;
        sendapdu[3] = (byte) 0x00;
        sendapdu[4] = (byte) 0x04;
        Log.v(TAG, "" + new String(sendapdu));
        byte[] resp = new byte[256];
        int retpicc = -1;
        try {
            retpicc = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, sendapdu, 5, respdata, resplen);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "" + resplen);
        Log.v(TAG, "" + resp);

        Log.v(TAG, "piccdetect");
        int piccDetect = -1;
        try {
            piccDetect = mBankCard.piccDetect();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (retpicc == 0 && retvaluedete == 7) {
            mText = "PICC readcard and sendapdu success";
        } else {
            mText = "PICC fail";
        }
        mHandler.sendEmptyMessage(UPDATE_TEXT);
    }

    public void newiccard() {
        try {
            mCore.buzzer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "icccard");
        byte[] respdata = new byte[100];
        int[] resplen = new int[1];
        int retvalueocicc = -1;
        int retvaluedeteicc = -1;
        do {
            try {
                retvalueocicc = mBankCard.openCloseCardReader(0x01, BankCard.CARD_READ_OPEN);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "SDK_OpenCloseCardReader:" + retvalueocicc);
        } while (mCanRead && retvalueocicc != RspCode.OK && mICCFlag);
        do {
            try {
                retvaluedeteicc = mBankCard.cardReaderDetact(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_NMODE_ICC, BankCard.CARD_READ_BANKCARD, respdata, resplen, "app1");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "SDK_CardReaderDetact:" + retvaluedeteicc);
        } while (mCanRead && retvaluedeteicc != 5 && mICCFlag);

        if (retvaluedeteicc == 5) {
            mText = "IC read card success";
        } else {
            mText = "IC error";
        }
        mHandler.sendEmptyMessage(UPDATE_TEXT);
    }

    public void newmag() {
        Log.v(TAG, "bankcard readcard");
        byte[] respdata = new byte[1024];
        int[] resplen = new int[1];
        try {
            mCore.buzzer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        int retvalueocmag = -1;
        int retvaluedetemag = -1;
        boolean magflag = false;
        do {
            try {
                retvalueocmag = mBankCard.openCloseCardReader(0x04, BankCard.CARD_READ_OPEN);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "SDK_OpenCloseCardReader:" + retvalueocmag);
        } while (mCanRead && retvalueocmag != RspCode.OK && mMagFlag);
        do {
            try {
                retvaluedetemag = mBankCard.cardReaderDetact(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_NMODE_MAG, BankCard.CARD_READ_BANKCARD, respdata, resplen, "app1");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "SDK_CardReaderDetact:" + retvaluedetemag);
            if (retvaluedetemag == 2 || retvaluedetemag == 0) {
                magflag = true;
            }
        } while (mCanRead && !magflag && mMagFlag);
        if (retvaluedetemag == 0) {
            mText = "MAG Test success";
        } else if (retvaluedetemag == 2) {
            mText = "MAG Test fail";
        } else {
            mText = "MAG Test error";
        }
        mHandler.sendEmptyMessage(UPDATE_TEXT);
    }

    private void readNonContactInfo() {
        int retOpen = 0;
        do {
            try {
                retOpen = mBankCard.openCloseCardReader(0x02, BankCard.CARD_READ_OPEN);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "SDK_OpenCloseCardReader:" + retOpen);
        } while (mCanRead && retOpen != RspCode.OK && mReadNonContactFlag);
        try {
            mCore.buzzer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        int[] outLen = new int[2];
        byte[] outData = new byte[64];
        int ret = -1;
        do {
            try {
                ret = mBankCard.readContactlessInfo(outData, outLen);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (ret == 0) {
                break;
            }
        } while (mCanRead && mReadNonContactFlag);

        if (ret == 0) {
            int len = outLen[0];
            String dataContent = bytesToHexString(outData, len);
            mText = getString(R.string.read_non_context_success, len, dataContent);
            mHandler.sendEmptyMessage(UPDATE_TEXT);
        }
    }

    public static String bytesToHexString(byte[] src, int len) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < len; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    private void felicaTest(){
        int[] cardNums = new int[1];
        byte[] respdata = new byte[100];
        int[] resplen = new int[1];
        try {
            mCore.buzzer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        int retvalue = -1;
        try {
            retvalue = mBankCard.openCloseCardReader(0x02, BankCard.CARD_READ_CLOSE);//防止上面PICC开启的卡槽影响Felica_Open（Prevent the above PICC open card slot from affecting "felica_Open"）
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        do {
            try {
                retvalue = mBankCard.Felica_Open(0x00,0xFFFF,0x01, cardNums, respdata,resplen);
                Log.i("retvalue","" + retvalue);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }while (mCanRead && retvalue != RspCode.OK && mFelicaFlag);

        if (retvalue == 0){
            mText = "Felica Test success";
        }else {
            mText = "Felica Test failure";
        }
        mHandler.sendEmptyMessage(UPDATE_TEXT);
    }
}
