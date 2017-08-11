package net.chetong.order.model;

/**
 * 影像-图片节点
 */
public class PhotoNode extends Node{
	
	private String workId;// 作业id
	private String orderNo;// 订单号
	private String caseNo;// 报案号
	private String imageUrl;// 上传url
	private String fileSize;// 文件大小
	private String takephotoTime;// 拍照时间
	private String uploadTime;// 上传时间
	private String userId;//用户名
	private String serviceId;
	private String taskId;//任务id
	private String isYC;//是否是永诚
	private String uploadType;//上传类型 app 或 web
	private String isPassimage;/*-- 增加字段：上传图片为审核通过 0-否，1-是 --*/
	
	public String getIsPassimage() {
		return isPassimage;
	}

	public void setIsPassimage(String isPassimage) {
		this.isPassimage = isPassimage;
	}

	public PhotoNode() {
		super("1");
	}
	
	public String getWorkId() {
		return workId;
	}
	public void setWorkId(String workId) {
		this.workId = workId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getCaseNo() {
		return caseNo;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getTakephotoTime() {
		return takephotoTime;
	}
	public void setTakephotoTime(String takephotoTime) {
		this.takephotoTime = takephotoTime;
	}
	public String getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public String getIsYC() {
		return isYC;
	}
	
	public void setIsYC(String isYC) {
		this.isYC = isYC;
	}

	public String getUploadType() {
		return uploadType;
	}

	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}
}
