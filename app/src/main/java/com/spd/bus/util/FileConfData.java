package com.spd.bus.util;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.spd.bus.MyApplication;
import com.spd.bus.entity.Payrecord;
import com.spd.bus.sql.SqlStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: FileConfData
 * package：com.yihuatong.tjgongjiaos.backupdata
 * Created by hzjst on 2018/9/27.
 * E_mail：hzjstning@163.com
 * Description：
 */
public class FileConfData {

    public static String writeDB(Context context) {

        //********************读取info备份文件,更新至参数表中***********************//
        JSONObject jsbInfo = JSONObject.parseObject(Configurations.read_config(MyApplication.FILENAME_INFO));
        SqlStatement.updataDeviceInfo(jsbInfo.getString("deviceId")
                , jsbInfo.getString("busId"), jsbInfo.getString("price")
                , jsbInfo.getString("info"));

        String driverNr = Configurations.read_config(MyApplication.FILENAME_DRIVER_NR);
        SharedXmlUtil.getInstance(context).write("TAGS", driverNr);
        //******************** 读取driverSignRecord备份文件,更新至Payrecord表中***********************//
        ActiveAndroid.beginTransaction();
        try {
            Payrecord pay = new Payrecord();
            pay.setRecord(Configurations.read_config(MyApplication.FILENAME_ICCARD));
            pay.setDatatime(String.valueOf(System.currentTimeMillis()));
            pay.setTag(1);
            pay.setXiaofei(0);
            pay.setBuscard(0);
            pay.setJilu(2);
            pay.setWritetag(1);
            pay.setTraffic(0);
            pay.setTradingflow(0);
            pay.save();
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

        //********************读取云闪付密钥备份文件***********************//
//        List<UnionQrKey> listUnionKey = new ArrayList<UnionQrKey>();
//        listUnionKey = SqlStatement.getAllUnionKey();
//        JSONObject json = JSONObject.parseObject( Configurations
//                .read_config( MyApplication.FILENAME_UNIONKEY ) );
//        JSONArray jsonArray = json.getJSONArray( "keylist" );
//        for (int i = 0; i < jsonArray.size(); i++) {
//            JSONObject jsonObjects = (JSONObject) jsonArray.get( i );
//            if (listUnionKey.size() == 0) {
//                //========================讀取UnionQrKey存入表中==================//
//                UnionQrKey unKey = new UnionQrKey();
//                unKey.setCert_format( jsonObjects.getString( "cert_format" ) );
//                unKey.setCert_expire_time( jsonObjects.getString( "cert_expire_time" ) );
//                unKey.setCert_no( jsonObjects.getString( "cert_no" ) );
//                unKey.setCert_seq( jsonObjects.getString( "cert_seq" ) );
//                unKey.setCert_sign( jsonObjects.getString( "cert_sign" ) );
//                unKey.setEncrypt_algorithm( jsonObjects.getString( "encrypt_algorithm" ) );
//                unKey.setOrg_id( jsonObjects.getString( "org_id" ) );
//                unKey.setParameter_id( jsonObjects.getString( "parameter_id" ) );
//                unKey.setPublic_key( HzjString.bytesToHexString1(
//                        Base64Encrypt.base64Decode( jsonObjects.getString( "public_key" ) ) ).trim() );
//                unKey.setPublickey_length( jsonObjects.getString( "publickey_length" ) );
//                unKey.setSign_algorithm( jsonObjects.getString( "sign_algorithm" ) );
//                unKey.save();
//            }
//        }

        //********************读取支付宝密钥，存入表中,更新至alipaykey表中***********************//
//        JSONObject jsbAlipaykKey = JSONObject.parseObject( Configurations
//                .read_config( MyApplication.FILENAME_ALIPAY ) );
//        SqlStatement.updataDeviceALipayKey( jsbAlipaykKey.getString( "key_list" )
//                , jsbAlipaykKey.getString( "card_type_list" ) );

        //********************读取AppSercet、appkey备份文件,更新至支付宝Sercet表中***********************//
//        JSONObject jsbAlipaySercet = JSONObject.parseObject( Configurations
//                .read_config( MyApplication.FILENAME_ALIPAY_SECCRET ) );
//        SqlStatement.updataDeviceALipaySercet( jsbAlipaySercet.getString( "appKey" )
//                , jsbAlipaySercet.getString( "appSercet" ) );

        //******************** 读取appId以及unionPosKey备份文件,更新至uniondeng表中***********************//
//        JSONObject jsbUnionAppId = JSONObject.parseObject( Configurations
//                .read_config( MyApplication.FILENAME_UNION_APPID ) );
//        SqlStatement.updataDeviceUnionPosAppId( jsbUnionAppId.getString( "name" )
//                , jsbUnionAppId.getString( "appid" ) );
        //******************** 更新银联秘钥unionPosKey表中***********************//
//        SqlStatement.UpdateUnionPosKey( jsbUnionAppId.getString( "poskey" ) );

        return "00";
    }
}
