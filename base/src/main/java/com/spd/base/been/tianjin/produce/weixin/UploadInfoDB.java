package com.spd.base.been.tianjin.produce.weixin;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by 张明_ on 2019/3/12.
 * Email 741183142@qq.com
 */
@Entity
public class UploadInfoDB {
    //用户ID
    private String open_id;
    //司机签到时间
    private String driverSignTime;
    //路队号
    private String team;
    //线路号
    private String route;
    //票价
    private String account;
    //公司号
    private String dept;
    // 上车时间 (验码完成时间)
    private String in_station_time;
    //车辆号
    private String bus_no;
    //司机卡号
    private String driver;
    //机具号
    private String pos_id;
    //验码记录
    @Id(autoincrement = false)
    private String record_in;

    //是否上传
    private boolean isUpload;

    @Generated(hash = 1534426963)
    public UploadInfoDB(String open_id, String driverSignTime, String team,
            String route, String account, String dept, String in_station_time,
            String bus_no, String driver, String pos_id, String record_in,
            boolean isUpload) {
        this.open_id = open_id;
        this.driverSignTime = driverSignTime;
        this.team = team;
        this.route = route;
        this.account = account;
        this.dept = dept;
        this.in_station_time = in_station_time;
        this.bus_no = bus_no;
        this.driver = driver;
        this.pos_id = pos_id;
        this.record_in = record_in;
        this.isUpload = isUpload;
    }

    @Generated(hash = 1352250925)
    public UploadInfoDB() {
    }

    public String getOpen_id() {
        return this.open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public String getDriverSignTime() {
        return this.driverSignTime;
    }

    public void setDriverSignTime(String driverSignTime) {
        this.driverSignTime = driverSignTime;
    }

    public String getTeam() {
        return this.team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getRoute() {
        return this.route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDept() {
        return this.dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getIn_station_time() {
        return this.in_station_time;
    }

    public void setIn_station_time(String in_station_time) {
        this.in_station_time = in_station_time;
    }

    public String getBus_no() {
        return this.bus_no;
    }

    public void setBus_no(String bus_no) {
        this.bus_no = bus_no;
    }

    public String getDriver() {
        return this.driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getPos_id() {
        return this.pos_id;
    }

    public void setPos_id(String pos_id) {
        this.pos_id = pos_id;
    }

    public String getRecord_in() {
        return this.record_in;
    }

    public void setRecord_in(String record_in) {
        this.record_in = record_in;
    }

    public boolean getIsUpload() {
        return this.isUpload;
    }

    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }


}
