package com.spd.bus.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Class: TransportCard
 * package：com.yihuatong.tjgongjiaos.entity
 * Created by hzjst on 2018/5/17.
 * E_mail：hzjstning@163.com
 * Description：参数表
 */
@Table(name = "parameter", id = "Id")
public class TransportCard extends Model {
    @Column(name = "info")//参数
    private String info;
    @Column(name = "bus_number")
    private String bus_number;
    @Column(name = "device_number")
    private String device_number;
    @Column(name = "price")
    private String price;// 0提交，1未提交
    @Column(name = "blackversion")
    // 黑名单
    private String blackversion;
    @Column(name = "whiteversion")
    // 白名单
    private String whiteversion;
    @Column(name = "softversion")
    // 软件版本
    private String softversion;
    @Column(name = "binversion")
    // 软件版本
    private String binversion;
    @Column(name = "ratebinversion")
    // bin软件版本
    private String ratebinversion;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getBus_number() {
        return bus_number;
    }

    public void setBus_number(String bus_number) {
        this.bus_number = bus_number;
    }

    public String getDevice_number() {
        return device_number;
    }

    public void setDevice_number(String device_number) {
        this.device_number = device_number;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBlackversion() {
        return blackversion;
    }

    public void setBlackversion(String blackversion) {
        this.blackversion = blackversion;
    }

    public String getWhiteversion() {
        return whiteversion;
    }

    public void setWhiteversion(String whiteversion) {
        this.whiteversion = whiteversion;
    }

    public String getSoftversion() {
        return softversion;
    }

    public void setSoftversion(String softversion) {
        this.softversion = softversion;
    }

    public String getBinversion() {
        return binversion;
    }

    public void setBinversion(String binversion) {
        this.binversion = binversion;
    }

    public String getRatebinversion() {
        return ratebinversion;
    }

    public void setRatebinversion(String ratebinversion) {
        this.ratebinversion = ratebinversion;
    }
}
