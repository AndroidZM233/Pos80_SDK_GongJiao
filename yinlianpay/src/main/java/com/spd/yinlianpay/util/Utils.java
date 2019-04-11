package com.spd.yinlianpay.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.Log;


import com.spd.yinlianpay.WeiPassGlobal;
import com.spd.yinlianpay.trade.TradeInfo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ui.wangpos.com.utiltool.HEXUitl;

/**
 * Created by Tommy on 2016/6/13.
 */
public class Utils {

    public static String[] CAPK_DATA = {
            "9F0605A0000000039F220172DF05083230313030313031DF060101DF070101DF028190BD9F074D8F60501D2E87B3AB03DCA80C83AF9CE81372AD34B7FA639767E5E6B2491ADCAF943FA165D09AB25B4B8FF541E6D2D3B0B70705B105266751D27E8E56FD9D0974F67B3B2E84322DA7E56152A4E42CC63727EB160B2E5310DF125E74F55618FE8727B167B6456431CFDE80C025D0CB1DE7DDC3186B7314085C7CCA301C691F5577690FD2DE5FC62665CB163F0DDF0403010001DF03141ECF2EFE0B01FA7C94F3960056E748C8FF4F1D09",
            "9F0605A0000000049F220172DF05083230313030313031DF060101DF070101DF028190BD9F074D8F60501D2E87B3AB03DCA80C83AF9CE81372AD34B7FA639767E5E6B2491ADCAF943FA165D09AB25B4B8FF541E6D2D3B0B70705B105266751D27E8E56FD9D0974F67B3B2E84322DA7E56152A4E42CC63727EB160B2E5310DF125E74F55618FE8727B167B6456431CFDE80C025D0CB1DE7DDC3186B7314085C7CCA301C691F5577690FD2DE5FC62665CB163F0DDF0403010001DF03147070A62C1C1F3C293F9B270B874C2880BFDE37EA",
            "9F0605A0000000039F220198DF05083230303631303331DF060101DF070101DF0270CA026E52A695E72BD30AF928196EEDC9FAF4A619F2492E3FB31169789C276FFBB7D43116647BA9E0D106A3542E3965292CF77823DD34CA8EEC7DE367E08070895077C7EFAD939924CB187067DBF92CB1E785917BD38BACE0C194CA12DF0CE5B7A50275AC61BE7C3B436887CA98C9FD39DF040103DF0314E7AC9AA8EED1B5FF1BD532CF1489A3E5557572C1",
            "9F0605A0000003339F220101DF05083230323031323331DF060101DF070101DF028180BBE9066D2517511D239C7BFA77884144AE20C7372F515147E8CE6537C54C0A6A4D45F8CA4D290870CDA59F1344EF71D17D3F35D92F3F06778D0D511EC2A7DC4FFEADF4FB1253CE37A7B2B5A3741227BEF72524DA7A2B7B1CB426BEE27BC513B0CB11AB99BC1BC61DF5AC6CC4D831D0848788CD74F6D543AD37C5A2B4C5D5A93BDF040103DF0314E881E390675D44C2DD81234DCE29C3F5AB2297A0",
            "9F0605A0000003339F220102DF05083230323031323331DF060101DF070101DF028190A3767ABD1B6AA69D7F3FBF28C092DE9ED1E658BA5F0909AF7A1CCD907373B7210FDEB16287BA8E78E1529F443976FD27F991EC67D95E5F4E96B127CAB2396A94D6E45CDA44CA4C4867570D6B07542F8D4BF9FF97975DB9891515E66F525D2B3CBEB6D662BFB6C3F338E93B02142BFC44173A3764C56AADD202075B26DC2F9F7D7AE74BD7D00FD05EE430032663D27A57DF040103DF031403BB335A8549A03B87AB089D006F60852E4B8060",
            "9F0605A0000003339F220103DF05083230323031323331DF060101DF070101DF0281B0B0627DEE87864F9C18C13B9A1F025448BF13C58380C91F4CEBA9F9BCB214FF8414E9B59D6ABA10F941C7331768F47B2127907D857FA39AAF8CE02045DD01619D689EE731C551159BE7EB2D51A372FF56B556E5CB2FDE36E23073A44CA215D6C26CA68847B388E39520E0026E62294B557D6470440CA0AEFC9438C923AEC9B2098D6D3A1AF5E8B1DE36F4B53040109D89B77CAFAF70C26C601ABDF59EEC0FDC8A99089140CD2E817E335175B03B7AA33DDF040103DF031487F0CD7C0E86F38F89A66F8C47071A8B88586F26",
            "9F0605A0000003339F220104DF05083230323031323331DF060101DF070101DF0281F8BC853E6B5365E89E7EE9317C94B02D0ABB0DBD91C05A224A2554AA29ED9FCB9D86EB9CCBB322A57811F86188AAC7351C72BD9EF196C5A01ACEF7A4EB0D2AD63D9E6AC2E7836547CB1595C68BCBAFD0F6728760F3A7CA7B97301B7E0220184EFC4F653008D93CE098C0D93B45201096D1ADFF4CF1F9FC02AF759DA27CD6DFD6D789B099F16F378B6100334E63F3D35F3251A5EC78693731F5233519CDB380F5AB8C0F02728E91D469ABD0EAE0D93B1CC66CE127B29C7D77441A49D09FCA5D6D9762FC74C31BB506C8BAE3C79AD6C2578775B95956B5370D1D0519E37906B384736233251E8F09AD79DFBE2C6ABFADAC8E4D8624318C27DAF1DF040103DF0314F527081CF371DD7E1FD4FA414A665036E0F5E6E5",
            "9F0605A0000003339F220109DF05083230323031323331DF060101DF070101DF0281B0EB374DFC5A96B71D2863875EDA2EAFB96B1B439D3ECE0B1826A2672EEEFA7990286776F8BD989A15141A75C384DFC14FEF9243AAB32707659BE9E4797A247C2F0B6D99372F384AF62FE23BC54BCDC57A9ACD1D5585C303F201EF4E8B806AFB809DB1A3DB1CD112AC884F164A67B99C7D6E5A8A6DF1D3CAE6D7ED3D5BE725B2DE4ADE23FA679BF4EB15A93D8A6E29C7FFA1A70DE2E54F593D908A3BF9EBBD760BBFDC8DB8B54497E6C5BE0E4A4DAC29E5DF040103DF0314A075306EAB0045BAF72CDD33B3B678779DE1F527",
            "9F0605A0000003339F22010BDF05083230323031323331DF060101DF070101DF0281F8CF9FDF46B356378E9AF311B0F981B21A1F22F250FB11F55C958709E3C7241918293483289EAE688A094C02C344E2999F315A72841F489E24B1BA0056CFAB3B479D0E826452375DCDBB67E97EC2AA66F4601D774FEAEF775ACCC621BFEB65FB0053FC5F392AA5E1D4C41A4DE9FFDFDF1327C4BB874F1F63A599EE3902FE95E729FD78D4234DC7E6CF1ABABAA3F6DB29B7F05D1D901D2E76A606A8CBFFFFECBD918FA2D278BDB43B0434F5D45134BE1C2781D157D501FF43E5F1C470967CD57CE53B64D82974C8275937C5D8502A1252A8A5D6088A259B694F98648D9AF2CB0EFD9D943C69F896D49FA39702162ACB5AF29B90BADE005BC157DF040103DF0314BD331F9996A490B33C13441066A09AD3FEB5F66C",
            "9F0605A0000003339F220108DF05083230323031323331DF060101DF070101DF028190B61645EDFD5498FB246444037A0FA18C0F101EBD8EFA54573CE6E6A7FBF63ED21D66340852B0211CF5EEF6A1CD989F66AF21A8EB19DBD8DBC3706D135363A0D683D046304F5A836BC1BC632821AFE7A2F75DA3C50AC74C545A754562204137169663CFCC0B06E67E2109EBA41BC67FF20CC8AC80D7B6EE1A95465B3B2657533EA56D92D539E5064360EA4850FED2D1BFDF040103DF0314EE23B616C95C02652AD18860E48787C079E8E85A",
            "9F0605A0000000659F2201ECDF05083230303631323331DF060101DF070101DF0281F8A9EDFDC58029A7EC003D13F22F6AED5622786D45F7C36516A3DBFE4D75BFCE00F4CF656670CD07A66A99A7CD35D2F5228CB2D794B95C4930FDDAD17F8C9293164AFEC876D5644DD31ABFE86B7AA512C58D5C71310FB36E8D7CCFF4C958669C0042DFF048F52E412B530C3BB77555B6F9B35E2C0F1B17A6180D03D94914B4970A42309F259DB37EC77FF6BA04BACF6B17FF7B10C1A04272D08C043A1C8E8951681DE41BE30F4E42D3ED3FE3328BD4C6327B19D110A2E85D9DC4C34225A2F0CA7684FF5C05C1F01135FC51D7331E3A413AED0942C8BBDB975104E171B08EE7C2B388EC4EA493BE5FCB0C416DF2A9DBBCDFA5D12344EC30576BDF040103DF0314A15945946935956845ADB8ABE73E5B0BEEF76ECB",
            "9f0605a0000000039f220192df050420991231df0281b0996af56f569187d09293c14810450ed8ee3357397b18a2458efaa92da3b6df6514ec060195318fd43be9b8f0cc669e3f844057cbddf8bda191bb64473bc8dc9a730db8f6b4ede3924186ffd9b8c7735789c23a36ba0b8af65372eb57ea5d89e7d14e9c7b6b557460f10885da16ac923f15af3758f0f03ebd3c5c2c949cba306db44e6a2c076c5f67e281d7ef56785dc4d75945e491f01918800a9e2dc66f60080566ce0daf8d17ead46ad8e30a247c9fdf040103df0314429c954a3859cef91295f663c963e582ed6eb253bf010131",
            "9F0605A0000000049F2201FADF060101DF060101DF028190A90FCD55AA2D5D9963E35ED0F440177699832F49C6BAB15CDAE5794BE93F934D4462D5D12762E48C38BA83D8445DEAA74195A301A102B2F114EADA0D180EE5E7A5C73E0C4E11F67A43DDAB5D55683B1474CC0627F44B8D3088A492FFAADAD4F42422D0E7013536C3C49AD3D0FAE96459B0F6B1B6056538A3D6D44640F94467B108867DEC40FAAECD740C00E2B7A8852DDF040103DF050420991231DF03145BED4068D96EA16D2D77E03D6036FC7A160EA99C"
    };

    public static String[] AID_DATA = {
            "9F0607A0000000031010DF0101009F0802008CDF1105D84000A800DF1205D84004F800DF130500100000009F1B0400100000DF150435303030DF160100DF170100DF14029F37DF1801019F7B06000000000100DF1906000000100000DF2006000000200000",
            "9F0607A0000000032010DF0101009F0802008CDF1105D84000A800DF1205D84004F800DF130500100000009F1B0400100000DF150435303030DF160100DF170100DF14029F37DF1801019F7B06000000000100DF1906000000100000DF2006000000200000",
            "9F0607A0000000033010DF0101009F0802008CDF1105D84000A800DF1205D84004F800DF130500100000009F1B0400100000DF150435303030DF160100DF170100DF14029F37DF1801019F7B06000000000100DF1906000000100000DF2006000000200000",
            "9F0607A0000000038010DF0101009F0802008CDF1105D84000A800DF1205D84004F800DF130500100000009F1B0400100000DF150435303030DF160100DF170100DF14029F37DF1801019F7B06000000000100DF1906000000100000DF2006000000200000",

//            "9F0607A0000000046000DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000DF811A039F6A04",
//            "9F0607A0000000046010DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000DF811A039F6A04",
//            "9F0607A0000000042000DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000DF811A039F6A04",
//            "9F0607A0000000042010DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000DF811A039F6A04",
//            "9F0607A0000000043000DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000DF811A039F6A04",
//            "9F0607A0000000048010DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000DF811A039F6A04",

            "9F0607A00000000410109F0100DF2006000000100000DF21060000001000009F090200029F1501129F16009F1A0200569F1C009F1D086CFF0000000000009F1E04112233449F3303E000089F3501229F400500000000009F4E009F6D0200019F7E00DF6000DF6200DF6300DF810800DF810900DF810A00DF810C0102DF810D00DF81170100DF81180160DF81190108DF811A039F6A04DF811B0120DF811C020000DF811D0100DF811E0110DF811F0108DF8120050000000000DF8121050000000000DF8122050000000000DF81230400010000DF81240400150000DF81250400200000DF81260400010000DF812C0100",
            "9F0607A00000000430609F0100DF2006000000100000DF21060000000010009F090200029F1501129F16009F1A0200569F1C009F1D0844FF8000000000009F1E04112233449F33030000089F3501229F400500000000009F4E009F6D0200019F7E00DF6000DF6200DF6300DF810800DF810900DF810A00DF810C0102DF810D00DF81170100DF81180160DF81190108DF811A039F6A04DF811B0120DF811C020000DF811D0100DF811E0110DF811F0108DF8120050000000000DF8121050000000000DF8122050000000000DF81230400010000DF81240400150000DF81250400200000DF81260400010000DF812C0100",
            "9F0605B0123456789F01009F090200029F1501129F1600DF21060000000010009F1A0200569F1C009F1E04112233449F33030000089F3501229F400500000000009F4E009F6D0200019F7E00DF6000DF6200DF6300DF810800DF810900DF810A00DF810C0102DF810D00DF81170100DF81180160DF81190108DF811A039F6A04DF811B0120DF811C020000DF811D0100DF811E0110DF811F0108DF8120050000000000DF8121050000000000DF8122050000000000DF81230400010000DF81240400030000DF81250400050000DF81260400001000DF812C0100"
    };

    public static final String TAG = "469";

    public static final int CardTypeMC = 1;//磁条卡
    public static final int CardTypeIC_ICC = 2;//IC卡 插卡
    public static final int CardTypeIC_PICC = 3;//IC卡 贴卡，非接触
    public static final int CardTypeInput = 4;//手输卡号

    public static int cardType = CardTypeMC;
    public static boolean sReversalTest = false;

    public static final int METRO_HEIGHT = 241;

    public static final String TELLER_LIST_MODE = "teller_list_mode";
    public static final int TELLER_LIST_SEARCH = 0;
    public static final int TELLER_LIST_DELETE = 1;

    public static final String PRINT_MODE = "print_mode";
    public static final String PRINT_CONTENT = "print_content";
    public static final int PRINT_MODE_LAST = 0;
    public static final int PRINT_MODE_ANYONE = 1;
    public static final int PRINT_MODE_DETAIL = 2;
    public static final int PRINT_MODE_SUMMARY = 3;

    public static final String MODIFY_MODE = "modify_mode";
    public static final int MODIFY_DIRECTOR = 0;
    public static final int MODIFY_EMPLOYEE = 1;

    public static final String UNLOCK_NUM = "unlock_num";
    public static final String UNLOCK_PASS = "unlock_pass";
    public static final String PACKAGE_NAME = "package_name";
    public static final String CLASS_NAME = "class_name";

    public static int[] mManagerMainImageArray;
    public static int[] mManagerPrintImageArray;
    public static int[] mManagerTransactionImageArray;
    public static int[] mManagerListImageArray;
    public static int[] mManagerEmployeeImageArray;

    public static final byte[] generateXkey(byte[] input) {
        if (input == null || input.length != 8) {
            throw new IllegalArgumentException("无效参数");
        }
        byte[] tid1Bytes = new byte[input.length];
        System.arraycopy(input, 4, tid1Bytes, 0, 4);
        System.arraycopy(input, 0, tid1Bytes, 4, 4);

        System.err.println("tid1----" + HEXUitl.bytesToHex(tid1Bytes));

        byte[] temp = new byte[]{(byte) 0xCD, (byte) 0xA8, (byte) 0xC1,
                (byte) 0xAA, (byte) 0xD0, (byte) 0xC2, (byte) 0xD0, (byte) 0xCB};
        byte[] tid2Bytes = xor(tid1Bytes, temp);
        System.err.println("----tid2bytes----" + HEXUitl.bytesToHex(tid2Bytes));

        int t;
        for (int i = 0; i < tid2Bytes.length; i++) {
            t = tid2Bytes[i] < 0 ? tid2Bytes[i] + 256 : tid2Bytes[i];
            tid2Bytes[i] = (byte) ((t / 9) ^ (t % 9));
        }

        return tid2Bytes;
    }

    public static final byte[] generateTtek(String terminalNo, String pwd) {
        if (terminalNo == null || terminalNo.length() != 8) {
            throw new IllegalArgumentException("无效终端号" + terminalNo);
        }
        byte[] tid2Bytes = generateXkey(terminalNo.getBytes());
        System.err.println("----tid2bytes2---" + HEXUitl.bytesToHex(tid2Bytes));

        byte[] temp = new byte[]{(byte) 0xD6, (byte) 0xA7, (byte) 0xB8, (byte) 0xB6,
                (byte) 0xCE, (byte) 0xDE, (byte) 0xD3, (byte) 0xC7};
        byte[] xtid = new byte[tid2Bytes.length + temp.length];
        System.arraycopy(tid2Bytes, 0, xtid, 0, tid2Bytes.length);
        System.arraycopy(temp, 0, xtid, tid2Bytes.length, temp.length);
        System.err.println("---xtid---" + HEXUitl.bytesToHex(xtid));

        byte[] md5Pwd = HEXUitl.hexToBytes(getMD5String(pwd));
        System.err.println("---md5--" + HEXUitl.bytesToHex(md5Pwd));

        byte[] ttek = xor(xtid, md5Pwd);

        return ttek;
    }

    public static final byte[] xor(byte[] bs, byte[] ps) {
        for (int i = 0; i < bs.length; i++) {
            bs[i] ^= ps[i];
        }
        return bs;
    }

    public static String getMD5String(String input) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(input.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString().toUpperCase();
    }

    public static long decodeBalancAmount(String s) {
        long result = 0;
        for (int i = 0; i < s.length(); i += 20) {
            int need = s.length() - i;
            if (need > 20) {
                need = 20;
            }
            if (need != 20) {
                break;
            }
            String sub = s.substring(i, i + need);
            // 如果有两部分，择取后半部分（可用余额）
            result = Long.parseLong(sub.substring(8, 20));
        }
        return result;
    }

    public static boolean needResign(String code) {
        if ("99".equals(code) || "A0".equals(code) || "77".equals(code)
                || "58".equals(code) || "A7".equals(code)) {
            return true;
        }
        return false;
    }

    public static String getSpiltCardNo(String no) {
        if (no == null || no.length() < 16) {
            return no;
        }
        String cardNo = no.substring(0, 4) + " " + no.substring(4, 8) + " "
                + no.substring(8, 12) + " " + no.substring(12, 16);
        if (no.length() > 16) {
            cardNo += " " + no.substring(16);
        }
        return cardNo;
    }

    public static String getTitle(int tradeType) {
        if (tradeType <= TradeInfo.Type_Cancel) {
            return "刷卡支付";
        } else if (tradeType <= TradeInfo.Type_WXRefund) {
            return "微信支付";
        } else if (tradeType <= TradeInfo.Type_ZFBRefund) {
            return "支付宝支付";
        } else if (tradeType <= TradeInfo.Type_TLWalletVoid) {
            return "通联钱包支付";
        } else if (tradeType <= TradeInfo.Type_KaQuanHeXiao) {
            return "优惠券核销";
        } else {
            return "未定义支付" + tradeType;
        }
    }

    public static String getTradeTypeString(int tradeType) {
        switch (tradeType) {
            case TradeInfo.Type_Sale:
                return "Sale";
            case TradeInfo.Type_Void:
                return "Void";
            case TradeInfo.Type_Refund:
                return "Refund";
            case TradeInfo.Type_Auth:
                return "预授权";
            case TradeInfo.Type_Cancel:
                return "预授权撤销";
            case TradeInfo.Type_AuthComplete:
                return "预授权完成";
            case TradeInfo.Type_CompleteVoid:
                return "预授权完成撤销";
            case TradeInfo.TYPE_LZF_SALE:
                return "龙支付消费(LZF)";
            case TradeInfo.TYPE_LZF_REFOUND:
                return "龙支付退货";
            case TradeInfo.TYPE_LZF_QURETY:
                return "龙支付查询";
            case TradeInfo.Type_WXOfflinePay:
                return "微信离线支付";
            case TradeInfo.Type_WXVoid:
                return "微信支付撤销";
            case TradeInfo.Type_WXRefund:
                return "微信支付退货";
            case TradeInfo.Type_ZFBOfflinePay:
                return "支付宝支付";
            case TradeInfo.Type_ZFBVoid:
                return "支付宝支付撤销";
            case TradeInfo.Type_ZFBRefund:
                return "支付宝支付退货";
            case TradeInfo.Type_BDOfflinePay:
                return "百度钱包支付";
            case TradeInfo.Type_BDVoid:
                return "百度钱包支付撤销";
            case TradeInfo.Type_BDRefund:
                return "百度钱包支付退货";
            case TradeInfo.Type_TLWalletSale:
                return "通联钱包支付";
            case TradeInfo.Type_TLWalletVoid:
                return "通联钱包支付撤销";
            case TradeInfo.Type_CouponSale:
                return "优惠券消费";
            case TradeInfo.Type_CouponVoid:
                return "优惠券撤销";
            case TradeInfo.Type_CouponRefund:
                return "优惠券退货";
            case TradeInfo.Type_PointSale:
                return "积分消费";
            case TradeInfo.Type_PointVoid:
                return "积分撤销";
            case TradeInfo.Type_PointRefund:
                return "积分退货";
            case TradeInfo.Type_ETicketSale:
                return "电子票消费";
            case TradeInfo.Type_ETicketVoid:
                return "电子票撤销";
            case TradeInfo.Type_ETicketRefund:
                return "电子票退货";
            case TradeInfo.Type_KaQuanHeXiao:
                return "优惠券核销";
            case TradeInfo.TYPE_HELP_FARMERS_DRAW:
                return "助农取款";
//            case TradeInfo.Type_DianZiXianJin:
//                return "电子现金";
            case TradeInfo.Type_CoilingSale:
                return "指定账户圈存";

            case TradeInfo.Type_HelpFarmersTransfer:
                return "转账";

            case TradeInfo.Type_doHelpFarmersCashTransfer:
                return "助农现金汇款";
            case TradeInfo.Type_BindAccTransfer:
                return "绑定账户汇款";
            case TradeInfo.Type_CrossLineTransfer:
                return "绑定账户汇款";
            case TradeInfo.Type_LineTransfer:
                return "行内汇款";
            case TradeInfo.Type_doLinePayment:
                return "助农转账";
            case TradeInfo.Type_doPOSFinancialTransfer:
                return "POS理财账户转账";
            case TradeInfo.Type_doTeleRecharge:
                return "电信充值";
            case TradeInfo.Type_loanLending:
                return "自助放款";
            case TradeInfo.Type_loanRepayment:
                return "自助还款";

            default:
                return "UNKNOWN" + tradeType;
        }
    }

    public static final String getPrintType(int type) {
        switch (type) {
            case TradeInfo.Type_Sale:
                return "SA";
            case TradeInfo.Type_Void:
                return "SD";
            case TradeInfo.Type_Refund:
                return "RE";
            case TradeInfo.Type_AuthComplete:
                return "AS";
            case TradeInfo.Type_CompleteVoid:
                return "AV";
            case TradeInfo.Type_CouponSale:
                return "C";
            case TradeInfo.Type_ETicketSale:
                return "E";
            case TradeInfo.Type_PointSale:
                return "B";
            case TradeInfo.Type_CouponVoid:
                return "D";
            case TradeInfo.Type_CouponRefund:
                return "R";
            case TradeInfo.Type_WXOfflinePay:
                return "WX";
            case TradeInfo.Type_WXVoid:
                return "WD";
            case TradeInfo.Type_WXRefund:
                return "WR";
            case TradeInfo.Type_ZFBOfflinePay:
                return "AL";
            case TradeInfo.Type_ZFBVoid:
                return "AD";
            case TradeInfo.Type_ZFBRefund:
                return "AR";
            case TradeInfo.Type_BDOfflinePay:
                return "BD";
            case TradeInfo.Type_BDVoid:
                return "BD";
            case TradeInfo.Type_BDRefund:
                return "BR";
            case TradeInfo.Type_TLWalletSale:
                return "II";
            case TradeInfo.Type_TLWalletVoid:
                return "ID";
            case TradeInfo.Type_KaQuanHeXiao:
                return "WQ";
            default:
                return "UN";
        }
    }

    public static boolean isSale(int type) {
        return type == TradeInfo.Type_Sale || type == TradeInfo.Type_WXOfflinePay || type == TradeInfo.Type_ZFBOfflinePay
                || type == TradeInfo.Type_BDOfflinePay || type == TradeInfo.Type_TLWalletSale || type == TradeInfo.Type_CouponSale
                || type == TradeInfo.Type_PointSale || type == TradeInfo.Type_ETicketSale||type==TradeInfo.Type_CoilingSale;
    }

    public static boolean isChexiao(int type) {
        return type == TradeInfo.Type_Void || type == TradeInfo.Type_Cancel || type == TradeInfo.Type_CompleteVoid
                || type == TradeInfo.Type_WXVoid || type == TradeInfo.Type_ZFBVoid || type == TradeInfo.Type_TLWalletVoid
                || type == TradeInfo.Type_BDVoid || type == TradeInfo.Type_CouponVoid || type == TradeInfo.Type_PointVoid
                || type == TradeInfo.Type_ETicketVoid;
    }

    public static boolean isRefund(int type) {
        return type == TradeInfo.Type_Refund || type == TradeInfo.Type_WXRefund || type == TradeInfo.Type_ZFBRefund
                || type == TradeInfo.Type_BDRefund || type == TradeInfo.Type_CouponRefund || type == TradeInfo.Type_PointRefund
                || type == TradeInfo.Type_ETicketRefund;
    }

    public static boolean isVoid_(int pos) {
        return pos == 1 || pos == 2 || pos == 4 || pos == 6 || pos == 7 || pos == 9 || pos == 10 || pos == 12 || pos == 13
                || pos == 15 || pos == 17 || pos == 18 || pos == 20 || pos == 21 || pos == 23 || pos == 24;
    }

    public static int[] getImageArray(Context context, int arrayId){
        TypedArray ta = context.getResources().obtainTypedArray(arrayId);
        int length = ta.length();
        int[] imageArray = new int[length];
        for (int i = 0; i < length; i++) {
            int id = ta.getResourceId(i, 0);
            imageArray[i] = id;
        }
        return imageArray;
    }

/*    public static void getAllImageArray(Context context) {
        if (mManagerMainImageArray == null) {
            mManagerMainImageArray = getImageArray(context, R.array.manager_image_array);
        }
        if (mManagerPrintImageArray == null) {
            mManagerPrintImageArray = getImageArray(context, R.array.manager_print_image_array);
        }
        if (mManagerTransactionImageArray == null) {
            mManagerTransactionImageArray = getImageArray(context, R.array.manager_transaction_image_array);
        }
        if (mManagerEmployeeImageArray == null) {
            mManagerEmployeeImageArray = getImageArray(context, R.array.manager_employee_image_array);
        }
        if (mManagerListImageArray == null) {
            mManagerListImageArray = getImageArray(context, R.array.manager_list_image_array);
        }

    }*/

    /**
     * 奇校验
     * <ul><li>就是让原有数据序列中（包括你要加上的一位）1的个数为奇数</li>
     * <li>1000110（0）你必须添0这样原来有3个1已经是奇数了所以你添上0之后1的个数还是奇数个</li>
     * </ul>
     * @param bytes 长度为8的整数倍
     * @param parity 0:奇校验,1:偶校验
     * @return
     * @throws Exception
     */
    public static byte[] parityOfOdd(byte[] bytes, int parity) throws Exception {
        if(bytes == null || bytes.length % 8 != 0){
            throw new Exception("数据错误!");
        }
        if(!(parity == 0 || parity == 1)){
            throw new Exception("参数错误!");
        }
        byte[] _bytes = bytes;
        String s; // 字节码转二进制字符串
        char[] cs ; // 二进制字符串转字符数组
        int count; // 为1的总个数
        boolean lastIsOne; // 最后一位是否为1
        for(int i=0;i<_bytes.length;i++){
            // 初始化参数
            s = Integer.toBinaryString((int)_bytes[i]); // 字节码转二进制字符串
            cs = s.toCharArray();// 二进制字符串转字符数组
            count = 0;// 为1的总个数
            lastIsOne = false;// 最后一位是否为1
            for(int j=0;j<s.length();j++){
                if(cs[j] == '1'){
                    count++;
                }
                if(j == (cs.length -1)){ // 判断最后一位是否为1
                    if(cs[j] == '1'){
                        lastIsOne = true;
                    } else {
                        lastIsOne = false;
                    }
                }
            }
            // 偶数个1时
            if(count % 2 == parity){
                // 最后一位为1,变为0
                if(lastIsOne){
                    _bytes[i] = (byte) (_bytes[i] - 0x01);
                } else {
                    // 最后一位为0,变为1
                    _bytes[i] = (byte) (_bytes[i] + 0x01);
                }
            }
        }
        return _bytes;
    }
    public static String bcd2Asc(byte[] bcd)
    {
        if ((bcd == null) || (bcd.length <= 0)) {
            return null;
        }
        try
        {
            String stmp = "";
            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < bcd.length; i++)
            {
                stmp = Integer.toHexString(bcd[i] & 0xFF);
                sb.append(stmp.length() == 1 ? "0" + stmp : stmp);
            }
            return sb.toString().toUpperCase().trim();
        }
        catch (Exception localException) {}
        return null;
    }
    public static byte[] asc2Bcd(String asc, int length)
    {
        if ((asc == null) || (asc.length() <= 0) || (length <= 0) || (length > asc.length())) {
            return null;
        }
        try
        {
            int len = length;
            int mod = len % 2;
            if (mod != 0)
            {
                asc = "0" + asc;
                len++;
            }
            byte[] abt = new byte[len];
            if (len >= 2) {
                len /= 2;
            }
            byte[] bbt = new byte[len];
            abt = asc.getBytes();
            for (int p = 0; p < len; p++)
            {
                int j;
                if ((abt[(2 * p)] >= 48) && (abt[(2 * p)] <= 57))
                {
                    j = abt[(2 * p)] - 48;
                }
                else
                {
                    if ((abt[(2 * p)] >= 97) && (abt[(2 * p)] <= 122)) {
                        j = abt[(2 * p)] - 97 + 10;
                    } else {
                        j = abt[(2 * p)] - 65 + 10;
                    }
                }
                int k;
                if ((abt[(2 * p + 1)] >= 48) && (abt[(2 * p + 1)] <= 57))
                {
                    k = abt[(2 * p + 1)] - 48;
                }
                else
                {
                    if ((abt[(2 * p + 1)] >= 97) && (abt[(2 * p + 1)] <= 122)) {
                        k = abt[(2 * p + 1)] - 97 + 10;
                    } else {
                        k = abt[(2 * p + 1)] - 65 + 10;
                    }
                }
                int a = (j << 4) + k;
                byte b = (byte)a;
                bbt[p] = b;
            }
            return bbt;
        }
        catch (Exception localException) {}
        return null;
    }

    public static String bcd2Asc(byte[] bcd, int length)
    {
        if ((bcd == null) || (bcd.length <= 0) || (length <= 0) || (length > bcd.length)) {
            return null;
        }
        try
        {
            String stmp = "";
            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < length; i++)
            {
                stmp = Integer.toHexString(bcd[i] & 0xFF);
                sb.append(stmp.length() == 1 ? "0" + stmp : stmp);
            }
            return sb.toString().toUpperCase().trim();
        }
        catch (Exception localException) {}
        return null;
    }

    public static String getConditionCode() {
        int i = 0;
        String str = "";
        String dt = WeiPassGlobal.getTransactionInfo().getSettleDate();
        String refno = WeiPassGlobal.getTransactionInfo().getReferNo();
        if(TextUtils.isEmpty(dt))
            dt ="0000";
        Log.i("esign","dt="+dt+"refno="+refno);
        if (dt != null && dt.length() >= 4) {
            str = dt.substring(0, 4);
        }
        if (refno != null && refno.length() >= 12) {
            str += refno.substring(0, 12);
        }
        if(str.length()>=16){
            byte[] block = asc2Bcd(str,16);
            for (i = 0; i < 4; i++) {
                block[i] ^= block[i + 4];
            }
            str =bcd2Asc(block,4);
        }
        else{
            str = "00000000";
        }
        return str;
    }
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len)
    {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++)
        {
            bcd[i] = asc_to_bcd(ascii[(j++)]);
            bcd[i] = ((byte)((j >= asc_len ? 0 : asc_to_bcd(ascii[(j++)])) + (bcd[i] << 4)));
        }
        return bcd;
    }
    private static byte asc_to_bcd(byte asc)
    {
        byte bcd;
        if ((asc >= 48) && (asc <= 57))
        {
            bcd = (byte)(asc - 48);
        }
        else
        {
            if ((asc >= 65) && (asc <= 70))
            {
                bcd = (byte)(asc - 65 + 10);
            }
            else
            {
                if ((asc >= 97) && (asc <= 102)) {
                    bcd = (byte)(asc - 97 + 10);
                } else {
                    bcd = (byte)(asc - 48);
                }
            }
        }
        return bcd;
    }

    /**
     * 格式化输出 字符串
     * [*]左对齐,右补空格

     *
     * @param str
     * @param min_length : 最小输出长度
     * @return
     */
    public static String formatLeftS(String str, int min_length) {
        String format = "%-" + (min_length < 1 ? 1 : min_length) + "s";
        return String.format(format, str);
    }

    public static String getPrinterformatlefts(String title, int liecount) {
        try {
            String CenterTitle = "";
            byte[] bytes = new byte[liecount-title.length()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = 0x20;
            }
            byte[] bytes1 = title.getBytes();
            System.arraycopy(bytes1, 0, bytes, 0, bytes1.length);
            return new String(bytes);
        }catch (Exception ex)
        {
            return title;
        }

    }

    public static boolean jiesuo()
    {
        boolean flag = false;
        try {
            File file = new File("data/system/jh.key");
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream di = new DataInputStream(fileInputStream);
            String str = null;

            while ((str = di.readLine()) != null) {
                if (str.equals("com.wangpos.com.apk")) {
                    flag = true;//解锁
                    file.delete();
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;//true 解锁；false 已为激活状态
    }
    public static boolean suoji()
    {
        try {
            File file = new File("data/system/jh.key");
            BufferedReader br = new BufferedReader(new FileReader(file));

                if (file.exists()) {//若文件存在，删除
                    file.delete();
                }
            //创建新文件并写入包名
                    file.createNewFile();
                    FileWriter fw  = new FileWriter(file);
                    fw.write("com.wangpos.com.apk");
                    fw.flush();
                    fw.close();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return true;
    }

}
