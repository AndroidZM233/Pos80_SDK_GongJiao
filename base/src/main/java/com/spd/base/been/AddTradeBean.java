package com.spd.base.been;

/**
 * //                            _ooOoo_
 * //                           o8888888o
 * //                           88" . "88
 * //                           (| -_- |)
 * //                            O\ = /O
 * //                        ____/`---'\____
 * //                      .   ' \\| |// `.
 * //                       / \\||| : |||// \
 * //                     / _||||| -:- |||||- \
 * //                       | | \\\ - /// | |
 * //                     | \_| ''\---/'' | |
 * //                      \ .-\__ `-` ___/-. /
 * //                   ___`. .' /--.--\ `. . __
 * //                ."" '< `.___\_<|>_/___.' >'"".
 * //               | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * //                 \ \ `-. \_ __\ /__ _/ .-` / /
 * //         ======`-.____`-.___\_____/___.-`____.-'======
 * //                            `=---='
 * //
 * //         .............................................
 * //                  佛祖镇楼                  BUG辟易
 *
 * @author :EchoXBR in  2018/12/25 下午4:06.
 * 功能描述:添加交易记录实体类
 */
public class AddTradeBean {

    //终端流水号
    private String localTxnSeq;
    //交易性质
    private String txnAttr="1";
    //终端编号
    private String posId;
    //人脸唯一标识码
    private String faceid;
    //识别通过 扣款2元 固定值
    private String txnamt="200";
    private String txnDate;
    private String txnTime;

    public String getLocalTxnSeq() {
        return localTxnSeq;
    }

    public void setLocalTxnSeq(String localTxnSeq) {
        this.localTxnSeq = localTxnSeq;
    }

    public String getTxnAttr() {
        return txnAttr;
    }

    public void setTxnAttr(String txnAttr) {
        this.txnAttr = txnAttr;
    }

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    public String getFaceid() {
        return faceid;
    }

    public void setFaceid(String faceid) {
        this.faceid = faceid;
    }

    public String getTxnamt() {
        return txnamt;
    }

    public void setTxnamt(String txnamt) {
        this.txnamt = txnamt;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }

    public String getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(String txnTime) {
        this.txnTime = txnTime;
    }
}
