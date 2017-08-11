package net.chetong.order.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 页面轨迹信息VO
 * 
 * ViewTrackInfo
 * 
 * lijq
 * 2017年2月23日 下午3:35:46
 * 
 * @version 1.0.0
 *
 */
public class ViewTrackInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id; //轨迹记录表id
	
	private String userId;
	
	private String state;
	
	private Integer startTime;
	
	private Integer endTime = null;
	
	private String userName;
	
	private String phone;
	
	private String header;
	
	private String driverPoint;
	
	private String accidentPoint;
	
	private Object track;
	
	private BigDecimal longitude;
	
	private BigDecimal latitude;
	
	private String dealStat;
	

	public String getDealStat() {
		return dealStat;
	}

	public void setDealStat(String dealStat) {
		this.dealStat = dealStat;
	}


	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}


	public Integer getStartTime() {
		return startTime;
	}

	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}

	public Integer getEndTime() {
		return endTime;
	}

	public void setEndTime(Integer endTime) {
		this.endTime = endTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getDriverPoint() {
		return driverPoint;
	}

	public void setDriverPoint(String driverPoint) {
		this.driverPoint = driverPoint;
	}

	public String getAccidentPoint() {
		return accidentPoint;
	}

	public void setAccidentPoint(String accidentPoint) {
		this.accidentPoint = accidentPoint;
	}

	public Object getTrack() {
		return track;
	}

	public void setTrack(Object track) {
		this.track = track;
	}
	
	
	

}
