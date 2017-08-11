package net.chetong.order.model;

public class TransGetUserResponse {
	private Long userId;
	private String userName;
	private String mobile;
	private String isOnline;
	private String distance;
	private String groupManageFee;
	private String baseFee;
	private String remoteFee;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getGroupManageFee() {
		return groupManageFee;
	}
	public void setGroupManageFee(String groupManageFee) {
		this.groupManageFee = groupManageFee;
	}
	public String getBaseFee() {
		return baseFee;
	}
	public void setBaseFee(String baseFee) {
		this.baseFee = baseFee;
	}
	public String getRemoteFee() {
		return remoteFee;
	}
	public void setRemoteFee(String remoteFee) {
		this.remoteFee = remoteFee;
	}
}
