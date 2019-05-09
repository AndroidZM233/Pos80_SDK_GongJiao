package com.spd.bus.util;

import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.spd.base.been.tianjin.CityCode;
import com.spd.base.been.tianjin.White;
import com.spd.base.db.DbDaoManage;
import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.DateUtils;
import com.spd.base.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张明_ on 2019/3/28.
 * Email 741183142@qq.com
 */
public class ConfigUtils {
    /**
     * 读取本地是否有参数配置备份文件
     */
    public static void loadTxtConfig() {
        List<RunParaFile> runParaFiles = DbDaoManage.getDaoSession().getRunParaFileDao().loadAll();
        if (runParaFiles == null || runParaFiles.size() == 0) {
            String content = FileUtils.readFileContent(Environment
                    .getExternalStorageDirectory() + "/card.txt").toString();
            if (!TextUtils.isEmpty(content)) {
                Gson gson = new Gson();
                RunParaFile runParaFile = gson.fromJson(content, RunParaFile.class);
                DbDaoManage.getDaoSession().getRunParaFileDao().insert(runParaFile);
            }
        }
    }

    public static void logWrite(String content) {
        content = DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss) + content;
        FileUtils.writeFile(Environment.getExternalStorageDirectory() + "/busLog.txt"
                , content + "\n", true);
    }

    public static void jsonToDB() {
        String white = "[{\"_Id\":1,\"data\":\"00300083010\"},{\"_Id\":2,\"data\":\"00301011000\"},{\"_Id\":3,\"data\":\"00301013120\"},{\"_Id\":4,\"data\":\"00301023140\"},{\"_Id\":5,\"data\":\"00301063330\"},{\"_Id\":6,\"data\":\"00301092410\"},{\"_Id\":7,\"data\":\"00301108710\"},{\"_Id\":8,\"data\":\"00301131121\"},{\"_Id\":9,\"data\":\"00301135810\"},{\"_Id\":10,\"data\":\"00301154540\"},{\"_Id\":11,\"data\":\"00301168330\"},{\"_Id\":12,\"data\":\"00301171210\"},{\"_Id\":13,\"data\":\"00301182420\"},{\"_Id\":14,\"data\":\"00301192720\"},{\"_Id\":15,\"data\":\"00301207310\"},{\"_Id\":16,\"data\":\"00301213630\"},{\"_Id\":17,\"data\":\"00301256630\"},{\"_Id\":18,\"data\":\"00301273020\"},{\"_Id\":19,\"data\":\"00301283910\"},{\"_Id\":20,\"data\":\"00301294950\"},{\"_Id\":21,\"data\":\"00301303160\"},{\"_Id\":22,\"data\":\"00301314500\"},{\"_Id\":23,\"data\":\"00301321920\"},{\"_Id\":24,\"data\":\"00301333970\"},{\"_Id\":25,\"data\":\"00301343930\"},{\"_Id\":26,\"data\":\"00301355850\"},{\"_Id\":27,\"data\":\"00301384730\"},{\"_Id\":28,\"data\":\"00301396900\"},{\"_Id\":29,\"data\":\"00301403040\"},{\"_Id\":30,\"data\":\"00301413060\"},{\"_Id\":31,\"data\":\"00301423050\"},{\"_Id\":32,\"data\":\"00301433180\"},{\"_Id\":33,\"data\":\"00301443110\"},{\"_Id\":34,\"data\":\"00301453080\"},{\"_Id\":35,\"data\":\"00301463030\"},{\"_Id\":36,\"data\":\"00301473070\"},{\"_Id\":37,\"data\":\"00301482450\"},{\"_Id\":38,\"data\":\"00301493350\"},{\"_Id\":39,\"data\":\"00301513950\"},{\"_Id\":40,\"data\":\"00301523990\"},{\"_Id\":41,\"data\":\"00301533940\"},{\"_Id\":42,\"data\":\"00301544010\"},{\"_Id\":43,\"data\":\"00301554050\"},{\"_Id\":44,\"data\":\"00301564030\"},{\"_Id\":45,\"data\":\"00301573918\"},{\"_Id\":46,\"data\":\"00301581270\"},{\"_Id\":47,\"data\":\"00301592610\"},{\"_Id\":48,\"data\":\"00301616020\"},{\"_Id\":49,\"data\":\"00301621240\"},{\"_Id\":50,\"data\":\"00301642220\"},{\"_Id\":51,\"data\":\"00301656900\"},{\"_Id\":52,\"data\":\"00301663310\"},{\"_Id\":53,\"data\":\"00301691210\"},{\"_Id\":54,\"data\":\"00301701340\"},{\"_Id\":55,\"data\":\"00301711380\"},{\"_Id\":56,\"data\":\"00301721460\"},{\"_Id\":57,\"data\":\"00301731240\"},{\"_Id\":58,\"data\":\"00301741260\"},{\"_Id\":59,\"data\":\"00301751410\"},{\"_Id\":60,\"data\":\"00301761430\"},{\"_Id\":61,\"data\":\"00301771480\"},{\"_Id\":62,\"data\":\"00301781310\"},{\"_Id\":63,\"data\":\"00301791270\"},{\"_Id\":64,\"data\":\"00301817410\"},{\"_Id\":65,\"data\":\"00301827510\"},{\"_Id\":66,\"data\":\"00301837360\"},{\"_Id\":67,\"data\":\"00301847380\"},{\"_Id\":68,\"data\":\"00301857530\"},{\"_Id\":69,\"data\":\"00301867340\"},{\"_Id\":70,\"data\":\"00301877450\"},{\"_Id\":71,\"data\":\"00301887430\"},{\"_Id\":72,\"data\":\"00301897470\"},{\"_Id\":73,\"data\":\"00301907580\"},{\"_Id\":74,\"data\":\"00301917490\"},{\"_Id\":75,\"data\":\"00301927570\"},{\"_Id\":76,\"data\":\"00301937550\"},{\"_Id\":77,\"data\":\"00301947540\"},{\"_Id\":78,\"data\":\"00301957560\"},{\"_Id\":79,\"data\":\"00301967950\"},{\"_Id\":80,\"data\":\"00301984520\"},{\"_Id\":81,\"data\":\"00301994630\"},{\"_Id\":82,\"data\":\"00302002900\"},{\"_Id\":83,\"data\":\"00302017910\"},{\"_Id\":84,\"data\":\"00302027930\"},{\"_Id\":85,\"data\":\"00302037970\"},{\"_Id\":86,\"data\":\"00302047920\"},{\"_Id\":87,\"data\":\"00302068040\"},{\"_Id\":88,\"data\":\"00302077990\"},{\"_Id\":89,\"data\":\"00302104560\"},{\"_Id\":90,\"data\":\"00302113450\"},{\"_Id\":91,\"data\":\"00302127030\"},{\"_Id\":92,\"data\":\"00302135030\"},{\"_Id\":93,\"data\":\"00302156410\"},{\"_Id\":94,\"data\":\"00302184280\"},{\"_Id\":95,\"data\":\"00302195510\"},{\"_Id\":96,\"data\":\"00302215840\"},{\"_Id\":97,\"data\":\"00302222410\"},{\"_Id\":98,\"data\":\"00302234650\"},{\"_Id\":99,\"data\":\"00302246650\"},{\"_Id\":100,\"data\":\"00302273610\"},{\"_Id\":101,\"data\":\"00302293740\"},{\"_Id\":102,\"data\":\"00302303720\"},{\"_Id\":103,\"data\":\"00302313750\"},{\"_Id\":104,\"data\":\"00302323650\"},{\"_Id\":105,\"data\":\"00302333620\"},{\"_Id\":106,\"data\":\"00302353680\"},{\"_Id\":107,\"data\":\"00302393710\"},{\"_Id\":108,\"data\":\"00302403640\"},{\"_Id\":109,\"data\":\"00302428800\"},{\"_Id\":110,\"data\":\"00302448800\"},{\"_Id\":111,\"data\":\"00302478800\"},{\"_Id\":112,\"data\":\"00302498800\"},{\"_Id\":113,\"data\":\"00302518800\"},{\"_Id\":114,\"data\":\"00302528800\"},{\"_Id\":115,\"data\":\"00302538800\"},{\"_Id\":116,\"data\":\"00302548800\"},{\"_Id\":117,\"data\":\"00302555010\"},{\"_Id\":118,\"data\":\"00302564960\"},{\"_Id\":119,\"data\":\"00302575110\"},{\"_Id\":120,\"data\":\"00302594920\"},{\"_Id\":121,\"data\":\"00302605017\"},{\"_Id\":122,\"data\":\"00302614980\"},{\"_Id\":123,\"data\":\"00302625060\"},{\"_Id\":124,\"data\":\"00302635040\"},{\"_Id\":125,\"data\":\"00302644930\"},{\"_Id\":126,\"data\":\"00302655020\"},{\"_Id\":127,\"data\":\"00302665150\"},{\"_Id\":128,\"data\":\"00302697010\"},{\"_Id\":129,\"data\":\"00302707020\"},{\"_Id\":130,\"data\":\"00302717110\"},{\"_Id\":131,\"data\":\"00302727090\"},{\"_Id\":132,\"data\":\"00302757130\"},{\"_Id\":133,\"data\":\"00302767150\"},{\"_Id\":134,\"data\":\"00302785950\"},{\"_Id\":135,\"data\":\"00302805890\"},{\"_Id\":136,\"data\":\"00302825930\"},{\"_Id\":137,\"data\":\"00302865980\"},{\"_Id\":138,\"data\":\"00302885820\"},{\"_Id\":139,\"data\":\"00302896050\"},{\"_Id\":140,\"data\":\"00302926060\"},{\"_Id\":141,\"data\":\"00302937010\"},{\"_Id\":142,\"data\":\"00302942460\"},{\"_Id\":143,\"data\":\"00302952424\"},{\"_Id\":144,\"data\":\"00302971610\"},{\"_Id\":145,\"data\":\"00302981620\"},{\"_Id\":146,\"data\":\"00302991650\"},{\"_Id\":147,\"data\":\"00303001660\"},{\"_Id\":148,\"data\":\"00303011680\"},{\"_Id\":149,\"data\":\"00303021690\"},{\"_Id\":150,\"data\":\"00303031710\"},{\"_Id\":151,\"data\":\"00303041730\"},{\"_Id\":152,\"data\":\"00303051750\"},{\"_Id\":153,\"data\":\"00303061770\"},{\"_Id\":154,\"data\":\"00303071810\"},{\"_Id\":155,\"data\":\"00303081910\"},{\"_Id\":156,\"data\":\"00303091930\"},{\"_Id\":157,\"data\":\"00303101940\"},{\"_Id\":158,\"data\":\"00303111960\"},{\"_Id\":159,\"data\":\"00303121980\"},{\"_Id\":160,\"data\":\"00303131990\"},{\"_Id\":161,\"data\":\"00303142010\"},{\"_Id\":162,\"data\":\"00303152030\"},{\"_Id\":163,\"data\":\"00303162050\"},{\"_Id\":164,\"data\":\"00303172070\"},{\"_Id\":165,\"data\":\"00303182080\"},{\"_Id\":166,\"data\":\"00303192230\"},{\"_Id\":167,\"data\":\"00303222260\"},{\"_Id\":168,\"data\":\"00303232270\"},{\"_Id\":169,\"data\":\"00303242280\"},{\"_Id\":170,\"data\":\"00303272320\"},{\"_Id\":171,\"data\":\"00303282330\"},{\"_Id\":172,\"data\":\"00303292340\"},{\"_Id\":173,\"data\":\"00303302360\"},{\"_Id\":174,\"data\":\"00303322430\"},{\"_Id\":175,\"data\":\"00303332440\"},{\"_Id\":176,\"data\":\"00303342470\"},{\"_Id\":177,\"data\":\"00303362510\"},{\"_Id\":178,\"data\":\"00303372640\"},{\"_Id\":179,\"data\":\"00303382670\"},{\"_Id\":180,\"data\":\"00303392680\"},{\"_Id\":181,\"data\":\"00303402690\"},{\"_Id\":182,\"data\":\"00303412710\"},{\"_Id\":183,\"data\":\"00303422740\"},{\"_Id\":184,\"data\":\"00303432750\"},{\"_Id\":185,\"data\":\"00303442760\"},{\"_Id\":186,\"data\":\"00303452780\"},{\"_Id\":187,\"data\":\"00303462790\"},{\"_Id\":188,\"data\":\"00303473320\"},{\"_Id\":189,\"data\":\"00303493370\"},{\"_Id\":190,\"data\":\"00303523430\"},{\"_Id\":191,\"data\":\"00303554210\"},{\"_Id\":192,\"data\":\"00303584240\"},{\"_Id\":193,\"data\":\"00303634350\"},{\"_Id\":194,\"data\":\"00303664530\"},{\"_Id\":195,\"data\":\"00303714680\"},{\"_Id\":196,\"data\":\"00303734750\"},{\"_Id\":197,\"data\":\"00303765210\"},{\"_Id\":198,\"data\":\"00303775220\"},{\"_Id\":199,\"data\":\"00303805270\"},{\"_Id\":200,\"data\":\"00303815280\"},{\"_Id\":201,\"data\":\"00303825310\"},{\"_Id\":202,\"data\":\"00303835320\"},{\"_Id\":203,\"data\":\"00303845330\"},{\"_Id\":204,\"data\":\"00303865360\"},{\"_Id\":205,\"data\":\"00303895520\"},{\"_Id\":206,\"data\":\"00303905530\"},{\"_Id\":207,\"data\":\"00303915540\"},{\"_Id\":208,\"data\":\"00303985630\"},{\"_Id\":209,\"data\":\"00304026110\"},{\"_Id\":210,\"data\":\"00304036140\"},{\"_Id\":211,\"data\":\"00304066230\"},{\"_Id\":212,\"data\":\"00304106310\"},{\"_Id\":213,\"data\":\"00304116320\"},{\"_Id\":214,\"data\":\"00304166420\"},{\"_Id\":215,\"data\":\"00304176510\"},{\"_Id\":216,\"data\":\"00304206570\"},{\"_Id\":217,\"data\":\"00304226590\"},{\"_Id\":218,\"data\":\"00304286730\"},{\"_Id\":219,\"data\":\"00304357710\"},{\"_Id\":220,\"data\":\"00304387760\"},{\"_Id\":221,\"data\":\"00304428210\"},{\"_Id\":222,\"data\":\"00304438220\"},{\"_Id\":223,\"data\":\"00304448230\"},{\"_Id\":224,\"data\":\"00304458240\"},{\"_Id\":225,\"data\":\"00304468250\"},{\"_Id\":226,\"data\":\"00304478260\"},{\"_Id\":227,\"data\":\"00304488270\"},{\"_Id\":228,\"data\":\"00304498280\"},{\"_Id\":229,\"data\":\"00304508290\"},{\"_Id\":230,\"data\":\"00304518310\"},{\"_Id\":231,\"data\":\"00304528340\"},{\"_Id\":232,\"data\":\"00304538360\"},{\"_Id\":233,\"data\":\"00304548380\"},{\"_Id\":234,\"data\":\"00304558510\"},{\"_Id\":235,\"data\":\"00304568520\"},{\"_Id\":236,\"data\":\"00304638720\"},{\"_Id\":237,\"data\":\"00304648730\"},{\"_Id\":238,\"data\":\"00304658740\"},{\"_Id\":239,\"data\":\"00304678810\"},{\"_Id\":240,\"data\":\"00304815375\"},{\"_Id\":241,\"data\":\"00304827310\"},{\"_Id\":242,\"data\":\"00304832411\"},{\"_Id\":243,\"data\":\"00304842412\"},{\"_Id\":244,\"data\":\"00304852413\"},{\"_Id\":245,\"data\":\"00304862415\"},{\"_Id\":246,\"data\":\"00304872421\"},{\"_Id\":247,\"data\":\"00304882422\"},{\"_Id\":248,\"data\":\"00304892423\"},{\"_Id\":249,\"data\":\"00304902425\"},{\"_Id\":250,\"data\":\"00304912431\"},{\"_Id\":251,\"data\":\"00304922432\"},{\"_Id\":252,\"data\":\"00304932433\"},{\"_Id\":253,\"data\":\"00304942434\"},{\"_Id\":254,\"data\":\"00304952441\"},{\"_Id\":255,\"data\":\"00304962442\"},{\"_Id\":256,\"data\":\"00304972451\"},{\"_Id\":257,\"data\":\"00304982452\"},{\"_Id\":258,\"data\":\"00304992453\"},{\"_Id\":259,\"data\":\"00305002454\"},{\"_Id\":260,\"data\":\"00305012455\"},{\"_Id\":261,\"data\":\"00305022461\"},{\"_Id\":262,\"data\":\"00305032462\"},{\"_Id\":263,\"data\":\"00305042464\"},{\"_Id\":264,\"data\":\"00305052472\"},{\"_Id\":265,\"data\":\"00305062474\"},{\"_Id\":266,\"data\":\"00305072477\"},{\"_Id\":267,\"data\":\"00305082478\"},{\"_Id\":268,\"data\":\"00305092491\"},{\"_Id\":269,\"data\":\"00305102492\"},{\"_Id\":270,\"data\":\"00305112493\"},{\"_Id\":271,\"data\":\"00305122494\"},{\"_Id\":272,\"data\":\"00305132495\"},{\"_Id\":273,\"data\":\"00305142496\"},{\"_Id\":274,\"data\":\"00305152497\"},{\"_Id\":275,\"data\":\"00305162498\"},{\"_Id\":276,\"data\":\"00305172511\"},{\"_Id\":277,\"data\":\"00305182512\"},{\"_Id\":278,\"data\":\"00305192514\"},{\"_Id\":279,\"data\":\"00305202400\"},{\"_Id\":280,\"data\":\"00305212482\"},{\"_Id\":281,\"data\":\"00305222466\"},{\"_Id\":282,\"data\":\"00305236421\"},{\"_Id\":283,\"data\":\"00305246423\"},{\"_Id\":284,\"data\":\"00305256424\"},{\"_Id\":285,\"data\":\"00305266425\"},{\"_Id\":286,\"data\":\"00305276426\"},{\"_Id\":287,\"data\":\"00305286427\"},{\"_Id\":288,\"data\":\"00305296428\"},{\"_Id\":289,\"data\":\"00305306429\"},{\"_Id\":290,\"data\":\"00305316431\"},{\"_Id\":291,\"data\":\"00305326432\"},{\"_Id\":292,\"data\":\"00305336433\"},{\"_Id\":293,\"data\":\"00305346434\"},{\"_Id\":294,\"data\":\"00305356435\"},{\"_Id\":295,\"data\":\"00305366436\"},{\"_Id\":296,\"data\":\"00305376437\"},{\"_Id\":297,\"data\":\"00305386438\"},{\"_Id\":298,\"data\":\"00305396440\"},{\"_Id\":299,\"data\":\"00305406400\"},{\"_Id\":300,\"data\":\"00305437972\"},{\"_Id\":301,\"data\":\"00305447090\"},{\"_Id\":302,\"data\":\"0010004ffff\"},{\"_Id\":303,\"data\":\"0010005ffff\"},{\"_Id\":304,\"data\":\"0011100ffff\"},{\"_Id\":305,\"data\":\"0011120ffff\"},{\"_Id\":306,\"data\":\"0011130ffff\"},{\"_Id\":307,\"data\":\"0011150ffff\"},{\"_Id\":308,\"data\":\"0011170ffff\"},{\"_Id\":309,\"data\":\"0011210ffff\"},{\"_Id\":310,\"data\":\"0011250ffff\"},{\"_Id\":311,\"data\":\"0011251ffff\"},{\"_Id\":312,\"data\":\"0011362ffff\"},{\"_Id\":313,\"data\":\"0011380ffff\"},{\"_Id\":314,\"data\":\"0012140ffff\"},{\"_Id\":315,\"data\":\"0012142ffff\"},{\"_Id\":316,\"data\":\"0012144ffff\"},{\"_Id\":317,\"data\":\"0012153ffff\"},{\"_Id\":318,\"data\":\"0012154ffff\"},{\"_Id\":319,\"data\":\"0012155ffff\"},{\"_Id\":320,\"data\":\"0012230ffff\"},{\"_Id\":321,\"data\":\"0012253ffff\"},{\"_Id\":322,\"data\":\"0012260ffff\"},{\"_Id\":323,\"data\":\"0012320ffff\"},{\"_Id\":324,\"data\":\"0013000ffff\"},{\"_Id\":325,\"data\":\"0013120ffff\"},{\"_Id\":326,\"data\":\"0013130ffff\"},{\"_Id\":327,\"data\":\"0013131ffff\"},{\"_Id\":328,\"data\":\"0013140ffff\"},{\"_Id\":329,\"data\":\"0013150ffff\"},{\"_Id\":330,\"data\":\"0013160ffff\"},{\"_Id\":331,\"data\":\"0013180ffff\"},{\"_Id\":332,\"data\":\"0013210ffff\"},{\"_Id\":333,\"data\":\"0013220ffff\"},{\"_Id\":334,\"data\":\"0013250ffff\"},{\"_Id\":335,\"data\":\"0013300ffff\"},{\"_Id\":336,\"data\":\"0013340ffff\"},{\"_Id\":337,\"data\":\"0013320ffff\"},{\"_Id\":338,\"data\":\"0013350ffff\"},{\"_Id\":339,\"data\":\"0013410ffff\"},{\"_Id\":340,\"data\":\"0013500ffff\"},{\"_Id\":341,\"data\":\"0013511ffff\"},{\"_Id\":342,\"data\":\"0013620ffff\"},{\"_Id\":343,\"data\":\"0013622ffff\"},{\"_Id\":344,\"data\":\"0013640ffff\"},{\"_Id\":345,\"data\":\"0014102ffff\"},{\"_Id\":346,\"data\":\"0014250ffff\"},{\"_Id\":347,\"data\":\"0014331ffff\"},{\"_Id\":348,\"data\":\"0014420ffff\"},{\"_Id\":349,\"data\":\"0014500ffff\"},{\"_Id\":350,\"data\":\"0014501ffff\"},{\"_Id\":351,\"data\":\"0014511ffff\"},{\"_Id\":352,\"data\":\"0014620ffff\"},{\"_Id\":353,\"data\":\"0014630ffff\"},{\"_Id\":354,\"data\":\"0014670ffff\"},{\"_Id\":355,\"data\":\"0014730ffff\"},{\"_Id\":356,\"data\":\"0015190ffff\"},{\"_Id\":357,\"data\":\"0015240ffff\"},{\"_Id\":358,\"data\":\"0015580ffff\"},{\"_Id\":359,\"data\":\"0015630ffff\"},{\"_Id\":360,\"data\":\"0015720ffff\"},{\"_Id\":361,\"data\":\"0016150ffff\"},{\"_Id\":362,\"data\":\"0016217ffff\"},{\"_Id\":363,\"data\":\"0016374ffff\"},{\"_Id\":364,\"data\":\"0016430ffff\"},{\"_Id\":365,\"data\":\"0016500ffff\"},{\"_Id\":366,\"data\":\"0016710ffff\"},{\"_Id\":367,\"data\":\"0017120ffff\"},{\"_Id\":368,\"data\":\"0017121ffff\"},{\"_Id\":369,\"data\":\"0017140ffff\"},{\"_Id\":370,\"data\":\"0017190ffff\"},{\"_Id\":371,\"data\":\"0017300ffff\"},{\"_Id\":372,\"data\":\"0017309ffff\"},{\"_Id\":373,\"data\":\"0017441ffff\"},{\"_Id\":374,\"data\":\"0018340ffff\"},{\"_Id\":375,\"data\":\"0019001ffff\"}]";
        String cityCode = "[{\"Id\":1,\"city\":\"邯郸\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1581270\",\"updated_at\":\"2019-04-021 10:42:37\"},{\"Id\":2,\"city\":\"保定\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1701340\",\"updated_at\":\"2019-04-021 10:42:37\"},{\"Id\":3,\"city\":\"沧州\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1761430\",\"updated_at\":\"2019-04-021 10:42:37\"},{\"Id\":4,\"city\":\"张家口\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1711380\",\"updated_at\":\"2019-04-021 10:42:37\"},{\"Id\":5,\"city\":\"承德\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1751410\",\"updated_at\":\"2019-04-021 10:42:37\"},{\"Id\":6,\"city\":\"廊坊\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1721460\",\"updated_at\":\"2019-04-021 10:42:37\"},{\"Id\":7,\"city\":\"石家庄\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1171210\",\"updated_at\":\"2019-04-021 10:42:37\"},{\"Id\":8,\"city\":\"北京\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1011000\",\"updated_at\":\"2019-04-021 10:42:37\"},{\"Id\":9,\"city\":\"天津\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1131121\",\"updated_at\":\"2019-04-021 10:42:37\"},{\"Id\":10,\"city\":\"石家庄\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1691210\",\"updated_at\":\"2019-04-021 10:42:37\"},{\"Id\":11,\"city\":\"唐山\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1731240\",\"updated_at\":\"2019-04-021 10:42:37\"},{\"Id\":12,\"city\":\"秦皇岛\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1741260\",\"updated_at\":\"2019-04-021 10:42:37\"},{\"Id\":13,\"city\":\"衡水\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1771480\",\"updated_at\":\"2019-04-021 10:42:37\"},{\"Id\":14,\"city\":\"邢台\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1781310\",\"updated_at\":\"2019-04-021 10:42:37\"},{\"Id\":15,\"city\":\"邯郸\",\"created_at\":\"2019-04-021 10:42:37\",\"is_available\":\"1\",\"issuer_code\":\"1791270\",\"updated_at\":\"2019-04-021 10:42:37\"}]";
        final Gson gson = new GsonBuilder().serializeNulls().create();
        //Json的解析类对象
        JsonParser parser = new JsonParser();
        //将JSON的String 转成一个JsonArray对象
        JsonArray jsonArray = parser.parse(white).getAsJsonArray();
        ArrayList<White> whiteArrayList = new ArrayList<>();

        //加强for循环遍历JsonArray
        for (JsonElement user : jsonArray) {
            //使用GSON，直接转成Bean对象
            White whiteBean = gson.fromJson(user, White.class);
            whiteArrayList.add(whiteBean);
        }
        DbDaoManage.getDaoSession().getWhiteDao().deleteAll();
        DbDaoManage.getDaoSession().getWhiteDao().insertInTx(whiteArrayList);

        //将JSON的String 转成一个JsonArray对象
        JsonArray cityArray = parser.parse(cityCode).getAsJsonArray();
        ArrayList<CityCode> cityArrayList = new ArrayList<>();

        //加强for循环遍历JsonArray
        for (JsonElement user : cityArray) {
            //使用GSON，直接转成Bean对象
            CityCode cityCodeBean = gson.fromJson(user, CityCode.class);
            cityArrayList.add(cityCodeBean);
        }
        DbDaoManage.getDaoSession().getCityCodeDao().deleteAll();
        DbDaoManage.getDaoSession().getCityCodeDao().insertInTx(cityArrayList);

    }
}