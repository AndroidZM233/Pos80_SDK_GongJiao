package com.spd.bus.dock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spd.bus.R;
import com.spd.bus.util.keyrandom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import wangpos.sdk4.libbasebinder.Dock;

//import com.weipass.sdkmanager.dock;

public class DockTest extends Activity {
    TextView tvdockshow, statusshow;
    byte[] read_data = new byte[1];
    int[] len = new int[1];
    boolean statuscheck = false;
    Handler mHandler;
    Context mContext;
    Button btndocktest, btnddockversion, btndockupdate, btndocksend, btndockexit;
    int i = 0;

    private Dock mDock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.docktest);
        mContext = this;
        copyImage2Data(R.mipmap.wang1, "im1.jpg");
        copyImage2Data(R.mipmap.wang2, "im2.jpg");
        tvdockshow = (TextView) findViewById(R.id.tvdockshow);
        statusshow = (TextView) findViewById(R.id.statusshow);
        btndocktest = (Button) findViewById(R.id.btndocktest);
        btnddockversion = (Button) findViewById(R.id.btnddockversion);
        btndockupdate = (Button) findViewById(R.id.btndockupdate);
        btndocksend = (Button) findViewById(R.id.btndocksend);
        btndockexit = (Button) findViewById(R.id.btndockexit);
        mHandler = new EventHandler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mDock = new Dock(DockTest.this.getApplicationContext());
            }
        }).start();

        Thread thread = new StatusThread();
        thread.start();

        btndocktest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentdocktest = new Intent(DockTest.this, Docktesttipsshow.class);
                startActivity(intentdocktest);
                tvdockshow.setText("");
            }
        });
        btnddockversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] read_data = new byte[32];
                int[] len = new int[1];
                int i = 0;
                try {
                    i = mDock.version(read_data, len);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Log.v("----zhangjinglog----", "version" + i + "--readdata" + read_data);
                tvdockshow.setText("version:" + new String(read_data));
            }
        });

        btndockupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentupdate = new Intent(DockTest.this, Dockupdate.class);
                startActivity(intentupdate);
            }
        });

        btndocksend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentsend = new Intent(DockTest.this, Docksendpicture.class);
                startActivity(intentsend);
            }
        });
        btndockexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                statuscheck = true;
            }
        });
    }

    //把图片文件复制到指定目录//PicID是drawable的图片资源ID
    public void copyImage2Data(Integer PicID, String LogoFileName) {
        Log.d("zhangjinglog", "mythou copyImage2Data----->Enter PicID=" + PicID);

        try {
            String LogoFileRoot = "/sdcard/Pictures/";
            //计算图片存放全路径
            String LogoFilePath = LogoFileRoot + LogoFileName;
            File dir = new File(LogoFileRoot);
            //如果文件夹不存在，创建一个（只能在应用包下面的目录，其他目录需要申请权限 OWL）
            if (!dir.exists()) {
                Log.d("zhangjinglog", "mythou copyImage2Data----->dir not exist");
            }

            boolean result = dir.mkdirs();
            Log.d("zhangjinglog", "dir.mkdirs()----->result = " + result);

            // 获得封装  文件的InputStream对象
            InputStream is = mContext.getResources().openRawResource(PicID);

            Log.d("zhangjinglog", "copyImage2Data----->InputStream open");

            FileOutputStream fos = new FileOutputStream(LogoFilePath);

            byte[] buffer = new byte[8192];
            System.out.println("3");
            int count = 0;

            // 开始复制Logo图片文件
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
                System.out.println("4");
            }
            fos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class StatusThread extends Thread {
        public void run() {
            mDock = new Dock(DockTest.this.getApplicationContext());
            while (!statuscheck) {
                try {
                    mDock.status(read_data, len);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                i++;
                if (keyrandom.bytesToHexString(read_data).equals("01")) {
                    Message msg = new Message();
                    msg.what = 1001;
                    msg.obj = i + "";
                    mHandler.sendMessage(msg);
                } else if (keyrandom.bytesToHexString(read_data).equals("00")) {
                    Message msg = new Message();
                    msg.what = 1002;
                    msg.obj = i + "";
                    mHandler.sendMessage(msg);
                }

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class EventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    statusshow.setText("Connection" + msg.obj);
                    btndocktest.setEnabled(true);
                    btnddockversion.setEnabled(true);
                    btndockupdate.setEnabled(true);
                    btndocksend.setEnabled(true);
                    btndockexit.setEnabled(true);
                    break;
                case 1002:
                    statusshow.setText("Unconnection" + msg.obj);
                    break;
                default:
                    break;
            }
        }
    }
}
