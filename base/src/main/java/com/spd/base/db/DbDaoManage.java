package com.spd.base.db;

import android.app.Application;

import com.spd.base.been.tianjin.DaoMaster;
import com.spd.base.been.tianjin.DaoSession;


public class DbDaoManage {
    private static DaoSession daoSession;
    /**
     * 初始化数据库
     *
     * @param application
     */
    public static void initDb(Application application) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(application, "Pickrand1", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getReadableDb());
        daoSession = daoMaster.newSession();
    }
    public static DaoSession getDaoSession() {
        return daoSession;
    }
}
