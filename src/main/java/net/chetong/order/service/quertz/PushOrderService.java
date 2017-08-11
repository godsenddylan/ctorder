package net.chetong.order.service.quertz;

/**
 * 超时订单推送提醒
 * @author lijq
 *
 */
public interface PushOrderService {
	
	/**
	 * 订单超时推送提醒
	 */
	public void checkOverOrder();
	
	/**
	 * 
	 * 车险订单
	 * @throws
	 */
	public void orderLoseForCarInsurance();
	
	/**
	 * 货运险订单
	 * @throws
	 */
	public void orderLoseForFreight();

}
