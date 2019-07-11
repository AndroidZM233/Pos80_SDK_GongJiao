package com.spd.base.been.tianjin;

import java.util.List;

/**
 * Created by 张明_ on 2019/7/11.
 * Email 741183142@qq.com
 */
public class YinLianBlackBack {

    /**
     * code : 00
     * blank_num : 3
     * time : 20190710
     * blank_data : [{"cardNo":"6225767707528865"},{"cardNo":"6216610200016617577"},{"cardNo":"6217570200001901891"}]
     */

    private String code;
    private int blank_num;
    private String time;
    private List<BlankDataBean> blank_data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getBlank_num() {
        return blank_num;
    }

    public void setBlank_num(int blank_num) {
        this.blank_num = blank_num;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<BlankDataBean> getBlank_data() {
        return blank_data;
    }

    public void setBlank_data(List<BlankDataBean> blank_data) {
        this.blank_data = blank_data;
    }

    public static class BlankDataBean {
        /**
         * cardNo : 6225767707528865
         */

        private String cardNo;

        public String getCardNo() {
            return cardNo;
        }

        public void setCardNo(String cardNo) {
            this.cardNo = cardNo;
        }
    }
}
