package com.spd.bus.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Class: CreateJsonConfig
 * package：com.yihuatong.tjgongjiaos.restoredata
 * Created by hzjst on 2018/9/27.
 * E_mail：hzjstning@163.com
 * Description：
 */
public class CreateJsonConfig {// 設備信息轉json

    public static String jsonInfo(String busId, String deviceId, String price,
                                  String info) {
        JSONObject jsonInfo = new JSONObject();
        jsonInfo.put( "deviceId", deviceId );
        jsonInfo.put( "busId", busId );
        jsonInfo.put( "price", price );
        jsonInfo.put( "info", info );
        return jsonInfo.toString();
    }

    // 銀聯key轉json
    public static String jsonUnionAppId(String name, String appid,
                                        String version, String address, String poskey) {
        JSONObject jsonInfo = new JSONObject();
        jsonInfo.put( "name", name );
        jsonInfo.put( "appid", appid );
        jsonInfo.put( "version", version );
        jsonInfo.put( "address", address );
        jsonInfo.put( "poskey", poskey );
        return jsonInfo.toString();
    }

    // 支付寶key轉json
    public static String jsonAlipayKey(String key_list, String card_type_list) {
        JSONObject jsonAlipayKey = new JSONObject();
        jsonAlipayKey.put( "key_list", key_list );
        jsonAlipayKey.put( "card_type_list", card_type_list );
        return jsonAlipayKey.toString();
    }

    // 支付宝sercet
    public static String jsonAlipaySercet(String appKey, String appSercet) {
        JSONObject jsonAlipayKey = new JSONObject();
        jsonAlipayKey.put( "appKey", appKey );
        jsonAlipayKey.put( "appSercet", appSercet );
        return jsonAlipayKey.toString();
    }

    // 银联密钥
//    public static String jsonUnionKey(List<UnionQrKey> listUnionKey) {
//        // JSONObject jsonAlipayKey = new JSONObject();
//        // String[] array;
//        // for (int i = 0; i < listUnionKey.size(); i++) {
//        // jsonAlipayKey.put("cert_format", listUnionKey.get(i)
//        // .getCert_format());
//        // jsonAlipayKey.put("org_id", listUnionKey.get(i).getOrg_id());
//        // jsonAlipayKey.put("cert_expire_time", listUnionKey.get(i)
//        // .getCert_expire_time());
//        // jsonAlipayKey.put("cert_no", listUnionKey.get(i).getCert_no());
//        // jsonAlipayKey.put("sign_algorithm", listUnionKey.get(i)
//        // .getSign_algorithm());
//        // jsonAlipayKey.put("encrypt_algorithm", listUnionKey.get(i)
//        // .getEncrypt_algorithm());
//        // jsonAlipayKey.put("parameter_id", listUnionKey.get(i)
//        // .getParameter_id());
//        // jsonAlipayKey
//        // .put("public_key", listUnionKey.get(i).getPublic_key());
//        // jsonAlipayKey.put("cert_sign", listUnionKey.get(i).getCert_sign());
//        // jsonAlipayKey.put("cert_seq", listUnionKey.get(i).getCert_seq());
//        // jsonAlipayKey.put("publickey_length", listUnionKey.get(i)
//        // .getPublickey_length());
//        // array[i] = jsonAlipayKey.toString();
//        // }
//        JSONArray arrays = JSONArray
//                .parseArray( JSON.toJSONString( listUnionKey ) );
//        JSONObject jsonUnionKey = new JSONObject();
//        jsonUnionKey.put( "keylist", arrays );
//        return jsonUnionKey.toString();
//    }

}