package net.chetong.order.service.order;

public interface OrderFeeService {
	/**
	 * 清除订单的团队费用信息
	 * @param orderId
	 */
	void cleanOrderGroupFee(Long orderId);
}
