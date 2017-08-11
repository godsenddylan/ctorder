package net.chetong.order.model;

import java.io.Serializable;

/**
 * 驾驶员信息
 * @author wufeng@chetong.net
 *
 */
public class FhDriverInfoVO implements Serializable {

	private static final long serialVersionUID = -2841897157483154923L;
	
	private String createdBy = null;			/** 创建人 */
	private String createdDate = null;			/** 创建日期 */
	private String updatedBy = null;			/** 修改人 */
	private String updatedDate = null;			/** 修改日期 */
	
	private String id = null;					/** 表ID */
	private String driverName = null;			/** 驾驶员名字 */
	private String driverPhone = null;			/** 驾驶员电话 */
	private String driverCard = null;			/** 驾驶证 */
	private String permitModel = null;			/** 准驾车型 */
	private String isDriver = null;				/** 是否指定驾驶员 */
	private String driverType = null;			/** 驾驶证类型  */
	private String remark = null;				/**	备注说明 */
	private String carId = null;				/** 车ID **/
	
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
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getDriverPhone() {
		return driverPhone;
	}
	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}
	public String getDriverCard() {
		return driverCard;
	}
	public void setDriverCard(String driverCard) {
		this.driverCard = driverCard;
	}
	public String getPermitModel() {
		return permitModel;
	}
	public void setPermitModel(String permitModel) {
		this.permitModel = permitModel;
	}
	public String getIsDriver() {
		return isDriver;
	}
	public void setIsDriver(String isDriver) {
		this.isDriver = isDriver;
	}
	public String getDriverType() {
		return driverType;
	}
	public void setDriverType(String driverType) {
		this.driverType = driverType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCarId() {
		return carId;
	}
	public void setCarId(String carId) {
		this.carId = carId;
	}
	
}
