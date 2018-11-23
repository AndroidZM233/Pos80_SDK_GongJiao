package com.spd.base.database;

import android.app.Application;


import com.spd.base.been.MyObjectBox;

import io.objectbox.BoxStore;


public class BoxStorManage {

    private static BoxStore sBoxStore;

    public static void init(Application application) {
        sBoxStore = MyObjectBox.builder().androidContext(application).build();
    }

    private static class BoxStorManageHolder {
        private static final BoxStorManage INSTANCE = new BoxStorManage();
    }

    public static BoxStorManage getInstance() {
        return BoxStorManage.BoxStorManageHolder.INSTANCE;
    }
    public BoxStore getBoxDao() {
        return sBoxStore;
    }

}
