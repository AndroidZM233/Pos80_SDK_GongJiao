package com.spd.base.db;

import android.app.Application;


import com.spd.base.beenwechat.DaoMaster;
import com.spd.base.beenwechat.DaoSession;
import com.spd.base.database.BoxStorManage;

import org.greenrobot.greendao.database.Database;

public class DbDaoManage {


    private static DaoSession daoSession;

    /**
     * 初始化数据库
     *
     * @param application
     */
    public static void initDb(Application application) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(application, "spdBusDb", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getReadableDb());
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }
}
