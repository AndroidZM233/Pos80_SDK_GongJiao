package com.spd.bus.spdata.spdbuspay;

import android.content.Context;

import com.bluering.pos.sdk.qr.QrCodeInfo;
import com.spd.alipay.been.AliCodeinfoData;
import com.spd.base.beenali.AlipayQrcodekey;
import com.spd.base.beenbosi.BosiQrcodeKey;
import com.spd.base.beenupload.QrcodeUpload;
import com.spd.base.beenwechat.WechatQrcodeKey;
import com.spd.bus.spdata.mvp.BasePresenter;
import com.spd.bus.spdata.mvp.BaseView;

import java.util.List;

import io.reactivex.internal.schedulers.ImmediateThinScheduler;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SpdBusPayContract {
    public interface View extends BaseView {
        /**
         * /界面显示
         */
        void success(String msg);

        void erro(String msg);

        void showAliPublicKey(int result);

        void showAliPayInit(int result);

        void showWechatPublicKey(WechatQrcodeKey wechatQrcodeKey);

        void showCheckAliQrCode(AliCodeinfoData aliCodeinfoData);

        void showReleseAlipayJni(int result);

        void showCheckWechatQrCode(int result, String wechatResult, String openId);

        //博思
        void showBosikey(BosiQrcodeKey bosiQrcodeKey);


        void showBosiCerVersion(String vension);

        void showCheckBosiQrCode(QrCodeInfo qrCodeInfo);

        void showUpdataBosiKey(int state);


    }

    interface Presenter extends BasePresenter<View> {
        /**
         * 支付宝二维码
         */
        /**
         * 获取支付宝证书key
         */
        void getAliPubKey();

        void aliPayInitJni(List<AlipayQrcodekey.PublicKeyListBean> publicKeyListBeans);

        /**
         * 校验支付宝二维码
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
         */
        void checkAliQrCode(String code, String recordId,
                            String posId, String posMfId, String posSwVersion,
                            String merchantType, String currency, int amount,
                            String vehicleId, String plateNo, String driverId,
                            String lineInfo, String stationNo, String lbsInfo,
                            String recordType);

        /**
         * 上传支付宝结果
         * @param qrcodeUpload
         */
        void uploadAlipayRe(QrcodeUpload qrcodeUpload);

        /**
         * 释放支付宝库
         */
        void releseAlipayJni();

        /**
         * 腾讯（微信）二维码
         */
        void getWechatPublicKey();

        /**
         * 初始化微信库
         */
        void wechatInitJin();

        /**
         * 校验博思二维码
         * @param code
         * @param pbKeyList
         * @param macKeyList
         * @param payfee
         * @param scene
         * @param scantype
         * @param posId
         * @param posTrxId
         */
        void checkWechatQrCode(String code, List<WechatQrcodeKey.PubKeyListBean> pbKeyList, List<WechatQrcodeKey.MacKeyListBean> macKeyList, int payfee, byte scene, byte scantype, String posId, String posTrxId);

        void uploadWechatRe(QrcodeUpload qrcodeUpload);


        /**
         * 博思二维码
         */
        /**
         *
         * @param context  上下文对象
         * @param filePath  保存证书key路径
         */
        void bosiInitJin(Context context, String filePath);

        /**
         * 获取博思二维码证书key
         */
        void getBosikey();

        /**
         * 获取证书key版本
         * @return 证书版本号
         */
        int getBosiCerVersion();

        /**
         * 更新证书key
         * @param cer
         * @return
         */
        int updataBosiKey(String cer);

        /**
         * 校验博思码
         * @param qrcode
         */
        void checkBosiQrCode(String qrcode);

        void uploadBosiRe(QrcodeUpload qrcodeUpload);

    }
}
