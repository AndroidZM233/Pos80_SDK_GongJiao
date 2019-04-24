package com.spd.base.been.tianjin;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 黑名单数据库
 * Created by 张明_ on 2019/4/24.
 * Email 741183142@qq.com
 */
@Entity
public class BlackDB {
    @Id(autoincrement = false)
    private String data;
    private String version;
    @Generated(hash = 543229465)
    public BlackDB(String data, String version) {
        this.data = data;
        this.version = version;
    }
    @Generated(hash = 334677556)
    public BlackDB() {
    }
    public String getData() {
        return this.data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getVersion() {
        return this.version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
}
