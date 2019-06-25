package com.spd.bus.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Class: Blacklist
 * package：com.yihuatong.tjgongjiaos.entity
 * Created by hzjst on 2018/3/7.
 * E_mail：hzjstning@163.com
 * Description：黑名单
 */
@Table(name = "blacklist", id = "Id")
public class Blacklist extends Model {
    @Column(name = "blackcode")
    private String blackcode;

    public String getBlackcode() {
        return blackcode;
    }

    public void setBlackcode(String blackcode) {
        this.blackcode = blackcode;
    }
}
