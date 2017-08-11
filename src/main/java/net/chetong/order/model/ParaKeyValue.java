/*****************************************************
 *
 * Copyright (c) 2016 , 邦泰联合(北京)科技有限公司
 * All rights reserved
 *
 * 文 件 名 : ParaKeyValue.java
 * 摘    要 :  
 * 版    本 : 1.0
 * 作    者 : CodeGen
 * 创建日期 : 2016-06-06 02:29:35
 * 备    注 : 本文件由工具自动生成，请勿手动修改。
 *
 *****************************************************/
package net.chetong.order.model; 

import java.util.Date;

public class ParaKeyValue implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*--  --*/
	private String paraType;	/*-- 参数类型 --*/
	private String paraKey;	/*-- 键值 --*/
	private String paraRelationship;	/*-- 关系,最好用mysql的运算符号 --*/
	private String paraValue;	/*-- 存值 --*/
	private String paraValue2;	/*-- 存值2,当有范围时使用 --*/
	private String paraNote;	/*-- 说明 --*/
	private String enableFlag;	/*-- 是否可用,1-可用,0-不可用 --*/
	private String createBy;	/*-- 创建人 --*/
	private Date createTime;	/*-- 创建时间 --*/

	/**
	 * 构造函数.
	 */
	public ParaKeyValue() {}
	
	
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
	 * getParaType.
	 */
	public String getParaType(){
		return paraType;
	}
	
	/**
   * setParaType.
   */
  
	public void setParaType(String paraType){
		this.paraType = paraType;
	}

		
	/**
	 * getParaKey.
	 */
	public String getParaKey(){
		return paraKey;
	}
	
	/**
   * setParaKey.
   */
  
	public void setParaKey(String paraKey){
		this.paraKey = paraKey;
	}

		
	/**
	 * getParaRelationship.
	 */
	public String getParaRelationship(){
		return paraRelationship;
	}
	
	/**
   * setParaRelationship.
   */
  
	public void setParaRelationship(String paraRelationship){
		this.paraRelationship = paraRelationship;
	}

		
	/**
	 * getParaValue.
	 */
	public String getParaValue(){
		return paraValue;
	}
	
	/**
   * setParaValue.
   */
  
	public void setParaValue(String paraValue){
		this.paraValue = paraValue;
	}

		
	/**
	 * getParaValue2.
	 */
	public String getParaValue2(){
		return paraValue2;
	}
	
	/**
   * setParaValue2.
   */
  
	public void setParaValue2(String paraValue2){
		this.paraValue2 = paraValue2;
	}

		
	/**
	 * getParaNote.
	 */
	public String getParaNote(){
		return paraNote;
	}
	
	/**
   * setParaNote.
   */
  
	public void setParaNote(String paraNote){
		this.paraNote = paraNote;
	}

		
	/**
	 * getEnableFlag.
	 */
	public String getEnableFlag(){
		return enableFlag;
	}
	
	/**
   * setEnableFlag.
   */
  
	public void setEnableFlag(String enableFlag){
		this.enableFlag = enableFlag;
	}

		
	/**
	 * getCreateBy.
	 */
	public String getCreateBy(){
		return createBy;
	}
	
	/**
   * setCreateBy.
   */
  
	public void setCreateBy(String createBy){
		this.createBy = createBy;
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

}
