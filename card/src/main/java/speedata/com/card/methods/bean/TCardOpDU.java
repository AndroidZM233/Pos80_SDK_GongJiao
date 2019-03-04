package speedata.com.tianjin.methods.bean;

/**
 * Created by 张明_ on 2019/2/19.
 * Email 741183142@qq.com
 */
public class TCardOpDU {
    public byte[] ucDateTime = new byte[8];
    public byte ucRcdType;
    public byte ucCardClass;
    public byte ucCAPP;
    public byte ucProcSec;
    //CPU
    private byte[] ucIssuerCode = new byte[2];//0,1
    private byte[] ucCityCode = new byte[2];//2,3
    private byte[] ucVocCode = new byte[2];//4,5
    private byte[] ucRfu1 = new byte[2];//6,7
    private byte ucAppTypeFlag;//8
    private byte ucAppVer; //9
    private byte[] ucAppSnr = new byte[10];//10-19
    private byte[] ucAppStartDate = new byte[4];//20-23
    private byte[] ucAppEndDate = new byte[4];//24-27
    private byte[] ucRfu2 = new byte[2];//28-29

    private byte ucMainCardType;//30
    private byte ucSubCardType;//31
    private byte ucCardAppFlag;//32
    private byte[] ucCheckDate = new byte[4];//33-36
    private byte ucAppStartFlag;//37
    private byte ucRadioInCard; //38
    private char uiValidDays; //39-40
    private byte ucTimeLimitInCard;//41
    private byte[] ucRfu3 = new byte[6];//42-47
    /////////////////////////////////////////////////////JT
//Mifare
    private byte[] ucBlk5 = new byte[16];
    private byte[] ucBlk6 = new byte[16];

    private byte ucfBLACK;
    private byte ucPurStartFlag;
    private byte[] ucIssueDate = new byte[4];
    private byte ucBlackCard;
    private byte[] PurIncApprovalNr = new byte[6];// 79-84: PurIncDev4+PurIncCount2
    private byte[] YueIncApprovalNr = new byte[6];// 85-90: YueIncDev4+YueIncCount2

    /////////////////////////////////////////////
    private byte ucCardTicket;
    private byte ucCardRadioP;
    private byte fToCheck;//年检
    private byte fPermit;//带人
    private byte ucTickCnt;
    private byte fInBus;

    private byte[] ucIncPurDev = new byte[4];
    private char uiIncPurCount;
    private byte[] ucIncPurDate = new byte[4];

    private int lPurOriMoney;
    private int lPurSub;
    private char uiPurCount;

    private byte fYueAsPur;
    private byte ucYueSec;
    private byte ucYueFlag;
    private byte[] ucIncYueDev = new byte[4];
    private char uiIncYueCount;
    private byte[] ucYueUsingDate = new byte[3];
    private byte ucYuePosition;
    private int ulYueBase;
    private int[] ulYueBase2 = new int[4];

    private int lYueOriMoney;
    private int lActYueOriMoney;
    private int lYueSub;
    private int lActYueSub;
    private char uiYueCount;

    private byte ucOtherCity;
    private byte fUsePSAM;
    private byte ucPSAMPOS;
    private byte[] ucSafeAuthCode = new byte[9];
    private byte ucDiv;
    private byte ucKeyID;


    private int ulTradeValue;
    private int ulBalance;
    private char uiOffLineCount;
    private byte ucKeyVer;
    private byte ucKeyAlg;
    private byte[] ucPOSSnr = new byte[6];
    private int ulPOSTradeCount;
    private byte[] ucRandom = new byte[4];
    private byte[] ucMAC1 = new byte[4];
    private byte[] ucMAC2 = new byte[4];
    private byte[] ucTAC = new byte[4];

    private byte[] ucDatToCard = new byte[150];
    private byte[] ucDatInCard = new byte[150];
    private byte[] ucFKDevNr = new byte[3];
    private byte[] ucFKDriSnr = new byte[4];
    private byte[] ucFKCorNr = new byte[4];
    private byte[] ucFKLineNr = new byte[3];
    private byte[] ucFKBusNr = new byte[3];
    private byte fErr;
    //Use for Record
    private byte[] ucRcdCorNr = new byte[4];
    private byte[] ucRcdLineNr = new byte[4];
    private byte[] ucRcdBusNr = new byte[4];
    private byte[] ucRcdDevNr = new byte[4];
    private byte ucRcdStopIDUp;
    private byte ucRcdStopIDDn;
    //PBOC
    private char uiATC;
    private byte ucValue5F34;
    //JTB
    private byte ucSimple;
    private byte ucLastTradeEnd;
    private byte ucTradeType;
    private int ulFKPurSub;

    //Other City
    private byte fUseHC;//天津
    private byte fHCSeg;
    private byte fHC;
    private byte ucHCRadioP;

    private int HCQYueSub;
    private int HCQPurSub;
    private byte ucHCRcdValid;
    private int ulHCCadUTC;
    private byte fHCInCard;
    private byte fHCTradeMode;
    private byte[] HCPsamNr = new byte[4];
    private byte fHCMode;

    private byte First;
    private byte YueSec;

    private byte[] ucYueStartDate = new byte[4];
    private byte[] ucYueEndDate = new byte[4];

    private byte DBYueFlag;

    //  uint8_t fTimeLimit;
    private int ulHCUTC;
    private char uiHCDUTC;
    private int ulHCStartUTC;
    private int ulHCOriMoney;
    private int ulHCSub;
    private char uiHCTimes;
    private byte[] ucHCBusNr = new byte[3];
    private byte[] ucHCPsamNevNo = new byte[6];
    private byte[] ucHCLineNr = new byte[2];
    private byte ucHCRcdType;
    private char uiHCPrice;
    private byte ucHCBusType;

    private byte ucCardVer;


}
