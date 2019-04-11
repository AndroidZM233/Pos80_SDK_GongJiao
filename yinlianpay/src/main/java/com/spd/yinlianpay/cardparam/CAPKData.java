package com.spd.yinlianpay.cardparam;


import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018\9\6 0006.
 */

public class CAPKData {
    @SerializedName("RID")
    private String rid;
    @SerializedName("Public Key Index")
    private String ridIndex;
    @SerializedName("Public Key Expired Date")
    private String expirationDate;
    @SerializedName("Public Key Hash Algorithm")
    private String hashAlgorithm;
    @SerializedName("Public Key Algorithm")
    private String publicKeyAlgorithm;
    @SerializedName("Public Key Modulus")
    private String modulus;
    @SerializedName("Public Key Exponent")
    private String exponent;
    @SerializedName("Public Key Check Value")
    private String checkSum;

    @Override
    public String toString() {
        return "CAPKData{" +
                "ridIndex='" + ridIndex + '\'' +
                ", rid='" + rid + '\'' +
                ", modulus='" + modulus + '\'' +
                ", exponent='" + exponent + '\'' +
                ", checkSum='" + checkSum + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", publicKeyAlgorithm='" + publicKeyAlgorithm + '\'' +
                ", hashAlgorithm='" + hashAlgorithm + '\'' +
                '}';
    }

    public String getRidIndex() {
        return ridIndex;
    }

    public String getRid() {
        return rid;
    }

    public String getModulus() {
        return modulus;
    }

    public String getExponent() {
        return exponent;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getPublicKeyAlgorithm() {
        return publicKeyAlgorithm;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }
}
