/*****************************************************
 *
 * Copyright (c) 2015 , 邦泰联合(北京)科技有限公司
 * All rights reserved
 *
 * 文 件 名 : PrNegoPriceInfo.java
 * 摘    要 :  
 * 版    本 : 1.0
 * 作    者 : CodeGen
 * 创建日期 : 2015-05-08 15:00:30
 * 备    注 : 本文件由工具自动生成，请勿手动修改。
 *
 *****************************************************/
package net.chetong.order.model;

/**
 * 机构团队议价信息类型
 * 
 * @author
 *
 */
public class PrNegoPriceInfoVO implements java.io.Serializable {
	private static final long serialVersionUID = 3687917914358476850L;

	private Long id; /*-- id --*/
	private Long buyerUserId; /*-- 买家id（保险公司） --*/
	private String buyerName; /*-- 买家名称 --*/
	private Long teamUserId; /*-- 团队id --*/
	private String teamName; /*-- 团队名称 --*/
	private String subjectType; /*-- 服务类别1-查勘 2-定损 3-其他 --*/
	private String status; /*-- 状态1 -启用 0-停用 --*/
	private String isDefault; /*-- 是否默认 1-是 0-否 --*/
	private Long linkId; /*-- 关联id --*/
	private String createTime;

	/**
	 * 构造函数.
	 */
	public PrNegoPriceInfoVO() {
	}

	/**
	 * Getter/Setter方法.
	 */

	/**
	 * getId.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * setId.
	 */

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * getBuyerUserId.
	 */
	public Long getBuyerUserId() {
		return buyerUserId;
	}

	/**
	 * setBuyerUserId.
	 */

	public void setBuyerUserId(Long buyerUserId) {
		this.buyerUserId = buyerUserId;
	}

	/**
	 * getBuyerName.
	 */
	public String getBuyerName() {
		return buyerName;
	}

	/**
	 * setBuyerName.
	 */

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	/**
	 * getTeamUserId.
	 */
	public Long getTeamUserId() {
		return teamUserId;
	}

	/**
	 * setTeamUserId.
	 */

	public void setTeamUserId(Long teamUserId) {
		this.teamUserId = teamUserId;
	}

	/**
	 * getTeamName.
	 */
	public String getTeamName() {
		return teamName;
	}

	/**
	 * setTeamName.
	 */

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	/**
	 * getSubjectType.
	 */
	public String getSubjectType() {
		return subjectType;
	}

	/**
	 * setSubjectType.
	 */

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	/**
	 * getStatus.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * setStatus.
	 */

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * getIsDefault.
	 */
	public String getIsDefault() {
		return isDefault;
	}

	/**
	 * setIsDefault.
	 */

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	/**
	 * getLinkId.
	 */
	public Long getLinkId() {
		return linkId;
	}

	/**
	 * setLinkId.
	 */

	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
