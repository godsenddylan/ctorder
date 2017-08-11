package net.chetong.order.model; 


public class FhLossImageVO implements java.io.Serializable {
	private static final long serialVersionUID = 1699288656775912792L;
	private Long id;	/*--  --*/
	private String guid;	/*-- guid --*/
	private String imgType;	/*-- 图片类型 --*/
	private String imgName;	/*-- 图片名称 --*/
	private String imgPath;	/*-- 大图片路径 --*/
	private String smallPath;	/*-- 缩略图路径 --*/
	private String insertTime;	/*-- 插入时间 --*/
	private String remark;	/*-- 备注 --*/
	private String enabled;	/*-- 是否可用 --*/
	private String taskId;	/*-- 任务id --*/
	private String uploadTime;	/*-- 上传时间 --*/
	private String userId;	/*-- 上传者 --*/
	private String tagId;
	private String uploadType; /*-- 上传类型，1-pc上传 2-app上传 --*/
	private String isPassimage;  /*-- 增加字段：上传图片为审核通过 0-否，1-是 --*/ 

	public String getIsPassimage() {
		return isPassimage;
	}


	public void setIsPassimage(String isPassimage) {
		this.isPassimage = isPassimage;
	}


	/**
	 * 构造函数.
	 */
	public FhLossImageVO() {}
	
	
	/**
	 * Getter/Setter方法.
	 */
		
	/**
	 * getId.
	 */
	public Long getId(){
		return id;
	}
	
	/**
   * setId.
   */
  
	public void setId(Long id){
		this.id = id;
	}

		
	/**
	 * getGuid.
	 */
	public String getGuid(){
		return guid;
	}
	
	/**
   * setGuid.
   */
  
	public void setGuid(String guid){
		this.guid = guid;
	}

		
	/**
	 * getImgType.
	 */
	public String getImgType(){
		return imgType;
	}
	
	/**
   * setImgType.
   */
  
	public void setImgType(String imgType){
		this.imgType = imgType;
	}

		
	/**
	 * getImgName.
	 */
	public String getImgName(){
		return imgName;
	}
	
	/**
   * setImgName.
   */
  
	public void setImgName(String imgName){
		this.imgName = imgName;
	}

		
	/**
	 * getImgPath.
	 */
	public String getImgPath(){
		return imgPath;
	}
	
	/**
   * setImgPath.
   */
  
	public void setImgPath(String imgPath){
		this.imgPath = imgPath;
	}

		
	/**
	 * getSmallPath.
	 */
	public String getSmallPath(){
		return smallPath;
	}
	
	/**
   * setSmallPath.
   */
  
	public void setSmallPath(String smallPath){
		this.smallPath = smallPath;
	}

		
	/**
	 * getInsertTime.
	 */
	public String getInsertTime(){
		return insertTime;
	}
	
	/**
   * setInsertTime.
   */
  
	public void setInsertTime(String insertTime){
		this.insertTime = insertTime;
	}

		
	/**
	 * getRemark.
	 */
	public String getRemark(){
		return remark;
	}
	
	/**
   * setRemark.
   */
  
	public void setRemark(String remark){
		this.remark = remark;
	}

		
	/**
	 * getEnabled.
	 */
	public String getEnabled(){
		return enabled;
	}
	
	/**
   * setEnabled.
   */
  
	public void setEnabled(String enabled){
		this.enabled = enabled;
	}

		
	/**
	 * getTaskId.
	 */
	public String getTaskId(){
		return taskId;
	}
	
	/**
   * setTaskId.
   */
  
	public void setTaskId(String taskId){
		this.taskId = taskId;
	}

		
	/**
	 * getUploadTime.
	 */
	public String getUploadTime(){
		return uploadTime;
	}
	
	/**
   * setUploadTime.
   */
  
	public void setUploadTime(String uploadTime){
		this.uploadTime = uploadTime;
	}

		
	/**
	 * getUserId.
	 */
	public String getUserId(){
		return userId;
	}
	
	/**
   * setUserId.
   */
  
	public void setUserId(String userId){
		this.userId = userId;
	}


	public String getTagId() {
		return tagId;
	}


	public void setTagId(String tagId) {
		this.tagId = tagId;
	}


	public String getUploadType() {
		return uploadType;
	}


	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}

}
