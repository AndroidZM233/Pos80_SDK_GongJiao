package com.spd.yinlianpay.termConig;

import com.google.gson.annotations.SerializedName;

/**
 * Created by guoxiaomeng on 2018/11/21.
 */

public class TermConfigParam {
    /*byte[] ucMaxLifeTimeTornFlg           = new byte[mLengths[0]];     // DF811C 异常终端交易记录生命周期标识
    byte[] aucMaxLifeTimeTorn          = new byte[mLengths[1]];     //DF811C 异常终端交易记录生命周期
    byte[] ucMaxNumberTornFlg         = new byte[mLengths[2]];     //DF811D 异常终端交易记录最大数标识
    byte[] ucMaxNumberTorn        = new byte[mLengths[3]];     //DF811D 异常终端交易记录最大数
    byte[] ucBalanceBeforeGACFlg       = new byte[mLengths[4]];     //DF8104 在GAC前读取余额标识
    byte[] aucBalanceBeforeGAC      = new byte[mLengths[5]];     //DF8104 在GAC前读取余额
    byte[] ucBalanceAfterGACFlg         = new byte[mLengths[6]];     //DF8105 在GAC后读取余额标识
    byte[] aucBalanceAfterGAC      = new byte[mLengths[7]];     //DF8105 在GAC后读取余额
    byte[] ucMobileSupFlg        = new byte[mLengths[8]];     //9F7E 手机支持指示器标识
    byte[] ucMobileSup      = new byte[mLengths[9]];     //9F7E 手机支持指示器
    byte[] ucHoldTimeValueFlg        = new byte[mLengths[10]];    //DF8130 持有时间标识
    byte[] ucHoldTimeValue        = new byte[mLengths[11]];    //DF8130 持有时间Hold Time Value
    byte[] ucInterDevSerNumFlg        = new byte[mLengths[12]];    //9F1E 制作商为IFD分配的唯一永久序列号标识
    byte[] aucInterDevSerNum        = new byte[mLengths[13]];    //9F1E 制作商为IFD分配的唯一永久序列号Interface Device Serial Number
    byte[] ucKernelIDFlg        = new byte[mLengths[14]];    //DF810C 内核类型标识
    byte[] ucKernelID        = new byte[mLengths[15]];    //DF810C Kernel ID
    byte[] ucMsgHoldTimeFlg        = new byte[mLengths[16]];    //DF812D 消息持有时间标识
    byte[] aucMsgHoldTime        = new byte[mLengths[17]];    //DF812D 消息持有时间Message Hold Time

    byte[] MinimumRRGP = new byte[mLengths[18]]; //DF8132
    byte[] MaximumRelayRGP = new byte[mLengths[19]];//DF8133
    byte[] TerminalETTFRRCAPDU = new byte[mLengths[20]];//DF8134
    byte[] TerminalETTFRRRAPDU = new byte[mLengths[21]];//DF8135
    byte[] RelayRAT = new byte[mLengths[22]];//DF8136
    byte[] RelayRTTMT = new byte[mLengths[23]];//DF8137

    byte[] MinimumRRGPfalg = new byte[mLengths[24]]; //DF8132
    byte[] MaximumRelayRGPfalg = new byte[mLengths[25]];//DF8133
    byte[] TerminalETTFRRCAPDUfalg = new byte[mLengths[26]];//DF8134
    byte[] TerminalETTFRRRAPDUfalg = new byte[mLengths[27]];//DF8135
    byte[] RelayRATfalg = new byte[mLengths[28]];//DF8136
    byte[] RelayRTTMTfalg = new byte[mLengths[29]];//DF8137*/


    @SerializedName("ucMaxLifeTimeTornFlg")
    private String ucMaxLifeTimeTornFlg           ;//= new byte[mLengths[0]];     // DF811C 异常终端交易记录生命周期标识
    @SerializedName("aucMaxLifeTimeTorn")
    private String aucMaxLifeTimeTorn          ;//= new byte[mLengths[1]];     //DF811C 异常终端交易记录生命周期

    public String getUcMaxLifeTimeTornFlg() {
        return ucMaxLifeTimeTornFlg;
    }

    public void setUcMaxLifeTimeTornFlg(String ucMaxLifeTimeTornFlg) {
        this.ucMaxLifeTimeTornFlg = ucMaxLifeTimeTornFlg;
    }

    public String getAucMaxLifeTimeTorn() {
        return aucMaxLifeTimeTorn;
    }

    public void setAucMaxLifeTimeTorn(String aucMaxLifeTimeTorn) {
        this.aucMaxLifeTimeTorn = aucMaxLifeTimeTorn;
    }

    public String getUcMaxNumberTornFlg() {
        return ucMaxNumberTornFlg;
    }

    public void setUcMaxNumberTornFlg(String ucMaxNumberTornFlg) {
        this.ucMaxNumberTornFlg = ucMaxNumberTornFlg;
    }

    public String getUcMaxNumberTorn() {
        return ucMaxNumberTorn;
    }

    public void setUcMaxNumberTorn(String ucMaxNumberTorn) {
        this.ucMaxNumberTorn = ucMaxNumberTorn;
    }

    public String getUcBalanceBeforeGACFlg() {
        return ucBalanceBeforeGACFlg;
    }

    public void setUcBalanceBeforeGACFlg(String ucBalanceBeforeGACFlg) {
        this.ucBalanceBeforeGACFlg = ucBalanceBeforeGACFlg;
    }

    public String getAucBalanceBeforeGAC() {
        return aucBalanceBeforeGAC;
    }

    public void setAucBalanceBeforeGAC(String aucBalanceBeforeGAC) {
        this.aucBalanceBeforeGAC = aucBalanceBeforeGAC;
    }

    public String getUcBalanceAfterGACFlg() {
        return ucBalanceAfterGACFlg;
    }

    public void setUcBalanceAfterGACFlg(String ucBalanceAfterGACFlg) {
        this.ucBalanceAfterGACFlg = ucBalanceAfterGACFlg;
    }

    public String getAucBalanceAfterGAC() {
        return aucBalanceAfterGAC;
    }

    public void setAucBalanceAfterGAC(String aucBalanceAfterGAC) {
        this.aucBalanceAfterGAC = aucBalanceAfterGAC;
    }

    public String getUcMobileSupFlg() {
        return ucMobileSupFlg;
    }

    public void setUcMobileSupFlg(String ucMobileSupFlg) {
        this.ucMobileSupFlg = ucMobileSupFlg;
    }

    public String getUcMobileSup() {
        return ucMobileSup;
    }

    public void setUcMobileSup(String ucMobileSup) {
        this.ucMobileSup = ucMobileSup;
    }

    public String getUcHoldTimeValueFlg() {
        return ucHoldTimeValueFlg;
    }

    public void setUcHoldTimeValueFlg(String ucHoldTimeValueFlg) {
        this.ucHoldTimeValueFlg = ucHoldTimeValueFlg;
    }

    public String getUcHoldTimeValue() {
        return ucHoldTimeValue;
    }

    public void setUcHoldTimeValue(String ucHoldTimeValue) {
        this.ucHoldTimeValue = ucHoldTimeValue;
    }

    public String getUcInterDevSerNumFlg() {
        return ucInterDevSerNumFlg;
    }

    public void setUcInterDevSerNumFlg(String ucInterDevSerNumFlg) {
        this.ucInterDevSerNumFlg = ucInterDevSerNumFlg;
    }

    public String getAucInterDevSerNum() {
        return aucInterDevSerNum;
    }

    public void setAucInterDevSerNum(String aucInterDevSerNum) {
        this.aucInterDevSerNum = aucInterDevSerNum;
    }

    public String getUcKernelIDFlg() {
        return ucKernelIDFlg;
    }

    public void setUcKernelIDFlg(String ucKernelIDFlg) {
        this.ucKernelIDFlg = ucKernelIDFlg;
    }

    public String getUcKernelID() {
        return ucKernelID;
    }

    public void setUcKernelID(String ucKernelID) {
        this.ucKernelID = ucKernelID;
    }

    public String getUcMsgHoldTimeFlg() {
        return ucMsgHoldTimeFlg;
    }

    public void setUcMsgHoldTimeFlg(String ucMsgHoldTimeFlg) {
        this.ucMsgHoldTimeFlg = ucMsgHoldTimeFlg;
    }

    public String getAucMsgHoldTime() {
        return aucMsgHoldTime;
    }

    public void setAucMsgHoldTime(String aucMsgHoldTime) {
        this.aucMsgHoldTime = aucMsgHoldTime;
    }

    public String getMinimumRRGP() {
        return MinimumRRGP;
    }

    public void setMinimumRRGP(String minimumRRGP) {
        MinimumRRGP = minimumRRGP;
    }

    public String getMaximumRelayRGP() {
        return MaximumRelayRGP;
    }

    public void setMaximumRelayRGP(String maximumRelayRGP) {
        MaximumRelayRGP = maximumRelayRGP;
    }

    public String getTerminalETTFRRCAPDU() {
        return TerminalETTFRRCAPDU;
    }

    public void setTerminalETTFRRCAPDU(String terminalETTFRRCAPDU) {
        TerminalETTFRRCAPDU = terminalETTFRRCAPDU;
    }

    public String getTerminalETTFRRRAPDU() {
        return TerminalETTFRRRAPDU;
    }

    public void setTerminalETTFRRRAPDU(String terminalETTFRRRAPDU) {
        TerminalETTFRRRAPDU = terminalETTFRRRAPDU;
    }

    public String getRelayRAT() {
        return RelayRAT;
    }

    public void setRelayRAT(String relayRAT) {
        RelayRAT = relayRAT;
    }

    public String getRelayRTTMT() {
        return RelayRTTMT;
    }

    public void setRelayRTTMT(String relayRTTMT) {
        RelayRTTMT = relayRTTMT;
    }

    public String getMinimumRRGPfalg() {
        return MinimumRRGPfalg;
    }

    public void setMinimumRRGPfalg(String minimumRRGPfalg) {
        MinimumRRGPfalg = minimumRRGPfalg;
    }

    public String getMaximumRelayRGPfalg() {
        return MaximumRelayRGPfalg;
    }

    public void setMaximumRelayRGPfalg(String maximumRelayRGPfalg) {
        MaximumRelayRGPfalg = maximumRelayRGPfalg;
    }

    public String getTerminalETTFRRCAPDUfalg() {
        return TerminalETTFRRCAPDUfalg;
    }

    public void setTerminalETTFRRCAPDUfalg(String terminalETTFRRCAPDUfalg) {
        TerminalETTFRRCAPDUfalg = terminalETTFRRCAPDUfalg;
    }

    public String getTerminalETTFRRRAPDUfalg() {
        return TerminalETTFRRRAPDUfalg;
    }

    public void setTerminalETTFRRRAPDUfalg(String terminalETTFRRRAPDUfalg) {
        TerminalETTFRRRAPDUfalg = terminalETTFRRRAPDUfalg;
    }

    public String getRelayRATfalg() {
        return RelayRATfalg;
    }

    public void setRelayRATfalg(String relayRATfalg) {
        RelayRATfalg = relayRATfalg;
    }

    public String getRelayRTTMTfalg() {
        return RelayRTTMTfalg;
    }

    public void setRelayRTTMTfalg(String relayRTTMTfalg) {
        RelayRTTMTfalg = relayRTTMTfalg;
    }
    @SerializedName("ucMaxNumberTornFlg")
    private String ucMaxNumberTornFlg         ;//= new byte[mLengths[2]];     //DF811D 异常终端交易记录最大数标识
    @SerializedName("ucMaxNumberTorn")
    private String ucMaxNumberTorn        ;//= new byte[mLengths[3]];     //DF811D 异常终端交易记录最大数
    @SerializedName("ucBalanceBeforeGACFlg")
    private String ucBalanceBeforeGACFlg       ;//= new byte[mLengths[4]];     //DF8104 在GAC前读取余额标识
    @SerializedName("aucBalanceBeforeGAC")
    private String aucBalanceBeforeGAC      ;//= new byte[mLengths[5]];     //DF8104 在GAC前读取余额
    @SerializedName("ucBalanceAfterGACFlg")
    private String ucBalanceAfterGACFlg         ;//= new byte[mLengths[6]];     //DF8105 在GAC后读取余额标识
    @SerializedName("aucBalanceAfterGAC")
    private String aucBalanceAfterGAC      ;//= new byte[mLengths[7]];     //DF8105 在GAC后读取余额
    @SerializedName("ucMobileSupFlg")
    private String ucMobileSupFlg        ;//= new byte[mLengths[8]];     //9F7E 手机支持指示器标识
    @SerializedName("ucMobileSup")
    private String ucMobileSup      ;//= new byte[mLengths[9]];     //9F7E 手机支持指示器
    @SerializedName("ucHoldTimeValueFlg")
    private String ucHoldTimeValueFlg        ;//= new byte[mLengths[10]];    //DF8130 持有时间标识
    @SerializedName("ucHoldTimeValue")
    private String ucHoldTimeValue        ;//= new byte[mLengths[11]];    //DF8130 持有时间Hold Time Value
    @SerializedName("ucInterDevSerNumFlg")
    private String ucInterDevSerNumFlg        ;//= new byte[mLengths[12]];    //9F1E 制作商为IFD分配的唯一永久序列号标识
    @SerializedName("aucInterDevSerNum")
    private String aucInterDevSerNum        ;//= new byte[mLengths[13]];    //9F1E 制作商为IFD分配的唯一永久序列号Interface Device Serial Number
    @SerializedName("ucKernelIDFlg")
    private String ucKernelIDFlg        ;//= new byte[mLengths[14]];    //DF810C 内核类型标识
    @SerializedName("ucKernelID")
    private String ucKernelID        ;//= new byte[mLengths[15]];    //DF810C Kernel ID
    @SerializedName("ucMsgHoldTimeFlg")
    private String ucMsgHoldTimeFlg        ;//= new byte[mLengths[16]];    //DF812D 消息持有时间标识
    @SerializedName("aucMsgHoldTime")
    private String aucMsgHoldTime        ;//= new byte[mLengths[17]];    //DF812D 消息持有时间Message Hold Time
    @SerializedName("MinimumRRGP")
    private String MinimumRRGP ;//= new byte[mLengths[18]]; //DF8132
    @SerializedName("MaximumRelayRGP")
    private String MaximumRelayRGP ;//= new byte[mLengths[19]];//DF8133
    @SerializedName("TerminalETTFRRCAPDU")
    private String TerminalETTFRRCAPDU ;//= new byte[mLengths[20]];//DF8134
    @SerializedName("TerminalETTFRRRAPDU")
    private String TerminalETTFRRRAPDU ;//= new byte[mLengths[21]];//DF8135
    @SerializedName("RelayRAT")
    private String RelayRAT ;//= new byte[mLengths[22]];//DF8136
    @SerializedName("RelayRTTMT")
    private String RelayRTTMT ;//= new byte[mLengths[23]];//DF8137
    @SerializedName("MinimumRRGPfalg")
    private String MinimumRRGPfalg ;//= new byte[mLengths[24]]; //DF8132
    @SerializedName("MaximumRelayRGPfalg")
    private String MaximumRelayRGPfalg ;//= new byte[mLengths[25]];//DF8133
    @SerializedName("TerminalETTFRRCAPDUfalg")
    private String TerminalETTFRRCAPDUfalg ;//= new byte[mLengths[26]];//DF8134

    @SerializedName("TerminalETTFRRRAPDUfalg")
    private String TerminalETTFRRRAPDUfalg ;//= new byte[mLengths[27]];//DF8135
    @SerializedName("RelayRATfalg")
    private String RelayRATfalg ;//= new byte[mLengths[28]];//DF8136
    @SerializedName("RelayRTTMTfalg")
    private String RelayRTTMTfalg ;//= new byte[mLengths[29]];//DF8137

}
