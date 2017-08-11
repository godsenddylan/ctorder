package net.chetong.order.model;

import java.io.Serializable;

public class PdOrderPayInfoVO implements Serializable {

	private static final long serialVersionUID = -3100790518454328668L;
	
	private String id = null;    		/** 表ID */
	private String orderType = null;	/** 订单类型(1 内部订单 2外部订单 3标准订单) 废弃 */
	private String baseRatio = null;	/** 基础费支付比例 */
	private String travelRatio = null;	/** 远程作业费支付比例 */
	private String extraRatio = null;	/** 附加费支付比例 */
	private String rewardRatio = null;	/** 买家奖励支付比例 */
	private String isDefault = null;	/** 是否默认 */
	private String buyerId = null;		/** 买家ID */
	private String serviceId = null;	/** 服务类型 */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getBaseRatio() {
		return baseRatio;
	}
	public void setBaseRatio(String baseRatio) {
		this.baseRatio = baseRatio;
	}
	public String getTravelRatio() {
		return travelRatio;
	}
	public void setTravelRatio(String travelRatio) {
		this.travelRatio = travelRatio;
	}
	public String getExtraRatio() {
		return extraRatio;
	}
	public void setExtraRatio(String extraRatio) {
		this.extraRatio = extraRatio;
	}
	public String getRewardRatio() {
		return rewardRatio;
	}
	public void setRewardRatio(String rewardRatio) {
		this.rewardRatio = rewardRatio;
	}
	public String getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	public String getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
}
