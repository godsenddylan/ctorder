package net.chetong.order.model;

import java.io.Serializable;

/***
 * 定损信息
 * @author wufeng@chetong.net
 *
 */
public class FhLossInfoVO implements Serializable {

	private static final long serialVersionUID = 2819962844296522052L;
	
	private String createdBy = null;			/** 创建人 */
	private String createdDate = null;			/** 创建日期 */
	private String updatedBy = null;			/** 修改人 */
	private String updatedDate = null;			/** 修改日期 */
	private String id = null;					/** 表ID */
	private String orderNo = null;				/** 订单号 */
	private String reportNo = null;				/** 报案号 **/
	private String carId = null;				/** 车ID */
	private String lossType = null;				/** 定损类型  1=定损 2=物损 */
	private String lossTarget = null;			/** 损失目标  1=标的 2=三者 */
	private String isAllLoss = null;			/** 是否全损 */
	private String lossPlace = null;			/** 损失部位 */
	private String insureDate = null;			/** 承保日期 */
	private String otherForceAmount = null;		/** 其他交强险赔款金额 */
	private String lossDesp = null;				/** 定损说明 描述*/
	private String lossMarkupRate = null;		/** 配件定损加价率 */
	private String lossMarkupAmount = null;		/** 配件定损加价金额 */
	private String auditMarkupRate = null;		/** 配件核价加价率 */
	private String auditMarkupAmount = null;	/** 配件核价加价金额 */
	private String partLossAmount = null;		/** 配件定损总额 */
	private String partAuditAmount = null;		/** 配件核损总额 */
	private String repairLossAmount = null;		/** 维修工时费定损总额 */
	private String repairAuditAmount = null;	/** 维修工时费核损总额 */
	private String feeLossAmount = null;		/** 费用项目定损总额 */
	private String feeAuditAmount = null;		/** 费用项目核损总额 */
	private String damageLossAmount = null;		/** 物损项目定损总额 */
	private String damageAuditAmount = null;	/** 物损项目核损总额 */
	private String lossTotalAmount = null;		/** 定损总额 */
	
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
	public String getIsAllLoss() {
		return isAllLoss;
	}
	public void setIsAllLoss(String isAllLoss) {
		this.isAllLoss = isAllLoss;
	}
	public String getLossPlace() {
		return lossPlace;
	}
	public void setLossPlace(String lossPlace) {
		this.lossPlace = lossPlace;
	}
	public String getInsureDate() {
		return insureDate;
	}
	public void setInsureDate(String insureDate) {
		this.insureDate = insureDate;
	}
	public String getOtherForceAmount() {
		return otherForceAmount;
	}
	public void setOtherForceAmount(String otherForceAmount) {
		this.otherForceAmount = otherForceAmount;
	}
	public String getLossDesp() {
		return lossDesp;
	}
	public void setLossDesp(String lossDesp) {
		this.lossDesp = lossDesp;
	}
	public String getLossMarkupRate() {
		return lossMarkupRate;
	}
	public void setLossMarkupRate(String lossMarkupRate) {
		this.lossMarkupRate = lossMarkupRate;
	}
	public String getLossMarkupAmount() {
		return lossMarkupAmount;
	}
	public void setLossMarkupAmount(String lossMarkupAmount) {
		this.lossMarkupAmount = lossMarkupAmount;
	}
	public String getAuditMarkupRate() {
		return auditMarkupRate;
	}
	public void setAuditMarkupRate(String auditMarkupRate) {
		this.auditMarkupRate = auditMarkupRate;
	}
	public String getAuditMarkupAmount() {
		return auditMarkupAmount;
	}
	public void setAuditMarkupAmount(String auditMarkupAmount) {
		this.auditMarkupAmount = auditMarkupAmount;
	}
	public String getPartLossAmount() {
		return partLossAmount;
	}
	public void setPartLossAmount(String partLossAmount) {
		this.partLossAmount = partLossAmount;
	}
	public String getPartAuditAmount() {
		return partAuditAmount;
	}
	public void setPartAuditAmount(String partAuditAmount) {
		this.partAuditAmount = partAuditAmount;
	}
	public String getRepairLossAmount() {
		return repairLossAmount;
	}
	public void setRepairLossAmount(String repairLossAmount) {
		this.repairLossAmount = repairLossAmount;
	}
	public String getRepairAuditAmount() {
		return repairAuditAmount;
	}
	public void setRepairAuditAmount(String repairAuditAmount) {
		this.repairAuditAmount = repairAuditAmount;
	}
	public String getFeeLossAmount() {
		return feeLossAmount;
	}
	public void setFeeLossAmount(String feeLossAmount) {
		this.feeLossAmount = feeLossAmount;
	}
	public String getFeeAuditAmount() {
		return feeAuditAmount;
	}
	public void setFeeAuditAmount(String feeAuditAmount) {
		this.feeAuditAmount = feeAuditAmount;
	}
	public String getLossTotalAmount() {
		return lossTotalAmount;
	}
	public void setLossTotalAmount(String lossTotalAmount) {
		this.lossTotalAmount = lossTotalAmount;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getLossType() {
		return lossType;
	}
	public void setLossType(String lossType) {
		this.lossType = lossType;
	}
	public String getLossTarget() {
		return lossTarget;
	}
	public void setLossTarget(String lossTarget) {
		this.lossTarget = lossTarget;
	}
	public String getDamageLossAmount() {
		return damageLossAmount;
	}
	public void setDamageLossAmount(String damageLossAmount) {
		this.damageLossAmount = damageLossAmount;
	}
	public String getDamageAuditAmount() {
		return damageAuditAmount;
	}
	public void setDamageAuditAmount(String damageAuditAmount) {
		this.damageAuditAmount = damageAuditAmount;
	}
	public String getCarId() {
		return carId;
	}
	public void setCarId(String carId) {
		this.carId = carId;
	}
	public String getReportNo() {
		return reportNo;
	}
	public void setReportNo(String reportNo) {
		this.reportNo = reportNo;
	}
	
}
