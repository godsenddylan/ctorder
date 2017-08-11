package net.chetong.order.model;
import java.io.Serializable;
public class FhBankInfoVO implements Serializable {
	
	private static final long serialVersionUID = 8935040633065215753L;
	
	private String createdBy;             /** 创建人  **/
	private String createdDate;             /** 创建日期  **/
	private String updatedBy;             /** 修改人  **/
	private String updatedDate;             /** 修改日期  **/
	private String id;             /** 表ID  **/
	private String reportNo;             /** 报案号  **/
	private String insuredMan;             /** 被保险人  **/
	private String clientName;             /** 客户名称  **/
	private String clientType;             /** 客户类型  **/
	private String account;             /** 账号  **/
	private String paymentObj;             /** 支付对象  **/
	private String purpose;             /** 用途  **/
	private String cardType;             /** 卡折类型  **/
	private String bankTypeCode;             /** 银行类型  **/
	private String bankTypeName;             /** 银行类型名称  **/
	private String bankRegionCode;             /** 银行区域  **/
	private String bankRegionName;             /** 银行区域名称  **/
	private String bankName;             /** 开户行名称  **/
	private String bankCode;             /** 开户行代码  **/
	private String bankLocationCode;             /** 银行网点编码  **/
	private String bankLocationName;             /** 银行网点名称  **/
	private String userPhone;             /** 用户电话号码  **/
	private String remark;             /** 备注  **/
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
	public String getReportNo() {
		return reportNo;
	}
	public void setReportNo(String reportNo) {
		this.reportNo = reportNo;
	}
	public String getInsuredMan() {
		return insuredMan;
	}
	public void setInsuredMan(String insuredMan) {
		this.insuredMan = insuredMan;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPaymentObj() {
		return paymentObj;
	}
	public void setPaymentObj(String paymentObj) {
		this.paymentObj = paymentObj;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getBankTypeCode() {
		return bankTypeCode;
	}
	public void setBankTypeCode(String bankTypeCode) {
		this.bankTypeCode = bankTypeCode;
	}
	public String getBankTypeName() {
		return bankTypeName;
	}
	public void setBankTypeName(String bankTypeName) {
		this.bankTypeName = bankTypeName;
	}
	public String getBankRegionCode() {
		return bankRegionCode;
	}
	public void setBankRegionCode(String bankRegionCode) {
		this.bankRegionCode = bankRegionCode;
	}
	public String getBankRegionName() {
		return bankRegionName;
	}
	public void setBankRegionName(String bankRegionName) {
		this.bankRegionName = bankRegionName;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getBankLocationCode() {
		return bankLocationCode;
	}
	public void setBankLocationCode(String bankLocationCode) {
		this.bankLocationCode = bankLocationCode;
	}
	public String getBankLocationName() {
		return bankLocationName;
	}
	public void setBankLocationName(String bankLocationName) {
		this.bankLocationName = bankLocationName;
	}
	public String getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}