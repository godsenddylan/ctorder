package net.chetong.order.model;

import java.io.Serializable;

public class FhCarInfoVO implements Serializable {


	private static final long serialVersionUID = -370322918597822709L;
	
	private String createdBy = null;			/** 创建人 */
	private String createdDate = null;			/** 创建日期 */
	private String updatedBy = null;			/** 修改人 */
	private String updatedDate = null;			/** 修改日期 */
	
	private String id = null; 					/** 表ID */
	private String surveyId = null;				/** 查勘ID */
	private String reportNo = null;				/** 报案号 */
	private String carMark = null; 				/** 车牌 */
	private String vinNo = null; 				/** 车架号 */
	private String engineNo = null; 			/** 发动机号(永) */
	private String firstDate = null;			/** 初登日期 **/
	private String carType = null;				/** 厂牌车型 */
	private String personCount = null;			/** 案件发生时的乘客数 **/
	private String isDrive = null; 				/** 是否可以驾驶 */
	private String carColour = null;			/** 车辆颜色 */
	private String mileageNum = null;			/** 案发时公里数 */
	private String isOverload = null;			/** 是否超载 */
	private String overloadWeight = null;		/** 超载重量*/
	private String markType = null;				/** 号牌种类 */
	private String seatCount = null;			/** 车辆座位数 */
	private String drivingLicense = null;		/** 出险车辆行驶证号码 */
	private String targetType = null; 			/** 1=标的 2=三者 */
	private String remark = null; 				/** 备注 */
	
	private String producerName = null; //生产厂家名称
	private String brandName = null;//品牌名称
	private String serialName = null;//车系名称
	private String typeCode = null;//车型代码
	private String typeName = null;//车型名称
	private String className = null;//车辆类型
	private String productYear = null;//生产年份
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCarMark() {
		return carMark;
	}
	public void setCarMark(String carMark) {
		this.carMark = carMark;
	}
	public String getVinNo() {
		return vinNo;
	}
	public void setVinNo(String vinNo) {
		this.vinNo = vinNo;
	}
	public String getEngineNo() {
		return engineNo;
	}
	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}
	public String getFirstDate() {
		return firstDate;
	}
	public void setFirstDate(String firstDate) {
		this.firstDate = firstDate;
	}
	public String getCarType() {
		return carType;
	}
	public void setCarType(String carType) {
		this.carType = carType;
	}
	public String getPersonCount() {
		return personCount;
	}
	public void setPersonCount(String personCount) {
		this.personCount = personCount;
	}
	public String getIsDrive() {
		return isDrive;
	}
	public void setIsDrive(String isDrive) {
		this.isDrive = isDrive;
	}
	public String getCarColour() {
		return carColour;
	}
	public void setCarColour(String carColour) {
		this.carColour = carColour;
	}
	public String getMileageNum() {
		return mileageNum;
	}
	public void setMileageNum(String mileageNum) {
		this.mileageNum = mileageNum;
	}
	public String getIsOverload() {
		return isOverload;
	}
	public void setIsOverload(String isOverload) {
		this.isOverload = isOverload;
	}
	public String getOverloadWeight() {
		return overloadWeight;
	}
	public void setOverloadWeight(String overloadWeight) {
		this.overloadWeight = overloadWeight;
	}
	public String getMarkType() {
		return markType;
	}
	public void setMarkType(String markType) {
		this.markType = markType;
	}
	public String getSeatCount() {
		return seatCount;
	}
	public void setSeatCount(String seatCount) {
		this.seatCount = seatCount;
	}
	public String getDrivingLicense() {
		return drivingLicense;
	}
	public void setDrivingLicense(String drivingLicense) {
		this.drivingLicense = drivingLicense;
	}
//	public String getLossId() {
//		return lossId;
//	}
//	public void setLossId(String lossId) {
//		this.lossId = lossId;
//	}
	public String getTargetType() {
		return targetType;
	}
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}
	public String getProducerName() {
		return producerName;
	}
	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getSerialName() {
		return serialName;
	}
	public void setSerialName(String serialName) {
		this.serialName = serialName;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getProductYear() {
		return productYear;
	}
	public void setProductYear(String productYear) {
		this.productYear = productYear;
	}
	public String getReportNo() {
		return reportNo;
	}
	public void setReportNo(String reportNo) {
		this.reportNo = reportNo;
	}
	
}
