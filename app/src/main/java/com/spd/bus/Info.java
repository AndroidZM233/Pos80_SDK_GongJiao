package com.spd.bus;

/**
 * Created by 张明_ on 2019/3/11.
 * Email 741183142@qq.com
 */
public class Info {
    public static final String ZFB_APP_SERCET = "ZFB_APP_SERCET";
    public static final String ZFB_APP_KEY = "ZFB_APP_KEY";
    public static final String YL_TRANS_SEQ = "YL_TRANS_SEQ";
    public static final String BUS_RECORD = "BUS_RECORD";
    public static final String BUS_NO = "BUS_NO";
    public static final String BUS_NO_INIT = "30001234";
    public static final String POS_ID = "POS_ID";
    public static final String POS_ID_INIT = "17510850";
    public static final String BLACK = "BLACK";
    public static final String WHITE = "WHITE";
    public static final String YLSM_KEY = "YLSM_KEY";
    public static final String IS_CONFIG_CHANGE = "IS_CONFIG_CHANGE";

    public static final String ALL_MONEY = "ALL_MONEY";
    public static final String ALL_PEOPLE = "ALL_PEOPLE";
    public static final String ALL_YUE = "ALL_YUE";
    public static final String DRIVER_MONEY = "DRIVER_MONEY";
    public static final String DRIVER_PEOPLE = "DRIVER_PEOPLE";
    public static final String DRIVER_YUE = "DRIVER_YUE";
    public static final String BINS = "BINS";
    public static final String DOWN_APP_VERSION = "DOWN_APP_VERSION";

    public static final int PARAMETER = 99;
    public static final int VERIFY_BUS = 100;

    public static String REQUEST_CARDRECORD_URL = "http://60.29.169.178:29834";
    public static final String url1 = REQUEST_CARDRECORD_URL + "/yht-iccard-server/ws";
    public static final String UNION_NAMESPACE = "http://service.up.com/";
    public static final String URL_UNION_PAY = "http://123.150.11.50:29838/unionpay/services/NetbarServices/";
    public static final String NAMESPACE = "http://service.iccard.yht.com/";
    public static final String URL2 = REQUEST_CARDRECORD_URL + "/yht-iccard-server/ws/posXBService";
    private static String IP_COMMON = "http://123.150.11.50:18188";
    public static final String REGIDTER_URL = IP_COMMON
            + "/transfor/gate?sid=10202&encode=utf-8&reqData=";
    // 请求获取秘钥版本以及服务请求编码 ip不确定
    public static final String URL_KEYVERSION = IP_COMMON
            + "/transfor/gate?sid=10600&reqData={}&appKey=";
    // 请求获取支付宝秘钥 ip不确定
    public static final String URL_KEYLIST = IP_COMMON
            + "/transfor/gate?sid=10604&reqData=";
    //注冊地址
    public static final String JM_REGISER_DATA="http://112.74.41.206:8086/transfor/gate?sid=10202&encode=utf-8&appKey=9A174E6CF9AAD017&reqData=";



}
