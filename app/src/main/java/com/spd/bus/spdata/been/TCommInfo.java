package com.spd.bus.spdata.been;

import com.spd.base.utils.Datautils;

/**
 * Created by 张明_ on 2018/8/30.
 * Email 741183142@qq.com
 */

public class TCommInfo {
    public byte fValid;                                   // 有效标志
    public byte cPtr;                                     // 记录指针,0
    public byte[] ucPurCount = new byte[2];                            // 钱包计数,1,2
    public byte fProc;                                    // 进程标志,3
    public byte[] ucYueCount = new byte[2];                            // 月票计数,4,5
    public byte fBlack;                                   // 黑名单标志,6
    public byte fFileNr;                                  // 记录类型,7
    public byte fSubWay;                                  // 轨道交通专用标志,8
    public byte[] rfu = new byte[6];                                      // 9-14
    public byte chk;

    public int iPurCount;
    public int iYueCount;

    public void setData(byte[] data) {
        cPtr = data[0];
        ucPurCount = Datautils.cutBytes(data, 1, 2);
        fProc = data[3];
        ucYueCount = Datautils.cutBytes(data, 4, 2);
        fBlack = data[6];
        fFileNr = data[7];
        fSubWay = data[8];
        rfu = Datautils.cutBytes(data, 9, 6);
        chk = data[15];
    }
}
