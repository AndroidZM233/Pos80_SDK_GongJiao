package com.spd.bus.spdata.been;

/**
 * Created by 张明_ on 2018/8/30.
 * Email 741183142@qq.com
 */

public class TCommInfo {
    /**
     * 有效标志
     */
    public byte fValid;
    /**
     * 记录指针,1
     */
    public byte cPtr;
    /**
     * 钱包计数,2,3
     */
    public byte[] iPurCount;
    /**
     * 进程标志,4
     */
    public byte fProc;
    /**
     * 月票计数,5,6
     */
    public byte[] iYueCount;
    /**
     * 黑名单标志,7
     */
    public byte fBlack;
    /**
     * 文件标识
     */
    public byte fFileNr;
    /**
     * 本次交易时间
     */
    public byte[] utcTime;

}
