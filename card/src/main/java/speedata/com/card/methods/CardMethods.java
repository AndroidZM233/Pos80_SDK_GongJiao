package speedata.com.tianjin.methods;

import android.os.RemoteException;
import android.util.Log;

import com.spd.base.utils.Datautils;

import java.util.Arrays;

import wangpos.sdk4.libbasebinder.BankCard;

/**
 * Created by 张明_ on 2019/2/18.
 * Email 741183142@qq.com
 */
public class CardMethods {
    private static final String TAG = "SPEEDATA_BUS";
    /**
     * //获取PSAM卡终端机编号指令
     */
    public static final byte[] PSAN_GET_ID = {0x00, (byte) 0xB0, (byte) 0x96, 0x00, 0x06};
    /**
     * //交通部
     */
    public static final byte[] PSAM_SELECT_DIR = {0x00, (byte) 0xA4, 0x00, 0x00, 0x02, (byte) 0x80, 0x11};

    /**
     * //读取psam卡17文件
     */
    public static final byte[] PSAM_GET_17FILE = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x01};

    /**
     * //选择PPSE支付环境
     */
    public static final byte[] SELEC_PPSE = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0e, 0x32, 0x50, 0x41, 0x59, 0x2e, 0x53, 0x59, 0x53, 0x2e, 0x44, 0x44, 0x46, 0x30, 0x31};

    /**
     * //选择电子钱包应用
     */
    public static final byte[] SELECT_ICCARD_QIANBAO = {0x00, (byte) 0xA4, 0x04, 0x00, 0x08, (byte) 0xA0, 0x00, 0x00, 0x06, 0x32, 0x01, 0x01, 0x05};

    /**
     * //读CPU卡应用下公共应用基本信息文件指令 15文件
     */
    public static final byte[] READ_ICCARD_15FILE = {0x00, (byte) 0xB0, (byte) 0x95, 0x00, 0x00};

    /**
     * 读CPU17文件
     */

    public static final byte[] READ_ICCARD_17FILE = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x00};

    /**
     * //住建部
     */
    public static final byte[] PSAMZHUJIAN_SELECT_DIR = {0x00, (byte) 0xA4, 0x00, 0x00, 0x02, (byte) 0x10, 0x01};
    /**
     * 返回正确结果
     */
    public static final byte[] APDU_RESULT_SUCCESS = {(byte) 0x90, 0x00};
    /**
     * CPU卡黑名单结果
     */
    public static final byte[] APDU_RESULT_FAILE = {(byte) 0x62, (byte) 0x83};
    //本地城市代码
    public static final byte[] ucCityCodeII = {(byte) 0x11, (byte) 0x21};
    //本地发型机构代码
    public static final byte[] ucIssuerCodeII = {(byte) 0x01, (byte) 0x13, (byte) 0x11,
            (byte) 0x21, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,};
    //受理方机构标识
    public static final byte[] ucShouLiCodeII = {(byte) 0x11, (byte) 0x13, (byte) 0x11,
            (byte) 0x21, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,};
    //住建部
    public static final byte[] ZJBCARD = {(byte) 0x01};
    //交通部
    public static final byte[] JTBCARD = {(byte) 0x02};

    /**
     * 封装接口自定义
     *
     * @param cardType 卡片类型
     * @param sendApdu 发送指令
     * @return 结果
     */
    public static byte[] sendApdus(BankCard mBankCard, int cardType, byte[] sendApdu) {
        byte[] reBytes = null;
        //微智接口返回数据
        byte[] respdata = new byte[512];
        //微智接口返回数据长度
        int[] resplen = new int[1];
        try {
            int retvalue = mBankCard.sendAPDU(cardType, sendApdu, sendApdu.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                Log.e(TAG, "微智接口返回错误码" + retvalue);
                return reBytes;
            }
            if (!Arrays.equals(APDU_RESULT_SUCCESS, Datautils.cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                return Datautils.cutBytes(respdata, resplen[0] - 2, 2);
            }
            reBytes = Datautils.cutBytes(respdata, 0, resplen[0] - 2);
        } catch (RemoteException e) {
            e.printStackTrace();
            try {
                mBankCard.breakOffCommand();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
        return reBytes;
    }
}
