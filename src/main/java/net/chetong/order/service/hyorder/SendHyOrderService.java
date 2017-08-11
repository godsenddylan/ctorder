package net.chetong.order.service.hyorder;

import java.util.Map;

import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

public interface SendHyOrderService {

	public ResultVO<Object> sendHyOrder(Map<String, Object> paraMap) throws ProcessException;

	public ResultVO<Object> reSendHyOrder(Map<String, Object> paraMap) throws ProcessException;
}
