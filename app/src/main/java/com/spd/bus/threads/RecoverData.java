package com.spd.bus.threads;

import com.spd.bus.entity.Payrecord;

/**
 * Class: RecoverData
 * package：com.yihuatong.tjgongjiaos.activity.threadrecord
 * Created by hzjst on 2018/9/12.
 * E_mail：hzjstning@163.com
 * Description：
 */
public class RecoverData extends Thread {
    private String jilu;
    private int TAG;
    private String datatime;
    private double times;
    private int JILU;
    private int buscard;
    private int writetag;
    private int traffic;
    private long tradingflow;

    public RecoverData(String jilu, int TAG, String datatime, double times,
                       int JILU, int buscard, int writetag, int traffic, long tradingflow) {
        this.jilu = jilu;
        this.TAG = TAG;
        this.datatime = datatime;
        this.times = times;
        this.JILU = JILU;
        this.buscard = buscard;
        this.writetag = writetag;
        this.traffic = traffic;
        this.tradingflow = tradingflow;
    }

    @Override
    public void run() {
        Payrecord pay = new Payrecord();
        pay.setRecord( jilu );
        pay.setTag( TAG );
        pay.setDatetime( datatime );
        pay.setXiaofei( times );
        pay.setJilu( JILU );
        pay.setBuscard( buscard );
        pay.setWritetag( writetag );
        pay.setTraffic( traffic );
        pay.setTradingflow( tradingflow );
        pay.save();
        super.run();
    }
}
