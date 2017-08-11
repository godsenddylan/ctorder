package net.chetong.order.model.form;

/**
 * 订单转派查询车童请求对象
 * Copyright (c) 2017,深圳市车童网络技术有限公司
 * All rights reserved
 * @author wufj
 * @date 2017年2月17日
 */
public class TransGetUserRequest {
	/**查询关键字 电话号码和用户名**/
	private String queryKey;
	/**订单号**/
	private String orderNo;
	/**旧卖家id**/
	private String oldSellerId;
	
	public String getQueryKey() {
		return queryKey;
	}
	public void setQueryKey(String queryKey) {
		this.queryKey = queryKey;
	}
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
	@Override
	public String toString() {
		return "TransGetUserRequest [queryKey=" + queryKey + ", orderNo=" + orderNo + ", oldSellerId=" + oldSellerId
				+ "]";
	}
}
