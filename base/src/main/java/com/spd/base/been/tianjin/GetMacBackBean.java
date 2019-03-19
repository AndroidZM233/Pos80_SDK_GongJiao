package com.spd.base.been.tianjin;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 张明_ on 2019/2/21.
 * Email 741183142@qq.com
 */
@Entity
public class GetMacBackBean {

    /**
     * id : 10
     * num : 8
     * macKeyList : [{"1":"AB888C81CF35EF87A59C8F977DEFB6D0"},{"2":"FC6FC86E629B86E8751454DC614C79FE"},{"3":"18CADDA64D283F352ABE60FC64ED4A34"},{"4":"3DA96F73683B54AF75489D8DD5FE77C0"},{"5":"320B1AE92CC9D43BB7BA237DE1EAE354"},{"6":"4C3AEADD361FBFA0AB95B8BED4F9573F"},{"7":"306B0E229A7E4A9F864EA5D1987D2039"},{"8":"EEEAAA6BF198BD5052B3F00367D0B7A8"}]
     * insertTime : 1540494598000
     */

    private int id;
    private int num;
    private String macKeyList;
    private long insertTime;
    @Generated(hash = 455519378)
    public GetMacBackBean(int id, int num, String macKeyList, long insertTime) {
        this.id = id;
        this.num = num;
        this.macKeyList = macKeyList;
        this.insertTime = insertTime;
    }
    @Generated(hash = 1173030565)
    public GetMacBackBean() {
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getNum() {
        return this.num;
    }
    public void setNum(int num) {
        this.num = num;
    }
    public String getMacKeyList() {
        return this.macKeyList;
    }
    public void setMacKeyList(String macKeyList) {
        this.macKeyList = macKeyList;
    }
    public long getInsertTime() {
        return this.insertTime;
    }
    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }
}
