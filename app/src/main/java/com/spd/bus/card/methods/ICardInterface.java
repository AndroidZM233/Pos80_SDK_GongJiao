package com.spd.bus.card.methods;

import com.spd.base.been.tianjin.CardBackBean;

import wangpos.sdk4.libbasebinder.BankCard;

/**
 * Created by 张明_ on 2019/2/18.
 * Email 741183142@qq.com
 */
interface ICardInterface {
    CardBackBean mainMethod(BankCard mBankCard, byte[] cpuCard, byte[] lPurSub);

    //3031  0105指令
    int getFirst(BankCard mBankCard);

    //15 17文件
    int getSnr(BankCard mBankCard);

    //恢复
    int doFSysSta(BankCard mBankCard);

    //消费
    CardBackBean consumption(BankCard mBankCard, byte[] cpuCard);
}
