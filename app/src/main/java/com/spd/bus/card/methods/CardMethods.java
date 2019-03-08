package com.spd.bus.card.methods;

import android.os.RemoteException;
import android.util.Log;

import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.Datautils;
import com.spd.bus.card.methods.bean.TCardOpDU;
import com.spd.bus.card.methods.bean.TPCardDU;

import java.util.Arrays;

import wangpos.sdk4.libbasebinder.BankCard;

/**
 * Created by 张明_ on 2019/2/18.
 * Email 741183142@qq.com
 */
public class CardMethods {
    private static final String TAG = "SPEEDATA_BUS";
    /**
     * //获取PSAM卡终端机编号指令
     */
    public static final byte[] PSAN_GET_ID = {0x00, (byte) 0xB0, (byte) 0x96, 0x00, 0x06};
    /**
     * //交通部
     */
    public static final byte[] PSAM_SELECT_DIR = {0x00, (byte) 0xA4, 0x00, 0x00, 0x02, (byte) 0x80, 0x11};

    /**
     * //读取psam卡17文件
     */
    public static final byte[] PSAM_GET_17FILE = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x01};

    /**
     * //选择PPSE支付环境
     */
    public static final byte[] SELEC_PPSE = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0e, 0x32, 0x50, 0x41, 0x59, 0x2e, 0x53, 0x59, 0x53, 0x2e, 0x44, 0x44, 0x46, 0x30, 0x31};

    /**
     * //选择电子钱包应用
     */
    public static final byte[] SELECT_ICCARD_QIANBAO = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, (byte) 0xA0, 0x00, 0x00, 0x06, 0x32, 0x01, 0x01, 0x05};

    /**
     * //读CPU卡应用下公共应用基本信息文件指令 15文件
     */
    public static final byte[] READ_ICCARD_15FILE = {0x00, (byte) 0xB0, (byte) 0x95, 0x00, 0x00};

    /**
     * 读CPU17文件
     */

    public static final byte[] READ_ICCARD_17FILE = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x00};

    /**
     * //住建部
     */
    public static final byte[] PSAMZHUJIAN_SELECT_DIR = {0x00, (byte) 0xA4, 0x00, 0x00, 0x02, (byte) 0x10, 0x01};
    /**
     * 返回正确结果
     */
    public static final byte[] APDU_RESULT_SUCCESS = {(byte) 0x90, 0x00};
    /**
     * CPU卡黑名单结果
     */
    public static final byte[] APDU_RESULT_FAILE_6A81 = {(byte) 0x6A, (byte) 0x81};
    public static final byte[] APDU_RESULT_FAILE_6A82 = {(byte) 0x6A, (byte) 0x82};
    public static final byte[] APDU_RESULT_FAILE_6283 = {(byte) 0x62, (byte) 0x83};
    public static final byte[] APDU_RESULT_FAILE_6284 = {(byte) 0x62, (byte) 0x84};
    public static final byte[] APDU_RESULT_FAILE3_9303 = {(byte) 0x93, (byte) 0x03};
    public static final byte[] APDU_RESULT_FAILE3_6300 = {(byte) 0x63, (byte) 0x00};

    //8054错误
    public static final byte[] APDU_8054_FAILE = {(byte) 0x93, (byte) 0x02};


    //本地城市代码
    public static final byte[] ucCityCodeII = {(byte) 0x11, (byte) 0x21};
    //本地发型机构代码
    public static final byte[] ucIssuerCodeII = {(byte) 0x01, (byte) 0x13, (byte) 0x11,
            (byte) 0x21, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,};
    //受理方机构标识
    public static final byte[] ucShouLiCodeII = {(byte) 0x11, (byte) 0x13, (byte) 0x11,
            (byte) 0x21, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,};
    //住建部
    public static final byte ZJBCARD = (byte) 0x01;
    //交通部
    public static final byte JTBCARD = (byte) 0x02;

    public static final byte[] ucCityCode = {(byte) 0x30, (byte) 0x00};

    public static final int MAXVALUE = 100000;

    /**
     * 封装接口自定义
     *
     * @param cardType 卡片类型
     * @param sendApdu 发送指令
     * @return 结果
     */
    public static byte[] sendApdus(BankCard mBankCard, int cardType, byte[] sendApdu) {
        byte[] reBytes = null;
        //微智接口返回数据
        byte[] respdata = new byte[512];
        //微智接口返回数据长度
        int[] resplen = new int[1];
        try {
            int retvalue = mBankCard.sendAPDU(cardType, sendApdu, sendApdu.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                Log.e(TAG, "微智接口返回错误码" + retvalue);
                return reBytes;
            }
            if (!Arrays.equals(APDU_RESULT_SUCCESS, Datautils.cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                return Datautils.cutBytes(respdata, resplen[0] - 2, 2);
            }
            reBytes = Datautils.cutBytes(respdata, 0, resplen[0] - 2);
        } catch (RemoteException e) {
            e.printStackTrace();
            try {
                mBankCard.breakOffCommand();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
        return reBytes;
    }

    /**
     * PSAM卡校验MAC2指令[终端向PSAM卡发送]：8072 000004 XXXXXXXX（MAC2 用户卡8054指令返回）
     *
     * @param data 8070返回 mac2
     * @return 返回 8072校验mac2
     */
    public static byte[] checkPsamMac2(byte[] data) {
        String psam_mac2 = "8072000004" + Datautils.byteArrayToString(data);
        return Datautils.HexString2Bytes(psam_mac2);
    }


    /**
     * PSAM 卡产生MAC1指令 8070
     *
     * @param balance
     * @return 返回结果为：XXXXXXXX（终端脱机交易序号）XXXXXXXX（MAC1）
     */
    public static byte[] initSamForPurchase(TPCardDU cardDU, TCardOpDU tCardOpDU) {
        byte[] cmd = new byte[42];
        cmd[0] = (byte) 0x80;
        cmd[1] = 0x70;
        cmd[2] = 0x00;
        cmd[3] = 0x00;
        cmd[4] = 0x24;
        System.arraycopy(tCardOpDU.rondomCpu, 0, cmd, 5, 4);
        System.arraycopy(tCardOpDU.uiOffLineCount, 0, cmd, 9, 2);
        System.arraycopy(tCardOpDU.lPurSubByte, 0, cmd, 11, 4);
        //是否为复合消费
        if (tCardOpDU.ucCAPP == (byte) 0x01) {
            cmd[15] = (byte) 0x09;
        } else {
            cmd[15] = (byte) 0x06;
        }
        //系统时间
        System.arraycopy(tCardOpDU.ucDateTime, 0, cmd, 16, 7);
        cmd[23] = tCardOpDU.ucKeyVer;
        cmd[24] = tCardOpDU.ucKeyAlg;
        System.arraycopy(Datautils.cutBytes(tCardOpDU.ucAppSnr, 2, 8),
                0, cmd, 25, 8);
        //交通部CPU卡
        if (cardDU.cardClass == 0x07) {
            System.arraycopy(tCardOpDU.ucFile15Top8, 0, cmd, 33, 8);
        } else {
            byte[] concatAll = Datautils.concatAll(tCardOpDU.ucCityCode, new byte[]{(byte) 0xFF, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
            System.arraycopy(concatAll, 0, cmd, 33, 8);
        }


        cmd[41] = (byte) 0x08;

        return cmd;
    }

    /**
     * 用户卡扣款指令[终端向用户卡发送] 8054
     *
     * @return 8054指令
     */
    public static byte[] getIcPurchase(TCardOpDU tCardOpDU) {
        byte[] cmd = new byte[21];
        cmd[0] = (byte) 0x80;
        cmd[1] = (byte) 0x54;
        cmd[2] = (byte) 0x01;
        cmd[3] = (byte) 0x00;
        cmd[4] = (byte) 0x0F;
        System.arraycopy(tCardOpDU.ucPsamATC, 0, cmd, 5, 4);
        //PSAM_ATC 4 //系统时间
        System.arraycopy(tCardOpDU.ucDateTime, 0, cmd, 9, 7);
        System.arraycopy(tCardOpDU.ucMAC1, 0, cmd, 16, 4);
        cmd[20] = 0x08;
        return cmd;
    }

    /***************************************************************************
     *函数名称：fIsUseYue，判断各种卡是否有消费优惠区的权限                     *
     *功能：    1。检查发卡城市是否本城市，非本城市，禁止使用优惠区             *
     *          2。判断当前卡是否允许优惠区消费                                 *
     *返回值：  0   = 当前卡不允许消费优惠区                                    *
     *          1   = 当前卡允许消费优惠区                                      *
     *          255 = 当前卡月票区未启用或损坏                                  *
     ***************************************************************************/
    public static int fIsUseYue(TPCardDU carddu, TCardOpDU tCardOpDu, RunParaFile runParaFile) {
        // 过年检期，无月票权限
        long systemTime = Long.parseLong(Datautils.byteArrayToString(Datautils
                .cutBytes(Datautils.getDateTime(), 0, 4)));
        long ucCheckDate = Long.parseLong(Datautils.byteArrayToString(tCardOpDu.ucCheckDate));
        int valid = 0, i;
        //0x03,CPU卡
        if (carddu.cardClass == (byte) 0x03) {
            // TODO: 2019/2/28 测试先屏蔽掉
//            if (systemTime > ucCheckDate) {
//                return 0;
//            }
            tCardOpDu.yueSec = (byte) 0x07;// 7扇区为月票区
            byte[] ucCpuYuePower = runParaFile.getUcCpuYuePower();
            for (valid = 0, i = 0; i < 3; i++) {
                valid <<= 8;
                valid += ucCpuYuePower[i] & 0xFF;
            }
            byte ucType = tCardOpDu.ucMainCardType;
            valid &= (0x800000L >> ucType);
        } else {
            if (((carddu.cardClass == (byte) 0x51) || (carddu.cardClass == (byte) 0x71))
                    && (carddu.cardType != (byte) 0x00) && (carddu.cardType != 6))// =0x51/0x71，城市卡
            {
                return 0;
            }
            if (carddu.cardClass == (byte) 0x01)                                      // =0x01，公交卡
            {
                // 过年检期，无月票权限
                if (systemTime > ucCheckDate) {
                    return 0;
                }
                tCardOpDu.yueSec = (byte) 0x07; // 7扇区为月票区
                byte[] ucBusYuePower = runParaFile.getUcBusYuePower();
                for (valid = 0, i = 0; i < 3; i++) {
                    valid <<= 8;
                    valid += ucBusYuePower[i] & 0xFF;
                }
                byte ucType = carddu.cardType;
                valid &= (0x800000L >> ucType);
            } else if ((carddu.cardClass == (byte) 0x51) || (carddu.cardClass == (byte) 0x71))           // 城市卡,不判年检期(城市卡无年检日期)
            {
                tCardOpDu.yueSec = (byte) 0x07; // 7扇区为月票区
                byte[] ucCityYuePower = runParaFile.getUcCityYuePower();                                                  // 7扇区为月票区
                for (valid = 0, i = 0; i < 2; i++) {
                    valid <<= 8;
                    valid += ucCityYuePower[i] & 0xFF;
                }
                valid <<= 8;
                byte ucType = carddu.subType;
                valid &= (0x800000L >> ucType);
            }                                                                    // 无权限，返回0
            if ((carddu.cardClass == (byte) 0x51) || (carddu.cardClass == (byte) 0x71)) {
                if ((carddu.cardType == (byte) 0x01) || (carddu.cardType == (byte) 0x02)
                        || (carddu.cardType == (byte) 0x11)) {
                    return 1;
                }
            }
        }
        if (valid == 0) {
            return valid;
        } else {
            return 1;
        }

    }

    public static int cpuCardGetYueBasePos(byte[] pAppDateTime, TPCardDU carddu, byte[] ucDat) {
        carddu.uiIncYueCount = ucDat[19];
        //月票启用标志
        if (ucDat[1] != (byte) 0x01) {
            return 0;
        }
        byte[] yueStartDate = Datautils.cutBytes(ucDat, 5, 3);
        byte[] yueEndDate = Datautils.cutBytes(ucDat, 9, 3);

        carddu.yueBase = Datautils.cutBytes(ucDat, 12, 2);

        byte[] dateTime = Datautils.cutBytes(pAppDateTime, 1, 3);
        int dateTimeInt = Integer.parseInt(Datautils.byteArrayToString(dateTime));
        int yueStartDateInt = Integer.parseInt(Datautils.byteArrayToString(yueStartDate));
        int yueEndDateInt = Integer.parseInt(Datautils.byteArrayToString(yueEndDate));

        if (dateTimeInt >= yueStartDateInt && dateTimeInt <= yueEndDateInt) {
            carddu.yuePosition = (byte) 0x00;
            carddu.yueUsingDate = dateTime;
            return 1;
        } else {
            return 0;
        }
    }


    /***************************************************************************
     *函数名称：fIsUsePur，判断各种卡是否有消费钱包区的权限                     *
     *功能：    1。检查发卡城市是否本城市，非本城市，禁止使用优惠区             *
     *          2。判断当前卡是否允许优惠区消费                                 *
     *返回值：  0   = 当前卡不允许消费钱包区                                    *
     *          1   = 当前卡允许消费钱包区                                      *
     ***************************************************************************/
    public static int fIsUsePur(TPCardDU carddu, TCardOpDU tCardOpDu, RunParaFile runParaFile) {
        int valid = 0, i;
        byte ucType;

        if (carddu.cardClass == 0x03 || carddu.cardClass == 0x07)    //0x03,CPU卡
        {
            byte[] ucCpuPurPower = runParaFile.getUcCpuPurPower();
            for (valid = 0, i = 0; i < 3; i++) {
                valid <<= 8;
                valid += ucCpuPurPower[i] & 0xFF;
            }
            ucType = tCardOpDu.ucMainCardType;
            valid &= (0x800000L >> ucType);

            if (carddu.cardClass == 0x07 && (tCardOpDu.ucMainCardType == 0x01 || tCardOpDu.ucMainCardType == 0x11)) {
                byte[] ucCityMainPurPower = runParaFile.getUcCityMainPurPower();
                for (valid = 0, i = 0; i < 2; i++) {
                    valid <<= 8;
                    valid += ucCityMainPurPower[i] & 0xFF;
                }
                valid <<= 8;
                ucType = tCardOpDu.ucMainCardType;
                valid &= (0x800000L >> ucType);
            }


        } else if (((carddu.cardClass == 0x51) || (carddu.cardClass == 0x71))
                && (carddu.cardType != 0) && (carddu.cardType != 6))   // =0x51/0x71，城市卡
        {
            byte[] ucCityMainPurPower = runParaFile.getUcCityMainPurPower();
            for (valid = 0, i = 0; i < 2; i++) {
                valid <<= 8;
                valid += ucCityMainPurPower[i] & 0xFF;
            }
            valid <<= 8;
            ucType = carddu.cardType;
            valid &= (0x800000L >> ucType);
        } else {
            if (carddu.cardClass == 0x01)                                        // =0x01，公交卡
            {
                // 钱包未启用，返回0
                if (carddu.fStartUsePur == (byte) 0x00) {
                    return 0;
                }
                byte[] ucBusPurPower = runParaFile.getUcBusPurPower();
                for (valid = 0, i = 0; i < 3; i++) {
                    valid <<= 8;
                    valid += ucBusPurPower[i] & 0xFF;
                }
                ucType = carddu.cardType;
                valid &= (0x800000L >> ucType);
            } else if ((carddu.cardClass == 0x51) || (carddu.cardClass == 0x71))     // =0x51/0x71，城市卡
            {
                byte[] ucCitySubPurPower = runParaFile.getUcCitySubPurPower();
                for (valid = 0, i = 0; i < 2; i++) {
                    valid <<= 8;
                    valid += ucCitySubPurPower[i] & 0xFF;
                }
                valid <<= 8;
                ucType = carddu.subType;
                valid &= (0x800000L >> ucType);
            }
        }
        if (valid == 0) {
            return valid;
        }
        return 1;
    }


    // 判断各种卡是否有消费优惠区的权限
    public static int fIsUseHC(TCardOpDU tCardOpDU, RunParaFile runParaFile) {
        int valid;
        int i;
        byte ucType;

        byte[] ucTransferCtrl = runParaFile.getUcTransferCtrl();
        byte[] ucTransferPower = runParaFile.getUcTransferPower();
        if (Arrays.equals(ucTransferCtrl, new byte[]{(byte) 0x01})) {
            return 0;
        }
        for (valid = 0, i = 0; i < 3; i++) {
            valid <<= 8;
            valid += ucTransferPower[i] & 0xFF;
        }

        ucType = tCardOpDU.ucMainCardType;
        valid &= (0x800000L >> ucType);

        if (valid == 0) {
            return valid;
        }
        return 1;
    }

    public static long byteToLong(byte[] bytes) {
        int i;
        int Long;

        for (Long = 0, i = 0; i < 4; i++) {
            Long <<= 8;
            Long += bytes[i] & 0xFF;
        }
        return Long;
    }

//    public static byte[] uTCToBCDTime(long ulUTC) {
//        long ulHMS;
//        long uiYMD, uiFYMD;
//        int year;
//        int month, day, hour, minute, second, leap;
//        byte[] mBCDTime = new byte[7];
//
//        ulUTC += 28800; //北京时间调整,加8*60*60
//        uiYMD = ulUTC / 86400L;                            // 天数
//        ulHMS = ulUTC % 86400L;                            // 剩余总秒数/天
//
//        second = (int) (ulHMS % 60L);                 // 秒数
//        ulHMS = ulHMS / 60L;
//        minute = (int) (ulHMS % 60L);                 // 分数
//        ulHMS = ulHMS / 60L;
//        hour = (int) (ulHMS % 24L);                 // 小时数
//
//        year = (int) (uiYMD / 1461L) * 4;                           // 4年总天数=1461天
//        uiFYMD = uiYMD % 1461L;                               // 4year
//
//        leap = 0;
//        if (uiFYMD > 1095) {
//            uiFYMD -= 1096;
//            year += 3;
//            leap = 0;
//        } else if (uiFYMD > 729) {
//            uiFYMD -= 730;
//            year += 2;
//            leap = 1;
//        } else if (uiFYMD > 364) {
//            uiFYMD -= 365;
//            year += 1;
//            leap = 0;
//        }
//        year += 1970;
//
//        if (uiFYMD > 333 + leap)                                // 12月
//        {
//            day = (int) uiFYMD - 334 - leap + 1;
//            month = 12;
//        } else if (uiFYMD > 303 + leap)                           // 11月
//        {
//            day = (int) uiFYMD - 304 - leap + 1;
//            month = 11;
//        } else if ((int) uiFYMD > 272 + leap)                           // 10月
//        {
//            day = (int) uiFYMD - 273 - leap + 1;
//            month = 10;
//        } else if ((int) uiFYMD > 242 + leap)                           // 9月
//        {
//            day = (int) uiFYMD - 243 - leap + 1;
//            month = 9;
//        } else if (uiFYMD > 211 + leap)                           // 8月
//        {
//            day = (int) uiFYMD - 212 - leap + 1;
//            month = 8;
//        } else if ((int) uiFYMD > 180 + leap)                           // 7月
//        {
//            day = (int) uiFYMD - 181 - leap + 1;
//            month = 7;
//        } else if (uiFYMD > 150 + leap)                           // 6月
//        {
//            day = (int) uiFYMD - 151 - leap + 1;
//            month = 6;
//        } else if (uiFYMD > 119 + leap)                           // 5月
//        {
//            day = (int) uiFYMD - 120 - leap + 1;
//            month = 5;
//        } else if (uiFYMD > 89 + leap)                            // 4月
//        {
//            day = (int) uiFYMD - 90 - leap + 1;
//            month = 4;
//        } else if (uiFYMD > 58 + leap)                            // 3月
//        {
//            day = (int) uiFYMD - 59 - leap + 1;
//            month = 3;
//        } else if (uiFYMD > 30)                                 // 2月
//        {
//            day = (int) uiFYMD - 31 + 1;
//            month = 2;
//        } else                                               // 1月
//        {
//            day = (int) uiFYMD + 1;
//            month = 1;
//        }
//        mBCDTime[0] = HEXtoBCD(year / 100);
//        mBCDTime[1] = HEXtoBCD(year % 100);
//        mBCDTime[2] = HEXtoBCD(month);
//        mBCDTime[3] = HEXtoBCD(day);
//        mBCDTime[4] = HEXtoBCD(hour);
//        mBCDTime[5] = HEXtoBCD(minute);
//        mBCDTime[6] = HEXtoBCD(second);
//        return mBCDTime;
//    }


    //折扣率 算票价
    public static int getRadioPurSub(TPCardDU carddu, TCardOpDU tCardOpDu, RunParaFile runParaFile) {
        int radio = 100;
        int calcRet;
        int ucBCDType;
        int price;

        price = Datautils.byteArrayToInt(runParaFile.getKeyV1());

        if (tCardOpDu.ucOtherCity != 0 && carddu.cardClass == 0x03)//非本地卡
        {
            return price;
        }
        //天津无带人说法
        if (carddu.cardClass == 0x07)//交通部CPU卡
        {
            //取城市卡普通卡折扣率
            radio = 100;
        } else if (carddu.cardClass == 0x03)//CPU卡
        {
            byte[] ucCpuRadioP = runParaFile.getUcCpuRadioP();
            int toInt = Datautils.byteArrayToInt(new byte[]{tCardOpDu.ucMainCardType});
            if (carddu.fUseHC == 0)//CPU卡
            {
                radio = Datautils.byteArrayToInt(new byte[]{ucCpuRadioP[toInt]});
            } else {
                if (carddu.fHC == 0)//首乘
                {
                    radio = Datautils.byteArrayToInt(ucCpuRadioP);
                } else if (carddu.fHC == 1)//首次换乘
                {
                    radio = Datautils.byteArrayToInt(runParaFile.getUcTransfer1stRadioPur());
                } else if (carddu.fHC == 2)//二次换乘
                {
                    radio = Datautils.byteArrayToInt(runParaFile.getUcTransfer2ndRadioPur());
                } else if (carddu.fHC <= Datautils.byteArrayToInt(runParaFile.getUcTransferCntLimit()))//多次换乘，换乘限制次数以下(含换乘限制次数)
                {
                    radio = Datautils.byteArrayToInt(runParaFile.getUcTransferMulRadioPur());
                } else {
                    //换乘限制次数以上
                    radio = Datautils.byteArrayToInt(new byte[]{ucCpuRadioP[toInt]});
                }
            }
        } else if (carddu.cardClass == 0x01)                                      // =0x01，公交卡
        {
            carddu.fUseHC = 0;
            int cardTypeToInt = Datautils.byteArrayToInt(new byte[]{carddu.cardType});
            byte[] ucBusRadioP = runParaFile.getUcBusRadioP();
            radio = Datautils.byteArrayToInt(new byte[]{ucBusRadioP[cardTypeToInt]});
        } else if ((carddu.cardClass == 0x51) || (carddu.cardClass == 0x71)) {
            carddu.fUseHC = 0;
            if ((carddu.cardType != 0) && (carddu.cardType != 6))        // 主卡类型==非0或非6, 特种卡
            {
                ucBCDType = (carddu.cardType >> 4) * 10 + (carddu.cardType & 0x0f);
                if (ucBCDType <= 0x0f) // 主卡类型在16种卡之内，取主卡折扣率
                {
                    radio = Datautils.byteArrayToInt(new byte[]{runParaFile.getUcCityMainRadioP()[ucBCDType]});
                } else { // 大于16种，取普通卡子卡类型折扣率
                    radio = Datautils.byteArrayToInt(new byte[]{runParaFile.getUcCitySubRadioP()[0]});
                }
            } else {// 主卡类型==0（公交兼容卡）或6（测试卡）
                // 取子卡类型折扣率
                int subTypeToInt = Datautils.byteArrayToInt(new byte[]{carddu.subType});
                radio = Datautils.byteArrayToInt(new byte[]{runParaFile.getUcCitySubRadioP()[subTypeToInt]});
            }
        }
        calcRet = (price) * radio;
        if (calcRet % 100 == 0) {
            calcRet = calcRet / 100;
        } else {
            calcRet = calcRet / 100 + 1;
        }

        return calcRet;
    }
}
