package com.wechat;


import com.wechat.been.WechatPublicKey;
import com.wechat.mvp.BasePresenter;
import com.wechat.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class WechatsContract {
    public interface View extends BaseView {
        /**
         * /界面显示
         */
        void success(Object o);

        void erro(String msg);

        void getPublicKey(WechatPublicKey publicKey);
    }

    interface Presenter extends BasePresenter<View> {
        //实现功能
        void checkQRCode(String code, List<WechatPublicKey.PubKeyListBean> pbKeyList, List<WechatPublicKey.MacKeyListBean> macKeyList);

        void getPublicKey();

        void getPrivateKey();
    }
}
