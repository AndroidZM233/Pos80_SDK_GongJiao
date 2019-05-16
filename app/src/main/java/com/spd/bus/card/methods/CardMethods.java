package com.spd.bus.card.methods;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.spd.base.been.tianjin.CardRecord;
import com.spd.base.been.tianjin.CardRecordDao;
import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.Datautils;
import com.spd.base.been.tianjin.TCardOpDU;
import com.spd.bus.Info;
import com.spd.bus.MyApplication;
import com.spd.bus.util.DataUploadToTianJinUtils;
import com.spd.base.utils.DateUtils;
import com.spd.base.utils.LogUtils;
import com.spd.bus.spdata.been.PsamBeen;
import com.spd.bus.spdata.been.TCommInfo;

import java.util.Arrays;
import java.util.List;

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
    public static final byte[] PSAM_SELECT_DIR = {0x00, (byte) 0xA4, 0x00, 0x00, 0x02
            , (byte) 0x80, 0x11};

    /**
     * //读取psam卡17文件
     */
    public static final byte[] PSAM_GET_17FILE = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x01};

    /**
     * //选择PPSE支付环境
     */
    public static final byte[] SELEC_PPSE = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0e, 0x32
            , 0x50, 0x41, 0x59, 0x2e, 0x53, 0x59, 0x53, 0x2e, 0x44, 0x44, 0x46, 0x30, 0x31};

    /**
     * //选择电子钱包应用
     */
    public static final byte[] SELECT_ICCARD_QIANBAO = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08
            , (byte) 0xA0, 0x00, 0x00, 0x06, 0x32, 0x01, 0x01, 0x05};

    /**
     * //读CPU卡应用下公共应用基本信息文件指令 15文件
     */
    public static final byte[] READ_ICCARD_15FILE = {0x00, (byte) 0xB0, (byte) 0x95
            , 0x00, 0x00};

    /**
     * 读CPU17文件
     */

    public static final byte[] READ_ICCARD_17FILE = {0x00, (byte) 0xB0, (byte) 0x97
            , 0x00, 0x00};

    /**
     * //住建部
     */
    public static final byte[] PSAMZHUJIAN_SELECT_DIR = {0x00, (byte) 0xA4, 0x00, 0x00
            , 0x02, (byte) 0x10, 0x01};
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
    public static final byte ZJBCARD = (byte) 0x03;
    //交通部
    public static final byte JTBCARD = (byte) 0x07;

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
                LogUtils.d("微智接口返回错误码" + retvalue);
                return reBytes;
            }
            byte[] resultBytes = Datautils.cutBytes(respdata, resplen[0] - 2, 2);
            if (resultBytes[0] == (byte) 0x6C) {
                sendApdu[4] = resultBytes[1];
                retvalue = mBankCard.sendAPDU(cardType, sendApdu, sendApdu.length, respdata, resplen);
                if (retvalue != 0) {
                    mBankCard.breakOffCommand();
                    LogUtils.d("微智接口返回错误码" + retvalue);
                    return reBytes;
                }
            }
            if (!Arrays.equals(APDU_RESULT_SUCCESS, Datautils
                    .cutBytes(respdata, resplen[0] - 2, 2))) {
//                mBankCard.breakOffCommand();
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
    public static byte[] initSamForPurchase(TCardOpDU tCardOpDu) {
        byte[] cmd = new byte[42];
        cmd[0] = (byte) 0x80;
        cmd[1] = 0x70;
        cmd[2] = 0x00;
        cmd[3] = 0x00;
        cmd[4] = 0x24;
        System.arraycopy(tCardOpDu.rondomCpu, 0, cmd, 5, 4);
        System.arraycopy(tCardOpDu.uiOffLineCount, 0, cmd, 9, 2);
        byte[] ulTradeValue = Datautils.intToByteArray1(tCardOpDu.ulTradeValue);
        System.arraycopy(ulTradeValue, 0, cmd, 11, 4);
        //是否为复合消费
        if (tCardOpDu.ucCAPP == (byte) 0x01) {
            cmd[15] = (byte) 0x09;
        } else {
            cmd[15] = (byte) 0x06;
        }
        //系统时间
        System.arraycopy(tCardOpDu.ucDateTime, 0, cmd, 16, 7);
        cmd[23] = tCardOpDu.ucKeyVer;
        cmd[24] = tCardOpDu.ucKeyAlg;
        System.arraycopy(Datautils.cutBytes(tCardOpDu.ucAppSnr, 2, 8),
                0, cmd, 25, 8);
        //交通部CPU卡
        if (tCardOpDu.cardClass == 0x07) {
            System.arraycopy(tCardOpDu.ucFile15Top8, 0, cmd, 33, 8);
        } else {
            byte[] concatAll = Datautils.concatAll(tCardOpDu.ucCityCode, new byte[]{(byte) 0xFF, (byte) 0x00,
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
        System.arraycopy(tCardOpDU.ulPOSTradeCountByte, 0, cmd, 5, 4);
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
    public static int fIsUseYue(TCardOpDU tCardOpDu, RunParaFile runParaFile) {
        // 过年检期，无月票权限
        long systemTime = Long.parseLong(Datautils.byteArrayToString(Datautils
                .cutBytes(Datautils.getDateTime(), 0, 4)));
        long ucCheckDate = Long.parseLong(Datautils.byteArrayToString(tCardOpDu.ucCheckDate));
        int valid = 0, i;
        //0x03,CPU卡
        if (tCardOpDu.cardClass == (byte) 0x03) {
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
            if (((tCardOpDu.cardClass == (byte) 0x51) || (tCardOpDu.cardClass == (byte) 0x71))
                    && (tCardOpDu.ucMainCardType != (byte) 0x00) && (tCardOpDu.ucMainCardType != 6))// =0x51/0x71，城市卡
            {
                return 0;
            }
            if (tCardOpDu.cardClass == (byte) 0x01)                                      // =0x01，公交卡
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
                byte ucType = tCardOpDu.ucMainCardType;
                valid &= (0x800000L >> ucType);
            } else if ((tCardOpDu.cardClass == (byte) 0x51) || (tCardOpDu.cardClass == (byte) 0x71))           // 城市卡,不判年检期(城市卡无年检日期)
            {
                tCardOpDu.yueSec = (byte) 0x07; // 7扇区为月票区
                byte[] ucCityYuePower = runParaFile.getUcCityYuePower();                                                  // 7扇区为月票区
                for (valid = 0, i = 0; i < 2; i++) {
                    valid <<= 8;
                    valid += ucCityYuePower[i] & 0xFF;
                }
                valid <<= 8;
                byte ucType = tCardOpDu.ucMainCardType;
                valid &= (0x800000L >> ucType);

            }                                                                    // 无权限，返回0
            if ((tCardOpDu.cardClass == (byte) 0x51) || (tCardOpDu.cardClass == (byte) 0x71)) {
                if ((tCardOpDu.ucMainCardType == (byte) 0x01) || (tCardOpDu.ucMainCardType == (byte) 0x02)
                        || (tCardOpDu.ucMainCardType == (byte) 0x11)) {
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

    public static int cpuCardGetYueBasePos(byte[] pAppDateTime, TCardOpDU carddu, byte[] ucDat) {
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
    public static int fIsUsePur(TCardOpDU tCardOpDu, RunParaFile runParaFile) {
        int valid = 0, i;
        byte ucType;
        int to16Int;

        if (tCardOpDu.cardClass == 0x03 || tCardOpDu.cardClass == 0x07)    //0x03,CPU卡
        {
            byte[] ucCpuPurPower = runParaFile.getUcCpuPurPower();
            for (valid = 0, i = 0; i < 3; i++) {
                valid <<= 8;
                valid += ucCpuPurPower[i] & 0xFF;
            }
            ucType = tCardOpDu.ucMainCardType;
            valid &= (0x800000L >> ucType);

            if (tCardOpDu.cardClass == 0x07 && (tCardOpDu.ucMainCardType == 0x01 || tCardOpDu.ucMainCardType == 0x11)) {
                byte[] ucCityMainPurPower = runParaFile.getUcCityMainPurPower();
                for (valid = 0, i = 0; i < 2; i++) {
                    valid <<= 8;
                    valid += ucCityMainPurPower[i] & 0xFF;
                }
                valid <<= 8;
                to16Int = Datautils.btyeTo16Int(tCardOpDu.subType);
                valid &= (0x800000L >> to16Int);
            }


        } else if (((tCardOpDu.cardClass == 0x51) || (tCardOpDu.cardClass == 0x71))
                && (tCardOpDu.ucMainCardType != 0) && (tCardOpDu.ucMainCardType != 6))   // =0x51/0x71，城市卡
        {
            byte[] ucCityMainPurPower = runParaFile.getUcCityMainPurPower();
            for (valid = 0, i = 0; i < 2; i++) {
                valid <<= 8;
                valid += ucCityMainPurPower[i] & 0xFF;
            }
            valid <<= 8;
            to16Int = Datautils.btyeTo16Int(tCardOpDu.subType);
            valid &= (0x800000L >> to16Int);
        } else {
            if (tCardOpDu.cardClass == 0x01)                                        // =0x01，公交卡
            {
                // 钱包未启用，返回0
                if (tCardOpDu.fStartUsePur == (byte) 0x00) {
                    return 0;
                }
                byte[] ucBusPurPower = runParaFile.getUcBusPurPower();
                for (valid = 0, i = 0; i < 3; i++) {
                    valid <<= 8;
                    valid += ucBusPurPower[i] & 0xFF;
                }
                ucType = tCardOpDu.ucMainCardType;
                valid &= (0x800000L >> ucType);
            } else if ((tCardOpDu.cardClass == 0x51) || (tCardOpDu.cardClass == 0x71))     // =0x51/0x71，城市卡
            {
                byte[] ucCitySubPurPower = runParaFile.getUcCitySubPurPower();
                for (valid = 0, i = 0; i < 2; i++) {
                    valid <<= 8;
                    valid += ucCitySubPurPower[i] & 0xFF;
                }
                valid <<= 8;
                ucType = tCardOpDu.subType;
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


    //折扣率 算票价
    public static int getRadioPurSub(TCardOpDU tCardOpDu, RunParaFile runParaFile) {
        int radio = 100;
        int calcRet;
        int ucBCDType;
        int price;

        price = Datautils.byteArrayToInt(runParaFile.getKeyV1());

        if (tCardOpDu.ucOtherCity != 0 && tCardOpDu.cardClass == 0x03)//非本地卡
        {
            return price;
        }
        //天津无带人说法
        if (tCardOpDu.cardClass == 0x07)//交通部CPU卡
        {
            //取城市卡普通卡折扣率
            if (tCardOpDu.isJTBCardAvailable) {
                radio = runParaFile.getUcCitySubRadioP()[0];
            } else {
                radio = 100;
            }

        } else if (tCardOpDu.cardClass == 0x03)//CPU卡
        {
            byte[] ucCpuRadioP = runParaFile.getUcCpuRadioP();
            int toInt = Datautils.byteArrayToInt(new byte[]{tCardOpDu.ucMainCardType});
            if (tCardOpDu.fUseHC == 0)//CPU卡
            {
                radio = Datautils.byteArrayToInt(new byte[]{ucCpuRadioP[toInt]});
            } else {
                if (tCardOpDu.fHC == 0)//首乘
                {
                    radio = Datautils.byteArrayToInt(ucCpuRadioP);
                } else if (tCardOpDu.fHC == 1)//首次换乘
                {
                    radio = Datautils.byteArrayToInt(runParaFile.getUcTransfer1stRadioPur());
                } else if (tCardOpDu.fHC == 2)//二次换乘
                {
                    radio = Datautils.byteArrayToInt(runParaFile.getUcTransfer2ndRadioPur());
                } else if (tCardOpDu.fHC <= Datautils.byteArrayToInt(runParaFile.getUcTransferCntLimit()))//多次换乘，换乘限制次数以下(含换乘限制次数)
                {
                    radio = Datautils.byteArrayToInt(runParaFile.getUcTransferMulRadioPur());
                } else {
                    //换乘限制次数以上
                    radio = Datautils.byteArrayToInt(new byte[]{ucCpuRadioP[toInt]});
                }
            }
        } else if (tCardOpDu.cardClass == 0x01)                                      // =0x01，公交卡
        {
            tCardOpDu.fUseHC = 0;
            int cardTypeToInt = Datautils.byteArrayToInt(new byte[]{tCardOpDu.ucMainCardType});
            byte[] ucBusRadioP = runParaFile.getUcBusRadioP();
            radio = Datautils.byteArrayToInt(new byte[]{ucBusRadioP[cardTypeToInt]});
        } else if ((tCardOpDu.cardClass == 0x51) || (tCardOpDu.cardClass == 0x71)) {
            tCardOpDu.fUseHC = 0;
            if ((tCardOpDu.ucMainCardType != 0) && (tCardOpDu.ucMainCardType != 6))        // 主卡类型==非0或非6, 特种卡
            {
                ucBCDType = (tCardOpDu.ucMainCardType >> 4) * 10 + (tCardOpDu.ucMainCardType & 0x0f);
                if (ucBCDType <= 0x0f) // 主卡类型在16种卡之内，取主卡折扣率
                {
                    radio = Datautils.byteArrayToInt(new byte[]{runParaFile.getUcCityMainRadioP()[ucBCDType]});
                } else { // 大于16种，取普通卡子卡类型折扣率
                    radio = Datautils.byteArrayToInt(new byte[]{runParaFile.getUcCitySubRadioP()[0]});
                }
            } else {// 主卡类型==0（公交兼容卡）或6（测试卡）
                // 取子卡类型折扣率
                int subTypeToInt = Datautils.byteArrayToInt(new byte[]{tCardOpDu.subType});
                radio = Datautils.byteArrayToInt(new byte[]{runParaFile
                        .getUcCitySubRadioP()[subTypeToInt]});
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


    /**
     * 改写24 25块数据
     *
     * @param blk 块号
     * @return
     */
    public static boolean modifyInfoArea(int blk, TCommInfo CInfo, BankCard mBankCard
            , byte[][] lodkey, byte[] snUid) {
        /**
         * 微智接口返回数据
         */
        byte[] respdata = new byte[512];
        /**
         * 微智接口返回数据长度
         */
        int[] resplen = new int[1];
        /**
         * 微智接口返回状态 非0错误
         */
        int retvalue = -1;

        CInfo.ucPurCount[0] = (byte) (CInfo.iPurCount >> 8);
        CInfo.ucPurCount[1] = (byte) CInfo.iPurCount;
        CInfo.ucYueCount[0] = (byte) (CInfo.iYueCount >> 8);
        CInfo.ucYueCount[1] = (byte) CInfo.iYueCount;

        byte[] info = new byte[16];
        byte[] tpdt = new byte[16];
        byte chk;
        int i;
        info[0] = CInfo.cPtr;
        System.arraycopy(CInfo.ucPurCount, 0, info, 1, 2);
        info[3] = CInfo.fProc;
        System.arraycopy(CInfo.ucYueCount, 0, info, 4, 2);
        info[6] = CInfo.fBlack;
        info[7] = CInfo.fFileNr;

        System.arraycopy(new byte[]{(byte) 0xff, (byte) 0xff, (byte)
                0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}, 0, info, 8, 7);
        for (chk = 0, i = 0; i < 15; i++) {
            chk ^= info[i];
        }
        info[15] = chk;
        try {
            //认证6扇区24块
            byte[] lodKeys = lodkey[6];
            retvalue = mBankCard.m1CardKeyAuth(0x41, blk, lodKeys.length, lodKeys
                    , snUid.length, snUid);
            if (retvalue != 0) {
                Log.e(TAG, "Modify_InfoArea: 认证6扇区24块失败");
                return false;
            }
            retvalue = mBankCard.m1CardWriteBlockData(blk, info.length, info);
            if (retvalue != 0) {
                Log.e(TAG, "Modify_InfoArea: 写6扇区24块错误");
                return false;
            }
            retvalue = mBankCard.m1CardReadBlockData(blk, respdata, resplen);
            if (retvalue != 0) {
                Log.e(TAG, "Modify_InfoArea: 读6扇区24块错误");
                return false;
            }
            tpdt = Datautils.cutBytes(respdata, 1, resplen[0] - 1);
            if (!Arrays.equals(info, tpdt)) {
                Log.e(TAG, "Modify_InfoArea: ");
                return false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /********************************************************************
     *            OnAppendRecordSelf() : 自动生成司机上班记录            *
     *           =============================================           *
     *  主要函数模块功能：                                               *
     ********************************************************************/
    public static void onAppendRecordSelf(Context context, byte exchType, TCardOpDU cardOpDU
            , RunParaFile runParaFile, List<PsamBeen> psamBeenList) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] rcdBuffer = new byte[64];
                int i, chk;
                CardRecordDao cardRecordDao = DbDaoManage.getDaoSession().getCardRecordDao();
                rcdBuffer[2] = exchType;
                rcdBuffer[3] = (byte) 0x11;
                rcdBuffer[4] = cardOpDU.cardClass;
                System.arraycopy(cardOpDU.issueSnr, 0, rcdBuffer, 5, 2);
                System.arraycopy(cardOpDU.snr, 0, rcdBuffer, 7, 4);
                System.arraycopy(cardOpDU.issueSnr, 0, rcdBuffer, 11, 8);
                rcdBuffer[19] = cardOpDU.ucMainCardType;
                rcdBuffer[20] = (byte) 0x00;
                System.arraycopy(cardOpDU.startUserDate, 0, rcdBuffer, 21, 3);
                System.arraycopy(cardOpDU.ucDateTimeUTC, 0, rcdBuffer, 24, 4);
                // 28:刷卡上班记录生成方式
                rcdBuffer[28] = (byte) 0x01;
                // 29-30:POS城市代码
                System.arraycopy(runParaFile.getCityNr(), 0, rcdBuffer, 29, 2);
                System.arraycopy(runParaFile.getAreaNr(), 0, rcdBuffer, 31, 1);
                System.arraycopy(runParaFile.getVocNr(), 0, rcdBuffer, 32, 1);
                System.arraycopy(runParaFile.getCorNr(), 0, rcdBuffer, 33, 1);
                System.arraycopy(runParaFile.getTeamNr(), 0, rcdBuffer, 34, 2);
                System.arraycopy(runParaFile.getLineNr(), 0, rcdBuffer, 36, 2);
                // 31-40: Ar,Vo,Co,Te,Li
                System.arraycopy(runParaFile.getBusNr(), 0, rcdBuffer, 38, 3);
                // 41-44: Dev  取sn号后八位
                System.arraycopy(runParaFile.getDevNr(), 0, rcdBuffer, 41, 4);
                System.arraycopy(runParaFile.getKeyV1(), 0, rcdBuffer, 45, 2);
                // 47: 通用折扣率
                rcdBuffer[47] = runParaFile.getUcCitySubRadioP()[0];

                if (psamBeenList.size() == 2) {
                    System.arraycopy(psamBeenList.get(0).getSnr(), 3, rcdBuffer, 52, 1);
                    System.arraycopy(psamBeenList.get(0).getSnr(), 7, rcdBuffer, 53, 3);
                    System.arraycopy(psamBeenList.get(1).getSnr(), 3, rcdBuffer, 56, 1);
                    System.arraycopy(psamBeenList.get(1).getSnr(), 7, rcdBuffer, 57, 3);
                }

                //60??  站点信息
                rcdBuffer[60] = (byte) 0x00;

                for (chk = 0, i = 0; i < 64; i++) {
                    chk ^= rcdBuffer[i];
                }
                rcdBuffer[61] = (byte) chk;
                CardRecord cardRecord = new CardRecord();
                cardRecord.setIsUpload(false);

                LogUtils.d("查库开始");
                long count = DbDaoManage.getDaoSession().getCardRecordDao().count();
                Long id = 1L;
                if (count != 0L) {
                    CardRecord record = DbDaoManage.getDaoSession().getCardRecordDao().loadByRowId(count);
                    if (record.getRecord() != null) {
                        if (record.getRecord().length == 128) {
                            byte[] secondBytes = Datautils.cutBytes(record.getRecord()
                                    , 64, 64);
                            byte[] bytes = new byte[64];
                            if (Arrays.equals(secondBytes, bytes)) {
                                id = record.getRecordId() + 1;
                            } else {
                                id = record.getRecordId() + 2;
                            }

                        } else {
                            id = record.getRecordId() + 1;
                        }
                    }

                }
                LogUtils.d("查库开始2");
                char mID = (char) (id & 0xffff);
                byte[] charToByte = Datautils.charToByte(mID);
                System.arraycopy(charToByte, 0, rcdBuffer, 0, 2);
                System.arraycopy(charToByte, 0, rcdBuffer, 62, 2);

                cardRecord.setRecord(rcdBuffer);
                cardRecord.setRecordId(id);
                cardRecord.setBusRecord(Datautils.byteArrayToString(rcdBuffer));
                DbDaoManage.getDaoSession().getCardRecordDao().insertOrReplace(cardRecord);
                SharedXmlUtil.getInstance(context).write(Info.BUS_RECORD
                        , Datautils.byteArrayToString(rcdBuffer));
                //上传信息
                DataUploadToTianJinUtils.uploadCardData(context);
            }
        }).start();

    }


    /**
     * 写消费记录
     */
    public static void onAppendRecordTrade(Context context, byte exchType, TCardOpDU cardOpDU
            , RunParaFile runParaFile, List<PsamBeen> psamBeenList, BankCard mBankCard) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                int i;
                byte[] rcdBuffer = new byte[128];
                // 0~1:	 流水号
                // 02:   记录类型  0钱包2月票
                rcdBuffer[2] = exchType;
                rcdBuffer[3] = cardOpDU.cardClass == (byte) 0x07 ? (byte) 0x12 : (byte) 0x11;
                rcdBuffer[4] = cardOpDU.cardClass;
                LogUtils.d("写记录开始" + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));
                if ((cardOpDU.cardClass == (byte) 0x01) || (cardOpDU.cardClass == (byte) 0x51)
                        || (cardOpDU.cardClass == (byte) 0x71)) // M1??(S50/S70)
                {
                    // 5-6:  发卡方代码 得0
                    // 7-10:  卡芯片号
                    System.arraycopy(cardOpDU.snr, 0, rcdBuffer, 7, 4);
                    // 11-18: 卡发行流水号
                    System.arraycopy(cardOpDU.issueSnr, 0, rcdBuffer, 11, 8);
                    // 15-18: ??MAC
                    // 19:   卡主类型
                    rcdBuffer[19] = cardOpDU.ucMainCardType;
                    // 20:卡子类型
                    if (cardOpDU.cardClass == (byte) 0x01) {
                        rcdBuffer[20] = (byte) 0x00;
                    } else if ((cardOpDU.cardClass == (byte) 0x51) || (cardOpDU.cardClass == (byte) 0x71)) {
                        rcdBuffer[20] = cardOpDU.subType;
                    } else {
                        rcdBuffer[20] = (byte) 0x00;
                    }
                    // 21-23:应用日期
                    System.arraycopy(cardOpDU.issueDate, 0, rcdBuffer, 21, 3);
                    //24-27:交易时间
                    System.arraycopy(cardOpDU.ucDateTimeUTC, 0, rcdBuffer, 24, 4);
                } else {//CPU卡
                    if (cardOpDU.ucCardClass == JTBCARD)//交通部
                    {
                        //5-6:  发卡方代码
                        System.arraycopy(cardOpDU.ucIssuerCode, 0, rcdBuffer, 5, 2);
                        //7-8:  城市代码
                        System.arraycopy(cardOpDU.ucCityCode, 0, rcdBuffer, 7, 2);
                        //9-18: 应用序列号
                        System.arraycopy(cardOpDU.ucAppSnr, 0, rcdBuffer, 9, 10);
                    } else {
                        //5-6:  发卡方代码
                        System.arraycopy(cardOpDU.ucIssuerCode, 0, rcdBuffer, 5, 2);
                        //7-8:  城市代码
                        System.arraycopy(cardOpDU.ucCityCode, 0, rcdBuffer, 7, 2);
                        //9-10: 行业代码
                        System.arraycopy(cardOpDU.ucVocCode, 0, rcdBuffer, 9, 2);
                        //11-18:应用序列号
                        System.arraycopy(cardOpDU.ucAppSnr, 2, rcdBuffer, 11, 8);
                    }
                    rcdBuffer[19] = cardOpDU.ucMainCardType;                //19:   卡主类型
                    rcdBuffer[20] = cardOpDU.ucSubCardType;                //20:   卡子类型
                    //21-24:应用启动日期
                    System.arraycopy(cardOpDU.ucAppStartDate, 1, rcdBuffer, 21, 3);
                    //24-27:交易时间
                    System.arraycopy(cardOpDU.ucDateTimeUTC, 0, rcdBuffer, 24, 4);

                }
                if ((cardOpDU.cardClass == (byte) 0x01) || (cardOpDU.cardClass == (byte) 0x51)
                        || (cardOpDU.cardClass == (byte) 0x71)) // M1卡(S50/S70)
                {
                    if (cardOpDU.ucProcSec == 2)// 消费钱包
                    {
                        //28-29: 充值计数器
                        System.arraycopy(cardOpDU.uiIncPurCount, 0, rcdBuffer, 28, 2);
                        //30-32: 充值日期
                        System.arraycopy(cardOpDU.ucIncPurDate, 0, rcdBuffer, 30, 3);
                        if (cardOpDU.cardClass == (byte) 0x01) {
                            //33-34：卡内钱包计数器
                            rcdBuffer[33] = (byte) (cardOpDU.purCount >> 8);
                            rcdBuffer[34] = (byte) cardOpDU.purCount;
                        } else {
                            //33-34：卡内钱包计数器
                            rcdBuffer[33] = (byte) ((cardOpDU.purCount + 1) >> 8);
                            rcdBuffer[34] = (byte) (cardOpDU.purCount + 1);
                        }
                        rcdBuffer[35] = 0x06;                                //35:    交易类型标识
                        rcdBuffer[36] = (byte) (cardOpDU.purorimoneyInt >> 16);                //36-38	交易前余额
                        rcdBuffer[37] = (byte) (cardOpDU.purorimoneyInt >> 8);
                        rcdBuffer[38] = (byte) cardOpDU.purorimoneyInt;
                        rcdBuffer[39] = (byte) (cardOpDU.pursubInt >> 16);                    //39-41 交易额
                        rcdBuffer[40] = (byte) (cardOpDU.pursubInt >> 8);
                        rcdBuffer[41] = (byte) cardOpDU.pursubInt;

                        if (psamBeenList.size() != 0) {
                            rcdBuffer[44] = psamBeenList.get(0).getSnr()[3];
                            System.arraycopy(psamBeenList.get(0).getSnr(), 7, rcdBuffer, 45, 3);
                        }

                        //48-51: PSAM卡终端交易序号
                        System.arraycopy(cardOpDU.ucIncPurDev, 0, rcdBuffer, 48, 4);
                        //56-58: 交易实扣金额
                        rcdBuffer[56] = (byte) (cardOpDU.pursubInt >> 16);
                        rcdBuffer[57] = (byte) (cardOpDU.pursubInt >> 8);
                        rcdBuffer[58] = (byte) cardOpDU.pursubInt;
                    } else {//月票
                        //27-28: 充值计数器
                        rcdBuffer[28] = (byte) (cardOpDU.uiIncYueCount >> 8);
                        rcdBuffer[29] = (byte) cardOpDU.uiIncYueCount;
                        //29-31: 充值日期
                        System.arraycopy(cardOpDU.yueUsingDate, 0, rcdBuffer, 30, 3);
                        //33-34: 是公交月票卡,记录里是月票计数器
                        if (cardOpDU.cardClass == (byte) 0x01) {
                            rcdBuffer[33] = (byte) (cardOpDU.yueCount >> 8);
                            rcdBuffer[34] = (byte) cardOpDU.yueCount;
                        } else {
                            //33-34: 是城市月票卡,记录里是钱包计数器
                            rcdBuffer[33] = (byte) ((cardOpDU.yueCount + 1) >> 8);
                            rcdBuffer[34] = (byte) (cardOpDU.yueCount + 1);
                        }
                        rcdBuffer[35] = (byte) (cardOpDU.yueOriMoney >> 24);                //35-38 交易前余额
                        rcdBuffer[36] = (byte) (cardOpDU.yueOriMoney >> 16);
                        rcdBuffer[37] = (byte) (cardOpDU.yueOriMoney >> 8);
                        rcdBuffer[38] = (byte) cardOpDU.yueOriMoney;
                        rcdBuffer[39] = (byte) (cardOpDU.yueSub >> 16);                //39-41 交易额
                        rcdBuffer[40] = (byte) (cardOpDU.yueSub >> 8);
                        rcdBuffer[41] = (byte) cardOpDU.yueSub;
                        if (psamBeenList.size() != 0) {
                            rcdBuffer[44] = psamBeenList.get(0).getSnr()[3];
                            System.arraycopy(psamBeenList.get(0).getSnr(), 7, rcdBuffer, 45, 3);
                        }
                        rcdBuffer[56] = (byte) (cardOpDU.actYueSub >> 16);                //56-58: 交易实扣金额
                        rcdBuffer[57] = (byte) (cardOpDU.actYueSub >> 8);
                        rcdBuffer[58] = (byte) cardOpDU.actYueSub;
                    }
                } else {//CPU
                    if (cardOpDU.ucProcSec == 2)//传统钱包
                    {
                        //33-34:消费序号
                        System.arraycopy(cardOpDU.uiOffLineCount, 0, rcdBuffer, 33, 2);
                        //35:交易类型标识表
                        rcdBuffer[35] = cardOpDU.ucCAPP == (byte) 0x01 ? (byte) 0x09 : (byte) 0x06;
                        rcdBuffer[36] = (byte) (cardOpDU.purorimoneyInt >> 16);
                        rcdBuffer[37] = (byte) (cardOpDU.purorimoneyInt >> 8);
                        rcdBuffer[38] = (byte) cardOpDU.purorimoneyInt;
                        rcdBuffer[39] = (byte) (cardOpDU.pursubInt >> 16);                    //40-41交易额
                        rcdBuffer[40] = (byte) (cardOpDU.pursubInt >> 8);
                        rcdBuffer[41] = (byte) cardOpDU.pursubInt;
                        System.arraycopy(cardOpDU.ucPOSSnr, 0, rcdBuffer, 42, 6);
                        rcdBuffer[48] = (byte) (cardOpDU.ulPOSTradeCount >> 24);        //48-51: PSAM???????????
                        rcdBuffer[49] = (byte) (cardOpDU.ulPOSTradeCount >> 16);
                        rcdBuffer[50] = (byte) (cardOpDU.ulPOSTradeCount >> 8);
                        rcdBuffer[51] = (byte) cardOpDU.ulPOSTradeCount;
                        //52-55: TAC码
                        System.arraycopy(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}
                                , 0, rcdBuffer, 52, 4);
                        rcdBuffer[56] = (byte) (cardOpDU.pursubInt >> 16);                    //56-58:交易实扣金额
                        rcdBuffer[57] = (byte) (cardOpDU.pursubInt >> 8);
                        rcdBuffer[58] = (byte) cardOpDU.pursubInt;
                    } else {//传统优惠区
                        //30-32:充值日期
                        System.arraycopy(cardOpDU.ucYueUsingDate, 0, rcdBuffer, 30, 3);
                        //33-34:消费序号
                        System.arraycopy(cardOpDU.uiOffLineCount, 0, rcdBuffer, 33, 2);
                        //35:交易类型标识表
                        rcdBuffer[35] = cardOpDU.ucCAPP == (byte) 0x01 ? (byte) 0x09 : (byte) 0x06;
                        rcdBuffer[36] = (byte) (cardOpDU.yueOriMoney >> 16);                    //35-38交易前余额
                        rcdBuffer[37] = (byte) (cardOpDU.yueOriMoney >> 8);
                        rcdBuffer[38] = (byte) cardOpDU.yueOriMoney;
                        rcdBuffer[39] = (byte) (cardOpDU.yueSub >> 16);                    //39-41 交易额
                        rcdBuffer[40] = (byte) (cardOpDU.yueSub >> 8);
                        rcdBuffer[41] = (byte) cardOpDU.yueSub;
                        //42-47: PSAM卡终端机编号
                        System.arraycopy(cardOpDU.ucPOSSnr, 0, rcdBuffer, 42, 6);
                        rcdBuffer[48] = (byte) (cardOpDU.ulPOSTradeCount >> 24);        //48-51: PSAM卡终端交易序号
                        rcdBuffer[49] = (byte) (cardOpDU.ulPOSTradeCount >> 16);
                        rcdBuffer[50] = (byte) (cardOpDU.ulPOSTradeCount >> 8);
                        rcdBuffer[51] = (byte) cardOpDU.ulPOSTradeCount;
                        //52-55: TAC码
                        System.arraycopy(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}
                                , 0, rcdBuffer, 52, 4);
                        rcdBuffer[56] = (byte) (cardOpDU.actYueSub >> 16);                //56-58: 交易实扣金额
                        rcdBuffer[57] = (byte) (cardOpDU.actYueSub >> 8);
                        rcdBuffer[58] = (byte) cardOpDU.actYueSub;
                    }
                }
                rcdBuffer[59] = cardOpDU.fUseHC == 0 ? (byte) 0xff : (byte) cardOpDU.fHC;
                rcdBuffer[60] = (byte) 0x00;

                String bus = SharedXmlUtil.getInstance(context).read(Info.BUS_RECORD, "");
                if (TextUtils.isEmpty(bus)) {
                    System.arraycopy(new byte[]{(byte) 0x00, (byte) 0x00}, 0
                            , rcdBuffer, 62, 2);
                } else {
                    byte[] busByte = Datautils.cutBytes(Datautils.HexString2Bytes(bus), 0, 2);
                    System.arraycopy(busByte, 0, rcdBuffer, 62, 2);
                }


                if ((cardOpDU.cardClass == (byte) 0x01) || (cardOpDU.cardClass == (byte) 0x51)
                        || (cardOpDU.cardClass == (byte) 0x71)) {
                    byte[] tac = calcTAC(cardOpDU, rcdBuffer, mBankCard);
                    System.arraycopy(tac, 0, rcdBuffer, 52, 4);
                } else { //CPUCard
                    System.arraycopy(cardOpDU.ucTAC, 0, rcdBuffer, 52, 4);
                }


                if (cardOpDU.ucCardClass == JTBCARD) {
                    for (i = 64; i < 128; i++) {
                        rcdBuffer[i] = 0;
                    }
                    rcdBuffer[64 + 2] = exchType;                                // 2:    记录类型
                    rcdBuffer[64 + 3] = (byte) 0x22;                                    // 3:	 分段标示
                    System.arraycopy(psamBeenList.get(0).getSnr(), 0, rcdBuffer, 64 + 4, 10);
                    rcdBuffer[64 + 14] = cardOpDU.ucKeyVer;
                    rcdBuffer[64 + 15] = cardOpDU.ucKeyAlg;
                    // 17-20: 应用日期
                    System.arraycopy(cardOpDU.ucAppEndDate, 0, rcdBuffer, 64 + 16, 4);
                    rcdBuffer[64 + 62] = rcdBuffer[62];
                    rcdBuffer[64 + 63] = rcdBuffer[63];

                }

                CardRecord cardRecord = new CardRecord();
                cardRecord.setIsUpload(false);
                cardRecord.setBusRecord(bus);
                LogUtils.d("查库开始" + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));
                long count = DbDaoManage.getDaoSession().getCardRecordDao().count();
                Long id = 0L;
                if (count != 0L) {
                    CardRecord record = DbDaoManage.getDaoSession().getCardRecordDao().loadByRowId(count);
                    if (record.getRecord() != null) {
                        if (record.getRecord().length == 128) {
                            byte[] secondBytes = Datautils.cutBytes(record.getRecord()
                                    , 64, 64);
                            byte[] bytes = new byte[64];
                            if (Arrays.equals(secondBytes, bytes)) {
                                id = record.getRecordId() + 1;
                            } else {
                                id = record.getRecordId() + 2;
                            }
                        } else {
                            id = record.getRecordId() + 1;
                        }
                    }
                }
                LogUtils.d("查库开始2" + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));
                char mID = (char) (id & 0xffff);
                byte[] charToByte = Datautils.charToByte(mID);
                System.arraycopy(charToByte, 0, rcdBuffer, 0, 2);
                if (cardOpDU.ucCardClass == JTBCARD) {
                    Long secondId = id + 1;
                    char mID2 = (char) (secondId & 0xffff);
                    byte[] charToByte2 = Datautils.charToByte(mID2);
                    System.arraycopy(charToByte2, 0, rcdBuffer, 64, 2);
                    LogUtils.d("查库开始3" + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));
                }
                cardRecord.setRecord(rcdBuffer);
                cardRecord.setRecordId(id);
                MyApplication.setCardRecordList(cardRecord);

                if (cardRecord == null || cardRecord.getRecord() == null) {
                    LogUtils.d("记录信息为空");
                    return;
                }
                DbDaoManage.getDaoSession().getCardRecordDao().insert(cardRecord);
                DataUploadToTianJinUtils.uploadCardData(context);
                LogUtils.d("写记录结束" + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));
            }
        }).start();

    }

    // 计算TAC
    private static byte[] calcTAC(TCardOpDU carddu, byte[] rcdBuffer, BankCard mBankCard) {
        byte[] tac = new byte[4];
        byte[] pSAMDat = new byte[80];
        byte[] pSAMCmd;
        int rlen;
        int returnStatus;
        int i;

        if (carddu.cardClass == (byte) 0x01)                                        // 公交卡
        {
            for (i = 0; i < 4; i++) {
                tac[i] = (byte) 0x30;                                     // TAC错误=0x30,0x30,0x30,0x30
            }
        } else if ((carddu.cardClass == (byte) 0x51) || (carddu.cardClass == (byte) 0x71))        // 城市卡
        {
            pSAMCmd = new byte[]{(byte) 0x80, (byte) 0x1a, (byte) 0x24, (byte) 0x01, (byte) 0x08
                    , (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
                    , (byte) 0x00, (byte) 0x00};
            pSAMDat = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM1_APDU
                    , pSAMCmd);
            if (pSAMDat == null || pSAMDat.length == 2) {
            }
            pSAMCmd = new byte[45];
            System.arraycopy(new byte[]{(byte) 0x80, (byte) 0xfa, (byte) 0x05, (byte) 0x00, (byte) 0x28}
                    , 0, pSAMCmd, 0, 5);
            for (i = 0; i < 8; i++) {
                pSAMCmd[5 + i] = 0;                                     // 05-12: 8??0,??'???
            }
            for (i = 0; i < 4; i++) {
                pSAMCmd[13 + i] = rcdBuffer[44 + i];                // 13-16 SAM卡号 4
            }
            pSAMCmd[17] = (byte) 0x00;
            pSAMCmd[18] = (byte) 0x00;
            for (i = 0; i < 2; i++) {
                pSAMCmd[19 + i] = rcdBuffer[i];                    // 17-20: 终端交易流水号4
            }

            for (i = 0; i < 2; i++) {
                pSAMCmd[21 + i] = rcdBuffer[11 + i];                // 21-22: 城市代码 2
            }
            for (i = 0; i < 4; i++) {
                pSAMCmd[23 + i] = rcdBuffer[15 + i];                // 23-26: 卡发行流水号 4
            }
            pSAMCmd[27] = rcdBuffer[19];                                    // 27:    卡类型 1

            pSAMCmd[28] = rcdBuffer[38];                                    // 28-30: 交易前余额，3B
            pSAMCmd[29] = rcdBuffer[37];
            pSAMCmd[30] = rcdBuffer[36];

            pSAMCmd[31] = rcdBuffer[41];                                     // 31-33: 交易额，3B
            pSAMCmd[32] = rcdBuffer[40];
            pSAMCmd[33] = rcdBuffer[39];

            for (i = 0; i < 7; i++) {
                pSAMCmd[34 + i] = carddu.ucDateTime[i];                          // 34-40: 交易日期4+交易时间3
            }

            for (i = 0; i < 2; i++) {
                pSAMCmd[41 + i] = rcdBuffer[33 + i];                // 41-42: 卡交易计数器 2
            }

            pSAMCmd[43] = (byte) 0x80;
            pSAMCmd[44] = (byte) 0x00;                                  // 43-44

            pSAMDat = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM1_APDU
                    , pSAMCmd);
            if (pSAMDat == null || pSAMDat.length == 2) {
                for (i = 0; i < 4; i++) {
                    tac[i] = (byte) 0x30;
                }
            } else {
                for (i = 0; i < 4; i++) {
                    tac[i] = pSAMDat[i];
                }
            }
        }
        return tac;
    }


    /**
     * 用户卡(IC卡)交易初始化指令 8050指令
     *
     * @return 8050指令
     */
    public static byte[] cpuCardInit(TCardOpDU cardOpDU) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("805001020B");
        if (cardOpDU.ucCAPP == 1) {
            stringBuilder.replace(5, 6, "3");
        }
        stringBuilder.append(Datautils.byteArrayToString(cardOpDU.ucKeyID))
                .append(Datautils.byteArrayToString(cardOpDU.pursub))
                .append(Datautils.byteArrayToString(cardOpDU.ucPOSSnr))
                .append("0F");
        return Datautils.HexString2Bytes(stringBuilder.toString());
    }
}
