/*****************************************************
 *
 * Copyright (c) 2016 , 邦泰联合(北京)科技有限公司
 * All rights reserved
 *
 * 文 件 名 : FmOrderDurationInfo.java
 * 摘    要 :  
 * 版    本 : 1.0
 * 作    者 : CodeGen
 * 创建日期 : 2016-06-06 02:29:36
 * 备    注 : 本文件由工具自动生成，请勿手动修改。
 *
 *****************************************************/
package net.chetong.order.model; 

import java.util.Date;

public class FmOrderDurationInfo implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*--  --*/
	private Long orderId;	/*-- 订单id --*/
	private String orderNo;	/*-- 订单编号 --*/
	private Long serviceId;	/*-- 服务id: --*/
	private Long workDuration;	/*-- 累计作业时长 --*/
	private Long aduitDuration;	/*-- 待审核时长 --*/
	private Date createTime;	/*-- 创建时间 --*/
	private Date updateTime;	/*-- 更新时间 --*/

	/**
	 * 构造函数.
	 */
	public FmOrderDurationInfo() {}
	
	
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
	 * getOrderId.
	 */
	public Long getOrderId(){
		return orderId;
	}
	
	/**
   * setOrderId.
   */
  
	public void setOrderId(Long orderId){
		this.orderId = orderId;
	}

		
	/**
	 * getOrderNo.
	 */
	public String getOrderNo(){
		return orderNo;
	}
	
	/**
   * setOrderNo.
   */
  
	public void setOrderNo(String orderNo){
		this.orderNo = orderNo;
	}

		
	/**
	 * getServiceId.
	 */
	public Long getServiceId(){
		return serviceId;
	}
	
	/**
   * setServiceId.
   */
  
	public void setServiceId(Long serviceId){
		this.serviceId = serviceId;
	}

		
	/**
	 * getWorkDuration.
	 */
	public Long getWorkDuration(){
		return workDuration;
	}
	
	/**
   * setWorkDuration.
   */
  
	public void setWorkDuration(Long workDuration){
		this.workDuration = workDuration;
	}

		
	/**
	 * getAduitDuration.
	 */
	public Long getAduitDuration(){
		return aduitDuration;
	}
	
	/**
   * setAduitDuration.
   */
  
	public void setAduitDuration(Long aduitDuration){
		this.aduitDuration = aduitDuration;
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

}
