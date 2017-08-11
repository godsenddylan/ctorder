package net.chetong.order.model;

import java.io.Serializable;

/***
 * 定损物损项目信息
 * @author wufeng@chetong.net
 *
 */
public class FhLossItemVO implements Serializable {

	private static final long serialVersionUID = -6498296572881918425L;
	
	private String createdBy = null;			/** 创建人 */
	private String createdDate = null;			/** 创建日期 */
	private String updatedBy = null;			/** 修改人 */
	private String updatedDate = null;			/** 修改日期 */	
	private String id = null;				/** 表ID */
	private String lossId = null;			/** 定损ID */
	private String insureCode = null;		/** 险种名称 */
	private String lossName = null;			/** 物损名称 */
	private String standard = null;			/** 规格型号 */
	private String lossCount = null;		/** 定损数量 */
	private String price = null;			/** 单价 */
	private String lossDegree = null;		/** 损失程度 */
	private String salvage = null;			/** 定损残值 */
	private String totalAmount = null;		/** 损失合计 */
	private String auditPrice = null;		/** 核损单价 */
	private String auditCount = null;		/** 核损数量 */
	private String auditLossDegree = null;	/** 核损损失程度 */
	private String auditSalvage = null;		/** 核损残值 */
	private String auditAmount = null;		/** 核损总额 */
	private String firstAmount = null;			/** 第一次定损总额 **/
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
	public String getLossId() {
		return lossId;
	}
	public void setLossId(String lossId) {
		this.lossId = lossId;
	}
	public String getInsureCode() {
		return insureCode;
	}
	public void setInsureCode(String insureCode) {
		this.insureCode = insureCode;
	}
	public String getLossName() {
		return lossName;
	}
	public void setLossName(String lossName) {
		this.lossName = lossName;
	}
	public String getStandard() {
		return standard;
	}
	public void setStandard(String standard) {
		this.standard = standard;
	}
	public String getLossCount() {
		return lossCount;
	}
	public void setLossCount(String lossCount) {
		this.lossCount = lossCount;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getLossDegree() {
		return lossDegree;
	}
	public void setLossDegree(String lossDegree) {
		this.lossDegree = lossDegree;
	}
	public String getSalvage() {
		return salvage;
	}
	public void setSalvage(String salvage) {
		this.salvage = salvage;
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
	public String getAuditLossDegree() {
		return auditLossDegree;
	}
	public void setAuditLossDegree(String auditLossDegree) {
		this.auditLossDegree = auditLossDegree;
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
	public String getFirstAmount() {
		return firstAmount;
	}
	public void setFirstAmount(String firstAmount) {
		this.firstAmount = firstAmount;
	}
	
}
