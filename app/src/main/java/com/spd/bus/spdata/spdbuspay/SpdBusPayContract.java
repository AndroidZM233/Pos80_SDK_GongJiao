package com.spd.bus.spdata.spdbuspay;

import com.spd.alipay.been.AliCodeinfoData;
import com.spd.alipay.been.AlipayPublicKey;
import com.spd.alipay.been.AlipayUploadBeen;
import com.spd.bus.spdata.mvp.BasePresenter;
import com.spd.bus.spdata.mvp.BaseView;
import com.wechat.been.WechatPublicKey;

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
        void success(String msg);

        void erro(String msg);

        void getAliPublicKey(AlipayPublicKey alipayPublickKey);

        void aliPayInit(int result);

        void getWechatPublicKey(WechatPublicKey wechatPublicKey);

        void checkAliQrCodeShow(AliCodeinfoData aliCodeinfoData);

        void releseAlipayJni(int result);

        void checkWechatQrCode(int result, String wechatResult, String openId);
    }

    interface Presenter extends BasePresenter<View> {
        void getAliPublicKey();

        void aliPayInit(List<AlipayPublicKey.PublicKeyListBean> publicKeyListBeans);

        void checkAliQrCode(String code, String recordId,
                            String posId, String posMfId, String posSwVersion,
                            String merchantType, String currency, int amount,
                            String vehicleId, String plateNo, String driverId,
                            String lineInfo, String stationNo, String lbsInfo,
                            String recordType);

        void uploadAlipay(AlipayUploadBeen alipayUploadBeen);

        void releseAlipayJni();

        void getWechatPublicKey();

        void wechatInit();

        void checkWechatQrCode(String code, List<WechatPublicKey.PubKeyListBean> pbKeyList, List<WechatPublicKey.MacKeyListBean> macKeyList, int payfee, byte scene, byte scantype, String posId, String posTrxId);

    }
}
