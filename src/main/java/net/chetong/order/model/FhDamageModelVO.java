/*****************************************************
 *
 * Copyright (c) 2014 , 邦泰联合(北京)科技有限公司
 * All rights reserved
 *
 * 文 件 名 : FhDamageModel.java
 * 摘    要 :  
 * 版    本 : 1.0
 * 作    者 : CodeGen
 * 创建日期 : 2014-12-30 14:42:00
 * 备    注 : 本文件由工具自动生成，请勿手动修改。
 *
 *****************************************************/
package net.chetong.order.model; 


public class FhDamageModelVO implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*--  --*/
	private String project;	/*-- 项目名称 --*/
	private String standard;	/*-- 型号 --*/
	private String unit;	/*-- 单位 --*/
	private String num;	/*-- 数量 --*/
	private String price;	/*-- 单价 --*/
	private String subtotal;	/*-- 小计 --*/
	private String explain;	/*-- 说明 --*/
	private String orderno;	/*--  --*/
	private String orderCode;	/*-- 订单号 --*/
	private String guid;	/*-- guid --*/
	private String auditPrice;//2015-08-05 wfj 需求变动 核价费用
	private String explain2;	/*-- 核价说明 --*/
	private String auditunitPrice;	/*--核价单价 --*/

	/**
	 * 构造函数.
	 */
	public FhDamageModelVO() {}
	
	
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
	 * getProject.
	 */
	public String getProject(){
		return project;
	}
	
	/**
   * setProject.
   */
  
	public void setProject(String project){
		this.project = project;
	}

		
	/**
	 * getStandard.
	 */
	public String getStandard(){
		return standard;
	}
	
	/**
   * setStandard.
   */
  
	public void setStandard(String standard){
		this.standard = standard;
	}

		
	/**
	 * getUnit.
	 */
	public String getUnit(){
		return unit;
	}
	
	/**
   * setUnit.
   */
  
	public void setUnit(String unit){
		this.unit = unit;
	}

		
	/**
	 * getNum.
	 */
	public String getNum(){
		return num;
	}
	
	/**
   * setNum.
   */
  
	public void setNum(String num){
		this.num = num;
	}

		
	/**
	 * getPrice.
	 */
	public String getPrice(){
		return price;
	}
	
	/**
   * setPrice.
   */
  
	public void setPrice(String price){
		this.price = price;
	}

		
	/**
	 * getSubtotal.
	 */
	public String getSubtotal(){
		return subtotal;
	}
	
	/**
   * setSubtotal.
   */
  
	public void setSubtotal(String subtotal){
		this.subtotal = subtotal;
	}

		
	/**
	 * getExplain.
	 */
	public String getExplain(){
		return explain;
	}
	
	/**
   * setExplain.
   */
  
	public void setExplain(String explain){
		this.explain = explain;
	}

		
	/**
	 * getOrderno.
	 */
	public String getOrderno(){
		return orderno;
	}
	
	/**
   * setOrderno.
   */
  
	public void setOrderno(String orderno){
		this.orderno = orderno;
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


	public String getAuditPrice() {
		return auditPrice;
	}


	public void setAuditPrice(String auditPrice) {
//		if(auditPrice==null||"".equals(auditPrice.trim())){  //数据初始化时，若没有指定核价费用。那么核价费用默认为车童提交的单价费用
//			this.auditPrice=(Float.valueOf(price==null?"0":price)*Float.valueOf(num==null?"0":num))+"";
//		}else{
			this.auditPrice = auditPrice;
//		}
	}


	public String getExplain2() {
		return explain2;
	}


	public void setExplain2(String explain2) {
		this.explain2 = explain2;
	}


	public String getAuditunitPrice() {
		return auditunitPrice;
	}


	public void setAuditunitPrice(String auditunitPrice) {
		this.auditunitPrice = auditunitPrice;
	}
	
	

}
