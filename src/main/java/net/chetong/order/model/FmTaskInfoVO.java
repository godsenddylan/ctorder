package net.chetong.order.model;

import java.io.Serializable;

public class FmTaskInfoVO implements Serializable {

	private static final long serialVersionUID = 9174234185276699483L;
	
	private String createdBy = null;			/** 创建人 */
	private String createdDate = null;			/** 创建日期 */
	private String updatedBy = null;			/** 修改人 */
	private String updatedDate = null;			/** 修改日期 */	
	
	private String id = null;					/** 表ID */
	private String companyTaskId = null;		/** 任务ID */
	private String reportNo = null;				/** 报案号 */
	private String taskType = null;				/** 任务类型 */
	private String source = null;				/** 来源：1=永诚 */
	private String state = null;				/** 状态 0:待派单 1:作业中 2:待审核 3:审核退回(作业中) 4:审核通过(完成) */
	private String handlerCode = null;			/** 任务处理人  */
	private String isSend = null;				/** 是否发送数据给保险公司：0:不需要 1:需要 */
	private String sendState = null;			/** 发送状态: 0未发送 1发送成功 2发送失败 */
	private String insertDate = null;			/** 任务建立时间 */
	private String inputUserId;					/** 派单人id */
	private String sendUserId;					/** 录单人id */
	private String workAddress;					/** 作业地点*/
	private String isShow;						/** 是否显示：0或null =显示  1=不显示在调度中心 */
	private String companyUser = null;			/** 保险公司对应的作业工号 */
	
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
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCompanyTaskId() {
		return companyTaskId;
	}
	public void setCompanyTaskId(String companyTaskId) {
		this.companyTaskId = companyTaskId;
	}
	public String getHandlerCode() {
		return handlerCode;
	}
	public void setHandlerCode(String handlerCode) {
		this.handlerCode = handlerCode;
	}
	public String getIsSend() {
		return isSend;
	}
	public void setIsSend(String isSend) {
		this.isSend = isSend;
	}
	public String getSendState() {
		return sendState;
	}
	public void setSendState(String sendState) {
		this.sendState = sendState;
	}
	public String getInsertDate() {
		return insertDate;
	}
	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}
	public String getInputUserId() {
		return inputUserId;
	}
	public void setInputUserId(String inputUserId) {
		this.inputUserId = inputUserId;
	}
	public String getSendUserId() {
		return sendUserId;
	}
	public void setSendUserId(String sendUserId) {
		this.sendUserId = sendUserId;
	}
	public String getWorkAddress() {
		return workAddress;
	}
	public void setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
	}
	public String getIsShow() {
		return isShow;
	}
	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}
	public String getCompanyUser() {
		return companyUser;
	}
	public void setCompanyUser(String companyUser) {
		this.companyUser = companyUser;
	}
	
}
