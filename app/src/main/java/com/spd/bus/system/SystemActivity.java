package com.spd.bus.system;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spd.bus.R;
import com.spd.bus.util.ByteUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libbasebinder.RspCode;

public class SystemActivity extends Activity {

    private BankCard mBankCard;
    private Core mCore;

    static int openled = 0;

    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys);

        new Thread(){
            @Override
            public void run() {
                mBankCard = new BankCard(getApplicationContext());
                mCore = new Core(getApplicationContext());
            }
        }.start();

        tvResult = (TextView)findViewById(R.id.textviewsys);

        Button btnGetDataTime = (Button)findViewById(R.id.getdatatime);
        btnGetDataTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] datetime = new byte[14];
                try {
                    // Get SP system clock in format ASCII14 yyyyMMddHHmmss
                    mCore.getDateTime(datetime);
                    String strDate = new String(datetime);
                    strDate = strDate.substring(0,4) + "-" + strDate.substring(4,6) + "-" +
                            strDate.substring(6,8) + " " + strDate.substring(8,10) + ":" +
                            strDate.substring(10,12) + ":" + strDate.substring(12,14);
                    tvResult.setText("Date Time: " + strDate);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    tvResult.setText("Exception " + e.toString());
                }
            }
        });

        Button btnSetDataTime = (Button)findViewById(R.id.setdatatime);
        btnSetDataTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // Set SP system clock in format ASCII14 yyyyMMddHHmmss
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = new Date(System.currentTimeMillis());
            String str = simpleDateFormat.format(date);// 1971 < year < 2099
            try {
                mCore.setDateTime(str.getBytes("UTF-8"));
                tvResult.setText("Set DateTime: " + "complete");
            } catch (Exception e) {
                e.printStackTrace();
                tvResult.setText("Exception " + e.toString());
            }
            }
        });

        Button btnReadSN = (Button)findViewById(R.id.readsn);
        btnReadSN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Read device wangpos SN
                tvResult.setText("SN: " + Build.SERIAL);
            }
        });

        Button btnReadSPSN = (Button)findViewById(R.id.readspsn);
        btnReadSPSN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // Read device SP (secure processor) SN
            // This is reserved for customer use
            // Memory must allock at least 32 bytes
            byte[] SN = new byte[32];
            int[] length = new int[1];
            try {
                mCore.readSN(SN, length);
                String strSN = new String(SN);
                tvResult.setText("SP SN: "+ strSN);
            } catch (Exception e) {
                e.printStackTrace();
                tvResult.setText("Exception " + e.toString());
            }
            }
        });

        Button btnWriteSPSN = (Button)findViewById(R.id.writespsn);
        btnWriteSPSN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // Write device SP (secure processor) SN
            // This is reserved for customer use
            // Maximum support length is 32 bytes
            String SN = "1234567890ABCDEFG";
            try {
                byte[] bytes = SN.getBytes("UTF-8");
                mCore.writeSN(bytes, bytes.length);
                tvResult.setText("SP SN: write finish");
            } catch (Exception e) {
                e.printStackTrace();
                tvResult.setText("Exception " + e.toString());
            }
            }
        });

        Button btnBakBatVal = (Button)findViewById(R.id.getbatlev);
        btnBakBatVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getBatteryLevel need pre-alloc 3 bytes of memory before use.
                // The result will be the format like 3.01V -> 0x33,0x30,0x31
                byte[] BatteryLevel = new byte[3];
                try {
                    int ret = mCore.getBatteryLevel(BatteryLevel);
                    if (ret == RspCode.OK) {
                        String val = new String(BatteryLevel);
                        val = val.substring(0,1) + "." + val.substring(1,3) + "v";
                        tvResult.setText("Backup battery voltage: " + val);
                    } else
                        tvResult.setText("Get backup battery fail.");
                } catch (Exception e) {
                    e.printStackTrace();
                    tvResult.setText("Exception " + e.toString());
                }
            }
        });

        Button btnGetTemper = (Button)findViewById(R.id.gettemper);
        btnGetTemper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // Temper status is SP hardware protect function
            // The result is 2 bytes
            // First byte means this function is open or not
            // Second byte means each line status and final result
            byte[] data = new byte[2];
            int[] length = new int[1];
            String result;
            try {
                int i = mCore.getTamper(data, length);
                int status = data[1];
                if (i != RspCode.OK)
                    result = "Get Temper Fail1";
                else {
                    if (data[0] == 0x00)
                        result = "Temper not enabled";
                    else
                        result = "Temper enabled";

                    if (data[1] == 0xF1)
                        result = "Device tempered";
                    else if (data[1] == 0xF2)
                        result = "Device error need retry";
                    else
                        result += " each line status is " + Integer.toBinaryString(status);
                }
                tvResult.setText(result);
            } catch (Exception e) {
                e.printStackTrace();
                tvResult.setText("Exception " + e.toString());
            }
            }
        });

        Button btnEnableTemper = (Button)findViewById(R.id.enabtem);
        btnEnableTemper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            byte[] data = new byte[1];
            int[] length = new int[1];
            try {
                int i = mCore.enableTamper(data, length);
                if (i != RspCode.OK) {
                    tvResult.setText("Enable temper fail");
                }else {
                    int result = ByteUtil.bytes2Int(data);
                    if (result == 0x00)
                        tvResult.setText("Enable temper success");
                    else if (result == 0xF1)
                        tvResult.setText("Enable temper not ready");
                    else if (result == 0xF2)
                        tvResult.setText("Enable temper security fail");
                    else if (result == 0xF3)
                        tvResult.setText("Enable temper no PMK");
                    else if (result == 0xF4)
                        tvResult.setText("Enable temper no update Public KeyEx");
                    else
                        tvResult.setText("Enable temper other error");
                }
            } catch (Exception e) {
                e.printStackTrace();
                tvResult.setText("Exception " + e.toString());
            }
            }
        });

        Button btnGetDevVersion = (Button)findViewById(R.id.getdevver);
        btnGetDevVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // Read device wangpos firmware version
            tvResult.setText("Device version: " + Build.DISPLAY);
            }
        });

        Button btnGetSPVersion = (Button)findViewById(R.id.getspver);
        btnGetSPVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            byte[] DevicesVersion = new byte[128];
            int[] len = new int[1];
            try {
                int i = mCore.getDevicesVersion(DevicesVersion, len);
                if (i == RspCode.OK) {
                    String ver = new String(DevicesVersion);
//                    int index = ver.indexOf("SP");
//                    ver = ver.substring(0,index);
                    tvResult.setText("code: " + ver);
                } else
                    tvResult.setText("code get fail");
            } catch (Exception e) {
                e.printStackTrace();
                tvResult.setText("Exception " + e.toString());
            }
            }
        });

        Button btnGetFirVersion = (Button)findViewById(R.id.getfirver);
        if ("WPOS-TAB".equals(Build.MODEL)||"WPOS-MINI".equals(Build.MODEL)){
            btnGetFirVersion.setVisibility(View.GONE);
        }
        btnGetFirVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               tvResult.setText("ver: SP_V1.21_B0T0_170920");
            }
        });

        Button btnGMVersion = (Button)findViewById(R.id.getgmssta);
        btnGMVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            byte[] GM = new byte[20];
            int[] gmlen = new int[1];
            try {
                int i = mCore.getGMStatus(GM, gmlen);
                if (GM[0] == 0x00)
                    tvResult.setText("No China National Cryptologic chip");
                else if (GM[0] == 0x01)
                    tvResult.setText("Have China National Cryptologic chip without firmware");
                else if (GM[0] == 0x02) {
                    byte[] GMV = new byte[19];
                    System.arraycopy(GM, 1, GMV, 0, 19);
                    String ver = new String(GMV);
                    tvResult.setText("Have China National Cryptologic chip and firmware: " + ver);
                }
            } catch (Exception e) {
                e.printStackTrace();
                tvResult.setText("Exception " + e.toString());
            }
            }
        });

        /*Button serviceV = (Button)findViewById(R.id.getserviceV) ;
        serviceV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String res = mCore.getSDKVersion();
                    tvResult.setText("version: " + res);
                } catch (Exception e) {
                    e.printStackTrace();
                    tvResult.setText("Exception " + e.toString());
                }
            }
        });*/

        Button btnBreak = (Button)findViewById(R.id.breakoff);
        btnBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                mBankCard.breakOffCommand();
                tvResult.setText("This command is used to end other command operations");
            } catch (Exception e) {
                e.printStackTrace();
                tvResult.setText("Exception " + e.toString());
            }
            }
        });

        Button btnBuzzer = (Button)findViewById(R.id.buzzer) ;
        btnBuzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
//                mCore.buzzer();
                tvResult.setText("Can you hear 'bi..'?");

                mCore.buzzerEx(1000);
            } catch (Exception e) {
                e.printStackTrace();
                tvResult.setText("Exception " + e.toString());
            }
            }
        });

        Button kernel = (Button)findViewById(R.id.kernel) ;
        kernel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                   int res = mCore.SetKernel(0x00,0xC0,0x00);
                } catch (Exception e) {
                    e.printStackTrace();
                    tvResult.setText("Exception " + e.toString());
                }
            }
        });

        Button btnled = (Button)findViewById(R.id.led);
        btnled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // led function, first four parameters corresponding to each led, last parameter is on/off operation
            tvResult.setText("Press again and again");
            try {
                if (openled == 0)
                    mCore.led(1, 0, 0, 0, 1);
                else if (openled == 1)
                    mCore.led(0, 1, 0, 0, 1);
                else if (openled == 2)
                    mCore.led(0, 0, 1, 0, 1);
                else if (openled == 3)
                    mCore.led(0, 0, 0, 1, 1);
                else if (openled == 4)
                    mCore.led(1, 1, 1, 1, 0);

                openled++;

                if (openled == 5)
                    openled = 0;
//                mCore.ledFlash(1,1,1,1,1000,1000,3000);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
            }
        });

        Button btnGetSpSn = (Button)findViewById(R.id.get_sp_id) ;
        btnGetSpSn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ret = 0;
                byte[] id = new byte[64];
                int[] idLen = new int[1];
                try {
                    ret = mCore.getSpID(id, idLen);
                    if (RspCode.OK == ret){
                        byte[] temp = new byte[idLen[0]];
                        System.arraycopy(id, 0, temp,0, idLen[0]);
                        tvResult.setText(ByteUtil.bytes2HexString(temp));
                    }else {
                        tvResult.setText("Get SP ID Error!");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        Button BtnExit = (Button)findViewById(R.id.exit);
        BtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
