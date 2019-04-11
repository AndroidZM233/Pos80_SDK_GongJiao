package com.spd.yinlianpay.util;

import android.os.RemoteException;


import com.spd.yinlianpay.WeiPassGlobal;
import com.spd.yinlianpay.context.MyContext;

import wangpos.sdk4.libbasebinder.Core;

/**
 * Created by SXK on 2017/9/7.
 */

public class LedUtils {
    /**
     BIT0: LED 1，绿 3
     BIT1: LED 2，蓝 1
     BIT2: LED 3，红 4
     BIT3: LED 4，黄 2
     * */
    Core core = MyContext.mCore;
    public static final LedUtils LEDUTILS = new LedUtils();
    public static LedUtils getInstance() {
        return LEDUTILS;
    }
    //输金额 等卡 蓝
   public void waitting() {
       MyContext.context.getMainLooper();
       try {
           core.ledFlash(1,0,0,0,0xFFFF,0,0);
       } catch (RemoteException e) {
           e.printStackTrace();
       }
   }
    //卡片处理黄
    public int cardData() {
        if(!WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("07"))
            return 0;
        try {
          int ret =  core.ledFlash(0,1,0,0,0xFFFF,0,0);
            return ret;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    boolean connectFlag ;
    //通讯 闪烁绿
    public int connect()  {
        if(!WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("07"))
            return 0;
                try {
                   return core.ledFlash(0, 0, 1, 0, 200,200,0xFFFF);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return 0;
    }
    //成功 绿
    public int success()  {
        if(!WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("07"))
            return 0;
        connectFlag = false;
        try {
//            core.ledFlash(0, 1, 0, 0, 0,0,750);
         int ret =   core.ledFlash(0,0,1,0,0xFFFF,0,0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    //失败 红
    public int error()  {
        if(!WeiPassGlobal.getTransactionInfo().getServiceCode().startsWith("07"))
            return 0;
        connectFlag = false;
        try {
//            core.ledFlash(0, 0, 0, 1, 750,0,750);
           int ret = core.ledFlash(0, 0, 1, 0, 0,0,0);
            ret =core.ledFlash(0,0,0,1,0xFFFF,0,0);
            return ret;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    //
    public int close() {
        connectFlag = false;
        try {
           int ret = core.ledFlash(0,0,0,0,0,0,0);
            return ret;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
