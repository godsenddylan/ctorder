package net.chetong.order.model.form;

import java.io.Serializable;

@SuppressWarnings("serial")
public class OrderInfoUrlBean implements Serializable {
	
	private String userId;
	
	private String serviceId;
	
	private String orderType;
	
	private String subjectId;
	
	private String isAllow; //是否授权现场定损 0 - 否 1 -是
	
	private String isAllowMediation; //是否授权一次性调解 0 - 否 1 -是
	
	private String orderDetailUrl; //订单详情查看url
	
	private String orderWorkUrl; //订单作业url
	
	private String systemSource; // 这个值为 1 时是永诚系统的订单
	
	private String linkMan;
	
	private String linkTel;
	
	private String buyerUserId;
	
	private String caseNo;
	
	private String carNo;
	
	private String isEdit; //是否可编辑 1-是，0-否
	
	private String dealStat;
	
	private String signaturePath;  //签名图片路径
	
	private String leaveMsgUrl; // 留言的url
	
	private String allowAppealAudit; // 申诉的类型: auditNo-退回申诉, auditBad-差评申诉
	
	private String isSimple; //是否简易流程，1-是，0-否
	
	public String getIsSimple() {
		return isSimple;
	}

	public void setIsSimple(String isSimple) {
		this.isSimple = isSimple;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getIsAllow() {
		return isAllow;
	}

	public void setIsAllow(String isAllow) {
		this.isAllow = isAllow;
	}

	public String getIsAllowMediation() {
		return isAllowMediation;
	}

	public void setIsAllowMediation(String isAllowMediation) {
		this.isAllowMediation = isAllowMediation;
	}

	public String getOrderDetailUrl() {
		return orderDetailUrl;
	}

	public void setOrderDetailUrl(String orderDetailUrl) {
		this.orderDetailUrl = orderDetailUrl;
	}

	public String getOrderWorkUrl() {
		return orderWorkUrl;
	}

	public void setOrderWorkUrl(String orderWorkUrl) {
		this.orderWorkUrl = orderWorkUrl;
	}

	public String getSystemSource() {
		return systemSource;
	}

	public void setSystemSource(String systemSource) {
		this.systemSource = systemSource;
	}

	public String getLinkMan() {
		return linkMan;
	}

	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}

	public String getLinkTel() {
		return linkTel;
	}

	public void setLinkTel(String linkTel) {
		this.linkTel = linkTel;
	}

	public String getBuyerUserId() {
		return buyerUserId;
	}

	public void setBuyerUserId(String buyerUserId) {
		this.buyerUserId = buyerUserId;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	public String getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(String isEdit) {
		this.isEdit = isEdit;
	}

	public String getDealStat() {
		return dealStat;
	}

	public void setDealStat(String dealStat) {
		this.dealStat = dealStat;
	}

	public String getSignaturePath() {
		return signaturePath;
	}

	public void setSignaturePath(String signaturePath) {
		this.signaturePath = signaturePath;
	}

	public String getLeaveMsgUrl() {
		return leaveMsgUrl;
	}

	public void setLeaveMsgUrl(String leaveMsgUrl) {
		this.leaveMsgUrl = leaveMsgUrl;
	}

	public String getAllowAppealAudit() {
		return allowAppealAudit;
	}

	public void setAllowAppealAudit(String allowAppealAudit) {
		this.allowAppealAudit = allowAppealAudit;
	}
}
