package com.spd.yinlianpay;


import com.spd.yinlianpay.trade.TradeInfo;

import java.io.File;


/**
 * Created by zhou on 2016/9/6.
 */
public class WeiPassGlobal {
    private static final String SIGNPIC_PATH = File.separator + "sdcard" + File.separator + "ccbback" + File.separator;
    public static final String SIGNPIC_NAME = "sign_pic";
    public static TradeInfo tradeInfo;
    public static final String TradeTypeFlag = "tradeTypeFlag";
    public static final String OrderAmount = "orderAmount";
    public static final String ApplicationInvokeName = "ApplicationInvokeName";

    public static final int BankPayCashierActivityFlag = 101;
    public static final int QrcodePayCashierActivityFlag = 102;

    private static TransactionInfo transactionInfo = null;

    public static TransactionInfo getTransactionInfo() {
        return transactionInfo == null ? transactionInfo = new TransactionInfo() : transactionInfo;
    }

    public static TransactionInfo transactionClear() {
        return transactionInfo = new TransactionInfo();
    }
}
