package net.chetong.order.model;

import java.util.List;

public class HyCaseTemplate {
	/**
	 * 模板信息
	 * @author 
	 *         2016年01月15日 下午3:44:17
	 */
	private Long id;// ID
	private String fileName;// 文件名
	private String fileUrl;// 文件腾讯云url
	private String fileUrls;// 文件腾讯云url
	private String fileLevel;// 模板级别（0 系统 1买家 2案件）
	private String fileDesc;// 文件的描述（备注）
	private String fileSize;// 文件大小
	private String orgId;// 买家userId（没有为NULL）
	private String caseId;// 案件id（没有为NULL）
	private String caseNo;// 案件号
	private String createBy;// 添加人userid
	private String createTime;// 创建时间
	private String updateBy;// 更新人
	private String updateTime;// 
	private List<HyCaseTemplateImage> hyCaseTemplateImageList; //任务中的运输路线信息
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public String getFileLevel() {
		return fileLevel;
	}
	public void setFileLevel(String fileLevel) {
		this.fileLevel = fileLevel;
	}
	public String getFileDesc() {
		return fileDesc;
	}
	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	public String getCaseNo() {
		return caseNo;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public List<HyCaseTemplateImage> getHyCaseTemplateImageList() {
		return hyCaseTemplateImageList;
	}
	public void setHyCaseTemplateImageList(List<HyCaseTemplateImage> hyCaseTemplateImageList) {
		this.hyCaseTemplateImageList = hyCaseTemplateImageList;
	}

}
