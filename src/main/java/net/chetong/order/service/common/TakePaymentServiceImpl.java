package net.chetong.order.service.common;

import org.springframework.stereotype.Service;

import net.chetong.order.model.CtTakePaymentVO;
import net.chetong.order.util.ctenum.ServiceId;

/**
 * 
 * 
 * @author wufeng@chetong.net
 * @creation 2015年11月4日
 */

@Service("takePaymentService")
public class TakePaymentServiceImpl extends BaseService implements TakePaymentService {

	@Override
	public CtTakePaymentVO queryCtTakePayment(long userId, ServiceId serviceId) {
		CtTakePaymentVO tackPaymentExample = new CtTakePaymentVO();
		tackPaymentExample.setUserId(userId);
		tackPaymentExample.setServiceId(serviceId.getValue());
		tackPaymentExample.setPayStatus("1"); // 1-正常
		return commExeSqlDAO.queryForObject("ct_take_payment.queryCtTakePayment", tackPaymentExample);
	}
}
