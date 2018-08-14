package com.wpos.sdkdemo.bean;

/**
 * Created by wangpos on 2017/5/27.
 */

public class TradeInfo {
    private static TradeInfo card = null;
    private boolean isOnLine;// 是否线上交易
    private int tradeType;//交易类型
    private int serialNo;//流水号
    private int cardType;//卡类型
    public String pin;//PIN数据AN16
    public String id;//主账号 N..19
    public String cardSerialNumber;//卡片序列号 N3
    public String magneticCardData2;//磁道2数据
    public String magneticCardData3;//磁道3数据
    public String icCardData;//IC卡数据域
    private String posInputType;//卡输入方式.
    public String validityPeriod;//卡有效期
    public String exCardInfo;
    private long amount;
    private String terminalNo;
    private String merchantNo;
    // IC 卡交易结果
    private int ICResult;
    byte[] aid;
    int aidLen;
    private byte[] appCrypt = new byte[8];//应用密文
    private byte[]    TVR = new byte[5];                                     //TAG95   终端验证结果TVR 5
    private byte[]    TSI = new byte[2];                                     //TAG9B   TSI  2
    private byte[]    ATC = new byte[2];                                     //TAG9F36 应用交易序号   ATC 2
    private byte[]    CVM = new byte[3];                                     //TAG9F34 持卡人验证方法(CVM)结果 CVM 3
    private byte[]    aucAppLabel =new byte[16];                             //TAG50   应用标签  16
    private int     aucAppLabelLen ;                                         //应用标签长度
    private byte[]    aucAppPreferName = new byte[16];                       //TAG9F12 应用首选名 16
    private int     aucAppPreferNameLen;                                     //首选名称长度
    private byte[]    aucUnPredNum = new byte[4];                            //TAG9F37 不可预知数字  4
    private byte[]    aucAIP = new byte[2];                                  //TAG82   应用交互特征   AIP 2
    private byte[]    aucCVR = new byte[7];                                  //TAG9F10 发卡行应用数据 IAD 7

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

    public byte[] getTVR() {
        return TVR;
    }

    public byte[] getTSI() {
        return TSI;
    }

    public byte[] getATC() {
        return ATC;
    }

    public byte[] getCVM() {
        return CVM;
    }

    public int getICResult() {
        return ICResult;
    }

    public void setICResult(int ICResult) {
        this.ICResult = ICResult;
    }

    public byte[] getAucAppLabel() {
        return aucAppLabel;
    }

    public void setAucAppLabel(byte[] aucAppLabel) {
        this.aucAppLabel = aucAppLabel;
    }

    public int getAucAppLabelLen() {
        return aucAppLabelLen;
    }

    public void setAucAppLabelLen(int aucAppLabelLen) {
        this.aucAppLabelLen = aucAppLabelLen;
    }

    public byte[] getAucAppPreferName() {
        return aucAppPreferName;
    }

    public void setAucAppPreferName(byte[] aucAppPreferName) {
        this.aucAppPreferName = aucAppPreferName;
    }

    public int getAucAppPreferNameLen() {
        return aucAppPreferNameLen;
    }

    public void setAucAppPreferNameLen(int aucAppPreferNameLen) {
        this.aucAppPreferNameLen = aucAppPreferNameLen;
    }

    public byte[] getAucUnPredNum() {
        return aucUnPredNum;
    }

    public void setAucUnPredNum(byte[] aucUnPredNum) {
        this.aucUnPredNum = aucUnPredNum;
    }

    public byte[] getAucAIP() {
        return aucAIP;
    }

    public void setAucAIP(byte[] aucAIP) {
        this.aucAIP = aucAIP;
    }

    public byte[] getAucCVR() {
        return aucCVR;
    }

    public void setAucCVR(byte[] aucCVR) {
        this.aucCVR = aucCVR;
    }

    public String toString(){
    	return "tradeType="+tradeType+"\n"+"cardType="+cardType+"\n"+"serialNo="+serialNo+"\n"+"pin="+pin+"\n"+"id="+id+"\n"
    			+"cardSerialNumber="+cardSerialNumber+"\n"+"magneticCardData2="+magneticCardData2+"\n"
    			+"magneticCardData3="+magneticCardData3+"\n"+"icCardData="+icCardData+"\n"+"posInputType="+posInputType+"\n"
    			+"validityPeriod="+validityPeriod+"\n"+"exCardInfo="+exCardInfo;
    }

    public void init(){
        this.isOnLine = false;
        this.tradeType = 0;
        this.serialNo = 0;
        this.cardType = -1;
        this.pin = null;
        this.id = null;
        this.cardSerialNumber = null;
        this.magneticCardData2 = null;
        this.magneticCardData3 = null;
        this.icCardData = null;
        this.posInputType = null;
        this.validityPeriod = null;
        this.exCardInfo = null;
        this.amount = 0;
        this.terminalNo = null;
        this.merchantNo = null;
        this.ICResult = 0;
        this.aid = null;
        this.aidLen = 0;
        this.appCrypt = new byte[8];
        this.TVR = new byte[5];
        this.TSI = new byte[2];
        this.ATC = new byte[2];
        this.CVM = new byte[3];
        this.aucAppLabel =new byte[16];
        this.aucAppLabelLen = 0;
        this.aucAppPreferName = new byte[16];
        this.aucAppPreferNameLen = 0;
        this.aucUnPredNum = new byte[4];
        this.aucAIP = new byte[2];
        this.aucCVR = new byte[7];
    }

    public static TradeInfo getInstance(){
        if (card == null) {
            card = new TradeInfo();
        }
        return card;
    }

    public int getTradeType() {
        return tradeType;
    }
    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }
    public int getCardType() {
        return cardType;
    }
    public void setCardType(int cardType) {
        this.cardType = cardType;
    }
    public String getPin() {
        return pin;
    }
    public void setPin(String pin) {
        this.pin = pin;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCardSerialNumber() {
        return cardSerialNumber;
    }
    public void setCardSerialNumber(String cardSerialNumber) {
        this.cardSerialNumber = cardSerialNumber;
    }
    public String getMagneticCardData2() {
        return magneticCardData2;
    }
    public void setMagneticCardData2(String magneticCardData2) {
        this.magneticCardData2 = magneticCardData2;
    }
    public String getMagneticCardData3() {
        return magneticCardData3;
    }
    public void setMagneticCardData3(String magneticCardData3) {
        this.magneticCardData3 = magneticCardData3;
    }
    public String getIcCardData() {
        return icCardData;
    }
    public void setIcCardData(String icCardData) {
        this.icCardData = icCardData;
    }
    public String getPosInputType() {
        return posInputType;
    }
    public void setPosInputType(String posInputType) {
        this.posInputType = posInputType;
    }
    public String getValidityPeriod() {
        return validityPeriod;
    }
    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }
    public String getExCardInfo() {
        return exCardInfo;
    }
    public void setExCardInfo(String exCardInfo) {
        this.exCardInfo = exCardInfo;
    }
    public byte[] getAppCrypt() {
        return appCrypt;
    }
    public void setAppCrypt(byte[] appCrypt) {
        this.appCrypt = appCrypt;
    }
    public long getAmount() {
        return amount;
    }
    public void setAmount(long amount) {
        this.amount = amount;
    }
    public String getTerminalNo() {
        return terminalNo;
    }
    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }
    public String getMerchantNo() {
        return merchantNo;
    }
    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }
    public boolean isOnLine() {
        return isOnLine;
    }
    public void setOnLine(boolean onLine) {
        isOnLine = onLine;
    }
    public int getSerialNo() {
        return serialNo;
    }
    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * 交易类型
     */
    public static final int Type_OffLine_Sale=99;//脱机
    private static final int Base = 100;//联机
    /** 消费 */
    public static final int Type_Sale = Base + 1;
    /** 消费撤销 */
    public static final int Type_Void = Base + 2;
    /** 退货 */
    public static final int Type_Refund = Base + 3;
    /** 预授权完成 */
    public static final int Type_AuthComplete = Base + 4;
    /** 预授权完成撤销 */
    public static final int Type_CompleteVoid = Base + 5;
    public static final int Type_QueryBalance = Base +11;

    // TODO 预留 6~20
    //积分消费冲正
    public static final int Type_IntegralUndoDownVoid = Base + 6;

    public static final int Type_IntegralUndoDown = Base + 7;

    public static final int Type_HelpFarmersunDown = Base + 8;
    public static final int Type_CoilingSale = Base + 10;//圈存
    //积分消费撤消
    public  static final int Type_IntegralUndo = Base + 9;

    /** 预授权 */
    public static final int Type_Auth = Base + 21;
    /** 预授权撤销 */
    public static final int Type_Cancel = Base + 22;
    /** 微信离线支付（主扫） */
    public static final int Type_WXOfflinePay = Base + 23;
    // TODO 24 微信被扫
    /** 微信退款 */
    public static final int Type_WXVoid = Base + 25;
    /** 微信退货 */
    public static final int Type_WXRefund = Base + 26;
    /** 支付宝离线支付（主扫） */
    public static final int Type_ZFBOfflinePay = Base + 27;
    // TODO 28 支付宝被扫
    /** 支付宝退款*/
    public static final int Type_ZFBVoid = Base + 29;
    /** 支付宝退货 */
    public static final int Type_ZFBRefund = Base + 30;
    /** 百度钱包支付 */
    public static final int Type_BDOfflinePay = Base + 31;
    /** 百度钱包退款 */
    public static final int Type_BDVoid = Base + 32;
    /** 百度钱包退货 */
    public static final int Type_BDRefund = Base + 33;
    /** 通联钱包消费 */
    public static final int Type_TLWalletSale = Base + 34;
    /** 通联钱包消费撤销 */
    public static final int Type_TLWalletVoid = Base + 35;
    /** 优惠券消费 */
    public static final int Type_CouponSale = Base + 36;
    /** 优惠券撤销 */
    public static final int Type_CouponVoid = Base + 37;
    /** 优惠券退货 */
    public static final int Type_CouponRefund = Base + 38;
    /** 积分消费 */
    public static final int Type_PointSale = Base + 39;
    /** 积分撤销 */
    public static final int Type_PointVoid = Base + 40;
    /** 积分退货 */
    public static final int Type_PointRefund = Base + 41;
    /** 电子票消费 */
    public static final int Type_ETicketSale = Base + 42;
    /** 电子票撤销 */
    public static final int Type_ETicketVoid = Base + 43;
    /** 电子票退货 */
    public static final int Type_ETicketRefund = Base + 44;
    /** 卡券查询 */
    public static final int Type_KaQuanQuery = Base + 45;
    /** 卡券核销 */
    public static final int Type_KaQuanHeXiao = Base + 46;
    /**
     * 电子现金
     */
    public static final int Type_DianZiXianJin = Base + 47;
    public static final int Type_ScanSale= Base + 90;
    public static final int Type_ScanVoid = Base + 91;
    public static final int Type_ScanRefund = Base + 92;
    /**
     * 转账
     */
    public static final int Type_HelpFarmersTransfer = Base + 93;
    //助农现金汇kuan

    public static final int Type_doHelpFarmersCashTransfer = Base + 94;
    //BindAccTransfer = 54;// 绑定账户汇款
    public static final int Type_BindAccTransfer = Base + 95;
    public static final int Type_CrossLineTransfer =   Base+96;//绑定账户汇款
    public static final int Type_LineTransfer = Base+97;//行内汇款
    public static final int Type_doLinePayment=Base+98;// 助农转账
    public static final int Type_doPOSFinancialTransfer=Base+99;//POS理财账户转账;a
    public static final int Type_doTeleRecharge=Base+100;//电信充值
    public static final int Type_loanLending = Base+101;//自助放款
    public static final int Type_loanRepayment = Base+102;//自助还款


    /**
     * 卡类型  -
     *
     */
    public static final int TRANS_CARDTYPE_INTERNAL	 =	   0 ;   //内卡
    public static final int TRANS_CARDTYPE_VISA		=	   1  ;  //威士卡   VISA
    public static final int TRANS_CARDTYPE_MASTER	=	   2   ; //万事达卡 MasterCard
    public static final int TRANS_CARDTYPE_JCB		=	   3    ;//JCB卡    JCB
    public static final int TRANS_CARDTYPE_DINNER	=	   4    ;//大来卡   Dinner Club
    public static final int TRANS_CARDTYPE_AE		=	   5    ;//运通卡   American Express
    public static final int TRANS_CARDTYPE_FOREIGN	=	   6    ;//外卡
    public static final int TRANS_CARDTYPE_INTERNALSMART =  10   ;//

    public static final int OFFLINEAPPROVED       = 0x01    ;    //脱机成功
    public static final int OFFLINEDECLINED     = 0x02       ;    //脱机拒绝
    public static final int ONLINEAPPROVED      = 0x03       ;    //联机成功
    public static final int ONLINEDECLINED     = 0x04        ;    //联机拒绝
    public static final int UNABLEONLINE_OFFLINEAPPROVED= 0x05;    //联机失败 脱机成功
    public static final int UNABLEONINE_OFFLINEDECLINED= 0x06 ;    //联机失败 脱机拒绝


    public static final int SUCCESS = 0x00;// 成功结果
    public static final int CANCEL = 0x01;// 成功结果
    public static final int ERR_END = 0x02;// 错误
    /**
     * ic 卡 处理结果
     */
    //联机返回码处理
    public static final int ONLINE_APPROVE    = 0x00,      //联机批准(发卡行批准交易)
            ONLINE_FAILED     = 0x01,      //联机失败
            ONLINE_REFER      = 0x02,      //联机参考(发卡行参考)
            ONLINE_DENIAL     = 0x03,      //联机拒绝(发卡行拒绝交易)
            ONLINE_ABORT      = 0x04,      //终止交易

    //处理发卡行发起的参考
    REFER_APPROVE     = 0x01,      //参考返回码(选择批准) 接受交易
            REFER_DENIAL      = 0x02;     //参考返回码(选择拒绝) 拒绝交易



    /**
     * emvCore 交易结果
     */
    public static final String ARC_OFFLINEAPPROVED       =    "Y1";   //脱机成功
    public static final String ARC_OFFLINEDECLINED       =    "Z1";   //脱机拒绝
    public static final String ARC_REFERRALAPPROVED      =    "Y2";   //
    public static final String ARC_REFERRALDECLINED      =    "Z2";   //
    public static final String ARC_ONLINEFAILOFFLINEAPPROVED ="Y3";   //联机失败 脱机成功
    public static final String ARC_ONLINEFAILOFFLINEDECLINED ="Z3";   //联机失败 脱机拒绝

    /**
     * emvCore 交易结果
     */

    /**
     * app path
     */
    public static final int PATH_PBOC  = 0x00;    //应用路径：标准PBOC
    public static final int PATH_QPBOC = 0x01;     //应用路径：qPBOC
    public static final int PATH_MSD   = 0x02;     //应用路径：MSD
    public static final int PATH_ECash = 0x03;     //应用路径：电子现金
}
