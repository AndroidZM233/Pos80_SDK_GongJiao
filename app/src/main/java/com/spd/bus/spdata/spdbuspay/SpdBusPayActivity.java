package com.spd.bus.spdata.spdbuspay;


import com.spd.alipay.been.AliCodeinfoData;
import com.spd.alipay.been.AlipayPublicKey;
import com.spd.bus.spdata.mvp.MVPBaseActivity;
import com.wechat.been.WechatPublicKey;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class SpdBusPayActivity extends MVPBaseActivity<SpdBusPayContract.View, SpdBusPayPresenter> implements SpdBusPayContract.View {

    @Override
    public void success(String msg) {

    }

    @Override
    public void erro(String msg) {

    }

    @Override
    public void getAliPublicKey(AlipayPublicKey alipayPublickKey) {

    }

    @Override
    public void aliPayInit(int result) {

    }

    @Override
    public void checkAliQrCode(AliCodeinfoData testdata) {

    }

    @Override
    public void releseAlipayJni(int result) {

    }

    @Override
    public void checkWechatQrCode(int result, String wechatResult, String openId) {

    }

    @Override
    public void getWechatPublicKey(WechatPublicKey wechatPublicKey) {

    }


}
