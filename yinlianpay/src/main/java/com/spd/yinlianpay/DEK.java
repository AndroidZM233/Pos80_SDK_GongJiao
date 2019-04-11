package com.spd.yinlianpay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoxiaomeng on 2018/11/19.
 */

public class DEK {
    public String getDETVALUE() {
        return DETVALUE;
    }

    public void setDETVALUE(String DETVALUE) {
        this.DETVALUE = DETVALUE;
    }

    private String DETVALUE;

    public List<DET> getDets() {
        return dets;
    }

    public void setDets(List<DET> dets) {
        this.dets = dets;
    }

    private List<DET> dets = new ArrayList<>();


}
