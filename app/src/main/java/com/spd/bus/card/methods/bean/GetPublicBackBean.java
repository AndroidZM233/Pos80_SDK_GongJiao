package com.spd.bus.card.methods.bean;

/**
 * Created by 张明_ on 2019/2/21.
 * Email 741183142@qq.com
 */
public class GetPublicBackBean {

    /**
     * id : 10
     * num : 2
     * cache : 10
     * pubkeyList : [{"1":"0486A135BD93391DAC4257C3BD03E5EAE12A2EC8CDB299DCC45282CD2204A7FD1D6A6A555A21B051D41D0B8F81D0792FC548C776AAAD0B7B36"},{"2":"044BAA25DC0EB51E88A2A97E4777E3767987A6D7EA87E101FF6DF390A9F8D2022823BDEFFF49B464AF9F5AF113C7B65C2B934AC4F6817AC47D"}]
     * insertTime : 1540493998000
     */

    private int id;
    private int num;
    private int cache;
    private String pubkeyList;
    private long insertTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getCache() {
        return cache;
    }

    public void setCache(int cache) {
        this.cache = cache;
    }

    public String getPubkeyList() {
        return pubkeyList;
    }

    public void setPubkeyList(String pubkeyList) {
        this.pubkeyList = pubkeyList;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }
}
