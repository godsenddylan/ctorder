package net.chetong.order.model;

import java.io.Serializable;

/***
 * 费用项目信息
 * @author wufeng@chetong.net
 *
 */
public class FhFeeItemVO implements Serializable {

	private static final long serialVersionUID = 8311908830128078633L;
	
	private String createdBy = null;			/** 创建人 */
	private String createdDate = null;			/** 创建日期 */
	private String updatedBy = null;			/** 修改人 */
	private String updatedDate = null;			/** 修改日期 */
	private String id = null;					/** 表ID */
	private String lossId = null;				/** 定损ID */
	private String insureCode = null;			/** 险种名称 */
	private String feeType = null;				/** 费用类型 */	
	private String lossAmount = null;			/** 费用金额 */
	private String auditAmount = null;			/** 核损金额 */
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
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	public String getLossAmount() {
		return lossAmount;
	}
	public void setLossAmount(String lossAmount) {
		this.lossAmount = lossAmount;
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
