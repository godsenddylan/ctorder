package net.chetong.order.model.webservice;

public class YcImageVO implements java.io.Serializable{
	
	private static final long serialVersionUID = -37118728087882485L;
	private String reportNo = null;
	private String taskId = null;
	private String url = null;
	private String maxType = null;
	private String minType = null;
	private String remark = null;
	private String fileName = null;
	public String getReportNo() {
		return reportNo;
	}
	public void setReportNo(String reportNo) {
		this.reportNo = reportNo;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMaxType() {
		return maxType;
	}
	public void setMaxType(String maxType) {
		this.maxType = maxType;
	}
	public String getMinType() {
		return minType;
	}
	public void setMinType(String minType) {
		this.minType = minType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
