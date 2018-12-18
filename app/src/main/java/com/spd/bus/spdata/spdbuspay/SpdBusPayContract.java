package com.spd.bus.spdata.spdbuspay;

import android.content.Context;

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

        void showAliPublicKey(AlipayQrcodekey aliQrcodekey);

        void showAliPayInit(int result);

        void showWechatPublicKey(WechatQrcodeKey wechatQrcodeKey);

        void showCheckAliQrCode(AliCodeinfoData aliCodeinfoData);

        void showReleseAlipayJni(int result);

        void showCheckWechatQrCode(int result, String wechatResult, String openId);

        //博思
        void showBosikey(BosiQrcodeKey bosiQrcodeKey);

        void showSetBosiCerPath(int state);

        void showBosiCerVersion(String vension);

        void showUpdataBosiKey(int state);


    }

    interface Presenter extends BasePresenter<View> {
        /**
         * 支付宝二维码
         */
        void getAliPubKey();

        void aliPayInitJni(List<AlipayQrcodekey.PublicKeyListBean> publicKeyListBeans);

        void checkAliQrCode(String code, String recordId,
                            String posId, String posMfId, String posSwVersion,
                            String merchantType, String currency, int amount,
                            String vehicleId, String plateNo, String driverId,
                            String lineInfo, String stationNo, String lbsInfo,
                            String recordType);

        void uploadAlipayRe(QrcodeUpload qrcodeUpload);

        void releseAlipayJni();

        /**
         * 腾讯（微信）二维码
         */
        void getWechatPublicKey();

        void wechatInitJin();

        void checkWechatQrCode(String code, List<WechatQrcodeKey.PubKeyListBean> pbKeyList, List<WechatQrcodeKey.MacKeyListBean> macKeyList, int payfee, byte scene, byte scantype, String posId, String posTrxId);

//        void uploadWechatRe();


        /**
         * 博思二维码
         */

        void bosiInitJin(Context context, String filePath);

        void getBosikey();

        int getBosiCerVersion();

        int updataBosiKey(String cer);

        void checkBosiQrCode(String qrcode);

//        void uploadBosiRe();

    }
}
