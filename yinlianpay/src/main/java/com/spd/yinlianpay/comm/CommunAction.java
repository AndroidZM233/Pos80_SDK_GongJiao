package com.spd.yinlianpay.comm;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.spd.base.utils.Datautils;
import com.spd.yinlianpay.dlg.ProcessDlg;
import com.spd.yinlianpay.iso8583.PayException;
import com.spd.yinlianpay.net.CCBBManage;
import com.spd.yinlianpay.util.PrefUtil;

import java.io.DataInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ui.wangpos.com.utiltool.HEXUitl;
import ui.wangpos.com.utiltool.Util;


/**
 * Created by guoxiaomeng on 2017/6/23.
 */

public class CommunAction {
    private static ScheduledThreadPoolExecutor timer = new ScheduledThreadPoolExecutor(2);
    private static int timeOut = 60;
    private static Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            ProcessDlg.showTvProgress(msg.what);
        }
    };
    public static String arpcvalue = "";
    private static final String TAG = CommunAction.class.getSimpleName();
    //数据是否发送成功
    public static boolean isSendSuccess = false;
    static ScheduledFuture sf = null;
    static TimerTask timerTask;

    public static byte[] doNet(byte[] send) throws Exception {
        timeOut = 60;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                timeOut--;
                if (timeOut <= 0) {
                    sf.cancel(true);
                    timer.remove(timerTask);
                    try {
                        sf.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(timeOut);
                    return;
                }
                handler.sendEmptyMessage(timeOut);
            }
        };

        isSendSuccess = false;
        {
            byte[] pre = Util.toBEShort(send.length);
            pre = Arrays.copyOf(pre, 2 + send.length);
            System.arraycopy(send, 0, pre, 2, send.length);
            send = pre;
        }
        byte[] rs;
        sf = timer.scheduleWithFixedDelay(timerTask
                , 0, 1, TimeUnit.SECONDS);
        if (PrefUtil.getIsSocket()) {
            rs = doSocket(send);
        } else {
            rs = CCBBManage.qiandao(send);
        }
        return rs;
    }

    private static byte[] doSocket(byte[] send) throws Exception {
        String ip = "";
        int port = 0;
        try {
            ip = PrefUtil.getIP();
            if (ip.length() <= 0) {
                throw new PayException("Server IP address configuration failed");
            }
            port = PrefUtil.getPort();
            if (port == 0) {
                throw new PayException("Server port configuration failed");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new PayException("Server address configuration exception");
        }
        String toHex = HEXUitl.bytesToHex(send);
        System.out.println("AllSend:" + ip + " " + port + " " + toHex);
        Socket soc = null;
        int timeout = (PrefUtil.getOverTimeInt()) * 1000;
        try {
            soc = new Socket();
            soc.setSoTimeout(timeout);
            soc.connect(new InetSocketAddress(ip, port), 10000);
//            soc.getOutputStream().write(Datautils.HexString2Bytes("003C600601000060320032401708000020000000C00012000121313030323038333133303836343030343131313030303300110000000200300003303031"));
            soc.getOutputStream().write(send);
            isSendSuccess = true;
            DataInputStream dis = new DataInputStream(soc.getInputStream());
            int len = dis.readShort();
            byte[] rs = new byte[len];
            dis.readFully(rs);
            String toHexRs = HEXUitl.bytesToHex(rs);
            System.out.println("read data:" + HEXUitl.bytesToHex(rs));
            return rs;
        } catch (SocketTimeoutException ex) {
            throw new PayException("Network connection timeout");
        } finally {
            timeOut = 0;
            if (soc != null) {
                soc.close();
            }
        }
    }
}
