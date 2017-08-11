package net.chetong.order.model.form;

/**
 * 订单转派 请求参数对象
 * Copyright (c) 2017,深圳市车童网络技术有限公司
 * All rights reserved
 * @author wufj
 * @date 2017年2月16日
 */
public class TransformRequest {
	//订单号
	private String orderNo;
	//旧卖家id
	private String oldSellerId;
	//新卖家id
	private String newSellerId;
	//距离
	private String distanceDecimal;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOldSellerId() {
		return oldSellerId;
	}
	public void setOldSellerId(String oldSellerId) {
		this.oldSellerId = oldSellerId;
	}
	public String getNewSellerId() {
		return newSellerId;
	}
	public void setNewSellerId(String newSellerId) {
		this.newSellerId = newSellerId;
	}
	public String getDistanceDecimal() {
		return distanceDecimal;
	}
	public void setDistanceDecimal(String distanceDecimal) {
		this.distanceDecimal = distanceDecimal;
	}
	@Override
	public String toString() {
		return "TransformRequest [orderNo=" + orderNo + ", oldSellerId=" + oldSellerId + ", newSellerId=" + newSellerId
				+ ", distanceDecimal=" + distanceDecimal + "]";
	}
}
