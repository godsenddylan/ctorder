package net.chetong.order.model;

import java.io.Serializable;

/**
 * 报案信息
 * 
 * @author wufeng
 *
 */
public class FmOrderCaseVO implements Serializable {

	private static final long serialVersionUID = 4629422026835732168L;
	private String id; /*-- id --*/
	private String taskId; 
	private String taskDetailId; 
	private String caseNo; /*--  --*/
	private String caseTime; /*-- 报案时间 --*/
	private String carNo; /*-- 标的车牌号 --*/
	private String isAlert; /*-- 是否报警 0 - 否 1 - 是 --*/
	private String accidentTime; /*-- 出险时间 --*/
	private String accidentAddress; /*-- 出险具体地点 --*/
	private String delegateInfo; /*-- 委托描述 ，每个 主 务 该字段累加记录，每次记录内容（录入人、 时间、 要求） --*/
	private String subjectId;
	private String isThird;
	private String status;
	private String entrustId; //合约委托人id
	private String entrustName;  //合约委托人名称
	
	private String isEntrust;
	private String companyName;
	
	private String entrustLinkMan;   //对接人
	private String entrustLinkTel;   //对接人电话
	private String accidentLinkMan;
	private String accidentLinkTel;
	private String isAllow;
	private String allowMoney;
	private String createTime;
	private String creator;
	
	private String isAllowMediation;   /*是否授权一次性调解，0：否，1：是*/
	private String allowMediationMoney;  /*授权一次性调解金额*/
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskDetailId() {
		return taskDetailId;
	}

	public void setTaskDetailId(String taskDetailId) {
		this.taskDetailId = taskDetailId;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getCaseTime() {
		return caseTime;
	}

	public void setCaseTime(String caseTime) {
		this.caseTime = caseTime;
	}

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	public String getIsAlert() {
		return isAlert;
	}

	public void setIsAlert(String isAlert) {
		this.isAlert = isAlert;
	}

	public String getAccidentTime() {
		return accidentTime;
	}

	public void setAccidentTime(String accidentTime) {
		this.accidentTime = accidentTime;
	}

	public String getAccidentAddress() {
		return accidentAddress;
	}

	public void setAccidentAddress(String accidentAddress) {
		this.accidentAddress = accidentAddress;
	}

	public String getDelegateInfo() {
		return delegateInfo;
	}

	public void setDelegateInfo(String delegateInfo) {
		this.delegateInfo = delegateInfo;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getIsThird() {
		return isThird;
	}

	public void setIsThird(String isThird) {
		this.isThird = isThird;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEntrustId() {
		return entrustId;
	}

	public void setEntrustId(String entrustId) {
		this.entrustId = entrustId;
	}

	public String getEntrustName() {
		return entrustName;
	}

	public void setEntrustName(String entrustName) {
		this.entrustName = entrustName;
	}

	public String getEntrustLinkMan() {
		return entrustLinkMan;
	}

	public void setEntrustLinkMan(String entrustLinkMan) {
		this.entrustLinkMan = entrustLinkMan;
	}

	public String getEntrustLinkTel() {
		return entrustLinkTel;
	}

	public void setEntrustLinkTel(String entrustLinkTel) {
		this.entrustLinkTel = entrustLinkTel;
	}

	public String getAccidentLinkMan() {
		return accidentLinkMan;
	}

	public void setAccidentLinkMan(String accidentLinkMan) {
		this.accidentLinkMan = accidentLinkMan;
	}

	public String getAccidentLinkTel() {
		return accidentLinkTel;
	}

	public void setAccidentLinkTel(String accidentLinkTel) {
		this.accidentLinkTel = accidentLinkTel;
	}

	public String getIsAllow() {
		return isAllow;
	}

	public void setIsAllow(String isAllow) {
		this.isAllow = isAllow;
	}

	public String getAllowMoney() {
		return allowMoney;
	}

	public void setAllowMoney(String allowMoney) {
		this.allowMoney = allowMoney;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		if(null!=createTime&& !createTime.isEmpty()&&21==createTime.length()){
    		createTime = createTime.substring(0,19);
		}
		this.createTime = createTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getIsAllowMediation() {
		return isAllowMediation;
	}

	public void setIsAllowMediation(String isAllowMediation) {
		this.isAllowMediation = isAllowMediation;
	}

	public String getAllowMediationMoney() {
		return allowMediationMoney;
	}

	public void setAllowMediationMoney(String allowMediationMoney) {
		this.allowMediationMoney = allowMediationMoney;
	}

	public String getIsEntrust() {
		return isEntrust;
	}

	public void setIsEntrust(String isEntrust) {
		this.isEntrust = isEntrust;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	
	
}
