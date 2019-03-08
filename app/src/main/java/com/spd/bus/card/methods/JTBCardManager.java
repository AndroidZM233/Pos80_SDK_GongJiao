package com.spd.bus.card.methods;

import android.util.Log;

import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.Datautils;
import com.spd.bus.card.methods.bean.CardBackBean;
import com.spd.bus.card.methods.bean.TCardOpDU;
import com.spd.bus.card.methods.bean.TPCardDU;
import com.spd.bus.card.utils.LogUtils;
import com.spd.bus.spdata.been.PsamBeen;
import com.spd.bus.spdata.utils.PlaySound;
import com.spd.bus.util.TLV;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import wangpos.sdk4.libbasebinder.BankCard;

/**
 * 交通部卡
 * Created by 张明_ on 2019/2/18.
 * Email 741183142@qq.com
 */
public class JTBCardManager {
    private static final Object LOCK = new Object();
    private static JTBCardManager jtbCardManager;
    private TCardOpDU tCardOpDU;
    private TPCardDU cardDU;
    private final int METHOD_OK = 99;
    private byte[] problemIssueCode;
    //是否恢复
    private boolean fSysSta = false;
    //是否黑名单
    private boolean fBlackCard = false;
    private RunParaFile runParaFile;
    private int purDisable = 0;
    private int fOldDisable = 0;

    public static JTBCardManager getInstance() {
        if (jtbCardManager == null) {
            synchronized (LOCK) {
                jtbCardManager = new JTBCardManager();
            }
        }
        return jtbCardManager;
    }


    public CardBackBean mainMethod(BankCard mBankCard, List<PsamBeen> psamDatas, byte[] cpuCard, byte[] lPurSub) {
        long ltime = System.currentTimeMillis();
        tCardOpDU = new TCardOpDU();
        cardDU = new TPCardDU();
        cardDU.cardClass = (byte) 0x07;
        tCardOpDU.lPurSubByte = lPurSub;
        //复合交易
        tCardOpDU.ucCAPP = 1;
        int first = getFirst(mBankCard, psamDatas.get(1));
        if (first != METHOD_OK) {
            return new CardBackBean(first, 0);
        }
        int snr = getSnr(mBankCard);
        if (snr != METHOD_OK) {
            return new CardBackBean(snr, 0);
        }

        int fSysSta = doFSysSta(mBankCard);
        if (fSysSta != METHOD_OK) {
            return new CardBackBean(fSysSta, 0);
        }
        CardBackBean cardBackBean = consumption(mBankCard, cpuCard);
        LogUtils.i("===消费结束===" + (System.currentTimeMillis() - ltime));

        return cardBackBean;
    }

    public int getFirst(BankCard mBankCard, PsamBeen psamBeen) {
        byte[] resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, CardMethods.SELEC_PPSE);
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===消费记录3031error=== " + Datautils.byteArrayToString(resultBytes));
            return mustToSend(mBankCard, psamBeen);
        } else {
            List<String> listTlv = new ArrayList<>();
            TLV.anaTagSpeedata(resultBytes, listTlv);
            if (listTlv.contains("A000000632010105")) {
                //选择电子钱包应用
                resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, CardMethods.SELECT_ICCARD_QIANBAO);
                if (resultBytes == null || resultBytes.length == 2) {
                    LogUtils.e("===解析到TLV发送0105 return===" + Datautils.byteArrayToString(resultBytes));
                    return ReturnVal.CAD_READ;
                }
            } else {
                return mustToSend(mBankCard, psamBeen);

            }
        }
        return METHOD_OK;
    }

    private int mustToSend(BankCard mBankCard, PsamBeen psamBeen) {
        byte[] resultBytes;//选择电子钱包应用
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, CardMethods.SELECT_ICCARD_QIANBAO);
        if (resultBytes == null) {
            LogUtils.e("icExpance: 获取交易时间错误");
            return ReturnVal.CAD_READ;
        } else if (Arrays.equals(resultBytes, CardMethods.APDU_RESULT_FAILE_6283) ||
                Arrays.equals(resultBytes, CardMethods.APDU_RESULT_FAILE_6284) ||
                Arrays.equals(resultBytes, CardMethods.APDU_RESULT_FAILE3_9303)) {
            // TODO: 2019/1/3  查数据库黑名单报语音
            return ReturnVal.CAD_BLK;
        } else {
            return ZJBCardManager.getInstance().mainMethod(mBankCard, psamBeen);
        }
    }

    /**
     * 交通部读15 17文件
     *
     * @return
     */
    public int getSnr(BankCard mBankCard) {
        byte[] resultBytes;
        cardDU.cardClass = (byte) 0x07;
        tCardOpDU.fInBus = (byte) 0x01;
        //系统时间
        byte[] systemTime = Datautils.getDateTime();
        tCardOpDU.ucDateTime = systemTime;
        tCardOpDU.ucCardClass = CardMethods.JTBCARD;
        tCardOpDU.ucCheckDate = new byte[]{(byte) 0x20, (byte) 0x99, (byte) 0x12, (byte) 0x31};
        tCardOpDU.ucOtherCity = (byte) 0x00;

        //***Read 15 File  读应用下公共应用基本信息文件指令
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                , CardMethods.READ_ICCARD_15FILE);
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===读15文件error===" + Datautils.byteArrayToString(resultBytes));
            return ReturnVal.CAD_READ;
        }

        tCardOpDU.setDataStartUcIssuerCode(resultBytes);
        tCardOpDU.ucFile15Top8 = Datautils.concatAll(tCardOpDU.ucIssuerCode, tCardOpDU.ucCityCode
                , tCardOpDU.ucVocCode, tCardOpDU.ucRfu1);
        if (tCardOpDU.ucAppVer == (byte) 0xff) {
            return ReturnVal.CAD_EXPIRE;
        }
        //本地卡类型  00普通卡 01敬老卡 11残疾人卡
        tCardOpDU.ucMainCardType = resultBytes[28];
        tCardOpDU.ucSubCardType = (byte) 0x00;
        //代码翻译
        //memset(ucCheckSnr, 0, 10);
        //ucCheckSnr[0]=0x0;memcpy(ucCheckSnr+1,tCardOpDu.ucIssuerCode, 4);
        //memset(ucCheckSnr+5,0xFF,3);
        byte[] ucCheckSnr = Datautils.concatAll(new byte[]{(byte) 0x00}, tCardOpDU.ucIssuerCode,
                tCardOpDU.ucCityCode, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x00});
        //卡里的城市码不是天津码时
        if (!Arrays.equals(Datautils.concatAll(tCardOpDU.ucIssuerCode, tCardOpDU.ucCityCode)
                , new byte[]{(byte) 0x01, (byte) 0x13, (byte) 0x11, (byte) 0x21})) {
            tCardOpDU.ucOtherCity = (byte) 0x01;
        }

        byte[] ucDateTimeByte = Datautils.cutBytes(tCardOpDU.ucDateTime, 0, 4);
        int ucDateTimeInt = Integer.parseInt(Datautils.byteArrayToString(ucDateTimeByte));
        int ucAppStartDateInt = Integer.parseInt(Datautils.byteArrayToString(tCardOpDU.ucAppStartDate));
        //当前时间小于开始时间
        if (ucDateTimeInt < ucAppStartDateInt) {
            return ReturnVal.CAD_SELL;
        }
        int ucAppEndDateInt = Integer.parseInt(Datautils.byteArrayToString(tCardOpDU.ucAppEndDate));
        //当前时间大于结束时间
        if (ucDateTimeInt > ucAppEndDateInt) {
            return ReturnVal.CAD_EXPIRE;
        }

        //***Read 17 File
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, CardMethods.READ_ICCARD_17FILE);
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===读17文件error===" + Datautils.byteArrayToString(resultBytes));
            return ReturnVal.CAD_READ;
        }
        //9-10	互通卡种（互通标志，可确认挂失与否）
        // 2	n	与电子现金08文件数据同步  0000：非互通卡0001：互通卡
        if (tCardOpDU.ucOtherCity == (byte) 0x01) {
            tCardOpDU.ucMainCardType = (byte) 0x00;
            //=DBDat[10]; 异地卡都认为是普通卡
            tCardOpDU.ucSubCardType = (byte) 0x00;
        }
        return METHOD_OK;
    }

    public int doFSysSta(BankCard mBankCard) {
        if (fSysSta) {
            byte[] ucAppSnr = Datautils.cutBytes(tCardOpDU.ucAppSnr, 2, 8);
            byte[] problemIssue2 = Datautils.cutBytes(problemIssueCode, 2, 8);
            //不是同一张卡
            if (!Arrays.equals(ucAppSnr, problemIssue2)) {
                fSysSta = false;
            } else {
                tCardOpDU.uiOffLineCount = Datautils.cutBytes(problemIssueCode, 10, 2);
                byte[] value3;
                if (problemIssueCode[21] == (byte) 0x00) {
                    value3 = new byte[]{(byte) 0x06};
                } else {
                    value3 = new byte[]{(byte) 0x09};
                }
                byte[] dBCmd = Datautils.concatAll(new byte[]{(byte) 0x80, (byte) 0x5a, (byte) 0x00}, value3
                        , new byte[]{(byte) 0x02}, tCardOpDU.uiOffLineCount, new byte[]{(byte) 0x08});
                byte[] resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, dBCmd);
                if (resultBytes == null || resultBytes.length == 2) {
                    LogUtils.e("===恢复error===" + Datautils.byteArrayToString(resultBytes));
                    fSysSta = false;
                } else {
                    tCardOpDU.ucMAC2 = Datautils.cutBytes(resultBytes, 0, 4);
                    tCardOpDU.ucTAC = Datautils.cutBytes(resultBytes, 4, 4);
                    byte[] mac2 = Datautils.cutBytes(resultBytes, 0, 8);
                    byte[] psamCheckMac2 = CardMethods.checkPsamMac2(mac2);
                    resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM1_APDU, psamCheckMac2);
                    if (resultBytes == null || resultBytes.length == 2) {
                        LogUtils.e("===psam卡(8072)校验error===");
                        return ReturnVal.CAD_MAC2;
                    }
                    fSysSta = false;
                    tCardOpDU.ucProcSec = problemIssueCode[12];
                    // ucProcSec=2代表钱包消费
                    if (tCardOpDU.ucProcSec == (byte) 0x02) {
                        tCardOpDU.ucRcdType = (byte) 0x00;
                    } else {
                        tCardOpDU.ucRcdType = (byte) 0x02;
                    }
//                    tCardOpDU.ucRcdType = tCardOpDU.ucRcdType & 0xfe;

//                    OnAppendRecordTrade(tCardOpDu.ucRcdType);//写记录

                    return ReturnVal.CAD_OK;
                }
            }
        }

        // TODO: 2019/2/20 去获取当前卡号是否在黑名单
//        fBlackCard==CheckBlacklist(tCardOpDu.ucAppSnr);

        if (fBlackCard) {
            byte[] cmd801A = Datautils.concatAll(new byte[]{(byte) 0x80, (byte) 0x1a, (byte) 0x45, (byte) 0x02, (byte) 0x10},
                    Datautils.cutBytes(tCardOpDU.ucAppSnr, 2, 8), tCardOpDU.ucFile15Top8);
            byte[] resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM1_APDU, cmd801A);
            if (resultBytes == null || resultBytes.length == 2) {
                LogUtils.e("===801A指令error===" + Datautils.byteArrayToString(resultBytes));
                return ReturnVal.CAD_READ;
            }
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, Datautils.HexString2Bytes("0084000004"));
            if (resultBytes == null || resultBytes.length == 2) {
                LogUtils.e("===0084000004指令error===" + Datautils.byteArrayToString(resultBytes));
                return ReturnVal.CAD_READ;
            }
            byte[] cmd80fa = Datautils.concatAll(new byte[]{(byte) 0x80, (byte) 0xfa, (byte) 0x05, (byte) 0x00, (byte) 0x10}
                    , Datautils.cutBytes(resultBytes, 0, 4), new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
                            , (byte) 0x84, (byte) 0x1e, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x80, (byte) 0x00, (byte) 0x00});
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM1_APDU, cmd80fa);
            if (resultBytes == null || resultBytes.length == 2) {
                LogUtils.e("===80fa指令error===" + Datautils.byteArrayToString(resultBytes));
                return ReturnVal.CAD_READ;
            }
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, Datautils.HexString2Bytes("841e000004"));
            if (resultBytes == null || resultBytes.length == 2) {
                LogUtils.e("===841e000004指令error===" + Datautils.byteArrayToString(resultBytes));
                return ReturnVal.CAD_READ;
            }


            tCardOpDU.ucProcSec = (byte) 0x02;
            tCardOpDU.uiOffLineCount = new byte[]{(byte) 0x00, (byte) 0x00};
            tCardOpDU.lPurOriMoney = 0;
            tCardOpDU.lPurSub = 0;
            tCardOpDU.ulPOSTradeCount = 0;
            tCardOpDU.ucTAC = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            // TODO: 2019/2/20
//            PrepareRecord(0xE0);
//            OnAppendRecord(0xE0);
            return ReturnVal.CAD_BLK;
        }

        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession()
                .getRunParaFileDao().loadAll();
        if (runParaFiles.size() < 1) {
            return ReturnVal.NO_SET;
        }
        runParaFile = runParaFiles.get(0);

        byte[] resultBytes;
        if (tCardOpDU.ucProcSec == 2) {
            int ret = CardMethods.fIsUsePur(cardDU, tCardOpDU, runParaFile);    //判断钱包权限

            if ((tCardOpDU.ucOtherCity == 0) && (tCardOpDU.ucMainCardType == 0x01
                    || tCardOpDU.ucMainCardType == 0x11) && (ret == 0)) {
                purDisable = 1;
            } else if (ret == 0)        //没有权限
            {
                tCardOpDU.lPurOriMoney = 0;
                tCardOpDU.lPurSub = 0;
                return ReturnVal.CAD_EMPTY;
            }

        }
        cardDU.fUseHC = 0;
        //本地 老年卡 残疾人卡  第一次刷
        if ((tCardOpDU.ucOtherCity == 0) && (tCardOpDU.ucMainCardType == 0x01
                || tCardOpDU.ucMainCardType == 0x11) && (fOldDisable == 0)) {
            //读05文件30字节
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                    , new byte[]{(byte) 0x00, (byte) 0xB0, (byte) 0x85, (byte) 0x00, (byte) 0x00});
            if (resultBytes == null || resultBytes.length == 2) {
                LogUtils.e("===B085error===" + Datautils.byteArrayToString(resultBytes));
                return ReturnVal.CAD_READ;
            }
            byte[] ucDateTimeByte = Datautils.cutBytes(tCardOpDU.ucDateTime, 0, 4);
            int ucDateTimeInt = Integer.parseInt(Datautils.byteArrayToString(ucDateTimeByte));
            int dBDatInt = Integer.parseInt(Datautils
                    .byteArrayToString(Datautils.cutBytes(resultBytes, 36, 4)));
            //判断是否在有效期之内
            if (dBDatInt < ucDateTimeInt) {
                //超过有效期
                fOldDisable = 1;
            }

            //读取17上条复合记录命令48字节
            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                    , new byte[]{(byte) 0x00, (byte) 0xB2, (byte) 0x01, (byte) 0xF4, (byte) 0x00});
            if (resultBytes == null || resultBytes.length == 2) {
                LogUtils.e("===B201error===" + Datautils.byteArrayToString(resultBytes));
                return ReturnVal.CAD_READ;
            }

            //判断是否是同一辆车
            byte[] busBytes = Datautils.cutBytes(resultBytes, 3, 6);

            if (Arrays.equals(busBytes, tCardOpDU.ucPOSSnr)) {
                //上次刷卡时间
                byte[] busLastBytes = Datautils.cutBytes(resultBytes, 25, 7);
                String busLastStr = Datautils.byteArrayToString(busLastBytes);
                // 连续刷卡限制时间
                int timeLimit = Datautils.byteArrayToInt(runParaFile.getUcOldCardTimeLimit());
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                try {
                    Date busLast = format.parse(busLastStr);
                    Date dateTime = format.parse(Datautils.byteArrayToString(ucDateTimeByte));
                    Date busLastAfter = new Date(busLast.getTime() + timeLimit * 60000);
                    Date dateTimeAfter = new Date(dateTime.getTime() + timeLimit * 60000);
                    int compare = dateTime.compareTo(busLastAfter);
                    int compare1 = busLast.compareTo(dateTimeAfter);
                    if (compare < 0 && compare1 < 0) {
                        fOldDisable = 1;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return METHOD_OK;
    }

    public CardBackBean consumption(BankCard mBankCard, byte[] cpuCard) {
        LogUtils.d("===IC读1E文件 00b2send===00B201F400");
        byte[] resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, Datautils.HexString2Bytes("00B201F400"));
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===IC读1E文件error===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_READ, 0);
        }
        LogUtils.d("===IC读1E文件00b2return===" + Datautils.byteArrayToString(resultBytes));
        LogUtils.d("===IC余额(805c)send===  805C030204");
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, Datautils.HexString2Bytes("805C030204"));
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===IC卡初始化(8050)error===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_READ, 0);
        }
        LogUtils.d("===IC余额(805c)return===" + Datautils.byteArrayToString(resultBytes));
        LogUtils.d("===IC卡初始化(8050)send===" + Datautils.byteArrayToString(cpuCard));
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, cpuCard);
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===IC卡初始化(8050)error===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_READ, 0);
        }
        tCardOpDU.ulBalanceByte = Datautils.cutBytes(resultBytes, 0, 4);
        tCardOpDU.ulBalance = Datautils.byteArrayToInt(tCardOpDU.ulBalanceByte);
        tCardOpDU.uiOffLineCount = Datautils.cutBytes(resultBytes, 4, 2);
        tCardOpDU.ucKeyVer = resultBytes[9];
        tCardOpDU.ucKeyAlg = resultBytes[10];
        tCardOpDU.rondomCpu = Datautils.cutBytes(resultBytes, 11, 4);
        LogUtils.d("===IC卡初始化(8050)return=== " + Datautils.byteArrayToString(resultBytes) + "\n" +
                "===电子钱包余额:" + tCardOpDU.ulBalance + "\n" +
                "===CPU卡脱机交易序号:  " + Datautils.byteArrayToString(tCardOpDU.uiOffLineCount) + "\n" +
                "===密钥版本 : " + tCardOpDU.ucKeyAlg);
        // TODO: 2019/1/8  设定本次消费额
        if ((tCardOpDU.ulBalance / 100) > 1000) {
            // TODO: 2019/1/8  语音请投币  界面显示卡片已损坏
        }
        if ((tCardOpDU.ulBalance / 100) < 0.01) {
            // TODO: 2019/1/8 语音/界面  余额不足
        }
        byte[] psamMac1 = CardMethods.initSamForPurchase(cardDU, tCardOpDU);

        LogUtils.d("===获取MAC1(8070)send===" + Datautils.byteArrayToString(psamMac1));
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM1_APDU, psamMac1);
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===获取MAC1(8070)error===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_READ, 0);
        }
        LogUtils.d("===获取MAC18070return===" + Datautils.byteArrayToString(resultBytes));
        if (resultBytes.length <= 2) {
            LogUtils.e("===获取MAC1失败===" + Datautils.byteArrayToString(resultBytes));
            return new CardBackBean(ReturnVal.CAD_READ, 0);
        }
        tCardOpDU.ucPsamATC = Datautils.cutBytes(resultBytes, 0, 4);
        tCardOpDU.ucMAC1 = Datautils.cutBytes(resultBytes, 4, 4);

        if (tCardOpDU.ucCAPP == 1) {
            //80dc
            byte[] concatAll = Datautils.concatAll(new byte[]{(byte) 0x80, (byte) 0xDC
                            , (byte) 0x00, (byte) 0xF0, (byte) 0x30, tCardOpDU.ucTradeType
                            , (byte) 0x00, (byte) 0x00}
                    , tCardOpDU.ucPOSSnr
                    , new byte[]{(byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00
                            , (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}
                    , Datautils.intToByteArray1(tCardOpDU.ulTradeValue)
                    , Datautils.intToByteArray1(tCardOpDU.ulBalance - tCardOpDU.ulTradeValue)
                    , tCardOpDU.ucDateTime
                    , CardMethods.ucCityCodeII
                    , CardMethods.ucShouLiCodeII
                    , new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
                            , (byte) 0x00, (byte) 0x00});

            resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
                    , concatAll);
            if (resultBytes == null || resultBytes.length == 2) {
                LogUtils.e("===更新1E文件(80dc)error===" + Datautils.byteArrayToString(resultBytes));
                return new CardBackBean(ReturnVal.CAD_READ, 0);
            }
            LogUtils.d("===更新1E文件return===" + Datautils.byteArrayToString(resultBytes));
        }
        byte[] cmd = CardMethods.getIcPurchase(tCardOpDU);
        LogUtils.d("===IC卡(8054)消费send===" + Datautils.byteArrayToString(cmd));
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC, cmd);
        if (Arrays.equals(resultBytes, CardMethods.APDU_8054_FAILE)) {
            return new CardBackBean(ReturnVal.CAD_MAC1, 0);
        }
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===IC卡(8054)消费error===" + Datautils.byteArrayToString(resultBytes));
            fSysSta = true;
            problemIssueCode = Datautils.concatAll(tCardOpDU.ucAppSnr, tCardOpDU.uiOffLineCount,
                    tCardOpDU.lPurSubByte, tCardOpDU.ulBalanceByte, new byte[]{tCardOpDU.ucCAPP});
            return new CardBackBean(ReturnVal.CAD_RETRY, 0);
        }

        LogUtils.d("===IC卡(8054)消费返回===" + Datautils.byteArrayToString(resultBytes));
        tCardOpDU.ucTAC = Datautils.cutBytes(resultBytes, 0, 4);
        tCardOpDU.ucMAC2 = Datautils.cutBytes(resultBytes, 4, 4);
        byte[] psamCheckMac2 = CardMethods.checkPsamMac2(tCardOpDU.ucMAC2);
        LogUtils.d("===psam卡 8072校验 send===: " + Datautils.byteArrayToString(psamCheckMac2));
        resultBytes = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PSAM1_APDU, psamCheckMac2);
        if (resultBytes == null || resultBytes.length == 2) {
            LogUtils.e("===psam卡(8072)校验error===");
            return new CardBackBean(ReturnVal.CAD_READ, 0);
        }
        LogUtils.d("===psam卡 8072校验返回===: " + Datautils.byteArrayToString(resultBytes));
        PlaySound.play(PlaySound.xiaofeiSuccse, 0);
        return new CardBackBean(ReturnVal.CAD_OK, tCardOpDU.ulBalance);
    }
}
