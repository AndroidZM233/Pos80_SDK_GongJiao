package com.spd.yinlianpay.comm;

/**
 * Created by SXK on 2017/7/25.
 */

public class Field39Code {
    String codeList[] =
    {
                "00交易成功",
                "01交易失败, 请联系发卡行",
                "02交易失败, 请联系发卡行",
                "03商户未登记",
                "04没收卡, 请联系收单行",
                "05交易失败, 请联系发卡行",
                "06交易失败, 请联系发卡行",
                "07没收卡, 请联系收单行",
                "09交易失败, 请重试",
                "12交易失败, 请重试",
                "13交易金额超限, 请重试",
                "14无效卡号, 请联系发卡行",
                "15此卡不能受理",
                "19交易失败, 请联系发卡行",
                "20交易失败, 请联系发卡行",
                "21交易失败, 请联系发卡行",
                "22操作有误, 请重试",
                "23交易失败, 请联系发卡行",
                "25交易失败, 请联系发卡行",
                "30交易失败, 请重试",
                "31此卡不能受理",
                "33过期卡, 请联系发卡行",
                "34没收卡, 请联系收单行",
                "35没收卡, 请联系收单行",
                "36此卡有误, 请换卡重试",
                "37没收卡, 请联系收单行",
                "38密码错误次数超限",
                "39交易失败, 请联系发卡行",
                "40交易失败, 请联系发卡行",
                "41没收卡, 请联系收单行",
                "42交易失败, 请联系发卡方",
                "43没收卡, 请联系收单行",
                "44交易失败, 请联系发卡行",
                "45请使用芯片",
                "51余额不足, 请查询",
                "52交易失败, 请联系发卡行",
                "53交易失败, 请联系发卡行",
                "54过期卡, 请联系发卡行",
                "55Password error",
                "56交易失败, 请联系发卡行",
                "57交易失败, 请联系发卡行",
                "58终端无效, 请联系收单行或银联",
                "59交易失败, 请联系发卡行",
                "60交易失败, 请联系发卡行",
                "61金额太大",
                "62交易失败, 请联系发卡行",
                "63交易失败, 请联系发卡行",
                "64交易失败, 请联系发卡行",
                "65please Insert Card",
                "66交易失败, 请联系收单行或银联",
                "67没收卡",
                "68交易超时, 请重试",
                "69账户不正确",
                "75密码错误次数超限",
                "77请向网络中心签到",
                "79 POS终端重传脱机数据",
                "90交易失败, 请稍后重试",
                "91交易失败, 请稍后重试",
                "92交易失败, 请稍后重试",
                "93交易失败, 请联系发卡行",
                "94交易失败, 请稍后重试",
                "95交易失败, 请稍后重试",
                "96交易失败, 请稍后重试",
                "97终端未登记, 请联系收单行或银联",
                "98交易超时, 请重试",
                "99校验错, 请重新签到",
                "A0校验错, 请重新签到",
                "A1未知交易",
                "A2运营商系统忙",
                "A3证件号码不符",
                "A4证件类型不符",
                "A5客户信息已存在",
                "A6账户已签约",
                "A7客户身份不正确",
                "A8额度不足",
                "AA为非理财卡",
                "AB为理财卡非白金卡",
                "B1异地用户资料",
                "B2非法用户资料",
                "B3非法电话号码",
                "B4用户输入归属地州错",
                "B5户名反显交易仅适用于本行借记/准贷记账户", //linql20130408
                "B7本地州尚未开通该业务",
                "B8用户不存在或已销号",
                "B9非正常号码",
                "C1代收费不允许少缴费",
                "D0权益积分不足，请与客户经理联系",
                "D1该笔流水不是当前客户的，请查证",
                "D2该笔流水已撤销或冲正",
                "D3您输入的流水号不是当日流水",
                "D5没有找到服务信息，请与银行联系",
                "M1客户没有洗车资格",
                "M2本周已洗",
                "Z1该功能暂未开通",
                "Z2系统不支持该功能",
                "F2无此定制账号类型",
                "F3该定制账号尚未开通服务功能",
                "F5该账号无定制对应关系",
                "F6定制已取消",
                "F7无效运营商",
                "F8该运营商尚未开通",
                "FB该账号已经定制",
                "FR定制已超过有效期",
                "FEIC卡不允许做刷卡交易",
                "FD汇率查询不允许手输卡号",
                "N0该卡不支持DCC交易",
                "G1TMS无此交易类型",
                "G2TMS不支持终端厂商",
                "G3TMS不支持此型号终端",
                "G4无此终端序列号",
                "G5TMS未登记此商户",
                "G6终端无此应用",
                "G7终端放弃下载",
                "G8下载平台确认失败",
                "G9下载出现错误无法完成",
                "H1下载中断",
                "H2CRC校验错误",
                "RP签购单显示重打字样",
                "T1二维码号已使用",
                "T2二维码号已过期",
                "T3二维码号已撤销", ""
    };
    public String getErrCode(String code)
    {
        for (String thisCode: codeList) {
            if(thisCode.contains(code))
                return thisCode;
        }
        return code+"未知错误";
    }

}
