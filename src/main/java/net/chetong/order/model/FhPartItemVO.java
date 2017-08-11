package net.chetong.order.model;

import java.io.Serializable;

/***
 * 定损配件项目信息
 * @author wufeng@chetong.net
 *
 */
public class FhPartItemVO implements Serializable {

	private static final long serialVersionUID = 711114074467592919L;
	
	private String createdBy = null;			/** 创建人 */
	private String createdDate = null;			/** 创建日期 */
	private String updatedBy = null;			/** 修改人 */
	private String updatedDate = null;			/** 修改日期 */	
	private String id = null;				/** 表ID */
	private String lossId = null;			/** 定损ID */
	private String insureCode = null;		/** 险种名称 */
	private String partNo = null; 			/** 配件编号 */
	private String partName = null;			/** 配件名称 */
	private String partAmount = null;		/** 专修价 */
	private String marketPrice = null;		/** 市场价 */
	private String lossPrice = null;		/** 定损单价 */
	private String lossCount = null;		/** 定损数量 */
	private String partSalvage = null;		/** 定损配置残值 */
	private String totalAmount = null;		/** 定损总额 */
	
	private String auditPrice = null;		/** 核损价格 */
	private String auditCount = null;		/** 核损数量 */
	private String auditSalvage = null;		/** 核损残值 */
	private String auditAmount = null;		/** 核损总额 */
	private String callbackFlag = null; 	/** 是否回收 */
	private String firstAmount = null;			/** 第一次定损总额 **/
	private String selfDefine = null;		/**自定义配件标志*/
	private String factoryPartNo = null;   /**原厂配件编码*/
	
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
	public String getInsureCode() {
		return insureCode;
	}
	public void setInsureCode(String insureCode) {
		this.insureCode = insureCode;
	}
	public String getPartNo() {
		return partNo;
	}
	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}
	public String getPartName() {
		return partName;
	}
	public void setPartName(String partName) {
		this.partName = partName;
	}
	public String getPartAmount() {
		return partAmount;
	}
	public void setPartAmount(String partAmount) {
		this.partAmount = partAmount;
	}
	public String getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(String marketPrice) {
		this.marketPrice = marketPrice;
	}
	public String getLossPrice() {
		return lossPrice;
	}
	public void setLossPrice(String lossPrice) {
		this.lossPrice = lossPrice;
	}
	public String getLossCount() {
		return lossCount;
	}
	public void setLossCount(String lossCount) {
		this.lossCount = lossCount;
	}
	public String getPartSalvage() {
		return partSalvage;
	}
	public void setPartSalvage(String partSalvage) {
		this.partSalvage = partSalvage;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getAuditPrice() {
		return auditPrice;
	}
	public void setAuditPrice(String auditPrice) {
		this.auditPrice = auditPrice;
	}
	public String getAuditCount() {
		return auditCount;
	}
	public void setAuditCount(String auditCount) {
		this.auditCount = auditCount;
	}
	public String getAuditSalvage() {
		return auditSalvage;
	}
	public void setAuditSalvage(String auditSalvage) {
		this.auditSalvage = auditSalvage;
	}
	public String getAuditAmount() {
		return auditAmount;
	}
	public void setAuditAmount(String auditAmount) {
		this.auditAmount = auditAmount;
	}
	public String getCallbackFlag() {
		return callbackFlag;
	}
	public void setCallbackFlag(String callbackFlag) {
		this.callbackFlag = callbackFlag;
	}
	public String getLossId() {
		return lossId;
	}
	public void setLossId(String lossId) {
		this.lossId = lossId;
	}
	public String getFirstAmount() {
		return firstAmount;
	}
	public void setFirstAmount(String firstAmount) {
		this.firstAmount = firstAmount;
	}
	public String getSelfDefine() {
		return selfDefine;
	}
	public void setSelfDefine(String selfDefine) {
		this.selfDefine = selfDefine;
	}
	public String getFactoryPartNo() {
		return factoryPartNo;
	}
	public void setFactoryPartNo(String factoryPartNo) {
		this.factoryPartNo = factoryPartNo;
	}
	
}
