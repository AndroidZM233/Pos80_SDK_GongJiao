package com.spd.bus.spdata;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.spd.base.view.SignalView;
import com.spd.bus.R;

public class test extends BaseActivity {
    private SignalView mSignalView;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spd_bus_layout);
        mSignalView = (SignalView) findViewById(R.id.xinhao);
        getCurrentNetDBM(this,mSignalView);
    }

    /**
     * 得到当前的手机蜂窝网络信号强度
     * 获取LTE网络和3G/2G网络的信号强度的方式有一点不同，
     * LTE网络强度是通过解析字符串获取的，
     * 3G/2G网络信号强度是通过API接口函数完成的。
     * asu 与 dbm 之间的换算关系是 dbm=-113 + 2*asu
     */
    public void getCurrentNetDBM(Context context, SignalView mSignalView) {
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener mylistener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                String signalInfo = signalStrength.toString();
                String[] params = signalInfo.split(" ");

                if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
                    //4G网络 最佳范围   >-90dBm 越大越好
                    int Itedbm = Integer.parseInt(params[9]);
//                    mTv.setText("信号：：：" + Itedbm);
                    int sss = signalStrength.getCdmaDbm();
                    int sss1 = signalStrength.getCdmaEcio();
                    int sss2 = signalStrength.getEvdoDbm();
                    int sss5 = signalStrength.getEvdoEcio();
                    int sss7 = signalStrength.getEvdoSnr();
                    int sss8 = signalStrength.getGsmBitErrorRate();
                    int sss9 = signalStrength.getGsmBitErrorRate();

                    Log.i("tws", "onSignalStrengthsChanged: " + sss);
                    // 设置信号强度
                    if (Itedbm > -100 && Itedbm < 0) {
                        mSignalView.setSignalValue(5);
                    } else if (Itedbm < -100 && Itedbm > -110) {
                        mSignalView.setSignalValue(4);
                    } else if (Itedbm < -110 && Itedbm > -115) {
                        mSignalView.setSignalValue(3);
                    } else if (Itedbm < -115) {
                        mSignalView.setSignalValue(2);
                    } else {
                        mSignalView.setSignalValue(1);
                    }
//                    mSignalView.setSignalValue(0);
                    // 设置信号类型
//                    mSignalView.setSignalTypeText("×");
                    mSignalView.setSignalTypeText("4G");

                } else if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA ||
                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPA ||
                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSUPA ||
                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS) {
                    //3G网络最佳范围  >-90dBm  越大越好  ps:中国移动3G获取不到  返回的无效dbm值是正数（85dbm）
                    //在这个范围的已经确定是3G，但不同运营商的3G有不同的获取方法，故在此需做判断 判断运营商与网络类型的工具类在最下方
//                    String yys = IntenetUtil.getYYS(getApplication());//获取当前运营商
//                    if (yys == "中国移动") {
//                        setDBM(0 + "");//中国移动3G不可获取，故在此返回0
//                    } else if (yys == "中国联通") {
//                        int cdmaDbm = signalStrength.getCdmaDbm();
//                        setDBM(cdmaDbm + "");
//                    } else if (yys == "中国电信") {
//                        int evdoDbm = signalStrength.getEvdoDbm();
//                        setDBM(evdoDbm + "");
//                    }
                    // 设置信号强度
                    mSignalView.setSignalValue(3);
                    // 设置信号类型
                    mSignalView.setSignalTypeText("3G");
                } else {
                    //2G网络最佳范围>-90dBm 越大越好
                    int asu = signalStrength.getGsmSignalStrength();
                    int dbm = -113 + 2 * asu;
//                    setDBM(dbm + "");
                    // 设置信号强度
                    mSignalView.setSignalValue(0);
                    // 设置信号类型
//                    mSignalView.setSignalTypeText("2G");
                    mSignalView.setSignalTypeText("×");
                }

            }
        };
        //开始监听
        tm.listen(mylistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

}
