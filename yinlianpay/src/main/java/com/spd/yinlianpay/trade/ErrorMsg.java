package com.spd.yinlianpay.trade;

/**
 * Created by 91225 on 2017/8/30.
 */
public class ErrorMsg {


    public static String getEmvError(int emvError) {
        switch (emvError) {
            //EMVLIB返回值定义
            case ErrorMsgType.ERR_EMVRSP:
                return " Response Error ";
            case ErrorMsgType.ERR_APPBLOCK:
                //return "应用已锁";
                return "APPBLOCK";
            case ErrorMsgType.ERR_NOAPP:
                //return "卡片里没有EMV应用";
                return "ERR_NOAPP";
            case ErrorMsgType.ERR_USERCANCEL:
               // return "用户取消当前操作或交易";
                return "USER CANCEL";
            case ErrorMsgType.ERR_TIMEOUT:
                //return "用户操作超时";
                return "TIMEOUT";
            case ErrorMsgType.ERR_EMVDATA:
                return "ERR EMV DATA";
                //return "卡片数据错误";
            case ErrorMsgType.ERR_NOTACCEPT:
                return "NOT ACCEPT";
                //return "交易不接受";
            case ErrorMsgType.ERR_EMVDENIAL:
                //return "交易被拒绝";
                return "DENIAL";
            case ErrorMsgType.ERR_KEYEXP:
                //return "密钥过期";
                return "KEY EXPIRED";
            case ErrorMsgType.ERR_NOPINPAD:
                //return "没有密码键盘或键盘不可用";
                return "NO PINPAD";
            case ErrorMsgType.ERR_NOPIN:
                return "NO PIN";
                //return "没有密码或用户忽略了密码输入";
            case ErrorMsgType.ERR_CAPKCHECKSUM:
                return "ERROR CAPK CHECK SUM";
                //return "认证中心密钥校验和错误";
            case ErrorMsgType.ERR_NOTFOUND:
               // return "没有找到指定的数据或元素";
                return "NOT FOUND";
            case ErrorMsgType.ERR_NODATA:
                //return "指定的数据元素没有数据";
                return "NO DATA";
            case ErrorMsgType.ERR_OVERFLOW:
                //return "内存溢出";
                return "ERROR OVERFLOW";
            case ErrorMsgType.ERR_NOTRANSLOG:
                //return "无交易日志";
                return "ERROR NOT TRANSTION LOG";
            case ErrorMsgType.ERR_NORECORD:
                //return "无记录";
                return "NO RECORD";
            case ErrorMsgType.ERR_NOLOGITEM:
                //return "目志项目错误";
                return "NO LOGITEM";
            case ErrorMsgType.ERR_ICCRESET:
                //return "IC卡复位失败";
                return "ERROR ICC RESERT";
            case ErrorMsgType.ERR_ICCCMD:
                //return "IC命令失败";
                return "ERROR ICC CMD";
            case ErrorMsgType.ERR_ICCBLOCK:
                //return "IC卡锁卡";
                return "ICC BLOCK";
            case ErrorMsgType.ERR_ICCNORECORD:
                //return "IC卡无记录6A83";
                return "ERROR ICC NO RECORD";
            case ErrorMsgType.ERR_GENAC1_6985:
                //return "GENAC命令返回6985";
                return "ERROR GENAC1 6985";
            case ErrorMsgType.ERR_USECONTACT:
                //return "非接失败，改用接触界面";
                return "ERROR USE CONTACT";
            case ErrorMsgType.ERR_APPEXP:
                //return "qPBOC卡应用过期";
                return "QPBOC APP EXPIRER";
            case ErrorMsgType.ERR_BLACKLIST:
                //return "qPBOC黑名单卡";
                return "ERROR BLCAK LIST";
            case ErrorMsgType.ERR_GPORSP:
                //return "errfromGPO";
                return "ERROR GPO RESPONSE";
            case ErrorMsgType.ERR_USEMAG:
               //return "非接中止，改用磁卡";
                return "USR MSG";
            case ErrorMsgType.ERR_TRANSEXCEEDED:
                //return "非接交易超限";
                return "TRANSTION EXCEEDED";
            case ErrorMsgType.ERR_QPBOCFDDAFAIL:
                //return "非接qPBOCfDDA失败";
                return "QPBOC FDDA FAIL";
            case ErrorMsgType.EMV_DATA_EXIST:
                return "EMV exit";
            case ErrorMsgType.EMV_FILE_ERR:
                return "EMV err";
            case ErrorMsgType.EMV_PIN_BLOCK:
                //return "PIN重试次数超限";
                return "EMV PIN BLOCK";
            case 1002:
                return "ERR_TRANSEXCEEDED";
            case ErrorMsgType.ERR_DIALING:
               return  "unknown mistake";
            default:
                return "unknown";
        }

    }
}
