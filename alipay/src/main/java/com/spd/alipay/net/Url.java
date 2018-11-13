package com.spd.alipay.net;

public class Url {
    //正式环境下应发送注册信息，此处位测试，直接拉取公私钥
//    http://lzw.tunnel.qydev.com/tecentQR/QRCode/downloadAliPayKeyList
//    https://shaynelee.cn/tecentQR/QRCode/downloadAliPayKeyList
    private static final String IP = "140.143.4.112";
    private static final String PORT = ":443";
    //        static final String WECHATS_BASE_URL = "https://" + IP + PORT + "/tecentQR/QRCode/";
    static final String ALIPAY_BASE_URL = " https://shaynelee.cn/tecentQR/QRCode/";
    /**
     * 获取公钥
     */
    static final String PUB_KEY = "downloadAliPayKeyList";

    static final String MOBILE_MARK = "/ttsp/app_auth_dowload";
    /**
     * 扫码数据上传
     */
    static final String QR_CODE_SCAN = "ttsp/qrcode_scan";
    /**
     * 行程扣款
     */
    static final String ITINERARY = "ttsp/trip_consume";
    /**
     * 行程数据同步
     */
    static final String SYNC_DATA = "/ttsp/trip_sync";

}
