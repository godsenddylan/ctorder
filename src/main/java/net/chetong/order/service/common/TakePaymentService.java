package net.chetong.order.service.common;

import net.chetong.order.model.CtTakePaymentVO;
import net.chetong.order.util.ctenum.ServiceId;

/**
 * 机构
 * 
 * @author wufeng@chetong.net
 * @creation 2015年11月4日
 */

public interface TakePaymentService {

	/**
	 * 查询代支付
	 * @author wufj@chetong.net
	 *         2016年2月23日 上午11:05:02
	 * @param userId
	 * @param serviceId
	 * @return
	 */
	public CtTakePaymentVO queryCtTakePayment(long userId, ServiceId serviceId);
}
