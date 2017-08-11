package net.chetong.order.model;

import java.math.BigDecimal;

public class TransGetUserVO {
	private Long userId;
	private String lastName;
	private String firstName;
	private double personLongitude;
	private double personLatitude;
	private String mobile;
	private String isOnLine;
	private Long groupId;
	private BigDecimal distance; 
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public double getPersonLongitude() {
		return personLongitude;
	}
	public void setPersonLongitude(double personLongitude) {
		this.personLongitude = personLongitude;
	}
	public double getPersonLatitude() {
		return personLatitude;
	}
	public void setPersonLatitude(double personLatitude) {
		this.personLatitude = personLatitude;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getIsOnLine() {
		return isOnLine;
	}
	public void setIsOnLine(String isOnLine) {
		this.isOnLine = isOnLine;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public BigDecimal getDistance() {
		return distance;
	}
	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}
}
