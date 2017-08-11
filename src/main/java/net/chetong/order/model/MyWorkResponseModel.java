package net.chetong.order.model;

import java.math.BigDecimal;

import net.chetong.order.util.StringUtil;

/**
 * 导出数据查询
 * @author wufj@chetong.net
 *         2015年12月7日 下午1:58:19
 */
public class MyWorkResponseModel {
	private String caseNo;//报案号
	private String orderNo;//订单号
	private String insurerName;//承包人
	private String buyerName;//买家名称
	private String linkMan;//联系人
	private String carNo;//车牌号
	
	private String sellerName;//服务人姓名
	private String sellerTel;//服务人电话
	
	private String serviceId;//服务类型id
	private String orderType;//订单类型
	private String dealStat;//订单状态
	private String accidentTime;//委托时间
	private String workAddress;//作业地址
	private String getAddress;//抢单地址
	private String mileage;//距离
	private String lossAmount;//定损金额
	private String auditPrice;//核损金额
	private String extraReward;//买家奖励
	private String ctDeductMoney;//车童扣款
	private String teamDeductMoney;//团队扣款

	private String totalDeductMoney;//扣款合计
	
	private String deductReason;//扣款原因
	private String deductMark;//扣款备注
	
	private String groupManageFee;//团队管理费
	
	private String ctBaseFee;//基础费
	private String ctRemoteFee;//远程作业费
	private String ctOverFee;//超额附加费
	private String ctServiceFee;//服务费
	private String isSimple;//是否简易流程
	private String isFast; //是否快赔
	
	private String withdrawReason; //撤单原因
	private String withdrawTime; //撤单时间
	
	public String getCaseNo() {
		return caseNo;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getInsurerName() {
		return insurerName;
	}
	public void setInsurerName(String insurerName) {
		this.insurerName = insurerName;
	}
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public String getLinkMan() {
		return linkMan;
	}
	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}
	public String getCarNo() {
		return carNo;
	}
	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getSellerTel() {
		return sellerTel;
	}
	public void setSellerTel(String sellerTel) {
		this.sellerTel = sellerTel;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getDealStat() {
		return dealStat;
	}
	public void setDealStat(String dealStat) {
		this.dealStat = dealStat;
	}
	public String getAccidentTime() {
		return accidentTime;
	}
	public void setAccidentTime(String accidentTime) {
		this.accidentTime = accidentTime;
	}
	public String getWorkAddress() {
		return workAddress;
	}
	public void setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
	}
	public String getGetAddress() {
		return getAddress;
	}
	public void setGetAddress(String getAddress) {
		this.getAddress = getAddress;
	}
	public String getMileage() {
		return mileage;
	}
	public void setMileage(String mileage) {
		this.mileage = mileage;
	}
	public String getLossAmount() {
		return lossAmount;
	}
	public void setLossAmount(String lossAmount) {
		this.lossAmount = lossAmount;
	}
	public String getAuditPrice() {
		return auditPrice;
	}
	public void setAuditPrice(String auditPrice) {
		this.auditPrice = auditPrice;
	}
	public String getExtraReward() {
		return extraReward;
	}
	public void setExtraReward(String extraReward) {
		this.extraReward = extraReward;
	}
	public String getCtDeductMoney() {
		return ctDeductMoney;
	}
	public void setCtDeductMoney(String ctDeductMoney) {
		this.ctDeductMoney = ctDeductMoney;
	}
	public String getTeamDeductMoney() {
		return teamDeductMoney;
	}
	public void setTeamDeductMoney(String teamDeductMoney) {
		this.teamDeductMoney = teamDeductMoney;
	}
	public String getTotalDeductMoney() {
		return totalDeductMoney;
	}
	public void setTotalDeductMoney(String totalDeductMoney) {
		this.totalDeductMoney = totalDeductMoney;
	}
	public String getDeductReason() {
		return deductReason;
	}
	public void setDeductReason(String deductReason) {
		this.deductReason = deductReason;
	}
	public String getDeductMark() {
		return deductMark;
	}
	public void setDeductMark(String deductMark) {
		this.deductMark = deductMark;
	}
	public String getGroupManageFee() {
		return groupManageFee;
	}
	public void setGroupManageFee(String groupManageFee) {
		this.groupManageFee = groupManageFee;
	}
	public String getCtBaseFee() {
		return ctBaseFee;
	}
	public void setCtBaseFee(String ctBaseFee) {
		this.ctBaseFee = ctBaseFee;
	}
	public String getCtRemoteFee() {
		return ctRemoteFee;
	}
	public void setCtRemoteFee(String ctRemoteFee) {
		this.ctRemoteFee = ctRemoteFee;
	}
	public String getCtOverFee() {
		return ctOverFee;
	}
	public void setCtOverFee(String ctOverFee) {
		this.ctOverFee = ctOverFee;
	}
	public String getCtServiceFee() {
		if(ctServiceFee==null){
			BigDecimal ctBaseFee_decimal = new BigDecimal(StringUtil.isNullOrEmpty(ctBaseFee)?"0":ctBaseFee);
			BigDecimal ctRemoteFee_decimal = new BigDecimal(StringUtil.isNullOrEmpty(ctRemoteFee)?"0":ctRemoteFee);
			BigDecimal ctOverFee_decimal = new BigDecimal(StringUtil.isNullOrEmpty(ctOverFee)?"0":ctOverFee);
			return ctBaseFee_decimal.add(ctRemoteFee_decimal).add(ctOverFee_decimal).toString();
		}
		return ctServiceFee;
	}
	public void setCtServiceFee(String ctServiceFee) {
		this.ctServiceFee = ctServiceFee;
	}
	public String getIsSimple() {
		return isSimple;
	}
	public void setIsSimple(String isSimple) {
		this.isSimple = isSimple;
	}
	public String getIsFast() {
		return isFast;
	}
	public void setIsFast(String isFast) {
		this.isFast = isFast;
	}
	public String getWithdrawReason() {
		return withdrawReason;
	}
	public void setWithdrawReason(String withdrawReason) {
		this.withdrawReason = withdrawReason;
	}
	public String getWithdrawTime() {
		return withdrawTime;
	}
	public void setWithdrawTime(String withdrawTime) {
		this.withdrawTime = withdrawTime;
	}
}
