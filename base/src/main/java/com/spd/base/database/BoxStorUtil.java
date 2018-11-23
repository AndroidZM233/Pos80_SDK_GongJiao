package com.spd.base.database;

import com.spd.base.been.AlipayDatabaseBeen;

import java.util.List;

public class BoxStorUtil {

    public static void saveAlipayKey(AlipayDatabaseBeen alipayDatabaseBeen) {
        BoxStorManage.getInstance().getBoxDao().boxFor(AlipayDatabaseBeen.class).put(alipayDatabaseBeen);
    }

    public static List<AlipayDatabaseBeen> getAlipayKeyVension() {
        return BoxStorManage.getInstance().getBoxDao().boxFor(AlipayDatabaseBeen.class).getAll();
    }
}
