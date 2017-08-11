/*****************************************************
 *
 * Copyright (c) 2014 , 邦泰联合(北京)科技有限公司
 * All rights reserved
 *
 * 文 件 名 : FhLeaveModel.java
 * 摘    要 :  
 * 版    本 : 1.0
 * 作    者 : CodeGen
 * 创建日期 : 2014-12-30 14:42:00
 * 备    注 : 本文件由工具自动生成，请勿手动修改。
 *
 *****************************************************/
package net.chetong.order.model; 


public class FhLeaveModelVO implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*--  --*/
	private String name;	/*-- 名称 --*/
	private String insertTime;	/*-- 插入时间 --*/
	private String detail;	/*-- 详细内容 --*/
	private String reserved;	/*-- 订单号 --*/
	private String userId;	/*-- 用户编号 --*/
	private String leaveRole;	/*-- 留言角色0_自己;1_别人 --*/
	private String leaveType;	/*-- 留言类型 --*/
	private String creatTime;	/*-- 留言创建时间 --*/
	private String displayTime;	/*-- 留言显示时间 --*/

	/**
	 * 构造函数.
	 */
	public FhLeaveModelVO() {}
	
	
	/**
	 * Getter/Setter方法.
	 */
		
	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getLeaveRole() {
		return leaveRole;
	}


	public void setLeaveRole(String leaveRole) {
		this.leaveRole = leaveRole;
	}


	public String getLeaveType() {
		return leaveType;
	}


	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}


	public String getCreatTime() {
		return creatTime;
	}


	public void setCreatTime(String creatTime) {
		this.creatTime = creatTime;
	}


	public String getDisplayTime() {
		return displayTime;
	}


	public void setDisplayTime(String displayTime) {
		this.displayTime = displayTime;
	}


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
	 * getName.
	 */
	public String getName(){
		return name;
	}
	
	/**
   * setName.
   */
  
	public void setName(String name){
		this.name = name;
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
	 * getDetail.
	 */
	public String getDetail(){
		return detail;
	}
	
	/**
   * setDetail.
   */
  
	public void setDetail(String detail){
		this.detail = detail;
	}

		
	/**
	 * getReserved.
	 */
	public String getReserved(){
		return reserved;
	}
	
	/**
   * setReserved.
   */
  
	public void setReserved(String reserved){
		this.reserved = reserved;
	}

}
