package com.spd.alipay.been;

import java.util.Arrays;

public class AliCodeinfoData {
    /**
     * inforState  获取二维码信息返回状态
     *
     * @param:alg_id 算法ID
     * @param:key_id 密钥ID
     * @param:code_issuer_no 发码机构号
     * @param:code_issuer_no_len 发码机构号长度
     * @param:user_id 用户ID
     * @param:user_id_len 用户ID长度
     * @param:card_type 卡类型
     * @param:card_type_len 卡类型长度
     * @param:card_no 卡号
     * @param:card_no_len 卡号长度
     * @param:card_data 卡数据
     * @param:card_data_len 卡数据长度
     */
    public int inforState;
    public int algId;
    public int keyId;
    public byte[] codeIssuerNo;
    public int codeIssuerNoLen;
    public byte[] userId;
    public int userIdLen;
    public byte[] cardType;
    public int cardTypeLen;
    public byte[] cardNo;
    public int cardNoLen;
    public byte[] cardData;
    public int cardDataLen;
    public byte[] alipayResult;

    public AliCodeinfoData() {
        super();
    }

    public AliCodeinfoData(int inforState, byte[] userId, byte[] cardType, byte[] cardNo, byte[] alipayResult) {
        this.inforState = inforState;
        this.userId = userId;
        this.cardType = cardType;
        this.cardNo = cardNo;
        this.alipayResult = alipayResult;
    }

    @Override
    public String toString() {
        return "AliCodeinfoData{" +
                "inforState=" + inforState +
                ", algId=" + algId +
                ", keyId=" + keyId +
                ", codeIssuerNo=" + Arrays.toString(codeIssuerNo) +
                ", codeIssuerNoLen=" + codeIssuerNoLen +
                ", userId=" + Arrays.toString(userId) +
                ", userIdLen=" + userIdLen +
                ", cardType=" + Arrays.toString(cardType) +
                ", cardTypeLen=" + cardTypeLen +
                ", cardNo=" + Arrays.toString(cardNo) +
                ", cardNoLen=" + cardNoLen +
                ", cardData=" + Arrays.toString(cardData) +
                ", cardDataLen=" + cardDataLen +
                ", alipayResult=" + Arrays.toString(alipayResult) +
                '}';
    }
}
