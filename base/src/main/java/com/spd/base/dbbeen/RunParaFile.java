package com.spd.bus.card.methods.bean;

import org.greenrobot.greendao.annotation.Entity;

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
}
