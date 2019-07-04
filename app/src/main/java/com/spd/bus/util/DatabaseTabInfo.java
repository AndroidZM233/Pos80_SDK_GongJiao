package com.spd.bus.util;


import android.content.Context;

import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.spd.bus.Info;
import com.spd.bus.entity.TransportCard;
import com.spd.bus.sql.SqlStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: DatabaseTabInfo
 * package：com.yihuatong.tjgongjiaos.activity.util
 * Created by hzjst on 2018/9/8.
 * E_mail：hzjstning@163.com
 * Description：
 */
public class DatabaseTabInfo {
    public static String appkey = "6FF5BB054CE88470";
    public static String info = "";
    public static String busno = "123456";
    public static String deviceNo = "";
    public static String price = "";
    public static String dept = "";
    public static String line = "0";
    public static String tream = "";
    public static String blackVersion = "";
    public static String whiteVersion = "";
    public static String k21Version = "";
    private static DatabaseTabInfo databaseTabInfo;

    public static DatabaseTabInfo getIntence(String type) {
        if (databaseTabInfo == null) {
            databaseTabInfo = new DatabaseTabInfo();
        }
        if ("info".equals( type )) {
            getInfos();
        }
        return databaseTabInfo;
    }

    //获取注册信息，appKey
//    public static String getObtainAppkey() {
//        List<ObtainAppkeySercet> xmqlist = new ArrayList<ObtainAppkeySercet>();
//        xmqlist = SqlStatement.getXMApp();
//        if (xmqlist.size() != 0) {
//            appkey = xmqlist.get( 0 ).getAppKey();
//        }
//        return appkey;
//    }

    //获取参数信息
    private static List<TransportCard> getInfos() {
        List<TransportCard> parList = new ArrayList<TransportCard>();
        parList = SqlStatement.getParameterAll();
        if (parList.size()==0){
            return null;
        }
        info = parList.get( 0 ).getInfo();
        if (!info.equals( "00" )) {
            busno = parList.get( 0 ).getBus_number();
            deviceNo = parList.get( 0 ).getDevice_number();
            price = parList.get( 0 ).getPrice();
            k21Version = parList.get( 0 ).getSoftversion();
            blackVersion = parList.get( 0 ).getBlackversion();
            whiteVersion = parList.get( 0 ).getWhiteversion();
            dept = info.substring( 40, 42 );
            line = info.substring( 46, 50 );
            tream = info.substring( 42, 46 );
        }
        return parList;
    }

    //获取Key秘钥
//    public String getKeyVersion() {
//        List<PublicKeyAlipay> alipayKey = new ArrayList<PublicKeyAlipay>();
//        alipayKey = SqlStatement.getPubKeyAll();
//        String keyVersion = alipayKey.get( 0 ).getKeyversion();
//        return keyVersion;
//    }

}
