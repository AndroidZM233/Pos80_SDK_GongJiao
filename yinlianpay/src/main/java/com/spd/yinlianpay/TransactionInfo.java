package com.spd.yinlianpay;


import com.spd.yinlianpay.util.PrefUtil;

import java.io.Serializable;

import ui.wangpos.com.utiltool.Util;


public class TransactionInfo implements Serializable {
    public byte[] getAid() {
        return aid;
    }

    public void setAid(byte[] aid) {
        this.aid = aid;
    }

    public int getAidLen() {
        return aidLen;
    }

    public void setAidLen(int aidLen) {
        this.aidLen = aidLen;
    }

    public String getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    private String devicetype;//device type
//    @Id
    String id;
    //流水号
    private String transaceNo;
    //批次号
    private String bacthNo;
    //签名数据
    public byte[] singeData;
    //签名数据的字符串数据
    public String singeDataStr;
    //商户名称
    private String merchantName;
    // 商户号
    private String merchantId;
    // 终端号
    private String termId;
    // 交易卡号
    private String cardNo;
    // 服务点输入方式码（第22域），00：未指明；01：手工；02：磁条卡；05：接触式IC卡；07:非接卡
    private String serviceCode="";
    // 收单行号
    private String acquireNo;
    // 发卡行号
    private String issuerNo;
    // 交易类型
    private int transType= 100;
    // 有效期
    private String expireDate;
    // 授权码
   //@Column(column = COL_AUTH_NO)
    private String authNo;
    // 原授权码
    private String originAuthorNo;

    // 参考号
    private String referNo;

    // 清算日期（应答15域）
//    @Column(column = COL_SETTLE_DATE)
    private String settleDate;

    // 交易日期:YYYYMMDD
//    @Column(column = COL_TRANS_DATE)
    private String transDate;

    // 交易日期:HHMMSS
//    @Column(column = COL_TRANS_TIME)
    private String transTime;

    // 日期时间:yyyy/MM/DD HH:mm:ss
    // @Column(column = COL_DATE_TIME)
    private String dateTime;

    // 卡片序列号
//    @Column(column = COL_CARD_SN)
    private String cardSn;

    // 金额
//    @Column(column = COL_AMOUNT)
    private String amount="1";

    // ic卡数据(TC数据，IC卡交易批准)
//    @Column(column = COL_IC_DATA)
    private String icData;

    public String getTrack1() {
        return track1;
    }

    public void setTrack1(String track1) {
        this.track1 = track1;
    }

    // track1 一磁道 信息 ， 脱机交易使用
    private String track1;
    // track2 二磁道 信息 ， 脱机交易使用
    private String track2;
    // track3 三磁道 信息 ， 脱机交易使用
    private String track3;
    // 是否线上交易
    private boolean isOnLine;

    //53域，脱机交易上送
    private String field53;
    //61域，脱机交易用
    private String field61;
    //63域，脱机交易用
    private String field63;
    // 输入的 pin
    private String pin;
    // pin 输入方式
    private String pinPutCode;
    // pan 输入方式

    private String panPutCode;
    //pin  最大长度 （26 域）
    private String maxPinLen;
    //货币代码 ( 49 域 )
    private String currency;

    // 交易名称
    private String transacionName;

    // 交易英文名
    private String transacionName_EN;

   // 订单号
//    @Column(column = COL_ORDER_ID)
    private String orderId;

    // 原交易流水号
//    @Column(column = COL_OLD_TRACE)
    private String oldTrace;

    // 凭条重打印标志
//    @Column(column = COL_REPRINT)
    private boolean reprint;

    // 订单状态：0-未撤销，1-已撤销
//    @Column(column = COL_STATUS)
    private int status;

    // 终端读取能力和基于PBOC借/贷记标准的IC卡条件代码
//    @Column(column = COL_FIELD60)
    private String field60;


    // 是否已上送（批上送）
//    @Column(column = COL_UPLOAD)
    private boolean upload;

    //脱机上送
    private boolean offlineupload;

    // 支付方式(0-离线支付，1-在线支付)
//    @Column(column = COL_PAY_WAY)
    private int payWay;
    // 签名状态 false  未签名
//    @Column(column = COL_SIGN_STATYUS)
    private boolean isSign;

    // IC 卡交易结果
    private int ICResult;
    // IC条件码
    private String ICCondition;
    // 卡类型
    private int  cardType;
    // 卡组织
    private String cardGroup;
    //电子现金余额
    private String EC_Balance;
    //是否支持电子现金  1 支持  ， 0  不支持
    private int isECTrans;

    public String getOriginAuthorNo() {
        return originAuthorNo;
    }

    public void setOriginAuthorNo(String originAuthorNo) {
        this.originAuthorNo = originAuthorNo;
    }


    public int getIsECTrans() {
        return isECTrans;
    }

    public void setIsECTrans(int isECTrans) {
        this.isECTrans = isECTrans;
    }

    // Aid
    byte[] aid;
    //Aid 长度
    int aidLen;
    private byte    appCrypt[] = new byte[8];                                //应用密文
    private byte    TVR[] = new byte[5];                                     //TAG95   终端验证结果TVR 5
    private byte    TSI[] = new byte[2];                                     //TAG9B   TSI  2
    private byte    ATC[] = new byte[2];                                     //TAG9F36 应用交易序号   ATC 2
    private byte    CVM[] = new byte[3];                                     //TAG9F34 持卡人验证方法(CVM)结果 CVM 3
    private byte    aucAppLabel[] =new byte[16];                             //TAG50   应用标签  16
    private int     aucAppLabelLen ;                                         //应用标签长度
    private byte    aucAppPreferName[] = new byte[16];                       //TAG9F12 应用首选名 16
    private int     aucAppPreferNameLen;                                     //首选名称长度
    private byte    aucUnPredNum[] = new byte[4];                            //TAG9F37 不可预知数字  4
    private byte    aucAIP[] = new byte[2];                                  //TAG82   应用交互特征   AIP 2
    private byte    aucCVR[] = new byte[32];                                  //TAG9F10 发卡行应用数据 IAD

    public String getEC_Balance() {
        return EC_Balance;
    }

    public void setEC_Balance(String EC_Balance) {
        this.EC_Balance = EC_Balance;
    }

    public int getAucAppPreferNameLen() {
        return aucAppPreferNameLen;
    }

    public void setAucAppPreferNameLen(int aucAppPreferNameLen) {
        this.aucAppPreferNameLen = aucAppPreferNameLen;
    }

    public int getAucAppLabelLen() {
        return aucAppLabelLen;
    }

    public void setAucAppLabelLen(int aucAppLabelLen) {
        this.aucAppLabelLen = aucAppLabelLen;
    }

    public byte[] getAppCrypt() {
        return appCrypt;
    }

    public void setAppCrypt(byte[] appCrypt) {
        this.appCrypt = appCrypt;
    }

    public byte[] getCVM() {
        return CVM;
    }

    public byte[] getATC() {
        return ATC;
    }

    public byte[] getTSI() {
        return TSI;
    }

    public byte[] getTVR() {
        return TVR;
    }

    public void setTVR(byte[] TVR) {
        this.TVR = TVR;
    }

    public void setTSI(byte[] TSI) {
        this.TSI = TSI;
    }

    public void setATC(byte[] ATC) {
        this.ATC = ATC;
    }

    public void setCVM(byte[] CVM) {
        this.CVM = CVM;
    }

    public void setAucAppLabel(byte[] aucAppLabel) {
        this.aucAppLabel = aucAppLabel;
    }

    public void setAucAppPreferName(byte[] aucAppPreferName) {
        this.aucAppPreferName = aucAppPreferName;
    }

    public void setAucUnPredNum(byte[] aucUnPredNum) {
        this.aucUnPredNum = aucUnPredNum;
    }

    public void setAucAIP(byte[] aucAIP) {
        this.aucAIP = aucAIP;
    }

    public void setAucCVR(byte[] aucCVR) {
        this.aucCVR = aucCVR;
    }

    public byte[] getAucAppLabel() {
        return aucAppLabel;
    }


    public byte[] getAucAppPreferName() {
        return aucAppPreferName;
    }

    public byte[] getAucUnPredNum() {
        return aucUnPredNum;
    }

    public byte[] getAucAIP() {
        return aucAIP;
    }

    public byte[] getAucCVR() {
        return aucCVR;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getCardGroup() {
        return cardGroup;
    }

    public void setCardGroup(String cardGroup) {
        this.cardGroup = cardGroup;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
    /**
     * @return 流水号
     */
    public String getTransaceNo() {
        if(transaceNo==null || transaceNo.length()<=0)
        {
            transaceNo = Util.to(PrefUtil.getSerialNo(),6);
        }
        return transaceNo;
    }

    /**
     * @param transaceNo 流水号
     */
    public void setTransaceNo(String transaceNo) {
        this.transaceNo = transaceNo;
        //PrefUtil.putSerialNo(Integer.parseInt(transaceNo));
    }

    /**
     * @return 批次号
     */
    public String getBacthNo() {
        if(bacthNo==null || bacthNo.length()<=0)
        {
            bacthNo = PrefUtil.getBatchNo();
        }
        return bacthNo;
    }

    /**
     * @param bacthNo 批次号
     */
    public void setBacthNo(String bacthNo) {
        this.bacthNo = bacthNo;
        PrefUtil.putBatchNo(bacthNo);
    }
    //singeDataStr
    /**
     * @return 签名数据
     */
    public String getsingeDataStr() {
        if(singeDataStr==null || singeDataStr.length()<=0)
        {
            singeDataStr = PrefUtil.getSignDataStr();
        }
        return singeDataStr;
    }

    /**
     * @param singeDataStr 签名数据
     */
    public void setsingeDataStr(String singeDataStr) {
        this.singeDataStr = singeDataStr;
        PrefUtil.setSignDataStr(singeDataStr);
    }



    /**
     * @return 商户名称
     */
    public String getMerchantName() {
        if(merchantName==null || merchantName.length()<=0)
        {
            merchantName = PrefUtil.getMerchantName();
        }
        return merchantName;
    }

    /**
     * @param merchantName 商户名称
     */
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        PrefUtil.putMerchantName(merchantName);
    }

    /**
     * @return 商户号
     */
    public String getMerchantId() {
        if(merchantId ==null || merchantId.length()<0)
        {
            merchantId = PrefUtil.getMerchantNo();
        }
        return merchantId;
    }

    /**
     * @param merchantId 商户号
     */
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
        PrefUtil.putMerchantNo(merchantId);
    }

    /**
     * @return 终端号
     */
    public String getTermId() {
        if(termId==null || termId.length()<=0)
        {
            termId = PrefUtil.getTerminalNo();
        }
        return termId;
    }

    /**
     * @param termId 终端号
     */
    public void setTermId(String termId) {
        this.termId = termId;
        PrefUtil.putTerminalNo(termId);
    }

    /**
     * @return 交易卡号
     */
    public String getCardNo() {
        return cardNo;
    }

    /**
     * @param cardNo 交易卡号
     */
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    /**
     * @return 服务点输入方式码（第22域），00：未指明；01：手工；02：磁条卡；05：接触式IC卡；07:非接卡
     */
    public String getServiceCode() {
        return serviceCode;
    }

    /**
     * @param serviceCode 服务点输入方式码（第22域），00：未指明；01：手工；02：磁条卡；05：接触式IC卡；07:非接卡
     */
    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    /**
     * @return 收单行号
     */
    public String getAcquireNo() {
        return acquireNo;
    }

    /**
     * @param acquireNo 收单行号
     */
    public void setAcquireNo(String acquireNo) {
        this.acquireNo = acquireNo;
    }

    /**
     * @return 发卡行号
     */
    public String getIssuerNo() {
        return issuerNo;
    }

    /**
     * @param issuerNo 发卡行号
     */
    public void setIssuerNo(String issuerNo) {
        this.issuerNo = issuerNo;
    }

    /**
     * @return 交易类型
     */
    public int getTransType() {
        return transType;
    }

    /**
     * @param transType 交易类型
     */
    public void setTransType(int transType) {
        this.transType = transType;
    }

    /**
     * @return 有效期
     */
    public String getExpireDate() {
        return expireDate;
    }

    /**
     * @param expireDate 有效期
     */
    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    /**
     * @return 授权码
     */
    public String getAuthNo() {
        return authNo;
    }

    /**
     * @param authNo 授权码
     */
    public void setAuthNo(String authNo) {
        this.authNo = authNo;
    }

    /**
     * @return 参考号
     */
    public String getReferNo() {
        return referNo;
    }

    /**
     * @param referNo 参考号
     */
    public void setReferNo(String referNo) {
        this.referNo = referNo;
    }

    /**
     * @return 结算日期
     */
    public String getSettleDate() {
        return settleDate;
    }

    /**
     * @param settleDate 结算日期
     */
    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    /**
     * @return 交易日期 YYmmdd
     */
    public String getTransDate() {
        return transDate;
    }

    /**
     * @param transDate 交易日期 YYmmdd
     */
    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    /**
     * @return 交易时间 HHmmss
     */
    public String getTransTime() {
        return transTime;
    }

    /**
     * @param transTime 交易时间 HHmmss
     */
    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    /**
     * @return 日期时间:yyyy/MM/DD HH:mm:ss
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * @param dateTime 日期时间:yyyy/MM/DD HH:mm:ss
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return 卡片序列号
     */
    public String getCardSn() {
        return cardSn;
    }

    /**
     * @param cardSn 卡片序列号
     */
    public void setCardSn(String cardSn) {
        this.cardSn = cardSn;
    }

    /**
     * @return 交易金额
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount 交易金额
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * @return 55 域 IC 卡数据
     */
    public String getIcData() {
        return icData;
    }

    /**
     * @param icData 55 域 IC 卡数据
     */
    public void setIcData(String icData) {
        this.icData = icData;
    }

    /**
     * @return 订单号
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * @param orderId 订单号
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * @return 原交易流水
     */
    public String getOldTrace() {
        return oldTrace;
    }

    /**
     * @param oldTrace 原交易流水
     */
    public void setOldTrace(String oldTrace) {
        this.oldTrace = oldTrace;
    }

    /**
     * @return 是否重打印
     */
    public boolean isReprint() {
        return reprint;
    }

    /**
     * @param reprint 是否重打印
     */
    public void setReprint(boolean reprint) {
        this.reprint = reprint;
    }

    /**
     * @return 订单状态：0-未撤销，1-已撤销
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status 订单状态：0-未撤销，1-已撤销
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return 终端读取能力和基于PBOC借/贷记标准的IC卡条件代码
     */
    public String getField60() {
        return field60;
    }

    /**
     * @param field60 终端读取能力和基于PBOC借/贷记标准的IC卡条件代码
     */
    public void setField60(String field60) {
        this.field60 = field60;
    }


    /**
     * @return 是否已上送（批上送）
     */
    public boolean isUpload() {
        return upload;
    }

    /**
     * @param upload 是否已上送（批上送）
     */
    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    /**
     * @return  支付方式(0-离线支付，1-在线支付)
     */
    public int getPayWay() {
        return payWay;
    }

    /**
     * @param payWay  支付方式(0-离线支付，1-在线支付)
     */
    public void setPayWay(int payWay) {
        this.payWay = payWay;
    }

    /**
     * @return 是否签名
     */
    public boolean isSign() {
        return isSign;
    }

    /**
     * @param sign 是否签名
     */
    public void setSign(boolean sign) {
        isSign = sign;
    }

    /**
     * @return 二磁道信息
     */
    public String getTrack2() {
        return track2;
    }

    /**
     * @param track2 二磁道信息
     */
    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    /**
     * @return 三磁道信息
     */
    public String getTrack3() {
        return track3;
    }

    /**
     * @param track3 三磁道信息
     */
    public void setTrack3(String track3) {
        this.track3 = track3;
    }

    /**
     * @return 53域，脱机交易上送
     */
    public String getField53() {
        return field53;
    }

    /**
     * @param field53 53域，脱机交易上送
     */
    public void setField53(String field53) {
        this.field53 = field53;
    }

    /**
     * @return 61域，脱机交易用
     */
    public String getField61() {
        return field61;
    }

    /**
     * @param field61 61域，脱机交易用
     */
    public void setField61(String field61) {
        this.field61 = field61;
    }

    /**
     * @return 63域，脱机交易用
     */
    public String getField63() {
        return field63;
    }

    /**
     * @param field63 63域，脱机交易用
     */
    public void setField63(String field63) {
        this.field63 = field63;
    }

    /**
     * @return pin 输入方式
     */
    public String getPinPutCode() {
        return pinPutCode;
    }

    /**
     * @param pinPutCode pin 输入方式
     */
    public void setPinPutCode(String pinPutCode) {
        this.pinPutCode = pinPutCode;
    }

    /**
     * @return pan 输入方式
     */
    public String getPanPutCode() {
        return panPutCode;
    }

    /**
     * @param panPutCode 输入方式
     */
    public void setPanPutCode(String panPutCode) {
        this.panPutCode = panPutCode;
    }

    /**
     * @return pin 最大限制 （ 26 域）
     */
    public String getMaxPinLen() {
        return maxPinLen;
    }

    /**
     * @param maxPinLen 最大限制 （ 26 域）
     */
    public void setMaxPinLen(String maxPinLen) {
        this.maxPinLen = maxPinLen;
    }

    /**
     * @return 货币代码
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency 货币代码
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTransacionName() {
        return transacionName;
    }

    public void setTransacionName(String transacionName) {
        this.transacionName = transacionName;
    }

    public String getTransacionName_EN() {
        return transacionName_EN;
    }

    public void setTransacionName_EN(String transacionName_EN) {
        this.transacionName_EN = transacionName_EN;
    }

    /**
     * @return IC 交易结果
     */
    public int getICResult() {
        return ICResult;
    }

    /**
     * @param ICResult IC 交易结果
     */
    public void setICResult(int ICResult) {
        this.ICResult = ICResult;
    }

    /**
     * @return IC 条件码
     */
    public String getICCondition() {
        return ICCondition;
    }

    /**
     * @param ICCondition IC 条件码
     */
    public void setICCondition(String ICCondition) {
        this.ICCondition = ICCondition;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getReprint() {
        return this.reprint;
    }

    public boolean getUpload() {
        return this.upload;
    }

    public boolean getIsSign() {
        return this.isSign;
    }

    public void setIsSign(boolean isSign) {
        this.isSign = isSign;
    }

    /**
     * @return 是否联机交易
     */
    public boolean isOnLine() {
        return isOnLine;
    }

    /**
     * @param onLine 是否是联机交易
     */
    public void setOnLine(boolean onLine) {
        isOnLine = onLine;
    }

}
