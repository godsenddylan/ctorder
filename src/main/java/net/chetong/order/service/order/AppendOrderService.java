package net.chetong.order.service.order;

import java.util.List;
import java.util.Map;

import net.chetong.order.model.FhCarModelVO;
import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.exception.ServiceException;

public interface AppendOrderService {

	public Map<String, String> append(Map<String, Object> paraMap) throws ProcessException;

	/**
	 * 三者定损-查询三者车列表
	 * 
	 * @param caseNo
	 *            报案号
	 * 
	 * @return List<FhCarModelVO>
	 **/
	public List<FhCarModelVO> queryThreeCarList(String caseNo);
}
