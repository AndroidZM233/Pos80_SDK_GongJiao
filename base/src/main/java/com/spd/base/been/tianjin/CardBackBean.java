package com.spd.base.been.tianjin;

/**
 * Created by 张明_ on 2019/2/20.
 * Email 741183142@qq.com
 */
public class CardBackBean {
    private int backValue;
    private int blance;

    public CardBackBean(int backValue, int blance) {
        this.backValue = backValue;
        this.blance = blance;
    }

    public int getBackValue() {
        return backValue;
    }

    public void setBackValue(int backValue) {
        this.backValue = backValue;
    }

    public int getBlance() {
        return blance;
    }

    public void setBlance(int blance) {
        this.blance = blance;
    }
}
