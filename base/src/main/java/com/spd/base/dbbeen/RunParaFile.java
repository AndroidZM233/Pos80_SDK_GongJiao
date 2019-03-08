package com.spd.base.dbbeen;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 张明_ on 2019/2/26.
 * Email 741183142@qq.com
 */
@Entity
public class RunParaFile {
    private byte[] sWDate = new byte[3];
    private byte[] sWVer = new byte[1];
    private byte[] lFDate = new byte[3];
    private byte[] lFVer = new byte[1];
    private byte[] uiLFCrc = new byte[4];
    private byte[] fMP = new byte[1];                                         // ????????
    private byte[] fCJHTMode = new byte[1];                                 // ??????
    private byte[] wlStopNum = new byte[1];                                 // ????
    private byte[] ucCANAdr = new byte[1];                                  // ????,A=???,B=???

    private byte[] cityNr = new byte[2];                                 // 城市代码
    private byte[] areaNr = new byte[1];                                 // 区域号
    private byte[] vocNr = new byte[1];                                  // 行业号
    private byte[] corNr = new byte[1];                                  // 公司号
    private byte[] teamNr = new byte[2];                                 // 路队号
    private byte[] lineNr = new byte[2];                                 // 线路号
    private byte[] busN = new byte[3];                                  // 车辆号
    private byte[] devNr = new byte[4];                                  // 车载机设备号

    private byte[] keyV1 = new byte[2];                                    // 基本票价
    private byte[] keyV2 = new byte[2];                                     // 键2票价，=0时为单票制车
    private byte[] keyV3 = new byte[2];                                     // 键3票价
    private byte[] keyV4 = new byte[2];                                     // 键4票价
    private byte[] keyV5 = new byte[2];                                     // 键5票价
    private byte[] keyV6 = new byte[2];                                     // 键6票价
    private byte[] keyV7 = new byte[2];                                     // 键7票价
    private byte[] keyV8 = new byte[2];                                     // 键8票价

    private byte[] ucBusYuePower = new byte[3];
    private byte[] ucBusPurPower = new byte[3];
    private byte[] ucCpuYuePower = new byte[3];
    private byte[] ucCpuPurPower = new byte[3];
    private byte[] ucCityYuePower = new byte[2];
    private byte[] ucCityMainPurPower = new byte[2];

    private byte[] ucCitySubPurPower = new byte[2];
    private byte[] ucBusYueTimeLimit = new byte[1];
    private byte[] ucBusPurTimeLimit = new byte[1];
    private byte[] ucCpuYueTimeLimit = new byte[1];
    private byte[] ucCpuPurTimeLimit = new byte[1];
    private byte[] ucCityYueTimeLimit = new byte[1];
    private byte[] ucCityPurTimeLimit = new byte[1];
    private byte[] ucOldCardTimeLimit = new byte[1];
    private byte[] ucCardCheckDays = new byte[1];          // ?????????
    private byte[] ucAddValueLowP = new byte[2];          // ?????????

    private byte[] ucBusRadioP = new byte[24];      //
    private byte[] ucCpuRadioP = new byte[24];          //
    private byte[] ucCityMainRadioP = new byte[16];   //
    private byte[] ucCitySubRadioP = new byte[16];      //

    private byte[] ucTransferCtrl = new byte[1];            // 换乘控制 			0xb0
    private byte[] ucTransferCntLimit = new byte[1];       // 换乘次数限制			0xb1
    private byte[] ucTransferTimeLong = new byte[2];   // 换乘优惠时长			0xb2
    private byte[] ucTransferPower = new byte[3];        // 换乘权限				0xb4
    private byte[] ucTransfer1stRadioYue = new byte[1];    // 月票首次换乘折扣     0xb7
    private byte[] ucTransfer2ndRadioYue = new byte[1];    // 月票二次换乘折扣
    private byte[] ucTransferMulRadioYue = new byte[1];    // 月票多次换乘折扣
    private byte[] ucTransfer1stRadioPur = new byte[1];    // 钱包首次换乘折扣
    private byte[] ucTransfer2ndRadioPur = new byte[1];    // 钱包二次换乘折扣
    private byte[] ucTransferMulRadioPur = new byte[1];    // 钱包多次换乘折扣

    private byte[] ucJamTime = new byte[8];             //高峰时间
    private byte[] ucAirDate = new byte[8];             //空调日期

    private byte[] ucAPNLen = new byte[1];                    //APN接入点名称长度
    private byte[] cAPN = new byte[15];                    //APN接入点名称

    private byte[] ucUserLen = new byte[1];                //上网用户名称长度
    private byte[] cUser = new byte[15];                //上网用户名称

    private byte[] ucPasswordLen = new byte[1];            //上网口令长度
    private byte[] cPassword = new byte[15];            //上网口令

    private byte[] ucChannel = new byte[1];                //信道号						0xf0
    private byte[] ucKeyMode = new byte[1];                //加密类型	0:Open	  1:WEP
    private byte[] ucMask = new byte[4];                //子网掩码 四种0:no set  1:255.255.255.0  2:255.255.0.0   3:255.0.0.0
    private byte[] ucGateIP = new byte[4];                //默认网关
    private byte[] ucServerIP = new byte[4];            //IP
    private byte[] uiServerPort = new byte[2];                //Port端口号
    @Generated(hash = 1748536959)
    public RunParaFile(byte[] sWDate, byte[] sWVer, byte[] lFDate, byte[] lFVer, byte[] uiLFCrc, byte[] fMP,
            byte[] fCJHTMode, byte[] wlStopNum, byte[] ucCANAdr, byte[] cityNr, byte[] areaNr, byte[] vocNr, byte[] corNr,
            byte[] teamNr, byte[] lineNr, byte[] busN, byte[] devNr, byte[] keyV1, byte[] keyV2, byte[] keyV3, byte[] keyV4,
            byte[] keyV5, byte[] keyV6, byte[] keyV7, byte[] keyV8, byte[] ucBusYuePower, byte[] ucBusPurPower,
            byte[] ucCpuYuePower, byte[] ucCpuPurPower, byte[] ucCityYuePower, byte[] ucCityMainPurPower,
            byte[] ucCitySubPurPower, byte[] ucBusYueTimeLimit, byte[] ucBusPurTimeLimit, byte[] ucCpuYueTimeLimit,
            byte[] ucCpuPurTimeLimit, byte[] ucCityYueTimeLimit, byte[] ucCityPurTimeLimit, byte[] ucOldCardTimeLimit,
            byte[] ucCardCheckDays, byte[] ucAddValueLowP, byte[] ucBusRadioP, byte[] ucCpuRadioP, byte[] ucCityMainRadioP,
            byte[] ucCitySubRadioP, byte[] ucTransferCtrl, byte[] ucTransferCntLimit, byte[] ucTransferTimeLong,
            byte[] ucTransferPower, byte[] ucTransfer1stRadioYue, byte[] ucTransfer2ndRadioYue,
            byte[] ucTransferMulRadioYue, byte[] ucTransfer1stRadioPur, byte[] ucTransfer2ndRadioPur,
            byte[] ucTransferMulRadioPur, byte[] ucJamTime, byte[] ucAirDate, byte[] ucAPNLen, byte[] cAPN,
            byte[] ucUserLen, byte[] cUser, byte[] ucPasswordLen, byte[] cPassword, byte[] ucChannel, byte[] ucKeyMode,
            byte[] ucMask, byte[] ucGateIP, byte[] ucServerIP, byte[] uiServerPort) {
        this.sWDate = sWDate;
        this.sWVer = sWVer;
        this.lFDate = lFDate;
        this.lFVer = lFVer;
        this.uiLFCrc = uiLFCrc;
        this.fMP = fMP;
        this.fCJHTMode = fCJHTMode;
        this.wlStopNum = wlStopNum;
        this.ucCANAdr = ucCANAdr;
        this.cityNr = cityNr;
        this.areaNr = areaNr;
        this.vocNr = vocNr;
        this.corNr = corNr;
        this.teamNr = teamNr;
        this.lineNr = lineNr;
        this.busN = busN;
        this.devNr = devNr;
        this.keyV1 = keyV1;
        this.keyV2 = keyV2;
        this.keyV3 = keyV3;
        this.keyV4 = keyV4;
        this.keyV5 = keyV5;
        this.keyV6 = keyV6;
        this.keyV7 = keyV7;
        this.keyV8 = keyV8;
        this.ucBusYuePower = ucBusYuePower;
        this.ucBusPurPower = ucBusPurPower;
        this.ucCpuYuePower = ucCpuYuePower;
        this.ucCpuPurPower = ucCpuPurPower;
        this.ucCityYuePower = ucCityYuePower;
        this.ucCityMainPurPower = ucCityMainPurPower;
        this.ucCitySubPurPower = ucCitySubPurPower;
        this.ucBusYueTimeLimit = ucBusYueTimeLimit;
        this.ucBusPurTimeLimit = ucBusPurTimeLimit;
        this.ucCpuYueTimeLimit = ucCpuYueTimeLimit;
        this.ucCpuPurTimeLimit = ucCpuPurTimeLimit;
        this.ucCityYueTimeLimit = ucCityYueTimeLimit;
        this.ucCityPurTimeLimit = ucCityPurTimeLimit;
        this.ucOldCardTimeLimit = ucOldCardTimeLimit;
        this.ucCardCheckDays = ucCardCheckDays;
        this.ucAddValueLowP = ucAddValueLowP;
        this.ucBusRadioP = ucBusRadioP;
        this.ucCpuRadioP = ucCpuRadioP;
        this.ucCityMainRadioP = ucCityMainRadioP;
        this.ucCitySubRadioP = ucCitySubRadioP;
        this.ucTransferCtrl = ucTransferCtrl;
        this.ucTransferCntLimit = ucTransferCntLimit;
        this.ucTransferTimeLong = ucTransferTimeLong;
        this.ucTransferPower = ucTransferPower;
        this.ucTransfer1stRadioYue = ucTransfer1stRadioYue;
        this.ucTransfer2ndRadioYue = ucTransfer2ndRadioYue;
        this.ucTransferMulRadioYue = ucTransferMulRadioYue;
        this.ucTransfer1stRadioPur = ucTransfer1stRadioPur;
        this.ucTransfer2ndRadioPur = ucTransfer2ndRadioPur;
        this.ucTransferMulRadioPur = ucTransferMulRadioPur;
        this.ucJamTime = ucJamTime;
        this.ucAirDate = ucAirDate;
        this.ucAPNLen = ucAPNLen;
        this.cAPN = cAPN;
        this.ucUserLen = ucUserLen;
        this.cUser = cUser;
        this.ucPasswordLen = ucPasswordLen;
        this.cPassword = cPassword;
        this.ucChannel = ucChannel;
        this.ucKeyMode = ucKeyMode;
        this.ucMask = ucMask;
        this.ucGateIP = ucGateIP;
        this.ucServerIP = ucServerIP;
        this.uiServerPort = uiServerPort;
    }
    @Generated(hash = 1810956422)
    public RunParaFile() {
    }
    public byte[] getSWDate() {
        return this.sWDate;
    }
    public void setSWDate(byte[] sWDate) {
        this.sWDate = sWDate;
    }
    public byte[] getSWVer() {
        return this.sWVer;
    }
    public void setSWVer(byte[] sWVer) {
        this.sWVer = sWVer;
    }
    public byte[] getLFDate() {
        return this.lFDate;
    }
    public void setLFDate(byte[] lFDate) {
        this.lFDate = lFDate;
    }
    public byte[] getLFVer() {
        return this.lFVer;
    }
    public void setLFVer(byte[] lFVer) {
        this.lFVer = lFVer;
    }
    public byte[] getUiLFCrc() {
        return this.uiLFCrc;
    }
    public void setUiLFCrc(byte[] uiLFCrc) {
        this.uiLFCrc = uiLFCrc;
    }
    public byte[] getFMP() {
        return this.fMP;
    }
    public void setFMP(byte[] fMP) {
        this.fMP = fMP;
    }
    public byte[] getFCJHTMode() {
        return this.fCJHTMode;
    }
    public void setFCJHTMode(byte[] fCJHTMode) {
        this.fCJHTMode = fCJHTMode;
    }
    public byte[] getWlStopNum() {
        return this.wlStopNum;
    }
    public void setWlStopNum(byte[] wlStopNum) {
        this.wlStopNum = wlStopNum;
    }
    public byte[] getUcCANAdr() {
        return this.ucCANAdr;
    }
    public void setUcCANAdr(byte[] ucCANAdr) {
        this.ucCANAdr = ucCANAdr;
    }
    public byte[] getCityNr() {
        return this.cityNr;
    }
    public void setCityNr(byte[] cityNr) {
        this.cityNr = cityNr;
    }
    public byte[] getAreaNr() {
        return this.areaNr;
    }
    public void setAreaNr(byte[] areaNr) {
        this.areaNr = areaNr;
    }
    public byte[] getVocNr() {
        return this.vocNr;
    }
    public void setVocNr(byte[] vocNr) {
        this.vocNr = vocNr;
    }
    public byte[] getCorNr() {
        return this.corNr;
    }
    public void setCorNr(byte[] corNr) {
        this.corNr = corNr;
    }
    public byte[] getTeamNr() {
        return this.teamNr;
    }
    public void setTeamNr(byte[] teamNr) {
        this.teamNr = teamNr;
    }
    public byte[] getLineNr() {
        return this.lineNr;
    }
    public void setLineNr(byte[] lineNr) {
        this.lineNr = lineNr;
    }
    public byte[] getBusN() {
        return this.busN;
    }
    public void setBusN(byte[] busN) {
        this.busN = busN;
    }
    public byte[] getDevNr() {
        return this.devNr;
    }
    public void setDevNr(byte[] devNr) {
        this.devNr = devNr;
    }
    public byte[] getKeyV1() {
        return this.keyV1;
    }
    public void setKeyV1(byte[] keyV1) {
        this.keyV1 = keyV1;
    }
    public byte[] getKeyV2() {
        return this.keyV2;
    }
    public void setKeyV2(byte[] keyV2) {
        this.keyV2 = keyV2;
    }
    public byte[] getKeyV3() {
        return this.keyV3;
    }
    public void setKeyV3(byte[] keyV3) {
        this.keyV3 = keyV3;
    }
    public byte[] getKeyV4() {
        return this.keyV4;
    }
    public void setKeyV4(byte[] keyV4) {
        this.keyV4 = keyV4;
    }
    public byte[] getKeyV5() {
        return this.keyV5;
    }
    public void setKeyV5(byte[] keyV5) {
        this.keyV5 = keyV5;
    }
    public byte[] getKeyV6() {
        return this.keyV6;
    }
    public void setKeyV6(byte[] keyV6) {
        this.keyV6 = keyV6;
    }
    public byte[] getKeyV7() {
        return this.keyV7;
    }
    public void setKeyV7(byte[] keyV7) {
        this.keyV7 = keyV7;
    }
    public byte[] getKeyV8() {
        return this.keyV8;
    }
    public void setKeyV8(byte[] keyV8) {
        this.keyV8 = keyV8;
    }
    public byte[] getUcBusYuePower() {
        return this.ucBusYuePower;
    }
    public void setUcBusYuePower(byte[] ucBusYuePower) {
        this.ucBusYuePower = ucBusYuePower;
    }
    public byte[] getUcBusPurPower() {
        return this.ucBusPurPower;
    }
    public void setUcBusPurPower(byte[] ucBusPurPower) {
        this.ucBusPurPower = ucBusPurPower;
    }
    public byte[] getUcCpuYuePower() {
        return this.ucCpuYuePower;
    }
    public void setUcCpuYuePower(byte[] ucCpuYuePower) {
        this.ucCpuYuePower = ucCpuYuePower;
    }
    public byte[] getUcCpuPurPower() {
        return this.ucCpuPurPower;
    }
    public void setUcCpuPurPower(byte[] ucCpuPurPower) {
        this.ucCpuPurPower = ucCpuPurPower;
    }
    public byte[] getUcCityYuePower() {
        return this.ucCityYuePower;
    }
    public void setUcCityYuePower(byte[] ucCityYuePower) {
        this.ucCityYuePower = ucCityYuePower;
    }
    public byte[] getUcCityMainPurPower() {
        return this.ucCityMainPurPower;
    }
    public void setUcCityMainPurPower(byte[] ucCityMainPurPower) {
        this.ucCityMainPurPower = ucCityMainPurPower;
    }
    public byte[] getUcCitySubPurPower() {
        return this.ucCitySubPurPower;
    }
    public void setUcCitySubPurPower(byte[] ucCitySubPurPower) {
        this.ucCitySubPurPower = ucCitySubPurPower;
    }
    public byte[] getUcBusYueTimeLimit() {
        return this.ucBusYueTimeLimit;
    }
    public void setUcBusYueTimeLimit(byte[] ucBusYueTimeLimit) {
        this.ucBusYueTimeLimit = ucBusYueTimeLimit;
    }
    public byte[] getUcBusPurTimeLimit() {
        return this.ucBusPurTimeLimit;
    }
    public void setUcBusPurTimeLimit(byte[] ucBusPurTimeLimit) {
        this.ucBusPurTimeLimit = ucBusPurTimeLimit;
    }
    public byte[] getUcCpuYueTimeLimit() {
        return this.ucCpuYueTimeLimit;
    }
    public void setUcCpuYueTimeLimit(byte[] ucCpuYueTimeLimit) {
        this.ucCpuYueTimeLimit = ucCpuYueTimeLimit;
    }
    public byte[] getUcCpuPurTimeLimit() {
        return this.ucCpuPurTimeLimit;
    }
    public void setUcCpuPurTimeLimit(byte[] ucCpuPurTimeLimit) {
        this.ucCpuPurTimeLimit = ucCpuPurTimeLimit;
    }
    public byte[] getUcCityYueTimeLimit() {
        return this.ucCityYueTimeLimit;
    }
    public void setUcCityYueTimeLimit(byte[] ucCityYueTimeLimit) {
        this.ucCityYueTimeLimit = ucCityYueTimeLimit;
    }
    public byte[] getUcCityPurTimeLimit() {
        return this.ucCityPurTimeLimit;
    }
    public void setUcCityPurTimeLimit(byte[] ucCityPurTimeLimit) {
        this.ucCityPurTimeLimit = ucCityPurTimeLimit;
    }
    public byte[] getUcOldCardTimeLimit() {
        return this.ucOldCardTimeLimit;
    }
    public void setUcOldCardTimeLimit(byte[] ucOldCardTimeLimit) {
        this.ucOldCardTimeLimit = ucOldCardTimeLimit;
    }
    public byte[] getUcCardCheckDays() {
        return this.ucCardCheckDays;
    }
    public void setUcCardCheckDays(byte[] ucCardCheckDays) {
        this.ucCardCheckDays = ucCardCheckDays;
    }
    public byte[] getUcAddValueLowP() {
        return this.ucAddValueLowP;
    }
    public void setUcAddValueLowP(byte[] ucAddValueLowP) {
        this.ucAddValueLowP = ucAddValueLowP;
    }
    public byte[] getUcBusRadioP() {
        return this.ucBusRadioP;
    }
    public void setUcBusRadioP(byte[] ucBusRadioP) {
        this.ucBusRadioP = ucBusRadioP;
    }
    public byte[] getUcCpuRadioP() {
        return this.ucCpuRadioP;
    }
    public void setUcCpuRadioP(byte[] ucCpuRadioP) {
        this.ucCpuRadioP = ucCpuRadioP;
    }
    public byte[] getUcCityMainRadioP() {
        return this.ucCityMainRadioP;
    }
    public void setUcCityMainRadioP(byte[] ucCityMainRadioP) {
        this.ucCityMainRadioP = ucCityMainRadioP;
    }
    public byte[] getUcCitySubRadioP() {
        return this.ucCitySubRadioP;
    }
    public void setUcCitySubRadioP(byte[] ucCitySubRadioP) {
        this.ucCitySubRadioP = ucCitySubRadioP;
    }
    public byte[] getUcTransferCtrl() {
        return this.ucTransferCtrl;
    }
    public void setUcTransferCtrl(byte[] ucTransferCtrl) {
        this.ucTransferCtrl = ucTransferCtrl;
    }
    public byte[] getUcTransferCntLimit() {
        return this.ucTransferCntLimit;
    }
    public void setUcTransferCntLimit(byte[] ucTransferCntLimit) {
        this.ucTransferCntLimit = ucTransferCntLimit;
    }
    public byte[] getUcTransferTimeLong() {
        return this.ucTransferTimeLong;
    }
    public void setUcTransferTimeLong(byte[] ucTransferTimeLong) {
        this.ucTransferTimeLong = ucTransferTimeLong;
    }
    public byte[] getUcTransferPower() {
        return this.ucTransferPower;
    }
    public void setUcTransferPower(byte[] ucTransferPower) {
        this.ucTransferPower = ucTransferPower;
    }
    public byte[] getUcTransfer1stRadioYue() {
        return this.ucTransfer1stRadioYue;
    }
    public void setUcTransfer1stRadioYue(byte[] ucTransfer1stRadioYue) {
        this.ucTransfer1stRadioYue = ucTransfer1stRadioYue;
    }
    public byte[] getUcTransfer2ndRadioYue() {
        return this.ucTransfer2ndRadioYue;
    }
    public void setUcTransfer2ndRadioYue(byte[] ucTransfer2ndRadioYue) {
        this.ucTransfer2ndRadioYue = ucTransfer2ndRadioYue;
    }
    public byte[] getUcTransferMulRadioYue() {
        return this.ucTransferMulRadioYue;
    }
    public void setUcTransferMulRadioYue(byte[] ucTransferMulRadioYue) {
        this.ucTransferMulRadioYue = ucTransferMulRadioYue;
    }
    public byte[] getUcTransfer1stRadioPur() {
        return this.ucTransfer1stRadioPur;
    }
    public void setUcTransfer1stRadioPur(byte[] ucTransfer1stRadioPur) {
        this.ucTransfer1stRadioPur = ucTransfer1stRadioPur;
    }
    public byte[] getUcTransfer2ndRadioPur() {
        return this.ucTransfer2ndRadioPur;
    }
    public void setUcTransfer2ndRadioPur(byte[] ucTransfer2ndRadioPur) {
        this.ucTransfer2ndRadioPur = ucTransfer2ndRadioPur;
    }
    public byte[] getUcTransferMulRadioPur() {
        return this.ucTransferMulRadioPur;
    }
    public void setUcTransferMulRadioPur(byte[] ucTransferMulRadioPur) {
        this.ucTransferMulRadioPur = ucTransferMulRadioPur;
    }
    public byte[] getUcJamTime() {
        return this.ucJamTime;
    }
    public void setUcJamTime(byte[] ucJamTime) {
        this.ucJamTime = ucJamTime;
    }
    public byte[] getUcAirDate() {
        return this.ucAirDate;
    }
    public void setUcAirDate(byte[] ucAirDate) {
        this.ucAirDate = ucAirDate;
    }
    public byte[] getUcAPNLen() {
        return this.ucAPNLen;
    }
    public void setUcAPNLen(byte[] ucAPNLen) {
        this.ucAPNLen = ucAPNLen;
    }
    public byte[] getCAPN() {
        return this.cAPN;
    }
    public void setCAPN(byte[] cAPN) {
        this.cAPN = cAPN;
    }
    public byte[] getUcUserLen() {
        return this.ucUserLen;
    }
    public void setUcUserLen(byte[] ucUserLen) {
        this.ucUserLen = ucUserLen;
    }
    public byte[] getCUser() {
        return this.cUser;
    }
    public void setCUser(byte[] cUser) {
        this.cUser = cUser;
    }
    public byte[] getUcPasswordLen() {
        return this.ucPasswordLen;
    }
    public void setUcPasswordLen(byte[] ucPasswordLen) {
        this.ucPasswordLen = ucPasswordLen;
    }
    public byte[] getCPassword() {
        return this.cPassword;
    }
    public void setCPassword(byte[] cPassword) {
        this.cPassword = cPassword;
    }
    public byte[] getUcChannel() {
        return this.ucChannel;
    }
    public void setUcChannel(byte[] ucChannel) {
        this.ucChannel = ucChannel;
    }
    public byte[] getUcKeyMode() {
        return this.ucKeyMode;
    }
    public void setUcKeyMode(byte[] ucKeyMode) {
        this.ucKeyMode = ucKeyMode;
    }
    public byte[] getUcMask() {
        return this.ucMask;
    }
    public void setUcMask(byte[] ucMask) {
        this.ucMask = ucMask;
    }
    public byte[] getUcGateIP() {
        return this.ucGateIP;
    }
    public void setUcGateIP(byte[] ucGateIP) {
        this.ucGateIP = ucGateIP;
    }
    public byte[] getUcServerIP() {
        return this.ucServerIP;
    }
    public void setUcServerIP(byte[] ucServerIP) {
        this.ucServerIP = ucServerIP;
    }
    public byte[] getUiServerPort() {
        return this.uiServerPort;
    }
    public void setUiServerPort(byte[] uiServerPort) {
        this.uiServerPort = uiServerPort;
    }
}
