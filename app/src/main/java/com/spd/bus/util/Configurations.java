package com.spd.bus.util;

import android.annotation.SuppressLint;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@SuppressLint("SdCardPath")
public class Configurations {
    public static String fileName_log = "/sdcard/log.conf"; // 公交配置信息
    public static String fileName_bus = "/sdcard/bus.conf"; // 公交配置信息
    public static String fileName_qq = "/sdcard/QQPublicKeydata.conf";
    public static String res_B = "";
    public static String res_A = "";
    public static String A;

    // 写入文件
//    public static boolean Writetxt(JSONObject js, String fileName) {
//        boolean key = false;
//        try {
//            FileOutputStream fout = new FileOutputStream( fileName );
//            // String result = AES.Encrypt(js.toString(), AES.key);
//            String result = js.toString();
//           // System.out.println( "result===" + result );
//            byte[] bytes = result.getBytes( "UTF-8" );
//            fout.write( bytes );
//            fout.close();
//            key = true;
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//        }
//        return key;
//    }

    public static boolean writlog(String code, String file) {
        FileOutputStream fout;
        try {
            fout = new FileOutputStream( file );
            byte[] bytes = code.getBytes( "UTF-8" );
            fout.write( bytes );
            fout.close();
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;

    }

    // 写消费记录
    public static boolean writRecord(String record, String file) {
        FileOutputStream fout;
        try {
            fout = new FileOutputStream( file, true );
            byte[] bytes = record.getBytes( "UTF-8" );
            fout.write( bytes );
            fout.close();
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;

    }

    // 写消费记录
    public static boolean writRecord2(String record, String file) {
        FileOutputStream fout;
        try {
            fout = new FileOutputStream( fileName_log, true );
            byte[] bytes = record.getBytes( "UTF-8" );
            fout.write( bytes );
            fout.close();
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;

    }

    // // bus配置文件读取
    // public static MyApplication read_bustext() {
    // MyApplication bs = new MyApplication();
    // try {
    // FileInputStream fin = new FileInputStream(fileName_bus);
    // int length = fin.available();
    // byte[] buffer = new byte[length];
    // fin.read(buffer);
    // res_B = EncodingUtils.getString(buffer, "UTF-8");
    // String result = AES.Decrypt(res_B, AES.key);
    // JSONObject jasonObject = JSONObject.parseObject(result);
    // fin.close();
    // System.out.println(jasonObject + "---------dui");
    // bs.pubtongka = Float.valueOf(jasonObject.getString("pubtongka"));
    // bs.laorenka = Float.valueOf(jasonObject.getString("laorenka"));
    // bs.canjiren = Float.valueOf(jasonObject.getString("canjiren"));
    // bs.xueshengyue = Float
    // .valueOf(jasonObject.getString("xueshengyue"));
    // bs.jiaoshi = Float.valueOf(jasonObject.getString("jiaoshi"));
    // bs.chengrenyue = Float
    // .valueOf(jasonObject.getString("chengrenyue"));
    // bs.ertong = Float.valueOf(jasonObject.getString("ertong"));
    // bs.junren = Float.valueOf(jasonObject.getString("junren"));
    // bs.tuixiu = Float.valueOf(jasonObject.getString("tuixiu"));
    // bs.shehui = Float.valueOf(jasonObject.getString("shehui"));
    // bs.mianfei = Float.valueOf(jasonObject.getString("mianfei"));
    // bs.jiaoquyue = Float.valueOf(jasonObject.getString("jiaoquyue"));
    // bs.waizeng = Float.valueOf(jasonObject.getString("waizeng"));
    // bs.mangren = Float.valueOf(jasonObject.getString("mangren"));
    // bs.laonianganbu = Float.valueOf(jasonObject
    // .getString("laonianganbu"));
    // bs.fuli = Float.valueOf(jasonObject.getString("fuli"));
    // bs.jinian = Float.valueOf(jasonObject.getString("jinian"));
    // bs.zaizhiguangli = Float.valueOf(jasonObject
    // .getString("zaizhiguangli"));
    // bs.shezhi = Float.valueOf(jasonObject.getString("shezhi"));
    // bs.tingyun = Float.valueOf(jasonObject.getString("tingyun"));
    // bs.qiyong = Float.valueOf(jasonObject.getString("qiyong"));
    // bs.ceshi = Float.valueOf(jasonObject.getString("ceshi"));
    // bs.yanshi = Float.valueOf(jasonObject.getString("yanshi"));
    //
    // bs.guzhangka = Float.valueOf(jasonObject.getString("guzhangka"));
    //
    // bs.diaoduqiandian = jasonObject.getString("diaoduqiandian");
    //
    // bs.xianlu = jasonObject.getString("xianlu");
    //
    // bs.ludui = jasonObject.getString("ludui");
    //
    // bs.gongsi = jasonObject.getString("gongsi");
    //
    // System.out.println(bs.toString());
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // System.out.println(res_B + "---");
    //
    // return bs;
    // }

    // 将消费记录写入文件，方便采集
    public static boolean writeRecord(String expensecal, String fileName) {
        boolean key = false;
        try {
            FileOutputStream fos = new FileOutputStream( fileName );
            // String result = AES.Encrypt(expensecal.toString(), AES.key);
            // String result=js.toString();
            byte[] bytes = expensecal.getBytes( "UTF-8" );
            fos.write( bytes );
            fos.close();
            key = true;
        } catch (Exception e) {

            e.printStackTrace();

        }
        return key;

    }

    // // ali配置文件读取
    //
    // public static Alidata read_alitext() {
    // Alidata ad = new Alidata();
    // try {
    // FileInputStream fin = new FileInputStream(fileName_ali);
    // int length = fin.available();
    // byte[] buffer = new byte[length];
    // fin.read(buffer);
    // res_A = EncodingUtils.getString(buffer, "UTF-8");
    // String result=AES.Decrypt(res_A, AES.key);
    // JSONObject jasonObject = JSONObject.parseObject(result);
    //
    // fin.close();
    // System.out.println(jasonObject + "---------");
    //
    // ad.App_id = jasonObject.getString("App_id");
    // ad.Code_key = JSONObject.parseObject(jasonObject
    // .getString("Code_key"));
    // ad.private_key = jasonObject.getString("private_key");
    // System.out.println(ad.Code_key);
    // System.out.println(ad.private_key);
    // System.out.println(ad.Code_key.get("1") + "---");
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // System.out.println(res_A + "---");
    //
    // return ad;
    // }
    public static boolean writeConf(String expensecal, String fileName) {
        boolean key = false;
        try {
            FileOutputStream fos = new FileOutputStream( fileName );
            // String result = AES.Encrypt(expensecal.toString(), AES.key);
            String result = expensecal.toString();
            byte[] bytes = expensecal.getBytes( "UTF-8" );
            fos.write( bytes );
            fos.close();
            key = true;
        } catch (Exception e) {

            e.printStackTrace();

        }
        return key;

    }

    // 文件读取

//    public static String read_alitext() {
//        try {
//            FileInputStream fin = new FileInputStream( fileName_bus );
//            int length = fin.available();
//            byte[] buffer = new byte[length];
//            fin.read( buffer );
//            res_A = EncodingUtils.getString( buffer, "UTF-8" );
//            String result = AES.Decrypt( res_A, AES.key );
//            JSONObject jasonObject = JSONObject.parseObject( result );
//
//            fin.close();
//            System.out.println( jasonObject + "---------" );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println( res_A + "---" );
//
//        return null;
//    }

    public static String red_conf() {
        String result = "";
        try {
            FileInputStream fin = new FileInputStream(
                    "/sdcard/MyDownLoad/version.conf" );
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read( buffer );
            res_A = new String( buffer, "UTF-8" );
            // result = AES.Decrypt(res_A, AES.key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println( res_A + "---" + res_A );
        return res_A;
    }

    public static String readVersion(String fileName) {
        try {
            FileInputStream fin = new FileInputStream(
                    fileName );
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read( buffer );
            res_A = new String( buffer, "UTF-8" );
            // result = AES.Decrypt(res_A, AES.key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println( res_A + "---" + res_A );
        return res_A;

    }

    public static String read_config(String red_conf) {
        try {
            FileInputStream fin = new FileInputStream( red_conf );
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read( buffer );
            res_A = new String( buffer, "UTF-8" );
            //String result = AES.Decrypt(res_A, AES.key);
            //JSONObject jasonObject = JSONObject.parseObject(result);
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
      //  System.out.println( res_A + "---" );

        return res_A;
    }
}
