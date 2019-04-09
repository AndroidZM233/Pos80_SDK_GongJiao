package com.spd.bus.card.utils;

import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.FileUtils;

import java.util.List;

/**
 * Created by 张明_ on 2019/3/28.
 * Email 741183142@qq.com
 */
public class ConfigUtils {
    /**
     * 读取本地是否有参数配置备份文件
     */
    public static void loadTxtConfig() {
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
        if (runParaFiles == null || runParaFiles.size() == 0) {
            String content = FileUtils.readFileContent(Environment
                    .getExternalStorageDirectory() + "/card.txt").toString();
            if (!TextUtils.isEmpty(content)) {
                Gson gson = new Gson();
                RunParaFile runParaFile = gson.fromJson(content, RunParaFile.class);
                DbDaoManage.getDaoSession().getRunParaFileDao().insert(runParaFile);
            }
        }
    }
}
