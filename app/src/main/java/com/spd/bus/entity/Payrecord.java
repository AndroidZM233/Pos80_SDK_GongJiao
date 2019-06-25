package com.spd.bus.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

//交易数据记录表
@Table(name = "Payrecord", id = "_Id")
public class Payrecord extends Model {

    @Column(name = "record")
    // 记录
    private String record;
    @Column(name = "datetime")
    // 时间戳
    private String datetime;
    // @Column(name = "time")
    // private String time;
    @Column(name = "tag")
    // 上传标志
    private int tag;// 0提交，1未提交
    @Column(name = "xiaofei")
    // 消费金额
    private double xiaofei;
    // 0代表司机记录，1代表消费记录,2代表月票,3灰记录，4交通部黑名单
    @Column(name = "buscard")
    private int buscard;

    @Column(name = "jilu")
    // 是否灰记录 0正常；1灰记录，3交通部黑名单
    private int jilu;
    @Column(name = "writetag")
    // 写文件标志
    private int writetag;

    @Column(name = "traffic")
    // 是否交通部标示 0首条，1第二条，2交通部黑名单
    private int traffic;
    @Column(name = "tradingflow")
    private long tradingflow;

    public long getTradingflow() {
        return tradingflow;
    }

    public void setTradingflow(long tradingflow) {
        this.tradingflow = tradingflow;
    }

    public int getTraffic() {
        return traffic;
    }

    public void setTraffic(int traffic) {
        this.traffic = traffic;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getWritetag() {
        return writetag;
    }

    public void setWritetag(int writetag) {
        this.writetag = writetag;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatatime(String datetime) {
        this.datetime = datetime;
    }

    public int getJilu() {
        return jilu;
    }

    public void setJilu(int jilu) {
        this.jilu = jilu;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public double getXiaofei() {
        return xiaofei;
    }

    public void setXiaofei(double xiaofei) {
        this.xiaofei = xiaofei;
    }

    public int getBuscard() {
        return buscard;
    }

    public void setBuscard(int buscard) {
        this.buscard = buscard;
    }

    @Override
    public String toString() {
        return record;
    }

}
