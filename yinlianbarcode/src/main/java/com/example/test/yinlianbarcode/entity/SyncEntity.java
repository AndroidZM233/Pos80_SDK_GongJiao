package com.example.test.yinlianbarcode.entity;

import com.google.gson.Gson;

/**
 * Created by 张明_ on 2018/8/6.
 * Email 741183142@qq.com
 */

public class SyncEntity {

    /**
     * trans_seq : 7098194024184262
     * voucher_no : 2370822086755328
     * trip_no : 2370822086755328
     * app_id : A310120180000029
     * service_id : 02
     * user_id : 2231566612315668
     * voucher_type : 00
     * create_time : 20180417094241
     * op_id : C450120182000001
     * line_no : 0001
     * station_no : 0111
     * direction : 01
     * terminal_no : 5101000000000001
     * terminal_ip : 10.10.3.101
     * scan_time : 20180417094234
     * scan_confirm_type : 01
     * qrcode_data : ASEpACcAI3CCIIZ1UyilEBIBgAAAJFEBAAEiMVZmEjFWaAJtexEAHgMAGAAAAAAAAAAAAAAjcIIghnVTKAAAAAAAAAuqgHWYyq7t
     * lng : null
     * lat : null
     * amount : 0000000001
     * reserved : null
     */
    String trip_no;
    String app_id;
    String user_id;
    String service_id;
    String in_voucher_no;
    String in_line_no;
    String in_station_no;
    String in_time;
    String in_sys_time;
    String out_qrcode_data ;

    String fellow_no = "0000000000000000";
    String in_line_name = null;
    String in_station_name = null;
    String in_status = "01";
    String in_sys_lng = null;
    String in_sys_lat = null;
    String in_confirm_type = "01";
    String out_voucher_no = null;
    String out_line_no = null;
    String out_line_name = null;
    String out_station_no = null;
    String out_station_name = null;
    String out_time = null;
    String out_status = null;
    String out_sys_time = null;
    String out_sys_lng = null;
    String out_sys_lat = null;
    String out_confirm_type = null;
    String mileage = null;
    String trip_sts = "01";
    String trip_sts_seq = "1";

    public String getTrip_no() {
        return trip_no;
    }

    public void setTrip_no(String trip_no) {
        this.trip_no = trip_no;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getIn_voucher_no() {
        return in_voucher_no;
    }

    public void setIn_voucher_no(String in_voucher_no) {
        this.in_voucher_no = in_voucher_no;
    }

    public String getIn_line_no() {
        return in_line_no;
    }

    public void setIn_line_no(String in_line_no) {
        this.in_line_no = in_line_no;
    }

    public String getIn_station_no() {
        return in_station_no;
    }

    public void setIn_station_no(String in_station_no) {
        this.in_station_no = in_station_no;
    }

    public String getIn_time() {
        return in_time;
    }

    public void setIn_time(String in_time) {
        this.in_time = in_time;
    }

    public String getIn_sys_time() {
        return in_sys_time;
    }

    public void setIn_sys_time(String in_sys_time) {
        this.in_sys_time = in_sys_time;
    }

    public String getOut_qrcode_data() {
        return out_qrcode_data;
    }

    public void setOut_qrcode_data(String out_qrcode_data) {
        this.out_qrcode_data = out_qrcode_data;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
