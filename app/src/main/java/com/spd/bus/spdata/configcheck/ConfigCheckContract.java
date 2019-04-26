package com.spd.bus.spdata.configcheck;

import android.content.Context;

import com.spd.bus.spdata.mvp.BasePresenter;
import com.spd.bus.spdata.mvp.BaseView;

import wangpos.sdk4.libbasebinder.BankCard;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ConfigCheckContract {
    interface View extends BaseView {
        void setTextView(int address,String msg);

        void openActivity();
    }

    interface Presenter extends BasePresenter<View> {
        void initPsam(Context context);
    }
}
