package net.chetong.order.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 导出数据查询
 * @author wufj@chetong.net
 *         2015年12月7日 下午1:58:19
 */
public class MyEntrustResponseModel implements Serializable {
	private Long id;	
	private String dealStat;
	private String orderNo;
	private String carNo;
	private String caseId;
	private String caseNo;
	private String accidentAddress;
	private Long buyerId;
	private String buyerName;
	private Long sellerId;
	private String sellerName;
	private String sellerMobile;
	private String sellerSex;
	private String orderType;
	private String payMoney;
	private BigDecimal rewardMoney;
	private String serviceMoney;
	private String getTime;
	private String finishTime;
	private String orgIcon;
	private String isAlow;
	private String urlParams;
	private String ctAddress;
	private String senderName;
	private String buyerLoginName;
	private String buyerOrgName;
	private String linkTel;
	private String linkMan;
	private String auditName;
	private String orderInoutType;
	private BigDecimal baseMoney;
	private BigDecimal travelMoney;
	private BigDecimal extraMoney;
	private String rescueType;
	private BigDecimal sendMoney;
	private BigDecimal auditMoney;
	private String isForward;
	private String orderSource;
	private String serviceName;
	private String ext1;
	private String ext12;
	private String sendTime;
	private String priceType;
	private String isRemote;
	
	private BigDecimal baseMoneyTdk;///基础费的通道费开票费
	private BigDecimal travelMoneyTdk;///差旅费的通道费开票费
	private BigDecimal extraMoneyTdk;///附加费的通道费开票费
	private BigDecimal rewardMoneyTdk;///奖励的通道费开票费
	
	private BigDecimal guideBaseFee;
	private BigDecimal guideAddFee;
	private BigDecimal guideBaseInvoiceFee;
	private BigDecimal guideAddInvoiceFee;
	
	private String orderImportId;/*导入订单记录id*/
	private String isSimple;/*是否简易流程订单 0-否，1-是*/
	private String isFast;/*是否快赔订单 0-否，1-是*/
	
	private String withdrawReason; //撤单原因
	private String withdrawTime; //撤单时间
	
	public String getExt1() {
		return ext1;
	}
	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}
	public String getRescueType() {
		return rescueType;
	}
	public void setRescueType(String rescueType) {
		this.rescueType = rescueType;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public String getCarNo() {
		return carNo;
	}
	public String getCaseNo() {
		return caseNo;
	}
	public String getAccidentAddress() {
		return accidentAddress;
	}
	public Long getBuyerId() {
		return buyerId;
	}
	public String getBuyerName() {
		return buyerName;
	}
	public Long getSellerId() {
		return sellerId;
	}
	public String getSellerName() {
		return sellerName;
	}
	public String getSellerMobile() {
		return sellerMobile;
	}
	public String getOrderType() {
		return orderType;
	}
	public String getPayMoney() {
		return payMoney;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public void setAccidentAddress(String accidentAddress) {
		this.accidentAddress = accidentAddress;
	}
	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public void setSellerMobile(String sellerMobile) {
		this.sellerMobile = sellerMobile;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDealStat() {
		return dealStat;
	}
	public void setDealStat(String dealStat) {
		this.dealStat = dealStat;
	}
	
	public String getGetTime() {
		return getTime;
	}
	public void setGetTime(String getTime) {
		this.getTime = getTime;
	}
	public String getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}
	public String getSellerSex() {
		return sellerSex;
	}
	public void setSellerSex(String sellerSex) {
		this.sellerSex = sellerSex;
	}
	public String getServiceMoney() {
		return serviceMoney;
	}
	public void setServiceMoney(String serviceMoney) {
		this.serviceMoney = serviceMoney;
	}
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	public String getOrgIcon() {
		return orgIcon;
	}
	public void setOrgIcon(String orgIcon) {
		this.orgIcon = orgIcon;
	}
	public String getIsAlow() {
		return isAlow;
	}
	public void setIsAlow(String isAlow) {
		this.isAlow = isAlow;
	}
	public String getUrlParams() {
		return urlParams;
	}
	public void setUrlParams(String urlParams) {
		this.urlParams = urlParams;
	}
	public String getCtAddress() {
		return ctAddress;
	}
	public void setCtAddress(String ctAddress) {
		this.ctAddress = ctAddress;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getBuyerLoginName() {
		return buyerLoginName;
	}
	public void setBuyerLoginName(String buyerLoginName) {
		this.buyerLoginName = buyerLoginName;
	}
	public String getBuyerOrgName() {
		return buyerOrgName;
	}
	public void setBuyerOrgName(String buyerOrgName) {
		this.buyerOrgName = buyerOrgName;
	}
	public String getLinkTel() {
		return linkTel;
	}
	public void setLinkTel(String linkTel) {
		this.linkTel = linkTel;
	}
	public String getAuditName() {
		return auditName;
	}
	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}
	public String getOrderInoutType() {
		return orderInoutType;
	}
	public void setOrderInoutType(String orderInoutType) {
		this.orderInoutType = orderInoutType;
	}
	public String getIsForward() {
		return isForward;
	}
	public void setIsForward(String isForward) {
		this.isForward = isForward;
	}
	public String getOrderSource() {
		return orderSource;
	}
	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}
	public String getExt12() {
		return ext12;
	}
	public void setExt12(String ext12) {
		this.ext12 = ext12;
	}
	public BigDecimal getBaseMoneyTdk() {
		return baseMoneyTdk;
	}
	public void setBaseMoneyTdk(BigDecimal baseMoneyTdk) {
		this.baseMoneyTdk = baseMoneyTdk;
	}
	public BigDecimal getTravelMoneyTdk() {
		return travelMoneyTdk;
	}
	public void setTravelMoneyTdk(BigDecimal travelMoneyTdk) {
		this.travelMoneyTdk = travelMoneyTdk;
	}
	public BigDecimal getExtraMoneyTdk() {
		return extraMoneyTdk;
	}
	public void setExtraMoneyTdk(BigDecimal extraMoneyTdk) {
		this.extraMoneyTdk = extraMoneyTdk;
	}
	public BigDecimal getRewardMoneyTdk() {
		return rewardMoneyTdk;
	}
	public void setRewardMoneyTdk(BigDecimal rewardMoneyTdk) {
		this.rewardMoneyTdk = rewardMoneyTdk;
	}
	public String getOrderImportId() {
		return orderImportId;
	}
	public void setOrderImportId(String orderImportId) {
		this.orderImportId = orderImportId;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getLinkMan() {
		return linkMan;
	}
	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}
	public String getPriceType() {
		return priceType;
	}
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}
	public String getIsRemote() {
		return isRemote;
	}
	public void setIsRemote(String isRemote) {
		this.isRemote = isRemote;
	}
	public BigDecimal getGuideBaseFee() {
		return guideBaseFee;
	}
	public void setGuideBaseFee(BigDecimal guideBaseFee) {
		this.guideBaseFee = guideBaseFee;
	}
	public BigDecimal getGuideAddFee() {
		return guideAddFee;
	}
	public void setGuideAddFee(BigDecimal guideAddFee) {
		this.guideAddFee = guideAddFee;
	}
	public BigDecimal getGuideBaseInvoiceFee() {
		return guideBaseInvoiceFee;
	}
	public void setGuideBaseInvoiceFee(BigDecimal guideBaseInvoiceFee) {
		this.guideBaseInvoiceFee = guideBaseInvoiceFee;
	}
	public BigDecimal getGuideAddInvoiceFee() {
		return guideAddInvoiceFee;
	}
	public void setGuideAddInvoiceFee(BigDecimal guideAddInvoiceFee) {
		this.guideAddInvoiceFee = guideAddInvoiceFee;
	}
	public BigDecimal getRewardMoney() {
		return rewardMoney;
	}
	public void setRewardMoney(BigDecimal rewardMoney) {
		this.rewardMoney = rewardMoney;
	}
	public BigDecimal getBaseMoney() {
		return baseMoney;
	}
	public void setBaseMoney(BigDecimal baseMoney) {
		this.baseMoney = baseMoney;
	}
	public BigDecimal getTravelMoney() {
		return travelMoney;
	}
	public void setTravelMoney(BigDecimal travelMoney) {
		this.travelMoney = travelMoney;
	}
	public BigDecimal getExtraMoney() {
		return extraMoney;
	}
	public void setExtraMoney(BigDecimal extraMoney) {
		this.extraMoney = extraMoney;
	}
	public BigDecimal getSendMoney() {
		return sendMoney;
	}
	public void setSendMoney(BigDecimal sendMoney) {
		this.sendMoney = sendMoney;
	}
	public BigDecimal getAuditMoney() {
		return auditMoney;
	}
	public void setAuditMoney(BigDecimal auditMoney) {
		this.auditMoney = auditMoney;
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
