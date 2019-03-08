package com.spd.bus.card.methods.bean.produce;

import java.util.List;

/**
 * Created by 张明_ on 2019/3/7.
 * Email 741183142@qq.com
 */
public class ProduceYinLianCard {

    private List<PayinfoBean> payinfo;

    public List<PayinfoBean> getPayinfo() {
        return payinfo;
    }

    public void setPayinfo(List<PayinfoBean> payinfo) {
        this.payinfo = payinfo;
    }

    public static class PayinfoBean {
        /**
         * busNo : 121749
         * trans_seq : 0000000000000001
         * app_id : a310120180000028
         * service_id : 2
         * scan_time : 20180823094823
         * trip_no : 2733221582211072
         * driver : 00003000000151642876
         * line_no : 1459
         * amount : 200
         * team : 1459
         * route : 1459
         * posId : 17510418
         * voucher_type : 0
         * terminal_no : 17510418
         * dept : 12
         * voucher_no : 2733221582211072
         * user_id : 8652e0ca66263043
         * qrcode_data : AhEQBKMQEgGAAAAowAIQAQAeAgAnMyIVgiEQcgMWPGeGUuDKZiYwQxgAAAAAAAAAAAAAJzMiFYIhEHIAAAAAAAAVwCpoWcy4xw+1cCWGWeWuLA8aOBKZlym1FrmXsOPK/BLTbjaaChceab+br2LjHJb75O4NtbyMoRVnq/0sNiydBg==
         * create_time : 20180823094823
         * scan_confirm_type : 1
         */

        private String busNo;
        private String trans_seq;
        private String app_id;
        private String service_id;
        private String scan_time;
        private String trip_no;
        private String driver;
        private String line_no;
        private String amount;
        private String team;
        private String route;
        private String posId;
        private String voucher_type;
        private String terminal_no;
        private String dept;
        private String voucher_no;
        private String user_id;
        private String qrcode_data;
        private String create_time;
        private String scan_confirm_type;

        public String getBusNo() {
            return busNo;
        }

        public void setBusNo(String busNo) {
            this.busNo = busNo;
        }

        public String getTrans_seq() {
            return trans_seq;
        }

        public void setTrans_seq(String trans_seq) {
            this.trans_seq = trans_seq;
        }

        public String getApp_id() {
            return app_id;
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        public String getService_id() {
            return service_id;
        }

        public void setService_id(String service_id) {
            this.service_id = service_id;
        }

        public String getScan_time() {
            return scan_time;
        }

        public void setScan_time(String scan_time) {
            this.scan_time = scan_time;
        }

        public String getTrip_no() {
            return trip_no;
        }

        public void setTrip_no(String trip_no) {
            this.trip_no = trip_no;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getLine_no() {
            return line_no;
        }

        public void setLine_no(String line_no) {
            this.line_no = line_no;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
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

        public String getPosId() {
            return posId;
        }

        public void setPosId(String posId) {
            this.posId = posId;
        }

        public String getVoucher_type() {
            return voucher_type;
        }

        public void setVoucher_type(String voucher_type) {
            this.voucher_type = voucher_type;
        }

        public String getTerminal_no() {
            return terminal_no;
        }

        public void setTerminal_no(String terminal_no) {
            this.terminal_no = terminal_no;
        }

        public String getDept() {
            return dept;
        }

        public void setDept(String dept) {
            this.dept = dept;
        }

        public String getVoucher_no() {
            return voucher_no;
        }

        public void setVoucher_no(String voucher_no) {
            this.voucher_no = voucher_no;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getQrcode_data() {
            return qrcode_data;
        }

        public void setQrcode_data(String qrcode_data) {
            this.qrcode_data = qrcode_data;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getScan_confirm_type() {
            return scan_confirm_type;
        }

        public void setScan_confirm_type(String scan_confirm_type) {
            this.scan_confirm_type = scan_confirm_type;
        }
    }
}
