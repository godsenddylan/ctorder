package net.chetong.order.model;

import java.io.Serializable;

public class FhInsuredBankInfoVO implements Serializable {

	private static final long serialVersionUID = -4376803808670341705L;
	
	private String createdBy = null; 		/** 创建人 */
	private String createdDate = null;		/** 创建时间 */
	private String updatedBy = null;		/** 更新人 */
	private String updatedDate = null;		/** 更新时间 */
	private String id = null;				/** 表ID */
	private String surveyId = null;			/** 查勘ID */
	private String accountName = null;		/** 持卡人 */
	private String bank = null;				/** 开户行 */
	private String insuredAccount = null;	/** 卡号 */
	private String remark = null;			/** 备注 */
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
	public String getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getInsuredAccount() {
		return insuredAccount;
	}
	public void setInsuredAccount(String insuredAccount) {
		this.insuredAccount = insuredAccount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
