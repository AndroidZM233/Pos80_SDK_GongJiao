package com.spd.alipay.net;

public class Url {
    //正式环境下应发送注册信息，此处位测试，直接拉取公私钥
    //http://140.143.4.112:9002
   // http://lzw.tunnel.qydev.com
//    https://shaynelee.cn/tecentQR/QRCode/downloadAliPayKeyList
    private static final String IP = "140.143.4.112";
    private static final String PORT = ":9002";
    static final String ALIPAY_BASE_URL2 = "http://" + IP + PORT;
    static final String ALIPAY_BASE_URL = " https://shaynelee.cn/tecentQR/QRCode/";
    /**
     * 获取公钥
     */
    static final String PUB_KEY = "downloadAliPayKeyList";


    /**
     * 扫码数据上传
     */
    static final String AILI_UPLOAD_ = "";

}
