package com.wechat.net;

public class Url {
    //"http://192.168.1.102:8020/acquirements_tasks/QRCode/uploadQRCode"
    //正式环境下应发送注册信息，此处位测试，直接拉取公私钥
//    https://140.143.4.112:443/tecentQR/QRCode/downloadKeyList
    // https://shaynelee.cn/tecentQR/QRCode/downloadKeyList
    private static final String IP = "140.143.4.112";
    private static final String PORT = ":443";
    //        static final String WECHATS_BASE_URL = "https://" + IP + PORT + "/tecentQR/QRCode/";
    static final String WECHATS_BASE_URL = "https://shaynelee.cn/tecentQR/QRCode/";
    /**
     * 获取公钥
     */
    static final String PUB_KEY = "downloadKeyList";

    /**
     * 扫码数据上传
     */
    static final String QR_CODE_SCAN = "ttsp/qrcode_scan";

}
