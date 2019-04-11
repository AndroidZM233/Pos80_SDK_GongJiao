package com.spd.yinlianpay.iso8583;

/**
 * Created by guoxiaomeng on 2017/6/23.
 */

public class PayException extends Exception {
    private static final long serialVersionUID = 7487634485730132399L;

    public PayException(String s) {
        super(s);
    }

    public PayException(String message, Throwable cause) {
        super(message, cause);
    }
}
