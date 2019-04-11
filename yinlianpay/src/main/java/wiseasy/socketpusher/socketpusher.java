//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package wiseasy.socketpusher;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import com.spd.yinlianpay.context.MyContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class socketpusher {
    static final String TAG = "cn.wiseasy.socketpusher";
    private static Socket SendSocket = null;
    private static OutputStream outputStream = null;
    private static InputStream inputStream = null;
    private static final boolean DEBUG = true;
    private volatile Looper mServiceLooper;
    private volatile socketpusher.ServiceHandler h;

    public socketpusher() {
        Log.i(TAG, "socketpusher: ");
        HandlerThread thread = new HandlerThread("IntentService[socketpusher]");
        thread.start();
        this.mServiceLooper = thread.getLooper();
        this.h = new socketpusher.ServiceHandler(this.mServiceLooper);
    }

    public boolean initSocket() {
        try {
            if(SendSocket == null) {
                SendSocket = new Socket("127.0.0.1", 23801);
                outputStream = SendSocket.getOutputStream();
                inputStream = SendSocket.getInputStream();
                return true;
            }

            Log.e("cn.wiseasy.socketpusher", "init socket : socket not closed");
            return true;
        } catch (UnknownHostException var2) {
            Log.e("cn.wiseasy.socketpusher", "unknown host exception: " + var2.toString());
        } catch (IOException var3) {
            Log.e("cn.wiseasy.socketpusher", "io exception: " + var3.toString());
        } catch (Exception var4) {
            Log.e("cn.wiseasy.socketpusher", "exception: " + var4.toString());
        }

        return false;
    }

    public boolean SendLog(byte[] senddata, int sendlen) {
        try {
            if(!this.initSocket()) {
                return false;
            }

            Log.e("cn.wiseasy.socketpusher", "send len: " + sendlen);
            outputStream.write(senddata, 0, sendlen);
            Log.e("cn.wiseasy.socketpusher", "send finish" + sendlen);
            outputStream.flush();
            SendSocket.setSoTimeout(2000);
            if(inputStream.read() != -1) {
                return true;
            }
        } catch (IOException var4) {
            Log.e("cn.wiseasy.socketpusher", "io exception: " + var4.toString());
            this.closeSocket();
        }

        return false;
    }

    public boolean SendLog(String log) {
        try {

            return this.SendLog(log.getBytes("GB2312"), log.getBytes("GB2312").length);
        } catch (UnsupportedEncodingException var7) {
            Log.e("cn.wiseasy.socketpusher", "unsupport encode type " + var7.toString());
            return false;
        }
    }

    public boolean SendLogToPC(String log) {
        Message msg = new Message();
        msg.obj = log;
        this.h.sendMessage(msg);
        return true;
    }

    public void closeSocket() {
        try {
            outputStream.close();
            outputStream = null;
            inputStream.close();
            inputStream = null;
            SendSocket.close();
            SendSocket = null;
        } catch (IOException var2) {
            Log.e("cn.wiseasy.socketpusher", "io exception: " + var2.toString());
        } catch (Exception var3) {
            Log.e("cn.wiseasy.socketpusher", "exception: " + var3.toString());
        }

    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            long e = System.currentTimeMillis();
            Log.e("cn.wiseasy.socketpusher", "======= " + e);
            Date nowTime = new Date(System.currentTimeMillis());
            Log.e("cn.wiseasy.socketpusher", "======= " + nowTime);
            SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            String retStrFormatNowDate = sdFormatter.format(nowTime);
            String log = retStrFormatNowDate + ";" + msg.obj.toString();
            Log.i(TAG, "SendLog: "+log);
            String broadcastIntent = "com.sxk.sendlog";//自己自定义
            Intent intent = new Intent(broadcastIntent);
            Bundle bundle = new Bundle();
            bundle.putInt("type",0);
            bundle.putString("sendlog",log);
            intent.putExtra("sendlog",bundle);
            MyContext.context.sendBroadcast(intent);
            Log.i(TAG, "handleMessage: sendlog");
            socketpusher.this.SendLog(log);
        }
    }
}
