package com.spd.bus.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Class: White
 * package：com.yihuatong.tjgongjiaos.entity
 * Created by hzjst on 2018/3/7.
 * E_mail：hzjstning@163.com
 * Description：白名单
 */
@Table(name = "White", id = "_Id")
public class White extends Model {
    @Column(name = "data")
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
