package com.spd.base.been.tianjin;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 张明_ on 2019/5/6.
 * Email 741183142@qq.com
 */
@Entity
public class White {
    private int _Id;
    private String data;
    @Generated(hash = 264454422)
    public White(int _Id, String data) {
        this._Id = _Id;
        this.data = data;
    }
    @Generated(hash = 1575778372)
    public White() {
    }
    public int get_Id() {
        return this._Id;
    }
    public void set_Id(int _Id) {
        this._Id = _Id;
    }
    public String getData() {
        return this.data;
    }
    public void setData(String data) {
        this.data = data;
    }

}
