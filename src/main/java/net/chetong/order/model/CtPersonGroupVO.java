/*****************************************************
 *
 * Copyright (c) 2015 , 邦泰联合(北京)科技有限公司
 * All rights reserved
 *
 * 文 件 名 : CtPersonGroup.java
 * 摘    要 :  
 * 版    本 : 1.0
 * 作    者 : CodeGen
 * 创建日期 : 2015-01-08 14:20:59
 * 备    注 : 本文件由工具自动生成，请勿手动修改。
 *
 *****************************************************/
package net.chetong.order.model; 

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
@XmlRootElement
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CtPersonGroupVO implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*--  --*/
	private Long userId;	/*--  --*/
	private Long groupId;	/*--  --*/
	private String stat;	/*-- 状态 0 - 待审批 1 - 已加入 2 - 已退出 3 - 未通过 --*/
	private Date applyTime;	/*-- 申请加入时间 --*/
	private Date auditTime;	/*-- 审核时间 --*/
	private Long auditUserId;	/*-- 审核人 --*/
	private Long serviceId;	/*--  --*/
	private String ext1;	/*-- groupId类型：1 个人加入机构 2个人加入团队 3团队加入机构 --*/
	private String ext2;	/*-- 扩展字段2 --*/
	private String ext3;	/*-- 扩展字段3 --*/

	/**
	 * 构造函数.
	 */
	public CtPersonGroupVO() {}
	
	
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
	 * getUserId.
	 */
	public Long getUserId(){
		return userId;
	}
	
	/**
   * setUserId.
   */
  
	public void setUserId(Long userId){
		this.userId = userId;
	}

		
	/**
	 * getGroupId.
	 */
	public Long getGroupId(){
		return groupId;
	}
	
	/**
   * setGroupId.
   */
  
	public void setGroupId(Long groupId){
		this.groupId = groupId;
	}

		
	/**
	 * getStat.
	 */
	public String getStat(){
		return stat;
	}
	
	/**
   * setStat.
   */
  
	public void setStat(String stat){
		this.stat = stat;
	}

		
	/**
	 * getApplyTime.
	 */
	public Date getApplyTime(){
		return applyTime;
	}
	
	/**
   * setApplyTime.
   */
  
	public void setApplyTime(Date applyTime){
		this.applyTime = applyTime;
	}

		
	/**
	 * getAuditTime.
	 */
	public Date getAuditTime(){
		return auditTime;
	}
	
	/**
   * setAuditTime.
   */
  
	public void setAuditTime(Date auditTime){
		this.auditTime = auditTime;
	}

		
	/**
	 * getAuditUserId.
	 */
	public Long getAuditUserId(){
		return auditUserId;
	}
	
	/**
   * setAuditUserId.
   */
  
	public void setAuditUserId(Long auditUserId){
		this.auditUserId = auditUserId;
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
