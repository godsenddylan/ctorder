package net.chetong.order.service.order;

import java.util.Map;

import net.chetong.order.util.exception.ProcessException;

public interface SendOrderService {

	public void sendOrder(Map<String, Object> paraMap) throws ProcessException;
	
	public void reSendOrder(Map<String, Object> paraMap) throws ProcessException;
}
