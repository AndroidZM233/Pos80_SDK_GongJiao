package com.wpos.sdkdemo.spdata;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wpos.sdkdemo.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libbasebinder.HEX;

public class PsamIcActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btnOneExcute, btnLooperExcute;
    private TextView tvShowMsg;
    private BankCard mBankCard;
    private Core mCore;
    private static final String TAG = "PsamIcActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initCard();

    }

    private void initCard() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBankCard = new BankCard(getApplicationContext());
                mCore = new Core(getApplicationContext());
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBankCard = null;
        try {
            mBankCard.breakOffCommand();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        try {
        //会闪退
//            mBankCard.openCloseCardReader(BankCard.CARD_MODE_PSAM1,0x02);
//            mBankCard.breakOffCommand();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    private void initUI() {
        setContentView(R.layout.activity_psam_ic);
        btnOneExcute = (Button) findViewById(R.id.btn_excute);
        btnLooperExcute = (Button) findViewById(R.id.btn_start);
        btnOneExcute.setOnClickListener(this);
        btnLooperExcute.setOnClickListener(this);
        tvShowMsg = (TextView) findViewById(R.id.tv_show_msg);
    }


    @Override
    public void onClick(View v) {
        if (v == btnLooperExcute) {
            test();
        } else if (v == btnOneExcute) {

            test();
        }
    }

    //XXXXXXXX（余额）XXXX（CPU卡脱机交易序号）XXXXXX XX（密钥版本）XX（算法标识）XXXXXXXX（随机数）
    private byte[] blance = new byte[4];
    private byte[] ATC = new byte[2];
    private byte[] keyVersion = new byte[4];
    private byte flag;
    private byte[] rondomCpu = new byte[4];


    private byte[] random = {0x00, (byte) 0x84, 0x00, 0x00, 0x08};
    private byte[] dir1 = {0x00, (byte) 0xa4, 0x04, 0x00, 0x08, (byte) 0xa0, 0x00, 0x00, (byte) 0xd6, 0x32, 0x01, 0x01, 0x05};
    //4D4F542E43505449433032


    private byte[] psam1_get_id = {0x00, (byte) 0xB0, (byte) 0x96, 0x00, 0x06};
    private byte[] psam2_select_dir = {0x00, (byte) 0xa4, 0x00, 0x00, 0x02, (byte) 0x80, 0x11};
    private byte[] psam2_select_dir2 = {0x00, (byte) 0xA4, 0x04, 0x00, 0x06, (byte) 0xBD, (byte) 0xA8, (byte) 0xC9, (byte) 0xE8, (byte) 0xB2, (byte) 0xBF};
    private byte[] psam3_get_index = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x01};

    //                          00          A4      04  00      0E 32       50 41 59        2E 53 59        53 2E 44            44 46 30 31
    private byte[] ic4_dir1 = {0x00, (byte) 0xa4, 0x04, 0x00, 0x0e, 0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2e, 0x44, 0x44, 0x46, 0x30, 0x31};
    //x00\xA4\x04\x00\x0e\x32\x50\x41\x59\x2e\x53\x59\x53\x2e\x44\x44\x46\x30\x31 返回9000 之后发\x00\xA4\x04\x00\x08\xA0\x00\x00\x06\x32\x01\x01\x05
    private byte[] ic4_dir2 = {0x00, (byte) 0xa4, 0x04, 0x00, 0x08, (byte) 0xa0, 0x00, 0x00, 0x06, 0x32, 0x01, 0x01, 0x05};

    private byte[] ic_read_file = {0x00, (byte) 0xB0, (byte) 0x95, 0x00, 0x00};

    //                        80 50 01 02 0B XX（密钥索引 PSAM卡00B0970001指令返回）XX XX XX XX（本次交易金额）XX XX XX XX XX XX（终端机编号 PSAM卡00B0960006指令返回）
    byte[] init_ic = {(byte) 0x80, 0x50, 0x01, 0x02, 0x0B, 0x01, 0x00, 0x00, 0x00, 0x02, 0x41, 0x31, 0x01, 0x21, 0x01, (byte) 0x97};
    byte[] ic_file = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, (byte) 0xA0, 0x00, 0x00, 0x06, 0x32, 0x01, 0x01, 0x05};

    private byte[] cardId = new byte[8];
    private byte[] city = new byte[2];

    private byte[] file15_8 = new byte[8];

    private void test() {
        try {
            mCore.buzzer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        byte[] respdata = new byte[100];
        int[] resplen = new int[1];
        int retvalue = -1;


        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PSAM1, 60, respdata, resplen, "app1");
            respdata = new byte[8];
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, psam1_get_id, psam1_get_id.length, respdata, resplen);
            tvShowMsg.setText("psam psam1_get_id return " + HEX.bytesToHex(respdata) + "\n");
            Log.d(TAG, "====PSAM卡终端机编号==== " + HEX.bytesToHex(respdata) + "   " + retvalue);

//            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, psam2_select_dir2, psam2_select_dir2.length, respdata, resplen);
//            tvShowMsg.setText("psam psam2_select_dir2 return " + HEX.bytesToHex(respdata) + "\n");
//            Log.d(TAG, "psam psam2_select_dir2 return " + HEX.bytesToHex(respdata));
////
            respdata = new byte[40];
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, psam2_select_dir, psam2_select_dir.length, respdata, resplen);

            tvShowMsg.setText("psam psam2_select_dir return " + HEX.bytesToHex(respdata) + "\n");
            Log.d(TAG, "===选文件 80 11 ====" + HEX.bytesToHex(respdata) + "   " + retvalue);

            respdata = new byte[3];
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, psam3_get_index, psam3_get_index.length, respdata, resplen);
            tvShowMsg.setText("psam_get_index return " + HEX.bytesToHex(respdata) + "\n");
            Log.d(TAG, "===读17文件获取秘钥索引===" + HEX.bytesToHex(respdata));

//            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, random, random.length, respdata, resplen);
//            tvShowMsg.append("ic 84 return " + HEX.bytesToHex(respdata) + "\n");
//            Log.d(TAG, "===ic 84 return ===" + HEX.bytesToHex(respdata));

            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC, 60, respdata, resplen, "app1");
//            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, ic4_dir1, ic4_dir1.length, respdata, resplen);
//            tvShowMsg.append("send  dir  return " + HEX.bytesToHex(respdata) + "\n");
//            Log.d(TAG, "===用户卡切换文件 ===" + HEX.bytesToHex(respdata));
////
//
            respdata = new byte[100];
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, ic_file, ic_file.length, respdata, resplen);
            tvShowMsg.append("send  ic_file  return " + HEX.bytesToHex(respdata) + "\n");
            Log.d(TAG, "===切换IC文件===" + HEX.bytesToHex(respdata));


            respdata = new byte[40];
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, ic_read_file, ic_read_file.length, respdata, resplen);
            tvShowMsg.append("send  dir  return " + HEX.bytesToHex(respdata) + "\n");
            Log.d(TAG, "===读15文件 ===" + HEX.bytesToHex(respdata));
            //02313750FFFFFFFF0201 031049906000000000062017010820991231000090000000000000000000
            System.arraycopy(respdata, 12, cardId, 0, 8);
            System.arraycopy(respdata, 0, file15_8, 0, 8);
            Log.d(TAG, "===卡应用序列号 ===" + HEX.bytesToHex(cardId));

            System.arraycopy(respdata, 2, city, 0, 2);
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, init_ic, init_ic.length, respdata, resplen);
            tvShowMsg.append("send  init_ic  return " + HEX.bytesToHex(respdata) + "\n");
            Log.d(TAG, "===IC卡初始化===" + HEX.bytesToHex(respdata));
            //000046BA       010D                 000000 01         00          766DD305  9000000000
            //XXXXXXXX（余额）XXXX（CPU卡脱机交易序号）XXXXXXXX（密钥版本）XX（算法标识）XXXXXXXX（随机数）

            System.arraycopy(respdata, 0, blance, 0, 4);
            System.arraycopy(respdata, 4, ATC, 0, 2);
            System.arraycopy(respdata, 6, keyVersion, 0, 4);

            flag = respdata[10];
            System.arraycopy(respdata, 11, rondomCpu, 0, 4);


            byte[] psam_mac1 = initSamForPurchase();
            Log.d(TAG, "===获取MAC1  send===" + HEX.bytesToHex(psam_mac1));
            respdata = new byte[100];
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM1_APDU, psam_mac1, psam_mac1.length, respdata, resplen);
            tvShowMsg.append("send  psam_mac1  return " + HEX.bytesToHex(respdata) + "\n");
            Log.d(TAG, "===获取MAC1===" + HEX.bytesToHex(respdata) + "   " + retvalue);
            praseMAC1(respdata);

            byte[] cmd = getIcPurchase();
            Log.d(TAG, "===IC 卡 54消费发送===" + HEX.bytesToHex(cmd));
            respdata = new byte[500];
            retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PICC, cmd, cmd.length, respdata, resplen);
            tvShowMsg.append("IC 卡 54消费返回 " + HEX.bytesToHex(respdata) + "\n");
            Log.d(TAG, "===IC 卡 54消费返回===" + HEX.bytesToHex(respdata) + " " + retvalue);

            mBankCard.breakOffCommand();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    private void praseMAC1(byte[] data) {
        if (data.length <= 2) {
            Log.e(TAG, "===获取MAC1失败===" + HEX.bytesToHex(data));
            return;
        }
        System.arraycopy(data, 0, PSAM_ATC, 0, 4);
        System.arraycopy(data, 4, MAC1, 0, 4);
    }

    private byte[] PSAM_ATC = new byte[4];
    private byte[] MAC1 = new byte[4];


    //50返回
    //XXXXXXXX（余额）XXXX（CPU卡脱机交易序号）XXXXXXXX（密钥版本）XX（算法标识）XXXXXXXX（随机数）
    //000046BA 010D 00000001 00 C6332B0B 900


    //8070 000024 86E30A38 010D 00000001 06 2018 0814 101010 01 00 9060000000000620 0000 FF000000000000
    // （MAC1计算）命令 PSAM
    //PSAM卡产生MAC1指令[终端向PSAM卡发送]：
    // 8070 000024 XXXXXXXX（随机数 用户卡8050指令返回）XXXX（CPU卡脱机交易序号 用户卡8050指令返回）XXXXXXXX（本次交易金额）
    // 06（消费交易 固定值）XXXXXXXXXXXXXX（交易日期及时间 终端系统日期与时间）XX（密钥版本 用户卡8050指令返回）XX（算法标识 用户卡8050指令返回）
    // XXXXXXXXXXXXXXXX（卡应用序列号 15文件下13-20字段）XXXX（城市代码 15文件下03-04字段）FF0000000000（固定值）


    //返回结果为：XXXXXXXX（终端脱机交易序号）XXXXXXXX（MAC1）

    private byte[] initSamForPurchase() {

        byte[] result = new byte[4];
        byte[] cmd = new byte[42];
        cmd[0] = (byte) 0x80;
        cmd[1] = 0x70;
        cmd[2] = 0x00;
        cmd[3] = 0x00;
        cmd[4] = 0x24;
//        System.arraycopy(random, 0, cmd, 5, random.length);// 4
        System.arraycopy(rondomCpu, 0, cmd, 5, 4);
        System.arraycopy(ATC, 0, cmd, 9, 2);
        //交易金额
        byte[] temp = intToByteArray(1);
        System.arraycopy(temp, 0, cmd, 11, 4);
        cmd[15] = (byte) 0x06;

        // 固定时间
        cmd[16] = 0x20;
        cmd[17] = 0x18;
        cmd[18] = 0x08;
        cmd[19] = 0x14;
        cmd[20] = 0x10;
        cmd[21] = 0x10;
        cmd[22] = 0x10;
//        byte[] dateTime = getDateTime();
//        Log.d(TAG, "===dateTime===" + HEX.bytesToHex(dateTime) + "   ");
//        System.arraycopy(dateTime, 0, cmd, 17, 7);
        cmd[23] = 0x01;
        cmd[24] = 0x00;//arithmeticFlog
        System.arraycopy(cardId, 0, cmd, 25, 8);// 8 ->35字节
        System.arraycopy(file15_8, 0, cmd, 33, 8);// 2 ->36字节
        //固定值FF00 00 00 00 00
//        cmd[35] = (byte) 0xff;
//        cmd[36] = (byte) 0x00;
//        cmd[37] = (byte) 0x00;
//        cmd[38] = (byte) 0x00;
//        cmd[39] = (byte) 0x00;
        cmd[41] = (byte) 0x08;

        return cmd;
    }

    //    8054 01000F XXXXXXXX（终端脱机交易序号 PSAM卡8070指令返回）XXXXXXXXXXXXXX（交易日期及时间 与8070指令时间一致）
    //    8054 01000F 000002E0 20180814101010 CE1814D9
    //    8054 01000F 000002E0 20180814101010 CE1814D9
//        8054 01000F 000002D7 20160725183127 F49C63F1 08
//        8054 01000F 000002E0 20180814101010 EC2E23BF 08
    //    000002E0 CE1814D9 9000
    // XXXXXXXX（MAC1 PSAM卡8070指令返回）[本指令执行成功后用户卡会自动记录交易记录明细]
    private byte[] getIcPurchase() {
        byte[] cmd = new byte[20];

        cmd[0] = (byte) 0x80;
        cmd[1] = (byte) 0x54;
        cmd[2] = (byte) 0x01;
        cmd[3] = (byte) 0x00;
        cmd[4] = (byte) 0x0f;
        //PSAM_ATC 4
        System.arraycopy(PSAM_ATC, 0, cmd, 5, 4);
        //固定时间
        cmd[9] = 0x20;
        cmd[10] = 0x18;
        cmd[11] = 0x08;
        cmd[12] = 0x14;
        cmd[13] = 0x10;
        cmd[14] = 0x10;
        cmd[15] = 0x10;
        System.arraycopy(MAC1, 0, cmd, 16, 4);
//        cmd[20] = 0x08;
        return cmd;
    }


    private byte[] intToByteArray(int data) {
        byte[] result = new byte[4];
        result[0] = (byte) ((data >> 24) & 0xFF);
        result[1] = (byte) ((data >> 16) & 0xFF);
        result[2] = (byte) ((data >> 8) & 0xFF);
        result[3] = (byte) (data & 0xFF);
        return result;
    }

    private byte[] getDateTime() {
//        byte[] dateTime = new byte[7];
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-yy-MM-dd-HH-mm-ss");//可以方便地修改日期格式
        String currentTime = dateFormat.format(now);
        String[] tempString = currentTime.split("-");
        byte[] tempByte = new byte[7];
        for (int i = 0; i < tempString.length; i++) {
            tempByte[i] = (byte) Integer.parseInt(tempString[i]);
        }
        // 赋值当前日期和时间
        return tempByte;
    }


}
