package com.spd.bus.card.methods;

import com.spd.base.been.tianjin.CardBackBean;

/**
 * Created by 张明_ on 2019/2/19.
 * Email 741183142@qq.com
 */
public class ReturnVal {
    public final static int CAD_READ = 1;
    public final static int CAD_EXPIRE = 2;
    public final static int CAD_SELL = 3;
    public final static int CAD_OK = 4;
    public final static int CAD_BLK = 5;
    public final static int CAD_MAC2 = 6;
    public final static int CAD_MAC1 = 7;
    public final static int CAD_RETRY = 8;
    public final static int CAD_SETCOK = 9;
    public final static int CAD_EMPTY = 10;
    public final static int CAD_BROKEN = 11;
    public final static int NO_SET = 12;
    public final static int CAD_TEST_C = 13;
    public final static int CAD_KEYSETIN = 14;
    public final static int CAD_ACCESS = 15;
    public final static int CAD_LOGON = 16;
    public final static int CAD_SETCERR = 17;
    public final static int CAD_WRITE = 18;

    public static void doVal(CardBackBean cardBackBean) {
        int value = cardBackBean.getBackValue();
        switch (value) {
            case CAD_READ:
                break;
            case CAD_EXPIRE:
                break;
            case CAD_SELL:
                break;
            case CAD_OK:
                break;
            case CAD_BLK:
                break;
            case CAD_MAC2:
                break;
            default:
                break;
        }
    }


}
