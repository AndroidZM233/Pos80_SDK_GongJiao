package com.wpos.sdkdemo.sp;

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

import wangpos.sdk4.libbasebinder.Updatesp;

public class Spupdate extends Activity {
    TextView twztviewupspshow;
    Handler mHandler;
    int ret = 1;
    private Updatesp mUpdatesp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upsp);
        twztviewupspshow = (TextView)findViewById(R.id.twztviewupspshow);
        mHandler = new EventHandler();
        new Thread() {
            @Override
            public void run() {
                mUpdatesp = new Updatesp(getApplicationContext());
            }
        }.start();
        ((Button)findViewById(R.id.btnupspstart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("zhangjing log ","start update sp");
                twztviewupspshow.setText("waiting ...");
                Thread threadsp = new UpdatespThread();
                threadsp.start();
            }
        });
        ((Button)findViewById(R.id.btnupspexit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public class EventHandler extends Handler {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    Log.v("------str1-----",(String)msg.obj);
                    twztviewupspshow.setText((String)msg.obj);
                    break;
                case 1002:
                    Log.v("------str2-----",(String)msg.obj);
                    twztviewupspshow.setText((String)msg.obj);
                    break;
                case 1003:
                    Log.v("------str3-----",(String)msg.obj);
                    twztviewupspshow.setText((String)msg.obj);
                    break;
                default:
                    break;
            }
        }
    }

    public class UpdatespThread extends Thread {
        public void run(){
            Log.v("zhangjing log updatesp","--start thread");
            try {
                ret = mUpdatesp.updatesp(null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            String[] retstatus = new String[2];
            Log.v("----zhangjing logstatus",""+retstatus[0]+retstatus[1]+"//"+retstatus);
            Log.v("-----------------",""+ret);
            String str1 = "update sp success\n滴声后完成更新";
            String str2 = "update sp fail";
            String str3 = "更新完成";
            if(ret == 0){
                Message msg = new Message();
                msg.what = 1001;
                msg.obj = str1;
                mHandler.sendMessage(msg);
            }else if(ret == -1){
                Message msg1 = new Message();
                msg1.what = 1002;
                msg1.obj = str2;
                mHandler.sendMessage(msg1);
            }else if(ret == 2){
                Message msg2 = new Message();
                msg2.what = 1003;
                msg2.obj = str3;
                mHandler.sendMessage(msg2);
            }
        }
    }
}
