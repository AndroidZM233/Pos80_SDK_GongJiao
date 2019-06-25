package com.spd.bus.threads;

/**
 * 記錄上傳雙免
 * Created by 张明_ on 2019/6/25.
 * Email 741183142@qq.com
 */

import android.os.Handler;

import com.spd.base.utils.LogUtils;
import com.spd.bus.sql.SqlStatement;
import com.spd.bus.util.HzjString;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class UnionSocketThread extends Thread {
    private String tradingFlow;
    private String record;
    private String unionCardCode;
    private Handler handler;

    public UnionSocketThread(String tradingFlow, String record,
                             String unionCardCode, Handler handler) {
        this.tradingFlow = tradingFlow;
        this.record = record;
        this.unionCardCode = unionCardCode;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("123.150.11.50", 18000),
                    3000);
            DataOutputStream out;
            byte[] temp = new byte[1024];
            out = new DataOutputStream(socket.getOutputStream());
            out.write(HzjString.hexStringToBytes(record));
            out.flush();
            // 向服务器发送信息
            InputStream inputStream = socket.getInputStream();
            socket.setSoTimeout(3000);
            int bytes = inputStream.read(temp);
            //String input = HzjString.BinaryToHexString( temp );
//                String result =  HzjString.BinaryToHexString( temp ).substring( 0,
//                        Integer.parseInt(  HzjString.BinaryToHexString( temp ).substring( 0, 4 ), 16 ) * 2 + 4 );
//                Logger.i( "银联消费result=" + result );
            String code = com.yht.q6jni.Jni.QapassDectUnPack(HzjString.BinaryToHexString(temp).substring(0,
                    Integer.parseInt(HzjString.BinaryToHexString(temp).substring(0, 4), 16) * 2 + 4));
            LogUtils.i("银联消费code=" + code);
            // String xiangyingma = code.substring( 0, 2 );
            if (code.substring(0, 2).equals("00")) {
                // String jiansuohao = code.substring( 2, 26 );
                // doubleState---2双免未结算，0双免结算成功，1双免结算失败，
                // oDAState-------ODA状态 -----6 ODA记录启用 5 ODA记录暂未启用
                // isPay------- 0 未支付 1已支付（双免）
                // /payStatus-----// 00成功，其它失败（双免）
                SqlStatement.updataUnionSMSUC(tradingFlow, code.substring(0, 2),
                        code.substring(2, 26));
                handler.sendMessage(handler.obtainMessage(7, "双免"));
            } else if (code.substring(0, 2).equals("58")) {
                if (0 == SqlStatement.SelectUnionBlack(unionCardCode)) {
                    SqlStatement.updataUnionODASUC(tradingFlow);
                    handler.sendMessage(handler.obtainMessage(7, "oda"));
                } else {
                    SqlStatement.updataUnionSMSUC_failed(tradingFlow,
                            code.substring(0, 2), "");
                    handler.sendMessage(handler.obtainMessage(8, "无效卡号"));
                }
            } else if (code.substring(0, 2).equals("51")) {
                SqlStatement.updataUnionSMSUC_failed(tradingFlow, code.substring(0, 2),
                        "");
                handler.sendMessage(handler.obtainMessage(12, "C余额不足"));
            } else {
                SqlStatement.updataUnionSMSUC_failed(tradingFlow, code.substring(0, 2),
                        "");
                handler.sendMessage(handler.obtainMessage(12, "C" + code.substring(0, 2)));
            }
            // 关闭各种输入输出流
            out.close();
            socket.close();
        } catch (IOException e) {
            if (0 == SqlStatement.SelectUnionBlack(unionCardCode)) {
                SqlStatement.updataUnionODASUC(tradingFlow);
                handler.sendMessage(handler.obtainMessage(7, "oda"));
            } else {
                SqlStatement.updataUnionSMSUC_failed(tradingFlow, "", "");
                handler.sendMessage(handler.obtainMessage(8, "无效卡号"));
            }
            e.printStackTrace();
        } catch (Exception e1) {
            if (0 == SqlStatement.SelectUnionBlack(unionCardCode)) {
                SqlStatement.updataUnionODASUC(tradingFlow);
                handler.sendMessage(handler.obtainMessage(7, "oda"));
            } else {
                SqlStatement.updataUnionSMSUC_failed(tradingFlow, "", "");
                handler.sendMessage(handler.obtainMessage(8, "无效卡号"));
            }
        }
        super.run();
    }
}