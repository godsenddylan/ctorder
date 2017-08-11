package net.chetong.order.model.form;

public class QueryImageModel {
	private String userId;
	private String caseNo;
	private String orderNo;
	private String serviceId;
	private String tagType; //图片标签类型
	private  String isYC;//是否是永诚订单 1 是 0 不是
	
	public QueryImageModel() {
		super();
	}

	public QueryImageModel(String userId, String caseNo, String orderNo, String serviceId,String tagType) {
		this.userId = userId;
		this.caseNo = caseNo;
		this.orderNo = orderNo;
		this.serviceId = serviceId;
		this.tagType = tagType;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
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
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getTagType() {
		return tagType;
	}
	public void setTagType(String tagType) {
		this.tagType = tagType;
	}
	public String getIsYC() {
		return isYC;
	}
	public void setIsYC(String isYC) {
		this.isYC = isYC;
	}
}
