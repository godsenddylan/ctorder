package net.chetong.order.model; 

import java.math.BigDecimal;
import java.util.Date;

public class FhRepairModelVO implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*--  --*/
	private String repairName;	/*-- 维修名称 --*/
	private BigDecimal repairAmount;	/*-- 维修金额 --*/
	private String insertTime;	/*-- 插入时间 --*/
	private String guid;	/*-- guid --*/
	private Long lossId;	/*-- 任务id --*/
	private String repairType;	/*-- 维修类型 --*/
	private String repairCode;	/*-- 维修编码 --*/
	private String repairWhour;	/*-- 维修工时 --*/
	private BigDecimal referAmount;	/*-- 维修金额 --*/
	private String remark;	/*-- 备注 --*/
	private Long repairId;	/*-- 维修id --*/
	private Integer isManual;	/*-- 是否自定义 --*/
	private BigDecimal auditPrice;	/*-- 核损价格 --*/
	private String remark2;	/*-- 备注 --*/

	/**
	 * 构造函数.
	 */
	public FhRepairModelVO() {}
	
	
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
	 * getRepairName.
	 */
	public String getRepairName(){
		return repairName;
	}
	
	/**
   * setRepairName.
   */
  
	public void setRepairName(String repairName){
		this.repairName = repairName;
	}

		
	/**
	 * getRepairAmount.
	 */
	public BigDecimal getRepairAmount(){
		return repairAmount;
	}
	
	/**
   * setRepairAmount.
   */
  
	public void setRepairAmount(BigDecimal repairAmount){
		this.repairAmount = repairAmount;
	}

		
	/**
	 * getInsertTime.
	 */
	public String getInsertTime(){
		return insertTime;
	}
	
	/**
   * setInsertTime.
   */
  
	public void setInsertTime(String insertTime){
		this.insertTime = insertTime;
	}

		
	/**
	 * getGuid.
	 */
	public String getGuid(){
		return guid;
	}
	
	/**
   * setGuid.
   */
  
	public void setGuid(String guid){
		this.guid = guid;
	}

		
	/**
	 * getLossId.
	 */
	public Long getLossId(){
		return lossId;
	}
	
	/**
   * setLossId.
   */
  
	public void setLossId(Long lossId){
		this.lossId = lossId;
	}

		
	/**
	 * getRepairType.
	 */
	public String getRepairType(){
		return repairType;
	}
	
	/**
   * setRepairType.
   */
  
	public void setRepairType(String repairType){
		this.repairType = repairType;
	}

		
	/**
	 * getRepairCode.
	 */
	public String getRepairCode(){
		return repairCode;
	}
	
	/**
   * setRepairCode.
   */
  
	public void setRepairCode(String repairCode){
		this.repairCode = repairCode;
	}

		
	/**
	 * getRepairWhour.
	 */
	public String getRepairWhour(){
		return repairWhour;
	}
	
	/**
   * setRepairWhour.
   */
  
	public void setRepairWhour(String repairWhour){
		this.repairWhour = repairWhour;
	}

		
	/**
	 * getReferAmount.
	 */
	public BigDecimal getReferAmount(){
		return referAmount;
	}
	
	/**
   * setReferAmount.
   */
  
	public void setReferAmount(BigDecimal referAmount){
		this.referAmount = referAmount;
	}

		
	/**
	 * getRemark.
	 */
	public String getRemark(){
		return remark;
	}
	
	/**
   * setRemark.
   */
  
	public void setRemark(String remark){
		this.remark = remark;
	}

		
	/**
	 * getRepairId.
	 */
	public Long getRepairId(){
		return repairId;
	}
	
	/**
   * setRepairId.
   */
  
	public void setRepairId(Long repairId){
		this.repairId = repairId;
	}

		
	/**
	 * getIsManual.
	 */
	public Integer getIsManual(){
		return isManual;
	}
	
	/**
   * setIsManual.
   */
  
	public void setIsManual(Integer isManual){
		this.isManual = isManual;
	}

		
	/**
	 * getAuditPrice.
	 */
	public BigDecimal getAuditPrice(){
		return auditPrice;
	}
	
	/**
   * setAuditPrice.
   */
  
	public void setAuditPrice(BigDecimal auditPrice){
		this.auditPrice = auditPrice;
	}


	public String getRemark2() {
		return remark2;
	}


	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

}
