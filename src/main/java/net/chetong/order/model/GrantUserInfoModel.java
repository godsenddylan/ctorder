package net.chetong.order.model;

public class GrantUserInfoModel {

	private long grantId;
	private long grantUserId;
	private String grantName;
	
	public long getGrantId() {
		return grantId;
	}
	public void setGrantId(long grantId) {
		this.grantId = grantId;
	}
	public String getGrantName() {
		return grantName;
	}
	public void setGrantName(String grantName) {
		this.grantName = grantName;
	}
	public long getGrantUserId() {
		return grantUserId;
	}
	public void setGrantUserId(long grantUserId) {
		this.grantUserId = grantUserId;
	}
	
}
