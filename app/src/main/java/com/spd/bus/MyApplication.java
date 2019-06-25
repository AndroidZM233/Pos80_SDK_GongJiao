package com.spd.bus;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.example.test.yinlianbarcode.interfaces.OnBackListener;
import com.example.test.yinlianbarcode.utils.ScanUtils;
import com.honeywell.barcode.HSMDecodeComponent;
import com.honeywell.barcode.HSMDecoder;

import com.honeywell.camera.CameraManager;
import com.spd.base.been.tianjin.CardRecord;
import com.spd.base.db.DbDaoManage;
import com.spd.base.utils.DateUtils;
import com.spd.base.utils.LogUtils;
import com.spd.bus.spdata.been.PsamBeen;
import com.spd.bus.util.CrashHandler;
import com.spd.bus.util.PlaySound;
import com.spd.bus.timer.HeartTimer;

import java.util.ArrayList;
import java.util.List;


import static com.honeywell.barcode.Symbology.QR;

public class MyApplication extends Application {
    private static HSMDecoder hsmDecoder;
    private HSMDecodeComponent hsmDecodeComponent;
    public static List<CardRecord> cardRecordList = new ArrayList<>();
    public static List<PsamBeen> psamDatas = new ArrayList<>();
    public static byte fSysSta = (byte) 0x01;
    public static final String FILENAME_INFO = "/sdcard/mydownload/info.conf";
    public static final String FILENAME_ICCARD = "/sdcard/mydownload/sijirecord.conf";
    //激活成功与否
    public static boolean isScanSuccess = false;
    /**
     * 单例
     */
    private static MyApplication m_application;

    public static MyApplication getInstance() {
        return m_application;
    }


    public static void setCardRecordList(CardRecord cardRecord) {
        LogUtils.v("start");
        if (cardRecordList.size() < 2) {
            cardRecordList.add(cardRecord);
        } else {
            cardRecordList.remove(cardRecordList.get(0));
            cardRecordList.add(cardRecord);
        }
        LogUtils.v("end");
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


        new Thread(new Runnable() {
            @Override
            public void run() {
//                if (mBankCard == null) {
//                    mBankCard = new BankCard(getApplicationContext());
//                }
//                if (mCore == null) {
//                    mCore = new Core(getApplicationContext());
//                }
//                if (mKey == null) {
//                    mKey = new Key(getApplicationContext());
//                }
//                if (emvCore == null) {
//                    emvCore = new EmvCore(getApplicationContext());
//                }
//                if (log == null) {
//                    log = new socketpusher();
//                }

//                try {
                //更新sp时间到系统时间
//                    byte[] dateTime = new byte[14];
//                    mCore.getDateTime(dateTime);
//                    String strDate = new String(dateTime);
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//                    long timeToLong = simpleDateFormat.parse(strDate).getTime();
//                    LogUtils.i("时间：" + timeToLong);
//                    Intent intent = new Intent();
//                    intent.setAction(ACTION_SET_SYSTIME_BYSP);
//                    intent.putExtra("sp_time", timeToLong);
//                    sendBroadcast(intent);

//                    successCallBack();
                initScanBards(getApplicationContext());
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
            }
        }).start();

        HeartTimer.getIntance(getApplicationContext()).initTimer();

        LogUtils.d("查库开始" + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));
        long dbCount = DbDaoManage.getDaoSession().getCardRecordDao().count();
        if (dbCount > 0L) {
            int count = 20;
            if (dbCount < 20L) {
                count = (int) dbCount;
            }
            for (int j = count - 1; j >= 0; j--) {
                CardRecord cardRecord = DbDaoManage.getDaoSession().getCardRecordDao()
                        .loadByRowId(dbCount - j);
                if (cardRecord != null) {
                    cardRecordList.add(cardRecord);
                }

            }
        }
        LogUtils.d("查库结束" + DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_yyyyMMddHHmmss));


    }


//    public static BankCard getmBankCard() {
//        return mBankCard;
//    }
//
//    public static void setmBankCard(BankCard mBankCard) {
//        MyApplication.mBankCard = mBankCard;
//    }
//
//    public static Core getmCore() {
//        return mCore;
//    }
//
//    public static void setmCore(Core mCore) {
//        MyApplication.mCore = mCore;
//    }
//
//    public static Key getmKey() {
//        return mKey;
//    }
//
//    public static EmvCore getEmvCore() {
//        return emvCore;
//    }
//
//    public static BankCard getBankCardInstance() {
//        if (mBankCard != null) {
//            return mBankCard;
//        } else {
//            return null;
//        }
//    }

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
