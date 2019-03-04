package speedata.com.tianjin.methods;

import com.spd.base.utils.Datautils;

import java.util.Arrays;

import speedata.com.tianjin.methods.bean.TCardOpDU;
import speedata.com.tianjin.methods.bean.TPCardDU;
import wangpos.sdk4.libbasebinder.BankCard;

/**
 * 交通部卡
 * Created by 张明_ on 2019/2/18.
 * Email 741183142@qq.com
 */
public class JTBCardManager implements ICardInterface {
    private static final Object LOCK = new Object();
    private static JTBCardManager jtbCardManager;
    private TCardOpDU tCardOpDU = new TCardOpDU();
    private TPCardDU cardDU = new TPCardDU();

    public static JTBCardManager getInstance() {
        if (jtbCardManager == null) {
            synchronized (LOCK) {
                jtbCardManager = new JTBCardManager();
            }
        }
        return jtbCardManager;
    }


    /**
     * 交通部消费
     *
     * @return
     */
    @Override
    public int getSnr(BankCard mBankCard) {
        cardDU.setCardClass(new byte[]{(byte) 0x07});
//        tCardOpDU.setfInBus(1);
//        //系统时间
//        byte[] systemTime = Datautils.getDateTime();
//        tCardOpDU.setUcDateTime(systemTime);
//        tCardOpDU.setUcCardClass(CardMethods.JTBCARD);
//        tCardOpDU.setUcCheckDate(new byte[]{(byte) 0x20, (byte) 0x99, (byte) 0x12, (byte) 0x31});
//        tCardOpDU.setUcOtherCity(0);
//
//        //读应用下公共应用基本信息文件指令 Read 15 File
//        byte[] uiReturnStatus = CardMethods.sendApdus(mBankCard, BankCard.CARD_MODE_PICC
//                , CardMethods.READ_ICCARD_15FILE);
//        if (Arrays.equals(CardMethods.APDU_RESULT_SUCCESS))

        return -1;
    }
}
