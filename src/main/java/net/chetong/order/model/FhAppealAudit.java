package net.chetong.order.model;

import java.util.Date;

import net.chetong.order.util.DateUtil;

public class FhAppealAudit implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*--  --*/
	private Long fhAuditModelId;	/*-- fhAuditModel.id --*/
	private String orderCode;	/*-- 订单编码 --*/
	private Date appealTime;	/*-- 申诉时间 --*/
	private String appealType;	/*-- 申诉类型:auditno-审核退回,auditbad-差评 --*/
	private String appealOpinion;	/*-- 申诉意见 --*/
	private String appealPicsUrl;	/*-- 申诉的图片url,最多三张,用逗号分隔 --*/
	private String appealStat;	/*-- 申诉状态:0-开始申诉,1-申诉成功,-1-申诉失败 --*/
	private Long auditUserId;	/*-- 审核的用户id --*/
	private Long buyerUserId;	/*-- 委托人id --*/
	private Long sellerUserId;	/*-- 车童id --*/
	private Long checkUserId;	/*-- 申诉审核人id,sysOperator表 --*/
	private String checkOpinion;	/*-- 申诉审核意见 --*/
	private Date checkTime;	/*-- 审核时间 --*/
	
	public String getAppealTimeLabel() {
		String appealTimeLabel = null;
		if (appealTime != null) {
			appealTimeLabel = DateUtil.dateToString(appealTime, null);
		}
		return appealTimeLabel;
	}


	public String getCheckTimeLabel() {
		String checkTimeLabel = null;
		if (checkTime != null) {
			checkTimeLabel = DateUtil.dateToString(checkTime, null);
		}
		return checkTimeLabel;
	}

	/**
	 * 构造函数.
	 */
	public FhAppealAudit() {}
	
	
	/**
	 * Getter/Setter方法.
	 */
		
	/**
	 * getId.
	 */
	public Long getId(){
		return id;
	}
	
	/**
   * setId.
   */
  
	public void setId(Long id){
		this.id = id;
	}

		
	/**
	 * getFhAuditModelId.
	 */
	public Long getFhAuditModelId(){
		return fhAuditModelId;
	}
	
	/**
   * setFhAuditModelId.
   */
  
	public void setFhAuditModelId(Long fhAuditModelId){
		this.fhAuditModelId = fhAuditModelId;
	}

		
	/**
	 * getOrderCode.
	 */
	public String getOrderCode(){
		return orderCode;
	}
	
	/**
   * setOrderCode.
   */
  
	public void setOrderCode(String orderCode){
		this.orderCode = orderCode;
	}

		
	/**
	 * getAppealTime.
	 */
	public Date getAppealTime(){
		return appealTime;
	}
	
	/**
   * setAppealTime.
   */
  
	public void setAppealTime(Date appealTime){
		this.appealTime = appealTime;
	}

		
	/**
	 * getAppealType.
	 */
	public String getAppealType(){
		return appealType;
	}
	
	/**
   * setAppealType.
   */
  
	public void setAppealType(String appealType){
		this.appealType = appealType;
	}

		
	/**
	 * getAppealOpinion.
	 */
	public String getAppealOpinion(){
		return appealOpinion;
	}
	
	/**
   * setAppealOpinion.
   */
  
	public void setAppealOpinion(String appealOpinion){
		this.appealOpinion = appealOpinion;
	}

		
	/**
	 * getAppealPicsUrl.
	 */
	public String getAppealPicsUrl(){
		return appealPicsUrl;
	}
	
	/**
   * setAppealPicsUrl.
   */
  
	public void setAppealPicsUrl(String appealPicsUrl){
		this.appealPicsUrl = appealPicsUrl;
	}

		
	/**
	 * getAppealStat.
	 */
	public String getAppealStat(){
		return appealStat;
	}
	
	/**
   * setAppealStat.
   */
  
	public void setAppealStat(String appealStat){
		this.appealStat = appealStat;
	}

		
	/**
	 * getAuditUserId.
	 */
	public Long getAuditUserId(){
		return auditUserId;
	}
	
	/**
   * setAuditUserId.
   */
  
	public void setAuditUserId(Long auditUserId){
		this.auditUserId = auditUserId;
	}

		
	/**
	 * getBuyerUserId.
	 */
	public Long getBuyerUserId(){
		return buyerUserId;
	}
	
	/**
   * setBuyerUserId.
   */
  
	public void setBuyerUserId(Long buyerUserId){
		this.buyerUserId = buyerUserId;
	}

		
	/**
	 * getSellerUserId.
	 */
	public Long getSellerUserId(){
		return sellerUserId;
	}
	
	/**
   * setSellerUserId.
   */
  
	public void setSellerUserId(Long sellerUserId){
		this.sellerUserId = sellerUserId;
	}

		
	/**
	 * getCheckUserId.
	 */
	public Long getCheckUserId(){
		return checkUserId;
	}
	
	/**
   * setCheckUserId.
   */
  
	public void setCheckUserId(Long checkUserId){
		this.checkUserId = checkUserId;
	}

		
	/**
	 * getCheckOpinion.
	 */
	public String getCheckOpinion(){
		return checkOpinion;
	}
	
	/**
   * setCheckOpinion.
   */
  
	public void setCheckOpinion(String checkOpinion){
		this.checkOpinion = checkOpinion;
	}

		
	/**
	 * getCheckTime.
	 */
	public Date getCheckTime(){
		return checkTime;
	}
	
	/**
   * setCheckTime.
   */
  
	public void setCheckTime(Date checkTime){
		this.checkTime = checkTime;
	}

}
