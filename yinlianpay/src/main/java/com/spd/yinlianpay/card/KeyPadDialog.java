package com.spd.yinlianpay.card;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.spd.yinlianpay.R;
import com.spd.yinlianpay.WeiPassGlobal;
import com.spd.yinlianpay.context.MyContext;

import java.util.Arrays;

import ui.wangpos.com.utiltool.ByteUtil;
import ui.wangpos.com.utiltool.HEXUitl;
import wangpos.sdk4.base.ICallbackListener;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libbasebinder.RspCode;


public class KeyPadDialog {
    Core mCore;
    Button btnb1, btnb2, btnb3, btnb4, btnb5, btnb6, btnb7, btnb8, btnb9, btnb0,
            btncancel, btnconfirm, btnclean;
    View view;
    Dialog dialog;

    Context mcontext = null;

    Handler mHandler = null;
    static KeyPadDialog keypad;
    private ICallbackListener callback;
    public static abstract class OnPinPadListener
    {
       protected  abstract void onSuccess();
       protected  abstract void onSuccess(String pin);
       protected void onError(){}
       protected void onError(String errorMsg){
            onError();
        }
       protected void onError(int errorCode,String errorMsg){
            onError(errorMsg);
        }
    }

    private static OnPinPadListener tmponPinPadListener;
    public static KeyPadDialog getInstance() {
        if(keypad == null )
        {
            keypad = new KeyPadDialog();
        }
        return keypad;

    }

    public KeyPadDialog() {
//        init();
    }

    public void showDialog(final Activity context, final OnPinPadListener onPinPadListener)
    {
        tmponPinPadListener = onPinPadListener;
//        Looper.prepare();
        mHandler = new EventHandler();
        mCore = MyContext.mCore;
        /*new Thread() {
            @Override
            public void run() {
                mCore = new Core(context.getApplicationContext());
            }
        }.start();*/

        dialog = new Dialog(context);
        view = LayoutInflater.from(context).inflate(R.layout.layout_pin,null);
        btnb1 = (Button) view.findViewById(R.id.button1);
        btnb2 = (Button) view.findViewById(R.id.button2);
        btnb3 = (Button) view.findViewById(R.id.button3);
        btnb4 = (Button) view.findViewById(R.id.button4);
        btnb5 = (Button) view.findViewById(R.id.button5);
        btnb6 = (Button) view.findViewById(R.id.button6);
        btnb7 = (Button) view.findViewById(R.id.button7);
        btnb8 = (Button) view.findViewById(R.id.button8);
        btnb9 = (Button) view.findViewById(R.id.button9);
        btnb0 = (Button) view.findViewById(R.id.button0);
        btncancel = (Button) view.findViewById(R.id.buttoncan);
        btnconfirm = (Button) view.findViewById(R.id.buttonconfirm);
        btnclean = (Button) view.findViewById(R.id.buttonclean);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity( Gravity.BOTTOM);

//        dialogWindow.setWindowAnimations(R.style.dialogstyle);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        view.measure(0, 0);
        lp.height = view.getMeasuredHeight();
        lp.alpha = 9f;
        dialogWindow.setAttributes(lp);
//        view.setVisibility(View.INVISIBLE);
        dialog.setContentView(view);
        if (!context.isFinishing() || !context.isDestroyed())
        dialog.show();
        Log.v("button",btnb0.getY()+"---"+btnb0.getX()+"----"+btnb0.getPivotX()+"----"+btnb0.getPivotX());
        callback = new ICallbackListener.Stub(){
            @Override
            public int emvCoreCallback(int command, byte[] data, byte[] result, int[] resultlen) throws RemoteException {
                Log.e("dialog emvCoreCallback"," command:"+command+"\tdata"+data[0]+mHandler);
                if (command != mCore.CALLBACK_PIN)
                    return -1;
                if (data[0] == mCore.PIN_CMD_PREPARE) {
                    Log.e("PINPad", "pin pad init data len is " + data.length);

                    Message msg = new Message();
                    msg.what = 1;
                    Bundle bd = new Bundle();
                    bd.putByteArray("data", data);
                    msg.setData(bd);
                    Log.i("KeyPadDialog", "PIN_CMD_PREPARE: "+new String(data)+"---"+data[1]);
                    if (mHandler != null)
                        mHandler.sendMessage(msg);

                    try {
                        mCore.generatePINPrepareData(result, btnb1, btnb2, btnb3, btnb4, btnb5,
                                btnb6, btnb7, btnb8, btnb9, btnb0, btncancel,
                                btnconfirm, btnclean,  context);
                        resultlen[0]=105;
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
                    Log.i("KeyPadDialog", "PIN_CMD_UPDATE: "+new String(data));
                    msg.setData(bd);
                    if (mHandler != null)
                        mHandler.sendMessage(msg);
                } else if (data[0] == mCore.PIN_CMD_QUIT) {
                    Log.i("KeyPadDialog", "emvCoreCallback: "+mCore.PIN_CMD_QUIT+"---"+mHandler);
                    result[0] = 0;
                    resultlen[0] = 1;

                    Message msg = new Message();
                    msg.what = 3;
                    Bundle bd = new Bundle();
                    bd.putByteArray("data", data);
                    Log.i("KeyPadDialog", "PIN_CMD_QUIT: "+data[1]+"--->"+ ByteUtil.bytes2HexString(data));
                    msg.setData(bd);
                    if(data[1]==0)
                    {
                        String pin =  ByteUtil.bytes2HexString(Arrays.copyOfRange(data,4,4+data[3]));
                        Log.i("KeyPadDialog", "pin data len: "+ Arrays.copyOfRange(data,4,4+data[3]).length+"pin data"+pin);
                        WeiPassGlobal.getTransactionInfo().setPin(pin);
                        Log.i("KeyPadDialog", "emvCoreCallback: "+WeiPassGlobal.getTransactionInfo().getPin()+"-->"+pin);
                        UnionPayCard.inPutPinCode = 1;
                        //onPinPadListener.onSuccess();
                    }
                    if (mHandler != null)
                        mHandler.sendMessage(msg);

                }

                return 0;
            }
        };

        ((TextView)view.findViewById(R.id.textView)).setText("");
        btnclean.setText("cancel");
        ((TextView) view.findViewById(R.id.textView)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()==0)
                    btnclean.setText("cancel");
                else
                    btnclean.setText("clear");
            }
        });
        new PINThread().start();


        btnclean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.buttonexit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
//Looper.loop();
    }



    public int showDialog(final Activity context, final int command, final byte[] data, final byte[] result, final int[] resultlen, final OnPinPadListener onPinPadListener)
    {

        tmponPinPadListener = onPinPadListener;
//        Looper.prepare();
        Log.e("emvcore","showDialog currentThread().getName"+ Thread.currentThread().getName());
        mHandler = new EventHandler();
       /* new Thread() {
            @Override
            public void run() {
                mCore = new Core(context.getApplicationContext());
            }
        }.start();*/
        mCore = MyContext.mCore;
        if(data[0]!=0x01&&dialog!=null&&dialog.isShowing())
        {
            if (command != mCore.CALLBACK_PIN) {
                //onPinPadListener.onSuccess();
                return -1;
            }
            if (data[0] == mCore.PIN_CMD_PREPARE) {
                Log.e("PINPad", "pin pad init data len is " + data.length);

                Message msg = new Message();
                msg.what = 1;
                Bundle bd = new Bundle();
                bd.putByteArray("data", data);
                msg.setData(bd);
                if (mHandler != null)
                    mHandler.sendMessage(msg);

                try {
                    mCore.generatePINPrepareData(result, btnb1, btnb2, btnb3, btnb4, btnb5,
                            btnb6, btnb7, btnb8, btnb9, btnb0, btncancel,
                            btnconfirm, btnclean,  context);
                    resultlen[0]=105;
                   // onPinPadListener.onSuccess();
                } catch (Exception e) {
                    Log.e("PINPad", "mReceiver RemoteException " + e.toString());
                    //onPinPadListener.onSuccess();
                }
            } else if (data[0] == mCore.PIN_CMD_UPDATE) {
                result[0] = 0;
                resultlen[0] = 1;

                Message msg = new Message();
                msg.what = 2;
                Bundle bd = new Bundle();
                bd.putByteArray("data", data);
                msg.setData(bd);
                //onPinPadListener.onSuccess();
                if (mHandler != null)
                    mHandler.sendMessage(msg);
                onPinPadListener.onSuccess();

            } else if (data[0] == mCore.PIN_CMD_QUIT) {
                result[0] = 0;
                resultlen[0] = 1;

                Message msg = new Message();
                msg.what = 3;
                Bundle bd = new Bundle();
                bd.putByteArray("data", data);
                msg.setData(bd);
                //onPinPadListener.onSuccess();
                if (mHandler != null)
                    mHandler.sendMessage(msg);
            }
        }
        else {

            view = LayoutInflater.from(context).inflate(R.layout.layout_pin, null);
//            if (dialog == null)
                dialog = new Dialog(context);
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    if (command != mCore.CALLBACK_PIN) {
                        //onPinPadListener.onSuccess();
                        return;
                    }
                    if (data[0] == mCore.PIN_CMD_PREPARE) {
                        Log.e("PINPad", "pin pad init data len is " + data.length);

                        Message msg = new Message();
                        msg.what = 1;
                        Bundle bd = new Bundle();
                        bd.putByteArray("data", data);
                        msg.setData(bd);
                        if (mHandler != null)
                            mHandler.sendMessage(msg);

                        try {
                            mCore.generatePINPrepareData(result, btnb1, btnb2, btnb3, btnb4, btnb5,
                                    btnb6, btnb7, btnb8, btnb9, btnb0, btncancel,
                                    btnconfirm, btnclean, context);
                            resultlen[0] = 105;
                            onPinPadListener.onSuccess();
                        } catch (Exception e) {
                            Log.e("PINPad", "mReceiver RemoteException " + e.toString());
                            //onPinPadListener.onSuccess();
                        }
                    } else if (data[0] == mCore.PIN_CMD_UPDATE) {
                        result[0] = 0;
                        resultlen[0] = 1;

                        Message msg = new Message();
                        msg.what = 2;
                        Bundle bd = new Bundle();
                        bd.putByteArray("data", data);
                        msg.setData(bd);
                        //onPinPadListener.onSuccess();
                        if (mHandler != null)
                            mHandler.sendMessage(msg);

                    } else if (data[0] == mCore.PIN_CMD_QUIT) {
                        result[0] = 0;
                        resultlen[0] = 1;

                        Message msg = new Message();
                        msg.what = 3;
                        Bundle bd = new Bundle();
                        bd.putByteArray("data", data);
                        msg.setData(bd);
                        //onPinPadListener.onSuccess();
                        if (mHandler != null)
                            mHandler.sendMessage(msg);
                    }
                }
            });
            btnb1 = (Button) view.findViewById(R.id.button1);
            btnb2 = (Button) view.findViewById(R.id.button2);
            btnb3 = (Button) view.findViewById(R.id.button3);
            btnb4 = (Button) view.findViewById(R.id.button4);
            btnb5 = (Button) view.findViewById(R.id.button5);
            btnb6 = (Button) view.findViewById(R.id.button6);
            btnb7 = (Button) view.findViewById(R.id.button7);
            btnb8 = (Button) view.findViewById(R.id.button8);
            btnb9 = (Button) view.findViewById(R.id.button9);
            btnb0 = (Button) view.findViewById(R.id.button0);
            btncancel = (Button) view.findViewById(R.id.buttoncan);
            btnconfirm = (Button) view.findViewById(R.id.buttonconfirm);
            btnclean = (Button) view.findViewById(R.id.buttonclean);
            Window dialogWindow = dialog.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);
            Log.v("button", btnb0.getY() + "---" + btnb0.getX() + "----" + btnb0.getPivotX() + "----" + btnb0.getPivotX());
//            dialogWindow.setWindowAnimations(R.style.dialogstyle);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.x = 0;
            lp.y = 0;
            view.measure(0, 0);
            lp.height = view.getMeasuredHeight();
            lp.alpha = 9f;
            dialogWindow.setAttributes(lp);
//        view.setVisibility(View.INVISIBLE);
            dialog.setContentView(view);
            Log.i("", "showDialog: context.isDestroyed()"+context.isDestroyed()+"context.isFinishing()"+context.isFinishing());
            if (!context.isFinishing() || !context.isDestroyed()) {
                try {
                    dialog.show();
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            Log.v("button show", "-----");


            ((TextView) view.findViewById(R.id.textView)).setText("");


            view.findViewById(R.id.buttonexit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
//        Looper.loop();
        }
            return 0;
        }


    public  class PINThread extends Thread {
        @Override
        public void run () {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte[] formatdata = new byte[8];
            String pan = WeiPassGlobal.getTransactionInfo().getCardNo();
            //StartPinInput(int timeouttime,    Timeout time for user input password
            //              int keyindex,       The key index for PEK, If use 0xFF will use corresponding
            //                                  key index for open card reader.
            //      btnclean.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(TextUtils.isEmpty(((TextView)findViewById(R.id.textView)).getText().toString()))
//                    finish();
//            }
//        });         int supportbypass,  Support bypass password (no password)
            //              int pinlenmin,     The minimal length for user input.（Must bigger than 3）
            //              int pinlenmax,      The maximum length for user input. (Must smaller than 13)
            //              int pinblockformat, 0x00 ISO9564 format0
            //                                  0x01 ISO9564 format1
            //                                  0x03 ISO9564 format3
            //              byte[] formatdata,  format data, this can be random number, transaction serial number,
            //                                  all zero if no need
            //              int panlen,         The length of PAN (Primary account number)
            //              byte[] pandata)     The data of PAN
            int ret = -1;
            try {
                ret = mCore.startPinInput(60, MyContext.keyPacketName, 0, 4, 12, 0x00, formatdata, pan.length(), pan.getBytes("UTF-8"), callback);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ret != RspCode.OK) {
                // do some error notify
                //finish();
            }
        }
    }
    private void init(){
        Looper.prepare();
        mHandler = new EventHandler();
//        new PINThread().start();
    }

    class EventHandler extends Handler {
        private String strpin;
        public EventHandler() {
        }

        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            Bundle bd = null;
            byte[] data = null;
            Log.i("EventHandler", "handleMessage: "+msg.what);
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
                    view.setVisibility(View.VISIBLE);
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
                    ((TextView)view.findViewById(R.id.textView)).setText(stars);
                    break;
                case 3:
                    dialog.cancel();
                    // Input PIN process is finished.
//                    RestoreKeyPad();
                    bd = msg.getData();
                    data = bd.getByteArray("data");
                    //Success
                    if (data[1] == mCore.PIN_QUIT_SUCCESS) {
                        //No PIN upload
                        UnionPayCard.inPutPinCode =1;
                        if (data[2] == mCore.PIN_QUIT_NOUPLOAD) {
                            ((TextView)view.findViewById(R.id.textView)).setText("No PIN inputed");
                            tmponPinPadListener.onSuccess();
                        }
                        //Pain PIN
                        //only for test mode
                        else if (data[2] == mCore.PIN_QUIT_PAINUPLOAD) {
                            int pinlen = data[3];
                            Log.e("PINPad", "Pain pinlen is " + pinlen);
                            byte[] PINData = new byte[pinlen];
                            PINData[0] = data[1];
                            System.arraycopy(data, 4, PINData, 0, pinlen);
                            strpin = HEXUitl.bytesToHex(PINData);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                 /*   try {
                                        MyContext.log.SendLogToPC("密码加密时间:" + Integer.valueOf(strpin.substring(strpin.length() - 2)) + "ms");
                                    }catch (Exception ex)
                                    {
                                        ex.printStackTrace();
                                    }*/
                                    tmponPinPadListener.onSuccess(strpin);
                                }
                            }).start();

                            ((TextView)view.findViewById(R.id.textView)).setText(strpin);
                        }
                        //Encrypt PIN
                        else if (data[2] == mCore.PIN_QUIT_PINBLOCKUPLOAD) {
                            int pinlen = data[3];
                            Log.e("PINPad", "Encrypt pinlen is " + pinlen);
                            byte[] PINData = new byte[pinlen];
                            PINData[0] = data[1];
                            System.arraycopy(data, 4, PINData, 0, pinlen);

                            strpin = HEXUitl.bytesToHex(PINData);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                  /*  try {
                                        MyContext.log.SendLogToPC("密码加密时间:" + Integer.valueOf(strpin.substring(strpin.length() - 2)) + "ms");
                                    }catch (Exception ex)
                                    {
                                        ex.printStackTrace();
                                    }*/
                                    tmponPinPadListener.onSuccess(strpin);
                                }
                            }).start();
                            ((TextView)view.findViewById(R.id.textView)).setText(strpin);
                        }
                    }
                    //Calcel
                    else if (data[1] == mCore.PIN_QUIT_CANCEL) {
                        tmponPinPadListener.onError(0,"cancel no pin");
                        ((TextView)view.findViewById(R.id.textView)).setText("User calceled");
                    }
                    //bypass
                    else if (data[1] == mCore.PIN_QUIT_BYPASS) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                tmponPinPadListener.onSuccess("");
                            }
                        }).start();

                        ((TextView)view.findViewById(R.id.textView)).setText("bypass");
                    }
                    //error
                    else if (data[1] == mCore.PIN_QUIT_ERROR) {
                        tmponPinPadListener.onError(mCore.PIN_QUIT_ERROR,"pin  error");
                        ((TextView)view.findViewById(R.id.textView)).setText("Error");
                    }
                    //timeout
                    else if (data[1] == mCore.PIN_QUIT_TIMEOUT) {
                        tmponPinPadListener.onError(mCore.PIN_QUIT_TIMEOUT,"pin Timeout");
                        ((TextView)view.findViewById(R.id.textView)).setText("Timeout");
                    }
                    //no PAN
                    else if (data[1] == mCore.PIN_QUIT_ERRORPAN) {
                        tmponPinPadListener.onError(mCore.PIN_QUIT_ERRORPAN,"no Card NO");
                        ((TextView)view.findViewById(R.id.textView)).setText("No PAN");
                    }
                    //others
                    else {
                        tmponPinPadListener.onError(-1,"Other Error");
                        ((TextView)view.findViewById(R.id.textView)).setText("Other Error");
                    }
                    break;
                default:
                    break;
            }
        }
    }
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
