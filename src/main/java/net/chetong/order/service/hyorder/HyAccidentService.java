package net.chetong.order.service.hyorder;


import net.chetong.order.model.HyOrderWorkVO;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

public interface HyAccidentService {
	
	/**
	 * 保存订单留言信息
	 * @author 
	 *         2015年12月29日 上午10:27:07
	 * @param modelMap
	 * @return
	 */
	public ResultVO<Object> saveHyOrderWork(HyOrderWorkVO hyOrderWorkVO) throws ProcessException;
	
	/**				
	 * 查询留言信息
	 * @author 
	 *         2015年12月29日 上午10:27:07
	 * @param orderNo
	 * @return
	 */
	public ResultVO<Object> queryHyOrderWork(String orderNo) throws ProcessException;
}
