package com.spd.yinlianpay.net;

import android.util.Log;


import com.spd.yinlianpay.util.PrefUtil;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https 忽略证书请求
 */
public class CCBBManage {

    private static byte[] rs = null;
    static long times = 0;

    public static byte[] qiandao(final byte[] messageBytes) {
        long time = System.currentTimeMillis();
        String hostname = PrefUtil.getIP();
        int port = PrefUtil.getPort();
        Socket socket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;
        try {
            BaseHttpSSLSocketFactory fc = new BaseHttpSSLSocketFactory();
            socket = fc.createSocket(hostname, port);
            socket.setSoTimeout(1000);
            OutputStream os = socket.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("POST / HTTP/1.1\r\n");
            sb.append(("Host: " + hostname + "\r\n"));
            sb.append("Accept: */*\r\n");
            sb.append("User-Agent: Java\r\n"); // Be honest.
            sb.append("Content-Type: x-ISO-TPDU/x-auth\r\n");
            sb.append("Content-Length: " + messageBytes.length + "\r\n");
            sb.append("\r\n");
            byte[] a = sb.toString().getBytes();
            Log.i("stw", "http请求头" + sb.toString());
            byte[] data3 = new byte[a.length + messageBytes.length];
            System.arraycopy(a, 0, data3, 0, a.length);
            System.arraycopy(messageBytes, 0, data3, a.length, messageBytes.length);
            Log.i("stw", "请求报文: " + DataConversionUtils.byteArrayToString(data3));
            os.write(data3);
            os.flush();
            Log.i("stw", "网络请求发送===" + (System.currentTimeMillis() - time));
            times = System.currentTimeMillis();
            int retlen = 0;
            InputStream is = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
            for (String line; (line = reader.readLine()) != null; ) {
                if (line.isEmpty()) {
                    break; // Stop when headers are completed.
                }
                System.out.println(line);
                Matcher matcher = Pattern.compile("Content-Length:\\s?(\\d+)").matcher(line);
                if (matcher.find()) {
                    retlen = Integer.parseInt(matcher.group(1));
                }
            }
            Log.i("stw", "网络请求返回333333333===" + (System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
            char[] cbuf = new char[retlen];
            int realreadlen = reader.read(cbuf);
            Log.i("stw", "qiandao: ");
            byte[] resultBytes = new String(cbuf).getBytes("ISO-8859-1");
            Log.i("stw", "返回报文" + DataConversionUtils.byteArrayToString(resultBytes));
            Log.i("stw", "网络请求返回===" + (System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
            rs = DataConversionUtils.cutBytes(resultBytes, 2, resultBytes.length - 2);
            Log.i("stw", "返回报文zhen" + DataConversionUtils.byteArrayToString(rs));
            return rs;
        } catch (SocketTimeoutException e) {
            // TODO: 2019/4/11 超时 0200 发送oda 脱机
            Log.i("soket1111", "qiandao111111111:%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");
            e.printStackTrace();
        } catch (SocketException e) {
            Log.i("soket1111", "qiandao2222222222:%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");
            // TODO: 2019/4/11   没有网络 失败 
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("soket1111", "qiandao3333333333333:%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException logOrIgnore) {
                }
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException logOrIgnore) {
                }
            }
            return rs;
        }
    }

    private boolean timers() {
        //org.apache.commons.lang3.concurrent.BasicThreadFactory
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //do something
                if ((System.currentTimeMillis()-times)>2000) {
                }
            }
        },0,50, TimeUnit.MILLISECONDS);
        while ((System.currentTimeMillis()-times)>2000){
            return false;
        }
        return true;
    }
}
