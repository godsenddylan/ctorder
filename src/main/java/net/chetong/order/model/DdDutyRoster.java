/**
 * Copyright (c) 2016 , 深圳市车童网络技术有限公司
 * All rights reserved

 * 文  件  名：DdDutyRoster.java
 * 摘        要：车童排班信息(dd_duty_roster)
 * 版        本：1.0
 * 创建时间：2017-02-47 10:36:26
 * 备        注：本文件由工具自动生成，若字段有变动请重新生成，建议不要手动修改...
 */
package net.chetong.order.model;

import java.util.Date;
import com.chetong.aic.entity.base.BaseModel;


/**
 * 车童排班信息(dd_duty_roster)
 */
public class DdDutyRoster extends BaseModel {

	private static final long serialVersionUID = 1L;

	/***/
	private Long id;

	/**车童ID*/
	private Long ctUserId;

	/**1：车童；2：团队长；3：受阻人；4：机构负责人*/
	private String userType;

	/***/
	private Long ruleId;

	/**工作区域代码*/
	private String areaCode;

	/**排班开始日期*/
	private Date beginDate;

	/**排班结束日期*/
	private Date endDate;

	/**单双号标识：0：双号；1：单号；2：不区分*/
	private Integer oddEven;

	/**状态：0：无效；1：有效*/
	private Integer status;

	/**优先级*/
	private Integer level;

	/***/
	private Long createUserId;

	/***/
	private Date createTime;

	/***/
	private Date modifyTime;

	/**手机号码*/
	private String mobile;

	/**服务类别*/
	private Long serviceId;



	public void setId(Long id){
		this.id = id;
	}

	public Long getId(){
		return this.id;
	}

	public void setCtUserId(Long ctUserId){
		this.ctUserId = ctUserId;
	}

	public Long getCtUserId(){
		return this.ctUserId;
	}

	public void setUserType(String userType){
		this.userType = userType;
	}

	public String getUserType(){
		return this.userType;
	}

	public void setRuleId(Long ruleId){
		this.ruleId = ruleId;
	}

	public Long getRuleId(){
		return this.ruleId;
	}

	public void setAreaCode(String areaCode){
		this.areaCode = areaCode;
	}

	public String getAreaCode(){
		return this.areaCode;
	}

	public void setBeginDate(Date beginDate){
		this.beginDate = beginDate;
	}

	public Date getBeginDate(){
		return this.beginDate;
	}

	public void setEndDate(Date endDate){
		this.endDate = endDate;
	}

	public Date getEndDate(){
		return this.endDate;
	}

	public void setOddEven(Integer oddEven){
		this.oddEven = oddEven;
	}

	public Integer getOddEven(){
		return this.oddEven;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

	public void setLevel(Integer level){
		this.level = level;
	}

	public Integer getLevel(){
		return this.level;
	}

	public void setCreateUserId(Long createUserId){
		this.createUserId = createUserId;
	}

	public Long getCreateUserId(){
		return this.createUserId;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}

	public Date getCreateTime(){
		return this.createTime;
	}

	public void setModifyTime(Date modifyTime){
		this.modifyTime = modifyTime;
	}

	public Date getModifyTime(){
		return this.modifyTime;
	}

	public void setMobile(String mobile){
		this.mobile = mobile;
	}

	public String getMobile(){
		return this.mobile;
	}

	public void setServiceId(Long serviceId){
		this.serviceId = serviceId;
	}

	public Long getServiceId(){
		return this.serviceId;
	}

}
