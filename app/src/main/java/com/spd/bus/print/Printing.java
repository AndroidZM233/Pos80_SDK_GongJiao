package com.spd.bus.print;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.spd.bus.R;

import java.io.IOException;
import java.io.InputStream;

import wangpos.sdk4.libbasebinder.Printer;
import wangpos.sdk4.libbasebinder.Printer.Align;
import wangpos.sdk4.libbasebinder.RspCode;

public class Printing extends AppCompatActivity {
    private System mSystem;
    private Printer mPrinter;

    private boolean bloop = false;
    private boolean bthreadrunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
        new Thread() {
            @Override
            public void run() {
                mPrinter = new Printer(getApplicationContext());
                try {
                    mPrinter.setPrintFontType(Printing.this, "");//fonnts/PraduhhTheGreat.ttf
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        findViewById(R.id.buttonprinter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloop = false;
                if (bthreadrunning == false)
                    new PrintThread().start();
            }
        });

        findViewById(R.id.buttonpres).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloop = true;
                findViewById(R.id.buttonprinter).setEnabled(false);
                findViewById(R.id.buttonpres).setEnabled(false);
                new PrintThread().start();
            }
        });

        findViewById(R.id.buttonexitprint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloop = false;
                finish();
            }
        });

        registerReceiver(mInfoReceiver, new IntentFilter("com.wpos.printer_card"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mInfoReceiver != null) {
            unregisterReceiver(mInfoReceiver);
        }
    }

    private void testPrintLaguage(int result){
        try {
            result = mPrinter.printString("A number of national languages, for example：", 22, Align.LEFT, false, false);
            //法语
            result = mPrinter.printString("Bonjour Comment ça va Au revoir!", 25, Align.CENTER, false, false);
            //德语
            result = mPrinter.printString("Guten Tag Wie geht's?Auf Wiedersehen.", 25, Align.CENTER, false, false);
            //阿拉伯语
            result = mPrinter.printString(" في متصفحه. عبارة اصفحة ارئيسية تستخدم أيضاً إشا", 25, Align.CENTER, false, false);
            //乌克兰语
            result = mPrinter.printString("Доброго дня Як справи? Бувайте!", 25, Align.CENTER, false, false);
            //格鲁吉亚语
            result = mPrinter.printString("გამარჯობა（gamarǰoba）კარგად（kargad）", 25, Align.CENTER, false, false);
            //韩语
            result = mPrinter.printString("안녕하세요 잘 지내세요 안녕히 가세요!", 25, Align.CENTER, false, false);
            //日语
            result = mPrinter.printString("こんにちは お元気ですか またね！", 25, Align.CENTER, false, false);
            //印尼语
            result = mPrinter.printString("Selamat Pagi/Siang Apa kabar? Sampai nanti!", 25, Align.CENTER, false, false);
            //南非荷兰语
            result = mPrinter.printString("Goeie dag Hoe gaan dit? Totsiens!", 25, Align.CENTER, false, false);
            //....
        } catch (RemoteException e){
            e.printStackTrace();
        }

    }

    private void testPrintString(int result){
        try {
            //default content print
            result = mPrinter.printString("www.wiseasy.com", 25, Align.CENTER, true, false);
            result = mPrinter.printString("北京微智全景信息技术有限公司",25,Align.CENTER,false,false);
            result = mPrinter.printString("  ", 30, Align.CENTER, false,false);
            result = mPrinter.printString("--------------------------------------------", 30, Align.CENTER, false, false);
            result = mPrinter.printString("Meal Package:KFC $100 coupons", 25, Align.LEFT, false, false);
            result = mPrinter.printString("Selling Price:$90", 25, Align.LEFT, false, false);
            result = mPrinter.printString("Merchant Name:KFC（ZS Park）", 25, Align.LEFT, false, false);
            result = mPrinter.printString("Payment Time:17/3/29 9:27", 25, Align.LEFT, false, false);
            result = mPrinter.printString("--------------------------------------------", 30, Align.CENTER, false, false);
            result = mPrinter.printString("NO. of Coupons:5", 25, Align.LEFT, false, false);
            result = mPrinter.printString("Total Amount:$450", 25, Align.LEFT, false,false);
            result = mPrinter.printString("SN:1234 4567 4565,", 25, Align.LEFT, false, false);
            //The content is too long and automatically moves to the next line
            result = mPrinter.printString("1、content too long and moves to the next line automatically ，，，", 25, Align.LEFT, false, false);
            //The content is too long to move to the next line,According to the set lineSpacing display
            result = mPrinter.printStringExt("2、content too long but not move to next line，，，", 0,0f,1.0f, Printer.Font.SERIF, 25,Align.LEFT,false,false,false);
            //font style print
            result = mPrinter.printStringExt("Default Bold Font ",0,0f,2.0f, Printer.Font.DEFAULT,30, Align.LEFT,false, false, false);//Default Font
            result = mPrinter.printStringExt("Default Bold Font ", 0,0f,2.0f, Printer.Font.DEFAULT_BOLD, 30,Align.CENTER,false,false,false);
            result = mPrinter.printStringExt("Monospace Font ", 0,0f,2.0f, Printer.Font.MONOSPACE, 30,Align.RIGHT,false,false,false);
            result = mPrinter.printStringExt("Sans Serif Font ", 0,0f,1.0f, Printer.Font.SANS_SERIF, 30,Align.LEFT,false,false,false);
            result = mPrinter.printStringExt("Sans Serif Font", 0,0f,1.0f, Printer.Font.SERIF, 25,Align.LEFT,true,false,false);

            //two content left and right in one line
            result = mPrinter.print2StringInLine("left","right",1.0f,Printer.Font.DEFAULT,25,Align.LEFT,false, false, false);

        } catch (RemoteException e){
            e.printStackTrace();
        }

    }

    private void testPrintImageBase(String pic){
        try {
            InputStream inputStream = null;
            Bitmap bitmap = null;
            if (pic.equals("logo")) {
                inputStream = getAssets().open("logo.png");
                bitmap =  BitmapFactory.decodeStream(inputStream);
                mPrinter.printImageBase(bitmap, 100, 100, Align.LEFT, 0);
            }else if (pic.equals("wiseasy")) {
                inputStream = getAssets().open("wechat.png");
                bitmap =  BitmapFactory.decodeStream(inputStream);
                mPrinter.printImageBase(bitmap, 300, 300, Align.CENTER, 0);
            }
            bitmap.recycle();
        }catch (IOException e){
            e.printStackTrace();
        }catch (RemoteException ex){
            ex.printStackTrace();
        }
    }

    /**
     * PrinterControl(int command,请求状态，必需域	1	HEX1	0x01 开始打印（打开打印机）
     0x02 打印中，用于数据传输
     0x03 走纸
     0x0A 结束打印（关闭打印机）
     * int length,打印数据长度/走纸长度，必需域	N	HEX2	最大1024，高字节在前，开始和结束时如果无数据，传0
     * byte[] data)打印点阵数据（可选域）	N	HEX	开始和结束也可以传输数据
     */
    public class PrintThread extends Thread {
        @Override
        public void run () {
            bthreadrunning = true;
            int datalen = 0;
            int result = 0;
            byte[] senddata = null;
            do {
                try {
                    result = mPrinter.printInit();
                    //clear print cache
                    mPrinter.clearPrintDataCache();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    testPrintImageBase("logo");
                    // Print text
                    testPrintString(result);
                    // print bar_Code
                    result = mPrinter.printBarCodeBase("1234567890abcdefg", Printer.BarcodeType.CODE_128, Printer.BarcodeWidth.LARGE, 50, 20);
                    //print QR_Code(text)
                    result = mPrinter.printQRCode("http://www.wangpos.com/",400);
//                    testPrintImageBase("wiseasy");
                    //laguage print
                    testPrintLaguage(result);
                    //print end reserve height
                    result = mPrinter.printPaper(100);
                    //Detecting the in-place status of the card during printing
//                    mPrinter.printPaper_trade(5,100);
                    if (result != RspCode.OK)
                        bloop = false;
                    result = mPrinter.printFinish();
                    if (result != RspCode.OK)
                        bloop = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } while (bloop);
            bthreadrunning = false;
        }
    }
    private BroadcastReceiver mInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int res = intent.getIntExtra("printer_c",0);
            Log.e("TAG","res==="+res);
            //do something
        }
    };
}