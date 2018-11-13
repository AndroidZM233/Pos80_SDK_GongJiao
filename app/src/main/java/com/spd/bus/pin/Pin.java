package com.spd.bus.pin;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spd.bus.R;

import wangpos.sdk4.base.ICallbackListener;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libbasebinder.RspCode;

public class Pin extends AppCompatActivity {
    Button btnb1, btnb2, btnb3, btnb4, btnb5, btnb6, btnb7, btnb8, btnb9, btnb0,
            btncancel, btnconfirm, btnclean,btndelete;

    Context mcontext = null;
    private Core mCore;
    Handler mHandler = null;
    private  boolean isClickCleanBtn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        mcontext = this;
        mHandler = new EventHandler();


        new Thread() {
            @Override
            public void run() {
                mCore = new Core(getApplicationContext());
            }
        }.start();
        findViewById(R.id.buttonstart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.textView)).setText("");
                new PINThread().start();
            }
        });
        findViewById(R.id.buttonexit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnb1 = (Button) findViewById(R.id.button1);
        btnb2 = (Button) findViewById(R.id.button2);
        btnb3 = (Button) findViewById(R.id.button3);
        btnb4 = (Button) findViewById(R.id.button4);
        btnb5 = (Button) findViewById(R.id.button5);
        btnb6 = (Button) findViewById(R.id.button6);
        btnb7 = (Button) findViewById(R.id.button7);
        btnb8 = (Button) findViewById(R.id.button8);
        btnb9 = (Button) findViewById(R.id.button9);
        btnb0 = (Button) findViewById(R.id.button0);
        btncancel = (Button) findViewById(R.id.buttoncan);
        btnconfirm = (Button) findViewById(R.id.buttonconfirm);
        btnclean = (Button) findViewById(R.id.buttonclean);
        btndelete = (Button) findViewById(R.id.buttondelete);

        btnclean.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        isClickCleanBtn = true;
                    break;
                }
                return false;
            }
        });
    }

    public class PINThread extends Thread {
        @Override
        public void run() {
            isClickCleanBtn = false;
            byte[] formatdata = new byte[8];
            String pan = "1111111111111111";
            int ret = -1;
            try {
                ret = mCore.startPinInput(60, "app1", 1, 4, 12, 0x00, formatdata, pan.length(), pan.getBytes("UTF-8"), callback);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ret != RspCode.OK) {
                // do some error notify
                //finish();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //newConfig.orientation获得当前屏幕状态是横向或者竖向
        //Configuration.ORIENTATION_PORTRAIT 表示竖向
        //Configuration.ORIENTATION_LANDSCAPE 表示横屏
        if(mAllowSendRotationCommand){
            try {
                Log.e("zys", "1+++++++++++");
                mCore.pinPadRotation();
                Log.e("zys", "2===========");
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private boolean mAllowSendRotationCommand = false;

    class EventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bd = null;
            byte[] data = null;
            switch (msg.what) {
                case 1:
                    // PIN input process start, secure chip generated random key sequence need display.
                    bd = msg.getData();
                    data = bd.getByteArray("data");
                    String displaynumber = null;
                    displaynumber = "" + (data[4] - 0x30);
                    btnb1.setText(displaynumber);

                    displaynumber = "" + (data[5] - 0x30);
                    btnb2.setText(displaynumber);

                    displaynumber = "" + (data[6] - 0x30);
                    btnb3.setText(displaynumber);

                    displaynumber = "" + (data[7] - 0x30);
                    btnb4.setText(displaynumber);

                    displaynumber = "" + (data[8] - 0x30);
                    btnb5.setText(displaynumber);

                    displaynumber = "" + (data[9] - 0x30);
                    btnb6.setText(displaynumber);

                    displaynumber = "" + (data[10] - 0x30);
                    btnb7.setText(displaynumber);

                    displaynumber = "" + (data[11] - 0x30);
                    btnb8.setText(displaynumber);

                    displaynumber = "" + (data[12] - 0x30);
                    btnb9.setText(displaynumber);

                    displaynumber = "" + (data[13] - 0x30);
                    btnb0.setText(displaynumber);
                    break;
                case 2:
                    // User input, need show corresponding amount of stars *
                    bd = msg.getData();
                    data = bd.getByteArray("data");
                    int count = data[1];
                    String stars = "";
                    for (int i = 0; i < count; i++) {
                        stars += "*";
                    }
                    ((TextView) findViewById(R.id.textView)).setText(stars);
                    break;
                case 3:
                    // Input PIN process is finished.
                    RestoreKeyPad();
                    bd = msg.getData();
                    data = bd.getByteArray("data");
                    //success
                    if (data[1] == mCore.PIN_QUIT_SUCCESS) {
                        //No PIN upload
                        if (data[2] == mCore.PIN_QUIT_NOUPLOAD) {
                            ((TextView) findViewById(R.id.textView)).setText("No PIN inputed");
                        }
                        //Pain PIN
                        //only for test mode
                        else if (data[2] == mCore.PIN_QUIT_PAINUPLOAD) {
                            int pinlen = data[3];
                            Log.e("PINPad", "Pain pinlen is " + pinlen);
                            byte[] PINData = new byte[pinlen + 1];
                            PINData[0] = data[1];
                            System.arraycopy(data, 4, PINData, 1, pinlen);
                            String strpin = new String(PINData);
                            ((TextView) findViewById(R.id.textView)).setText(strpin);
                        }
                        //Encrypt PIN
                        else if (data[2] == mCore.PIN_QUIT_PINBLOCKUPLOAD) {
                            int pinlen = data[3];
                            Log.e("PINPad", "Encrypt pinlen is " + pinlen);
                            byte[] PINData = new byte[pinlen + 1];
                            PINData[0] = data[1];
                            System.arraycopy(data, 4, PINData, 1, pinlen);
                            String strpin = new String(PINData);
                            ((TextView) findViewById(R.id.textView)).setText(strpin);
                        }
                    }
                    //Calcel
                    else if (data[1] == mCore.PIN_QUIT_CANCEL) {

                        if(isClickCleanBtn){
                            isClickCleanBtn = false;
                            ((TextView) findViewById(R.id.textView)).setText("User cleaned");
                        }else {
                            ((TextView) findViewById(R.id.textView)).setText("User cancelled");
                        }

                    }
                    //bypass
                    else if (data[1] == mCore.PIN_QUIT_BYPASS) {
                        ((TextView) findViewById(R.id.textView)).setText("bypass");
                    }
                    //error
                    else if (data[1] == mCore.PIN_QUIT_ERROR) {
                        ((TextView) findViewById(R.id.textView)).setText("Error");
                    }
                    //timeout
                    else if (data[1] == mCore.PIN_QUIT_TIMEOUT) {
                        ((TextView) findViewById(R.id.textView)).setText("Timeout");
                    }
                    //no PAN
                    else if (data[1] == mCore.PIN_QUIT_ERRORPAN) {
                        ((TextView) findViewById(R.id.textView)).setText("No PAN");
                    }
                    //others
                    else {
                        ((TextView) findViewById(R.id.textView)).setText("Other Error");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private ICallbackListener callback = new ICallbackListener.Stub() {
        @Override
        public int emvCoreCallback(int command, byte[] data, byte[] result, int[] resultlen) throws RemoteException {
            Log.e("zys", "callback ****************");
            if (command != mCore.CALLBACK_PIN)
                return -1;
            if (data[0] == mCore.PIN_CMD_PREPARE) {
                mAllowSendRotationCommand = true;
                Log.e("PINPad", "Pin pad init data len is " + data.length);

                Message msg = new Message();
                msg.what = 1;
                Bundle bd = new Bundle();
                bd.putByteArray("data", data);
                msg.setData(bd);
                if (mHandler != null)
                    mHandler.sendMessage(msg);
                //键盘初始化按钮布局坐标(Keyboard initialization button layout coordinates)
                try {
                    mCore.generatePINPrepareData(result, btnb1, btnb2, btnb3, btnb4, btnb5,
                            btnb6, btnb7, btnb8, btnb9, btnb0, btncancel,
                            btnconfirm, btnclean,btndelete, (Activity) mcontext);
                    if (btndelete != null) {
                        resultlen[0] = 113;
                    }else {
                        resultlen[0] = 105;
                    }
                } catch (Exception e) {
                    Log.e("PINPad", "mReceiver RemoteException " + e.toString());
                }
            } else if (data[0] == mCore.PIN_CMD_UPDATE) {
                result[0] = 0;
                resultlen[0] = 1;

                Message msg = new Message();
                msg.what = 2;
                Bundle bd = new Bundle();
                bd.putByteArray("data", data);
                msg.setData(bd);
                if (mHandler != null)
                    mHandler.sendMessage(msg);
            } else if (data[0] == mCore.PIN_CMD_QUIT) {
                mAllowSendRotationCommand = false;
                result[0] = 0;
                resultlen[0] = 1;

                Message msg = new Message();
                msg.what = 3;
                Bundle bd = new Bundle();
                bd.putByteArray("data", data);
                msg.setData(bd);
                if (mHandler != null)
                    mHandler.sendMessage(msg);
            }
            return 0;
        }
    };

    private void RestoreKeyPad() {
        btnb1.setText("1");
        btnb2.setText("2");
        btnb3.setText("3");
        btnb4.setText("4");
        btnb5.setText("5");
        btnb6.setText("6");
        btnb7.setText("7");
        btnb8.setText("8");
        btnb9.setText("9");
        btnb0.setText("0");
    }
}
