package com.spd.alipay;

import com.spd.alipay.been.AlipayPublicKey;
import com.spd.base.mvp.BasePresenter;
import com.spd.base.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class AlipayContract {
    public interface View extends BaseView {
        /**
         * /界面显示
         */
        void success(Object o);

        void erro(String msg);

        void getPublicKey(AlipayPublicKey alipayPublickKey);

    }

    interface Presenter extends BasePresenter<View> {
        //实现功能
        void getPublicKey();
    }
}
