package net.chetong.order.service.hyorder;

import org.springframework.ui.ModelMap;

import net.chetong.order.model.HyExpressVO;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

public interface HyExpressService {
	
	/**
	 * 更新快递信息
	 * @author 
	 *         2015年12月29日 上午10:27:07
	 * @param modelMap
	 * @return
	 */
	public ResultVO<Object> insertHyExpress(ModelMap modelMap) throws ProcessException;
	
	/**				
	 * 查询快递信息
	 * @author 
	 *         2015年12月29日 上午10:27:07
	 * @param orderNo
	 * @return
	 */
	public ResultVO<Object> queryHyExpressByOrderNo(String orderNo) throws ProcessException;
	
	/**
	 * 删除快递图片链接
	 * @author 
	 *         2015年12月29日 上午10:27:07
	 * @param modelMap
	 * @return
	 */
	public ResultVO<Object> updateHyExpressPic(ModelMap modelMap) throws ProcessException;

	/**
	 * 车童修改订单的快递信息
	 * @author luoqiao
	 * @time 2016-11-10 14:20:50
	 * @param modelMap：
	 * 					orderNo 订单号
	 * 					userId 当前登录人id（车童）
	 * @return
	 */
	public ResultVO<String> updateOrderExpressBySeller(ModelMap modelMap);

}
