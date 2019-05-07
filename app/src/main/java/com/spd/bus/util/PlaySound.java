package com.spd.bus.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


import com.spd.bus.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo-pc on 2017/8/10.
 */

public class PlaySound {

    private static Map<Integer, Integer> mapSRC;
    private static SoundPool sp; //声音池

    public static final int initerro = 0;
    public static final int dang = 1;
    public static final int xiaofeiSuccse = 2;
    public static final int qingchongshua = 3;
    public static final int setSuccess = 4;
    public static final int ZHIFUBAO = 5;
    public static final int ZHENGZAICHULI = 6;
    public static final int XUESHENGKA = 6;
    public static final int QINGTOUBI = 7;
    public static final int ERWEIMASHIXIAO = 8;
    public static final int QINGSHEZHI = 9;
    public static final int MASHANGYIXING = 10;
    public static final int SIJISHANGBAN = 11;
    public static final int WUXIAOKA = 12;
    public static final int ZHENGZZAICHULI = 13;
    public static final int YINLIAN = 14;
    public static final int JINGLAOKA = 15;
    public static final int DIDI = 16;
    public static final int AIXINKA = 17;
    public static final int CODESHIXIAO = 18;
    public static final int QINGQIANDAO = 19;
    public static int NO_CYCLE = 0;//不循环


    //初始化声音池
    public static void initSoundPool(Context context) {
        if (sp == null) {
            sp = new SoundPool(4, AudioManager.STREAM_NOTIFICATION, 0);
        }
        mapSRC = new HashMap<>();
        mapSRC.put(initerro, sp.load(context, R.raw.test, 0));
        mapSRC.put(dang, sp.load(context, R.raw.di, 0));
        mapSRC.put(xiaofeiSuccse, sp.load(context, R.raw.xiaofeichenggong, 0));
        mapSRC.put(qingchongshua, sp.load(context, R.raw.qingchongshua, 0));
        mapSRC.put(setSuccess, sp.load(context, R.raw.shezhiwancheng, 0));
        mapSRC.put(ZHIFUBAO, sp.load(context, R.raw.zhifubao, 0));
        mapSRC.put(ZHENGZAICHULI, sp.load(context, R.raw.zhengzaichulizhong, 0));
        mapSRC.put(XUESHENGKA, sp.load(context, R.raw.xueshengka, 0));
        mapSRC.put(ERWEIMASHIXIAO, sp.load(context, R.raw.erweimashixiao, 0));
        mapSRC.put(QINGTOUBI, sp.load(context, R.raw.qingtoubi, 0));
        mapSRC.put(QINGSHEZHI, sp.load(context, R.raw.qingshezhi, 0));
        mapSRC.put(MASHANGYIXING, sp.load(context, R.raw.mashangyixing, 0));
        mapSRC.put(SIJISHANGBAN, sp.load(context, R.raw.sijishangban, 0));
        mapSRC.put(WUXIAOKA, sp.load(context, R.raw.wuxiaoka, 0));
        mapSRC.put(ZHENGZZAICHULI, sp.load(context, R.raw.zhengzaichulizhong, 0));
        mapSRC.put(YINLIAN, sp.load(context, R.raw.unionpay, 0));
        mapSRC.put(JINGLAOKA, sp.load(context, R.raw.jinglaoka, 0));
        mapSRC.put(DIDI, sp.load(context, R.raw.dd1, 0));
        mapSRC.put(AIXINKA, sp.load(context, R.raw.aixinka, 0));
        mapSRC.put(CODESHIXIAO, sp.load(context, R.raw.erweimashixiao, 0));
        mapSRC.put(QINGQIANDAO, sp.load(context, R.raw.qingqiandao, 0));
    }


    /**
     * 播放声音池的声音
     */
    public static void play(int sound, int number) {
        sp.play(mapSRC.get(sound),//播放的声音资源
                1f,//左声道，范围为0--1.0
                1f,//右声道，范围为0--1.0
                1, //优先级，0为最低优先级
                number,//循环次数,0为不循环
                1);//播放速率，1为正常速率
    }


}
