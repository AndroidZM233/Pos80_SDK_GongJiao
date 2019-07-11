package com.spd.bus.spdata.spdbuspay;

import android.content.Context;

import com.spd.alipay.been.AliCodeinfoData;
import com.spd.alipay.been.TianjinAlipayRes;
import com.spd.base.been.AlipayQrcodekey;
import com.spd.base.been.tianjin.CardBackBean;
import com.spd.base.been.WechatQrcodeKey;
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

        void erro(int msg);

        void showAliPublicKey(int result);

        void showAliPayInit(int result);


        void showCheckAliQrCode(TianjinAlipayRes tianjinAlipayRes, String orderNr);


        void showCheckWechatQrCode(int result);

    }

    interface Presenter extends BasePresenter<View> {

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


        void checkWechatTianJin(Context context,String code, byte scene,
                                byte scantype, String posTrxId,String driverTime);

        /**
         * 初始化微信库
         */
        void wechatInitJin();

        void uploadWechatRe(Context context);


        void checkYinLianCode(Context context, String qrcode);

        void uploadSM(Context context);

        void uploadYinLian(Context context);
        void getYinLianBlack();


    }
}
