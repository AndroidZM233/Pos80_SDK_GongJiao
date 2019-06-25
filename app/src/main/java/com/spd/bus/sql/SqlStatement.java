package com.spd.bus.sql;

import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.spd.bus.OverallSituationState;
import com.spd.bus.entity.Blacklist;
import com.spd.bus.entity.CityCodeTriff;
import com.spd.bus.entity.MobileApp;
import com.spd.bus.entity.Payrecord;
import com.spd.bus.entity.TransportCard;
import com.spd.bus.entity.UnionBlack;
import com.spd.bus.entity.UnionPay;
import com.spd.bus.entity.UnionQrKey;
import com.spd.bus.entity.White;
import com.spd.bus.util.DateTime;

import java.util.ArrayList;
import java.util.List;

import static com.spd.bus.util.DateTime.getMonthAGO;


/**
 * Class: SqlStatement
 * package：com.yihuatong.tjgongjiaos.sql
 * Created by hzjst on 2018/3/1.
 * E_mail：hzjstning@163.com
 * Description：sql操作语句（select、update、delete）
 */
public class SqlStatement {
    /**
     * 类名：Payrecord
     * 表名：Payrecord
     * 刷卡消费记录表操作
     */
//1.全查询，
    public static List<Payrecord> selectCatdRecord() {
        return new Select().from(Payrecord.class).execute();
    }

    //2.查询倒数第一条司机记录
    public static List<Payrecord> ReciprocalDriverRecord() {
        return new Select().from(Payrecord.class).where("jilu=?", 2)
                .orderBy("_Id DESC").limit(1).execute();

    }

    //3.查询余额当前总金额
    public static List<Payrecord> selectTotalAmount() {
        return new Select().from(Payrecord.class).where("traffic=?", 0)
                .and("buscard!=?", 0).and("jilu=?", 0).execute();

    }

    //4.查询当前月票的人数
    public static List<Payrecord> selectMonthlyTicket() {

        return new Select().from(Payrecord.class).where("buscard=?", 2)
                .execute();
    }

    //5.查询最新司机记录的ID
    public static List<Payrecord> driverRecord() {

        return new Select("_Id").from(Payrecord.class).where("buscard=?", 0)
                .orderBy("_Id DESC").limit(1).execute();
    }

    //6.查询当班司机下的消费记录
    public static List<Payrecord> driverConsumptionRecord(long id) {
        return new Select().from(Payrecord.class).where("_Id>?", id).execute();

    }

    //7.查询当班司机月票人次
    public static List<Payrecord> driverNowMonthlyTicket(long id) {
        return new Select().from(Payrecord.class).where("_Id>?", id)
                .and("buscard=?", 2).execute();
    }

    //8.当班司机总人次
    public static int headcount(long id) {
        return new Select("count(*)").from(Payrecord.class).where("_Id>?", id)
                .and("jilu!=?", 1).and("traffic=?", 0).and("buscard!=?", 0)
                .count();

    }

    //9.查询未上传的记录、去除交通部第二条的
    public static List<Payrecord> UnuploadedRecord() {
        return new Select().from(Payrecord.class).where("tag=?", 1)
                .and("traffic=?", 0).execute();

    }

    //10.查询未上传的最新的消费记录
    public static List<Payrecord> selectConsumptionRecord() {
        return new Select().from(Payrecord.class).where("tag=?", 1).and("buscard!=?", 0).orderBy("_Id ASC").limit(1).execute();

    }
    //11.根据id查询临近未上传消费记录的司机记录

    public static List<Payrecord> selectDriverRecord(int id) {
        return new Select().from(Payrecord.class).where("_Id=?", id)
                .and("buscard=?", 0).orderBy("_Id DESC").limit(1).execute();
    }

    //12.修改上传后的标识、 // 将上传至web端的数据提交状态改为0
    public static void UpdataSubmit(List<Payrecord> list) {
        Log.i("SqlStatement", "修改tag为0");
        for (int i = 0; i < list.size(); i++) {
            new Update(Payrecord.class).set("TAG = ?", 0)
                    .where("_Id=?", list.get(i).getId()).execute();

        }
    }

    //13.定期删除消费记录表中的数据
    public static void timeDeleting() {
        //new Select( "datetome" ).from( Payrecord.class ).where( "datetime<?", new DateTime().getMonthAGO() ).execute();
        new Delete().from(Payrecord.class).where("datetime<?", new DateTime().getMonthAGO())
                .execute();

    }

    //14.查询未上传的记录 tag=1的记录
    public static List<Payrecord> selectTagRecord() {
        return new Select().from(Payrecord.class).where("tag=?", 1).orderBy("_Id ASC").limit(1000).execute();

    }

    //15.删除超过三个月的消费记录
    // 定期刪除
    public static void deleteRecordOld() {
        try {
            new Delete().from(Payrecord.class)
                    .where("datetime<?", getMonthAGO()).and("jilu!=?", 2)
                    .execute();
        } catch (Exception e) {
            Log.i("deleterecord_old", "删除错误！");
        }
    }

    //16.
    public static List<Payrecord> busrecordTradingFlow(int id) {
        return new Select().from(Payrecord.class).where("tradingflow=?", id)
                .and("buscard=?", 0).orderBy("_Id DESC").limit(1).execute();
    }

    //17.查询后六条
    public static List<Payrecord> recordListSel() {
        return new Select().from(Payrecord.class).where("jilu=?", 0)
                .orderBy("_Id DESC").limit(6).execute();
    }

    //18.查詢已上傳的記錄
    public static List<Payrecord> getUpLoayrecord() {
        return new Select().from(Payrecord.class).where("tag=?", 0)
                .and("buscard=?", 1).execute();

    }

    //19.刪除上傳完的記錄
    public static void deleterecord_old1() {
        try {
            new Delete().from(Payrecord.class).where("buscard!=?", 0)
                    .and("tag=?", 0).execute();
        } catch (Exception e) {
            Log.i("deleterecord_old", "删除错误！");
        }
    }

    //20.获取最后一条记录
    public static List<Payrecord> getRecordID() {

        return new Select().from(Payrecord.class).orderBy("_Id DESC").limit(1)
                .execute();

    }

//    /**
//     * 类名：XMQrRecord
//     * 表名：kpqrrecord
//     * 描述：存储客票流水
//     */
//    //1.全查询
//    public static List<XMQrRecord> getXMRecord() {
//        return new Select().from( XMQrRecord.class ).execute();
//    }
//
//    //2.查询未上传的记录
//    public static List<XMQrRecord> getXMQAppTag() {
//        return new Select().from( XMQrRecord.class ).where( "tag=?", 1 ).execute();
//    }
//
//    //3.更新上传后的标识
//    public static void updataUploadRecordTag(List<XMQrRecord> lsit) {
//
//        for (int i = 0; i < lsit.size(); i++) {
//            new Update( XMQrRecord.class ).set( "tag=?", 0 )
//                    .where( "Id=?", lsit.get( i ).getId() ).execute();
//        }
//    }
//
//    //4.电子客票记录达到30000时，清空一次表
//    public static void deleteRecordNow() {
//        try {
//            new Delete().from( XMQrRecord.class ).where( "tag=?", 0 ).execute();
//        } catch (Exception e) {
//            Log.i( "deleterecord_old", "删除错误！" );
//        }
//
//    }
//
//    //5.出巡上傳完的的記錄
//    public static List<XMQrRecord> getQrrecord() {
//        return new Select().from( XMQrRecord.class ).where( "tag=?", 0 ).execute();
//
//    }
//
//    /***
//     * 类名：PublicKeyAlipay
//     * 表名：qrcodekey
//     * 说明：存储支付宝秘钥，版本号、卡类型
//     */
//    //1.更新版本
//    public static int updataKeyversion(String data) {
//        new Update( PublicKeyAlipay.class ).set( "keyversion=?", data )
//                .where( "Id=?", 1 ).execute();
//        return 0;
//    }
//
//    //2.更新秘钥列表
//    public static int updateKey(String data) {
//        new Update( PublicKeyAlipay.class ).set( "key_list=?", data )
//                .where( "Id=?", 1 ).execute();
//        return 0;
//    }
//
//    //3.更新cardListType ,码支持的类型
//    public static int updateKeyCardyType(String cardtype) {
//        new Update( PublicKeyAlipay.class ).set( "card_type_list=?", cardtype )
//                .where( "Id=?", 1 ).execute();
//        return 0;
//    }
//
//    // 4.全查询
//    public static List<PublicKeyAlipay> getPubKeyAll() {
//        return new Select().from( PublicKeyAlipay.class ).execute();
//    }
//
//    // 5.更新支付宝密钥
//    public static int updataDeviceALipayKey(String key_list,
//                                            String card_type_list) {
//        new Update( PublicKeyAlipay.class )
//                .set( "key_list=?," + "card_type_list=?", key_list,
//                        card_type_list ).where( "Id=?", 1 ).execute();
//        return 0;
//    }

//    /**
//     * 类名：ObtainAppkeySercet
//     * 表名：appkeysercet
//     * 更新appkey、sercet等等
//     */
//    //1.更新所有字段。 机具注册服务，初始化查询
//    public static int updateObtain(String sid, String appKey, String sercet, String deviceId, String tracingId, String remake) {
//        new Update( ObtainAppkeySercet.class ).set( "sid=?," + "appKey=?," + "appSercet=?," + "deviceId=?," + "tracingId=?," + "remake=?", sid, appKey, sercet, deviceId, tracingId, remake ).where( "Id=?", 1 )
//                .execute();
//
//        return 0;
//    }
//
//    //2.全查询。（查询appkey和appSercet,小码端）
//    public static List<ObtainAppkeySercet> getXMApp() {
//        return new Select().from( ObtainAppkeySercet.class ).execute();
//    }
//
//    //3.更新支付宝Sercet
//    public static int updataDeviceALipaySercet(String appKey, String appSercet) {
//        new Update( ObtainAppkeySercet.class )
//                .set( "appKey=?," + "appSercet=?", appKey, appSercet )
//                .where( "Id=?", 1 ).execute();
//        return 0;
//    }
//
    /**
     * 类名：TransportCard
     * 表名：parameter
     * 参数表操作（）
     */
    //1.全查询
    public static List<TransportCard> getParameterAll() {
        //   Logger.i( "查询=" + new Select().from( TransportCard.class ).execute() );
        return new Select().from( TransportCard.class ).execute();

    }

    //2.更新表中，deviceid（设备号）
    public static int updataSN(String sn) {
        new Update( TransportCard.class ).set( "device_number=?", sn )
                .where( "Id=?", 1 ).execute();
        return 0;
    }

    //3.更新k21是否更新标志Tag
    public static int updateBin(String binversion) {
        new Update( TransportCard.class ).set( "binversion=?", binversion )
                .where( "Id=?", 1 ).execute();
        return 0;
    }

    //4.更新K21版本号
    //备注：表中字段交错（softversion字段即为k21版本字段）
    public static int updataBinVersion(String binsoftversion) {
        new Update( TransportCard.class ).set( "softversion=?", binsoftversion )
                .where( "Id=?", 1 ).execute();
        return 0;
    }

    //5.更细车辆号
    public static int updateBusNo(String busNo) {
        new Update( TransportCard.class ).set( "bus_number=?", busNo )
                .where( "Id=?", 1 ).execute();
        return 0;
    }

    //6.更新黑名单版本，
    public static int updateBlack(String blackData) {
        new Update( TransportCard.class ).set( "blackversion=?", blackData )
                .where( "Id=?", 1 ).execute();
        return 0;
    }

    //7.更新白名单版本
    public static int updateWhite(String whiteData) {
        new Update( TransportCard.class ).set( "whiteversion=?", whiteData )
                .where( "Id=?", 1 ).execute();
        return 0;
    }

    //8.更新參數列表
    public static int updataDeviceInfo(String deviceId, String busNumber,
                                       String price, String info) {
        new Update( TransportCard.class )
                .set( "device_number=?," + "bus_number=?," + "price=?,"
                        + "info=?", deviceId, busNumber, price, info )
                .where( "Id=?", 1 ).execute();
        return 0;
    }

//    /**
//     * 类名：PsamSim
//     * 表名：psamsimon
//     * 机具信息以及sim、psam信息
//     */
//    //1.全查询
//    public static List<PsamSim> getPsamAll() {
//        return new Select().from( PsamSim.class ).execute();
//    }
//
//    //2.更新 SIM卡的序列号
//    public static int updateSimSerisal(String simSerisal) {
//        new Update( PsamSim.class ).set( "sim_serial=?", simSerisal ).where( "Id=?", 1 )
//                .execute();
//        return 0;
//    }
//
//    //3.更新 SIM卡的IMSI
//    public static int updataSimImsi(String simImsi) {
//        new Update( PsamSim.class ).set( "sim_imsi=?", simImsi ).where( "Id=?", 1 )
//                .execute();
//        return 0;
//    }
//
//    //4.更新机具的卡的IMEI
//    public static int updataSimImei(String machImei) {
//        new Update( PsamSim.class ).set( "mach_imei=?", machImei ).where( "Id=?", 1 )
//                .execute();
//        return 0;
//    }
//
//    //5.更新住建部 psam卡号
//    public static int updataPsamUrd(String psamUrdcardON) {
//        new Update( PsamSim.class ).set( "psamurdcardon=?", psamUrdcardON ).where( "Id=?", 1 )
//                .execute();
//        return 0;
//    }
//
//    //6.更新交通部 psam卡号
//    public static int updataPsamTri(String psamTricardON) {
//        new Update( PsamSim.class ).set( "psamtricardon=?", psamTricardON ).where( "Id=?", 1 )
//                .execute();
//        return 0;
//    }
//
    /***
     * 类名：White
     * 表名：White
     * 说明：白名单（交通部、住建部）
     */
    //1.查询本次交易卡是否是白名单
    public static int SelectCardWhite(String code) {
        List<White> list = new ArrayList<White>();
        list = new Select().from( White.class ).where( "data=?", code ).execute();
        if (list.size() != 0) {
            // 是白名单
            return 1;
        }
        // 不是白名单
        return 0;

    }

    //2.更新白名单版本号
    public static int updata_white(String sn) {
        new Update( TransportCard.class ).set( "whiteversion=?", sn ).where( "Id=?", 1 )
                .execute();
        return 0;
    }

    //3.刪除白名單
    public static void deleteWhiteCardcode() {
        try {
            new Delete().from( White.class ).execute();
        } catch (Exception e) {
            Log.i( "deleterecord_old", "删除错误！" );
        }
    }

    /***
     * 类名：Blacklist
     * 表名：blacklist
     * 说明：黑名单（公交、城市卡）
     *
     */
    //1.查询本次交易卡是否是黑名单
    public static int SelectCard(String code) {
        List<Blacklist> list = new ArrayList<Blacklist>();
        list = new Select().from( Blacklist.class ).where( "blackcode=?", code )
                .execute();
        if (list.size() != 0) {
            // 是黑名单
            return 1;
        }
        // 不是黑名单
        return 0;

    }

    // 2.更新黑名单版本号
    public static int updata_black(String sn) {
        new Update( TransportCard.class ).set( "blackversion=?", sn ).where( "Id=?", 1 )
                .execute();
        return 0;
    }

    //3.刪除黑名單
    public static void deleteBlackCardcode() {
        try {
            new Delete().from( Blacklist.class ).execute();
        } catch (Exception e) {
            Log.i( "deleterecord_old", "删除错误！" );
        }
    }
//
//    /***
//     * 类名：UnionPosKey
//     * 表名：unionpaykey
//     * 说明：银联POS秘钥
//     *
//     */
//    //1.查询此表中的pos中的秘钥
//    public static List<UnionPosKey> getUnionPosKey() {
//        return new Select().from( UnionPosKey.class ).execute();
//
//    }
//
//    //2、更新POS秘钥
//    public static int UpdateUnionPosKey(String poskey) {
//        new Update( UnionPosKey.class ).set( "poskey=?", poskey ).where( "Id=?", 1 )
//                .execute();
//        return 0;
//    }

    /**
     * 类名：UnionBlack
     * 表名：unionblack
     * 说明：银联黑名单
     */
    //1.查询黑名单
    public static int SelectUnionBlack(String code) {
        List<UnionBlack> list = new ArrayList<UnionBlack>();
        list = new Select().from( UnionBlack.class ).where( "unionCode=?", code )
                .execute();
        if (list.size() != 0) {
            // 是黑
            return 1;
        }
        // 不是黑
        return 0;
    }

    //2.删除黑名单
    public static void deleteUnionBlack() {
        try {
            new Delete().from( UnionBlack.class ).execute();
        } catch (Exception e) {
            Log.i( "deleterecord_old", "删除错误！" );
        }
    }

    /**
     * 类名：UnionPay
     * 表明：unionpay
     * 说明：银行卡或者pay
     */
    //1.全查询

    //2.更新狀態（ODA）
    public static int updataUnionODASUC(String tradingFlow) {
        new Update(UnionPay.class)
                .set("doubleState=?," + "type=?," + "isPay=?," + "payStatus=?,"
                                + "responseCode=?," + "retrievingNum=?," + "oDAState=?",
                        OverallSituationState.SM_PAYRECODE_SUCCESS, "04", "0",
                        "07", "", "", "06").where("tradingFlow=?", tradingFlow)
                .execute();
        return 0;
    }

    //3.银联双免成功后需更新字段
    public static int updataUnionSMSUC(String tradingFlow, String responseCode,
                                       String retrievingNum) {
        new Update(UnionPay.class)
                .set("doubleState=?," + "type=?," + "isPay=?," + "payStatus=?,"
                                + "responseCode=?," + "retrievingNum=?",
                        OverallSituationState.SM_PAYRECODE_SUCCESS,
                        OverallSituationState.SM_TYPE,
                        OverallSituationState.ISPAY_SUCESS,
                        OverallSituationState.SM_PAYSTATUS_SUCCESS,
                        responseCode, retrievingNum)
                .where("tradingFlow=?", tradingFlow).execute();
        return 0;
    }

    //4.
    public static int updataUnionSMSUC_failed(String tradingFlow,
                                              String responseCode, String retrievingNum) {
        new Update(UnionPay.class)
                .set("doubleState=?," + "type=?," + "responseCode=?,"
                                + "retrievingNum=?",
                        OverallSituationState.SM_PAYRECODE_FAILED,
                        OverallSituationState.SM_TYPE, responseCode,
                        retrievingNum).where("tradingFlow=?", tradingFlow)
                .execute();
        return 0;
    }

    //5.查询未上传的记录
    public static List<UnionPay> getUnionPayReocrdTag() {
        return new Select().from(UnionPay.class).where("tag=?", 1).execute();
    }

    //6.更新上传记录的标识TAG
    public static void updataICUnion(List<UnionPay> lsit) {
        for (int i = 0; i < lsit.size(); i++) {
            new Update(UnionPay.class).set("tag=?", 0)
                    .where("Id=?", lsit.get(i).getId()).execute();
        }
    }

    //7.查詢上傳完成的記錄
    public static List<UnionPay> getUpLoadUnionPayReocrdTag() {
        return new Select().from(UnionPay.class).where("tag=?", 0).execute();
    }

    //8.删除上传完的记录
    public static void deleterecordOldUnion() {
        try {
            new Delete().from(UnionPay.class).where("tag=?", 0).execute();
        } catch (Exception e) {
            Log.i("deleterecordOldUnion", "删除错误！");
        }
    }
//    /**
//     * 类名：UnionQrcodeRecord
//     * 表名：unionqrrecord
//     * 说明：银联云闪付
//     */
//    //1.更新银联云闪付码上传后结果tag
//    public static void updataQRUnionTag(List<UnionQrcodeRecord> lsit) {
//
//        for (int i = 0; i < lsit.size(); i++) {
//            new Update( UnionQrcodeRecord.class ).set( "tag=?", 0 )
//                    .where( "Id=?", lsit.get( i ).getId() ).execute();
//        }
//    }
//
//    //2.
////.删除上传完的记录
//    public static void deleteQrrecordOldUnion() {
//        try {
//            new Delete().from( UnionQrcodeRecord.class ).where( "tag=?", 0 ).execute();
//        } catch (Exception e) {
//            Log.i( "deleteQrrecordOldUnion", "删除错误！" );
//        }
//    }
//
//    //3.查询未上传的记录
//    public static List<UnionQrcodeRecord> getUnionQrRecord() {
//        return new Select().from( UnionQrcodeRecord.class ).where( "tag=?", 1 )
//                .execute();
//    }
//
//    //4.查询上传的记录
//    public static List<UnionQrcodeRecord> getNoUnionQrRecord() {
//        return new Select().from( UnionQrcodeRecord.class ).where( "tag=?", 0 )
//                .execute();
//    }

    /**
     * 类名：UnionQrKey
     * 表名：unionkey
     * 说明：银联云闪付秘钥
     */
    //1.查询银联qr秘钥
    public static List<UnionQrKey> getAllUnionKey() {
        return new Select().from(UnionQrKey.class).execute();

    }

    //2.删除秘钥
    public static void DeleteUnionOldKey() {
        new Delete().from(UnionQrKey.class).execute();
    }


    /**
     * 类名：CityCodeTriff
     * 表名：
     * 说明：交通部折扣，表中存储的为各城市发卡方代码
     */
    //1.查询已有城市发卡方代码
    public static List<CityCodeTriff> getTriffCity() {
        return new Select().from(CityCodeTriff.class).execute();
    }

    /**
     * 类名：MobileApp
     * 表名：
     * 说明：存储为APPID，
     */
    //1.查询appid
    public static List<MobileApp> getAppinfo() {
        return new Select().from(MobileApp.class).execute();

    }
    //2.更新银联APPid

    public static int updataDeviceUnionPosAppId(String name, String appid) {
        new Update(MobileApp.class).set("name=?," + "appid=?", name, appid)
                .where("Id=?", 1).execute();
        return 0;
    }

//    /**
//     * 类名：TXMacKey
//     * 表名：txmackey
//     * 说明： Mac
//     */
//    //1.查询是否存在Mac
//    public static List<TXMacKey> getTXMacKey() {
//        return new Select().from( TXMacKey.class ).execute();
//
//    }
//
//    //2.先删除后存储
//    public static void deleteMacKey() {
//        try {
//            new Delete().from( TXMacKey.class ).execute();
//        } catch (Exception e) {
//            Log.i( "deleteQrrecordOldUnion", "删除错误！" );
//        }
//    }
//
//    //3.根据版号查询对应的mac
//    public static List<TXMacKey> getTXMacKeyData(String countNo) {
//        return new Select().from( TXMacKey.class ).where( "countmacno=?", countNo ).execute();
//
//    }
//
//    /**
//     * 类名：TXPublicKey
//     * 表名：txpubkey
//     * 说明： 公钥
//     */
//    //1.查询是否存在publickey
//    public static List<TXPublicKey> getTXPublicKey() {
//        return new Select().from( TXPublicKey.class ).execute();
//
//    }
//
//    //2.先删除后存储
//    public static void deletePublicKey() {
//        try {
//            new Delete().from( TXPublicKey.class ).execute();
//        } catch (Exception e) {
//            Log.i( "deleteQrrecordOldUnion", "删除错误！" );
//        }
//    }
//
//    //3.根据号查询对应公钥
//    public static String getTXPubKeyData(int countNo) {
//        List<TXPublicKey> txPublicKeys = new Select( "publickey" ).from( TXPublicKey.class ).where( "countno=?", countNo ).execute();
//        return txPublicKeys.get( 0 ).getPublickey();
//    }
//
//    /**
//     * 类名：TXQrRecord
//     * 表名：txqrrecord
//     * 说明：TXqr记录
//     */
//    //1.查询未上传的
//    public static List<TXQrRecord> getTXQrRecord() {
//        return new Select().from( TXQrRecord.class ).where( "tag=?", "1" ).execute();
//
//    }
//
//    public static void updataQRTXTag(List<TXQrRecord> lsit) {
//
//        for (int i = 0; i < lsit.size(); i++) {
//            new Update( TXQrRecord.class ).set( "tag=?", 0 )
//                    .where( "Id=?", lsit.get( i ).getId() ).execute();
//        }
//    }
//
//    /**
//     * 类名：
//     * 表名：
//     * 说明：
//     */
//    //1.查询未上传的
//    public static List<GoldcodeRecord> getGoldQrRecord() {
//        return new Select().from( GoldcodeRecord.class ).where( "tag=?", "1" ).execute();
//    }
//
//    //2.更新上传后标识
//    public static void updateQRGold(List<GoldcodeRecord> lisGold) {
//        for (int i = 0; i < lisGold.size(); i++) {
//            new Update( GoldcodeRecord.class ).set( "tag=?", 0 ).where( "_Id=?", lisGold.get( i ).getId() ).execute();
//        }
//    }
}
