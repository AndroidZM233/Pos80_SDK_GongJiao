package com.example.test.yinlianbarcode.net;

/**
 * @author :Reginer in  2017/9/7 23:17.
 * 联系方式:QQ:282921012
 * 功能描述:urls
 */
class Urls {
    private static final String IP = "60.190.227.167";
    private static final String PORT = ":8085";
    static final String BASE_URL = "http://" + IP + PORT + "/";
    /**
     * 获取公钥
     */
    static final String PUB_KEY = "ttsp/cert_download";

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
