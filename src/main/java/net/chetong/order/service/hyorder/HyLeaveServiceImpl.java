package net.chetong.order.service.hyorder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;

@Service("hyLeaveService")
public class HyLeaveServiceImpl extends BaseService implements HyLeaveService{
	
	@Resource
	private HyOrderService hyOrderService;
	
	/**
	 * 添加订单留言
	 * @author 
	 *         2015年12月29日 上午10:30:49
	 * @param modelMap
	 * @return
	 */
	@Override
	public ResultVO<Object> saveHyLeave(ModelMap modelMap) throws ProcessException{
		try {
			commExeSqlDAO.insertVO("sqlmap_hy_order.saveHyLeave", modelMap);
			ResultVO<Object> resultVO = new ResultVO<Object>();
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO);
			return resultVO;
		} catch (Exception e) {
			log.error("货运险保存订单留言信息出错",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("货运险保存订单留言信息出错",e);
		}
	}
	
	/**
	 * 查询留言信息
	 * @author 
	 *         2015年12月29日 上午10:30:27
	 * @param orderNo
	 * @return
	 */
	@Override
	public ResultVO<Object> queryHyLeave(ModelMap modelMap) throws ProcessException{
		try{
			String orderNo = (String) modelMap.get("orderNo");
			String userId = (String) modelMap.get("userId");
			String role = hyOrderService.checkRole(orderNo, userId);
			List<Object> queryForList = commExeSqlDAO.queryForList("sqlmap_hy_order.queryHyLeaveByOrderNo", orderNo);
			if("seller".equals(role)){
				//车童查询留言信息则更新留言的状态为已读
				Map<String,String> paramsMap = new HashMap<String,String>();
				paramsMap.put("orderNo", orderNo);
				commExeSqlDAO.updateVO("sqlmap_hy_order.updateHyLeaveStat", paramsMap);
			}
			ResultVO<Object> resultVO = new ResultVO<Object>();
			//更新所有留言信息
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, queryForList);
			return resultVO;
		}catch(Exception e){
			log.error("货运险查询留言信息异常:",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("货运险查询留言信息异常", e);
		}
	}
}
