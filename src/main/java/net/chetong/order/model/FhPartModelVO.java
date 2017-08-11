package net.chetong.order.model; 

import java.math.BigDecimal;
import java.util.Date;

public class FhPartModelVO implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*--  --*/
	private String partName;	/*-- 配件名称 --*/
	private String partCode;	/*-- 配件编码 --*/
	private String insertTime;	/*-- 插入时间 --*/
	private String guid;	/*-- guid --*/
	private Long lossId;	/*-- 任务id --*/
	private BigDecimal marketPrice;	/*-- 市场价 --*/
	private BigDecimal sPrice;	/*-- 4s价格 --*/
	private BigDecimal complexPrice;	/*-- 综合价格 --*/
	private String remark;	/*-- 备注 --*/
	private Long partId;	/*-- 配件id --*/
	private Integer partNum;	/*-- 配件数量 --*/
	private Integer isManual;	/*-- 是否 --*/
	private BigDecimal partPrice;	/*-- 配件价格 --*/
	private BigDecimal auditPrice;	/*-- 核损价格 --*/
	private String remark2;	/*-- 核损备注 --*/
	private String auditunitPrice;	/*-- 核价单价 --*/

	/**
	 * 构造函数.
	 */
	public FhPartModelVO() {}
	
	
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
	 * getPartName.
	 */
	public String getPartName(){
		return partName;
	}
	
	/**
   * setPartName.
   */
  
	public void setPartName(String partName){
		this.partName = partName;
	}

		
	/**
	 * getPartCode.
	 */
	public String getPartCode(){
		return partCode;
	}
	
	/**
   * setPartCode.
   */
  
	public void setPartCode(String partCode){
		this.partCode = partCode;
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
	 * getMarketPrice.
	 */
	public BigDecimal getMarketPrice(){
		return marketPrice;
	}
	
	/**
   * setMarketPrice.
   */
  
	public void setMarketPrice(BigDecimal marketPrice){
		this.marketPrice = marketPrice;
	}

		
	/**
	 * getSPrice.
	 */
	public BigDecimal getSPrice(){
		return sPrice;
	}
	
	/**
   * setSPrice.
   */
  
	public void setSPrice(BigDecimal sPrice){
		this.sPrice = sPrice;
	}

		
	/**
	 * getComplexPrice.
	 */
	public BigDecimal getComplexPrice(){
		return complexPrice;
	}
	
	/**
   * setComplexPrice.
   */
  
	public void setComplexPrice(BigDecimal complexPrice){
		this.complexPrice = complexPrice;
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
	 * getPartId.
	 */
	public Long getPartId(){
		return partId;
	}
	
	/**
   * setPartId.
   */
  
	public void setPartId(Long partId){
		this.partId = partId;
	}

		
	/**
	 * getPartNum.
	 */
	public Integer getPartNum(){
		return partNum;
	}
	
	/**
   * setPartNum.
   */
  
	public void setPartNum(Integer partNum){
		this.partNum = partNum;
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
	 * getPartPrice.
	 */
	public BigDecimal getPartPrice(){
		return partPrice;
	}
	
	/**
   * setPartPrice.
   */
  
	public void setPartPrice(BigDecimal partPrice){
		this.partPrice = partPrice;
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

	public String getAuditunitPrice() {
		return auditunitPrice;
	}

	public void setAuditunitPrice(String auditunitPrice) {
		this.auditunitPrice = auditunitPrice;
	}

}
