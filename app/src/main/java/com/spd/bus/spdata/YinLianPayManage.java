package com.spd.bus.spdata;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.spd.bus.Info;
import com.spd.bus.MyApplication;
import com.spd.yinlianpay.WeiPassGlobal;
import com.spd.yinlianpay.card.UnionPayCard;
import com.spd.yinlianpay.comm.ChannelTool;
import com.spd.yinlianpay.context.MyContext;
import com.spd.yinlianpay.dlg.ProcessDlg;
import com.spd.yinlianpay.iso8583.Body;
import com.spd.yinlianpay.iso8583.Msg;
import com.spd.yinlianpay.listener.OnCommonListener;
import com.spd.yinlianpay.listener.OnTraditionListener;
import com.spd.yinlianpay.trade.TradeInfo;
import com.spd.yinlianpay.util.LedUtils;
import com.spd.yinlianpay.util.PrefUtil;

import ui.wangpos.com.utiltool.DateUtil;
import ui.wangpos.com.utiltool.HEXUitl;

public class YinLianPayManage implements OnTraditionListener {
    private Context myContext = null;
    private String TAG = "stw";

    public YinLianPayManage(Context context) {
        myContext = context;
    }

    public void yinLianLogin() {
        MyContext.onCreate(myContext, MyApplication.getmKey(), MyApplication.getmCore(), MyApplication.getEmvCore(), MyApplication.getBankCardInstance());
        Log.i(TAG, "yinLianLogin: 开始");

//        PrefUtil.setIP("140.207.168.62");
//        PrefUtil.setPort(30000);
        //天津 socket请求
        PrefUtil.setIP("123.150.11.50");
        PrefUtil.setPort(18000);
        PrefUtil.putMerchantName("APP Cash");
        //TPDU
        PrefUtil.setTPDU("6000730000");
        //报文头
        PrefUtil.setHead("603200324017");
        if (PrefUtil.getTerminalNo() == null) {
            //终端号
//            PrefUtil.putTerminalNo("95516001");
            PrefUtil.putTerminalNo(Info.POS_ID_INIT);
        }
        if (PrefUtil.getMerchantNo() == null) {
            //商户号
            PrefUtil.putMerchantNo("898120041110190");
        }
        PrefUtil.putReversal(null);
        //设置主密钥
//        String key = SharedXmlUtil.getInstance(myContext).read(Info.YLSM_KEY
//                , "");
//        if (!TextUtils.isEmpty(key)){
//            PrefUtil.setMasterkey(key);
//        }else {
//            PrefUtil.setMasterkey("D364A873541673734AA19BFB7649948C");
//        }

        //交易批次号
        if (WeiPassGlobal.getTransactionInfo().getBacthNo() != null) {
            String ss = WeiPassGlobal.getTransactionInfo().getBacthNo();
            PrefUtil.putBatchNo(WeiPassGlobal.getTransactionInfo().getBacthNo());
        } else {
            PrefUtil.putBatchNo("000001");
        }

        if (PrefUtil.getSerialNo() != 0) {
            int s = PrefUtil.getSerialNo();
            PrefUtil.putSerialNo(PrefUtil.getSerialNo());
        } else {
            PrefUtil.putSerialNo(1);
        }
        //是否为socket/hhlsocket 请求
        PrefUtil.setIsSocket(true);

        //更新主密钥
        try {
            byte[] masterkey = HEXUitl.hexToBytes(PrefUtil.getMasterKey());
            //秘钥明文导入
            int algorithmType = 0;
            int masterkeyret = MyApplication.getmKey().updateKeyWithAlgorithm(0x03, algorithmType, 0, new byte[8], masterkey, false,
                    0x00, new byte[4], MyContext.keyPacketName, MyContext.specifyId);
            Log.i(TAG, "更新主密钥：：：" + masterkeyret);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        PrefUtil.setOperatID("01");
        //签到
        ChannelTool.FIXED_EXECUTOR.execute(tLoginRun);

    }

    Runnable tLoginRun = new Runnable() {
        @Override
        public void run() {
            try {
                WeiPassGlobal.transactionClear();
                WeiPassGlobal.getTransactionInfo().setTransType(TradeInfo.Type_Sale);
                ChannelTool.login("01", "0000", new OnCommonListener() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "onSuccess:签到成功 开始下载 AID CAPK ");
                        //下载 AID CAPK 并写入内核
                        ChannelTool.doDownParamter(1, YinLianPayManage.this);
                        ChannelTool.doDownParamter(2, YinLianPayManage.this);
                    }

                    @Override
                    public void onProgress(final String progress) {
                        Log.i(TAG, "onProgress: " + progress);
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        Log.i(TAG, "onError: " + errorMsg);
                    }

                    @Override
                    public void onDataBack() {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onResult(TradeInfo info) {
        Log.i(TAG, "onResult: ");
    }

    @Override
    public void onSuccess() {
        Log.i(TAG, "onSuccess:  ");
        if (PrefUtil.getICPARAMETER() && (PrefUtil.getICPASSWORD() || PrefUtil.getICSMD())) {
            PrefUtil.setISFIRSTRUN(true);
        }
    }

    @Override
    public void onProgress(String progress) {
        Log.i(TAG, "onProgress: ");
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        Toast.makeText(myContext, errorMsg, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onError: ");
    }

    @Override
    public void onDataBack() {
    }

    public void readCardInfo(String s, Handler readCardHandler) {
        WeiPassGlobal.getTransactionInfo().setServiceCode(s);
        UnionPayCard.init(myContext, readCardHandler);
        UnionPayCard.readBankCardInfo(MyApplication.mBankCard,
                WeiPassGlobal.getTransactionInfo().getTermId(),
                WeiPassGlobal.getTransactionInfo().getMerchantName());
    }

}
