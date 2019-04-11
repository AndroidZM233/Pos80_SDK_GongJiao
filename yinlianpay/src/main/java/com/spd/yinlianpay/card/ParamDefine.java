package com.spd.yinlianpay.card;

/**
 * Created by zhangning on 2017/12/27.
 */

public class ParamDefine {

    public static final int MAX_TORN_NUM = 5;
    // the source of data elements
    public static final int CLSS_SRC_ICC = 0;  //ICC
    public static final int CLSS_SRC_TM = 1;  //terminal
    public static final int CLSS_SRC_ISS = 2;  //issuer

    public static final int CLSS_TAG_NOT_EXIST = 0;//Tag is present
    public static final int CLSS_TAG_EXIST_WITHVAL = 1;//Tag is present and not empty
    public static final int CLSS_TAG_EXIST_NOVAL = 2;//Tag is present but empty

    public static final int CLSS_MAX_TAG_LENGTH = 4;//three byte is using now, byte 4 is for future use



    public static final int EMV_OK = 0;         //成功
    public static final int ICC_RESET_ERR = -1;         //IC卡复位失败
    public static final int ICC_CMD_ERR = -2;         //IC命令失败
    public static final int ICC_BLOCK = -3;         //IC卡锁卡

    public static final int EMV_RSP_ERR = -4;         //IC返回码错误
    public static final int EMV_APP_BLOCK = -5;         //应用已锁
    public static final int EMV_NO_APP = -6;         //卡片里没有EMV应用
    public static final int EMV_USER_CANCEL = -7;         //用户取消当前操作或交易
    public static final int EMV_TIME_OUT = -8;         //用户操作超时
    public static final int EMV_DATA_ERR = -9;         //卡片数据错误
    public static final int EMV_NOT_ACCEPT = -10;        //交易不接受
    public static final int EMV_DENIAL = -11;        //交易被拒绝
    public static final int EMV_KEY_EXP = -12;        //密钥过期

    //回调函数或其他函数返回码定义
    public static final int EMV_NO_PINPAD = -13;        //没有密码键盘或键盘不可用
    public static final int EMV_NO_PASSWORD = -14;        //没有密码或用户忽略了密码输入
    public static final int EMV_SUM_ERR = -15;        //认证中心密钥校验和错误
    public static final int EMV_NOT_FOUND = -16;        //没有找到指定的数据或元素
    public static final int EMV_NO_DATA = -17;        //指定的数据元素没有数据
    public static final int EMV_OVERFLOW = -18;        //内存溢出

    public static final int NO_TRANS_LOG = -19;
    public static final int RECORD_NOTEXIST = -20;
    public static final int LOGITEM_NOTEXIST = -21;
    public static final int ICC_RSP_6985 = -22;

    public static final int EMV_PARAM_ERR = -30;        // 20081008 liuxl


    public static final int CLSS_USE_CONTACT = -23;    // 必须使用其他界面进行交易
    public static final int EMV_FILE_ERR = -24;
    public static final int CLSS_TERMINATE = -25;    // 应终止交易       -25
    public static final int CLSS_FAILED = -26;    // 交易失败 20081217
    public static final int CLSS_DECLINE = -27;
    public static final int CLSS_PARAM_ERR = -30; // -26 // 因EMV 内核中的参数错误定义为-30
    public static final int CLSS_WAVE2_OVERSEA = -31;  // 20090418 for visa wave2 trans
    public static final int CLSS_WAVE2_TERMINATED = -32; // 20090421 for wave2 DDA response TLV format error
    public static final int CLSS_WAVE2_US_CARD = -33;  // 20090418 for visa wave2 trans
    public static final int CLSS_WAVE3_INS_CARD = -34; // 20090427 FOR VISA L3
    public static final int CLSS_RESELECT_APP = -35;
    public static final int CLSS_CARD_EXPIRED = -36; // liuxl 20091104 for qPBOC spec updated

    public static final int EMV_NO_APP_PPSE_ERR = -37;

    public static final byte[] MSG_HOLD_TIME = new byte[]{0x00,0x00,0x13};
    public static final int MAX_TRANS_NUM = 8;

    //Outcome Parameter Set
    public static final int OC_APPROVED = 0x10;
    public static final int OC_DECLINED = 0x20;
    public static final int OC_ONLINE_REQUEST = 0x30;
    public static final int OC_END_APPLICATION = 0x40;
    public static final int OC_SELECT_NEXT = 0x50;
    public static final int OC_TRY_ANOTHER_INTERFACE = 0x60;
    public static final int OC_TRY_AGAIN = 0x70;
    public static final int OC_NA = 0xF0;
    public static final int OC_A = 0x00;
    public static final int OC_B = 0x10;
    public static final int OC_C = 0x20;
    public static final int OC_D = 0x30;

    public static String getOutCome(int outcomevale)
    {
        switch (outcomevale)
        {
            case OC_APPROVED:
                return "APPROVED";
            case OC_DECLINED:
                return "DECLINED";
            case OC_ONLINE_REQUEST:
                return "ONLINE REQUEST";
            case OC_END_APPLICATION:
                return "END APPLICATION";
            case OC_SELECT_NEXT:
                return "SELECT NEXT";
            case OC_TRY_ANOTHER_INTERFACE:
                return "TRY ANOTHER INTERFACE";
            case OC_TRY_AGAIN:
                return "TRY AGAIN";
            case OC_NA:
                return "N/A";
        }
        return "";
    }


    public static final int CLSS_CARD_DATA_MISSING = 0x01;
    public static final int CLSS_PARSING_ERROR = 0x02;
    public static final int CLSS_CARD_DATA_ERROR = 0x03;
    public static final int CLSS_MAGSTRIPE_NOT_SUPPORTED = 0x04;
    public static final int CLSS_MSG_DECLINED = 0x05;
    public static final int CLSS_SEE_DEVICE = 0x06;
    public static final int CLSS_CLEAR_DISPLAY = 0x10;
    public static final int CLSS_IDSDATAERROR = 0x11;
    public static final int CLSS_IDSNOMATCHINGAC = 0x12;



    public static final int CLSS_PATH_NORMAL = 0;
    public static final int CLSS_VISA_MSD = 1;   // scheme_visa_msd_20
    public static final int CLSS_VISA_QVSDC = 2;   // scheme_visa_wave3
    public static final int CLSS_VISA_VSDC = 3;   // scheme_visa_full_vsdc
    public static final int CLSS_VISA_CONTACT = 4;

    public static final int CLSS_MC_MAG = 5;
    public static final int CLSS_MC_MCHIP = 6;

    public static final int CLSS_BAL_BEFORE_GAC = 0x10;//Balance Read Before Gen AC
    public static final int CLSS_BAL_AFTER_GAC = 0x20;//Balance Read After Gen AC

    public static final int KERNTYPE_DEF = 0;
    public static final int KERNTYPE_JCB = 1;
    public static final int KERNTYPE_MC = 2;
    public static final int KERNTYPE_VIS = 3;
    public static final int KERNTYPE_PBOC = 4;
    public static final int KERNTYPE_AE = 5;
    public static final int KERNTYPE_ZIP = 6;
    public static final int KERNTYPE_FLASH = 7;
    public static final int KERNTYPE_RFU = 0xFF;

    public static final int RD_CVM_NO = 0x00;//no CVM
    public static final int RD_CVM_SIG = 0x10;//signature
    public static final int RD_CVM_ONLINE_PIN = 0x11;//online PIN
    public static final int RD_CVM_OFFLINE_PIN = 0x12;//offline PIN
    public static final int RD_CVM_CONSUMER_DEVICE = 0x1F;//Refer to consumer device

    public static final int CLSS_DELETE_TORN = 0x01;
    public static final int CLSS_SAVE_TORN = 0x02;


    public static final int AC_AAC = 0x00;
    public static final int AC_TC = 0x01;
    public static final int AC_ARQC = 0x02;
    public static final int TXN_NO = 0x00;
    public static final int TXN_YES = 0x01;




    //DF8115 L1
    public static final int L1_OK = 0x00;
    public static final int L1_TIME_OUT_ERROR = 0x01;
    public static final int L1_TRANSMISSION_ERROR = 0x02;
    public static final int L1_PROTOCOL_ERROR = 0x03;

    //DF8115 L2
    public static final int L2_OK = 0x00;
    public static final int L2_CARD_DATA_MISSING = 0x01;
    public static final int L2_CAM_FAILED = 0x02;
    public static final int L2_STATUS_BYTES = 0x03;
    public static final int L2_PARSING_ERROR = 0x04;
    public static final int L2_MAX_LIMIT_EXCEEDED = 0x05;
    public static final int L2_CARD_DATA_ERROR = 0x06;
    public static final int L2_MAGSTRIPE_NOT_SUPPORTED = 0x07;
    public static final int L2_NO_PPSE = 0x08;
    public static final int L2_PPSE_FAULT = 0x09;
    public static final int L2_EMPTY_CANDIDATE_LIST = 0x0A;
    public static final int L2_IDS_READ_ERROR = 0x0B;
    public static final int L2_IDS_WRITE_ERROR = 0x0C;
    public static final int L2_IDS_DATA_ERROR = 0x0D;
    public static final int L2_IDS_NO_MATCHING_AC = 0x0E;
    public static final int L2_TERMINAL_DATA_ERROR = 0x0F;

    //DF8115 L3
    public static final int L3_OK = 0x00;
    public static final int L3_TIME_OUT = 0x01;
    public static final int L3_STOP = 0x02;
    public static final int L3_AMOUNT_NOT_PRESENT = 0x03;

    //DF8116 message identifier
    public static final int MI_CARD_READ_OK = 0x17;
    public static final int MI_TRY_AGAIN = 0x21;
    public static final int MI_APPROVED = 0x03;
    public static final int MI_APPROVED_SIGN = 0x1A;
    public static final int MI_DECLINED = 0x07;
    public static final int MI_ERROR_OTHER_CARD = 0x1C;
    public static final int MI_INSERT_CARD = 0x1D;
    public static final int MI_SEE_PHONE = 0x20;
    public static final int MI_AUTHORISING_PLEASE_WAIT = 0x1B;
    public static final int MI_CLEAR_DISPLAY = 0x1E;
    public static final int MI_NA = 0xFF;

    public static String getDf8116(int msg)
    {
        switch (msg)
        {
            case  0x17:
              return "CARD READ OK";
            case   0x21:
                return "TRY AGAIN";
            case   0x03:
                return "APPROVED";
            case   0x1A:
                return "TRY AGAIN";
            case   0x07:
                return "DECLINED";
            case   0x1C:
                return "ERROR OTHER CARD";
            case   0x1D:
                return "INSERT CARD";
            case   0x20:
                return "SEE PHONE";
            case   0x1B:
                return "AUTHORISING PLEASE WAIT";
            case   0x1E:
                return "CLEAR DISPLAY";
            default:
                return "";
        }
    }

    //DF8116 STATUS
    public static final int MI_NOT_READY = 0x00;
    public static final int MI_IDLE = 0x01;
    public static final int MI_READY_TO_READ = 0x02;
    public static final int MI_PROCESSING = 0x03;
    public static final int MI_CARD_READ_SUCCESSFULLY = 0x04;
    public static final int MI_PROCESSING_ERROR = 0x05;
    //NA   FF
    //DF8116 VALUE QUALIFIER
    public static final int MI_NONE = 0x00;
    public static final int MI_AMOUNT = 0x01;
    public static final int MI_BALANCE = 0x02;


    public static final int ENGLISH = 49;
    public static final int CHINESE = 50;

    public static final int MODE_ICC = 0x02;
    public static final int MODE_VISA_MAG = 0x90;
    public static final int MODE_VISA_MAG_IC_SUCC = 0x91;
    public static final int MODE_VISA_MAG_IC_FAIL = 0x92; // 默锟斤拷值, 锟斤拷锟斤拷锟斤拷


    public static final int PART_MATCH = 0x00;      //部分匹配
    public static final int FULL_MATCH = 0x01;       //应用选择匹配标志(完全匹配)

    public static final int MAX_APP_REVOCLIST_NUM = 100;//(4*MAX_REVOCLIST_NUM)

    public static final int err = 0;
    public static final int MYOK = 1;

    public static final int T_OCPS = 0x01;//Outcome Parameter Set
    public static final int T_DISD = 0x02;//Discretionary Data
    public static final int T_UIRD = 0x04;//User Interface Request Data
    public static final int T_ERRI = 0x08;//Error Indication

    // add end
    public static final int ALL_AID = 0x00;
    public static final int MCD_AID = 0x01;
    public static final int MESTRO_AID = 0x02;
    public static final int TEST_AID = 0x03;

    public static final int REFER_APPROVE = 0x01;       //
    public static final int REFER_DENIAL = 0x02;       //
    public static final int ONLINE_APPROVE = 0x00;       //
    public static final int ONLINE_FAILED = 0x01;       //
    public static final int ONLINE_REFER = 0x02;       //
    public static final int ONLINE_DENIAL = 0x03;       //
    public static final int ONLINE_ABORT = 0x04;       //
    public static final int CLSS_DECLINED = 0x00;
    public static final int CLSS_APPROVE = 0x01;
    public static final int CLSS_ONLINE_REQUEST = 0x02;
    public static final int CLSS_TYR_ANOTHER_INTERFACE = 0x03;
    public static final int CLSS_END_APPLICATIION = 0x04;


    public static final int INSERTED_ICCARD = 0x50;
    public static final int SWIPED_MAGCARD = 0x51;



    public static byte[] mcd = new byte[]{(byte)0xA0,0x00,0x00,0x00,0x04,0x10,0x10};
    public static byte[] mestro = new byte[]{(byte)0xA0,0x00,0x00,0x00,0x04,0x30,0x60};
    public static byte[] test = new byte[]{(byte)0xB0,0x12,0x34,0x56,0x78};

    public static String IP = "10.30.0.165";
    public static int Port = 8887;
    public static int OverTimeInt = 60;

    public static String getIP() {
        return IP;
    }

    public static void setIP(String IP) {
        ParamDefine.IP = IP;
    }

    public static int getPort() {
        return Port;
    }

    public static void setPort(int port) {
        Port = port;
    }

    public static int getOverTimeInt() {
        return OverTimeInt;
    }

    public static void setOverTimeInt(int overTimeInt) {
        OverTimeInt = overTimeInt;
    }


    public static String BatchNo = "000001";
    public static String TPDU = "6000060000";
    public static String Head = "613100171012";
    public static int TranceNo = 0;
    public static String glOnlinePin = "";

    public static String getBatchNo() {
        return BatchNo;
    }

    public static void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    public static String getTPDU() {
        return TPDU;
    }

    public static void setTPDU(String tpdu) {
        TPDU = tpdu;
    }

    public static String getHead() {
        return Head;
    }

    public static void setHead(String head){
        Head = head;
    }

    public static int getTranceNo() {
        return TranceNo;
    }

    public static void setTranceNo(int tranceNo) {
        TranceNo = tranceNo;
    }

    public static String getGlOnlinePin() {
        return glOnlinePin;
    }

    public static void setGlOnlinePin(String glOnlinePin) {
        ParamDefine.glOnlinePin = glOnlinePin;
    }
}
