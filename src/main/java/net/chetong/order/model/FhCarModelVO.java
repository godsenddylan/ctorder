/*****************************************************
 *
 * Copyright (c) 2014 , 邦泰联合(北京)科技有限公司
 * All rights reserved
 *
 * 文 件 名 : FhCarModel.java
 * 摘    要 :  
 * 版    本 : 1.0
 * 作    者 : CodeGen
 * 创建日期 : 2014-12-30 14:42:01
 * 备    注 : 本文件由工具自动生成，请勿手动修改。
 *
 *****************************************************/
package net.chetong.order.model; 


public class FhCarModelVO implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*--  --*/
	private String carmark;	/*-- 车牌 --*/
	private String drivername;	/*-- 驾驶员 --*/
	private String driverphone;	/*-- 驾驶员电话 --*/
	private String guid;	/*-- guid --*/
	private String isMain;	/*-- 是否标的 --*/
	private String orderCode;	/*-- 订单号 --*/
	private String remark;	/*-- 备注 --*/
	private String insertTime;	/*-- 插入时间 --*/
	private String lastTime;	/*-- 最后一次操作时间 --*/

	/**
	 * 构造函数.
	 */
	public FhCarModelVO() {}
	
	
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
	 * getCarmark.
	 */
	public String getCarmark(){
		return carmark;
	}
	
	/**
   * setCarmark.
   */
  
	public void setCarmark(String carmark){
		this.carmark = carmark;
	}

		
	/**
	 * getDrivername.
	 */
	public String getDrivername(){
		return drivername;
	}
	
	/**
   * setDrivername.
   */
  
	public void setDrivername(String drivername){
		this.drivername = drivername;
	}

		
	/**
	 * getDriverphone.
	 */
	public String getDriverphone(){
		return driverphone;
	}
	
	/**
   * setDriverphone.
   */
  
	public void setDriverphone(String driverphone){
		this.driverphone = driverphone;
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
	 * getIsMain.
	 */
	public String getIsMain(){
		return isMain;
	}
	
	/**
   * setIsMain.
   */
  
	public void setIsMain(String isMain){
		this.isMain = isMain;
	}

		
	/**
	 * getOrderCode.
	 */
	public String getOrderCode(){
		return orderCode;
	}
	
	/**
   * setOrderCode.
   */
  
	public void setOrderCode(String orderCode){
		this.orderCode = orderCode;
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
	 * getLastTime.
	 */
	public String getLastTime(){
		return lastTime;
	}
	
	/**
   * setLastTime.
   */
  
	public void setLastTime(String lastTime){
		this.lastTime = lastTime;
	}

}
