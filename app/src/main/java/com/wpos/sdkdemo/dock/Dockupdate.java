package com.wpos.sdkdemo.dock;

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
import com.wpos.sdkdemo.util.keyrandom;

import wangpos.sdk4.libbasebinder.Dock;

public class Dockupdate extends Activity {
    TextView  updateshow;
    boolean startcheck = false;
    boolean startsucc = false;
    Handler mHandler;
    int i = 0;
    int j = 99;
    Button updatestart;
    private Dock mDock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dockupdate);
        mHandler = new EventHandler();
        updatestart = (Button)findViewById(R.id.updatestart);
        updateshow = (TextView)findViewById(R.id.updateshow);
        updatestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] read_data = new byte[1];
                int[] len = new int[1];
                try {
                    int k = mDock.updateResult(read_data , len);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if(keyrandom.bytesToHexString(read_data).equals("00")){
                    ///空闲
                    startsucc = false;
                    startcheck = false;
                    byte[] send_data = new byte[1];
                    send_data[0] = 0x01;
                    try {
                        j = mDock.updateStart(send_data , 1);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    Log.v("-----zhangjing log---","--"+j);
                    if(j == 1){
                        Log.v("zhangjinglog","btb enable false");
                        Log.v("zhangjing log","start thread");
                        updateshow.setText("Free");
                        Thread thread = new startThread();
                        startsucc = true;
                        thread.start();
                    }else {
                        updateshow.setText("Start error");
                    }
                }else if(keyrandom.bytesToHexString(read_data).equals("01")) {
                    //升级中
                    startcheck = false;
                    startsucc = false;
                    updateshow.setText("Updating");
                    Log.v("zhangjinglog","updating");
                    Thread thread = new startThread();
                    thread.start();
                }else if(keyrandom.bytesToHexString(read_data).equals("02")) {
                    //升级成功
                    startcheck = false;
                    startsucc = false;
                    updateshow.setText("Update success");
                    Log.v("zhangjinglog","updatesucc");
                    Thread thread = new startThread();
                    thread.start();
                }else if(keyrandom.bytesToHexString(read_data).equals("03")) {
                    //升级失败
                    startsucc = false;
                    startcheck = false;
                    updateshow.setText("Update fail");
                    Log.v("zhangjinglog","updatefail");
                    Thread thread = new startThread();
                    thread.start();
                }
            }

        });

        ((Button)findViewById(R.id.updateexit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startcheck = true;
                finish();
            }
        });

        new Thread(){
            @Override
            public void run() {
                mDock = new Dock(getApplicationContext());
            }
        }.start();
    }

    public class startThread extends Thread {
        public void run() {
            while (!startcheck){
                byte[] read_data = new byte[1];
                int[] len = new int[1];
                int j = -1;
                try {
                    j = mDock.updateResult(read_data , len);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Log.v("-----zhangjing log---","--"+j+"--readdata"+ keyrandom.bytesToHexString(read_data));
                i++;
                Log.v("zhangjinglog","--readdata");
                if(keyrandom.bytesToHexString(read_data).equals("00")){
                    //空闲
                    Log.v("zhangjinglog","--readdata1");
                    startcheck = true;
                    Message msg = new Message();
                    msg.what = 1000;
                    msg.obj = i + "";
                    mHandler.sendMessage(msg);
                }else if(keyrandom.bytesToHexString(read_data).equals("01")){
                    //升级中
                    Log.v("zhangjinglog","--readdata2");
                    startcheck = false;
                    Message msg = new Message();
                    msg.what = 1001;
                    msg.obj = i + "";
                    mHandler.sendMessage(msg);
                }else if(keyrandom.bytesToHexString(read_data).equals("02")){
                    //升级成功
                    Log.v("zhangjinglog","--readdata3");
                    if(startsucc == true){
                        startcheck = true;
                    }else{
                        startcheck = false;
                    }
                    Message msg = new Message();
                    msg.what = 1002;
                    msg.obj = i + "";
                    mHandler.sendMessage(msg);
                }else if(keyrandom.bytesToHexString(read_data).equals("03")){
                    //升级失败
                    Log.v("zhangjinglog","--readdata4");
                    if(startsucc == true){
                        startcheck = true;
                    }else{
                        startcheck = false;
                    }

                    Message msg = new Message();
                    msg.what = 1003;
                    msg.obj = i + "";
                    mHandler.sendMessage(msg);
                }

                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class EventHandler extends Handler {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    updateshow.setText("Free"+msg.obj);
                    break;
                case 1001:
                    updateshow.setText("Updating "+msg.obj);
                    break;
                case 1002:
                    updateshow.setText("Update success"+msg.obj);
                    break;
                case 1003:
                    updateshow.setText("Update fail"+msg.obj);
                    break;
                default:
                    break;
            }
        }
    }
}
