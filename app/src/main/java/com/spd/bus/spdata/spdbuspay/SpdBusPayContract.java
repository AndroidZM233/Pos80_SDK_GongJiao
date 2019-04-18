package com.spd.bus.spdata.spdbuspay;

import android.content.Context;

import com.spd.alipay.been.AliCodeinfoData;
import com.spd.alipay.been.TianjinAlipayRes;
import com.spd.base.been.AlipayQrcodekey;
import com.spd.base.been.BosiQrcodeKey;
import com.spd.base.been.tianjin.CardBackBean;
import com.spd.base.beenupload.AlipayQrCodeUpload;
import com.spd.base.beenupload.BosiQrCodeUpload;
import com.spd.base.beenupload.WeichatQrCodeUpload;
import com.spd.base.been.WechatQrcodeKey;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.bus.spdata.mvp.BasePresenter;
import com.spd.bus.spdata.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SpdBusPayContract {
    public interface View extends BaseView {
        /**
         * /界面显示
         */
        void successCode(CardBackBean cardBackBean);

        void success(String msg);

        void erro(String msg);

        void showAliPublicKey(int result);

        void showAliPayInit(int result);

        void showWechatPublicKey(WechatQrcodeKey wechatQrcodeKey);

        void showCheckAliQrCode(TianjinAlipayRes tianjinAlipayRes, RunParaFile runParaFile,String orderNr);

        void showReleseAlipayJni(int result);

        void showCheckWechatQrCode(int result, String wechatResult, String userId);

        void doCheckWechatTianJin();
    }

    interface Presenter extends BasePresenter<View> {
        void produce();
        /**
         * 支付宝二维码
         */
        /**
         * 获取支付宝证书key
         */
        void getAliPubKey();


        void aliPayInitJni();


        void getZhiFuBaoPosInfo(Context context);


        /**
         * 校验支付宝二维码
         *
         * @param code
         */
        void checkAliQrCode(String code);

        /**
         * 上传支付宝结果
         *
         * @param qrcodeUpload
         */
        void uploadAlipayRe(Context context);

        /**
         * 释放支付宝库
         */
        void releseAlipayJni();

        /**
         * 腾讯（微信）二维码
         */
        void getWechatPublicKey();




        void checkWechatTianJin(String code, int payfee, byte scene,
                                byte scantype, String posId, String posTrxId);

        /**
         * 初始化微信库
         */
        void wechatInitJin();

        void checkWechatQrCode(String code, List<WechatQrcodeKey.PubKeyListBean> pbKeyList, List<WechatQrcodeKey.MacKeyListBean> macKeyList, int payfee, byte scene, byte scantype, String posId, String posTrxId);

        void uploadWechatRe(Context context);


        void checkYinLianCode(Context context,String qrcode);


    }
}
