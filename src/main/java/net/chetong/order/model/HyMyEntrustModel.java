package net.chetong.order.model;

import java.io.Serializable;
import java.util.Date;

public class HyMyEntrustModel implements Serializable{

	/**货运险我的委托导出
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;	
	private String dealStat;//	订单状态	
	private String orderNo;//订单号
	private String carNo;
	private String caseNo;//报案号
	private String accidentAddress;//出险地点	
	private Long buyerId;
	private String buyerName;//买家帐号
	private String buyerMobile;//买家电话	
	private Long sellerId;
	private String sellerName;//卖家名称	服务人名称
	private String sellerMobile;//卖家电话 服务人电话
	private String sellerSex; //服务人性别
	private String entrustName;//	合约委托人名字	
	private String entrustMobile;//	合约委托人电话	
	private Date getTime;//	派单时间	
//	private Date finishTime;
//	private String isAlow;
//	private String urlParams;
	private String ctAddress;//	接单地点	
//	private String senderName;
	private String buyerLoginName;//买家帐号	
//	private String buyerOrgName;
//	private String linkTel;
	private String auditName;//	审核人	
//	private String orderInoutType;
	private String buyerMoney;//买家支付金额
	private String sellerMoney;//卖家支付金额
	private String groupMoney;//团队获得佣金
//	private String isForward;
//	private String orderSource;
//	private String serviceName;
//	private String ext1;
//	private String sendTime;
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
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getCarNo() {
		return carNo;
	}
	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}
	public String getCaseNo() {
		return caseNo;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public String getAccidentAddress() {
		return accidentAddress;
	}
	public void setAccidentAddress(String accidentAddress) {
		this.accidentAddress = accidentAddress;
	}
	public Long getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public String getBuyerMobile() {
		return buyerMobile;
	}
	public void setBuyerMobile(String buyerMobile) {
		this.buyerMobile = buyerMobile;
	}
	public Long getSellerId() {
		return sellerId;
	}
	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getSellerMobile() {
		return sellerMobile;
	}
	public void setSellerMobile(String sellerMobile) {
		this.sellerMobile = sellerMobile;
	}
	public String getSellerSex() {
		return sellerSex;
	}
	public void setSellerSex(String sellerSex) {
		this.sellerSex = sellerSex;
	}
	public String getEntrustName() {
		return entrustName;
	}
	public void setEntrustName(String entrustName) {
		this.entrustName = entrustName;
	}
	public String getEntrustMobile() {
		return entrustMobile;
	}
	public void setEntrustMobile(String entrustMobile) {
		this.entrustMobile = entrustMobile;
	}
	public Date getGetTime() {
		return getTime;
	}
	public void setGetTime(Date getTime) {
		this.getTime = getTime;
	}
	public String getCtAddress() {
		return ctAddress;
	}
	public void setCtAddress(String ctAddress) {
		this.ctAddress = ctAddress;
	}
	public String getBuyerLoginName() {
		return buyerLoginName;
	}
	public void setBuyerLoginName(String buyerLoginName) {
		this.buyerLoginName = buyerLoginName;
	}
	public String getAuditName() {
		return auditName;
	}
	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}
	public String getBuyerMoney() {
		return buyerMoney;
	}
	public void setBuyerMoney(String buyerMoney) {
		this.buyerMoney = buyerMoney;
	}
	public String getSellerMoney() {
		return sellerMoney;
	}
	public void setSellerMoney(String sellerMoney) {
		this.sellerMoney = sellerMoney;
	}
	public String getGroupMoney() {
		return groupMoney;
	}
	public void setGroupMoney(String groupMoney) {
		this.groupMoney = groupMoney;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
