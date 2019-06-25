package com.spd.bus.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
//银联刷卡记录
@Table(name = "unionpay", id = "Id")
public class UnionPay extends Model {
	@Column(name = "doubleState")
	private String doubleState;// 双免状态  ----2双免未结算，0双免结算成功，1双免结算失败，
	@Column(name = "oDAState")
	private String oDAState;// ODA状态 -----6 ODA记录启用  5 ODA记录暂未启用
	@Column(name = "type")
	private String type;// 类型：双免（00）或者ODA（01） 02默认值
	@Column(name = "dateTime")
	private long dateTime;// 时间
	@Column(name = "smRecord")
	private String smRecord;// 双免记录
	@Column(name = "primaryAcountNum")
	private String primaryAcountNum;// 主账号
	@Column(name = "amount")
	private String amount;// 消费金额
	@Column(name = "tradingFlow")
	private String tradingFlow;// 交易流水
	@Column(name = "stationTime")
	private String stationTime;// 进站时间
	@Column(name = "cardSerial")
	private String cardSerial;// 卡片序列号
	@Column(name = "TwoTrackData")
	private String TwoTrackData;// 二磁道数据
	@Column(name = "ICCardDataDomain")
	private String ICCardDataDomain;// IC卡数据域
	@Column(name = "batchNumber")
	private String batchNumber;// 批次号
	@Column(name = "isPay")
	private String isPay;// 0 未支付 1已支付（双免）
	@Column(name = "payStatus")
	private String payStatus;// 00成功，其它失败（双免）
	@Column(name = "responseCode")
	private String responseCode;// 响应码（双免）
	@Column(name = "retrievingNum")
	private String retrievingNum;// 追踪标识
	@Column(name = "tag")
	private int tag;// 上传标志（0已上传，1未上传）
	@Column(name = "priority")
	private int priority;// 优先级（0,1,2,3,4,5,6）优先级一次递增
	@Column(name = "remake")
	private String remake;// 备注

	public String getDoubleState() {
		return doubleState;
	}

	public void setDoubleState(String doubleState) {
		this.doubleState = doubleState;
	}

	public String getoDAState() {
		return oDAState;
	}

	public void setoDAState(String oDAState) {
		this.oDAState = oDAState;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}

	public String getSmRecord() {
		return smRecord;
	}

	public void setSmRecord(String smRecord) {
		this.smRecord = smRecord;
	}

	public String getPrimaryAcountNum() {
		return primaryAcountNum;
	}

	public void setPrimaryAcountNum(String primaryAcountNum) {
		this.primaryAcountNum = primaryAcountNum;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTradingFlow() {
		return tradingFlow;
	}

	public void setTradingFlow(String tradingFlow) {
		this.tradingFlow = tradingFlow;
	}

	public String getStationTime() {
		return stationTime;
	}

	public void setStationTime(String stationTime) {
		this.stationTime = stationTime;
	}

	public String getCardSerial() {
		return cardSerial;
	}

	public void setCardSerial(String cardSerial) {
		this.cardSerial = cardSerial;
	}

	public String getTwoTrackData() {
		return TwoTrackData;
	}

	public void setTwoTrackData(String twoTrackData) {
		TwoTrackData = twoTrackData;
	}

	public String getICCardDataDomain() {
		return ICCardDataDomain;
	}

	public void setICCardDataDomain(String iCCardDataDomain) {
		ICCardDataDomain = iCCardDataDomain;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public String getIsPay() {
		return isPay;
	}

	public void setIsPay(String isPay) {
		this.isPay = isPay;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getRetrievingNum() {
		return retrievingNum;
	}

	public void setRetrievingNum(String retrievingNum) {
		this.retrievingNum = retrievingNum;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getRemake() {
		return remake;
	}

	public void setRemake(String remake) {
		this.remake = remake;
	}

}
