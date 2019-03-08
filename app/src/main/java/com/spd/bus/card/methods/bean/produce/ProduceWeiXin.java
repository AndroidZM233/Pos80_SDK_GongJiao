package com.spd.bus.card.methods.bean.produce;

import java.util.List;

/**
 * Created by 张明_ on 2019/3/7.
 * Email 741183142@qq.com
 */
public class ProduceWeiXin {

    private List<PayinfoBean> payinfo;

    public List<PayinfoBean> getPayinfo() {
        return payinfo;
    }

    public void setPayinfo(List<PayinfoBean> payinfo) {
        this.payinfo = payinfo;
    }

    public static class PayinfoBean {
        /**
         * open_id : 30206282AA719899
         * driverSignTime : 2018-11-02 14:32:21
         * team : 0503
         * route : 0503
         * account : 10
         * dept : 00
         * in_station_time : 2018-11-02 14:50:28
         * bus_no : 110020
         * driver : 00003000000151650676
         * pos_id : 17460046
         * record_in : AAEBAqmGPc9IZ2FSOnqeYFL6RtZTPkbojU8dzTHrYkzw/fiTmYSQT5oA9/eB1bGP/BMtWlWcFxlYv658n8q5S3v1e4kBX1ckdRLdDWDtp9aHQoYf
         */

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
        private String record_in;

        public String getOpen_id() {
            return open_id;
        }

        public void setOpen_id(String open_id) {
            this.open_id = open_id;
        }

        public String getDriverSignTime() {
            return driverSignTime;
        }

        public void setDriverSignTime(String driverSignTime) {
            this.driverSignTime = driverSignTime;
        }

        public String getTeam() {
            return team;
        }

        public void setTeam(String team) {
            this.team = team;
        }

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getDept() {
            return dept;
        }

        public void setDept(String dept) {
            this.dept = dept;
        }

        public String getIn_station_time() {
            return in_station_time;
        }

        public void setIn_station_time(String in_station_time) {
            this.in_station_time = in_station_time;
        }

        public String getBus_no() {
            return bus_no;
        }

        public void setBus_no(String bus_no) {
            this.bus_no = bus_no;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getPos_id() {
            return pos_id;
        }

        public void setPos_id(String pos_id) {
            this.pos_id = pos_id;
        }

        public String getRecord_in() {
            return record_in;
        }

        public void setRecord_in(String record_in) {
            this.record_in = record_in;
        }
    }
}
