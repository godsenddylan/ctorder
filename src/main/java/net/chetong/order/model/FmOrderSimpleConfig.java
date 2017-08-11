package net.chetong.order.model; 

import java.util.Date;

public class FmOrderSimpleConfig implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*-- id --*/
	private Long buyerUserId;	/*-- 委托方id（买方） --*/
	private String orderType;	/*-- 订单类型：0-全部、1-本地订单、2-异地订单 --*/
	private String isMustImg;   /*--是否必须上传图片才能完成订单：0-否，1-是 --*/
	private Date startTime;	/*-- 起始时间 --*/
	private Date endTime;	/*-- 结束时间 --*/
	private Date createTime;	/*-- 创建时间 --*/
	private Date updateTime;	/*-- 最新更改时间 --*/
	private String ext1;	/*-- 备用字段1 --*/
	private String ext2;	/*-- 备用字段2 --*/
	private String ext3;	/*-- 备用字段3 --*/

	/**
	 * 构造函数.
	 */
	public FmOrderSimpleConfig() {}
	
	
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
	 * getBuyerUserId.
	 */
	public Long getBuyerUserId(){
		return buyerUserId;
	}
	
	/**
   * setBuyerUserId.
   */
  
	public void setBuyerUserId(Long buyerUserId){
		this.buyerUserId = buyerUserId;
	}

		
	/**
	 * getOrderType.
	 */
	public String getOrderType(){
		return orderType;
	}
	
	/**
   * setOrderType.
   */
  
	public void setOrderType(String orderType){
		this.orderType = orderType;
	}

		
	public String getIsMustImg() {
		return isMustImg;
	}


	public void setIsMustImg(String isMustImg) {
		this.isMustImg = isMustImg;
	}


	/**
	 * getStartTime.
	 */
	public Date getStartTime(){
		return startTime;
	}
	
	/**
   * setStartTime.
   */
  
	public void setStartTime(Date startTime){
		this.startTime = startTime;
	}

		
	/**
	 * getEndTime.
	 */
	public Date getEndTime(){
		return endTime;
	}
	
	/**
   * setEndTime.
   */
  
	public void setEndTime(Date endTime){
		this.endTime = endTime;
	}

		
	/**
	 * getCreateTime.
	 */
	public Date getCreateTime(){
		return createTime;
	}
	
	/**
   * setCreateTime.
   */
  
	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}

		
	/**
	 * getUpdateTime.
	 */
	public Date getUpdateTime(){
		return updateTime;
	}
	
	/**
   * setUpdateTime.
   */
  
	public void setUpdateTime(Date updateTime){
		this.updateTime = updateTime;
	}

		
	/**
	 * getExt1.
	 */
	public String getExt1(){
		return ext1;
	}
	
	/**
   * setExt1.
   */
  
	public void setExt1(String ext1){
		this.ext1 = ext1;
	}

		
	/**
	 * getExt2.
	 */
	public String getExt2(){
		return ext2;
	}
	
	/**
   * setExt2.
   */
  
	public void setExt2(String ext2){
		this.ext2 = ext2;
	}

		
	/**
	 * getExt3.
	 */
	public String getExt3(){
		return ext3;
	}
	
	/**
   * setExt3.
   */
  
	public void setExt3(String ext3){
		this.ext3 = ext3;
	}

}
