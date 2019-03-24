package com.spd.base.been.tianjin;

/**
 * Created by 张明_ on 2019/2/20.
 * Email 741183142@qq.com
 */
public class CardBackBean {
    private int backValue;
    private TCardOpDU cardOpDU;

    public CardBackBean(int backValue, TCardOpDU cardOpDU) {
        this.backValue = backValue;
        this.cardOpDU = cardOpDU;
    }

    public TCardOpDU getCardOpDU() {
        return cardOpDU;
    }

    public void setCardOpDU(TCardOpDU cardOpDU) {
        this.cardOpDU = cardOpDU;
    }

    public int getBackValue() {
        return backValue;
    }

    public void setBackValue(int backValue) {
        this.backValue = backValue;
    }

}
