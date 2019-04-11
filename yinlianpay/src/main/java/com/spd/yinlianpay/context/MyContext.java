package com.spd.yinlianpay.context;

import android.content.Context;


import com.spd.yinlianpay.DEK;
import com.spd.yinlianpay.util.SecurityUtils;

import java.util.List;

import sdk4.wangpos.libemvbinder.EmvCore;
import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libkeymanagerbinder.Key;
import wiseasy.socketpusher.socketpusher;

/**
 * Created by guoxiaomeng on 2017/6/27.
 */

public class MyContext {
    public static final int MSG_BACK = 0x1000, MSG_PROGRESS = MSG_BACK + 1,
            MSG_ERROR = MSG_BACK + 2, MSG_RESULT = MSG_BACK + 3, MSG_CARD = MSG_BACK + 4,
            MSG_SWIP = MSG_BACK + 5;
    public static final int ShowErrorDialogFlag = 105;//error tip
    public static final int Hide_Progress = 10001;
    public static final int ShowToastFlag = 106;
    public static final int RESULT_SUCCESS = 0x1024;
    public static final int RESULT_OUTCOME = 0x2010;
    public static final int RESULT_FAIL = RESULT_SUCCESS + 1;

    public static Context context;
    public static final String TAG = MyContext.class.getSimpleName();
    public static List<DEK> dekList;
    public static String Ver = "00009999170724";
    public static String keyPacketName = "ui.wangpos.com.ccbbank";
    public static int specifyId = 2;
    public static Key mKey;
    public static Core mCore;
    public static EmvCore emvCore;
    public static BankCard bankCard;

    public MyContext() {
    }

    public static void onCreate(Context context, Key mKey, Core mCore, EmvCore emvCore, BankCard bankCard) {
        SecurityUtils.init();
        MyContext.context = context;
        MyContext.mKey = mKey;
        MyContext.mCore = mCore;
        MyContext.emvCore = emvCore;
        MyContext.bankCard = bankCard;
    }


    //log to pc
    public static socketpusher log = new socketpusher();


    public static Context getInstance() {
        return context;
    }

}
