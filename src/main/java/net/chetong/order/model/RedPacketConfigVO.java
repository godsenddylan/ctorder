package net.chetong.order.model;

import java.math.BigDecimal;

/**
 * 红包规则类
 * @author wfj
 */
public class RedPacketConfigVO {

	private Long id;         //id
	private String type;    //红包种类   1.种子红包 2.普惠红包 3.夜间红包
 	private String batch;   //批次
	private String areaCode;   //市
	private String provCode;   //省
	private int ctNumber;       //车童数量
	private String sendTime;    //派发时间
	private String failTime;    //失效时间
	private BigDecimal totalAmount;    //红包总金额
	private BigDecimal lastAmount; //红包剩余总金额
	private int lastCount;             //红包剩余数量
	private int totalCount;            //红包总数量
	private BigDecimal amount;         //红包金额
	private BigDecimal topPrize;   //大奖金额
	private Long batchCreaterId;       //批次创建人
	private String dealStat;           //审核状态   1.未审核 2.审核完成 3.审核退回
	private Long dealPersonId;         //审核人id
	private int successNum;            //抢单成功量
	private int sendNum;               //派单量
	private int failNum;               //失败量
	private int seedOnlineDuration;    //种子车童在线时长 
	private String message;             //红包寄语
	private String title;              //红包主题
	private int singleMinAmount;     //普惠红包最小金额
	private int singleMaxAmount;       //普惠红包最大金额
	private String multiRule;            //是否多规则
	private BigDecimal amountOne;        //第一天红包金额
	private BigDecimal amountTwo;        //第二天红包金额
	private BigDecimal amountMore;       //多于3天红包金额
	private String isActive; //是否启用规则 1：启用 0：没启用
	private String successMessage; //成功寄语
	private String failMessage; //失败寄语
	private int bigPrizeNum;
	private int middlePrizeNum;
	private int smallPrizeNum;
	private int grabNum;
	private String ctType;
	
	public String getSuccessMessage() {
		return successMessage;
	}
	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
	public String getFailMessage() {
		return failMessage;
	}
	public void setFailMessage(String failMessage) {
		this.failMessage = failMessage;
	}
	public int getLastCount() {
		return lastCount;
	}
	public void setLastCount(int lastCount) {
		this.lastCount = lastCount;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getProvCode() {
		return provCode;
	}
	public void setProvCode(String provCode) {
		this.provCode = provCode;
	}
	public int getCtNumber() {
		return ctNumber;
	}
	public void setCtNumber(int ctNumber) {
		this.ctNumber = ctNumber;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		if(null!=sendTime&& !sendTime.isEmpty()&&21==sendTime.length()){
			sendTime = sendTime.substring(0,19);
		}
		this.sendTime = sendTime;
	}
	public String getFailTime() {
		return failTime;
	}
	public void setFailTime(String failTime) {
		if(null!=failTime&& !failTime.isEmpty()&&21==failTime.length()){
			failTime = failTime.substring(0,19);
		}
		this.failTime = failTime;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getDealStat() {
		return dealStat;
	}
	public void setDealStat(String dealStat) {
		this.dealStat = dealStat;
	}
	public int getSuccessNum() {
		return successNum;
	}
	public void setSuccessNum(int successNum) {
		this.successNum = successNum;
	}
	public int getSendNum() {
		return sendNum;
	}
	public void setSendNum(int sendNum) {
		this.sendNum = sendNum;
	}
	public int getFailNum() {
		return failNum;
	}
	public void setFailNum(int failNum) {
		this.failNum = failNum;
	}
	public int getSeedOnlineDuration() {
		return seedOnlineDuration;
	}
	public void setSeedOnlineDuration(int seedOnlineDuration) {
		this.seedOnlineDuration = seedOnlineDuration;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getSingleMinAmount() {
		return singleMinAmount;
	}
	public void setSingleMinAmount(int singleMinAmount) {
		this.singleMinAmount = singleMinAmount;
	}
	public int getSingleMaxAmount() {
		return singleMaxAmount;
	}
	public void setSingleMaxAmount(int singleMaxAmount) {
		this.singleMaxAmount = singleMaxAmount;
	}
	public String getMultiRule() {
		return multiRule;
	}
	public void setMultiRule(String multiRule) {
		this.multiRule = multiRule;
	}
	public BigDecimal getAmountOne() {
		return amountOne;
	}
	public void setAmountOne(BigDecimal amountOne) {
		this.amountOne = amountOne;
	}
	public BigDecimal getAmountTwo() {
		return amountTwo;
	}
	public void setAmountTwo(BigDecimal amountTwo) {
		this.amountTwo = amountTwo;
	}
	public BigDecimal getAmountMore() {
		return amountMore;
	}
	public void setAmountMore(BigDecimal amountMore) {
		this.amountMore = amountMore;
	}
	public Long getBatchCreaterId() {
		return batchCreaterId;
	}
	public void setBatchCreaterId(Long batchCreaterId) {
		this.batchCreaterId = batchCreaterId;
	}
	public Long getDealPersonId() {
		return dealPersonId;
	}
	public void setDealPersonId(Long dealPersonId) {
		this.dealPersonId = dealPersonId;
	}
	public BigDecimal getLastAmount() {
		return lastAmount;
	}
	public void setLastAmount(BigDecimal lastAmount) {
		this.lastAmount = lastAmount;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public BigDecimal getTopPrize() {
		return topPrize;
	}
	public void setTopPrize(BigDecimal topPrize) {
		this.topPrize = topPrize;
	}
	public int getBigPrizeNum() {
		return bigPrizeNum;
	}
	public void setBigPrizeNum(int bigPrizeNum) {
		this.bigPrizeNum = bigPrizeNum;
	}
	public int getMiddlePrizeNum() {
		return middlePrizeNum;
	}
	public void setMiddlePrizeNum(int middlePrizeNum) {
		this.middlePrizeNum = middlePrizeNum;
	}
	public int getSmallPrizeNum() {
		return smallPrizeNum;
	}
	public void setSmallPrizeNum(int smallPrizeNum) {
		this.smallPrizeNum = smallPrizeNum;
	}
	public int getGrabNum() {
		return grabNum;
	}
	public void setGrabNum(int grabNum) {
		this.grabNum = grabNum;
	}
	public String getCtType() {
		return ctType;
	}
	public void setCtType(String ctType) {
		this.ctType = ctType;
	}
}
