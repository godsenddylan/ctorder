package net.chetong.order.model;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

public class FhAuditModelVO implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -812362556157159738L;
	/**
	 * 属性定义.
	 */

	private Long id; /*--  --*/
	private String orderCode; /*-- 订单号 --*/
	private String auditTime; /*-- 审核时间 --*/
	private String auditOpinion; /*-- 审核意见 --*/
	private String auditResult; /*-- 审核结果 --*/
	private Integer taskId; /*-- 任务id --*/
	private String auditType; /*-- 审核类型 --*/
	private String extraReward; /*-- 额外奖励 --*/
	private String evaluateOpinion; /*-- 评价意见 --*/
	private String serviceEvaluation; /*-- 服务评价 --*/
	private String reply; /*-- 回复 --*/
	private String auditResultLable; /*-- 审核状态描述 --*/
	private Long creatorId;         /*-- 创建人id--*/
	private String creatorName;    /*-- 创建人名称--*/
	private String creatorRole;    /*--操作人角色--*/
	private String auditNoReason; /*-- 审核不通过或3分以下的原因 --*/
	private String extraType;/*--特殊费用类型：1-奖励 2-扣款--*/
	private String extraReason;/*--扣款原因类型--*/
	private BigDecimal ctDeductMoney;	/*-- 车童扣款金额 --*/
	private BigDecimal teamDeductMoney;	/*-- 团队扣款金额 --*/
	private String extraExplain;/*--特殊费用说明--*/
	
	private BigDecimal assessmentFee;	/*-- 货运险公估费 暂存字段 --*/

	/**
	 * 构造函数.
	 */
	public FhAuditModelVO() {
	}

	/**
	 * Getter/Setter方法.
	 */


	/**
	 * getId.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * setId.
	 */

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * getOrderCode.
	 */
	public String getOrderCode() {
		return orderCode;
	}

	/**
	 * setOrderCode.
	 */

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	/**
	 * getAuditTime.
	 */
	public String getAuditTime() {		
		return auditTime;
	}

	/**
	 * setAuditTime.
	 */

	public void setAuditTime(String auditTime) {
		this.auditTime = auditTime;
	}

	/**
	 * getAuditOpinion.
	 */
	public String getAuditOpinion() {		
		return auditOpinion;
	}

	/**
	 * setAuditOpinion.
	 */

	public void setAuditOpinion(String auditOpinion) {
		this.auditOpinion = auditOpinion;
	}

	/**
	 * getAuditResult.
	 */
	public String getAuditResult() {
		return auditResult;
	}

	/**
	 * setAuditResult.
	 */

	public void setAuditResult(String auditResult) {
		this.auditResult = auditResult;
	}

	/**
	 * getTaskId.
	 */
	public Integer getTaskId() {
		return taskId;
	}

	/**
	 * setTaskId.
	 */

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	/**
	 * getAuditType.
	 */
	public String getAuditType() {
		return auditType;
	}

	/**
	 * setAuditType.
	 */

	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}

	/**
	 * getExtraReward.
	 */
	public String getExtraReward() {
		return extraReward;
	}

	/**
	 * setExtraReward.
	 */

	public void setExtraReward(String extraReward) {
		this.extraReward = extraReward;
	}

	/**
	 * getEvaluateOpinion.
	 */
	public String getEvaluateOpinion() {
		return evaluateOpinion;
	}

	/**
	 * setEvaluateOpinion.
	 */

	public void setEvaluateOpinion(String evaluateOpinion) {
		this.evaluateOpinion = evaluateOpinion;
	}

	/**
	 * getServiceEvaluation.
	 */
	public String getServiceEvaluation() {
		return serviceEvaluation;
	}

	/**
	 * setServiceEvaluation.
	 */

	public void setServiceEvaluation(String serviceEvaluation) {
		this.serviceEvaluation = serviceEvaluation;
	}

	/**
	 * getReply.
	 */
	public String getReply() {
		return reply;
	}

	/**
	 * setReply.
	 */

	public void setReply(String reply) {
		this.reply = reply;
	}

		public Long getCreatorId() {
		return creatorId;
	}


	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}


	public String getCreatorName() {		
		return creatorName;
	}


	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	
	public String getAuditNoReason() {
		return auditNoReason;
	}

	public void setAuditNoReason(String auditNoReason) {
		this.auditNoReason = auditNoReason;
	}

	public String getAuditResultLable() {
		if (StringUtils.isNotBlank(auditResult)) {
			if ("0".equals(auditResult)) {
				this.auditResultLable = "审核退回";
			} else if ("1".equals(auditResult)) {
				this.auditResultLable = "审核通过";
			}else if("2".equals(auditResult)){
				this.auditResultLable = "申请审核";
			}else{
				this.auditResultLable = "";
			}
		}
		return auditResultLable;
	}


	public String getCreatorRole() {
		if(StringUtils.isNotBlank(auditResult)){
			if("0".equals(auditResult)){
				this.creatorRole = "审核人";
			}else if("1".equals(auditResult)){
				this.creatorRole = "审核人";
			}else if("2".equals(auditResult)){
				this.creatorRole = "车童";
			}else{
				this.creatorRole = "";
			}
		}
		return creatorRole;
	}


	public void setCreatorRole(String creatorRole) {
		this.creatorRole = creatorRole;
	}

	public String getExtraType() {
		return extraType;
	}

	public void setExtraType(String extraType) {
		this.extraType = extraType;
	}

	public String getExtraReason() {
		return extraReason;
	}

	public void setExtraReason(String extraReason) {
		this.extraReason = extraReason;
	}

	public BigDecimal getCtDeductMoney() {
		return ctDeductMoney;
	}

	public void setCtDeductMoney(BigDecimal ctDeductMoney) {
		this.ctDeductMoney = ctDeductMoney;
	}

	public BigDecimal getTeamDeductMoney() {
		return teamDeductMoney;
	}

	public void setTeamDeductMoney(BigDecimal teamDeductMoney) {
		this.teamDeductMoney = teamDeductMoney;
	}

	public String getExtraExplain() {
		return extraExplain;
	}

	public void setExtraExplain(String extraExplain) {
		this.extraExplain = extraExplain;
	}

	public void setAuditResultLable(String auditResultLable) {
		this.auditResultLable = auditResultLable;
	}
	
	public BigDecimal getAssessmentFee() {
		return assessmentFee;
	}

	public void setAssessmentFee(BigDecimal assessmentFee) {
		this.assessmentFee = assessmentFee;
	}

}
