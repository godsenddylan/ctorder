package net.chetong.order.model;

import java.io.Serializable;

/***
 * 修理厂信息
 * @author wufeng@chetong.net
 *
 */
public class FhRepairFactoryInfoVO implements Serializable{

	private static final long serialVersionUID = 2867669827224590873L;
	private String createdBy = null;			/** 创建人 */
	private String createdDate = null;			/** 创建日期 */
	private String updatedBy = null;			/** 修改人 */
	private String updatedDate = null;			/** 修改日期 */	
	
	private String id = null;				/** 表ID */
	private String lossId = null;			/** 定损ID */
	private String isPushRepair = null;		/** 是否推送修理 */
	private String noPushReason = null;		/** 未推送修备注 */
	private String channelFactory = null;	/** 渠道修理厂 */
	private String factoryName = null;		/** 修理厂名称 */
	private String factoryType = null;		/** 修理厂类型 */
	private String organizationNo = null; 	/** 组织机构代码证号码 */
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLossId() {
		return lossId;
	}
	public void setLossId(String lossId) {
		this.lossId = lossId;
	}
	public String getIsPushRepair() {
		return isPushRepair;
	}
	public void setIsPushRepair(String isPushRepair) {
		this.isPushRepair = isPushRepair;
	}
	public String getNoPushReason() {
		return noPushReason;
	}
	public void setNoPushReason(String noPushReason) {
		this.noPushReason = noPushReason;
	}
	public String getChannelFactory() {
		return channelFactory;
	}
	public void setChannelFactory(String channelFactory) {
		this.channelFactory = channelFactory;
	}
	public String getFactoryName() {
		return factoryName;
	}
	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}
	public String getFactoryType() {
		return factoryType;
	}
	public void setFactoryType(String factoryType) {
		this.factoryType = factoryType;
	}
	public String getOrganizationNo() {
		return organizationNo;
	}
	public void setOrganizationNo(String organizationNo) {
		this.organizationNo = organizationNo;
	}
}
