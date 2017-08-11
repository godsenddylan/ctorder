package net.chetong.order.service.hyorder;

import org.springframework.ui.ModelMap;

import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

public interface HyLeaveService {
	
	/**
	 * 保存订单留言信息
	 * @author 
	 *         2015年12月29日 上午10:27:07
	 * @param modelMap
	 * @return
	 */
	public ResultVO<Object> saveHyLeave(ModelMap modelMap) throws ProcessException;
	
	/**				
	 * 查询留言信息
	 * @author 
	 *         2015年12月29日 上午10:27:07
	 * @param userId
	 * @return
	 */
	public ResultVO<Object> queryHyLeave(ModelMap modelMap) throws ProcessException;
}
