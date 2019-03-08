package com.spd.bus.card.methods.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

/**
 * 交易记录数据库
 * Created by 张明_ on 2019/2/20.
 * Email 741183142@qq.com
 */
@Entity
public class Save128CodeBean {
    //记录号
    @Id(autoincrement = false)
    private byte[] id = new byte[2];

    //预留
    private byte[] reserved1 = new byte[1];
    //记录类型
    private byte[] recordType = new byte[1];
    //预留
    private byte[] reserved2 = new byte[1];
    //卡种
    private byte[] cards = new byte[1];
    //卡主信息结构体
    private byte[] cardStructure = new byte[14];
    //卡主类型
    private byte[] cardMainType = new byte[1];
    //卡子类型
    private byte[] cardSonType = new byte[1];
    //交易时间
    private byte[] tradingHour = new byte[4];
    //交易类型标识
    private byte[] transactionTypeFlag = new byte[1];
    //交易实际金额
    private byte[] actualAmount = new byte[3];
    //交易应收金额
    private byte[] transactionReceivable = new byte[3];
    //交易虚拟金额
    private byte[] transactionVirtualAmount = new byte[3];
    //交易原额
    private byte[] originalAmount = new byte[3];
    //消费序号
    private byte[] consumptionNum = new byte[2];
    //TAC码
    private byte[] TAC = new byte[4];
    //消费密钥版本号
    private byte[] cKeyVersion = new byte[1];
    //消费密钥索引
    private byte[] cKeyIndex = new byte[1];
    //PSAM卡终端机编号
    private byte[] psamTerminalNum = new byte[6];
    //PSAM卡终端交易序号
    private byte[] psamTransactionNum = new byte[4];
    //PSAM卡卡号
    private byte[] psamCardNum = new byte[10];
    //个性化数据
    private byte[] personalizedData = new byte[26];
    //公司代码
    private byte[] companyCode = new byte[4];
    //路队线路代码
    private byte[] routeCode = new byte[4];
    //车辆代码
    private byte[] vehicleCode = new byte[4];
    //卡机设备号
    private byte[] cardDeviceNum = new byte[4];
    //司机卡号
    private byte[] driveNum = new byte[10];
    //换乘标志
    private byte[] changeCarLogo = new byte[1];
    //上下行标识
    private byte[] upDownLogo = new byte[1];
    //上车站点
    private byte[] onSite = new byte[1];
    //上车时间
    private byte[] onTime = new byte[4];
    //下车站点
    private byte[] offSite = new byte[1];
    //校验码
    private byte[] checkCode = new byte[1];
    //是否上传
    private boolean isUpload = false;
}
