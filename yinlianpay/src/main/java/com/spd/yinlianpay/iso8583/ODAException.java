package com.spd.yinlianpay.iso8583;


public class ODAException extends Exception {
    public ODAException(String s) {
        super(s);
    }

    public ODAException(String message, Throwable cause) {
        super(message, cause);
    }
}
