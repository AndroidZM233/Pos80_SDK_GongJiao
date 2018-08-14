package com.wpos.sdkdemo.readcard;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wpos.sdkdemo.R;
import com.wpos.sdkdemo.util.keyrandom;

import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.IDCard;
import wangpos.sdk4.libbasebinder.RspCode;

public class IDCardMana extends Activity {
    private static final String TAG = "IDCardMana";
    TextView tvidcardshow1, tvidcardshow2, tvidcardshow3;

    private IDCard mIDCard;
    private BankCard mBankCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard);
        tvidcardshow1 = (TextView) findViewById(R.id.idcardshow1);
        tvidcardshow2 = (TextView) findViewById(R.id.idcardshow2);
        tvidcardshow3 = (TextView) findViewById(R.id.idcardshow3);

        new Thread() {
            @Override
            public void run() {
                mIDCard = new IDCard(getApplicationContext());
                mBankCard = new BankCard(getApplicationContext());
            }
        }.start();

        ((Button) findViewById(R.id.btniddetect)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean whileCondition = false;
                try {
                    whileCondition = BankCard.CARD_DETECT_EXIST != mIDCard.idcDetect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                //卡在位检测
                do {
                    SystemClock.sleep(200);
                    //卡片在位检测(身份证)
                    /*
                    * 返回参数
                    *   0：未检测到卡
                        1：检测到卡*/
                    try {
                        Log.v(TAG, "-------[" + mIDCard.idcDetect() + "]");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } while (whileCondition);
                try {
                    Log.v(TAG, "--detect:[" + mIDCard.idcDetect() + "]");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                int sdkIDCDetect = -1;
                try {
                    sdkIDCDetect = mIDCard.idcDetect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (sdkIDCDetect == 1) {
                    tvidcardshow1.setText("idcarddetect check piccCardTest success: " + sdkIDCDetect);
                    tvidcardshow2.setText("");
                    tvidcardshow3.setText("");
                } else {
                    tvidcardshow1.setText("idcarddetect：error");
                    tvidcardshow2.setText("");
                    tvidcardshow3.setText("");
                }
            }
        });

        ((Button) findViewById(R.id.btnidcard)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IDCard();
            }
        });
        ((Button) findViewById(R.id.btnidpoweroff)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * ICSlotPower(  int slottype,        卡片类型	N	HEX1	0x01:接触卡
                                                                            Ox02：非接卡
                *               int operationtype,   操作码	N	HEX1	0x01:下电
                                                                        0x02:上电
                *               int time)            间隔时间	N	HEX1	单位（秒）*/
                int retoff = 0;
//                try {
//                    retoff = mIDCard.icsLotPower(IDCard.CARD_MODE_PICC, IDCard.CARD_OPER_OFF, 5);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
                Log.v(TAG, "icslot poweroff :[" + retoff + "]");
                if (retoff == 0) {
                    tvidcardshow1.setText("Card reader was closed: [" + retoff + "]");
                    tvidcardshow2.setText("");
                    tvidcardshow3.setText("");
                } else {
                    tvidcardshow1.setText("Card reader close fail");
                    tvidcardshow2.setText("");
                    tvidcardshow3.setText("");
                }
            }
        });
        ((Button) findViewById(R.id.btnidexit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void IDCard() {
        byte[] respdata = new byte[1];
        int[] resplen = new int[1];
        int retvalue = 0;
        int retyrtime = 0;

        int retvalueoc = 0;
        try {
            retvalueoc = mIDCard.openCloseIDCardReader(BankCard.CARD_NMODE_PICC, BankCard.CARD_READ_OPEN);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "SDK_OpenCloseCardReader:" + retvalueoc);

        if (retvalueoc == RspCode.OK) {
            do {
                retyrtime++;
                try {
                    retvalue = mIDCard.iDCardReaderDetact(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_READ_PICCDETACT, IDCard.CARD_MODE_IDCARD, respdata, resplen, "app1");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Log.v(TAG, "SDK_CardReaderDetact:" + retvalue);
                SystemClock.sleep(100);
            } while ((retvalue != 7) && (retyrtime < 100));
        }

        String s1 = new String(respdata);
        Log.v(TAG, "" + keyrandom.bytesToHexString(respdata));
        Log.v(TAG, "" + resplen);

        Log.v(TAG, "readcard:[" + keyrandom.bytesToHexString(respdata) + "]");
        if (retvalue == 7) {
            tvidcardshow1.setText("Open piccCardTest reader: Checked PICC piccCardTest" + keyrandom.bytesToHexString(respdata));
        } else {
            tvidcardshow1.setText("Open piccCardTest reader：error");
        }
        byte[] sendapdu = new byte[5];
        sendapdu[0] = 0x00;
        sendapdu[1] = 0x36;
        sendapdu[2] = 0x00;
        sendapdu[3] = 0x00;
        sendapdu[4] = 0x08;
        byte[] outdata = new byte[100];
        int[] outlen = new int[1];
        int retapdu = -1;
        int retapdu1 = -1;
        int retapdu2 = -1;
        int retapdu3 = -1;
        try {
            retapdu = mIDCard.sendApdu(IDCard.CARD_MODE_PICC, sendapdu, sendapdu.length, outdata, outlen);
            Log.v(TAG, "------" + outlen[0]);
            retapdu1 = mIDCard.sendApdu(IDCard.CARD_MODE_PICC, sendapdu, sendapdu.length, outdata, outlen);
            Log.v(TAG, "---------" + outlen[0]);
            retapdu2 = mIDCard.sendApdu(IDCard.CARD_MODE_PICC, sendapdu, sendapdu.length, outdata, outlen);
            Log.v(TAG, "-------" + outlen[0]);
            retapdu3 = mIDCard.sendApdu(IDCard.CARD_MODE_PICC, sendapdu, sendapdu.length, outdata, outlen);
            Log.v(TAG, "----------" + outlen[0]);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (retapdu == 0) {
            tvidcardshow2.setText("send apdu success\noutdata:" + keyrandom.bytesToHexString(outdata));
        } else {
            tvidcardshow2.setText("send apdu fail");
        }

        //SDK_Picc_GetCardSN(byte[] data, SN数据
        //                   int[] len)   SN长度
        byte[] data = new byte[50];
        int[] len = new int[1];
        int retsn = -1;
        try {
            retsn = mIDCard.piccGetCardSN(data, len);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (retsn == 0) {
            tvidcardshow3.setText("Get PICC piccCardTest SN（UID）:" + keyrandom.bytesToHexString(data));
        } else {
            tvidcardshow3.setText("Get PICC piccCardTest SN (UID): ERROR");
        }

        try {
            retvalueoc = mIDCard.openCloseIDCardReader(BankCard.CARD_NMODE_PICC, BankCard.CARD_READ_CLOSE);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
