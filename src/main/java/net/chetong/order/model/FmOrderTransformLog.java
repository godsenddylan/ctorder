/**
 * Copyright (c) 2016 , 深圳市车童网络技术有限公司
 * All rights reserved

 * 文  件  名：FmOrderTransformLog.java
 * 摘        要：订单转派日志表(fm_order_transform_log)
 * 版        本：1.0
 * 创建时间：2017-02-47 16:33:39
 * 备        注：本文件由工具自动生成，若字段有变动请重新生成，建议不要手动修改...
 */
package net.chetong.order.model;

import java.util.Date;


/**
 * 订单转派日志表(fm_order_transform_log)
 */
public class FmOrderTransformLog {

	private static final long serialVersionUID = 1L;

	/***/
	private Long id;

	/**订单id*/
	private Long orderId;

	/**订单号*/
	private String orderNo;

	/**旧订单卖家id*/
	private Long oldSellerId;

	/***/
	private String oldSellerName;

	/**订单新卖家id*/
	private Long newSellerId;

	/***/
	private String newSellerName;

	/**转派时间*/
	private Date transTime;



	public void setId(Long id){
		this.id = id;
	}

	public Long getId(){
		return this.id;
	}

	public void setOrderId(Long orderId){
		this.orderId = orderId;
	}

	public Long getOrderId(){
		return this.orderId;
	}

	public void setOrderNo(String orderNo){
		this.orderNo = orderNo;
	}

	public String getOrderNo(){
		return this.orderNo;
	}

	public void setOldSellerId(Long oldSellerId){
		this.oldSellerId = oldSellerId;
	}

	public Long getOldSellerId(){
		return this.oldSellerId;
	}

	public void setOldSellerName(String oldSellerName){
		this.oldSellerName = oldSellerName;
	}

	public String getOldSellerName(){
		return this.oldSellerName;
	}

	public void setNewSellerId(Long newSellerId){
		this.newSellerId = newSellerId;
	}

	public Long getNewSellerId(){
		return this.newSellerId;
	}

	public void setNewSellerName(String newSellerName){
		this.newSellerName = newSellerName;
	}

	public String getNewSellerName(){
		return this.newSellerName;
	}

	public void setTransTime(Date transTime){
		this.transTime = transTime;
	}

	public Date getTransTime(){
		return this.transTime;
	}

}
