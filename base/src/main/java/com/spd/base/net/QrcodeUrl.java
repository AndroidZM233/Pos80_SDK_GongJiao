package com.spd.base.net;

public class QrcodeUrl {
    //正式环境下应发送注册信息，此处位测试，直接拉取公私钥
//    http://ip:port/tecentQR/QRCode/uploadWeChatQRCode
    //    http://ip:port/tecentQR/QRCode/uploadAliPayQRCode
//    http://140.143.4.112:9002/tecentQR/QRCode/downloadAliPayKeyList
//    http://ip:port/tecentQR/QRCode/downloadWeChatKeyList
//    http://ip:port/tecentQR/QRCode/uploadBoSiQRRecord
//    http://ip:port/tecentQR/QRCode/downloadBoSiKeyList

    private static final String IP = "140.143.4.112";
    private static final String PORT = ":9002";
    //    static final String ALIPAY_BASE_URL = "http://" + IP + PORT + "/tecentQR/QRCode/";
    static final String ALIPAY_BASE_URL = "https://shaynelee.cn/tecentQR/QRCode/";
    /**
     * 获取支付宝密钥
     */
    static final String PUB_ALI_KEY = "downloadAliPayKeyList";
    /**
     * 上传支付宝验码结果
     */
    static final String UPLOAD_ALI = "uploadAliPayQRCode";

    /**
     * 获取腾讯（微信）密钥
     */
    static final String PUB_WECHAT_KEY = "downloadWeChatKeyList";
    /**
     * 上传腾讯（微信）验码结果
     */
    static final String UPLOAD_WECHAT = "uploadWeChatQRCode";

    /**
     * 获取博思（海南）密钥
     */
    static final String PUB_BOSI_KEY = "downloadBoSiKeyList";

    /**
     * 上传博思（海南）验码结果
     */
    static final String UPLOAD_BOSI = "uploadBoSiQRRecord";


}
