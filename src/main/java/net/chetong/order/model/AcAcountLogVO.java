package net.chetong.order.model;

import java.io.Serializable;

public class AcAcountLogVO implements Serializable {

	private static final long serialVersionUID = 5685638764045873821L;
	
	private String id;	/*-- 表ID --*/
	private String userId;	/*--  --*/
	private String tradeId;	/*--  --*/
	private String tradeSeq;	/*-- 交易流水号 --*/
	private String balanceType;	/*-- 收支类型 +收入  -支出 --*/
	private String tradeType;	/*-- 交易类型 01充值收入（+） 02提现支出 03保证金收入（+） 04保证金支出0 5保证金提现06保证金扣款07退款收入（+）08退款支出09坏账收入（+）10公估服务收入 （+）11公估服务支出 12-公估退款收入（估服务支出 12-公估退款收入（+）13-投诉罚款支出 14 - 申诉补偿收入（+）15-奖励充值 16转账支出 17 转账收入 18推荐奖励  19其他 20委托派单收入   21委托派单支出    22  删单费用返还（+） 23 删单费用扣除(-) --*/
	private String tradeStat;	/*-- 交易状态 0 交易中 1交易完成 2交易关闭(失败) --*/
	private String tradeTime;	/*-- 交易时间 --*/
	private String tradeMoney;	/*-- 交易金额 收入为正 支出为负 --*/
	private String totalMoney;	/*-- 当前账户总额 --*/
	private String tradeDesc;	/*-- 交易描述 --*/
	private String operId;	/*--  --*/
	private String operTime;	/*-- 操作时间 --*/
	private String ext1;	/*-- 扩展字段1 --*/
	private String ext2;	/*-- 扩展字段2 --*/
	private String ext3;	/*-- 扩展字段3 --*/
	private String note;	/*-- 备注 --*/
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTradeId() {
		return tradeId;
	}
	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}
	public String getTradeSeq() {
		return tradeSeq;
	}
	public void setTradeSeq(String tradeSeq) {
		this.tradeSeq = tradeSeq;
	}
	public String getBalanceType() {
		return balanceType;
	}
	public void setBalanceType(String balanceType) {
		this.balanceType = balanceType;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getTradeStat() {
		return tradeStat;
	}
	public void setTradeStat(String tradeStat) {
		this.tradeStat = tradeStat;
	}
	public String getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}
	public String getTradeMoney() {
		return tradeMoney;
	}
	public void setTradeMoney(String tradeMoney) {
		this.tradeMoney = tradeMoney;
	}
	public String getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}
	public String getTradeDesc() {
		return tradeDesc;
	}
	public void setTradeDesc(String tradeDesc) {
		this.tradeDesc = tradeDesc;
	}
	public String getOperId() {
		return operId;
	}
	public void setOperId(String operId) {
		this.operId = operId;
	}
	public String getOperTime() {
		return operTime;
	}
	public void setOperTime(String operTime) {
		this.operTime = operTime;
	}
	public String getExt1() {
		return ext1;
	}
	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}
	public String getExt2() {
		return ext2;
	}
	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}
	public String getExt3() {
		return ext3;
	}
	public void setExt3(String ext3) {
		this.ext3 = ext3;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	} 
	
}
