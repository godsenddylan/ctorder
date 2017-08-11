package net.chetong.order.model;

import java.io.Serializable;

/**
 * 获取开启记录车童轨迹信息VO
 * 
 * TrackOrderVO
 * 
 * lijq
 * 2017年2月16日 上午9:14:44
 * 
 * @version 1.0.0
 *
 */
public class TrackOrderVO extends BaseVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String sellerUserId;
	
	private String userId;
	
	private String orderNo;
	
	private String caseNo;
	
	private Integer arriveScope; //已到达目的地范围
	
	private Integer trackRemindTime; //未到达提醒时间
	
	private Integer trackOvertime; //超时超时时间
	
	private Double longitude;//车主经度
	
	private Double latitude; //车主纬度
	
	private String getTime; //抢单时间
	
	private String workAddress;
	private String serviceId;
    private String supportLinkman;
	private String supportLinktel;
    private String limitTime;
    
    
	public String getWorkAddress() {
		return workAddress;
	}

	public void setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getSupportLinkman() {
		return supportLinkman;
	}

	public void setSupportLinkman(String supportLinkman) {
		this.supportLinkman = supportLinkman;
	}

	public String getSupportLinktel() {
		return supportLinktel;
	}

	public void setSupportLinktel(String supportLinktel) {
		this.supportLinktel = supportLinktel;
	}

	public String getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(String limitTime) {
		this.limitTime = limitTime;
	}

	public String getSellerUserId() {
		return sellerUserId;
	}

	public void setSellerUserId(String sellerUserId) {
		this.sellerUserId = sellerUserId;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getArriveScope() {
		return arriveScope;
	}

	public void setArriveScope(Integer arriveScope) {
		this.arriveScope = arriveScope;
	}

	public Integer getTrackRemindTime() {
		return trackRemindTime;
	}

	public void setTrackRemindTime(Integer trackRemindTime) {
		this.trackRemindTime = trackRemindTime;
	}

	public Integer getTrackOvertime() {
		return trackOvertime;
	}

	public void setTrackOvertime(Integer trackOvertime) {
		this.trackOvertime = trackOvertime;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getGetTime() {
		return getTime;
	}

	public void setGetTime(String getTime) {
		this.getTime = getTime;
	}
	

}
