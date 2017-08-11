package net.chetong.order.model.orgimgpull;

public class OrderInfo {
	private String taskId;
	private String caseNo;
	private String accidentTime;
	private String sellerUserName;
	private String buyerUserName;
	private String carNo;
	private String orderType;
	private String guid;
	
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getCaseNo() {
		return caseNo;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public String getAccidentTime() {
		return accidentTime;
	}
	public void setAccidentTime(String accidentTime) {
		this.accidentTime = accidentTime;
	}
	public String getSellerUserName() {
		return sellerUserName;
	}
	public void setSellerUserName(String sellerUserName) {
		this.sellerUserName = sellerUserName;
	}
	public String getBuyerUserName() {
		return buyerUserName;
	}
	public void setBuyerUserName(String buyerUserName) {
		this.buyerUserName = buyerUserName;
	}
	public String getCarNo() {
		return carNo;
	}
	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;  
        if(this == obj) return true;  
        if(obj instanceof OrderInfo){   
        	OrderInfo user =(OrderInfo)obj;
        	if(this.caseNo!=null&&this.caseNo.equals(user.equals(obj))){
        		return true;
        	}
        }
        return false;  
	}
	
	@Override
	public int hashCode() {
		return caseNo.hashCode();
	}
}
