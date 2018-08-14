package com.wpos.sdkdemo.print;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wpos.sdkdemo.R;
import com.wpos.sdkdemo.util.ByteUtil;

import wangpos.sdk4.libbasebinder.Printer;

public class PrinterManager extends Activity {
    Button btnprintcon , btnprintstatus , btnexit,btnpmkilometres,kilometres_clear;
    TextView tvshow;
    private Printer mPrinter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printmanager);
        btnprintcon = (Button)findViewById(R.id.btnpmcontrol);
        btnprintstatus = (Button)findViewById(R.id.btnpmstatus);
        btnpmkilometres = (Button)findViewById(R.id.btnpmkilometres);
        kilometres_clear = (Button)findViewById(R.id.kilometres_clear);

        tvshow = (TextView)findViewById(R.id.tvshowprinttips);
        btnexit = (Button)findViewById(R.id.btnpmexit);

        new Thread(){
            @Override
            public void run() {
                mPrinter = new Printer(getApplicationContext());
            }
        }.start();

        btnprintcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentprincon = new Intent(PrinterManager.this , Printing.class);
                startActivity(intentprincon);
            }
        });

//        intent6 = new Intent(MainActivity.this, BTPrinting.class);
        btnprintstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            int[] status = new int[1];
            int ret = -1;
            try {
                ret = mPrinter.getPrinterStatus(status);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            //GETPrinterstatus(int[] status) 状态值	N	HEX1
            // 00 打印机正常
            // 0x01：参数错误
            // 0x06：不可执行
            // 0x8A：缺纸,
            // 0x8B：过热
            if(ret == 0){
                tvshow.setText("Printer status: "+status[0]);
            }else{
                tvshow.setText("Fail");
            }
            }
        });

        btnpmkilometres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ret = -1;
                try {
                    byte[] outData = new byte[4];
                    ret = mPrinter.Get_ClearPrinterMileage(0x01,outData);
                    if(ret == 0){
                        tvshow.setText("Printer Mileage: "+ ByteUtil.bytes2Int(outData)+" mm");
                    }else{
                        tvshow.setText("Fail");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        kilometres_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ret = -1;
                try {
                    byte[] outData = new byte[4];
                    ret = mPrinter.Get_ClearPrinterMileage(0x00,outData);
                    if(ret == 0){
                        tvshow.setText("Printer Mileage Clear success");
                    }else{
                        tvshow.setText("Fail");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        btnexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
