package com.spd.base.been.tianjin.produce.yinlian;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by 张明_ on 2019/3/12.
 * Email 741183142@qq.com
 */
@Entity
public class UploadInfoYinLianDB {

    @Id(autoincrement = false)
    //交易流水号
    private String trans_seq;

    //车辆号
    private String busNo;

    //APPID
    private String app_id;
    //业务标识
    private String service_id;
    //扫码时间
    private String scan_time;
    private String trip_no;
    //司机号
    private String driver;
    //线路号
    private String line_no;
    //金额
    private String amount;
    //路队
    private String team;
    //线路号
    private String route;
    //机具号
    private String posId;
    //用户凭证类型
    private String voucher_type;
    //机具号
    private String terminal_no;
    //公司号
    private String dept;
    private String voucher_no;
    //用户标识
    private String user_id;
    //二级码原数据
    private String qrcode_data;
    //生成时间
    private String create_time;
    //扫码确认类型
    private String scan_confirm_type;

    //是否上传
    private boolean isUpload;

    @Generated(hash = 291665890)
    public UploadInfoYinLianDB(String trans_seq, String busNo, String app_id,
            String service_id, String scan_time, String trip_no, String driver,
            String line_no, String amount, String team, String route, String posId,
            String voucher_type, String terminal_no, String dept, String voucher_no,
            String user_id, String qrcode_data, String create_time,
            String scan_confirm_type, boolean isUpload) {
        this.trans_seq = trans_seq;
        this.busNo = busNo;
        this.app_id = app_id;
        this.service_id = service_id;
        this.scan_time = scan_time;
        this.trip_no = trip_no;
        this.driver = driver;
        this.line_no = line_no;
        this.amount = amount;
        this.team = team;
        this.route = route;
        this.posId = posId;
        this.voucher_type = voucher_type;
        this.terminal_no = terminal_no;
        this.dept = dept;
        this.voucher_no = voucher_no;
        this.user_id = user_id;
        this.qrcode_data = qrcode_data;
        this.create_time = create_time;
        this.scan_confirm_type = scan_confirm_type;
        this.isUpload = isUpload;
    }

    @Generated(hash = 615220577)
    public UploadInfoYinLianDB() {
    }

    public String getTrans_seq() {
        return this.trans_seq;
    }

    public void setTrans_seq(String trans_seq) {
        this.trans_seq = trans_seq;
    }

    public String getBusNo() {
        return this.busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public String getApp_id() {
        return this.app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getService_id() {
        return this.service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getScan_time() {
        return this.scan_time;
    }

    public void setScan_time(String scan_time) {
        this.scan_time = scan_time;
    }

    public String getTrip_no() {
        return this.trip_no;
    }

    public void setTrip_no(String trip_no) {
        this.trip_no = trip_no;
    }

    public String getDriver() {
        return this.driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getLine_no() {
        return this.line_no;
    }

    public void setLine_no(String line_no) {
        this.line_no = line_no;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getPosId() {
        return this.posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    public String getVoucher_type() {
        return this.voucher_type;
    }

    public void setVoucher_type(String voucher_type) {
        this.voucher_type = voucher_type;
    }

    public String getTerminal_no() {
        return this.terminal_no;
    }

    public void setTerminal_no(String terminal_no) {
        this.terminal_no = terminal_no;
    }

    public String getDept() {
        return this.dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getVoucher_no() {
        return this.voucher_no;
    }

    public void setVoucher_no(String voucher_no) {
        this.voucher_no = voucher_no;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getQrcode_data() {
        return this.qrcode_data;
    }

    public void setQrcode_data(String qrcode_data) {
        this.qrcode_data = qrcode_data;
    }

    public String getCreate_time() {
        return this.create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getScan_confirm_type() {
        return this.scan_confirm_type;
    }

    public void setScan_confirm_type(String scan_confirm_type) {
        this.scan_confirm_type = scan_confirm_type;
    }

    public boolean getIsUpload() {
        return this.isUpload;
    }

    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }


}
