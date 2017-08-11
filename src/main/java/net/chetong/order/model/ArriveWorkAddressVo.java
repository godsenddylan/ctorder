package net.chetong.order.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 确认到达目的地VO对象
 * 
 * @author lijq
 *
 */
public class ArriveWorkAddressVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long userId; // 车童id

	private String supportLinktel; // 技术支持联系人电话

	private String caseLinktel; // 案件现场联系人电话
	
	private String linkTel; //fmOrder联系人电话

	private Long serviceId;
	private String serviceName; /*-- 服务类别 --*/

	private BigDecimal longitude; /*-- 经度 --*/
	private BigDecimal latitude; /*-- 纬度--*/
	
	private BigDecimal orderlongitude; /*-- 订单作业经度 --*/
	private BigDecimal orderlatitude; /*-- 订单作业纬度 --*/
	private Date lastNotifyTime; /*-- 上次通讯时间 --*/
	
	private String caseNo;//报案号
	private String orderNo; //订单号
	
	private String workAddress; //到达地址
	
	private String limitTime; //时限要求
	
	private String trackState; //轨迹状态

	public String getTrackState() {
		return trackState;
	}

	public void setTrackState(String trackState) {
		this.trackState = trackState;
	}

	public String getLinkTel() {
		return linkTel;
	}

	public void setLinkTel(String linkTel) {
		this.linkTel = linkTel;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(String limitTime) {
		this.limitTime = limitTime;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getWorkAddress() {
		return workAddress;
	}

	public void setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
	}

	public BigDecimal getOrderlongitude() {
		return orderlongitude;
	}

	public void setOrderlongitude(BigDecimal orderlongitude) {
		this.orderlongitude = orderlongitude;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getSupportLinktel() {
		return supportLinktel;
	}

	public void setSupportLinktel(String supportLinktel) {
		this.supportLinktel = supportLinktel;
	}

	public String getCaseLinktel() {
		return caseLinktel;
	}

	public void setCaseLinktel(String caseLinktel) {
		this.caseLinktel = caseLinktel;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public Date getLastNotifyTime() {
		return lastNotifyTime;
	}

	public void setLastNotifyTime(Date lastNotifyTime) {
		this.lastNotifyTime = lastNotifyTime;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getOrderlatitude() {
		return orderlatitude;
	}

	public void setOrderlatitude(BigDecimal orderlatitude) {
		this.orderlatitude = orderlatitude;
	}
	
	

}
