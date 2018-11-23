package com.spd.bus.spdata.been;




public class ErroCode {



    /**
     * 支付宝错误码
     * MALFORMED_QRCODE        -1   二维码格式错误 二维码格式错误，检查输入的二维码是否正确。
     * QRCODE_INFO_EXPIRED     -2 二维码信息失效 用户离线公交码失效，提示用户刷新二维码。
     * QRCODE_KEY_EXPIRED      -3   二维码密钥失效 用户离线公交码授权秘钥失效，提示用户联网刷新离线公交码后再使用。
     * POS_PARAM_ERROR         -4   pos_param 错误 输入的 POS 信息参数错误，检查输入的   pos_param 是否正确。
     * QUOTA_EXCEEDED          -5 用户单笔额度超限 用户单笔限额超限，提示用户由于额度超限。
     * NO_ENOUGH_MEMORY        -6 内存不足 内存申请失败，程序运行内存不足。
     * SYSTEM_ERROR            -7  系统内部错误 内部处理发生异常，通常不会发生。遇到内部  错误，可联系支付宝技术排查。
     * CARDTYPE_UNSUPPORTED    -8 卡类型不支持 POS 不支持二维码对应的卡类型
     * NOT_INITIALIZED         -9 未执行初始化 未执行初始化，请先执行初始化
     * ILLEGAL_PARAM           -10 参数错误 入参错误导致的失败
     * PROTO_UNSUPPORTED       -11 不支持的协议版本 当前使用 sdk 不支持二维码的协议版本
     * QRCODE_DUPLICATED       -12 重复的二维码 使用已经验证成功的二维码重复刷码
     * INSTITUTION_NOT_SUPPORT -13 机构不支持 不支持二维码中指定的机构
     * INIT_DUPLICATED         -14 重复初始化 已经执行过初始化，不允许多次执行
     */
    public static final int SUCCESS = 1;
    public static final int MALFORMED_QRCODE = -1;
    public static final int QRCODE_INFO_EXPIRED = -2;
    public static final int QRCODE_KEY_EXPIRED = -3;
    public static final int POS_PARAM_ERROR = -4;
    public static final int QUOTA_EXCEEDED = -5;
    public static final int NO_ENOUGH_MEMORY = -6;
    public static final int SYSTEM_ERROR = -7;
    public static final int CARDTYPE_UNSUPPORTED = -8;
    public static final int NOT_INITIALIZED = -9;
    public static final int ILLEGAL_PARAM = -10;
    public static final int PROTO_UNSUPPORTED = -11;
    public static final int QRCODE_DUPLICATED = -12;
    public static final int INSTITUTION_NOT_SUPPORT = -13;
    public static final int INIT_DUPLICATED = -14;

    /**
     * 微信错误码
     * EC_SUCCESS         = 0;//	成功
     * EC_FORMAT          = -10000;//二维码格式错误
     * EC_CARD_PUBLIC_KEY = -10001;//卡证书公钥错误
     * EC_CARD_CERT       = -10002;//卡证书验签失败
     * EC_USER_PUBLIC_KEY = -10003;//	卡证书用户公钥错误
     * EC_USER_SIGN       = -10004;//二维码验签错误
     * EC_CARD_CERT_TIME  = -10005;//	卡证书过期
     * EC_CODE_TIME       = -10006;//二维码过期
     * EC_FEE             = -10007;//超过最大金额
     * EC_BALANCE         = -10008;//余额不足
     * EC_OPEN_ID         = -10009;//输入的open_id不匹配
     * EC_PARAM_ERR       = -10010;//参数错误
     * EC_MEM_ERR         = -10011;//内存申请错误
     * EC_CARD_CERT_SIGN_ALG_NOT_SUPPORT = -10012;//卡证书签名算法不支持
     * EC_MAC_ROOT_KEY_DECRYPT_ERR       = -10013;//	加密的mac根密钥解密失败
     * EC_MAC_SIGN_ERR                   = -10014;//mac校验失败
     * EC_QRCODE_SIGN_ALG_NOT_SUPPORT    = -10015;//	二维码签名算法不支持
     * EC_SCAN_RECORD_ECRYPT_ERR         = -10016;//	扫码记录加密失败
     * EC_SCAN_RECORD_ECODE_ERR          = -10017;//	扫码记录编码失败
     * EC_FAIL                           = -20000;//其它错误
     */
    public static final int EC_SUCCESS = 0;
    public static final int EC_FORMAT = -10000;
    public static final int EC_CARD_PUBLIC_KEY = -10001;
    public static final int EC_CARD_CERT = -10002;
    public static final int EC_USER_PUBLIC_KEY = -10003;
    public static final int EC_USER_SIGN = -10004;
    public static final int EC_CARD_CERT_TIME = -10005;
    public static final int EC_CODE_TIME = -10006;
    public static final int EC_FEE = -10007;
    public static final int EC_BALANCE = -10008;
    public static final int EC_OPEN_ID = -10009;
    public static final int EC_PARAM_ERR = -10010;
    public static final int EC_MEM_ERR = -10011;
    public static final int EC_CARD_CERT_SIGN_ALG_NOT_SUPPORT = -10012;
    public static final int EC_MAC_ROOT_KEY_DECRYPT_ERR = -10013;
    public static final int EC_MAC_SIGN_ERR = -10014;
    public static final int EC_QRCODE_SIGN_ALG_NOT_SUPPORT = -10015;
    public static final int EC_SCAN_RECORD_ECRYPT_ERR = -10016;
    public static final int EC_SCAN_RECORD_ECODE_ERR = -10017;

    public static final int EC_FAIL = -20000;
}
