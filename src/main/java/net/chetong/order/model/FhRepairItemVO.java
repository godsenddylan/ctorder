package net.chetong.order.model;

import java.io.Serializable;

/***
 * 定损维修项目信息
 * @author wufeng@chetong.net
 *
 */
public class FhRepairItemVO implements Serializable {

	private static final long serialVersionUID = -1429610766854027351L;
	private String createdBy = null;			/** 创建人 */
	private String createdDate = null;			/** 创建日期 */
	private String updatedBy = null;			/** 修改人 */
	private String updatedDate = null;			/** 修改日期 */	
	private String id = null;				/** 表ID */
	private String lossId = null;			/** 定损ID */
	private String insureCode = null;		/** 险种名称 */
	private String repairName = null;		/** 修理项目 */
	private String repairAmount = null;		/** 工时费报价 */
	private String auditPrice = null;		/** 工时费核损 */
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
	public String getInsureCode() {
		return insureCode;
	}
	public void setInsureCode(String insureCode) {
		this.insureCode = insureCode;
	}
	public String getRepairName() {
		return repairName;
	}
	public void setRepairName(String repairName) {
		this.repairName = repairName;
	}
	public String getRepairAmount() {
		return repairAmount;
	}
	public void setRepairAmount(String repairAmount) {
		this.repairAmount = repairAmount;
	}
	public String getAuditPrice() {
		return auditPrice;
	}
	public void setAuditPrice(String auditPrice) {
		this.auditPrice = auditPrice;
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
	
}
