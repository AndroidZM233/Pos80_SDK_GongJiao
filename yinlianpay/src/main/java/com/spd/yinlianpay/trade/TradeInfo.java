package com.spd.yinlianpay.trade;

import com.spd.yinlianpay.iso8583.Msg;
import com.spd.yinlianpay.util.Utils;

import java.io.Serializable;


/**
 * Created by Tommy on 2016/6/12.
 */
public class TradeInfo implements Serializable {
    public Msg msg;
    public static final int Type_OffLine_Sale=99;
    private static final int Base = 100;
    //login
    public static final int Type_Login = Base +110;
    //   0x01现金  0x02购物  0x03服务  0x04反现
    //   0x05查询  0x06支付  0x07管理  0x08转账
    //   0x09退货
    /** 消费 */
    public static final int Type_Sale = 0x02;
    //查询
    public static final int Type_QueryBalance = 0x05;
    /** 退货 */
    public static final int Type_Refund = 0x09;

    /** 消费撤销 */
    public static final int Type_Void = Base + 2;
    /** 预授权完成 */
    public static final int Type_AuthComplete = Base + 4;
    /** 预授权完成撤销 */
    public static final int Type_CompleteVoid = Base + 5;


    // TODO 预留 6~20
    public static final int Type_CoilingSale = Base + 10;//圈存

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
    /** 龙支付 消费 */
    public static final int TYPE_LZF_SALE = Base + 47;
    /** 龙支付 退货 */
    public static final int TYPE_LZF_REFOUND = Base + 58;
    /** 龙支付 查询*/
    public static final int TYPE_LZF_QURETY = Base + 49;
    /** 助农取款*/
    public static final int TYPE_HELP_FARMERS_DRAW = Base + 50;

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

    /**
     * 卡类型  -
     *
     */

    /**
     * ic 卡 处理结果
     */
    public static final int OFFLINEAPPROVED       = 0x01    ;    //脱机成功
    public static final int OFFLINEDECLINED     = 0x02       ;    //脱机拒绝
    public static final int ONLINEAPPROVED      = 0x03       ;    //联机成功
    public static final int ONLINEDECLINED     = 0x04        ;    //联机拒绝
    public static final int UNABLEONLINE_OFFLINEAPPROVED= 0x05;    //联机失败 脱机成功
    public static final int UNABLEONINE_OFFLINEDECLINED= 0x06 ;    //联机失败 脱机拒绝


    public static final int SUCCESS = 0x00;// 成功结果
    public static final int CANCEL = 0x01;// 成功结果


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
    public static final int PATH_MAG   = 0x05;     //应用路径：PayPass Mag
    public static final int PATH_CHIP  = 0x06;     //应用路径：PayPass Chip



    /**
     * app path
     */

    private SettleInfo settleInfo; // 结算数据（如果不为空，则保存到数据库）
    private String merchantName; // 商户名称（基本不变，可不存储到数据库）
    private int tradeType; // 交易类型
    private String cardNo; // 卡号
    private long amount; // 订单（交易）金额
    private long discount = 0; // 优惠金额
    private long returnAmt = 0; // 已退款/货金额
    private String serialNo; // 流水号
    private String time; // 交易时间
    private String date; // 交易日期
    private String expOfDate; // 卡有效期
    private String instiNo; // 受理方机构号
    private String referNo; // 参考号
    private String originReferNo; // 原参考号
    private String authorNo; // 授权码
    private String balance; // 余额（可不存储）
    private String issuer; // 发卡机构
    private String acquirer; // 收单机构
    private String batchNo; // 批次号
    private String originSerialNo; // 原交易流水号
    private String originTsn; // 原交易单号
    private String originAuthorNo; // 原授权码
    private String TSN; // 交易单号（微信/支付宝/通联钱包）
    private String ext46; // 46域扩展
    private String ext47; // 47域扩展
    private String extEx; // 未知扩展
    private String icCardInfo; // Ic卡数据
    private String noteInfo;//备注信息
    private String version;// 版本号
    private String pwdkey;//密钥

   private String tdata;//电子现金脱机消费日期

    public String getTdata() {
        return tdata;
    }

    public void setTdata(String tdata) {
        this.tdata = tdata;
    }

    public String getPwdkey() {
        return pwdkey;
    }

    public void setPwdkey(String pwdkey) {
        this.pwdkey = pwdkey;
    }

    public boolean isOK = false, isNeedQuery = false;
    public String errorCode, errorMsg;

    public String getOriginReferNo() {
        return originReferNo;
    }

    public void setOriginReferNo(String originReferNo) {
        this.originReferNo = originReferNo;
    }

    public String getOriginAuthorNo() {
        return originAuthorNo;
    }

    public void setOriginAuthorNo(String originAuthorNo) {
        this.originAuthorNo = originAuthorNo;
    }

    public String getOriginTsn() {
        return originTsn;
    }

    public void setOriginTsn(String originTsn) {
        this.originTsn = originTsn;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInstiNo() {
        return instiNo;
    }

    public void setInstiNo(String instiNo) {
        this.instiNo = instiNo;
    }

    public String getIcCardInfo() {
        return icCardInfo;
    }

    public void setIcCardInfo(String icCardInfo) {
        this.icCardInfo = icCardInfo;
    }

    public String getTSN() {
        return TSN;
    }

    public void setTSN(String TSN) {
        this.TSN = TSN;
    }

    public String getExtEx() {
        return extEx;
    }

    public void setExtEx(String extEx) {
        this.extEx = extEx;
    }

    public String getAcquirer() {
        return acquirer;
    }

    public void setAcquirer(String acquirer) {
        this.acquirer = acquirer;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getAuthorNo() {
        return authorNo;
    }

    public void setAuthorNo(String authorNo) {
        this.authorNo = authorNo;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDiscount() {
        return discount;
    }

    public void setDiscount(long discount) {
        this.discount = discount;
    }

    public String getExpOfDate() {
        return expOfDate;
    }

    public void setExpOfDate(String expOfDate) {
        this.expOfDate = expOfDate;
    }

    public String getExt46() {
        return ext46;
    }

    public void setExt46(String ext46) {
        this.ext46 = ext46;
    }

    public String getExt47() {
        return ext47;
    }

    public void setExt47(String ext47) {
        this.ext47 = ext47;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getOriginSerialNo() {
        return originSerialNo;
    }

    public void setOriginSerialNo(String originSerialNo) {
        this.originSerialNo = originSerialNo;
    }

    public String getReferNo() {
        return referNo;
    }

    public void setReferNo(String referNo) {
        this.referNo = referNo;
    }

    public long getReturnAmt() {
        return returnAmt;
    }

    public void setReturnAmt(long returnAmt) {
        this.returnAmt = returnAmt;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public SettleInfo getSettleInfo() {
        return settleInfo;
    }

    public void setSettleInfo(SettleInfo settleInfo) {
        this.settleInfo = settleInfo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTradeType() {
        return tradeType;
    }

    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }

    public String getNoteInfo() {
        return noteInfo;
    }

    public void setNoteInfo(String noteInfo) {
        this.noteInfo = noteInfo;
    }

    public static String getTradeString(int tradeType)
    {
        switch (tradeType)
        {
            /** 消费 */
            case  Type_Sale :
                return "Purchase";
            /** 消费撤销 */
            case Type_Void :
                return "消费撤销";
            case Type_QueryBalance:
                return "Balance Inquiry";
            case Type_Refund:
                return "Refund";
            case Type_Auth:
                return "预授权(PREAUTH)";
            case Type_AuthComplete:
                return "预授权完成(AUTH COMPLETE)";
            case Type_Cancel:
                return "预授权撤销(CANCEL)";
            case Type_CompleteVoid:
                return "预授权完成撤销";
            case TYPE_LZF_SALE:
                return "龙支付消费(LZF)";
            case TYPE_LZF_REFOUND:
                return "龙支付退货";
            case TYPE_LZF_QURETY:
                return "龙支付查询";
            case TYPE_HELP_FARMERS_DRAW:
                return "助农取款";

            default:
                return "";
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("settleInfo=").append(settleInfo).append("\n")
                .append("merchantName=").append(merchantName).append(", ")
                .append("tradeType=").append(Utils.getTradeTypeString(tradeType)).append(", ")
                .append("cardNo=").append(cardNo).append(", amount=").append(amount).append(", discount=")
                .append(discount).append(", returnAmt=").append(returnAmt).append(", serialNo=").append(serialNo)
                .append(", time=").append(time).append(", date=").append(date).append(", expOfDate=").append(expOfDate)
                .append(", instiNo=").append(instiNo).append(", referNo=").append(referNo).append(", originReferNo=")
                .append(originReferNo).append(", authorNo=").append(authorNo).append(", balance=").append(balance)
                .append(", issuer=").append(issuer).append(", acquirer=").append(acquirer).append(", batchNo=")
                .append(batchNo).append(", originSerialNo=").append(originSerialNo).append(", originTsn=")
                .append(originTsn).append(", originAuthorNo=").append(originAuthorNo).append(", tsn=").append(TSN)
                .append("\next46=").append(ext46).append("\next47=").append(ext47).append("\nextEx=").append(extEx)
                .append("\nicCardInfo=").append(icCardInfo).append("\nnoteInfo=").append(noteInfo)
                .append("\nversion=").append(version).append("\nisOK=").append(isOK).append(",errorCode=")
                .append(errorCode).append(",errorMsg=").append(errorMsg).append(",isNeedQuery=").append(isNeedQuery);
        return builder.toString();
    }
    public static String castLZFStats(String code)
    {
        switch (code) {
            case "01":
                return "交易失败";
            case "02":
                return "无法确定交易结果";
            case "04":
                return "无法确定交易结果";
            case "TO":
                return "交易超时";
            case "RE":
                return "交易被冲正";
            case "CA":
                return "交易被撤销";
            default:
                return "未知错误";
        }
    }
    public static String castLZFType(int type)
    {
        switch (type) {
            case TYPE_LZF_SALE:
                return "P1DP10008";
            case TYPE_LZF_REFOUND:
                return "P1DP10006";
            case TYPE_LZF_QURETY:
                return "A02915018";
            default:
                return "";
        }
    }
}
