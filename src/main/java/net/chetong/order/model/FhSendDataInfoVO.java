package net.chetong.order.model;

import java.io.Serializable;

public class FhSendDataInfoVO implements Serializable {

	private static final long serialVersionUID = 1622972850174906471L;
	private String createdBy = null;			/** 创建人 */
	private String createdDate = null;			/** 创建日期 */
	private String updatedBy = null;			/** 修改人 */
	private String updatedDate = null;			/** 修改日期 */	
	
	private String id = null;					/** 表ID */
	private String reportNo = null;				/** 报案号 */
	private String relationId = null;			/** 关联表ID */
	private String relationType = null;			/** 关联表类型：1=fm_task_info永诚作业 2=hy_image永诚图片 */
	private String content = null;				/** 发送内容 **/
	private String sendDate = null;				/** 发送时间 */
	private String sendState = null;			/** 发送状态 0未发送  1发送成功 2发送失败 */
	private String backResult = null;			/** 返回结果 */
	private String remark = null;				/** 备注 */
	
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
	public String getRelationId() {
		return relationId;
	}
	public void setRelationId(String relationId) {
		this.relationId = relationId;
	}
	public String getRelationType() {
		return relationType;
	}
	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSendDate() {
		return sendDate;
	}
	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}
	public String getSendState() {
		return sendState;
	}
	public void setSendState(String sendState) {
		this.sendState = sendState;
	}
	public String getBackResult() {
		return backResult;
	}
	public void setBackResult(String backResult) {
		this.backResult = backResult;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
