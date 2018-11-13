package com.spd.bus.emv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.spd.bus.R;
import com.spd.bus.bean.TradeInfo;
import com.spd.bus.listener.OnPinPadListener;
import com.spd.bus.util.ByteUtil;
import com.spd.bus.util.MoneyUtil;
import com.spd.bus.util.StringUtils;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import sdk4.wangpos.libemvbinder.EmvCore;
import wangpos.sdk4.emv.ICallbackListener;
import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.Core;


public class PayActivity extends Activity implements View.OnClickListener {
    private String TAG = "TRANS";
    private Context context;
    private TextView yuanText,fenText,statusText,tv_cardNo;
    private Long orderAmount;

    private Core mCore;
    private EmvCore emvCore;
    private BankCard mBankCard;
    private String strTxt = "";
    private boolean isOffLine = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initView();
        initData();
    }

    private void initView() {
        ImageView titleBackImage = (ImageView) findViewById(R.id.titleBackImage);
        tv_cardNo = (TextView) findViewById(R.id.tv_cardNo);
        yuanText = (TextView) findViewById(R.id.bankOrderYuanText);
        fenText = (TextView) findViewById(R.id.bankOrderFenText);
        statusText = (TextView) findViewById(R.id.bankOrderStatusText);
        titleBackImage.setOnClickListener(this);
    }

    private void initData() {
        context = this;
        Intent intent = getIntent();
        orderAmount = intent.getLongExtra("orderAmount",0L);
        String amountStr = MoneyUtil.fen2yuan(orderAmount);
        int dotIndex = amountStr.indexOf(".");
        yuanText.setText(amountStr.substring(0, dotIndex));
        fenText.setText(amountStr.substring(dotIndex));

        new Thread(new Runnable() {
            @Override
            public void run() {
                emvCore = new EmvCore(getApplicationContext());
                mCore = new Core(getApplicationContext());
                mBankCard = new BankCard(getApplicationContext());

                doTrade(orderAmount);
            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    statusText.setText(strTxt);
                break;
                case 1:
                    tv_cardNo.setText(TradeInfo.getInstance().getId());
                break;
                case 2:
                    statusText.setText("get card information success,start trading ,pls connect your services ...");
                break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.titleBackImage:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBankCard != null) {
            try {
                mBankCard.breakOffCommand();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void doTrade(Long amount) {
        strTxt = "waiting read card...";
        handler.sendEmptyMessage(0);

        TradeInfo.getInstance().init();
        TradeInfo.getInstance().setTradeType(TradeInfo.Type_Sale);
        TradeInfo.getInstance().setMerchantNo("123456789012");
        TradeInfo.getInstance().setTerminalNo("12345678");
        TradeInfo.getInstance().setSerialNo(000001);
        TradeInfo.getInstance().setAmount(amount);
        TradeInfo.getInstance().setOnLine(true);//默认是联机交易

        try {
            mBankCard.breakOffCommand();//结束上一笔交易的命令(Order to close the previous transaction)
            byte[] outData = new byte[512];
            int[] outDataLen = new int[1];
            int result = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PICC|BankCard.CARD_MODE_ICC|BankCard.CARD_MODE_MAG,0x60,outData,outDataLen,"app1");
            if (result == 0) {
                Log.d("outData", ByteUtil.bytes2HexString(outData));
                switch (outData[0]) {
                    case 0x00://磁条卡成功(read card success,type MAG)
                        mCore.buzzer();
                        int len1 = outData[1];
                        int len2 = outData[2];
                        int len3 = outData[3];
                        Log.i(TAG, "run: read card len1==" + len1 + "len2==" + len2 + "len3==" + len3);
                        byte[] data1 = Arrays.copyOfRange(outData, 4, len1 + 4);
                        Log.i(TAG, "run: card data1==" + ByteUtil.bytes2HexString(data1));
                        byte[] data2 = Arrays.copyOfRange(outData, 4 + len1, len2 + len1 + 4);
                        Log.i(TAG, "run: card data2==" + ByteUtil.bytes2HexString(data2));
                        byte[] data3 = Arrays.copyOfRange(outData, 4 + len1 + len2, len3 + len2 + 4 + len1);
                        Log.i(TAG, "run: card data3==" + ByteUtil.bytes2HexString(data3));
                        if (len2 > 0) {
                            TradeInfo.getInstance().setMagneticCardData2(new String(data2));
                            String serviceCode = ByteUtil.bytes2HexString(data2).split("D")[1];
                            if (StringUtils.isEmpty(TradeInfo.getInstance().getId())) {
                                TradeInfo.getInstance().setId(new String(data2).split("=")[0]);
                                handler.sendEmptyMessage(1);
                            }
                            Log.i(TAG, "run: serviceCode" + serviceCode + "--" + new String(Arrays.copyOfRange(ByteUtil.hexString2Bytes(serviceCode), 4, 6)));
                            String expireDate = new String(Arrays.copyOfRange(ByteUtil.hexString2Bytes(serviceCode), 0, 4));
                            Log.i(TAG, "run: expireDate==" + expireDate.replace("=", "D"));
                            TradeInfo.getInstance().setValidityPeriod(expireDate);
                            serviceCode = new String(Arrays.copyOfRange(ByteUtil.hexString2Bytes(serviceCode), 4, 6));
                            if (serviceCode.startsWith("2") || serviceCode.startsWith("6")) {
                                //IC卡不支持降级操作
                                strTxt = "The IC card does not support degraded operation";
                                handler.sendEmptyMessage(0);
                                return;
                            }
                        }
                        if (len3 > 0) {
                            TradeInfo.getInstance().setMagneticCardData3(new String(data3));
                        }
                        if (len2 > 0||len3 > 0) {
                            readCardInfo("02");
                        }else {
                            strTxt = "The card operate fail";
                            handler.sendEmptyMessage(0);
                        }
                        break;
                    case 0x01:
                        //读卡失败(read card failed)
                        break;
                    case 0x02:
                        //刷磁条卡成功，加密处理失败(read card success,but encryption processing failed)
                        break;
                    case 0x03:
                        //刷卡超时(read card timeout)
                        break;
                    case 0x04:
                        //取消读卡(cancel read card)
                        break;
                    case 0x05:
                        //IC成功(read card success,type ICC)
                        mCore.buzzer();
                        readCardInfo("05");
                        break;
                    case 0x07:
                        //PICC成功(read card success,type PICC)
                        mCore.buzzer();
                        readCardInfo("07");
                        break;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void readCardInfo(String s) {
        isOffLine = false;
        strTxt = "read card success ..." + s;
        handler.sendEmptyMessage(0);
        TradeInfo.getInstance().setPosInputType(s);
        EMVManager.setEMVManager(context,handler,emvCore);
        switch (s) {
            case "02":
                activeShowPinPad("02");
                break;
            case "05":
                try {
                    strTxt = "EMV-Process waiting…";
                    handler.sendEmptyMessage(0);
                    int result = EMVManager.PBOC_Simple(iCallBackListener);
                    if (result != TradeInfo.SUCCESS) {
                        strTxt = "EMV-Process fail==" + result;
                        handler.sendEmptyMessage(0);
                    } else {
                        int transType = TradeInfo.getInstance().getTradeType();
                        Log.v(TAG, "PBOC_Simple。transType==" + transType);
                        if (transType == TradeInfo.Type_QueryBalance
                                || transType == TradeInfo.Type_Sale
                                || transType == TradeInfo.Type_Auth
                                || transType == TradeInfo.Type_CoilingSale) {//EMV内核弹密
                            // TODO: 2017/10/11 阻塞方法，返回交易结果
                            int transResult = EMVManager.EMV_TransProcess(iCallBackListener);
                            Log.d(TAG,"checkResult=="+transResult);
                            if (transResult != -8) {
                                strTxt = "EMV-Process fail";
                                handler.sendEmptyMessage(0);
                            }
                        } else {
                            activeShowPinPad("07");
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case "07":
                try {
                    strTxt = "EMV-Process waiting…";
                    handler.sendEmptyMessage(0);
                    int result = EMVManager.QPBOC_PreProcess(iCallBackListener);
                    if (result != TradeInfo.SUCCESS) {
                        Log.d(TAG, "QPBOC_PreProcess fail，result==" + result);
                    } else {
                        result = EMVManager.PBOC_Simple(iCallBackListener);
                        if (result == TradeInfo.SUCCESS) {
                            activeShowPinPad("07");
                        } else {
                            strTxt = "PBOC fail result==" + result;
                            handler.sendEmptyMessage(0);
                            Log.d(TAG, "PBOC_Simple fail，result==" + result);
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void activeShowPinPad(final String tradeType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String cardNo = TradeInfo.getInstance().getId();
                KeyPadDialog.getInstance(mCore).showDialog((Activity)context,cardNo,new OnPinPadListener() {
                    @Override
                    public void onUpDate() {
                    }

                    @Override
                    public void onCancel() {
                        strTxt = "User canceled";
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onByPass() {
                        strTxt = "ByPass";
                        handler.sendEmptyMessage(0);
                        if ("07".equals(tradeType)) {
                            try {
                                int result = EMVManager.EMV_TransProcess(iCallBackListener);
                                Log.d(TAG, "result==" + result);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }else {//MAG
                            handler.sendEmptyMessage(2);
                        }
                    }

                    @Override
                    public void onSuccess(String pin) {
                        Log.d(TAG, "Pin==" + pin);
                        if ("07".equals(tradeType)) {
                            try {
                                int result = EMVManager.EMV_TransProcess(iCallBackListener);
                                Log.d(TAG, "result==" + result);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }else {//MAG
                            handler.sendEmptyMessage(2);
                        }
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        Log.e(TAG, "errorCode==" + errorCode+"\nerrorMsg=="+errorMsg);
                    }
                });
            }
        });
    }

    private CountDownLatch countDownLatch = null;
    private ICallbackListener iCallBackListener = new ICallbackListener.Stub() {
        @Override
        public int emvCoreCallback(final int command, final byte[] data, final byte[] result, final int[] resultlen) throws RemoteException {
            countDownLatch = new CountDownLatch(1);
            Log.d(TAG, "emvCoreCallback。command==" + command);
            switch (command) {
                case 2818://Core.CALLBACK_PIN
                    Log.i("iCallbackListener", "Core.CALLBACK_PIN");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            KeyPadDialog.getInstance(mCore).showDialog((Activity)context,command, data, result, resultlen, new OnPinPadListener() {
                                @Override
                                public void onUpDate() {
                                    countDownLatch.countDown();
                                }

                                @Override
                                public void onCancel() {
                                    countDownLatch.countDown();
                                    strTxt = "User canceled";
                                    handler.sendEmptyMessage(0);
                                }

                                @Override
                                public void onByPass() {
                                    countDownLatch.countDown();
                                    strTxt = "ByPass";
                                    handler.sendEmptyMessage(0);
                                }

                                @Override
                                public void onSuccess(String pin) {
                                    countDownLatch.countDown();
                                    Log.d(TAG, "Pin==" + pin);
                                    if (pin != null&&"offLine".equals(pin)) {
                                        isOffLine = true;
                                        strTxt = "offLine success";
                                        handler.sendEmptyMessage(0);
                                    }
                                }

                                @Override
                                public void onError(int errorCode, String errorMsg) {
                                    countDownLatch.countDown();
                                    strTxt = "errorCode==" + errorCode+"\nerrorMsg=="+errorMsg;
                                    handler.sendEmptyMessage(0);
                                }
                            });
                        }
                    });
                    break;
                case 2821://Core.CALLBACK_ONLINE
                    Log.i("iCallbackListener", "Core.CALLBACK_ONLINE");
                    if (isOffLine) {
                        countDownLatch.countDown();
                    }else {
                        strTxt = "getCardInformation …";
                        handler.sendEmptyMessage(0);
                        int ret = EMVManager.EMV_OnlineProc(result, resultlen,countDownLatch,handler);
                        Log.i("iCallbackListener", "Core.CALLBACK_ONLINE, ret = " + ret);
                    }
                    //交易完成后调用此方法释放等待，回传sp联机结果
//                    countDownLatch.countDown();
                    break;
                case 2823://Core.CALLBACK_PINRESULT
                    strTxt = "OffLine pin check success";
                    handler.sendEmptyMessage(0);
                    countDownLatch.countDown();
                    break;
                case 2817://Core.CALLBACK_NOTIFY
                    Log.i("iCallbackListener", "Core.CALLBACK_NOTIFY");
                    countDownLatch.countDown();
                    //app select operation
                    break;
                case 2819://Core.CALLBACK_AMOUNT
                    long amount = TradeInfo.getInstance().getAmount();
                    String a = MoneyUtil.fen2yuan(amount);
                    Log.d(TAG, "amount==" + amount);
                    result[0] = 0;
                    Log.d(TAG, "int2Bytes==" + (int) MoneyUtil.yuan2fen(Double.parseDouble(a)));
                    byte[] tmp = ByteUtil.int2Bytes((int) MoneyUtil.yuan2fen(Double.parseDouble(a)));
                    System.arraycopy(tmp, 0, result, 1, 4);
                    resultlen[0] = 9;
                    countDownLatch.countDown();
                    break;
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 0;
        }
    };
}
