package com.spd.yinlianpay.card;


import ui.wangpos.com.utiltool.ByteUtil;

/**
 * Created by HS on 2018/9/6.
 */

public class PID {

    int[] mLengths = new int[]{
            17,                     //0  PID
            1,                      //1  PID length
            1,                      //2  Whether pid is enabled
            1,                      //3  Status check switch
            1,                      //4  amount 0 check
            1,                      //5  0 amount
            1,                      //6  Contactless transaction limit check switch
            1,                      //7  CVM limit check switch
            1,                      //8  Non-connected minimum check
            8,                      //9  Non-receiving transaction limit
            8,                      //10 CVM quota
            8,                      //11 Non-receiving minimum
            1,                      //12 TAG9F66
            1,                      //13 Non-connected application does not allow indicato
    };

    byte[] PID = new byte[mLengths[0]];
    byte[] PIDLen = new byte[mLengths[1]];
    byte[] EnableFlag = new byte[mLengths[2]];
    byte[] StatusCheckFlag = new byte[mLengths[3]];
    byte[] AmtZeroCheckFlag = new byte[mLengths[4]];
    byte[] AmtZeroCheckOption = new byte[mLengths[5]];
    byte[] RCTLCheckFlag = new byte[mLengths[6]];
    byte[] CVMlimitCheckFlag = new byte[mLengths[7]];
    byte[] FloorLimitCheckFlag = new byte[mLengths[8]];

    byte[] PaywaveTransLimit = new byte[mLengths[9]];
    byte[] PaywaveCVMLimit = new byte[mLengths[10]];
    byte[] PaywaveFloorLimit = new byte[mLengths[11]];

    byte[] TermQuali_byte2 = new byte[mLengths[12]];
    byte[] CLNotAllowFlag = new byte[mLengths[13]];

    public void setPID(String pid){
        PID = ByteUtil.hexString2Bytes(pid);
    }
    public void setPIDLen(String pidLen){
        PIDLen = ByteUtil.hexString2Bytes(pidLen);
    }
    public void setEnableFlag(String enableFlag){
        EnableFlag = ByteUtil.hexString2Bytes(enableFlag);
    }
    public void setStatusCheckFlag(String statusCheckFlag){
        StatusCheckFlag = ByteUtil.hexString2Bytes(statusCheckFlag);
    }
    public void setAmtZeroCheckFlag(String amtZeroCheckFlag){
        AmtZeroCheckFlag = ByteUtil.hexString2Bytes(amtZeroCheckFlag);
    }

    public void setAmtZeroCheckOption(String amtZeroCheckOption){
        AmtZeroCheckOption = ByteUtil.hexString2Bytes(amtZeroCheckOption);
    }

    public void setRCTLCheckFlag(String rctlCheckFlag){
        RCTLCheckFlag = ByteUtil.hexString2Bytes(rctlCheckFlag);
    }

    public void setCVMlimitCheckFlag(String cvmlimitCheckFlag){
        CVMlimitCheckFlag = ByteUtil.hexString2Bytes(cvmlimitCheckFlag);
    }

    public void setFloorLimitCheckFlag(String floorLimitCheckFlag){
        FloorLimitCheckFlag = ByteUtil.hexString2Bytes(floorLimitCheckFlag);
    }

    public void setPaywaveTransLimit(String paywaveTransLimit){
        PaywaveTransLimit = ByteUtil.hexString2Bytes(paywaveTransLimit);
    }

    public void setPaywaveCVMLimit(String paywaveCVMLimit){
        PaywaveCVMLimit = ByteUtil.hexString2Bytes(paywaveCVMLimit);
    }
    public void setPaywaveFloorLimit(String paywaveFloorLimit){
        PaywaveFloorLimit = ByteUtil.hexString2Bytes(paywaveFloorLimit);
    }
    public void setTermQuali_byte2(String termQuali_byte2){
        TermQuali_byte2 = ByteUtil.hexString2Bytes(termQuali_byte2);
    }
    public void setCLNotAllowFlag(String clNotAllowFlag){
        CLNotAllowFlag = ByteUtil.hexString2Bytes(clNotAllowFlag);
    }



    public PID() {
        init();
    }

    byte[] mDataArray = null;

    public void init() {
        int arraySize = 0;
        for (int i = 0; i < mLengths.length; i++) {
            arraySize += mLengths[i];
        }
        mDataArray = new byte[arraySize];
    }
    public byte[] toByteArray() {
        init();
        int pos = 0;
        int index = 0;
        System.arraycopy(PID,0,mDataArray,pos,PID.length);
        pos += mLengths[index++];
        System.arraycopy(PIDLen,0,mDataArray,pos,PIDLen.length);
        pos += mLengths[index++];
        System.arraycopy(EnableFlag,0,mDataArray,pos,EnableFlag.length);
        pos += mLengths[index++];
        System.arraycopy(StatusCheckFlag,0,mDataArray,pos,StatusCheckFlag.length);
        pos += mLengths[index++];
        System.arraycopy(AmtZeroCheckFlag,0,mDataArray,pos,AmtZeroCheckFlag.length);
        pos += mLengths[index++];
        System.arraycopy(AmtZeroCheckOption,0,mDataArray,pos,AmtZeroCheckOption.length);
        pos += mLengths[index++];
        System.arraycopy(RCTLCheckFlag,0,mDataArray,pos,RCTLCheckFlag.length);
        pos += mLengths[index++];
        System.arraycopy(CVMlimitCheckFlag,0,mDataArray,pos,CVMlimitCheckFlag.length);
        pos += mLengths[index++];
        System.arraycopy(FloorLimitCheckFlag,0,mDataArray,pos,FloorLimitCheckFlag.length);
        pos += mLengths[index++];
        System.arraycopy(PaywaveTransLimit,0,mDataArray,pos,PaywaveTransLimit.length);
        pos += mLengths[index++];
        System.arraycopy(PaywaveCVMLimit,0,mDataArray,pos,PaywaveCVMLimit.length);
        pos += mLengths[index++];
        System.arraycopy(PaywaveFloorLimit,0,mDataArray,pos,PaywaveFloorLimit.length);
        pos += mLengths[index++];
        System.arraycopy(TermQuali_byte2,0,mDataArray,pos,TermQuali_byte2.length);
        pos += mLengths[index++];
        System.arraycopy(CLNotAllowFlag,0,mDataArray,pos,CLNotAllowFlag.length);
        pos += mLengths[index++];
        return mDataArray;
    }
}
