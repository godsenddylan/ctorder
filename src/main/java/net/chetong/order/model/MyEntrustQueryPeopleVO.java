package net.chetong.order.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 派单车童列表model
 */
public class MyEntrustQueryPeopleVO implements Serializable{
	private static final long serialVersionUID = 8297817320659268333L;
	private long userId;
	private String serviceId;
	private String lastName; //姓
	private String firstName;//名
	private String sex; //性别 0 - 男 1 - 女
	private String reviewRank; //评价等级
	private String reviewScore; //评价积分
	private String serviceYear; //服务年限
	private double personLongitude; //服务人所在的经度
	private double personLatitude;  //服务人所在的纬度
	private BigDecimal baseMoney; //基础费
	private BigDecimal remoteMoney; //差旅费
	private BigDecimal totalMoney; //总共的金额
	private String isBusy; //置忙 
	private String iconImgPath;//头像地址
	private String isCollect; //是否收藏 1-是 0-不是
	private long negoId = 0L; //议价信息id
	private long teamUserId; //团队用户id
	private String isNego; //是否议价的车童 1-是 0-否
	private String isSeedPerson;//是否种子车童
	private String finishOrderCount;//接单量
	private BigDecimal distance;//距离
	private String isFixedPrice = "0";//是否一口价 0 不是 1是
	private String isOnLine;//是否在线
	private String isScheduler = "0";//是否调度帐号
	
	private BigDecimal guideBaseMoney; //指导价基础费
	private BigDecimal ctMoney; //车童价格
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getReviewRank() {
		return reviewRank;
	}
	public void setReviewRank(String reviewRank) {
		this.reviewRank = reviewRank;
	}
	public String getReviewScore() {
		return reviewScore;
	}
	public void setReviewScore(String reviewScore) {
		this.reviewScore = reviewScore;
	}
	public String getServiceYear() {
		return serviceYear;
	}
	public void setServiceYear(String serviceYear) {
		this.serviceYear = serviceYear;
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
	public BigDecimal getBaseMoney() {
		return baseMoney;
	}
	public void setBaseMoney(BigDecimal baseMoney) {
		this.baseMoney = baseMoney;
	}
	public BigDecimal getRemoteMoney() {
		return remoteMoney;
	}
	public void setRemoteMoney(BigDecimal remoteMoney) {
		this.remoteMoney = remoteMoney;
	}
	public BigDecimal getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}
	public String getIsBusy() {
		return isBusy;
	}
	public void setIsBusy(String isBusy) {
		this.isBusy = isBusy;
	}
	public String getIconImgPath() {
		return iconImgPath;
	}
	public void setIconImgPath(String iconImgPath) {
		this.iconImgPath = iconImgPath;
	}
	public String getIsCollect() {
		return isCollect;
	}
	public void setIsCollect(String isCollect) {
		this.isCollect = isCollect;
	}
	public long getNegoId() {
		return negoId;
	}
	public void setNegoId(long negoId) {
		this.negoId = negoId;
	}
	public long getTeamUserId() {
		return teamUserId;
	}
	public void setTeamUserId(long teamUserId) {
		this.teamUserId = teamUserId;
	}
	public String getIsNego() {
		return isNego;
	}
	public void setIsNego(String isNego) {
		this.isNego = isNego;
	}
	public String getIsSeedPerson() {
		return isSeedPerson;
	}
	public void setIsSeedPerson(String isSeedPerson) {
		this.isSeedPerson = isSeedPerson;
	}
	public String getFinishOrderCount() {
		return finishOrderCount;
	}
	public void setFinishOrderCount(String finishOrderCount) {
		this.finishOrderCount = finishOrderCount;
	}
	public BigDecimal getDistance() {
		return distance;
	}
	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}
	public String getIsFixedPrice() {
		return isFixedPrice;
	}
	public void setIsFixedPrice(String isFixedPrice) {
		this.isFixedPrice = isFixedPrice;
	}
	public String getIsOnLine() {
		return isOnLine;
	}
	public void setIsOnLine(String isOnLine) {
		this.isOnLine = isOnLine;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getIsScheduler() {
		return isScheduler;
	}
	public void setIsScheduler(String isScheduler) {
		this.isScheduler = isScheduler;
	}
	public BigDecimal getGuideBaseMoney() {
		return guideBaseMoney;
	}
	public void setGuideBaseMoney(BigDecimal guideBaseMoney) {
		this.guideBaseMoney = guideBaseMoney;
	}
	public BigDecimal getCtMoney() {
		return ctMoney;
	}
	public void setCtMoney(BigDecimal ctMoney) {
		this.ctMoney = ctMoney;
	}
	
}
