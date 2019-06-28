package com.spd.bus;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.test.yinlianbarcode.interfaces.OnBackListener;
import com.example.test.yinlianbarcode.utils.ScanUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.honeywell.barcode.HSMDecodeComponent;
import com.honeywell.barcode.HSMDecoder;

import com.honeywell.camera.CameraManager;
import com.spd.base.been.tianjin.CardRecord;
import com.spd.base.db.DbDaoManage;
import com.spd.base.utils.DateUtils;
import com.spd.base.utils.LogUtils;
import com.spd.bus.entity.CityCodeTriff;
import com.spd.bus.entity.MobileApp;
import com.spd.bus.entity.Payrecord;
import com.spd.bus.entity.TransportCard;
import com.spd.bus.entity.UnionPay;
import com.spd.bus.entity.UnionQrKey;
import com.spd.bus.entity.White;
import com.spd.bus.spdata.been.PsamBeen;
import com.spd.bus.sql.SqlStatement;
import com.spd.bus.util.Base64Encrypt;
import com.spd.bus.util.Configurations;
import com.spd.bus.util.CrashHandler;
import com.spd.bus.util.CreateJsonConfig;
import com.spd.bus.util.HzjString;
import com.spd.bus.util.PlaySound;
import com.spd.bus.timer.HeartTimer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static com.honeywell.barcode.Symbology.QR;

@SuppressLint("SdCardPath")
public class MyApplication extends Application {
    private static HSMDecoder hsmDecoder;
    private HSMDecodeComponent hsmDecodeComponent;
    public static List<CardRecord> cardRecordList = new ArrayList<>();
    public static List<PsamBeen> psamDatas = new ArrayList<>();
    public static byte fSysSta = (byte) 0x01;
    public static final String FILENAME_INFO = "/sdcard/mydownload/info.conf";
    public static final String FILENAME_ICCARD = "/sdcard/mydownload/sijirecord.conf";
    public static final String FILENAME_UNION_APPID = "/sdcard/mydownload/appid.conf";
    public static final String FILENAME_CITYCODE = "/sdcard/mydownload/citycode.conf";
    //激活成功与否
    public static boolean isScanSuccess = false;
    /**
     * 单例
     */
    private static MyApplication m_application;

    public static MyApplication getInstance() {
        return m_application;
    }


    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(getApplicationContext());
        // 初始化ActiveAndroid
        ActiveAndroid.initialize(this);
        DbDaoManage.initDb(this);
//        CrashReport.initCrashReport(getApplicationContext(),"ca2f83cd2c",true);
        PlaySound.initSoundPool(this);

        initTable();

        new Thread(new Runnable() {
            @Override
            public void run() {
                initScanBards(getApplicationContext());
            }
        }).start();

        HeartTimer.getIntance(getApplicationContext()).initTimer();

    }

    //个别表进行初始化
    private void initTable() {
//        List<UnionPay> unionPayAll = SqlStatement.getUnionPayAll();
//        if (unionPayAll == null || unionPayAll.size() == 0) {
//            UnionPay unionPay = new UnionPay();
//            unionPay.setTag(0);
//            unionPay.save();
//        }
        List<TransportCard> list = new ArrayList<TransportCard>();
        list = SqlStatement.getParameterAll();
        int s = list.size();
        if (list == null || s == 0) {
            TransportCard tpcd = new TransportCard();
            tpcd.setBus_number("000000");
            tpcd.setDevice_number("17510850");
            tpcd.setInfo("00");
            tpcd.setPrice("0001");
            tpcd.setBlackversion("00000000");
            tpcd.setWhiteversion("00000000");
            tpcd.setSoftversion("1.0.2");
            tpcd.setBinversion("0");
            tpcd.save();
        }
        String key_list = "[{\"key_id\":0,\"public_key\":\"02AB2FDB0AB23506C48012642E1A572FC46B5D8B8EE3B92A602CC1109921F84B0E\"},{\"key_id\":1,\"public_key\":\"02EA95A096BB5BE9693635DCD2231D210E15B8803C10FFE5293B29A67251C3605B\"},{\"key_id\":2,\"public_key\":\"038824D92AEFA2B1EE8F349FA2C38DBB0D0EAB057B99FF2F1899BBFA1F29F1162B\"},{\"key_id\":3,\"public_key\":\"03B41B517513E03EBEE3DB4D57594E5A0F19688E9A5067AB23B6C61366634AF572\"},{\"key_id\":4,\"public_key\":\"0282ABB881685EC8082B816C84F0AF7BA714674B027617AA9C0BEA540F50B61245\"},{\"key_id\":5,\"public_key\":\"0275FE4E714229B1732E2CFDA44011F6718E9E2261B74AE3886C667D23AB21EB7F\"},{\"key_id\":6,\"public_key\":\"02D4ABD6277C42125EC93811FB75F02E8D29CE19F17B0118A06FB64DDDAD8F151D\"},{\"key_id\":7,\"public_key\":\"03C833BCD6FE464F2F923D3B87C4945E7F661D686A379B454306C79000ED3778B1\"},{\"key_id\":8,\"public_key\":\"0270E69E1719C835C307D16050141E0527DD5B99E025F7D5A91AEAB6DF95DC7606\"},{\"key_id\":9,\"public_key\":\"0230866CB7FA9800E779E873B8E9786295E851CF327905FF290123B91F2E4E4DE3\"},{\"key_id\":10,\"public_key\":\"03B469F0764B2CB93FAEDD604FA1CECB7AA33AAC6E217E06A4D6FA86749A51E6CC\"},{\"key_id\":11,\"public_key\":\"0365C74E6B3D53D2742CB7EF60DA78F3931BE28AC12E25DF05C449CD04D0AE096F\"},{\"key_id\":12,\"public_key\":\"0310109AED9B06D38AF10FA1729119E05C465578A3ADBCDFBEDDA723112E2DB13C\"},{\"key_id\":13,\"public_key\":\"034CB34A4D15F9114A3868CC6B6C9003F89682A8C45D8BBE44BD388CC0BDA2C1CE\"},{\"key_id\":14,\"public_key\":\"0366B8E3A4BBE12ED11C54B48A9E9B724A07CF8E98F78522B23EFD95D6A2EC00DD\"},{\"key_id\":15,\"public_key\":\"02E7079580CE071A928446E81428EC4873A2F8C879311687A466FDACD13F9DD29E\"},{\"key_id\":16,\"public_key\":\"02396D50BBCF15A40D3BBD06F7D7763D3E5795128F3FB1FEC0C95085CC1FE636E1\"},{\"key_id\":17,\"public_key\":\"02C06EA706FCC49F200BAE60FE5F85C519EE21620821B370CB815AB849BE22A9D5\"},{\"key_id\":18,\"public_key\":\"03383F60535555C5277BC5163F087C7EAE0D79CCC86B9DB96565D9A333404D638C\"},{\"key_id\":19,\"public_key\":\"039CB571D5C1398F340D01C380BE676B51B7BA4DACED0D22879A27403BB3F49D59\"},{\"key_id\":20,\"public_key\":\"03F6677074424BF61A9EF90663D91D9CC97A02E5462D0386FBCBED7FC111ECF12A\"},{\"key_id\":21,\"public_key\":\"028DC1E334FA617A711B9E5A5060E29DADA03AF0DB642B9264903224A3A92509A2\"},{\"key_id\":22,\"public_key\":\"02DF95E6C7491E0F90A2322075BD973FBCB2D163B92623BBE153F65814583F17C9\"},{\"key_id\":23,\"public_key\":\"0399F5E924A3C5B8ECFE6F3D1BE9B7C176BEBE6857F428849CB8E8BCDCE689D827\"},{\"key_id\":24,\"public_key\":\"03CA2BFD5C6B52D0E8D826378EF23A01839D36C60F76ECDB1BCC0B2E55E04251F0\"},{\"key_id\":25,\"public_key\":\"02557D5AF16A345815C2F3896535A7F969AA7BE9F0A300386FF0D637A7891001B0\"},{\"key_id\":26,\"public_key\":\"029AD06148C81E0025CB1685591513BE657A6A9A9BE9E83B2EE110221B26306EFC\"},{\"key_id\":27,\"public_key\":\"03871839999D6003D907E9884DC18928261845480C213E480D530E03CE46084087\"},{\"key_id\":28,\"public_key\":\"0379BE1176150C83D256F020D5B0A409A7615C99EBC7070F27E762900DA760F32F\"},{\"key_id\":29,\"public_key\":\"034BFD4A1E3E39C51BCC60E62DD3F80D8392A4418155309E50F2CDD94985FAD026\"},{\"key_id\":30,\"public_key\":\"03999A50E628878F0670DBBFE20F98919E4FEEBB00CFED98F0867C5313546693D4\"},{\"key_id\":31,\"public_key\":\"03079B27692B20A315D95A48DA2F7F2CCC0B80723F8A65DD13B3999706DE75F559\"}]";
        String card_type_list = "[\"T0120000\",\"ANT00001\"]";
        String Key = "{\"code\":\"00\",\"last_time\":\"20171201012500\",\"keys\":[{\"cert_format\":\"12\",\"org_id\":\"A0000001\",\"cert_expire_time\":\"1250\",\"cert_no\":\"1001\",\"sign_algorithm\":\"04\",\"encrypt_algorithm\":\"00\",\"parameter_id\":\"11\",\"public_key\":\"LiNTcxSdh91LnjVKa6YiRmhslzbwqOKq3/t0L29oqKGJHNka7Fs2IhwYAyeVe/Dsi92kOEkLizZFE/hpRg83gg==\",\"cert_sign\":\"Yr42PQ445bdcb/sUFCRdk4wA3mCGEnWQ4izwFp67m91zxpOya7igPInatFD/mhC7V1bHMGVjPuSI10hDB+3nuA==\",\"cert_seq\":\"101181\",\"publickey_length\":\"88\"},{\"cert_format\":\"12\",\"org_id\":\"A0000001\",\"cert_expire_time\":\"1250\",\"cert_no\":\"1002\",\"sign_algorithm\":\"04\",\"encrypt_algorithm\":\"00\",\"parameter_id\":\"11\",\"public_key\":\"jOiWwkTAIERZPfuOHpMsn3N+ms8SXPnWzLoDfQ+XWPqfaoAR2XpIgN3MP6XMShNdpcSOst7YLm9BI/a5u7zj+Q==\",\"cert_sign\":\"Fk51Keu8H/1xgaDm5ey1gGMPrrUP9MLHtn2QE+Kfm1crRgo2dwKF5CLDIfL0uID4ov9oCVsYhVpxi2qw1uPfIg==\",\"cert_seq\":\"101177\",\"publickey_length\":\"88\"},{\"cert_format\":\"12\",\"org_id\":\"A0000001\",\"cert_expire_time\":\"1250\",\"cert_no\":\"1003\",\"sign_algorithm\":\"04\",\"encrypt_algorithm\":\"00\",\"parameter_id\":\"11\",\"public_key\":\"SqXm4wkf21r7QLxPCHBvwmsuHAYqtBXPER6QgCT8nELi0YJzSEWCsXHEFHJZf3YBDDzlAwoe6b8vsPHPqvea3g==\",\"cert_sign\":\"lmBWeMrDkvqvhJs/wOFBiI24hHmCWFxLo5AHFowkThY12A7wuSG+4ihjWJhMmZLx6Ms+AaMEWdp0l9d93RO/kQ==\",\"cert_seq\":\"101189\",\"publickey_length\":\"88\"},{\"cert_format\":\"12\",\"org_id\":\"A0000001\",\"cert_expire_time\":\"1250\",\"cert_no\":\"1004\",\"sign_algorithm\":\"04\",\"encrypt_algorithm\":\"00\",\"parameter_id\":\"11\",\"public_key\":\"d7ZcFOoOrS7krskxi9D2un+Fh5DxvrY54jTUAbeYZwNN2CerSgvRPyPE9uJAv/SA7PhOhxR9iHhr1fBEC2X3bg==\",\"cert_sign\":\"2QEJMqTDMp02+3lFkC7lWiaoQp2gmlhF1t8rBh7GYMqzn0WaWC3KDdYSaG2HbFRPx5Hf/LHaDMNRyyIpgIvAjA==\",\"cert_seq\":\"101185\",\"publickey_length\":\"88\"},{\"cert_format\":\"12\",\"org_id\":\"A0000001\",\"cert_expire_time\":\"1250\",\"cert_no\":\"1005\",\"sign_algorithm\":\"04\",\"encrypt_algorithm\":\"00\",\"parameter_id\":\"11\",\"public_key\":\"GqVIh+X299DlV6MGGTbwGwEvp0Ofh2y52gzjACKXHp/E5wvaDa2OeX3qvTIetEe3Za/ZFpqHZAWCl+6lJBBmig==\",\"cert_sign\":\"ED/oSxeeZSKdEyNx90PrBAUc/CvRBA8n2w/u80aCmDfyMDQsyYKziqNxOo2x8S/ogbBZoNEBWtzxqhHtBTPtOw==\",\"cert_seq\":\"101197\",\"publickey_length\":\"88\"}]}";
        List<UnionQrKey> listUnionKey = new ArrayList<UnionQrKey>();
        listUnionKey = SqlStatement.getAllUnionKey();
        if (listUnionKey == null || listUnionKey.size() == 0) {
            JSONObject jsonObject = JSONObject.parseObject(Key);
            if (jsonObject.getString("code").equals("00")) {
                com.alibaba.fastjson.JSONArray jsonArray = jsonObject
                        .getJSONArray("keys");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObjects = (JSONObject) jsonArray.get(i);
                    UnionQrKey unKey = new UnionQrKey();
                    unKey.setCert_format(jsonObjects.getString("cert_format"));
                    unKey.setCert_expire_time(jsonObjects
                            .getString("cert_expire_time"));
                    unKey.setCert_no(jsonObjects.getString("cert_no"));
                    unKey.setCert_seq(jsonObjects.getString("cert_seq"));
                    unKey.setCert_sign(jsonObjects.getString("cert_sign"));
                    unKey.setEncrypt_algorithm(jsonObjects
                            .getString("encrypt_algorithm"));
                    unKey.setOrg_id(jsonObjects.getString("org_id"));
                    unKey.setParameter_id(jsonObjects.getString("parameter_id"));
                    unKey.setPublic_key(HzjString.bytesToHexString1(
                            Base64Encrypt.base64Decode(jsonObjects
                                    .getString("public_key"))).trim());
                    unKey.setPublickey_length(jsonObjects
                            .getString("publickey_length"));
                    unKey.setSign_algorithm(jsonObjects
                            .getString("sign_algorithm"));
                    unKey.save();
                }
            }
        }
        String cityCode = "{\"1\": {\"city\": \"邯郸\",\"issuer_code\": \"1581270\"},\"2\": {\"city\": \"保定\",\"issuer_code\": \"1701340\"},\"3\": {\"city\": \"沧州\",\"issuer_code\": \"1761430\"},\"4\": {\"city\": \"张家口\",\"issuer_code\": \"1711380\"},\"5\": {\"city\": \"承德\",\"issuer_code\": \"1751410\"},\"6\": {\"city\": \"廊坊\",\"issuer_code\": \"1721460\"},\"7\": {\"city\": \"石家庄\",\"issuer_code\": \"1171210\"},\"8\": {\"city\": \"北京\",\"issuer_code\": \"1011000\"},\"9\": {\"city\": \"天津\",\"issuer_code\": \"1131121\"},\"10\": {\"city\": \"石家庄\",\"issuer_code\": \"1691210\"},\"11\": {\"city\": \"唐山\",\"issuer_code\": \"1731240\"},\"12\": {\"city\": \"秦皇岛\",\"issuer_code\": \"1741260\"},\"13\": {\"city\": \"衡水\",\"issuer_code\": \"1771480\"},\"14\": {\"city\": \"邢台\",\"issuer_code\": \"1781310\"},\"15\": {\"city\": \"邯郸\",\"issuer_code\": \"1791270\"}}";
        List<CityCodeTriff> listTriff = new ArrayList<CityCodeTriff>();
        listTriff = SqlStatement.getTriffCity();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd1 HH:mm:ss");
        if (listTriff == null || listTriff.size() == 0) {
            for (int i = 1; i < 16; i++) {
                try {
                    JSONObject jsObject = JSONObject.parseObject(cityCode);
                    String jsb = jsObject.getString(String.valueOf(i));
                    JSONObject jsbObject = JSONObject.parseObject(jsb);
                    Date date = new Date(System.currentTimeMillis());
                    String order_time = sdf.format(date);
                    CityCodeTriff cTriff = new CityCodeTriff();
                    cTriff.setIssuer_code(jsbObject.getString("issuer_code"));
                    cTriff.setCity(jsbObject.getString("city"));
                    cTriff.setCreated_at(order_time);
                    cTriff.setUpdated_at(order_time);
                    cTriff.setIs_available("1");
                    cTriff.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        String whiteString = "[{\"_Id\":1,\"data\":\"00300083010\"},{\"_Id\":2,\"data\":\"00301011000\"},{\"_Id\":3,\"data\":\"00301013120\"},{\"_Id\":4,\"data\":\"00301023140\"},{\"_Id\":5,\"data\":\"00301063330\"},{\"_Id\":6,\"data\":\"00301092410\"},{\"_Id\":7,\"data\":\"00301108710\"},{\"_Id\":8,\"data\":\"00301131121\"},{\"_Id\":9,\"data\":\"00301135810\"},{\"_Id\":10,\"data\":\"00301154540\"},{\"_Id\":11,\"data\":\"00301168330\"},{\"_Id\":12,\"data\":\"00301171210\"},{\"_Id\":13,\"data\":\"00301182420\"},{\"_Id\":14,\"data\":\"00301192720\"},{\"_Id\":15,\"data\":\"00301207310\"},{\"_Id\":16,\"data\":\"00301213630\"},{\"_Id\":17,\"data\":\"00301256630\"},{\"_Id\":18,\"data\":\"00301273020\"},{\"_Id\":19,\"data\":\"00301283910\"},{\"_Id\":20,\"data\":\"00301294950\"},{\"_Id\":21,\"data\":\"00301303160\"},{\"_Id\":22,\"data\":\"00301314500\"},{\"_Id\":23,\"data\":\"00301321920\"},{\"_Id\":24,\"data\":\"00301333970\"},{\"_Id\":25,\"data\":\"00301343930\"},{\"_Id\":26,\"data\":\"00301355850\"},{\"_Id\":27,\"data\":\"00301384730\"},{\"_Id\":28,\"data\":\"00301396900\"},{\"_Id\":29,\"data\":\"00301403040\"},{\"_Id\":30,\"data\":\"00301413060\"},{\"_Id\":31,\"data\":\"00301423050\"},{\"_Id\":32,\"data\":\"00301433180\"},{\"_Id\":33,\"data\":\"00301443110\"},{\"_Id\":34,\"data\":\"00301453080\"},{\"_Id\":35,\"data\":\"00301463030\"},{\"_Id\":36,\"data\":\"00301473070\"},{\"_Id\":37,\"data\":\"00301482450\"},{\"_Id\":38,\"data\":\"00301493350\"},{\"_Id\":39,\"data\":\"00301513950\"},{\"_Id\":40,\"data\":\"00301523990\"},{\"_Id\":41,\"data\":\"00301533940\"},{\"_Id\":42,\"data\":\"00301544010\"},{\"_Id\":43,\"data\":\"00301554050\"},{\"_Id\":44,\"data\":\"00301564030\"},{\"_Id\":45,\"data\":\"00301573918\"},{\"_Id\":46,\"data\":\"00301581270\"},{\"_Id\":47,\"data\":\"00301592610\"},{\"_Id\":48,\"data\":\"00301616020\"},{\"_Id\":49,\"data\":\"00301621240\"},{\"_Id\":50,\"data\":\"00301642220\"},{\"_Id\":51,\"data\":\"00301656900\"},{\"_Id\":52,\"data\":\"00301663310\"},{\"_Id\":53,\"data\":\"00301691210\"},{\"_Id\":54,\"data\":\"00301701340\"},{\"_Id\":55,\"data\":\"00301711380\"},{\"_Id\":56,\"data\":\"00301721460\"},{\"_Id\":57,\"data\":\"00301731240\"},{\"_Id\":58,\"data\":\"00301741260\"},{\"_Id\":59,\"data\":\"00301751410\"},{\"_Id\":60,\"data\":\"00301761430\"},{\"_Id\":61,\"data\":\"00301771480\"},{\"_Id\":62,\"data\":\"00301781310\"},{\"_Id\":63,\"data\":\"00301791270\"},{\"_Id\":64,\"data\":\"00301817410\"},{\"_Id\":65,\"data\":\"00301827510\"},{\"_Id\":66,\"data\":\"00301837360\"},{\"_Id\":67,\"data\":\"00301847380\"},{\"_Id\":68,\"data\":\"00301857530\"},{\"_Id\":69,\"data\":\"00301867340\"},{\"_Id\":70,\"data\":\"00301877450\"},{\"_Id\":71,\"data\":\"00301887430\"},{\"_Id\":72,\"data\":\"00301897470\"},{\"_Id\":73,\"data\":\"00301907580\"},{\"_Id\":74,\"data\":\"00301917490\"},{\"_Id\":75,\"data\":\"00301927570\"},{\"_Id\":76,\"data\":\"00301937550\"},{\"_Id\":77,\"data\":\"00301947540\"},{\"_Id\":78,\"data\":\"00301957560\"},{\"_Id\":79,\"data\":\"00301967950\"},{\"_Id\":80,\"data\":\"00301984520\"},{\"_Id\":81,\"data\":\"00301994630\"},{\"_Id\":82,\"data\":\"00302002900\"},{\"_Id\":83,\"data\":\"00302017910\"},{\"_Id\":84,\"data\":\"00302027930\"},{\"_Id\":85,\"data\":\"00302037970\"},{\"_Id\":86,\"data\":\"00302047920\"},{\"_Id\":87,\"data\":\"00302068040\"},{\"_Id\":88,\"data\":\"00302077990\"},{\"_Id\":89,\"data\":\"00302104560\"},{\"_Id\":90,\"data\":\"00302113450\"},{\"_Id\":91,\"data\":\"00302127030\"},{\"_Id\":92,\"data\":\"00302135030\"},{\"_Id\":93,\"data\":\"00302156410\"},{\"_Id\":94,\"data\":\"00302184280\"},{\"_Id\":95,\"data\":\"00302195510\"},{\"_Id\":96,\"data\":\"00302215840\"},{\"_Id\":97,\"data\":\"00302222410\"},{\"_Id\":98,\"data\":\"00302234650\"},{\"_Id\":99,\"data\":\"00302246650\"},{\"_Id\":100,\"data\":\"00302273610\"},{\"_Id\":101,\"data\":\"00302293740\"},{\"_Id\":102,\"data\":\"00302303720\"},{\"_Id\":103,\"data\":\"00302313750\"},{\"_Id\":104,\"data\":\"00302323650\"},{\"_Id\":105,\"data\":\"00302333620\"},{\"_Id\":106,\"data\":\"00302353680\"},{\"_Id\":107,\"data\":\"00302393710\"},{\"_Id\":108,\"data\":\"00302403640\"},{\"_Id\":109,\"data\":\"00302428800\"},{\"_Id\":110,\"data\":\"00302448800\"},{\"_Id\":111,\"data\":\"00302478800\"},{\"_Id\":112,\"data\":\"00302498800\"},{\"_Id\":113,\"data\":\"00302518800\"},{\"_Id\":114,\"data\":\"00302528800\"},{\"_Id\":115,\"data\":\"00302538800\"},{\"_Id\":116,\"data\":\"00302548800\"},{\"_Id\":117,\"data\":\"00302555010\"},{\"_Id\":118,\"data\":\"00302564960\"},{\"_Id\":119,\"data\":\"00302575110\"},{\"_Id\":120,\"data\":\"00302594920\"},{\"_Id\":121,\"data\":\"00302605017\"},{\"_Id\":122,\"data\":\"00302614980\"},{\"_Id\":123,\"data\":\"00302625060\"},{\"_Id\":124,\"data\":\"00302635040\"},{\"_Id\":125,\"data\":\"00302644930\"},{\"_Id\":126,\"data\":\"00302655020\"},{\"_Id\":127,\"data\":\"00302665150\"},{\"_Id\":128,\"data\":\"00302697010\"},{\"_Id\":129,\"data\":\"00302707020\"},{\"_Id\":130,\"data\":\"00302717110\"},{\"_Id\":131,\"data\":\"00302727090\"},{\"_Id\":132,\"data\":\"00302757130\"},{\"_Id\":133,\"data\":\"00302767150\"},{\"_Id\":134,\"data\":\"00302785950\"},{\"_Id\":135,\"data\":\"00302805890\"},{\"_Id\":136,\"data\":\"00302825930\"},{\"_Id\":137,\"data\":\"00302865980\"},{\"_Id\":138,\"data\":\"00302885820\"},{\"_Id\":139,\"data\":\"00302896050\"},{\"_Id\":140,\"data\":\"00302926060\"},{\"_Id\":141,\"data\":\"00302937010\"},{\"_Id\":142,\"data\":\"00302942460\"},{\"_Id\":143,\"data\":\"00302952424\"},{\"_Id\":144,\"data\":\"00302971610\"},{\"_Id\":145,\"data\":\"00302981620\"},{\"_Id\":146,\"data\":\"00302991650\"},{\"_Id\":147,\"data\":\"00303001660\"},{\"_Id\":148,\"data\":\"00303011680\"},{\"_Id\":149,\"data\":\"00303021690\"},{\"_Id\":150,\"data\":\"00303031710\"},{\"_Id\":151,\"data\":\"00303041730\"},{\"_Id\":152,\"data\":\"00303051750\"},{\"_Id\":153,\"data\":\"00303061770\"},{\"_Id\":154,\"data\":\"00303071810\"},{\"_Id\":155,\"data\":\"00303081910\"},{\"_Id\":156,\"data\":\"00303091930\"},{\"_Id\":157,\"data\":\"00303101940\"},{\"_Id\":158,\"data\":\"00303111960\"},{\"_Id\":159,\"data\":\"00303121980\"},{\"_Id\":160,\"data\":\"00303131990\"},{\"_Id\":161,\"data\":\"00303142010\"},{\"_Id\":162,\"data\":\"00303152030\"},{\"_Id\":163,\"data\":\"00303162050\"},{\"_Id\":164,\"data\":\"00303172070\"},{\"_Id\":165,\"data\":\"00303182080\"},{\"_Id\":166,\"data\":\"00303192230\"},{\"_Id\":167,\"data\":\"00303222260\"},{\"_Id\":168,\"data\":\"00303232270\"},{\"_Id\":169,\"data\":\"00303242280\"},{\"_Id\":170,\"data\":\"00303272320\"},{\"_Id\":171,\"data\":\"00303282330\"},{\"_Id\":172,\"data\":\"00303292340\"},{\"_Id\":173,\"data\":\"00303302360\"},{\"_Id\":174,\"data\":\"00303322430\"},{\"_Id\":175,\"data\":\"00303332440\"},{\"_Id\":176,\"data\":\"00303342470\"},{\"_Id\":177,\"data\":\"00303362510\"},{\"_Id\":178,\"data\":\"00303372640\"},{\"_Id\":179,\"data\":\"00303382670\"},{\"_Id\":180,\"data\":\"00303392680\"},{\"_Id\":181,\"data\":\"00303402690\"},{\"_Id\":182,\"data\":\"00303412710\"},{\"_Id\":183,\"data\":\"00303422740\"},{\"_Id\":184,\"data\":\"00303432750\"},{\"_Id\":185,\"data\":\"00303442760\"},{\"_Id\":186,\"data\":\"00303452780\"},{\"_Id\":187,\"data\":\"00303462790\"},{\"_Id\":188,\"data\":\"00303473320\"},{\"_Id\":189,\"data\":\"00303493370\"},{\"_Id\":190,\"data\":\"00303523430\"},{\"_Id\":191,\"data\":\"00303554210\"},{\"_Id\":192,\"data\":\"00303584240\"},{\"_Id\":193,\"data\":\"00303634350\"},{\"_Id\":194,\"data\":\"00303664530\"},{\"_Id\":195,\"data\":\"00303714680\"},{\"_Id\":196,\"data\":\"00303734750\"},{\"_Id\":197,\"data\":\"00303765210\"},{\"_Id\":198,\"data\":\"00303775220\"},{\"_Id\":199,\"data\":\"00303805270\"},{\"_Id\":200,\"data\":\"00303815280\"},{\"_Id\":201,\"data\":\"00303825310\"},{\"_Id\":202,\"data\":\"00303835320\"},{\"_Id\":203,\"data\":\"00303845330\"},{\"_Id\":204,\"data\":\"00303865360\"},{\"_Id\":205,\"data\":\"00303895520\"},{\"_Id\":206,\"data\":\"00303905530\"},{\"_Id\":207,\"data\":\"00303915540\"},{\"_Id\":208,\"data\":\"00303985630\"},{\"_Id\":209,\"data\":\"00304026110\"},{\"_Id\":210,\"data\":\"00304036140\"},{\"_Id\":211,\"data\":\"00304066230\"},{\"_Id\":212,\"data\":\"00304106310\"},{\"_Id\":213,\"data\":\"00304116320\"},{\"_Id\":214,\"data\":\"00304166420\"},{\"_Id\":215,\"data\":\"00304176510\"},{\"_Id\":216,\"data\":\"00304206570\"},{\"_Id\":217,\"data\":\"00304226590\"},{\"_Id\":218,\"data\":\"00304286730\"},{\"_Id\":219,\"data\":\"00304357710\"},{\"_Id\":220,\"data\":\"00304387760\"},{\"_Id\":221,\"data\":\"00304428210\"},{\"_Id\":222,\"data\":\"00304438220\"},{\"_Id\":223,\"data\":\"00304448230\"},{\"_Id\":224,\"data\":\"00304458240\"},{\"_Id\":225,\"data\":\"00304468250\"},{\"_Id\":226,\"data\":\"00304478260\"},{\"_Id\":227,\"data\":\"00304488270\"},{\"_Id\":228,\"data\":\"00304498280\"},{\"_Id\":229,\"data\":\"00304508290\"},{\"_Id\":230,\"data\":\"00304518310\"},{\"_Id\":231,\"data\":\"00304528340\"},{\"_Id\":232,\"data\":\"00304538360\"},{\"_Id\":233,\"data\":\"00304548380\"},{\"_Id\":234,\"data\":\"00304558510\"},{\"_Id\":235,\"data\":\"00304568520\"},{\"_Id\":236,\"data\":\"00304638720\"},{\"_Id\":237,\"data\":\"00304648730\"},{\"_Id\":238,\"data\":\"00304658740\"},{\"_Id\":239,\"data\":\"00304678810\"},{\"_Id\":240,\"data\":\"00304815375\"},{\"_Id\":241,\"data\":\"00304827310\"},{\"_Id\":242,\"data\":\"00304832411\"},{\"_Id\":243,\"data\":\"00304842412\"},{\"_Id\":244,\"data\":\"00304852413\"},{\"_Id\":245,\"data\":\"00304862415\"},{\"_Id\":246,\"data\":\"00304872421\"},{\"_Id\":247,\"data\":\"00304882422\"},{\"_Id\":248,\"data\":\"00304892423\"},{\"_Id\":249,\"data\":\"00304902425\"},{\"_Id\":250,\"data\":\"00304912431\"},{\"_Id\":251,\"data\":\"00304922432\"},{\"_Id\":252,\"data\":\"00304932433\"},{\"_Id\":253,\"data\":\"00304942434\"},{\"_Id\":254,\"data\":\"00304952441\"},{\"_Id\":255,\"data\":\"00304962442\"},{\"_Id\":256,\"data\":\"00304972451\"},{\"_Id\":257,\"data\":\"00304982452\"},{\"_Id\":258,\"data\":\"00304992453\"},{\"_Id\":259,\"data\":\"00305002454\"},{\"_Id\":260,\"data\":\"00305012455\"},{\"_Id\":261,\"data\":\"00305022461\"},{\"_Id\":262,\"data\":\"00305032462\"},{\"_Id\":263,\"data\":\"00305042464\"},{\"_Id\":264,\"data\":\"00305052472\"},{\"_Id\":265,\"data\":\"00305062474\"},{\"_Id\":266,\"data\":\"00305072477\"},{\"_Id\":267,\"data\":\"00305082478\"},{\"_Id\":268,\"data\":\"00305092491\"},{\"_Id\":269,\"data\":\"00305102492\"},{\"_Id\":270,\"data\":\"00305112493\"},{\"_Id\":271,\"data\":\"00305122494\"},{\"_Id\":272,\"data\":\"00305132495\"},{\"_Id\":273,\"data\":\"00305142496\"},{\"_Id\":274,\"data\":\"00305152497\"},{\"_Id\":275,\"data\":\"00305162498\"},{\"_Id\":276,\"data\":\"00305172511\"},{\"_Id\":277,\"data\":\"00305182512\"},{\"_Id\":278,\"data\":\"00305192514\"},{\"_Id\":279,\"data\":\"00305202400\"},{\"_Id\":280,\"data\":\"00305212482\"},{\"_Id\":281,\"data\":\"00305222466\"},{\"_Id\":282,\"data\":\"00305236421\"},{\"_Id\":283,\"data\":\"00305246423\"},{\"_Id\":284,\"data\":\"00305256424\"},{\"_Id\":285,\"data\":\"00305266425\"},{\"_Id\":286,\"data\":\"00305276426\"},{\"_Id\":287,\"data\":\"00305286427\"},{\"_Id\":288,\"data\":\"00305296428\"},{\"_Id\":289,\"data\":\"00305306429\"},{\"_Id\":290,\"data\":\"00305316431\"},{\"_Id\":291,\"data\":\"00305326432\"},{\"_Id\":292,\"data\":\"00305336433\"},{\"_Id\":293,\"data\":\"00305346434\"},{\"_Id\":294,\"data\":\"00305356435\"},{\"_Id\":295,\"data\":\"00305366436\"},{\"_Id\":296,\"data\":\"00305376437\"},{\"_Id\":297,\"data\":\"00305386438\"},{\"_Id\":298,\"data\":\"00305396440\"},{\"_Id\":299,\"data\":\"00305406400\"},{\"_Id\":300,\"data\":\"00305437972\"},{\"_Id\":301,\"data\":\"00305447090\"},{\"_Id\":302,\"data\":\"0010004ffff\"},{\"_Id\":303,\"data\":\"0010005ffff\"},{\"_Id\":304,\"data\":\"0011100ffff\"},{\"_Id\":305,\"data\":\"0011120ffff\"},{\"_Id\":306,\"data\":\"0011130ffff\"},{\"_Id\":307,\"data\":\"0011150ffff\"},{\"_Id\":308,\"data\":\"0011170ffff\"},{\"_Id\":309,\"data\":\"0011210ffff\"},{\"_Id\":310,\"data\":\"0011250ffff\"},{\"_Id\":311,\"data\":\"0011251ffff\"},{\"_Id\":312,\"data\":\"0011362ffff\"},{\"_Id\":313,\"data\":\"0011380ffff\"},{\"_Id\":314,\"data\":\"0012140ffff\"},{\"_Id\":315,\"data\":\"0012142ffff\"},{\"_Id\":316,\"data\":\"0012144ffff\"},{\"_Id\":317,\"data\":\"0012153ffff\"},{\"_Id\":318,\"data\":\"0012154ffff\"},{\"_Id\":319,\"data\":\"0012155ffff\"},{\"_Id\":320,\"data\":\"0012230ffff\"},{\"_Id\":321,\"data\":\"0012253ffff\"},{\"_Id\":322,\"data\":\"0012260ffff\"},{\"_Id\":323,\"data\":\"0012320ffff\"},{\"_Id\":324,\"data\":\"0013000ffff\"},{\"_Id\":325,\"data\":\"0013120ffff\"},{\"_Id\":326,\"data\":\"0013130ffff\"},{\"_Id\":327,\"data\":\"0013131ffff\"},{\"_Id\":328,\"data\":\"0013140ffff\"},{\"_Id\":329,\"data\":\"0013150ffff\"},{\"_Id\":330,\"data\":\"0013160ffff\"},{\"_Id\":331,\"data\":\"0013180ffff\"},{\"_Id\":332,\"data\":\"0013210ffff\"},{\"_Id\":333,\"data\":\"0013220ffff\"},{\"_Id\":334,\"data\":\"0013250ffff\"},{\"_Id\":335,\"data\":\"0013300ffff\"},{\"_Id\":336,\"data\":\"0013340ffff\"},{\"_Id\":337,\"data\":\"0013320ffff\"},{\"_Id\":338,\"data\":\"0013350ffff\"},{\"_Id\":339,\"data\":\"0013410ffff\"},{\"_Id\":340,\"data\":\"0013500ffff\"},{\"_Id\":341,\"data\":\"0013511ffff\"},{\"_Id\":342,\"data\":\"0013620ffff\"},{\"_Id\":343,\"data\":\"0013622ffff\"},{\"_Id\":344,\"data\":\"0013640ffff\"},{\"_Id\":345,\"data\":\"0014102ffff\"},{\"_Id\":346,\"data\":\"0014250ffff\"},{\"_Id\":347,\"data\":\"0014331ffff\"},{\"_Id\":348,\"data\":\"0014420ffff\"},{\"_Id\":349,\"data\":\"0014500ffff\"},{\"_Id\":350,\"data\":\"0014501ffff\"},{\"_Id\":351,\"data\":\"0014511ffff\"},{\"_Id\":352,\"data\":\"0014620ffff\"},{\"_Id\":353,\"data\":\"0014630ffff\"},{\"_Id\":354,\"data\":\"0014670ffff\"},{\"_Id\":355,\"data\":\"0014730ffff\"},{\"_Id\":356,\"data\":\"0015190ffff\"},{\"_Id\":357,\"data\":\"0015240ffff\"},{\"_Id\":358,\"data\":\"0015580ffff\"},{\"_Id\":359,\"data\":\"0015630ffff\"},{\"_Id\":360,\"data\":\"0015720ffff\"},{\"_Id\":361,\"data\":\"0016150ffff\"},{\"_Id\":362,\"data\":\"0016217ffff\"},{\"_Id\":363,\"data\":\"0016374ffff\"},{\"_Id\":364,\"data\":\"0016430ffff\"},{\"_Id\":365,\"data\":\"0016500ffff\"},{\"_Id\":366,\"data\":\"0016710ffff\"},{\"_Id\":367,\"data\":\"0017120ffff\"},{\"_Id\":368,\"data\":\"0017121ffff\"},{\"_Id\":369,\"data\":\"0017140ffff\"},{\"_Id\":370,\"data\":\"0017190ffff\"},{\"_Id\":371,\"data\":\"0017300ffff\"},{\"_Id\":372,\"data\":\"0017309ffff\"},{\"_Id\":373,\"data\":\"0017441ffff\"},{\"_Id\":374,\"data\":\"0018340ffff\"},{\"_Id\":375,\"data\":\"0019001ffff\"}]";
        List<White> whites = SqlStatement.selectWhite();
        if (whites == null || whites.size() == 0) {
            final Gson gson = new GsonBuilder().serializeNulls().create();
            //Json的解析类对象
            JsonParser parser = new JsonParser();
            //将JSON的String 转成一个JsonArray对象
            JsonArray jsonArray = parser.parse(whiteString).getAsJsonArray();
            //加强for循环遍历JsonArray
            for (JsonElement user : jsonArray) {
                //使用GSON，直接转成Bean对象
                String data = user.getAsJsonObject().get("data").getAsString();
                White white = new White();
                white.setData(data);
                white.save();
            }
        }


        List<MobileApp> listAppid = new ArrayList<MobileApp>();
        listAppid = SqlStatement.getAppinfo();
        if (listAppid == null || listAppid.size() == 0) {
            MobileApp mApp = new MobileApp();
            mApp.setName("云闪付");
            mApp.setAppid("a310120180000028");
            mApp.setVersion("0");
            mApp.setAddress("天津");
            mApp.save();
        }
        if (listAppid.size() < 2) {
            MobileApp mApp = new MobileApp();
            mApp.setName("中国银行");
            mApp.setAppid("a120120180000033");

            mApp.setVersion("0");
            mApp.setAddress("天津");
            mApp.save();
        }
        File file = new File(FILENAME_INFO);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (Configurations.read_config(FILENAME_INFO) == null
                || Configurations.read_config(FILENAME_INFO).equals("")
                && !list.get(0).getInfo().equals("00")) {
            //备份司机记录
            List<Payrecord> listRecord = SqlStatement.ReciprocalDriverRecord();
            if (listRecord.size() != 0) {
                Configurations.writlog(listRecord.get(0).getRecord(),
                        MyApplication.FILENAME_ICCARD);
                // 备份参数列表info
                //list = SqlStatement.getParameterAll();
                Configurations.writlog(CreateJsonConfig.jsonInfo(list.get(0).getBus_number()
                        , list.get(0).getDevice_number()
                        , list.get(0).getPrice()
                        , list.get(0).getInfo())
                        , FILENAME_INFO);
                //备份城市代码
                Configurations.writlog(cityCode, FILENAME_CITYCODE);
            }


        }

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public static HSMDecoder getHSMDecoder() {
        return hsmDecoder;
    }

    public static void setHsmDecoder(HSMDecoder hsmDecoder) {
        MyApplication.hsmDecoder = hsmDecoder;
    }

    /**
     * 初始化银联二维码支付
     */
    public void initScanBards(Context context) {
        hsmDecoder = HSMDecoder.getInstance(getApplicationContext());
        hsmDecoder.enableAimer(false);
        hsmDecoder.setOverlayText("");
        hsmDecoder.enableSound(false);
        hsmDecoder.enableSymbology(QR);
        CameraManager cameraManager = CameraManager.getInstance(getApplicationContext());
        ScanUtils.activateScan(context, new OnBackListener() {
            @Override
            public void onBack() {
                MyApplication.getHSMDecoder().enableSymbology(QR);
                isScanSuccess = true;
                successCallBack();
                Toast.makeText(context, "激活成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                MyApplication.getHSMDecoder().enableSymbology(QR);
                isScanSuccess = false;
                errorCallBack();
                Toast.makeText(context, "激活失败！", Toast.LENGTH_SHORT).show();
            }
        }, false);
    }


    @Override
    public void onTerminate() {
        LogUtils.i("onTerminate:    application 结束");
        HSMDecoder.disposeInstance();
        HeartTimer.getIntance(getApplicationContext()).dispose();
        //清理
        ActiveAndroid.dispose();
        super.onTerminate();
    }

    private static InitDevListener initDevListener;

    public interface InitDevListener {
        void onSuccess();

        void onError();
    }

    public static void setInitDevListener(InitDevListener initDevListener) {
        MyApplication.initDevListener = initDevListener;
    }

    private void errorCallBack() {
        initDevListener.onError();
    }

    private void successCallBack() {
        initDevListener.onSuccess();
    }
}
