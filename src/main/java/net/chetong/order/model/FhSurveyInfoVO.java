package net.chetong.order.model;

import java.io.Serializable;

/**
 * 查勘信息
 * @author wufeng@chetong.net
 *
 */
public class FhSurveyInfoVO implements Serializable {
	
	private static final long serialVersionUID = -5897894576672834726L;
	private String createdBy = null;			/** 创建人 */
	private String createdDate = null;			/** 创建日期 */
	private String updatedBy = null;			/** 修改人 */
	private String updatedDate = null;			/** 修改日期 */	
	private String id = null;					/** 表ID */
	private String accidentCauseBig = null;		/** 出险原因大类 */
	private String accidentCauseSmall = null;	/** 出险原因小类 */
	private String accidentSubcause = null;		/** 事故分类 */
	private String accidentArea = null;			/** 出险区域 */
	private String disposeDept = null;			/** 事故处理类型*/
	private String roadType = null; 			/** 道路信息（永） */
	private String accidentDuty = null; 		/** 事故责任（永改） */
	private String dutyPercent = null; 			/** 事故责任比例（永） */
	private String isInjured = null; 			/** 是否涉及人伤  是否包含人伤*/
	private String isLoss = null; 				/** 是否物损   是否包含财损*/
	private String accidentAddress = null;		/** 出险详细地址 */
	private String accidentProvince = null; 	/** 出险区域-省（永） */
	private String accidentCity = null; 		/** 出险区域-市（永） */
	private String accidentCounty = null; 		/** 出险区域-区、县、镇、乡（永） */
	private String accidentStreet = null; 		/** 出险区域-街（永） */
//	private String postcode = null;				/** 邮编 */
//	private String smsMan = null;				/** 短信接收人姓名 */
//	private String smsMobile = null;			/** 短信接收人手机号码 */
	
	private String claimAction = null; 			/** 理赔类型（永） */
	private String isCali = null; 				/** 是否互碰自赔 */
	private String accidentCourse = null;		/** 出险经过 */
	private String userCode = null; 			/** 查勘员编码 */
	private String surveyTime = null; 			/** 查勘时间 */
	private String surveyPlace = null; 			/** 查勘地点 */
	private String surveyConclusion = null;		/** 查勘结论 **/
	private String placeType = null;			/** 查勘地点类型  */
	
	private String beijingFlag = null;			/** 北京互碰垫付标志**/
	private String devolveFlag = null;			/** 委托索赔标志**/
	private String bigFlag = null;				/** 大案标志**/
	private String payInfoFlag = null;			/** 支付标志**/
	
	private String reportNo = null;				/** 报案号 */
	private String orderNo = null;				/** 订单号 */
	private String estimateLossAmount = null;	/** 查勘估算金额 */
	private String insurmedtel = null ;/** 被保险人联系方式*/
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
	public String getAccidentCauseBig() {
		return accidentCauseBig;
	}
	public void setAccidentCauseBig(String accidentCauseBig) {
		this.accidentCauseBig = accidentCauseBig;
	}
	public String getAccidentCauseSmall() {
		return accidentCauseSmall;
	}
	public void setAccidentCauseSmall(String accidentCauseSmall) {
		this.accidentCauseSmall = accidentCauseSmall;
	}
	public String getAccidentSubcause() {
		return accidentSubcause;
	}
	public void setAccidentSubcause(String accidentSubcause) {
		this.accidentSubcause = accidentSubcause;
	}
	public String getAccidentArea() {
		return accidentArea;
	}
	public void setAccidentArea(String accidentArea) {
		this.accidentArea = accidentArea;
	}
	public String getDisposeDept() {
		return disposeDept;
	}
	public void setDisposeDept(String disposeDept) {
		this.disposeDept = disposeDept;
	}
	public String getRoadType() {
		return roadType;
	}
	public void setRoadType(String roadType) {
		this.roadType = roadType;
	}
	public String getAccidentDuty() {
		return accidentDuty;
	}
	public void setAccidentDuty(String accidentDuty) {
		this.accidentDuty = accidentDuty;
	}
	public String getDutyPercent() {
		return dutyPercent;
	}
	public void setDutyPercent(String dutyPercent) {
		this.dutyPercent = dutyPercent;
	}
	public String getIsInjured() {
		return isInjured;
	}
	public void setIsInjured(String isInjured) {
		this.isInjured = isInjured;
	}
	public String getIsLoss() {
		return isLoss;
	}
	public void setIsLoss(String isLoss) {
		this.isLoss = isLoss;
	}
	public String getAccidentAddress() {
		return accidentAddress;
	}
	public void setAccidentAddress(String accidentAddress) {
		this.accidentAddress = accidentAddress;
	}
	public String getAccidentProvince() {
		return accidentProvince;
	}
	public void setAccidentProvince(String accidentProvince) {
		this.accidentProvince = accidentProvince;
	}
	public String getAccidentCity() {
		return accidentCity;
	}
	public void setAccidentCity(String accidentCity) {
		this.accidentCity = accidentCity;
	}
	public String getAccidentCounty() {
		return accidentCounty;
	}
	public void setAccidentCounty(String accidentCounty) {
		this.accidentCounty = accidentCounty;
	}
	public String getAccidentStreet() {
		return accidentStreet;
	}
	public void setAccidentStreet(String accidentStreet) {
		this.accidentStreet = accidentStreet;
	}
	public String getClaimAction() {
		return claimAction;
	}
	public void setClaimAction(String claimAction) {
		this.claimAction = claimAction;
	}
	public String getIsCali() {
		return isCali;
	}
	public void setIsCali(String isCali) {
		this.isCali = isCali;
	}
	public String getAccidentCourse() {
		return accidentCourse;
	}
	public void setAccidentCourse(String accidentCourse) {
		this.accidentCourse = accidentCourse;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getSurveyTime() {
		return surveyTime;
	}
	public void setSurveyTime(String surveyTime) {
		this.surveyTime = surveyTime;
	}
	public String getSurveyPlace() {
		return surveyPlace;
	}
	public void setSurveyPlace(String surveyPlace) {
		this.surveyPlace = surveyPlace;
	}
	public String getSurveyConclusion() {
		return surveyConclusion;
	}
	public void setSurveyConclusion(String surveyConclusion) {
		this.surveyConclusion = surveyConclusion;
	}
	public String getReportNo() {
		return reportNo;
	}
	public void setReportNo(String reportNo) {
		this.reportNo = reportNo;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getEstimateLossAmount() {
		return estimateLossAmount;
	}
	public void setEstimateLossAmount(String estimateLossAmount) {
		this.estimateLossAmount = estimateLossAmount;
	}
	public String getPlaceType() {
		return placeType;
	}
	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}
	public String getBeijingFlag() {
		return beijingFlag;
	}
	public void setBeijingFlag(String beijingFlag) {
		this.beijingFlag = beijingFlag;
	}
	public String getDevolveFlag() {
		return devolveFlag;
	}
	public void setDevolveFlag(String devolveFlag) {
		this.devolveFlag = devolveFlag;
	}
	public String getBigFlag() {
		return bigFlag;
	}
	public void setBigFlag(String bigFlag) {
		this.bigFlag = bigFlag;
	}
	public String getPayInfoFlag() {
		return payInfoFlag;
	}
	public void setPayInfoFlag(String payInfoFlag) {
		this.payInfoFlag = payInfoFlag;
	}
	public String getInsurmedtel() {
		return insurmedtel;
	}
	public void setInsurmedtel(String insurmedtel) {
		this.insurmedtel = insurmedtel;
	}
	
}
