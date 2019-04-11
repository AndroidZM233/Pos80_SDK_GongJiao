package com.spd.yinlianpay.listener;


import com.spd.yinlianpay.trade.TradeInfo;

/**
 * Created by Tommy on 2016/6/12.
 */
public interface OnTraditionListener extends OnCommonListener {

    void onResult(TradeInfo info);
}
