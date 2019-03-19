package com.spd.alipay;

import com.spd.alipay.been.AliCodeinfoData;
import com.spd.alipay.been.TianjinAlipayRes;
import com.spd.base.been.AlipayQrcodekey;

import java.util.List;

public class AlipayJni {
    /**
     * 支付宝初始化（高版本）
     *
     * @param publicKeyListBeans
     * @return
     */
    public int initAliDev(List<AlipayQrcodekey.PublicKeyListBean> publicKeyListBeans) {
        return initdev(publicKeyListBeans);
    }

    /**
     * 天津jni初始化（低版本）
     *
     * @param pubkeyJson
     * @param cardTypeJson
     * @return
     */
    public int initAliDev(String pubkeyJson, String cardTypeJson) {
        return initTJDev(pubkeyJson, cardTypeJson);
    }

    /**
     * 支付宝验证二维码（高版本）
     *
     * @param aliCodeinfoData
     * @param code
     * @param recordId
     * @param posId
     * @param posMfId
     * @param posSwVersion
     * @param merchantType
     * @param currency
     * @param amount
     * @param vehicleId
     * @param plateNo
     * @param driverId
     * @param lineInfo
     * @param stationNo
     * @param lbsInfo
     * @param recordType
     * @return
     */
    public AliCodeinfoData checkAliQrCode(AliCodeinfoData aliCodeinfoData, String code, String recordId,
                                          String posId, String posMfId, String posSwVersion,
                                          String merchantType, String currency, int amount,
                                          String vehicleId, String plateNo, String driverId,
                                          String lineInfo, String stationNo, String lbsInfo,
                                          String recordType) {

        return checkAliQrCodeJni(aliCodeinfoData, code, recordId,
                posId, posMfId, posSwVersion,
                merchantType, currency, amount,
                vehicleId, plateNo, driverId,
                lineInfo, stationNo, lbsInfo,
                recordType);
    }

    /**
     * 支付宝验证二维码（低版本）
     *
     * @param tianjinAlipayRes 返回实体
     * @param qrCode           二维码
     * @param deviceNum        pos_id	(商户下唯一的pos号) subject	(脱机记录标题，建议放入公交路线)
     * @param linNum           (脱机记录标题，建议放入公交路线)
     * @param money            消费金额
     * @param type             (脱机记录标题，建议放入公交路线)
     * @param recordId         (记录id，商户下本次脱机记录唯一id号，record_id必须保证商户唯一，建议通过POS，时间等信息拼装)
     * @return TianjinAlipayRes
     */
    public TianjinAlipayRes checkAliQrCode(TianjinAlipayRes tianjinAlipayRes, String qrCode,
                                           String deviceNum, String linNum, int money, String type, String recordId) {
        return checkTJAliQrCodeJni(tianjinAlipayRes, qrCode,
                deviceNum, linNum, money, type, recordId);
    }

    /**
     * 初始化 （高版本）
     *
     * @param publicKeyListBeans 获取到的公钥
     * @return
     */
    private native int initdev(List<AlipayQrcodekey.PublicKeyListBean> publicKeyListBeans);

    /**
     * 天津初始化 （低版本）
     *
     * @param pubkeyJson
     * @param cardTypeJson
     * @return
     */
    private native int initTJDev(String pubkeyJson, String cardTypeJson);

    /**
     * 天津验码 （低版本）
     *
     * @param tianjinAlipayRes
     * @param qrCode
     * @param deviceNum
     * @param linNum
     * @param money
     * @param type
     * @param recordId
     * @return
     */
    private native TianjinAlipayRes checkTJAliQrCodeJni(TianjinAlipayRes tianjinAlipayRes, String qrCode,
                                                        String deviceNum, String linNum, int money, String type, String recordId);

    /**
     * 销毁（高版本）
     *
     * @return
     */
    public native int release();


    /**
     * 验码（高版本）
     *
     * @param testdata     返回验码状态以及相关参数
     * @param code         支付宝二维码
     * @param recordId     (记录id，商户下本次脱机记录唯一id号，record_id必须保证商户唯一，建议通过POS，时间等信息拼装)
     * @param posId        (商户下唯一的pos号)
     * @param posMfId      终端制造商id)
     * @param posSwVersion (终端软件版本)
     * @param merchantType （商户mcc码）
     * @param currency     (币种 人民币请填入156)
     * @param amount       （交易金额， 单位：分）
     * @param vehicleId    （车辆id）
     * @param plateNo      (车牌号)
     * @param driverId     （司机号）
     * @param lineInfo     (线路信息)
     * @param stationNo    (站点信息)
     * @param lbsInfo      (地理位置信息)
     * @param recordType   (脱机记录类型，公交场景为"BUS", 地铁场景为"SUBWAY")
     * @return 返回验码状态以及相关参数
     */
    private native AliCodeinfoData checkAliQrCodeJni(AliCodeinfoData aliCodeinfoData, String code, String recordId,
                                                     String posId, String posMfId, String posSwVersion,
                                                     String merchantType, String currency, int amount,
                                                     String vehicleId, String plateNo, String driverId,
                                                     String lineInfo, String stationNo, String lbsInfo,
                                                     String recordType);

    static {
        System.loadLibrary("alipay");
    }
}
